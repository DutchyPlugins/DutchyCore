package dev.array21.dutchycore.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import dev.array21.dutchycore.DutchyCore;
import dev.array21.dutchycore.annotations.Nullable;
import dev.array21.dutchycore.commands.executors.ModulesCommandExecutor;
import dev.array21.dutchycore.commands.tabcompleters.ModulesCommandTabCompleter;
import dev.array21.dutchycore.module.commands.ModuleCommand;
import dev.array21.dutchycore.module.commands.ModuleCommandExecutor;
import dev.array21.dutchycore.module.commands.ModuleCommandTabCompleter;
import dev.array21.dutchycore.module.commands.ModuleTabCompleter;

public class CommandRegister {
	
	private DutchyCore plugin;
	
	public CommandRegister(DutchyCore plugin) {
		this.plugin = plugin;
	}
	
	public void registerDefault() {
		
		//Register executors
		registerCommand(this.plugin, "modules", new ModulesCommandExecutor(), "dutchycore");
		
		//Register tab completers
		registerTabCompleter("modules", new ModulesCommandTabCompleter());
		
		//Register permissions
		registerPermissionNode("dutchycore.modules", PermissionDefault.TRUE, "Allows usage of /modules", null);
	}
	
	/**
	 * Register a command
	 * @param plugin DutchyCore instance
	 * @param commandName The name of the command
	 * @param moduleCommand The class implementing ModuleCommand, which will execute the command
	 * @param namespace The namespace to register the command for
	 */
	public void registerCommand(DutchyCore plugin, String commandName, ModuleCommand moduleCommand, String namespace) {
		
		//Get the commandMap Field in the Server class
		Field commandMapField = null;
		try {
			commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		//Set it accessible so we can modify it (it's private by default)
		commandMapField.setAccessible(true);
		
		//Get the commandMapField as a CommandMap
		CommandMap commandMap = null;
		try {
			commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
				
		//Get the constructor for PluginCommand
		Constructor<?> pluginCommandConstructor = null;
		try {
			pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	
		//Set it to be accessible so we can invoke it
		pluginCommandConstructor.setAccessible(true);
		
		//Create an instance of PluginCommand
		PluginCommand pluginCmd = null;
		try {
			pluginCmd = (PluginCommand) pluginCommandConstructor.newInstance(commandName, plugin);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		
		//Disable the access again
		pluginCommandConstructor.setAccessible(false);
		
		//Add our Executor to the PluginCommand
		pluginCmd.setExecutor(new ModuleCommandExecutor(moduleCommand));
		
		//Register our command
		commandMap.register(namespace, pluginCmd);
		
		//Disable access to the commandMapField again
		commandMapField.setAccessible(false);
	}
	
	/**
	 * Register a ModuleTabCompleter for a command<br>
	 * <strong>The command has to be registered before registering a ModuleTabCompleter!</strong>
	 * @param commandName The name of the command
	 * @param tabCompleter The class implementing ModuleTabCompleter, which will handle tab completion
	 */
	public void registerTabCompleter(String commandName, ModuleTabCompleter tabCompleter) {
		//Get the commandMap Field in the Server class
		Field commandMapField = null;
		try {
			commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		//Set it accessible so we can modify it (it's private by default)
		commandMapField.setAccessible(true);
		
		//Get the commandMapField as a CommandMap
		CommandMap commandMap = null;
		try {
			commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Command cmd = commandMap.getCommand(commandName);
		
		//Check if the plugincommand is null (that means that it hasn't been registered yet)
		if(cmd == null) {
			throw new IllegalStateException("Trying to set a ModuleTabCompleter for a nonexistend command is illegal");
		}
		
		//Type check
		if(!(cmd instanceof PluginCommand)) {
			throw new IllegalStateException(String.format("Command %s was not properly registered!", commandName));
		}
		
		PluginCommand pluginCmd = (PluginCommand) cmd;
		
		//Set the tab completer
		pluginCmd.setTabCompleter(new ModuleCommandTabCompleter(tabCompleter));
	}
	
	/**
	 * Register a permission node with Bukkit
	 * @param name The name of the permission node
	 * @param permissionDefault {@link Nullable} The default Permission (e.g grant it to all players on default)
	 * @param description {@link Nullable} The description of the permission
	 * @param children {@link Nullable} A HashMap with nodenames of all children of this permission. Value is true if they are a child, false if they are not
	 */ 
	public void registerPermissionNode(String name, @Nullable PermissionDefault permissionDefault, @Nullable String description, @Nullable HashMap<String, Boolean> children) {		
		
		//Get the SimplePluginManager Field
		Field simplePluginManagerField = null;
		try {
			simplePluginManagerField = Bukkit.getServer().getClass().getDeclaredField("pluginManager");
		} catch(NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
				
		//Allow access to the field
		simplePluginManagerField.setAccessible(true);
		
		//Get the SimplePluginManager from the Field
		SimplePluginManager simplePluginManager = null;
		try {
			simplePluginManager = (SimplePluginManager) simplePluginManagerField.get(Bukkit.getServer());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		//If the description is empty, set it to an empty String
		if(description == null) {
			description = "";
		}
		
		//If the permissionDefault is null, we default to OP-only (vanilla behaviour)
		if(permissionDefault == null) {
			permissionDefault = PermissionDefault.OP;
		}
		
		//Create the permission node
		//If children is null, we dont give the children argument, otherwhise we do
		Permission perm = null;
		if(children == null) {
			perm = new Permission(name, description, permissionDefault);
		} else {
			perm = new Permission(name, description, permissionDefault, children);
		}
		
		//Add the permission node to the SimplePluginManager
		simplePluginManager.addPermission(perm);
		
		//Remove access again
		simplePluginManagerField.setAccessible(false);
	}
}
