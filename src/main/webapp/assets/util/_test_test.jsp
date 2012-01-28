<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="hotmath.testset.ha.HaUserDao"%>
<%@page import="hotmath.testset.ha.HaUser"%>
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
<%@ page contentType="text/plain" import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%
    String arg = request.getParameter("test");
    if(arg == null) {
       return;
    }


    final int segmentSlot = 2;
    int segment = 1;
    String chapter = "Course Test";




    Connection conn = HotMathProperties.getInstance().getDataSourceObject().getSbDBConnection().getConnection();

    CmProgramFlow flow = new CmProgramFlow(conn, 408018);
    flow.getActiveInfo().setActiveSegmentSlot(2);
    flow.saveActiveInfo(conn);


    final StudentUserProgramModel userProgram = flow.getUserProgram();
    HaUser user = HaUserDao.getInstance().lookUser(userProgram.getUserId(),false);
    final HaTestConfig testConfig = user.getTestConfig();


    HaTestDef testDef = userProgram.getTestDef();
    // List<String> testIds = HaTestDefDao.getInstance().getTestIdsForSegment(userProgram, segment, testDef.getTextCode(), "Course Test", testConfig,segmentSlot);
    //List<String> testIds = HaTestDefDao.getInstance().getTestIds(userProgram, testDef.getTextCode(), chapter, 1, 1, 10, testConfig);
    
    
    List<String> testIds = HaTestDefDao.getInstance().getTestIds(userProgram, testDef.getTextCode(), chapter, segmentSlot, 0, 99999, testConfig);
    
    int cnt = testIds.size();

    // break program into segments?
    int solsPerSeg = (config != null) ? solsPerSeg = cnt / testConfig.getSegmentCount() : 0;
     solsPerSeg = (solsPerSeg < 5) ? cnt : solsPerSeg;

    int segPnEnd = ((segment) * solsPerSeg);
    int segPnStart = (segPnEnd - (solsPerSeg - 1));

    
    //testIds = HaTestDefDao.getInstance().getTestIdsForSegment(userProgram, segment, testDef.getTextCode(), "Course Test", testConfig,2);
%>
</head>
<body>
info: <%= testIds.size() + ", " + solsPerSeg + ", " + segPnStart + ", " + segPnEnd %>
<br/>
Config: <%= testConfig %>
<br/>
TEST 2 Created: <%= testIds.size() %>
<br/>
<%
    for(String id: testIds) {
        %>
        <%= id + "\n" %>
        <%
    }
     %>
    }

%>
</body>
</html>