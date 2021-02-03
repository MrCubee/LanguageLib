package fr.mrcubee.langlib;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import static fr.mrcubee.util.plugin.PluginType.getPluginLogger;

public class PluginLang {

    private final Logger pluginLogger;
    private final File langFolder;
    private final Map<Lang, LangMessage> langMessage;
    private final Map<LangMessage, Long> langMessageLastRead;
    private Lang defaultLang;

    protected PluginLang(File langFolder, Logger pluginLogger) {
        this.pluginLogger = pluginLogger;
        this.langFolder = langFolder;
        this.langMessage = new HashMap<Lang, LangMessage>();
        this.langMessageLastRead = new WeakHashMap<LangMessage, Long>();
    }

    public Properties importInternal(Lang lang) {
        Logger logger;
        InputStream inputStream;
        Properties properties;

        if (lang == null)
            return null;
        logger = getPluginLogger();
        if (logger == null)
            return null;
        logger.info("[LANG] Importing internal " + lang + " language configuration....");
        inputStream = getClass().getResourceAsStream("/lang/" + lang + ".lang");
        if (inputStream == null) {
            logger.warning("[LANG] No internal " + lang+ " language configuration.");
            return null;
        }
        properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException exception) {
            logger.severe("[LANG] Error during import internal " + lang + " language configuration. Exception: " + exception.getMessage());
            return null;
        }
        return properties;
    }

    public LangMessage loadLangFile(Lang lang) {
        LangMessage langMessage;
        Properties properties;
        File langFile;
        FileInputStream fileInputStream = null;

        if (lang == null)
            return null;
        pluginLogger.info("[LANG] Loading " + lang + " language...");
        properties = importInternal(lang);
        langMessage = new LangMessage(lang);
        if (properties != null) {
            properties.forEach((messageId, message) -> {
                langMessage.registerMessage(messageId.toString(), message.toString());
            });
            properties.clear();
        }
        langFile = new File(this.langFolder, lang + ".lang");
        if (!langFile.exists()) {
            pluginLogger.severe("[LANG] External " + lang+ " language configuration doesn't exit.");
            return langMessage;
        }
        pluginLogger.info("[LANG] Importing external " + lang + " language configuration....");
        try {
            fileInputStream = new FileInputStream(langFile);
        } catch (FileNotFoundException ignored) {}
        if (fileInputStream == null) {
            pluginLogger.severe("[LANG] " + langFile.getPath() + " can't open.");
            return langMessage;
        }
        if (properties == null)
            properties = new Properties();
        try {
            properties.load(fileInputStream);
        } catch (IOException ignored) {}
        if (properties.size() < 1)
            pluginLogger.warning("[LANG] External " + langFile.getPath() + " language configuration file is empty.");
        properties.forEach((messageId, message) -> {
            langMessage.registerMessage(messageId.toString(), message.toString());
        });
        try {
            fileInputStream.close();
        } catch (IOException ignored) {}
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
            if (langMessage == null || langMessage.size() < 1)
                return null;
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
