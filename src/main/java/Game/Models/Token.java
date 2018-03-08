package Game.Models;

import java.util.Base64;
import java.util.Random;

public class Token {
	
	public static String generateToken(String string){
		StringBuilder token = new StringBuilder();
		
		Random random = new Random();
		
		long currentMillis = System.currentTimeMillis();
		//Removing everything but the last 5 characters, as they are mostly the same
		String millis = (Long.toString(currentMillis + (random.nextInt(10000) - 5000)).substring(8));
		
		for (int i = 0; i < 100; i++) {
			int randomNumber = random.nextInt(999999);
			string += randomNumber;
		}
		
		String abc = "ABCDEFGhijklMNOpQRStUVWxYZabcdefgHIJKLmnoPqrsTuvwXyz1234567890!\"#€%&/()=?©@£$∞§|[]≈±;:_,.-‚…–";
		for (int i = 0; i < 15; i++) {
			string += abc.charAt(random.nextInt(93));
		}
		
		if(string.length() > 50){
			string = string.substring(0, 50);
		}
		
		StringBuilder nameCharCodes = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			Character thisCharacter = string.charAt(i);
			nameCharCodes.append((int) thisCharacter);
		}
		
		String beforeConversion = millis + nameCharCodes.toString();
		
		return digitsToToken(beforeConversion, token);
	}
	
	private static String digitsToToken(String digits, StringBuilder token){
		//The length / 3 will give the amount of times it can divide by three (obv), and then
		//the final loop should use the remaining chars
		
		String abc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-+"; //Base 64
		
		digits += Long.toString(System.nanoTime());
		
		int lengthMod3 = digits.length() / 3;
		
		for (int i = 0; i < lengthMod3; i++) {
			String first3 = digits.substring(0, 3);
			int first3AsInt = Integer.parseInt(first3);
			
			token.append(abc.charAt(first3AsInt % 64));
			
			digits = digits.substring(3);
			
//			first3AsInt %= 88;
//			first3AsInt += 35;
//
//			token.append((char) first3AsInt);
		}
		if(digits.length() > 0) {
			int finalPart = Integer.parseInt(digits);
			
			
			token.append(abc.charAt(finalPart % 64));
//
//			finalPart %= 88;
//			finalPart += 35;
//
//			token.append((char) finalPart);
		}
		return token.toString();
	}
}
