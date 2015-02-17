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
package com.sudolink.game;

import com.sudolink.entities.Asteroid;
import com.sudolink.entities.TimedTextDisplay;
import com.sudolink.enumeration.EntityState;
import com.sudolink.manager.AudioManager;
import com.sudolink.manager.FontManager;
import com.sudolink.manager.GameObjectsManager;
import java.awt.Color;
import java.util.Random;

/**
 * Represents a 'wave' (level) of asteroids.
 * @author Matthew MacGregor
 */
public class GameWave {

    private int asteroidsInWave = 7;
    private int waveNumber = 1;
    private final int asteroidsMax = 100;
    private int asteroidsToSpawn = asteroidsInWave;
    private final GameTimer spawnTimer;

    public GameWave() {
        
        spawnTimer = new GameTimer(60) {
            @Override
            public void action() {
                if (asteroidsToSpawn > 0) {
                    int spawn = (int) asteroidsInWave / 5;
                    spawnAsteroids(spawn);
                    asteroidsToSpawn -= spawn;
                }
            }
        };

        launchWave();

    }

    public void update(int asteroidsRemaining) {
        
        if ( isWaveDefeated(asteroidsRemaining) ) {
        
            launchWave();
            
        }

        spawnTimer.tick();
    }

    private boolean isWaveDefeated(int asteroidsRemaining) {
        return asteroidsRemaining <= 0 && asteroidsToSpawn <= 0;
    }
    
    private void launchWave() {

        asteroidsInWave =  waveNumber * asteroidsInWave;
        asteroidsInWave = (asteroidsInWave > asteroidsMax) ? asteroidsMax : asteroidsInWave;
        asteroidsToSpawn = asteroidsInWave;
        
        // Display a text object indicating which wave is beginning
        TimedTextDisplay waveText = createWaveText();
        GameObjectsManager.getInstance().add(waveText);
        AudioManager.getInstance().playClip("alien-communication");
        
        waveNumber++;
        
    }
    
    private TimedTextDisplay createWaveText() {
        
        TimedTextDisplay waveText = new TimedTextDisplay(
                FontManager.getInstance().getFont("silkscreen", 24),
                "Wave " + waveNumber
        );
        
        waveText.setTimer(new GameTimer(50, waveText) {
            @Override
            public void action() {
      
                TimedTextDisplay parent = (TimedTextDisplay) getParent();
                parent.setEnabled(false);
                parent.setState(EntityState.Killed);

            }
        });
        
        waveText.setXY(GameCanvas.SCREEN_WIDTH * 0.5f, GameCanvas.SCREEN_HEIGHT * 0.75f);
        waveText.setHorizontallyCentered(true);
        waveText.setForeground(Color.red);
        return waveText;
    }

    private void spawnAsteroids(int count) {
        //TODO: Improve the Asteroid spawning mechanism (make it generic?)
        
        GameObjectsManager gm = GameObjectsManager.getInstance();
        
        Random random = new Random();
        int where       = random.nextInt(2);
        int direction   = random.nextInt(30) + 90;
        int direction2  = random.nextInt(30) + 240;
        float speed     = random.nextFloat() + 1.5f;
        float variance  = random.nextFloat() + 1.0f;
        
        double randomHeight =  Math.random();
        
        final int OFFSCREEN_LEFT = -40;
        final int OFFSCREEN_RIGHT = GameCanvas.SCREEN_WIDTH + 20;
        
        for (int i = 0; i < count; i++) {

            switch (where) {
                case 0:
                    gm.add(new Asteroid(OFFSCREEN_LEFT, (int) (GameCanvas.SCREEN_HEIGHT * randomHeight), (direction * variance), speed));
                    break;
                case 1:
                    gm.add(new Asteroid(OFFSCREEN_LEFT, (int) (GameCanvas.SCREEN_HEIGHT * randomHeight), (direction2 * variance), speed));
                    break;
                default:
                    gm.add(new Asteroid(OFFSCREEN_RIGHT, -40, direction, speed));
            }
            where++;
            randomHeight = Math.random();
            if (where > 1) {
                where = 0;
            }
        }
    }

}
