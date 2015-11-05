
--
-- Final view structure for view `V_SUBSCRIBERS_SERVICES_FLAT`
--

/*!50001 DROP TABLE IF EXISTS `V_SUBSCRIBERS_SERVICES_FLAT`*/;
/*!50001 DROP VIEW IF EXISTS `V_SUBSCRIBERS_SERVICES_FLAT`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_SUBSCRIBERS_SERVICES_FLAT` AS select `s`.`ID` AS `ID`,`s`.`PASSWORD` AS `PASSWORD`,`s`.`DATE_CREATED` AS `DATE_CREATED`,`s`.`TYPE` AS `type`,`s`.`STATUS` AS `STATUS`,`s`.`RESPONSIBLE_NAME` AS `RESPONSIBLE_NAME`,`s`.`STUDENT_EMAIL` AS `STUDENT_EMAIL`,`s`.`STUDENT_ZIP` AS `STUDENT_ZIP`,`s`.`SCHOOL_TYPE` AS `SCHOOL_TYPE`,`s`.`LINKED_ACCOUNT` AS `LINKED_ACCOUNT`,`s`.`TIME_ZONE_CODE` AS `TIME_ZONE_CODE`,`s`.`sales_zone` AS `SALES_ZONE`,`ss`.`usage_count` AS `USAGE_COUNT`,`info`.`cm_pass_percent_all` AS `cm_pass_percent_all`,`info`.`cm_pass_percent_week` AS `cm_pass_percent_week`,`ss`.`date_created` AS `service_created`,`ss`.`service_name` AS `service_name`,`ss`.`status` AS `service_status`,`ss`.`date_expire` AS `service_expire`,`ss`.`referrer` AS `service_referrer`,`s`.`comments` AS `subscriber_comments`,`ss`.`comments` AS `service_comments`,`pr`.`title` AS `pilot_teacher_title`,`pr`.`phone` AS `pilot_phone`,`pr`.`cc_emails` AS `pilot_emails`,`pr`.`comments` AS `pilot_challenges`,`pr`.`motivation` AS `pilot_motivation`,`pr`.`enrollment` AS `pilot_enrollment`,`ct`.`cnt_admin_logins` AS `cnt_admin_logins`,`ct`.`cnt_student_logins` AS `cnt_student_logins`,`ct`.`cnt_assignments` AS `cnt_assignments`,`h`.`passcode` AS `admin_password` from (((((`SUBSCRIBERS` `s` join `SUBSCRIBERS_SERVICES` `ss` on((`s`.`ID` = `ss`.`subscriber_id`))) left join `SUBSCRIBERS_INFO_temp` `info` on((`info`.`id` = `s`.`ID`))) left join `HA_ADMIN_PILOT_REQUEST` `pr` on((`pr`.`subscriber_id` = `s`.`ID`))) left join `HA_ADMIN` `h` on((`h`.`subscriber_id` = `s`.`ID`))) left join `CM_ADMIN_INFO_temp` `ct` on((`ct`.`admin_id` = `h`.`aid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_ADMIN_UTILIZATION`
--

/*!50001 DROP TABLE IF EXISTS `v_ADMIN_UTILIZATION`*/;
/*!50001 DROP VIEW IF EXISTS `v_ADMIN_UTILIZATION`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_ADMIN_UTILIZATION` AS select `u`.`admin_id` AS `admin_id`,count(0) AS `count(*)` from (((`HA_TEST_RUN_INMH_USE` `i` join `HA_TEST_RUN` `r` on((`r`.`run_id` = `i`.`run_id`))) join `HA_TEST` `t` on((`t`.`test_id` = `r`.`test_id`))) join `HA_USER` `u` on((`u`.`uid` = `t`.`user_id`))) group by `u`.`admin_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_ASSIGNMENT_USERS`
--

/*!50001 DROP TABLE IF EXISTS `v_ASSIGNMENT_USERS`*/;
/*!50001 DROP VIEW IF EXISTS `v_ASSIGNMENT_USERS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_ASSIGNMENT_USERS` AS select `a`.`assign_key` AS `assign_key`,`a`.`has_specified_users` AS `has_specified_users`,`u`.`uid` AS `uid` from ((`HA_USER` `u` join `CM_GROUP` `g` on((`g`.`id` = `u`.`group_id`))) join `CM_ASSIGNMENT` `a` on((`a`.`group_id` = `g`.`id`))) where ((`a`.`has_specified_users` = 0) and (`u`.`is_active` = 1) and (`u`.`is_auto_create_template` = 0)) union select `s`.`assign_key` AS `assign_key`,1 AS `has_specified_users`,`s`.`uid` AS `uid` from `CM_ASSIGNMENT_USERS_SPECIFIED` `s` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_CM_PILOT`
--

/*!50001 DROP TABLE IF EXISTS `v_CM_PILOT`*/;
/*!50001 DROP VIEW IF EXISTS `v_CM_PILOT`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_CM_PILOT` AS select `s`.`ID` AS `ID`,`s`.`PASSWORD` AS `PASSWORD`,`s`.`DATE_CREATED` AS `DATE_CREATED`,`s`.`RESPONSIBLE_NAME` AS `RESPONSIBLE_NAME`,`s`.`STUDENT_EMAIL` AS `STUDENT_EMAIL`,`s`.`STUDENT_ZIP` AS `STUDENT_ZIP`,`s`.`SCHOOL_TYPE` AS `SCHOOL_TYPE`,`s`.`sales_zone` AS `SALES_ZONE`,`ss`.`date_created` AS `service_created`,`ss`.`date_expire` AS `service_expire`,`pr`.`title` AS `pilot_teacher_title`,`pr`.`phone` AS `pilot_phone`,replace(replace(`pr`.`comments`,'\\\n',' '),'\\\\r','') AS `pilot_challenges`,replace(replace(`pr`.`cc_emails`,'\\\n',' '),'\\\\r','') AS `pilot_emails`,replace(replace(`pr`.`motivation`,'\\\n',' '),'\\\\r','') AS `pilot_motivation`,`pr`.`enrollment` AS `pilot_enrollment` from (((`SUBSCRIBERS` `s` join `SUBSCRIBERS_SERVICES` `ss` on((`s`.`ID` = `ss`.`subscriber_id`))) left join `SUBSCRIBERS_INFO_temp` `info` on((`info`.`id` = `s`.`ID`))) left join `HA_ADMIN_PILOT_REQUEST` `pr` on((`pr`.`subscriber_id` = `s`.`ID`))) where ((`ss`.`service_name` = 'catchup') and (`s`.`TYPE` = 'ST') and (`ss`.`date_created` > (curdate() - interval 30 day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_CUSTOM_PROGRAM_INFO`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_CUSTOM_PROGRAM_INFO`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_CUSTOM_PROGRAM_INFO`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_CUSTOM_PROGRAM_INFO` AS select `cp`.`admin_id` AS `admin_id`,`cp`.`id` AS `id`,`cp`.`name` AS `name`,`cp`.`is_template` AS `is_template`,`cp`.`is_archived` AS `is_archived`,`cp`.`archive_date` AS `archive_date`,`up`.`id` AS `prog_id`,count(`up`.`id`) AS `assigned_count`,count(`t`.`test_id`) AS `inuse_count`,`cp`.`load_order` AS `load_order` from ((`HA_CUSTOM_PROGRAM` `cp` left join `CM_USER_PROGRAM` `up` on((`up`.`custom_program_id` = `cp`.`id`))) left join `HA_TEST` `t` on((`t`.`user_prog_id` = `up`.`id`))) group by `cp`.`id` union select NULL AS `admin_id`,`cp`.`id` AS `id`,`cp`.`name` AS `name`,`cp`.`is_template` AS `is_template`,0 AS `is_archived`,NULL AS `archive_date`,0 AS `prog_id`,0 AS `assigned_count`,0 AS `inuse_count`,`cp`.`load_order` AS `load_order` from `HA_CUSTOM_PROGRAM` `cp` where (`cp`.`is_template` = 1) order by `is_template` desc,`load_order`,`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_CURRENT_STATUS`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_CURRENT_STATUS`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_CURRENT_STATUS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_CURRENT_STATUS` AS select `t`.`test_id` AS `test_id`,`t`.`pid` AS `pid`,`c`.`is_correct` AS `is_correct`,`c`.`response_number` AS `response_number` from (`HA_TEST_IDS` `t` left join `HA_TEST_CURRENT` `c` on(((`t`.`test_id` = `c`.`test_id`) and (`t`.`pid` = `c`.`pid`)))) order by `t`.`tid` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_USAGE`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_USAGE`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_USAGE`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_USAGE` AS select `a`.`subscriber_id` AS `subscriber_id`,count(0) AS `view_count` from ((`v_HA_USER_INMH_VIEWS_TOTAL` `v` join `HA_USER` `u` on((`u`.`uid` = `v`.`uid`))) join `HA_ADMIN` `a` on((`u`.`admin_id` = `a`.`aid`))) group by `a`.`subscriber_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_USER_ACTIVE`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_USER_ACTIVE`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_USER_ACTIVE`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_USER_ACTIVE` AS select `HA_USER`.`uid` AS `uid`,`HA_USER`.`admin_id` AS `admin_id`,`HA_USER`.`user_name` AS `user_name`,`HA_USER`.`user_passcode` AS `user_passcode`,`HA_USER`.`user_email` AS `user_email`,`HA_USER`.`test_def_id` AS `test_def_id`,`HA_USER`.`active_run_id` AS `active_run_id`,`HA_USER`.`active_test_id` AS `active_test_id`,`HA_USER`.`active_segment` AS `active_segment`,`HA_USER`.`prescription_session` AS `prescription_session`,`HA_USER`.`test_def_chapter` AS `test_def_chapter`,`HA_USER`.`is_active` AS `is_active`,`HA_USER`.`group_id` AS `group_id`,`HA_USER`.`active_run_session` AS `active_run_session`,`HA_USER`.`user_prog_id` AS `user_prog_id`,`HA_USER`.`gui_background_style` AS `gui_background_style`,`HA_USER`.`is_show_work_required` AS `is_show_work_required`,`HA_USER`.`is_tutoring_available` AS `is_tutoring_available`,`HA_USER`.`is_auto_create_template` AS `is_auto_create_template`,`HA_USER`.`active_segment_slot` AS `active_segment_slot`,`HA_USER`.`date_created` AS `date_created`,`HA_USER`.`is_demo` AS `is_demo`,`HA_USER`.`is_self_pay` AS `is_self_pay` from `HA_USER` where (`HA_USER`.`is_active` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_USER_INMH_VIEWS_TOTAL`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_USER_INMH_VIEWS_TOTAL`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_USER_INMH_VIEWS_TOTAL`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_USER_INMH_VIEWS_TOTAL` AS select `u`.`uid` AS `uid`,`v`.`use_id` AS `use_id`,`v`.`run_id` AS `run_id`,`v`.`item_type` AS `item_type`,`v`.`item_file` AS `item_file`,`v`.`view_time` AS `view_time`,`v`.`session_number` AS `session_number` from (((`HA_TEST_RUN_INMH_USE` `v` join `HA_TEST_RUN` `r` on((`v`.`run_id` = `r`.`run_id`))) join `HA_TEST` `t` on((`r`.`test_id` = `t`.`test_id`))) join `HA_USER` `u` on((`t`.`user_id` = `u`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-11-05 10:56:06
