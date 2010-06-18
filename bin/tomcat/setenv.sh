#!/bin/sh
#
export CATALINA_OPTS=-Dcm.log.base=~hotmath/tomcat2

JAVA_OPTS=-server -Xms1024m -Xmx1024m -XX:MaxPermSize=128m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -XX:+UseConcMarkSweepGC