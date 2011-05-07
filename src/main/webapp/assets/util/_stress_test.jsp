<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.gwt.shared.server.service.command.stress.CmStress"%>
<html>
<head>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.assessment.*,hotmath.util.*, hotmath.util.sql.*,java.sql.*" %>
<%
    int runId = SbUtilities.getInt(request.getParameter("count"));

     String as[] = {"-count=" + runId};
     CmStress.main(as);
%>
</head>
<body>
<h1>Stress Test</h1>
</body>
