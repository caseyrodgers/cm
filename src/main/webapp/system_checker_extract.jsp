<!DOCTYPE HTML PUBLIC"-// W3C//DTD HTML 4.01//EN"" http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
<%
    eu.bitwalker.useragentutils.UserAgent userAgent = eu.bitwalker.useragentutils.UserAgent.parseUserAgentString(request.getHeader("User-Agent")); 
%>
<script>
   var _userAgent = {
       "version":parseFloat("<%= userAgent.getBrowserVersion() %>"),
       "fullVersion":"<%= userAgent.getBrowserVersion() %>",
       "browser":"<%= userAgent.getBrowser().getName()%>"
       };
</script>     
<title>Catchup Math System Requirements Checker</title>
        <link rel="stylesheet" type="text/css" href="/assets/css/SexyButtons/sexybuttons.css">
        <link rel="stylesheet" type="text/css" href="/assets/css/core.css">
<!--[if IE]>
        <link rel="stylesheet" type="text/css" href="/assets/css/core-ie.css" />
<![endif]-->
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script>
var _protocol = document.location.protocol;
document.write(unescape("%3Cscript src='" + _protocol + "//fonts.googleapis.com/css?family=Cantarell' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-634757-3");
pageTracker._trackPageview();
} catch(err) {}</script>      
<link rel="stylesheet" type="text/css" href="/assets/css/success.css">
<style>

body {
   background: white;
}
.column {
  padding-right: 25px;
}

div div p img {
   margin-right: 10px;
}

#final_message {
    font-size: 1.2em;
    font-weight: bold;
}

.not_ready {
    color: red;
}

.ready {
}
</style>
    </head>
    <body>
    
    
                    <div id='main-content' class='yui3-g'>
                    <div class='yui3-u-1'>
                        <!-- InstanceBeginEditable name="main-content" -->
						    <h1>Catchup Math System Requirement Checker</h1>
						    <p>This page will check your computer system and make sure it is capable of running Catchup Math.</p>
                             <p>
						       For additional help with clearing your browser cache, see <a target='_blank' href='http://kb.iu.edu/data/ahic.html'>this site</a>. 
						    </p>

						    <div id='final_message'></div>
						    
						    <div style='margin-left:10px'>
						        <h2>JavaScript</h2>
						        <div id='javascript_check'><p><img style='margin-right: 10px;'src='/gwt-resources/images/check_incorrect.png'/>Not enabled.  You need to have Javascript enabled.</p></div>
						        <h2>Browser</h2>
						        <div id='browser_check'><p>..checking..</p></div>
                                <h2>Window Size</h2>
                                <div id='window_check'><p>..checking..</p></div>
						        <h2 id='flash_check_label'>Flash</h2>
						        <div id='flash_check'><p>..checking..</p></div>
						        <h2>Network Speed</h2>
						        <div id='network_check'><p>..checking..</p></div>
						    </div>   
                        <!-- InstanceEndEditable -->
                    </div>
                </div>
            </div>
    
    
<script src="/assets/js/yahooapis/yui-3.5.1-min.js" type="text/javascript"></script>
<script src='/assets/js/core.js'></script>
<script src='/assets/js/video-utils.js'></script>
        <!-- InstanceBeginEditable name="AfterJavascriptLoad" --><!-- AfterJavascriptLoad -->
        <script src='/assets/js/system_checker.js'></script>
        <script src='/assets/js/swfobject.js'></script>
        <!-- InstanceEndEditable -->
    </body>
<!-- InstanceEnd --></html>
