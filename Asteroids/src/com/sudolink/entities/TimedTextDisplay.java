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

import com.sudolink.game.GameTimer;
import java.awt.Font;

/**
 * Provides a TextDisplay with an embedded timer, allowing timed behavior.
 * @author Matthew MacGregor
 */
public class TimedTextDisplay extends TextDisplay {
    
    private GameTimer timer;
    
    public TimedTextDisplay(Font f) {
        super(f);
    }
    
    @Override
    public void update() {
        super.update();
        if( timer != null ) {
            timer.tick();
        }
        
    }

    /**
     * @param timer the timer to set
     */
    public void setTimer(GameTimer timer) {
        this.timer = timer;
    }
    
}
