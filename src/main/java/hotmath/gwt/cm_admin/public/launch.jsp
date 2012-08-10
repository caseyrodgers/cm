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
    <!--  For Tutor/Show Work -->
    <link rel="stylesheet" href="/assets/css/reset.css" type="text/css"/>
    <link rel="stylesheet" href="/css/bookindex_with_tutor_combined.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/testset.css" />
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMath.css" />    
    <link rel="stylesheet" type="text/css" href="/gwt-resources/gxt-2.2.3/css/gxt-all.css" />
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMathAdmin.css" />
    <link rel="stylesheet" type="text/css" href="/css/tutor_widget.css" />
    
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Catchup Math Admin</title>
    <script type="text/javascript" language="javascript" src="/cm_admin/cm_admin.nocache.js"></script>
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/loading_image.css" />
    <script type="text/javascript"
      src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
    </script>

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

     <script src="/js/cm_with_tutor_combined.js"></script>
     <script src="/gwt-resources/js/cm_core.js"></script>
     <script src="/gwt-resources/js/CatchupMath.js"></script>
     <script type="text/javascript" language="javascript" src="/gwt-resources/js/quiz.js"></script>
     <script type="text/javascript" language="javascript" src="/gwt-resources/js/swfobject/swfobject.js"></script>
     <script type="text/javascript" language="javascript" src="/js/hm_mathjax.js"></script>
     <script>
         var __securityKey = '<%= securityKey %>'
     </script>
    <div id='login_info' style='display: none'>
        <%= jsonizedLoginInfo %>
    </div>     
     
</html>
