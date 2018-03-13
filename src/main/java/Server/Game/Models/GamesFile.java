package Server.Game.Models;

import Server.Backend.FileHandling;
import Server.Constants;
import Server.Exceptions.NoSuchGameException;
import Server.Exceptions.NoSuchUserException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Set;

import static Server.Backend.JSON.GAMES_LIST_KEY;
import static Server.Backend.JSON.ID_KEY;

public class GamesFile {
	
	JsonObject gamesListObject;
	
	public GamesFile() throws IOException{
		JsonObject gamesFileObject = FileHandling.getContentOfFileAsJSON(FileHandling.File.Games);
		gamesListObject = gamesFileObject.getAsJsonObject(GAMES_LIST_KEY);
	}
	
	/**
	 * Get a Game object represented by the game with the parameter as ID
	 * @param id the id of the game
	 * @return the Game object of the game
	 * @throws NoSuchGameException if there is no game with ID
	 */
	public Game getGame(String id) throws NoSuchGameException{
		JsonElement gameElement = gamesListObject.get(id);
		if(gameElement != null && !gameElement.isJsonNull()){
			JsonObject gameJsonObject = gameElement.getAsJsonObject();
			gameJsonObject.addProperty(ID_KEY, id);
			
			Game gameObject = new Game(gameJsonObject);
			return gameObject;
		}
		else{
			throw new NoSuchGameException(id);
		}
	}
	
	public boolean gameWithIdExists(String id){
		Set<String> gameIDSet = gamesListObject.keySet();
		
		return gameIDSet.contains(id);
	}

	public void addGame(Game game) throws IOException, NoSuchUserException{
		
		gamesListObject.add(game.getID(), game.asJson());
		
		UsersFile usersFile = Constants.USERS_FILE;
		
		usersFile.addGameToUser(game.getUsersList().get(0).getUserToken(), game.getID());
		usersFile.addGameToUser(game.getUsersList().get(1).getUserToken(), game.getID());
	}
	
	public void removeGame(Game game) throws IOException{
		gamesListObject.remove(game.getID());
	}
	
	public void save(){
		JsonObject gamesFile = new JsonObject();
		gamesFile.add(GAMES_LIST_KEY, gamesListObject);
		FileHandling.saveToFile(gamesFile, FileHandling.File.Games);
	}
	
}