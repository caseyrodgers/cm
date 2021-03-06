<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="hotmath.gwt.cm_tools.client.data.HaLoginInfo"%>
<%
  /** Provide a wrapper around CM Admin initialization to allow for pre-fetching data
      and inserting into initial HTML download
  */
  HaLoginInfo loginInfo = (HaLoginInfo)request.getSession().getAttribute("loginInfo");
  if(loginInfo == null) {
	  throw new Exception("'loginInfo' could not be found in session");
  }
%>
<html>
  <head>
  
    <!--  For Tutor/Show Work -->  
    <link rel="stylesheet" href="/css/bookindex_with_tutor_combined.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMath_combined.css" />    

      
    <link rel="stylesheet" type="text/css" href="/gwt-resources/gxt-2.1.3/css/gxt-all.css" />
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMathAdmin.css" />
    
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Catchup Math Admin</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="/cm_admin/cm_admin.nocache.js"></script>
    
    <link rel="stylesheet" type="text/css" href="/gwt-resources/css/loading_image.css" />
    <script>var __securityKey = '<%= loginInfo!=null?loginInfo.getKey():"" %>'</script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                 	          -->
  <body>
    <div id="loading" style="display:none">
        <div class="loading-indicator">
        <img src="/gwt-resources/gxt-2.1.3/images/default/shared/large-loading.gif" width="32" height="32"/>Catchup Math Admin<br />
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
     
     <script type="text/javascript" language="javascript" src="/gwt-resources/js/swfobject/swfobject.js"></script>     
</html>
