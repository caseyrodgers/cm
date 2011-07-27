package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.CmReportCardDao;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.export.ExportStudentsInExcelFormat;
import hotmath.cm.util.report.ReportUtils;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.testset.ha.HaAdmin;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sb.mail.SbMailManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Export student data Command
 * 
 * @author bob
 *
 */

public class ExportStudentsCommand implements ActionHandler<ExportStudentsAction,StringHolder>{

	private static Log LOG = LogFactory.getLog(ExportStudentsCommand.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean runInSeparateThread = true;

    @Override
    public StringHolder execute(Connection conn, ExportStudentsAction action) throws Exception {

    	List<StudentModelExt> studentList = new GetStudentGridPageCommand().getStudentPool(conn, action.getPageAction());

		StringHolder sh = new StringHolder();
	    StringBuilder sb = new StringBuilder();
	    
	    if (studentList.size() > 0) {
    	    sb.append("Student data export for ").append(studentList.size()).append(" students will be emailed to ");
	        sb.append(action.getEmailAddress());
	    }
	    else {
	    	sb.append("Sorry, nothing to Export.");
	    }
	    sh.setResponse(sb.toString());

	    // if nothing to Export, return
	    if (studentList.size() < 1) return sh;
	    
	    String filterDescr = null;
	    if (action.getFilterMap() != null) {
	    	filterDescr =
	    		ReportUtils.getFilterDescription(conn, action.getPageAction().getAdminId(), CmAdminDao.getInstance(), action.getFilterMap());
	    }

	    ExportStudentDataRunnable exportRunnable =
	    	new ExportStudentDataRunnable(action.getPageAction().getAdminId(), studentList, action.getEmailAddress(), filterDescr);

	    if (runInSeparateThread) {
            Thread t = new Thread(exportRunnable);
            t.start();
	    }
	    else {
	    	exportRunnable.run();
	    }

	    return sh;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ExportStudentsAction.class;
    }

    public void setRunInSeparateThread(boolean runInSeparateThread) {
		this.runInSeparateThread = runInSeparateThread;
	}

	public boolean isRunInSeparateThread() {
		return runInSeparateThread;
	}

	class ExportStudentDataRunnable implements Runnable {

    	private Integer adminUid;
    	private List<StudentModelExt> studentList;
    	private String emailAddr;
    	private String filterDescr;

    	public ExportStudentDataRunnable(final Integer adminUid, final List<StudentModelExt> studentList,
    			final String emailAddr, final String filterDescr) {
    		this.adminUid = adminUid;
    		this.studentList = studentList;
    		this.emailAddr = emailAddr;
    		this.filterDescr = filterDescr;
    	}

    	public void run() {
    		List<StudentReportCardModelI> rcList = new ArrayList<StudentReportCardModelI>();
    		Connection conn = null;
    		try {
    			conn = HMConnectionPool.getConnection();

    			CmReportCardDao rcDao = CmReportCardDao.getInstance();

    			for (StudentModelExt sm : studentList) {
    				StudentReportCardModelI rc = rcDao.getStudentReportCard(conn, sm.getUid(), null, null);
    				rcList.add(rc);
    			}

    			HaAdmin haAdmin = CmAdminDao.getInstance().getAdmin(adminUid);

    			AccountInfoModel acctInfo = CmAdminDao.getInstance().getAccountInfo(adminUid);
    			String todaysDate = sdf.format(new Date());

    			StringBuilder titleBuff = new StringBuilder();
    			titleBuff.append(acctInfo.getSchoolName()).append(" (");
    			titleBuff.append(acctInfo.getAdminUserName()).append(") ");
    			titleBuff.append("Student Data Export on ").append(todaysDate);    	

    			ByteArrayOutputStream baos = null;

    			StringBuilder sb = new StringBuilder();
    			if (filterDescr != null)
    				sb.append("Filter: ( ").append(filterDescr).append(" )");
    			else
    				sb.append("Filter: ( All Students )");

    			ExportStudentsInExcelFormat exporter = new ExportStudentsInExcelFormat(studentList);
    			exporter.setReportCardList(rcList);
    			exporter.setTitle(titleBuff.toString());
    			exporter.setFilterDescr(sb.toString());

    			baos = exporter.export();

    			// write to temporary file to be cleaned up later
    			String outputBase = CmWebResourceManager.getInstance().getFileBase();

    			// if outputBase/adminId directory doesn't exist, create it
    			String unique = Long.toString(System.currentTimeMillis());
    			outputBase = outputBase + "/" + adminUid;
    			String outputDir = FileUtil.ensureOutputDir(outputBase, unique);

    			File filePath = new File(outputDir, "CM-" + haAdmin.getUserName().toUpperCase() + "-" + todaysDate + ".xls");
    			LOG.info("Writing XLS output: " + filePath);
    			FileOutputStream fos = null;

    			fos = new FileOutputStream(filePath);
    			baos.writeTo(fos);

    			StringBuilder msgBuff = new StringBuilder();
    			msgBuff.append("The student data export you requested for ");
    			msgBuff.append(acctInfo.getSchoolName()).append(" (");
    			msgBuff.append(acctInfo.getAdminUserName()).append(") ");
    			if (filterDescr != null) {
    				msgBuff.append("with ").append(sb.toString());
    			}
				msgBuff.append(" is attached.");

    			String[] toEmailAddrs = new String[2];
    			toEmailAddrs[0] = emailAddr;
    			toEmailAddrs[1] = "admin@hotmath.com";
    			
    			if(emailAddr != null) {
	    			SbMailManager.getInstance().sendFile(filePath.getPath(), "Your Catchup Math Export File",
	    					msgBuff.toString(), toEmailAddrs, "registration@hotmath.com");
    			}
    		}
    		catch (Exception e) {
    			LOG.error("*** Exception generating / mailing student data export ***", e);
    		}
    		finally {
    			SqlUtilities.releaseResources(null, null, conn);
    		}
    	}
    }
}

