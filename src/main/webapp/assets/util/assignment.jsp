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
int assignKey = 0;

try {
	  String key = request.getParameter("key");
	  String lines = request.getParameter("lines");
	  int numWorkLines = 0;
	  if (key != null && key.trim().length() > 0) {
		  assignKey = Integer.parseInt(key);
	  }
	  if (lines != null && lines.trim().length() > 0) {
		  numWorkLines = Integer.parseInt(lines);
	  }
	  conn = HMConnectionPool.getConnection();
	  GetAssignmentHTMLHelper helper = new GetAssignmentHTMLHelper();
	  assignmentHTML = helper.getAssignmentHTML(assignKey, numWorkLines, conn);
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

    <!-- script type="text/javascript" src="http://catchupmath.com/assets/mathjax/2.3/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
    </script -->

    <!--[if gt IE 5.5000]>
        <link rel="stylesheet" href="/gwt-resources/css/CatchupMath-resource_container-IE.css">
    <![endif]-->

    <link rel="stylesheet" type="text/css" href="http://catchupmath.com/gwt-resources/css/CatchupMath_combined.min.css">
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/assignment-print.css" media="print" />
    <style>
      #hm_flash_widget { display:none; }
      .prob-stmt { padding-left:10px; padding-right:10px; }
    </style>

    <!-- script type="text/javascript" charset="utf-8" async="" data-requirecontext="_" data-requiremodule="amd_main.nocache"
       src="http://catchupmath.com/gwt-resources/js/amd_main.nocache.js"></script -->
    <script src="http://code.jquery.com/jquery-1.10.2.js"></script>

    <script type="text/javascript">
      window.onload = function() {
          var widgets = $('.hm_flash_widget');
          if (widgets.length < 1) return;
          for (var idx=0; idx<widgets.length; idx++) {
              var innerDivs = $( widgets[idx] ).children(".hm_flash_widget_def");
              if (innerDivs.length < 1) continue;
              //var jsonStr = fixJSON(innerDivs[0].innerHTML);
              var json = innerDivs[0].innerHTML;
              //var json = $.parseJSON(jsonStr);
              var jso = eval('(' + json + ')');
              widgetType = jso.type;
              //alert("widgetType: " + widgetType);
              widgets[idx].innerHTML = widgetType + "<br/>" + jsonStr;
          }
      };

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

    </script>

  </head>

  <body>

      <div style='margin-bottom:15px;'>
          <a href="#" onclick="window.print();return false;">print</a>
      </div>
      <div id="main-content" class='assignment-print'>
          <%= assignmentHTML %>
          <% if (errMsg.length() > 0) { %>
          Error: <%= errMsg %>
          <% } %>
      </div>

      <!-- script type="text/javascript" language="javascript" src="http://catchupmath.com/gwt-resources/js/CatchupMath_combined.js"></script -->
      <!-- script type="text/javascript" language="javascript" src="http://catchupmath.com/gwt-resources/js/whiteboard_v3.js"></script -->
  </body>
</html>