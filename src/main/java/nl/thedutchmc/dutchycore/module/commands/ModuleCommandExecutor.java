package nl.thedutchmc.dutchycore.module.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ModuleCommandExecutor implements CommandExecutor {

	private ModuleCommand moduleCommand;
	
	public ModuleCommandExecutor(ModuleCommand moduleCommand) {		
		this.moduleCommand = moduleCommand;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return this.moduleCommand.fire(sender, args);
	}
}
