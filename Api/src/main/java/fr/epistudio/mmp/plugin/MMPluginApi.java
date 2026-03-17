package fr.epistudio.mmp.plugin;

import fr.epistudio.mmp.commands.CommandRegister;
import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.prometheus.Prometheus;
import fr.olymus.heracles.Heracles;

import java.util.List;

public sealed interface MMPluginApi permits MMPlugin  {


    void onLoad();

    void onEnable();

    void onDisable();

    List<CommandRegister> getCommandRegisters();

    void registerCommand(CommandRegister command);


}
