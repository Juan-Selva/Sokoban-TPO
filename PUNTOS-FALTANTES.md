# Puntos faltantes

Lista de lo que queda pendiente para cerrar el trabajo. El código ya está
preparado para recibir imágenes y sonidos: solo hay que **colocar los archivos**
con el nombre exacto en la carpeta indicada (no hay que tocar código).

---

## 1) Imágenes de las entidades (sprites)

- **Carpeta:** `src/main/resources/sprites/`
- **Formato:** PNG, recomendado 48×48 px (cuadrado, fondo transparente).
- **Nombre exacto:** cada archivo se llama igual que la "clave de presentación"
  del elemento (en mayúsculas). Si falta un archivo, ese elemento se dibuja con
  color (fallback automático), así que se pueden ir agregando de a uno.

### Celdas (fondo del tablero)

| Elemento | Archivo a agregar |
|---|---|
| Espacio vacío / piso | `CELDA_VACIA.png` |
| Pared | `PARED.png` |
| Casilla de destino | `DESTINO.png` |
| Terreno resbaladizo | `TERRENO_RESBALADIZO.png` |
| Casillero cerrojo | `CERROJO.png` |
| Muro abierto | `MURO_ABIERTO.png` |
| Muro cerrado | `MURO_CERRADO.png` |

### Entidades (se dibujan encima de la celda)

| Elemento | Archivo a agregar |
|---|---|
| Jugador (sokoban) | `JUGADOR.png` |
| Caja normal | `CAJA_NORMAL.png` |
| Caja frágil | `CAJA_FRAGIL.png` |
| Caja llave | `CAJA_LLAVE.png` |

> Total: **11 imágenes**. Sobre la caja frágil se sigue mostrando el número de
> resistencia restante encima del sprite.
>
> Importante: los assets deben ser de **dominio público** y hay que registrar su
> origen y licencia (para el informe).

---

## 2) Sonidos de las acciones

- **Carpeta:** `src/main/resources/sounds/`
- **Formato:** WAV (PCM, p. ej. 16-bit). No usar MP3 (es lo que soporta
  `javax.sound.sampled` del JDK).
- **Nombre exacto:** si falta un archivo, esa acción simplemente no suena
  (best-effort), así que se pueden ir agregando de a uno.

| Acción del juego | Archivo a agregar |
|---|---|
| El jugador se mueve a una celda libre | `movimiento.wav` |
| El jugador empuja una caja | `empuje.wav` |
| Se deshace un movimiento (Undo / tecla U) | `undo.wav` |
| Se reinicia el nivel (tecla R) | `reinicio.wav` |
| Se completa el nivel (victoria) | `victoria.wav` |

> Total: **5 sonidos**. Sonidos cortos (décimas de segundo) para
> movimiento/empuje. Deben ser de **dominio público** (registrar origen/licencia).

---

## 3) Informe técnico

- Documento con las **decisiones de diseño y su justificación**.
- Base ya disponible en la carpeta `docs/` (modelo de dominio, patrones GoF,
  relaciones/dependencias, justificación de diseño) para apoyarse.
- Debe incluir, como mínimo:
  - Arquitectura general (MVC) y separación de capas.
  - Principios aplicados (SOLID, GRASP) con ejemplos del código.
  - Patrones GoF usados y por qué (Builder, Factory Method, Decorator, Facade,
    Command, Memento, Observer, State, Template Method).
  - Las dos funcionalidades adicionales (visión limitada y tiempo límite) y los
    patrones que implican.
  - Criterio de puntaje elegido.
  - **Declaración del uso de herramientas de IA** (obligatorio según la consigna).
- Exportar a **PDF** para la entrega.

---

## 4) Video gameplay

- Video que muestre **todas las funcionalidades** implementadas:
  - Movimiento y empuje de cajas.
  - Caja normal, frágil (rotura) y llave (abre el muro con el cerrojo).
  - Terreno resbaladizo (deslizamiento).
  - Deshacer (Undo) y reiniciar nivel.
  - Resumen de puntaje al completar un nivel.
  - Las dos funcionalidades adicionales: **tiempo límite** (nivel 3) y
    **visión reducida** (nivel 4).
  - HUD (movimientos, empujes, nivel, undos, tiempo).

---

## 5) Diagrama de clases UML

- Diagrama de clases en **StarUML** (entregable).
- Actualizar/limpiar el diagrama actual (`docs/04-diagrama-clases.puml`):
  - **Quitar** el patrón Visitor (ya no se usa; el renderizado va por
    `clavePresentacion()`).
  - **Agregar** el patrón State del `Cerrojo` (`EstadoCerrojo`, `CerrojoSinLlave`,
    `CerrojoConLlave`).
  - **Agregar** las clases de puntaje (`ResultadoNivel`, `Calificacion`).
  - **Agregar** `EventoJuego` (selección polimórfica del sonido) y la dependencia
    `ResolutorMovimiento` / `Juego` → `EventoJuego`.
- Exportar el diagrama (imagen) para incluir en el informe y la entrega.

---
