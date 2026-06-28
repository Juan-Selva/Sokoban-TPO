package com.sokoban.dominio;

/**
 * Celda con dos comportamientos opuestos segun su estado interno (patron State).
 * Cerrado bloquea; abierto deja pasar. NO hay if(abierto): la decision (y la
 * apariencia) se delegan al EstadoMuro actual. Las transiciones las dispara el
 * Cerrojo asociado. Por defecto arranca cerrado (ver 01-modelo-dominio §1.11).
 */
public class MuroAbiertoCerrado extends Celda {

    private EstadoMuro estado;

    public MuroAbiertoCerrado(Posicion posicion) {
        super(posicion);
        this.estado = new MuroCerrado();
    }

    public void abrir() {
        this.estado = new MuroAbierto();
    }

    public void cerrar() {
        this.estado = new MuroCerrado();
    }

    @Override
    public boolean esTransitable() {
        return estado.esTransitable();
    }

    @Override
    public String clavePresentacion() {
        return estado.clavePresentacion();
    }

    @Override
    public void registrarEnlaces(RegistroEnlaces registro) {
        registro.agregarMuro(this);
    }

    @Override
    public void capturarEstadoEn(MementoTablero memento) {
        memento.guardarEstadoMuro(this, estado);
    }

    @Override
    public void restaurarEstadoDe(MementoTablero memento) {
        this.estado = memento.estadoMuroDe(this);
    }
}
