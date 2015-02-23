package be.guntherdw.minecraft.customfont;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.PhysicalFont;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CustomFont {

    private UnicodeFont font;
    private UnicodeFont font_italic;
    private UnicodeFont font_bold;
    private UnicodeFont font_italic_bold;
    private UnicodeFont font_toDraw;

    public Random fontRandom = new Random();

    public Set<Character> extraChars = new HashSet<Character>();

    private java.util.List<CustomString> customStringList = new ArrayList<CustomString>();

    private static Minecraft mcInstance = null;

    public CustomFont(Font awtFont) {

        if(mcInstance == null)
            mcInstance = Minecraft.getMinecraft();

        int size = awtFont.getSize();

        font = new UnicodeFont(awtFont, size, false, false);
        font.addAsciiGlyphs();
        font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        try { font.loadGlyphs(); } catch(Exception ex) {}

        font_italic = new UnicodeFont(awtFont, size, false, true);
        font_italic.addAsciiGlyphs();
        font_italic.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        try { font_italic.loadGlyphs(); } catch(Exception ex) {}

        font_bold = new UnicodeFont(awtFont, size, true, false);
        font_bold.addAsciiGlyphs();
        font_bold.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        try { font_bold.loadGlyphs(); } catch(Exception ex) {}

        font_italic_bold = new UnicodeFont(awtFont, size, true, true);
        font_italic_bold.addAsciiGlyphs();
        font_italic_bold.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        try { font_italic_bold.loadGlyphs(); } catch(Exception ex) {}

        font_toDraw = font;

    }

    public CustomFont(String fontName, int size) {
        this(new Font(fontName, Font.PLAIN, size));
    }

    public UnicodeFont getFont() {
        return font_toDraw;
    }

    public String getFontName() {
        try {
            Font font = font_toDraw.getFont();
            Field f2d = font.getClass().getDeclaredField("font2DHandle");
            f2d.setAccessible(true);
            Font2D font2D = ((Font2DHandle) f2d.get(font)).font2D;
            // return font2D.getPostscriptName();
            return font2D.getFontName(null);

        } catch (Exception e) {
            e.printStackTrace();
            return font_toDraw.getFont().getFontName();
        }
    }

    public String getFontFile() {
        try {
            Font font = font_toDraw.getFont();
            Field f2d = font.getClass().getDeclaredField("font2DHandle");
            f2d.setAccessible(true);
            Font2D font2D = ((Font2DHandle) f2d.get(font)).font2D;

            if (font2D instanceof PhysicalFont) {
                Field platName = PhysicalFont.class.getDeclaredField("platName");
                platName.setAccessible(true);
                return (String) platName.get(font2D);
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return font_toDraw.getFontFile();
        }
    }

    public void drawRect(int left, int top, int right, int bottom, int color) {
        int var5;

        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float blue = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float red = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex((double) left, (double) bottom, 0.0D);
        worldRenderer.addVertex((double) right, (double) bottom, 0.0D);
        worldRenderer.addVertex((double) right, (double) top, 0.0D);
        worldRenderer.addVertex((double) left, (double) top, 0.0D);
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void drawStringWithShadow(String text, int x, int y, int color) {
        this.drawString(mcInstance.fontRendererObj, text, x+1, y+1, color, true);
        this.drawString(mcInstance.fontRendererObj, text, x,   y,   color, false);
    }

    public void resetRenderPipeline() {
        this.customStringList.clear();
    }

    public void addStringToRenderPipeline(String text, int x, int y, int color) {
        this.customStringList.add(new CustomString(text, x, y, color, true));
    }

    public void drawStringWithShadow(ScaledResolution scaledResolution, String text, int x, int y, int color) {
        this.drawString(mcInstance.fontRendererObj, text, x + scaledResolution.getScaleFactor(), y + scaledResolution.getScaleFactor(), color, true);
        this.drawString(mcInstance.fontRendererObj, text, x, y, color, false);
    }

    public void drawString(String text, int x, int y, int color) {
        FontRenderer fontRenderer = mcInstance.fontRendererObj;
        this.drawString(fontRenderer, text, x, y, color, false);
    }

    public void drawString(FontRenderer fontRenderer,String text, int x, int y, int color, boolean shadow) {

        font_toDraw = font;

        if (text.contains("§")) {
            String[] lines = text.split("§");

            boolean firstColor = text.startsWith("§");
            boolean randomStyle = false;
            boolean boldStyle = false;
            boolean strikethroughStyle = false;
            boolean underlineStyle = false;
            boolean italicStyle = false;

            int pos = 0;
            int newX = 0;
            String colorChar;

            for (String s : lines) {
                if (s.length() > 0) {
                    colorChar = s.charAt(0) + "";
                    int colorCode = "0123456789abcdefklmnor".indexOf(colorChar.toLowerCase());

                    if (!firstColor && pos == 0) {
                        color = PrivateFieldsCustomFont.colorCode.get(fontRenderer)[shadow ? 15 + 16 : 15];
                        x += renderString(font, fontRenderer, s, x, y, color, shadow);
                        x += font.getWidth(s);
                    } else {
                        if (colorCode > -1 && colorCode < 16) {
                            color = PrivateFieldsCustomFont.colorCode.get(fontRenderer)[shadow ? colorCode + 16 : colorCode];
                            s = s.substring(1);
                        } else if (colorCode == 16) {
                            s = s.substring(1);
                            randomStyle = true;
                            newX = 0;
                            char[] charArray = s.toCharArray();
                            char newchar = 'a';
                            for (int j = 0; j < s.length(); j++) {
                                if (charArray[j] != ' ') {
                                    do {
                                        newchar = (char) (fontRandom.nextInt(94) + 32);
                                    }
                                    while (font_toDraw.getWidth(charArray[j] + "") != font_toDraw.getWidth(newchar + ""));
                                }
                                newX += font_toDraw.getWidth(charArray[j]+"");
                                charArray[j] = newchar;
                            }

                            s = String.valueOf(charArray);
                        } else if (colorCode == 17) {
                            s = s.substring(1);
                            boldStyle = true;
                            if (italicStyle)
                                font_toDraw = font_italic_bold;
                            else
                                font_toDraw = font_bold;
                        } else if (colorCode == 18) {
                            s = s.substring(1);
                            strikethroughStyle = true;
                        } else if (colorCode == 19) {
                            s = s.substring(1);
                            underlineStyle = true;
                        } else if (colorCode == 20) {
                            s = s.substring(1);
                            italicStyle = true;
                            if (boldStyle)
                                font_toDraw = font_italic_bold;
                            else
                                font_toDraw = font_italic;
                        } else if (colorCode == 21) {
                            s = s.substring(1);
                            randomStyle = false;
                            boldStyle = false;
                            strikethroughStyle = false;
                            underlineStyle = false;
                            italicStyle = false;
                            color = PrivateFieldsCustomFont.colorCode.get(fontRenderer)[shadow ? 15 + 16 : 15];
                            font_toDraw = font;
                        } else {
                            color = PrivateFieldsCustomFont.colorCode.get(fontRenderer)[shadow ? 15 + 16 : 15];
                        }
                        int fy = y;
                        int ox = x;
                        if (boldStyle) fy -= 2;
                        x += renderString(font_toDraw, fontRenderer, s, x, fy, color, shadow);
                        if(!randomStyle)
                            x += font_toDraw.getWidth(s);
                        else
                            x += newX;

                        if(strikethroughStyle) {
                            // enableDefaults();
                            GlStateManager.disableBlend();
                            int hy = y + (font_toDraw.getLineHeight()/2);
                            this.drawRect(ox, hy, x, hy + 2, addAlpha(color, 255));
                            GlStateManager.enableBlend();
                            // disableDefaults();
                        }

                        if (underlineStyle) {
                            GlStateManager.disableBlend();
                            this.drawRect(ox, y + font_toDraw.getLineHeight(), x, y + font_toDraw.getLineHeight() - 2, addAlpha(color, 255));
                            GlStateManager.enableBlend();
                        }
                    }
                }
                pos++;
            }
        } else {
            if(color == -1 || color == 0xFFFFFF)
                color = PrivateFieldsCustomFont.colorCode.get(fontRenderer)[shadow ? 15 + 16 : 15];

            renderString(text, x, y, color, shadow);
        }
    }

    public int gethex(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public int addAlpha(int var0, int var1) {
        int var2 = var0 >> 16 & 255;
        int var3 = var0 >> 8 & 255;
        int var4 = var0 & 255;
        return gethex(var2, var3, var4, var1);
    }

    public void renderString(String text, int x, int y, int color, boolean shadow) {
        if (shadow) {
            color = (color & 16579836) >> 2 | color & -16777216;
        }
        font.drawString(x, y, text, getSlickColorFromRGB(color));

    }

    public void addGlyph(char c) {
        font.addGlyphs(c, c);
        font_bold.addGlyphs(c, c);
        font_italic.addGlyphs(c, c);
        font_italic_bold.addGlyphs(c, c);
        font_toDraw.addGlyphs(c, c);
    }

    public void loadExtraGlyphs() {
        try { font.loadGlyphs(); } catch (Exception e) {}
        try { font_bold.loadGlyphs(); } catch (Exception e) {}
        try { font_italic.loadGlyphs(); } catch (Exception e) {}
        try { font_italic_bold.loadGlyphs(); } catch (Exception e) {}
        try { font_toDraw.loadGlyphs(); } catch (Exception e) {}
    }

    public int getAlpha() {
        float chatOpacity = this.mcInstance.gameSettings.chatOpacity * 0.9F + 0.1F;

        return (int) (chatOpacity * 255.0D);
    }

    public int renderString(UnicodeFont font, FontRenderer fontRenderer, String text, int x, int y, int color, boolean shadow) {
        if (shadow) {
            color = (color & 16579836) >> 2 | color & -16777216;
        }

        boolean vanilla = true;

        int cfrom = 0;
        int cto = 0;

        int width = 0;
        int nx = x;

        ScaledResolution sr = new ScaledResolution(mcInstance, mcInstance.displayWidth, mcInstance.displayHeight);
        // TODO : Fix this, kinda
        boolean loadExtraGlyphs = false;

        for(char c : text.toCharArray()) {
            if(c > 255 && !extraChars.contains(c)) {
                // System.out.println("Adding extra character : "+c);
                extraChars.add(c);
                loadExtraGlyphs = true;
                this.addGlyph(c);
            }
        }
        if(loadExtraGlyphs) {
            this.loadExtraGlyphs();
        }
        /*
        char[] chars = text.toCharArray();
        for (char c : chars) {
            int ic = (int) c;

            if(!(ic > 31 && ic < 256)) {
                // System.out.println("Getting non-vanilla char for "+ic);
                vanilla = false;
                if (text.length() > 0)
                    text = text.substring(0, cto) + " " + text.substring(cto + 1);
                else
                    text = "";

                if (cto > 0) {
                    font.drawString(nx, y, text.substring(cfrom, cto), getSlickColorFromRGB(color));
                    nx += font.getWidth(text.substring(cfrom, cto));
                    cfrom = cto + 1;
                }
                GlStateManager.translate(0.0F, (float) (sr.getScaledHeight() - 30), 0.0F);

                fontRenderer.drawString(c + "", nx / sr.getScaleFactor(), (y / sr.getScaleFactor()) + 1, color);
                width += fontRenderer.getCharWidth(c);
                nx += width * sr.getScaleFactor();
                GlStateManager.translate(0.0F, mcInstance.displayHeight - (48 * sr.getScaleFactor()), 0.0F);
            }

            cto++;
        }
        if(!vanilla && cfrom != cto) {
            if(!text.trim().equals(""))
                font.drawString(nx, y, text.substring(cfrom), getSlickColorFromRGB(color));
        }
        if (vanilla) {
            if(!text.trim().equals("")) */
                font.drawString(nx, y, text, getSlickColorFromRGB(color));
        // }
        return width>0?width / sr.getScaleFactor():0;
    }

    /**
     * Trims a string to fit a specified Width.
     */
    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    public String trimStringToWidth(String text, int width, boolean reverse) {
        StringBuilder var4 = new StringBuilder();
        int var5 = 0;
        int var6 = reverse ? text.length() - 1 : 0;
        int var7 = reverse ? -1 : 1;
        boolean var8 = false;
        boolean var9 = false;

        for (int var10 = var6; var10 >= 0 && var10 < text.length() && var5 < width; var10 += var7) {
            char var11 = text.charAt(var10);
            int var12 = this.getCharWidth(var11);

            if (var8) {
                var8 = false;

                if (var11 != 108 && var11 != 76) {
                    if (var11 == 114 || var11 == 82) {
                        var9 = false;
                    }
                } else {
                    var9 = true;
                }
            } else if (var12 < 0) {
                var8 = true;
            } else {
                var5 += var12;

                if (var9) {
                    ++var5;
                }
            }

            if (var5 > width) {
                break;
            }

            if (reverse) {
                var4.insert(0, var11);
            } else {
                var4.append(var11);
            }
        }

        return var4.toString();
    }

    /**
     * Remove all newline characters from the end of the string
     */
    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    public int getFontHeight() {
        return font_toDraw.getLineHeight();
    }

    public int getCharWidth(char character) {
        if (character == 167) {
            return -1;
        } else if (character == 32) {
            return 4;
        } else {
            return font_toDraw.getWidth(character+ "");
        }
    }
    
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        } else {
            text = EnumChatFormatting.getTextWithoutFormattingCodes(text);
            return font_toDraw.getWidth(text);
        }
    }

    public Color getSlickColorFromRGB(int color) {
        return new Color(color + (getAlpha() << 24));
    }

    public void setupOverlayRendering(ScaledResolution scaledResolution) {
        int sf = scaledResolution.getScaleFactor();
        float scale = 1.0F / sf;
        GlStateManager.scale(scale, scale, scale);
    }

    public void resetMinecraftRendering(ScaledResolution scaledResolution) {
        int sf = scaledResolution.getScaleFactor();
        float scale = 1.0F * sf;
        GlStateManager.scale(scale, scale, scale);
    }

    public float getChatScale() {
        return this.mcInstance.gameSettings.chatScale;
    }

    /**
     * Push the render pipeline onto the screen all at once
     */
    public void pushRenderPipeline(ScaledResolution scaledResolution) {
        if(customStringList.isEmpty()) return;

        setupOverlayRendering(scaledResolution);

        GlStateManager.translate(1.0F * scaledResolution.getScaleFactor(), -1.0F * scaledResolution.getScaleFactor(), 0.0F);
        float chatScale = getChatScale();
        GlStateManager.scale(chatScale, chatScale, chatScale);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for(CustomString cs : customStringList) {
            if(cs.getChatString() == null || cs.getChatString().trim().equals("")) continue;

            if(cs.hasShadow())
                this.drawStringWithShadow(scaledResolution, cs.getChatString(), cs.getX(), cs.getY(), cs.getColor());
            else
                this.drawString(cs.getChatString(), cs.getX(), cs.getY(), cs.getColor());
        }


        GlStateManager.bindTexture(0); // Slick doesn't integrate with our GlStateManager!
        GlStateManager.resetColor();   // Slick doesn't integrate with our GlStateManager!

        resetMinecraftRendering(scaledResolution);

        /**
         * Reset the pipeline for the next frame
         */
        this.resetRenderPipeline();
    }
}