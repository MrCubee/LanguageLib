package fr.mrcubee.util.plugin.util.bungeecord;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public class PluginFinder {

    public static Object findPlugin(int hashSourceCode) {
        for (Plugin plugin : BungeeCord.getInstance().getPluginManager().getPlugins())
            if (plugin.getClass().getProtectionDomain().getCodeSource().hashCode() == hashSourceCode)
                return plugin;
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
