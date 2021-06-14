package dev.array21.dutchycore.module.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import dev.array21.dutchycore.DutchyCore;
import dev.array21.dutchycore.annotations.Nullable;
import dev.array21.dutchycore.config.Utf8YamlConfiguration;
import dev.array21.dutchycore.module.Module;

public class ModuleConfiguration {
	private Module module;
	private DutchyCore plugin;
	private HashMap<String, Object> config = new HashMap<>();
	private FileConfiguration fileConfig;
	private File configFile;
	
	public ModuleConfiguration(Module module, DutchyCore plugin) {
		this.module = module;
		this.plugin = plugin;
	}
	
	/**
	 * Read the configuration into memory
	 */
	public void read() {
		configFile = new File(plugin.getDataFolder() + File.separator + "moduleconfig", module.getName() + ".yml");
		
		//Check if the config file exists. If not, make it
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Create a UTF8 yaml configuration
		fileConfig = new Utf8YamlConfiguration();
		
		try {
			//Load the config
			fileConfig.load(configFile);
			
			//Read the config into a hashmap
			for(String key : fileConfig.getKeys(false)) {
				this.config.put(key, fileConfig.get(key));
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the configuration file
	 */
	public void save() {
		//Check if fileConfig is not null
		Validate.notNull(this.fileConfig);
		
		//Loop over the hashmap and 'sync' it with fileConfig
		for(Map.Entry<String, Object> entry : this.config.entrySet()) {
			this.fileConfig.set(entry.getKey(), entry.getValue());
		}
		
		//Save the fileConfig back into the config file
		try {
			fileConfig.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a config value
	 * @param key The key to get the value for
	 * @return The value associated with the key
	 */
	@Nullable
	public Object getValue(String key) {
		Validate.notNull(key);
		
		return this.config.get(key);
	}
	
	/**
	 * Set a config value
	 * @param key The key to set
	 * @param value The value to set
	 */
	public void setValue(String key, Object value) {
		Validate.notNull(key);
		Validate.notNull(value);
		
		this.config.put(key, value);
	}
}
