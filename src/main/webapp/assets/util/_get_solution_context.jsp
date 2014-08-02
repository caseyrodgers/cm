<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.testset.ha.SolutionDao"%>
<html>
<head>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.assessment.*,hotmath.util.*, hotmath.util.sql.*,java.sql.*" %>
<%
    int runId = SbUtilities.getInt(request.getParameter("run_id"));
    String pid = request.getParameter("pid");
    
    String variables = SolutionDao.getInstance().getSolutionContext(runId, pid).getContextJson();
%>
</head>
<body>
  <h1>Solution Context for (run_id=<%= runId %>, pid=<%= pid %>)</h1>
  <pre>
      <%= variables %>
  </pre>
</body>
