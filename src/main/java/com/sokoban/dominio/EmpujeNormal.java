package com.sokoban.dominio;

/** Costo de empuje estandar (cajas normales, fragiles y llave). */
public class EmpujeNormal implements EstrategiaEmpuje {

    private static final int COSTO = 1;

    @Override
    public int costo() {
        return COSTO;
    }
}
