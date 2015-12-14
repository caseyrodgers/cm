package hotmath.cm.util.export;

import hotmath.gwt.cm_admin.server.model.activity.StudentActivitySummaryModel;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.CustomProgramComposite.Type;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Export Student detail, report card, and ccss coverage data in Excel format
 * 
 * @author bob
 *
 */

public class ExportStudentsInExcelFormat {
	
	private static final Logger LOGGER = Logger.getLogger(ExportStudentsInExcelFormat.class);
	
	private static final String DATE_FMT = "%1$tY-%1$tm-%1$td";
	
	private static final String PCNT_FMT = "%3.0f%s";
	
	private List<StudentModelI> studentList;
	
	private List<StudentReportCardModelI> rcList;
	
	private Map<Integer, List<StudentActivitySummaryModel>> sasMap;
	
	private Map<Integer, Integer> totalTimeMap;

	private Map<Integer, List<String>> topicsMap;

	private Map<Integer, List<String>> standardsMap;

	private Map<Integer, Map<String, List<String>>> standardsByTopicMap;

	private Map<Integer, List<String>> standardsNotCoveredMap;

	private String filterDescr;

	private String title;
	
	private Integer adminId;

	private String ccssLevelName;

	private int ccssColumnCount;

	public ExportStudentsInExcelFormat() {
	}

	public ExportStudentsInExcelFormat(List<StudentModelI> studentList) {
		this.studentList = studentList;
	}

	public List<StudentModelI> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<StudentModelI> studentList) {
		this.studentList = studentList;
	}

	public List<StudentReportCardModelI> getReportCardList() {
		return rcList;
	}

	public void setReportCardList(List<StudentReportCardModelI> rcList) {
		this.rcList = rcList;
	}

	public Map<Integer, Integer> getTotalTimeOnTaskMap() {
		return totalTimeMap;
	}

	public void setTotalTimeMap(Map<Integer, Integer> totalTimeMap) {
		this.totalTimeMap = totalTimeMap;
	}

	public void setTopicsMap(Map<Integer, List<String>> topicsMap) {
		this.topicsMap = topicsMap;
	}

	public void setStandardsMap(Map<Integer, List<String>> standardsMap) {
		this.standardsMap = standardsMap;
	}

	public void setStandardsByTopicMap(Map<Integer, Map<String, List<String>>> stdByTopicMap) {
		this.standardsByTopicMap = stdByTopicMap;
	}

	public void setStandardsNotCoveredMap(Map<Integer, List<String>> standardsNotCoveredMap) {
		this.standardsNotCoveredMap = standardsNotCoveredMap;
	}

	public String getFilterDescr() {
		return filterDescr;
	}

	public void setFilterDescr(String filterDescr) {
		this.filterDescr = filterDescr;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCcssLevelName() {
		return ccssLevelName;
	}

	public void setCcssLevelName(String ccssLevelName) {
		this.ccssLevelName = ccssLevelName;
	}

	public Map<Integer, List<StudentActivitySummaryModel>> getStudentActivitySummaryMap() {
		return sasMap;
	}

	public void setStudentActivitySummaryMap(Map<Integer, List<StudentActivitySummaryModel>> sasMap) {
		this.sasMap = sasMap;
	}

	private static String[] headings = {
		"Student", "Password", "Group", "Current Program", "Status", "% Complete", "Quizzes",
		"Last Quiz", "Last Login", "Total Lessons", "Total Standards", "Quizzes Attempted", "Quizzes Passed",
		"Passed Quiz Avg Score", "Total Time", "Total Logins", "First Login", "First Program"
	};
	
	private static String[] headingsSheet2 = {
		"Student", "Program", "Prog-Type", "Date", "Status", "Total Quizzes", "Passed Quizzes",
		"Total Units", "Passed Quiz Avg", "All Quiz Avg", "Quiz 1", "Quiz 2",
		"Quiz 3", "Quiz 4", "Quiz 5", "Quiz 6", "Quiz 7", "Quiz 8", "Quiz 9", "Quiz 10"
	};
	
	private static String[] headingsSheet3 = {
		"Student", "Group"
	};
	
	public ByteArrayOutputStream export() throws Exception {

		Workbook wb = new HSSFWorkbook();

		buildStudentSheet(wb);
		buildStudentProgramSheet(wb);
		buildStudentStandardsSheet(wb);
	    
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    wb.write(bos);
	    bos.close();

	    return bos;
	}

	private void buildStudentSheet(Workbook wb) {
		Sheet sheet = wb.createSheet("Overview");

	    sheet.setDisplayGridlines(false);
	    sheet.setPrintGridlines(false);
	    sheet.setFitToPage(true);
	    sheet.setHorizontallyCenter(true);
	    PrintSetup printSetup = sheet.getPrintSetup();
	    printSetup.setLandscape(true);

	    //the following three statements are required only for HSSF
	    sheet.setAutobreaks(true);
	    printSetup.setFitHeight((short)1);
	    printSetup.setFitWidth((short)1);

	    Map<String, CellStyle> styles = createStyles(wb);
		
        int idx = 1;
	    sheet.createRow(0);
	    Row titleRow = sheet.createRow(idx++);
	    Cell titleCell = titleRow.createCell(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(title).append(" ").append(filterDescr);
	    titleCell.setCellValue(sb.toString());
        titleCell.setCellStyle(styles.get("title"));
        
	    //the header row: centered text in 48pt font
	    Row headerRow = sheet.createRow(idx++);
	    headerRow.setHeightInPoints(12.75f);
	    int[] charCount = new int[headings.length];

	    for (int i = 0; i < headings.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(headings[i]);
	        cell.setCellStyle(styles.get("header"));
	        charCount[i] = headings[i].length();
	    }
		
	    for (StudentModelI sm : studentList) {

	    	StudentReportCardModelI rc = rcList.get(idx-3);

	        Row row = sheet.createRow(idx++);
		    int col = 0;

		    Cell cell = row.createCell(col);
	        cell.setCellValue(sm.getName());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < (sm.getName().length() + 5)) charCount[col] = sm.getName().length() + 5;

		    cell = row.createCell(++col);
	        cell.setCellValue(sm.getPasscode());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < sm.getPasscode().length()) charCount[col] = sm.getPasscode().length();

		    cell = row.createCell(++col);
	        cell.setCellValue(sm.getGroup());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < sm.getGroup().length()) charCount[col] = sm.getGroup().length();

		    cell = row.createCell(++col);
	        cell.setCellValue(sm.getProgram().getProgramDescription());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < sm.getProgram().getProgramDescription().length())
	        	charCount[col] = sm.getProgram().getProgramDescription().length();

		    cell = row.createCell(++col);
	        cell.setCellValue(sm.getStatus());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < sm.getStatus().length()) charCount[col] = sm.getStatus().length();

		    cell = row.createCell(++col);
		    String percentComplete = getPercentComplete(sm);
	        cell.setCellValue(percentComplete);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < percentComplete.length()) charCount[col] = percentComplete.length();

		    cell = row.createCell(++col);
		    String quizzes = defineQuizzesColumn(sm);
	        cell.setCellValue(quizzes);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < quizzes.length()) charCount[col] = quizzes.length();

		    cell = row.createCell(++col);
		    String lastQuiz = defineLastQuizColumn(sm);
		    if(lastQuiz == null) {
		        lastQuiz = "";
		    }
		    
	        cell.setCellValue(lastQuiz);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < lastQuiz.length()) charCount[col] = lastQuiz.length();

		    cell = row.createCell(++col);
	        cell.setCellValue(sm.getLastLogin());
	        cell.setCellStyle(styles.get("data"));
	        String lastLogin = sm.getLastLogin();
	        if(lastLogin != null) {
	            if (charCount[col] < (lastLogin.length() + 5)) charCount[col] = sm.getLastLogin().length() + 5;
	        }

		    Map<String, Integer> usageMap = rc.getResourceUsage();
		    cell = row.createCell(++col);
		    String lessonCount = String.valueOf((usageMap != null && usageMap.get("review") != null)?usageMap.get("review"):0);
	        cell.setCellValue(Integer.parseInt(lessonCount));
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < lessonCount.length()) charCount[col] = lessonCount.length();

		    cell = row.createCell(++col);
		    List<String> stdList = standardsMap.get(sm.getUid());
    		int stdCount = (stdList != null) ? stdList.size() : 0;
		    String standardCount = String.valueOf(stdCount);
	        cell.setCellValue(stdCount);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < standardCount.length()) charCount[col] = standardCount.length();

		    cell = row.createCell(++col);
		    String quizAtmpt = String.valueOf(rc.getQuizCount());
	        cell.setCellValue(rc.getQuizCount());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < quizAtmpt.length()) charCount[col] = quizAtmpt.length();

		    cell = row.createCell(++col);
		    String quizPassd = String.valueOf(rc.getQuizPassCount());
	        cell.setCellValue(rc.getQuizPassCount());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < quizPassd.length()) charCount[col] = quizPassd.length();

		    cell = row.createCell(++col);
		    String quizAvg = (rc.getQuizAvgPassPercent() != null) ? String.format("%d%s", rc.getQuizAvgPassPercent(), "%") : "n/a";
	        cell.setCellValue(quizAvg);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < quizAvg.length()) charCount[col] = quizAvg.length();

		    cell = row.createCell(++col);
		    Integer tot = totalTimeMap.get(rc.getStudentUid());
		    String timeOnTask = (tot != null) ? String.format("%d", tot) : "0";
	        cell.setCellValue(Integer.parseInt(timeOnTask));
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < timeOnTask.length()) charCount[col] = timeOnTask.length();

		    cell = row.createCell(++col);
		    String totalLogins = String.valueOf((usageMap != null && usageMap.get("login") != null) ? usageMap.get("login") : 0);
	        cell.setCellValue(Integer.parseInt(totalLogins));
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < totalLogins.length()) charCount[col] = totalLogins.length();

		    cell = row.createCell(++col);
			Date actDate = rc.getFirstActivityDate();
			String activityDate = (actDate != null) ? String.format(DATE_FMT, actDate) : " ";
	        cell.setCellValue(activityDate);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < activityDate.length()) charCount[col] = activityDate.length();

		    cell = row.createCell(++col);
	        cell.setCellValue(rc.getInitialProgramShortName());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < (rc.getInitialProgramShortName().length() + 5))
	        	charCount[col] = rc.getInitialProgramShortName().length() + 5;

	    }

	    for (int i = 0; i < headings.length; i++) {
            sheet.setColumnWidth(i, 256*charCount[i]);
	    }
	    
	    // add legend
	    addLegend(idx, sheet, styles);
	}

	private void buildStudentProgramSheet(Workbook wb) {
		Sheet sheet = wb.createSheet("Details");

	    sheet.setDisplayGridlines(false);
	    sheet.setPrintGridlines(false);
	    sheet.setFitToPage(true);
	    sheet.setHorizontallyCenter(true);
	    PrintSetup printSetup = sheet.getPrintSetup();
	    printSetup.setLandscape(true);

	    //the following three statements are required only for HSSF
	    sheet.setAutobreaks(true);
	    printSetup.setFitHeight((short)1);
	    printSetup.setFitWidth((short)1);

	    Map<String, CellStyle> styles = createStyles(wb);
		
	    int idx = 1;

	    sheet.createRow(0);
	    Row titleRow = sheet.createRow(idx++);
	    Cell titleCell = titleRow.createCell(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(title).append(" ").append(filterDescr);
	    titleCell.setCellValue(sb.toString());
        titleCell.setCellStyle(styles.get("title"));
        
	    //the header row: centered text in 48pt font
	    Row headerRow = sheet.createRow(idx++);
	    headerRow.setHeightInPoints(12.75f);
	    int[] charCount = new int[headingsSheet2.length];

	    for (int i = 0; i < headingsSheet2.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(headingsSheet2[i]);
	        cell.setCellStyle(styles.get("header"));
	        charCount[i] = headingsSheet2[i].length();
	    }
		
	    for (StudentModelI sm : studentList) {

	    	List<StudentActivitySummaryModel> sasList = sasMap.get(sm.getUid());

	    	if (sasList == null)  {
	    		if (LOGGER.isDebugEnabled()) {
	    			LOGGER.debug("+++ no activity, skipping uid: " + sm.getUid());
	    		}
	    		continue;
	    	}

	    	for (StudentActivitySummaryModel model : sasList) {

	    		Row row = sheet.createRow(idx++);
	    		int col = 0;

	    		Cell cell = row.createCell(col);
	    		cell.setCellValue(sm.getName());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < (sm.getName().length() + 5)) charCount[col] = sm.getName().length() + 5;

	    		cell = row.createCell(++col);
	    		cell.setCellValue(model.getProgramName());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < model.getProgramName().length()) charCount[col] = model.getProgramName().length();

	    		cell = row.createCell(++col);
	    		cell.setCellValue(model.getProgramType());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < model.getProgramType().length()) charCount[col] = model.getProgramType().length();

	    		cell = row.createCell(++col);
	    		cell.setCellValue(model.getUseDate());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < model.getUseDate().length()) charCount[col] = model.getUseDate().length();

	    		cell = row.createCell(++col);
	    		cell.setCellValue(model.getStatus());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < model.getStatus().length())
	    			charCount[col] = model.getStatus().length();

	    		cell = row.createCell(++col);
	    		cell.setCellValue(model.getTotalQuizzes());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < String.valueOf(model.getTotalQuizzes()).length())
	    			charCount[col] = String.valueOf(model.getTotalQuizzes()).length();

	    		cell = row.createCell(++col);
	    		cell.setCellValue(model.getPassedQuizzes());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < String.valueOf(model.getPassedQuizzes()).length())
	    			charCount[col] = String.valueOf(model.getPassedQuizzes()).length();

	    		cell = row.createCell(++col);
	    		cell.setCellValue(model.getSectionNum());
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < String.valueOf(model.getSectionNum()).length())
	    			charCount[col] = String.valueOf(model.getSectionNum()).length();

	    		cell = row.createCell(++col);
	    		String percent = getPercent(model.getPassedQuizAvg());
	    		cell.setCellValue(percent);
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < percent.length()) charCount[col] = percent.length();

	    		cell = row.createCell(++col);
	    		percent = getPercent(model.getAllQuizAvg());
	    		cell.setCellValue(percent);
	    		cell.setCellStyle(styles.get("data"));
	    		if (charCount[col] < percent.length()) charCount[col] = percent.length();

	    		List<Integer> quizScores = model.getQuizScores();

	    		int quizCount = 0;
	    		for (Integer quizScore : quizScores) {
	    			quizCount++;
	    			cell = row.createCell(++col);
	    			percent = (quizScore != null) ? getPercentWithZero(quizScore) : " ";
	    			cell.setCellValue(percent);
	    			cell.setCellStyle(styles.get("data"));
	    			if (charCount[col] < percent.length()) charCount[col] = percent.length();
	    		}
	    		while (quizCount < 10) {
	    			quizCount++;
	    			cell = row.createCell(++col);
	    			cell.setCellValue(" ");
	    			cell.setCellStyle(styles.get("data"));
	    		}
	    	}
	    }

	    for (int i = 0; i < headings.length; i++) {
            sheet.setColumnWidth(i, 256*charCount[i]);
	    }
	    
	    // add legend
	    addStudentProgramLegend(idx, sheet, styles);
	}

	int firstColmWidth;
	int colmWidth = 15;

	private void buildStudentStandardsSheet(Workbook wb) {
		Sheet sheet = wb.createSheet("Standards");

	    sheet.setDisplayGridlines(false);
	    sheet.setPrintGridlines(false);
	    sheet.setFitToPage(true);
	    sheet.setHorizontallyCenter(true);
	    PrintSetup printSetup = sheet.getPrintSetup();
	    printSetup.setLandscape(true);

	    //the following three statements are required only for HSSF
	    sheet.setAutobreaks(true);
	    printSetup.setFitHeight((short)1);
	    printSetup.setFitWidth((short)1);

	    Map<String, CellStyle> styles = createStyles(wb);
		
	    int idx = 1;

	    ccssColumnCount = calcColumnCount();
	    LOGGER.info("ccssColumnCount: " + ccssColumnCount);

	    sheet.createRow(0);
	    Row titleRow = sheet.createRow(idx++);
	    Cell titleCell = titleRow.createCell(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(title).append(" ").append(filterDescr);
	    titleCell.setCellValue(sb.toString());
        titleCell.setCellStyle(styles.get("title"));
        
	    //the header row: centered text in 48pt font
	    Row headerRow = sheet.createRow(idx++);
	    headerRow.setHeightInPoints(12.75f);
	    int[] charCount = new int[headingsSheet3.length];

	    for (int i = 0; i < headingsSheet3.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(headingsSheet3[i]);
	        cell.setCellStyle(styles.get("header"));
	        charCount[i] = headingsSheet3[i].length();
	    }
		
	    for (StudentModelI sm : studentList) {

	    	// leave blank row between each student's records
    		Row row = sheet.createRow(idx++);
    		row = sheet.createRow(idx++);;

    		Cell cell = row.createCell(0);
    		cell.setCellValue(sm.getName());
    		int width = sm.getName().length();
    		if (firstColmWidth < width)
    			firstColmWidth = width;
    		cell.setCellStyle(styles.get("header"));
    		cell = row.createCell(1);
    		cell.setCellValue(sm.getGroup());
    		cell.setCellStyle(styles.get("header"));

    		//List<String> stdList = standardsMap.get(sm.getUid());
    		//List<String> topicList = topicsMap.get(sm.getUid());
    		Map<String, List<String>> stdByTopicMap = standardsByTopicMap.get(sm.getUid());
    		Set<String> topicSet = null;
    		if (stdByTopicMap != null) {
    			topicSet = stdByTopicMap.keySet();
    			LOGGER.info("topicSet.size(): " + topicSet.size());
    		}

    		row = sheet.createRow(idx++);
    		addLessonCoverage(row, topicSet, styles);
    		
    		row = sheet.createRow(idx++);
    		addStandardsCoverage(row, stdByTopicMap, styles);

    		if (ccssLevelName == null) continue;
    		row = sheet.createRow(idx++);
    		addStandardsNotCovered(row, standardsNotCoveredMap.get(sm.getUid()), styles);
    		
	    }

	    sheet.setColumnWidth(0, 320*firstColmWidth);
	    for (int i = 1; i <= ccssColumnCount; i++) {
	    	sheet.setColumnWidth(i, 256 * colmWidth);
	    }

	    // add legend
	    addStandardsLegend(idx, sheet, styles);
	}

	private int calcColumnCount() {
		int maxCount = getMaxColumns();
		LOGGER.info("topics Coverd #: " + maxCount);
		int count = getMaxColumns(standardsNotCoveredMap);
		LOGGER.info("stds not covered #: " + count);
		if (count > maxCount) maxCount = count;
		if (maxCount > 255) maxCount = 255;
		return maxCount;
	}

	private int getMaxColumns() {
		Set<Integer> keys = standardsByTopicMap.keySet();
		int count = 0;
		for (Integer uid : keys) {
			Map<String, List<String>> items = standardsByTopicMap.get(uid);
			if (count < items.size())
				count = items.size();
		}
		return count;
	}

	private int getMaxColumns(Map<Integer, List<String>> map) {
		Set<Integer> keys = map.keySet();
		Iterator<Integer> iter = keys.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Integer uid = iter.next();
			List<String> items = map.get(uid);
			if (count < items.size())
				count = items.size();
		}
		return count;
	}

	private void addLessonCoverage(Row row, Set<String> list, Map<String, CellStyle> styles) {
        addCells("Covered Topics: ", row, list, styles, false);
	}

	private void addStandardsCoverage(Row row, List<String> list, Map<String, CellStyle> styles) {
        addCells("Covered Standards: ", row, list, styles, true);
	}

	private void addStandardsCoverage(Row row, Map<String, List<String>> stdByTopicMap, Map<String, CellStyle> styles) {
		List<String> list = new ArrayList<String>();
		if (stdByTopicMap != null) {
			for (String topic : stdByTopicMap.keySet()) {
				List<String> stds = stdByTopicMap.get(topic);
				StringBuilder sb = new StringBuilder();
				boolean isFirst = true;
				for (String std : stds) {
					if (isFirst != true) sb.append(" ");
					isFirst = false;
					sb.append(std);
				}
				list.add(sb.toString());
			}
		}
		addCells("Covered Standards: ", row, list, styles, false);
	}

	private void addStandardsNotCovered(Row row, List<String> list, Map<String, CellStyle> styles) {
        addCells("Remaining Standards: ", row, list, styles, true);
	}

	private void addCells(String title, Row row, Collection<String> list, Map<String, CellStyle> styles, boolean calcWidth) {
		Cell cell = row.createCell(0);
		cell.setCellValue(title);
		cell.setCellStyle(styles.get("data"));

		int idx = 1;

		if (list != null) {
			for (String data : list) {
				cell = row.createCell(idx++);
				cell.setCellValue(data);
				int width = data.length();
				if (calcWidth && colmWidth < width)
					colmWidth = width;
				cell.setCellStyle(styles.get("data"));
			}
		}
		if (idx <= ccssColumnCount)
			addEmptyCells(row, idx, styles);
	}

	private void addEmptyCells(Row row, int idx, Map<String, CellStyle> styles) {
		Cell cell;
		while (idx <= ccssColumnCount) {
			cell = row.createCell(idx++);
			cell.setCellValue("");
			cell.setCellStyle(styles.get("data"));
		}
	}

	private void addLegend(int idx, Sheet sheet, Map<String, CellStyle> styles) {
    	idx = idx + 3;
	    int col = 0;

        Row row = sheet.createRow(idx++);
	    Cell cell = row.createCell(col);
        cell.setCellValue("% Complete - percentage of completion in current program");
        cell.setCellStyle(styles.get("data"));
    	
        row = sheet.createRow(idx++);
	    cell = row.createCell(col);
        cell.setCellValue("Last Quiz - status or score for most recent Quiz");
        cell.setCellStyle(styles.get("data"));

        row = sheet.createRow(idx++);
	    cell = row.createCell(col);
        cell.setCellValue("Last Login - date of most recent CM activity");
        cell.setCellStyle(styles.get("data"));

        row = sheet.createRow(idx++);
        cell = row.createCell(col);
        cell.setCellValue("Total Logins - number of times the student has logged in");
        cell.setCellStyle(styles.get("data"));

        row = sheet.createRow(idx++);
        cell = row.createCell(col);
        cell.setCellValue("First Login - date of Students first activity in CM");
        cell.setCellStyle(styles.get("data"));
    }
    
    private void addStudentProgramLegend(int idx, Sheet sheet, Map<String, CellStyle> styles) {
    	idx = idx + 3;
	    int col = 0;

        Row row = sheet.createRow(idx++);
	    Cell cell = row.createCell(col);
        cell.setCellValue("Total Units - Completed Sections for Proficiency and Chapter Programs, completed lessons for Custom Programs, completed questions for Custom Quizzes.");
        cell.setCellStyle(styles.get("data"));
    	
        row = sheet.createRow(idx++);
	    cell = row.createCell(col);
        cell.setCellValue("Quiz 1-10 - scores are shown in reverse chronological order (i.e., Quiz 1 is the most recent)");
        cell.setCellStyle(styles.get("data"));
    }

    private void addStandardsLegend(int idx, Sheet sheet,
			Map<String, CellStyle> styles) {
    	idx = idx + 3;
	    int col = 0;

        Row row = sheet.createRow(idx++);
	    Cell cell = row.createCell(col);
		
        cell.setCellValue("Covered Topics - topic name followed by A (Assignment), L (Lesson), Q (Quiz) to indicate how the topic was covered.");
        cell.setCellStyle(styles.get("data"));
    	
        row = sheet.createRow(idx++);
	    cell = row.createCell(col);
        cell.setCellValue("Covered Standards - standard name followed by A (Assignment), L (Lesson), Q (Quiz) to indicate how the standard was covered.");
        cell.setCellStyle(styles.get("data"));

        if (this.ccssLevelName == null) return;

        row = sheet.createRow(idx++);
	    cell = row.createCell(col);
        cell.setCellValue("Remaining Standards - standard names from strand '" + ccssLevelName + "' that have not been covered.");
        cell.setCellStyle(styles.get("data"));
    }

    private Map<String, CellStyle> createStyles(Workbook wb) {

		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

		Font headerFont = wb.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		CellStyle style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		styles.put("header", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setWrapText(true);
		styles.put("data", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		styles.put("title", style);

		return styles;
	}

	private CellStyle createBorderedStyle(Workbook wb){
		CellStyle style = wb.createCellStyle();
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

	private String defineQuizzesColumn(StudentModelI sm) {
        if (!sm.getProgram().isCustom()
            && (sm.getPassingCount() > 0 || sm.getNotPassingCount() > 0)) {
            StringBuilder sb = new StringBuilder();
            sb.append(sm.getPassingCount()).append(" passed out of ");
            sb.append(sm.getPassingCount() + sm.getNotPassingCount());
            return sb.toString();
        } else {
            return "";
        }
	}

	private String defineLastQuizColumn(StudentModelI sm) {
        if (sm.getProgram().getCustom().isCustomLessons()) {
            return "";
        } else {
            return sm.getLastQuiz();
        }
	}

	private String getPercentComplete(StudentModelI sm) {
		String[] tokens = sm.getStatus().split(" ");
		if (tokens[0].equalsIgnoreCase("NOT")) {
			return "0%"; 
		}
		else if (tokens[0].equalsIgnoreCase("COMPLETED")) {
			return "100%";
		}
		
		if (!sm.getProgram().isCustom()) {
			float sectionCount = (sm.getSectionCount() == null) ? 0 : sm.getSectionCount();
			float sectionNum   = (sm.getSectionNum() == null) ? 0 : sm.getSectionNum();
			if (sectionCount != 0.0f) {
				float percent = (sectionNum != sectionCount) ?
						(sectionNum * 100.0f) / sectionCount : 90.0f;
						String percentComplete = String.format(PCNT_FMT, percent, "%");
						return percentComplete;
			}
		} else if(sm.getProgram().getCustom().getType() == Type.LESSONS) {
			float currentLesson = Integer.parseInt(tokens[0]);
			float totalLessons = Integer.parseInt(tokens[2]);
			if (totalLessons != 0.0f) {
				// set percent complete to 90 if on last lesson but not 'COMPLETED'
				float percent = (currentLesson != totalLessons) ?
						(currentLesson * 100.0f) / totalLessons : 90.0f;
						String percentComplete = String.format(PCNT_FMT, percent, "%");
						return percentComplete;
			}
		} else if(sm.getProgram().getCustom().getType() == Type.QUIZ) {
			return "50%";
		}
		return "";
	}
	
	private String getPercent(int percent) {
        return (percent > 0) ? String.format("%d%s", percent, "%") : " ";
	}

	private String getPercentWithZero(int percent) {
        return String.format("%d%s", percent, "%");
	}

}