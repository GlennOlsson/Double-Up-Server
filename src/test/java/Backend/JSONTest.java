package Backend;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import static Backend.JSON.*;
import static Backend.FileHandling.*;

import static org.junit.Assert.*;

public class JSONTest {
	
	private JsonObject gamesFileObject;
	private JsonObject usersFileObject;
	private JsonObject secretFileObject;
	
	@Before
	public void setUp() throws Exception {
		gamesFileObject = getContentOfFileAsJSON(File.Games);
		usersFileObject = getContentOfFileAsJSON(File.Users);
		secretFileObject = getContentOfFileAsJSON(File.Secret);
	}
	
	@Test
	public void gameFileKeysWorks(){
		gamesFileObject.get(GAMES_KEY);
		gamesFileObject.get(USERS_LIST_KEY);
		gamesFileObject.get(TURN_KEY);
		gamesFileObject.get(CURRENT_AMOUNT_KEY);
		gamesFileObject.get(IS_OVER_KEY);
	}
	
	@Test
	public void userFileKeysWorks(){
		usersFileObject.get(USERNAME_KEY);
		usersFileObject.get(BANK_KEY);
		usersFileObject.get(GAMES_LIST_KEY);
		usersFileObject.get(NOTIFICATION_TOKEN_KEY);
	}
	
	@Test
	public void secretFileKeysWorks(){
		secretFileObject.get(CERT_PASS_KEY);
		secretFileObject.get(CERT_PATH_KEY);
		secretFileObject.get(APP_BUNDLE_KEY);
		secretFileObject.get(TEST_TOKEN_KEY);
	}
	
}