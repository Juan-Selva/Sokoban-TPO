package com.sokoban.comandos;

import com.sokoban.partida.Juego;

/**
 * Patron Command: encapsula una accion del jugador. Desacopla el input (tecla o
 * boton) de la ejecucion, que es uniforme a traves de la fachada Juego.
 */
public interface Command {
    void ejecutar(Juego juego);
}
