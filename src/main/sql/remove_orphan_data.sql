

delete from CM_USER_PROGRAM
where user_id not in (
  select uid
  from HA_USER
);


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

delete from  HA_TEST_RUN_WIDGET_INPUT_ANSWERS
where run_id not in (
                        select run_id
                        from   HA_TEST_RUN
                        );                          

delete from  HA_TEST_RUN_MAX
where run_id not in (
                        select run_id
                        from   HA_TEST_RUN
                        );                          

delete from  HA_SOLUTION_CONTEXT
where run_id not in ( select run_id
                      from   HA_TEST_RUN
                    );

delete from  HA_USER_EXTENDED
where user_id not in ( select uid
                   from   HA_USER );                          

delete from  HA_USER_LOGIN
where user_type='STUDENT' and user_id not in ( select uid from HA_USER );

delete from  HA_USER_LOGIN_LAST
where user_id not in ( select uid from HA_USER
                       union
                       select aid from HA_ADMIN );                          

delete from  HA_USER_SETTINGS
where user_id not in ( select uid from HA_USER );

delete from CM_ASSIGNMENT_PID_ANSWERS
where user_id not in ( select uid from HA_USER );

delete from CM_ASSIGNMENT_PID_CONTEXT
where uid not in ( select uid from HA_USER );

delete from CM_ASSIGNMENT_PID_STATUS
where uid not in ( select uid from HA_USER );

delete from CM_ASSIGNMENT_PID_WHITEBOARD
where user_id not in ( select uid from HA_USER );

delete from CM_ASSIGNMENT_USER
where uid not in ( select uid from HA_USER );

delete from CM_ASSIGNMENT_USERS_SPECIFIED
where uid not in ( select uid from HA_USER );
