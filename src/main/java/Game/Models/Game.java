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

public class Game{
	
	private ArrayList<User> usersList;
	
	private JsonArray usersArray;
	private String turn;
	private int currentAmount;
	private boolean isOver;
	private String createDate;
	
	private String ID;
	
	
	private Game(JsonObject jsonObject){
		assignFieldsFromJSON(jsonObject);
	}
	
	public Game(String ID) throws IOException{
		try{
			JsonObject gameFileJSON = FileHandling.getContentOfFileAsJSON(FileHandling.File.Games);
			JsonObject gamesObject = gameFileJSON.getAsJsonObject(GAMES_LIST_KEY);
			
			JsonObject thisGameObject = gamesObject.getAsJsonObject(ID);
			
			thisGameObject.addProperty(ID_KEY, ID);
			
			assignFieldsFromJSON(thisGameObject);
			
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private Game(){
		//Not accessible
	}
	
	public String getCreateDate() {
		return createDate;
	}
	
	public String getTurn() {
		return turn;
	}
	
	public void setTurn(String turn) {
		this.turn = turn;
	}
	
	public int getCurrentAmount() {
		return currentAmount;
	}
	
	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}
	
	public boolean isOver() {
		return isOver;
	}
	
	public void setOver(boolean over) {
		isOver = over;
	}
	
	public String getID() {
		return ID;
	}
	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public ArrayList<User> getUsersList() {
		usersList = new ArrayList<>();
		
		for(JsonElement jsonElement : usersArray){
			String userToken = jsonElement.getAsString();
			usersList.add(new User(userToken));
		}
		
		return usersList;
	}
	
	public String toJSONString() {
		return JSON.beautifyJSON(asJson());
	}
	
	public JsonObject asJson(){
		JsonObject gameJSON = new JsonObject();
		
		gameJSON.add(USERS_LIST_KEY, usersArray);
		gameJSON.addProperty(TURN_KEY, turn);
		gameJSON.addProperty(IS_OVER_KEY, isOver);
		gameJSON.addProperty(CURRENT_AMOUNT_KEY, currentAmount);
		gameJSON.addProperty(CREATE_DATE_KEY, createDate);
		
		return gameJSON;
	}
	
	private void assignFieldsFromJSON(JsonObject jsonObject) throws NullPointerException{
		usersArray = jsonObject.getAsJsonArray(USERS_LIST_KEY);
		
		turn = jsonObject.get(TURN_KEY).getAsString();
		currentAmount = jsonObject.get(CURRENT_AMOUNT_KEY).getAsInt();
		isOver = jsonObject.get(IS_OVER_KEY).getAsBoolean();
		createDate = jsonObject.get(CREATE_DATE_KEY).getAsString();
		
		ID = jsonObject.get(ID_KEY).getAsString();
	}
	
	public static Game createNewGame(String[] userTokens, String whoStarts, int startValue){
		if(userTokens.length != 2){
			throw new IllegalArgumentException("Bad length");
		}
		
		if(! userTokens[0].equals(whoStarts) && ! userTokens[1].equals(whoStarts)){
			throw new IllegalArgumentException("User who stars is not in usersArray");
		}
		
		JsonObject newGameJSON = new JsonObject();
		
		JsonArray usersList = new JsonArray();
		
		usersList.add(userTokens[0]);
		usersList.add(userTokens[1]);
		
		newGameJSON.add(USERS_LIST_KEY, usersList);
		newGameJSON.addProperty(TURN_KEY, whoStarts);
		newGameJSON.addProperty(CURRENT_AMOUNT_KEY, startValue);
		newGameJSON.addProperty(IS_OVER_KEY, false);
		newGameJSON.addProperty(CREATE_DATE_KEY, Logger.getDate());
		
		String generatedGameID = generateToken(whoStarts + Integer.toString(startValue) + userTokens[0]);
		newGameJSON.addProperty(ID_KEY, generatedGameID);
		
		Game newGame = new Game(newGameJSON);
		return newGame;
	}
	
	public static boolean hasGameWithID(String id) throws IOException{
		JsonObject gamesFile = FileHandling.getContentOfFileAsJSON(FileHandling.File.Games);
		
		JsonArray idsArray = gamesFile.getAsJsonArray(GAME_IDS_LIST_KEY);
		JsonObject gamesObject = gamesFile.getAsJsonObject(GAMES_LIST_KEY);
		
		JsonElement idString = JSON.parseStringToJSONElement(id);
		
		return idsArray.contains(idString) && gamesObject.has(id);
	}
	
	public boolean equals(Game otherGame) {
		return ID.equals(otherGame.getID());
	}
}
