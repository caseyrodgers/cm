#!/bin/bash
export CATALINA_HOME=~hotmath/tomcat2
while true;do
  $CATALINA_HOME/bin/catalina.sh run >> $CATALINA_HOME/logs/catalina.out 2>&1
  sleep 5;
done;