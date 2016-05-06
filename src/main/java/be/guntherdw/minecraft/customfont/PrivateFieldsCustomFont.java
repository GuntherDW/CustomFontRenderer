package be.guntherdw.minecraft.customfont;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.util.PrivateFields;
import net.minecraft.client.gui.*;

import java.util.List;

/**
 * Created by guntherdw on 12/01/15.
 */
public class PrivateFieldsCustomFont<P, T> extends PrivateFields<P, T> {
    /**
     * Creates a new private field entry
     *
     * @param owner
     * @param obf
     */
    protected PrivateFieldsCustomFont(Class<P> owner, Obf obf) {
        super(owner, obf);
    }

    public static final PrivateFieldsCustomFont<GuiIngame, GuiNewChat>             guiNewChat = new PrivateFieldsCustomFont<GuiIngame, GuiNewChat>(GuiIngame.class, CustomFontObf.persistantChatGui);
    public static final PrivateFieldsCustomFont<FontRenderer, int[]>                colorCode = new PrivateFieldsCustomFont<FontRenderer, int[]>(FontRenderer.class, CustomFontObf.colorCode);
    public static final PrivateFieldsCustomFont<GuiNewChat, List<ChatLine>>    chatcomponents = new PrivateFieldsCustomFont<GuiNewChat, List<ChatLine>>(GuiNewChat.class, CustomFontObf.chatcomponents);
    public static final PrivateFieldsCustomFont<GuiChat, GuiTextField>             inputField = new PrivateFieldsCustomFont<GuiChat, GuiTextField>(GuiChat.class, CustomFontObf.inputField);
    public static final PrivateFieldsCustomFont<Gui, Integer>                          zLevel = new PrivateFieldsCustomFont<Gui, Integer>(Gui.class, CustomFontObf.zLevel);

    // public static final PrivateFieldsCustomFont<EnumChatFormatting, Character>       formattingCode = new PrivateFieldsCustomFont<EnumChatFormatting, Character>(EnumChatFormatting.class, WatsonObf.EnumChatFormatting_formattingCode);
}
