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
 * The base of all in-game objects (anything that needs to be drawn or updated).
 * @author Matthew MacGregor
 */
public abstract class GameObject {

    public static final short TURN_LEFT = -1;
    public static final short TURN_RIGHT = 1;
    public static final short TURN_NONE = 0;
    
    
    /**
     * The update method is called on each game loop and manages updating the
     * position of the game objects. Guaranteed to be called before the draw.
     */
    public void update() {
        screenWrap();
    }

    /**
     * The draw method handles drawing the object to the screen. For performance
     * reasons, be sure to update game object position in the update() method.
     * @param g2d The Graphics context.
     */
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
    
    /**
     * @return Returns a rectangle that represents the boundaries of the game object.
     */
    public Rectangle getBounds() {
        Rectangle rect = new Rectangle((int) x, (int) y, (int) getWidth(), (int) getHeight());
        return rect;
    }

    /**
     * Handles collisions with another game object. Subclasses must implement
     * this behavior to suit the game rules.
     * 
     * @param o The object that is colliding with this object.
     */
    public abstract void collide(GameObject o);
    
    /**
     * Draws to a buffer instead of the graphics context. Implement this method
     * in subclasses.
     * 
     * @param g2d 
     */
    protected abstract void drawToBuffer( Graphics2D g2d );
    
    /**
     * Another object may cause damage to this object. The base object does not
     * implement damage handling. This method should be overriden for objects
     * that may take damage.
     * 
     * @param source The GameObject that is causing the damage.
     * @param amount The amount of damage being caused.
     */
    public void damage(GameObject source, int amount) {
        // Base object cannot take damage. Override.
    }

    /**
     * Reports if part of the game object has exited the screen. 
     * @return True if offscreen.
     */
    public boolean isOffScreen() {
        return getX() < 0
                || getX() > GameCanvas.SCREEN_WIDTH
                || getY() < 0
                || getY() > GameCanvas.SCREEN_HEIGHT;

    }

    /**
     * Sets the coordinates of the object.
     * @param x Horizontal coordinate.
     * @param y Vertical coordinate.
     */
    public void setXY(float x, float y) {
        setX(x);
        setY(y);
    }

    /**
     * Updates the object coordinates by the amount of x, y.
     * @param x Horizontal coordinate.
     * @param y Verical coordinate.
     */
    public void moveToward(float x, float y) {
        setXY(this.x + x, this.y + y);
    }

    /**
     * Returns the depth (z-index) of this object. Used to determine the order
     * of drawing objects.
     * @return The z-index.
     */
    public int getZ() {
        return z;
    }

    /**
     * Sets the depth (z-index) of the object.
     * @param z the z to set
     */
    public void setZ(int z) {
            this.z = z;
    }

    /**
     * Gets the current speed of the object.
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the object.
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
            this.speed = speed;
    }

    /**
     * Gets the direction that this object is moving. This may not be the 
     * same as the rotation of the object. Imagine that the nose of the ship
     * might be pointed at 90 degrees (rotation) but is floating toward 0 
     * degrees (direction) at 1 pixel per frame (speed).
     * 
     * @return the direction as degrees.
     */
    public float getDirection() {
        return direction;
    }

    /**
     * Sets the direction that the object is moving.
     * 
     * @param direction The direction to set expressed as degrees.
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
     * Sets the rotation of the object. Rotation refers to the angle at which
     * the object's "nose" is pointed. 
     * 
     * @param rotation the rotation to set expressed as degrees.
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
     * Returns the rotation of the object. 
     * 
     * @return the rotation expressed as degrees.
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Increases the rotation of the object by the amount of value. Use a negative
     * value to decrease the amount of rotation.
     * 
     * @param value The amount to increment (degrees).
     */
    public void increaseRotation(float value) {
            this.rotation += value;
    }

    /**
     * Turn rate expresses how fast an object is spinning.
     * @return the turnRate
     */
    public float getTurnRate() {
        if (turnRate > turnRateMax) {
            turnRate = turnRateMax;
        }
        return turnRate;
    }

    /**
     * Turn rate expresses how fast an object is spinning.
     * @param turnRate the turnRate to set
     * @param turnDirection turn direction is expressed as the constants 
     * TURN_LEFT, TURN_RIGHT, TURN_NONE.
     */
    public void setTurnRate(float turnRate, int turnDirection) {
            this.turnRate = turnRate * turnDirection;
    }

    /**
     * Makes an object spin faster in the given direction.
     * 
     * @param increase The amount to increase the spin. 
     * @param turnDirection The direction to spin, expressed as the constants
     * TURN_LEFT, TURN_RIGHT, TURN_NONE.
     */
    public void increaseTurnRate(float increase, int turnDirection) {
        turnRate += increase * turnDirection;
    }

    /**
     * Turn rate max applies a clamp on the turn rate. Increasing the turn rate
     * past this maximum will result in setting the turn rate to the max.
     * 
     * @return the turnRateMax 
     */
    public float getTurnRateMax() {
        return turnRateMax;
    }

    /**
     * Sets the turn rate maximum for this object.
     * 
     * @param turnRateMax the turnRateMax to set
     */
    public void setTurnRateMax(float turnRateMax) {
    
            this.turnRateMax = turnRateMax;
     
    }

    /**
     * Adds to the speed of the object.
     * @param amount 
     */
    public void accelerate(float amount) {

        speed += amount;

        int posneg = (speed < 0) ? -1 : 1;

        if( Math.abs(speed) > speedMax ) {
            speed = speedMax * posneg;
        }
        //lock the direction of motion
    }

    /**
     * Subtracts amount from the speed of the object.
     * @param amount 
     */
    public void decelerate(float amount) {
        accelerate(-amount);
    }
   
    /**
     * Sets the state of this object. (Active, Impervious, etc).
     * @param state the state to set
     */
    public void setState(EntityState state) {
        this.state = state;
    }

    /**
     * Gets the state of this object.
     * @return EntityState
     */
    public EntityState getState() {
        return state;
    }

    /**
     * Returns the x coordinate of this object.
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x coordinate of this object.
     * @param x the x to set
     */
    public void setX(float x) {
      
            this.x = x;
      
    }

    /**
     * Gets the y coordinate of this object.
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y coordinate of this object.
     * @param y the y to set
     */
    public void setY(float y) {
 
            this.y = y;
    
    }

    /**
     * Objects can automatically wrap to the opposite side of the screen. 
     * Otherwise, they will float offscreen until destroyed.
     * 
     * @return the isScreenWrapEnabled
     */
    public boolean isScreenWrapEnabled() {
        return isScreenWrapEnabled;
    }

    /**
     * Enables or disables screen wrap behavior for this object.
     * 
     * @param isScreenWrapEnabled the isScreenWrapEnabled to set
     */
    public void setIsScreenWrapEnabled(boolean isScreenWrapEnabled) {
        
        this.isScreenWrapEnabled = isScreenWrapEnabled;
    }

    /**
     * Height of the object.
     * 
     * @return the height
     */
    public final float getHeight() {
        return height;
    }

    /**
     * Sets the height of the object. 
     *
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Gets the width of the object.
     * 
     * @return the width
     */
    public final float getWidth() {
        return width;
    }

    /**
     * Sets the width of the object.
     * 
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Gets the team for this object. 
     * 
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Sets the team for this object.
     * 
     * @param team the team to set
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Sets the maximum speed for this object.
     * 
     * @param speedMax the speedMax to set
     */
    public void setSpeedMax(float speedMax) {
        this.speedMax = speedMax;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Protected Methods">
    
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
    
    protected void drawBoundingBox(Graphics2D g2d) {
        if (GameMain.getInstance().isDebug()) {
            g2d.setPaint(Color.DARK_GRAY);
            Rectangle r = getBounds();
            g2d.drawRect(0, 0, r.width, r.height);
        }
    }

    protected boolean isCollision(GameObject o) {

        if (o != this && o.getState() == Active) {
            Rectangle r = getBounds();
            Rectangle ro = o.getBounds();
            return r.intersects(ro);
        }
        return false;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private Methods">
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private Members">
    private int z = 0;
    private float x = 0;
    private float y = 0;
    private float direction = 0;
    private float speed = 0;
    private float speedMax = 3;
    private float rotation = 0;
    private float turnRate = 0;
    private float turnRateMax = 3;
    private EntityState state = Active;
    private boolean isScreenWrapEnabled = true;
    private float height = 10;
    private float width = 10;
    private Team team = Team.Neutral;
    private BufferedImage backbuffer;
    // </editor-fold>




}
