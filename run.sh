#!/bin/bash

#compile
ant all
mvn install

#Start server
gnome-terminal -x sh -c "./runServer.sh; bash"

# #Start clients
gnome-terminal -x sh -c "./runClient.sh; bash"
gnome-terminal -x sh -c "./runClient2.sh; bash"

# gnome-terminal -x runClient.sh
# gnome-terminal -x runClient.sh


