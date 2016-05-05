package be.guntherdw.minecraft.customfont.font;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class UnicodeFont {

    List<ColorEffect> effects;
    private Font font;
    private String fontFile;

    public UnicodeFont(Font awtFont, int size, boolean bold, boolean italic) {
        effects = new ArrayList<ColorEffect>();
    }

    public void addAsciiGlyphs() {
    }

    public List<ColorEffect> getEffects() {
        return effects;
    }

    public void loadGlyphs() {

    }

    public Font getFont() {
        return font;
    }

    public String getFontFile() {
        return fontFile;
    }

    public int getWidth(String s) {
        return 0;
    }

    public int getLineHeight() {
        return 0;
    }

    public void drawString(int x, int y, String text, Color color) {

    }

    public void addGlyphs(char from, char to) {

    }
}
