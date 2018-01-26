import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;

import java.io.File;

public class Test {
	
	public static void main(String[] args) throws Exception{
	    new Test();
	}
	
	public Test() throws Exception{
		final ApnsClient apnsClient = new ApnsClientBuilder()
				.setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
				.setClientCredentials(new File("/path/to/certificate.p12"), "p12-file-password")
				.build();
	}
	
}
