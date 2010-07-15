#!/bin/sh
#
# return current CPU usage for CM (tomcat instance), MySQL, or httpd (Apache)
#

case $1 in

    mysql)
        ps -eo pcpu,pid,user,args | grep mysql | grep port | grep -v grep | awk '{print $1}';;
    
    cm)
        ps -eo pcpu,pid,user,args | grep `cat ${HOME}/cm.pid` | grep -v grep | awk '{print $1}';;
    
    httpd)
    
        hps=`ps -eo pcpu,pid,user,args | grep httpd | grep apache | grep -v grep | awk '{print $1}' | sed 's/\.//'`
        p=0;
        for c in $hps; do
            p=`expr $p + $c`;
        done;
        w=`expr $p / 10`;
        d=`expr $p % 10`;
        echo $w.$d;;
    
    *)
        echo "usage: $0 {cm | mysql | httpd}";
        exit 1;;

esac;

exit 0;