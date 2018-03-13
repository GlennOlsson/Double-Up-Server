package Server;

import APNs.API.Notification.NotificationClient;
import Server.Game.Models.GamesFile;
import Server.Game.Models.UsersFile;

public class Constants {
	
	public static String CERT_PATH;
	public static String CERT_PASS;
	public static String APP_BUNDLE;
	public static NotificationClient NOTIFICATION_CLIENT;
	
	public static final String DATE_PATTERN = "dd/MM - yyyy -- HH:mm:ss";
	
	public static GamesFile GAMES_FILE;
	public static UsersFile USERS_FILE;
	
}
