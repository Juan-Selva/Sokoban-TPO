package com.sokoban.dominio;

/**
 * Entidad que el jugador empuja. Cada subtipo reacciona al empuje a su manera
 * (polimorfismo), sin que nadie pregunte "que caja sos".
 *
 * intentarEmpujar mueve la caja UNA celda si el destino es transitable y esta
 * libre (esto impide empujar dos cajas a la vez, R6). El deslizamiento sobre
 * terreno resbaladizo lo continua el ResolutorMovimiento.
 */
public abstract class Caja extends Entidad {

    private final EstrategiaEmpuje estrategiaEmpuje;

    protected Caja(Posicion posicion) {
        this(posicion, new EmpujeNormal());
    }

    protected Caja(Posicion posicion, EstrategiaEmpuje estrategiaEmpuje) {
        super(posicion);
        this.estrategiaEmpuje = estrategiaEmpuje;
    }

    /** Si esta caja cuenta para la victoria. La caja llave queda exenta (R17). */
    public boolean cuentaParaVictoria() {
        return true;
    }

    /** Energia que cuesta empujar esta caja (delegado en su Strategy). */
    public int getCostoEmpuje() {
        return estrategiaEmpuje.costo();
    }

    /** Mueve la caja una celda si el destino es transitable y esta libre. */
    protected boolean moverUnaCelda(Direccion direccion) {
        if (!tablero.celdaEn(posicion).permiteSalida(tablero)) {
            return false;
        }
        Posicion destino = posicion.sumar(direccion);
        if (!tablero.puedeRecibirEntidad(destino)) {
            return false;
        }
        tablero.mover(this, destino);
        return true;
    }

    /** Reaccion a un empuje del jugador. Cada subtipo la ajusta (polimorfismo). */
    public boolean intentarEmpujar(Direccion direccion) {
        return moverUnaCelda(direccion);
    }

    /** Avance durante un deslizamiento: NO cuenta como empuje ni desgasta (R13). */
    public boolean deslizar(Direccion direccion) {
        return moverUnaCelda(direccion);
    }

    /** Si tras un empuje la caja debe quitarse del tablero (solo la fragil rota). */
    public boolean debeSerEliminada() {
        return false;
    }

    /** Si al eliminarse del tablero el nivel debe reiniciarse (caja fragil rota, R9). */
    public boolean reiniciaNivelAlEliminarse() {
        return false;
    }

    @Override
    public void notificarEntrada(Celda celda) {
        celda.efectoEntrada(this);
    }

    @Override
    public void notificarSalida(Celda celda) {
        celda.efectoSalida(this);
    }

    @Override
    public void agregarseA(Tablero tablero) {
        tablero.agregarCaja(this);
    }

    /** Doble despacho con el cerrojo: por defecto una caja no lo activa. */
    public void alEntrarEnCerrojo(Cerrojo cerrojo) {
        // Solo la CajaLlave reacciona.
    }

    public void alSalirDeCerrojo(Cerrojo cerrojo) {
        // Solo la CajaLlave reacciona.
    }
}
