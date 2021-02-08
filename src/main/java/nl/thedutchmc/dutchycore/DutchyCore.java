package nl.thedutchmc.dutchycore;

import org.bukkit.plugin.java.JavaPlugin;

import nl.thedutchmc.dutchycore.commands.CommandRegister;
import nl.thedutchmc.dutchycore.module.ModuleLoader;

public class DutchyCore extends JavaPlugin {

	private static DutchyCore INSTANCE;
	
	private static ModuleLoader moduleLoader;
	private static CommandRegister commandRegister;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		logInfo(String.format("Welcome to DutchyCore version %s!", this.getDescription().getVersion()));

		commandRegister = new CommandRegister(this);
		commandRegister.registerDefault();
		
		moduleLoader = new ModuleLoader();
		moduleLoader.loadAllModules(this);
		
		logInfo("Startup complete!");
	}
	
	public static void logInfo(String log) {
		INSTANCE.getLogger().info(log.toString());
	}
	
	public static void logWarn(String log) {
		INSTANCE.getLogger().warning(log.toString());
	}
	
	public static ModuleLoader getModuleLoader() {
		return DutchyCore.moduleLoader;
	}
	
	public static CommandRegister getCommandRegister() {
		return DutchyCore.commandRegister;
	}
}
