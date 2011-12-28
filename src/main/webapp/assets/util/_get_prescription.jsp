<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.cm.util.CatchupMathProperties"%>
<%@page import="hotmath.cm.util.CmCacheManager"%>
<%@page import="hotmath.assessment.*"%>
<html><!-- InstanceBegin template="/Templates/hm_core_layout.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.util.*, java.sql.*,hotmath.util.sql.*" %>
<%
    AssessmentPrescription inmhList=null;
    List<AssessmentPrescriptionSession> sessions=null;
    Connection conn=null;
    Map<String,Integer> lessonRanks=null;
    try {
      conn = HMConnectionPool.getConnection();
      
      lessonRanks = AssessmentPrescription.getLessonRankings(conn);
      
      inmhList = AssessmentPrescriptionManager.getInstance().getPrescription(conn, SbUtilities.getInt(request.getParameter("run_id")));
      sessions = inmhList.getSessions();
    }
    finally {
      SqlUtilities.releaseResources(null,null,conn);
    }

    int gradeLevelProgram = inmhList.getGradeLevel();
%>
</head>
<body>
  <h1>Assessment Prescription for: <%= inmhList.getTestRun().getPidList() %></h1>
  <h2>Grade Level: <%= gradeLevelProgram %>, Allow RPA: <%= CatchupMathProperties.getInstance().getProperty("prescription.allow_rpa") %></h2>
  <ul>
      <%
          for(AssessmentPrescriptionSession s: sessions) {
          %>
              <li><h2> <%= s %> rank: <%= AssessmentPrescription.getLessonRank(lessonRanks, s.getSessionItems().get(0).getItem().getFile()) %></h2></li>
              <ol>
                  <%
                      for(AssessmentPrescription.SessionData sessionData: s.getSessionItems()) {
                          int gradeLevel = 0;
                          if(!sessionData.getRpp().isFlashRequired()) {
                              gradeLevel = new ProblemID(sessionData.getRpp().getFile()).getGradeLevel();
                          }
                    	  %>
                    	  <li><a href='/tutor/?pid=<%= sessionData.getRpp().getFile() %>'><%= sessionData.getRpp().getFile() %></a>
                    	      (level: <%= gradeLevel %>, weight: <%= sessionData.getWeight() %>, include: <%= sessionData.getNumPids() %>)
                    	      <a href='_get_run_pool.jsp?run_id=<%= inmhList.getTestRun().getRunId() %>&item=<%= sessionData.getItem().getFile() %>'>pool</a>
                    	  </li>
                    	  <%
                      }
                  %>
              </ol>
          <%
          }
      %>
  </ul>
</body>
