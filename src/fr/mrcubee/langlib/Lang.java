package fr.mrcubee.langlib;

import fr.mrcubee.langlib.util.PluginFinder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

/** This class is the main one of the library and this class manages languages between plugins and players.
 * @author MrCubee
 * @version 1.0
 * @since 1.0
 */
public class Lang {

    private final static Map<Object, PluginLang> PLUGIN_LANG = new WeakHashMap<Object, PluginLang>();
    private final static Map<Object, String> PLAYER_LANG = new WeakHashMap<Object, String>();

    /** This function returns the message formatted with the colors applied
     * @since 1.0
     * @param original The message format returned by the language dictionary.
     * @param rescueMessage message format used if no dictionary contains the unique message identifier.
     * @param color Applies the color format from '&' character.
     * @param objects The elements required by the message format.
     * @return The formatted message.
     * @see String#format(String, Object...)
     */
    private static String rescueMessage(String original, String rescueMessage, boolean color, Object... objects) {
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

    /** Retrieves the formatted message from the plugin and the specified language.
     * @since 1.0
     * @param plugin The plugin from which to take the configuration.
     * @param lang The targeted language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     * @param messageId The unique identifier of the message to use to find the message format in the dictionary.
     * @param rescueMessage Message format used if no dictionary contains the unique message identifier.
     * @param color Applies the color format from '&' character.
     * @param objects The elements required by the message format.
     * @return The formatted message.
     * @see String#format(String, Object...)
     */
    private static String getMessageFromId(Object plugin, String lang, String messageId, String rescueMessage, boolean color, Object... objects) {
        Logger logger;
        File dataFolder;
        PluginLang pluginLang;
        String message;

        if (plugin == null || messageId == null)
            return rescueMessage(null, rescueMessage, color, objects);
        logger = PluginFinder.INSTANCE.findLogger(plugin);
        dataFolder = PluginFinder.INSTANCE.findDataFolder(plugin);
        if (logger == null || dataFolder == null)
            return rescueMessage(null, rescueMessage, color, objects);
        pluginLang = PLUGIN_LANG.get(plugin);
        if (pluginLang == null) {
            pluginLang = new PluginLang(plugin, new File(dataFolder, "lang/"), logger);
            PLUGIN_LANG.put(plugin, pluginLang);
        }
        message = pluginLang.getMessageFromId(lang, messageId);
        if (message == null && pluginLang.getDefaultLang() != null)
            message = pluginLang.getMessageFromId(pluginLang.getDefaultLang(), messageId);
        return rescueMessage(message, rescueMessage, color, objects);
    }

    /** Set the default language to be used by the current plugin if it is not specified.
     * @since 1.0
     * @param lang The desired language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     * @return Returns true if the default language has been applied to the current plugin, or then returns false.
     */
    public static boolean setDefaultLang(String lang) {
        Object plugin = PluginFinder.INSTANCE.findPlugin();
        Logger logger = PluginFinder.INSTANCE.findLogger(plugin);
        PluginLang pluginLang;
        File dataFolder;

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

    /** Get the default language used by the current plugin if it is not specified.
     * @since 1.0
     * @return The default language of the current plugin (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     */
    public static String getDefaultLang() {
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

    /** Define a specific language for a player
     * @since 1.0
     * @param player The targeted player.
     * @param lang The desired language (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     */
    public static void setPlayerLang(Object player, String lang) {
        if (player == null)
            return;
        if (lang == null) {
            PLAYER_LANG.remove(player);
            return;
        }
        PLAYER_LANG.put(player, lang);
    }

    /** Retrieves the language defined for the targeted player.
     * @since 1.0
     * @param player The targeted player.
     * @return The language defined for the targeted player (see minecraft <a href="https://minecraft.gamepedia.com/Language">Locale Code</a>).
     */
    public static String getPlayerLang(Object player) {
        if (player == null)
            return null;
        return PLAYER_LANG.get(player);
    }

    /** Retrieves the formatted message of the current plugin and the player language.
     * @since 1.0
     * @param player The player language to retrieve.
     * @param messageId The unique identifier of the message to use to find the message format in the dictionary.
     * @param rescueMessage Message format used if no dictionary contains the unique message identifier.
     * @param color Applies the color format from '&' character.
     * @param objects The elements required by the message format.
     * @return The formatted message.
     * @see String#format(String, Object...)
     */
    public static String getMessage(Player player, String messageId, String rescueMessage, boolean color, Object... objects) {
        return getMessageFromId(PluginFinder.INSTANCE.findPlugin(), getPlayerLang(player), messageId, rescueMessage, color, objects);
    }

    /** Retrieves the formatted message of the current plugin and the default language.
     * @since 1.0
     * @param messageId The unique identifier of the message to use to find the message format in the dictionary.
     * @param rescueMessage Message format used if no dictionary contains the unique message identifier.
     * @param color Applies the color format from '&' character.
     * @param objects The elements required by the message format.
     * @return The formatted message.
     * @see String#format(String, Object...) 
     */
    public static String getMessage(String messageId, String rescueMessage, boolean color, Object... objects) {
        return getMessageFromId(PluginFinder.INSTANCE.findPlugin(), null, messageId, rescueMessage, color, objects);
    }

    /** This function unloads languages loaded by plugins that are no longer used.
     * @since 1.0
     * @param seconds Time (in seconds) of inactivity before deleting the language.
     */
    public static void clean(int seconds) {
        PLUGIN_LANG.values().forEach(pluginLang -> pluginLang.clean(seconds));
    }
}
