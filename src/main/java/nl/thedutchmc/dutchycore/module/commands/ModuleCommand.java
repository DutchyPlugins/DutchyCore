package nl.thedutchmc.dutchycore.module.commands;

public interface ModuleCommand {

	/**
	 * Fire a ModuleCommand
	 * @see org.bukkit.command.CommandExecutor#onCommand(CommandSender, org.bukkit.command.Command, String, String[])
	 * @param sender The {@link org.bukkit.command.CommandSender}
	 * @param args Arguments passed to the Command by the player
	 * @return True if a valid command
	 */
	public boolean fire(org.bukkit.command.CommandSender sender, String[] args);
}
