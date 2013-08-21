<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="java.sql.Connection"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="hotmath.assessment.InmhItemData"%>
<%@ page contentType="text/html" import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*, hotmath.inmh.*" %>
<%
    String arg = request.getParameter("test");
    if(arg == null) {
       return;
    }

    String lesson = request.getParameter("lesson");
    InmhItemData itemData = new InmhItemData(new INeedMoreHelpItem("Review", lesson, ""));
    Connection conn=null;
    Object data=null;
    try {
	    conn = HMConnectionPool.getConnection();
	    data = itemData.getWidgetPool(conn, "test");
    }
    finally {
        SqlUtilities.releaseResources(null,null,conn);
    }

%>
<head>
</head>
<body>
OK
<br/>
<%= data %>
</body>
</html>