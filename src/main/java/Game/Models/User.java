package Game.Models;

import Backend.FileHandling;
import Backend.JSON;
import Backend.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static Backend.JSON.*;
import static Game.Models.Token.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("CanBeFinal")
public class User {
	private String username;
	private Integer bankAmount;
	private String notificationToken;
	private ArrayList<Game> gamesList;
	private String createDate;
	
	private String lastLoginDate;
	private Integer startCount;
	private String appVersion;
	
	private boolean isTestUser;
	
	private String newToken;
	
	JsonArray gamesArray;
	
	private String userToken;
	
	public User(String token){
		try{
			userToken = token;
			
			JsonObject userFileJSON = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
			
			JsonObject usersObject = userFileJSON.getAsJsonObject(USERS_LIST_KEY);
			
			JsonObject thisUserObject = usersObject.getAsJsonObject(userToken);
			thisUserObject.addProperty(TOKEN_KEY, userToken);
			
			assignFieldsFromJSON(thisUserObject);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private User(JsonObject jsonObject){
		assignFieldsFromJSON(jsonObject);
	}
	
	private void assignFieldsFromJSON(JsonObject jsonObject){
		username = jsonObject.get(USERNAME_KEY).getAsString();
		bankAmount = jsonObject.get(BANK_KEY).getAsInt();
		gamesArray = jsonObject.getAsJsonArray(GAMES_KEY);
		
		if(jsonObject.has(NOTIFICATION_TOKEN_KEY)){
			//Otherwise, notifications has not been allowed by client
			notificationToken = jsonObject.get(NOTIFICATION_TOKEN_KEY).getAsString();
		}
		
		createDate = jsonObject.get(CREATE_DATE_KEY).getAsString();

		startCount = jsonObject.get(AMOUNT_OF_STARTS_KEY).getAsInt();
		lastLoginDate = jsonObject.get(LAST_LOGIN_DATE_KEY).getAsString();
		
		if(jsonObject.has(IS_TEST_USER_KEY)){
			isTestUser = jsonObject.get(IS_TEST_USER_KEY).getAsBoolean();
		}
		else{
			isTestUser = false;
		}
		
		if(jsonObject.has(VERSION_KEY)){
			appVersion = jsonObject.get(VERSION_KEY).getAsString();
		}
		else{
			appVersion = "< 1.1";
		}
		
		if(jsonObject.has(NEW_TOKEN_KEY)){
			newToken = jsonObject.get(NEW_TOKEN_KEY).getAsString();
		}
		
		userToken = jsonObject.get(TOKEN_KEY).getAsString();
	}
	
	private User(){
		//Not accessible
	}
	
	public void newStart() throws IOException{
		startCount++;
		lastLoginDate = Logger.getDate();
	}
	
	/**
	 * OBS! CAN BE NULL
	 * @return new token, null if should not change
	 */
	public String getNewToken() {
		return newToken;
	}
	
	public void setNewToken(String newToken) {
		this.newToken = newToken;
	}
	
	public JsonArray getGamesAsJSONArray(){
		return gamesArray;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Integer getBankAmount() {
		return bankAmount;
	}
	
	public void setBankAmount(Integer bankAmount) {
		this.bankAmount = bankAmount;
	}
	
	/**
	 *
	 * @param toAdd be negative to subtract
	 */
	public void addToBankAmount(Integer toAdd){
		this.bankAmount += toAdd;
	}
	
	public String getUserToken() {
		return userToken;
	}
	
	public ArrayList<Game> getGamesList() {
		String gameID = "";
		try{
			gamesList = new ArrayList<Game>();
			
			for(JsonElement jsonElement : gamesArray){
				gameID = jsonElement.getAsString();
				gamesList.add(new Game(gameID));
			}
		}
		catch (Exception e){
			Logger.logError(e, "in getGamesList", "Probable cause is that there is no game with said ID. (" + gameID + ")");
		}
		
		return gamesList;
	}
	
	public void addGame(String id){
		JsonElement idAsJson = JSON.parseStringToJSONElement(id);
		if(!gamesArray.contains(idAsJson)) {
			gamesArray.add(id);
		}
	}
	
	public String getNotificationToken(){
		return notificationToken;
	}
	
	public void setNotificationToken(String notificationToken){
		this.notificationToken = notificationToken;
	}
	
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	public boolean isTestUser() {
		return isTestUser;
	}
	
	public String toJSONString(){
		return JSON.beautifyJSON(asJson());
	}
	
	public JsonObject asJson(){
		JsonObject userJSON = new JsonObject();
		
		userJSON.addProperty(USERNAME_KEY, username);
		userJSON.addProperty(BANK_KEY, bankAmount);
		userJSON.add(GAMES_LIST_KEY, gamesArray);
		userJSON.addProperty(NOTIFICATION_TOKEN_KEY, notificationToken);
		userJSON.addProperty(CREATE_DATE_KEY, createDate);
		userJSON.addProperty(LAST_LOGIN_DATE_KEY, lastLoginDate);
		userJSON.addProperty(AMOUNT_OF_STARTS_KEY, startCount);
		userJSON.addProperty(IS_TEST_USER_KEY, isTestUser);
		userJSON.addProperty(VERSION_KEY, appVersion);
		
		if(newToken != null){
			userJSON.addProperty(NEW_TOKEN_KEY, newToken);
		}
		
		return userJSON;
	}
	
	public static User createNewUser(String username, String notificationToken){
		JsonObject newUserObject = new JsonObject();
		
		newUserObject.addProperty(USERNAME_KEY, username);
		newUserObject.addProperty(BANK_KEY, 100); //Start amount for new users
		newUserObject.add(GAMES_LIST_KEY, new JsonArray());
		newUserObject.addProperty(CREATE_DATE_KEY, Logger.getDate());
		newUserObject.addProperty(LAST_LOGIN_DATE_KEY, Logger.getDate());
		newUserObject.addProperty(AMOUNT_OF_STARTS_KEY, 1);
		newUserObject.addProperty(IS_TEST_USER_KEY, false);
		newUserObject.addProperty(VERSION_KEY, "?");
		
		if(notificationToken != null && notificationToken.length() > 0) {
			newUserObject.addProperty(NOTIFICATION_TOKEN_KEY, notificationToken);
		}
		
		String generatedUserToken = generateToken(username + notificationToken);
		
		newUserObject.addProperty(TOKEN_KEY, generatedUserToken);
		
		User newUser = new User(newUserObject);
		
		return newUser;
	}
	
	public static boolean doesUserExistWithUsername(String username) throws IOException{
		return getUserWithUsername(username) != null;
	}
	
	public static User getUserWithUsername(String username) throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		JsonObject usersObject = usersFile.getAsJsonObject(USERS_LIST_KEY);
		
		String[] userTokens = usersObject.keySet().toArray(new String[0]);
		
		for(String token : userTokens){
			
			User userOfToken = new User(token);
			
			if(username.toLowerCase().equals(userOfToken.getUsername().toLowerCase())){
				//Username exists
				return userOfToken;
			}
		}
		return null;
	}
	
	public static boolean doesUserExistWithToken(String token) throws IOException{
		JsonObject usersFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		
		JsonObject usersObject = usersFile.getAsJsonObject(USERS_LIST_KEY);
		
		Set<String> userIdSet = usersObject.keySet();
		
		return userIdSet.contains(token);
	}
	
	public boolean equals(User otherUser) {
		return userToken.equals(otherUser.getUserToken());
	}
}












