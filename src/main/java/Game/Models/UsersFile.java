package Game.Models;

import Backend.FileHandling;
import Backend.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import static Backend.JSON.*;

public class UsersFile {
	
	JsonObject usersObject;
	
	public UsersFile() throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		usersObject = usersFile.getAsJsonObject(USERS_LIST_KEY);
	}
	
	public void addUser(User user) throws IOException{
		usersObject.add(user.getUserToken(), user.asJson());
	}
	
	public void removeUser(User user) throws IOException{
		usersObject.add(user.getUserToken(), user.asJson());
	}
	
	public void addGameToUser(String userToken, String gameID) throws IOException{
		User user = new User(userToken);
		user.addGame(gameID);
		addUser(user);
	}
	
	public String[] getAllUserIDs() throws IOException{
		String[] usersIDsJSONArray = usersObject.keySet().toArray(new String[0]);
		return usersIDsJSONArray;
	}
	
	public void save(){
		JsonObject usersFile = new JsonObject();
		usersFile.add(USERS_LIST_KEY, usersObject);
		FileHandling.saveToFile(usersFile, FileHandling.File.Users);
	}
}
