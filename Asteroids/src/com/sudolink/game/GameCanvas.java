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
import com.sudolink.manager.GameObjectsManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Matthew MacGregor
 */
public class GameCanvas extends JPanel {

    public static int SCREEN_WIDTH = 900;
    public static int SCREEN_HEIGHT = 600;

    private BufferedImage backbuffer = null;
    private Graphics2D g2d = null;
    private AffineTransform identity = null;

    public GameCanvas(KeyListener listener) {
        refreshBuffer(GameCanvas.SCREEN_WIDTH, SCREEN_HEIGHT);
        identity = new AffineTransform();
        setFocusable(true);
        addKeyListener(listener);
    }

    /**
     * Allows the owner of this component to reset the size (in response to a 
     * screen resize event, for example).
     * @param width
     * @param height
     */
    public final void refreshBuffer(int width, int height) {
        GameCanvas.SCREEN_WIDTH = width;
        GameCanvas.SCREEN_HEIGHT = height;
        setSize(width, height);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        backbuffer = new BufferedImage(SCREEN_WIDTH,
                SCREEN_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        g2d = backbuffer.createGraphics();
    }

    /**
     * This fires the main draw loop.
     */
    public void updateGraphics() {
        
        g2d.setTransform(getIdentity());
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        List<GameObject> objects = GameObjectsManager.getInstance().get();
        
        // This shouldn't need synchronization (?)
        for (GameObject o : objects) {
            switch (o.getState()) {
                // Only draw active objects
                case Active:
                case Impervious:
                case Ghost:
                    g2d.setTransform(getIdentity());
                    o.draw(g2d);

                // Ignore these objects
                case Passive:
                case Killed:
                default:
                    ;

            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(backbuffer, 0, 0, this);
    }

    /**
     * @return the identity
     */
    public AffineTransform getIdentity() {
        return identity;
    }

}
