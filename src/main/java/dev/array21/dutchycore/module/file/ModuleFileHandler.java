package dev.array21.dutchycore.module.file;

import java.io.File;

import dev.array21.dutchycore.DutchyCore;
import dev.array21.dutchycore.module.Module;
import dev.array21.dutchycore.module.PluginModule;

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
		
		this.moduleConfiguration.read();
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
		
		this.moduleStorage.read();
		return this.moduleStorage;
	}
	
	public File getModuleConfigurationFile() {
		return new File(plugin.getDataFolder() + File.separator + "moduleconfig", module.getName() + ".yml");
	}
	
	public File getModuleStorageFile() {
		return new File(plugin.getDataFolder() + File.separator + "modulestorage", module.getName() + ".yml");
	}
}
