package fr.mrcubee.util.plugin.util;

import fr.mrcubee.util.plugin.PluginType;

import java.lang.reflect.Method;

public class PluginFinder {

    public static Method getMethodFindPlugin(PluginType pluginType) {
        Method method = null;

        if (pluginType == null || pluginType.getFinderClass() == null)
            return null;
        try {
            method = pluginType.getFinderClass().getDeclaredMethod("findPlugin", int.class);
        } catch (NoSuchMethodException ignored) {}
        return method;
    }

    public static Method getMethodFindLogger(PluginType pluginType) {
        Method method = null;

        if (pluginType == null || pluginType.getFinderClass() == null)
            return null;
        try {
            method = pluginType.getFinderClass().getDeclaredMethod("findLogger", int.class);
        } catch (NoSuchMethodException ignored) {}
        return method;
    }

    public static Method getMethodFindDataFolder(PluginType pluginType) {
        Method method = null;

        if (pluginType == null || pluginType.getFinderClass() == null)
            return null;
        try {
            method = pluginType.getFinderClass().getDeclaredMethod("findDataFolder", int.class);
        } catch (NoSuchMethodException ignored) {}
        return method;
    }
}
