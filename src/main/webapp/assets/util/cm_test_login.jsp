<%@page import="java.sql.Connection"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="hotmath.gwt.shared.server.service.command.GeneratePdfCommand"%>
<%@page import="hotmath.gwt.shared.client.rpc.action.GeneratePdfAction"%>
<%@page import="hotmath.gwt.shared.client.rpc.CmWebResource"%>
<%@page import="hotmath.testset.ha.HaUserFactory"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="hotmath.gwt.cm_tools.client.data.HaBasicUser"%>
<%@page import="hotmath.testset.ha.HaLoginInfo"%><h1>Test Result</h1>
<%@ page import="hotmath.gwt.shared.server.service.*" %>
<%
     Connection c = null;
             HaBasicUser cmUser=null;
     try {

         String user = request.getParameter("user");
         String pwd = request.getParameter("pwd");

             SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
             if(user.equals("catchup_demo")) {
                 cmUser = HaUserFactory.createDemoUser();
             }
             else {
                 cmUser = HaUserFactory.loginToCatchup(user, pwd);
             }
             HaLoginInfo loginInfo = new HaLoginInfo(cmUser);

     }
     finally {
         SqlUtilities.releaseResources(null,null,c);
     }

     String result = LoadTester.loadTestCreatePrescription();
%>
<pre>
<%= cmUser %>
<%= result %>
</pre>
