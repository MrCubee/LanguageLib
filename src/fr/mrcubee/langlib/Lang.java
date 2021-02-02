package fr.mrcubee.langlib;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import static fr.mrcubee.util.plugin.PluginType.getPlugin;
import static fr.mrcubee.util.plugin.PluginType.getPluginLogger;
import static fr.mrcubee.util.plugin.PluginType.getPluginDataFolder;

public enum Lang {

    DEFAULT(null),
    EN_US("EN_us"),
    FR_FR("FR_fr");

    private final static Map<Object, PluginLang> PLUGIN_LANG = new WeakHashMap<Object, PluginLang>();
    private final static Map<Player, Lang> PLAYER_LANG = new WeakHashMap<Player, Lang>();

    private final String langCode;

    Lang(String langCode) {
        this.langCode = langCode;
    }

    @Override
    public String toString() {
        return this.langCode;
    }

    private String rescueMessage(String original, String rescueMessage, Object... objects) {
        if (original != null)
            return String.format(original, objects);
        else if (rescueMessage != null)
            return String.format(rescueMessage, objects);
        return null;
    }

    public String getMessageFromId(String messageId, String rescueMessage, Object... objects) {
        Object plugin;
        PluginLang pluginLang;
        Logger logger;
        File dataFolder;
        String message;

        if (messageId == null)
            return rescueMessage(null, rescueMessage, objects);;
        plugin = getPlugin();
        if (plugin == null)
            return rescueMessage(null, rescueMessage, objects);
        pluginLang = PLUGIN_LANG.get(plugin);
        if (pluginLang == null) {
            logger = getPluginLogger();
            dataFolder = getPluginDataFolder();
            if (logger == null || dataFolder == null)
                return rescueMessage(null, rescueMessage, objects);
            pluginLang = new PluginLang(new File(dataFolder, "lang/"), logger);
            PLUGIN_LANG.put(plugin, pluginLang);
        }
        message = pluginLang.getMessageFromId(this, messageId);
        return rescueMessage(message, rescueMessage, objects);
    }

    public static boolean setDefaultLang(Lang lang) {
        PluginLang pluginLang;
        Object plugin;
        Logger logger;
        File dataFolder;

        if (lang == null)
            return false;
        plugin = getPlugin();
        logger = getPluginLogger();
        if (plugin == null || logger == null)
            return false;
        pluginLang = PLUGIN_LANG.get(plugin);
        if (pluginLang == null) {
            dataFolder = getPluginDataFolder();
            if (dataFolder == null)
                return false;
            logger.info("[LANG] load language system...");
            pluginLang = new PluginLang(new File(dataFolder, "lang/"), logger);
            PLUGIN_LANG.put(plugin, pluginLang);
            logger.info("[LANG] language system loaded.");
        }
        logger.info("[LANG] set default language to " + lang + ".");
        pluginLang.setDefaultLang(lang);
        return true;
    }

    public static Lang getDefaultLang() {
        Object plugin = getPlugin();
        PluginLang pluginLang;

        if (plugin == null)
            return null;
        pluginLang = PLUGIN_LANG.get(plugin);
        if (pluginLang == null)
            return null;
        return pluginLang.getDefaultLang();
    }
    public static void removeInstance() {
        Object plugin = getPlugin();

        if (plugin == null)
            return;
        PLUGIN_LANG.remove(plugin);
    }

    public static void setPlayerLang(Player player, Lang lang) {
        Lang defaultLang;

        if (player == null)
            return;
        defaultLang = getDefaultLang();
        if (lang == null || (defaultLang != null && defaultLang.equals(lang))) {
            PLAYER_LANG.remove(player);
            return;
        } else if (lang.equals(Lang.DEFAULT))
            return;
        PLAYER_LANG.put(player, lang);
    }

    public static Lang getPlayerLang(Player player) {
        Lang lang;

        if (player == null)
            return null;
        lang = PLAYER_LANG.get(player);
        if (lang != null) {
            if (lang.equals(Lang.DEFAULT)) {
                PLAYER_LANG.remove(player);
                return getDefaultLang();
            }
            return lang;
        }
        return getDefaultLang();
    }

    public static String getMessage(Player player, String messageId, String rescueMessage, Object... objects) {
        Lang lang = getPlayerLang(player);

        if (lang == null)
            return rescueMessage;
        return lang.getMessageFromId(messageId, rescueMessage, objects);
    }

    public static String getMessage(String messageId, String rescueMessage, Object... objects) {
        Lang lang = getDefaultLang();

        if (lang == null)
            return rescueMessage;
        return lang.getMessageFromId(messageId, rescueMessage, objects);
    }

    public static void clean(int seconds) {
        PLUGIN_LANG.values().forEach(pluginLang -> pluginLang.clean(seconds));
    }
}
