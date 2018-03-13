package Server.Exceptions;

public class NoSuchUserException extends Exception{
	
	public NoSuchUserException(String token){
	    super("No User with token: " + token);
	}
	
}
