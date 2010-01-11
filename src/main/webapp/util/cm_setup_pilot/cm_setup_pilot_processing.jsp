<%@page import="hotmath.servlet.Registration"%>
<%@page import="hotmath.paypal.ipn.PayPalIpnManager"%>
<%@page import="hotmath.paypal.ipn.PayPalIpnRequestWaiter"%>
<%@page import="hotmath.cm.util.CmPilotCreate"%>
<%@ page isErrorPage="false" %>
<%@ page import="java.util.*,hotmath.*,hotmath.subscriber.*,sb.util.*"%>
<%


String _subscriberId = request.getParameter("id");
if(_subscriberId == null)
    throw new Exception("'id' must be specified");

String te = request.getParameter("show_work_required");
Integer aid=null;
if(te != null) {
    Boolean showWorkRequired = SbUtilities.getBoolean(request.getParameter("show_work_required"));
    Integer maxStudentCount = SbUtilities.getInt(request.getParameter("max_student_count"));
    
    aid=new CmPilotCreate(_subscriberId,false, 0, showWorkRequired, maxStudentCount).getAid();
}
else {
    throw new Exception("Pilot configuration information was not found");
}
%>
<head>
    <link rel="stylesheet" href="cm_setup_pilot.css" type="text/css"/>
</head>
<body>
<h1>Catchup Pilot Setup Complete</h1>
<p>
The Catchup Math pilot has been created successfully.
</p>
<p>
<a target='_new' href='/cm_admin/CatchupMathAdmin.html?uid=<%= aid %>&debug=true'>Admin Page</a> 
</p>
</body>
<script>
   
</script>

</html>

