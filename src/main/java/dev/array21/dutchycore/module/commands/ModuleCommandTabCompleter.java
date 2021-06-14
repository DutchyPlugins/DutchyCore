package dev.array21.dutchycore.module.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ModuleCommandTabCompleter implements TabCompleter {

	private ModuleTabCompleter moduleTabCompleter;
	
	public ModuleCommandTabCompleter(ModuleTabCompleter moduleTabCompleter) {
		this.moduleTabCompleter = moduleTabCompleter;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		String[] completed = moduleTabCompleter.complete(sender, args);
		return (completed != null) ? Arrays.asList(completed) : new ArrayList<>();
	}
}
