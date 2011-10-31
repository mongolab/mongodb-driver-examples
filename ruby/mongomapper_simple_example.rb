#!/usr/bin/env ruby

# mongomapper_simple_example.rb
# 
# A sample ruby script covering connection to a MongoDB database given a
# fully-qualified URI, using the MongoMapper ODM.
# There are a few additional means, but we prefer the URI
# connection model because developers can use the same code to handle various
# database configuration possibilities (single, master/slave, replica sets).
#
# Author::  Mongolab

# First, require the MongoMapper ruby driver. We also need uri support. 
# Depending on your environment, you may or may not need to require 'rubygems'.
#
require 'mongo_mapper'

# If your database is running in auth mode, you will need to provide a URI
# with user info and a database path, ex:
#  'mongodb://username:password@localhost:27017/mongoquest'
#
mongo_uri = 'mongodb://localhost:27017'
db_name = 'mongoquest'

# Mongo::Connection.from_uri() creates a connection given a fully-qualified
# mongodb URI. Note that the MongoMapper.database is set using the string
# database name, not a call to Mongo::Connection.db()
#
MongoMapper.connection = Mongo::Connection.from_uri(mongo_uri)
MongoMapper.database = db_name

# The world of MongoQuest won't be exciting unless there are a few
# non-player characters (NPCs) to give us a reason to fight. Not to
# mention quests! Here is a MongoMapper Document Model for NPCs.
# There are quite a few options available here, but we'll keep it simple,
# declaring only the data type of a few keys. MongoMapper automatically
# backs this model up with a MongoDB collection.
#
class Npc
  include MongoMapper::Document

  key :name, String
  key :occupation, String
  key :saying, String
end

# What follows is code that can vary widely depending on your style.
# First we'll add a villager to the NPC collection. The .save call
# places the document in the database.
#
puts 'Adding'
npc = Npc.new(:name => 'Mike', :occupation => 'villager', :saying => "Times 'er tuff")
npc.save

# Here we query for all documents in the collection with the name
# of Mike.
#
puts 'Querying'
Npc.where(:name => 'Mike').each { |result| puts result.inspect }

# Now we're going to update Mike. Seems times have improved and he's
# ready to make some rat tail soup. Unfortunately there are no
# adventurers to help him yet. Each query result is operable as a
# MongoMapper document.
#
puts 'Updating'
Npc.where(:name => 'Mike').each {
    |result|
    result.saying = 'Wish I had 10 rat tails!'
    result.save
}

# This time we'll take a look at everything.
#
puts 'Querying again'
Npc.all.each { |result| puts result.inspect }

# Since this is an example, we'll clean up after ourselves.
#
Npc.collection.drop

# See ya
#
exit