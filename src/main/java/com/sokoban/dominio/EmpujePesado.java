package com.sokoban.dominio;

/** Costo de empuje elevado (caja pesada). */
public class EmpujePesado implements EstrategiaEmpuje {

    private static final int COSTO = 3;

    @Override
    public int costo() {
        return COSTO;
    }
}
