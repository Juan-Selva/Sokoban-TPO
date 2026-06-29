# 05 — Comparación de patrones GoF: Trabajo 1 vs Trabajo 2

> **Trabajo 1** = `Sokoban-SolidSnake`. **Trabajo 2** = este proyecto (Sokoban).
>
> Compara qué patrones usa cada proyecto, con foco en cómo cada uno respeta (o no) la
> siguiente restricción central: **el tipo o el estado de un objeto nunca se determina con
> `if/else/switch/instanceof`; se resuelve utilizando polimorfismo y herencia.**

---

## 1. Funcionalidades adicionales

La consigna exige **dos funcionalidades extra** por equipo, cada una apoyada en patrones de diseño. Acá se resume qué agregó cada trabajo y cómo funciona en la práctica.

### Trabajo 1 (SolidSnake)

**1. Deslizamiento con Strategy (`MovementStrategy`)**
- **Qué es:** cuando una caja entra a terreno resbaladizo, no se detiene en la primera celda: sigue deslizándose en la misma dirección hasta chocar o salir del piso resbaladizo.
- **Cómo funciona:** la caja (o el movimiento) compone una `MovementStrategy` — normal o resbaladiza — que define si, tras un empuje, la caja continúa sola. El tipo de estrategia se elige al crear la caja; el empuje delega en esa estrategia sin `switch` por tipo de celda en el método central.

**2. Costo de empuje con Strategy (`PushEnergyStrategy`)**
- **Qué es:** empujar no cuesta lo mismo para todas las cajas: las “pesadas” consumen más energía del jugador que las normales.
- **Cómo funciona:** cada caja lleva una `PushEnergyStrategy` (`normal` / `high`). Al empujar, el juego consulta la estrategia compuesta para saber cuánta energía restar. Es el mismo patrón Strategy que en el Trabajo 2, pero acotado al costo de empuje (el deslizamiento es la otra funcionalidad extra de ese proyecto).

---

### Trabajo 2 (este proyecto)

**1. Visión limitada (Decorator)**
- **Qué es:** en algunos niveles el jugador solo ve un radio **N** de celdas alrededor de su posición; el resto del tablero se muestra oscuro.
- **Cómo funciona:** si el `.txt` declara `VISION n`, el `LectorTxt` envuelve el `NivelBase` en un `VisionLimitadaDecorator`, que guarda el radio. La **lógica del juego no cambia** (colisiones, empujes y victoria son iguales); solo `PanelTablero` oculta lo que está fuera del radio usando la posición del jugador y `nivel.getRadioVision()`. El Decorator agrega la modalidad sin tocar la construcción base del nivel ni crear subclases por combinación.

**2. Energía, caja pesada y botella (Strategy + ítems)**
- **Qué es:** el jugador tiene una reserva de **energía** (máx. 20). Moverse gasta 1; empujar gasta lo que marque la caja (normal 1, **pesada 3**). Si se queda sin energía para moverse, el nivel se **reinicia solo**. Las **botellas** (`B` en el `.txt`) repiten energía al pisarlas (+12) y desaparecen.
- **Cómo funciona:**
  - **Caja pesada (`P`):** cada `Caja` compone una `EstrategiaEmpuje` (`EmpujeNormal` / `EmpujePesado`). El costo lo responde la estrategia con `costo()`, no un `if` preguntando el tipo de caja.
  - **Consumo:** `ResolutorMovimiento` descuenta energía tras un movimiento o empuje efectivo; si no alcanza, la acción no se ejecuta.
  - **Botella:** `Item` / `BotellaAgua` aplican su efecto de forma polimórfica cuando el jugador entra a la celda (`ItemFactory` + registro en el `.txt`, carácter `B`).
  - **Reinicio por agotamiento:** el resolutor devuelve `EventoJuego.SIN_ENERGIA`; ese valor del enum define en `aplicarConsecuencia()` que se reinicie el nivel — sin condicionales en el controlador.
  - **Undo / reinicio:** energía e ítems forman parte del Memento, así que deshacer o reiniciar restaura también esos datos.

> **Nota:** en el Trabajo 2 el deslizamiento en terreno resbaladizo es **mecánica base** del dominio (celda `TerrenoResbaladizo`), no una funcionalidad adicional. Las dos extras elegidas son **visión limitada** y el **sistema de energía** (con caja pesada y botella como piezas del mismo diseño).

---

### Comparación rápida

| | Trabajo 1 | Trabajo 2 |
|---|-----------|-----------|
| **Extra 1** | Deslizamiento (`MovementStrategy`) | Visión limitada (`VisionLimitadaDecorator`) |
| **Extra 2** | Costo de empuje variable (`PushEnergyStrategy`) | Energía + caja pesada + botella (`EstrategiaEmpuje`, `Item`) |
| **Patrón principal** | Strategy (ambas) | Decorator + Strategy / polimorfismo de ítems |
| **¿Afecta solo la vista?** | No | Sí, solo la visión limitada; la energía es lógica de partida |

---

## 2. Tabla comparativa

### Resumen

| | Trabajo 1 (SolidSnake) | Trabajo 2 (este proyecto) |
|---|------------------------|---------------------------|
| **Patrones GoF usados** | 4 | 10 |

Los cuatro patrones del Trabajo 1 existen también en el Trabajo 2, pero allí se implementan sin consultar el tipo con condicionales. El Trabajo 2 suma seis patrones más que el otro no tiene.

---

### Patrones presentes en ambos trabajos

**Factory Method**
- Trabajo 1: sí, con `switch` sobre el carácter del `.txt`.
- Trabajo 2: sí, con `Map<Character, creador>` en `CeldaFactory`, `EntidadFactory` e `ItemFactory`.

**Memento**
- Trabajo 1: sí, con `instanceof` al capturar y `switch` al restaurar.
- Trabajo 2: sí, con `capturarEstadoEn` / `restaurarEstadoDe` en cada objeto.

**State**
- Trabajo 1: sí, pero solo en caja frágil y con transiciones por `if` (ver §3).
- Trabajo 2: sí, en muro (`EstadoMuro`) y cerrojo (`EstadoCerrojo`).

**Strategy**
- Trabajo 1: sí (`MovementStrategy`, `PushEnergyStrategy`).
- Trabajo 2: sí (`EstrategiaEmpuje` para el costo de empuje).

---

### Patrones solo en el Trabajo 2

| Patrón | Dónde se usa |
|--------|----------------|
| **Builder** | `NivelBuilder` + `LectorTxt` como Director |
| **Decorator** | `VisionLimitadaDecorator` |
| **Facade** | `Juego` como cara del modelo |
| **Command** | `MoverCommand`, `UndoCommand`, `ReiniciarCommand` |
| **Observer** | `Juego → Vista` (el modelo no conoce Swing) |
| **Template Method** | `ResolutorMovimiento` (flujo fijo de cada movimiento) |

En el Trabajo 1, ese flujo de movimiento está concentrado en `Game.movePlayer` sin patrón equivalente.

---

### Lectura rápida por patrón

| Patrón | T1 | T2 | Diferencia principal |
|--------|:--:|:--:|----------------------|
| Factory Method | ✅ | ✅ | T1: `switch`; T2: registro `Map` (OCP) |
| Memento | ✅ | ✅ | T1: `instanceof`/`switch`; T2: hooks polimórficos |
| State | ✅* | ✅ | T1: parcial; T2: muro + cerrojo completos |
| Strategy | ✅ | ✅ | Bien resuelto en ambos (ver §3) |
| Builder | ❌ | ✅ | Solo T2 |
| Decorator | ❌ | ✅ | Solo T2 |
| Facade | ❌ | ✅ | Solo T2 |
| Command | ❌ | ✅ | Solo T2 |
| Observer | ❌ | ✅ | Solo T2 |
| Template Method | ❌ | ✅ | Solo T2 |

*\* En T1 el State está aplicado de forma incompleta y con condicionales (detalle en §3).*

---

## 3. Patrones compartidos (mismo patrón, distinta implementacion)

**Factory Method.** Ambos traducen el carácter del `.txt` a la clase concreta.
- Trabajo 1: `txtLevelFactory` lo hace con `switch (symbol)`. Concentra el conocimiento de todos los tipos en un punto y obliga a editarlo por cada tipo nuevo → viola la restricción anti-`switch` y OCP.
- Trabajo 2: `CeldaFactory`/`EntidadFactory`/`ItemFactory` usan `Map<Character, Function<Posicion, …>>`; sumar un tipo es registrar una entrada, sin condicionales.

**Memento.** Ambos guardan/restauran snapshots para el undo.
- Trabajo 1: al **capturar** discrimina con `instanceof` (`if (b instanceof FragileBox) … else if (b instanceof KeyBox) …`) y al **restaurar** usa `switch` sobre un string `boxType`/`itemType`. Doble violación de la regla.
- Trabajo 2: cada celda/entidad guarda lo suyo de forma polimórfica (`capturarEstadoEn`/`restaurarEstadoDe`); el memento no pregunta tipos.

**State.** Es el contraste más ilustrativo.
- Trabajo 1: aplica State **solo a la caja frágil** (`Intact`/`Damaged`/`Broken`), pero la transición se decide con `if (resistance <= 0) … else if (resistance <= 2) …` — el `if` que el patrón debía eliminar. Y el **muro abierto/cerrado** (el caso textualmente prohibido por la consigna) **no** usa State: se resuelve con un booleano `isClosedWall()` y reemplazando la instancia por `OpenWall`, más cadenas de `instanceof` en el render.
- Trabajo 2: State para **muro** (`EstadoMuro`) y **cerrojo** (`EstadoCerrojo`); las transiciones las disparan eventos del dominio (la llave entra/sale), nunca un condicional sobre un valor.

**Strategy.** El patrón que el Trabajo 1 sí resuelve razonablemente bien, y que ambos comparten.
- Trabajo 1: `MovementStrategy` (normal/slippery) y `PushEnergyStrategy` (normal/high) por composición, sin condicionales por tipo. Buen uso.
- Trabajo 2: `EstrategiaEmpuje` (`EmpujeNormal`/`EmpujePesado`) para el costo de energía. (El deslizamiento, que el Trabajo 1 modela como Strategy, en el Trabajo 2 lo resuelve la propia celda de forma polimórfica.)

---

## 4. Errores de polimorfismo que no pasan por un patrón

Además de los anteriores, en el Trabajo 1 hay violaciones fuera de los patrones (no tienen equivalente en el Trabajo 2 porque allí se resolvieron por diseño):

**Render con `instanceof`** (`BoardPanel`)
- Qué hace mal: la vista pregunta `if (element instanceof Wall) … else if (… instanceof Destination) …` (y lo mismo con cajas e ítems) para decidir qué dibujar.
- Por qué es un error: cada vez que aparece un subtipo nuevo, hay que **modificar** el panel y agregar otra rama. El polimorfismo pide que el **objeto sepa representarse** (o al menos exponga un token neutro) y que quien dibuja no conozca la jerarquía concreta. Con `instanceof` la vista queda acoplada a todos los subtipos y el despacho deja de ser polimórfico.
- En el Trabajo 2: cada celda/entidad expone `clavePresentacion()`; la vista mapea ese string a color/imagen sin preguntar el tipo.

**`Game` como “clase Dios”** (`Game.movePlayer`)
- Qué hace mal: un solo método valida límites, detecta cajas, empuja, desliza, descuenta resistencia, cuenta movimientos y verifica victoria.
- Por qué es un error: concentra responsabilidades que pertenecen a **otras clases del dominio** (celda, caja, tablero, estado de partida). Eso impide que cada subtipo **redefina su comportamiento** en su propia clase: el flujo no delega en polimorfismo, sino que el método central decide qué hacer según lo que encuentra en el tablero. Rompe cohesión y obliga a tocar `Game` ante cualquier regla nueva.
- En el Trabajo 2: `ResolutorMovimiento` fija el orden del movimiento (Template Method) y **delega** en cada objeto; `EstadoJuego` lleva contadores e historial.

**Predicados de tipo** (`isWalkable`, `isDestinationCell`, `isLockCell`, `isClosedWall`, etc.)
- Qué hace mal: métodos que por dentro hacen `instanceof` (o equivalente) para clasificar celdas y responder sí/no.
- Por qué es un error: vuelven a **preguntar el tipo** en lugar de enviar un mensaje al objeto. La jerarquía de `Celda` deja de ser útil: el comportamiento no vive en el subtipo (`esTransitable`, `efectoEntrada`, `permiteSalida`), sino en funciones externas que discriminan. Cada celda nueva exige otro predicado o otra rama, en lugar de implementar el método correspondiente.
- En el Trabajo 2: cada celda resuelve transitabilidad y efectos de forma polimórfica; nadie pregunta “¿qué tipo de celda sos?”.

---
