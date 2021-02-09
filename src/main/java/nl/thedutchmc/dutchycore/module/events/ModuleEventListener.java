package nl.thedutchmc.dutchycore.module.events;

public interface ModuleEventListener {
	
	/**
	 * This method is called when the ModuleEvent this listener subscribed to is fired
	 * @param event ModuleEvent instance
	 */
	public void onEvent(ModuleEvent event);
}
