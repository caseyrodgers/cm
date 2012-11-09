<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.cm.login.ClientEnvironment"%>
<%@page import="hotmath.testset.ha.HaTestRunDao"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="java.sql.Connection"%>
<%@page import="hotmath.assessment.AssessmentPrescription"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.apache.log4j.LogManager"%>
<head>
<%@ page import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%

    int runId = Integer.parseInt(request.getParameter("rid"));
    Connection conn = HMConnectionPool.getConnection();
    AssessmentPrescription assPres = new AssessmentPrescription(conn,HaTestRunDao.getInstance().lookupTestRun(runId), new ClientEnvironment(false));
%>
</head>
<body>
<h1>Prescription for run_id: <%= runId  %></h1>
lessons: <%= assPres.getSessions().size() %>, level: <%= assPres.getGradeLevel() %>, pids: <%= assPres.getTestRun().getPidList() %>
</body>
