package Backend;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandling {
	
	private static final String SECRET_FILE = "Secret.json";
	private static final String USERS_FILE = "Users.json";
	private static final String GAMES_FILE = "Games.json";
	
	public enum File{
		
		Users(getPath() + USERS_FILE), Games(getPath() + GAMES_FILE), Secret(SECRET_FILE);
		
		private String fileName;
		File(String fileName){
			this.fileName = fileName;
		}
		
		public String getFilePath() {
			return fileName;
		}
	}
	
	
	/**
	 * Returns the content of the file as a string, utilizing java.nio
	 * @return the full content of the file
	 */
	public static JsonObject getContentOfFileAsJSON(File file) throws IOException {
		byte[] fileInBytes = Files.readAllBytes(Paths.get(file.getFilePath()));
		String contentOfFileString = new String(fileInBytes);
		
//		System.out.println(contentOfFileString);
		
		JsonObject fileObject = JSON.parseStringToJSON(contentOfFileString);
		
		return fileObject;
	}
	
	/**
	 * Tries to save the content parameter to the file with the file name
	 * @param content the content to save
	 */
	public static void saveToFile(String content, File file){
		try{
			Files.write(Paths.get(file.getFilePath()), content.getBytes());
			Logger.print("Successfully saved to " + file.fileName + " file");
		}
		catch (Exception e){
			System.err.println("Could not save file");
		}
	}
	
	/**
	 * Tries to save the json parameter to the file with the file name
	 * @param content the content to save
	 */
	public static void saveToFile(JsonObject content, File file){
		try{
			
			String beautifiedJSON = JSON.beautifyJSON(content);
			
			Files.write(Paths.get(file.getFilePath()), beautifiedJSON.getBytes());
			Logger.print("Successfully saved to " + file.fileName + " file");
		}
		catch (Exception e){
			System.err.println("Could not save file");
		}
	}
	
	private static String getPath(){
		if(System.getProperty("os.name").toLowerCase().contains("linux")) {
			//Only works on Tau
			return "/NAS/Glenn/DoubleUp-Server/";
		}
		else if(System.getProperty("os.name").toLowerCase().contains("mac os x")) {
			return "/Volumes/NASDisk/Glenn/DoubleUp-Server/";
		}
		else {
			System.err.println("ERROR WITH OS NAME");
			return "";
		}
	}
	
}
