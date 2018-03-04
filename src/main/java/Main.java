import APNs.API.Exception.CertNotSetException;
import APNs.API.Notification.Constants;
import APNs.API.Notification.Notification;
import APNs.API.Notification.NotificationClient;
import Backend.FileHandling;
import Backend.Logger;
import Spark.HTTPListener;
import com.google.gson.JsonObject;
import com.sun.tools.corba.se.idl.constExpr.Not;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
		
		Logger.print("      Starting - " + Logger.getDate());
		
		new HTTPListener();
		
		//Trying to send notification to developer that app server has started
		try{
			NotificationClient client = new NotificationClient();
			Notification notification = new Notification(testToken);
			notification.setTitle("Server is up and running again!");
			notification.setBody("The reset was a success");
			
			client.sendPushNotification(notification);
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
