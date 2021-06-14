package dev.array21.dutchycore.module.commands;

public interface ModuleTabCompleter {

	public String[] complete(org.bukkit.command.CommandSender sender, String[] args);
}
