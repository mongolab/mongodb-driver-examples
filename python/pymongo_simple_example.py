#!/usr/bin/python 

# pymongo_example_simple.py
# 
# A sample python script covering connection to a MongoDB database given a
# fully-qualified URI. There are a few alternate methods, but we prefer the URI
# connection model because developers can use the same code to handle various
# database configuration possibilities (single, master/slave, replica sets).
#
# Author::  MongoLab

# First, require the pymongo MongoDB driver.
#
import sys
import pymongo

def main(args):

    # If your database server is running in auth mode, you will need user and
    # database info. Ex:
    #    mongodb_uri = 'mongodb://username:password@localhost:27017/dbname'
    #
    mongodb_uri = 'mongodb://localhost:27017'
    db_name = 'mongoquest'

    # pymongo.Connection creates a connection directly from the URI, performing
    # authentication using the provided user components if necessary.
    #
    try:
        connection = pymongo.Connection(mongodb_uri)
        database = connection[db_name]
    except:
        print('Error: Unable to connect to database.')
        connection = None
        
    # What follows is insert, update, and selection code that can vary widely
    # depending on coding style.
    #
    if connection is not None:
        
        # To begin with, we'll add a few adventurers to the database. Note that
        # nothing is required to create the adventurers collection--it is
        # created automatically when we insert into it. These are simple JSON 
        # objects.
        #
        database.adventurers.insert({'name': 'Cooper',
                                     'class': 'fighter',
                                     'level': 5,
                                     'equipment': {'main-hand': 'sword',
                                                   'off-hand': 'shield',
                                                   'armor': 'plate'}})
        database.adventurers.insert({'name': 'Nishira',
                                     'class': 'warlock',
                                     'level': 10,
                                     'equipment': {'main-hand': 'wand',
                                                   'off-hand': 'dagger',
                                                   'armor': 'cloth'}})
        database.adventurers.insert({'name': 'Mordo',
                                     'class': 'wizard',
                                     'level': 11,
                                     'equipment': {'off-hand': 'dagger',
                                                   'armor': 'leather'}})
        
        # Because it seems we forgot to equip Mordo, we'll need to get him 
        # ready. Note the dot notation used to address the 'main-hand' key.
        # Don't send a JSON object describing the 'main-hand' key in the 
        # context of the 'equipment' key, or MongoDB will overwrite the other 
        # keys stored under 'equipment'. Mordo would be embarassed without 
        # armor.
        #
        # Note that in python, MongoDB $ operators should be quoted.
        #
        database.adventurers.update({'name': 'Mordo' },
                                    {'$set': {'equipment.main-hand': 'staff'}})
        
        # Now that everyone's ready, we'll send them off through standard 
        # output. Unfortunately this adventure is is for adventurers level 10 
        # or higher. We pass a JSON object describing our query as the value
        # of the key we'd like to evaluate.
        #
        party = database.adventurers.find({'level': {'$gte': 10}})
        
        # Our query returns a Cursor, which can be counted and iterated 
        # normally.
        #
        if party.count() > 0:
            print('The quest begins!')
            for adventurer in party:
                print('%s, level %s %s, departs wearing %s and wielding a %s and %s.'
                       % ( adventurer['name'], adventurer['level'],
                           adventurer['class'],
                           adventurer['equipment']['armor'],
                           adventurer['equipment']['main-hand'],
                           adventurer['equipment']['off-hand'] ))
            print('Good luck, you %s brave souls!' % party.count())
        else:
            print('No one is high enough level!')
        
        # Since this is an example, we'll clean up after ourselves.
        database.drop_collection('adventurers')

if __name__ == '__main__': 
    main(sys.argv[1:])
