package com.sokoban.partida;

/**
 * Puerto de presentacion para avisos de la partida dirigidos al jugador. Lo
 * implementa la capa de control/vista. {@link EventoJuego} lo usa por doble
 * despacho ({@code presentar}) para avisar la derrota, sin que la capa partida
 * conozca Swing ni se necesite un condicional por tipo de evento.
 */
public interface PresentadorPartida {

    /** El jugador perdio el nivel (p. ej. sin energia o caja fragil rota). */
    void alPerderNivel();
}
