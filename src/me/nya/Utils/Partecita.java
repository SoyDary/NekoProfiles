package me.nya.Utils;

import net.md_5.bungee.api.chat.TextComponent;

public class Partecita {
    String oldText;
    TextComponent component;
    
    public Partecita(TextComponent component, String oldText) {   	
        this.component = component;
        this.oldText = oldText;
    	
    }
    
    public TextComponent getTextComponent() {
        return this.component;
    }

    public String getPapiVar() {
        return this.oldText;
    }
    
    
}
