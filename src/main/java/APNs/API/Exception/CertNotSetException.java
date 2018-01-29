package APNs.API.Exception;

public class CertNotSetException extends Exception {
	
	public CertNotSetException(){
		super();
	}
	
	public CertNotSetException(String message) {
		super(message);
	}
	
	public CertNotSetException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CertNotSetException(Throwable cause) {
		super(cause);
	}
}
