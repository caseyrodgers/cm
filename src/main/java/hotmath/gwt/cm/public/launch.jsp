<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="hotmath.testset.ha.HaLoginInfo"%>
<%
  /** Provide a wrapper around CM Student initialization to allow for pre-fetching data
      and inserting into initial HTML download.
  */
  String securityKey = (String)request.getSession().getAttribute("securityKey");
  String jsonizedLoginInfo = (String)request.getSession().getAttribute("jsonizedLoginInfo");
  String jsonizedUserInfo = (String)request.getSession().getAttribute("jsonizedUserInfo");
  if(jsonizedLoginInfo == null || jsonizedUserInfo == null) {
	  throw new Exception("'loginInfo' could not be found in session");
  }
%>
<html>
  <head>
  <!--  
      <script type="text/javascript" language="javascript" src="/assets/js/MathJax/MathJax.js"></script>
  -->  
  <link rel="stylesheet" type="text/css" href="/gwt-resources/gxt-2.2.1/css/gxt-all.css" />
  <link class="gray" rel="stylesheet" type="text/css" href="/gwt-resources/gxt-2.2.1/css/gxt-gray.css" />
  <link rel="stylesheet" type="text/css" href="/css/tutor_widget.css" />
  <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMath_combined.min.css" />
  <link rel="stylesheet" type="text/css" href="/css/SexyButtons/sexybuttons.css" />
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <title>Catchup Math</title>
  </head>
  <body>
    <div id="loading">
        <div class="loading-indicator">
        <img src="/gwt-resources/gxt-2.2.1/images/default/shared/large-loading.gif" width="32" height="32"/>Catchup Math<br />
        <span id="loading-msg">loading...</span>
        </div>
    </div>  
    <div id='debug-panel'></div>
    <div id='main-content'></div>
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    <script type="text/javascript" language="javascript" src="/cm_student/cm_student.nocache.js"></script>
    <script type="text/javascript" language="javascript" src="/gwt-resources/js/CatchupMath_combined.min.js"></script>
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

	  var __securityKey = '<%= securityKey %>';
    </script>
    <div id='login_info' style='display: none'>
        <%= jsonizedLoginInfo %>
    </div>
    <div id='user_info' style='display: none'>
        <%= jsonizedUserInfo %>
    </div>
  </body>
</html>
