/*
 * Copyright (c) 2015 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with CSharpDriver-1.8.2
 * Documentation: http://api.mongodb.org/csharp/
 * A C# class connecting to a MongoDB database given a MongoDB Connection URI.
 */

using System;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Builders;

namespace SimpleExample
{

  class Simple
  {

    // Extra helper code

    static BsonDocument[] CreateSeedData()
    {

      BsonDocument seventies = new BsonDocument {
        { "Decade" , "1970s" },
        { "Artist" , "Debby Boone" },
        { "Title" , "You Light Up My Life" },
        { "WeeksAtOne" , 10 }
      };
 
      BsonDocument eighties = new BsonDocument {
        { "Decade" , "1980s" },
        { "Artist" , "Olivia Newton-John" },
        { "Title" , "Physical" },
        { "WeeksAtOne" , 10 }
      };
      
      BsonDocument nineties = new BsonDocument {
        { "Decade" , "1990s" },
        { "Artist" , "Mariah Carey" },
        { "Title" , "One Sweet Day" },
        { "WeeksAtOne" , 16 }
      };

      BsonDocument[] SeedData = { seventies, eighties, nineties };
      return SeedData;
    }

    static void Main()
    {

      // Create seed data

      BsonDocument[] seedData = CreateSeedData();

      // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname

      String uri = "mongodb://user:pass@host:port/db";
      
      MongoUrl url = new MongoUrl(uri);
      MongoClient client = new MongoClient(url);
      MongoServer server = client.GetServer();
      MongoDatabase db = server.GetDatabase(url.DatabaseName);
      
      /*
       * First we'll add a few songs. Nothing is required to create the
       * songs collection; it is created automatically when we insert.
       */
      
      var songs = db.GetCollection<BsonDocument>("songs");

       // Use Insert for single BsonDocument insertion.

      songs.InsertBatch(seedData);

      /*
       * Then we need to give Boyz II Men credit for their contribution to
       * the hit "One Sweet Day".
       */
    
      QueryDocument updateQuery = new QueryDocument{ { "Title", "One Sweet Day" } };

      songs.Update(updateQuery, new UpdateDocument{ { "$set", new BsonDocument( "Artist", "Mariah Carey ft. Boyz II Men") } });

      /*
       * Finally we run a query which returns all the hits that spent 10 
       * or more weeks at number 1.
       */

      QueryDocument findQuery = new QueryDocument{ { "WeeksAtOne", new BsonDocument{ { "$gte", 10 } } } };
      var cursor = songs.Find(findQuery).SetSortOrder(SortBy.Ascending("Decade"));
      
      foreach (var song in cursor) 
      {
        var test = song["Decade"];
        Console.WriteLine("In the {0}, {1} by {2} topped the charts for {3} straight weeks",
          song["Decade"], song["Title"], song["Artist"], song["WeeksAtOne"]);
      }

      // Since this is an example, we'll clean up after ourselves.

      songs.Drop();

      // Only disconnect when your app is terminating.

      server.Disconnect();
    }
  }
}
