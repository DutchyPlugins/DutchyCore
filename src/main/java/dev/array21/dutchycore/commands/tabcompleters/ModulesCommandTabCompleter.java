package dev.array21.dutchycore.commands.tabcompleters;

import org.bukkit.command.CommandSender;

import dev.array21.dutchycore.module.commands.ModuleTabCompleter;

public class ModulesCommandTabCompleter implements ModuleTabCompleter {

	@Override
	public String[] complete(CommandSender sender, String[] args) {
		return null; //Dont want any tab completion
	}
}
