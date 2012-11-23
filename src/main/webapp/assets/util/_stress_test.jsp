<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.cm.util.stress.CmStressRunner"%>
<%@page import="hotmath.cm.util.stress.CmStressImplDefault"%>
<html>
<head>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.assessment.*,hotmath.util.*,hotmath.util.sql.*,java.sql.*" %>
<%
    int count = SbUtilities.getInt(request.getParameter("count"));
    int delay = SbUtilities.getInt(request.getParameter("delay"));
    int adminId = SbUtilities.getInt(request.getParameter("admin_id"));
    
    if(count == 0 || delay == 0 || adminId == 0) {
        throw new Exception("required arguments not supplied");
    }
    
    String testClassName=request.getParameter("test_class");
    
    new CmStressRunner(adminId, count, delay, testClassName).runTests();
%>
</head>
<body>
<h1>Stress Tests Running: <%= count %>  Delay: <%= delay %> aid: <%= adminId %></h1>
<h2>Test Class: <%= testClassName %></h2>
</body>

