package fr.mrcubee.langlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.logging.Logger;

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

    public LangMessage loadLangFile(Lang lang) {
        LangMessage langMessage;
        Properties properties;
        File langFile;
        FileInputStream fileInputStream = null;

        if (lang == null)
            return null;
        pluginLogger.info("[LANG] Loading " + lang + " file...");
        langFile = new File(this.langFolder, lang + ".lang");
        if (!langFile.exists()) {
            pluginLogger.severe("[LANG] " + langFile.getAbsolutePath() + " doesn't exit.");
            lang.saveDefault();
            if (!langFile.exists()) {
                pluginLogger.severe("[LANG] " + langFile.getAbsolutePath() + " doesn't exit.");
                return null;
            }
        }
        try {
            fileInputStream = new FileInputStream(langFile);
        } catch (FileNotFoundException ignored) {}
        if (fileInputStream == null) {
            pluginLogger.severe("[LANG] " + langFile.getPath() + " can't open.");
            return null;
        }
        properties = new Properties();
        try {
            properties.load(fileInputStream);
        } catch (IOException ignored) {}
        if (properties.size() < 1) {
            pluginLogger.warning("[LANG] " + langFile.getPath() + " file is is empty.");
            return null;
        }
        langMessage = new LangMessage(lang);
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
            if (langMessage == null)
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
