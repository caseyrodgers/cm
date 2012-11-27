<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.cm.util.stress.CmStressGetStudentGrid"%>
<%@page import="hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction"%>
<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%>
<%@page import="hotmath.cm.util.stress.CmStressRunner"%>
<html>
<head>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.assessment.*,hotmath.util.*,hotmath.util.sql.*,java.sql.*" %>
<%
    int adminId = SbUtilities.getInt(request.getParameter("admin_id"));
    new CmStressGetStudentGrid().runTest(adminId, 0, null, null);
%>
</head>
<body>
<h1>Stress Tests Complete: <%= adminId %></h1>
</body>

