package splat.core;

@SuppressWarnings("serial")
public class PlatformException extends Exception {

	public PlatformException() {
		super();
	}

	public PlatformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PlatformException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlatformException(String message) {
		super(message);
	}

	public PlatformException(Throwable cause) {
		super(cause);
	}

}
