import APNs.API.Exception.CertNotSetException;
import Server.Constants;
import APNs.API.Notification.Notification;
import APNs.API.Notification.NotificationClient;
import Server.Backend.FileHandling;
import Server.Backend.Logger;
import Server.Game.Models.GamesFile;
import Server.Game.Models.UsersFile;
import Server.Spark.HTTPListener;
import com.google.gson.JsonObject;

import java.io.IOException;

import static Server.Backend.JSON.*;

public class Main {
	private static String certPassword;
	private static String certPath;
	private static String testToken;
	private static String appBundle;
	
	public static void main(String[] args) throws IOException, CertNotSetException{
		initialise();
		
		Constants.CERT_PASS = certPassword;
		Constants.CERT_PATH = certPath;
		Constants.APP_BUNDLE = appBundle;
		
		Constants.GAMES_FILE = new GamesFile();
		Constants.USERS_FILE = new UsersFile();
		
		Logger.print("      Starting - " + Logger.getDate());
		
		new HTTPListener();
		
		try{
			Constants.NOTIFICATION_CLIENT = new NotificationClient();
		}
		catch (Exception e){
			Logger.logError(e, "Could not create a notification client", "General exception");
		}
		//Trying to send notification to developer that app server has started
		try{
			Notification notification = new Notification(testToken);
			notification.setTitle("Server is up and running again!");
			notification.setBody("The reset was a success");
			
			Constants.NOTIFICATION_CLIENT.sendPushNotification(notification);
		}
		catch (Exception e){
			Logger.logError(e, "Could not send notifiation to Dev", "Unknown reason");
		}
	}
	
	public static void initialise() throws IOException{
		JsonObject json = FileHandling.getContentOfFileAsJSON(FileHandling.File.Secret);
		
		certPassword = json.get(CERT_PASS_KEY).getAsString();
		certPath = json.get(CERT_PATH_KEY).getAsString();
		testToken = json.get(TEST_TOKEN_KEY).getAsString();
		appBundle = json.get(APP_BUNDLE_KEY).getAsString();
	}
}
