<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.gwt.cm_rpc.client.rpc.QuizResultsMetaInfo"%>
<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.GetQuizResultsHtmlAction"%>
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
    
    QuizResultsMetaInfo quizResults = ActionDispatcher.getInstance().execute(new GetQuizResultsHtmlAction(runId));
%>
</head>
<body>
<h1>Prescription for run_id: <%= runId  %></h1>

     lessons: <%= assPres.getSessions().size() %>, 
     <br/>
     level: <%= assPres.getGradeLevel() %>, 
     <br/>
     pids: <%= assPres.getTestRun().getPidList() %>
     <br/>
     answers: <%= quizResults.getRpcData().getDataAsString("quiz_result_json") %>
     <br/>
</body>
