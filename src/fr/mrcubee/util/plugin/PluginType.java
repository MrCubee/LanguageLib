package fr.mrcubee.util.plugin;

import fr.mrcubee.util.plugin.util.PluginFinder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public enum PluginType {

    BUKKIT("org.bukkit.plugin.java.JavaPlugin", "fr.mrcubee.util.plugin.util.bukkit.PluginFinder"),
    BUNGEE_CORD("net.md_5.bungee.api.plugin.Plugin", "fr.mrcubee.util.plugin.util.bungeecord.PluginFinder");

    private final String className;
    private final Class<?> clazz;
    private final Class<?> finderClazz;
    private final Method findPluginMainMethod;
    private final Method findPluginLoggerMethod;
    private final Method findPluginDataFolderMethod;

    PluginType(String className, String finderClassName) {
        Class<?> tempClass = null;
        Method tempMethod = null;

        this.className = className;
        try {
            tempClass = Class.forName(className);
        } catch (ClassNotFoundException ignored) {}
        this.clazz = tempClass;
        if (this.clazz == null || finderClassName == null) {
            this.finderClazz = null;
            this.findPluginMainMethod = null;
            this.findPluginLoggerMethod = null;
            this.findPluginDataFolderMethod = null;
            return;
        }
        tempClass = null;
        try {
            tempClass = Class.forName(finderClassName);
        } catch (ClassNotFoundException ignored) {}
        this.finderClazz = tempClass;
        if (finderClazz == null) {
            this.findPluginMainMethod = null;
            this.findPluginLoggerMethod = null;
            this.findPluginDataFolderMethod = null;
            return;
        }
        this.findPluginMainMethod = PluginFinder.getMethodFindPlugin(this);
        this.findPluginLoggerMethod = PluginFinder.getMethodFindLogger(this);
        this.findPluginDataFolderMethod = PluginFinder.getMethodFindDataFolder(this);
    }

    public String getMainClassName() {
        return this.className;
    }

    public Class<?> getMainClass() {
        return this.clazz;
    }

    public Class<?> getFinderClass() {
        return this.finderClazz;
    }

    public File getDataFolderPluginFromSourceCodeHash(int sourceCodeHash) {
        File result = null;

        if (this.findPluginDataFolderMethod == null)
            return null;
        try {
            result = (File) this.findPluginDataFolderMethod.invoke(null, sourceCodeHash);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
        return result;
    }

    public File getDataFolderPluginFromClass(Class<?> clazz) {
        if (clazz == null || this.findPluginDataFolderMethod == null)
            return null;
        return getDataFolderPluginFromSourceCodeHash(clazz.getProtectionDomain().getCodeSource().hashCode());
    }

    public Logger getLoggerPluginFromSourceCodeHash(int sourceCodeHash) {
        Logger result = null;

        if (this.findPluginLoggerMethod == null)
            return null;
        try {
            result = (Logger) this.findPluginLoggerMethod.invoke(null, sourceCodeHash);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
        return result;
    }

    public Logger getLoggerPluginFromClass(Class<?> clazz) {
        if (clazz == null || this.findPluginLoggerMethod == null)
            return null;
        return getLoggerPluginFromSourceCodeHash(clazz.getProtectionDomain().getCodeSource().hashCode());
    }

    public Object getMainPluginFromSourceCodeHash(int sourceCodeHash) {
        Object result = null;

        if (this.findPluginMainMethod == null)
            return null;
        try {
            result = this.findPluginMainMethod.invoke(null, sourceCodeHash);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
        return result;
    }

    public Object getMainPluginFromClass(Class<?> clazz) {
        if (clazz == null || this.findPluginMainMethod == null)
            return null;
        return getMainPluginFromSourceCodeHash(clazz.getProtectionDomain().getCodeSource().hashCode());
    }

    public static PluginType getMainClassType() {
        for (PluginType mainClassType : PluginType.values())
            if (mainClassType.getMainClass() != null)
                return mainClassType;
        return null;
    }

    public static File getPluginDataFolder(Class<?> clazzInPlugin) {
        PluginType mainClassType;

        if (clazzInPlugin == null)
            return null;
        mainClassType = getMainClassType();
        if (mainClassType == null) {
            return null;
        }
        return mainClassType.getDataFolderPluginFromClass(clazzInPlugin);
    }

    public static File getPluginDataFolder(int sourceCodeHash) {
        PluginType mainClassType;

        mainClassType = getMainClassType();
        if (mainClassType == null)
            return null;
        return mainClassType.getDataFolderPluginFromSourceCodeHash(sourceCodeHash);
    }

    public static File getPluginDataFolder() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Class<?> clazz = null;

        if (stackTraceElements.length < 4)
            return null;
        try {
            clazz = Class.forName(stackTraceElements[3].getClassName());
        } catch (ClassNotFoundException ignored) {}
        return getPluginDataFolder(clazz);
    }

    public static Logger getPluginLogger(Class<?> clazzInPlugin) {
        PluginType mainClassType;

        if (clazzInPlugin == null)
            return null;
        mainClassType = getMainClassType();
        if (mainClassType == null) {
            return null;
        }
        return mainClassType.getLoggerPluginFromClass(clazzInPlugin);
    }

    public static Logger getPluginLogger(int sourceCodeHash) {
        PluginType mainClassType;

        mainClassType = getMainClassType();
        if (mainClassType == null)
            return null;
        return mainClassType.getLoggerPluginFromSourceCodeHash(sourceCodeHash);
    }

    public static Logger getPluginLogger() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Class<?> clazz = null;

        if (stackTraceElements.length < 4)
            return null;
        try {
            clazz = Class.forName(stackTraceElements[3].getClassName());
        } catch (ClassNotFoundException ignored) {}
        return getPluginLogger(clazz);
    }

    public static Object getPlugin(Class<?> clazzInPlugin) {
        PluginType mainClassType;
        
        if (clazzInPlugin == null)
            return null;
        mainClassType = getMainClassType();
        if (mainClassType == null) {
            return null;
        }
        return mainClassType.getMainPluginFromClass(clazzInPlugin);
    }

    public static Object getPlugin(int sourceCodeHash) {
        PluginType mainClassType;

        mainClassType = getMainClassType();
        if (mainClassType == null)
            return null;
        return mainClassType.getMainPluginFromSourceCodeHash(sourceCodeHash);
    }

    public static Object getPlugin() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Class<?> clazz = null;

        if (stackTraceElements.length < 4)
            return null;
        try {
            clazz = Class.forName(stackTraceElements[3].getClassName());
        } catch (ClassNotFoundException ignored) {}
        return getPlugin(clazz);
    }
}