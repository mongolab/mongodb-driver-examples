#!/usr/bin/env ruby

# mongoid_simple_example.rb
# 
# A sample ruby script covering connection to a MongoDB database given a
# fully-qualified URI, using the Mongoid ODM.
# There are a few additional means, but we prefer the URI
# connection model because developers can use the same code to handle various
# database configuration possibilities (single, master/slave, replica sets).
#
# Author::  Mongolab

# First, require the Mongoid ruby driver. Depending on your environment,
# you may or may not need to require 'rubygems'.
#
require 'mongoid'

# If your database is running in auth mode, you will need to provide a URI
# with user info and a database path, ex:
#  'mongodb://username:password@localhost:27017/mongoquest'
#
mongo_uri = 'mongodb://localhost:27017'
db_name = 'mongoquest'

# Mongo::Connection.from_uri() creates a connection given a fully-qualified
# mongodb URI. Note that we pass the URI string directly and append a db
# call with the database name, passing the result to Mongoid.database.
#
Mongoid.database = Mongo::Connection.from_uri(mongo_uri).db(db_name)

# Here we define our Mongoid Document Model. There are quite a few
# options available here, but we'll keep it simple, declaring only
# the data type of a few keys. Mongoid automatically backs this
# model up with a MongoDB collection.
#
class Site
  include Mongoid::Document
    
  field :name, type: String
  field :description, type: String
  field :level, type: Integer
end

# What follows is code that can vary widely depending on your style.
# First we'll add a few sites describing places adventurers could visit.
#
puts 'Adding'
new_site = Site.new(name: 'Airship Docks', description: 'Docks without water. For ships without water', level: 1)
new_site.save
new_site2 = Site.new(name: 'Portal District', description: 'The area of the city where all the portals go. So they can be guarded together.', level: 2)
new_site2.save
new_site3 = Site.new(name: 'Wilderness', description: 'Crazy stuff goes on beyond the city walls.', level: 3)
new_site3.save

# Now we'll check out sites available to adventurers level 2 or lower.
#
puts 'Querying'
Site.where(:level.lte => 2).each { |result| puts result.inspect }

# After having visited the Airship Docks, we've found quite a few bandits,
# and should probably up the difficulty so level 1 adventurers don't
# go in unwarned. Note that each query result is operable as a Mongoid
# document.
#
puts 'Updating'
Site.where(:name => 'Airship Docks').each {
    |result|
    result.level = 2
    result.save
}

# Now let's check out our level 2 or lower sites.
#
puts 'Querying again'
Site.where(:level.lte => 2).each { |result| puts result.inspect }

# Since this is an example, we'll clean up after ourselves.
#
Site.collection.drop

# See ya
#
exit