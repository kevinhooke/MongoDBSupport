package kh.mongo;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * Initializes a MongoDB connection on OpenShift. Reads environment properties for MongoDB hostname,
 * port, userid/pwd and initializes a MongoClient with the properties.
 * 
 * @author kevin.hooke
 *
 */
public class MongoConnection {

	private static String mongoDBHostName;
	private static String mongoDBPort;
	private static String mongoDBDatabaseName;
	private static String mongoDBUserName;
	private static String mongoDBPassword;
	private static Integer port;

	private static MongoClient mongoClient;
	
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MongoConnection.class);
	/*
	 * If running in OpenShift environment, read System environment properties,
	 * otherwise if not found (not running on OpenShift), read properties
	 * from -D command-line properties.
	 */
	static {
		mongoDBDatabaseName = System.getenv("OPENSHIFT_GEAR_NAME");
		if (mongoDBDatabaseName == null){
			mongoDBDatabaseName = System.getProperty("OPENSHIFT_GEAR_NAME");
		}
		//try property for MongoDB in container
		if (mongoDBDatabaseName == null){
			mongoDBDatabaseName = System.getenv("MONGODB_DB_NAME");
		}		
		if (mongoDBDatabaseName == null){
			mongoDBDatabaseName = "mydb"; //hardcoded local db for testing
		}
		
		LOGGER.info("MongoDB database name (OPENSHIFT_GEAR_NAME / MONGODB_DB_NAME): " + mongoDBDatabaseName);
		
		mongoDBHostName = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
		if(mongoDBHostName == null){
			mongoDBHostName = System.getenv("MONGODB_DB_HOST");
		}
		if(mongoDBHostName == null){
			mongoDBHostName = System.getProperty("OPENSHIFT_MONGODB_DB_HOST");
		}
		
		mongoDBPort = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
		if(mongoDBPort == null){
			mongoDBPort = System.getenv("MONGODB_DB_PORT");
		}
		if(mongoDBPort == null){
			mongoDBPort = System.getProperty("OPENSHIFT_MONGODB_DB_PORT");
		}
		if(mongoDBPort == null){
			throw new MongoConnectionMissingPropertyException("Property not found: OPENSHIFT_MONGODB_DB_PORT / MONGODB_DB_PORT");
		}
		else{
			port = Integer.valueOf(mongoDBPort);
		}
		
		mongoDBUserName = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");		
		mongoDBPassword = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
		

	}

	private MongoConnection(){
	}
	
	
	public static DB getMongoDB() throws UnknownHostException {
		MongoCredential credential = null;

		if (mongoClient == null) {

			if (mongoDBUserName != null && mongoDBPassword != null) {
				credential = MongoCredential.createMongoCRCredential(mongoDBUserName,
						mongoDBDatabaseName, mongoDBPassword.toCharArray());

				LOGGER.info("Opening connection to Mongo with credentials: "
						+ mongoDBHostName + " : " + port);
				mongoClient = new MongoClient(new ServerAddress(mongoDBHostName, port),
						Arrays.asList(credential));
			} else {
				LOGGER.info("Opening connection to Mongo without credentials: "
						+ mongoDBHostName + " : " + port);
				mongoClient = new MongoClient(new ServerAddress(mongoDBHostName, port));
			}
		}
		DB mongoDB = mongoClient.getDB(mongoDBDatabaseName);

		return mongoDB;

	}
}
