<!DOCTYPE html>
<%@ page import="hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentHTML" %>
<%@ page import="hotmath.gwt.shared.server.service.command.helper.GetAssignmentHTMLHelper" %>
<%@ page import="hotmath.util.HMConnectionPool" %>
<%@ page import="hotmath.util.sql.SqlUtilities" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.sql.Connection" %>

<%
Logger _logger = Logger.getLogger(this.getClass()); 
Connection conn = null;
String errMsg = "";
String stackTrace = null;
String assignmentHTML = "";
String assignmentHeader = "";
int assignKey = 0;

try {
	  String key = request.getParameter("key");
	  String lines = request.getParameter("lines");
	  int numWorkLines = 10;
	  if (key != null && key.trim().length() > 0) {
		  assignKey = Integer.parseInt(key);
	  }
	  if (lines != null && lines.trim().length() > 0) {
		  numWorkLines = Integer.parseInt(lines);
	  }
	  conn = HMConnectionPool.getConnection();
	  GetAssignmentHTMLHelper helper = new GetAssignmentHTMLHelper();
	  assignmentHTML = helper.getAssignmentHTML(assignKey, numWorkLines, conn);
	  assignmentHeader = helper.getHeader(assignKey);
}
catch (Exception e) {
 	  errMsg = e.getMessage();
 	  _logger.error(String.format("assignKey: %d", assignKey), e);
}
finally {
	  SqlUtilities.releaseResources(null, null, conn);
}
%>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Assignment</title>

    <!-- $(document).ready(function() { -->

    <script type="text/javascript" src="http://catchupmath.com/assets/mathjax/2.3/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
    </script>

    <!--[if gt IE 5.5000]>
        <link rel="stylesheet" href="/gwt-resources/css/CatchupMath-resource_container-IE.css">
    <![endif]-->

    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMath_combined.min.css">
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/assignment-print.css" media="print" />
    <style>
     
        .tutorData {
	         display: none !important;
         }
         
         
        .prob-stmt { 
	         padding-left:10px; 
	         padding-right:10px; 
        }
          
    </style>

    <script src="http://code.jquery.com/jquery-1.10.2.js"></script>

    <script type="text/javascript">
    
      $(document).ready(function() {
         initializeAssignmentPrint();
      });

      function getHtmlForWidget(widgetType) {
    	  switch(widgetType) {
    	  case "widget_plot":
    	  case "xy":
    		  return "<img src='/assets/images/x-y-axes.png'>";
    		  break;
    	  case "x":
    		  return "<img src='/assets/images/number-line.png'>"; 
          default:
        	  return "<img src='/assets/images/underline.png'>";
    	  }
    	  
      }

      function getWidgetType(jso) {
    	  type = jso.type;
    	  if (type == null) {
    	      type = jso.qtype;
    	  }
    	  return type;
      }
/*
      function fixJSON(nonStdJSON) {
      	var stdJSON = "{";
      	var kvPairs = nonStdJSON.split(',');
      	for (var i in kvPairs) {
      		var arr = kvPairs[i].split(":");
      		//alert("key: " + arr[0] + ", value: " + arr[1]);
      		var key = arr[0].replace("{", "").replace(/ /g, "");
      		var value = arr[1].replace("}", "");
      		stdJSON += '"' + key + '":"' + value + '"';
      		if (i < (kvPairs.length-1)) {
      			stdJSON += ', '; 
      		}
      	}
      	stdJSON += "}";
      	
      	return stdJSON;
      }
 */
    </script>

  </head>

  <body>

      <div style='margin-bottom:15px;'>
          <a href="#" onclick="window.print();return false;">print</a>
      </div>
      <div id="main-content" class='assignment-print'>
          <%= assignmentHeader %>
          <%= assignmentHTML %>
          <% if (errMsg.length() > 0) { %>
          Error: <%= errMsg %>
          <% } %>
      </div>

      <script type="text/javascript" language="javascript" src="/gwt-resources/js/CatchupMath_combined.js"></script>
      <script type="text/javascript" language="javascript" src="/gwt-resources/js/whiteboard_v3.js"></script>
      <script type="text/javascript" language="javascript" src="/gwt-resources/js/assignment_print.js"></script>
  </body>
</html>