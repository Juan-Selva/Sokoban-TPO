# 01 — Modelo del dominio

> Documento conceptual. Define **qué es** cada cosa en el juego, **qué sabe**, **qué hace** y **cómo se relaciona** con las demás. 
---

## 1. Glosario del dominio

Términos que vamos a usar de manera consistente a lo largo del proyecto. Cada vez que aparezca uno de estos términos en código, documentación o discusiones, debe entenderse exactamente como se define acá.

### 1.1 Tablero
Espacio rectangular donde transcurre el juego. Está formado por una grilla de **celdas** dispuestas en filas y columnas. El tablero contiene también a las **entidades** (jugador y cajas) que se mueven sobre él.

### 1.2 Posición
Par de coordenadas (fila, columna) que identifica de manera única una ubicación en el tablero. Toda celda tiene una posición fija; toda entidad tiene una posición que puede cambiar.

### 1.3 Dirección
Uno de los cuatro sentidos cardinales en que el jugador puede moverse o empujar: **arriba, abajo, izquierda, derecha**. No existen movimientos diagonales.

### 1.4 Celda
La unidad más pequeña del tablero. Una celda ocupa exactamente una posición y representa el "piso" de esa posición. Define qué puede pasar cuando algo intenta entrar a ella. **Las celdas no se mueven**. Tipos:

- **Celda vacía**: piso normal, transitable sin efectos especiales.
- **Pared**: bloquea el paso de cualquier entidad. Es estructural y no cambia durante el juego.
- **Destino**: piso transitable que además marca un lugar donde una caja debe quedar para cumplir la condición de victoria.
- **Terreno resbaladizo**: piso transitable, pero cuando una caja entra a él se desliza en la misma dirección hasta chocar contra un obstáculo o llegar a una celda no resbaladiza.
- **Cerrojo**: piso transitable que se activa cuando una caja llave queda encima de él. Mientras esté activo, abre los muros abiertos/cerrados asociados.
- **Muro abierto/cerrado**: celda que tiene dos estados. Cuando está **cerrado** se comporta como pared (bloquea); cuando está **abierto** se comporta como celda vacía. Cada muro está vinculado a **un** cerrojo (correspondencia uno a uno, ver §5.2): cuando ese cerrojo está activado, el muro está abierto.

> **Nota:** cada celda es de **un único tipo**. No existen combinaciones (no hay celdas destino-resbaladizas, ni cerrojo-destino, etc.).

### 1.5 Entidad
Cualquier objeto que ocupa una posición del tablero pero **puede moverse** o **ser movido**. Las dos entidades del juego son el jugador y las cajas. Se diferencian de las celdas en que no son parte del piso, sino que están "encima" del piso.

### 1.6 Jugador (Sokoban)
La entidad controlada por la persona que juega. Hay exactamente un jugador por nivel. Ocupa una sola celda. Puede moverse en cualquiera de las cuatro direcciones, una celda por vez, según las reglas del juego.

### 1.7 Caja
Entidad que el jugador empuja para llevarla hasta un destino. Variantes:

- **Caja normal**: sin características especiales. Se empuja, se desliza si está sobre terreno resbaladizo. Su empuje cuesta 1 de energía.
- **Caja frágil**: se comporta como una caja normal, pero tiene una **resistencia** finita. Cada empuje le resta un punto de resistencia. Cuando llega a cero, la caja **se rompe** y desaparece del tablero.
- **Caja llave**: se comporta como una caja normal, pero al quedar sobre su celda cerrojo asignada abre los muros vinculados a ese cerrojo. Puede ser empujada fuera del cerrojo **solo si ninguna celda del muro asociado tiene una entidad encima**; si está ocupado, el empuje no se realiza. Una caja llave **nunca puede ser frágil**.
- **Caja pesada**: se comporta como una caja normal en cuanto a colisiones, deslizamiento y victoria, pero **empujarla cuesta más energía** (3 en vez de 1). No se rompe. (Funcionalidad adicional — ver §1.20.)

### 1.8 Empuje
Acción que ocurre cuando el jugador intenta moverse hacia una celda ocupada por una caja. Si la caja puede moverse a su próxima celda en la misma dirección, ambos se mueven (jugador y caja); si no puede, ninguno se mueve. **Empujar es distinto de moverse**: el contador de empujes solo incrementa cuando hay empuje efectivo.

### 1.9 Movimiento
Acción del jugador que cambia su posición en una unidad en alguna dirección. Cada movimiento puede ser un movimiento simple (a celda vacía) o un movimiento con empuje (a celda con caja). En ambos casos cuenta como un movimiento del jugador.

### 1.10 Deslizamiento
Comportamiento de una caja cuando entra a una celda de terreno resbaladizo. La caja sigue moviéndose en la dirección del empuje original sin intervención del jugador, hasta que ocurre alguna de estas condiciones:

- Choca contra una pared, un muro cerrado u otra caja: se detiene en la celda anterior.
- Llega a una celda que no es resbaladiza: se detiene en esa celda.

Durante un deslizamiento, una caja frágil **no pierde resistencia adicional**: el empuje original ya descontó su punto. (Decisión de diseño — ver §5.)

### 1.11 Nivel
Configuración inicial completa de un tablero: dimensiones, ubicación de cada tipo de celda, posición inicial del jugador y de cada caja, asociación entre cerrojos y muros, y **estado inicial de cada muro abierto/cerrado** (por defecto cerrado, pero el archivo puede especificarlo). Cada nivel se carga desde un archivo de texto.

### 1.12 Estado del juego
Conjunto de información que describe la situación actual de una partida en un instante dado: qué nivel se está jugando, posiciones actuales de todas las entidades, estado de los muros (abiertos/cerrados), resistencia restante de cada caja frágil, **energía actual del jugador**, **ítems presentes** en el tablero, contadores (movimientos, empujes, undos usados), e historial reciente para soportar undo. Todo esto (incluida la energía y los ítems) entra en el snapshot del Memento, de modo que el undo y el reinicio lo restauran.

### 1.13 Historial
Registro de los últimos N estados del juego (N = 15 según el enunciado). Permite reconstruir la situación tal como estaba varios movimientos atrás. Es la base de la funcionalidad undo.

### 1.14 Undo
Operación que restaura el estado del juego a como estaba **5 movimientos atrás, en un único salto**. Si el historial tiene menos de 5 movimientos, restaura al estado más antiguo disponible (lo que haya). Solo puede ejecutarse hasta 3 veces consecutivas (se "consume" una vez por uso, y se "recarga" cuando el jugador realiza nuevos movimientos — ver §5.3).

### 1.15 Victoria
Condición que se cumple cuando **todas las cajas normales y frágiles existentes están sobre celdas destino**, simultáneamente. Las cajas frágiles rotas no cuentan como cajas pendientes (porque ya no existen). **Las cajas llave quedan exentas**: su rol es abrir/cerrar muros (desafío del nivel), no ser objetivo de victoria. El nivel se considera completado y se avanza al siguiente.

### 1.16 Puntaje
Valor numérico calculado al completar un nivel, en función de los movimientos realizados, los empujes efectuados y los undos utilizados. Fórmula:

```
score = max(0, 1000 − 3·movimientos − 5·empujes − 100·undos)
```

El puntaje se traduce además en una calificación por **estrellas (1 a 3)** con hard-caps por uso de undos. Ver §5.4 para el detalle completo.

### 1.17 Reinicio de nivel
Operación que restaura el nivel actual a su configuración inicial: todas las entidades vuelven a sus posiciones de partida, los muros recuperan su estado original, las cajas frágiles recuperan su resistencia completa y todos los contadores (movimientos, empujes, undos) se resetean a cero. El historial se descarta. No avanza al siguiente nivel.

### 1.18 Visión limitada
Modalidad opcional de un nivel en la que el jugador solo puede ver las celdas dentro de un radio N alrededor de su posición actual. Las celdas fuera del radio se muestran ocultas. La lógica del juego no cambia; solo cambia lo que se renderiza. El radio es un parámetro del nivel.

### 1.19 Energía (funcionalidad adicional)
Reserva de "stamina" del jugador. Arranca llena (máximo 20 por defecto). Cada acción la consume: **moverse cuesta 1** y **empujar cuesta lo que defina la caja** (normal 1, pesada 3). Si no hay energía suficiente para una acción, esa acción no se realiza. Si el jugador se queda **sin energía para moverse**, el nivel se reinicia automáticamente (ver R24). La energía es parte del estado (entra al Memento).

### 1.20 Ítem (funcionalidad adicional)
Objeto recogible que descansa sobre el piso y **no se empuja**. Cuando el jugador entra a su celda, aplica su efecto y desaparece del tablero. La única variante actual es la **botella de agua**, que repone energía (+12, sin superar el máximo). Los ítems forman parte del estado (entran al Memento, así que el undo los repone).

> El **tiempo límite** que figuraba en versiones previas del diseño fue **retirado** de la implementación (ver §5.8); la otra funcionalidad adicional, además de la visión limitada, es ahora el sistema de energía + caja pesada + botella.

---

## 2. Reglas del juego

Cada regla está numerada para poder referenciarla desde código y tests.
### Movimiento del jugador

- **R1**. El jugador solo puede moverse en una de las cuatro direcciones cardinales, una celda por vez.
- **R2**. El jugador puede entrar a una celda si la celda es transitable Y no hay otra entidad bloqueando esa celda. Las celdas transitables son: vacía, destino, resbaladiza, cerrojo y muro abierto.
- **R3**. El jugador no puede atravesar paredes ni muros cerrados.
- **R4**. El jugador no puede atravesar cajas. Si quiere ir a una celda con caja, debe empujarla (R5).

### Empuje

- **R5**. Cuando el jugador intenta moverse hacia una celda con caja, se intenta empujar la caja en esa dirección. La caja se mueve si y solo si la celda destino de la caja es transitable Y no hay otra entidad allí. Si la caja se mueve, el jugador también; si no, ninguno.
- **R6**. El jugador no puede empujar más de una caja a la vez. Si detrás de la caja hay otra caja, no se puede empujar.
- **R7**. Empujar una caja cuenta como **un movimiento** y **un empuje**. Moverse a una celda libre cuenta como **un movimiento** y **cero empujes**.

### Cajas especiales

- **R8**. Una caja frágil pierde un punto de resistencia por cada empuje efectivo del jugador (no por cada celda recorrida en deslizamiento — ver R12).
- **R9**. Cuando la resistencia de una caja frágil llega a cero, la caja desaparece del tablero (ya no es un obstáculo ni cuenta para victoria).
- **R10**. Una caja llave, al quedar exactamente sobre **su** celda cerrojo asignada, "activa" ese cerrojo. Mientras el cerrojo esté activo, los muros asociados están abiertos.
- **R11**. Una caja llave puede ser empujada fuera de su cerrojo **solo si ninguna celda del muro asociado tiene una entidad encima** (jugador o caja). Si el muro está libre, el empuje se realiza: el cerrojo se desactiva y los muros vuelven a cerrarse. Si el muro está ocupado, el empuje queda bloqueado (no se mueve ni la caja llave ni el jugador). Como restricción asociada: una caja llave nunca puede ser frágil (ver §1.7 y §5.2).

### Deslizamiento

- **R12**. Cuando una caja entra a una celda resbaladiza por efecto de un empuje, continúa moviéndose en la misma dirección sin intervención del jugador hasta que: (a) la próxima celda no sea transitable o esté ocupada, o (b) la próxima celda no sea resbaladiza. En (a) se detiene en la celda actual; en (b) se detiene en la próxima celda no resbaladiza.
- **R13**. Durante un deslizamiento, los movimientos extra de la caja **no cuentan como empujes adicionales** del jugador (un solo empuje contabilizado). El jugador no se mueve durante el deslizamiento (solo se movió la celda inicial del empuje).

### Undo

- **R14**. El historial conserva los últimos 15 movimientos del jugador.
- **R15**. Un uso del undo restaura el estado del juego a **5 movimientos atrás, en un único salto**. Si el historial contiene menos de 5 movimientos, restaura al estado más antiguo disponible (en el extremo, al estado inicial del nivel).
- **R16**. El jugador puede usar undo hasta 3 veces consecutivas. Una vez agotados los 3 usos, debe realizar nuevos movimientos para volver a tener undos disponibles. (Definir detalle en §5.3.)

### Victoria y avance

- **R17**. Un nivel está ganado cuando toda **caja normal o frágil** existente (no rota) está sobre una celda destino. Las cajas llave no cuentan para la victoria.
- **R18**. Al ganar un nivel, se muestra un resumen con movimientos, empujes, undos usados y puntaje final, y se avanza al siguiente nivel.

### Reinicio

- **R19**. El jugador puede reiniciar el nivel actual en cualquier momento. El reinicio restaura el estado del juego a la configuración inicial del nivel: posiciones de todas las entidades, estado de muros y cerrojos, resistencia de cajas frágiles y todos los contadores se resetean a cero. El historial se descarta.

### Visión limitada

- **R20**. Si un nivel tiene visión limitada, solo se renderizan las celdas y entidades dentro del radio N (parámetro del nivel) alrededor de la posición actual del jugador. Las celdas fuera del radio se muestran como oscuras/ocultas.
- **R21**. La visión limitada no afecta la lógica del juego: colisiones, empujes, deslizamientos y victoria se resuelven igual independientemente de si una celda es visible o no.

### Energía (funcionalidad adicional)

- **R22**. Cada acción consume energía del jugador: **moverse a una celda libre cuesta 1**; **empujar una caja cuesta lo que defina la caja** (normal 1, pesada 3).
- **R23**. Si el jugador no tiene energía suficiente para empujar una caja, el empuje no se realiza (pero puede seguir moviéndose si le alcanza para moverse).
- **R24**. Si el jugador no tiene energía ni para moverse (energía 0), el nivel **se reinicia automáticamente** (aplica R19).
- **R25**. La **botella de agua**, al ser recogida (el jugador entra a su celda), repone energía (+12) sin superar el máximo; luego desaparece del tablero. El jugador arranca cada nivel con su energía máxima (20).

> **Nota:** el **tiempo límite** (R22–R24 en versiones previas del diseño) fue retirado. La numeración R22–R25 corresponde ahora al sistema de energía.

---

## 3. Responsabilidades por entidad

Para cada entidad: **qué información maneja** y **qué decisiones toma**. Esto es la base del principio GRASP "Information Expert".

### Tablero
- **Sabe**: las dimensiones de la grilla, qué celda hay en cada posición, qué entidades hay y dónde están, qué nivel representa.
- **Decide**: si una posición está dentro de los límites; qué celda y qué entidad hay en una posición dada; si se cumplió la condición de victoria.
- **No decide**: cómo se mueve cada entidad (delega en la entidad), ni cómo dibujar nada (eso es de la View).

### Posición y Dirección
- **Saben**: sus coordenadas / sus deltas (dx, dy).
- **Deciden**: cómo calcular una nueva posición sumando una dirección.
- Son objetos inmutables (value objects).

### Celda (concepto general)
- **Sabe**: su posición.
- **Decide**: si es transitable; qué pasa cuando una entidad entra a ella (efecto de entrada).
- **No decide**: qué entidad hay encima (eso lo sabe el tablero).

### Pared
- Caso particular de celda: nunca transitable, sin efecto de entrada (porque nadie entra).

### Celda vacía y celda destino
- Ambas transitables, sin efecto de entrada. La diferencia es que la celda destino se cuenta para la condición de victoria.

### Terreno resbaladizo
- Transitable. Efecto de entrada para una caja: dispara el deslizamiento (R12).

### Cerrojo
- Transitable. Conoce su muro vinculado. Su comportamiento "con/sin llave" se modela con el patrón State (no con un atributo consultado por condicionales): estando con llave, decide si permite que esa llave salga según si la celda del muro asociado está libre (R11).

### Muro abierto/cerrado
- Sabe si está abierto o cerrado (a través de su `EstadoMuro`). Su estado lo cambia el cerrojo vinculado. Cuando está abierto se comporta como celda vacía; cuando está cerrado, como pared.

### Jugador
- **Sabe**: su posición y su **energía** (actual y máxima).
- **Decide**: si puede moverse en una dirección dada (consultando al tablero); si tiene energía para una acción; consume/repone energía. Cuando se mueve, comunica al tablero el cambio de posición.

### Caja (concepto general)
- **Sabe**: su posición y su **costo de empuje** (delegado en una `EstrategiaEmpuje`).
- **Decide**: si puede ser empujada en una dirección dada (consultando al tablero); cómo reacciona al empuje (cada subtipo lo resuelve a su manera).

### Caja normal
- No agrega información. Su reacción al empuje es simplemente moverse una celda en la dirección recibida.

### Caja frágil
- **Sabe** además: su resistencia restante.
- **Decide** además: cuándo romperse (al llegar a 0). Cuando se rompe, debe avisar al tablero para que la elimine.

### Caja llave
- **Sabe** además: a qué cerrojo específico "responde" (identificador de cerrojo). La correspondencia es **uno a uno**: cada caja llave activa únicamente su cerrojo asignado (ver §5.2).

### Caja pesada
- No agrega información propia; solo trae una `EstrategiaEmpuje` de mayor costo (3). Su reacción al empuje es la de una caja normal.

### Ítem / Botella de agua
- **Sabe**: su posición.
- **Decide**: qué efecto aplica al jugador que la recoge (la botella repone energía). Cada ítem resuelve su efecto a su manera (polimorfismo), sin que nadie pregunte su tipo.

### Estado del juego
- **Sabe**: contadores actuales (movimientos, empujes, undos usados, undos disponibles), historial de estados (snapshots/Memento).
- **Decide**: cuándo permitir undo, cómo registrar un nuevo movimiento, y cómo ejecutar el reinicio (restaura el snapshot inicial y resetea contadores e historial).

### Nivel
- **Sabe**: la disposición inicial completa del tablero; si tiene visión limitada y su radio.
- **Decide**: cómo construir un tablero a partir de su disposición (delega la construcción a las factories).

---

## 4. Relaciones entre entidades

> Las relaciones se describen en lenguaje natural. El UML las formalizará después.

- Un **tablero** está compuesto por muchas **celdas** (una por posición) y contiene muchas **entidades** (un jugador y cero o más cajas).
- Una **celda** tiene una **posición** fija; una **entidad** tiene una **posición** que cambia con el tiempo.
- El **jugador** es una **entidad**.
- Una **caja** es una **entidad**. **Caja normal**, **caja frágil**, **caja llave** y **caja pesada** son tipos especializados de **caja**. Cada caja tiene una **estrategia de empuje** que define su costo de energía.
- El **tablero** contiene además **ítems** (cero o más), por ejemplo botellas de agua, que el jugador recoge al pisarlos.
- El **jugador** tiene **energía**, que las acciones consumen y los ítems reponen.
- Una **celda cerrojo** está asociada a un **muro abierto/cerrado** (correspondencia uno a uno; ver §5.2).
- El **estado del juego** registra cada movimiento del jugador en el **historial**: se lo notifica el resolutor de movimiento durante la resolución (no observa al tablero — ver §8 de `03`).
- Un **nivel** es la "receta" para crear un **tablero** en su configuración inicial.
- El **tablero** y el **estado del juego** son consultados por la **vista** para dibujar el HUD y el board.
- El **tablero** y el **estado del juego** son modificados por el **controlador**, nunca directamente por la vista.

---

## 5. Decisiones de diseño abiertas (a definir antes del UML)

Estas son decisiones que conviene cerrar ahora para que el UML quede consistente. Marcadas con propuesta inicial — **necesito tu validación**.

### 5.1 Separación entre Celda y Entidad
**Decisión:** ✅ mantener dos jerarquías separadas (`Celda` para el piso, `Entidad` para lo que se mueve).
**Razón:** son cosas conceptualmente distintas. Una celda no se mueve nunca; una entidad sí. Mezclarlas obligaría a que cada celda "supiera" si tiene algo encima, lo cual es responsabilidad del tablero. Además permite que en una misma posición coexistan, por ejemplo, una celda destino y una caja encima (estado típico de victoria parcial).

### 5.2 Cerrojos y muros: cardinalidad
**Decisión:** ✅ cada **caja llave** tiene asignado un **cerrojo específico**. Al colocarla sobre ese cerrojo, se abre el muro cerrado asociado (que puede estar compuesto por varias celdas contiguas). Relación: cada cerrojo pertenece a un muro en particular, y cada caja llave responde a un cerrojo específico.

**Caso borde — muro cerrándose con entidad encima:** ✅ resuelto **bloqueando el empuje**. Una caja llave solo puede salir de su cerrojo si **ninguna celda del muro asociado** tiene una entidad encima (jugador o caja). Si el muro está ocupado, el empuje queda bloqueado: ni la caja llave ni el jugador se mueven. Como consecuencia, **una caja llave nunca puede ser frágil** (si pudiera romperse, eso cerraría el muro de golpe sin chequear ocupación).

### 5.3 Recarga de undos
**Decisión:** ✅ se cuentan como **consecutivos** mientras el jugador no haga ningún movimiento entre ellos. Apenas el jugador mueve una vez, el contador de undos consecutivos se reinicia a 0 (vuelve a tener 3 disponibles).


### 5.4 Fórmula del puntaje
**Decisión:** ✅ sistema híbrido **sin metadata por nivel** (el `.txt` no necesita declarar un "óptimo"). El puntaje es un valor numérico que se traduce en estrellas con hard-caps por uso de undos.

**Fórmula:**

```
score = max(0, 1000 − 3·movimientos − 5·empujes − 100·undos)
```

**Estrellas:**

| Estrellas | Condición |
|-----------|-----------|
| 3★ | undos = 0 **Y** score ≥ 600 |
| 2★ | undos ≤ 2 **Y** score ≥ 300 |
| 1★ | completó el nivel |

**Razón:** se descartó la alternativa "par por nivel" (eficiencia = par_movimientos / movimientos_reales) porque implicaba resolver manualmente cada nivel antes de publicarlo, trabajo de diseño no exigido por el enunciado. El sistema híbrido cumple el requisito ("puntaje en función de movimientos, empujes y undos") y penaliza fuertemente el uso de undos vía los hard-caps de estrellas.

### 5.5 Caja frágil y deslizamiento
**Decisión:** ✅ un empuje del jugador descuenta **una sola** unidad de resistencia, sin importar cuántas celdas se deslice la caja después.
**Razón:** lo contrario haría el juego imprevisible (el jugador no sabe cuántas celdas se va a deslizar la caja). Implícito en R8 y R13.
**Nota de representación:** la resistencia se puede modelar como un entero que indica la cantidad de empujes restantes antes de romperse.

### 5.6 Movimiento simultáneo y orden de resolución
**Decisión:** ✅ cada acción del jugador se resuelve completamente antes de aceptar la siguiente entrada. No hay paralelismo. El deslizamiento ocurre como parte de la resolución del empuje, antes de devolver el control al jugador.

### 5.7 ¿El jugador puede pisar una celda destino o cerrojo vacíos?
**Decisión:** ✅ el jugador puede pisar celdas destino y cerrojo vacías sin efecto (solo las cajas activan esos efectos sobre ellas: destino cuenta para victoria, cerrojo activa el muro). **No** puede atravesar muros cerrados; sí puede pisar muros abiertos (cuando la caja llave asociada está sobre el cerrojo).

### 5.8 Energía, caja pesada y botella (funcionalidad adicional) — y baja del tiempo límite
**Decisión:** ✅ se reemplaza el **tiempo límite** por un sistema de **energía**:

- El jugador tiene energía (máximo 20, arranca llena). Moverse cuesta 1; empujar cuesta el costo de la caja (normal 1, **pesada 3**), modelado con un **Strategy** (`EstrategiaEmpuje`).
- Si no alcanza para empujar una caja, el empuje no se hace (puede seguir caminando). Si no alcanza ni para moverse (energía 0), el nivel **se reinicia solo** (R24).
- La **botella de agua** (`Item`) repone +12 al recogerla.

**Cómo se decide el reinicio por falta de energía (sin condicionales):** el resolutor devuelve un `EventoJuego` y cada valor del enum sabe **su consecuencia** (`aplicarConsecuencia`): `SIN_ENERGIA` reinicia el nivel. El controlador no usa un `if` sobre el evento.

**Razón de bajar el tiempo límite:** mantenerlo junto con la energía daba dos modalidades de "presión temporal" redundantes; la energía + caja pesada + botella es una funcionalidad adicional más rica y mejor integrada al dominio (sin hilos de UI). La **visión limitada** se mantiene como la otra funcionalidad adicional.

---

## 6. Lo que **no** modela este documento (queda fuera de alcance del modelo del dominio)

Estas cosas existen en el proyecto pero pertenecen a otras capas o a fases posteriores:

- **Renderizado gráfico**: cómo se dibuja una celda o una caja en pantalla.
- **Sonidos**: qué clip suena al empujar.
- **Input**: cómo se traduce una tecla del teclado a una dirección.
- **Persistencia**: cómo se guarda el progreso entre sesiones (en este TI no hay).
- **Carga de archivos `.txt`**: el formato y el parser se diseñan en el documento de la Fase 2.
- **Patrones de implementación** (Command, Memento, Observer, etc.): se introducen al pasar a UML y código. Acá solo describimos el dominio.

---

## 7. Checklist de validación

Antes de pasar al UML, confirmar:

- [x] El glosario cubre todos los términos del enunciado del TI.
- [x] Las reglas R1–R25 cubren todos los requisitos funcionales del PDF y las funcionalidades adicionales (visión limitada y energía/caja pesada/botella; el tiempo límite fue retirado).
- [x] Las decisiones de §5 están aprobadas o ajustadas.
- [x] No hay solapamiento de responsabilidades entre entidades.
- [x] Cada entidad sabe lo mínimo necesario para tomar sus decisiones (Information Expert).

Una vez aprobado este documento, pasamos a `02-diagrama-clases.md`.
