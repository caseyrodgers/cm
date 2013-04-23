package hotmath.cm.util.export;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Export Grade Book in Excel format
 * 
 * @author bob
 *
 */

public class ExportGradebookInExcelFormat {
	
	private static final Logger LOGGER = Logger.getLogger(ExportGradebookInExcelFormat.class);
	
	private static final String DATE_FMT = "%1$tm-%1$td";
	
	private static final String PCNT_FMT = "%3.0f%s";
	
	private List<Integer> studentList;
	
	private Map<Integer, String> studentNameMap;

	private Map<Integer, List<StudentAssignment>> studentAssignMap;

    private List<Assignment> assignmentList;
	
	private String filterDescr;

	private String title;
	
	private Integer adminId;
	
	public ExportGradebookInExcelFormat() {
	}
	
	public ExportGradebookInExcelFormat(Map<Integer, String> stuNameMap,
			Map<Integer, List<StudentAssignment>> stuAssignMap) {
		studentNameMap = stuNameMap;
		studentAssignMap = stuAssignMap;
	}

	public void setAssignmentList(List<Assignment> asgList) {
		this.assignmentList = asgList;
	}

	public void setStudentList(List<Integer> uidList) {
		this.studentList = uidList;
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
		"Student", "Average"
	};
	
	public ByteArrayOutputStream export() throws Exception {

		Workbook wb = new HSSFWorkbook();

		buildGradebookSheet(wb);
	    
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    wb.write(bos);
	    bos.close();

	    return bos;
	}

	private void buildGradebookSheet(Workbook wb) {
		Sheet sheet = wb.createSheet("Grade Book");

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
		
	    Row cmRow = sheet.createRow(0);
	    Cell cmCell = cmRow.createCell(0);
	    cmCell.setCellValue("CatchupMath Grade Book");
        cmCell.setCellStyle(styles.get("title"));

        int idx = 1;
        Row titleRow = sheet.createRow(idx++);
	    Cell titleCell = titleRow.createCell(0);
	    StringBuilder sb = new StringBuilder();
	    sb.append(title).append(" ").append(filterDescr);
	    titleCell.setCellValue(sb.toString());
        titleCell.setCellStyle(styles.get("title"));
        
	    //the header row: centered text in 48pt font
	    Row headerRow = sheet.createRow(idx++);
	    headerRow.setHeightInPoints(12.75f);

	    int numAssignments = (assignmentList != null) ? assignmentList.size() : 0;
	    int[] charCount = new int[numAssignments + 2];

        Cell cell = headerRow.createCell(0);
        cell.setCellValue(headings[0]);
        cell.setCellStyle(styles.get("header"));
        charCount[0] = headings[0].length();

        int k = 1;
        for (Assignment asg : assignmentList) {
	        cell = headerRow.createCell(k);
	        String dueDate = String.format(DATE_FMT, asg.getDueDate());
	        cell.setCellValue(dueDate);
	        cell.setCellStyle(styles.get("header"));
	        charCount[k++] = dueDate.length();
        }
        cell = headerRow.createCell(k);
        cell.setCellValue(headings[1]);
        cell.setCellStyle(styles.get("header"));
        charCount[k] = headings[1].length();

	    for (Integer uid : studentList) {

	        Row row = sheet.createRow(idx++);
		    int col = 0;

		    cell = row.createCell(col);
	        cell.setCellValue(studentNameMap.get(uid));
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < (studentNameMap.get(uid).length() + 5)) charCount[col] = studentNameMap.get(uid).length() + 5;

	        int totalScore = 0;
	        List<StudentAssignment> sasList = studentAssignMap.get(uid);
	        for (StudentAssignment sas : sasList) {
			    cell = row.createCell(++col);
			    String score = sas.getHomeworkGrade();
		        cell.setCellValue(score);
		        cell.setCellStyle(styles.get("data"));
		        if (charCount[col] < score.length()) charCount[col] = score.length();
		        totalScore += Integer.parseInt(score.substring(0, score.length()-1)); 
	        }
	        String avgScore = String.format(PCNT_FMT, (double)totalScore/(double)sasList.size(), "%");
		    cell = row.createCell(++col);
	        cell.setCellValue(avgScore);
	        cell.setCellStyle(styles.get("data"));
	        if (charCount[col] < avgScore.length()) charCount[col] = avgScore.length();

	        for (int i=sasList.size(); i < numAssignments; i++) {
			    cell = row.createCell(++col);
		        cell.setCellValue("");
		        cell.setCellStyle(styles.get("data"));
	        }
	    }

	    for (int i = 0; i < charCount.length; i++) {
            sheet.setColumnWidth(i, 256*charCount[i]);
	    }
	    
	    // add legend
	    // addLegend(idx, sheet, styles);
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

}