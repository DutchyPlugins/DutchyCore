package nl.thedutchmc.dutchycore.commands.tabcompleters;

import org.bukkit.command.CommandSender;

import nl.thedutchmc.dutchycore.module.commands.ModuleTabCompleter;

public class ModulesCommandTabCompleter implements ModuleTabCompleter {

	@Override
	public String[] complete(CommandSender sender, String[] args) {
		return null; //Dont want any tab completion
	}
}
