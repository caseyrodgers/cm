<%@page import="java.sql.Connection"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="hotmath.gwt.shared.server.service.command.GeneratePdfCommand"%>
<%@page import="hotmath.gwt.shared.client.rpc.action.GeneratePdfAction"%>
<%@page import="hotmath.gwt.shared.client.rpc.CmWebResource"%>
<%@page import="hotmath.testset.ha.HaUserFactory"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType"%><h1>Test Result</h1>
<%@ page import="hotmath.gwt.shared.server.service.*" %>
<%
     Connection c = null;
     try {
         c = HMConnectionPool.getConnection();
         
         List<Integer> studentUids = Arrays.asList(HaUserFactory.createDemoUser().getUserKey());
         GeneratePdfAction action = new GeneratePdfAction(GeneratePdfAction.PdfType.REPORT_CARD,13,studentUids);
         CmWebResource resource = new GeneratePdfCommand().execute(c, action);
     }
     finally {
         SqlUtilities.releaseResources(null,null,c);
     }

     String result = LoadTester.loadTestCreatePrescription();
%>
<pre>
<%= result %>
</pre>
