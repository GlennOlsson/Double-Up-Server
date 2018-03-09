import APNs.API.Notification.Constants;
import Backend.FileHandling;
import APNs.API.Notification.Notification;
import APNs.API.Notification.NotificationClient;
import Backend.JSON;
import Backend.Logger;
import Game.Models.Game;
import Game.Models.Token;
import Game.Models.User;
import Game.Models.UsersFile;
import Spark.GameRequest;
import Spark.HTTPListener;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;

import java.io.File;
import java.io.FileReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
		
		new HTTPListener();
		
//		UsersFile file = new UsersFile();
//
//		User user = new User("TOKEN1");
//		file.addUser(user);
//		file.save();
		
//		System.out.println(FileHandling.getContentOfFileAsJSON(FileHandling.File.Users));
		
		
		
//		String dateString = "08/03 - 2018 -- 15:51:22";
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM - YYYY -- HH:mm:ss", Locale.getDefault());
//		ParsePosition pp1 = new ParsePosition(0);
//		Date date = simpleDateFormat.parse(dateString, pp1);
//
//		Calendar oneWeekAgo = Calendar.getInstance();
//		oneWeekAgo.setTime(new Date());
//
//		oneWeekAgo.add(Calendar.DATE, -7);
//
//		System.out.println(simpleDateFormat.format(oneWeekAgo.getTime()));
//		System.out.println(simpleDateFormat.format(date));
//
//		System.out.println(oneWeekAgo.before(date));
		
	}
	
	public Test(String hey){
		try{
			NotificationClient client = new NotificationClient();
			
			Notification not1 = new Notification("D4CDBAE420D5D514C7043EB322FFE9B7910B6F6C8780966DC6B0C83E7654A3AF");
			for (int i = 0; i < 1; i++) {
				not1.setBody("Vad tycks om ljudet?");
				not1.setSoundPath("NotificationSound.m4a");
				not1.setBadgeNumber(69);
				
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
				.setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
				.setClientCredentials(new File(certPath), certPassword)
				.build();
		
		//Creating notification
		final SimpleApnsPushNotification pushNotification;
		{
			final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
			
			payloadBuilder.setAlertTitle("Shithead");
			
			payloadBuilder.setAlertBody("\uD83D\uDE21");

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
