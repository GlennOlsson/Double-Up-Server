package Server.Spark;

import APNs.API.Exception.CertNotSetException;
import Server.Constants;
import APNs.API.Notification.Notification;
import Server.Backend.FileHandling;
import Server.Backend.JSON;
import Server.Backend.Logger;
import Server.Exceptions.NoSuchGameException;
import Server.Exceptions.NoSuchUserException;
import Server.Game.Models.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static Server.Backend.JSON.*;


public class GameRequest {
	public static Response createNewGame(Request request, Response response){
		try{
			JsonObject requestJSON = JSON.parseStringToJSON(request.body());
			
			String token = requestJSON.get(TOKEN_KEY).getAsString();
			
			User thisUser = Constants.USERS_FILE.getUser(token);
			
			User opponentUser = null;
			if(requestJSON.has(REQUESTED_USER_KEY) && ! requestJSON.get(REQUESTED_USER_KEY).isJsonNull()){
				String requestedOpponentUsername = requestJSON.get(REQUESTED_USER_KEY).getAsString();
				
				opponentUser = Constants.USERS_FILE.getUserWithUsername(requestedOpponentUsername);
				
				if(opponentUser == null){
					response.status(401);
					response.body(Integer.toString(response.status()));
					
					return response;
				}
			}
			
			if(opponentUser == null){
				//No user supplied, will be assigned a random user
				
				//So it does not go infinite
				int numberOfLoops = 0;
				
				//Setting to requestUser, as the loop will check while it is equal to that username
				String opponentUsername = thisUser.getUsername();
				while(opponentUsername.toLowerCase().equals(thisUser.getUsername().toLowerCase()) ||
						opponentUsername.toLowerCase().equals("test1") || //The test users
						opponentUsername.toLowerCase().equals("test2")){
					opponentUsername = getRandomUser().getUsername();
					numberOfLoops++;
					
					if(numberOfLoops > 75){
						//The chance that 75 random users returns one of the 3 above is near none, if there are more users
						response.status(402);
						response.body(Integer.toString(response.status()));
					}
				}
				//We now have a opponent username for sure
				opponentUser = Constants.USERS_FILE.getUserWithUsername(opponentUsername);
			}
			
			int startAmount = requestJSON.get(START_AMOUNT_KEY).getAsInt();
			
			String[] userTokens = new String[]{thisUser.getUserToken(), opponentUser.getUserToken()};
			
			Game thisGame = Game.createNewGame(userTokens, opponentUser.getUserToken(), startAmount);
			
			GamesFile gamesFile = Constants.GAMES_FILE;
			gamesFile.addGame(thisGame);
			gamesFile.save();
			
			UsersFile usersFile = Constants.USERS_FILE;
			
			thisUser.addGame(thisGame.getID());
			thisUser.addToBankAmount(-startAmount);
			usersFile.addUser(thisUser);
			
			opponentUser.addGame(thisGame.getID());
			usersFile.addUser(opponentUser);
			
			usersFile.save();
			
			JsonObject responseJSON = new JsonObject();
			responseJSON.addProperty(GAME_ID_KEY, thisGame.getID());
			responseJSON.addProperty(OPPONENT_USERNAME_KEY, opponentUser.getUsername());
			
			String notificationString = thisUser.getUsername() + " has sent you " + startAmount + "!";
			sendNotification(opponentUser, notificationString);
			
			response.status(200);
			response.body(responseJSON.toString());
			
			return response;
			
		}
		catch (IOException e){
			Logger.logError(e, "IOException in createNewGame", "/newGame");
			response.status(501);
			response.body(Integer.toString(response.status()));
		}
		catch (Exception e){
			Logger.logError(e, "General exception in createNewGame", "/newGame");
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		return response;
	}
	
	public static Response playOnGame(Request request, Response response){
		try {
			JsonObject requestJSON = JSON.parseStringToJSON(request.body());
			
			String token = requestJSON.get(TOKEN_KEY).getAsString();
			String gameID = requestJSON.get(GAME_ID_KEY).getAsString();
			boolean didDouble = requestJSON.get(DID_DOUBLE_KEY).getAsBoolean();
			int currentAmount = requestJSON.get(CURRENT_AMOUNT_KEY).getAsInt();
			
			Game currentGame = null;
			try{
				currentGame = Constants.GAMES_FILE.getGame(gameID);
			}
			catch (NoSuchGameException e){
				Logger.print("No game with id: " + gameID + ", returning 401");
				
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			//Current game can't be null now
			ArrayList<User> usersInGame = currentGame.getUsersList();
			
			User thisUser = Constants.USERS_FILE.getUser(token);
			User otherUser = ! usersInGame.get(0).getUserToken().equals(token) ? usersInGame.get(0) : usersInGame.get(1);
			
			if(!usersInGame.get(0).getUserToken().equals(token) && !usersInGame.get(1).getUserToken().equals(token)) {
				//User of token is not in the game
				Logger.print("User with token: " + token + " tried to play on game they're not in with id: " + gameID);
				
				response.status(402);
				response.body(Integer.toString(response.status()));
				
				return response;
			}
			
			if(currentGame.isOver()){
				Logger.print("User with token " + token + " tried to play on game that's over, with id: " + gameID);
				
				response.status(406);
				response.body(Integer.toString(response.status()));
				
				return response;
			}
			
			String tokenOfTurn = currentGame.getTurn();
			
			if(!tokenOfTurn.equals(token)){
				Logger.print("User with token: " + token + " tried to play on game while not their turn id: " + gameID);
				
				response.status(403);
				response.body(Integer.toString(response.status()));
				
				return response;
			}
			
			currentGame.setOver(! didDouble);
			currentGame.newPlay();
			
			if(didDouble){
				if(currentGame.getCurrentAmount() * 2 != currentAmount) {
					response.status(405);
					response.body(Integer.toString(response.status()));
					
					return response;
				}
				if(thisUser.getBankAmount() < currentAmount){
					response.status(407);
					response.body(Integer.toString(response.status()));
					
					return response;
				}
				
				thisUser.addToBankAmount(-currentGame.getCurrentAmount());
				
				currentGame.setTurn(otherUser.getUserToken());
				currentGame.setCurrentAmount(currentAmount);
				
				String notificationString = thisUser.getUsername() + " has sent you " + currentAmount + "!";
				sendNotification(otherUser, notificationString);
				
				Constants.USERS_FILE.addUser(thisUser);
				Constants.GAMES_FILE.addGame(currentGame);
			}
			else {
				//Server.Game is over
				Constants.GAMES_FILE.addGame(currentGame);
				thisUser.addToBankAmount(currentGame.getCurrentAmount());
				
				String notificationString = thisUser.getUsername() + " has decided to keep the " + currentAmount + " bucks";
				sendNotification(otherUser, notificationString);
				
				Constants.USERS_FILE.addUser(thisUser);
			}
			
			Constants.USERS_FILE.save();
			Constants.GAMES_FILE.save();
			
			response.status(200);
			response.body(Integer.toString(response.status()));
		}
		catch (IOException e){
			Logger.logError(e, "IOException in playOnGame", "/playGame");
			response.status(501);
			response.body(Integer.toString(response.status()));
		}
		catch (Exception e){
			Logger.logError(e, "General exception in playOnGame", "/playGame");
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		
		return response;
	}
	
	public static Response getGameBoardInfo(Response response, Request request){
		try{
			String boardID = request.params(":gameID");
			
			if(!Constants.GAMES_FILE.gameWithIdExists(boardID)){
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			Game thisGame = Constants.GAMES_FILE.getGame(boardID);
			
			JsonObject gameAsObject = thisGame.asJson();
			JsonArray userTokensArray = gameAsObject.getAsJsonArray(USERS_LIST_KEY);
			
			User user1 = Constants.USERS_FILE.getUser(userTokensArray.get(0).getAsString());
			JsonElement user1Name = JSON.parseStringToJSONElement(user1.getUsername());
			
			User user2 = Constants.USERS_FILE.getUser(userTokensArray.get(1).getAsString());
			JsonElement user2Name = JSON.parseStringToJSONElement(user2.getUsername());
			
			userTokensArray.set(0, user1Name);
			userTokensArray.set(1, user2Name);
			
			String tokenOfTurn = gameAsObject.get(TURN_KEY).getAsString();
			
			User userOfTurn = tokenOfTurn.equals(user1.getUserToken()) ? user1 : user2;
			
			String usernameOfTurn = userOfTurn.getUsername();
			
			gameAsObject.addProperty(TURN_KEY, usernameOfTurn);
			
			gameAsObject.remove(CREATE_DATE_KEY);
			
			response.body(JSON.beautifyJSON(gameAsObject));
			response.status(200);
			
		}
		catch (Exception e){
			Logger.logError(e, "General exception in gameInfo", "/gameInfo");
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		return response;
	}
	
	public static Response getBoardsOfUser(Response response, Request request){
		try{
			String userToken = request.params(":token");
			
			if(! Constants.USERS_FILE.doesUserExistWithToken(userToken)){
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			User thisUser = Constants.USERS_FILE.getUser(userToken);
			
			ArrayList<Game> gamesList = thisUser.getGamesList();
			
			JsonArray gamesArray = new JsonArray();
			for(Game game : gamesList){
				JsonObject gameObject = new JsonObject();
				gameObject.addProperty(GAME_ID_KEY, game.getID());
				
				ArrayList<User> users = game.getUsersList();
				User otherUser = users.get(0).equals(thisUser) ? users.get(1) : users.get(0);
				gameObject.addProperty(OPPONENT_USERNAME_KEY, otherUser.getUsername());
				
				gameObject.addProperty(IS_OVER_KEY, game.isOver());
				gameObject.addProperty(CURRENT_AMOUNT_KEY, game.getCurrentAmount());
				gameObject.addProperty(TURN_KEY, game.getTurn().equals(thisUser.getUserToken()));
				
				gamesArray.add(gameObject);
			}
			
			response.body(JSON.beautifyJSON(gamesArray));
			response.status(200);
			
		}
		catch (Exception e){
			Logger.logError(e, "General exception in /games/:token", "/games/:token");
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		
		return response;
	}
	
	private static void sendNotification(User userToSend, String content)
			throws CertNotSetException, IOException, InterruptedException, ExecutionException{
		try{
			//Will try to send notification to opponent, if they have a notification token
			String notificationToken = userToSend.getNotificationToken();
			if(notificationToken != null && notificationToken.length() > 0){
				
				Notification newGameNotification = new Notification(notificationToken);
				newGameNotification.setBody(content);
				newGameNotification.setBadgeNumber(1);
				newGameNotification.setSoundPath("NotificationSound.m4a");
				
				boolean accepted = Constants.NOTIFICATION_CLIENT.sendPushNotification(newGameNotification);
				
				Logger.print("Sent notification to " + userToSend.getUsername() + " ? " + accepted);
			}
			else{
				Logger.print(userToSend.getUsername() + " has not accepted notifications");
			}
		}
		catch (Exception e){
			Logger.print("Could not send Notification");
			Logger.logError(e, "Trying to send notification", "To " + userToSend.getUsername() + " with content "
					 + content);
		}
	}
	
	private static User getRandomUser() throws IOException, NoSuchUserException{
		JsonObject userFileObject = FileHandling.getContentOfFileAsJSON(FileHandling.File.Users);
		JsonObject userObject = userFileObject.getAsJsonObject(USERS_LIST_KEY);
		
		Set<String> setOfTokens = userObject.keySet();
		int randomIndex = new Random().nextInt(setOfTokens.size());
		
		String[] arrayOfTokens = setOfTokens.toArray(new String[]{});
		
		String randomToken = arrayOfTokens[randomIndex];
		User thisUser = Constants.USERS_FILE.getUser(randomToken);
		
		if(thisUser.isTestUser()){
			return getRandomUser();
		}
		
		return thisUser;
	}
}














