<%@page import="hotmath.servlet.Registration"%>
<%@page import="hotmath.paypal.ipn.PayPalIpnManager"%>
<%@page import="hotmath.paypal.ipn.PayPalIpnRequestWaiter"%><html>
<%@ page isErrorPage="false" %>
<%@ page import="java.util.*,hotmath.*,hotmath.subscriber.*,sb.util.*"%>
<%
String _subscriberId = request.getParameter("id");
if(_subscriberId == null)
    throw new Exception("'id' must be specified");
%>
<head>
    <link rel="stylesheet" href="cm_setup_pilot.css" type="text/css"/>
</head>
<body>
<h1>Catchup Pilot Setup</h1>
<p>This will create or recreate a Catchup Math Pilot for the Hotmath subscriber with the id: <b><%= _subscriberId %></b>.</p>
<p>The Admin user, if it does not currently exist, will be setup with password 'admin123'</p>
<p>The expiration date will be for 30 days from today.  If an existing account exists, then an additional 30 days is added.</p>
<p>The user John Doe, if it does not currently exist, will be created with the password jd12345 and the following additional groups:
    <ul>
    <li>quizme - a selfreg group with show work required and auto-enroll prog</li>
    <li>prealgebra - a selfreg group with show work req and Prealgebra prof prog</li>
    <li>algebra1 - a selfreg group with show work req and algebra1 prof prog</li>
    <li>geometry - a selfreg group with show work req and geometry prof prog</li>
    <li>algebra2 - a selfreg group with show work req and algebra2 prof prog</li>
    <li>gradprep - a self-reg group with show work req and cashee program</li>
    </ul>
</p>
<p>You can modify the following attributes for the pilot:</p>
<form id="verticalForm" action="cm_setup_pilot_processing.jsp"> 
            <input type='hidden' name='id' value='<%= _subscriberId %>'/>
			<fieldset> 
				<legend> 
					Define Pilot Setup
				</legend> 
				<label class="first" for="tutoring_enabled"> 
					Tutoring Enabled
					<select id="tutoring_enabled" name="tutoring_enabled"> 
						<option selected="selected" value="0">Disabled</option> 
						<option value="1">Enabled</option> 
					</select> 
				</label> 
				<label for="tutoring_hours"> 
					Tutoring Hours
					<input id="tutoring_hours" name="tutoring_hours" type="text" value="0" /> 
				</label> 
				<label for="show_work_required"> 
					Show Work Required
   			        <select id="show_work_required" name="show_work_required">
						<option selected="selected" value="0">Disabled</option> 
   			            <option value="1">Enabled</option> 
					</select> 
				</label>
                <label for="max_student_count"> 
					Max Number of Students
					<input id="max_student_count" name="max_student_count" type="text" value="1000" /> 
				</label>
   			    <input type='submit' value='Create Pilot'/>
			</fieldset> 
		</form> 
</body>
<script>
   
</script>
</html>

