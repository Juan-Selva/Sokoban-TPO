package com.sokoban.comandos;

import com.sokoban.partida.Juego;

/** Deshace 5 movimientos en un salto (hasta 3 usos consecutivos). */
public class UndoCommand implements Command {

    @Override
    public void ejecutar(Juego juego) {
        juego.deshacer();
    }
}
