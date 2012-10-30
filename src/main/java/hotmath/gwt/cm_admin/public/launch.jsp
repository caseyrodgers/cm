<!DOCTYPE html>
<%@page import="hotmath.gwt.cm_tools.client.data.HaLoginInfo"%>
<%
  /** Provide a wrapper around CM Admin initialization to allow for pre-fetching data
      and inserting into initial HTML download
  */
  String securityKey = (String)request.getSession().getAttribute("securityKey");
  if(securityKey == null) {
      throw new Exception("'securityKey' could not be found in session");
  }
  String jsonizedLoginInfo = (String)request.getSession().getAttribute("jsonizedLoginInfo");
  if(jsonizedLoginInfo == null) {
      throw new Exception("'loginInfo' could not be found in session");
  }  
%>
<html>
  <head>
  <head>

    <script type="text/javascript"
      src="http://cdn.mathjax.org/mathjax/2.0-latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
    </script>


    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Catchup Math Admin</title>

  <link rel="stylesheet" type="text/css" href="/gwt-resources/css/gxt-reset.css" />

  <link rel="stylesheet" type="text/css" href="/gwt-resources/gxt-2.2.3/css/gxt-all.css" />
  <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMath_combined.min.css" />
  <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMathAdmin.css" />


  <link rel="stylesheet" type="text/css" href="/css/tutor_widget.css" />
  <link rel="stylesheet" type="text/css" href="/css/SexyButtons/sexybuttons.css" />
  <link rel="stylesheet" type="text/css" href="/gwt-resources/css/whiteboard.css" />
  <link rel="stylesheet" type="text/css" href="/gwt-resources/css/mathquill/mathquill.css" />


    <script type="text/javascript" language="javascript" src="/cm_admin/cm_admin.nocache.js"></script>
  </head>

  <body>
    <div id="loading" style="display:none">
        <div class="loading-indicator">
        <img src="/gwt-resources/gxt-2.2.3/images/default/shared/large-loading.gif" width="32" height="32"/>Catchup Math Admin<br />
        <span id="loading-msg">working...</span>
        </div>
    </div>  
    <div id='debug-panel'></div>
    <div id='main-content'></div>
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
  </body>
    
    <script type="text/javascript" language="javascript" src="/gwt-resources/js/CatchupMath_combined.js"></script>
    <script type="text/javascript" language="javascript" src="/gwt-resources/js/CatchupMathAdmin.js"></script>
    
    <!--[if lt IE 9]><script type="text/javascript" src='/gwt-resources/js/excanvas.js'></script><![endif]-->
    
    <script src='http://code.jquery.com/jquery-1.8.1.min.js'></script>
    <script src='/gwt-resources/js/whiteboard.js'></script>
    <script src='/gwt-resources/js/mathquill/mathquill.js'></script>

     <script>
         var __securityKey = '<%= securityKey %>'
     </script>
    <div id='login_info' style='display: none'>
        <%= jsonizedLoginInfo %>
    </div>     
     
</html>
