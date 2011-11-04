import com.mongodb.*;
import java.net.UnknownHostException;
import java.util.ArrayList;

/*
 * JavaSimpleExample.java
 * 
 * This simple example class connects to a MongoDB database and inserts
 * JSON documents.
 *
 * @author: MongoLab
 */
public class JavaSimpleExample {

    // If your database server is running in auth mode, be sure to include user info
    // in the URI. Ex: "mongodb://username:password@localhost:27017/mongoquest"
    private static String uriString = "mongodb://localhost:27017/mongoquest";

    public static void main(String[] args){
	
	// We opt to use the MongoURI class to access MongoDB connection methods.
	MongoURI uri = new MongoURI(uriString);
	DB database = null;
	DBCollection locations = null;

	try {
	    // The MongoURI class can connect and return a database given the URI above.
	    database = uri.connectDB();
	    // If you are running in auth mode and have provided user info
	    // in your URI, you can use this line.
	    // database.authenticate(uri.getUsername(), uri.getPassword());
	} catch(UnknownHostException uhe) {
	    System.out.println("UnknownHostException: " + uhe);
	} catch(MongoException me) {
	    System.out.println("MongoException: " + me);
	}

	if (database != null) {
	    // We retrieve the collection we'll be working with.
	    locations = database.getCollection("locations");

	    // In this example, we build BasicDBObjects describing two
	    // locations, Arganis and Kent. The first, Arganis, would use
	    // the following JSON:
	    //
            // {'name': 'Arganis',
            //  'weather': 'temperate',
            //  'terrain': ['forests', 'plains'],
            //  'benefits': ['lodging', 'trade', 'justice'],
            //  'dangers': ['bandits', 'rebels', 'goblins', 'ghosts']}
	    BasicDBObject arganis = new BasicDBObject();
	    ArrayList<String> arganisTerrain = new ArrayList<String>();
	    ArrayList<String> arganisBenefits = new ArrayList<String>();
	    ArrayList<String> arganisDangers = new ArrayList<String>();
	    arganis.put("name", "Arganis");
	    arganis.put("weather", "temperate");
	    arganisTerrain.add("forests");
	    arganisTerrain.add("plains");
	    arganis.put("terrain", arganisTerrain);
	    arganisBenefits.add("lodging");
            arganisBenefits.add("trade");
	    arganisBenefits.add("justice");
            arganis.put("benefits", arganisBenefits);
	    arganisDangers.add("bandits");
	    arganisDangers.add("rebels");
	    arganisDangers.add("ghosts");
	    arganis.put("dangers", arganisDangers);

	    // The second, Kent, is a bit more dangerous a place.
	    // {'name': 'Kent',
	    //  'weather': 'temperate',
	    //  'terrain': ['hills', 'plains'],
	    //  'benefits': ['lodging', 'trade']
	    //  'dangers': ['bandits', 'rebels', 'famine', 'goblins']}
            BasicDBObject kent = new BasicDBObject();
            ArrayList<String> kentTerrain = new ArrayList<String>();
            ArrayList<String> kentBenefits = new ArrayList<String>();
            ArrayList<String> kentDangers = new ArrayList<String>();
            kent.put("name", "Kent");
            kent.put("weather", "temperate");
	    kentTerrain.add("hills");
            kentTerrain.add("plains");
            kent.put("terrain", kentTerrain);
            kentBenefits.add("lodging");
            kentBenefits.add("trade");
            kent.put("benefits", kentBenefits);
            kentDangers.add("bandits");
            kentDangers.add("rebels");
            kentDangers.add("famine");
	    kentDangers.add("goblins");
            kent.put("dangers", kentDangers);

	    // Then, we pass the BasicDBObjects to the .insert() function
            // in our collection object.
            locations.insert(arganis);
            locations.insert(kent);

            // For the sake of fealty, we should add the name of the leader of
	    // Arganis. Note that we can build our documents more quickly with
	    // nested calls to the BasicDBObject constructor.
            locations.update(new BasicDBObject("name", "Arganis"),
                             new BasicDBObject("$set",
					       new BasicDBObject("leader",
								 "King Argan III")));

	    // Now let's query for locations with forests.
	    System.out.println("We'd like to visit a forest. Out of " +
	    		       locations.count() + " locations, the " +
			       "following have forests:");
	    
	    // Assign the results of a find operation to a DBCursor object.
	    // Cursors can be iterated through using familiar next/hasNext logic.
	    DBCursor results = locations.find(new BasicDBObject("terrain", "forests"));
	    while(results.hasNext()){
	        DBObject result = results.next();
		System.out.println((String) result.get("name") + " has forests.");
		System.out.println("Maybe " +
				   (String) result.get("leader") +
				   " will let us hunt there.");
	    }
	    
	    // Since this is an example we'll clean up after ourselves.
	    locations.drop();
	}
    }
}
