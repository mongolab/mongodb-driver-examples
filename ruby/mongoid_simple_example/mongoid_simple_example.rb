#!/usr/bin/ruby

# Copyright (c) 2014 ObjectLabs Corporation
# Distributed under the MIT license - http://opensource.org/licenses/MIT

# Written with mongoid 3.1.6 
# Documentation: http://mongoid.org/en/mongoid/index.html 
# A mongoid script connecting to a MongoDB database given a MongoDB Connection URI.

require 'mongoid'
Mongoid.load!('mongoid.yml', :production)

### Define Schema

class Song
  include Mongoid::Document
  field :decade
  field :artist
  field :song
  field :weeksAtOne
  store_in collection: 'songs'
end

### Create seed data

seventies = Song.new(
  decade: '1970s', 
  artist: 'Debby Boone', 
  song: 'You Light Up My Life',
  weeksAtOne: 10
)

eighties = Song.new(
  decade: '1980s', 
  artist: 'Olivia Newton-John', 
  song: 'Physical',
  weeksAtOne: 10
)

nineties = Song.new(
  decade: '1990s', 
  artist: 'Mariah Carey', 
  song: 'One Sweet Day',
  weeksAtOne: 16
)

### Write the songs to your MongoDB

seventies.save()
eighties.save()
nineties.save()

# We need to give Boyz II Men credit for their contribution to
# the hit "One Sweet Day"

Song.where(song: 'One Sweet Day').update(artist: 'Mariah Carey ft. Boyz II Men')

# Finally we run a query which returns all the hits that spent 10 or
# more weeks at number 1

songs = Song.where(:weeksAtOne.gte => 10).sort(decade: 1)

songs.each{ |doc| puts "In the #{ doc['decade'] }," +
                        " #{ doc['song'] } by #{ doc['artist'] }" +
                        " topped the charts for #{ doc['weeksAtOne'] }" +
                        " straight weeks." }

### Since this is an example, we"ll clean up after ourselves.

Song.collection.drop()
