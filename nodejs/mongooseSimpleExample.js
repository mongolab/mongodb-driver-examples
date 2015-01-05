/*
 * Copyright (c) 2015 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with: mongoose@3.8.8, mongodb-uri@0.9.3
 * Documentation: http://mongoosejs.com/docs/guide.html
 * A Mongoose script connecting to a MongoDB database given a MongoDB Connection URI.
 * MongoLab blog post on Mongoose: http://blog.mongolab.com/2014/04/mongodb-driver-mongoose/
 */
var mongoose = require('mongoose');
var uriUtil = require('mongodb-uri');
 
/* 
 * Mongoose by default sets the auto_reconnect option to true.
 * We recommend setting socket options at both the server and replica set level.
 * We recommend a 30 second connection timeout because it allows for 
 * plenty of time in most operating environments.
 */
var options = { server: { socketOptions: { keepAlive: 1, connectTimeoutMS: 30000 } }, 
                replset: { socketOptions: { keepAlive: 1, connectTimeoutMS : 30000 } } };       
 
/*
 * Mongoose uses a different connection string format than MongoDB's standard.
 * Use the mongodb-uri library to help you convert from the standard format to
 * Mongoose's format.
 */
var mongodbUri = 'mongodb://user:pass@host:port/db';
var mongooseUri = uriUtil.formatMongoose(mongodbUri);

mongoose.connect(mongooseUri, options);

var db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));

db.once('open', function callback () {

  // Create song schema
  var songSchema = mongoose.Schema({
    decade: String,
    artist: String,
    song: String,
    weeksAtOne: Number
  });

  // Store song documents in a collection called "songs"
  var Song = mongoose.model('songs', songSchema);

  // Create seed data
  var seventies = new Song({
    decade: '1970s',
    artist: 'Debby Boone',
    song: 'You Light Up My Life',
    weeksAtOne: 10
  });

  var eighties = new Song({
    decade: '1980s',
    artist: 'Olivia Newton-John',
    song: 'Physical',
    weeksAtOne: 10
  });

  var nineties = new Song({
    decade: '1990s',
    artist: 'Mariah Carey',
    song: 'One Sweet Day',
    weeksAtOne: 16
  });

  /*
   * First we'll add a few songs. Nothing is required to create the 
   * songs collection; it is created automatically when we insert.
   */
  seventies.save();
  eighties.save();
  nineties.save();

  /*
   * Then we need to give Boyz II Men credit for their contribution
   * to the hit "One Sweet Day".
   */
  Song.update({ song: 'One Sweet Day'}, { $set: { artist: 'Mariah Carey ft. Boyz II Men'} }, 
    function (err, numberAffected, raw) {

      if (err) return handleError(err);

      /*
       * Finally we run a query which returns all the hits that spend 10 or
       * more weeks at number 1.
       */
      Song.find({ weeksAtOne: { $gte: 10} }).sort({ decade: 1}).exec(function (err, docs){

        if(err) throw err;

        docs.forEach(function (doc) {
          console.log(
            'In the ' + doc['decade'] + ', ' + doc['song'] + ' by ' + doc['artist'] + 
            ' topped the charts for ' + doc['weeksAtOne'] + ' straight weeks.'
          );
        });

        // Since this is an example, we'll clean up after ourselves.
        mongoose.connection.db.collection('songs').drop(function (err) {
          if(err) throw err;

          // Only close the connection when your app is terminating
          mongoose.connection.db.close(function (err) {
            if(err) throw err;
          });
        });
      });
    }
  )
});
