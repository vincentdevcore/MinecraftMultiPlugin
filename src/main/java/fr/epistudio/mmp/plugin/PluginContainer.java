package fr.epistudio.mmp.plugin;

public final class PluginContainer {

    private final PluginDescription desc;
    private final PluginClassLoader loader;
    private MMPlugin instance;

    public PluginContainer(PluginDescription desc, PluginClassLoader loader) {
        this.desc = desc;
        this.loader = loader;
    }


    public PluginDescription getDescription() {
        return desc;
    }

    public PluginClassLoader getClassLoader() {
        return loader;
    }

    public MMPlugin getInstance() {
        return instance;
    }

    public void setInstance(MMPlugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("Plugin instance already set for " + desc.name);
        }
        this.instance = plugin;
    }
}
