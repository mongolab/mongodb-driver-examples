/* 
 * nodeSimpleExample.js
 *
 * This simple example class connects to a MongoDB database and inserts,
 * and queries a few documents.
 * 
 * Mongoose is a MongoDB ODM that reduces callbacks and provides strong
 * URI-based connection support, is a good option for using MongoDB with
 * node.js
 *
 * Author: MongoLab
 */ 
var mongoose = require('mongoose');
var mongoUri = 'mongodb://localhost:27017/mongoquest';

/*
 * The .connect method accepts a fully-qualified URI and provides a
 * reference to the URI-described database as mongoose.connection.db.
 *
 * If your database server is running in auth mode, your URI must include
 * user info for authentication. Ex:
 *    'mongodb://username:password@localhost:27017/mongoquest'
 */
mongoose.connect(mongoUri);

/*
 * This callback responds to the connection open event produced by the code
 * above.
 */
mongoose.connection.on("open", function() {
        /*
	 * We obtain a reference to the quests collection.
	 */
	mongoose.connection.db.collection('quests', function(err, collection) {
		
		/*
		 * The following inserts add JSON documents describing quests
		 * to the collection we retrieved.
		 */
		collection.insert({'goal': 'Save the kingdom!',
			           'level': 5,
			           'experience': 14000,
			           'reward': {'title': 'Noble',
				              'gold': 22050}},
		                  function(err,result) {});
		collection.insert({'goal': 'Make the roads safe',
			           'level': 3,
			           'experience': 3000,
			           'reward': {'title': 'Protector',
				              'discount': 5,
				              'gold': 100}},
		                  function(err,result) {});
		collection.insert({'goal': 'Explore a new land',
			           'level': 1,
			           'experience': 1000,
			           'reward': {'gold': 50}},
		                  function(err,result) {});

		/*
		 * For updating, we pass JSON update documents similar
		 * to what MongoDB would expect in a direct shell
		 * connection. Here we want to make the bandits a little
		 * wealthier.
		 */
		collection.update({'goal': 'Make the roads safe'},
				  {'$set': {'reward.gold': 150}},
				  {},
				  function (err, result) {});
		
		/*
		 * We also pass JSON query documents to MongoDB to get a
		 * list of quests that our level 4 power-gamer might want
		 * (Quest level 4 or lower, offering 2000 XP or more).
		 */
		collection.find({'level': {'$lte': 4},
                                 'experience': {'$gte': 2000}}).toArray(function (err, goodQuests) {
					                                    console.log(goodQuests);
					                                    collection.drop();
					                                    mongoose.disconnect();
        	       		                                });
	});
});