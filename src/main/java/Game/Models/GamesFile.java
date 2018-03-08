package Game.Models;

import Backend.FileHandling;
import Backend.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import static Backend.JSON.GAMES_LIST_KEY;

public class GamesFile {

	public static void addGame(Game game) throws IOException{
		JsonObject gamesFileObject = FileHandling.getContentOfFileAsJSON(FileHandling.File.Games);
		JsonObject gamesListObject = gamesFileObject.getAsJsonObject(GAMES_LIST_KEY);
		
		gamesListObject.add(game.getID(), game.asJson());
		
		UsersFile.addGameToUser(game.getUsersList().get(0).getUserToken(), game.getID());
		UsersFile.addGameToUser(game.getUsersList().get(1).getUserToken(), game.getID());
		
		FileHandling.saveToFile(gamesFileObject, FileHandling.File.Games);
	}
	
	public static void removeGame(Game game) throws IOException{
		JsonObject gamesFileObject = FileHandling.getContentOfFileAsJSON(FileHandling.File.Games);
		JsonObject gamesListObject = gamesFileObject.getAsJsonObject(GAMES_LIST_KEY);
		
		gamesListObject.remove(game.getID());
	}

}
