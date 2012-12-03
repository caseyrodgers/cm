<!DOCTYPE HTML>
<%@page import="org.apache.log4j.Logger"%>
<%
    if(request.getParameter("force") != null) {
        Logger.getRootLogger().info("Forcing Java garbage collection (GC)");
        System.gc();
    }
    else {
        throw new Exception("_java_gc.jsp: called without proper arguments");
    }
%>
<html>
<head>
<title>Java Garbage Collection Request</title>
</head>
<body>
<h1>Java Garbage Collection has been requested (<%= new java.util.Date() %>))</h1>
</body>
</html>

