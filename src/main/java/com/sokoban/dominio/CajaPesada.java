package com.sokoban.dominio;

/**
 * Caja pesada: se empuja con las mismas reglas que una normal (colisiones,
 * deslizamiento, victoria), pero su empuje cuesta mas energia. La diferencia se
 * encapsula en su EstrategiaEmpuje (EmpujePesado), no en condicionales.
 */
public class CajaPesada extends Caja {

    public CajaPesada(Posicion posicion) {
        super(posicion, new EmpujePesado());
    }

    @Override
    public String clavePresentacion() {
        return "CAJA_PESADA";
    }
}
