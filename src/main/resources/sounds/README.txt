Efectos de sonido (opcionales)
==============================

Coloca aqui archivos WAV (PCM, p. ej. 16-bit) con EXACTAMENTE estos nombres. Si
un archivo no esta, ese evento simplemente no suena (best-effort), asi que podes
ir agregando sonidos de a uno.

  movimiento.wav   -> el jugador se mueve a una celda libre
  empuje.wav       -> el jugador empuja una caja
  undo.wav         -> se deshace un movimiento (tecla U)
  reinicio.wav     -> se reinicia el nivel (tecla R)
  victoria.wav     -> se completa el nivel
  derrota.wav      -> cartel de derrota (sin energia o caja fragil rota)
  sin_energia.wav  -> reservado (las derrotas usan derrota.wav)

Total: 7 sonidos.

Notas:
- Formato WAV/PCM (no MP3): es lo que soporta javax.sound.sampled del JDK.
- Sonidos cortos (decimas de segundo) para movimiento/empuje.
- Licencias: ver ../ASSETS-LICENCIAS.txt
