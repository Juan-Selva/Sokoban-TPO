package com.sokoban.controller;

import com.sokoban.comandos.Command;
import com.sokoban.comandos.MoverCommand;
import com.sokoban.comandos.ReiniciarCommand;
import com.sokoban.comandos.UndoCommand;
import com.sokoban.dominio.Direccion;
import com.sokoban.nivel.CeldaFactory;
import com.sokoban.nivel.EntidadFactory;
import com.sokoban.nivel.ItemFactory;
import com.sokoban.nivel.LectorTxt;
import com.sokoban.nivel.Nivel;
import com.sokoban.partida.Juego;
import com.sokoban.view.VentanaJuego;
import java.io.IOException;

/**
 * Coordina input, modelo y vista (MVC). Traduce las acciones del usuario a
 * Command que ejecuta sobre la fachada Juego y gestiona el avance de niveles.
 * Si el jugador se queda sin energia (evento SIN_ENERGIA), reinicia el nivel.
 *
 * La vista es opcional (puede ser null en tests headless).
 */
public class Controlador {

    private static final String[] RUTAS_NIVELES = {
            "levels/nivel1.txt",
            "levels/nivel2.txt",
            "levels/nivel3.txt",
            "levels/nivel4.txt",
            "levels/nivel5.txt",
            "levels/nivel6.txt",
            "levels/nivel7.txt"
    };

    private final LectorTxt lector;
    private VentanaJuego vista;

    private int indiceNivel;
    private Nivel nivelActual;
    private Juego juego;

    public Controlador() {
        this.lector = new LectorTxt(new CeldaFactory(), new EntidadFactory(), new ItemFactory());
        this.indiceNivel = 0;
    }

    public void setVista(VentanaJuego vista) {
        this.vista = vista;
    }

    public void iniciar() {
        cargarNivel(0);
    }

    public void mover(Direccion direccion) {
        ejecutar(new MoverCommand(direccion));
        sonarUltimoEvento();
        // La consecuencia de quedarse sin energia (reinicio) la resuelve el propio
        // evento dentro del modelo (EventoJuego.aplicarConsecuencia); aqui solo
        // queda la coordinacion de avance de nivel ante la victoria.
        if (juego.hayVictoria()) {
            reproducirSonido("victoria");
            manejarVictoria();
        }
    }

    public void deshacer() {
        ejecutar(new UndoCommand());
        sonarUltimoEvento();
    }

    public void reiniciar() {
        ejecutar(new ReiniciarCommand());
        sonarUltimoEvento();
    }

    public Juego getJuego() {
        return juego;
    }

    public Nivel getNivelActual() {
        return nivelActual;
    }

    public int getNumeroNivel() {
        return indiceNivel + 1;
    }

    private void ejecutar(Command command) {
        juego.ejecutar(command);
    }

    // El modelo dice "que paso" (EventoJuego) y cada evento conoce su clave de
    // sonido: no hay condicionales para adivinarlo. Si la vista o el archivo no
    // estan, simplemente no suena.
    private void sonarUltimoEvento() {
        reproducirSonido(juego.getUltimoEvento().getClaveSonido());
    }

    private void reproducirSonido(String clave) {
        if (vista != null) {
            vista.reproducirSonido(clave);
        }
    }

    private void cargarNivel(int indice) {
        try {
            this.indiceNivel = indice;
            this.nivelActual = lector.leer(RUTAS_NIVELES[indice]);
            this.juego = new Juego(nivelActual.construirTablero());
            if (vista != null) {
                juego.agregarObservador(vista);
                vista.refrescarNivel();
            }
        } catch (IOException e) {
            if (vista != null) {
                vista.mostrarError("No se pudo cargar el nivel: " + e.getMessage());
            }
        }
    }

    private void manejarVictoria() {
        boolean hayMasNiveles = indiceNivel < RUTAS_NIVELES.length - 1;
        if (vista != null) {
            vista.mostrarVictoria(juego.calcularResultado(), hayMasNiveles);
        }
        if (hayMasNiveles) {
            cargarNivel(indiceNivel + 1);
        }
    }
}
