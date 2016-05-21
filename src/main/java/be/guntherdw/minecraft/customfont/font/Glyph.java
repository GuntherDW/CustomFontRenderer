package be.guntherdw.minecraft.customfont.font;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author GuntherDW
 */
public class Glyph {

    /**
     * The character code of this glyph
     */
    public int charcode;

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
     * The geometry of the glyph
     */

    public Rectangle2D geom;

    /**
     * Has this Glyph been rendered to the image yet?
     */
    public boolean isMissing = true;

    /**
     * The GL Texture ID this glyph is stored in
     */
    public int glTextureID;

    /**
     * The Graphics2D image of the glyph.
     */
    public BufferedImage image;

    public Glyph(int charcode, UnicodeFont font) {
        this.charcode = charcode;

        /* this.xPos =                  charcode % font.divider;
        this.yPos = (int) Math.floor(charcode / font.divider); */
    }

    public void renderGlyph(UnicodeFont font) {
        /* int x = font.paddingX * (font.divider / 32);
        inty = font.paddingY * (font.divider / 32); */
        int x = 0;
        int y = 0;

        int imageSize = font.divider;

        image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font.getFont());

        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, imageSize, imageSize);
        graphics.setColor(Color.white);

        FontMetrics fontMetrics = graphics.getFontMetrics();
        if(font.fontMetrics == null) font.fontMetrics = fontMetrics;

        this.geom   = fontMetrics.getStringBounds((char)this.charcode + "", null);
        this.width  = fontMetrics.stringWidth((char)this.charcode + "");
        this.height = fontMetrics.getDescent() + fontMetrics.getAscent() + fontMetrics.getLeading();

        graphics.drawString((char) charcode+"", imageSize/2 - width/2, imageSize/2 + (font.getLineHeight())/2);

    }
}
