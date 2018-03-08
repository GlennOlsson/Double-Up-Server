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
		
		String[] userTokens = UsersFile.getAllUserIDs();
		
		for(String token : userTokens){
			User user = new User(token);
			System.out.println(user.getUsername() + " has game? " + user.getGamesList().remove(game));
		}
		
		GamesFile.removeGame(game);
		
	}
}
