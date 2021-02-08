package nl.thedutchmc.dutchycore.module;

public class Module {

	private PluginModule module;
	private String name, mainClass, version, author, infoUrl;
	
	public Module(PluginModule module, String name, String mainClass, String version, String author, String infoUrl) {
		this.module = module;
		this.name = name;
		this.mainClass = mainClass;
		this.version = version;
		this.author = author;
		this.infoUrl = infoUrl;
	}
	
	public PluginModule getModule() {
		return this.module;
	}
	
	/**
	 * Get the name of the module
	 * @return Returns the name of the module
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the main class of the module
	 * @return Returns the main class of the module
	 */
	public String getMainClass() {
		return this.mainClass;
	}
	
	/**
	 * Get the version of the module
	 * @return Returns the version of the module
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * Get the author of the module
	 * @return Returns the author of the module
	 */
	public String getAuthor() {
		return this.author;
	}
	
	/**
	 * Get the info URL of the module
	 * @return Returns the info URL of the module
	 */
	public String getInfoUrl() {
		return this.infoUrl;
	}
}
