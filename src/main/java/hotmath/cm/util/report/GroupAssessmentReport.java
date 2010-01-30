package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmAdminTrendingDataFactory;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmAdminTrendingDataFactory.TYPE;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.CmList;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class GroupAssessmentReport {
	
	private String reportName;
	
    private static final Logger LOG = Logger.getLogger(GroupAssessmentReport.class);	

    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(final Connection conn, String reportId, Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;
        List<Integer> studentUids = (List<Integer>) CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);

        CmAdminDao adminDao = new CmAdminDao();

        AccountInfoModel info = adminDao.getAccountInfo(conn, adminId);
        if (info == null)
            return null;

        CmAdminTrendingDataI td = CmAdminTrendingDataFactory.create(TYPE.DUMMY);
        CmList<TrendingData> tdList = td.getTrendingData();

        setReportName(info);

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        int count = (studentUids != null) ? studentUids.size() : 0;
        HeaderFooter header = ReportUtils.getGroupReportHeader(info, count);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        Table tbl = new Table(2);
        tbl.setWidth(100.0f);
        tbl.setBorder(Table.BOTTOM);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        addHeader("Lesson Name", "25%", tbl);
        addHeader("Assignment Count", "20%", tbl);

        tbl.endHeaders();

        int i = 0;
        for (TrendingData d : tdList) {
            addCell(d.getLessonName(), tbl, ++i);
            addCell(String.valueOf(d.getCountAssigned()), tbl, i);
        }

        document.add(tbl);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        document.close();
        return baos;
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
