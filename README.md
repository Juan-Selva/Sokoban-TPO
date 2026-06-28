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
│   │   │                 #   State de muro y cerrojo, Posicion, Direccion, Mementos
│   │   ├── partida/      # Juego (Facade), EstadoJuego, ResolutorMovimiento (Template Method),
│   │   │                 #   Historial + Memento (undo), Temporizador
│   │   ├── nivel/        # Nivel + Decorators, LectorTxt, NivelBuilder, Factories
│   │   ├── comandos/     # Command: Mover / Undo / Reiniciar
│   │   ├── observer/     # Interfaces Observer / Observable
│   │   ├── controller/   # Controlador (input → Command, avance de niveles)
│   │   ├── view/         # Swing: VentanaJuego, PanelTablero, PanelHud, PaletaPresentacion
│   │   └── Main.java
│   └── resources/
│       └── levels/       # Niveles en formato .txt
└── test/
    └── java/com/sokoban/ # Tests unitarios JUnit 5
```

## Formato de niveles (`.txt`)

Cada nivel es un archivo de texto. Caracteres de la grilla:

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

Directivas opcionales de cabecera (una por línea, antes de la grilla):

- `VISION n` — visión limitada con radio `n`.
- `TIEMPO n` — tiempo límite de `n` segundos.

## Funcionalidades adicionales

Las dos funcionalidades adicionales requeridas se implementan con el patrón **Decorator** sobre el nivel:

- **Visión limitada**: solo se renderizan las celdas dentro de un radio alrededor del jugador (no afecta la lógica del juego).
- **Tiempo límite**: cuenta regresiva que, al llegar a cero, reinicia el nivel.

## Diseño

Documentación de modelo de dominio, patrones aplicados y justificación de diseño en `docs/`:

- `docs/01-modelo-dominio.md`
- `docs/02-patrones-gof.md`
- `docs/03-relaciones-dependencias.md`
- `docs/05-justificacion-diseno.md`

Patrones GoF aplicados: Builder, Factory Method, Decorator, Facade, Command, Memento, Observer, State y Template Method. El renderizado sin `instanceof` se resuelve con una clave de presentación polimórfica (`clavePresentacion()`), no con Visitor.

## Equipo

_(completar con los integrantes)_
