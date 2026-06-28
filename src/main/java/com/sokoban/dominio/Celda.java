package com.sokoban.dominio;

/**
 * Piso de una posicion. NUNCA se mueve. Define que pasa cuando una entidad
 * intenta entrar (transitable o no, efecto de entrada/salida).
 *
 * El despacho por tipo se resuelve por polimorfismo: cada subtipo redefine lo
 * suyo. No existe ningun instanceof/switch para preguntar "que celda sos".
 */
public abstract class Celda {

    private final Posicion posicion;

    protected Celda(Posicion posicion) {
        this.posicion = posicion;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    /** Si una entidad puede ubicarse en esta celda. Polimorfico. */
    public abstract boolean esTransitable();

    /** Si esta celda cuenta para la condicion de victoria (solo Destino). */
    public boolean cuentaParaVictoria() {
        return false;
    }

    /**
     * Efecto al entrar una caja. Hook polimorfico: por defecto no hace nada;
     * Cerrojo lo usa para activarse via doble despacho con la caja.
     */
    public void efectoEntrada(Caja caja) {
        // Sin efecto por defecto.
    }

    /** Efecto al salir una caja. Hook simetrico a efectoEntrada. */
    public void efectoSalida(Caja caja) {
        // Sin efecto por defecto.
    }

    /**
     * Si una entidad puede SALIR de esta celda. Hook polimorfico: por defecto si.
     * El Cerrojo lo redefine para aplicar R11 (la caja llave no puede salir si el
     * muro asociado esta ocupado), sin que nadie pregunte el tipo/estado con un if.
     */
    public boolean permiteSalida(Tablero tablero) {
        return true;
    }

    /** Si una caja que entra debe deslizarse (solo TerrenoResbaladizo). */
    public boolean provocaDeslizamiento() {
        return false;
    }

    /** Se registra para enlaces (solo Cerrojo y MuroAbiertoCerrado lo hacen). */
    public void registrarEnlaces(RegistroEnlaces registro) {
        // Por defecto una celda no participa de enlaces.
    }

    /** Guarda su estado mutable en el memento. Por defecto las celdas son inmutables. */
    public void capturarEstadoEn(MementoTablero memento) {
        // Sin estado mutable por defecto.
    }

    /** Restaura su estado mutable desde el memento. */
    public void restaurarEstadoDe(MementoTablero memento) {
        // Sin estado mutable por defecto.
    }

    /**
     * Token neutro de presentacion: la View lo mapea a una imagen/color.
     * Mantiene el dominio sin conocer Swing y sin instanceof.
     */
    public abstract String clavePresentacion();
}
