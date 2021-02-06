package fr.mrcubee.langlib.util;

import java.io.File;
import java.util.logging.Logger;

public abstract class PluginFinder {

    public static final PluginFinder INSTANCE = getFinder();

    public abstract Object findPlugin();
    public abstract  Logger findLogger(Object plugin);
    public abstract File findDataFolder(Object plugin);

    private static Class<?> getClass(String className) {
        Class<?> clazz = null;

        if (className == null)
            return null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ignored) {}
        return clazz;
    }

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
