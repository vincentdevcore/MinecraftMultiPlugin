package fr.epistudio.mmp.commands;

import org.bukkit.command.CommandExecutor;

public record CommandRegister(String commandName, CommandExecutor commandExecutor, String description) {
}
