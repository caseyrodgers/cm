<!DOCTYPE HTML PUBLIC"-// W3C//DTD HTML 4.01//EN"" http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.gwt.shared.client.util.CmException"%>
<html><!-- InstanceBegin template="/Templates/main.dwt" codeOutsideHTMLIsLocked="false" -->
    <head>
        <!-- InstanceBeginEditable name="doctitle" -->
<title>Catchup Math Login Page</title>
<!-- InstanceEndEditable -->
        <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/3.2.0/build/cssreset/reset-min.css">
        <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/3.2.0/build/cssgrids/grids-min.css">
        <link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Cantarell">
        <link rel="stylesheet" type="text/css" href="/assets/css/SexyButtons/sexybuttons.css">
        <link rel="stylesheet" type="text/css" href="/assets/css/core.css">
<!--[if IE]>
        <link rel="stylesheet" type="text/css" href="/assets/css/core-ie.css" />
<![endif]-->
        
<script>
   /** specify which main menu item is selected
   */
   var _mainMenuItem=-1;
</script>
        <!-- InstanceBeginEditable name="head" -->
<link rel="stylesheet" href="/resources/SexyButtons/sexybuttons.css" type="text/css" />

<%
   Exception exp = (Exception)request.getSession().getAttribute("exception");
   String reason = null;
   if(exp instanceof CmException) {
	   reason = exp.getMessage();   
   }
   else {
	   reason = "Invalid Login Name and/or Password";
   }
   
   boolean showMoreInfo=false; // request.getParameter("debug") != null;
%>
<link rel="stylesheet" type="text/css" href="/resources/css/login.css" />
<style>
.info-section {
	display: none;
}
#login-wrapper {
	text-align: center;
	margin: auto;
	margin-top: 40px;
	padding-bottom: 10px;
	width: 370px;
	border: 1px solid A3C530;
	height: auto;
	background: white;
}

.round-corners {
	border: 1px solid #A3C530;
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
  
#more-info {
   margin-top: 10px;
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
                            <a class="notselected" href="/login.html">
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
        <div id='login-wrapper' class='round-corners'>
          <h1>Login Problem</h1>
          <p class='reason'><%= reason %></p>
 
          <button onclick='doLoginAgain();' class="sexybutton sexysimple sexyblue">
                <div style='display: inline;font-size: 120%'>Try Again</div>
          </button>
          <% if(showMoreInfo)  {
              %>    	   
              <div style='margin-top: 20x;'>
	              <br/>
		          <a onclick="document.getElementById('more-info').style.display = 'block';this.style.display = 'none'">More Info</a>
		          <div id='more-info' style='display: none'>
		             <%= exp != null?exp.getMessage():"No more info" %>
		          </div>
	          </div>
	          <%
	          }
	      %>
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
                                <a href='/assets/forms/cm-price-list.pdf'>Price List
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/assets/forms/cm-order-form.pdf'>Order Form
                                </a>
                            </li>
                        </ul>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>                            <a href='/educators.html'>Educators Page
                            </a>
                        </h2>
                        <ul>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/teacher-training-video.html'>Teacher Training Video
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/administrator-getting-started.html'>Getting Started Guide
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/assets/documents/cm-usage-performance-data.pdf'>Performance Data
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/standards.html'>Standards
                                </a>
                            </li>                        
                        </ul>
                    </div>
                    <div class='yui3-u-1-3'>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>                            <a href='/students.html'>Students
                            </a>
                        </h2>
                        <ul>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/student-video.html'>Student Video
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small.png'/>
                                <a href='/catchupmath_personal.html'>Retail Purchase
                                </a>
                            </li>
                        </ul>
                        <h2>
                            <img src='/assets/images/arrow_right_small_white.png'/>                            <a href='/intervention.html'>Intervention
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
                                <a href='/faq.html'>FAQ
                                </a>
                            </li>                                                  
                        </ul>
                    </div>
                    <div class='yui3-u-1-3'>
                        <h2>&nbsp;
                        </h2>
                        <ul class='sitemap-links'>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='/support.html'>Support
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='http://hotmath.com/pressroom/'>Press Room
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
                                <a href='/about.html'>About
                                </a>
                            </li>
                            <li>
                                <img src='/assets/images/arrow_right_small_red.png'/>
                                <a href='/contact.html'>Contact
                                </a>
                            </li>                         
                        </ul>
                    </div>          
                </div>
            </div>
        </div>
        <!-- Footer -->
      <div class='yui3-g' id='footer'>
            <div class='yui3-u-1'>
                <div class='copyright'>Copyright &copy; Hotmath, Inc.                </div>
            </div>
        </div>          
        
<script src="http://yui.yahooapis.com/3.2.0/build/yui/yui-min.js" type="text/javascript"></script>
<script src='/assets/js/core.js'></script>
        <!-- InstanceBeginEditable name="AfterJavascriptLoad" --><!-- AfterJavascriptLoad -->
		<script>
		  function doLoginAgain() {
       	     history.go(-1);
          }
		</script>
		<!-- InstanceEndEditable -->
    </body>
<!-- InstanceEnd --></html>
