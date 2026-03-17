package fr.epistudio.mmp.plugin;

import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL jar, ClassLoader parent) {
        super(new URL[]{jar}, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {

            Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                if (resolve) resolveClass(loaded);
                return loaded;
            }

            // Always parent-first for shared/core classes
            if (name.startsWith("java.")
                    || name.startsWith("jdk.")
                    || name.startsWith("javax.")
                    || name.startsWith("org.bukkit.")
                    || name.startsWith("net.minecraft.")
                    || name.startsWith("fr.epistudio.mmp.")
                    || name.startsWith("fr.olympus.")) {
                Class<?> c = super.loadClass(name, resolve);
                return c;
            }

            // Child-first only for plugin-owned classes
            try {
                Class<?> c = findClass(name);
                if (resolve) resolveClass(c);
                return c;
            } catch (ClassNotFoundException ignored) {
                return super.loadClass(name, resolve);
            }
        }
    }
}