/*
 * Copyright (c) 2017 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with mongo-3.4.2.jar
 * Documentation: http://api.mongodb.org/java/
 * A Java class connecting to a MongoDB database given a MongoDB Connection URI.
 */
import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

public class JavaSimpleExample {

    // Extra helper code

    public static ArrayList<Document>[] createSeedData(){
        
        Document seventies = new Document();
        seventies.put("decade", "1970s");
        seventies.put("artist", "Debby Boone");
        seventies.put("song", "You Light Up My Life");
        seventies.put("weeksAtOne", 10);
        
        Document eighties = new Document();
        eighties.put("decade", "1980s");
        eighties.put("artist", "Olivia Newton-John");
        eighties.put("song", "Physical");
        eighties.put("weeksAtOne", 10);
        
        Document nineties = new Document();
        nineties.put("decade", "1990s");
        nineties.put("artist", "Mariah Carey");
        nineties.put("song", "One Sweet Day");
        nineties.put("weeksAtOne", 16);
        
        final List<Document> seedData = {seventies, eighties, nineties};
        
        return seedData;
    }
    
    public static void main(String[] args) throws UnknownHostException{
        
        // Create seed data
        
        final List<Document> seedData = createSeedData();
        
        // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
       
        // MongoClientURI uri  = new MongoClientURI("mongodb://user:pass@host:port/db"); 
        MongoClientURI uri  = new MongoClientURI("mongodb://localhost:27017/db"); 
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());
        
        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */
        
        MongoCollection songs = db.getCollection("songs");

        // Note that the insert method can take either an array or a document.
        
        songs.insertMany(seedData);
       
        /*
         * Then we need to give Boyz II Men credit for their contribution to
         * the hit "One Sweet Day".
         */

        Document updateQuery = new Document("song", "One Sweet Day");
        songs.updateOne(updateQuery, new Document("$set", new Document("artist", "Mariah Carey ft. Boyz II Men")));
        
        /*
         * Finally we run a query which returns all the hits that spent 10 
         * or more weeks at number 1.
         */
      
        Document findQuery = new Document("weeksAtOne", new Document("$gte",10));
        Document orderBy = new Document("decade", 1);

        MongoCursor<Document> docs = songs.find(findQuery).sort(orderBy).iterator();

        // while(docs.hasNext()){
        //     DBObject doc = docs.next();
        //     System.out.println(
        //         "In the " + doc.get("decade") + ", " + doc.get("song") + 
        //         " by " + doc.get("artist") + " topped the charts for " + 
        //         doc.get("weeksAtOne") + " straight weeks."
        //     );
        // }
        
        try {
            while (docs.hasNext()) {
                System.out.println(docs.next().toJson());
            }
        } finally {
            docs.close();
        }

        // Since this is an example, we'll clean up after ourselves.

        songs.drop();
        
        // Only close the connection when your app is terminating

        client.close();
    }
}