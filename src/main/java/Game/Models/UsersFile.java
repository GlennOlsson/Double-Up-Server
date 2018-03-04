package Game.Models;

import Backend.FileHandling;
import Backend.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import static Backend.JSON.*;

public class UsersFile {
	
	public static void addUser(User user) throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		
		JsonObject usersObject = usersFile.getAsJsonObject(USERS_LIST_KEY);
		usersObject.add(user.getUserToken(), user.asJson());
		
		usersFile.add(USERS_LIST_KEY, usersObject);
		
		FileHandling.saveToFile(usersFile, FileHandling.File.Users);
	}
	
	public static void addGameToUser(String userToken, String gameID) throws IOException{
		User user = new User(userToken);
		user.addGame(gameID);
		addUser(user);
	}
	
	public static String[] getAllUserIDs() throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		JsonObject usersObject = usersFile.getAsJsonObject(USERS_LIST_KEY);
		String[] usersIDsJSONArray = usersObject.keySet().toArray(new String[0]);
		return usersIDsJSONArray;
	}
	
}
