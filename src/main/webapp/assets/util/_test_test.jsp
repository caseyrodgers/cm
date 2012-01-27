<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="hotmath.testset.ha.StudentUserProgramModel"%>
<%@page import="hotmath.testset.ha.HaTestDefDao"%>
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
    
    StudentUserProgramModel userProg = CmUserProgramDao.getInstance().loadProgramInfoCurrent(23472);
    List<String> pids = HaTestDefDao.getInstance().getTestIds(userProg, "prealgptests2", "Course Test", 2, 1, 10, new HaTestConfig("{segments:6}")); 
%>
</head>
<body>
TEST: <%= pids.size() %>
</body>
