package dev.array21.dutchycore.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionDefault;

import dev.array21.dutchycore.DutchyCore;
import dev.array21.dutchycore.annotations.EventHandler;
import dev.array21.dutchycore.annotations.Nullable;
import dev.array21.dutchycore.module.commands.ModuleCommand;
import dev.array21.dutchycore.module.commands.ModuleTabCompleter;
import dev.array21.dutchycore.module.events.ModuleEvent;
import dev.array21.dutchycore.module.events.ModuleEventListener;
import dev.array21.dutchycore.module.file.ModuleFileHandler;

public abstract class PluginModule {

	private DutchyCore plugin;
	private ModuleFileHandler moduleFileHandler;

	/**
	 * This method should only be called by {@link ModuleLoader}
	 * @param plugin The DutchyCore instance
	 */
	protected void init(DutchyCore plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Enable a module
	 * @param plugin An instance of DutchyCore
	 */
	public abstract void enable(DutchyCore plugin);
	
	/**
	 * Called after all modules have been enabled
	 */
	public void postEnable() {
		//Implementation by module is optional
	}
	
	
	/**
	 * Get the Module associated with this PluginModule
	 * @return Returns the Module
	 */
	public Module getModuleInfo() {
		for(Module m : DutchyCore.getModuleLoader().getAllModules()) {
			if(m.getModule().equals(this)) {
				return m;
			}
		}
		
		//Unreachable
		return null;
	}
	
	/**
	 * Get a PluginModule
	 * @param moduleName The name of the Module
	 * @return Returns a PluginModule, if none was found null
	 */
	@Nullable
	public PluginModule getPluginModule(String moduleName) {
		for(Module m : DutchyCore.getModuleLoader().getAllModules()) {
			if(m.getName().equalsIgnoreCase(moduleName)) return m.getModule();
		}
		
		return null;
	}
	
	/**
	 * Throw a ModuleEvent
	 * @param <T> The type of the ModuleEvent to be thrown
	 * @param moduleEvent Instance of the ModuleEvent
	 */
	public <T extends ModuleEvent> void throwModuleEvent(T moduleEvent) {
		
		//Get all methods which handle events
		HashMap<ModuleEventListener, Method> handlers = new HashMap<>();
		for(ModuleEventListener listener : DutchyCore.getModuleLoader().moduleEventListeners) {
			Class<?> clazz = listener.getClass();
			
			for(Method m : getMethodsAnnotatedWith(clazz, EventHandler.class)) {
				if(doesMethodHaveParameter(m, moduleEvent.getClass())) {
					handlers.put(listener, m);
				}
			}
		}
		
		//Get all listeners for this ModuleEvent
		//List<ModuleEventListener> listeners = DutchyCore.getModuleLoader().moduleEventListeners.get(moduleEvent.getClass());
		
		//Iterate over the event listeners and call the onEvent method
		handlers.forEach((k, v) -> {
			try {
				v.invoke(k, moduleEvent);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}
	
	private boolean doesMethodHaveParameter(Method method, Class<?> parameterType) {
		for(Class<?> clazz : method.getParameterTypes()) {
			if(clazz.equals(parameterType)) return true;
		}
		
		return false;
	}
	
	private List<Method> getMethodsAnnotatedWith(Class<?> classToSearch, Class<? extends Annotation> annotation) {
		List<Method> methods = new ArrayList<>();
		
		for(Method m : classToSearch.getDeclaredMethods()) {
			if(m.isAnnotationPresent(annotation)) {
				methods.add(m);
			}
		}
		
		return methods;
	}
	
	/**
	 * Register a ModuleEventListener
	 * @param eventListener An instance of a ModuleEventListener that wants to receive events
	 */
	public void registerModuleEventListener(ModuleEventListener eventListener) {
		DutchyCore.getModuleLoader().moduleEventListeners.add(eventListener);
	}
	
	/**
	 * Check if a module is registerd
	 * @param moduleName The name of the module
	 * @return Returns true if the module is registered
	 */
	public boolean isModuleRegistered(String moduleName) {
		for(Module m : DutchyCore.getModuleLoader().loadedModules.values()) {
			if(m.getName().equalsIgnoreCase(moduleName)) return true;
		}
		
		return false;
	}
	
	/**
	 * Register a command
	 * @param commandName The name of the command
	 * @param moduleCommand The class implementing ModuleCommand, which will execute the command
	 * @param pluginModule PluginModule instance
	 */
	public void registerCommand(String commandName, ModuleCommand moduleCommand, PluginModule pluginModule) {
		Module module = DutchyCore.getModuleLoader().getModule(pluginModule);
		DutchyCore.getCommandRegister().registerCommand(plugin, commandName, moduleCommand, module.getName().toLowerCase());
	}
	
	/**
	 * Register a ModuleTabCompleter for a command<br>
	 * <strong>The command has to be registered before registering a ModuleTabCompleter!</strong>
	 * @param commandName The name of the command
	 * @param tabCompleter The class implementing ModuleTabCompleter, which will handle tab completion
	 * @param pluginModule PluginModule instance
	 */
	public void registerTabCompleter(String commandName, ModuleTabCompleter tabCompleter, PluginModule pluginModule) {
		DutchyCore.getCommandRegister().registerTabCompleter(commandName, tabCompleter);
	}
	
	/**
	 * Register a permission node with Bukkit
	 * @param name The name of the permission node
	 * @param permissionDefault {@link Nullable} The default Permission (e.g grant it to all players on default)
	 * @param description {@link Nullable} The description of the permission
	 * @param children {@link Nullable} A HashMap with nodenames of all children of this permission. Value is true if they are a child, false if they are not
	 */ 
	public void registerPermissionNode(String name, @Nullable PermissionDefault permissionDefault, @Nullable String description, @Nullable HashMap<String, Boolean> children) {		
		DutchyCore.getCommandRegister().registerPermissionNode(name, permissionDefault, description, children);
	}
	
	/**
	 * Register an event listener
	 * @param listener The event listener to register
	 */
	public void registerEventListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this.plugin);
	}
	
	/**
	 * Unregister an event listener
	 * @param listener The event listener to unregister
	 */
	public void unregisterEventListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}
	
	/**
	 * Get the DutchyCore instance
	 * @return Returns the DutchCore instance
	 */
	public DutchyCore getCore() {
		return this.plugin;
	}
	
	/**
	 * Check if a module is installed
	 * @param moduleName The name of the module to check for
	 * @return Returns true if the module is installed, false if it is not
	 */
	@Deprecated
	public boolean isModuleInstalled(String moduleName) {
		for(Module m : DutchyCore.getModuleLoader().loadedModules.values()) {
			if(m.getName().equals(moduleName)) return true;
		}
		
		return false;
	}

	/**
	 * Log at INFO level
	 * @param log The object to log
	 */
	public void logInfo(Object log) {
		DutchyCore.logInfo(String.format("[%s] %s", DutchyCore.getModuleLoader().loadedModules.get(this).getName(), log.toString()));
	}
	
	/**
	 * Log at WARN level
	 * @param log The object to log
	 */
	public void logWarn(Object log) {
		DutchyCore.logWarn(String.format("[%s] %s", DutchyCore.getModuleLoader().loadedModules.get(this).getName(), log.toString()));
	}
	
	/**
	 * Get the file handler for this PluginModule
	 * @return Returns the ModuleFileHandler for this PluginModule
	 */
	public ModuleFileHandler getModuleFileHandler() {
		if(this.moduleFileHandler == null) {
			this.moduleFileHandler = new ModuleFileHandler(this.plugin, this);
		}
		
		return this.moduleFileHandler;
	}
}
