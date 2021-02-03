package fr.mrcubee.langlib;

import java.util.HashMap;
import java.util.Map;

public class LangMessage {

    private final Lang lang;
    private final Map<String, String> messages;

    protected LangMessage(Lang lang) {
        this.lang = lang;
        this.messages = new HashMap<String, String>();
    }

    protected void registerMessage(String messageId, String message) {
        if (messageId == null || message == null)
            return;
        this.messages.put(messageId, message);
    }

    protected void unRegisterMessage(String messageId) {
        if (messageId == null)
            return;
        this.messages.remove(messageId);
    }

    public String getMessage(String messageId) {
        if (messageId == null)
            return null;
        return this.messages.get(messageId);
    }

    public Lang getLang() {
        return this.lang;
    }

    public int size() {
        return this.messages.size();
    }
}
