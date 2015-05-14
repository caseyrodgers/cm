-- MySQL dump 10.13  Distrib 5.1.66, for unknown-linux-gnu (x86_64)
--
-- Host: localhost    Database: hotmath_live_2
-- ------------------------------------------------------
-- Server version	5.5.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Temporary table structure for view `SUB_INFO`
--

DROP TABLE IF EXISTS `SUB_INFO`;
/*!50001 DROP VIEW IF EXISTS `SUB_INFO`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `SUB_INFO` (
  `id` varchar(50),
  `type` varchar(50),
  `password` varchar(50),
  `student_email` varchar(50),
  `linked_account` varchar(50),
  `subscriber_id` varchar(100),
  `date_created` date,
  `status` varchar(50),
  `payment_method` varchar(50),
  `date_expire` date,
  `referrer` varchar(50),
  `service_name` varchar(50),
  `comments` text,
  `ssid` int(11),
  `usage_count` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `SUB_SERVICES`
--

DROP TABLE IF EXISTS `SUB_SERVICES`;
/*!50001 DROP VIEW IF EXISTS `SUB_SERVICES`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `SUB_SERVICES` (
  `id` varchar(50),
  `student_email` varchar(50),
  `comments` text,
  `service_name` varchar(50),
  `date_expire` date,
  `status` varchar(50)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_ALL_FAILED_QUIZZES`
--

DROP TABLE IF EXISTS `V_ALL_FAILED_QUIZZES`;
/*!50001 DROP VIEW IF EXISTS `V_ALL_FAILED_QUIZZES`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_ALL_FAILED_QUIZZES` (
  `test_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `user_id` int(11),
  `run_id` int(11),
  `quiz_percent` decimal(17,4),
  `run_time` datetime,
  `run_day` int(3),
  `add_days` int(3)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_ALL_FAILED_QUIZZES_X`
--

DROP TABLE IF EXISTS `V_ALL_FAILED_QUIZZES_X`;
/*!50001 DROP VIEW IF EXISTS `V_ALL_FAILED_QUIZZES_X`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_ALL_FAILED_QUIZZES_X` (
  `test_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `user_id` int(11),
  `run_id` int(11),
  `quiz_percent` decimal(17,4),
  `run_time` datetime,
  `day_of_school_year` int(6)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_ALL_PASSED_QUIZZES`
--

DROP TABLE IF EXISTS `V_ALL_PASSED_QUIZZES`;
/*!50001 DROP VIEW IF EXISTS `V_ALL_PASSED_QUIZZES`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_ALL_PASSED_QUIZZES` (
  `test_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `user_id` int(11),
  `run_id` int(11),
  `quiz_percent` decimal(17,4),
  `run_time` datetime,
  `run_day` int(3),
  `add_days` int(3)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_ALL_PASSED_QUIZZES_X`
--

DROP TABLE IF EXISTS `V_ALL_PASSED_QUIZZES_X`;
/*!50001 DROP VIEW IF EXISTS `V_ALL_PASSED_QUIZZES_X`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_ALL_PASSED_QUIZZES_X` (
  `test_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `user_id` int(11),
  `run_id` int(11),
  `quiz_percent` decimal(17,4),
  `run_time` datetime,
  `day_of_school_year` int(6)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_EARLIEST_FAILED_QUIZ`
--

DROP TABLE IF EXISTS `V_EARLIEST_FAILED_QUIZ`;
/*!50001 DROP VIEW IF EXISTS `V_EARLIEST_FAILED_QUIZ`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_EARLIEST_FAILED_QUIZ` (
  `test_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `user_id` int(11),
  `min_runid` int(11),
  `run_time` datetime,
  `run_day` int(3),
  `add_days` int(3),
  `quiz_percent` decimal(17,4)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_EARLIEST_FAILED_QUIZ_X`
--

DROP TABLE IF EXISTS `V_EARLIEST_FAILED_QUIZ_X`;
/*!50001 DROP VIEW IF EXISTS `V_EARLIEST_FAILED_QUIZ_X`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_EARLIEST_FAILED_QUIZ_X` (
  `test_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `user_id` int(11),
  `min_runid` int(11),
  `run_time` datetime,
  `day_of_school_year` int(6),
  `quiz_percent` decimal(17,4)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_FAILED_QUIZ_COUNT`
--

DROP TABLE IF EXISTS `V_FAILED_QUIZ_COUNT`;
/*!50001 DROP VIEW IF EXISTS `V_FAILED_QUIZ_COUNT`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_FAILED_QUIZ_COUNT` (
  `user_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `fail_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_FAIL_AND_NOT_PASS_QUIZ_DATA`
--

DROP TABLE IF EXISTS `V_FAIL_AND_NOT_PASS_QUIZ_DATA`;
/*!50001 DROP VIEW IF EXISTS `V_FAIL_AND_NOT_PASS_QUIZ_DATA`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_FAIL_AND_NOT_PASS_QUIZ_DATA` (
  `user_id` int(11),
  `test_segment` int(11),
  `test_def_id` int(11),
  `user_prog_id` int(11),
  `test_id` int(11),
  `fail_percent` decimal(17,4),
  `fail_day` int(6),
  `pass_percent` decimal(17,4)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`
--

DROP TABLE IF EXISTS `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`;
/*!50001 DROP VIEW IF EXISTS `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA` (
  `user_id` int(11),
  `test_segment` int(11),
  `test_def_id` int(11),
  `user_prog_id` int(11),
  `test_id` int(11),
  `fail_percent` decimal(17,4),
  `fail_day` int(6),
  `pass_percent` decimal(17,4)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_FAIL_THEN_PASS_QUIZ_DATA`
--

DROP TABLE IF EXISTS `V_FAIL_THEN_PASS_QUIZ_DATA`;
/*!50001 DROP VIEW IF EXISTS `V_FAIL_THEN_PASS_QUIZ_DATA`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_FAIL_THEN_PASS_QUIZ_DATA` (
  `user_id` int(11),
  `test_segment` int(11),
  `test_def_id` int(11),
  `user_prog_id` int(11),
  `test_id` int(11),
  `fail_percent` decimal(17,4),
  `fail_day` int(6),
  `pass_percent` decimal(17,4),
  `pass_day` int(6)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_INMH_ACTIVITY`
--

DROP TABLE IF EXISTS `V_INMH_ACTIVITY`;
/*!50001 DROP VIEW IF EXISTS `V_INMH_ACTIVITY`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_INMH_ACTIVITY` (
  `user_id` int(11),
  `day_of_school_year` int(6)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_LESSON_ACTIVITY`
--

DROP TABLE IF EXISTS `V_LESSON_ACTIVITY`;
/*!50001 DROP VIEW IF EXISTS `V_LESSON_ACTIVITY`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_LESSON_ACTIVITY` (
  `user_id` int(11),
  `day_of_school_year` int(6)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_PASSED_FAILED_QUIZZES`
--

DROP TABLE IF EXISTS `V_PASSED_FAILED_QUIZZES`;
/*!50001 DROP VIEW IF EXISTS `V_PASSED_FAILED_QUIZZES`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_PASSED_FAILED_QUIZZES` (
  `user_id` int(11),
  `test_segment` int(11),
  `test_def_id` int(11),
  `user_prog_id` int(11),
  `fail_percent` decimal(17,4),
  `pass_percent` decimal(17,4)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_QUIZ_START_ACTIVITY`
--

DROP TABLE IF EXISTS `V_QUIZ_START_ACTIVITY`;
/*!50001 DROP VIEW IF EXISTS `V_QUIZ_START_ACTIVITY`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_QUIZ_START_ACTIVITY` (
  `user_id` int(11),
  `day_of_school_year` int(6)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_QUIZ_TAKE_ACTIVITY`
--

DROP TABLE IF EXISTS `V_QUIZ_TAKE_ACTIVITY`;
/*!50001 DROP VIEW IF EXISTS `V_QUIZ_TAKE_ACTIVITY`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_QUIZ_TAKE_ACTIVITY` (
  `user_id` int(11),
  `day_of_school_year` int(6)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_SUBSCRIBERS_SERVICES_FLAT`
--

DROP TABLE IF EXISTS `V_SUBSCRIBERS_SERVICES_FLAT`;
/*!50001 DROP VIEW IF EXISTS `V_SUBSCRIBERS_SERVICES_FLAT`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_SUBSCRIBERS_SERVICES_FLAT` (
  `ID` varchar(50),
  `PASSWORD` varchar(50),
  `DATE_CREATED` date,
  `type` varchar(50),
  `STATUS` varchar(50),
  `RESPONSIBLE_NAME` varchar(50),
  `STUDENT_EMAIL` varchar(50),
  `STUDENT_ZIP` varchar(50),
  `SCHOOL_TYPE` varchar(50),
  `LINKED_ACCOUNT` varchar(50),
  `TIME_ZONE_CODE` varchar(100),
  `SALES_ZONE` varchar(25),
  `USAGE_COUNT` int(11),
  `cm_pass_percent_all` int(1),
  `cm_pass_percent_week` int(1),
  `service_created` date,
  `service_name` varchar(50),
  `service_status` varchar(50),
  `service_expire` date,
  `service_referrer` varchar(50),
  `subscriber_comments` text,
  `service_comments` text,
  `pilot_teacher_title` varchar(100),
  `pilot_phone` varchar(100),
  `pilot_emails` varchar(1500),
  `pilot_challenges` text,
  `pilot_motivation` varchar(250),
  `pilot_enrollment` int(11),
  `cnt_admin_logins` bigint(21),
  `cnt_student_logins` bigint(21),
  `cnt_assignments` bigint(21),
  `admin_password` varchar(100)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `V_USER_LESSON_COUNT`
--

DROP TABLE IF EXISTS `V_USER_LESSON_COUNT`;
/*!50001 DROP VIEW IF EXISTS `V_USER_LESSON_COUNT`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `V_USER_LESSON_COUNT` (
  `test_id` int(11),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `test_segment` int(11),
  `user_id` int(11),
  `lesson_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_ADMIN_UTILIZATION`
--

DROP TABLE IF EXISTS `v_ADMIN_UTILIZATION`;
/*!50001 DROP VIEW IF EXISTS `v_ADMIN_UTILIZATION`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_ADMIN_UTILIZATION` (
  `admin_id` int(11),
  `count(*)` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_ASSIGNMENT_PIDS`
--

DROP TABLE IF EXISTS `v_ASSIGNMENT_PIDS`;
/*!50001 DROP VIEW IF EXISTS `v_ASSIGNMENT_PIDS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_ASSIGNMENT_PIDS` (
  `pid1` varchar(150),
  `pid2` varchar(150),
  `id` int(11),
  `uid` int(11),
  `assign_key` int(11),
  `status` varchar(100)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_ASSIGNMENT_PID_STATUS`
--

DROP TABLE IF EXISTS `v_ASSIGNMENT_PID_STATUS`;
/*!50001 DROP VIEW IF EXISTS `v_ASSIGNMENT_PID_STATUS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_ASSIGNMENT_PID_STATUS` (
  `pid1` varchar(150),
  `pid2` varchar(150),
  `id` int(11),
  `uid` int(11),
  `assign_key` int(11),
  `status` varchar(100),
  `is_personalized` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_ASSIGNMENT_USERS`
--

DROP TABLE IF EXISTS `v_ASSIGNMENT_USERS`;
/*!50001 DROP VIEW IF EXISTS `v_ASSIGNMENT_USERS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_ASSIGNMENT_USERS` (
  `assign_key` int(11),
  `has_specified_users` bigint(20),
  `uid` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_CM_ADMIN_ESSENTIALS_COUNT`
--

DROP TABLE IF EXISTS `v_CM_ADMIN_ESSENTIALS_COUNT`;
/*!50001 DROP VIEW IF EXISTS `v_CM_ADMIN_ESSENTIALS_COUNT`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_CM_ADMIN_ESSENTIALS_COUNT` (
  `admin_id` int(11),
  `essentials_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_CM_PILOT`
--

DROP TABLE IF EXISTS `v_CM_PILOT`;
/*!50001 DROP VIEW IF EXISTS `v_CM_PILOT`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_CM_PILOT` (
  `ID` varchar(50),
  `PASSWORD` varchar(50),
  `DATE_CREATED` date,
  `RESPONSIBLE_NAME` varchar(50),
  `STUDENT_EMAIL` varchar(50),
  `STUDENT_ZIP` varchar(50),
  `SCHOOL_TYPE` varchar(50),
  `SALES_ZONE` varchar(25),
  `service_created` date,
  `service_expire` date,
  `pilot_teacher_title` varchar(100),
  `pilot_phone` varchar(100),
  `pilot_challenges` text,
  `pilot_emails` text,
  `pilot_motivation` varchar(250),
  `pilot_enrollment` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_CM_STUDENT_LOGIN_STATS`
--

DROP TABLE IF EXISTS `v_CM_STUDENT_LOGIN_STATS`;
/*!50001 DROP VIEW IF EXISTS `v_CM_STUDENT_LOGIN_STATS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_CM_STUDENT_LOGIN_STATS` (
  `subscriber_id` varchar(50),
  `password` varchar(50),
  `account_name` varchar(50),
  `responsible_name` varchar(50),
  `account_type` varchar(50),
  `account_email` varchar(50),
  `login_count` bigint(21),
  `begin_date` varchar(10),
  `end_date` varchar(10)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_CM_STUDENT_RESOURCE_LOGIN_STATS`
--

DROP TABLE IF EXISTS `v_CM_STUDENT_RESOURCE_LOGIN_STATS`;
/*!50001 DROP VIEW IF EXISTS `v_CM_STUDENT_RESOURCE_LOGIN_STATS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_CM_STUDENT_RESOURCE_LOGIN_STATS` (
  `subscriber_id` varchar(50),
  `password` varchar(50),
  `account_name` varchar(50),
  `responsible_name` varchar(50),
  `account_type` varchar(50),
  `account_email` varchar(50),
  `total_usage` decimal(42,0),
  `login_count` bigint(21),
  `begin_date` varchar(10),
  `end_date` varchar(10)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_CM_STUDENT_RESOURCE_STATS`
--

DROP TABLE IF EXISTS `v_CM_STUDENT_RESOURCE_STATS`;
/*!50001 DROP VIEW IF EXISTS `v_CM_STUDENT_RESOURCE_STATS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_CM_STUDENT_RESOURCE_STATS` (
  `subscriber_id` varchar(50),
  `password` varchar(50),
  `account_name` varchar(50),
  `responsible_name` varchar(50),
  `account_type` varchar(50),
  `account_email` varchar(50),
  `total_usage` decimal(42,0),
  `begin_date` varchar(10),
  `end_date` varchar(10)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_CM_SUBSCRIBER_INMH_USE`
--

DROP TABLE IF EXISTS `v_CM_SUBSCRIBER_INMH_USE`;
/*!50001 DROP VIEW IF EXISTS `v_CM_SUBSCRIBER_INMH_USE`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_CM_SUBSCRIBER_INMH_USE` (
  `subscriber_id` varchar(50),
  `password` varchar(50),
  `account_name` varchar(50),
  `responsible_name` varchar(50),
  `account_type` varchar(50),
  `account_email` varchar(50),
  `item_type` varchar(50),
  `item_use` bigint(21),
  `begin_date` varchar(10),
  `end_date` varchar(10)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_CUSTOM_PROGRAM_INFO`
--

DROP TABLE IF EXISTS `v_HA_CUSTOM_PROGRAM_INFO`;
/*!50001 DROP VIEW IF EXISTS `v_HA_CUSTOM_PROGRAM_INFO`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_CUSTOM_PROGRAM_INFO` (
  `admin_id` int(11),
  `id` int(11),
  `name` varchar(100),
  `is_template` int(11),
  `is_archived` bigint(20),
  `archive_date` datetime,
  `prog_id` bigint(20),
  `assigned_count` bigint(21),
  `inuse_count` bigint(21),
  `load_order` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_CURRENT_STATUS`
--

DROP TABLE IF EXISTS `v_HA_TEST_CURRENT_STATUS`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_CURRENT_STATUS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_CURRENT_STATUS` (
  `test_id` int(11),
  `pid` varchar(100),
  `is_correct` int(11),
  `response_number` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_INFO`
--

DROP TABLE IF EXISTS `v_HA_TEST_INFO`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INFO`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_INFO` (
  `uid` int(11),
  `user_name` varchar(50),
  `test_id` int(11),
  `test_create_time` datetime,
  `test_def_id` int(11),
  `test_name` varchar(100),
  `textcode` varchar(100),
  `chapter` varchar(100)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_INMH_VIEWS`
--

DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_INMH_VIEWS` (
  `user_name` varchar(50),
  `item_type` varchar(50),
  `view_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_INMH_VIEWS_INFO`
--

DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_INFO`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_INFO`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_INMH_VIEWS_INFO` (
  `user_name` varchar(50),
  `solution_views` bigint(21),
  `video_views` bigint(21),
  `review_views` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_INMH_VIEWS_review`
--

DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_review`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_review`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_INMH_VIEWS_review` (
  `user_name` varchar(50),
  `review_views` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_INMH_VIEWS_solution`
--

DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_solution`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_solution`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_INMH_VIEWS_solution` (
  `user_name` varchar(50),
  `solution_views` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_INMH_VIEWS_video`
--

DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_video`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_video`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_INMH_VIEWS_video` (
  `user_name` varchar(50),
  `video_views` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_INFO`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_INFO`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_INFO`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_INFO` (
  `uid` int(11),
  `user_name` varchar(50),
  `test_id` int(11),
  `run_id` int(11),
  `run_time` datetime,
  `answered_correct` int(11),
  `answered_incorrect` int(11),
  `not_answered` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_INMH_USE_REQ_PROBS`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_INMH_USE_REQ_PROBS`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_INMH_USE_REQ_PROBS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_INMH_USE_REQ_PROBS` (
  `run_id` int(11),
  `item_file` varchar(255),
  `session_number` int(11),
  `view_time` datetime
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_NOT_PASSING_TOTAL`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_NOT_PASSING_TOTAL`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_NOT_PASSING_TOTAL`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_NOT_PASSING_TOTAL` (
  `total` bigint(21),
  `is_passing` int(1),
  `user_id` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_PASSING_TOTAL`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_PASSING_TOTAL`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_PASSING_TOTAL`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_PASSING_TOTAL` (
  `total` bigint(21),
  `is_passing` int(1),
  `user_id` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_PNP_TOTALS`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_PNP_TOTALS`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_PNP_TOTALS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_PNP_TOTALS` (
  `passing_count` bigint(21),
  `not_passing_count` bigint(21),
  `user_id` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_RESULTS`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_RESULTS`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_RESULTS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_RESULTS` (
  `run_id` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_inter`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_inter`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_inter`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_inter` (
  `max_run_time` datetime,
  `uid` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_last`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_last`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_last`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_last` (
  `uid` int(11),
  `user_name` varchar(50),
  `answered_correct` int(11),
  `answered_incorrect` int(11),
  `not_answered` int(11),
  `last_run_time` datetime
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_TEST_RUN_max`
--

DROP TABLE IF EXISTS `v_HA_TEST_RUN_max`;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_max`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_TEST_RUN_max` (
  `uid` int(11),
  `user_name` varchar(50),
  `answered_correct` int(11),
  `answered_incorrect` int(11),
  `not_answered` int(11),
  `last_run_time` datetime
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_USAGE`
--

DROP TABLE IF EXISTS `v_HA_USAGE`;
/*!50001 DROP VIEW IF EXISTS `v_HA_USAGE`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_USAGE` (
  `subscriber_id` varchar(50),
  `view_count` bigint(21)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_USER_ACTIVE`
--

DROP TABLE IF EXISTS `v_HA_USER_ACTIVE`;
/*!50001 DROP VIEW IF EXISTS `v_HA_USER_ACTIVE`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_USER_ACTIVE` (
  `uid` int(11),
  `admin_id` int(11),
  `user_name` varchar(50),
  `user_passcode` varchar(50),
  `user_email` varchar(50),
  `test_def_id` int(11),
  `active_run_id` int(11),
  `active_test_id` int(11),
  `active_segment` int(11),
  `prescription_session` int(11),
  `test_def_chapter` varchar(100),
  `is_active` int(11),
  `group_id` int(11),
  `active_run_session` int(11),
  `user_prog_id` int(11),
  `gui_background_style` varchar(50),
  `is_show_work_required` int(11),
  `is_tutoring_available` int(11),
  `is_auto_create_template` int(11),
  `active_segment_slot` int(11),
  `date_created` datetime,
  `is_demo` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_USER_ACTIVITY`
--

DROP TABLE IF EXISTS `v_HA_USER_ACTIVITY`;
/*!50001 DROP VIEW IF EXISTS `v_HA_USER_ACTIVITY`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_USER_ACTIVITY` (
  `user_id` int(11),
  `activity_type` varchar(15),
  `activity_key` varchar(255),
  `activity_time` datetime
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_HA_USER_INMH_VIEWS_TOTAL`
--

DROP TABLE IF EXISTS `v_HA_USER_INMH_VIEWS_TOTAL`;
/*!50001 DROP VIEW IF EXISTS `v_HA_USER_INMH_VIEWS_TOTAL`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_HA_USER_INMH_VIEWS_TOTAL` (
  `uid` int(11),
  `use_id` int(11),
  `run_id` int(11),
  `item_type` varchar(50),
  `item_file` varchar(100),
  `view_time` datetime,
  `session_number` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_SOLUTION_XML`
--

DROP TABLE IF EXISTS `v_SOLUTION_XML`;
/*!50001 DROP VIEW IF EXISTS `v_SOLUTION_XML`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_SOLUTION_XML` (
  `PID` varchar(150),
  `SOLUTION_STEPS_XML` text,
  `TUTOR_DEFINE_XML` text
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_SUBSCRIBERS_EXPIRED`
--

DROP TABLE IF EXISTS `v_SUBSCRIBERS_EXPIRED`;
/*!50001 DROP VIEW IF EXISTS `v_SUBSCRIBERS_EXPIRED`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_SUBSCRIBERS_EXPIRED` (
  `id` varchar(50),
  `type` varchar(50),
  `school_type` varchar(50),
  `password` varchar(50),
  `service_count` bigint(21),
  `max_expire` date
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE`
--

DROP TABLE IF EXISTS `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE`;
/*!50001 DROP VIEW IF EXISTS `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE` (
  `subscriber_id` varchar(100),
  `service_count` bigint(21),
  `max_expire` date
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_USER_ACTIVE_PROGRAM`
--

DROP TABLE IF EXISTS `v_USER_ACTIVE_PROGRAM`;
/*!50001 DROP VIEW IF EXISTS `v_USER_ACTIVE_PROGRAM`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_USER_ACTIVE_PROGRAM` (
  `uid` int(11),
  `user_name` varchar(50),
  `user_prog_id` int(11),
  `test_def_id` int(11),
  `date_completed` datetime,
  `test_name` varchar(100),
  `active_segment` int(11),
  `active_test_id` int(11),
  `active_run_id` int(11),
  `active_segment_slot` int(11)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `v_cm_renewed_admin`
--

DROP TABLE IF EXISTS `v_cm_renewed_admin`;
/*!50001 DROP VIEW IF EXISTS `v_cm_renewed_admin`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `v_cm_renewed_admin` (
  `admin_id` int(11),
  `subscriber_id` varchar(50)
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `SUB_INFO`
--

/*!50001 DROP TABLE IF EXISTS `SUB_INFO`*/;
/*!50001 DROP VIEW IF EXISTS `SUB_INFO`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SUB_INFO` AS select `s`.`ID` AS `id`,`s`.`TYPE` AS `type`,`s`.`PASSWORD` AS `password`,`s`.`STUDENT_EMAIL` AS `student_email`,`s`.`LINKED_ACCOUNT` AS `linked_account`,`ss`.`subscriber_id` AS `subscriber_id`,`ss`.`date_created` AS `date_created`,`ss`.`status` AS `status`,`ss`.`payment_method` AS `payment_method`,`ss`.`date_expire` AS `date_expire`,`ss`.`referrer` AS `referrer`,`ss`.`service_name` AS `service_name`,`ss`.`comments` AS `comments`,`ss`.`ssid` AS `ssid`,`ss`.`usage_count` AS `usage_count` from (`SUBSCRIBERS` `s` join `SUBSCRIBERS_SERVICES` `ss`) where (`s`.`ID` = `ss`.`subscriber_id`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SUB_SERVICES`
--

/*!50001 DROP TABLE IF EXISTS `SUB_SERVICES`*/;
/*!50001 DROP VIEW IF EXISTS `SUB_SERVICES`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SUB_SERVICES` AS select `s`.`ID` AS `id`,`s`.`STUDENT_EMAIL` AS `student_email`,`s`.`comments` AS `comments`,`ss`.`service_name` AS `service_name`,`ss`.`date_expire` AS `date_expire`,`ss`.`status` AS `status` from (`SUBSCRIBERS` `s` left join `SUBSCRIBERS_SERVICES` `ss` on((`ss`.`subscriber_id` = `s`.`ID`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_ALL_FAILED_QUIZZES`
--

/*!50001 DROP TABLE IF EXISTS `V_ALL_FAILED_QUIZZES`*/;
/*!50001 DROP VIEW IF EXISTS `V_ALL_FAILED_QUIZZES`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_ALL_FAILED_QUIZZES` AS (select `t`.`test_id` AS `test_id`,`t`.`user_prog_id` AS `user_prog_id`,`t`.`test_def_id` AS `test_def_id`,`t`.`test_segment` AS `test_segment`,`t`.`user_id` AS `user_id`,`r`.`run_id` AS `run_id`,((`r`.`answered_correct` / ((`r`.`answered_correct` + `r`.`answered_incorrect`) + `r`.`not_answered`)) * 100) AS `quiz_percent`,`r`.`run_time` AS `run_time`,dayofyear(`r`.`run_time`) AS `run_day`,if((`r`.`run_time` > _latin1'2010-12-31 23:59:59'),365,0) AS `add_days` from (`HA_TEST_RUN` `r` join `HA_TEST` `t`) where ((`r`.`is_passing` = 0) and (`r`.`test_id` = `t`.`test_id`) and (`r`.`run_time` >= _latin1'2010-08-01') and (`t`.`test_segment` > 0))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_ALL_FAILED_QUIZZES_X`
--

/*!50001 DROP TABLE IF EXISTS `V_ALL_FAILED_QUIZZES_X`*/;
/*!50001 DROP VIEW IF EXISTS `V_ALL_FAILED_QUIZZES_X`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_ALL_FAILED_QUIZZES_X` AS (select `t`.`test_id` AS `test_id`,`t`.`user_prog_id` AS `user_prog_id`,`t`.`test_def_id` AS `test_def_id`,`t`.`test_segment` AS `test_segment`,`t`.`user_id` AS `user_id`,`r`.`run_id` AS `run_id`,((`r`.`answered_correct` / ((`r`.`answered_correct` + `r`.`answered_incorrect`) + `r`.`not_answered`)) * 100) AS `quiz_percent`,`r`.`run_time` AS `run_time`,((dayofyear(`r`.`run_time`) + if((`r`.`run_time` > _latin1'2010-12-31 23:59:59'),365,0)) - dayofyear(_latin1'2010-07-31 00:00:00')) AS `day_of_school_year` from (`HA_TEST_RUN` `r` join `HA_TEST` `t`) where ((`r`.`is_passing` = 0) and (`r`.`test_id` = `t`.`test_id`) and (`r`.`run_time` >= _latin1'2010-08-01 00:00:00') and (`t`.`test_segment` > 0))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_ALL_PASSED_QUIZZES`
--

/*!50001 DROP TABLE IF EXISTS `V_ALL_PASSED_QUIZZES`*/;
/*!50001 DROP VIEW IF EXISTS `V_ALL_PASSED_QUIZZES`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_ALL_PASSED_QUIZZES` AS (select `t`.`test_id` AS `test_id`,`t`.`user_prog_id` AS `user_prog_id`,`t`.`test_def_id` AS `test_def_id`,`t`.`test_segment` AS `test_segment`,`t`.`user_id` AS `user_id`,`r`.`run_id` AS `run_id`,((`r`.`answered_correct` / ((`r`.`answered_correct` + `r`.`answered_incorrect`) + `r`.`not_answered`)) * 100) AS `quiz_percent`,`r`.`run_time` AS `run_time`,dayofyear(`r`.`run_time`) AS `run_day`,if((`r`.`run_time` > _latin1'2010-12-31 23:59:59'),365,0) AS `add_days` from (`HA_TEST_RUN` `r` join `HA_TEST` `t`) where ((`r`.`is_passing` = 1) and (`r`.`test_id` = `t`.`test_id`) and (`r`.`run_time` >= _latin1'2010-08-01') and (`t`.`test_segment` > 0))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_ALL_PASSED_QUIZZES_X`
--

/*!50001 DROP TABLE IF EXISTS `V_ALL_PASSED_QUIZZES_X`*/;
/*!50001 DROP VIEW IF EXISTS `V_ALL_PASSED_QUIZZES_X`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_ALL_PASSED_QUIZZES_X` AS (select `t`.`test_id` AS `test_id`,`t`.`user_prog_id` AS `user_prog_id`,`t`.`test_def_id` AS `test_def_id`,`t`.`test_segment` AS `test_segment`,`t`.`user_id` AS `user_id`,`r`.`run_id` AS `run_id`,((`r`.`answered_correct` / ((`r`.`answered_correct` + `r`.`answered_incorrect`) + `r`.`not_answered`)) * 100) AS `quiz_percent`,`r`.`run_time` AS `run_time`,((dayofyear(`r`.`run_time`) + if((`r`.`run_time` > _latin1'2010-12-31 23:59:59'),365,0)) - dayofyear(_latin1'2010-07-31 00:00:00')) AS `day_of_school_year` from (`HA_TEST_RUN` `r` join `HA_TEST` `t`) where ((`r`.`is_passing` = 1) and (`r`.`test_id` = `t`.`test_id`) and (`r`.`run_time` >= _latin1'2010-08-01 00:00:00') and (`t`.`test_segment` > 0))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_EARLIEST_FAILED_QUIZ`
--

/*!50001 DROP TABLE IF EXISTS `V_EARLIEST_FAILED_QUIZ`*/;
/*!50001 DROP VIEW IF EXISTS `V_EARLIEST_FAILED_QUIZ`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_EARLIEST_FAILED_QUIZ` AS (select `V_ALL_FAILED_QUIZZES`.`test_id` AS `test_id`,`V_ALL_FAILED_QUIZZES`.`user_prog_id` AS `user_prog_id`,`V_ALL_FAILED_QUIZZES`.`test_def_id` AS `test_def_id`,`V_ALL_FAILED_QUIZZES`.`test_segment` AS `test_segment`,`V_ALL_FAILED_QUIZZES`.`user_id` AS `user_id`,min(`V_ALL_FAILED_QUIZZES`.`run_id`) AS `min_runid`,`V_ALL_FAILED_QUIZZES`.`run_time` AS `run_time`,dayofyear(`V_ALL_FAILED_QUIZZES`.`run_time`) AS `run_day`,if((`V_ALL_FAILED_QUIZZES`.`run_time` > _latin1'2010-12-31 11:59:59'),365,0) AS `add_days`,`V_ALL_FAILED_QUIZZES`.`quiz_percent` AS `quiz_percent` from `V_ALL_FAILED_QUIZZES` group by `V_ALL_FAILED_QUIZZES`.`user_prog_id`,`V_ALL_FAILED_QUIZZES`.`test_segment`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_EARLIEST_FAILED_QUIZ_X`
--

/*!50001 DROP TABLE IF EXISTS `V_EARLIEST_FAILED_QUIZ_X`*/;
/*!50001 DROP VIEW IF EXISTS `V_EARLIEST_FAILED_QUIZ_X`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_EARLIEST_FAILED_QUIZ_X` AS (select `fq`.`test_id` AS `test_id`,`fq`.`user_prog_id` AS `user_prog_id`,`fq`.`test_def_id` AS `test_def_id`,`fq`.`test_segment` AS `test_segment`,`fq`.`user_id` AS `user_id`,min(`fq`.`run_id`) AS `min_runid`,`fq`.`run_time` AS `run_time`,((dayofyear(`fq`.`run_time`) + if((`fq`.`run_time` > _latin1'2010-12-31 23:59:59'),365,0)) - dayofyear(_latin1'2010-07-31 00:00:00')) AS `day_of_school_year`,`fq`.`quiz_percent` AS `quiz_percent` from `V_ALL_FAILED_QUIZZES` `fq` group by `fq`.`user_prog_id`,`fq`.`test_segment`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_FAILED_QUIZ_COUNT`
--

/*!50001 DROP TABLE IF EXISTS `V_FAILED_QUIZ_COUNT`*/;
/*!50001 DROP VIEW IF EXISTS `V_FAILED_QUIZ_COUNT`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_FAILED_QUIZ_COUNT` AS (select `s`.`user_id` AS `user_id`,`s`.`user_prog_id` AS `user_prog_id`,`s`.`test_def_id` AS `test_def_id`,`s`.`test_segment` AS `test_segment`,count(0) AS `fail_count` from (`V_PASSED_FAILED_QUIZZES` `s` join `HA_TEST_DEF` `t`) where ((`s`.`pass_percent` is not null) and (`s`.`test_def_id` = `t`.`test_def_id`)) group by `s`.`user_prog_id`,`s`.`test_segment`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_FAIL_AND_NOT_PASS_QUIZ_DATA`
--

/*!50001 DROP TABLE IF EXISTS `V_FAIL_AND_NOT_PASS_QUIZ_DATA`*/;
/*!50001 DROP VIEW IF EXISTS `V_FAIL_AND_NOT_PASS_QUIZ_DATA`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_FAIL_AND_NOT_PASS_QUIZ_DATA` AS (select `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`user_id` AS `user_id`,`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`test_segment` AS `test_segment`,`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`test_def_id` AS `test_def_id`,`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`user_prog_id` AS `user_prog_id`,`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`test_id` AS `test_id`,`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`fail_percent` AS `fail_percent`,`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`fail_day` AS `fail_day`,`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`pass_percent` AS `pass_percent` from `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA` where isnull(`V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`.`pass_percent`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`
--

/*!50001 DROP TABLE IF EXISTS `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`*/;
/*!50001 DROP VIEW IF EXISTS `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_FAIL_AND_PASS_NOT_PASS_QUIZ_DATA` AS (select `fq`.`user_id` AS `user_id`,`fq`.`test_segment` AS `test_segment`,`fq`.`test_def_id` AS `test_def_id`,`fq`.`user_prog_id` AS `user_prog_id`,`fq`.`test_id` AS `test_id`,`fq`.`quiz_percent` AS `fail_percent`,`fq`.`day_of_school_year` AS `fail_day`,`pq`.`quiz_percent` AS `pass_percent` from (`V_EARLIEST_FAILED_QUIZ_X` `fq` left join `V_ALL_PASSED_QUIZZES_X` `pq` on(((`pq`.`user_prog_id` = `fq`.`user_prog_id`) and (`pq`.`test_segment` = `fq`.`test_segment`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_FAIL_THEN_PASS_QUIZ_DATA`
--

/*!50001 DROP TABLE IF EXISTS `V_FAIL_THEN_PASS_QUIZ_DATA`*/;
/*!50001 DROP VIEW IF EXISTS `V_FAIL_THEN_PASS_QUIZ_DATA`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_FAIL_THEN_PASS_QUIZ_DATA` AS (select `fq`.`user_id` AS `user_id`,`fq`.`test_segment` AS `test_segment`,`fq`.`test_def_id` AS `test_def_id`,`fq`.`user_prog_id` AS `user_prog_id`,`fq`.`test_id` AS `test_id`,`fq`.`quiz_percent` AS `fail_percent`,`fq`.`day_of_school_year` AS `fail_day`,`pq`.`quiz_percent` AS `pass_percent`,`pq`.`day_of_school_year` AS `pass_day` from (`V_EARLIEST_FAILED_QUIZ_X` `fq` join `V_ALL_PASSED_QUIZZES_X` `pq`) where ((`pq`.`user_prog_id` = `fq`.`user_prog_id`) and (`pq`.`test_segment` = `fq`.`test_segment`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_INMH_ACTIVITY`
--

/*!50001 DROP TABLE IF EXISTS `V_INMH_ACTIVITY`*/;
/*!50001 DROP VIEW IF EXISTS `V_INMH_ACTIVITY`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_INMH_ACTIVITY` AS (select distinct `t`.`user_id` AS `user_id`,((dayofyear(`l`.`view_time`) + if((`l`.`view_time` > _latin1'2010-12-31 23:59:59'),365,0)) - dayofyear(_latin1'2010-07-31 00:00:00')) AS `day_of_school_year` from ((`HA_TEST_RUN_INMH_USE` `l` join `HA_TEST_RUN` `r`) join `HA_TEST` `t`) where ((`l`.`view_time` >= _latin1'2010-08-01 00:00:00') and (`l`.`run_id` = `r`.`run_id`) and (`r`.`test_id` = `t`.`test_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_LESSON_ACTIVITY`
--

/*!50001 DROP TABLE IF EXISTS `V_LESSON_ACTIVITY`*/;
/*!50001 DROP VIEW IF EXISTS `V_LESSON_ACTIVITY`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_LESSON_ACTIVITY` AS (select distinct `t`.`user_id` AS `user_id`,((dayofyear(`l`.`date_completed`) + if((`l`.`date_completed` > _latin1'2010-12-31 23:59:59'),365,0)) - dayofyear(_latin1'2010-07-31 00:00:00')) AS `day_of_school_year` from ((`HA_TEST_RUN_LESSON` `l` join `HA_TEST_RUN` `r`) join `HA_TEST` `t`) where ((`l`.`date_completed` >= _latin1'2010-08-01 00:00:00') and (`l`.`run_id` = `r`.`run_id`) and (`r`.`test_id` = `t`.`test_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_PASSED_FAILED_QUIZZES`
--

/*!50001 DROP TABLE IF EXISTS `V_PASSED_FAILED_QUIZZES`*/;
/*!50001 DROP VIEW IF EXISTS `V_PASSED_FAILED_QUIZZES`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_PASSED_FAILED_QUIZZES` AS (select `fq`.`user_id` AS `user_id`,`fq`.`test_segment` AS `test_segment`,`fq`.`test_def_id` AS `test_def_id`,`fq`.`user_prog_id` AS `user_prog_id`,`fq`.`quiz_percent` AS `fail_percent`,`pq`.`quiz_percent` AS `pass_percent` from (`V_ALL_FAILED_QUIZZES` `fq` left join `V_ALL_PASSED_QUIZZES` `pq` on(((`pq`.`user_prog_id` = `fq`.`user_prog_id`) and (`pq`.`test_segment` = `fq`.`test_segment`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_QUIZ_START_ACTIVITY`
--

/*!50001 DROP TABLE IF EXISTS `V_QUIZ_START_ACTIVITY`*/;
/*!50001 DROP VIEW IF EXISTS `V_QUIZ_START_ACTIVITY`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_QUIZ_START_ACTIVITY` AS (select distinct `t`.`user_id` AS `user_id`,((dayofyear(`t`.`create_time`) + if((`t`.`create_time` > _latin1'2010-12-31 23:59:59'),365,0)) - dayofyear(_latin1'2010-07-31 00:00:00')) AS `day_of_school_year` from `HA_TEST` `t` where (`t`.`create_time` > _latin1'2010-08-01 00:00:00')) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_QUIZ_TAKE_ACTIVITY`
--

/*!50001 DROP TABLE IF EXISTS `V_QUIZ_TAKE_ACTIVITY`*/;
/*!50001 DROP VIEW IF EXISTS `V_QUIZ_TAKE_ACTIVITY`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_QUIZ_TAKE_ACTIVITY` AS (select distinct `t`.`user_id` AS `user_id`,((dayofyear(`r`.`run_time`) + if((`r`.`run_time` > _latin1'2010-12-31 23:59:59'),365,0)) - dayofyear(_latin1'2010-07-31 00:00:00')) AS `day_of_school_year` from (`HA_TEST_RUN` `r` join `HA_TEST` `t`) where ((`r`.`run_time` > _latin1'2010-08-01 00:00:00') and (`t`.`test_id` = `r`.`test_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

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
-- Final view structure for view `V_USER_LESSON_COUNT`
--

/*!50001 DROP TABLE IF EXISTS `V_USER_LESSON_COUNT`*/;
/*!50001 DROP VIEW IF EXISTS `V_USER_LESSON_COUNT`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `V_USER_LESSON_COUNT` AS (select `t`.`test_id` AS `test_id`,`t`.`user_prog_id` AS `user_prog_id`,`t`.`test_def_id` AS `test_def_id`,`t`.`test_segment` AS `test_segment`,`t`.`user_id` AS `user_id`,count(`rl`.`id`) AS `lesson_count` from (((`HA_TEST` `t` join `HA_TEST_RUN` `tr`) join `HA_TEST_RUN_LESSON` `rl`) join `HM_USER_CHANCE_REPORT` `s`) where ((`t`.`test_id` = `tr`.`test_id`) and (`tr`.`run_id` = `rl`.`run_id`) and (`tr`.`run_time` >= _latin1'2010-08-01 00:00:00') and (`t`.`test_segment` > 0) and (`s`.`user_id` = `t`.`user_id`) and (`t`.`test_def_id` in (34,38,39,40,42))) group by `t`.`test_id`,`t`.`test_segment`,`t`.`user_id`) */;
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
-- Final view structure for view `v_ASSIGNMENT_PIDS`
--

/*!50001 DROP TABLE IF EXISTS `v_ASSIGNMENT_PIDS`*/;
/*!50001 DROP VIEW IF EXISTS `v_ASSIGNMENT_PIDS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_ASSIGNMENT_PIDS` AS select `p1`.`pid` AS `pid1`,`p1`.`pid` AS `pid2`,`ps`.`id` AS `id`,`ps`.`uid` AS `uid`,`p1`.`assign_key` AS `assign_key`,`ps`.`status` AS `status` from ((`CM_ASSIGNMENT` `a` join `CM_ASSIGNMENT_PIDS` `p1` on((`p1`.`assign_key` = `a`.`assign_key`))) left join `CM_ASSIGNMENT_PID_STATUS` `ps` on(((`ps`.`assign_key` = `p1`.`assign_key`) and (`ps`.`pid` = `p1`.`pid`)))) where ((`a`.`assign_key` = 4415) and (`a`.`is_personalized` = 0)) union select `p1`.`pid` AS `pid1`,`p2`.`pid` AS `pid2`,`ps`.`id` AS `id`,`ps`.`uid` AS `uid`,`p1`.`assign_key` AS `assign_key`,`ps`.`status` AS `status` from (((`CM_ASSIGNMENT` `a` join `CM_ASSIGNMENT_PIDS` `p1` on((`p1`.`assign_key` = `a`.`assign_key`))) join `CM_ASSIGNMENT_PIDS_USER` `p2` on(((`p2`.`assign_key` = `p1`.`assign_key`) and (`p2`.`apid_id` = `p1`.`id`)))) left join `CM_ASSIGNMENT_PID_STATUS` `ps` on(((`ps`.`assign_key` = `p2`.`assign_key`) and (`ps`.`pid` = `p2`.`pid`) and (`ps`.`uid` = `p2`.`uid`)))) where ((`a`.`assign_key` = 4415) and (`a`.`is_personalized` = 1)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_ASSIGNMENT_PID_STATUS`
--

/*!50001 DROP TABLE IF EXISTS `v_ASSIGNMENT_PID_STATUS`*/;
/*!50001 DROP VIEW IF EXISTS `v_ASSIGNMENT_PID_STATUS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_ASSIGNMENT_PID_STATUS` AS select `p1`.`pid` AS `pid1`,`p1`.`pid` AS `pid2`,`ps`.`id` AS `id`,`ps`.`uid` AS `uid`,`p1`.`assign_key` AS `assign_key`,`ps`.`status` AS `status`,`a`.`is_personalized` AS `is_personalized` from ((`CM_ASSIGNMENT` `a` join `CM_ASSIGNMENT_PIDS` `p1` on((`p1`.`assign_key` = `a`.`assign_key`))) left join `CM_ASSIGNMENT_PID_STATUS` `ps` on(((`ps`.`assign_key` = `p1`.`assign_key`) and (`ps`.`pid` = `p1`.`pid`)))) where (`a`.`is_personalized` = 0) union select `p1`.`pid` AS `pid1`,`p2`.`pid` AS `pid2`,`ps`.`id` AS `id`,`ps`.`uid` AS `uid`,`p1`.`assign_key` AS `assign_key`,`ps`.`status` AS `status`,`a`.`is_personalized` AS `is_personalized` from (((`CM_ASSIGNMENT` `a` join `CM_ASSIGNMENT_PIDS` `p1` on((`p1`.`assign_key` = `a`.`assign_key`))) join `CM_ASSIGNMENT_PIDS_USER` `p2` on(((`p2`.`assign_key` = `p1`.`assign_key`) and (`p2`.`apid_id` = `p1`.`id`)))) left join `CM_ASSIGNMENT_PID_STATUS` `ps` on(((`ps`.`assign_key` = `p2`.`assign_key`) and (`ps`.`pid` = `p2`.`pid`) and (`ps`.`uid` = `p2`.`uid`)))) where (`a`.`is_personalized` = 1) */;
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
-- Final view structure for view `v_CM_ADMIN_ESSENTIALS_COUNT`
--

/*!50001 DROP TABLE IF EXISTS `v_CM_ADMIN_ESSENTIALS_COUNT`*/;
/*!50001 DROP VIEW IF EXISTS `v_CM_ADMIN_ESSENTIALS_COUNT`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_CM_ADMIN_ESSENTIALS_COUNT` AS (select `u`.`admin_id` AS `admin_id`,count(0) AS `essentials_count` from ((`HA_USER` `u` join `CM_USER_PROGRAM` `p` on((`u`.`user_prog_id` = `p`.`id`))) join `HA_TEST_DEF` `t` on(((`p`.`test_def_id` = `t`.`test_def_id`) and (`t`.`textcode` like 'essptests%')))) where ((`u`.`is_active` = 1) and (`u`.`is_auto_create_template` = 0)) group by `u`.`admin_id`) */;
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
/*!50001 VIEW `v_CM_PILOT` AS select `s`.`ID` AS `ID`,`s`.`PASSWORD` AS `PASSWORD`,`s`.`DATE_CREATED` AS `DATE_CREATED`,`s`.`RESPONSIBLE_NAME` AS `RESPONSIBLE_NAME`,`s`.`STUDENT_EMAIL` AS `STUDENT_EMAIL`,`s`.`STUDENT_ZIP` AS `STUDENT_ZIP`,`s`.`SCHOOL_TYPE` AS `SCHOOL_TYPE`,`s`.`sales_zone` AS `SALES_ZONE`,`ss`.`date_created` AS `service_created`,`ss`.`date_expire` AS `service_expire`,`pr`.`title` AS `pilot_teacher_title`,`pr`.`phone` AS `pilot_phone`,replace(replace(`pr`.`comments`,'\n',' '),'\r','') AS `pilot_challenges`,replace(replace(`pr`.`cc_emails`,'\n',' '),'\r','') AS `pilot_emails`,replace(replace(`pr`.`motivation`,'\n',' '),'\r','') AS `pilot_motivation`,`pr`.`enrollment` AS `pilot_enrollment` from (((`SUBSCRIBERS` `s` join `SUBSCRIBERS_SERVICES` `ss` on((`s`.`ID` = `ss`.`subscriber_id`))) left join `SUBSCRIBERS_INFO_temp` `info` on((`info`.`id` = `s`.`ID`))) left join `HA_ADMIN_PILOT_REQUEST` `pr` on((`pr`.`subscriber_id` = `s`.`ID`))) where ((`ss`.`service_name` = 'catchup') and (`s`.`TYPE` = 'ST') and (`ss`.`date_created` > (curdate() - interval 30 day))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_CM_STUDENT_LOGIN_STATS`
--

/*!50001 DROP TABLE IF EXISTS `v_CM_STUDENT_LOGIN_STATS`*/;
/*!50001 DROP VIEW IF EXISTS `v_CM_STUDENT_LOGIN_STATS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_CM_STUDENT_LOGIN_STATS` AS select `a`.`subscriber_id` AS `subscriber_id`,`s`.`PASSWORD` AS `password`,ifnull(`s`.`SCHOOL_TYPE`,'') AS `account_name`,ifnull(`s`.`RESPONSIBLE_NAME`,'') AS `responsible_name`,`s`.`TYPE` AS `account_type`,`s`.`STUDENT_EMAIL` AS `account_email`,count(0) AS `login_count`,date_format(`ul`.`login_time`,'%Y-%m-%d') AS `begin_date`,date_format(`ul`.`login_time`,'%Y-%m-%d') AS `end_date` from (((`HA_USER_LOGIN` `ul` join `HA_USER` `u` on((`u`.`uid` = `ul`.`user_id`))) join `HA_ADMIN` `a` on((`a`.`aid` = `u`.`admin_id`))) join `SUBSCRIBERS` `s` on((`s`.`ID` = `a`.`subscriber_id`))) where ((`ul`.`user_type` <> 'ADMIN') and (`ul`.`is_consumed` = 1)) group by `a`.`subscriber_id`,date_format(`ul`.`login_time`,'%Y-%m-%d') order by `s`.`PASSWORD`,date_format(`ul`.`login_time`,'%Y-%m-%d') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_CM_STUDENT_RESOURCE_LOGIN_STATS`
--

/*!50001 DROP TABLE IF EXISTS `v_CM_STUDENT_RESOURCE_LOGIN_STATS`*/;
/*!50001 DROP VIEW IF EXISTS `v_CM_STUDENT_RESOURCE_LOGIN_STATS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_CM_STUDENT_RESOURCE_LOGIN_STATS` AS select `r`.`subscriber_id` AS `subscriber_id`,`r`.`password` AS `password`,`r`.`account_name` AS `account_name`,`r`.`responsible_name` AS `responsible_name`,`r`.`account_type` AS `account_type`,`r`.`account_email` AS `account_email`,`r`.`total_usage` AS `total_usage`,ifnull(`l`.`login_count`,0) AS `login_count`,`r`.`begin_date` AS `begin_date`,`r`.`end_date` AS `end_date` from (`v_CM_STUDENT_RESOURCE_STATS` `r` left join `v_CM_STUDENT_LOGIN_STATS` `l` on(((`l`.`subscriber_id` = `r`.`subscriber_id`) and (`l`.`begin_date` = `r`.`begin_date`)))) union select `l`.`subscriber_id` AS `subscriber_id`,`l`.`password` AS `password`,`l`.`account_name` AS `account_name`,`l`.`responsible_name` AS `responsible_name`,`l`.`account_type` AS `account_type`,`l`.`account_email` AS `account_email`,ifnull(`r`.`total_usage`,0) AS `total_usage`,`l`.`login_count` AS `login_count`,`l`.`begin_date` AS `begin_date`,`l`.`end_date` AS `end_date` from (`v_CM_STUDENT_LOGIN_STATS` `l` left join `v_CM_STUDENT_RESOURCE_STATS` `r` on(((`r`.`subscriber_id` = `l`.`subscriber_id`) and (`r`.`begin_date` = `l`.`begin_date`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_CM_STUDENT_RESOURCE_STATS`
--

/*!50001 DROP TABLE IF EXISTS `v_CM_STUDENT_RESOURCE_STATS`*/;
/*!50001 DROP VIEW IF EXISTS `v_CM_STUDENT_RESOURCE_STATS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_CM_STUDENT_RESOURCE_STATS` AS select `v_CM_SUBSCRIBER_INMH_USE`.`subscriber_id` AS `subscriber_id`,`v_CM_SUBSCRIBER_INMH_USE`.`password` AS `password`,`v_CM_SUBSCRIBER_INMH_USE`.`account_name` AS `account_name`,`v_CM_SUBSCRIBER_INMH_USE`.`responsible_name` AS `responsible_name`,`v_CM_SUBSCRIBER_INMH_USE`.`account_type` AS `account_type`,`v_CM_SUBSCRIBER_INMH_USE`.`account_email` AS `account_email`,sum(`v_CM_SUBSCRIBER_INMH_USE`.`item_use`) AS `total_usage`,`v_CM_SUBSCRIBER_INMH_USE`.`begin_date` AS `begin_date`,`v_CM_SUBSCRIBER_INMH_USE`.`end_date` AS `end_date` from `v_CM_SUBSCRIBER_INMH_USE` group by `v_CM_SUBSCRIBER_INMH_USE`.`subscriber_id`,`v_CM_SUBSCRIBER_INMH_USE`.`begin_date` order by `v_CM_SUBSCRIBER_INMH_USE`.`password`,`v_CM_SUBSCRIBER_INMH_USE`.`begin_date` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_CM_SUBSCRIBER_INMH_USE`
--

/*!50001 DROP TABLE IF EXISTS `v_CM_SUBSCRIBER_INMH_USE`*/;
/*!50001 DROP VIEW IF EXISTS `v_CM_SUBSCRIBER_INMH_USE`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_CM_SUBSCRIBER_INMH_USE` AS select `a`.`subscriber_id` AS `subscriber_id`,`s`.`PASSWORD` AS `password`,ifnull(`s`.`SCHOOL_TYPE`,'') AS `account_name`,ifnull(`s`.`RESPONSIBLE_NAME`,'') AS `responsible_name`,`s`.`TYPE` AS `account_type`,`s`.`STUDENT_EMAIL` AS `account_email`,`tri`.`item_type` AS `item_type`,count(0) AS `item_use`,date_format(`tri`.`view_time`,'%Y-%m-%d') AS `begin_date`,date_format(`tri`.`view_time`,'%Y-%m-%d') AS `end_date` from (((((`HA_TEST_RUN_INMH_USE` `tri` join `HA_TEST_RUN` `tr` on((`tr`.`run_id` = `tri`.`run_id`))) join `HA_TEST` `t` on((`t`.`test_id` = `tr`.`test_id`))) join `HA_USER` `u` on((`u`.`uid` = `t`.`user_id`))) join `HA_ADMIN` `a` on((`a`.`aid` = `u`.`admin_id`))) join `SUBSCRIBERS` `s` on((`s`.`ID` = `a`.`subscriber_id`))) group by `a`.`subscriber_id`,`tri`.`item_type`,date_format(`tri`.`view_time`,'%Y-%m-%d') order by `s`.`PASSWORD`,date_format(`tri`.`view_time`,'%Y-%m-%d'),`tri`.`item_type` */;
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
-- Final view structure for view `v_HA_TEST_INFO`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_INFO`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INFO`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_INFO` AS select `u`.`uid` AS `uid`,`u`.`user_name` AS `user_name`,`t`.`test_id` AS `test_id`,`t`.`create_time` AS `test_create_time`,`d`.`test_def_id` AS `test_def_id`,`d`.`test_name` AS `test_name`,`d`.`textcode` AS `textcode`,`d`.`chapter` AS `chapter` from ((`HA_USER` `u` join `HA_TEST` `t`) join `HA_TEST_DEF` `d`) where ((`t`.`user_id` = `u`.`uid`) and (`d`.`test_def_id` = `t`.`test_def_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_INMH_VIEWS`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_INMH_VIEWS` AS select `u`.`user_name` AS `user_name`,`i`.`item_type` AS `item_type`,count(0) AS `view_count` from (((`HA_TEST_RUN_INMH_USE` `i` join `HA_TEST` `t`) join `HA_TEST_RUN` `r`) join `HA_USER` `u`) where ((`t`.`user_id` = `u`.`uid`) and (`r`.`test_id` = `t`.`test_id`) and (`i`.`run_id` = `r`.`run_id`)) group by `u`.`user_name`,`i`.`item_type` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_INMH_VIEWS_INFO`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_INFO`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_INFO`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_INMH_VIEWS_INFO` AS select `u`.`user_name` AS `user_name`,`s`.`solution_views` AS `solution_views`,`v`.`video_views` AS `video_views`,`r`.`review_views` AS `review_views` from (((`HA_USER` `u` left join `v_HA_TEST_INMH_VIEWS_solution` `s` on((`s`.`user_name` = `u`.`user_name`))) left join `v_HA_TEST_INMH_VIEWS_video` `v` on((`v`.`user_name` = `u`.`user_name`))) left join `v_HA_TEST_INMH_VIEWS_review` `r` on((`r`.`user_name` = `u`.`user_name`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_INMH_VIEWS_review`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_review`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_review`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_INMH_VIEWS_review` AS select `v_HA_TEST_INMH_VIEWS`.`user_name` AS `user_name`,`v_HA_TEST_INMH_VIEWS`.`view_count` AS `review_views` from `v_HA_TEST_INMH_VIEWS` where (`v_HA_TEST_INMH_VIEWS`.`item_type` = 'review') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_INMH_VIEWS_solution`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_solution`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_solution`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_INMH_VIEWS_solution` AS select `v_HA_TEST_INMH_VIEWS`.`user_name` AS `user_name`,`v_HA_TEST_INMH_VIEWS`.`view_count` AS `solution_views` from `v_HA_TEST_INMH_VIEWS` where (`v_HA_TEST_INMH_VIEWS`.`item_type` = 'solution') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_INMH_VIEWS_video`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_INMH_VIEWS_video`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_INMH_VIEWS_video`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_INMH_VIEWS_video` AS select `v_HA_TEST_INMH_VIEWS`.`user_name` AS `user_name`,`v_HA_TEST_INMH_VIEWS`.`view_count` AS `video_views` from `v_HA_TEST_INMH_VIEWS` where (`v_HA_TEST_INMH_VIEWS`.`item_type` = 'video') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_INFO`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_INFO`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_INFO`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_INFO` AS select `u`.`uid` AS `uid`,`u`.`user_name` AS `user_name`,`t`.`test_id` AS `test_id`,`r`.`run_id` AS `run_id`,`r`.`run_time` AS `run_time`,`r`.`answered_correct` AS `answered_correct`,`r`.`answered_incorrect` AS `answered_incorrect`,`r`.`not_answered` AS `not_answered` from ((`HA_USER` `u` join `HA_TEST` `t` on((`u`.`uid` = `t`.`user_id`))) join `HA_TEST_RUN` `r` on((`t`.`test_id` = `r`.`test_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_INMH_USE_REQ_PROBS`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_INMH_USE_REQ_PROBS`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_INMH_USE_REQ_PROBS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_INMH_USE_REQ_PROBS` AS select `l`.`run_id` AS `run_id`,`p`.`pid` AS `item_file`,max(`l`.`lesson_number`) AS `session_number`,max(`l`.`lesson_viewed`) AS `view_time` from (`HA_TEST_RUN_LESSON` `l` join `HA_TEST_RUN_LESSON_PID` `p` on((`p`.`lid` = `l`.`id`))) where (`l`.`lesson_viewed` is not null) group by `l`.`run_id`,`p`.`pid` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_NOT_PASSING_TOTAL`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_NOT_PASSING_TOTAL`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_NOT_PASSING_TOTAL`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_NOT_PASSING_TOTAL` AS (select count(0) AS `total`,0 AS `is_passing`,`t`.`user_id` AS `user_id` from ((`HA_TEST_RUN` `r` join `HA_TEST` `t` on((`r`.`test_id` = `t`.`test_id`))) join `HA_TEST_DEF` `td` on(((`td`.`test_def_id` = `t`.`test_def_id`) and (`td`.`test_name` <> 'Custom')))) where (`r`.`is_passing` = 0) group by `t`.`user_id`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_PASSING_TOTAL`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_PASSING_TOTAL`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_PASSING_TOTAL`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_PASSING_TOTAL` AS (select count(0) AS `total`,1 AS `is_passing`,`t`.`user_id` AS `user_id` from ((`HA_TEST_RUN` `r` join `HA_TEST` `t` on((`r`.`test_id` = `t`.`test_id`))) join `HA_TEST_DEF` `td` on(((`td`.`test_def_id` = `t`.`test_def_id`) and (`td`.`test_name` <> 'Custom')))) where (`r`.`is_passing` = 1) group by `t`.`user_id`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_PNP_TOTALS`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_PNP_TOTALS`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_PNP_TOTALS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_PNP_TOTALS` AS (select ifnull(`p`.`total`,0) AS `passing_count`,ifnull(`n`.`total`,0) AS `not_passing_count`,`u`.`uid` AS `user_id` from ((`HA_USER` `u` left join `v_HA_TEST_RUN_PASSING_TOTAL` `p` on((`p`.`user_id` = `u`.`uid`))) left join `v_HA_TEST_RUN_NOT_PASSING_TOTAL` `n` on((`n`.`user_id` = `u`.`uid`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_RESULTS`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_RESULTS`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_RESULTS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_RESULTS` AS select `s`.`run_id` AS `run_id` from (((`HA_TEST_RUN_RESULTS` `s` join `HA_TEST_RUN` `r`) join `HA_TEST` `t`) join `HA_USER` `u`) where ((`t`.`user_id` = `u`.`uid`) and (`r`.`test_id` = `t`.`test_id`) and (`s`.`run_id` = `r`.`run_id`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_inter`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_inter`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_inter`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_inter` AS select max(`r`.`run_time`) AS `max_run_time`,`u`.`uid` AS `uid` from ((`HA_TEST_RUN` `r` join `HA_TEST` `t`) join `HA_USER` `u`) where ((`r`.`test_id` = `t`.`test_id`) and (`t`.`user_id` = `u`.`uid`)) group by `u`.`uid` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_last`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_last`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_last`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_last` AS select `u`.`uid` AS `uid`,`u`.`user_name` AS `user_name`,`r`.`answered_correct` AS `answered_correct`,`r`.`answered_incorrect` AS `answered_incorrect`,`r`.`not_answered` AS `not_answered`,`r`.`run_time` AS `last_run_time` from (((`HA_USER` `u` join `HA_TEST` `t`) join `HA_TEST_RUN` `r`) join `v_HA_TEST_RUN_inter` `s`) where ((`t`.`user_id` = `u`.`uid`) and (`r`.`test_id` = `t`.`test_id`) and (`s`.`max_run_time` = `r`.`run_time`) and (`s`.`uid` = `u`.`uid`)) group by `u`.`uid` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_TEST_RUN_max`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_TEST_RUN_max`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_TEST_RUN_max`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_TEST_RUN_max` AS select `i`.`uid` AS `uid`,`i`.`user_name` AS `user_name`,`i`.`answered_correct` AS `answered_correct`,`i`.`answered_incorrect` AS `answered_incorrect`,`i`.`not_answered` AS `not_answered`,`i`.`run_time` AS `last_run_time` from `v_HA_TEST_RUN_INFO` `i` where `i`.`run_time` in (select max(`x`.`run_time`) AS `max_run_time` from `HA_TEST_RUN` `x` where (`x`.`test_id` = `i`.`test_id`)) order by `i`.`run_time` desc */;
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
/*!50001 VIEW `v_HA_USER_ACTIVE` AS select `HA_USER`.`uid` AS `uid`,`HA_USER`.`admin_id` AS `admin_id`,`HA_USER`.`user_name` AS `user_name`,`HA_USER`.`user_passcode` AS `user_passcode`,`HA_USER`.`user_email` AS `user_email`,`HA_USER`.`test_def_id` AS `test_def_id`,`HA_USER`.`active_run_id` AS `active_run_id`,`HA_USER`.`active_test_id` AS `active_test_id`,`HA_USER`.`active_segment` AS `active_segment`,`HA_USER`.`prescription_session` AS `prescription_session`,`HA_USER`.`test_def_chapter` AS `test_def_chapter`,`HA_USER`.`is_active` AS `is_active`,`HA_USER`.`group_id` AS `group_id`,`HA_USER`.`active_run_session` AS `active_run_session`,`HA_USER`.`user_prog_id` AS `user_prog_id`,`HA_USER`.`gui_background_style` AS `gui_background_style`,`HA_USER`.`is_show_work_required` AS `is_show_work_required`,`HA_USER`.`is_tutoring_available` AS `is_tutoring_available`,`HA_USER`.`is_auto_create_template` AS `is_auto_create_template`,`HA_USER`.`active_segment_slot` AS `active_segment_slot`,`HA_USER`.`date_created` AS `date_created`,`HA_USER`.`is_demo` AS `is_demo` from `HA_USER` where (`HA_USER`.`is_active` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_HA_USER_ACTIVITY`
--

/*!50001 DROP TABLE IF EXISTS `v_HA_USER_ACTIVITY`*/;
/*!50001 DROP VIEW IF EXISTS `v_HA_USER_ACTIVITY`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_HA_USER_ACTIVITY` AS select `HA_TEST`.`user_id` AS `user_id`,'quiz' AS `activity_type`,`HA_TEST`.`test_id` AS `activity_key`,`HA_TEST`.`create_time` AS `activity_time` from `HA_TEST` union select `t`.`user_id` AS `user_id`,'run' AS `activity_type`,`r`.`run_id` AS `activity_key`,`r`.`run_time` AS `activity_time` from (`HA_TEST_RUN` `r` join `HA_TEST` `t` on((`r`.`test_id` = `t`.`test_id`))) union select `t`.`user_id` AS `user_id`,'lesson_view' AS `activity_type`,`l`.`lesson_name` AS `activity_key`,`l`.`lesson_viewed` AS `activity_time` from ((`HA_TEST_RUN_LESSON` `l` join `HA_TEST_RUN` `r` on(((`r`.`run_id` = `l`.`run_id`) and (`l`.`lesson_viewed` is not null)))) join `HA_TEST` `t` on((`t`.`test_id` = `r`.`test_id`))) union select `t`.`user_id` AS `user_id`,'lesson_complete' AS `activity_type`,`l`.`lesson_name` AS `activity_key`,`l`.`date_completed` AS `activity_time` from ((`HA_TEST_RUN_LESSON` `l` join `HA_TEST_RUN` `r` on(((`r`.`run_id` = `l`.`run_id`) and (`l`.`date_completed` is not null)))) join `HA_TEST` `t` on((`t`.`test_id` = `r`.`test_id`))) union select `t`.`user_id` AS `user_id`,'resource_view' AS `activity_type`,`i`.`item_type` AS `activity_key`,`i`.`view_time` AS `view_time` from ((`HA_TEST_RUN_INMH_USE` `i` join `HA_TEST_RUN` `r` on((`r`.`run_id` = `i`.`run_id`))) join `HA_TEST` `t` on((`t`.`test_id` = `r`.`test_id`))) */;
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

--
-- Final view structure for view `v_SOLUTION_XML`
--

/*!50001 DROP TABLE IF EXISTS `v_SOLUTION_XML`*/;
/*!50001 DROP VIEW IF EXISTS `v_SOLUTION_XML`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_SOLUTION_XML` AS select `SOLUTIONS`.`PROBLEMINDEX` AS `PID`,`SOLUTIONS`.`SOLUTIONXML` AS `SOLUTION_STEPS_XML`,`SOLUTIONS`.`tutor_define` AS `TUTOR_DEFINE_XML` from `SOLUTIONS` order by `SOLUTIONS`.`PROBLEMINDEX` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_SUBSCRIBERS_EXPIRED`
--

/*!50001 DROP TABLE IF EXISTS `v_SUBSCRIBERS_EXPIRED`*/;
/*!50001 DROP VIEW IF EXISTS `v_SUBSCRIBERS_EXPIRED`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_SUBSCRIBERS_EXPIRED` AS select `s`.`ID` AS `id`,`s`.`TYPE` AS `type`,`s`.`SCHOOL_TYPE` AS `school_type`,`s`.`PASSWORD` AS `password`,`sd`.`service_count` AS `service_count`,`sd`.`max_expire` AS `max_expire` from (`SUBSCRIBERS` `s` join `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE` `sd` on((`s`.`ID` = `sd`.`subscriber_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE`
--

/*!50001 DROP TABLE IF EXISTS `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE`*/;
/*!50001 DROP VIEW IF EXISTS `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_SUBSCRIBERS_SERVICES_EXPIRE_DATE` AS select `ss`.`subscriber_id` AS `subscriber_id`,count(0) AS `service_count`,max(`ss`.`date_expire`) AS `max_expire` from `SUBSCRIBERS_SERVICES` `ss` group by `ss`.`subscriber_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_USER_ACTIVE_PROGRAM`
--

/*!50001 DROP TABLE IF EXISTS `v_USER_ACTIVE_PROGRAM`*/;
/*!50001 DROP VIEW IF EXISTS `v_USER_ACTIVE_PROGRAM`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_USER_ACTIVE_PROGRAM` AS select `u`.`uid` AS `uid`,`u`.`user_name` AS `user_name`,`u`.`user_prog_id` AS `user_prog_id`,`p`.`test_def_id` AS `test_def_id`,`p`.`date_completed` AS `date_completed`,`d`.`test_name` AS `test_name`,`u`.`active_segment` AS `active_segment`,`u`.`active_test_id` AS `active_test_id`,`u`.`active_run_id` AS `active_run_id`,`u`.`active_segment_slot` AS `active_segment_slot` from ((`HA_USER` `u` left join `CM_USER_PROGRAM` `p` on((`p`.`id` = `u`.`user_prog_id`))) left join `HA_TEST_DEF` `d` on((`d`.`test_def_id` = `p`.`test_def_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_cm_renewed_admin`
--

/*!50001 DROP TABLE IF EXISTS `v_cm_renewed_admin`*/;
/*!50001 DROP VIEW IF EXISTS `v_cm_renewed_admin`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`hotmath`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_cm_renewed_admin` AS (select `a`.`aid` AS `admin_id`,`a`.`subscriber_id` AS `subscriber_id` from (`HA_ADMIN` `a` join `SUBSCRIBERS_SERVICES` `ss` on(((`ss`.`date_created` < '2013-07-01') and (`ss`.`service_name` = 'catchup') and (`ss`.`date_expire` > '2014-12-15')))) where ((`a`.`subscriber_id` = `ss`.`subscriber_id`) and (`a`.`aid` not in (2,3,4,5,6,7,8,9,13,71,73,216,1637,7108)))) */;
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

-- Dump completed on 2015-05-13 21:06:56
