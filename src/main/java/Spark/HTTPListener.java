package Spark;

import Backend.FileHandling;
import Backend.JSON;
import Backend.Logger;
import Game.Models.User;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

import static spark.Spark.*;
import static Backend.JSON.*;

public class HTTPListener {
	
	public HTTPListener(){
		port(8282);
		
		/*
		{
			"Username": "USERNAME",
			"NotificationToken": "TOKEN" //Can be absent or null
		}
		 */
		//Create new user
		post("/newUser", (request, response) -> {
			Logger.print("/newUser : " + request.body());
			response = UserRequest.createNewUser(request, response);
			
			return response.body();
		});
		
		/*
		{
			"Token": "TOKEN",
			"RequestedOpponent": "USERNAME" //If none, a random user will be assigned
			"StartAmount": AMOUNT
		}
		 */
		//New game
		post("/newGame", (request, response) -> {
			Logger.print("/newGame : " + request.body());
			response = GameRequest.createNewGame(request, response);
			
			return response.body();
		});
		
		/*
		{
			"Token": "TOKEN",
			"Username": "USERNAME"
		}
		 */
		//Change username
		post("/changeUsername", (request, response) -> {
			Logger.print("/changeUsername : " + request.body());
			response = UserRequest.changeUsername(request, response);
			
			return response.body();
		});
		
		
		/*
		//Many required fields to validate request
		{
			"GameID": "ID",
			"Token": "TOKEN",
			"DidDouble": true/false,
			"CurrentAmount": AMOUNT
		}
		 */
		//Play on a game
		post("/playGame", (request, response) -> {
			Logger.print("/playGame : " + request.body());
			response = GameRequest.playOnGame(request, response);
			
			Logger.print("Returning: " + response.body());
			return response.body();
		});
		
		/*
		{
			"Token": "TOKEN",
			"NotificationToken": "TOKEN" //Can be null or absent, if none is given (by notifications not accepted)
		}
		 */
		//New start, will send notification token. If none before, it will be kept
		post("/newStartup", (request, response) -> {
			Logger.print("/newStartup : " + request.body());
			response = UserRequest.newStart(request, response);
			
			return response.body();
		});
		
		//Return info on game
		get("/gameInfo/:gameID", ((request, response) -> {
			Logger.print("/gameInfo/:gameID : " + request.params());
			response = GameRequest.getGameBoardInfo(response, request);
			
			return response.body();
		}));
		
		
		//Return info on all games of user
		get("/games/:token", (request, response) -> {
			Logger.print("/games/:token : " + request.params());
			response = GameRequest.getBoardsOfUser(response, request);
			
			return response.body();
		});
		
		//Return info on a user
		get("/userInfo/:token", ((request, response) -> {
			Logger.print("/userInfo/:token : " + request.params());
			response = UserRequest.getUserInfo(request, response);
			
			return response.body();
		}));
		
		/*
		{
			"Token": "TOKEN",
			"Message: "MESSAGE"
		}
		 */
		//Message for the developer
		post("/message", (request, response) -> {
			Logger.print("/message : " + request.body());
			JsonObject requestJSON = JSON.parseStringToJSON(request.body());
			//TODO
			
			return "";
		});
		
		
		
		
	}
}
