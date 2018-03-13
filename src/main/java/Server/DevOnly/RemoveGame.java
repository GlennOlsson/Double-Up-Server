package Server.DevOnly;

import Server.Constants;
import Server.Exceptions.NoSuchGameException;
import Server.Exceptions.NoSuchUserException;
import Server.Game.Models.Game;
import Server.Game.Models.GamesFile;
import Server.Game.Models.User;
import Server.Game.Models.UsersFile;

import java.io.IOException;

public class RemoveGame {
	
	public static void main(String[] args) throws NoSuchGameException, IOException, NoSuchUserException{
		String gameID = "RMqA7SS2zGJIS2n0B";
	    new RemoveGame(gameID);
	}
	
	public RemoveGame(String gameID) throws NoSuchGameException, IOException, NoSuchUserException{
		UsersFile usersFile = new UsersFile();
		GamesFile gamesFile = new GamesFile();
		
		Game game = gamesFile.getGame(gameID);
		
		String[] userTokens = usersFile.getAllUserIDs();
		
		for(String token : userTokens){
			User user = usersFile.getUser(token);
			System.out.println(user.getUsername() + " has game? " + user.getGamesList().remove(game));
			usersFile.addUser(user);
		}
		
		usersFile.save();
		
		gamesFile.removeGame(game);
		gamesFile.save();
		
	}
}
