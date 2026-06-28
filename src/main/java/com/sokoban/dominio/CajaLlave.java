package com.sokoban.dominio;

/**
 * Caja que, al quedar sobre SU cerrojo asignado, abre el muro vinculado (R10).
 * Queda exenta de la condicion de victoria (R17). La correspondencia llave-cerrojo
 * es uno a uno: solo reacciona ante su cerrojo asignado.
 */
public class CajaLlave extends Caja {

    private Cerrojo cerrojoAsignado;

    public CajaLlave(Posicion posicion) {
        super(posicion);
    }

    public void setCerrojoAsignado(Cerrojo cerrojo) {
        this.cerrojoAsignado = cerrojo;
    }

    @Override
    public boolean cuentaParaVictoria() {
        return false;
    }

    @Override
    public void registrarEnlaces(RegistroEnlaces registro) {
        registro.agregarLlave(this);
    }

    @Override
    public void alEntrarEnCerrojo(Cerrojo cerrojo) {
        if (cerrojo == cerrojoAsignado) {
            cerrojo.alColocarLlave();
        }
    }

    @Override
    public void alSalirDeCerrojo(Cerrojo cerrojo) {
        if (cerrojo == cerrojoAsignado) {
            cerrojo.alRetirarLlave();
        }
    }

    @Override
    public String clavePresentacion() {
        return "CAJA_LLAVE";
    }
}
