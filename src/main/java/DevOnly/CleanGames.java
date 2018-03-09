package DevOnly;

import Game.Models.Game;
import Game.Models.User;
import Game.Models.UsersFile;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;

/**
 * Remove all game-id strings in the users that do not exist (anymore)
 */
public class CleanGames {
	public static void main(String[] args) throws IOException{
	    new CleanGames();
	}
	
	public CleanGames() throws IOException{
		UsersFile usersFile = new UsersFile();
		String[] allUserIDs = usersFile.getAllUserIDs();
		
		for(String userID : allUserIDs){
			User user = new User(userID);
			
			JsonArray gamesOfUser = user.getGamesAsJSONArray();
			for(int i = 0; i < gamesOfUser.size(); i++){
				String gameID = gamesOfUser.get(i).getAsString();
				try{
				    Game game = new Game(gameID);
				}
				catch (NullPointerException e){
					System.out.println("No game with ID " + gameID);
					gamesOfUser.remove(i);
				}
			}
			usersFile.addUser(user);
			usersFile.save();
		}
		
	}
	
}
