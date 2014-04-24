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
	Logger logger = Logger.getRootLogger();
	Connection conn = null;
	try {
		logger.error("Staring pilot update");
	      
        conn = HMConnectionPool.getConnection();

		String sql = "select h.aid from HA_ADMIN h JOIN SUBSCRIBERS s on h.subscriber_id = s.id where is_college = 1 ";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while(rs.next()) {
			int aid = rs.getInt("aid");
			logger.info("AID: " + aid);
			
			CmPilotCreate.setupPilotGroups(conn, aid, true);
		}
	} catch (Exception e) {
		logger.error("Error testing!", e);
	} finally {
		SqlUtilities.releaseResources(null, null, conn);
	}
	logger.info("Done!");
%>

<h1>
	DONE
</h1>