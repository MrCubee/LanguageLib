package fr.mrcubee.util.plugin.util.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public class PluginFinder {

    public static Object findPlugin(int hashSourceCode) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getProtectionDomain().getCodeSource().hashCode() == hashSourceCode)
                return plugin;
        }
        return null;
    }

    public static Logger findLogger(int hashSourceCode) {
        Object plugin = findPlugin(hashSourceCode);

        if (plugin == null)
            return null;
        return ((Plugin) plugin).getLogger();
    }

    public static File findDataFolder(int hashSourceCode) {
        Object plugin = findPlugin(hashSourceCode);

        if (plugin == null)
            return null;
        return ((Plugin) plugin).getDataFolder();
    }
}
