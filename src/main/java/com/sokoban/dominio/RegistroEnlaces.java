package com.sokoban.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Recolecta los elementos enlazables (cerrojos, muros, cajas llave) durante la
 * construccion del tablero y los vincula uno a uno por orden de aparicion.
 *
 * Cada elemento se registra a si mismo de forma polimorfica (registrarEnlaces),
 * por lo que aqui no hay ningun instanceof: el cerrojo i-esimo se asocia al muro
 * i-esimo, y la caja llave i-esima a ese cerrojo (correspondencia 1 a 1, §5.2).
 */
public class RegistroEnlaces {

    private final List<Cerrojo> cerrojos = new ArrayList<>();
    private final List<MuroAbiertoCerrado> muros = new ArrayList<>();
    private final List<CajaLlave> llaves = new ArrayList<>();

    public void agregarCerrojo(Cerrojo cerrojo) {
        cerrojos.add(cerrojo);
    }

    public void agregarMuro(MuroAbiertoCerrado muro) {
        muros.add(muro);
    }

    public void agregarLlave(CajaLlave llave) {
        llaves.add(llave);
    }

    public void enlazar() {
        int cerrojosConMuro = Math.min(cerrojos.size(), muros.size());
        for (int i = 0; i < cerrojosConMuro; i++) {
            cerrojos.get(i).setMuro(muros.get(i));
        }
        int llavesConCerrojo = Math.min(cerrojos.size(), llaves.size());
        for (int i = 0; i < llavesConCerrojo; i++) {
            llaves.get(i).setCerrojoAsignado(cerrojos.get(i));
        }
    }
}
