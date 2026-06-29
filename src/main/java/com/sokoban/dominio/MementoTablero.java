package com.sokoban.dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Snapshot del estado fisico mutable del tablero (parte del Memento del undo).
 * Lo construye y lo aplica el propio Tablero (Information Expert): cada celda y
 * cada entidad guardan/restauran SU estado de forma polimorfica (capturarEstadoEn
 * / restaurarEstadoDe), por lo que aqui no hay ningun instanceof.
 *
 * Una vez capturado se usa en modo solo-lectura; el contenedor de alto nivel
 * (EstadoJuegoMemento) lo trata como inmutable.
 */
public class MementoTablero {

    private final List<Caja> cajasPresentes;
    private final List<Item> itemsPresentes;
    private final Map<Entidad, Posicion> posiciones = new HashMap<>();
    private final Map<CajaFragil, Integer> resistencias = new HashMap<>();
    private final Map<MuroAbiertoCerrado, EstadoMuro> murosEstado = new HashMap<>();
    private final Map<Cerrojo, EstadoCerrojo> cerrojosEstado = new HashMap<>();
    private int energiaJugador;

    public MementoTablero(List<Caja> cajasPresentes, List<Item> itemsPresentes) {
        this.cajasPresentes = new ArrayList<>(cajasPresentes);
        this.itemsPresentes = new ArrayList<>(itemsPresentes);
    }

    public List<Caja> getCajasPresentes() {
        return cajasPresentes;
    }

    public List<Item> getItemsPresentes() {
        return itemsPresentes;
    }

    public void guardarPosicion(Entidad entidad, Posicion posicion) {
        posiciones.put(entidad, posicion);
    }

    public Posicion posicionDe(Entidad entidad) {
        return posiciones.get(entidad);
    }

    public void guardarResistencia(CajaFragil caja, int resistencia) {
        resistencias.put(caja, resistencia);
    }

    public int resistenciaDe(CajaFragil caja) {
        return resistencias.get(caja);
    }

    public void guardarEstadoMuro(MuroAbiertoCerrado muro, EstadoMuro estado) {
        murosEstado.put(muro, estado);
    }

    public EstadoMuro estadoMuroDe(MuroAbiertoCerrado muro) {
        return murosEstado.get(muro);
    }

    public void guardarEstadoCerrojo(Cerrojo cerrojo, EstadoCerrojo estado) {
        cerrojosEstado.put(cerrojo, estado);
    }

    public EstadoCerrojo estadoCerrojoDe(Cerrojo cerrojo) {
        return cerrojosEstado.get(cerrojo);
    }

    public void guardarEnergiaJugador(int energia) {
        this.energiaJugador = energia;
    }

    public int getEnergiaJugador() {
        return energiaJugador;
    }
}
