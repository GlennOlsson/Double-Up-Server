package Server.Spark;

import Server.Backend.JSON;
import Server.Backend.Logger;
import Server.Constants;
import Server.Game.Models.User;
import Server.Game.Models.UsersFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;

import java.io.IOException;

import static Server.Backend.JSON.*;

import static Server.Game.Models.User.*;

public class UserRequest {
	
	public static Response createNewUser(Request request, Response response){
		try{
			JsonObject requestJSON = JSON.parseStringToJSON(request.body());
			
			String suppliedUsername = requestJSON.get(USERNAME_KEY).getAsString();
			
			if(Constants.USERS_FILE.doesUserExistWithUsername(suppliedUsername)){
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			String suppliedNotificationToken = null;
			if(requestJSON.has(NOTIFICATION_TOKEN_KEY) && ! requestJSON.get(NOTIFICATION_TOKEN_KEY).isJsonNull()){
				suppliedNotificationToken = requestJSON.get(NOTIFICATION_TOKEN_KEY).getAsString();
			}
			
			User newUser = User.createNewUser(suppliedUsername, suppliedNotificationToken);
			
			UsersFile usersFile = Constants.USERS_FILE;
			usersFile.addUser(newUser);
			usersFile.save();
			
			JsonObject responseJSON = new JsonObject();
			responseJSON.addProperty("UserToken", newUser.getUserToken());
			response.body(responseJSON.toString());
			response.status(200);
			
		}
		catch (IOException e){
			e.printStackTrace();
			response.status(501);
			response.body(Integer.toString(response.status()));
		}
		catch (Exception e){
			e.printStackTrace();
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		return response;
	}
	
	public static Response changeUsername(Request request, Response response){
		
		try{
			JsonObject requestJSON = JSON.parseStringToJSON(request.body());
			
			String suppliedToken = requestJSON.get(TOKEN_KEY).getAsString();
			String newUsername = requestJSON.get(USERNAME_KEY).getAsString();
			
			Logger.print("User with token: " + suppliedToken + " tried to change to username " + newUsername);
			
			User userWithUsername = Constants.USERS_FILE.getUserWithUsername(newUsername);
			if(userWithUsername != null){
				if(! userWithUsername.getUserToken().equals(suppliedToken)) {
					Logger.print("Not allowed!");
					response.status(401);
					response.body(Integer.toString(response.status()));
					return response;
				}
				//Else is same user, can set new username as it might change case
			}
			
			User thisUser = Constants.USERS_FILE.getUser(suppliedToken);
			thisUser.setUsername(newUsername);
			
			UsersFile usersFile = Constants.USERS_FILE;
			usersFile.addUser(thisUser);
			usersFile.save();
			
			response.status(200);
			response.body(Integer.toString(response.status()));
		}
		catch (IOException e){
			e.printStackTrace();
			response.status(501);
			response.body(Integer.toString(response.status()));
		}
		catch (Exception e){
			e.printStackTrace();
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		return response;
	}
	
	public static Response newStart(Request request, Response response){
		try{
			JsonObject requestJSON = JSON.parseStringToJSON(request.body());
			
			String userToken = requestJSON.get(TOKEN_KEY).getAsString();
			
			User thisUser =  Constants.USERS_FILE.getUser(userToken);
			
			JsonElement notificationToken = requestJSON.get(NOTIFICATION_TOKEN_KEY);
			
			response.status(201);
			if(notificationToken != null && !notificationToken.isJsonNull()) {
				thisUser.setNotificationToken(notificationToken.getAsString());
				response.status(200);
			}
			
			JsonElement appVersion = requestJSON.get(VERSION_KEY);
			
			if(appVersion != null && !appVersion.isJsonNull()){
				thisUser.setAppVersion(appVersion.getAsString());
			}
			
			JsonObject responseObject = new JsonObject();
			
			//If new token is set, it is provided and the field is removed from the user
			String newToken = thisUser.getNewToken();
			if(newToken != null){
				Logger.print("New forced token! token: " + newToken);
				responseObject.addProperty(TOKEN_KEY, newToken);
				thisUser.setNewToken(null);
			}
			
			thisUser.newStart();
			
			UsersFile usersFile = Constants.USERS_FILE;
			
			usersFile.addUser(thisUser);
			usersFile.save();
			
			
			
			response.body(JSON.beautifyJSON(responseObject));
		}
		catch (IOException e){
			e.printStackTrace();
			response.status(501);
			response.body(Integer.toString(response.status()));
		}
		catch (Exception e){
			e.printStackTrace();
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		
		return response;
	}
	
	public static Response getUserInfo(Request request, Response response){
		try{
			String userToken = request.params(":token");
			if(! Constants.USERS_FILE.doesUserExistWithToken(userToken)){
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			User thisUser =  Constants.USERS_FILE.getUser(userToken);
			
			JsonObject responseObject = new JsonObject();
			responseObject.addProperty(USERNAME_KEY, thisUser.getUsername());
			responseObject.addProperty(BANK_KEY, thisUser.getBankAmount());
			
			response.status(200);
			response.body(JSON.beautifyJSON(responseObject));
			
		}
		catch (Exception e){
			e.printStackTrace();
			response.status(500);
			response.body(Integer.toString(response.status()));
		}
		
		return response;
	}
	
}
