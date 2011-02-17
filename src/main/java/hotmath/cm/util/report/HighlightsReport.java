package hotmath.cm.util.report;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;
import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class HighlightsReport {
    
    static private Logger __logger = Logger.getLogger(HighlightsReport.class);
	
	private String reportName;
	private Map<FilterType,String> filterMap;
	int adminId;
	HighlightReportLayout reportLayout;
	
	public HighlightsReport(int adminId, String reportName, HighlightReportLayout reportLayout) throws Exception {
	    this.adminId = adminId;
	    this.reportName = reportName;
        this.reportLayout = reportLayout;	    
	}

	public CmWebResource getWebResource(final Connection conn) throws Exception {
	    return makeWebResource(makePdf(conn, adminId));
	}
	
	private CmWebResource makeWebResource(ByteArrayOutputStream baos) throws Exception {
	    String outputBase = CmWebResourceManager.getInstance().getFileBase();
        File filePath = new File(outputBase, System.currentTimeMillis() + "-" + reportName + ".pdf");
        __logger.info("Writing PDF output: " + filePath);
        FileOutputStream fw = null;
        try {
            fw = new FileOutputStream(filePath);
            baos.writeTo(fw);

            return new CmWebResource(filePath.getPath(), CmWebResourceManager.getInstance().getFileBase(), CmWebResourceManager.getInstance().getWebBase());
        }
        finally {
            if (fw != null) fw.close();
        }	    
	}

    public ByteArrayOutputStream makePdf(final Connection conn, Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;
        String filterDescription;
        filterDescription = ReportUtils.getFilterDescription(conn, adminId, new CmAdminDao(), filterMap);

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        AccountInfoModel info = new CmAdminDao().getAccountInfo(conn,adminId);
        
        int rowCount = 0;
        String countLabel = null;

        //TODO: the following defensive code should not be needed, perhaps an out-of-date client?
        if (reportLayout != null) {
        	countLabel = reportLayout.getCountLabel();
        	if (reportLayout.getColumnValues() != null) {
        		rowCount = reportLayout.getColumnValues().length;
        	}
        	else {
        		__logger.warn(String.format("*** no columnValues in reportLayout, adminId: %d, title: %s",
        				adminId, reportLayout.getTitle()));
        	}
        }
        else {
    		__logger.warn(String.format("*** reportLayout is NULL, adminId: %d", adminId));
    		return baos;
        }

        HeaderFooter header = ReportUtils.getGroupReportHeader(info, countLabel, rowCount, filterDescription);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        Table tbl = new Table(reportLayout.getColumnLabels().length);
        tbl.setWidth(100.0f);
        tbl.setBorder(Table.BOTTOM);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        for(String labelToken: reportLayout.getColumnLabels()) {
            String p[] = labelToken.split(":");
            String label = p[0];
            int width = Integer.parseInt(p[1]);
            addHeader(label, width + "%", tbl);
        }

        tbl.endHeaders();

        document.add(Chunk.NEWLINE);

        int rowNum = 1;
        
        if (reportLayout.getColumnValues() != null) {
            for (String[] row : reportLayout.getColumnValues()) {
                for(String labelData: row) {
                    addCell(labelData, tbl, rowNum );
                }
                ++rowNum;

                document.add(Chunk.NEWLINE);
            }
        }
        document.add(tbl);
        document.add(Chunk.NEWLINE);

        document.close();
        return baos;
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
