#!/bin/bash

tc2_pid=$1;

d=`/usr/bin/dirname $0`

$d/cm_thread_dump.sh $tc2_pid

sleep 3;

export CATALINA_HOME=~hotmath/tomcat2

#. ${CATALINA_HOME}/bin/setenv.sh

echo CATALINA_HOME: ${CATALINA_HOME}

if [ -f ${CATALINA_HOME}/logs/catalina.out ]; then
    catalina_out="${CATALINA_HOME}/logs/catalina.out.`date +%c-%m-%d | awk '{print $5}'`"

    e=1;
    cat_out=$catalina_out
    while [ -f $cat_out ]; do
      cat_out=$catalina_out.$e
      e=`expr $e + 1`
    done

    echo "preserving catalina.out as $cat_out"

    mv ${CATALINA_HOME}/logs/catalina.out $cat_out
else
    echo ${CATALINA_HOME}/logs/catalina.out does not exist
fi

if [ -z $tc2_pid ]; then
    tc2_pid=`/usr/bin/ps -eaf | /usr/bin/grep tomcat2 | /usr/bin/grep -v grep | /usr/bin/grep Bootstrap | /usr/bin/awk '{print $2}'`

    if [ -z $tc2_pid ]; then
        echo "cant determine CM's Tomcat instance PID, exiting..."
        exit 1;
    fi
fi

echo "killing (SIGKILL) Catalina PID: $tc2_pid"

/bin/kill -9 $tc2_pid
