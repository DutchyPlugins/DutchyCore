package nl.thedutchmc.dutchycore.module.commands;

public interface ModuleTabCompleter {

	public String[] complete(org.bukkit.command.CommandSender sender, String[] args);
}
