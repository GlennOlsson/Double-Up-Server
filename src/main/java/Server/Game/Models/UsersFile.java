package Server.Game.Models;

import Server.Backend.FileHandling;
import Server.Constants;
import Server.Exceptions.NoSuchGameException;
import Server.Exceptions.NoSuchUserException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Set;

import static Server.Backend.JSON.*;

public class UsersFile {
	
	JsonObject usersObject;
	
	public UsersFile() throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		usersObject = usersFile.getAsJsonObject(USERS_LIST_KEY);
	}
	
	/**
	 * Get a User object represented by the user with the parameter as token
	 * @param token the user token of the user
	 * @return the User object of the game
	 * @throws NoSuchUserException if there is no user with token
	 */
	public User getUser(String token) throws NoSuchUserException {
		JsonElement userElement = usersObject.get(token);
		if(userElement != null && !userElement.isJsonNull()){
			JsonObject userJsonObject = userElement.getAsJsonObject();
			userJsonObject.addProperty(TOKEN_KEY, token);
			
			User userObject = new User(userJsonObject);
			return userObject;
		}
		else{
			throw new NoSuchUserException(token);
		}
	}
	
	public void addUser(User user) throws IOException{
		usersObject.add(user.getUserToken(), user.asJson());
	}
	
	public void removeUser(User user) throws IOException{
		usersObject.add(user.getUserToken(), user.asJson());
	}
	
	public User getUserWithUsername(String username){
		String[] userTokens = usersObject.keySet().toArray(new String[0]);
		
		for(String token : userTokens){
			try{
				User userOfToken = Constants.USERS_FILE.getUser(token);
				
				if(username.toLowerCase().equals(userOfToken.getUsername().toLowerCase())){
					//Username exists
					return userOfToken;
				}
			}
			catch (NoSuchUserException e){
				System.out.println("No user with token " + token + " in /getUsername but not throwing exception");
			}
		}
		return null;
	}
	
	public boolean doesUserExistWithUsername(String username){
		return getUserWithUsername(username) != null;
	}
	
	public boolean doesUserExistWithToken(String token){
		Set<String> userIdSet = usersObject.keySet();
		
		return userIdSet.contains(token);
	}
	
	public void addGameToUser(String userToken, String gameID) throws NoSuchUserException, IOException{
		User user = Constants.USERS_FILE.getUser(userToken);
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
