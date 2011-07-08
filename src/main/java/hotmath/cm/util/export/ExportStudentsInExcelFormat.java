package hotmath.cm.util.export;

import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportStudentsInExcelFormat {
	
	Logger LOGGER = Logger.getLogger(ExportStudentsInExcelFormat.class);
	
	private List<StudentModelExt> studentList;
	
	private Integer adminId;
	
	public ExportStudentsInExcelFormat() {
	}
	
	public ExportStudentsInExcelFormat(List<StudentModelExt> studentList, Integer adminId) {
		this.studentList = studentList;
		this.adminId = adminId;
	}

	public List<StudentModelExt> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<StudentModelExt> studentList) {
		this.studentList = studentList;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	private static String[] titles = {
		"Row", "Student", "Password", "Group", "Program", "Status", "Quizzes", "Last Quiz", "Last Login"
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

	    LOGGER.debug("+++ in export()");

	    //the following three statements are required only for HSSF
	    sheet.setAutobreaks(true);
	    printSetup.setFitHeight((short)1);
	    printSetup.setFitWidth((short)1);

	    Map<String, CellStyle> styles = createStyles(wb);
		
	    //the header row: centered text in 48pt font
	    Row headerRow = sheet.createRow(0);
	    headerRow.setHeightInPoints(12.75f);
	    for (int i = 0; i < titles.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(titles[i]);
	        cell.setCellStyle(styles.get("header"));
	    }
		
	    int idx = 1;
	    for (StudentModelI sm : studentList) {
	        Row row = sheet.createRow(idx++);
		    int col = 0;

		    Cell cell = row.createCell(col++);
	        cell.setCellValue(idx-1);
	        cell.setCellStyle(styles.get("data"));
	        
		    cell = row.createCell(col++);
	        cell.setCellValue(sm.getName());
	        cell.setCellStyle(styles.get("data"));

		    cell = row.createCell(col++);
	        cell.setCellValue(sm.getPasscode());
	        cell.setCellStyle(styles.get("data"));

		    cell = row.createCell(col++);
	        cell.setCellValue(sm.getGroup());
	        cell.setCellStyle(styles.get("data"));

		    cell = row.createCell(col++);
	        cell.setCellValue(sm.getProgram().getProgramDescription());
	        cell.setCellStyle(styles.get("data"));

		    cell = row.createCell(col++);
	        cell.setCellValue(sm.getStatus());
	        cell.setCellStyle(styles.get("data"));

		    cell = row.createCell(col++);
	        cell.setCellValue(defineQuizzesColumn(sm));
	        cell.setCellStyle(styles.get("data"));

		    cell = row.createCell(col++);
	        cell.setCellValue(sm.getLastQuiz());
	        cell.setCellStyle(styles.get("data"));

		    cell = row.createCell(col++);
	        cell.setCellValue(sm.getLastLogin());
	        cell.setCellStyle(styles.get("data"));

	    }

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    wb.write(bos);
	    bos.close();

	    return bos;
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
}