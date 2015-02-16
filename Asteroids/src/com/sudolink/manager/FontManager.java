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
package com.sudolink.manager;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matsu
 */
public class FontManager {

    private static FontManager instance;
    private final Map<String, Font> fonts;

    private FontManager() {
        fonts = new HashMap<>();
    }

    public static synchronized FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }

        return instance;
    }

    /**
     * This method inserts and loads a font, using the given fontname and filename
     * for the font file. Always prepare fonts before use.
     * 
     * @param fontname The unique key for the font.
     * @param filename The filename used to load the font.
     */
    public void prepareFont(String fontname, String filename) {
        Font font;
        try {
            font = Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream(filename)
            );
            fonts.put(fontname, font);
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(FontManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Returns a font for the given name (key) with the provided font size. If 
     * the particular font size has not yet been prepared, it will be prepared 
     * now and stored for later.
     * 
     * @param fontname The unique key for the font, provided at prepareFont().
     * @param size The size of the font.
     * @return 
     */
    public Font getFont(String fontname, float size) {
        String font_plus_size = fontname + (int) size;
        if (!fonts.containsKey(font_plus_size)) {
            fonts.put(font_plus_size, fonts.get(fontname).deriveFont(size));
        }
        return fonts.get(font_plus_size);
    }

}
