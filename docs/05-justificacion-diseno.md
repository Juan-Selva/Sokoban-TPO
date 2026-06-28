# 05 — Justificación de diseño (dominio y patrones)
---

## Parte 1 — Aspectos clave del dominio

Estas son las decisiones conceptuales que sostienen todo el diseño. Cada una condiciona qué patrones tienen sentido y cuáles no.

### 1.1 Dos jerarquías separadas: `Celda` (piso) vs `Entidad` (lo que se mueve)
El tablero se modela en **dos capas independientes**:

- **`Celda`** es el piso de una posición. Nunca se mueve. Define *qué pasa cuando algo intenta entrar* (transitable o no, efecto de entrada).
- **`Entidad`** es lo que está "encima" del piso y **puede moverse**: el jugador y las cajas.

**Por qué importa:** en una misma posición pueden coexistir una celda (p. ej. un destino) y una entidad (una caja encima). Si mezcláramos ambas en una sola jerarquía, cada celda tendría que "saber" si tiene algo encima — y eso es responsabilidad del **`Tablero`**, no de la celda. Esta separación es la base de casi todo el resto (Information Expert) y es lo primero que conviene poder explicar en la defensa.

### 1.2 Information Expert: cada objeto sabe lo mínimo para decidir lo suyo
Reparto de responsabilidades (GRASP):

- **`Tablero`**: dónde está cada cosa, límites de la grilla, condición de victoria. No sabe *cómo* se mueve cada entidad.
- **`Celda`**: si es transitable y qué efecto produce al entrar. No sabe quién está encima.
- **`Entidad`/`Caja`**: cómo reacciona a un empuje (cada subtipo a su manera). Consulta al tablero, no decide por otros.
- **`EstadoJuego`**: contadores, historial, reglas de undo. No conoce la vista.
- **`Temporizador`**: cuánto tiempo queda y cuándo avisar que se agotó. **No decide qué hacer** al agotarse — eso lo decide `EstadoJuego`.

Regla mental para la defensa: *"si te preguntan quién hace X, la respuesta es el que tiene la información para hacerlo."*

### 1.3 Restricción dura: nada de `instanceof` / `switch` / `if-else` por tipo
Todo despacho por tipo se resuelve con **polimorfismo**. Esto no es un detalle estético: es una restricción explícita del proyecto y la razón de ser de varios patrones.

- ¿Distintos tipos de celda reaccionan distinto al entrar una caja? → método polimórfico `efectoEntrada()` en cada subtipo.
- ¿Distintos tipos de caja reaccionan distinto a un empuje? → `intentarEmpujar()` polimórfico.
- ¿La vista tiene que dibujar cada tipo distinto sin preguntar "¿qué tipo sos?" → método polimórfico **`clavePresentacion()`** (token neutro que la vista mapea a imagen/color).
- ¿Hay que crear la celda correcta según un carácter del `.txt`? → **Factory** (registro `Map<char, Supplier>`), no un `switch`.

### 1.4 Inmutabilidad donde aporta seguridad
- **`Posicion` y `Direccion`** son *value objects* inmutables. `Posicion.sumar(Direccion)` devuelve una **nueva** posición. Esto evita aliasing y bugs de estado compartido.
- **`Nivel`** es inmutable en su configuración inicial: lo que cambia durante la partida son las posiciones de las entidades y el estado de los muros, no la "receta" del nivel. Por eso el reinicio puede reconstruir el tablero desde el nivel original.
- **`EstadoJuegoMemento`** es un snapshot inmutable: garantiza que el historial no se corrompa.

### 1.5 Reglas no obvias que el diseño tiene que respetar
Son las que suelen romper implementaciones ingenuas y las que más se preguntan:

- **Empujar ≠ moverse** (R7): el contador de empujes solo sube con empuje *efectivo*. Una caja, un solo empuje contabilizado, aunque después se deslice varias celdas (R13).
- **Deslizamiento** (R12): al entrar una caja a terreno resbaladizo sigue sola en la misma dirección hasta chocar o llegar a piso no resbaladizo. Ocurre **dentro** de la resolución del movimiento, antes de devolver el control (§5.6 del dominio).
- **Caja frágil**: pierde **una** unidad por empuje, no por celda deslizada (§5.5). Una caja llave **nunca** puede ser frágil.
- **Caja llave ↔ cerrojo ↔ muro** (R10–R11): correspondencia **uno a uno**. La llave solo puede salir del cerrojo si **ninguna celda del muro asociado** está ocupada; si no, el empuje se bloquea (evita "cerrar el muro" sobre una entidad).
- **Undo** (R14–R16): salta **5 movimientos atrás de una vez**, historial de **15**, máximo **3 usos consecutivos**; mover una vez recarga los usos (§5.3).
- **Victoria** (R17): todas las cajas **normales y frágiles** (no rotas) sobre destinos. Las **llave no cuentan**.

### 1.6 MVC estricto y dirección de dependencias
El modelo **no conoce** a la vista ni al controlador. La vista observa al modelo; el controlador lo modifica a través de una superficie estrecha. Ninguna clase de dominio importa Swing. Esto se ve en los paquetes (§10 de `03`): `dominio`/`partida`/`nivel` no dependen de `view`/`controller`.

---

## Parte 2 — Por qué cada patrón

Para cada patrón: **problema concreto → cómo lo resuelve → alternativa descartada**. Son 9 patrones GoF (Iterator y Visitor quedaron descartados; el renderizado sin `instanceof` se resuelve con la clave de presentación, ver `02`).

### Creacionales

#### Builder — construcción del `Nivel` desde el `.txt`
- **Problema:** un nivel se arma por pasos (dimensiones → celdas → entidades → asociaciones cerrojo-muro → estado inicial de muros → metadatos como visión o tiempo). Mezclar el parseo del archivo con la construcción del objeto ensucia ambas responsabilidades.
- **Solución:** `NivelBuilder` encapsula la secuencia de construcción; `LectorTxt` (Director) le va dictando los pasos a medida que lee el archivo. El resultado es un `Nivel` válido e inmutable.
- **Por qué no otra cosa:** un constructor gigante con 8 parámetros sería ilegible y no permitiría validar consistencia parcial. Builder es la forma canónica de construir objetos complejos e inmutables.

#### Factory (Method) — mapear carácter → `Celda` / `Entidad`
- **Problema:** el parser ve caracteres (`#`, `.`, `*`, `~`, …) y tiene que crear la instancia correcta. La solución intuitiva es un `switch (char)` — **prohibido** por la restricción del proyecto (1.3).
- **Solución:** `CeldaFactory` y `EntidadFactory` mantienen un registro `Map<Character, Supplier<...>>`. Pedir `crear(ch)` devuelve la instancia sin ningún condicional por tipo.
- **Beneficio extra (Open/Closed):** agregar un tipo nuevo de celda = registrar una entrada más, sin tocar código existente.

### Estructurales

#### Decorator — modificadores opcionales del nivel (visión limitada, tiempo límite)
- **Problema:** las dos funcionalidades extra son **ortogonales**: un nivel puede tener ninguna, una o ambas. Modelarlas con herencia (`NivelConVision`, `NivelConTiempo`, `NivelConVisionYTiempo`, …) explota combinatoriamente.
- **Solución:** `NivelDecorator` envuelve un `Nivel` y agrega su comportamiento. Se componen libremente: `VisionLimitadaDecorator(TiempoLimiteDecorator(NivelBase))`.
- **Por qué no otra cosa:** es el caso de libro del patrón — agregar responsabilidades sin alterar la interfaz ni subclasear cada combinación.

#### Facade — `Juego` como cara del modelo
- **Problema:** la vista y el controlador no deberían conocer la maraña interna del modelo (Tablero, EstadoJuego, Historial, Resolutor, Temporizador).
- **Solución:** `Juego` expone una API estrecha: `ejecutar(Command)`, `getSnapshot()`, `reiniciar()`, `cargarSiguienteNivel()`. Todo el acoplamiento exterior pasa por ahí.
- **Beneficio:** sostiene el MVC (1.6) y baja el acoplamiento (GRASP Low Coupling).

### De comportamiento

#### Command — acciones del jugador
- **Problema:** desacoplar el input (tecla/botón) de la acción ejecutada, y poder soportar undo, remapeo y testing.
- **Solución:** `MoverCommand`, `UndoCommand`, `ReiniciarCommand` implementan `Command.ejecutar(Juego)`. El controlador solo decide *qué Command crear*; la ejecución es uniforme.
- **Combo:** Command + Memento + Observer es la tríada estándar de undo en juegos.

#### Memento — undo / historial
- **Problema:** poder volver a un estado anterior **sin exponer** la representación interna del `EstadoJuego`.
- **Solución:** `EstadoJuegoMemento` captura un snapshot opaco (posiciones, resistencias, estado de muros, contadores). El `Historial` es una cola circular de hasta 15 mementos. Solo `EstadoJuego` crea/consume mementos; el historial solo los guarda.
- **Por qué no Prototype/clonado manual:** Memento encapsula el snapshot y respeta el ocultamiento de información; clonar a mano filtraría las internas.

#### Observer — notificación sin acoplamiento (2 relaciones)
- **Problema:** que la vista se entere de los cambios del modelo sin que el modelo conozca a la vista; y que el temporizador avise que expiró sin decidir qué hacer.
- **Solución:** `Juego → Vista` (repintar al cambiar el estado) y `Temporizador → EstadoJuego` (al expirar, dispara R23).
- **Decisión:** se **descartó** un tercer Observer `Tablero → EstadoJuego` porque el `ResolutorMovimiento` ya actualiza al `EstadoJuego` directamente (evita duplicar responsabilidad).

#### State — muro abierto/cerrado y cerrojo con/sin llave
- **Problema:** dos objetos tienen comportamientos opuestos según un estado interno que cambia en runtime. El muro: cerrado bloquea (como pared), abierto deja pasar (como vacía). El cerrojo: sin llave no restringe nada; con llave debe decidir si la caja llave puede salir (R11).
- **Solución:** `MuroAbiertoCerrado` delega a un `EstadoMuro` (`MuroAbierto` / `MuroCerrado`) y `Cerrojo` delega a un `EstadoCerrojo` (`CerrojoSinLlave` / `CerrojoConLlave`). La transición del muro la dispara el `Cerrojo`; la del cerrojo, la entrada/salida de la `CajaLlave`. Evita un `if (abierto)` / `if (tieneLlave)` en cada operación.
- **R11 sin condicionales:** la celda de origen decide polimórficamente si deja salir a la entidad (`Celda.permiteSalida`); el `CerrojoConLlave` consulta al `Tablero` si la celda del muro asociado está libre. Nadie pregunta el estado del cerrojo con un `if`.
- **Por qué no un booleano:** un booleano obligaría a condicionales repartidos por todos los métodos — exactamente lo que 1.3 prohíbe.

#### Template Method — resolución de un movimiento
- **Problema:** el flujo de cada movimiento es **fijo** (validar límites → consultar celda → resolver empuje → deslizamiento → registrar → verificar victoria), pero **algunos pasos son polimórficos** (la celda decide su efecto, la caja decide su reacción).
- **Solución:** `ResolutorMovimiento.resolver(Direccion)` fija el orden y delega los pasos variables a los objetos del dominio. El resolutor **no contiene lógica por tipo**.
- **Por qué no Chain of Responsibility:** los pasos no se reordenan ni se extienden en runtime; CoR sería más infraestructura para un flujo que no varía.

#### Renderizado sin `instanceof` — clave de presentación (no es un patrón GoF)
- **Problema:** la vista debe dibujar cada subtipo de celda y de entidad distinto, **sin** preguntar el tipo (1.3).
- **Solución:** cada `Celda`/`Entidad` expone un método polimórfico `clavePresentacion()` que devuelve un token neutro (`"PARED"`, `"DESTINO"`, …); la vista (`PaletaPresentacion`) lo mapea a imagen/color. El polimorfismo del método resuelve el despacho por subtipo.
- **Alternativa descartada (Visitor):** el doble despacho de Visitor resolvería lo mismo, pero suma dos jerarquías de interfaces (`CeldaVisitor`/`EntidadVisitor`) y un `RendererVisitor`; se descarta por andamiaje innecesario frente a la clave de presentación.

---

## Parte 3 — Cómo se combinan (la foto grande)

Los patrones no están sueltos; se apoyan entre sí:

- **Undo = Command + Memento + Observer.** El `Command` ejecuta la acción, se guarda un `Memento`, y la `Vista` (Observer) se repinta. `UndoCommand` retrocede 5 mementos.
- **Carga de niveles = Builder + Factory + Decorator.** El `LectorTxt` dirige al `NivelBuilder`, que usa las `Factory` para crear celdas/entidades y, si el `.txt` lo pide, envuelve el `NivelBase` en `Decorator`s.
- **Movimiento = Template Method + State + clave de presentación para dibujar.** El `ResolutorMovimiento` orquesta; el `State` del muro/cerrojo define si se puede pasar/salir; la vista repinta usando `clavePresentacion()`.
- **Aislamiento del modelo = Facade + Observer.** `Juego` (Facade) es la única puerta de entrada; `Observer` es la única salida hacia la vista.

> **Una frase para la defensa:** *cada patrón aparece para resolver un problema concreto del enunciado, y la mayoría existe para respetar la restricción de no usar `instanceof`/`switch` canalizando todo el despacho por tipo a través de polimorfismo.*
