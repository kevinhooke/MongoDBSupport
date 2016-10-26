package kh.mongo;

import static org.junit.Assert.assertNotNull;

import java.net.UnknownHostException;

import org.junit.Test;

import com.mongodb.DB;

public class MongoConnectionTest {

	@Test
	public void testGetConnection() throws UnknownHostException {
		
		System.setProperty("OPENSHIFT_MONGODB_DB_PORT", "27017");
		
		DB db = MongoConnection.getMongoDB();
		assertNotNull(db);
	}

}
