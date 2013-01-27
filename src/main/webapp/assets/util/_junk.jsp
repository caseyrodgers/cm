<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.solution.writer.SolutionHTMLCreatorIimplVelocity"%>
<%@page import="org.apache.log4j.Logger"%>
<html><!-- InstanceBegin template="/Templates/hm_core_layout.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<%
Logger logger = Logger.getRootLogger();
try {
    
    new SolutionHTMLCreatorIimplVelocity("TEst", "TEST");
	
}
catch(Exception e) {
	logger.error("Error testing!", e);
}
	
	logger.info("Done!");
%>