<%@ page import="hotmath.util.*" %>
<%@ page import="hotmath.util.sql.SqlUtilities" %>
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
        String.format("%-15s\t%8s\t%12s\n\n", 
        		"User Name", "CQ Count", "Expires");

	StringBuilder sb = new StringBuilder();

    try {
    	conn = HMConnectionPool.getConnection();

    	String sql = "select a.user_name, s.cq_count, sss.date_expire from " +
    	             "(select admin_id, count(*) cq_count from HA_CUSTOM_QUIZ  group by admin_id )s " +
    			     "join HA_ADMIN a on a.aid = s.admin_id " +
    	             "join SUBSCRIBERS ss on ss.id = a.subscriber_id " +
    			     "join SUBSCRIBERS_SERVICES sss on sss.subscriber_id = ss.id and sss.service_name = 'catchup' " +
    	             "order by cq_count desc, user_name asc";
    	//String sql = CmMultiLinePropertyReader.getInstance().getProperty("ACCOUNT_OVERAGE");
    	
    	stmt = conn.createStatement();

		rs = stmt.executeQuery(sql);
		 
		while (rs.next()) {
			overageCount++;
			java.sql.Date d = rs.getDate("date_expire");
			java.util.Date expireDate = new java.util.Date(d.getTime());
			String expireDateStr = sdf.format(expireDate);

			sb.append(
            String.format("%-15s\t%8d\t%12s\n", 
            		rs.getString("user_name"), rs.getInt("cq_count"), expireDateStr));
		 }
    }
    catch (Exception e){
    	errMsg = e.getMessage();
    }
    finally {
    	SqlUtilities.releaseResources(rs, null, conn);
    }
%>

<html>
  <body>
    <h2>
      CM Custom Quiz Count
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
<%=header.toString() %>
<%=sb.toString() %>    
    <%
    } %>
    
    </pre>
  </body>
</html>
