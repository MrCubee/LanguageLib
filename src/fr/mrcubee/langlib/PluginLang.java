package fr.mrcubee.langlib;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.logging.Logger;

/** Saves the plugin's configuration and languages.
 * @author MrCubee
 * @version 1.0
 * @since 1.0
 */
public class PluginLang {

    private final Object plugin;
    private final Logger pluginLogger;
    private final File langFolder;
    private final Map<String, LangMessage> langMessage;
    private final Map<LangMessage, Long> langMessageLastRead;
    private String defaultLang;

    /** Creates an instance of a plugin language configuration.
     * @since 1.0
     * @param plugin The targeted plugin
     * @param langFolder The folder containing the external language files.
     * @param pluginLogger The targeted plugin's logger.
     */
    protected PluginLang(Object plugin, File langFolder, Logger pluginLogger) {
        this.plugin = plugin;
        this.pluginLogger = pluginLogger;
        this.langFolder = langFolder;
        this.langMessage = new HashMap<String, LangMessage>();
        this.langMessageLastRead = new WeakHashMap<LangMessage, Long>();
    }

    /** Loads the desired internal lang file of the targeted plugin.
     * @since 1.0
     * @param lang The desired language file name (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     * @return Language messages.
     * @see Properties
     */
    private Properties loadInternal(String lang) {
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        Properties properties;

        if (lang == null)
            return null;
        inputStream = this.plugin.getClass().getResourceAsStream("/lang/" + lang + ".lang");
        if (inputStream == null)
            return null;
        inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        properties = new Properties();
        try {
            properties.load(inputStreamReader);
        } catch (IOException ignored) {}
        if (properties.size() < 1)
            return null;
        return properties;
    }

    /** Loads the desired external lang file of the targeted plugin.
     * @since 1.0
     * @param lang The desired language file name without file extension (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     * @return Language messages.
     * @see Properties
     */
    private Properties loadExternal(String lang) {
        InputStreamReader inputStreamReader = null;
        File langFile;
        Properties properties;

        if (lang == null)
            return null;
        langFile = new File(this.langFolder, lang + ".lang");
        if (!langFile.exists())
            return null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8);
        } catch (FileNotFoundException ignored) {}
        if (inputStreamReader == null)
            return null;
        properties = new Properties();
        try {
            properties.load(inputStreamReader);
        } catch (IOException ignored) {}
        try {
            inputStreamReader.close();
        } catch (IOException ignored) {}
        if (properties.size() < 1)
            return null;
        return properties;
    }

    /** Loads the dictionary of the desired language from internal and external files.
     * <p>
     * The extern file has priority over the internal file.
     * @since 1.0
     * @param lang The desired language file name without file extension (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     * @return The language dictionary.
     * @see LangMessage
     */
    private LangMessage loadLangFile(String lang) {
        LangMessage langMessage;
        Properties properties;

        if (lang == null)
            return null;
        properties = loadInternal(lang);
        langMessage = new LangMessage(lang);
        if (properties != null) {
            properties.forEach((messageId, message) -> {
                langMessage.registerMessage(messageId.toString(), message.toString());
            });
            properties.clear();
        }
        properties = loadExternal(lang);
        if (properties != null) {
            properties.forEach((messageId, message) -> {
                langMessage.registerMessage(messageId.toString(), message.toString());
            });
            properties.clear();
        }
        if (langMessage.size() < 1)
            return null;
        return langMessage;
    }

    /** Retrieve the message format from the unique message identifier in the desired language.
     * <p>
     * If the language dictionary is not loaded, it will try to load it before performing the search.
     * <p>
     * If the language dictionary does not exist or if the message format does not exist, it will search in the default language.
     * <p>
     * If the default language does not contain the message format, the function will return null.
     * @since 1.0
     * @param lang The desired language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     * @param messageId The unique message identifier of the message format you want to retrieve in the dictionary.
     * @return Message format.
     * @see String#format(String, Object...)
     */
    public String getMessageFromId(String lang, String messageId) {
        LangMessage langMessage;
        String message;

        if (lang == null || messageId == null)
            return null;
        langMessage = this.langMessage.get(lang);
        if (langMessage == null) {
            langMessage = loadLangFile(lang);
            if (langMessage == null) {
                if (this.defaultLang != null && !this.defaultLang.equals(lang))
                    return getMessageFromId(this.defaultLang, messageId);
                return null;
            }
            this.langMessage.put(lang, langMessage);
            this.langMessageLastRead.put(langMessage, System.currentTimeMillis());
        }
        message = langMessage.getMessage(messageId);
        if (message == null)
            return null;
        this.langMessageLastRead.put(langMessage, System.currentTimeMillis());
        return message;
    }

    /** Set the default language to be used by the plugin if it is not specified.
     * @since 1.0
     * @param defaultLang The desired language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     */
    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    /** Get the default language used by the plugin if it is not specified.
     * @since 1.0
     * @return The default language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     */
    public String getDefaultLang() {
        return this.defaultLang;
    }

    /** This function unloads languages loaded by the plugin which are no longer used.
     * @since 1.0
     * @param seconds Time (in seconds) of inactivity before deleting the language.
     */
    public void clean(int seconds) {
        long currentTime = System.currentTimeMillis();

        this.langMessage.values().removeIf(value -> {
            Long lastRead = this.langMessageLastRead.get(value);

            if (lastRead == null || (currentTime - lastRead) / 1000 >= seconds) {
                this.pluginLogger.info("[LANG] release " + value.getLang() + " language not use.");
                return true;
            }
            return false;
        });
    }
}
