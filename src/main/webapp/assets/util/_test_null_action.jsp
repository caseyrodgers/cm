<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.gwt.cm_rpc.client.rpc.RpcData"%>
<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.NullAction"%>
<head>
<%
    NullAction action = new NullAction();
    RpcData data = ActionDispatcher.getInstance().execute(action);
%>
</head>
<body>
TEST RESULTS
<br/>
<%= data %>
</body>
