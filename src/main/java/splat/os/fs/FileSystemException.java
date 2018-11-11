package splat.os.fs;

@SuppressWarnings("serial")
class FileSystemException extends RuntimeException {

	public FileSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileSystemException(String message) {
		super(message);
	}
	
}
