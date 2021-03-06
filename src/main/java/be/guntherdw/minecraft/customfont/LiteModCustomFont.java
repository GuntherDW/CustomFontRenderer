package be.guntherdw.minecraft.customfont;

import com.mumfrey.liteloader.OutboundChatFilter;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.ViewportListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.ChatUtilities;
import com.mumfrey.liteloader.util.PrivateFields;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class LiteModCustomFont implements Tickable, OutboundChatFilter, ViewportListener {

    private Minecraft minecraft;
    public static LiteModCustomFont instance;

    protected CustomFont customFont;
    protected String fontName = "Verdana bold";
    protected int fontSize = 16;

    public static byte amtOfLines = 20;
    public static byte maxWidth = 40;

    private CustomFontChatGui chatGui;

    private boolean replacedChat = false;
    protected boolean replaceFont = false;
    protected Font toReplaceFont = null;

    private File configPath;

    private boolean debug = true;

    private CustomFontSelectionDialog customFontSelectionDialog = new CustomFontSelectionDialog();


    /**
     * Called every frame
     *
     * @param minecraft    Minecraft instance
     * @param partialTicks Partial tick value
     * @param inGame       True if in-game, false if in the menu
     * @param clock        True if this is a new tick, otherwise false if it's a regular frame
     */
    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if(!inGame) return;
        if(replaceFont) {
            replaceFont = false;
            this.customFont = new CustomFont(toReplaceFont);

            fontName = toReplaceFont.getFontName();
            fontSize = toReplaceFont.getSize();
            recreateChatWindow();
            addChat("Font set to " + customFont.getFontName() + " (size:"+fontSize+") (file: "+customFont.getFontFile()+")!");
            saveSettings(configPath);
            toReplaceFont = null;
        }

        // Check if the chatUI is ours, if not, replace
        if(!replacedChat) {
            GuiIngame ingameGUI = minecraft.ingameGUI;
            if(!(ingameGUI.getChatGUI() instanceof CustomFontChatGui)) {
                System.out.println("Replacing chat UI!");
                recreateChatWindow();
                replacedChat = true;
                addChat("CustomFontRenderer version "+getVersion()+" loaded. Type /cf help for help!");
            }
        }

        if(debug) {
            // Render the text overlay
            int glImageID = customFont.getFont().glyphs['c'].glTextureID;

            GlStateManager.bindTexture(glImageID);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.color(1f, 1f, 1f, 1f);
            Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 256, 256);
            /* float zLevel = PrivateFieldsCustomFont.zLevel.get(Minecraft.getMinecraft().ingameGUI);

            for (int x = 0; x < customFont.font_toDraw.IMAGE_MAX_SIZE; x += customFont.font_toDraw.divider) {
                for (int y = 0; y < customFont.font_toDraw.IMAGE_MAX_SIZE; y += customFont.font_toDraw.divider) {
                    // System.out.println("x : "+x+"\ty : "+y);
                    Tessellator tess = Tessellator.getInstance();
                    VertexBuffer vb = tess.getBuffer();

                    vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

                    // GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.glLineWidth(1.0F);

                    vb.pos(x,                                     y, zLevel).endVertex();
                    vb.pos(customFont.font_toDraw.IMAGE_MAX_SIZE, y, zLevel).endVertex();

                    vb.pos(x,                                     y, zLevel).endVertex();
                    vb.pos(x, customFont.font_toDraw.IMAGE_MAX_SIZE, zLevel).endVertex();

                    tess.draw();
                }
            } */
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }
    }

    /**
     * Get the mod version string
     *
     * @return the mod version as a string
     */
    @Override
    public String getVersion() {
        String version = LiteLoader.getInstance().getModMetaData(this, "version", "");
        String build = LiteLoader.getInstance().getModMetaData(this, "revision", "");

        return version + (!(build.equals("")) ? " (build: " + build + ")" : "");
    }

    protected void setFont(Font font) {
        this.toReplaceFont = font;
        this.replaceFont = true;
    }

    public void saveSettings(File configPath) {
        File cfConfig = new File(configPath, "CustomFont-config.txt");
        System.out.println("Saving config file to "+cfConfig.getAbsolutePath());
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(cfConfig));
            printWriter.println("fontName:" + fontName);
            printWriter.println("fontSize:" + fontSize);
            printWriter.println("amtOfLines:" + amtOfLines);
            printWriter.println("maxWidth:" + maxWidth);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettings(File configPath) {
        File cfConfig = new File(configPath, "CustomFont-config.txt");
        System.out.println("Loading config file from "+cfConfig.getAbsolutePath());
        if (!cfConfig.exists()) saveSettings(configPath);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(cfConfig));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(":");
                if (split[0].equals("fontName")) {
                    String fontName = split[1];
                    for (int i = 2; i < split.length; i++) fontName += " " + split[i];
                    this.fontName = fontName;
                }
                if (split[0].equals("fontSize")) {
                    try {
                        Integer fs = Integer.parseInt(split[1]);
                        this.fontSize = fs;
                    } catch (NumberFormatException ex) {
                        this.fontSize = 16;
                    }
                }
                if (split[0].equals("amtOfLines")) {
                    try {
                        Integer lines = Integer.parseInt(split[1]);
                        this.amtOfLines = lines.byteValue();
                    } catch (NumberFormatException ex) {
                        this.amtOfLines = 20;
                    }
                }
                if (split[0].equals("maxWidth")) {
                    try {
                        Integer maxWidth = Integer.parseInt(split[1]);
                        this.maxWidth = maxWidth.byteValue();
                    } catch (NumberFormatException ex) {
                        this.maxWidth = 20;
                    }
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Do startup stuff here, minecraft is not fully initialised when this function is called so mods *must not*
     * interact with minecraft in any way here
     *
     * @param configPath Configuration path to use
     */
    @Override
    public void init(File configPath) {

        loadSettings(configPath);

        this.configPath = configPath;

        if (customFont == null) {
            customFont = new CustomFont(this.fontName, this.fontSize);
            // customFont = new CustomFont("Verdana bold", 16);
        }
        minecraft = Minecraft.getMinecraft();
        instance = this;
    }

    /**
     * Called when the loader detects that a version change has happened since this mod was last loaded
     *
     * @param version       new version
     * @param configPath    Path for the new version-specific config
     * @param oldConfigPath Path for the old version-specific config
     */
    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    /**
     * Get the display name
     *
     * @return display name
     */
    @Override
    public String getName() {
        return "Custom Font Renderer";
    }

    protected void addChat(String message) {
        if (minecraft != null && minecraft.ingameGUI.getChatGUI() != null) {
            minecraft.ingameGUI.getChatGUI().printChatMessage(ChatUtilities.convertLegacyCodes(new TextComponentString("[CFR] §6" + message)));
        }
    }

    /**
     * Raised when a chat message is being sent, return false to filter this message or true to allow it to be sent
     *
     * @param message
     */
    @Override
    public boolean onSendChatMessage(String message) {
        if(message.startsWith("/cf")) {
            String args[] = message.split(" ");
            if(args.length > 1) {
                String command = args[1];
                if (command.equalsIgnoreCase("toggle")) {
                    CustomFontChatGui.customChat = !CustomFontChatGui.customChat;
                    System.out.println("Custom chat is now " + (CustomFontChatGui.customChat ? "on" : "off"));
                } else if (command.equalsIgnoreCase("font")) {
                    if(args.length > 2) {
                        String fontName = args[2];
                        for (int i = 3; i < args.length; i++) fontName += " " + args[i];
                        customFont = new CustomFont(fontName, fontSize);
                        // chatGui.setCustomFont(customFont);
                        this.fontName = fontName;
                        recreateChatWindow();
                        addChat("Font set to " + customFont.getFontName() + " (file: "+customFont.getFontFile()+")!");
                        saveSettings(configPath);
                    } else {
                        addChat("Current font name : " + fontName);
                    }
                } else if (command.equalsIgnoreCase("size")) {
                    if(args.length > 2) {
                        try {
                            Integer fs = Integer.parseInt(args[2]);
                            customFont = new CustomFont(fontName, fs);
                            // chatGui.setCustomFont(customFont);
                            recreateChatWindow();
                            addChat("Font size set to " + fs + "!");
                            fontSize = fs;
                            saveSettings(configPath);
                        } catch (NumberFormatException ex) {
                            addChat("Not a number!");
                        }
                    } else {
                        addChat("Current font size : "+fontSize);
                    }
                } else if(command.equalsIgnoreCase("lines")) {
                    if(args.length > 2) {
                        try {
                            Integer lines = Integer.parseInt(args[2]);
                            LiteModCustomFont.amtOfLines = lines.byteValue();
                            addChat("Max amount of lines set to " + lines + "!");
                            saveSettings(configPath);
                        } catch (NumberFormatException ex) {
                            addChat("Not a number!");
                        }
                    } else {
                        addChat("Current max lines : "+LiteModCustomFont.amtOfLines);
                    }
                } else if (command.equalsIgnoreCase("width")) {
                    if (args.length > 2) {
                        try {
                            Integer maxWidth = Integer.parseInt(args[2]);
                            LiteModCustomFont.maxWidth = maxWidth.byteValue();
                            recreateChatWindow();
                            addChat("Max width set to " + maxWidth + "!");
                            saveSettings(configPath);
                        } catch (NumberFormatException ex) {
                            addChat("Not a number!");
                        }
                    } else {
                        addChat("Current max width : " + LiteModCustomFont.maxWidth);
                    }
                } else if(command.equalsIgnoreCase("select")) {
                    customFontSelectionDialog.showFontSelectorDialog();
                } else if(command.equalsIgnoreCase("replace")) {
                    GuiIngame ingameGUI = minecraft.ingameGUI;
                    chatGui = new CustomFontChatGui(minecraft, customFont);
                    PrivateFieldsCustomFont.guiNewChat.setFinal(ingameGUI, chatGui);
                } else if(command.equalsIgnoreCase("redraw")) {
                    recreateChatWindow();
                } else if(command.equalsIgnoreCase("info")) {
                    addChat("CustomFontRenderer ("+getVersion()+") debug info");
                    addChat("---------------");
                    addChat("");
                    addChat("Is current chat UI ours? "+(minecraft.ingameGUI.getChatGUI() instanceof CustomFontChatGui ? "yes":"no"));
                    // addChat("Font name : "+customFont.getFont().getFont().getName()+" (file: "+customFont.getFontFile()+")");
                    addChat("Font name : "+customFont.getFontName()+" (file: "+customFont.getFontFile()+")");
                    addChat("Font size : "+fontSize);
                    addChat("Max chat lines : "+LiteModCustomFont.amtOfLines);
                    addChat("Max chat width : "+LiteModCustomFont.maxWidth);
                } else if(command.equalsIgnoreCase("reset")) {
                    LiteModCustomFont.amtOfLines = 20;
                    LiteModCustomFont.maxWidth = 40;
                    this.fontName = "Verdana Bold";
                    this.fontSize = 16;
                    this.customFont = new CustomFont(fontName, fontSize);
                    saveSettings(configPath);
                    recreateChatWindow();
                    addChat("Reset to default values!");
                } else if(command.equalsIgnoreCase("help")) {
                    addChat("CustomFontRenderer ("+getVersion()+") help");
                    addChat("---------------");
                    addChat("");
                    addChat("/cf toggle : Toggle between custom chat and vanilla.");
                    addChat("/cf select : Select the font using a Font Selector window.");
                    addChat("/cf font [fontname]: Use this font to draw chat.");
                    addChat("/cf size [size]: Set this font size for chat.");
                    addChat("/cf lines [lines]: Set the max amount of drawable lines in chat.");
                    addChat("/cf width [width]: Set the max width of pixels of the chat window.");
                    addChat("/cf replace : Replace the chat window with a fresh CustomFontRenderer instance.");
                    addChat("/cf redraw : Try to recreate the chat window.");
                    addChat("/cf info : Show debugging info.");
                    addChat("/cf help : Show CustomFontRenderer help");
                    addChat("/cf reset : Reset settings to their default values.");

                } else if(command.equalsIgnoreCase("test")) {
                    addChat("§0§0§2§e§f§cMotd: §fWelkom op de TweakCraft server!");
                    addChat("§bVoor buildrights: http://www.tweakcraft.net");
                    addChat("§cRegels: ");
                    addChat("§c- §bDupen §f/ §bSpeedhack §f/ §bFlying §f/ §bX-Ray §c == BAN");
                    addChat("§c- Niets jatten, slopen, niet lopen kloten!");
                    addChat("l §nUnderline test");
                    addChat("l §lFat test");
                    addChat("l §oItalic test");
                    addChat("l Random unicode letters :");
                    addChat("ëâùéµù");
                    addChat("¡ ¢ £ ¤ ¥ ¦ § ¨ © ª « ¬ \u00AD ® ¯ ° ± ² ³ ´ µ ¶ · ¸ ¹ º » ¼ ½ ¾ ¿");
                    addChat("À Á Â Ã Ä Å Æ Ç È É Ê Ë Ì Í Î Ï Ð Ñ Ò Ó Ô Õ Ö × Ø Ù Ú Û Ü Ý Þ ß ");
                    addChat("à á â ã ä å æ ç è é ê ë ì í î ï ð ñ ò ó ô õ ö ÷ ø ù ú û ü ý þ ÿ");
                } else if(command.equals("debug")) {
                    this.debug = !this.debug;
                    addChat("Debugging mode " + (this.debug?"enabled":"disabled"));
                }
            } else {
                addChat("§6Usage :");
                addChat("§6 /cf [toggle|font|size|lines|width|replace|redraw|info|help|reset]");
            }
            return false;
        }
        return true;
    }

    private void recreateChatWindow() {
        GuiIngame ingameGUI = minecraft.ingameGUI;
        List<ITextComponent> components;

        GuiNewChat chatGUI = ingameGUI.getChatGUI();
        if(!(chatGUI instanceof CustomFontChatGui)) {
            components = new ArrayList<ITextComponent>();
            for(ChatLine chatLine : PrivateFieldsCustomFont.chatcomponents.get(chatGUI)) {
                components.add(chatLine.getChatComponent());
            }
        } else {
            components = new ArrayList<ITextComponent>();
            components.addAll(((CustomFontChatGui) chatGUI).getReceived_lines());
        }
        List<String> sentMessages = chatGUI.getSentMessages();

        chatGui = new CustomFontChatGui(minecraft, customFont);
        PrivateFieldsCustomFont.guiNewChat.setFinal(ingameGUI, chatGui);
        chatGui.redrawChat(components.toArray(new ITextComponent[components.size()]));
        for(String s : sentMessages)
            chatGui.addToSentMessages(s);

    }

    @Override
    public void onViewportResized(ScaledResolution resolution, int displayWidth, int displayHeight) {
        recreateChatWindow();
    }

    @Override
    public void onFullScreenToggled(boolean fullScreen) {
        recreateChatWindow();
    }
}
