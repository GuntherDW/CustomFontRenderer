package be.guntherdw.minecraft.customfont.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class UnicodeFont {

    private final int IMAGE_MAX_SIZE = 256;

    private List<ColorEffect> effects;
    private Font font;
    private boolean bold;
    private boolean italic;
    private String fontFile;
    private FontMetrics fontMetrics;

    private final String resLocation = "cf:font";

    private final int GLTextureID;

    private int xPos[] = new int[256];
    private int yPos[] = new int[256];

    private final int padding = 4;

    public UnicodeFont(Font awtFont, int size, boolean bold, boolean italic) {
        this.effects = new ArrayList<ColorEffect>();
        
        Map attributeMap = awtFont.getAttributes();
        this.bold = bold;
        this.italic = italic;
        this.fontFile = "null";

        attributeMap.put(TextAttribute.SIZE, new Float(size));
        attributeMap.put(TextAttribute.WEIGHT,    bold ? TextAttribute.WEIGHT_BOLD     : TextAttribute.WEIGHT_REGULAR);
        attributeMap.put(TextAttribute.POSTURE, italic ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);

        this.font = awtFont.deriveFont(attributeMap);

        BufferedImage bufferedImage = new BufferedImage(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font);

        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, 256, 256);
        graphics.setColor(Color.white);

        fontMetrics = graphics.getFontMetrics();

        int xPos = padding;
        int yPos = padding;
        for(int c = 32; c <= 126; c++) {
            graphics.drawString((char)c+"", xPos, yPos + fontMetrics.getAscent());
            this.xPos[c] = xPos;
            this.yPos[c] = yPos /* - fontMetrics.getMaxDescent() */;
            xPos += getWidth((char)c+"") + padding;
            if(xPos >= 250 - fontMetrics.getMaxAdvance()) {
                xPos = padding;
                yPos += fontMetrics.getMaxDescent() + fontMetrics.getMaxAscent() + size / 2;
            }
        }

        // ResourceLocation rl = new ResourceLocation(getResourceLocation());
        ITextureObject ito = new DynamicTexture(bufferedImage);
        // ((DynamicTexture) ito).loadTexture();
        GLTextureID = ito.getGlTextureId();
        // rl.
        // Minecraft.getMinecraft().getTextureManager().loadTexture(rl, bufferedImage);
    }

    private String getResourceLocation() {
        return resLocation + "/" + font.getFontName() + (font.isBold() ? "-bold" : "") + (font.isItalic() ? "-italic" : "");
    }

    /* private void generateResourceLocation(BufferedImage bufferedImage) {
        ResourceLocation newResourceLocation = new ResourceLocation(resLocation);
        Minecraft.getMinecraft().getTextureManager().
    } */

    /**
     * Load the usual ASCII characters into memory.
     */
    public void addAsciiGlyphs() {
        addGlyphs((char) 32, (char) 126);
    }

    public List<ColorEffect> getEffects() {
        return effects;
    }

    /**
     * Load the glyphs as textures on the GPU.
     * Throws exception when anything went wrong.
     */
    public void loadGlyphs() throws Exception {

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
        return (int) Math.round(fontMetrics.getStringBounds(string, null).getWidth());
    }

    /**
     * Get the height in pixels of the current font
     * @return the height in pixels
     */
    public int getLineHeight() {
        // return font.getSize()/*  * LiteModCustomFont */;
        // return font.getSize() + Minecraft.getMinecraft().gameSettings.guiScale;
        // return fontMetrics.getHeight() - fontMetrics.getDescent();
        // return fontMetrics.getHeight();
        return fontMetrics.getDescent() + fontMetrics.getAscent() + fontMetrics.getLeading();
    }

    /**
     * Render a string of text on the screen using the font
     * @param x X coordinate of where to draw the string
     * @param y Y coordinate of where to draw the string
     * @param text The line of text to render
     * @param color The color of the string
     */
    public void drawString(int x, int y, String text, Color color) {
        if(text.length() == 0) return;

        GuiIngame inGameGUI = Minecraft.getMinecraft().ingameGUI;

        GlStateManager.bindTexture(GLTextureID);

        int colorInt = color.getRGB();
        int alphaInt = color.getAlpha();
        float red =   (float)(colorInt >> 16 & 0xff) / 255F;
        float green = (float)(colorInt >>  8 & 0xff) / 255F;
        float blue =  (float)(colorInt       & 0xff) / 255F;
        float alpha = (float) alphaInt               / 255F;

        GlStateManager.color(red, green, blue, alpha);

        for(char c : text.toCharArray()) {
            drawChar(inGameGUI, x, y, c, color);
            x+=getWidth(c+"");
        }
    }

    private void drawChar(Gui gui, int x, int y, char c, Color color) {
        Rectangle2D r2d = fontMetrics.getStringBounds(c+"", null);
        // gui.drawTexturedModalRect(x, y, xPos[c], yPos[c], (int) r2d.getWidth(), (int) r2d.getHeight() + fontMetrics.getMaxDescent());
           gui.drawTexturedModalRect(x, y, xPos[c], yPos[c], (int) r2d.getWidth(), (int) r2d.getHeight() + fontMetrics.getMaxDescent());
    }

    /**
     * Loads additional characters into a buffer to be used. Will ignore already loaded glyphs.
     * @param from From where to start adding glyphs
     * @param to Where to end
     */
    public void addGlyphs(char from, char to) throws IllegalArgumentException {
        if(from > to) throw new IllegalArgumentException("The from value should be lower than the to value!");

    }
}
