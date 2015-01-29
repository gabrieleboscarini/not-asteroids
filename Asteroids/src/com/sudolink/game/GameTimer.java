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

import com.sudolink.enumeration.EntityState;
import static com.sudolink.enumeration.EntityState.Active;
import static com.sudolink.enumeration.EntityState.Passive;

/**
 *
 * @author matsu
 */
public abstract class GameTimer {
    
    private int currentTicks;
    private int intervalTicks;
    private long loopCount;
    private EntityState state = Active;
    private Object parent;
    
    
    public GameTimer( int interval ) {
        intervalTicks = interval;
    }
    
    public GameTimer(int interval, Object parent ) {
        this(interval);
        this.parent = parent;
    }
    
    public abstract void action(); 
    
    public void tick() {
        if( state == Active ) {
            currentTicks++;
            if( currentTicks >= intervalTicks ) {
                action();
                loopCount++;
                currentTicks = 0;
            }
        }
    }
    
    public void setInterval(int ticks) {
        intervalTicks = ticks;
    }
    
    public int getInterval() {
        return intervalTicks;
    }
    
    public int getTicks() {
        return currentTicks;
    }
    
    public void reset() {
        currentTicks = 0;
        loopCount = 0;
    }
    
    public long getLoopCount() {
        return loopCount;
    }
    
    public void stop() {
        setEnabled(false);
    }
    
    public void start() {
        setEnabled(true);
    }
    
    public void setEnabled(boolean enable) {
        state = (enable) ? Active : Passive;
    }
    
    public boolean isEnabled() {
        return state == Active;
    }

    /**
     * @return the parent
     */
    public Object getParent() {
        return parent;
    }
    
}
