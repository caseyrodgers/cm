#!/bin/bash
#export CATALINA_HOME=~hotmath/tomcat2

#. $CATALINA_HOME/bin/setenv.sh

tc2_pid=`ps -eaf | grep tomcat2 | grep -v grep | grep Bootstrap | awk '{print $2}'`

echo taking first thread dump... $tc2_pid
kill -3 $tc2_pid

echo waiting 10 seconds
sleep 10;

echo taking second thread dump... $tc2_pid
kill -3 $tc2_pid

echo DONE