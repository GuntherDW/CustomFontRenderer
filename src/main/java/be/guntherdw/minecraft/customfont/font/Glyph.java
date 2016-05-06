package be.guntherdw.minecraft.customfont.font;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * @author GuntherDW
 */
public class Glyph {

    /**
     * The character code of this glyph
     */
    private int charcode;

    /**
     * X position of the glyph
     */
    public int xPos;

    /**
     * Y position of the glyph
     */
    public int yPos;

    /**
     * The height of the glyph
     */
    public int height;

    /**
     * The width of the glyph
     */
    public int width;

    /**
     * Has this Glyph been rendered to the image yet?
     */
    public boolean isMissing = true;

    /**
     * The Graphics2D image of the glyph.
     */
    public BufferedImage image;

    public Glyph(int charcode, UnicodeFont font) {
        this.charcode = charcode;

        this.xPos =                  charcode % 32;
        this.yPos = (int) Math.floor(charcode / 32);
    }

    public void renderGlyph(UnicodeFont font) {
        int x = 2;
        int y = 2;

        int imageSize = 32;

        image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font.getFont());

        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, imageSize, imageSize);
        graphics.setColor(Color.white);

        FontMetrics fontMetrics = graphics.getFontMetrics();
        if(font.fontMetrics == null) font.fontMetrics = fontMetrics;

        this.width  = fontMetrics.stringWidth((char)this.charcode + "");
        this.height = fontMetrics.getDescent() + fontMetrics.getAscent() + fontMetrics.getLeading();

        graphics.drawString((char) charcode+"", x, y + fontMetrics.getAscent());

    }
}
