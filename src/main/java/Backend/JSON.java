package Backend;

import com.google.gson.*;

public class JSON {
	
	//Users.json
	public static final String USERNAME_KEY = "Username";
	public static final String BANK_KEY = "BankAmount";
	public static final String GAMES_LIST_KEY = "Games";
	public static final String NOTIFICATION_TOKEN_KEY = "NotificationToken";
	public static final String LAST_LOGIN_DATE_KEY = "LastLogin";
	public static final String AMOUNT_OF_STARTS_KEY = "StartedCount";
	
	//Games.json
	public static final String GAMES_KEY = "Games";
	public static final String USERS_LIST_KEY = "Users";
	public static final String TURN_KEY = "Turn";
	public static final String CURRENT_AMOUNT_KEY = "CurrentAmount";
	public static final String IS_OVER_KEY = "IsOver";
	public static final String CREATE_DATE_KEY = "CreateDate";
	
	//Secret.json
	public static final String CERT_PATH_KEY = "cert-path";
	public static final String CERT_PASS_KEY = "cert-pass";
	public static final String APP_BUNDLE_KEY = "app-bundle";
	public static final String TEST_TOKEN_KEY = "test-token";
	public static final String MAIN_URL_KEY = "main-url";
	
	//Other
	public static final String TOKEN_KEY = "Token";
	public static final String ID_KEY = "ID";
	public static final String REQUESTED_USER_KEY = "RequestedOpponent";
	public static final String START_AMOUNT_KEY = "StartAmount";
	public static final String GAME_ID_KEY = "GameID";
	public static final String OPPONENT_USERNAME_KEY = "OpponentUsername";
	public static final String DID_DOUBLE_KEY = "DidDouble";
	
	/**
	 * Parses an input string as a JSON object
	 * @param theString the json to parse
	 * @return the object of the string
	 */
	public static JsonObject parseStringToJSON(String theString) throws JsonSyntaxException {
		return parseStringToJSONElement(theString).getAsJsonObject();
	}
	
	public static JsonElement parseStringToJSONElement(String theString) throws JsonSyntaxException {
		JsonElement jsonElement = new JsonParser().parse(theString.trim());
		
		return jsonElement;
	}
	
	public static String beautifyJSON(JsonElement json){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		return gson.toJson(json);
	}
	
}
