while true; do
    wget http://localhost:8081/resources/util/cm_system_status.jsp -O - 2> /dev/null | grep HmConnection
    sleep 60
done

