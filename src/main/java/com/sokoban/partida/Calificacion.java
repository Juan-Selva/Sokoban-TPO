package com.sokoban.partida;

/**
 * Calificacion por estrellas de un nivel completado (§5.4). Cada valor conoce su
 * propio criterio (polimorfismo): se elige el primero que aplica, recorriendo de
 * mejor a peor. No es logica del juego Sokoban, sino calculo de negocio del
 * puntaje, por lo que las comparaciones aritmeticas de umbrales son admisibles.
 */
public enum Calificacion {

    TRES_ESTRELLAS(3) {
        @Override
        boolean aplica(int score, int undos) {
            return undos == 0 && score >= 600;
        }
    },
    DOS_ESTRELLAS(2) {
        @Override
        boolean aplica(int score, int undos) {
            return undos <= 2 && score >= 300;
        }
    },
    UNA_ESTRELLA(1) {
        @Override
        boolean aplica(int score, int undos) {
            return true;
        }
    };

    private final int estrellas;

    Calificacion(int estrellas) {
        this.estrellas = estrellas;
    }

    public int getEstrellas() {
        return estrellas;
    }

    abstract boolean aplica(int score, int undos);

    /** Mejor calificacion aplicable; los valores estan declarados de mejor a peor. */
    public static Calificacion de(int score, int undos) {
        for (Calificacion calificacion : values()) {
            if (calificacion.aplica(score, undos)) {
                return calificacion;
            }
        }
        return UNA_ESTRELLA;
    }
}
