package fr.mrcubee.langlib;

import java.util.HashMap;
import java.util.Map;

/** Language dictionary.
 * @author MrCubee
 * @version 1.0
 * @since 1.0
 */
public class LangMessage {

    private final String lang;
    private final Map<String, String> messages;

    /** Create an instance of a language dictionary.
     * @since 1.0
     * @param lang The targeted language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     */
    protected LangMessage(String lang) {
        this.lang = lang;
        this.messages = new HashMap<String, String>();
    }

    /** Register a new message format in the dictionary.
     * @since 1.0
     * @param messageId The unique message identifier.
     * @param message The message format.
     * @see String#format(String, Object...) 
     */
    protected void registerMessage(String messageId, String message) {
        if (messageId == null || message == null)
            return;
        this.messages.put(messageId, message);
    }

    /** Unregister a message format from the dictionary.
     * @since 1.0
     * @param messageId The unique message identifier.
     */
    protected void unRegisterMessage(String messageId) {
        if (messageId == null)
            return;
        this.messages.remove(messageId);
    }

    /** Retrieve the message format from the unique message identifier.
     * @since 1.0
     * @param messageId Unique message identifier.
     * @return Message format.
     * @see String#format(String, Object...) 
     */
    public String getMessage(String messageId) {
        if (messageId == null)
            return null;
        return this.messages.get(messageId);
    }

    /** Get dictionary language.
     * @since 1.0
     * @return Dictionary language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     */
    public String getLang() {
        return this.lang;
    }

    /** Retrieve the number of registered message format.
     * @since 1.0
     * @return Number of message format registered
     */
    public int size() {
        return this.messages.size();
    }
}
