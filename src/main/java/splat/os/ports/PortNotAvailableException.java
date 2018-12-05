package splat.os.ports;

@SuppressWarnings("serial")
public class PortNotAvailableException extends Exception {

	public PortNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public PortNotAvailableException(String message) {
		super(message);
	}

}
