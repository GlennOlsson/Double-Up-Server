package APNs.API.Notification;

import Server.Constants;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;

public class Notification {
	
	private String title;
	private String subtitle;
	private String body;
	private String soundPath;
	private Integer badgeNumber;
	
	private String clientToken;
	
	public Notification(String title, String subtitle, String body, String soundPath, Integer badgeNumber, String clientToken) {
		this.title = title;
		this.subtitle = subtitle;
		this.body = body;
		this.soundPath = soundPath;
		this.badgeNumber = badgeNumber;
		this.clientToken = clientToken;
		
	}
	
	public Notification(String clientToken){
		this.clientToken = clientToken;
	}
	
	private Notification(){
		//You must make a APNs.API.Notification.APNs.API.Notification instance with the client token
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSubtitle() {
		return subtitle;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getSoundPath() {
		return soundPath;
	}
	
	public void setSoundPath(String soundPath) {
		this.soundPath = soundPath;
	}
	
	public Integer getBadgeNumber() {
		return badgeNumber;
	}
	
	/**
	 * Set badge number for the app. If the parameter is 0, all notifications will disappear. If null,
	 * the current badge will be kept
	 * @param badgeNumber the badge number
	 */
	public void setBadgeNumber(Integer badgeNumber) {
		this.badgeNumber = badgeNumber;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}
	
	private SimpleApnsPushNotification create(){
		
		String appBundle = Constants.APP_BUNDLE;
		
		//Creating notification
		final SimpleApnsPushNotification pushNotification;
		
		final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();

		if(title != null && title.length() > 0){
			payloadBuilder.setAlertTitle(title);
		}
		
		if(subtitle != null && subtitle.length() > 0){
			payloadBuilder.setAlertSubtitle(subtitle);
		}
		
		if(body != null && body.length() > 0){
			payloadBuilder.setAlertBody(body);
		}
		
		if(soundPath != null && soundPath.length() > 0){
			payloadBuilder.setSoundFileName(soundPath);
		}
		
		//As badge can be both null and 0, no check is necessary
		payloadBuilder.setBadgeNumber(badgeNumber);
		
		final String payload = payloadBuilder.buildWithDefaultMaximumLength();
		final String token = TokenUtil.sanitizeTokenString(clientToken);
		
		pushNotification = new SimpleApnsPushNotification(token, appBundle, payload);
		
		return pushNotification;
	}
	
	public SimpleApnsPushNotification getNotification() {
		return create();
	}
}
