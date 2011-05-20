package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.ProgramSegmentData;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;


import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
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

public class GroupAssessmentReport {
	
	private String reportName;
	private String filterDescription;
	private Map<FilterType,String> filterMap;
	
    private static final Logger LOG = Logger.getLogger(GroupAssessmentReport.class);	

    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(final Connection conn, String reportId, Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;
        List<Integer> studentUids = (List<Integer>) CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);

        CmAdminDao adminDao = CmAdminDao.getInstance();

        AccountInfoModel info = adminDao.getAccountInfo(adminId);
        if (info == null)
            return null;

        List<StudentModelExt> studentPool = new ArrayList<StudentModelExt>();
        for(Integer uid: studentUids) {
            StudentModelExt st = new StudentModelExt();
            st.setUid(uid);
            studentPool.add(st);
        }
        CmList<TrendingData> trendingData = adminDao.getTrendingData(conn, adminId, studentPool, false);
        CmList<ProgramData> programData = adminDao.getTrendingData_ForProgram(conn, adminId, studentPool,false);

        setReportName(info);
        
        filterDescription = ReportUtils.getFilterDescription(conn, adminId, adminDao, filterMap);

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        int count = (studentUids != null) ? studentUids.size() : 0;
        HeaderFooter header = ReportUtils.getGroupReportHeader(info, count, filterDescription);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        PdfPTable tbl = new PdfPTable(2);
       
        tbl.setWidthPercentage(100);
        tbl.setSpacingBefore(5);
        tbl.setSpacingAfter(5);
        tbl.setKeepTogether(true);

        addTitle("Most Prescribed Lessons", tbl);
        addHeader("Lesson Name", "25%", tbl);
        addHeader("Assignment Count", "20%", tbl);
        int i = 0;
        for (TrendingData d : trendingData) {
            addCell(d.getLessonName(), tbl, ++i);
            addCell(String.valueOf(d.getCountAssigned()), tbl, i);
        }
        document.add(tbl);

        for (ProgramData d : programData) {
        	List<ProgramSegmentData> psDataList = d.getSegments();
            tbl = new PdfPTable(2);
            tbl.setWidthPercentage(100);
            tbl.setSpacingBefore(5);
            tbl.setSpacingAfter(5);
            tbl.setKeepTogether(true);
            addTitle(d.getProgramName(), tbl);
            addHeader("Section", "25%", tbl);
            addHeader("Student Count", "20%", tbl);
            i = 0;
            for (ProgramSegmentData psData : psDataList) {
                addCell("Section " + (psData.getSegment() + 1), tbl, ++i);
                addCell(String.valueOf(psData.getCountCompleted()), tbl, i);
            }
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(tbl);
        }
        document.close();

        return baos;
    }

    public void setFilterMap(Map<FilterType,String> filterMap) {
    	this.filterMap = filterMap;
    }

    private void setReportName(AccountInfoModel info) {
        StringBuilder sb = new StringBuilder();
        sb.append("CM-GroupAssessmentReport");
        if (info.getSchoolName() != null)
        	sb.append("-").append(info.getSchoolName().replaceAll("/| ", ""));
        reportName = sb.toString();
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
