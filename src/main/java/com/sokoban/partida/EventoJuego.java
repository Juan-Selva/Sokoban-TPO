package com.sokoban.partida;

/**
 * Resultado de una accion del jugador, expresado como token neutro. Cada evento
 * conoce su clave de sonido (misma idea que clavePresentacion() en el dominio):
 * el modelo no reproduce audio ni decide volumenes; solo expone "que paso" y la
 * vista lo traduce a un efecto. Asi la seleccion del sonido es polimorfica (cada
 * valor sabe su clave) y la capa de presentacion no necesita condicionales para
 * adivinar el evento comparando contadores.
 */
public enum EventoJuego {

    NADA(""),
    MOVIMIENTO("movimiento"),
    EMPUJE("empuje"),
    UNDO("undo"),
    REINICIO("reinicio"),
    SIN_ENERGIA("sin_energia") {
        @Override
        public void aplicarConsecuencia(Juego juego) {
            // Sin energia para seguir: el nivel se reinicia.
            juego.reiniciar();
        }
    };

    private final String claveSonido;

    EventoJuego(String claveSonido) {
        this.claveSonido = claveSonido;
    }

    /** Clave del efecto a reproducir; vacia cuando el evento no suena. */
    public String getClaveSonido() {
        return claveSonido;
    }

    /**
     * Consecuencia de este evento sobre la partida. Por defecto no hace nada;
     * cada valor que deba alterar el nivel la redefine. Asi la reaccion se elige
     * por polimorfismo (cada evento sabe que hacer), sin condicionales en quien
     * lo recibe.
     */
    public void aplicarConsecuencia(Juego juego) {
        // Sin consecuencia por defecto.
    }
}
