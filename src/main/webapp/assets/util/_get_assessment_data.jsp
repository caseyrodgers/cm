<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.assessment.*,hotmath.util.*, hotmath.util.sql.*,java.sql.*" %>
<%
    int runId = SbUtilities.getInt(request.getParameter("run_id"));
    
    AssessmentPrescription assPres=null;
    Connection conn=null;
    InmhAssessment inmhAssessment=null;
    try {
        conn = HMConnectionPool.getConnection();
        assPres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, runId);
        inmhAssessment = assPres.getAssessment();
    }
    finally {
        SqlUtilities.releaseResources(null,null,conn);
    }
%>
</head>
<body>
  <h1>Assessment Analysis: <%= assPres.getTestRun().getPidList() %></h1>
  <ul>
      <%
        for(INeedMoreHelpResourceTypeDef type: INeedMoreHelpResourceTypeDef.RESOURCE_TYPES) {
            
            %>
            <li><h2><%= type.getLabel() %></h2></li>
            <ol>
            <% 

            for(InmhItemData inmhData: inmhAssessment.getInmhItemUnion(type.getType())) {
                  if(inmhData.getInmhItem().getFile() == null)
                      continue;

              %>
              <li><h3><%= inmhData.getInmhItem() %>  (<a href='_get_run_pool.jsp?run_id=<%= assPres.getTestRun().getRunId() %>&item=<%= inmhData.getInmhItem().getFile() %>'>problem pool</a>)</h3></li>
              <ul>
              <%
              for(String s: inmhData.getPids()) {
                  %>
                  <li> <a href='/tutor/?pid=<%= s %>'><%= s %></a> </li>
                  <%
              }
              %>
              </ul>
              <%
           }
           %>
           </ol>
           <%
        }
      %>
  </ul>
</body>
