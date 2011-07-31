<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="hotmath.assessment.InmhAssessment"%>
<%@page import="hotmath.testset.ha.HaTestRun"%>
<%@page import="hotmath.testset.ha.HaTestRunDao"%>
<%@page import="java.sql.Connection"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="hotmath.assessment.AssessmentPrescription"%><html><!-- InstanceBegin template="/Templates/hm_core_layout.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<%@ page import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%

    String _matches[] = null;
    String _pid=null;
    ConcordanceEntry con = null;
    String _range = request.getParameter("range");
	int runGradeLevel=0;
	Connection conn=null;
	String item="";
	try {
	    conn = HMConnectionPool.getConnection();
	    
	    if(_range != null) {
	    	_matches = new ConcordanceEntry(conn, _range).getGUIDs();
	    }
	    else {
	    	item = request.getParameter("item");
	    	if(item == null)
	    		throw new Exception("'item' or 'range' must be specified");
	    	
	    	String runId = request.getParameter("run_id");
	    	if(runId == null)
	    	    throw new Exception("'run_id' must be specified");

	    	runGradeLevel = HaTestRunDao.getInstance().lookupTestRun(Integer.parseInt(runId)).getHaTest().getTestDef().getGradeLevel();
    	}
	}
   	finally {
   	    SqlUtilities.releaseResources(null,null,conn);
   	}
   	_range = item;
   	_matches = InmhAssessment.getItemSolutionPool(item);
%>
</head>
<body>
  <h1>Solutions matching range: <%= _range %></h1>
  <ol>
      <%
          for(String s: _matches) {
        	  if(s.startsWith("{")) {
        		  %>
           		      <li> <%= s %></li>
           		  <%
        	  }
        	  else if(new ProblemID(s).getGradeLevel() > runGradeLevel) {
		          %>
		              <li> <a href='/tutor/?pid=<%= s %>'><%= s %></a> (filtered out)</li>
		          <%
		      }
              else {
		          %>
	              <li> <a href='/tutor/?pid=<%= s %>'><%= s %></a> </li>
  	              <%
              }
          }

      %>
  </ol>
</body>
