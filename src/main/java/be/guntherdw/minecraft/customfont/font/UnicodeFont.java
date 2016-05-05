package be.guntherdw.minecraft.customfont.font;

import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class UnicodeFont {

    List<ColorEffect> effects;
    private Font font;
    private boolean bold;
    private boolean italic;
    private String fontFile;

    public UnicodeFont(Font awtFont, int size, boolean bold, boolean italic) {
        this.effects = new ArrayList<ColorEffect>();
        this.font = awtFont;
        this.bold = bold;
        this.italic = italic;
    }

    /**
     * Load the usual ASCII characters into memory.
     */
    public void addAsciiGlyphs() {
        addGlyphs((char) 33, (char) 126);
    }

    public List<ColorEffect> getEffects() {
        return effects;
    }

    /**
     * Load the glyphs as textures on the GPU.
     */
    public void loadGlyphs() {

    }

    /**
     * Returns the AWT font used for this instance
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Returns the path to the font used for this instance
     * @return the path, including the filename and extension
     */
    public String getFontFile() {
        return fontFile;
    }

    /**
     * Calculate how long the string is when using this font and effects without actually rendering it.
     * @param string The string to calculate the width from
     * @return The width of the string in pixels
     */
    public int getWidth(String string) {
        return 0;
    }

    /**
     * Get the height in pixels of the current font
     * @return the height in pixels
     */
    public int getLineHeight() {
        return 0;
    }

    /**
     * Render a string of text on the screen using the font
     * @param x X coordinate of where to draw the string
     * @param y Y coordinate of where to draw the string
     * @param text The line of text to render
     * @param color The color of the string
     */
    public void drawString(int x, int y, String text, Color color) {

    }

    /**
     * Loads additional characters into a buffer to be used. Will ignore already loaded glyphs.
     * @param from From where to start adding glyphs
     * @param to Where to end
     */
    public void addGlyphs(char from, char to) {

    }
}
