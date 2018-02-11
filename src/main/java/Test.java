import APNs.API.Notification.Constants;
import Backend.FileHandling;
import APNs.API.Notification.Notification;
import APNs.API.Notification.NotificationClient;
import Backend.Logger;
import Game.Models.Game;
import Game.Models.Token;
import Game.Models.User;
import Spark.HTTPListener;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import static Backend.JSON.*;
import static Game.Models.Token.*;

public class Test {
	
	private static String certPassword;
	private static String certPath;
	private static String testToken;
	private static String appBundle;
	
	public static void main(String[] args) throws Exception{
		
		JsonObject json = FileHandling.getContentOfFileAsJSON(FileHandling.File.Secret);
		
		certPassword = json.get(CERT_PASS_KEY).getAsString();
		certPath = json.get(CERT_PATH_KEY).getAsString();
		testToken = json.get(TEST_TOKEN_KEY).getAsString();
		appBundle = json.get(APP_BUNDLE_KEY).getAsString();
		
		Constants.CERT_PASS = certPassword;
		Constants.CERT_PATH = certPath;
		Constants.APP_BUNDLE = appBundle;
		
//		new HTTPListener();
		
		new Test();
	
	}
	
	public Test(String hey){
		try{
			NotificationClient client = new NotificationClient();
			
			Notification not1 = new Notification(testToken);
			for (int i = 0; i < 1; i++) {
				not1.setBody("Ur th g8est " + i);
				not1.setSoundPath("none.aiff");
				not1.setBadgeNumber(3);
				
				System.out.println(client.sendPushNotification(not1));
			}
			
			client.close();
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public Test() throws Exception{
		
		
		
		final ApnsClient apnsClient = new ApnsClientBuilder()
				.setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
				.setClientCredentials(new File(certPath), certPassword)
				.build();
		
		//Creating notification
		final SimpleApnsPushNotification pushNotification;
		{
			final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();

			payloadBuilder.setAlertBody("Hello, world! \\U270c");

			payloadBuilder.setAlertTitle("SUHH?!");

			payloadBuilder.setAlertSubtitle("Cunt");

			payloadBuilder.setSoundFileName("Popcorn.aiff");
			
			payloadBuilder.setActionButtonLabel("Hey");
			payloadBuilder.setShowActionButton(true);
			
			payloadBuilder.setBadgeNumber(3);
			
			final String payload = payloadBuilder.buildWithDefaultMaximumLength();
			final String token = TokenUtil.sanitizeTokenString(testToken);
			
			pushNotification = new SimpleApnsPushNotification(token, appBundle, payload);
		}
		
		//Sending
		final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
				sendNotificationFuture = apnsClient.sendNotification(pushNotification);
		
		//Handling transportation
		try {
			final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
					sendNotificationFuture.get();
			
			if (pushNotificationResponse.isAccepted()) {
				System.out.println("Push notification accepted by APNs gateway.");
				
				try{
					Thread.sleep(5000);
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				
				
			} else {
				System.out.println("APNs.API.Notification.APNs.API.Notification rejected by the APNs gateway: " +
						pushNotificationResponse.getRejectionReason());
				
				if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
					System.out.println("\t…and the token is invalid as of " +
							pushNotificationResponse.getTokenInvalidationTimestamp());
				}
			}
			
		} catch (final ExecutionException e) {
			System.err.println("Failed to send push notification.");
			e.printStackTrace();
		}
		
		apnsClient.close();
		
	}
	
}
