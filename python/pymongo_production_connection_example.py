#!/usr/bin/python

# Copyright (c) 2016 ObjectLabs Corporation
# Distributed under the MIT license - http://opensource.org/licenses/MIT

from pymongo import MongoClient  # pymongo>=3.2

"""
PyMongo Production Connection Example

The following example uses PyMongo to connect to a MongoDB deployment using the options we have found most appropriate to
production applications. This example supports both replica set and SSL connections.

For demonstration purposes, configuration is hard-coded; in practice, configuration should be externalized e.g. into a file or
environment variables.

  ##########################################################################################################################
  WARNING: This example requires PyMongo 3.2 or later to work correctly. In particular, if you are connecting over SSL
           with an earlier driver version additional configuration will be required for it to properly authenticate the
           server.
  ##########################################################################################################################
"""

# Your deployment's URI in the standard format (http://docs.mongodb.org/manual/reference/connection-string/).
#
# The URI can be found via the mLab management portal (http://docs.mlab.com/connecting/#connect-string).
#
# If you are using a PaaS add-on integration e.g. via Heroku, the URI is usually available via an environment variable. 
# Consult the documentation for the PaaS that you are using for more information on this.
uri = "mongodb://<dbuser>:<dbpassword>@<host1>:<port1>,<host2>:<port2>/<dbname>?replicaSet=<replicaSetName>&ssl=true"

# Pass the following keyword arguments to ensure proper production behavior:
#
#   connectTimeoutMS  30 s to allow for PaaS warm-up; adjust down as needed for faster failures. For more, see docs:
#                     http://docs.mlab.com/timeouts/#connection-timeout
#
#   socketTimeoutMS   No timeout (None) to allow for long-running operations
#                     (http://docs.mlab.com/timeouts/#socket-timeout).
#
#   socketKeepAlive   Enabled (True) to ensure idle connections are kept alive in the presence of a firewall.
#
# See PyMongo docs for more details about the connection options:
#
#   https://api.mongodb.org/python/3.2/api/pymongo/mongo_client.html#pymongo.mongo_client.MongoClient
#
client = MongoClient(uri,
                     connectTimeoutMS=30000,
                     socketTimeoutMS=None,
                     socketKeepAlive=True)

db = client.get_default_database()
print db.collection_names()