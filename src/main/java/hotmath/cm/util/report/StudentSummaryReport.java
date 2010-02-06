package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class StudentSummaryReport {
	
	private String reportName;

    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(String reportId, Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;
        List<Integer> studentUids = (List<Integer>) CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);

        CmAdminDao adminDao = new CmAdminDao();

        AccountInfoModel info = adminDao.getAccountInfo(adminId);
        if (info == null)
            return null;

        setReportName(info);
        
        CmStudentDao studentDao = new CmStudentDao();
        List<StudentModelI> sList=null;
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            sList = studentDao.getSummariesForActiveStudents(conn, adminId);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }

        Map<Integer, StudentModelI> map = new HashMap<Integer, StudentModelI>(sList.size());
        for (StudentModelI sm : sList) {
            map.put(sm.getUid(), sm);
        }
        List<StudentModelI> list = new ArrayList<StudentModelI>(studentUids.size());
        for (Integer uid : studentUids) {
            list.add(map.get(uid));
        }

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        HeaderFooter header = ReportUtils.getGroupReportHeader(info, list.size(), null);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        Table tbl = new Table(7);
        tbl.setWidth(100.0f);
        tbl.setBorder(Table.BOTTOM);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        addHeader("Student", "15%", tbl);
        addHeader("Group", "20%", tbl);
        addHeader("Program", "15%", tbl);
        addHeader("Status", "15%", tbl);
        addHeader("Last Quiz", "10%", tbl);
        addHeader("Last Login", "15%", tbl);
        addHeader("Usage", "10%", tbl);

        tbl.endHeaders();

        int i = 0;
        for (StudentModelI sm : list) {
            addCell(sm.getName(), tbl, ++i);
            addCell(sm.getGroup(), tbl, i);
            addCell(sm.getProgramDescr(), tbl, i);
            addCell(sm.getStatus(), tbl, i);
            addCell(sm.getLastQuiz(), tbl, i);
            addCell(sm.getLastLogin(), tbl, i);
            addCell(String.valueOf(sm.getTotalUsage()), tbl, i);
        }

        document.add(tbl);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        document.close();
        return baos;
    }
    
    private void setReportName(AccountInfoModel info) {
        StringBuilder sb = new StringBuilder();
        sb.append("CM-SummaryReport");
        if (info.getSchoolName() != null)
        	sb.append("-").append(info.getSchoolName().replaceAll("/| ", ""));
        reportName = sb.toString();
    }

    public String getReportName() {
    	return reportName;
    }

    private void addHeader(String label, String percentWidth, Table tbl) throws Exception {
        Chunk c = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(0, 0, 0)));
        c.setTextRise(4.0f);
        Cell cell = new Cell(c);
        cell.setWidth(percentWidth);
        cell.setHeader(true);
        cell.setColspan(1);
        cell.setBorder(Cell.BOTTOM);
        tbl.addCell(cell);
    }

    private void addCell(String content, Table tbl, int rowNum) throws Exception {
        if (content == null)
            content = "";
        Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0, 0, 0)));
        c.setTextRise(3.0f);
        Cell cell = new Cell(c);
        cell.setHeader(false);
        cell.setColspan(1);
        cell.setBorder(0);
        if (rowNum % 2 < 1)
            cell.setGrayFill(0.9f);
        tbl.addCell(cell);
    }

}
