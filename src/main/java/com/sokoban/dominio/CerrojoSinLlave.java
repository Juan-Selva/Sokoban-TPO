package com.sokoban.dominio;

/** Estado sin llave encima: no restringe la salida y el cerrojo no esta activo. */
public class CerrojoSinLlave implements EstadoCerrojo {

    @Override
    public boolean permiteSalidaLlave(Cerrojo cerrojo, Tablero tablero) {
        return true;
    }

    @Override
    public boolean estaActivo() {
        return false;
    }
}
