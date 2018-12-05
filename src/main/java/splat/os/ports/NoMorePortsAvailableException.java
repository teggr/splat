package splat.os.ports;

@SuppressWarnings("serial")
public class NoMorePortsAvailableException extends Exception {

	public NoMorePortsAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoMorePortsAvailableException(String message) {
		super(message);
	}

}
