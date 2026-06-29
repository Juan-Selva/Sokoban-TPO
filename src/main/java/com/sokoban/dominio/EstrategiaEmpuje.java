package com.sokoban.dominio;

/**
 * Strategy: el costo de energia de empujar una caja. Cada caja delega aqui en
 * vez de que alguien pregunte "que caja sos" para decidir el costo (sin if/switch).
 */
public interface EstrategiaEmpuje {
    int costo();
}
