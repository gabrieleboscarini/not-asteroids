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

import com.sudolink.entities.GameObject;
import com.sudolink.entities.PointsDisplay;
import com.sudolink.entities.Ship;
import com.sudolink.entities.TextDisplay;
import com.sudolink.entities.TickerTextDisplay;
import com.sudolink.manager.AudioManager;
import com.sudolink.manager.FontManager;
import com.sudolink.manager.GameObjectsManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

/**
 * Entry point for the game.
 * @author Matthew MacGregor
 */
public class GameMain implements KeyListener {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        GameMain.getInstance().start();
        
    }

    public GameMain() {

        GameObjectsManager gameObjects = GameObjectsManager.getInstance();
        gameloop = new GameLoop(this);
        gameTimers = new HashMap<>();

        initializeUI();
        initializeAudio();

        ship = new Ship();
        ship.setXY(GameCanvas.SCREEN_WIDTH / 2, GameCanvas.SCREEN_HEIGHT / 2);
        gameObjects.add(ship);

        //Initialize Fonts before initializing TextDisplay objects
        FontManager fm = FontManager.getInstance();
        fm.prepareFont("silkscreen", "/media/slkscr.ttf");

        TextDisplay title = new TextDisplay(fm.getFont("silkscreen", 24), "Not Asteroids");
        title.setXY(GameCanvas.SCREEN_WIDTH * 0.1f, 0);
        title.setForeground(Color.red);
 
        points = new PointsDisplay(fm.getFont("silkscreen", 24), "");
        points.setXY(GameCanvas.SCREEN_WIDTH * 0.7f, 0);

        pause = new TextDisplay(fm.getFont("silkscreen", 48), "Press ESC Key");
        pause.setForeground(Color.red);
        pause.setXY(GameCanvas.SCREEN_WIDTH * 0.5f, GameCanvas.SCREEN_HEIGHT * 0.5f);
        pause.setHorizontallyCentered(true);
        pause.setEnabled(false);
        
        ticker = new TickerTextDisplay(fm.getFont("silkscreen", 18));
        ticker.setForeground(Color.GRAY);
        ticker.setXY(GameCanvas.SCREEN_WIDTH * 0.5f, GameCanvas.SCREEN_HEIGHT * 0.6f);
        ticker.setEnabled(false);
        ticker.addMessage("Not Asteroids: A Retro Classic Redux");
        ticker.addMessage("Programmed by Matthew MacGregor");
        ticker.addMessage("Version " + GameVersion.getVersion());
        ticker.addMessage("For Your Vintage Gaming Pleasure");
        ticker.addMessage("Built From Scratch in Plain Ol' Java");
        ticker.addMessage("Sudolink, LLC 2015 * www.matthewmacgregor.net");
        ticker.addMessage("How Do You Like Them Asteroids?");
        
        gameObjects.add(points);
        gameObjects.add(title);
        gameObjects.add(pause);
        gameObjects.add(ticker);

        initializeTimers();
        gameWave = new GameWave();

    }

    private void initializeUI() {
        frame = new JFrame();
        canvas = new GameCanvas(this);
        frame.setTitle("Asteroids");
        frame.setSize(GameCanvas.SCREEN_WIDTH, GameCanvas.SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = frame.getBounds();
                canvas.refreshBuffer(r.width, r.height);
                if (points != null) {
                    points.setXY(GameCanvas.SCREEN_WIDTH * 0.7f, 0);
                }
                if( pause != null ) {
                    pause.setX(GameCanvas.SCREEN_WIDTH * 0.5f);
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        frame.pack();
        

    }

    private void initializeTimers() {
        //Initialize Timers
        GameTimer respawnTimer = new GameTimer(50) {
            @Override
            public void action() {
                ship.setXY(GameCanvas.SCREEN_WIDTH * 0.5f, GameCanvas.SCREEN_HEIGHT * 0.5f);
                ship.setRotation(0);
                ship.setAcceleration(0);
                ship.setDirection(0);
                ship.setSpeed(0);
                ship.toggleImpervious();
                this.stop();
                this.reset();
            }
        };
        respawnTimer.stop();
        gameTimers.put("respawnTimer", respawnTimer);
    }

    private void initializeAudio() {
        
        AudioManager am = AudioManager.getInstance();
        am.prepareAudioClip("explosion", "/media/explosion.wav");
        am.prepareAudioClip("shipExplosion", "/media/shipExplosion.wav");
        am.prepareAudioClip("alien-communication", "/media/alien-communication.wav");
        am.prepareAudioClip("asteroids", "/media/asteroids.aiff");
        am.prepareAudioClip("shoot", "/media/laser.wav");
        
    }

    public static GameMain getInstance() {

        if (instance == null) {
            instance = new GameMain();
            return instance;
        }

        return instance;
    }



    public void start() {
        
        frame.setVisible(true);
        gameloop.start();
        AudioManager.getInstance().loopClip("asteroids", Clip.LOOP_CONTINUOUSLY);
        
    }

    public void respawn() {

        gameTimers.get("respawnTimer").start();
        
    }

    public void updateTimers() {
        for (GameTimer t : gameTimers.values()) {
            t.tick();
        }
    }

    public void updateGame() {

        GameObjectsManager gom = GameObjectsManager.getInstance();
        gom.update();
        gameWave.update(gom.getAsteroidCount());
        updateTimers();
        
    }

    public boolean isDebug() {
        return false;
    }

    public void drawGame() {
        canvas.updateGraphics();
        canvas.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (ship.isGhost()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    if( ship.toggleGhost() == false ) {
                        pause.setEnabled(false);
                        ticker.setEnabled(false);
                    }
                    break;
            }
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
//                    ship.increaseTurnRate(0.6f, GameObject.TURN_LEFT);
                    ship.setTurnRate(3.5f, GameObject.TURN_LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
//                    ship.increaseTurnRate(0.6f, GameObject.TURN_RIGHT);
                    ship.setTurnRate(3.5f, GameObject.TURN_RIGHT);
                    break;
                case KeyEvent.VK_UP:
                    ship.accelerate(0.45f);
                    break;
                case KeyEvent.VK_DOWN:
                    ship.accelerate(-0.45f);
                    break;
                case KeyEvent.VK_F:
                    ship.fire();
                    break;
                case KeyEvent.VK_ESCAPE:
                    if( ship.toggleGhost() ) {
                        pause.setEnabled(true); 
                        ticker.setEnabled(true);
                    }
                    break;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch( e.getKeyCode() ) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                ship.setTurnRate(0f, GameObject.TURN_NONE);
                break;
        }
        
    }

    /**
     * @return the points
     */
    public int getPoints() {
        return points.getPoints();
    }

    /**
     * @param points the points to set
     */
    public void setPoints(int points) {
        this.points.setPoints(points);
    }

    public void addPoints(int points) {
        this.points.addPoints(points);
    }

        // <editor-fold defaultstate="collapsed" desc="Private Members">
    private final Ship ship;
    private final Map<String, GameTimer> gameTimers;
    private static GameMain instance;
    private final GameLoop gameloop;
    private GameCanvas canvas;
    private JFrame frame;
    private final PointsDisplay points;
    private final GameWave gameWave;
    private final TextDisplay pause;
    private final TickerTextDisplay ticker;
    // </editor-fold>

    
    
}
