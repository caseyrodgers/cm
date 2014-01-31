<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="hotmath.gwt.cm_rpc.client.model.WebLinkModel.LinkViewer"%>
<%@page import="hotmath.cm.dao.WebLinkDao"%>
<%@page import="hotmath.gwt.cm_rpc.client.model.WebLinkModel"%>
<%@page import="hotmath.assessment.Range"%>
<%@page import="hotmath.inmh.INeedMoreHelpResourceType"%>
<%@page import="hotmath.inmh.INeedMoreHelpItem"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.InmhItemData"%>
<%@page import="hotmath.testset.ha.HaTestRunDao"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData"%>
<%@page import="hotmath.assessment.AssessmentPrescription.SessionData"%>
<%@page import="hotmath.assessment.AssessmentPrescriptionSession"%>
<%@page import="java.util.List"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.PrescriptionData"%>
<%@page import="hotmath.cm.program.CmProgramFlow"%>
<%@page import="hotmath.gwt.shared.server.service.command.GetPrescriptionCommand"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="java.sql.Connection"%>
<%@page import="hotmath.gwt.cm_admin.server.model.CmStudentDao"%>
<%@page import="hotmath.assessment.AssessmentPrescription"%>
<%@page import="hotmath.assessment.AssessmentPrescriptionManager"%>
<%@page
	import="hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse"%>
<%@page import="hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction"%>
<%@page
	import="hotmath.solution.writer.SolutionHTMLCreatorIimplVelocity"%>
<%@page import="org.apache.log4j.Logger"%>
<html>
<!-- InstanceBegin template="/Templates/hm_core_layout.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<%
    Logger logger = Logger.getRootLogger();
    PrescriptionSessionResponse result = null;
    Connection conn=null;
    try {
        
        logger.error("Starting test 1");
        WebLinkDao.getInstance().getAllWebLinksDefinedForAdminPrivate(12008, true);
        logger.error("End test 1");
        WebLinkDao.getInstance().getWebLinksFor(813107, 12008, 171365, "Simple Interest");
        logger.error("END TEST2");
        
        //result = GetPrescriptionCommand.createPrescriptionResponse(conn, prescription,1);
    } catch (Exception e) {
        logger.error("Error testing!", e);
    }
    finally {
        SqlUtilities.releaseResources(null,null,conn);
    }
    logger.info("Done!");
%>

<h1>
	OK 8:
	<%=result%>
</h1>