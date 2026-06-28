package com.sokoban.dominio;

/**
 * Lo que esta "encima" del piso y puede moverse: el jugador y las cajas.
 * Conoce su posicion (mutable) y al tablero que consulta para decidir (Information
 * Expert). Notifica entrada/salida a la celda mediante doble despacho, de modo
 * que el tablero no necesita preguntar "sos caja o jugador" con instanceof.
 */
public abstract class Entidad {

    protected Posicion posicion;
    protected Tablero tablero;

    protected Entidad(Posicion posicion) {
        this.posicion = posicion;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    /** El tablero se inyecta al agregar la entidad (asociacion del UML). */
    public void vincular(Tablero tablero) {
        this.tablero = tablero;
    }

    /** Hook: por defecto una entidad no dispara efectos de celda. */
    public void notificarEntrada(Celda celda) {
        // El jugador no dispara efectos de celda (ver modelo §5.7).
    }

    public void notificarSalida(Celda celda) {
        // Idem.
    }

    /**
     * Se agrega a si misma al tablero en la posicion correcta (doble despacho):
     * el jugador se fija como jugador y las cajas se agregan a la lista de cajas,
     * sin que el constructor del nivel use instanceof.
     */
    public abstract void agregarseA(Tablero tablero);

    /** Se registra para enlaces (solo la caja llave lo hace). */
    public void registrarEnlaces(RegistroEnlaces registro) {
        // Por defecto una entidad no participa de enlaces.
    }

    /** Guarda su estado mutable (posicion) en el memento. */
    public void capturarEstadoEn(MementoTablero memento) {
        memento.guardarPosicion(this, posicion);
    }

    /** Restaura su estado mutable desde el memento. */
    public void restaurarEstadoDe(MementoTablero memento) {
        this.posicion = memento.posicionDe(this);
    }

    /**
     * Token neutro de presentacion: la View lo mapea a una imagen/color.
     * El dominio no conoce Swing y no se usa instanceof.
     */
    public abstract String clavePresentacion();

    /**
     * Texto opcional que la vista puede dibujar sobre la entidad (p. ej. la
     * resistencia de la caja fragil). Vacio por defecto: hook polimorfico que
     * evita que la vista pregunte el tipo concreto.
     */
    public String etiqueta() {
        return "";
    }
}
