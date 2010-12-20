<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.apache.log4j.Logger"%>
<%@page import="hotmath.testset.ha.HaUserFactory"%>
<%@page import="hotmath.gwt.cm_admin.server.model.CmStudentDao"%>
<%@page import="hotmath.gwt.cm_tools.client.model.StudentActiveInfo"%>
<%@page import="hotmath.gwt.cm_tools.client.model.StudentModelI"%>
<%@page import="hotmath.gwt.cm_tools.client.data.HaBasicUser"%>
<%@page import="hotmath.cm.util.CmCacheManager"%>
<%@page import="hotmath.assessment.*"%>
<html><!-- InstanceBegin template="/Templates/hm_core_layout.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.util.*, java.sql.*,hotmath.util.sql.*" %>
<%
Connection conn=null;
Logger logger = Logger.getRootLogger();
try {
	
	conn = HMConnectionPool.getConnection();
	for(int i=20;i>0;i--) {
		HaBasicUser u = HaUserFactory.createDemoUser();
		
		int uid = u.getUserKey();
		
		StudentModelI student = new CmStudentDao().getStudentModel(uid);
		StudentActiveInfo sai = new CmStudentDao().loadActiveInfo(conn, uid);
		logger.info("Junk Info: " + student.getUid() + ", " + sai.getActiveSegment());
	}
}
finally {
	SqlUtilities.releaseResources(null,null,conn);
}
	
	logger.info("Done!");
%>