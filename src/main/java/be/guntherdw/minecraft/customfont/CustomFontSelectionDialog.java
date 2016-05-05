package be.guntherdw.minecraft.customfont;

import say.swing.JFontChooser;

import java.awt.*;


/**
 * @author GuntherDW
 */
public class CustomFontSelectionDialog extends Component {

    protected static boolean isOpen = false;


    public void createFontSelectorDialog() {
        isOpen = true;
        final LiteModCustomFont customFontInstance = LiteModCustomFont.instance;

        JFontChooser fontChooser = new JFontChooser();
        if (customFontInstance.customFont.getFont().getFont() != null) {
            fontChooser.setSelectedFont(customFontInstance.customFont.getFont().getFont());
        }

        int res = fontChooser.showDialog(this);
        if (res == JFontChooser.OK_OPTION) {
            customFontInstance.setFont(fontChooser.getSelectedFont());
        }
        isOpen = false;
    }

    public void showFontSelectorDialog() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (!isOpen) {
                    createFontSelectorDialog();
                } else {
                    LiteModCustomFont.instance.addChat("Font selection window already opened!");
                }
            }
        };

        new Thread(r).start();
    }

}
