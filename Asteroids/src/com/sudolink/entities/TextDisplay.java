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

import com.sudolink.enumeration.Team;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author matsu
 */
public class TextDisplay extends GameObject {

    private String message;
    private Dimension size;
    private boolean shouldRedraw = true; //First pass should always redraw
    private BufferedImage backbuffer;
    private Graphics2D _g2d;
    private final Font font;
    private Color foreground;
    private final Color background;
    private boolean isEnabled = true;
    private boolean isHorizontallyCentered = false;
 

    public TextDisplay(Font f) {
        message = "";
        font = f;
        foreground = Color.WHITE;
        background = Color.BLACK;
        setTeam(Team.Neutral);
        initBuffer();
        resize();
    }

    @Override
    public void collide(GameObject o) {
        //Does nothing
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (isEnabled) {
            if (shouldRedraw) {
                drawToBuffer();
            }

            super.draw(g2d);
            g2d.drawImage(backbuffer, 0, 0, null);
        }

    }
    

    private void initBuffer() {
        refreshBuffer(1, 1);
    }

    private void refreshBuffer(int width, int height) {
        backbuffer = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB);
        _g2d = backbuffer.createGraphics();
        System.out.println("Refreshing the buffer.");
    }

    private void resize() {
        Dimension d = sizeText(message);
        
        //We only want to refresh the buffered image if it's bigger than 
        //the original (IS THIS CORRECT?) 
        if (d.width > getWidth() || d.height > getHeight()) {
      
            setWidth(d.width);
            setHeight(d.height);
            refreshBuffer(d.width, d.height);

        }
    }
    
    protected Dimension sizeText(String str) {
        // get metrics from the graphics
        FontMetrics metrics = _g2d.getFontMetrics(font);

        // calculate the size of a box to hold the
        // text with some padding.
        // get the height of a line of text in this
        // font and render context
        int hgt = metrics.getHeight();

        // get the advance of my text in this font
        // and render context (with a little padding).
        int adv = metrics.stringWidth(str);

        //Add a little bit of padding
        hgt += 2;
        adv += 2;
        
        return new Dimension(adv, hgt);
    }

    private void clearBackground() {
        _g2d.setBackground(background);
        _g2d.clearRect(0, 0, (int) getWidth(), (int) getHeight());
    }

    private void drawToBuffer() {

        if (_g2d == null) {
            throw new NullPointerException("Graphics2D object cannot be null!");
        }

        clearBackground();
        _g2d.setFont(font);
        _g2d.setColor(foreground);
        _g2d.drawString(message, 0, getHeight());
        shouldRedraw = false;
    }

    public void setText(String message) {
        this.message = message;
        shouldRedraw = true;
        resize();
    }

    /**
     * @param foreground the foreground to set
     */
    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    /**
     * @return the isEnabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * @param isHorizontallyCentered the isHorizontallyCentered to set
     */
    public void setHorizontallyCentered(boolean isHorizontallyCentered) {
        this.isHorizontallyCentered = isHorizontallyCentered;
    }
    
    public boolean isHorizontallyCentered() {
        return isHorizontallyCentered;
    }

    @Override
    public float getX() {
        if( isHorizontallyCentered ) {
       
            return super.getX() - (getWidth() / 2);
           
        }
        return super.getX();
    }
    
}