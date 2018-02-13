package Game.Models;

import Backend.FileHandling;
import Backend.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import static Backend.JSON.TOKEN_KEY;
import static Backend.JSON.USERS_LIST_KEY;
import static Backend.JSON.USER_TOKEN_LIST_KEY;

public class UsersFile {
	
	public static void addUser(User user) throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		
		JsonObject usersObject = usersFile.getAsJsonObject(USERS_LIST_KEY);
		usersObject.add(user.getUserToken(), user.asJson());
		
		JsonArray tokensArray = usersFile.getAsJsonArray(USER_TOKEN_LIST_KEY);
		
		JsonElement tokenAsJson = JSON.parseStringToJSONElement(user.getUserToken());
		if(!tokensArray.contains(tokenAsJson)) {
			tokensArray.add(user.getUserToken());
		}
		
		usersFile.add(USERS_LIST_KEY, usersObject);
		
		FileHandling.saveToFile(usersFile, FileHandling.File.Users);
	}
	
	public static void addGameToUser(String userToken, String gameID) throws IOException{
		User user = new User(userToken);
		user.addGame(gameID);
		addUser(user);
	}
	
	public static JsonArray getAllUserIDs() throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		JsonArray usersIDsJSONArray = usersFile.getAsJsonArray(USER_TOKEN_LIST_KEY);
		return usersIDsJSONArray;
	}
	
}
