package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.export.ExportGradebookInExcelFormat;
import hotmath.cm.util.report.ReportUtils;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.activity.StudentActivitySummaryModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;
import hotmath.gwt.shared.client.rpc.action.ExportGradebookAction;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sb.mail.SbMailManager;

/**
 * Export Grade book Command
 * 
 * @author bob
 *
 */

public class ExportGradebookCommand implements ActionHandler<ExportGradebookAction,StringHolder>{

	private static Log LOG = LogFactory.getLog(ExportGradebookCommand.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final String NEW_LINE = System.getProperty("line.separator");

	private boolean runInSeparateThread = true;

    @Override
    public StringHolder execute(Connection conn, ExportGradebookAction action) throws Exception {

    	List<StudentModelI> studentList = new GetStudentGridPageCommand().getStudentPool(action.getPageAction());

		StringHolder sh = new StringHolder();
	    StringBuilder sb = new StringBuilder();

	    if (studentList.size() > 0) {
    	    sb.append("Grade book export for Group ").append(action.getGroupName()).append(" will be emailed to ");
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

	    ExportGradebookRunnable exportRunnable =
	    	new ExportGradebookRunnable(action.getPageAction().getAdminId(), action.getGroupId(), studentList, action.getEmailAddress(),
	    			filterDescr, action.getFromDate(),  action.getToDate());

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
        return ExportGradebookAction.class;
    }

    public void setRunInSeparateThread(boolean runInSeparateThread) {
		this.runInSeparateThread = runInSeparateThread;
	}

	public boolean isRunInSeparateThread() {
		return runInSeparateThread;
	}

	class ExportGradebookRunnable implements Runnable {

    	private Integer adminUid;
    	private Integer groupId;
    	private List<StudentModelI> studentList;
    	private String emailAddr;
    	private String filterDescr;
    	private Date fromDate;
    	private Date toDate;

    	public ExportGradebookRunnable(final Integer adminUid, Integer groupId,
    			final List<StudentModelI> studentList, final String emailAddr, final String filterDescr, Date fromDate, Date toDate) {
    		this.adminUid = adminUid;
    		this.groupId  = groupId;
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

    			AssignmentDao asgDao = AssignmentDao.getInstance();

    			List<Assignment> asgList = asgDao.getAssignments(adminUid, groupId);

    			// sort assignments by ascending due date
    			Collections.sort(asgList, new Comparator<Assignment>() {
					@Override
					public int compare(Assignment asg1, Assignment asg2) {
						Date date1 = asg1.getDueDate();
						Date date2 = asg2.getDueDate();
						if (date1 == null && date2 != null) return -1;
						if (date1 != null && date2 == null) return 1;
						if (date1 == date2) {
							return asg1.getAssignKey() - asg2.getAssignKey();
						}
						return date1.compareTo(date2);
					}
    			});
    			
    			List<Integer> uidList = new ArrayList<Integer>();
    			
    			Map<Integer, CmList<StudentAssignment>> asgMap = new HashMap<Integer, CmList<StudentAssignment>>();

    			Map<Integer, String> stuNameMap = new HashMap<Integer, String>();
    			Map<Integer, List<StudentAssignment>> stuAssignMap = new HashMap<Integer, List<StudentAssignment>>();
    			
    			for (Assignment asg : asgList) {
    				CmList<StudentAssignment> saList = asgDao.getAssignmentGradeBook(asg.getAssignKey());
    				asgMap.put(asg.getAssignKey(), saList);

    				for (StudentAssignment sa : saList) {
    					stuNameMap.put(sa.getUid(), sa.getStudentName());
    					List<StudentAssignment> sasList = stuAssignMap.get(sa.getUid());
    					if (sasList == null) {
    						sasList = new ArrayList<StudentAssignment>();
    						uidList.add(sa.getUid());
    						stuAssignMap.put(sa.getUid(), sasList);
    					}
    					sasList.add(sa);
    				}
    			}

    			HaAdmin haAdmin = CmAdminDao.getInstance().getAdmin(adminUid);

    			AccountInfoModel acctInfo = CmAdminDao.getInstance().getAccountInfo(adminUid);
    			String acctCreateDate = sdf.format(acctInfo.getAccountCreateDate());
    			String todaysDate = sdf.format(new Date());

    			StringBuilder titleBuff = new StringBuilder();
    			titleBuff.append(acctInfo.getSchoolName()).append(" (");
    			titleBuff.append(acctInfo.getAdminUserName()).append(") ");
    			titleBuff.append("Grade Book Export on ").append(todaysDate);

    			String endDate = (toDate == null) ? todaysDate : sdf.format(toDate);
    	        String beginDate = (fromDate == null) ? acctCreateDate : sdf.format(fromDate);
    			titleBuff.append(" - Period covered is ").append(beginDate).append(" to ").append(endDate);

    	        Calendar now = Calendar.getInstance();
    	        now.add(Calendar.DATE, 1);

    			ByteArrayOutputStream baos = null;

    			StringBuilder sb = new StringBuilder();
    			if (filterDescr != null && filterDescr.trim().length() > 0)
    				sb.append(" - Filter: ( ").append(filterDescr).append(" )");
    			else
    				sb.append(" - Filter: ( All Students )");

    			ExportGradebookInExcelFormat exporter = new ExportGradebookInExcelFormat(stuNameMap, stuAssignMap);
    			exporter.setAssignmentList(asgList);
    			exporter.setStudentList(uidList);
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
    			msgBuff.append("The grade book export you requested for ");
    			msgBuff.append(acctInfo.getSchoolName()).append(" (");
    			msgBuff.append(acctInfo.getAdminUserName()).append(") ");
    			if (filterDescr != null) {
    				msgBuff.append("with ").append(sb.toString());
    			}
				msgBuff.append(" is attached.");

    			String[] toEmailAddrs = new String[2];
    			toEmailAddrs[0] = emailAddr;
    			toEmailAddrs[1] = "admin@catchupmath.com";
    			
    			if(emailAddr != null) {
	    			SbMailManager.getInstance().sendFile(filePath.getPath(), "Your Catchup Math Grade Book Export File",
	    					msgBuff.toString(), toEmailAddrs, "registration@catchupmath.com");
    			}
    		}
    		catch (Exception e) {
    			LOG.error("*** Exception generating / mailing grade book export ***", e);
    		}
    		finally {
    			SqlUtilities.releaseResources(null, null, conn);
    		}
    	}
    }
}

