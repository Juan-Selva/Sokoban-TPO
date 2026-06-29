# Falta terminar (desarrollo)

Lista de lo que **todavía falta en el producto** según la consigna del TI (PDF) y el estado actual del código.  
No incluye trámites de entrega (mail, video, informe PDF, sección Equipo del README).

---

## Ya está hecho (referencia rápida)

- Dominio completo: celdas, entidades, ítems, cerrojo/muro con **State**, undo, puntaje, MVC.
- Funcionalidades adicionales: **visión limitada** (Decorator) y **energía + caja pesada + botella** (Strategy + ítems).
- Imágenes y sonidos: **código y carpetas listos**; fallback a color/silencio si falta el archivo.
- **7 niveles** `.txt` en `Controlador.RUTAS_NIVELES`, tests pasando, JAR con `mvn package`.

### Mapa de niveles (qué demuestra cada uno)

| Nivel | Mecánicas destacadas |
|-------|----------------------|
| 1 | Dos cajas normales |
| 2 | Caja normal + terreno resbaladizo |
| 3 | Cerrojo/muro, caja llave, botellas, caja normal, resbaladizo |
| 4 | Cerrojo/muro, caja llave, botellas + caja normal (`$`) |
| 5 | Caja frágil (`F`) |
| 6 | Caja pesada (`P`) + costo de energía |
| 7 | Visión limitada (`VISION 2`) + exploración |

---
## Lo que falta:

## 1. Diagrama de clases UML

- Exportar desde **StarUML** (o herramienta equivalente) un diagrama **alineado con el código actual**.
- Debe reflejar, como mínimo:
  - Jerarquías `Celda` / `Entidad` / `Item`.
  - **State** en muro y cerrojo.
  - **Strategy** (`EstrategiaEmpuje`).
  - `EventoJuego`, `ResultadoNivel`, `Calificacion`.
  - Paquetes MVC (`controller`, `view`, `dominio`, `partida`, `nivel`, `comandos`).
  - **Sin Visitor**; renderizado vía `clavePresentacion()`.
  - **Sin** `Temporizador` / `TiempoLimiteDecorator` (reemplazados por energía).
- Guardar la imagen exportada en `docs/UML/` (o donde corresponda para la entrega).

---

## 2. Imágenes (sprites)

La consigna exige que **todos los elementos del tablero** tengan imagen. Hoy hay archivos `.png` placeholder (vacíos o inválidos): el juego **funciona con colores**, pero no cumple el requisito gráfico hasta poner sprites reales.

### Cómo agregarlas (no hay que tocar código)

1. Conseguir sprites **de dominio público** (anotar URL y licencia para el informe).
2. Editar/exportar cada uno como **PNG**, recomendado **48×48 px**, fondo transparente.
3. Copiarlos en:

   ```
   src/main/resources/sprites/
   ```

4. Cada archivo debe llamarse **exactamente** igual que la clave de presentación (mayúsculas):

   | Elemento | Archivo |
   |----------|---------|
   | Piso vacío | `CELDA_VACIA.png` |
   | Pared | `PARED.png` |
   | Destino | `DESTINO.png` |
   | Terreno resbaladizo | `TERRENO_RESBALADIZO.png` |
   | Cerrojo | `CERROJO.png` |
   | Muro abierto | `MURO_ABIERTO.png` |
   | Muro cerrado | `MURO_CERRADO.png` |
   | Jugador | `JUGADOR.png` |
   | Caja normal | `CAJA_NORMAL.png` |
   | Caja frágil | `CAJA_FRAGIL.png` |
   | Caja llave | `CAJA_LLAVE.png` |
   | Caja pesada | `CAJA_PESADA.png` |
   | Botella de agua | `BOTELLA_AGUA.png` |

   **Total: 13 imágenes.**  
   Hay slot para 11; **faltan crear** `CAJA_PESADA.png` y `BOTELLA_AGUA.png` en esa carpeta (y reemplazar los 11 placeholders por PNG reales).

5. Recompilar o volver a ejecutar:

   ```bash
   mvn clean package
   java -jar target/sokoban-1.0-SNAPSHOT.jar
   ```

   `PaletaPresentacion` carga `/sprites/<CLAVE>.png` automáticamente. Si un PNG falta o está corrupto, ese elemento sigue viéndose con **color + letra** (fallback).

6. Sobre `CAJA_FRAGIL`: el número de resistencia se dibuja **encima** del sprite.

7. Opcional: actualizar `src/main/resources/sprites/README.txt` para incluir `CAJA_PESADA` y `BOTELLA_AGUA` en la lista.

---

## 3. Sonidos

La consigna pide efectos de sonido en las acciones. La estructura ya está; faltan **archivos WAV reales** (los `.wav` actuales son placeholders vacíos).

### Cómo agregarlos (no hay que tocar código)

1. Conseguir efectos **de dominio público** (anotar origen/licencia).
2. Formato: **WAV PCM** (p. ej. 16-bit). **No usar MP3** (`javax.sound.sampled` del JDK no lo garantiza).
3. Copiarlos en:

   ```
   src/main/resources/sounds/
   ```

4. Nombres exactos:

   | Acción | Archivo |
   |--------|---------|
   | Movimiento a celda libre | `movimiento.wav` |
   | Empuje de caja | `empuje.wav` |
   | Undo | `undo.wav` |
   | Reinicio de nivel | `reinicio.wav` |
   | Victoria | `victoria.wav` |
   | Quedarse sin energía (reinicio auto) | `sin_energia.wav` |

   **Total: 6 sonidos.**  
   Hay slot para 5; falta **`sin_energia.wav`** y reemplazar los 5 existentes por WAV reales (`EventoJuego.SIN_ENERGIA` ya apunta a `sin_energia`).

5. Probar en el juego moviendo, empujando, deshaciendo, etc. Si falta un `.wav`, esa acción **no suena** y el juego sigue (best-effort).

6. Opcional: actualizar `src/main/resources/sounds/README.txt` para documentar `sin_energia.wav`.

---

## 4. Niveles

Cobertura de mecánicas respecto al dominio:

| Mecánica | ¿En código? | ¿En algún nivel? |
|----------|-------------|------------------|
| Caja normal | Sí | Sí (1, 2, 3, 7) |
| Terreno resbaladizo | Sí | Sí (2, 3) |
| Cerrojo + muro + caja llave | Sí | Sí (3, 4) |
| Caja frágil (`F`) | Sí | Sí (**nivel 5**) |
| Caja pesada (`P`) | Sí | Sí (**nivel 6**) |
| Botella (`B`) | Sí | Sí (3, 4) |
| Visión limitada (`VISION n`) | Sí | Sí (**nivel 7**, radio 2) |
| Energía / sin energía | Sí | Sí (3, 4 con botellas; 6 al empujar pesada) |

Todos los niveles cubren las mecánicas del dominio. El **nivel 4** incluye caja normal (`$`) con destino además de cerrojo/llave/botellas; ya no dispara victoria al cargar.

### Referencia — caracteres válidos en `.txt`

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

Directiva opcional (línea antes de la grilla): `VISION n`.

Los niveles van en `src/main/resources/levels/` y deben figurar en `Controlador.RUTAS_NIVELES`.

---

## 5. README del proyecto (desarrollo / uso)

El `README.md` raíz está **desactualizado** respecto al código:

- Menciona **tiempo límite** y `Temporizador` (eliminados).
- No documenta **`P`**, **`B`**, **`F`**, ni la funcionalidad de **energía**.
- Dice 4 niveles / estructura de paquetes incompleta (`Item`, `ItemFactory`, `ReproductorSonidos`, etc.).

Conviene actualizarlo para que quien clone el repo pueda ejecutar y entender el juego **sin contradecir la implementación**.

---

## 6. Registro de licencias de assets

La consigna exige assets de **dominio público**. Cuando agreguen sprites y sonidos reales, dejar en el repo (README, `sprites/README.txt`, `sounds/README.txt`, o archivo aparte) **origen + licencia** de cada archivo.

---

## Checklist resumido

- [x] Nivel con caja frágil (`nivel5.txt`)
- [x] Nivel con caja pesada (`nivel6.txt`)
- [x] Nivel con visión limitada (`nivel7.txt`, `VISION 2`)
- [x] Corregir `nivel4.txt` (caja normal objetivo; no victoria al cargar)
- [ ] UML exportado y consistente con el código actual
- [ ] 13 sprites PNG **reales** en `src/main/resources/sprites/` (incl. `CAJA_PESADA`, `BOTELLA_AGUA`)
- [ ] 6 sonidos WAV **reales** en `src/main/resources/sounds/` (incl. `sin_energia.wav`)
- [ ] Actualizar `README.md` (formato de niveles y funcionalidades)
- [ ] Anotar licencias de cada asset
