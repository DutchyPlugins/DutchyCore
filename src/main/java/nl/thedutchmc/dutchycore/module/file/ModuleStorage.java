package nl.thedutchmc.dutchycore.module.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import nl.thedutchmc.dutchycore.DutchyCore;
import nl.thedutchmc.dutchycore.config.Utf8YamlConfiguration;
import nl.thedutchmc.dutchycore.module.Module;

public class ModuleStorage {

	private Module module;
	private DutchyCore plugin;
	private HashMap<String, Object> storage = new HashMap<>();
	private FileConfiguration storageFileConfig;
	private File storageFile;
	
	public ModuleStorage(Module module, DutchyCore plugin) {
		this.module = module;
		this.plugin = plugin;
	}
	
	public void read() {
		storageFile = new File(plugin.getDataFolder() + File.separator + "modulestorage", module.getName() + ".yml");
		
		if(!storageFile.exists()) {
			try {
				storageFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		storageFileConfig = new Utf8YamlConfiguration();
		
		try {
			storageFileConfig.load(storageFile);
			
			for(String key : storageFileConfig.getKeys(false)) {
				this.storage.put(key, storageFileConfig.get(key));
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		Validate.notNull(this.storageFileConfig);
		
		for(Map.Entry<String, Object> entry : this.storage.entrySet()) {
			this.storageFileConfig.set(entry.getKey(), entry.getValue());
		}
		
		try {
			storageFileConfig.save(this.storageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object getValue(String key) {
		return this.storage.get(key);
	}
	
	public void setValue(String key, Object value) {
		this.storage.put(key, value);
	}
}
