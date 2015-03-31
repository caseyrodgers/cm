#!/bin/csh
set server=$1
if ($server == "") then
    set server=localhost
endif
echo 'Checking CM install at: ' $server
wget http://$server/assets/util/cm_system_status.jsp -O -
