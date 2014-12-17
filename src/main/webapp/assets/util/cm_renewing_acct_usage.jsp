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
        String.format("%-10s\t%-11s\t%-11s\t%-8s\t%-8s\t%-8s\t%-10s\t%-9s\n\n", 
        		"Subscriber ID", "Create Date", "Expire Date", "Prof RPP", "Chap RPP", "Exit RPP", "Custom RPP", "Total RPP");

	StringBuilder sb = new StringBuilder();
	int profTotal = 0;
	int chapTotal = 0;
	int exitTotal = 0;
	int cstmTotal = 0;

    try {
    	conn = HMConnectionPool.getConnection();

    	String sql = "select a.subscriber_id, ss.date_created, ss.date_expire, ifnull(p.rpp_count, 0) as prof_rpp_count, ifnull(c.rpp_count,0) as chap_rpp_count, " +
    	             "ifnull(e.rpp_count,0) as exit_rpp_count, ifnull(u.rpp_count,0) as cstm_rpp_count " +
    			     "from v_cm_renewed_admin a " +
    			     "join SUBSCRIBERS_SERVICES ss on a.subscriber_id = ss.subscriber_id " +
    			     "left outer join ( " +
    			     "    select a.admin_id, 'PROF' as program, count(*) as rpp_count " +
    			     "    from v_cm_renewed_admin a " +
    			     "    join HA_USER us on us.admin_id = a.admin_id " +
    			     "    join HA_TEST_DEF d on prog_id = 'PROF' " +
    			     "    join HA_TEST t on t.user_id = us.uid and t.test_def_id = d.test_def_id " +
    			     "    join HA_TEST_RUN r on r.test_id = t.test_id " +
    			     "    join HA_TEST_RUN_INMH_USE u on u.run_id = r.run_id and u.item_type='practice' " +
    			     "    group by us.admin_id " +
    			     ") p on p.admin_id = a.admin_id " +
    			     "left outer join ( " +
    			     "    select us.admin_id, 'CHAP' as program, count(*) as rpp_count " +
    			     "    from HA_TEST_DEF d " +
    			     "    join HA_TEST t on t.test_def_id = d.test_def_id " +
    			     "    join HA_TEST_RUN r on r.test_id = t.test_id " +
    			     "    join HA_TEST_RUN_INMH_USE u on u.run_id = r.run_id and u.item_type='practice' " +
    			     "    join v_cm_renewed_admin a " +
    			     "    join HA_USER us on us.admin_id = a.admin_id and us.uid = t.user_id " +
    			     "    where d.prog_id = 'CHAP' " +
    			     "    group by us.admin_id " +
    			     ") c on c.admin_id = a.admin_id " +
    	    		 "left outer join ( " +
    	    		 "    select us.admin_id, 'EXIT' as program, count(*) as rpp_count " +
    	    		 "    from HA_TEST_DEF d " +
    	    		 "    join HA_TEST t on t.test_def_id = d.test_def_id " +
    	    		 "    join HA_TEST_RUN r on r.test_id = t.test_id " +
    	    		 "    join HA_TEST_RUN_INMH_USE u on u.run_id = r.run_id and u.item_type='practice' " +
    	    		 "    join v_cm_renewed_admin a " +
    	    	     "    join HA_USER us on us.admin_id = a.admin_id and us.uid = t.user_id " +
    	    		 "    where d.prog_id like '%GRAD PREP%' " +
    	    		 "    group by us.admin_id " +
    	    		 ") e on e.admin_id = a.admin_id " +
    	    	     "left outer join ( " +
    	    	     "    select us.admin_id, 'CSTM' as program, count(*) as rpp_count " +
    	    	     "    from HA_TEST_DEF d " +
    	    	     "    join HA_TEST t on t.test_def_id = d.test_def_id " +
    	    	     "    join HA_TEST_RUN r on r.test_id = t.test_id " +
    	    	     "    join HA_TEST_RUN_INMH_USE u on u.run_id = r.run_id and u.item_type='practice' " +
    	    	     "    join v_cm_renewed_admin a " +
    	    	     "    join HA_USER us on us.admin_id = a.admin_id and us.uid = t.user_id " +
    	    	     "    where d.prog_id = 'CUSTOM' " +
    	    	     "    group by us.admin_id " +
    	    	     ") u on u.admin_id = a.admin_id " +
    			     "where ss.service_name = 'catchup'" +
    	    		 " order by a.subscriber_id";
    	
    	stmt = conn.createStatement();

		rs = stmt.executeQuery(sql);
		 
		while (rs.next()) {
			String subscriberId = rs.getString("subscriber_id");
			java.sql.Date d = rs.getDate("date_created");
			java.util.Date date = new java.util.Date(d.getTime());
			String createDateStr = sdf.format(date);
			d = rs.getDate("date_expire");
			date = new java.util.Date(d.getTime());
			String expireDateStr = sdf.format(date);
			int prof = rs.getInt("prof_rpp_count");
			int chap = rs.getInt("chap_rpp_count");
			int exit = rs.getInt("exit_rpp_count");
			int cstm = rs.getInt("cstm_rpp_count");
			profTotal += prof;
			chapTotal += chap;
			exitTotal += exit;
			cstmTotal += cstm;

			sb.append(
            String.format("%-10s\t%-11s\t%-11s\t%8d\t%8d\t%8d\t%10d\t%9d\n", 
            		subscriberId, createDateStr, expireDateStr, prof, chap, exit, cstm,
            		prof+chap+exit+cstm ));
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
      CM Renewing Account Usage
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
 <% }
    sb = new StringBuilder();
    sb.append(String.format("\n\t\t\t\tTotals:\t\t%8d\t%8d\t%8d\t%10d\t%9d\n", 
    		profTotal, chapTotal, exitTotal, cstmTotal, profTotal+chapTotal+exitTotal+cstmTotal));
 %>
 <%=sb.toString() %>
    </pre>
  </body>
</html>
