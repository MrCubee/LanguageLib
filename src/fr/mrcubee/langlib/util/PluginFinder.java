package fr.mrcubee.langlib.util;

import java.io.File;
import java.util.logging.Logger;

/** This class retrieves which plugin calls the function.
 * @version 1.0
 * @since 1.0
 * @author MrCubee
 */
public abstract class PluginFinder {

    public static final PluginFinder INSTANCE = getFinder();

    /** Retrieves which plugin calls the current function.
     * @since 1.0
     * @return The current plugin.
     */
    public abstract Object findPlugin();

    /** Retrieve the logger of the desired plugin.
     * @since 1.0
     * @param plugin The targeted plugin.
     * @return The plugin logger.
     * @see Logger
     */
    public abstract Logger findLogger(Object plugin);

    /** Retrieve the folder where the plugin data files are located.
     * @since 1.0
     * @param plugin The targeted plugin.
     * @return Folder where the plugin data files are located.
     * @see File
     */
    public abstract File findDataFolder(Object plugin);

    /** Searches class by its name.
     * @since 1.0
     * @param className The class name to search.
     * @return The class found.
     */
    private static Class<?> getClass(String className) {
        Class<?> clazz = null;

        if (className == null)
            return null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ignored) {}
        return clazz;
    }

    /** Retrieve the right finder depending on whether it's a Spigot or a Bungeecord.
     * @since 1.0
     * @return The correct finder for the current use.
     */
    private static PluginFinder getFinder() {
        if (getClass("org.bukkit.plugin.Plugin") != null)
            return new fr.mrcubee.langlib.util.bukkit.PluginFinder();
        else if (getClass("net.md_5.bungee.api.plugin.Plugin") != null)
            return new fr.mrcubee.langlib.util.bungeecord.PluginFinder();
        return new PluginFinder() {
            @Override
            public Object findPlugin() {
                return null;
            }

            @Override
            public Logger findLogger(Object plugin) {
                return null;
            }

            @Override
            public File findDataFolder(Object plugin) {
                return null;
            }
        };
    }
}
