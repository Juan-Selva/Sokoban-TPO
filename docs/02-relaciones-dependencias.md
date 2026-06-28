
| Símbolo | Significado |
|---------|-------------|
| `A ──▷ B` | `A` hereda de `B` (extends) |
| `A ╌╌▷ B` | `A` realiza la interfaz `B` (implements) |
| `A ◆── B` | **Composición**: `A` es dueño de `B`; si `A` desaparece, `B` también. |
| `A ◇── B` | **Agregación**: `A` contiene a `B` pero `B` puede existir sin `A`. |
| `A ─── B` | **Asociación**: `A` conoce a `B` (referencia permanente). |
| `A ╌╌▶ B` | **Dependencia**: `A` usa a `B` de forma transitoria (parámetro, retorno, variable local). |
| `1`, `*`, `0..1`, `1..*` | Cardinalidades estándar UML. |

## 2. Modelo del dominio

### 2.1 Jerarquía de `Celda`

`Celda` es abstracta. Subtipos concretos:

| Subtipo | Hereda de | Notas |
|---------|-----------|-------|
| `CeldaVacia` | `Celda` | Sin estado propio |
| `Pared` | `Celda` | Sin estado propio |
| `Destino` | `Celda` | Sin estado propio |
| `TerrenoResbaladizo` | `Celda` | Dispara deslizamiento al recibir caja |
| `Cerrojo` | `Celda` | Conoce su muro asociado; compone un `EstadoCerrojo` (patrón State: con/sin llave) |
| `MuroAbiertoCerrado` | `Celda` | Compone un `EstadoMuro` (patrón State) |

**Relaciones de la jerarquía:**

- Cada `Celda` **compone una** `Posicion` (1 a 1).
- Cada `Celda` expone `clavePresentacion()` (token neutro que la vista mapea a imagen/color; sin Visitor, sin `instanceof`).
- `Cerrojo ─── MuroAbiertoCerrado` (asociación 1 a 1 — el cerrojo conoce su muro y viceversa).
- `Cerrojo ◆── EstadoCerrojo` (composición 1 a 1 — el cerrojo es dueño de su estado actual; la referencia cambia entre `CerrojoSinLlave` y `CerrojoConLlave`). Reemplaza la antigua referencia directa a `CajaLlave`: el "tiene/no tiene llave" se modela por estado (patrón State), no con un atributo consultado por condicionales.
- `MuroAbiertoCerrado ◆── EstadoMuro` (composición 1 a 1 — el muro es dueño de su estado actual; la referencia cambia entre `MuroAbierto` y `MuroCerrado`).

### 2.2 Jerarquía de `Entidad`

`Entidad` es abstracta. Subjerarquía:

```
Entidad
├── Jugador
└── Caja (abstract)
    ├── CajaNormal
    ├── CajaFragil      (atributo: resistencia)
    └── CajaLlave       (asociación: cerrojo asignado)
```

**Relaciones de la jerarquía:**

- Cada `Entidad` **compone una** `Posicion` (1 a 1, mutable).
- Cada `Entidad` expone `clavePresentacion()` (igual que `Celda`; la vista lo mapea a imagen/color, sin Visitor).
- `Jugador ─── Tablero` (asociación 1 a 1 — el jugador consulta al tablero para validar movimientos).
- `Caja ─── Tablero` (asociación 1 a 1 — la caja consulta al tablero para validar empujes y deslizamientos).
- `CajaLlave ─── Cerrojo` (asociación 1 a 1 — la caja llave conoce su cerrojo asignado; ver §5.2 del modelo de dominio).

### 2.3 `Tablero`

El Tablero es el **agregador del estado físico** del nivel en curso.

| Hacia | Tipo | Cardinalidad | Detalle |
|-------|------|--------------|---------|
| `Celda` | Composición | 1 a `n·m` | Grilla bidimensional; el Tablero es dueño |
| `Jugador` | Composición | 1 a 1 | Exactamente un jugador por tablero |
| `Caja` | Composición | 1 a 0..* | Cantidad variable (incluyendo cero si todas frágiles se rompieron) |
| `Nivel` | Asociación | * a 1 | Conoce de qué Nivel fue construido (para el reset) |

**Lo que el Tablero NO conoce:** Vista, Controller, Comandos, Historial. Es puramente estado físico.

### 2.4 `Posicion` y `Direccion`

- `Posicion` es un **value object inmutable**: dos enteros `fila`, `columna`. Igualdad por valor.
- `Direccion` es un **enum** con cuatro valores (`ARRIBA`, `ABAJO`, `IZQUIERDA`, `DERECHA`), cada uno con su `dx`, `dy`.
- `Posicion ╌╌▶ Direccion` (dependencia transitoria — `Posicion.sumar(Direccion)` devuelve nueva `Posicion`).

Ningún otro objeto del sistema "compone" `Direccion`; se pasa como parámetro.

---

## 3. Estado del juego y partida

### 3.1 `Juego` (Facade)

`Juego` es la **cara visible del modelo** para el exterior (Controller, View).

| Hacia | Tipo | Cardinalidad | Motivo |
|-------|------|--------------|--------|
| `Tablero` | Composición | 1 a 1 | Estado físico actual |
| `EstadoJuego` | Composición | 1 a 1 | Estado lógico (contadores, historial) |
| `Nivel` | Asociación | 1 a 1 | Nivel cargado actualmente |
| `ResolutorMovimiento` | Composición | 1 a 1 | Aplicado a cada Command de movimiento |
| `Observer` (Vista) | Asociación | 1 a 0..* | Vía interfaz Observer; el Juego es Observable |

**Lo que `Juego` expone como API pública:**
- `ejecutar(Command)` → resuelve la acción
- `getTablero()` / `getEstadoJuego()` → para rendering y consultas de la vista
- `calcularResultado()` → `ResultadoNivel` (contadores + puntaje + calificación) al ganar
- `getUltimoEvento()` → `EventoJuego` de la última acción (lo usa la vista para el sonido)
- `agregarObservador(Observer)`, `quitarObservador(Observer)`

### 3.2 `EstadoJuego`

| Hacia | Tipo | Cardinalidad | Detalle |
|-------|------|--------------|---------|
| `Historial` | Composición | 1 a 1 | Buffer circular de hasta 15 Mementos |
| `Temporizador` | Composición | 1 a 0..1 | Solo si el nivel lo tiene |
| Contadores | Atributo | — | `movimientos`, `empujes`, `undosUsadosConsecutivos` |

`EstadoJuego` **observa** al `Temporizador`: cuando el temporizador agota, dispara R23 (reinicio del nivel).

Expone además consultas que la vista usa como datos/predicados (sin lógica en la vista): `puedeUndo()` (habilita/deshabilita el botón Deshacer), `getUndosDisponibles()` (`MAX − undosConsecutivos`, R16/§5.3) y `calcularResultado()`, que arma un `ResultadoNivel` (contadores + puntaje + calificación) al ganar. Lleva además `undosTotales` (distinto de los consecutivos) para el puntaje.

### 3.2.1 `ResultadoNivel` y `Calificacion` (puntaje)

- `ResultadoNivel` es un value object inmutable: `movimientos`, `empujes`, `undos`, `score` y `calificacion`. El `score` se calcula con la fórmula de §5.4 (aritmética pura, `Math.max` + restas).
- `Calificacion` es un `enum` (`TRES_ESTRELLAS` / `DOS_ESTRELLAS` / `UNA_ESTRELLA`): cada valor conoce su criterio (`aplica(score, undos)`) y se elige el primero aplicable de mejor a peor. Es cálculo de negocio del puntaje, no lógica del juego.
- `EstadoJuego ╌╌▶ ResultadoNivel` (lo produce); `ResultadoNivel ─── Calificacion`.

### 3.3 `Historial` y `Memento`

- `Historial ◆── EstadoJuegoMemento` (composición 1 a 0..15).
- `EstadoJuegoMemento` es **inmutable**: snapshot completo necesario para reconstruir el estado (posiciones del jugador y cajas, resistencias de frágiles, estado de muros, contadores).
- Solo `EstadoJuego` crea y consume Mementos (encapsulación del patrón Memento — el Historial solo guarda).

### 3.4 `Nivel` y sus decoradores

`Nivel` es abstracta (o interfaz). Implementaciones:

```
Nivel (interface)
├── NivelBase                    ← implementación normal
└── NivelDecorator (abstract)
    ├── VisionLimitadaDecorator  ← agrega radio de visión
    └── TiempoLimiteDecorator    ← agrega Temporizador
```

**Relaciones:**

- `NivelDecorator ◇── Nivel` (agregación 1 a 1 — envuelve a otro `Nivel`, que puede ser base u otro decorator).
- Los decoradores se pueden **componer libremente** (`VisionLimitadaDecorator(TiempoLimiteDecorator(NivelBase))` o viceversa).
- `Nivel` conoce su **layout inicial** (datos crudos: dimensiones, mapa de celdas, posiciones de entidades, asociaciones cerrojo–muro, estados iniciales de muros).
- `Nivel ╌╌▶ Tablero` (dependencia — al "iniciar" un nivel, produce un Tablero nuevo a partir de su layout).

### 3.5 `Temporizador`

| Hacia | Tipo | Cardinalidad | Detalle |
|-------|------|--------------|---------|
| `Observer` (EstadoJuego) | Asociación | 1 a 0..* | Notifica al expirar |
| Atributos | — | — | `tiempoLimite`, `tiempoRestante` |

El Temporizador no decide qué hacer al expirar: solo notifica. La decisión (reiniciar) la toma el `EstadoJuego`.

---

## 4. Carga de niveles

### 4.1 `LectorTxt`

| Hacia | Tipo | Cardinalidad | Detalle |
|-------|------|--------------|---------|
| `NivelBuilder` | Dependencia | * a 1 | Usa el builder paso a paso |
| Archivo `.txt` | Dependencia externa | — | Source de datos |

No conoce el dominio de juego directamente; solo el formato del archivo y el contrato del builder.

### 4.2 `NivelBuilder` (Builder)

| Hacia | Tipo | Cardinalidad | Detalle |
|-------|------|--------------|---------|
| `CeldaFactory` | Asociación | 1 a 1 | Para crear celdas a partir de chars |
| `EntidadFactory` | Asociación | 1 a 1 | Para crear entidades a partir de chars |
| `Nivel` | Dependencia | 1 a 1 | Output del proceso de construcción |

El Builder **produce un Nivel** terminado (potencialmente envuelto en Decorators si el `.txt` declara visión limitada o tiempo).

### 4.3 `CeldaFactory` y `EntidadFactory` (Factory)

- `CeldaFactory ◆── Map<Character, Function<Posicion, Celda>>` (composición — registro interno; `crear(char, Posicion)`).
- `EntidadFactory ◆── Map<Character, Function<Posicion, Entidad>>`.
- Sin dependencias hacia el resto del sistema: son factorías puras.

---

## 5. Acciones del jugador

### 5.1 `Command`

`Command` es una interfaz. Implementaciones:

```
Command (interface)
├── MoverCommand    (atributo: Direccion)
├── UndoCommand
└── ReiniciarCommand
```

**Relaciones:**

- `Command ╌╌▶ Juego` (dependencia — al ejecutarse, opera sobre la Facade).
- `Controlador ─── Command` (asociación — el Controller mantiene/crea Commands).

### 5.2 `ResolutorMovimiento` (Template Method)

Clase abstracta con un método plantilla `resolver(Direccion)` que orquesta el flujo:

1. Calcular posición destino del jugador (`Posicion.sumar(Direccion)`).
2. Consultar al Tablero qué celda y qué entidad hay en destino.
3. Si hay caja: delegar al método polimórfico `intentarEmpujar()` de esa caja.
4. Si la caja se mueve y aterriza en resbaladizo: disparar deslizamiento.
5. Notificar al `EstadoJuego` el movimiento (para historial y contadores).
6. Devolver el `EventoJuego` ocurrido (`MOVIMIENTO`, `EMPUJE` o `NADA`), que la vista usa para el sonido.

**Relaciones:**

- `ResolutorMovimiento ╌╌▶ Tablero`, `ResolutorMovimiento ╌╌▶ EstadoJuego` (dependencias usadas durante `resolver`).
- `resolver(Direccion)` devuelve un `EventoJuego`: el resultado se fija en las ramas de empuje/movimiento que ya existen, sin agregar condicionales de estado.
- Cada paso polimórfico (intentar empujar, efecto de entrada de la celda) se delega a los objetos del dominio: el Resolutor **no contiene lógica específica por tipo**.

---

## 6. Estado del muro y del cerrojo (State)

`MuroAbiertoCerrado` no contiene `if (abierto)`; delega a un `EstadoMuro`:

```
EstadoMuro (interface)
├── MuroAbierto    ← se comporta como CeldaVacia
└── MuroCerrado    ← se comporta como Pared
```

`Cerrojo` aplica el mismo patrón y no contiene `if (tieneLlave)`; delega a un `EstadoCerrojo`:

```
EstadoCerrojo (interface)
├── CerrojoSinLlave   ← no restringe la salida; no está activo
└── CerrojoConLlave   ← la llave solo sale si el muro asociado está libre (R11)
```

**Relaciones:**

- `MuroAbiertoCerrado ◆── EstadoMuro` (composición 1 a 1; referencia mutable).
- `Cerrojo ◆── EstadoCerrojo` (composición 1 a 1; referencia mutable).
- Transición del muro: el `Cerrojo`, al detectar `CajaLlave` encima, le pide al `MuroAbiertoCerrado` que pase a `MuroAbierto`. Cuando la caja llave es empujada fuera (si el muro está libre — R11), pasa a `MuroCerrado`.
- Transición del cerrojo: al colocarse la `CajaLlave` pasa a `CerrojoConLlave`; al retirarse, a `CerrojoSinLlave`.

**Cómo se aplica R11 sin condicionales de estado:** al intentar mover una caja, la celda de origen decide polimórficamente si permite la salida (`Celda.permiteSalida`, por defecto `true`). El `Cerrojo` delega esa decisión en su `EstadoCerrojo`: `CerrojoConLlave` consulta al `Tablero` (experto en ocupación) si la posición del muro asociado está libre. Así nadie pregunta "¿está activo el cerrojo?" con un `if`.

**Quién dispara las transiciones:** el `Cerrojo`, mediante una asociación bidireccional con su muro. El cambio de estado no lo decide el muro mismo.

---

## 7. Renderizado (clave de presentación + Observer)

### 7.1 `Vista` (Swing)

La vista (`VentanaJuego` + `PanelTablero` + `PanelHud`) observa al `Juego` y lo consulta para dibujar.

| Hacia | Tipo | Cardinalidad | Detalle |
|-------|------|--------------|---------|
| `Juego` | Asociación | 1 a 1 | Se registra como `Observer` y le pide el estado para dibujar |
| `Observer` (interfaz) | Realización | — | `VentanaJuego` la implementa para recibir notificaciones |

La Vista **no conoce subtipos concretos de `Celda` ni de `Entidad`**: a cada uno le pide `clavePresentacion()`.

### 7.2 Clave de presentación (imágenes)

- Cada `Celda`/`Entidad` expone `clavePresentacion()` → token neutro (`"PARED"`, `"DESTINO"`, `"JUGADOR"`, …).
- `PanelTablero` recorre el tablero, obtiene la clave de cada celda/entidad y la traduce con `PaletaPresentacion`, que mantiene un `Map<String, Image>` (sprites cargados desde `resources/sprites/<CLAVE>.png`) con **fallback** a un `Map<String, Color>` cuando no hay sprite. Así el juego funciona con o sin assets.
- El despacho por subtipo lo resuelve el polimorfismo de `clavePresentacion()`: no hay `instanceof`, `switch` ni Visitor, y el dominio no conoce Swing.

### 7.3 Sonido (`EventoJuego`)

- Cada acción del jugador produce un `EventoJuego` (`MOVIMIENTO`, `EMPUJE`, `UNDO`, `REINICIO`, `NADA`): token neutro que **conoce su propia clave de sonido**, exactamente la misma idea que `clavePresentacion()`.
- `ResolutorMovimiento.resolver(...)` devuelve el evento (lo asigna en las ramas de empuje / movimiento que ya existen, sin condicionales nuevos); `Juego` lo guarda y lo expone con `getUltimoEvento()`.
- El `Controlador` solo hace `getUltimoEvento().getClaveSonido()` y se lo pasa a la vista (`ReproductorSonidos`, capa view). **No** infiere el evento comparando contadores ni usa condicionales para elegir el efecto: la selección es polimórfica.
- El modelo no conoce el audio; solo dice "qué pasó". Si falta el `.wav` o no hay vista, no suena (best-effort). Simétrico con el renderizado por clave de presentación.

---

## 8. Observación de eventos

**Dos** relaciones Observer (cerrado — ver decisión abajo):

| Subject (Observable) | Observer | Evento |
|----------------------|----------|--------|
| `Juego` | `Vista` | Cambio en el estado del modelo → repintar |
| `Temporizador` | `EstadoJuego` | Tiempo expirado → R23 |

**Decisión (cierra el punto abierto previo):** ✅ se **descarta** el Observer `Tablero → EstadoJuego`. El `ResolutorMovimiento` (Template Method, §5.2) ya actualiza al `EstadoJuego` **directamente** en su paso 5 (registrar movimiento) y paso 6 (verificar victoria). Hacer que además el `EstadoJuego` observe al `Tablero` duplicaría esa responsabilidad y agregaría acoplamiento sin beneficio. Los eventos del dominio (caja sobre destino, caja rota, cerrojo activado) se resuelven dentro del flujo del Resolutor, no por observación.

---

## 9. Tabla resumen (vista compacta)

Solo las relaciones más significativas (omitiendo Posicion/Direccion por ubicuidad):

| Origen | Tipo | Destino | Card. |
|--------|------|---------|-------|
| Tablero | ◆── | Celda | 1 a n·m |
| Tablero | ◆── | Jugador | 1 a 1 |
| Tablero | ◆── | Caja | 1 a 0..* |
| Cerrojo | ─── | MuroAbiertoCerrado | 1 a 1 |
| Cerrojo | ◆── | EstadoCerrojo | 1 a 1 |
| CajaLlave | ─── | Cerrojo | 1 a 1 |
| MuroAbiertoCerrado | ◆── | EstadoMuro | 1 a 1 |
| Juego | ◆── | Tablero | 1 a 1 |
| Juego | ◆── | EstadoJuego | 1 a 1 |
| Juego | ◆── | ResolutorMovimiento | 1 a 1 |
| Juego | ─── | Observer | 1 a 0..* |
| EstadoJuego | ◆── | Historial | 1 a 1 |
| EstadoJuego | ◆── | Temporizador | 1 a 0..1 |
| Historial | ◆── | EstadoJuegoMemento | 1 a 0..15 |
| NivelDecorator | ◇── | Nivel | 1 a 1 |
| NivelBuilder | ─── | CeldaFactory | 1 a 1 |
| NivelBuilder | ─── | EntidadFactory | 1 a 1 |
| LectorTxt | ╌╌▶ | NivelBuilder | 1 a 1 |
| Command | ╌╌▶ | Juego | * a 1 |
| Controlador | ─── | Command | 1 a 1..* |
| Controlador | ─── | Juego | 1 a 1 |
| Vista | ─── | Juego | 1 a 1 |

---

## 10. Dependencias entre paquetes (sketch)

Si dividimos en paquetes/módulos lógicos:

```
sokoban
├── dominio          ← Celda, Entidad, Tablero, Posicion, Direccion, EstadoMuro, EstadoCerrojo
├── partida          ← Juego, EstadoJuego, Historial, Memento, ResolutorMovimiento,
│                       Temporizador, ResultadoNivel, Calificacion (puntaje), EventoJuego
├── nivel            ← Nivel, NivelBase, Decoradores, LectorTxt, NivelBuilder, Factories
├── comandos         ← Command y sus implementaciones
├── observer         ← Interfaces Observer/Observable
├── view             ← Vista Swing (VentanaJuego, PanelTablero, PanelHud, PaletaPresentacion, ReproductorSonidos)
└── controller       ← Controlador, mapeo input → Command
```

**Reglas de dependencia entre paquetes:**

- `dominio` no depende de nadie del proyecto (excepto `observer` para interfaces).
- `partida` depende de `dominio` y `comandos`.
- `nivel` depende de `dominio` y `partida`.
- `view` y `controller` dependen de `partida` y `comandos`.
- **Nada** del lado del modelo depende de `view` ni `controller`.

---

## 11. Checklist de validación

Antes de pasar al diagrama UML, confirmar:

- [x] Toda regla R1–R24 tiene asignación clara a una o varias clases responsables.
- [x] No hay ciclos de dependencias entre paquetes.
- [x] Cada patrón GoF aprobado (§02) tiene clases concretas mapeadas acá.
- [x] Las cardinalidades resisten una revisión cruzada: si `A ──── B` con `1 a *`, entonces `B ──── A` debe ser `* a 1`.
- [x] Ninguna clase del dominio puro (Celda, Entidad, Tablero) conoce a la Vista, el Controlador o Swing.
- [x] `instanceof` no aparece en ningún punto del diagrama (todas las decisiones por tipo se canalizan por polimorfismo).

> **Estado del documento:** ✅ **final.** El renderizado se resuelve con la clave de presentación (`clavePresentacion()`, ver §02), no con Visitor.

Una vez aprobado, pasamos a `04-diagrama-clases.puml`.
