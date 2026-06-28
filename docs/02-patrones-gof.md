# 02 — Patrones GoF
---

## Creacionales

### Builder — ✅
**Se utiliza** para la construcción del **Nivel** a partir del archivo `.txt`.

- **Por qué:** un Nivel se arma paso a paso (dimensiones → grilla de celdas → entidades → asociaciones cerrojo-muro → estado inicial de muros → metadatos como visión limitada o tiempo). Encapsular esa secuencia en un `NivelBuilder` separa la lógica de parseo del archivo de la lógica de dominio, y permite validar la consistencia antes de exponer el objeto terminado.
- **Beneficio extra:** el Nivel termina siendo inmutable en su configuración inicial (lo que cambia durante el juego son las posiciones de las entidades y el estado de los muros, no la grilla en sí). El Builder es la forma canónica de armar objetos complejos e inmutables.

### Factory (Method) — ✅
**Se utiliza** para mapear **caracteres del `.txt` a instancias concretas de `Celda`** (y análogamente para entidades).

- **Por qué:** el parser del nivel ve caracteres como `#` (pared), `.` (vacía), `*` (destino), `~` (resbaladizo), etc. Sin Factory, esto se resuelve naturalmente con `switch`/`if-else` sobre el char — exactamente lo que la convención del proyecto **prohíbe**. Un `CeldaFactory` con dispatch polimórfico (registro de creadores indexado por char) elimina el switch y centraliza el conocimiento de "qué char produce qué celda".
- **Forma concreta:** registro `Map<Character, Supplier<Celda>>` poblado al inicio, consultado por el Builder al recorrer el archivo.

---

## Estructurales

### Decorator — ✅
**Se utiliza** para los **modificadores opcionales del nivel**: visión limitada (§1.18) y tiempo límite (§1.19).

- **Por qué:** estos modificadores son **ortogonales** entre sí — un nivel puede tener ninguno, uno, o ambos. Modelarlos como subclases (`NivelConVision`, `NivelConTiempo`, `NivelConVisionYTiempo`, …) produce explosión combinatoria. Envolver un `Nivel` base con `VisionLimitadaDecorator` y/o `TiempoLimiteDecorator` mantiene la API uniforme y permite componerlos libremente.
- **Cuidado:** los decoradores deben respetar R21 (la visión limitada no afecta la lógica, solo el renderizado), lo cual encaja con el principio del patrón: agregar responsabilidades sin alterar la interfaz.

### Facade — ✅
**Se utiliza** como **fachada del modelo expuesta al Controller / View**.

- **Por qué:** la View no debe conocer la estructura interna del modelo (Tablero, EstadoJuego, Historial, Temporizador, etc.). Una clase `Juego` que ofrece operaciones de alto nivel — `ejecutar(Command)`, `getSnapshot()`, `reiniciar()`, `cargarSiguienteNivel()` — actúa como Facade y simplifica el acoplamiento.
- **Beneficio:** alinea con el principio MVC de que el Controller manipula el modelo a través de una superficie estrecha y bien definida.

---

## De comportamiento

### Command — ✅
**Se utiliza** para representar las **acciones del jugador**: `MoverCommand` (con su dirección), `UndoCommand`, `ReiniciarCommand`.

- **Por qué:**
  - Desacopla el input (tecla presionada / botón clickeado) de la acción ejecutada. La capa de input solo decide *qué Command crear*; la ejecución es uniforme.
  - Permite encolar, loguear y, sobre todo, **soportar undo** combinándolo con Memento.
  - Permite remapeo de teclas y testing aislado de cada acción.
- **Combo natural:** Command + Memento + Observer es la tríada estándar para implementar undo en un juego.

### Memento — ✅
**Se utiliza** para implementar el **undo** (§1.13–§1.14, R14–R16).

- **Por qué:** el Memento captura el estado del juego en un instante sin exponer su representación interna. El **Historial** (R14: últimos 15 movimientos) es exactamente una cola acotada de Mementos. Cada Memento contiene: posiciones de jugador y cajas, resistencias restantes de cajas frágiles, estado de cerrojos/muros, contadores.
- **Combinación con Command:** después de cada Command exitoso se guarda un Memento; un `Undo` retrocede 5 Mementos en un único salto (R15).

### Observer — ✅
**Se utiliza** en **dos** puntos del sistema.

- **MVC — `Juego → Vista`:** la View observa al modelo (`Juego`) para repintar cuando cambia el estado, sin que el modelo conozca a la View.
- **`Temporizador → EstadoJuego`:** el `Temporizador` notifica al `EstadoJuego` cuando el tiempo se agota (R23), respetando que la decisión de qué hacer es del EstadoJuego, no del Temporizador (§3 "Temporizador").

> Se descartó un tercer Observer `Tablero → EstadoJuego`: el `ResolutorMovimiento` (Template Method) ya actualiza al `EstadoJuego` directamente, así que observar al Tablero duplicaría la responsabilidad.

### State — ✅
**Se utiliza** en dos lugares: el **muro abierto/cerrado** y el **cerrojo** (con/sin llave).

- **Muro — por qué:** un muro abierto/cerrado tiene dos comportamientos completamente distintos según su estado interno: cuando está cerrado se comporta como pared (bloquea), cuando está abierto se comporta como celda vacía (transitable). Es el caso de libro del patrón State: mismo objeto, comportamiento polimórfico según un estado interno que cambia en runtime.
- **Cerrojo — por qué:** el cerrojo (`EstadoCerrojo` → `CerrojoSinLlave` / `CerrojoConLlave`) decide por polimorfismo si la caja llave puede salir (R11): estando con llave, solo permite la salida si la celda del muro asociado está libre. Así se evita un `if (tieneLlave)` y se respeta la prohibición de condicionales para determinar el estado del objeto, en simetría con el muro.
- **Beneficio:** evita un `if (estoyAbierto)` / `if (tieneLlave)` dentro de cada operación y permite que la transición sea explícita (un evento del cerrojo cambia la referencia al estado).

### Template Method — ✅
**Se utiliza** en el **algoritmo de resolución de un movimiento del jugador**.

- **Por qué:** la secuencia es fija (validar límites del tablero → consultar la celda destino → resolver si hay entidad bloqueando → ejecutar movimiento / empuje → manejar deslizamiento → registrar en historial → verificar victoria), pero algunos pasos son polimórficos (la celda destino decide su efecto de entrada; la caja decide su reacción al empuje). Un método plantilla en `ResolutorMovimiento` fija el orden y delega los pasos variables a los objetos del dominio.

### Presentación neutra (clave de presentación) — ✅

**Qué problema resuelve.** La `Vista` necesita **dibujar cada tipo de celda y de entidad de forma distinta** (la pared no se dibuja igual que un destino, ni un jugador igual que una caja) **sin preguntar el tipo** (`instanceof`/`switch` están prohibidos).

**Cómo se resuelve.** Cada `Celda`/`Entidad` expone un método polimórfico `clavePresentacion()` que devuelve un token neutro (`"PARED"`, `"DESTINO"`, `"JUGADOR"`, …). La View traduce ese token a un color o imagen mediante un mapa (`PaletaPresentacion`). El despacho por subtipo lo resuelve el polimorfismo del propio método; el dominio no conoce Swing y no hay un solo `instanceof`.

**Por qué no un método `dibujar()` directamente en cada `Celda`.** Metería `java.awt.Graphics` (Swing) **dentro del dominio**, rompiendo MVC: el modelo no puede conocer la capa de presentación.

**Por qué no Visitor.** El doble despacho de Visitor lograría lo mismo, pero introduce dos jerarquías de interfaces (`CeldaVisitor`/`EntidadVisitor`) y un `RendererVisitor`, andamiaje innecesario: la clave de presentación cumple la misma restricción (sin `instanceof`, sin Swing en el dominio) con polimorfismo simple. **Se adopta esta solución.**

**Misma idea para el sonido (`EventoJuego`).** Cada acción del jugador produce un `EventoJuego` (`MOVIMIENTO`, `EMPUJE`, `UNDO`, `REINICIO`, `NADA`) que el `ResolutorMovimiento`/`Juego` devuelve. Cada evento **conoce su propia clave de sonido**; el `Controlador` solo hace `getUltimoEvento().getClaveSonido()` y la vista (`ReproductorSonidos`) reproduce el `.wav` correspondiente. Así la selección del efecto es polimórfica: la capa de presentación **no compara contadores ni usa condicionales** para adivinar qué pasó, y el modelo sigue sin conocer el audio (best-effort: si falta el archivo, no suena).

---

## Resumen

| Categoría | Patrón | Uso |
|-----------|--------|-----|
| Creacional | Builder | ✅ Carga de niveles desde `.txt` |
| Creacional | Factory (Method) | ✅ Mapeo char → Celda / Entidad |
| Estructural | Decorator | ✅ Modificadores de nivel (visión, tiempo) |
| Estructural | Facade | ✅ Fachada `Juego` para Controller/View |
| Comportamiento | Command | ✅ Acciones del jugador + undo |
| Comportamiento | Memento | ✅ Historial / undo |
| Comportamiento | Observer | ✅ MVC y temporizador (2 relaciones) |
| Comportamiento | State | ✅ Muro abierto/cerrado y cerrojo con/sin llave (R11) |
| Comportamiento | Template Method | ✅ Resolución de movimientos |

> Renderizado sin `instanceof`: se resuelve con la **clave de presentación** (`clavePresentacion()`, polimorfismo simple), no con Visitor. Ver sección anterior.

---