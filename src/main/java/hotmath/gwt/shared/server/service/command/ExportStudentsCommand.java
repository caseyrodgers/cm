package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.cm.server.model.CmReportCardDao;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.export.ExportStudentsInExcelFormat;
import hotmath.cm.util.report.ReportUtils;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmHighlightsDao;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao;
import hotmath.gwt.cm_admin.server.model.activity.StudentActivitySummaryModel;
import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;
import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.testset.ha.HaAdmin;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
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

	// 7 Mb + 33% (overhead) => 9.3 Mb
	private long MAX_ATTACHMENT_SIZE = 7000000L;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final String ADMIN_EMAIL = "admin@catchupmath.com";
	private static final String DEFAULT_REP_EMAIL = "thamilton@catchupmath.com";

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
	    			action.getLevelName(), action.getFromDate(), action.getToDate());

	    if (runInSeparateThread == true) {
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
    	private String[] toEmailAddrs;
    	private String filterDescr;
    	private String levelName;
    	private Date fromDate;
    	private Date toDate;

    	private static final String THREE_SHEET_MSG = "Note that the spreadsheet has three sheets, one for Summary, one for Details, and one for Standards.";

    	public ExportStudentDataRunnable(final Integer adminUid, final List<StudentModelI> studentList,
    			final String emailAddr, final String filterDescr, String levelName, Date fromDate, Date toDate) {
    		this.adminUid = adminUid;
    		this.studentList = studentList;
    		this.emailAddr = emailAddr;
    		this.filterDescr = filterDescr;
    		this.levelName = levelName;
    		this.fromDate = fromDate;
    	    this.toDate = toDate;
			this.toEmailAddrs = new String[1];
			toEmailAddrs[0] = emailAddr;

    	}

    	public void run() {
    		List<StudentReportCardModelI> rcList = new ArrayList<StudentReportCardModelI>();
    		Map<Integer,List<StudentActivitySummaryModel>> sasMap = new HashMap<Integer,List<StudentActivitySummaryModel>> ();
    		Map<Integer,List<CCSSCoverageData>> ccssMap = new HashMap<Integer,List<CCSSCoverageData>>();
    		Map<Integer,List<String>> ccssNotCoveredMap = new HashMap<Integer,List<String>>();
    		Connection conn = null;
			AccountInfoModel acctInfo = null;
		    StringBuilder subjBuff = new StringBuilder();
    		try {
    			conn = HMConnectionPool.getConnection();

    			CmReportCardDao rcDao = CmReportCardDao.getInstance();
    			List<String> uidList = new ArrayList<String> ();
    			List<Integer> userList = new ArrayList<Integer>();

    			for (StudentModelI sm : studentList) {
    				StudentReportCardModelI rc = rcDao.getStudentReportCard(sm.getUid(), fromDate, toDate);
    				rc.setStudentUid(sm.getUid());
    				rcList.add(rc);
    				uidList.add(String.valueOf(sm.getUid()));
    				userList.add(sm.getUid());
    			}

    			StudentActivityDao saDao = StudentActivityDao.getInstance();
    			for (StudentModelI sm : studentList) {
    				List<StudentActivitySummaryModel> list = saDao.getStudentActivitySummary(sm.getUid(), fromDate, toDate);
    				if (list != null && list.size() > 0) sasMap.put(sm.getUid(), list);
    			}

    			CCSSReportDao ccssDao = CCSSReportDao.getInstance();
    			List<CCSSCoverageData> ccssList = null;
    			List<String> levelList = null;
    			ccssList = ccssDao.getStandardNamesForStudent(userList, fromDate, toDate);
    			levelList = ccssDao.getStandardNamesForLevel(levelName, true);
        	    buildStandardsMap(ccssMap, ccssNotCoveredMap, ccssList, levelList);
        	    if (levelName != null) {
        			fillNotCoveredMap(userList, ccssNotCoveredMap, levelList);
    			}
        	    Map<Integer,List<String>> topicMap = extractTopics(ccssMap);
        	    Map<Integer,List<String>> stdMap = extractStandards(ccssMap);

    			HaAdmin haAdmin = CmAdminDao.getInstance().getAdmin(adminUid);

    			acctInfo = CmAdminDao.getInstance().getAccountInfo(adminUid);
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

    	    	Map<Integer, Integer> activeTimeMap = CmHighlightsDao.getInstance().getActiveTimeMapForUids(conn, uidList, fromDate, toDate);

    			ByteArrayOutputStream baos = null;

    			StringBuilder sb = new StringBuilder();
    			if (filterDescr != null && filterDescr.trim().length() > 0)
    				sb.append(" - Filter: ( ").append(filterDescr).append(" )");
    			else
    				sb.append(" - Filter: ( All Students )");
    			if (levelName != null) {
    				sb.append(" - Strand: ( ").append(levelName).append(" )");
    			}

    			ExportStudentsInExcelFormat exporter = new ExportStudentsInExcelFormat(studentList);
    			exporter.setReportCardList(rcList);
    			exporter.setStudentActivitySummaryMap(sasMap);
    			exporter.setTotalTimeMap(activeTimeMap);
    			exporter.setTopicsMap(topicMap);
    			exporter.setStandardsMap(stdMap);
    			exporter.setStandardsNotCoveredMap(ccssNotCoveredMap);
    			exporter.setTitle(titleBuff.toString());
    			exporter.setFilterDescr(sb.toString());
    			exporter.setCcssLevelName(levelName);

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
    			long fileSize = filePath.length();

				StringBuilder msgBuff = new StringBuilder();
    			if (fileSize <= MAX_ATTACHMENT_SIZE) {
    				msgBuff.append("The student data export you requested for ");
    				msgBuff.append(acctInfo.getSchoolName()).append(" (");
    				msgBuff.append(acctInfo.getAdminUserName()).append(") ");
    				if (filterDescr != null || levelName != null) {
    					msgBuff.append("with ").append(sb.toString());
    				}
    				msgBuff.append(" is attached.");
    				msgBuff.append(NEW_LINE).append(NEW_LINE);
    				msgBuff.append(THREE_SHEET_MSG);
    				msgBuff.append(NEW_LINE).append(NEW_LINE);
    				msgBuff.append("The CatchupMath Team");

    				subjBuff.append("CatchupMath Export");
    				if (filterDescr != null || levelName != null) {
    					subjBuff.append(sb.toString());
    				}
    				if(emailAddr != null) {
    					SbMailManager.getInstance().sendFile(filePath.getPath(), subjBuff.toString(),
    							msgBuff.toString(), toEmailAddrs, "registration@catchupmath.com");
    				}
    			}
    			else {
    				// send file too large message
    				msgBuff.append("The student data export you requested for ");
    				msgBuff.append(acctInfo.getSchoolName()).append(" (");
    				msgBuff.append(acctInfo.getAdminUserName()).append(") ");
    				if (filterDescr != null || levelName != null) {
    					msgBuff.append("with ").append(sb.toString());
    				}
    				msgBuff.append(" is too large to send as an email attachment.  Please try to reduce it ");
    				long factor = fileSize/MAX_ATTACHMENT_SIZE;
    				if (factor > 0L) {
    					msgBuff.append(" to 1/").append(factor);
    				}
    				else {
    					factor = (100*(fileSize - MAX_ATTACHMENT_SIZE)) / MAX_ATTACHMENT_SIZE;
    					msgBuff.append(" by ").append(factor).append("% of");
    				}
    				msgBuff.append(" its current size by selecting a shorter date range or fewer students.");
    				msgBuff.append(NEW_LINE).append(NEW_LINE);
    				msgBuff.append("The CatchupMath Team");

    				subjBuff.append("CatchupMath Export Too Large");
    				if (filterDescr != null || levelName != null) {
    					subjBuff.append(sb.toString());
    				}
    				if(emailAddr != null) {
    					SbMailManager.getInstance().sendMessage(subjBuff.toString(), msgBuff.toString(), toEmailAddrs, "support@catchupmath.com");
    				}
    			}
    		}
    		catch (Exception e) {
    			LOG.error("*** Exception generating / mailing student data export for admin UID: " + adminUid, e);
    			if(emailAddr != null) {
    				try {
	    			    SbMailManager.getInstance().sendMessage(subjBuff.append(" ERROR").toString(),
	    					"Sorry, there was a problem generating your export file.  We will identify and fix the problem as soon as possible." +
	    			        "\n\n The CatchupMath Team",
	    					toEmailAddrs, "registration@catchupmath.com");
    				}
    				catch (Exception sbe) {
    	    			LOG.error("*** Exception mailing student data export error email ***", sbe);    					
    				}
    				try {
    					toEmailAddrs = new String[3];
    	    			if (acctInfo != null && acctInfo.getAccountRepEmail() != null) {
    	    				toEmailAddrs[0] = acctInfo.getAccountRepEmail();;    				
    	    			}
    	    			else {
    	    				toEmailAddrs[0] = DEFAULT_REP_EMAIL;
    	    			}
    					toEmailAddrs[1] = ADMIN_EMAIL;
    					toEmailAddrs[2] = "bobhall@catchupmath.com";
	    			    SbMailManager.getInstance().sendMessage(subjBuff.append(" ERROR").toString(),
	    					"There was a problem generating an export file for Admin UID: " + adminUid,
	    					toEmailAddrs, "errors@catchupmath.com");
    				}
    				catch (Exception sbe) {
    	    			LOG.error("*** Exception mailing internal student data export error email ***", sbe);    					
    				}
    				StringWriter sw = new StringWriter();
    				PrintWriter pw = new PrintWriter(sw, true);
    				e.printStackTrace(pw);
    				pw.flush();
    				sw.flush();
    				
    				String message = String.format("There was a problem generating an export file for Admin UID: %d \n\n%s",
    						adminUid, sw.toString());
    				try {
    					SbMailManager.getInstance().sendMessage("CM Export File ERROR details", message,
    							"errors@catchupmath.com", "errors@catchupmath.com",
    							"text/plain");
    				} catch (Exception ex) {
    					LOG.error("Could not send error notification email", ex);
    				}

    			}
    		}
    		finally {
    			SqlUtilities.releaseResources(null, null, conn);
    		}
    	}

		private Map<Integer, List<String>> extractStandards(
				Map<Integer, List<CCSSCoverageData>> ccssMap) {
			Map<Integer, List<String>> stdMap = new HashMap<Integer, List<String>>();

			Set<Integer> keys = ccssMap.keySet();
			Iterator<Integer> iter = keys.iterator();
			while (iter.hasNext()) {
				Integer uid = iter.next();
				List<String> list = new ArrayList<String>();
				for (CCSSCoverageData data : ccssMap.get(uid)) {
					list.add(data.getName());
				}
				sortStandards(list);
				stdMap.put(uid, list);
			}
			return stdMap;
		}

		private Map<Integer, List<String>> extractTopics(
				Map<Integer, List<CCSSCoverageData>> ccssMap) {
			Map<Integer, List<String>> topicMap = new HashMap<Integer, List<String>>();

			Set<Integer> keys = ccssMap.keySet();
			Iterator<Integer> iter = keys.iterator();
			while (iter.hasNext()) {
				Integer uid = iter.next();
				List<String> list = new ArrayList<String>();
				topicMap.put(uid, list);
				for (CCSSCoverageData data : ccssMap.get(uid)) {
					list.add(data.getLessonName());
				}
			}
			return topicMap;
		}

		private void fillNotCoveredMap(List<Integer> userList,
				Map<Integer, List<String>> ccssNotCoveredMap,
				List<String> levelList) {
			for (Integer uid : userList) {
				if (ccssNotCoveredMap.containsKey(uid) == false) {
					ccssNotCoveredMap.put(uid, levelList);
				}
			}
		}

    }

	private void buildStandardsMap(
			Map<Integer, List<CCSSCoverageData>> ccssMap, Map<Integer, List<String>> ccssNotCoveredMap,
			List<CCSSCoverageData> ccssList, List<String> levelList) {
		int userId = 0;
		List<CCSSCoverageData> list = null;
		for (CCSSCoverageData data : ccssList) {
			if (userId != data.getUserId()) {
				list = new ArrayList<CCSSCoverageData>();
				userId = data.getUserId();
				ccssMap.put(userId, list);
			}
			list.add(data);
		}
		Set<Integer> keys = ccssMap.keySet();
		Iterator<Integer> iter = keys.iterator();
		while (iter.hasNext()) {
			Integer uid = iter.next();
			List<CCSSCoverageData> cList = consolidateStandards(ccssMap.get(uid));
			ccssMap.put(uid, cList);
			List<String> ncList = getStandardsNotCovered(cList, levelList);
			ccssNotCoveredMap.put(uid,  ncList);
		}

		
	}

	private List<String> getStandardsNotCovered(List<CCSSCoverageData> cList,
			List<String> levelList) {
		List<String> retList = new ArrayList<String>();
		Set<String> coveredSet = getCoveredSet(cList);

		for (String std : levelList) {
			if (coveredSet.contains(std) == false) {
				retList.add(std);
			}
		}
		return retList;
	}

	private Set<String> getCoveredSet(List<CCSSCoverageData> cList) {
		Set<String> set = new HashSet<String>();
		if (cList == null) return set;
		for (CCSSCoverageData data : cList) {
			String name = data.getName();
			if (name == null) continue;
			int offset = name.indexOf(" - ");
			if (offset < 0) continue;
			set.add(name.substring(0, offset));
		}
		return set;
	}

	private List<CCSSCoverageData> consolidateStandards(
			List<CCSSCoverageData> list) {
		
		List<CCSSCoverageData> lessonList = new ArrayList<CCSSCoverageData>();
		List<CCSSCoverageData> standardList = new ArrayList<CCSSCoverageData>();
		Map<String, StringBuilder> lessonMap = new HashMap<String,StringBuilder>();
		Map<String, StringBuilder> standardMap = new HashMap<String,StringBuilder>();

		CCSSCoverageData lData = null;
		CCSSCoverageData sData = null;
		for (CCSSCoverageData data : list) {
			String lessonName = data.getLessonName();
			if (lessonMap.containsKey(lessonName) == false) {
				StringBuilder sb = new StringBuilder();
				sb.append(getActivityCode(data.getColumnLabels().get(0)));
				lessonMap.put(lessonName, sb);
				lData = new CCSSCoverageData();
				lData.setUserId(data.getUserId());
				lessonList.add(lData);
				lData.setLessonName(String.format("%s - %s", lessonName, sb.toString()));
			}
			else {
    			StringBuilder sb = lessonMap.get(lessonName);
	    		if (sb.toString().indexOf(getActivityCode(data.getColumnLabels().get(0))) < 0) {
	    			sb.append(getActivityCode(data.getColumnLabels().get(0)));
					lData.setLessonName(String.format("%s - %s", lessonName, sb.toString()));
	    		}
			}
			String standardName = data.getName();
			if (standardMap.containsKey(standardName) == false) {
				StringBuilder sb = new StringBuilder();
				sb.append(getActivityCode(data.getColumnLabels().get(0)));
				standardMap.put(standardName, sb);
				sData = new CCSSCoverageData();
				sData.setUserId(data.getUserId());
				standardList.add(sData);
				sData.setName(String.format("%s - %s", standardName, sb.toString()));
			}
			else {
    			StringBuilder sb = standardMap.get(standardName);
	    		if (sb.toString().indexOf(getActivityCode(data.getColumnLabels().get(0))) < 0) {
	    			sb.append(getActivityCode(data.getColumnLabels().get(0)));
					sData.setName(String.format("%s - %s", standardName, sb.toString()));
	    		}
			}
		}

		List<CCSSCoverageData> retList = new ArrayList<CCSSCoverageData>();
		int minSize = (lessonList.size() > standardList.size()) ? standardList.size() : lessonList.size();
		for (int idx=0; idx < minSize; idx++) {
			CCSSCoverageData data = new CCSSCoverageData();
			data.setUserId(lessonList.get(idx).getUserId());
			data.setLessonName(lessonList.get(idx).getLessonName());
			data.setName(standardList.get(idx).getName());
			retList.add(data);
		}
		if (minSize < lessonList.size()) {
			for (int idx=minSize; idx < lessonList.size(); idx++) {
				CCSSCoverageData data = new CCSSCoverageData();
				data.setUserId(lessonList.get(idx).getUserId());
				data.setLessonName(lessonList.get(idx).getLessonName());
				data.setName("");
				retList.add(data);
			}
		}
		else if (minSize < standardList.size()) {
			for (int idx=minSize; idx < standardList.size(); idx++) {
				CCSSCoverageData data = new CCSSCoverageData();
				data.setUserId(standardList.get(idx).getUserId());
				data.setName(standardList.get(idx).getName());
				data.setLessonName("");
				retList.add(data);
			}
		}
		
		return retList;
	}
	
    /**
     * 
     * @param standardList
     */
	private void sortStandards(List<String> standardList) {
        Collections.sort(standardList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
            	int index1 = o1.indexOf(" - ");
            	int index2 = o2.indexOf(" - ");

            	if (index1 < 0) {
            		return (index2<0) ? 0 : 1;
            	}
            	if (index2 < 0) {
            		return (index1<0) ? 0 : -1;
            	}
            	String name1 = o1.substring(0, index1);
                name1 = name1.replace(".", "-");
            	String name2 = o2.substring(0, index2);
                name2 = name2.replace(".", "-");

                String[] t1 = name1.split("-");
                String[] t2 = name2.split("-");

                int count;
                if (t1.length == t2.length){
                	count = t1.length;
                }
                else {
                	count = (t2.length > t1.length) ? t1.length : t2.length;
                }
            	for (int idx=0; idx<count; idx++) {
            		if (t1[idx].equalsIgnoreCase(t2[idx]) == false) {
            			boolean isNum1 = NumberUtils.isDigits(t1[idx]);
            			boolean isNum2 = NumberUtils.isDigits(t2[idx]);
            			if (isNum1 == false || isNum2 == false) {
            			    return t1[idx].compareToIgnoreCase(t2[idx]);
            			}
            			else {
            				int n1 = Integer.parseInt(t1[idx]);
            				int n2 = Integer.parseInt(t2[idx]);
            				return n1 - n2;
            			}
            		}
            	}
            	return (t1.length > count) ? 1 : -1;
            }
        });
	}

	private String getActivityCode(String activity) {
		return activity.substring(0, 1);
	}
}