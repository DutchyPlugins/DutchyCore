package nl.thedutchmc.dutchycore.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yaml.snakeyaml.Yaml;

import nl.thedutchmc.dutchycore.DutchyCore;
import nl.thedutchmc.dutchycore.Pair;
import nl.thedutchmc.dutchycore.annotations.Nullable;
import nl.thedutchmc.dutchycore.module.events.ModuleEvent;
import nl.thedutchmc.dutchycore.module.events.ModuleEventListener;
import nl.thedutchmc.dutchycore.module.exceptions.InvalidModuleException;

public class ModuleLoader {
	
	protected HashMap<PluginModule, Module> loadedModules = new HashMap<>();
	protected HashMap<Class<? extends ModuleEvent>, List<ModuleEventListener>> moduleEventListeners = new HashMap<>();
	
	private ModuleClassLoader moduleClassLoader;
	
	/**
	 * Load all modules<br>
	 * <br>
	 * All modules in pluginFolder/modules/ will be loaded
	 * @param plugin DutchyCore instance
	 */
	public void loadAllModules(DutchyCore plugin) {
		
		//Get the module folder, if it doesn't exist, create it
		File moduleFolder = new File(plugin.getDataFolder() + File.separator + "modules");
		if(!moduleFolder.exists()) {
			moduleFolder.mkdirs();
		}
		
		//Discover all modules in the module folder
		DutchyCore.logInfo("Discovering modules...");
		List<String> modulePaths = discoverModules(moduleFolder);
		DutchyCore.logInfo(String.format("Discovered %d module(s)!", modulePaths.size()));
	
		//Iterate over all module paths
		Module[] modulesToLoad = new Module[modulePaths.size()];
		URL[] jarUrls = new URL[modulePaths.size()];
		for(int i = 0; i < modulePaths.size(); i++) {
			File moduleFile = new File(modulePaths.get(i));
			
			//Load information from module.yml about the module
			modulesToLoad[i] = loadModuleInformation(moduleFile);
			
			//Add the file as an URL to the array of URL's
			try {
				jarUrls[i] = moduleFile.toURI().toURL();
			} catch(MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		//Create a ModuleClassLoader instance
		this.moduleClassLoader = new ModuleClassLoader(jarUrls, this.getClass().getClassLoader());
		
		//Iterate over the modulesToLoad array and load each Module
		for(Module module : modulesToLoad) {
			DutchyCore.logInfo("Loading modules " + module.getName());
			
			//Load the main class and call the init method
			PluginModule pluginModule = this.moduleClassLoader.loadMainClass(module.getMainClass());
			pluginModule.init(plugin);

			//Set the PluginModule on the Module
			module.setModule(pluginModule);
			
			//Add the PluginModule and Module to the map of loaded modules
			this.loadedModules.put(pluginModule, module);
			
			//Finally, call the enable method on the PluginModule to let the module do what it needs to for initalization
			pluginModule.enable(plugin);
		}
		
		//Iterate over the PluginModules in the map of loaded modules and call the postEnable method
		for(PluginModule pm : this.loadedModules.keySet()) {
			pm.postEnable();
		}
	}
	
	/**
	 * Get a Module
	 * @param pluginModule The PluginModule of the Module
	 * @return Returns the Module associated with the provided PluginModule. Null if not found
	 */
	@Nullable
	public Module getModule(PluginModule pluginModule) {
		return this.loadedModules.get(pluginModule);
	}
	
	/**
	 * Get all loaded modules
	 * @return Returns a list of all loaded Modules
	 */
	public List<Module> getAllModules() {
		return new ArrayList<>(this.loadedModules.values());
	}
	
	/**
	 * Discover all files ending in .jar in {@link modulesFolder}
	 * @param modulesFolder The folder to look in
	 * @return Returns a List of absolute paths for all files ending in .jar found in {@link modulesFolder}
	 */
	private List<String> discoverModules(File modulesFolder) {
		Stream<Path> walk = null;
		try {
			walk = Files.walk(Paths.get(modulesFolder.getAbsolutePath()));
		} catch(IOException e) {
			e.printStackTrace();
		}
		List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".jar")).collect(Collectors.toList());
		
		walk.close();
		return result;
	}
	
	/**
	 * Load a module from a File
	 * @param file The jarfile to load
	 * @param plugin Instance of DutchyCore
	 */
	private Module loadModuleInformation(File file) {
		//Load the modules module.yml file
		Pair<InputStream, JarFile> yamlLoaded = loadModuleYaml(file);
		
		//Get information from the YAML file
		HashMap<String, Object> yamlValues = loadYaml(yamlLoaded.getA());
		String mainClassName = (String) getYamlValue("main", yamlValues, file);
		String version = (String) getYamlValue("version", yamlValues, file);
		String author = (String) getYamlValue("author", yamlValues, file);
		String name = (String) getYamlValue("name", yamlValues, file);
		String infoUrl = (String) getYamlValue("infourl", yamlValues, file);
		
		//Try to close the JarFile, we dont need it open anymore
		try {
			yamlLoaded.getB().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Create a new Module object and return it
		return new Module(name,
				mainClassName,
				version,
				author,
				infoUrl);
	}
	
	public class ModuleClassLoader extends URLClassLoader {
				
		public ModuleClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}
		
		/**
		 * Load the main class of a Module
		 * @param className The name of the main class
		 * @return Returns an instance of PluginModule, or null if an exception occured
		 */
		@Nullable
		public PluginModule loadMainClass(String className) {
			try {
				//Get the Class for the provided mainClassName
				Class<?> jarClazz = findClass(className);
								
				//the main class extends PluginModule, so get the main class as a subclass of PluginModule
				Class<? extends PluginModule> moduleClazz = null;
				try {
					moduleClazz = jarClazz.asSubclass(PluginModule.class);
				} catch(ClassCastException e) {
					throw new InvalidModuleException(String.format("Provided main class %s does not extend PluginModuke", className));
				}
				
				//Get the constructor for the plugin class
				Constructor<?> pluginClazzConstructor = moduleClazz.getConstructor();
				
				//Create an instance of the plugin class
				return (PluginModule) pluginClazzConstructor.newInstance();
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	/**
	 * Load a module's module.yml file
	 * @param file The jarfile to load the yaml from
	 * @return Returns a Pair with the InputStream and the JarFile
	 */
	private Pair<InputStream, JarFile> loadModuleYaml(File file) {
		
		try {
			//Create a JarFile and get the entry module.yml file
			JarFile jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry("module.yml");
			
			//If entry is null, then there was no module.yml file, it's an invalid module
			if(entry == null) {
				jar.close();
				throw new InvalidModuleException(String.format("Missing module.yml in %s", file.getPath()));
			}
			
			//Get the InputStream for the module.yml file and return it
			InputStream stream = jar.getInputStream(entry);
			return new Pair<InputStream, JarFile>(stream, jar);	
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Get the a value for the associated key<br>
	 * Throws an {@link InvalidModuleException} when the key was not found
	 * @param key The key to get the value for
	 * @param yamlValues HashMap with key-value pairs loaded from a YAML file
	 * @param file The jarfile of the Module
	 * @return Returns the value associated with the key
	 */
	private Object getYamlValue(String key, HashMap<String, Object> yamlValues, File file) {
		
		//If the map doesnt contain the main key, we consider the module invalid
		if(!yamlValues.containsKey(key)) {
			throw new InvalidModuleException(String.format("Missing key '%s' in module.yml in %s", key, file.getPath()));
		}
		
		//Return the value of main
		return yamlValues.get(key);
	}
	
	/**
	 * Load a YAML file
	 * @param stream InputStream {@link #loadModuleYaml(File)}
	 * @return Returns a HashMap with key-value pairs from the yaml file
	 */
	private HashMap<String, Object> loadYaml(InputStream stream) {
		Yaml yaml = new Yaml();
		return yaml.load(stream);
	}
}
