package nl.thedutchmc.dutchycore.module.exceptions;

public class InvalidModuleException extends RuntimeException {

	private static final long serialVersionUID = 4820931778746010057L;

	private String exceptionMessage;
	
	public InvalidModuleException(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	public String getExceptionMessage() {
		return this.exceptionMessage;
	}
}
