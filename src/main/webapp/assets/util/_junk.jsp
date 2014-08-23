<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
       
       Object o = hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher.getInstance().execute(new hotmath.gwt.cm_rpc.client.rpc.GetChaptersForProgramSubjectAction("",""));
       
       // Object o = hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher.loadCommandClass(new hotmath.gwt.cm_rpc.client.rpc.GetChaptersForProgramSubjectAction());
%>

<h1>
	DONE: <%= o.getClass() %>
</h1>  