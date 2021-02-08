package nl.thedutchmc.dutchycore.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import nl.thedutchmc.dutchycore.DutchyCore;
import nl.thedutchmc.dutchycore.utils.FileUtils;

public class CoreConfiguration {

	private DutchyCore plugin;

	private HashMap<String, Object> configValues = new HashMap<>();
	
	public CoreConfiguration(DutchyCore plugin) {
		this.plugin = plugin;
	}
	
	public void read() {
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		
		if(!configFile.exists()) {
			FileUtils.saveResource("config.yml", configFile.getAbsolutePath());
		}
		
		FileConfiguration config = new Utf8YamlConfiguration();
		try {
			config.load(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			DutchyCore.logWarn(String.format("Config file %s is invalid", configFile.getPath()));
		}
		
		for(String key : config.getKeys(false)) {
			this.configValues.put(key, config.get(key));
		}
	}
	
	public Object getOption(String key) {
		return configValues.get(key);
	}
}
