#!/bin/bash
export CATALINA_HOME=~hotmath/tomcat2

. $CATALINA_HOME/bin/setenv.sh

kill -3 `cat $CATALINA_PID`

sleep 10;

kill -3 `cat $CATALINA_PID`