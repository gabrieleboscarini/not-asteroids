/*
*    Copyright 2014 Matthew MacGregor
*
*    This file is part of NotAsteroids.
*
*    NotAsteroids is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Foobar is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with NotAsteroids.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.sudolink.manager;

import com.sudolink.entities.Asteroid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author matsu
 */
public class AudioManager {

    private static AudioManager instance;
    private final Map<String, Clip> clips;

    private AudioManager() {
        clips = new HashMap<>();
    }

    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void prepareAudioClip(String key, String mediaFile) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource(mediaFile));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clips.put(key, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            Logger.getLogger(Asteroid.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Clip getClip(String key) {
        return clips.get(key);
    }

    public void playClip(String key) {
        Clip c = clips.get(key);
        c.setFramePosition(0);
        c.start();
    }

    public void loopClip(String key, int count) {
        Clip c = clips.get(key);
        c.setFramePosition(0);
        c.loop(count);
    }
}
