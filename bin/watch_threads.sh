while true; do
    date
    wget http://hotmath.kattare.com/system/hm_tc_thread_pool.jsp -O - 2> /dev/null | grep current
    sleep 60
done

