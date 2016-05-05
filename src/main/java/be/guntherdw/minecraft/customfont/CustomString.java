package be.guntherdw.minecraft.customfont;

import net.minecraft.util.IChatComponent;

/**
 * @author GuntherDW
 */
public class CustomString {
    private int color;
    private int x;
    private int y;

    private boolean shadow;

    private IChatComponent chatComponent;
    private String chatText;

    public CustomString(IChatComponent chatComponent, int x, int y, boolean shadow, int color) {
        this.chatComponent = chatComponent;
        this.y = y;
        this.x = x;
        this.shadow = shadow;
        this.color = color;
    }

    public CustomString(String chatText, int x, int y, int color, boolean shadow) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.shadow = shadow;
        this.chatText = chatText;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public int getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public IChatComponent getChatComponent() {
        return chatComponent;
    }

    public String getChatString() {
        if(chatComponent != null)
            return chatComponent.getUnformattedTextForChat();
        else
            return chatText;
    }
}
