package Spark;

import Backend.FileHandling;
import Backend.JSON;
import Backend.Logger;
import Game.Models.User;
import Game.Models.UsersFile;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;

import java.io.IOException;

import static Backend.JSON.*;

import static Game.Models.User.*;

public class UserRequest {
	
	public static Response createNewUser(Request request, Response response){
		try{
			JsonObject requestJSON = JSON.parseStringToJSON(request.body());
			
			String suppliedUsername = requestJSON.get(USERNAME_KEY).getAsString();
			
			if(doesUserExistWithUsername(suppliedUsername)){
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			String suppliedNotificationToken = null;
			if(requestJSON.has(NOTIFICATION_TOKEN_KEY) && ! requestJSON.get(NOTIFICATION_TOKEN_KEY).isJsonNull()){
				suppliedNotificationToken = requestJSON.get(NOTIFICATION_TOKEN_KEY).getAsString();
			}
			
			User newUser = User.createNewUser(suppliedUsername, suppliedNotificationToken);
			
			UsersFile.addUser(newUser);
			
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
			
			User userWithUsername = User.getUserWithUsername(newUsername);
			if(userWithUsername != null){
				if(! userWithUsername.getUserToken().equals(suppliedToken)) {
					Logger.print("Not allowed!");
					response.status(401);
					response.body(Integer.toString(response.status()));
					return response;
				}
				//Else is same user, can set new username as it might change case
			}
			
			User thisUser = new User(suppliedToken);
			thisUser.setUsername(newUsername);
			
			UsersFile.addUser(thisUser);
			
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
			
			if(requestJSON.has(NOTIFICATION_TOKEN_KEY) && ! requestJSON.get(NOTIFICATION_TOKEN_KEY).isJsonNull()){
				String notificationToken = requestJSON.get(NOTIFICATION_TOKEN_KEY).getAsString();
				String userToken = requestJSON.get(TOKEN_KEY).getAsString();
				
				User thisUser = new User(userToken);
				
				thisUser.setNotificationToken(notificationToken);
				
				UsersFile.addUser(thisUser);
				
				response.status(200);
				response.body(Integer.toString(response.status()));
			}
			else{
				response.status(201);
				response.body(Integer.toString(response.status()));
			}
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
}
