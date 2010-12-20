<%@ page import="hotmath.util.*,hotmath.util.sql.*,java.sql.*,hotmath.util.*,hotmath.assessment.*,hotmath.testset.ha.*" %>
<%

    int userId = Integer.parseInt(request.getParameter("user_id"));
    Connection conn=null;
    HaTest test = null;
    AssessmentPrescription assTest=null;
    HaUser user=null;
    try {
        conn = HMConnectionPool.getConnection();

        user = HaUser.lookUser(conn, userId,null);
        user.setActiveTest(0);
        user.setActiveTestRunId(0);
        user.setActiveTestSegment(0);
        user.setActiveTestRunSession(0);

        user.update(conn);
    }
    finally {
        SqlUtilities.releaseResources(null,null, conn);
    }
%>
<h1>User Reset: <%= user.getUserName()  %></h1>