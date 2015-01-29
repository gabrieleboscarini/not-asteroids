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
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author matsu
 */
public class StickParticle extends GameObject {

    private int lifespan = 0;
    private int lifespanMax = 50;
    private int length = 12;
    
    public StickParticle(float x, float y) {
        setXY(x,y);
        float tr = (float) Math.random() * 15;
        tr = (Math.random() > 0.5) ? tr : tr * -1; 
        float sp = (float) Math.random() * ( 3  - 1);
        lifespanMax = (int) (Math.random() * ( 100 - 50 ));
        setTurnRate(tr, GameObject.TURN_RIGHT);
        int direction = 1 +  (int)(Math.random() * 360);
        setDirection(direction);
        setSpeed(sp);
        length = (int) ((Math.random()) * (12 - 5) + 5);
    }
    
    
    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        g2d.setPaint(Color.ORANGE);
        g2d.drawLine( 0, 0, length, length);
    }
    
    @Override
    public void update() {
        if( lifespan > lifespanMax ) { setState(Killed); }
        super.update();
        increaseRotation(getTurnRate());
        float adjustment = 90;
        float vx = (float) Math.cos(Math.toRadians(getDirection() - adjustment)) * getSpeed();
        float vy = (float) Math.sin(Math.toRadians(getDirection() - adjustment)) * getSpeed();
        moveToward(vx, vy);
        lifespan++;
    }
    
    @Override
    public void collide(GameObject o) {
        
    }
    
}
