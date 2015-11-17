<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.testset.ha.SolutionDao"%>
<%@page import="hotmath.cm.util.CmPilotCreate"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.List"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.log4j.Logger"%>
<html>
<head>
<%
  SolutionDao.getInstance().testIt();
%>
</head>
<body>
<h1>
	Success!
</h1>
</body>

</html>
