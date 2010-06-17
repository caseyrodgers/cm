#!/bin/bash
#
# this script records the outout of 'ps' and 'top' commands in ~hotmath/cm-logs/
# in file_alarm_PID.out.YYYY-MM-DD* files, where PID is the PID of this script.
# 
# if a PID (tc2_pid) is supplied on the command line, then the cm_thread_dump.sh script is invoked with that PID.
#

pid=$$;

e=1;

ps_out_base=~hotmath/cm-logs/fire_alarm_$pid.out.`date +%c-%m-%d | awk '{print $5}'`

echo "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =" >> $ps_out_base;
echo `date` >> $ps_out_base;
echo " " >> $ps_out_base;
ps -eaf | sort >> $ps_out_base;

tc2_pid=$1;

if [ -z $tc2_pid ]; then
    echo  
    echo "PID must be supplied: $0 <PID>, to obtain thread dump" >> $ps_out_base
    echo  
else
    d=`/usr/bin/dirname $0`
    $d/cm_thread_dump.sh $tc2_pid
fi

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