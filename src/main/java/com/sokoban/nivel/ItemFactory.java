package com.sokoban.nivel;

import com.sokoban.dominio.BotellaAgua;
import com.sokoban.dominio.Item;
import com.sokoban.dominio.Posicion;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory Method para items recogibles. Igual que las otras factories: registro
 * Map sin switch. 'conoce' permite al lector/constructor enrutar un caracter a
 * item usando el registro, sin inspeccionar tipos.
 */
public class ItemFactory {

    private final Map<Character, Function<Posicion, Item>> creadores = new HashMap<>();

    public ItemFactory() {
        creadores.put('B', BotellaAgua::new);
    }

    public boolean conoce(char caracter) {
        return creadores.containsKey(caracter);
    }

    public Item crear(char caracter, Posicion posicion) {
        return creadores.get(caracter).apply(posicion);
    }
}
