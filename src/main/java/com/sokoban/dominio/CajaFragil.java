package com.sokoban.dominio;

/**
 * Caja con resistencia finita: pierde un punto por cada empuje EFECTIVO (R8),
 * no por cada celda deslizada (R13, decision §5.5). Al llegar a 0 esta rota y
 * el ResolutorMovimiento la quita del tablero (R9). Una caja llave nunca puede
 * ser fragil (por eso son subtipos hermanos, no se combinan).
 */
public class CajaFragil extends Caja {

    private int resistencia;

    public CajaFragil(Posicion posicion, int resistencia) {
        super(posicion);
        this.resistencia = resistencia;
    }

    public int getResistencia() {
        return resistencia;
    }

    public void recibirEmpuje() {
        resistencia--;
    }

    public boolean estaRota() {
        return resistencia <= 0;
    }

    @Override
    public boolean intentarEmpujar(Direccion direccion) {
        boolean movida = moverUnaCelda(direccion);
        if (movida) {
            recibirEmpuje();
        }
        return movida;
    }

    @Override
    public boolean debeSerEliminada() {
        return estaRota();
    }

    @Override
    public boolean reiniciaNivelAlEliminarse() {
        return estaRota();
    }

    @Override
    public void capturarEstadoEn(MementoTablero memento) {
        super.capturarEstadoEn(memento);
        memento.guardarResistencia(this, resistencia);
    }

    @Override
    public void restaurarEstadoDe(MementoTablero memento) {
        super.restaurarEstadoDe(memento);
        this.resistencia = memento.resistenciaDe(this);
    }

    @Override
    public String clavePresentacion() {
        return "CAJA_FRAGIL";
    }

    @Override
    public String etiqueta() {
        return String.valueOf(resistencia);
    }
}
