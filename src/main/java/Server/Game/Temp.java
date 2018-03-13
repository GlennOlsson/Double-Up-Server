package Server.Game;

//import static Server.Backend.FileHandling.USERS_FILE;
import static Server.Backend.FileHandling.saveToFile;

public class Temp {
	
	/*
	
	public static Response startUpdate(String token, Response response){
		try {
			if(token.length() == 0) {
				response.status(402);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			String contentOfFile = FileHandling.getContentOfFile(USERS_FILE);
			JsonObject object = JSON.parseStringToJSON(contentOfFile);
			JsonArray usersArray = object.get("users").getAsJsonArray();
			JsonObject userOfToken = new JsonObject();
			
			for(JsonElement jsonElement : usersArray){
				JsonObject thisObject = jsonElement.getAsJsonObject();
				String thisToken = thisObject.get("token").getAsString();
				if(thisToken.equals(token)){
					userOfToken = thisObject;
					break;
				}
			}
			
			if(userOfToken.toString().equals("{}")){
				//No user with token found
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			//Else
			
			int startCount = userOfToken.get("startCounter").getAsInt();
			startCount++;
			userOfToken.addProperty("startCounter", startCount);
			
			String beautyJSON = beautifyJSON(object);
			
			saveToFile(beautyJSON, USERS_FILE);
			
			response.status(200);
			response.body(Highscore.getHighscore(token).toString());
			
			return response;
			
		}
		catch (IOException e){
			response.status(555);
			response.body(Integer.toString(response.status()));
			response.body(Integer.toString(response.status()));
			Logger.logError(e, "Error in startUpdate()", "IOException");
			return response;
		}
		catch (Exception e){
			response.status(550);
			response.body(Integer.toString(response.status()));
			Logger.logError(e, "Error in startUpdate()", "General exception");
			return response;
		}
	}
	
	public static Response updateScore(String token, int score, Response response){
		try{
			if(token.length() == 0 || score < 0){
				response.status(402);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			String contentOfFile = FileHandling.getContentOfFile(USERS_FILE);
			JsonObject object = JSON.parseStringToJSON(contentOfFile);
			JsonArray usersArray = object.get("users").getAsJsonArray();
			JsonObject userOfToken = new JsonObject();
			
			for(JsonElement jsonElement : usersArray){
				JsonObject thisObject = jsonElement.getAsJsonObject();
				String thisToken = thisObject.get("token").getAsString();
				if(thisToken.equals(token)){
					userOfToken = thisObject;
					break;
				}
			}
			
			if(userOfToken.toString().equals("{}")){
				//No user with token found
				response.status(401);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			Logger.print("User of token: " + userOfToken.toString());
			
			int scoreOfUser = userOfToken.get("score").getAsInt();
			
			if(score <= scoreOfUser){
				response.status(201);
			}
			else{
				response.status(200);
			}
			//Else
			userOfToken.addProperty("score", score);
			
			String beautyJSON = beautifyJSON(object);
			
			saveToFile(beautyJSON, USERS_FILE);
			
			response.body(Highscore.getHighscore(token).toString());
			
			return response;
		}
		catch (IOException e){
			response.status(555);
			response.body(Integer.toString(response.status()));
			Logger.logError(e, "Error in updateScore()", "IOException");
			return response;
		}
		catch (Exception e){
			response.status(550);
			response.body(Integer.toString(response.status()));
			
			Logger.logError(e, "Error in updateScore()", "General exception");
			return response;
		}
	}
	
	public static Response createUser(String name, Response response){
		try{
			
			if(name.length() == 0){
				response.status(402);
				response.body(Integer.toString(response.status()));
				return response;
			}
			
			String contentOfFile = FileHandling.getContentOfFile(USERS_FILE);
			JsonObject object = JSON.parseStringToJSON(contentOfFile);
			JsonArray usersArray = object.get("users").getAsJsonArray();
			
			for(JsonElement jsonElement : usersArray){
				JsonObject thisObject = jsonElement.getAsJsonObject();
				String thisName = thisObject.get("name").getAsString();
				if(thisName.equals(name)){
					//Already exists a user with that name, the name must be unique
					response.status(401);
					response.body("401");
					return response;
				}
			}
			//Username is unique
			String token = generateToken(name);
			
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String currentDate = simpleDateFormat.format(calendar.getTime());
			
			int startCounter = 0;
			int points = 0;
			
			JsonObject newUserObject = new JsonObject();
			newUserObject.addProperty("name", name);
			newUserObject.addProperty("token", token);
			newUserObject.addProperty("createDate", currentDate);
			newUserObject.addProperty("startCounter", startCounter);
			newUserObject.addProperty("score", points);
			
			usersArray.add(newUserObject);
			
			String beautyJSON = beautifyJSON(object);
			
			saveToFile(beautyJSON, USERS_FILE);
			
			response.status(200);
			
			JsonObject responseJSON = new JsonObject();
			responseJSON.addProperty("token", token);
			
			response.body(responseJSON.toString());
			
			return response;
		}
		catch (IOException e){
			response.status(555);
			response.body(Integer.toString(response.status()));
			response.body(Integer.toString(response.status()));
			Logger.logError(e, "Error in createUser()", "IOException");
			return response;
		}
		catch (Exception e){
			response.status(550);
			response.body(Integer.toString(response.status()));
			Logger.logError(e, "Error in createUser()", "General exception");
			return response;
		}
	}
	
	private static String generateToken(String name){
		StringBuilder token = new StringBuilder();
		long currentMillis = System.currentTimeMillis();
		//Removing everything but the last 5 characters, as they are almost random
		String millis = Long.toString(currentMillis).substring(8);
		
		for (int i = 0; i < 5; i++) {
			int randomNumber = new Random().nextInt(999);
			randomNumber %= 88;
			randomNumber += 35;
			
			token.append((char) randomNumber);
		}
		
		if(name.length() < 10){
			name += "ABCDEFGHIJ";
		}
		if(name.length() > 10){
			name = name.substring(0, 10);
		}
		
		StringBuilder nameCharCodes = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			Character thisCharacter = name.charAt(i);
			nameCharCodes.append((int) thisCharacter);
		}
		
		String beforeConversion = millis + nameCharCodes;
		
		//Char codes between 35 and 123 are going to be used. Therefor,
		//taking 3 and 2 digits of the text, using modulo with 88 (123-35)
		//then adding 35, and finally converting those to characters will make an almost
		//unique token
		
		//The length / 3 will give the amount of times it can divide by three (obv), and then
		//the final loop should use the remaining chars
		int lengthMod3 = beforeConversion.length() / 3;
		
		for (int i = 0; i < lengthMod3; i++) {
			String first3 = beforeConversion.substring(0, 3);
			int first3AsInt = Integer.parseInt(first3);
			first3AsInt %= 88;
			first3AsInt += 35;
			
			beforeConversion = beforeConversion.substring(3);
			
			token.append((char) first3AsInt);
		}
		if(beforeConversion.length() > 0) {
			int finalPart = Integer.parseInt(beforeConversion);
			finalPart %= 88;
			finalPart += 35;
			
			token.append((char) finalPart);
		}
		
		return token.toString();
	}
	*/
}
