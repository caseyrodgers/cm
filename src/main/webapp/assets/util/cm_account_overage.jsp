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
        String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\n\n", 
        		"ID", "Subscriber ID", "Count", "Max", "Overage", "Status", "Expires");

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
            String.format("%d\t%s\t%d\t%d\t%d\t%s\t%s\n", 
            		rs.getInt("aid"), rs.getString("subscriber_id"), rs.getInt("student_count"),
            		rs.getInt("max_students"), rs.getInt("overage"), rs.getString("status"),
					expireDateStr));
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
