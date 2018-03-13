package Server.Exceptions;

public class NoSuchGameException extends Exception{
	
	public NoSuchGameException(String ID){
	    super("No game with ID: " + ID);
	}
	
}
