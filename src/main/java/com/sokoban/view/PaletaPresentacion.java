package com.sokoban.view;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Traduce la clave de presentacion (token neutro del dominio) a sprite, color y
 * letra. Es el unico lugar que conoce ese mapeo; el resto de la vista no pregunta
 * tipos. Si existe un sprite en resources/sprites/<CLAVE>.png se usa esa imagen;
 * si no, se cae al color (asi el juego funciona con o sin assets).
 */
final class PaletaPresentacion {

    private static final Color COLOR_DEFECTO = new Color(40, 40, 40);
    private static final char LETRA_DEFECTO = ' ';

    private static final Map<String, Color> COLORES = Map.ofEntries(
            Map.entry("CELDA_VACIA", new Color(92, 181, 69)),
            Map.entry("PARED", new Color(148, 41, 38)),
            Map.entry("DESTINO", new Color(233, 196, 70)),
            Map.entry("TERRENO_RESBALADIZO", new Color(140, 210, 255)),
            Map.entry("CERROJO", new Color(201, 64, 182)),
            Map.entry("MURO_CERRADO", new Color(45, 45, 45)),
            Map.entry("MURO_ABIERTO", new Color(130, 130, 130)),
            Map.entry("JUGADOR", new Color(72, 116, 201)),
            Map.entry("CAJA_NORMAL", new Color(174, 127, 73)),
            Map.entry("CAJA_FRAGIL", new Color(243, 197, 87)),
            Map.entry("CAJA_LLAVE", new Color(201, 64, 182)),
            Map.entry("CAJA_PESADA", new Color(90, 74, 58)),
            Map.entry("BOTELLA_AGUA", new Color(64, 196, 208))
    );

    private static final Map<String, Character> LETRAS = Map.ofEntries(
            Map.entry("DESTINO", '.'),
            Map.entry("CERROJO", 'C'),
            Map.entry("MURO_CERRADO", 'M'),
            Map.entry("JUGADOR", '@'),
            Map.entry("CAJA_NORMAL", '$'),
            Map.entry("CAJA_LLAVE", 'K'),
            Map.entry("CAJA_PESADA", 'P'),
            Map.entry("BOTELLA_AGUA", 'B')
    );

    private static final Map<String, Image> IMAGENES = cargarImagenes();

    private PaletaPresentacion() {
    }

    static Color color(String clave) {
        return COLORES.getOrDefault(clave, COLOR_DEFECTO);
    }

    static char letra(String clave) {
        return LETRAS.getOrDefault(clave, LETRA_DEFECTO);
    }

    /** Sprite de la clave, o null si todavia no se coloco el archivo. */
    static Image imagen(String clave) {
        return IMAGENES.get(clave);
    }

    private static Map<String, Image> cargarImagenes() {
        Map<String, Image> imagenes = new HashMap<>();
        for (String clave : COLORES.keySet()) {
            Image imagen = cargar(clave);
            if (imagen != null) {
                imagenes.put(clave, imagen);
            }
        }
        return imagenes;
    }

    private static Image cargar(String clave) {
        String ruta = "/sprites/" + clave + ".png";
        try (InputStream entrada = PaletaPresentacion.class.getResourceAsStream(ruta)) {
            if (entrada == null) {
                return null;
            }
            return ImageIO.read(entrada);
        } catch (IOException e) {
            return null;
        }
    }
}
