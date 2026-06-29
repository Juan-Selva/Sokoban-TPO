package com.sokoban.partida;

import com.sokoban.dominio.Tablero;

/**
 * Estado logico de la partida: contadores, historial y reglas de undo.
 * No conoce la vista ni la presentacion.
 */
public class EstadoJuego {

    private static final int SALTO_UNDO = 5;
    private static final int MAX_UNDOS_CONSECUTIVOS = 3;

    private final Tablero tablero;
    private final Historial historial = new Historial();

    private int movimientos;
    private int empujes;
    private int undosConsecutivos;
    private int undosTotales;

    private EstadoJuegoMemento mementoInicial;

    public EstadoJuego(Tablero tablero) {
        this.tablero = tablero;
    }

    /** Fija el punto de partida (estado inicial del nivel) para reinicio/undo. */
    public void guardarEstadoInicial() {
        this.mementoInicial = crearMemento();
        historial.descartar();
        historial.guardar(mementoInicial);
    }

    /** Registra un movimiento exitoso: actualiza contadores e historial. */
    public void registrarMovimiento(boolean huboEmpuje) {
        movimientos++;
        if (huboEmpuje) {
            empujes++;
        }
        undosConsecutivos = 0;
        historial.guardar(crearMemento());
    }

    public boolean puedeUndo() {
        return undosConsecutivos < MAX_UNDOS_CONSECUTIVOS && historial.tieneAlgoQueDeshacer();
    }

    public boolean ejecutarUndo() {
        if (!puedeUndo()) {
            return false;
        }
        restaurarDesde(historial.retroceder(SALTO_UNDO));
        undosConsecutivos++;
        undosTotales++;
        return true;
    }

    /** R19: vuelve a la configuracion inicial y resetea contadores e historial. */
    public void reiniciar() {
        restaurarDesde(mementoInicial);
        movimientos = 0;
        empujes = 0;
        undosConsecutivos = 0;
        undosTotales = 0;
        historial.descartar();
        historial.guardar(mementoInicial);
    }

    public int getMovimientos() {
        return movimientos;
    }

    public int getEmpujes() {
        return empujes;
    }

    public int getUndosConsecutivos() {
        return undosConsecutivos;
    }

    /** Usos de undo que quedan antes de tener que mover de nuevo (§5.3, R16). */
    public int getUndosDisponibles() {
        return MAX_UNDOS_CONSECUTIVOS - undosConsecutivos;
    }

    /** Total de undos usados en el nivel (para el puntaje y el resumen). */
    public int getUndosTotales() {
        return undosTotales;
    }

    /** Resumen del nivel con puntaje y calificacion (§1.16, §5.4). */
    public ResultadoNivel calcularResultado() {
        return new ResultadoNivel(movimientos, empujes, undosTotales);
    }

    private EstadoJuegoMemento crearMemento() {
        return new EstadoJuegoMemento(tablero.capturarEstado(), movimientos, empujes);
    }

    private void restaurarDesde(EstadoJuegoMemento memento) {
        tablero.restaurarEstado(memento.getEstadoTablero());
        this.movimientos = memento.getMovimientos();
        this.empujes = memento.getEmpujes();
    }
}
