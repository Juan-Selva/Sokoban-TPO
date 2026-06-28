package com.sokoban.comandos;

import com.sokoban.partida.Juego;

/** Reinicia el nivel a su configuracion inicial. */
public class ReiniciarCommand implements Command {

    @Override
    public void ejecutar(Juego juego) {
        juego.reiniciar();
    }
}
