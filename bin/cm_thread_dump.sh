#!/bin/bash
#export CATALINA_HOME=~hotmath/tomcat2

#. $CATALINA_HOME/bin/setenv.sh

tc2_pid=$1;

if [ -z $tc2_pid ]; then
    tc2_pid=`/usr/bin/ps -eaf | grep tomcat2 | /usr/bin/grep -v grep | /usr/bin/grep Bootstrap | /usr/bin/awk '{print $2}'`

    if [ -z $tc2_pid ]; then
        echo "can't determine CM's tomcat instance PID, exiting..."
        exit 1;
    fi
fi

echo taking first thread dump... $tc2_pid
/bin/kill -3 $tc2_pid

echo waiting 10 seconds
sleep 10;

echo taking second thread dump... $tc2_pid
/bin/kill -3 $tc2_pid

echo DONE