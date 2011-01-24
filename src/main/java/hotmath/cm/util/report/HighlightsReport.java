package hotmath.cm.util.report;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;

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
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class HighlightsReport {
    
    static private Logger __logger = Logger.getLogger(HighlightsReportPdf.class);
	
	private String reportName;
	private Map<FilterType,String> filterMap;
	CmList<HighlightReportData> models;
	int adminId;
	
	public HighlightsReport(int adminId, String reportName, CmList<HighlightReportData> models) throws Exception {
	    this.adminId = adminId;
	    this.reportName = reportName;
	    this.models = models;   
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
	
    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(final Connection conn, Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;
        String filterDescription;
        filterDescription = ReportUtils.getFilterDescription(conn, adminId, new CmAdminDao(), filterMap);

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        AccountInfoModel info = new CmAdminDao().getAccountInfo(conn,adminId);
        
        HeaderFooter header = ReportUtils.getGroupReportHeader(info, models.size(), filterDescription);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        Table tbl = new Table(2);
        tbl.setWidth(100.0f);
        tbl.setBorder(Table.BOTTOM);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        addHeader("Student", "70%", tbl);
        addHeader("<Data Label>", "30%", tbl);

        tbl.endHeaders();

        document.add(Chunk.NEWLINE);
        
        int i = 0;
        for (HighlightReportData d : models) {
            addCell(d.getName(), tbl, ++i);
            addCell(d.getData(), tbl, i);
        }
/*        
        for (HighlightReportData sm : models) {
            addReportDataLine(document, sm);
        }
*/
        document.add(tbl);
        document.add(Chunk.NEWLINE);

        document.close();
        return baos;
    }
    
    private void addReportDataLine(Document doc, HighlightReportData data) throws Exception {
            PdfPTable lessonTbl = new PdfPTable(1);
            lessonTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            
            Phrase phrase = new Phrase(new Chunk(data.getName(), FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(0, 0, 0))));
            Phrase content = new Phrase(new Chunk(data.getData(), FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(0, 0, 0))));            
            
            lessonTbl.addCell(phrase);
            lessonTbl.addCell(content);
            lessonTbl.setWidthPercentage(100.0f);
            lessonTbl.setSpacingBefore(20.0f);
            doc.add(lessonTbl);
            doc.add(Chunk.NEWLINE);
    }
    
    private Paragraph buildSectionContent(String label, String value, Boolean useDefault) {
        if (value == null || value.trim().length() == 0) {
            value = (useDefault) ? "n/a" : " ";
        }
        Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(
                0, 0, 0))));
        Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL,
                new Color(0, 0, 0))));
        phrase.add(content);
        Paragraph p = new Paragraph();
        p.add(phrase);
        p.setIndentationLeft(30.0f);
        return p;
    }    

    private Phrase buildSectionLabel(String label) {
        Chunk chunk = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0, 0, 0)));
        chunk.setUnderline(0.5f, -3f);
        Phrase phrase = new Phrase(chunk);
        return phrase;
    }

    public void setFilterMap(Map<FilterType,String> filterMap) {
    	this.filterMap = filterMap;
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

    static public void setBaseData(StudentModelI smBase, StudentModelI smExt) {
        smExt.setName(smBase.getName());
        smExt.setPasscode(smBase.getPasscode());
        smExt.getSettings().setShowWorkRequired(smBase.getSettings().getShowWorkRequired());
        smExt.getSettings().setTutoringAvailable(smBase.getSettings().getTutoringAvailable());
        smExt.setGroupId(smBase.getGroupId());
        smExt.setGroup(smBase.getGroup());
        smExt.setSectionNum(smBase.getSectionNum());
        smExt.setChapter(smBase.getChapter());
        smExt.setSectionNum(smBase.getSectionNum());
        smExt.setPassPercent(smBase.getPassPercent());
        smExt.setProgram(smBase.getProgram());
        smExt.setStatus(smBase.getStatus());
    }

}
