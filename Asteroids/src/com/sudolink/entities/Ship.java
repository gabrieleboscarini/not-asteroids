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
import javax.sound.sampled.Clip;

/**
 *
 * @author matsu
 */
public class Ship extends GameObject {

    private final int[] xpoly;
    private final int[] ypoly;

    private final Clip explosion;
    private Color shipColor = Color.ORANGE;
    private final GameTimer imperviumTimer;

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
        explosion = AudioManager.getInstance().getClip("shipExplosion");
        imperviumTimer = new GameTimer(150) {
            @Override
            public void action() {
                imperviumTimer.stop();
                imperviumTimer.reset();
                Ship.this.shipColor = Color.ORANGE;
                setState(Active);
            }
        };
        imperviumTimer.stop();
    }

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        drawBoundingBox(g2d);

        g2d.setPaint(Color.BLACK);
        g2d.fillPolygon(xpoly, ypoly, 3);
        g2d.setPaint(shipColor);
        g2d.drawPolygon(xpoly, ypoly, 3);

    }

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

    @Override
    protected void drawBoundingBox(Graphics2D g2d) {
        if (GameMain.getInstance().isDebug()) {
            g2d.setPaint(Color.DARK_GRAY);
            Rectangle r = getBounds();
            g2d.drawRect(0, 0, r.width, r.height);
        }
    }

    public void fire() {
        EntityState s = getState();
        if (s == Active || s == Impervious) {
            Rectangle r = getBounds();
            Bullet b = new Bullet((int) r.getCenterX(), (int) r.getCenterY(), getDirection());
            GameObjectsManager.getInstance().add(b);
        }
        AudioManager.getInstance().playClip("shoot");
    }

    private void explode() {
        this.setState(Passive);
        AudioManager.getInstance().playClip("shipExplosion");
        for (int i = 0; i < 7; i++) {
            GameObjectsManager.getInstance().add(new StickParticle(getX(), getY()));
        }

        GameMain.getInstance().respawn();
    }

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
    }

    public boolean isImpervious() {
        return getState() == EntityState.Impervious;
    }

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
    
    public boolean isGhost() {
        return getState() == EntityState.Ghost;
    }

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

}