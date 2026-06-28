package com.sokoban.dominio;

/**
 * Piso transitable que abre su muro asociado cuando una caja llave queda encima
 * (R10). Relacion uno a uno: cada cerrojo pertenece a un muro y responde a una
 * caja llave especifica.
 *
 * El cerrojo no pregunta "que tipo de caja entro": usa doble despacho
 * (efectoEntrada -> caja.alEntrarEnCerrojo) para que solo la CajaLlave reaccione.
 */
public class Cerrojo extends Celda {

    private MuroAbiertoCerrado muro;
    private EstadoCerrojo estado = new CerrojoSinLlave();

    public Cerrojo(Posicion posicion) {
        super(posicion);
    }

    public void setMuro(MuroAbiertoCerrado muro) {
        this.muro = muro;
    }

    public MuroAbiertoCerrado getMuro() {
        return muro;
    }

    public boolean estaActivo() {
        return estado.estaActivo();
    }

    /** Llamado por la caja llave (doble despacho) al quedar sobre el cerrojo. */
    public void alColocarLlave() {
        this.estado = new CerrojoConLlave();
        muro.abrir();
    }

    /** Llamado por la caja llave al salir del cerrojo. */
    public void alRetirarLlave() {
        this.estado = new CerrojoSinLlave();
        muro.cerrar();
    }

    @Override
    public boolean esTransitable() {
        return true;
    }

    /** R11: delega en el estado si la caja llave que tiene encima puede salir. */
    @Override
    public boolean permiteSalida(Tablero tablero) {
        return estado.permiteSalidaLlave(this, tablero);
    }

    @Override
    public void efectoEntrada(Caja caja) {
        caja.alEntrarEnCerrojo(this);
    }

    @Override
    public void efectoSalida(Caja caja) {
        caja.alSalirDeCerrojo(this);
    }

    @Override
    public String clavePresentacion() {
        return "CERROJO";
    }

    @Override
    public void registrarEnlaces(RegistroEnlaces registro) {
        registro.agregarCerrojo(this);
    }

    @Override
    public void capturarEstadoEn(MementoTablero memento) {
        memento.guardarEstadoCerrojo(this, estado);
    }

    @Override
    public void restaurarEstadoDe(MementoTablero memento) {
        this.estado = memento.estadoCerrojoDe(this);
    }
}
