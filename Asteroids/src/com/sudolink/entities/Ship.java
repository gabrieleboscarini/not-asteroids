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

import com.sudolink.enumeration.EntityState;
import static com.sudolink.enumeration.EntityState.Active;
import static com.sudolink.enumeration.EntityState.Impervious;
import com.sudolink.manager.AudioManager;
import com.sudolink.game.GameMain;
import static com.sudolink.enumeration.EntityState.Passive;
import static com.sudolink.enumeration.Team.Friend;
import com.sudolink.game.GameTimer;
import com.sudolink.manager.GameObjectsManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The player's ship.
 */
public class Ship extends GameObject {

    public Ship() {
        int unit = 8;
        int w = unit * 2;
        int h = unit * 3;
        setWidth(w);
        setHeight(h);
        setSpeedMax(6.0f);
        xpoly = new int[]{unit, w, 0};
        ypoly = new int[]{0, h, h};
        setZ(1000);

        setTeam(Friend);
        
        audioManager = AudioManager.getInstance();
        imperviumTimer = new GameTimer(150) {
            @Override
            public void action() {
                imperviumTimer.stop();
                imperviumTimer.reset();
                Ship.this.shipColor = Color.ORANGE;
                setState(Active);
                refreshBuffer();
            }
        };
        imperviumTimer.stop();
    }

    /**
     * We only ever need to draw the ship once, so we'll draw to a buffer and
     * draw the buffer to the screen.
     * 
     * @param g2d Graphics context for the buffer.
     */
    @Override
    protected void drawToBuffer( Graphics2D g2d ) {
   
        drawBoundingBox(g2d);

        g2d.setPaint(Color.BLACK);
        g2d.fillPolygon(xpoly, ypoly, 3);
        g2d.setPaint(shipColor);
        g2d.drawPolygon(xpoly, ypoly, 3);

    }
    
    /**
     * Update.
     */
    @Override
    public void update() {
        super.update();
        float newHeading = getDirection() + getTurnRate();
        setRotation(newHeading);
        setDirection(newHeading);

        float adjustment = 90;
        float vx = (float) Math.cos(Math.toRadians(getDirection() - adjustment)) * getSpeed();
        float vy = (float) Math.sin(Math.toRadians(getDirection() - adjustment)) * getSpeed();
        moveToward(vx, vy);
        imperviumTimer.tick();
    }

    /**
     * The boundaries of this ship object are slightly different than a typical
     * rectangular object.
     * 
     * @return the bounds of this object.
     */
    @Override
    public Rectangle getBounds() {
        Rectangle rect = new Rectangle(
                (int) (getX()),
                (int) (getY()),
                (int) (getWidth()),
                (int) (getHeight() * 0.8)
        );
        return rect;
    }

    /**
     * Fires a missile.
     */
    public void fire() {
        EntityState s = getState();
        if (s == Active || s == Impervious) {
            Rectangle r = getBounds();
            Bullet b = new Bullet((int) r.getCenterX(), (int) r.getCenterY(), getDirection());
            GameObjectsManager.getInstance().add(b);
        }
        playShootClip();
    }

    /**
     * The ship can be in an impervious state. The occurs just after the craft
     * has respawned, for example.
     */
    public void toggleImpervious() {
        if (getState() == Impervious) {
            setState(Active);
            shipColor = Color.ORANGE;
            imperviumTimer.stop();
            imperviumTimer.reset();
        } else {
            setState(EntityState.Impervious);
            shipColor = Color.GRAY;
            imperviumTimer.start();
        }
        refreshBuffer();
    }

    /**
     * Checks if the ship is in an impervious state.
     * @return true if the ship is impervious.
     */
    public boolean isImpervious() {
        return getState() == EntityState.Impervious;
    }

    /**
     * The ship "ghosts" when the game is paused.
     * @return true if the ship is in ghost mode.
     */
    public boolean toggleGhost() {
        
        if( getState() == EntityState.Passive ) {
            return false;
        }
        
        if (getState() == EntityState.Ghost) {
            setState(EntityState.Active);
            shipColor = Color.ORANGE;
            return false;
        } else {
            setState(EntityState.Ghost);
            shipColor = Color.DARK_GRAY;
            return true;
        }
    }
    
    /**
     * The ship "ghosts" when the game is paused.
     * @return true if the ship is in ghost mode.
     */
    public boolean isGhost() {
        return getState() == EntityState.Ghost;
    }

    /**
     * Handles collisions with other objects.
     * @param o the object colliding with the ship.
     */
    @Override
    public void collide(GameObject o) {
        if (getState() == Active && isCollision(o)) {
            if (o instanceof Asteroid) {
                Asteroid a = (Asteroid) o;
                if (a.getSize() > Asteroid.Size.TINY) {
                    explode();
                }
            }
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Private Methods">
    /**
     * Handles respawning the ship and playing the explosion routines.
     */
    private void explode() {
        this.setState(Passive);
        playExplosionClip();
        for (int i = 0; i < 7; i++) {
            GameObjectsManager.getInstance().add(new StickParticle(getX(), getY()));
        }

        GameMain.getInstance().respawn();
    }
    
    /**
     * Plays the shoot audio.
     */
    private void playShootClip() {
        audioManager.playClip("shoot");
    }
    
    /**
     * Plays the explosion audio.
     */
    private void playExplosionClip() {
        audioManager.playClip("shipExplosion");
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private Members">
    private final int[] xpoly;
    private final int[] ypoly;

    private final AudioManager audioManager;
    private Color shipColor = Color.ORANGE;
    private final GameTimer imperviumTimer;
    // </editor-fold>


}
