package Server.DevOnly;

import Server.Constants;
import Server.Exceptions.NoSuchGameException;
import Server.Exceptions.NoSuchUserException;
import Server.Game.Models.Game;
import Server.Game.Models.GamesFile;
import Server.Game.Models.User;
import Server.Game.Models.UsersFile;
import com.google.gson.JsonArray;

import java.io.IOException;

/**
 * Remove all game-id strings in the users that do not exist (anymore)
 */
public class CleanGames {
	public static void main(String[] args) throws IOException, NoSuchUserException{
	    new CleanGames();
	}
	
	public CleanGames() throws IOException, NoSuchUserException{
		UsersFile usersFile = new UsersFile();
		GamesFile gamesFile = new GamesFile();
		String[] allUserIDs = usersFile.getAllUserIDs();
		
		for(String userID : allUserIDs){
			User user = Constants.USERS_FILE.getUser(userID);
			
			JsonArray gamesOfUser = user.getGamesAsJSONArray();
			for(int i = 0; i < gamesOfUser.size(); i++){
				String gameID = gamesOfUser.get(i).getAsString();
				try{
				    Game game = gamesFile.getGame(gameID);
				}
				catch (NoSuchGameException e){
					System.out.println("No game with ID " + gameID);
					gamesOfUser.remove(i);
				}
			}
			usersFile.addUser(user);
			usersFile.save();
		}
		
	}
	
}
