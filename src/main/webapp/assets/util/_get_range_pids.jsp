<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="java.sql.Connection"%>
<%@page import="hotmath.assessment.InmhAssessment"%><html><!-- InstanceBegin template="/Templates/hm_core_layout.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<%@ page import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%

    String _matches[] = null;
    String _pid=null;
    ConcordanceEntry con = null;
    String _range = request.getParameter("range");
    
    Connection conn=null;
    try {
	    if(_range != null) {
	    	_matches = new ConcordanceEntry(conn, _range).getGUIDs();
	    }
	    else {
	    	String item = request.getParameter("item");
	    	if(item == null)
	    		throw new Exception("'item' or 'range' must be specified");
	    	
	    	_range = item;
	    	_matches = InmhAssessment.getItemSolutionPool(item);
	    }
    }
    finally {
        SqlUtilities.releaseResources(null,null,conn);
    }
%>
</head>
<body>
  <h1>Solutions matching range: <%= _range %></h1>
  <ol>
      <%
          for(String s: _matches) {
          %>
              <li> <a href='/tutor/?pid=<%= s %>'><%= s %></a> </li>
          <%
          }

      %>
  </ol>
</body>
