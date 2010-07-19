#!/bin/sh
#
# return current CPU usage for CM (tomcat instance), MySQL, or httpd (Apache)
#

case $1 in

    mysql)
        pid=`ps -eo pid,user,args | grep mysql | grep port | grep -v grep | awk '{print $1}'`;
        pidstat 1 1 -p $pid | grep Average | awk '{print $6}';
        ;;
    
    cm)
        pid=`cat /home/hotmath/cm.pid`;
        pidstat 1 1 -p $pid | grep Average | awk '{print $6}';
        ;;
    
    httpd)
    
        hpids=`ps -eo pid,user,args | grep httpd | grep apache | grep -v grep | awk '{print $1}'`;
        echo $hpids;
        
        pidarg=`echo $hpids | sed 's/ /,/g'`;
        echo $pidarg;
        
        hcpu=`pidstat 1 1 -p $pidarg | grep Average | grep -v CPU | awk '{print $6}' | sed 's/\.//'`;
        echo $hcpu;
        
        cpu=0;
        for c in $hcpu; do
            echo $c;
            cpu=`expr $p + $c`;
        done;
        echo $cpu;
    
        w=`expr $cpu / 100`;
        echo w: $w
        
        d=`expr $cpu % 100`;
        echo d: $d;
        
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