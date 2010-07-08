<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.cm.login.service.CmExceptionUserExpired"%>
<html>
<!-- InstanceBegin template="/Templates/cm_main_2.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<!-- InstanceBeginEditable name="doctitle" -->
<title>Catchup Math Login Page</title>
<!-- InstanceEndEditable -->
<link rel="icon" href="favicon.ico?" type="image/x-icon"/>
<link rel="stylesheet" type="text/css" href="/resources/js/yui/build/reset-fonts-grids/reset-fonts-grids.css" />
<link rel="stylesheet" type="text/css" href="/resources/js/yui/build/container/assets/skins/sam/container.css" />
<link rel="stylesheet" type="text/css" href="/resources/css/core.css"/>
<style>
.info-section-img {
	position: absolute;
	right: 20px;
	top: 23px;
}
</style>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-634757-3");
pageTracker._trackPageview();
} catch(err) {}</script>
<!-- InstanceBeginEditable name="head" -->
<link rel="stylesheet" href="/resources/SexyButtons/sexybuttons.css" type="text/css" />

<%
   Exception exp = (Exception)request.getSession().getAttribute("exception");
   String reason = null;
   if(exp instanceof CmExceptionUserExpired) {
	   reason = exp.getMessage();   
   }
   else {
	   reason = "Invalid Login Name and/or Password";
   }
   
%>
<style>
.info-section {
	display: none;
}
#login-wrapper {
	text-align: center;
	margin: auto;
	margin-top: 40px;
	width: 370px;
	border: 1px solid black;
	height: 150px;
}

#login-wrapper h1 {
  padding: 0;
  }
  

#login-wrapper p {
    margin: 0;
    padding: 0;
    width: auto;
  }
  
#login-wrapper button {
  margin-top: 15px;
  }
</style>
<!-- InstanceEndEditable -->
</head>
<body class='yui-skin-sam'>
<div id="custom-doc" class="yui-t7">
  <div id="main-wrapper">
    <div id="hd" role="banner">
      <div id='logo-div'> <img alt='Catchup Math - Quiz, Review, Practice - Get caught up in algebra and geometry' class='logo png_bg' src='/resources/images/logo.png'/> </div>
      <div id='login-btn'> <a id='login-anchor' href='/login.html'> <img src='/resources/images/login2.png'/> </a> <img class='powered_by_hotmath' src='/resources/images/powered_by_hotmath.png'/> </div>
      <div class="toolbar">
        <ul class='top-menu'>
          <li class='first'><a href='/'>Home</a></li>
          <li class='divider'>&nbsp;</li>
          <li><a href='/schools.html'>Schools</a></li>
          <li class='divider'>&nbsp;</li>
          <li><a href='/colleges.html'>Colleges</a></li>
          <li class='divider'>&nbsp;</li>
          <li><a href='/students.html'>Students</a></li>
          <li class='divider'>&nbsp;</li>
          <li><a href='/support.html'>Support</a></li>
          <li class='divider'>&nbsp;</li>
          <li><a href='/catchupmath_personal.html'>Purchase</a></li>
          <li class='divider'>&nbsp;</li>
          <li class='last'><a href='/contact.html'>Contact</a></li>
        </ul>
      </div>
    </div>
    <div id="bd" role="main"> <!-- InstanceBeginEditable name="MainBody" --> <!-- InstanceEndEditable -->
      <div id='bd-left'> <!-- InstanceBeginEditable name="BodyLeft" -->
        <div id='login-wrapper'>
          <h1>Login Problem</h1>
          <p class='reason'><%= reason %></p>
 
          <button onclick='history.go(-1);' class="sexybutton sexysimple sexyblue">
                <div style='display: inline;font-size: 120%'>Try Again</div>
          </button>


        </div>
        <!-- InstanceEndEditable --> </div>
      <div id='bd-right'> <!-- InstanceBeginEditable name="BodyRight" --><!-- InstanceEndEditable --> </div>
      <div class="info-section">
        <div class="section first"> <a href='/schools.html'> <img class='info-section-img' src='/resources/images/for_schools.png'/>
          <h3>For Schools</h3>
          <p class='copy-text'>Help under-prepared students succeed in their math courses and tests.</p>
          <div class='find-out-more'><img src='/resources/images/link_arrow.png'/> <span>Find out more</span></div>
          </a> </div>
        <div class='section'> <a href='/colleges.html'><img class='info-section-img' src='/resources/images/for_colleges.png'/>
          <h3>For Colleges</h3>
          <p class='copy-text'> Help your under-prepared students meet prerequisites or catch up in class.</p>
          <div class='find-out-more'><img src='/resources/images/link_arrow.png'/> <span>Find out more</span> </div>
          </a> </div>
        <div id='section-last' class='section last'> <a href='/students.html'><img class='info-section-img' src='/resources/images/for_students.png'/>
          <h3>For Students</h3>
          <p class='copy-text'> Use Catchup Math to get caught up in your class,
            or to get ready for your next class.</p>
          <div class='find-out-more'><img src='/resources/images/link_arrow.png'/> <span>Find out more</span> </div>
          </a> </div>
      </div>
    </div>
    <div id="ft" role="contentinfo">
      <ul class='bottom-menu'>
        <li class='first'><a href='/about.html'>About</a></li>
        <li class='divider'>|</li>
        <li><a href='/contact.html'>Contact</a></li>
        <li class='divider'>|</li>
        <li><a href='/sitemap.html'>Site Map</a></li>
        <li class='divider'>|</li>
        <li><a href='http://hotmath.com/pressroom/'>Press Room</a></li>
        <li class='divider'>|</li>
        <li><a href='http://hotmath.com/privacy_policy.html'>Privacy Policy</a></li>
        <li class='divider'>|</li>
        <li class='last'><a href='http://hotmath.com'>Hotmath.com</a></li>
      </ul>
    </div>
  </div>
</div>
<script type="text/javascript" src="/resources/js/yui/build//yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="/resources/js/yui/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="/resources/js/yui/build/container/container-min.js"></script>
<script type="text/javascript" src="/resources/js/yui/build/connection/connection-min.js"></script>
<script src="/resources/js/core.js"></script>
<script>
//    Ext.onReady(function() {
//        Ext.DomHelper.append(document.body, {tag: 'p', cls: 'test'});
//        Ext.select('p.test').update('Ext Core successfully injected');
//    });    
</script>
<!-- InstanceBeginEditable name="AfterCoreJavascript" --><!-- InstanceEndEditable -->
</body>
<!-- InstanceEnd -->
</html>
