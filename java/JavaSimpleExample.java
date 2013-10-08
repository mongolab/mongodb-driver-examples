/*
 * Copyright (c) 2013 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with mongo-2.11.2.jar
 * Documentation: http://api.mongodb.org/java/
 * A Java class connecting to a MongoDB database given a MongoDB Connection URI.
 */

import java.net.UnknownHostException;
import com.mongodb.*;


public class JavaSimpleExample {

    // Extra helper code

    public static BasicDBObject[] createSeedData(){
        
        BasicDBObject seventies = new BasicDBObject();
        seventies.put("decade", "1970s");
        seventies.put("artist", "Debby Boone");
        seventies.put("song", "You Light Up My Life");
        seventies.put("weeksAtOne", 10);
        
        BasicDBObject eighties = new BasicDBObject();
        eighties.put("decade", "1980s");
        eighties.put("artist", "Olivia Newton-John");
        eighties.put("song", "Physical");
        eighties.put("weeksAtOne", 10);
        
        BasicDBObject nineties = new BasicDBObject();
        nineties.put("decade", "1990s");
        nineties.put("artist", "Mariah Carey");
        nineties.put("song", "One Sweet Day");
        nineties.put("weeksAtOne", 16);
        
        final BasicDBObject[] seedData = {seventies, eighties, nineties};
        
        return seedData;
    }
    
    public static void main(String[] args) throws UnknownHostException{
        
        // Create seed data
        
        final BasicDBObject[] seedData = createSeedData();
        
        // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
       
        MongoClientURI uri  = new MongoClientURI("mongodb://user:pass@host:port/db"); 
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());
        
        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */
        
        DBCollection songs = db.getCollection("songs");

        // Note that the insert method can take either an array or a document.
        
        songs.insert(seedData);
       
        /*
         * Then we need to give Boyz II Men credit for their contribution to
         * the hit "One Sweet Day".
         */

        BasicDBObject updateQuery = new BasicDBObject("song", "One Sweet Day");
        songs.update(updateQuery, new BasicDBObject("$set", new BasicDBObject("artist", "Mariah Carey ft. Boyz II Men")));
        
        /*
         * Finally we run a query which returns all the hits that spent 10 
         * or more weeks at number 1.
         */
      
        BasicDBObject findQuery = new BasicDBObject("weeksAtOne", new BasicDBObject("$gte",10));
        BasicDBObject orderBy = new BasicDBObject("decade", 1);

        DBCursor docs = songs.find(findQuery).sort(orderBy);

        while(docs.hasNext()){
            DBObject doc = docs.next();
            System.out.println(
                "In the " + doc.get("decade") + ", " + doc.get("song") + 
                " by " + doc.get("artist") + " topped the charts for " + 
                doc.get("weeksAtOne") + " straight weeks."
            );
        }
        
        // Since this is an example, we'll clean up after ourselves.

        songs.drop();
        
        // Only close the connection when your app is terminating

        client.close();
    }
}
