/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sudolink.game;

import com.sudolink.entities.Asteroid;
import com.sudolink.entities.TimedTextDisplay;
import com.sudolink.enumeration.EntityState;
import com.sudolink.manager.AudioManager;
import com.sudolink.manager.FontManager;
import com.sudolink.manager.GameObjectsManager;
import java.awt.Color;

/**
 *
 * @author matsu
 */
public class GameWave {

    private int asteroidsInWave = 7;
    private int waveNumber = 0;
    private int asteroidsMax = 100;
    private int asteroidsToSpawn = asteroidsInWave;
    private final GameTimer spawnTimer;

    public GameWave() {
        spawnTimer = new GameTimer(90) {
            @Override
            public void action() {
                if (asteroidsToSpawn > 0) {
                    int spawn = (int) asteroidsInWave / 5;
                    spawnAsteroid(spawn);
                    asteroidsToSpawn -= spawn;
                    System.out.println("Need to spawn: " + asteroidsToSpawn);
                }
            }

        };

        launchWave();

    }

    public void update(int asteroidsRemaining) {
        if (asteroidsRemaining <= 0 && asteroidsToSpawn <= 0) {
            launchWave();
        }

        spawnTimer.tick();
    }

    private void launchWave() {
        System.out.println("Launching a wave.");
        asteroidsInWave = ( getWaveNumber() > 0 ) ? getWaveNumber() * asteroidsInWave : asteroidsInWave;
        waveNumber++;
        asteroidsInWave = (asteroidsInWave > asteroidsMax) ? asteroidsMax : asteroidsInWave;
        asteroidsToSpawn = asteroidsInWave;
        TimedTextDisplay waveText;
        waveText = new TimedTextDisplay(FontManager.getInstance().getFont("silkscreen", 24));
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
        waveText.setText("Wave " + waveNumber);
        GameObjectsManager.getInstance().add(waveText);
        AudioManager.getInstance().playClip("alien-communication");
    }

    public void spawnAsteroid(int count) {
        //TODO: Improve the Asteroid spawning mechanism (make it generic?)
        GameObjectsManager gm = GameObjectsManager.getInstance();
        int where = (int) Math.random() * 4;
        int direction = (int) (Math.random() * (120 - 90) + 90);
        int direction2 = (int) (Math.random() * (290 - 240) + 240);
        float speed = (float) Math.random() + 1.5f;
        for (int i = 0; i < count; i++) {

            switch (where) {
                case 0:
                    gm.add(new Asteroid(-40, (int) (GameCanvas.SCREEN_HEIGHT * 0.25), direction, speed));
                    break;
                case 1:
                    gm.add(new Asteroid(-40, (int) (GameCanvas.SCREEN_HEIGHT * 0.85), (float) (direction * 1.1), speed));
                    break;
                case 2:
                    gm.add(new Asteroid(GameCanvas.SCREEN_WIDTH + 20, (int) (GameCanvas.SCREEN_HEIGHT * 0.35), direction2, speed));
                    break;
                case 3:
                    gm.add(new Asteroid(GameCanvas.SCREEN_WIDTH + 20, (int) (GameCanvas.SCREEN_HEIGHT * 0.75), (float) (direction * 1.1), speed));
                    break;
                default:
                    gm.add(new Asteroid(GameCanvas.SCREEN_WIDTH + 20, -40, direction, speed));
            }
            where++;
            if (where > 5) {
                where = 0;
            }
        }
    }

    /**
     * @return the asteroidsMax
     */
    public int getAsteroidsMax() {
        return asteroidsMax;
    }

    /**
     * @param asteroidsMax the asteroidsMax to set
     */
    public void setAsteroidsMax(int asteroidsMax) {
        this.asteroidsMax = asteroidsMax;
    }

    /**
     * @return the waveNumber
     */
    public int getWaveNumber() {
        return waveNumber;
    }

}
