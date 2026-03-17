package fr.epistudio.mmp.plugin;

import fr.epistudio.mmp.commands.CommandRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Enregistre les plug-ins actifs et gère leur cycle de vie. */
public final class PluginManager {
    private final List<MMPlugin> plugins = new ArrayList<>();
    private final List<PluginContainer> containers = new ArrayList<>();

    public void register(MMPlugin plugin) { plugins.add(plugin); }

    public void register(PluginContainer container) { containers.add(container); }

    /** Appelé à la fin de l'application ou lors d'un reload. */
    public void shutdown() {
        Collections.reverse(plugins);          // d'abord les dépendants
        for (MMPlugin p : plugins) {
            try { p.onDisable(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        plugins.clear();
        containers.clear();
    }

    public void load(){
        for (PluginContainer pc : containers) {
            try { PluginLoader.load(pc); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void enable(){
        for (PluginContainer pc : containers) {
            try { PluginLoader.enable(pc, this); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public List<CommandRegister> getAllCommandRegisters() {
        List<CommandRegister> all = new ArrayList<>();
        for (MMPlugin plugin : plugins) {
            all.addAll(plugin.getCommandRegisters());
        }
        return all;
    }
}
