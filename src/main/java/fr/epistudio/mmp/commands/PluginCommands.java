package fr.epistudio.mmp.commands;

import fr.epistudio.mmp.MinecraftMultiPlugin;
import fr.epistudio.mmp.plugin.PluginContainer;
import fr.epistudio.mmp.plugin.PluginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PluginCommands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        PluginManager manager = MinecraftMultiPlugin.getInstance().getManager();
        List<CommandRegister> commandRegisters = manager.getAllCommandRegisters();
        if (strings.length == 0) {
            commandSender.sendMessage("Available commands:");
            for (CommandRegister register : commandRegisters) {
                commandSender.sendMessage("- /mmp " + register.commandName() + ": " + register.description());
            }
        } else {
            String subCommand = strings[0];
            for (CommandRegister register : commandRegisters) {
                if (register.commandName().equalsIgnoreCase(subCommand)) {
                    String[] args = new String[strings.length - 1];
                    System.arraycopy(strings, 1, args, 0, args.length);
                    return register.commandExecutor().onCommand(commandSender, command, s, args);
                }
            }
            commandSender.sendMessage("Unknown subcommand: " + subCommand + ". Use /mmp to see available commands.");
        }
        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        PluginManager manager = MinecraftMultiPlugin.getInstance().getManager();
        List<CommandRegister> commandRegisters = manager.getAllCommandRegisters();
        if (strings.length == 1) {
            String partial = strings[0].toLowerCase();
            return commandRegisters.stream()
                    .map(CommandRegister::commandName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .toList();
        }
        return null;
    }
}
