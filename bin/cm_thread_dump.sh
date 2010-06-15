#!/bin/bash
export CATALINA_HOME=~hotmath/tomcat2

. $CATALINA_HOME/bin/setenv.sh

echo taking first thread dump...
kill -3 `cat $CATALINA_PID`

echo waiting 10 seconds
sleep 10;

echo taking second thread dump...
kill -3 `cat $CATALINA_PID`

echo DONE