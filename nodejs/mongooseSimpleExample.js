/*
 * Copyright (c) 2017 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with: mongoose@5.0.3
 * Documentation: http://mongoosejs.com/docs/index.html
 * A Mongoose script connecting to a MongoDB database given a MongoDB Connection URI.
 */
const mongoose = require('mongoose');

let uri = 'mongodb://user:pass@host:port/dbname';

mongoose.connect(uri);

let db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));

db.once('open', function callback() {
  
  // Create song schema
  let songSchema = mongoose.Schema({
    decade: String,
    artist: String,
    song: String,
    weeksAtOne: Number
  });

  // Store song documents in a collection called "songs"
  let Song = mongoose.model('songs', songSchema);

  // Create seed data
  let seventies = new Song({
    decade: '1970s',
    artist: 'Debby Boone',
    song: 'You Light Up My Life',
    weeksAtOne: 10
  });

  let eighties = new Song({
    decade: '1980s',
    artist: 'Olivia Newton-John',
    song: 'Physical',
    weeksAtOne: 10
  });

  let nineties = new Song({
    decade: '1990s',
    artist: 'Mariah Carey',
    song: 'One Sweet Day',
    weeksAtOne: 16
  });

  /*
   * First we'll add a few songs. Nothing is required to create the
   * songs collection; it is created automatically when we insert.
   */
  let list = [seventies, eighties, nineties]
  
  Song.insertMany(list).then(() => {
    
    /*
     * Then we need to give Boyz II Men credit for their contribution
     * to the hit "One Sweet Day".
     */
     
    return Song.update({ song: 'One Sweet Day'}, { $set: { artist: 'Mariah Carey ft. Boyz II Men'} })
    
  }).then(() => {
    
    /*
     * Finally we run a query which returns all the hits that spend 10 or
     * more weeks at number 1.
     */
    
    return Song.find({ weeksAtOne: { $gte: 10} }).sort({ decade: 1})
    
  }).then(docs => {
    
    docs.forEach(doc => {
      console.log(
        'In the ' + doc['decade'] + ', ' + doc['song'] + ' by ' + doc['artist'] +
        ' topped the charts for ' + doc['weeksAtOne'] + ' straight weeks.'
      );
    });
    
  }).then(() => {
    
    // Since this is an example, we'll clean up after ourselves.
    return mongoose.connection.db.collection('songs').drop()
    
  }).then(() => {
    
    // Only close the connection when your app is terminating
    return mongoose.connection.close()
    
  }).catch(err => {
    
    // Log any errors that are thrown in the Promise chain
    console.log(err)
    
  })
});
