package fr.epistudio.mmp.plugin;

import fr.epistudio.mmp.MinecraftMultiPluginApi;
import fr.epistudio.mmp.commands.CommandRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract non-sealed class MMPlugin implements MMPluginApi {

    private MinecraftMultiPluginApi api;
    protected final List<CommandRegister> commandRegisters;

    protected MMPlugin() {
        this.commandRegisters = new ArrayList<>();
    }

    public final void attach(MinecraftMultiPluginApi api) {
        if (this.api != null) {
            throw new IllegalStateException("Plugin API already attached");
        }
        this.api = api;
    }

    protected final MinecraftMultiPluginApi getApi() {
        if (api == null) {
            throw new IllegalStateException("Plugin API has not been attached yet");
        }
        return api;
    }

    public void onLoad() {
        // Default implementation (can be overridden by plugins)
    }

    public void onEnable() {
        // Default implementation (can be overridden by plugins)
    }

    public void onDisable() {
        // Default implementation (can be overridden by plugins)
    }

    @Override
    public final List<CommandRegister> getCommandRegisters() {
        return Collections.unmodifiableList(commandRegisters);
    }

    @Override
    public void registerCommand(CommandRegister command) {
        commandRegisters.add(command);
    }


}
