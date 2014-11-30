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
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    String today = sdf.format(new java.util.Date());

	String header =
        String.format("%-10s\t%-30s\t%-20s\t%8s\t%-8s\t%12s\n\n", 
        		"User Name", "School Name", "Teacher Name", "CP Count", "Status", "Expires");

	StringBuilder sb = new StringBuilder();

    try {
    	conn = HMConnectionPool.getConnection();

    	String sql = "select a.user_name, ss.school_type, t.teacher_name, count(*) as cp_count, sss.date_expire, " +
			         "if(sss.date_expire > curdate(), 'Active', 'Inactive') as status " +
    	             "from CM_CUSTOM_PROBLEM_TEACHER t " + 
    			     "join CM_CUSTOM_PROBLEM p on p.teacher_id = t.teacher_id " +
    	             "join HA_ADMIN a on a.aid = t.admin_id " +
    	    	     "join SUBSCRIBERS ss on ss.id = a.subscriber_id " +
    	    		 "join SUBSCRIBERS_SERVICES sss on sss.subscriber_id = ss.id and sss.service_name = 'catchup' " +
    			     "group by t.teacher_id " +
    	             "order by status asc, cp_count desc, a.user_name asc, teacher_name asc";
    	
    	stmt = conn.createStatement();

		rs = stmt.executeQuery(sql);
		 
		while (rs.next()) {
			java.sql.Date d = rs.getDate("date_expire");
			java.util.Date expireDate = new java.util.Date(d.getTime());
			String expireDateStr = sdf.format(expireDate);

			sb.append(
            String.format("%-10s\t%-30s\t%-20s\t%8d\t%-8s\t%12s\n", 
            		rs.getString("user_name"), rs.getString("school_type"), rs.getString("teacher_name"), rs.getInt("cp_count"), rs.getString("status"), expireDateStr));
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
      CM Custom Problem Count
    </h2>
    <pre>
 Date: <%= today %>

    <%
    if (errMsg != null) {
    %>
 Error message: <%= errMsg %>
    <%
    }
    else { %>
 <%=header.toString() %>
 <%=sb.toString() %>
 <% } %>
    </pre>
  </body>
</html>
