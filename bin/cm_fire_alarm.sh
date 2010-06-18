#!/bin/bash
#
# this script records the output of 'ps' and 'top' commands in ~hotmath/cm-logs/
# in file_alarm_PID.out.YYYY-MM-DD* files, where PID is the PID of this script.
# 
# if a PID (tc2_pid) is supplied on the command line, then the cm_thread_dump.sh script is invoked with that PID.
#
# if a PID is not supplied, then it is obtained from ~hotmath/cm.pid if available
#

my_pid=$$;

ps_out_base=~hotmath/cm-logs/fire_alarm_$my_pid.out.`date +%c-%m-%d | awk '{print $5}'`

echo "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =" >> $ps_out_base;
echo `date` >> $ps_out_base;
echo  >> $ps_out_base;
ps -eaf | sort >> $ps_out_base;
echo  >> $ps_out_base;
echo "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =" >> $ps_out_base;
top -b -n 1 >> $ps_out_base;
echo  >> $ps_out_base;

tc2_pid=$1;

if [ -z $tc2_pid ]; then
    # attempt to obtain PID
    tc2_pid=`cat ~hotmath/cm.pid`
    if [ -z $tc2_pid ]; then
        echo PID could not be obtained from ~hotmath/cm.pid - thread dump not possible >> $ps_out_base
        echo  >> $ps_out_base
    else
        d=`/usr/bin/dirname $0`
        $d/cm_thread_dump.sh $tc2_pid 
    fi
else
    d=`/usr/bin/dirname $0`
    $d/cm_thread_dump.sh $tc2_pid
fi

e=0;

while [ true ]; do
    sleep 30;
    e=`expr $e + 1`;
    echo "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =" >> $ps_out_base.$e;
    echo `date` >> $ps_out_base.$e;
    echo  >> $ps_out_base.$e;
    ps -eaf | sort >> $ps_out_base.$e;
    echo  >> $ps_out_base.$e;
    echo "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =" >> $ps_out_base.$e;
    top -b -n 1 >> $ps_out_base.$e;
    echo  >> $ps_out_base.$e;
done
