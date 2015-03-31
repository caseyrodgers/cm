#!/bin/bash

tc2_pid=$1;

if [ -z $tc2_pid ]; then
    echo "PID must be supplied: $0 <PID>, exiting..."
    exit 1;
fi

d=`/usr/bin/dirname $0`

$d/cm_thread_dump.sh $tc2_pid

sleep 3;

export CATALINA_HOME=~hotmath/tomcat2

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

echo "killing (SIGKILL) Catalina PID: $tc2_pid"

/bin/kill -9 $tc2_pid
