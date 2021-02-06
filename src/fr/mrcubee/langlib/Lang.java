package fr.mrcubee.langlib;

import fr.mrcubee.langlib.util.PluginFinder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

public enum Lang {

    EN_US("EN_us"),
    ZH_zh("ZH_zh"),
    //HI_hi("HI_hi"),
    //ES_es("ES_es"),
    //AR_ar("AR_ar"),
    FR_FR("FR_fr");
    //RU_ru("RU_ru"),
    //PT_pt("PT_pt"),
    //ID_id("ID_id");

    private final static Map<Object, PluginLang> PLUGIN_LANG = new WeakHashMap<Object, PluginLang>();
    private final static Map<Object, Lang> PLAYER_LANG = new WeakHashMap<Object, Lang>();

    private final String langCode;

    Lang(String langCode) {
        this.langCode = langCode;
    }

    @Override
    public String toString() {
        return this.langCode;
    }

    private String rescueMessage(String original, String rescueMessage, boolean color, Object... objects) {
        if (original != null) {
            if (color)
                original = ChatColor.translateAlternateColorCodes('&', original);
            return String.format(original, objects);
        } else if (rescueMessage != null) {
            if (color)
                rescueMessage = ChatColor.translateAlternateColorCodes('&', rescueMessage);
            return String.format(rescueMessage, objects);
        }
        return null;
    }

    private String getMessageFromId(Object plugin, Logger logger, File dataFolder, String messageId, String rescueMessage, boolean color, Object... objects) {
        PluginLang pluginLang;
        String message;

        if (plugin == null || logger == null || dataFolder == null || messageId == null)
            return rescueMessage(null, rescueMessage, color, objects);
        pluginLang = PLUGIN_LANG.get(plugin);
        if (pluginLang == null) {
            pluginLang = new PluginLang(plugin, new File(dataFolder, "lang/"), logger);
            PLUGIN_LANG.put(plugin, pluginLang);
        }
        message = pluginLang.getMessageFromId(this, messageId);
        return rescueMessage(message, rescueMessage, color, objects);
    }

    public String getMessageFromId(String messageId, String rescueMessage, boolean color, Object... objects) {
        Object plugin = PluginFinder.INSTANCE.findPlugin();

        return getMessageFromId(plugin, PluginFinder.INSTANCE.findLogger(plugin),
                PluginFinder.INSTANCE.findDataFolder(plugin), messageId, rescueMessage, color, objects);
    }

    public static boolean setDefaultLang(Lang lang) {
        PluginLang pluginLang;
        Object plugin;
        Logger logger;
        File dataFolder;

        if (lang == null)
            return false;
        plugin = PluginFinder.INSTANCE.findPlugin();
        logger = PluginFinder.INSTANCE.findLogger(plugin);
        if (logger == null)
            return false;
        pluginLang = PLUGIN_LANG.get(plugin);
        if (pluginLang == null) {
            dataFolder = PluginFinder.INSTANCE.findDataFolder(plugin);
            if (dataFolder == null)
                return false;
            logger.info("[LANG] load language system...");
            pluginLang = new PluginLang(plugin, new File(dataFolder, "lang/"), logger);
            PLUGIN_LANG.put(plugin, pluginLang);
            logger.info("[LANG] language system loaded.");
        }
        logger.info("[LANG] set default language to " + lang + ".");
        pluginLang.setDefaultLang(lang);
        return true;
    }

    public static Lang getDefaultLang() {
        Object plugin = PluginFinder.INSTANCE.findPlugin();
        PluginLang pluginLang;

        if (plugin == null)
            return null;
        pluginLang = PLUGIN_LANG.get(plugin);
        if (pluginLang == null)
            return null;
        return pluginLang.getDefaultLang();
    }
    public static void removeInstance() {
        Object plugin = PluginFinder.INSTANCE.findPlugin();

        if (plugin == null)
            return;
        PLUGIN_LANG.remove(plugin);
    }

    public static void setPlayerLang(Object player, Lang lang) {
        Lang defaultLang;

        if (player == null)
            return;
        defaultLang = getDefaultLang();
        if (lang == null || (defaultLang != null && defaultLang.equals(lang))) {
            PLAYER_LANG.remove(player);
            return;
        }
        PLAYER_LANG.put(player, lang);
    }

    public static Lang getPlayerLang(Object player) {
        if (player == null)
            return null;
        return PLAYER_LANG.get(player);
    }

    public static String getMessage(Player player, String messageId, String rescueMessage, boolean color, Object... objects) {
        Lang lang = getPlayerLang(player);
        Object plugin = PluginFinder.INSTANCE.findPlugin();
        PluginLang pluginLang;

        if (lang == null && plugin != null && (pluginLang = PLUGIN_LANG.get(plugin)) != null)
            lang = pluginLang.getDefaultLang();
        if (lang == null)
            return String.format(rescueMessage, objects);
        return lang.getMessageFromId(plugin, PluginFinder.INSTANCE.findLogger(plugin),
                PluginFinder.INSTANCE.findDataFolder(plugin), messageId, rescueMessage, color, objects);
    }

    public static String getMessage(String messageId, String rescueMessage, boolean color, Object... objects) {
        Object plugin = PluginFinder.INSTANCE.findPlugin();
        PluginLang pluginLang;
        Lang lang = null;

        if (plugin != null && (pluginLang = PLUGIN_LANG.get(plugin)) != null)
            lang = pluginLang.getDefaultLang();
        if (lang == null)
            return String.format(rescueMessage, objects);
        return lang.getMessageFromId(plugin, PluginFinder.INSTANCE.findLogger(plugin),
                PluginFinder.INSTANCE.findDataFolder(plugin), messageId, rescueMessage, color, objects);
    }

    public static void clean(int seconds) {
        PLUGIN_LANG.values().forEach(pluginLang -> pluginLang.clean(seconds));
    }

    public static Lang getFromName(String name) {
        if (name == null)
            return null;
        for (Lang lang : Lang.values())
            if (lang.toString().equalsIgnoreCase(name))
                return lang;
        return null;
    }
}
