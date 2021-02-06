package fr.mrcubee.langlib;

import fr.mrcubee.langlib.util.PluginFinder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.logging.Logger;

public class PluginLang {

    private final Object plugin;
    private final Logger pluginLogger;
    private final File langFolder;
    private final Map<Lang, LangMessage> langMessage;
    private final Map<LangMessage, Long> langMessageLastRead;
    private Lang defaultLang;

    protected PluginLang(Object plugin, File langFolder, Logger pluginLogger) {
        this.plugin = plugin;
        this.pluginLogger = pluginLogger;
        this.langFolder = langFolder;
        this.langMessage = new HashMap<Lang, LangMessage>();
        this.langMessageLastRead = new WeakHashMap<LangMessage, Long>();
    }

    public Properties loadInternal(Lang lang) {
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        Properties properties;

        if (lang == null)
            return null;
        this.pluginLogger.info("[LANG] Importing internal " + lang + " language configuration....");
        inputStream = this.plugin.getClass().getResourceAsStream("/lang/" + lang + ".lang");
        if (inputStream == null) {
            this.pluginLogger.warning("[LANG] No internal " + lang+ " language configuration.");
            return null;
        }
        inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        properties = new Properties();
        try {
            properties.load(inputStreamReader);
        } catch (IOException exception) {
            this.pluginLogger.severe("[LANG] Error during import internal " + lang + " language configuration. Exception: " + exception.getMessage());
            return null;
        }
        return properties;
    }

    public Properties loadExternal(Lang lang) {
        InputStreamReader inputStreamReader = null;
        File langFile;
        Properties properties;

        if (lang == null)
            return null;
        langFile = new File(this.langFolder, lang + ".lang");
        if (!langFile.exists()) {
            pluginLogger.severe("[LANG] External " + lang+ " language configuration doesn't exit.");
            return null;
        }
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8);
        } catch (FileNotFoundException ignored) {}
        if (inputStreamReader == null) {
            pluginLogger.severe("[LANG] " + langFile.getPath() + " can't open.");
            return null;
        }
        properties = new Properties();
        try {
            properties.load(inputStreamReader);
        } catch (IOException ignored) {}
        try {
            inputStreamReader.close();
        } catch (IOException ignored) {}
        if (properties.size() < 1) {
            pluginLogger.warning("[LANG] External " + langFile.getPath() + " language configuration file is empty.");
            return null;
        }
        return properties;
    }

    public LangMessage loadLangFile(Lang lang) {
        LangMessage langMessage;
        Properties properties;

        if (lang == null)
            return null;
        this.pluginLogger.info("[LANG] Loading " + lang + " language...");
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

    public String getMessageFromId(Lang lang, String messageId) {
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
            pluginLogger.info("[LANG] " + lang + " file loaded.");
        }
        message = langMessage.getMessage(messageId);
        if (message == null) {
            pluginLogger.warning("[LANG] " + messageId + " message not found.");
            return null;
        }
        this.langMessageLastRead.put(langMessage, System.currentTimeMillis());
        return message;
    }

    public void setDefaultLang(Lang defaultLang) {
        this.defaultLang = defaultLang;
    }

    public Lang getDefaultLang() {
        return this.defaultLang;
    }

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
