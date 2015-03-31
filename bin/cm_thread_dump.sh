#!/bin/bash

tc2_pid=$1;

if [ -z $tc2_pid ]; then
    echo "PID must be supplied: $0 <PID>, exiting..."
    exit 1;
fi

echo taking first thread dump... $tc2_pid
/bin/kill -3 $tc2_pid

echo waiting 10 seconds
sleep 10;

echo taking second thread dump... $tc2_pid
/bin/kill -3 $tc2_pid

echo DONE