# Sokoban

Trabajo Integrador de la asignatura **Proceso de Desarrollo de Software** (Prof. Jonathan Pepe — UADE).

Implementación del clásico juego de lógica **Sokoban** en Java + Swing, aplicando principios SOLID, patrones GRASP, GoF y arquitectura MVC.

## Requisitos

- Java 17+
- Maven 3.8+

## Cómo ejecutar

```bash
mvn clean package
java -jar target/sokoban-1.0-SNAPSHOT.jar
```

O desde el IDE: importar como proyecto Maven y ejecutar `com.sokoban.Main`.

## Cómo correr los tests

```bash
mvn test
```

## Controles

- **Flechas** o **WASD**: mover al jugador
- **U** o botón **Deshacer**: deshacer 5 movimientos de una vez (máx. 3 usos consecutivos; se recargan al moverse)
- **R** o botón **Reiniciar**: reiniciar el nivel actual

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/sokoban/
│   │   ├── dominio/      # Tablero, Celda (+subtipos), Entidad/Caja (+variantes),
│   │   │                 #   Item (BotellaAgua), Strategy (EstrategiaEmpuje),
│   │   │                 #   State de muro y cerrojo, Posicion, Direccion, Mementos
│   │   ├── partida/      # Juego (Facade), EstadoJuego, ResolutorMovimiento (Template Method),
│   │   │                 #   Historial + Memento (undo), EventoJuego, Calificacion
│   │   ├── nivel/        # Nivel + Decorators, LectorTxt, NivelBuilder,
│   │   │                 #   CeldaFactory, EntidadFactory, ItemFactory
│   │   ├── comandos/     # Command: Mover / Undo / Reiniciar
│   │   ├── observer/     # Interfaces Observer / Observable
│   │   ├── controller/   # Controlador (input → Command, avance de niveles)
│   │   ├── view/         # Swing: VentanaJuego, PanelTablero, PanelHud,
│   │   │                 #   PaletaPresentacion, ReproductorSonidos
│   │   └── Main.java
│   └── resources/
│       ├── levels/       # Niveles en formato .txt (nivel1–nivel5)
│       ├── sprites/      # PNG opcionales por clave de presentación (fallback a color)
│       └── sounds/       # WAV opcionales por evento (best-effort)
└── test/
    └── java/com/sokoban/ # Tests unitarios JUnit 5
```

## Formato de niveles (`.txt`)

Cada nivel es un archivo de texto en `src/main/resources/levels/`. Caracteres de la grilla:

| Carácter | Elemento |
|----------|----------|
| `#` | Pared |
| (espacio) | Celda vacía |
| `.` | Destino |
| `~` | Terreno resbaladizo |
| `C` | Cerrojo |
| `M` | Muro abierto/cerrado |
| `@` | Jugador |
| `$` | Caja normal |
| `F` | Caja frágil |
| `K` | Caja llave |
| `P` | Caja pesada |
| `B` | Botella de agua |

Directiva opcional de cabecera (una línea, antes de la grilla):

- `VISION n` — visión limitada con radio `n` (solo afecta el renderizado).

Los cinco niveles están registrados en `Controlador.RUTAS_NIVELES`:

| Nivel | Mecánicas destacadas |
|-------|----------------------|
| 1 | Dos cajas normales (introducción) |
| 2 | Dos cajas, terreno resbaladizo, obstáculos y botella |
| 3 | Cerrojo/muro, caja llave; hay que abrir el muro antes de resolver las cajas |
| 4 | Todo lo anterior + caja frágil, caja pesada y botellas |
| 5 | Nivel final: todas las mecánicas + visión limitada (`VISION 3`) |

## Energía

El jugador tiene una reserva de energía (20 por defecto). Cada movimiento a celda libre cuesta **1**; empujar una caja cuesta lo que define su **Strategy** (`EstrategiaEmpuje`): **1** para cajas normales, frágiles y llave; **3** para cajas pesadas (`P`).

Las **botellas de agua** (`B`) repone energía al recogerlas. Si el jugador intenta actuar sin energía suficiente, se dispara el evento `SIN_ENERGIA` y el nivel se reinicia automáticamente.

## Funcionalidades adicionales

Las dos funcionalidades adicionales requeridas por la consigna son:

1. **Visión limitada** (patrón **Decorator** sobre `Nivel`): solo se renderizan las celdas dentro de un radio alrededor del jugador; la lógica del juego no cambia. Se activa con la directiva `VISION n` en el archivo del nivel.
2. **Energía + caja pesada + botella** (patrón **Strategy** en el costo de empuje + **ítems** en el tablero): el jugador gestiona energía para moverse y empujar; las cajas pesadas consumen más, y las botellas la repone.

## Sprites y sonidos

- **Sprites**: PNG en `src/main/resources/sprites/` con el nombre exacto de la clave de presentación (p. ej. `JUGADOR.png`). Si falta un archivo, ese elemento se dibuja con color y letra. Ver `sprites/README.txt`.
- **Sonidos**: WAV PCM en `src/main/resources/sounds/` (p. ej. `movimiento.wav`, `sin_energia.wav`). Si falta un archivo, esa acción no suena. Ver `sounds/README.txt`.

## Diseño

Documentación de modelo de dominio, patrones aplicados y justificación de diseño en `docs/`:

- `docs/01-modelo-dominio.md`
- `docs/02-relaciones-dependencias.md`
- `docs/03-justificacion-diseno.md`
- `docs/04-patrones-GoF.md`

Patrones GoF aplicados: Builder, Factory Method, Decorator, Facade, Command, Memento, Observer, State, Strategy y Template Method. El renderizado sin `instanceof` se resuelve con una clave de presentación polimórfica (`clavePresentacion()`), no con Visitor.

## Equipo

| Integrante | LU |
|------------|-----|
| Anabella Castellon | 1157949 |
| Alvaro Huanaco Llanque | 1205365 |
| Juan Selva | 1157155 |
| Oksana Bernkhardt | 1193028 |
