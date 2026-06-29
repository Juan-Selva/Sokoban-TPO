package com.sokoban.nivel;

import com.sokoban.dominio.Posicion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Director del Builder: lee un .txt del classpath, separa la directiva de
 * cabecera (VISION n) de la grilla y le dicta los pasos al NivelBuilder. No
 * conoce el dominio: solo el formato del archivo y el contrato del builder.
 */
public class LectorTxt {

    private static final String DIRECTIVA_VISION = "VISION ";

    private final CeldaFactory celdaFactory;
    private final EntidadFactory entidadFactory;
    private final ItemFactory itemFactory;

    public LectorTxt(CeldaFactory celdaFactory, EntidadFactory entidadFactory, ItemFactory itemFactory) {
        this.celdaFactory = celdaFactory;
        this.entidadFactory = entidadFactory;
        this.itemFactory = itemFactory;
    }

    public Nivel leer(String rutaRecurso) throws IOException {
        List<String> lineas = leerLineas(rutaRecurso);

        NivelBuilder builder = new NivelBuilder(celdaFactory, entidadFactory, itemFactory);
        List<String> grilla = new ArrayList<>();
        Integer vision = null;

        for (String linea : lineas) {
            if (linea.startsWith(DIRECTIVA_VISION)) {
                vision = Integer.parseInt(linea.substring(DIRECTIVA_VISION.length()).trim());
            } else {
                grilla.add(linea);
            }
        }

        int filas = grilla.size();
        int columnas = maxLongitud(grilla);
        builder.dimensiones(filas, columnas);

        for (int fila = 0; fila < filas; fila++) {
            String linea = grilla.get(fila);
            for (int columna = 0; columna < columnas; columna++) {
                char caracter = columna < linea.length() ? linea.charAt(columna) : ' ';
                Posicion posicion = new Posicion(fila, columna);
                enrutar(builder, caracter, posicion);
            }
        }

        if (vision != null) {
            builder.conVisionLimitada(vision);
        }

        return builder.build();
    }

    private void enrutar(NivelBuilder builder, char caracter, Posicion posicion) {
        if (entidadFactory.conoce(caracter)) {
            builder.agregarEntidad(caracter, posicion);
        } else if (itemFactory.conoce(caracter)) {
            builder.agregarItem(caracter, posicion);
        } else {
            builder.agregarCelda(caracter, posicion);
        }
    }

    private List<String> leerLineas(String rutaRecurso) throws IOException {
        InputStream entrada = getClass().getClassLoader().getResourceAsStream(rutaRecurso);
        if (entrada == null) {
            throw new IOException("No se encontro el recurso: " + rutaRecurso);
        }

        List<String> lineas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }

    private int maxLongitud(List<String> lineas) {
        int max = 0;
        for (String linea : lineas) {
            max = Math.max(max, linea.length());
        }
        return max;
    }
}
