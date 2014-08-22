<!DOCTYPE HTML PUBLIC"-// W3C//DTD HTML 4.01//EN"" http://www.w3.org/TR/html4/strict.dtd">
<html><!-- InstanceBegin template="/Templates/main.dwt" codeOutsideHTMLIsLocked="false" -->
    <head>
        <!-- InstanceBeginEditable name="doctitle" -->

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
  <meta name="description" content="Catchup Math success stories, heartfelt quotes from administrators, teachers, and students.">
  <meta name="keywords" content="Catchup Math, success stories, self-esteem, outstanding, results, scores improved, confidence" >

<title>Catchup Math System Requirements Checker</title>
<!-- InstanceEndEditable -->
        <link rel="stylesheet" type="text/css" href="/assets/css/yahooapis/reset-3.2.0-min.css">
        <link rel="stylesheet" type="text/css" href="/assets/css/yahooapis/grids-3.2.0-min.css">
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
<script>
   /** specify which main menu item is selected
   */
   var _mainMenuItem=-1;
</script>
        <!-- InstanceBeginEditable name="head" -->
<link rel="stylesheet" type="text/css" href="/assets/css/success.css">
<style>
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
<!-- InstanceEndEditable -->
    </head>
    <body class='yui3-skin-sam'>
        <div class='doc'>
            <div class="yui3-g">
                <!-- Header Area -->
                <div class='yui3-g' id='header'>    
                    <div class='yui3-u-7-8'>
                        <img class='header_image' src='/assets/images/header_main.png'/>
                    </div>
                </div>

                <!-- Menu bar Area -->
                <ul class="yui3-u-1" id='menubar2'>
                        <li>
                            <a class="notselected" href="/index.html">
                                <span>
                                    <span>Home
                                    </span>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="notselected" href="/schools.html">
                                <span>
                                    <span>Schools
                                    </span>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="notselected" href="/colleges.html">
                                <span>
                                    <span>Colleges
                                    </span>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="notselected" href="/students.html">
                                <span>
                                    <span>Students
                                    </span>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="notselected" href="/purchase.html">
                                <span>
                                    <span>Purchase
                                    </span>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="notselected" href="/contact.html">
                                <span>
                                    <span>Contact
                                    </span>
                                </span>
                            </a>
                        </li>
                        <li>
                            <a id='login_button' class="sexybutton sexy_cm_gray" href="/login.html">
                                <span>
                                    <span>Login
                                    </span>
                                </span>
                            </a>
                        </li>                                                       
                    </ul>
                </div>

                <!-- Main Content area -->
                <div id='main-content' class='yui3-g'>
                    <div class='yui3-u-1'>
                        <!-- InstanceBeginEditable name="main-content" -->
						    <h1>Catchup Math System Requirement Checker</h1>
						    <p>This page will check your computer system and make sure it is capable of running Catchup Math.</p>
                             <p>
						       For additional help with clearing your browser cache, see <a target='_blank' href='http://kb.iu.edu/data/ahic.html'>this site</a>. 
						    </p>
						    
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
						        <div id='final_message'></div>
						    </div>   
                        <!-- InstanceEndEditable -->
                    </div>
                </div>
            </div>
        </div>
        <div id='bottom-content-wrapper'>
            <div id='bottom-content' class='doc'>
                <!-- InstanceBeginEditable name="bottom-content" -->&nbsp;<!-- InstanceEndEditable -->
            </div>
        </div>
        <!-- Fast Find Area -->
        <div id='footer-find'>
            <div id='footer-wrapper'>
                <div class='yui3-g'>
                    <div class='yui3-u-1-3'>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>
                            <a href='/schools.html'>Schools
                            </a> <span class='bar'>| </span>
                            <img style='margin-right: 3px' src='/assets/images/arrow_right_small_white.png'/>
                            <a href='/colleges.html'>Colleges
                            </a>
                        </h2>
                        <ul>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/pilot/'>Sign Up for a Pilot
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a target='cm-price-list' href='/cm-price-list.html'>Price List
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a target='cm-order-form' href='/cm-order-form.html'>Order Form
                                </a>
                            </li>
                        </ul>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>
							<a href='/educators.html'>Educators Page
                            </a>
                        </h2>
                        <ul>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/programs.html'>Programs
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/faculty_friendly.html'>Teacher Convenience
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/administrator-getting-started.html'>Getting Started Guide
                                </a>
                            </li>
                            <li id='training_videos_link'>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/training-videos'>Teacher Training Videos
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div class='yui3-u-1-3'>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>
							<a href='/students.html'>Students
                            </a>
                        </h2>
                        <ul>
                            <li id='student_video_link'>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='#' onclick='showMonaMotivationalVideo();return false;'>Student Video
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/catchupmath_personal.html'>Retail Purchase
                                </a>
                            </li>
                        </ul>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>
							<a href='/catchup-math-intervention/'>Intervention
                            </a>
                        </h2>
                        <ul>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/research.html'>Research
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/success.html'>Success Stories
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/programs.html'>Programs
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/faq.html'>FAQ-1 (Prospective Licensees)
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/teacher-faq.html'>FAQ-2 (Current Licensees)
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div class='yui3-u-1-3'>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>
                            <a href='/about.html'>About
                            </a>
                        </h2>
                        <ul class='sitemap-links'>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='/math-for-all.html'>Community Access
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='/support.html'>Support
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='/pressroom/'>Press Room
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='/privacy.html'>Privacy
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='http://hotmath.com'>Hotmath.com
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='/contact.html'>Contact
                                </a>
                            </li>  
                        </ul>
                        <h2 id='webinar_link'>
                            <img src='/assets/images/arrow_right_small_white.png'/>
                            <a href='/webinar'>Webinar
                            </a>
                        </h2>

                    </div>          
                </div>
            </div>
        </div>
        <!-- Footer -->
      <div class='yui3-g' id='footer'>
            <div class='yui3-u-1'>
                <div class='copyright'>Copyright &copy; 2014 Hotmath, Inc.</div>
            </div>
        </div>          
        
<script src="/assets/js/yahooapis/yui-3.5.1-min.js" type="text/javascript"></script>
<script src='/assets/js/core.js'></script>
        <!-- InstanceBeginEditable name="AfterJavascriptLoad" --><!-- AfterJavascriptLoad -->
        <script src='/assets/js/system_checker.js'></script>
        <script src='/assets/js/swfobject.js'></script>
        <!-- InstanceEndEditable -->
    </body>
<!-- InstanceEnd --></html>