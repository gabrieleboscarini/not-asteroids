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
import com.sudolink.game.GameTimer;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a "ticker tape" style text display.
 * @author Matthew MacGregor
 */
public class TickerTextDisplay extends TextDisplay {

    public TickerTextDisplay(Font f) {
        super(f);
        messages = new ArrayList<>();
        keypressTimer = new GameTimer(2) {
            @Override
            public void action() {
                if (nextCharIndex == -1) {
                    if( messages.isEmpty() ) return;
                    currentMessage = messages.get(nextMessageIndex);
                    setText("");
                    nextCharIndex = 0;
                    setInterval(2);
                }
                else if (nextCharIndex >= 0 && nextCharIndex <= currentMessage.length()) {
                    setText(currentMessage.substring(0, nextCharIndex));
                    nextCharIndex++;
                }
                else if (nextCharIndex >= currentMessage.length()) {
                    setInterval(delayBeforeErase);
                    nextCharIndex = -1;
                    nextMessageIndex++;
                    if( nextMessageIndex >= messages.size() ) {
                        nextMessageIndex = 0;
                    }
                }
            }
        };
//        setHorizontallyCentered(true);
    }

    @Override
    public void update() {
        super.update();
        keypressTimer.tick();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        if (isEnabled) {
            keypressTimer.start();
        } else {
            keypressTimer.stop();
        }
    }

    @Override
    public float getX() {
        return GameCanvas.SCREEN_WIDTH * 0.5f - (sizeText(currentMessage).width / 2);
 
    }
    
    public void addMessage(String message) {
        this.messages.add(message);
    }
   
    // <editor-fold defaultstate="collapsed" desc="Private Members">
    private final GameTimer keypressTimer;
    private String currentMessage = "";
    private final List<String> messages;
    private int nextMessageIndex;
    private int nextCharIndex = -1;
    private int delayBeforeErase = 40; //ticks
    // </editor-fold>
    
}
