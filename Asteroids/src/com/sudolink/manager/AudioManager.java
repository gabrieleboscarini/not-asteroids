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
 * @author Matthew MacGregor
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

    /**
     * Loads an audio clip from a media file, stores the clip in memory.
     * @param key A unique string that represents the media file.
     * @param mediaFile The filename for the media file.
     */
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

    /**
     * Returns a clip for the given key. The clip must have been prepared before
     * this method is called.
     * 
     * @param key The key name for the media clip.
     * @return The clip.
     */
    public Clip getClip(String key) {
        return clips.get(key);
    }

    /**
     * Plays a clip for the given key.
     * @param key The key name for the media clip.
     */
    public void playClip(String key) {
        Clip c = clips.get(key);
        c.setFramePosition(0);
        c.start();
    }

    /**
     * Loops the clip for the given key.
     * @param key
     * @param count The number of times to loop the clip.
     */
    public void loopClip(String key, int count) {
        Clip c = clips.get(key);
        c.setFramePosition(0);
        c.loop(count);
    }
    
    /**
     * Stops the clip for the given key.
     * @param key 
     */
    public void stopClip( String key ) {
        Clip c = clips.get(key);
        c.stop();
    }
    
    /**
     * Removes the clip for the given key.
     * @param key 
     */
    public void removeClip( String key ) {
        Clip c = clips.remove(key);
        c.stop();
    }
}
