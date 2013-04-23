package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.CmReportCardDao;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.export.ExportStudentsInExcelFormat;
import hotmath.cm.util.report.ReportUtils;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmHighlightsDao;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao;
import hotmath.gwt.cm_admin.server.model.activity.StudentActivitySummaryModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.testset.ha.HaAdmin;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sb.mail.SbMailManager;

/**
 * Export student data Command
 * 
 * @author bob
 *
 */

public class ExportStudentsCommand implements ActionHandler<ExportStudentsAction,StringHolder>{

	private static Log LOG = LogFactory.getLog(ExportStudentsCommand.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final String NEW_LINE = System.getProperty("line.separator");

	private boolean runInSeparateThread = true;

    @Override
    public StringHolder execute(Connection conn, ExportStudentsAction action) throws Exception {

    	List<StudentModelI> studentList = new GetStudentGridPageCommand().getStudentPool(action.getPageAction());

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
	    	new ExportStudentDataRunnable(action.getPageAction().getAdminId(), studentList, action.getEmailAddress(), filterDescr,
	    			action.getFromDate(), action.getToDate());

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
    	private List<StudentModelI> studentList;
    	private String emailAddr;
    	private String filterDescr;
    	private Date fromDate;
    	private Date toDate;

    	public ExportStudentDataRunnable(final Integer adminUid, final List<StudentModelI> studentList,
    			final String emailAddr, final String filterDescr, Date fromDate, Date toDate) {
    		this.adminUid = adminUid;
    		this.studentList = studentList;
    		this.emailAddr = emailAddr;
    		this.filterDescr = filterDescr;
    		this.fromDate = fromDate;
    	    this.toDate = toDate;
    	}

    	public void run() {
    		List<StudentReportCardModelI> rcList = new ArrayList<StudentReportCardModelI>();
    		Map<Integer,List<StudentActivitySummaryModel>> sasMap = new HashMap<Integer,List<StudentActivitySummaryModel>> ();
    		Connection conn = null;
    		try {
    			conn = HMConnectionPool.getConnection();

    			CmReportCardDao rcDao = CmReportCardDao.getInstance();
    			List<String> uidList = new ArrayList<String> ();

    			for (StudentModelI sm : studentList) {
    				StudentReportCardModelI rc = rcDao.getStudentReportCard(sm.getUid(), fromDate, toDate);
    				rc.setStudentUid(sm.getUid());
    				rcList.add(rc);
    				uidList.add(String.valueOf(sm.getUid()));
    			}

    			StudentActivityDao saDao = StudentActivityDao.getInstance();
    			for (StudentModelI sm : studentList) {
    				List<StudentActivitySummaryModel> list = saDao.getStudentActivitySummary(sm.getUid(), fromDate, toDate);
    				if (list != null && list.size() > 0) sasMap.put(sm.getUid(), list);
    			}

    			HaAdmin haAdmin = CmAdminDao.getInstance().getAdmin(adminUid);

    			AccountInfoModel acctInfo = CmAdminDao.getInstance().getAccountInfo(adminUid);
    			String acctCreateDate = sdf.format(acctInfo.getAccountCreateDate());
    			String todaysDate = sdf.format(new Date());

    			StringBuilder titleBuff = new StringBuilder();
    			titleBuff.append(acctInfo.getSchoolName()).append(" (");
    			titleBuff.append(acctInfo.getAdminUserName()).append(") ");
    			titleBuff.append("Student Data Export on ").append(todaysDate);

    			String endDate = (toDate == null) ? todaysDate : sdf.format(toDate);
    	        String beginDate = (fromDate == null) ? acctCreateDate : sdf.format(fromDate);
    			titleBuff.append(" - Period covered is ").append(beginDate).append(" to ").append(endDate);

    	        Calendar now = Calendar.getInstance();
    	        now.add(Calendar.DATE, 1);

    	    	Map<Integer, Integer> totMap = CmHighlightsDao.getInstance().getTimeOnTaskMapForUids(conn, uidList, fromDate, toDate);

    			ByteArrayOutputStream baos = null;

    			StringBuilder sb = new StringBuilder();
    			if (filterDescr != null && filterDescr.trim().length() > 0)
    				sb.append(" - Filter: ( ").append(filterDescr).append(" )");
    			else
    				sb.append(" - Filter: ( All Students )");

    			ExportStudentsInExcelFormat exporter = new ExportStudentsInExcelFormat(studentList);
    			exporter.setReportCardList(rcList);
    			exporter.setStudentActivitySummaryMap(sasMap);
    			exporter.setTimeOnTaskMap(totMap);
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
				msgBuff.append(NEW_LINE).append(NEW_LINE);

				msgBuff.append("Note that the spreadsheet has two sheets, one for Summary and one for Details.");				

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

