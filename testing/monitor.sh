while true;do
  wget http://localhost:8081/resources/util/cm_system_status.jsp -O - 2> monitor.out | grep status;
  sleep 10;
done;
