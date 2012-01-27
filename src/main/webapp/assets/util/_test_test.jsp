<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="java.sql.Connection"%>
<%@page import="hotmath.gwt.cm_rpc.client.model.StudentActiveInfo"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="hotmath.cm.program.CmProgramFlow"%>
<%@page import="hotmath.gwt.cm_admin.server.model.CmStudentDao"%>
<%@page import="hotmath.testset.ha.HaTestDao"%>
<%@page import="hotmath.testset.ha.HaTest"%>
<%@page import="hotmath.testset.ha.StudentUserProgramModel"%>
<%@page import="hotmath.testset.ha.HaTestDefDao"%>
<%@page import="hotmath.cm.server.model.CmUserProgramDao"%>
<%@page import="hotmath.testset.ha.HaTestConfig"%>
<%@page import="hotmath.testset.ha.HaTestDef"%>
<%@page import="java.util.List"%>
<head>
<%@ page import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%
    String arg = request.getParameter("test");
    if(arg == null) {
       return;
    }
    
    Connection conn = HotMathProperties.getInstance().getDataSourceObject().getSbDBConnection().getConnection();
    
    StudentUserProgramModel userProg = CmUserProgramDao.getInstance().loadProgramInfoCurrent(23472);
    CmProgramFlow flow = new CmProgramFlow(conn, 23472);
    flow.getActiveInfo().setActiveSegmentSlot(2);
    flow.saveActiveInfo(conn);
    
    
    HaTestDef testDef = userProg.getTestDef();
    HaTest test = HaTestDao.getInstance().createTest(userProg.getUserId(), testDef, 1);
%>
</head>
<body>
TEST Created: <%= test.getNumTestQuestions() %>
</body>
