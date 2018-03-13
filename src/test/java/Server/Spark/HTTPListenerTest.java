package Server.Spark;

import Server.Backend.FileHandling;
import Server.Backend.JSON;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import static org.junit.Assert.*;

public class HTTPListenerTest {
	
	private String mainURL;
	
	@Before
	public void setUp() throws IOException{
		JsonObject object = FileHandling.getContentOfFileAsJSON(FileHandling.File.Secret);
//		mainURL = object.get(JSON.MAIN_URL_KEY).getAsString();
		mainURL = "http://localhost:8282";
	}
	
	@Test
	public void newUserCreatesUserAndEditsFileNoNotificationToken(){
		String randomUsername = "Test" + new Random().nextInt(10000);

		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.USERNAME_KEY, randomUsername);


		assertEquals(200, POST("/newUser", putObject));

	}

	@Test
	public void newUserCreatesUserAndEditsFileWithNotificationToken(){
		String randomUsername = "Test" + new Random().nextInt(10000);
		String notificationToken = "SOME-RANDOM-TOKEN";

		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.USERNAME_KEY, randomUsername);
		putObject.addProperty(JSON.NOTIFICATION_TOKEN_KEY, notificationToken);

		assertEquals(200, POST("/newUser", putObject));

	}

	@Test
	public void newUserWithOldUsernameReturns401(){
		String username = "Test2";

		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.USERNAME_KEY, username);

		assertEquals(401, POST("/newUser", putObject));
	}

	@Test
	public void newGameWorksWithSetUser(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
		putObject.addProperty(JSON.REQUESTED_USER_KEY, "Glenn");
		putObject.addProperty(JSON.START_AMOUNT_KEY, 20);

		assertEquals(200, POST("/newGame", putObject));
	}

//	@Test
//	public void newGameWorksWithRandomUser(){
//		JsonObject putObject = new JsonObject();
//		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
//		putObject.addProperty(JSON.START_AMOUNT_KEY, 40);
//
//		assertEquals(200, POST("/newGame", putObject));
//	}
	
	@Test
	public void playOnGameWorksBackAndForth(){
		
		long nano = System.nanoTime();
		
		String liveGameID = "1uqA7SS2zGJIS2n0B51D6q821HYnMxEmvL9Rww7eLj";

		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "GLENN-TOKEN");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, true);
		putObject.addProperty(JSON.GAME_ID_KEY, liveGameID);
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 40);

		assertEquals(200, POST("/playGame", putObject));

		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, true);
		putObject.addProperty(JSON.GAME_ID_KEY, liveGameID);
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 80);

		assertEquals(200, POST("/playGame", putObject));

		putObject.addProperty(JSON.TOKEN_KEY, "GLENN-TOKEN");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, false);
		putObject.addProperty(JSON.GAME_ID_KEY, liveGameID);
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 80);
		
		System.out.println("Nanotime: " + (System.nanoTime() - nano));
		
		assertEquals(200, POST("/playGame", putObject));
	}

	@Test
	public void playOnGameForNonExistingIDReturns401(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "GLENN-TOKEN");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, true);
		putObject.addProperty(JSON.GAME_ID_KEY, "BAD_GAME_ID");
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 40);

		assertEquals(401, POST("/playGame", putObject));
	}

	@Test
	public void playOnGameForTokenNotInGameReturns402(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "GLENN-TOKEN");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, true);
		putObject.addProperty(JSON.GAME_ID_KEY, "GameID1");
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 40);

		assertEquals(402, POST("/playGame", putObject));
	}

	@Test
	public void playOnGameNotUserOfTokensTurnReturns403(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, true);
		putObject.addProperty(JSON.GAME_ID_KEY, "GameID1");
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 40);

		assertEquals(403, POST("/playGame", putObject));
	}

	@Test
	public void playOnGameNotDoubledReturns405(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN2");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, true);
		putObject.addProperty(JSON.GAME_ID_KEY, "GameID1");
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 0);

		assertEquals(405, POST("/playGame", putObject));
	}

	@Test
	public void playOnGameOnGameThatIsAlreadyOverReturns406(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
		putObject.addProperty(JSON.DID_DOUBLE_KEY, true);
		putObject.addProperty(JSON.GAME_ID_KEY, "PVW6UkF1s51D6qO");
		putObject.addProperty(JSON.CURRENT_AMOUNT_KEY, 40);

		assertEquals(406, POST("/playGame", putObject));
	}


	@Test
	public void changeUsernameToNewNameReturns200(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
		putObject.addProperty(JSON.USERNAME_KEY, "Test" + new Random().nextInt(9999));

		assertEquals(200, POST("/changeUsername", putObject));
	}

	@Test
	public void changeUsernameToOldNameReturns401(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
		putObject.addProperty(JSON.USERNAME_KEY, "Glenn");

		assertEquals(401, POST("/changeUsername", putObject));
	}

	@Test
	public void newStartupReturns200IfTokenIsProvided(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");
		putObject.addProperty(JSON.NOTIFICATION_TOKEN_KEY, "NOTI-TOKEN");

		assertEquals(200, POST("/newStartup", putObject));
	}


	@Test
	public void newStartupReturns201IfTokenIsNotProvidedOrEqualsNull(){
		JsonObject putObject = new JsonObject();
		putObject.addProperty(JSON.TOKEN_KEY, "TOKEN1");

		assertEquals(201, POST("/newStartup", putObject));

		putObject.add(JSON.NOTIFICATION_TOKEN_KEY, null);

		assertEquals(201, POST("/newStartup", putObject));
	}
	
	
	public int POST(String URL, JsonObject json) {
		try {
			//POST
			String url = mainURL + URL;
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);
			
			
			// add header
			post.addHeader("Accept","application/json");
			
			
			StringEntity jsonRequest = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
			post.setEntity(jsonRequest);
			
			HttpResponse response = client.execute(post);
			
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));
			
			StringBuffer resultString = new StringBuffer();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				resultString.append(line);
			}
			
			return response.getStatusLine().getStatusCode();
			
		}
		catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
}