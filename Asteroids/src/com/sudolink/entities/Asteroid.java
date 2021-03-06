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
package com.sudolink.entities;

import static com.sudolink.enumeration.EntityState.Killed;
import static com.sudolink.enumeration.Team.Enemy;
import com.sudolink.game.GameMain;
import com.sudolink.manager.AudioManager;
import com.sudolink.manager.GameObjectsManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 *
 * @author matsu
 */
public class Asteroid extends GameObject {

    //Not using an enum here because of the awkwardness of enum.ordinal()
    public class Size {
        public static final int UNDEFINED = -1;
        public static final int TINY = 0;
        public static final int SMALL = 1;
        public static final int MEDIUM = 2;
        public static final int LARGE = 3;
    }
    
    public Asteroid(float x, float y, float angle, float speed, int sizeOverride ) {
        initSize(sizeOverride);
        
        setWidth(size * 10);
        setHeight(size  * 10);
        setX(x);
        setY(y);
        setSpeed(speed);
        setDirection(angle);
        setTurnRate(1, TURN_RIGHT);
        setTeam(Enemy);
        
        particleLifespanMax = (int)(Math.random() * 60);
        initPolygon();
    }
    
    public Asteroid(float x, float y, float angle, float speed ) {
        this(x, y, angle, speed, Size.UNDEFINED);
    }
    
    @Override
    protected void drawToBuffer( Graphics2D _g2d ) {
        Color transparent = new Color(1f, 0f, 0f, 0.0f);
        _g2d.setBackground(transparent);
        _g2d.clearRect(0, 0, (int) getWidth(), (int) getHeight());
        _g2d.setPaint(Color.BLACK);
        _g2d.fillPolygon(poly);
        _g2d.setPaint(Color.GREEN);
        _g2d.drawPolygon(poly);
 
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        
        super.draw(g2d);        
        
    }

    @Override
    public void update() {
        
        super.update();
        increaseRotation(getTurnRate());
        float adjustment = 90;
        float vx = (float) Math.cos(Math.toRadians(getDirection() - adjustment)) * getSpeed();
        float vy = (float) Math.sin(Math.toRadians(getDirection() - adjustment)) * getSpeed();
        moveToward(vx, vy);
        if( size == Size.TINY ) {
            if( particleLifespan > particleLifespanMax ) {
                setState(Killed);
            }
            particleLifespan++;
        }
        
        //Kill the asteroid if it's been offscreen for more than 300 ticks
        if( isOffScreen() ) {
            if( offscreenCount > 300 ) {
                setState(Killed);
            }
            offscreenCount++;
        }
        
    }

    @Override
    public void collide(GameObject o) {
        // Nothing is allowed to collide with Asteroids. In the future, could
        // allow asteroids to collide with each other...
    }

    /**
     * Asteroids are worth points relative to their size.
     * @return the number of points this asteroid is worth.
     */
    public int getPoints() {
        switch(size) {
            case Size.TINY:
            case Size.UNDEFINED:
                return 0;
            case Size.SMALL:
                return 3;
            case Size.MEDIUM:
                return 2;
            case Size.LARGE:
                return 1;
            default:
                return 0;
        }
    }
    
    /**
     * When an asteroid takes damage, it explodes. Depending on the size of 
     * the asteroid, it spawns smaller asteroids (or just debris).
     * 
     * @param source The object doing the damage.
     * @param amount The amount of damage.
     */
    @Override
    public void damage(GameObject source, int amount ) {
        setState(Killed);
        GameMain.getInstance().addPoints(getPoints());
        AudioManager.getInstance().playClip("explosion");
        if( getSize() > Size.TINY ) {
            for( int i = 0; i < 3; i++) { 
                int direction = 1 +  (int)(Math.random() * 360);
                int sz = (int)(Math.random() * ( getSize()));
                Asteroid a = new Asteroid(getX(), getY(), direction, 3.3f, sz );
                a.setTurnRate(3.0f, GameObject.TURN_RIGHT);
                GameObjectsManager.getInstance().add(a);
            }
            
            for( int i = 0; i < 6; i++ ) {
                int direction = 1 +  (int)(Math.random() * 360);
                GameObjectsManager.getInstance().add(new Asteroid(getX(), getY(), direction, 2.3f, Size.TINY ));
            }
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Private Methods">
    
    private int generateSize() {
        return (int)(Math.random() * 4) + 1;
    }
    
    private void initSize(int sizeOverride) {
        //TODO: Add validation for sideOverride
        size = ( sizeOverride == Size.UNDEFINED ) ? generateSize() : sizeOverride;
        switch(size) {
            case Size.SMALL:
                setZ(1);
                break;
            case Size.MEDIUM:
                setZ(2);
                break;
            case Size.LARGE:
                setZ(3);
                break;
            default:
                setZ(0);
        }
        
    }
    
    private void initPolygon() {
        if( getSize() == Size.TINY ) {
            poly = new Polygon();
            poly.addPoint(0, 0);
            poly.addPoint(1, 1);
            return;
        }
        
        double r1 = Math.random();
        double r2 = Math.random();  
        int[] xpoly = new int[] {              1,  
                                (int) (  getWidth() * 0.3 * r1  ), 
                                (int) (  getWidth() * 0.6  ), 
                                (int)    getWidth()         ,
                                (int) (  getWidth() * 0.7  ), 
                                (int) (  getWidth() * 0.3 * r2 )
                          };
        
        int[] ypoly = new int[] { 
                                (int) ( getHeight() * 0.5 * r1 ), 
                                (int)   1                       , 
                                (int) ( getHeight() * 0.1 * r2 ), 
                                (int) ( getHeight() * 0.3  ), 
                                (int) ( getHeight() * 0.9  ), 
                                (int)   getHeight() 
                          };
        poly = new Polygon(xpoly, ypoly, xpoly.length);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private Members">
    private int size;
    private int particleLifespan = 0;
    private int particleLifespanMax = 25;
    private int offscreenCount;
    private Polygon poly;
    // </editor-fold>
    
}
