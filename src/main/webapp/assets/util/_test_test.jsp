<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="hotmath.cm.server.model.CmUserProgramDao"%>
<%@page import="hotmath.testset.ha.HaTestConfig"%>
<%@page import="hotmath.testset.ha.HaTestDef"%>
<%@page import="java.util.List"%>
<head>
<%@ page import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%
    String test = request.getParameter("test");
    if(test == null) {
       return;
    }
    
    List<String> pids = new HaTestDef().getTestIdsForSegment(CmUserProgramDao.getInstance().loadProgramInfoCurrent(408018),1,new HaTestConfig(),2); 
%>
</head>
<body>
TEST: <%= pids.size() %>
</body>
