<%@ page import="hotmath.util.HMConnectionPool" %>
<%@ page import="hotmath.util.sql.SqlUtilities" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="hotmath.gwt.shared.server.service.command.helper.GetAssignmentHTMLHelper" %>
<%@ page import="hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentHTML" %>

<%
Connection conn = null;
String errMsg = null;
String assignmentHTML = "";

try {
	  String key = request.getParameter("key");
	  String lines = request.getParameter("lines");
	  int assignKey = 0;
	  int numWorkLines = 15;
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
}
finally {
	  SqlUtilities.releaseResources(null, null, conn);
}
%>

<html style="overflow: hidden;">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Assignment</title>  
    <script type="text/javascript" src="http://catchupmath.com/assets/mathjax/2.3/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
    </script>
        
    <!--[if gt IE 5.5000]>
        <link rel="stylesheet" href="gwt-resources/css/CatchupMath-resource_container-IE.css">
    <![endif]-->

    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMath_combined.min.css">
    <link rel="stylesheet" href="http://test.catchupmath.com/cm_student/css/chart.css">
    <script type="text/javascript" charset="utf-8" async="" data-requirecontext="_" data-requiremodule="amd_main.nocache" src="/gwt-resources/js/amd_main.nocache.js"></script>
  </head>
  <body cz-shortcut-listen="true"><div style="visibility: hidden; overflow: hidden; position: absolute; top: 0px; height: 1px; width: auto; padding: 0px; border: 0px; margin: 0px; text-align: left; text-indent: 0px; text-transform: none; line-height: normal; letter-spacing: normal; word-spacing: normal;"><div id="MathJax_Hidden"></div></div><div id="MathJax_Message" style="display: none;"></div>
        <div id="main-content">
            <%= assignmentHTML %>
        </div>

        <script type="text/javascript" language="javascript" src="/gwt-resources/js/CatchupMath_combined.js"></script>
        <script type="text/javascript" language="javascript" src="/cm_student/cm_student.nocache.js"></script><script language="javascript" src="http://test.catchupmath.com/cm_student/swfobject-2.0.js"></script><script defer="defer">cm_student.onInjectionDone('cm_student')</script>
    
        <!--[if lt IE 9]><script type="text/javascript" src='/gwt-resources/js/excanvas.js'></script><![endif]-->

        <script src="/gwt-resources/jlibs/requirejs-1.2.1/require_jquery.js" data-main="/gwt-resources/js/amd_main.nocache"></script>
        
        <script src="/gwt-resources/js/whiteboard_v3.js"></script>
        
        <script>
            /** for debugging */
            // _productionMode=false;
            // InmhButtons = {};
            /** setup timer to change loading message to a warning
              if loading message still shown.
            */
            function checkForCompletion() {
               var d = document.getElementById("working...");
               if(!__cmInitialized) {
                   d.innerHTML = "<div id='loading-error'><h2>Catchup Math loading delayed.</h2>Please click <a href='http://hotmath.com/resources/util/loading-error.jsp'>here</a> to proceed.</div>";
               }
            }
            setTimeout(checkForCompletion, 60000);

            var __securityKey = 'cm_f62fdf8d54137068e8a016378287942d';
      
            /** Disable MathJax */
            function __processMathJax() {}
        </script>

</body></html>