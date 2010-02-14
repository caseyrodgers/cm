package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class StudentListReport {
	
	private String reportName;
    private String filterDescription;
    private Map<FilterType,String> filterMap;
    String title;
    	
    private static final Logger LOG = Logger.getLogger(StudentListReport.class);	

    
    public StudentListReport(String title) {
        this.title = title;
    }
    
    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(final Connection conn, String reportId, Integer adminId, List<Integer> uids) throws Exception {
        ByteArrayOutputStream baos = null;
        List<Integer> studentUids = (List<Integer>) CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);

        CmAdminDao adminDao = new CmAdminDao();
        AccountInfoModel info = adminDao.getAccountInfo(conn,adminId);
        
        List<StudentModelI> students = new CmStudentDao().getStudentModels(conn,uids);

        setReportName("Student List");
        
        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        int count = (studentUids != null) ? studentUids.size() : 0;
        filterDescription = ReportUtils.getFilterDescription(conn, adminId, adminDao, filterMap);
        HeaderFooter header = ReportUtils.getGroupReportHeader(info, uids.size(), filterDescription); 
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        PdfPTable tbl = new PdfPTable(1);
       
        tbl.setWidthPercentage(100);
        tbl.setSpacingBefore(5);
        tbl.setSpacingAfter(5);
        tbl.setKeepTogether(true);

        addTitle(title, tbl);
        addHeader("Student Name", "25%", tbl);
        int i = 0;
        
        for(StudentModelI student: students) {
            addCell(student.getName(), tbl, ++i);
        }
        document.add(tbl);

        document.close();

        return baos;
    }

    public void setFilterMap(Map<FilterType,String> filterMap) {
    	this.filterMap = filterMap;
    }

    private void setReportName(String reportName) {
        reportName = reportName;
    }

    public String getReportName() {
    	return reportName;
    }

    private void addTitle(String label, PdfPTable tbl) throws Exception {
        Chunk c = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, new Color(0, 0, 0)));
        c.setTextRise(3.0f);
        PdfPCell cell = new PdfPCell(new Phrase(c));
        cell.disableBorderSide(PdfPCell.LEFT);
        cell.disableBorderSide(PdfPCell.RIGHT);
        cell.disableBorderSide(PdfPCell.TOP);
        cell.setColspan(2);
        tbl.addCell(cell);
    }

    private void addHeader(String label, String percentWidth, PdfPTable tbl) throws Exception {
        Chunk c = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(0, 0, 0)));
        c.setTextRise(2.0f);
        PdfPCell cell = new PdfPCell(new Phrase(c));
        cell.setColspan(1);
        cell.disableBorderSide(PdfPCell.LEFT);
        cell.disableBorderSide(PdfPCell.RIGHT);
        cell.disableBorderSide(PdfPCell.TOP);
        tbl.addCell(cell);
    }

    private void addCell(String content, PdfPTable tbl, int rowNum) throws Exception {
        if (content == null)
            content = "";
        Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0, 0, 0)));
        c.setTextRise(1.5f);
        PdfPCell cell = new PdfPCell(new Phrase(c));
        cell.setColspan(1);
        cell.setBorder(0);
        if (rowNum % 2 < 1)
            cell.setGrayFill(0.9f);
        disableBorders(cell);
        tbl.addCell(cell);
    }

    private void disableBorders(PdfPCell cell) {
      cell.disableBorderSide(PdfPCell.LEFT);
      cell.disableBorderSide(PdfPCell.RIGHT);
      cell.disableBorderSide(PdfPCell.TOP);
      cell.disableBorderSide(PdfPCell.BOTTOM);
    }

}
