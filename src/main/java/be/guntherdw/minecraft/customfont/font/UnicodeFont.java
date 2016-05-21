package be.guntherdw.minecraft.customfont.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class UnicodeFont {

    public static int IMAGE_MAX_SIZE = -1;

    private List<ColorEffect> effects;
    private Font font;
    private int size;
    private boolean bold;
    private boolean italic;
    private String fontFile;
    protected FontMetrics fontMetrics;

    private final String resLocation = "cf:font";
    protected final int paddingX = 4;
    protected final int paddingY = 4;

    public int divider = 32;

    public List<DynamicTexture> dynamicTextures;
    public int currentGLTextureID;

    /** TODO : Set to private when done debugging **/
    public Glyph[] glyphs;

    /* private int xPos[] = new int[256];
    private int yPos[] = new int[256]; */

    private void checkGlyphSpace(char c) {
        // First calculate how much of the space is used.
        int size = glyphs.length;
        int realsize = 0;
        for(Glyph glyph : glyphs) {
            if(glyph != null) realsize++;
        }

        if(c > size) {
            // System.out.println("Growing array!");
            Glyph[] tmp = new Glyph[(int) c + 64];
            System.arraycopy(glyphs, 0, tmp, 0, glyphs.length);
            glyphs = tmp;
        } else if(size - realsize < 256) {
            // Grow array
            /* System.out.println("realsize : "+realsize);
            System.out.println("newsize  : "+ (size + 256));
            System.out.println("Growing array!"); */
            Glyph[] tmp = new Glyph[size + 256];
            System.arraycopy(glyphs, 0, tmp, 0, glyphs.length);
            glyphs = tmp;
        }
    }

    public UnicodeFont(Font awtFont, int size, boolean bold, boolean italic) {

        if(UnicodeFont.IMAGE_MAX_SIZE == -1) {
            System.out.println("[CFR] Getting max texture size.");
            UnicodeFont.IMAGE_MAX_SIZE = Math.min(GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE), 1024);
            System.out.println("[CFR] Set maximum size to "+UnicodeFont.IMAGE_MAX_SIZE);
        }

        this.effects = new ArrayList<ColorEffect>();
        this.dynamicTextures = new ArrayList<DynamicTexture>();

        Map attributeMap = awtFont.getAttributes();
        this.bold = bold;
        this.italic = italic;
        this.fontFile = "null";
        this.size = size;
        this.glyphs = new Glyph[256];

        // attributeMap.put(TextAttribute.SIZE, new Float(size).floatValue());
        attributeMap.put(TextAttribute.WEIGHT,    bold ? TextAttribute.WEIGHT_BOLD     : TextAttribute.WEIGHT_REGULAR);
        attributeMap.put(TextAttribute.POSTURE, italic ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);

        this.font = awtFont.deriveFont(attributeMap);
        // this.fontMetrics = this.font.
    }

    private String getResourceLocation() {
        return resLocation + "/" + font.getFontName() + (font.isBold() ? "-bold" : "") + (font.isItalic() ? "-italic" : "");
    }

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
    public void loadGlyphs() {

        /** Destroy the old GLTextureID's first **/
        for(DynamicTexture dynamicTexture : dynamicTextures) {
            dynamicTexture.deleteGlTexture();
        }
        dynamicTextures.clear();
        currentGLTextureID = -1;

        BufferedImage bufferedImage = new BufferedImage(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        this.fontMetrics = graphics.getFontMetrics();

        this.divider = calcDivider(fontMetrics);

        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
        graphics.setColor(Color.white);

        Set<Integer> tempGlyphIDs = new HashSet<Integer>();

        int xPos = 0;
        int yPos = 0;

        boolean draw_box = false;

        for(int current = 0; current < glyphs.length; current++) {
            Glyph glyph = glyphs[current];

            if (glyph != null) {
                glyph = glyphs[current];
                if (glyph.isMissing) glyph.renderGlyph(this);
                graphics.drawImage(glyph.image, null, xPos, yPos);
                glyph.xPos = xPos;
                glyph.yPos = yPos;

                if(draw_box) {
                    graphics.drawLine(glyph.xPos, glyph.yPos, glyph.xPos, glyph.yPos + divider);
                    graphics.drawLine(glyph.xPos, glyph.yPos, glyph.xPos + divider, glyph.yPos);
                    graphics.drawLine(glyph.xPos + divider, glyph.yPos + divider, glyph.xPos, glyph.yPos + divider);
                    graphics.drawLine(glyph.xPos + divider, glyph.yPos + divider, glyph.xPos + divider, glyph.yPos);
                }
            }

            xPos += divider;
            if (xPos == IMAGE_MAX_SIZE) {
                yPos += divider;
                xPos = 0;
            }

            // Either last or full page
            if(current + 1 == glyphs.length || (xPos == IMAGE_MAX_SIZE && yPos == IMAGE_MAX_SIZE)) {
                ITextureObject ito = new DynamicTexture(bufferedImage);
                int tempTextureID = ito.getGlTextureId();

                for(Integer i : tempGlyphIDs) glyphs[i].glTextureID = tempTextureID;
                tempGlyphIDs.clear();

                xPos = 0;
                yPos = 0;
                if(!(current + 1 == glyphs.length)) { /** Clear the image **/
                    graphics.setColor(new Color(255, 255, 255, 0));
                    graphics.fillRect(0, 0, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
                    graphics.setColor(Color.white);
                }
            } else {
                if(glyph != null) tempGlyphIDs.add(glyph.charcode);
            }
        }

    }

    private int calcDivider(FontMetrics fontMetrics) {
        int rem = font.getSize();
        int size = 32;

        while(rem + ( paddingY * 2 ) > size + paddingY) {
            size *= 2;
            rem -= size;
        }

        return size;
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
        // return fontMetrics.stringWidth(string);
        // char[] chars = string.toCharArray();
        //
        // return (int) Math.ceil(fontMetrics.getStringBounds(string, null).getWidth()) + paddingX;
        char[] chars = string.toCharArray();
        int w = 0;
        for(char c : chars) {
            if(glyphs[c] != null)
                w += glyphs[c].width;
        }
        return w;
    }

    /**
     * Get the height in pixels of the current font
     * @return the height in pixels
     */
    public int getLineHeight() {
        // return fontMetrics.getDescent() + fontMetrics.getAscent() + fontMetrics.getLeading();
        return (int) Math.ceil(font.getSize2D());
    }

    /**
     * Render a string of text on the screen using the font
     * @param x X coordinate of where to draw the string
     * @param y Y coordinate of where to draw the string
     * @param text The line of text to render
     * @param color The color of the string
     */
    public void drawString(int x, int y, String text, int color) {
        if(text.length() == 0) return;

        GuiIngame inGameGUI = Minecraft.getMinecraft().ingameGUI;

        float red =   (float)(color >> 16 & 0xff) / 255F;
        float green = (float)(color >>  8 & 0xff) / 255F;
        float blue =  (float)(color       & 0xff) / 255F;
        float alpha = (float) color               / 255F;

        GlStateManager.color(red, green, blue, alpha);

        for(char c : text.toCharArray()) {
            drawChar(inGameGUI, x, y, c);
            x += getWidth(c+"");
        }
    }

    private void drawChar(Gui gui, int x, int y, char c) {

        if(glyphs[c] == null) {
            this.addGlyphs(c, c);
            this.loadGlyphs();
        }

        /**
         * If the glyph is on another Texture ID than the current, bind it first
         */
        if(glyphs[c].glTextureID != this.currentGLTextureID) {
            GlStateManager.bindTexture(glyphs[c].glTextureID);
        }

        int startX = (int) (glyphs[c].xPos + (divider / 2) - (glyphs[c].geom.getWidth()  / 2));
        int startY = (int) (glyphs[c].yPos + (divider / 2) - (glyphs[c].geom.getHeight() / 2));

        // int width  = glyphs[c].width;
        int width  = divider - paddingX;
        // int height = (int) glyphs[c].height;
        int height = divider - paddingY;

        gui.drawModalRectWithCustomSizedTexture(x,  y - (int) (glyphs[c].geom.getHeight() / 4), startX, startY, width, height, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
    }


    /**
     * Loads additional characters into a buffer to be used. Will ignore already loaded glyphs.
     * @param from From where to start adding glyphs
     * @param to Where to end
     */
    public void addGlyphs(char from, char to) throws IllegalArgumentException {
        if(from > to) throw new IllegalArgumentException("The from value should be lower than the to value!");
        for(int c = from; c <= to; c++) {
            addGlyphs(new String(Character.toChars(c)));
        }
    }

    public void addGlyphs(String text) {
        if(text == null) throw new IllegalArgumentException("text cannot be null!");
        char[] chars = text.toCharArray();
        for(char c : chars) {
            checkGlyphSpace(c);
            if(glyphs[c] == null) {
                glyphs[c] = new Glyph(c, this);
            }
        }
    }
}
