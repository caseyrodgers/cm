<!DOCTYPE html>
<%@page import="hotmath.gwt.cm_tools.client.data.HaLoginInfo"%>
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
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <title>Catchup Math</title>  
        <script type="text/javascript"
             src="http://catchupmath.com/assets/mathjax/2.2/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
        </script>

        
        <!--[if gt IE 5.5000]>
            <link rel="stylesheet" href="gwt-resources/css/CatchupMath-resource_container-IE.css">
        <![endif]-->

        <link rel="stylesheet" type="text/css" href="/gwt-resources/css/gxt-reset.css" />

        <!-- 
            <link rel="stylesheet" type="text/css" href="/gwt-resources/gxt-2.2.3/css/gxt-all.css" />
            <link class="gray" rel="stylesheet" type="text/css" href="/gwt-resources/gxt-2.2.3/css/gxt-gray.css" />
        -->
        <link rel="stylesheet" type="text/css" href="/gwt-resources/css/CatchupMath_combined.min.css" />
        <link rel="stylesheet" type="text/css" href="/css/SexyButtons/sexybuttons.css" />
  
        <link rel="stylesheet" type="text/css" href="/gwt-resources/js/calculator/jquery.calculator.css" />
    </head>
    <body>
        <div id="loading">
            <div class="loading-indicator">
                <img src="/gwt-resources/gxt-2.2.3/images/default/shared/large-loading.gif" width="32" height="32"/>Catchup Math<br />
                <span id="loading-msg">loading...</span>
            </div>
        </div>  
        <div id='debug-panel'></div>
        <div id='main-content'></div>

        <script> 
            if (typeof console === "undefined") {
                console = {
                    log: function (x) {
                       // empty
                    }
                };
            }
        </script>
        <!-- OPTIONAL: include this if you want history support -->
        <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
        <script type="text/javascript" language="javascript" src="/gwt-resources/js/CatchupMath_combined.js"></script>
        <script type="text/javascript" language="javascript" src="/cm_student/cm_student.nocache.js"></script>
    
        <!--[if lt IE 9]><script type="text/javascript" src='/gwt-resources/js/excanvas.js'></script><![endif]-->

        <script src="/gwt-resources/jlibs/requirejs-1.2.1/require_jquery.js" data-main="/gwt-resources/js/amd_main.nocache"></script>
        
        <script src='/gwt-resources/js/whiteboard_v3.js'></script>
        
        
        <!-- 
        <script type="text/javascript" language="javascript" src="/gwt-resources/js/CatchupMath_combined.js"></script>
        <script src='http://code.jquery.com/jquery-1.8.1.min.js'></script>
        <script src='/gwt-resources/js/whiteboard.js'></script>
        <script src='/gwt-resources/js/mathquill/mathquill.js'></script>
         -->

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
      
            /** Disable MathJax */
            function __processMathJax() {}
        </script>
        <div id='login_info' style='display: none'>
            <%= jsonizedLoginInfo %>
        </div>
        <div id='user_info' style='display: none'>
            <%= jsonizedUserInfo %>
        </div>

<!-- begin SnapEngage code -->
<!-- 
<script type="text/javascript">
  (function() {
    var se = document.createElement('script'); se.type = 'text/javascript'; se.async = true;
    se.src = '//commondatastorage.googleapis.com/code.snapengage.com/js/68c85e94-929f-49ec-ac9a-6f07e9918a0f.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(se, s);
  })();
</script>
-->
<!-- end SnapEngage code -->


  </body>
</html>
