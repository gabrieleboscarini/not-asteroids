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

import com.sudolink.game.GameCanvas;
import com.sudolink.game.GameMain;
import com.sudolink.enumeration.EntityState;
import static com.sudolink.enumeration.EntityState.Active;
import com.sudolink.enumeration.Team;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author Matthew MacGregor
 */
public abstract class GameObject {

    public static final short TURN_LEFT = -1;
    public static final short TURN_RIGHT = 1;
    public static final short TURN_NONE = 0;

    private int z = 0;
    private float x = 0;
    private float y = 0;

    private float direction = 0;
    private float speed = 0;
    private float speedMax = 3;

    private float rotation = 0;
    private float turnRate = 0;
    private float turnRateMax = 3;
    private float acceleration = 0;
    private float accelerationMax = 3;
    private EntityState state = Active;
    private boolean isScreenWrapEnabled = true;
    private float height = 10;
    private float width = 10;
    private Team team = Team.Neutral;
    
    private BufferedImage backbuffer;
    
    public void draw(Graphics2D g2d) {
        if( backbuffer == null ) {
            initBuffer();
            drawToBuffer(backbuffer.createGraphics());
        }
        
        g2d.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
        g2d.rotate(Math.toRadians(getRotation()));
        g2d.translate(-getWidth() / 2, -getHeight() / 2);
        // Solves the problem of polygons looking crappy during rotation.
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(backbuffer, 0, 0, null);
    }

    protected final void initBuffer() {
        BufferedImage buffer = new BufferedImage(
                (int)getWidth() + 1,
                (int)getHeight() + 1,
                BufferedImage.TYPE_INT_ARGB);
        setBuffer(buffer);
    }
    
    protected void refreshBuffer() {
        setBuffer(null);
    }
    
    protected abstract void drawToBuffer( Graphics2D g2d );
    
    protected Graphics2D getGraphics() {
        if( backbuffer != null ) {
            return backbuffer.createGraphics();
        }
        return null;
    }
    
    protected BufferedImage getBuffer() {
        return backbuffer;
    }
    
    protected void setBuffer(BufferedImage buffer) {
        backbuffer = buffer;
    }
    
    public void update() {
        screenWrap();
    }

    protected void drawBoundingBox(Graphics2D g2d) {
        if (GameMain.getInstance().isDebug()) {
            g2d.setPaint(Color.DARK_GRAY);
            Rectangle r = getBounds();
            g2d.drawRect(0, 0, r.width, r.height);
        }
    }

    public Rectangle getBounds() {
        Rectangle rect = new Rectangle((int) x, (int) y, (int) getWidth(), (int) getHeight());
        return rect;
    }

    public abstract void collide(GameObject o);

    protected boolean isCollision(GameObject o) {

        if (o != this && o.getState() == Active) {
            Rectangle r = getBounds();
            Rectangle ro = o.getBounds();
            return r.intersects(ro);
        }
        return false;
    }

    public void damage(GameObject source, int amount) {

    }

    public boolean isOffScreen() {
        return getX() < 0
                || getX() > GameCanvas.SCREEN_WIDTH
                || getY() < 0
                || getY() > GameCanvas.SCREEN_HEIGHT;

    }

    private void screenWrap() {
        if (isScreenWrapEnabled == false) {
            return;
        }

        if (getX() < -10 && getDirection() > 180) {
            setX(getX() + GameCanvas.SCREEN_WIDTH);
            setY(GameCanvas.SCREEN_HEIGHT - getY());
        } else if (getX() > GameCanvas.SCREEN_WIDTH && getDirection() <= 180) {
            setX(getX() - GameCanvas.SCREEN_WIDTH);
            setY(GameCanvas.SCREEN_HEIGHT - getY());
        } else if (getY() > GameCanvas.SCREEN_HEIGHT && (getDirection() >= 90 && getDirection() <= 270)) {
            setY(getY() - GameCanvas.SCREEN_HEIGHT);
            setX(GameCanvas.SCREEN_WIDTH - getX());
        } else if (getY() < -10 && (getDirection() > 270 || getDirection() < 90)) {
            setY(getY() + GameCanvas.SCREEN_HEIGHT);
            setX(GameCanvas.SCREEN_WIDTH - getX());
        }
    }

    public void setXY(float x, float y) {

        setX(x);
        setY(y);
    }

    public void moveToward(float x, float y) {
        setXY(this.x + x, this.y + y);
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(int z) {
     
            this.z = z;
        

    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
       
            this.speed = speed;
   
    }

    /**
     * @return the direction
     */
    public float getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(float direction) {
     
            this.direction = direction;
            if (this.direction > 360) {
                this.direction -= 360;
            } else if (this.direction < 0) {
                this.direction += 360;
            }
        
    }

    /**
     * @param rotation the rotation to set
     */
    public void setRotation(float rotation) {
 
            this.rotation = rotation;
            if (this.rotation > 360) {
                this.rotation -= 360;
            } else if (this.rotation < 0) {
                this.rotation += 360;
            }
       
    }

    /**
     * @return the rotation
     */
    public float getRotation() {
        return rotation;
    }

    public void increaseRotation(float value) {
       
            this.rotation += value;
//            return this.rotation;
    
    }

    /**
     * @return the turnRate
     */
    public float getTurnRate() {
        if (turnRate > turnRateMax) {
            turnRate = turnRateMax;
        }
        return turnRate;
    }

    /**
     * @param turnRate the turnRate to set
     * @param turnDirection
     */
    public void setTurnRate(float turnRate, int turnDirection) {
        
            this.turnRate = turnRate * turnDirection;
        

    }

    public void increaseTurnRate(float increase, int turnDirection) {
         
            turnRate += increase * turnDirection;
      
    }

    /**
     * @return the turnRateMax
     */
    public float getTurnRateMax() {
        return turnRateMax;
    }

    /**
     * @param turnRateMax the turnRateMax to set
     */
    public void setTurnRateMax(float turnRateMax) {
    
            this.turnRateMax = turnRateMax;
     
    }

    /**
     * @return the acceleration
     */
    public float getAcceleration() {
        return acceleration;
    }

    /**
     * @param acceleration the acceleration to set
     */
    public void setAcceleration(float acceleration) {
     
            this.acceleration = acceleration;
     
    }

    public void accelerate(float amount) {
        
            if (acceleration > accelerationMax) {
                acceleration = accelerationMax;
            }
            if (acceleration < 0) {
                acceleration = 0;
            }
            speed += amount;
            
            int posneg = (speed < 0) ? -1 : 1;
            
            if( Math.abs(speed) > speedMax ) {
                speed = speedMax * posneg;
            }
            //lock the direction of motion
       
    }

    /**
     * @return the accelerationMax
     */
    public float getAccelerationMax() {
        return accelerationMax;
    }

    /**
     * @param accelerationMax the accelerationMax to set
     */
    public void setAccelerationMax(float accelerationMax) {
     
            this.accelerationMax = accelerationMax;
  
    }

    /**
     * @param state the state to set
     */
    public void setState(EntityState state) {
        this.state = state;
    }

    public EntityState getState() {
        return state;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
      
            this.x = x;
      
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
 
            this.y = y;
    
    }

    /**
     * @return the isScreenWrapEnabled
     */
    public boolean isIsScreenWrapEnabled() {
        return isScreenWrapEnabled;
    }

    /**
     * @param isScreenWrapEnabled the isScreenWrapEnabled to set
     */
    public void setIsScreenWrapEnabled(boolean isScreenWrapEnabled) {
        
        this.isScreenWrapEnabled = isScreenWrapEnabled;
    }

    /**
     * @return the height
     */
    public final float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public final float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @param team the team to set
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * @return the speedMax
     */
    public float getSpeedMax() {
        return speedMax;
    }

    /**
     * @param speedMax the speedMax to set
     */
    public void setSpeedMax(float speedMax) {
        this.speedMax = speedMax;
    }

}
