#!/bin/sh
#
# return current CPU usage for CM (tomcat instance), MySQL, or httpd (Apache)
#

case $1 in

    mysql)
        pid=`ps -eo pid,user,args | grep mysql | grep port | grep -v grep | awk '{print $1}'`;
        cpu=`pidstat 1 1 -p $pid | grep Average | awk '{print $6}'`;
        echo -n $cpu;
        ;;
    
    cm)
        pid=`cat /home/hotmath/cm.pid`;
        cpu=`pidstat 1 1 -p $pid | grep Average | awk '{print $6}'`;
        echo -n $cpu;
        ;;
    
    httpd)
    
        hpids=`ps -eo pid,user,args | grep httpd | grep apache | grep -v grep | awk '{print $1}'`;
        pidarg=`echo $hpids | sed 's/ /,/g'`;
        hcpu=`pidstat 1 1 -p $pidarg | grep Average | grep -v CPU | awk '{print $6}' | sed 's/\.//'`;
        
        cpu=0;
        for c in $hcpu; do
            cpu=`expr $cpu + $c`;
        done;
    
        w=`expr $cpu / 100`;
        d=`expr $cpu % 100`;
        
        if [ $d -ge 10 ]; then
            echo -n $w.$d;
        else
            echo -n $w.0$d;
        fi
        ;;
    
    *)
        echo "usage: $0 {cm | mysql | httpd}";
        exit 1;;

esac;

exit 0;