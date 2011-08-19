<%@ page import="hotmath.util.*" %>
<%@ page import="hotmath.util.sql.SqlUtilities" %>
<%@ page import="hotmath.gwt.cm_rpc.server.rpc.ContextListener" %>
<%@ page import="hotmath.cm.util.CmMultiLinePropertyReader" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %>

<%
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    Statement stmt = null;
    
    String errMsg = null;
    
    int overageCount = 0;
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    String today = sdf.format(new java.util.Date());

	String header =
        String.format("%-15s\t%-50s\t%-15s\t%6s\t%6s\t%7s\t%6s\t%12s\n\n", 
        		"User Name", "School Name", "Rep", "Count", "Max", "Overage", "Status", "Expires");

	StringBuilder sb = new StringBuilder();

    try {
    	conn = HMConnectionPool.getConnection();

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("ACCOUNT_OVERAGE");
    	
    	stmt = conn.createStatement();

		rs = stmt.executeQuery(sql);
		 
		while (rs.next()) {
			overageCount++;
			java.sql.Date d = rs.getDate("date_expire");
			java.util.Date expireDate = new java.util.Date(d.getTime());
			String expireDateStr = sdf.format(expireDate);

			sb.append(
            String.format("%-15s\t%-50s\t%-15s\t%6d\t%6d\t%7d\t%6s\t%12s\n", 
            		rs.getString("user_name"), rs.getString("school_name"), rs.getString("rep_name"), 
            		rs.getInt("student_count"), rs.getInt("max_students"), rs.getInt("overage"),
            	    rs.getString("status"), expireDateStr));
		 }
    }
    catch (Exception e){
    	errMsg = e.getMessage();
    }
    finally {
    	SqlUtilities.releaseResources(rs, null, conn);
    }
%>

<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%><html>
  <body>
    <h2>
      CM Account Overage Report
    </h2>
    <pre>
    Date: <%= today %>

    <%
    if (errMsg != null) {
    %>
    Error message: <%= errMsg %>
    <%
    }
    %>
    <%
    if (overageCount == 0) { %>
    No accounts to report
    <%
    } else { %>
    <%= header.toString() %>
    <%= sb.toString() %>    
    <%
    } %>
    
    </pre>
  </body>
</html>
