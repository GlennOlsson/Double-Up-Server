package Game.Models;

import Backend.FileHandling;
import Backend.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import static Backend.JSON.GAMES_LIST_KEY;

public class GamesFile {
	
	JsonObject gamesListObject;
	
	public GamesFile() throws IOException{
		JsonObject gamesFileObject = FileHandling.getContentOfFileAsJSON(FileHandling.File.Games);
		gamesListObject = gamesFileObject.getAsJsonObject(GAMES_LIST_KEY);
	}

	public void addGame(Game game) throws IOException{
		
		gamesListObject.add(game.getID(), game.asJson());
		
		UsersFile usersFile = new UsersFile();
		
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
