package Server.Game.Models;

import Server.Backend.JSON;
import Server.Backend.Logger;
import Server.Constants;
import Server.Exceptions.NoSuchUserException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static Server.Backend.JSON.*;
import static Server.Game.Models.Token.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class Game implements Comparable<Game>{
	
	private ArrayList<User> usersList;
	
	private JsonArray usersArray;
	private String turn;
	private int currentAmount;
	private boolean isOver;
	private String createDate;
	
	private String lastPlay;
	
	private String ID;
	
	
	public Game(JsonObject jsonObject){
		assignFieldsFromJSON(jsonObject);
	}
	
	private Game(){
		//Not accessible
	}
	
	public void newPlay(){
		lastPlay = Logger.getDate();
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
	
	public Date getLastPlay() {
		Date lastPlayDate = null;
		try{
			lastPlayDate = Logger.parseDate(lastPlay);
		}
		catch (Exception e){
			Logger.logError(e, "Error in getLastPLay()", "Error with parsing date, returning null");
		}
		return lastPlayDate;
	}
	
	public void setOver(boolean over) {
		isOver = over;
	}
	
	public String getID() {
		return ID;
	}
	
	public ArrayList<User> getUsersList() {
		usersList = new ArrayList<>();
		
		for(JsonElement jsonElement : usersArray){
			String userToken = jsonElement.getAsString();
			
			try{
				usersList.add(Constants.USERS_FILE.getUser(userToken));
			}
			catch (NoSuchUserException e){
				System.out.println("No user with token " + userToken + " but no error thrown");
			}
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
		gameJSON.addProperty(LAST_PLAY_KEY, lastPlay);
		
		return gameJSON;
	}
	
	private void assignFieldsFromJSON(JsonObject jsonObject) throws NullPointerException{
		usersArray = jsonObject.getAsJsonArray(USERS_LIST_KEY);
		
		turn = jsonObject.get(TURN_KEY).getAsString();
		currentAmount = jsonObject.get(CURRENT_AMOUNT_KEY).getAsInt();
		isOver = jsonObject.get(IS_OVER_KEY).getAsBoolean();
		createDate = jsonObject.get(CREATE_DATE_KEY).getAsString();
		
		lastPlay = jsonObject.get(LAST_PLAY_KEY).getAsString();
		
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
		
		newGameJSON.addProperty(LAST_PLAY_KEY, Logger.getDate());
		
		String generatedGameID = generateToken(whoStarts + Integer.toString(startValue) + userTokens[0]);
		newGameJSON.addProperty(ID_KEY, generatedGameID);
		
		Game newGame = new Game(newGameJSON);
		return newGame;
	}
	
	public boolean equals(Game otherGame) {
		return ID.equals(otherGame.getID());
	}
	
	@Override
	public int compareTo(Game o){
		return getLastPlay().compareTo(o.getLastPlay());
	}
}
