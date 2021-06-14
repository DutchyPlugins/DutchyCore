package dev.array21.dutchycore.commands.executors;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dev.array21.dutchycore.DutchyCore;
import dev.array21.dutchycore.Triple;
import dev.array21.dutchycore.module.Module;
import dev.array21.dutchycore.module.commands.ModuleCommand;
import dev.array21.dutchycore.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class ModulesCommandExecutor implements ModuleCommand {

	@Override
	public boolean fire(CommandSender sender, String[] args) {
		
		if(!sender.hasPermission("dutchycore.modules")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		
		List<Module> modules = DutchyCore.getModuleLoader().getAllModules();
		
		String message = ChatColor.GOLD + "Module %s:"
				+ "\nVersion %s"
				+ "\nAuthor %s"
				+ "\nInfo url %s"
				+ "\n";
		
		message = Utils.processColours(message, new Triple<String, ChatColor, ChatColor>("%s", ChatColor.GREEN, ChatColor.GOLD));
		
		for(Module m : modules) {
			TextComponent textComponent = new TextComponent(String.format(message, m.getName(), m.getVersion(), m.getAuthor(), m.getInfoUrl()));
			textComponent.setClickEvent(new ClickEvent(Action.OPEN_URL, m.getInfoUrl()));
			
			sender.spigot().sendMessage(textComponent);
		}
		
		return true;
	}
}
