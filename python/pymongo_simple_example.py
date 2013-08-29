#/usr/bin/python

__author__ = 'mongolab'

# Written with pymongo-2.6
# A python script connecting to a MongoDB given a MongoDB Connection URI

import sys
from pymongo import MongoClient

### Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname

MONGODB_URI = 'mongodb://sandbox:test@ds039768.mongolab.com:39768/test2345' 

###############################################################################
# main
###############################################################################


def main(args):

    try:
        client = MongoClient(MONGODB_URI)
    except Exception, err:
        print 'Error: %s' % err
        return

    db = client.get_default_database()
    
    # First we'll add a few songs. Nothing is required to create the songs 
    # collection; it is created automatically when we insert.

    songs = db['songs']

    songs.insert(
        {
            'decade': '1970s',
            'artist': 'Debby Boone',
            'song': 'You Light Up My Life',
            'weeksAtOne': 10
        }
    )

    songs.insert(
        {
            'decade': '1980s',
            'artist': 'Olivia Newton-John',
            'song': 'Physical',
            'weeksAtOne': 10
        }
    )

    songs.insert(
        {
            'decade': '1990s',
            'artist': 'Mariah Carey',
            'song': 'One Sweet Day',
            'weeksAtOne': 16
        }
    )

    # Then we need to give Boyz II Men credit for their contribution to
    # the hit "One Sweet Day"

    query = {'song': 'One Sweet Day'}

    songs.update(query, {'$set': {'artist': 'Mariah Carey ft. Boyz II Men'}})

    # Finally we run a query which returns all the hits that spent 10 or
    # more weeks at number 1

    cursor = songs.find({'weeksAtOne': {'$gte': 10}}).sort('decade',1)

    for doc in cursor:
        print ('In the %s, %s by %s topped the charts for %d straight weeks.' %
               (doc['decade'], doc['song'], doc['artist'], doc['weeksAtOne']))
    
    ### Since this is an example, we'll clean up after ourselves.

    db.drop_collection('songs')


if __name__ == '__main__':
    main(sys.argv[1:])
