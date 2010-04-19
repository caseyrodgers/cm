delete from HA_TEST
where  user_id not in (
                        select user_id
                       from   HA_USER);

delete from HA_TEST_CURRENT
where  test_id not in (
                       select test_id
                       from   HA_TEST); 
                       
                       
delete from HA_TEST_IDS
where test_id  not in (
                       select test_id
                       from   HA_TEST); 
                       

delete from HA_TEST_RUN
where test_id not in (
                       select test_id
                       from HA_TEST);

                                              
delete from  HA_TEST_RUN_WHITEBOARD                                                                                                                                       
where run_id not in (
                        select run_id
                        from   HA_TEST_RUN
                        );
                        
delete from  HA_TEST_RUN_INMH_USE                                                                                                                                       
where run_id not in (
                        select run_id
                        from   HA_TEST_RUN
                        );                        
                        
                        
delete from  HA_TEST_RUN_LESSON                                                                                                                                       
where run_id not in (
                        select run_id
                        from   HA_TEST_RUN
                        );                           
                        
delete from  HA_TEST_RUN_LESSON_PID                                                                                                                                       
where run_id not in (
                        select run_id
                        from   HA_TEST_RUN
                        );                                                   
                        
                        
delete from  HA_TEST_RUN_RESULTS
where run_id not in (
                        select run_id
                        from   HA_TEST_RUN
                        );                          
