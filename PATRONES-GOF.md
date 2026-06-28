# Patrones GoF implementados en el proyecto

## Creacionales

### 1. Builder

**Qué es.** Patrón creacional que separa la **construcción** de un objeto complejo
de su representación, permitiendo armarlo paso a paso. Suele acompañarse de un
*Director* que conoce el orden de los pasos.

**Cómo está implementado.** `NivelBuilder` arma un `Nivel` por etapas
(`dimensiones()` → `agregarCelda()` / `agregarEntidad()` → `conVisionLimitada()` /
`conTiempoLimite()` → `build()`). El *Director* es `LectorTxt`, que lee el archivo
`.txt` y le dicta los pasos al builder sin conocer el dominio.

**Para qué se usa.** Para **cargar los niveles desde archivos de texto**:
transforma la grilla de caracteres y las directivas de cabecera (`VISION`,
`TIEMPO`) en un `Nivel` listo para jugar, manteniendo separados el formato del
archivo y el armado del modelo.

### 2. Factory Method

**Qué es.** Patrón creacional que delega la **creación de objetos** a un punto
centralizado, de modo que el código cliente no use `new` de clases concretas ni
condicionales por tipo.

**Cómo está implementado.** `CeldaFactory` y `EntidadFactory` mapean cada carácter
del `.txt` a la clase concreta mediante un registro `Map<Character, creador>`
(sin `switch` ni `if-else` por tipo). Agregar un nuevo elemento es **registrar una
entrada más** (principio Open/Closed).

**Para qué se usa.** Para **traducir los caracteres del nivel** a celdas
(`#` → `Pared`, `.` → `Destino`, etc.) y entidades (`@` → `Jugador`, `$` →
`CajaNormal`, etc.) durante la carga, sin acoplar el lector a los tipos concretos.

---

## Estructurales

### 3. Decorator

**Qué es.** Patrón estructural que **agrega responsabilidades** a un objeto de
forma dinámica, envolviéndolo en otro objeto con la misma interfaz, como
alternativa flexible a la herencia.

**Cómo está implementado.** `NivelDecorator` (abstracto) implementa `Nivel` y
envuelve otro `Nivel`. Sus concretos `VisionLimitadaDecorator` y
`TiempoLimiteDecorator` agregan una capacidad cada uno. El `NivelBuilder` envuelve
el `NivelBase` con los decoradores que correspondan.

**Para qué se usa.** Para las **dos funcionalidades adicionales**: visión limitada
(radio N) y tiempo límite (segundos), que se pueden combinar sobre cualquier nivel
sin modificar el `NivelBase`.

### 4. Facade

**Qué es.** Patrón estructural que ofrece una **interfaz única y simplificada** a
un subsistema complejo, reduciendo el acoplamiento del cliente con las partes
internas.

**Cómo está implementado.** `Juego` es la única clase que ven el Controller y la
View. Por dentro esconde `Tablero`, `EstadoJuego`, `ResolutorMovimiento` e
`Historial`, exponiendo una API acotada (`ejecutar()`, `hayVictoria()`,
`calcularResultado()`, `getUltimoEvento()`, registro de observadores).

**Para qué se usa.** Para **desacoplar la capa de presentación del modelo**: el
controlador opera el juego a través de la fachada sin conocer cómo se resuelve un
movimiento ni cómo se guarda el estado (refuerza MVC).

---

## De comportamiento

### 5. Command

**Qué es.** Patrón de comportamiento que **encapsula una acción como objeto**,
permitiendo parametrizar, encolar y desacoplar quién la invoca de quién la
ejecuta.

**Cómo está implementado.** La interfaz `Command` tiene `ejecutar(Juego)`, con los
concretos `MoverCommand`, `UndoCommand` y `ReiniciarCommand`. El `Controlador`
crea el comando y lo pasa a `Juego.ejecutar(...)`, que lo ejecuta y notifica a los
observadores una sola vez.

**Para qué se usa.** Para **representar las acciones del jugador** (mover,
deshacer, reiniciar) de manera uniforme, separando la entrada (teclado/botones)
de la lógica del modelo.

### 6. Memento

**Qué es.** Patrón de comportamiento que **captura y externaliza el estado
interno** de un objeto sin violar su encapsulamiento, para poder restaurarlo
después. Participan *Originator*, *Memento* y *Caretaker*.

**Cómo está implementado.** El *Originator* es `EstadoJuego`, que crea
`EstadoJuegoMemento` (y `MementoTablero` con posiciones, resistencias y estados de
muros/cerrojos). El *Caretaker* es `Historial`, una cola acotada a los últimos 15
estados que solo guarda/entrega mementos sin conocer su contenido.

**Para qué se usa.** Para el **deshacer (undo)**: restaurar el nivel hasta 5
movimientos hacia atrás (hasta 3 usos consecutivos) y para el **reinicio** del
nivel, sin exponer la representación interna del estado.

### 7. Observer

**Qué es.** Patrón de comportamiento que define una dependencia **uno a muchos**:
cuando un sujeto (*Observable*) cambia de estado, notifica automáticamente a sus
observadores.

**Cómo está implementado.** Interfaces `Observable` / `Observer`. Hay **dos
relaciones**: (1) `Juego` (sujeto) → `Vista` (observador), que repinta al cambiar
el modelo; (2) `Temporizador` (sujeto) → `EstadoJuego` (observador), que dispara
el reinicio cuando el tiempo se agota.

**Para qué se usa.** Para **mantener la vista sincronizada** con el modelo sin que
el modelo conozca Swing (MVC), y para **reaccionar al fin del tiempo** en los
niveles con límite.

### 8. State

**Qué es.** Patrón de comportamiento que permite que un objeto **altere su
comportamiento al cambiar su estado interno**, delegando en objetos-estado, en
lugar de usar condicionales (`if`/`switch`) sobre una variable de estado.

**Cómo está implementado.** Dos casos:
- `MuroAbiertoCerrado` delega en `EstadoMuro` (`MuroAbierto` / `MuroCerrado`):
  `esTransitable()` y `clavePresentacion()` dependen del estado actual.
- `Cerrojo` delega en `EstadoCerrojo` (`CerrojoSinLlave` / `CerrojoConLlave`):
  `estaActivo()` y `permiteSalida()` (regla R11) dependen del estado.

Las transiciones las disparan los eventos del dominio (la caja llave entra/sale
del cerrojo), nunca un condicional que consulte el estado.

**Para qué se usa.** Para modelar **muros que se abren/cierran** y **cerrojos con
o sin llave** de forma polimórfica, cumpliendo la restricción de cátedra de **no
usar condicionales para determinar el estado de un objeto**.

### 9. Template Method

**Qué es.** Patrón de comportamiento que define el **esqueleto de un algoritmo**
en un método, dejando que ciertos pasos variables se resuelvan por polimorfismo,
manteniendo fija la estructura general.

**Cómo está implementado.** `ResolutorMovimiento.resolver(Direccion)` fija el flujo
(calcular destino → resolver empuje o movimiento simple → deslizamiento →
registrar movimiento → devolver el evento ocurrido). Los pasos que varían se
delegan a los objetos del dominio (la caja decide su empuje, la celda decide si
provoca deslizamiento): el resolutor **no contiene lógica específica por tipo**.

**Para qué se usa.** Para **resolver cada movimiento del jugador** con un único
algoritmo estable, mientras cada tipo de caja y de celda aporta su comportamiento
particular sin romper la secuencia.

---

## Tabla resumen

| Categoría | Patrón | Clases principales | Para qué se usa |
|---|---|---|---|
| Creacional | Builder | `NivelBuilder`, `LectorTxt` | Cargar niveles desde `.txt` |
| Creacional | Factory Method | `CeldaFactory`, `EntidadFactory` | Caracter → Celda / Entidad |
| Estructural | Decorator | `NivelDecorator`, `VisionLimitadaDecorator`, `TiempoLimiteDecorator` | Funcionalidades extra (visión, tiempo) |
| Estructural | Facade | `Juego` | Fachada del modelo para Controller/View |
| Comportamiento | Command | `Command`, `MoverCommand`, `UndoCommand`, `ReiniciarCommand` | Acciones del jugador |
| Comportamiento | Memento | `EstadoJuegoMemento`, `MementoTablero`, `Historial` | Undo y reinicio |
| Comportamiento | Observer | `Observable`, `Observer` | Sincronizar vista y reaccionar al temporizador |
| Comportamiento | State | `EstadoMuro`, `EstadoCerrojo` (+ concretos) | Muro abierto/cerrado y cerrojo con/sin llave |
| Comportamiento | Template Method | `ResolutorMovimiento` | Resolver cada movimiento |
