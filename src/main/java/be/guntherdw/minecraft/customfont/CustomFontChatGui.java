package be.guntherdw.minecraft.customfont;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;

public class CustomFontChatGui extends GuiNewChat {
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;

    /**
     * A list of messages previously sent through the chat GUI
     */
    private final List<String> sentMessages = Lists.newArrayList();

    /**
     * Chat lines to be displayed in the chat box
     */
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();

    private final List<ITextComponent> received_lines = Lists.newArrayList();

    private int scrollPos;
    private boolean isScrolled;

    public CustomFont customFont;
    public static boolean customChat;

    public static int startHeight = 28;


    private int amtOfChat = 200;

    public CustomFontChatGui(Minecraft mcIn) {
        super(mcIn);
        this.mc = mcIn;
    }

    public CustomFontChatGui(Minecraft mcIn, CustomFont customFont) {
        super(mcIn);
        this.mc = mcIn;
        this.customFont = customFont;
        this.customChat = true;
    }

    public void redrawChat(ITextComponent[] components) {
        // int id = 1;
        for(ITextComponent component : components) {
            this.addToReceivedLinesWithoutLog(component);
            // id++;
        }
    }

    public void setCustomFont(CustomFont customFont) {
        this.customFont = customFont;
    }

    @Override
    public void drawChat(int p_146230_1_) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int lineCount = this.getLineCount();
            boolean isChatOpen = false;
            int amountOfShownLines = 0;
            int chatLinesSize = this.drawnChatLines.size();
            float chatOpacity = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int sf = scaledResolution.getScaleFactor();

            if (chatLinesSize > 0) {
                if (this.getChatOpen()) {
                    isChatOpen = true;
                }

                float chatScale = this.getChatScale();
                float chatWidth = this.getChatWidth();
                int var8 = MathHelper.ceiling_float_int(chatWidth / chatScale);

                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(chatScale, chatScale, 1.0F);
                int var9;
                int var11;
                int var14;

                for (var9 = 0; var9 + this.scrollPos < this.drawnChatLines.size() && var9 < lineCount; ++var9) {
                    ChatLine chatLine = this.drawnChatLines.get(var9 + this.scrollPos);

                    if (chatLine != null) {
                        var11 = p_146230_1_ - chatLine.getUpdatedCounter();

                        if (var11 < 200 || isChatOpen) {
                            double var12 = (double) var11 / 200.0D;
                            var12 = 1.0D - var12;
                            var12 *= 10.0D;
                            var12 = MathHelper.clamp_double(var12, 0.0D, 1.0D);
                            var12 *= var12;
                            var14 = (int) (255.0D * var12);

                            if (isChatOpen) {
                                var14 = 255;
                            }

                            var14 = (int) ((float) var14 * chatOpacity);
                            ++amountOfShownLines;

                            if (var14 > 3) {
                                if (customChat) {
                                    byte var15 = 0;
                                    // int fontHeight = customFont.getFontHeight() / sf;
                                    int fontHeight = customFont.getFontHeight() / sf;
                                    int var16 = -var9 * fontHeight;

                                    // GlStateManager.translate(2.0F, scaledResolution.getScaledHeight() - 30, 0.0F);
                                    customFont.drawRect(var15, var16 - fontHeight, var15 + var8 + 4, var16, var14 / 2 << 24);

                                    // GL11.glTranslatef(2.0F * sf, mc.displayHeight - (30 * sf), 0.0F);
                                    customFont.addStringToRenderPipeline(chatLine.getChatComponent().getFormattedText(), var15, (var16 - fontHeight) * sf, 16777215 + (var14 << 24));

                                } else {
                                    byte var15 = 0;
                                    int var16 = -var9 * 9;
                                    drawRect(var15, var16 - 9, var15 + var8 + 4, var16, var14 / 2 << 24);
                                    String var17 = chatLine.getChatComponent().getFormattedText();
                                    GlStateManager.enableBlend();
                                    this.mc.fontRendererObj.drawStringWithShadow(var17, (float) var15, (float) (var16 - 8), 16777215 + (var14 << 24));
                                    GlStateManager.disableAlpha();
                                    GlStateManager.disableBlend();
                                }
                            }
                        }
                    }
                }

                if(customChat)
                    customFont.pushRenderPipeline(scaledResolution);

                if (isChatOpen) {
                    int fontHeight = customChat ? customFont.getFontHeight() / scaledResolution.getScaleFactor() : this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int var18 =  chatLinesSize * fontHeight + chatLinesSize;
                    var11 = amountOfShownLines * fontHeight + amountOfShownLines;
                    int var19 = this.scrollPos * var11 / chatLinesSize;
                    int var13 =          var11 * var11 / var18;
                    var13 /= 2; // (Max amount of lines is 200, from 100, which makes it *2

                    if (var18 != var11) {
                        var14 = var19 > 0 ? 170 : 96;
                        int scrollBarColour = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -var19, 2, -var19 - var13,    scrollBarColour + (var14 << 24));
                        drawRect(2, -var19, 1, -var19 - var13,           13421772 + (var14 << 24));
                        // PrivateFieldsCustomFont.inputField.get((GuiChat) mc.currentScreen).setText("sp : "+scrollPos+" v19 : "+var19+" v13 : "+var13+" v4 : "+amountOfShownLines+" v9: "+fontHeight);
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    public void drawStringWithShadow(FontRenderer var0, ScaledResolution scaledResolution, String var1, int var2, int var3, int var4) {
        customFont.drawStringWithShadow(scaledResolution, var1, var2 + 5, (var3 - 2) * 2, var4);
    }

    /**
     * Clears the chat.
     */
    @Override
    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    @Override
    public void printChatMessage(ITextComponent p_146227_1_) {
        this.printChatMessageWithOptionalDeletion(p_146227_1_, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    @Override
    public void printChatMessageWithOptionalDeletion(ITextComponent p_146234_1_, int p_146234_2_) {
        this.setChatLine(p_146234_1_, p_146234_2_, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + p_146234_1_.getUnformattedText());
        // logger.info("[CHAT] " + p_146234_1_.getFormattedText());
    }

    public void addToReceivedLinesWithoutLog(ITextComponent chatComponent) {
        this.setChatLine(chatComponent, 0, this.mc.ingameGUI.getUpdateCounter(), false);
    }

    public static String removeFormattingCodes(String string) {
        return TextFormatting.getTextWithoutFormattingCodes(string);
    }

    public static String removeTextColorsIfConfigured(String p_178909_0_, boolean p_178909_1_) {
        return !p_178909_1_ && !Minecraft.getMinecraft().gameSettings.chatColours ? TextFormatting.getTextWithoutFormattingCodes(p_178909_0_) : p_178909_0_;
    }

    public List<ITextComponent> getChatComponents(ScaledResolution scaledResolution, ITextComponent chatComponent, int maxWidth, FontRenderer fontRenderer, boolean p_178908_3_, boolean p_178908_4_) {
        int var5 = 0;
        ITextComponent var6 = new TextComponentString("");
        List<ITextComponent> var7 = Lists.newArrayList();
        List<ITextComponent> var8 = Lists.newArrayList(chatComponent);
        // TODO : Get the colors that were unsolved on the last line and re-apply

        for (int var9 = 0; var9 < var8.size(); ++var9) {
            ITextComponent var10 = var8.get(var9);
            String var11 = var10.getUnformattedComponentText();
            boolean var12 = false;
            String var14;

            if (var11.contains("\n")) {
                int var13 = var11.indexOf(10);
                var14 = var11.substring(var13 + 1);
                var11 = var11.substring(0, var13 + 1);
                ITextComponent var15 = new TextComponentString(var14);
                var15.setStyle(var10.getStyle().createShallowCopy());
                var8.add(var9 + 1, var15);
                var12 = true;
            }

            String var21 = removeTextColorsIfConfigured(var10.getStyle().getFormattingCode() + var11, p_178908_4_);
            var14 = var21.endsWith("\n") ? var21.substring(0, var21.length() - 1) : var21;


            int var22 = customFont.getStringWidth(var14);

            TextComponentString var16 = new TextComponentString(var14);
            var16.setStyle(var10.getStyle().createShallowCopy());

            if (var5 + var22 > maxWidth) {
                String var17 = customFont.trimStringToWidth(var21, maxWidth - var5, false);
                String var18 = var17.length() < var21.length() ? var21.substring(var17.length()) : null;

                if (var18 != null && var18.length() > 0) {
                    int var19 = var17.lastIndexOf(" ");

                    if (var19 >= 0 && customFont.getStringWidth(var21.substring(0, var19)) > 0) {
                        var17 = var21.substring(0, var19);

                        if (p_178908_3_) {
                            ++var19;
                        }

                        var18 = var21.substring(var19);
                    } else if (var5 > 0 && !var21.contains(" ")) {
                        var17 = "";
                        var18 = var21;
                    }

                    TextComponentString var20 = new TextComponentString(var18);
                    var20.setStyle(var10.getStyle().createShallowCopy());
                    var8.add(var9 + 1, var20);
                }

                var22 = fontRenderer.getStringWidth(var17);
                var16 = new TextComponentString(var17);
                var16.setStyle(var10.getStyle().createShallowCopy());
                var12 = true;
            }

            if (var5 + var22 <= maxWidth) {
                var5 += var22;
                var6.appendSibling(var16);
            } else {
                var12 = true;
            }

            if (var12) {
                var7.add(var6);
                var5 = 0;
                var6 = new TextComponentString("");
            }
        }

        var7.add(var6);
        return var7;
    }

    private void setChatLine(ITextComponent p_146237_1_, int p_146237_2_, int p_146237_3_, boolean p_146237_4_) {
        if (p_146237_2_ != 0) {
            this.deleteChatLine(p_146237_2_);
        } else {
            received_lines.add(p_146237_1_);
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        List<ITextComponent> var6;

        int var5 = MathHelper.floor_float((float) this.getChatWidth() / getChatScale());

        if (customChat) {
            var6 = getChatComponents(scaledResolution, p_146237_1_, var5*scaledResolution.getScaleFactor(), this.mc.fontRendererObj, false, false);
        } else {
            var6 = GuiUtilRenderComponents.splitText(p_146237_1_, var5, this.mc.fontRendererObj, false, false);
        }

        boolean var7 = this.getChatOpen();
        TextComponentString var9;

        for (Iterator var8 = var6.iterator(); var8.hasNext(); this.drawnChatLines.add(0, new ChatLine(p_146237_3_, var9, p_146237_2_))) {
            var9 = (TextComponentString) var8.next();

            if (var7 && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }
        }

        while (this.drawnChatLines.size() > amtOfChat) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!p_146237_4_) {
            this.chatLines.add(0, new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_));

            while (this.chatLines.size() > amtOfChat) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    @Override
    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();

        for (int var1 = this.chatLines.size() - 1; var1 >= 0; --var1) {
            ChatLine var2 = (ChatLine) this.chatLines.get(var1);
            this.setChatLine(var2.getChatComponent(), var2.getChatLineID(), var2.getUpdatedCounter(), true);
        }
    }

    /**
     * Gets the list of messages previously sent through the chat GUI
     */
    @Override
    public List getSentMessages() {
        return this.sentMessages;
    }

    /**
     * Adds this string to the list of sent messages, for recall using the up/down arrow keys
     */
    @Override
    public void addToSentMessages(String p_146239_1_) {
        if (this.sentMessages.isEmpty() || !((String) this.sentMessages.get(this.sentMessages.size() - 1)).equals(p_146239_1_)) {
            this.sentMessages.add(p_146239_1_);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    @Override
    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    /**
     * Scrolls the chat by the given number of lines.
     */
    @Override
    public void scroll(int p_146229_1_) {
        this.scrollPos += p_146229_1_;
        int var2 = this.drawnChatLines.size();

        if (this.scrollPos > var2 - this.getLineCount()) {
            this.scrollPos = var2 - this.getLineCount();
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }
    /**
     * Gets the chat component under the mouse
     */
    @Override
    public TextComponentString getChatComponent(int x, int y) {
        if (!this.getChatOpen()) {
            return null;
        } else {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int scaleFactor = scaledResolution.getScaleFactor();
            float chatScale = this.getChatScale();

            if (customChat) {
                int var6 = x  - 1 * scaleFactor;
                int var7 = y / scaleFactor - CustomFontChatGui.startHeight;
                var6 = MathHelper.floor_float((float) var6 / chatScale);
                var7 = MathHelper.floor_float((float) var7 / chatScale);

                if (var6 >= 0 && var7 >= 0) {
                    int var8 = Math.min(this.getLineCount(), this.drawnChatLines.size());

                    if (var6 <= MathHelper.floor_float((float) (this.getChatWidth()*scaleFactor) / this.getChatScale()) && var7 < (customFont.getFontHeight()*scaleFactor) * var8 + var8) {
                        int var9 = var7 / (customFont.getFontHeight() / scaleFactor) + this.scrollPos;

                        if (var9 >= 0 && var9 < this.drawnChatLines.size()) {
                            ChatLine var10 = this.drawnChatLines.get(var9);
                            int var11 = 0;
                            Iterator var12 = var10.getChatComponent().iterator();

                            while (var12.hasNext()) {
                                TextComponentString var13 = (TextComponentString) var12.next();

                                if (var13 instanceof TextComponentString) {
                                    var11 += customFont.getStringWidth(removeFormattingCodes(((TextComponentString) var13).getUnformattedComponentText()));

                                    if (var11 > var6) {
                                        // PrivateFieldsCustomFont.inputField.get((GuiChat) mc.currentScreen).setText("var9 : "+var9+" x : "+x+" y : "+y+EnumChatFormatting.getTextWithoutFormattingCodes(var13.getFormattedText()));
                                        return var13;
                                    }/*  else {
                                        PrivateFieldsCustomFont.inputField.get((GuiChat) mc.currentScreen).setText("var9 : "+var9+" x : "+x+" y : "+y);
                                    } */
                                }
                            }
                        }

                        return null;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                int var6 = x / scaleFactor - 3;
                int var7 = y / scaleFactor - 27;
                var6 = MathHelper.floor_float((float) var6 / chatScale);
                var7 = MathHelper.floor_float((float) var7 / chatScale);

                if (var6 >= 0 && var7 >= 0) {
                    int var8 = Math.min(this.getLineCount(), this.drawnChatLines.size());

                    if (var6 <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && var7 < this.mc.fontRendererObj.FONT_HEIGHT * var8 + var8) {
                        int var9 = var7 / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                        if (var9 >= 0 && var9 < this.drawnChatLines.size()) {
                            ChatLine var10 = this.drawnChatLines.get(var9);
                            int var11 = 0;
                            Iterator var12 = var10.getChatComponent().iterator();

                            while (var12.hasNext()) {
                                TextComponentString var13 = (TextComponentString) var12.next();

                                if (var13 instanceof TextComponentString) {
                                    var11 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString) var13).getUnformattedComponentText(), false));

                                    if (var11 > var6) {
                                        return var13;
                                    }
                                }
                            }
                        }

                        return null;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Returns true if the chat GUI is open
     */
    @Override
    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    public List<ITextComponent> getReceived_lines() {
        return received_lines;
    }

    /**
     * finds and deletes a Chat line by ID
     */
    @Override
    public void deleteChatLine(int p_146242_1_) {
        Iterator var2 = this.drawnChatLines.iterator();
        ChatLine var3;

        while (var2.hasNext()) {
            var3 = (ChatLine) var2.next();

            if (var3.getChatLineID() == p_146242_1_) {
                var2.remove();
            }
        }

        var2 = this.chatLines.iterator();

        while (var2.hasNext()) {
            var3 = (ChatLine) var2.next();

            if (var3.getChatLineID() == p_146242_1_) {
                var2.remove();
                break;
            }
        }
    }

    @Override
    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    @Override
    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    /**
     * Returns the chatscale from mc.gameSettings.chatScale
     */
    @Override
    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float p_146233_0_) {
        int var1 = LiteModCustomFont.maxWidth * 8;
        int var2 = LiteModCustomFont.maxWidth;
        return MathHelper.floor_float(p_146233_0_ * (float) (var1 - var2) + (float) var2);
    }

    public static int calculateChatboxHeight(float p_146243_0_) {
        int var1 = LiteModCustomFont.amtOfLines * 9;
        int var2 = LiteModCustomFont.amtOfLines;

        return MathHelper.floor_float(p_146243_0_ * (float) (var1 - var2) + (float) var2);
    }

    @Override
    public int getLineCount() {
        return this.getChatHeight() / 9;
    }
}