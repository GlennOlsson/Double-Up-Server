package DevOnly;

import Game.Models.Game;
import Game.Models.GamesFile;
import Game.Models.User;
import Game.Models.UsersFile;

import java.io.IOException;
import java.util.ArrayList;

public class RemoveGame {
	
	public static void main(String[] args) throws IOException{
		String gameID = "RMqA7SS2zGJIS2n0B";
	    new RemoveGame(gameID);
	}
	
	public RemoveGame(String gameID) throws IOException{
		Game game = new Game(gameID);
		UsersFile usersFile = new UsersFile();
		GamesFile gamesFile = new GamesFile();
		
		String[] userTokens = usersFile.getAllUserIDs();
		
		for(String token : userTokens){
			User user = new User(token);
			System.out.println(user.getUsername() + " has game? " + user.getGamesList().remove(game));
			usersFile.addUser(user);
		}
		
		usersFile.save();
		
		gamesFile.removeGame(game);
		gamesFile.save();
		
	}
}
