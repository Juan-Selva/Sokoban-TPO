Sprites del juego (opcionales)
==============================

Coloca aqui imagenes PNG con EXACTAMENTE estos nombres. Si un archivo no esta,
el juego dibuja ese elemento con color (fallback automatico), asi que podes ir
agregando sprites de a uno.

Tamano recomendado: 48 x 48 px (cuadrado, fondo transparente).

Celdas (fondo):
  CELDA_VACIA.png
  PARED.png
  DESTINO.png
  TERRENO_RESBALADIZO.png
  CERROJO.png
  MURO_ABIERTO.png
  MURO_CERRADO.png

Entidades (se dibujan encima de la celda):
  JUGADOR.png
  CAJA_NORMAL.png
  CAJA_FRAGIL.png
  CAJA_LLAVE.png
  CAJA_PESADA.png

Items (se dibujan encima de la celda):
  BOTELLA_AGUA.png

Total: 13 imagenes.

Notas:
- Los nombres son la "clave de presentacion" del dominio; no los cambies.
- Sobre CAJA_FRAGIL se sigue dibujando el numero de resistencia restante.
- Licencias: ver ../ASSETS-LICENCIAS.txt
