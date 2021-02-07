package fr.mrcubee.langlib.util.bungeecord;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

/** This class retrieves which Bungeecord plugin calls the function.
 * @author MrCubee
 * @version 1.0
 * @since 1.0
 */
public class PluginFinder extends fr.mrcubee.langlib.util.PluginFinder {

    @Override
    public Object findPlugin() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Class<?> clazz = null;
        int hashSourceCode;

        if (stackTraceElements.length < 5)
            return null;
        try {
            clazz = Class.forName(stackTraceElements[4].getClassName());
        } catch (ClassNotFoundException ignored) {}
        if (clazz == null)
            return null;
        hashSourceCode = clazz.getProtectionDomain().getCodeSource().hashCode();
        for (Plugin plugin : BungeeCord.getInstance().getPluginManager().getPlugins())
            if (plugin.getClass().getProtectionDomain().getCodeSource().hashCode() == hashSourceCode)
                return plugin;
        return null;
    }

    @Override
    public Logger findLogger(Object plugin) {
        if (!(plugin instanceof Plugin))
            return null;
        return ((Plugin) plugin).getLogger();
    }

    @Override
    public File findDataFolder(Object plugin) {
        if (!(plugin instanceof Plugin))
            return null;
        return ((Plugin) plugin).getDataFolder();
    }
}
