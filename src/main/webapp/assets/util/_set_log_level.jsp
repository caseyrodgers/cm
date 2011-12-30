<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.apache.log4j.LogManager"%>
<head>
<%@ page import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%
    ConcordanceEntry con = null;
    String levelName = request.getParameter("level");
    if(levelName == null) 
      return;
    
    Level level = Level.INFO;
    if(levelName.equals("DEBUG")) {
        level = Level.DEBUG;
    }
    LogManager.getRootLogger().setLevel(level);
%>
</head>
<body>
<h1>Level set to: <%= level %></h1>
</body>
