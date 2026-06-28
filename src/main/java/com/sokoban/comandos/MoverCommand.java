package com.sokoban.comandos;

import com.sokoban.dominio.Direccion;
import com.sokoban.partida.Juego;

/** Mueve al jugador en una direccion. */
public class MoverCommand implements Command {

    private final Direccion direccion;

    public MoverCommand(Direccion direccion) {
        this.direccion = direccion;
    }

    @Override
    public void ejecutar(Juego juego) {
        juego.mover(direccion);
    }
}
