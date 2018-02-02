/*
 * Copyright (c) 2017 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with: mongodb@3.0.2
 * Documentation: https://mongodb.github.io/node-mongodb-native/
 * A Node script connecting to a MongoDB database given a MongoDB Connection URI.
*/

const mongodb = require('mongodb');

// Create seed data

let seedData = [
  {
    decade: '1970s',
    artist: 'Debby Boone',
    song: 'You Light Up My Life',
    weeksAtOne: 10
  },
  {
    decade: '1980s',
    artist: 'Olivia Newton-John',
    song: 'Physical',
    weeksAtOne: 10
  },
  {
    decade: '1990s',
    artist: 'Mariah Carey',
    song: 'One Sweet Day',
    weeksAtOne: 16
  }
];

// Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname

let uri = 'mongodb://user:pass@host:port/dbname';

mongodb.MongoClient.connect(uri, function(err, client) {

  if(err) throw err;

  /*
   * Get the database from the client. Nothing is required to create a
   * new database, it is created automatically when we insert.
   */

  let db = client.db('dbname')

  /*
   * First we'll add a few songs. Nothing is required to create the
   * songs collection; it is created automatically when we insert.
   */

  let songs = db.collection('songs');

   // Note that the insert method can take either an array or a dict.

  songs.insert(seedData, function(err, result) {

    if(err) throw err;

    /*
     * Then we need to give Boyz II Men credit for their contribution
     * to the hit "One Sweet Day".
     */

    songs.update(
      { song: 'One Sweet Day' },
      { $set: { artist: 'Mariah Carey ft. Boyz II Men' } },
      function (err, result) {

        if(err) throw err;

        /*
         * Finally we run a query which returns all the hits that spend 10 or
         * more weeks at number 1.
         */

        songs.find({ weeksAtOne : { $gte: 10 } }).sort({ decade: 1 }).toArray(function (err, docs) {

          if(err) throw err;

          docs.forEach(function (doc) {
            console.log(
              'In the ' + doc['decade'] + ', ' + doc['song'] + ' by ' + doc['artist'] +
              ' topped the charts for ' + doc['weeksAtOne'] + ' straight weeks.'
            );
          });

          // Since this is an example, we'll clean up after ourselves.
          songs.drop(function (err) {
            if(err) throw err;

            // Only close the connection when your app is terminating.
            client.close(function (err) {
              if(err) throw err;
            });
          });
        });
      }
    );
  });
});

