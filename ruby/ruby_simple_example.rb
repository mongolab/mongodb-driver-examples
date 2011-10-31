#!/usr/bin/env ruby

# ruby_simple_example.rb
# 
# A sample ruby script covering connection to a MongoDB database given a
# fully-qualified URI. There are a few additional means, but we prefer the URI
# connection model because developers can use the same code to handle various
# database configuration possibilities (single, master/slave, replica sets).
#
# Author::  Mongolab

# First, require the official MongoDB ruby driver. We also need URI support. 
# Depending on your environment, you may or may not need to require 'rubygems'.
#
require 'mongo'

# If your database server is running in auth mode, you will need to provide a URI
# with authentication info and a database name, ex:
#  'mongodb://username:password@localhost:27017/mongoquest'
#
mongo_uri = 'mongodb://localhost:27017'
db_name = 'mongoquest'

# Mongo::Connection.from_uri() creates a connection given a fully-qualified
# mongodb URI. Note that we pass the URI string directly and obtain a database
# reference from the connection.
#
connection = Mongo::Connection.from_uri(mongo_uri)
db = connection.db(db_name)

# What follows is code that can vary widely depending on coding style.
# First we identify our collection.
#
item_collection = db.collection('items')

# Then we insert a few items defined by their relative cost
# and size.
#
item_collection.insert({'name' => 'sword', 'size' => 3, 'cost' => 4})
item_collection.insert({'name' => 'map', 'size' => 2, 'cost' => 5})
item_collection.insert({'name' => 'leather armor', 'size' => 4, 'cost' => 7})
item_collection.insert({'name' => 'dagger', 'size' => 1, 'cost' => 2})
item_collection.insert({'name' => 'mcguffin', 'size' => 2, 'cost' => 10})

# A simple find call retrieves all documents in the collection.
# 
puts 'Initial item set'
item_collection.find().each { |result| puts result }

# Now we run an update query, passing the criteria, operation, and
# the multi modifier, which ensures the update will run against all
# results, not just the first. Here we are reducing the cost of all
# items more expensive than 3 by 1.
#
item_collection.update({'cost' => {'$gt' => 3}}, {'$inc' => {'cost' => -1}}, :multi => true)

puts 'Discounted item set'
item_collection.find().each { |result| puts result }

# Since this is an example, we'll clean up after ourselves.
#
item_collection.drop

# Thanks for shopping
#
exit
