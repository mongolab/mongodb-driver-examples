/*
 * Copyright (c) 2015 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with CSharpDriver-2.0.0
 * Documentation: http://api.mongodb.org/csharp/
 * A C# class connecting to a MongoDB database given a MongoDB Connection URI.
 */

using System;
using System.Threading;
using System.Threading.Tasks;
using MongoDB.Bson;
using MongoDB.Driver;

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

    async static Task AsyncCrud(BsonDocument[] seedData)
    {
      // Create seed data
      BsonDocument[] songData = seedData;

      // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
      String uri = "mongodb://user:pass@host:port/db";
  
      var client = new MongoClient(uri);
      var db = client.GetDatabase("db");
      
      /*
       * First we'll add a few songs. Nothing is required to create the
       * songs collection; it is created automatically when we insert.
       */
      
      var songs = db.GetCollection<BsonDocument>("songs");

      // Use InsertOneAsync for single BsonDocument insertion.
      await songs.InsertManyAsync(songData);

      /*
       * Then we need to give Boyz II Men credit for their contribution to
       * the hit "One Sweet Day".
       */

      var updateFilter = Builders<BsonDocument>.Filter.Eq("Title", "One Sweet Day");
      var update = Builders<BsonDocument>.Update.Set("Artist", "Mariah Carey ft. Boyz II Men");

      await songs.UpdateOneAsync(updateFilter, update);

      /*
       * Finally we run a query which returns all the hits that spent 10 
       * or more weeks at number 1.
       */

      var filter = Builders<BsonDocument>.Filter.Gte("WeeksAtOne", 10);
      var sort = Builders<BsonDocument>.Sort.Ascending("Decade");

      await songs.Find(filter).Sort(sort).ForEachAsync(song =>
        Console.WriteLine("In the {0}, {1} by {2} topped the charts for {3} straight weeks",
          song["Decade"], song["Title"], song["Artist"], song["WeeksAtOne"])
      );

      // Since this is an example, we'll clean up after ourselves.
      await db.DropCollectionAsync("songs");
    }

    static void Main()
    {
      BsonDocument[] seedData = CreateSeedData();
      AsyncCrud(seedData).Wait();
    }
  }
}
