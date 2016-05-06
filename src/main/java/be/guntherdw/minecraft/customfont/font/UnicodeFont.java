package be.guntherdw.minecraft.customfont.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
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

    protected final int IMAGE_MAX_SIZE = 1024;

    private List<ColorEffect> effects;
    private Font font;
    private int size;
    private boolean bold;
    private boolean italic;
    private String fontFile;
    protected FontMetrics fontMetrics;

    private final String resLocation = "cf:font";
    private final int paddingX = 4;
    private final int paddingY = 4;

    private ITextureObject ito = null;
    public int GLTextureID;

    private Glyph[] glyphs;

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
            System.out.println("Growing array!");
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
        this.effects = new ArrayList<ColorEffect>();

        Map attributeMap = awtFont.getAttributes();
        this.bold = bold;
        this.italic = italic;
        this.fontFile = "null";
        this.size = size;
        this.glyphs = new Glyph[256];

        attributeMap.put(TextAttribute.SIZE, new Float(size));
        attributeMap.put(TextAttribute.WEIGHT,    bold ? TextAttribute.WEIGHT_BOLD     : TextAttribute.WEIGHT_REGULAR);
        attributeMap.put(TextAttribute.POSTURE, italic ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);

        this.font = awtFont.deriveFont(attributeMap);
        // this.fontMetrics = this.font.
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
    public void loadGlyphs() {
        BufferedImage bufferedImage = new BufferedImage(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        /* graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(this.font);

        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
        graphics.setColor(Color.white);

        fontMetrics = graphics.getFontMetrics();

        int xPos = 2;
        int yPos = 2 - fontMetrics.getMaxDescent();
        for (int c = 32; c <= 126; c++) {
            graphics.drawString((char) c + "", xPos, yPos + fontMetrics.getAscent());
            this.xPos[c] = xPos;
            this.yPos[c] = yPos;
            xPos += getWidth((char) c + "") + paddingX;
            if (xPos >= (IMAGE_MAX_SIZE - paddingX) - fontMetrics.getMaxAdvance()) {
                xPos = 2;
                yPos += fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + size / 2;
            }
        } */

        int xPos = 0;
        int yPos = 0;
        for(Glyph glyph : glyphs) {
            if(glyph != null) {
                if(glyph.isMissing) glyph.renderGlyph(this);
                graphics.drawImage(glyph.image, null, xPos, yPos);
                glyph.xPos = xPos;
                glyph.yPos = yPos;
            }
            xPos += 32;
            // yPos += 32;
            if(xPos == IMAGE_MAX_SIZE) {
                yPos += 32;
                xPos = 0;
            }
        }

        // ResourceLocation rl = new ResourceLocation(getResourceLocation());
        if(ito != null) ((DynamicTexture) ito).deleteGlTexture();

        ito = new DynamicTexture(bufferedImage);
        // ((DynamicTexture) ito).loadTexture();
        GLTextureID = ito.getGlTextureId();
        // rl.
        // Minecraft.getMinecraft().getTextureManager().loadTexture(rl, bufferedImage);
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
        return fontMetrics.stringWidth(string);
    }

    /**
     * Get the height in pixels of the current font
     * @return the height in pixels
     */
    public int getLineHeight() {
        return fontMetrics.getDescent() + fontMetrics.getAscent() + fontMetrics.getLeading();
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

        GlStateManager.bindTexture(GLTextureID);

        /* int colorInt = color.getRGB();
        int alphaInt = color.getAlpha(); */
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
        Rectangle2D r2d = fontMetrics.getStringBounds(c+"", null);
        // if(IMAGE_MAX_SIZE == 256) {
        //  gui.drawTexturedModalRect(x, y, xPos[c], yPos[c], (int) r2d.getWidth(), (int) (r2d.getHeight() + fontMetrics.getMaxDescent()));
        if(glyphs[c] == null) {
            this.addGlyphs(c, c);
            this.loadGlyphs();
        }

        gui.drawModalRectWithCustomSizedTexture(x, y, glyphs[c].xPos, glyphs[c].yPos, (int) Math.ceil(glyphs[c].width) + paddingX, (int) (r2d.getHeight() + fontMetrics.getMaxDescent()), IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);

        /* } else {
            Tessellator tes = Tessellator.getInstance();
            VertexBuffer vb = tes.getBuffer();
            int zLevel = PrivateFieldsCustomFont.zLevel.get(gui);
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            vb.pos(x, y, zLevel).endVertex();
        */
            /*
            VertexBuffer lvt_10_1_ = lvt_9_1_.getBuffer();
            lvt_10_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_10_1_.pos((double) (x +     0), (double) (y + height), (double) this.zLevel).tex((double) ((float) (texPosX +     0) * lvt_7_1_), (double) ((float) (texPosY + height) * lvt_8_1_)).endVertex();
            lvt_10_1_.pos((double) (x + width), (double) (y + height), (double) this.zLevel).tex((double) ((float) (texPosX + width) * lvt_7_1_), (double) ((float) (texPosY + height) * lvt_8_1_)).endVertex();
            lvt_10_1_.pos((double) (x + width), (double) (y +      0), (double) this.zLevel).tex((double) ((float) (texPosX + width) * lvt_7_1_), (double) ((float) (texPosY +      0) * lvt_8_1_)).endVertex();
            lvt_10_1_.pos((double) (x +     0), (double) (y +      0), (double) this.zLevel).tex((double) ((float) (texPosX +     0) * lvt_7_1_), (double) ((float) (texPosY +      0) * lvt_8_1_)).endVertex();
            lvt_9_1_.draw();
            */
        //}
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
