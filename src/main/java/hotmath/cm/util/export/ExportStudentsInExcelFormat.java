package hotmath.cm.util.export;

import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.CustomProgramComposite.Type;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;

import org.apache.log4j.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Export Student detail and report card data in Excel format
 * 
 * @author bob
 *
 */

public class ExportStudentsInExcelFormat {
	
	private static final Logger LOGGER = Logger.getLogger(ExportStudentsInExcelFormat.class);
	
	private static final String DATE_FMT = "%1$tY-%1$tm-%1$td";
	
	private static final String PCNT_FMT = "%3.0f%s";
	
	private List<StudentModelExt> studentList;
	
	private List<StudentReportCardModelI> rcList;
	
	private Map<Integer, Integer> timeOnTaskMap;

	private String filterDescr;

	private String title;
	
	private Integer adminId;
	
	public ExportStudentsInExcelFormat() {
	}
	
	public ExportStudentsInExcelFormat(List<StudentModelExt> studentList) {
		this.studentList = studentList;
	}

	public List<StudentModelExt> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<StudentModelExt> studentList) {
		this.studentList = studentList;
	}

	public List<StudentReportCardModelI> getReportCardList() {
		return rcList;
	}

	public void setReportCardList(List<StudentReportCardModelI> rcList) {
		this.rcList = rcList;
	}

	public Map<Integer, Integer> getTimeOnTaskMap() {
		return timeOnTaskMap;
	}

	public void setTimeOnTaskMap(Map<Integer, Integer> timeOnTaskMap) {
		this.timeOnTaskMap = timeOnTaskMap;
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

	private static String[] headings = {
		"Student", "Password", "Group", "Current Program", "Status", "% Complete", "Quizzes",
		"Last Quiz", "Last Login", "Total Lessons", "Quizzes Attempted", "Quizzes Passed",
		"Passed Quiz Avg Score", "Time-on-Task", "Total Logins", "First Login", "First Program"
	};
	
	public ByteArrayOutputStream export() throws Exception {

		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Catchup Math Students");

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
		
	    Row titleRow = sheet.createRow(0);
	    Cell titleCell = titleRow.createCell(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(title).append(" ").append(filterDescr);
	    titleCell.setCellValue(sb.toString());
        titleCell.setCellStyle(styles.get("title"));
        
	    //the header row: centered text in 48pt font
	    Row headerRow = sheet.createRow(1);
	    headerRow.setHeightInPoints(12.75f);
	    int[] charCount = new int[headings.length];

	    for (int i = 0; i < headings.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(headings[i]);
	        cell.setCellStyle(styles.get("header"));
	        charCount[i] = headings[i].length();
	    }
		
	    int idx = 2;

	    for (StudentModelI sm : studentList) {

	    	StudentReportCardModelI rc = rcList.get(idx-2);

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
	        cell.setCellValue(lastQuiz);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < lastQuiz.length()) charCount[col] = lastQuiz.length();

		    cell = row.createCell(++col);
	        cell.setCellValue(sm.getLastLogin());
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < (sm.getLastLogin().length() + 5)) charCount[col] = sm.getLastLogin().length() + 5;

		    Map<String, Integer> usageMap = rc.getResourceUsage();
		    cell = row.createCell(++col);
		    String lessonCount = String.valueOf((usageMap.get("review") != null)?usageMap.get("review"):0);
	        cell.setCellValue(Integer.parseInt(lessonCount));
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < lessonCount.length()) charCount[col] = lessonCount.length();
	        
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
		    Integer tot = timeOnTaskMap.get(rc.getStudentUid());
		    String timeOnTask = (tot != null) ? String.format("%d", tot) : "0";
	        cell.setCellValue(timeOnTask);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < timeOnTask.length()) charCount[col] = timeOnTask.length();

		    cell = row.createCell(++col);
		    String totalLogins = String.valueOf((usageMap.get("login") != null) ? usageMap.get("login") : 0);
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
	    

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    wb.write(bos);
	    bos.close();

	    return bos;
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
			float currentLesson = Integer.parseInt(tokens[1]);
			float totalLessons = Integer.parseInt(tokens[3]);
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
}