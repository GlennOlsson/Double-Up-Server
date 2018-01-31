import APNs.API.Notification.Constants;
import Backend.FileHandling;
import Spark.HTTPListener;
import com.google.gson.JsonObject;

import java.io.IOException;

import static Backend.JSON.*;

public class Main {
	private static String certPassword;
	private static String certPath;
	private static String testToken;
	private static String appBundle;
	
	public static void main(String[] args) throws IOException{
		initialise();
		
		Constants.CERT_PASS = certPassword;
		Constants.CERT_PATH = certPath;
		Constants.APP_BUNDLE = appBundle;
		
	    new HTTPListener();
	}
	
	public static void initialise() throws IOException{
		JsonObject json = FileHandling.getContentOfFileAsJSON(FileHandling.File.Secret);
		
		certPassword = json.get(CERT_PASS_KEY).getAsString();
		certPath = json.get(CERT_PATH_KEY).getAsString();
		testToken = json.get(TEST_TOKEN_KEY).getAsString();
		appBundle = json.get(APP_BUNDLE_KEY).getAsString();
	}
}
