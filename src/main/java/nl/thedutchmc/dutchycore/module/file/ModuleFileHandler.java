package nl.thedutchmc.dutchycore.module.file;

import java.io.File;

import nl.thedutchmc.dutchycore.DutchyCore;
import nl.thedutchmc.dutchycore.module.Module;
import nl.thedutchmc.dutchycore.module.PluginModule;

public class ModuleFileHandler {

	private DutchyCore plugin;
	private Module module;
	private ModuleConfiguration moduleConfiguration;
	private ModuleStorage moduleStorage;
	
	public ModuleFileHandler(DutchyCore plugin, PluginModule pluginModule) {
		this.plugin = plugin;
		this.module = DutchyCore.getModuleLoader().getModule(pluginModule);
		
		//Check if the config directory exists, if not make it
		File moduleConfigDirectory = new File(plugin.getDataFolder() + File.separator + "moduleconfig");
		if(!moduleConfigDirectory.exists()) {
			moduleConfigDirectory.mkdirs();
		}
		
		//Check if the storage directory exists, if not make it
		File moduleStorageDirectory = new File(plugin.getDataFolder() + File.separator + "modulestorage");
		if(!moduleStorageDirectory.exists()) {
			moduleStorageDirectory.mkdirs();
		}
	}
	
	/**
	 * Get the configuration for this Module
	 * @return Returns the ModuleConfiguration
	 */
	public ModuleConfiguration getModuleConfiguration() {
		if(this.moduleConfiguration == null) {
			this.moduleConfiguration = new ModuleConfiguration(module, this.plugin);
		}
		
		return this.moduleConfiguration;
	}
	
	/**
	 * Get the storage for this Module
	 * @return Returns the ModuleStorage
	 */
	public ModuleStorage getModuleStorage() {
		if(this.moduleStorage == null) {
			this.moduleStorage = new ModuleStorage(module, plugin);
		}
		
		return this.moduleStorage;
	}
}
