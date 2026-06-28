package com.sokoban.view;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Reproduce efectos de sonido desde resources/sounds/&lt;evento&gt;.wav. Vive en la
 * capa de vista: el modelo no conoce el audio (MVC). Es best-effort: si falta el
 * archivo o el sistema no tiene audio, no falla; simplemente no suena.
 */
class ReproductorSonidos {

    void reproducir(String evento) {
        InputStream recurso = getClass().getResourceAsStream("/sounds/" + evento + ".wav");
        if (recurso == null) {
            return;
        }
        try (InputStream entrada = new BufferedInputStream(recurso);
             AudioInputStream audio = AudioSystem.getAudioInputStream(entrada)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.addLineListener(this::cerrarAlTerminar);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Best-effort: el audio nunca interrumpe el juego.
        }
    }

    private void cerrarAlTerminar(LineEvent evento) {
        if (evento.getType() == LineEvent.Type.STOP) {
            evento.getLine().close();
        }
    }
}
