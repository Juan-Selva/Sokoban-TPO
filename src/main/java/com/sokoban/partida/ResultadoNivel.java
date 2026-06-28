package com.sokoban.partida;

/**
 * Resumen inmutable de un nivel completado: contadores, puntaje y calificacion
 * (§1.16, §5.4). El score es aritmetica pura (max + restas) y la calificacion la
 * decide cada valor del enum Calificacion. No interviene en la logica del juego;
 * es el dato que la vista muestra al ganar.
 */
public final class ResultadoNivel {

    private final int movimientos;
    private final int empujes;
    private final int undos;
    private final int score;
    private final Calificacion calificacion;

    public ResultadoNivel(int movimientos, int empujes, int undos) {
        this.movimientos = movimientos;
        this.empujes = empujes;
        this.undos = undos;
        this.score = Math.max(0, 1000 - 3 * movimientos - 5 * empujes - 100 * undos);
        this.calificacion = Calificacion.de(score, undos);
    }

    public int getMovimientos() {
        return movimientos;
    }

    public int getEmpujes() {
        return empujes;
    }

    public int getUndos() {
        return undos;
    }

    public int getScore() {
        return score;
    }

    public Calificacion getCalificacion() {
        return calificacion;
    }

    public int getEstrellas() {
        return calificacion.getEstrellas();
    }
}
