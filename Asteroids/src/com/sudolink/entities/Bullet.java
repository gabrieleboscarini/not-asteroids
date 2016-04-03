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
import static com.sudolink.enumeration.Team.Enemy;
import static com.sudolink.enumeration.Team.Friend;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author matsu
 */
public class Bullet extends GameObject {
         
    public Bullet(float x, float y,  float r) {
  
        setXY(x, y);
        setSpeed(12);
        setDirection(r);
        setTeam(Friend);
    }
   
    @Override
    protected void drawToBuffer( Graphics2D g2d ) {
        g2d.setPaint(Color.RED);
        g2d.drawRect(0, 0, 2, 2);
    }
    
    @Override
    public void draw(Graphics2D g2d ) {
        super.draw(g2d);
        //g2d.translate(getX(),getY());

        
    }

    @Override
    public void update() {
        
        if( isOffScreen() ) { setState(EntityState.Killed); }
        
        float adjustment = 90;
        float vx = (float) Math.cos(Math.toRadians(getDirection() - adjustment)) * getSpeed() ;
        float vy = (float) Math.sin(Math.toRadians(getDirection() - adjustment)) * getSpeed() ;
        
        moveToward(vx, vy);
    }

    @Override
    public void collide(GameObject o) {
        if( !(o instanceof Bullet) && o.getTeam() == Enemy && isCollision(o) ) {
            //System.out.println("Bullet is no more.");
            setState(EntityState.Killed);
            o.damage(this, 1);
        }
    }
    
}
