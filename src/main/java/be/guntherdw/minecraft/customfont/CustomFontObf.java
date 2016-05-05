package be.guntherdw.minecraft.customfont;

import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * @author GuntherDW
 */
public class CustomFontObf extends Obf {

    public static final CustomFontObf   persistantChatGui   = new CustomFontObf("field_73840_e",  "l",    "persistantChatGUI");
    public static final CustomFontObf           colorCode   = new CustomFontObf("field_78285_g",  "f",    "colorCode");
    public static final CustomFontObf       chatcomponents  = new CustomFontObf("field_146253_i", "i",    "field_146253_i");
    public static final CustomFontObf           inputField  = new CustomFontObf("field_146415_a", "a",    "inputField");

    /**
     * @param seargeName
     * @param obfName
     * @param mcpName
     */
    protected CustomFontObf(String seargeName, String obfName, String mcpName) {
        super(seargeName, obfName, mcpName);
    }

    /**
     * @param seargeName
     * @param obfName
     */
    protected CustomFontObf(String seargeName, String obfName) {
        super(seargeName, obfName);
    }
}
