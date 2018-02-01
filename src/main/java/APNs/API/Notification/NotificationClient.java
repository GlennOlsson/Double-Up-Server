package APNs.API.Notification;

import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import APNs.API.Exception.CertNotSetException;

public class NotificationClient {

	private ApnsClient apnsClient;
	
	/**
	 * Create a APNs.API.Notification.NotificationClient. Use sendPushNotification() to send a APNs.API.Notification.APNs.API.Notification object
	 * @throws CertNotSetException if Constant.CERT_PATH or Constant.CERT.PASS has not been set
	 * @throws IOException if there is a communication error
	 */
	public NotificationClient() throws CertNotSetException, IOException{
		String certPath = Constants.CERT_PATH;
		String certPassword = Constants.CERT_PASS;
		
		if(certPassword == null || certPassword.length() == 0 || certPath == null || certPath.length() == 0){
			//Cert path and pass not set
			throw new CertNotSetException();
		}
			apnsClient = new ApnsClientBuilder()
					.setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
					.setClientCredentials(new File(certPath), certPassword)
					.build();
	}
	
	/**
	 * Send the notification that has been created
	 * @return whether the notification was accepted
	 */
	public boolean sendPushNotification(Notification pushNotification) throws ExecutionException, InterruptedException{
		
		SimpleApnsPushNotification simpleNotification = pushNotification.getNotification();
		
		//Sending
		final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
				sendNotificationFuture = apnsClient.sendNotification(simpleNotification);
		
		//Checking transportation
		final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
				sendNotificationFuture.get();
		
		
		
		return pushNotificationResponse.isAccepted();
	}
	
	public void close(){
		apnsClient.close();
	}
}
