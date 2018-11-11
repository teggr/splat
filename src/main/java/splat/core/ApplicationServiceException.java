package splat.core;

@SuppressWarnings("serial")
public class ApplicationServiceException extends Exception {

	public ApplicationServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationServiceException(String message) {
		super(message);
	}

}
