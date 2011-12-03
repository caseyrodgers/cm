package hotmath.cm.util.report;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmProgramListingDao;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;

import hotmath.gwt.shared.client.rpc.CmWebResource;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

public class ProgramDetailsReport {
    
    static private final Logger LOGGER = Logger.getLogger(ProgramDetailsReport.class);
	
	private String reportName = "program-details";
	int adminId;
    ProgramListing programListing;

	String labels[] = { " " };
	int widths[] = { 100 };

	public ProgramDetailsReport(int adminId, ProgramListing programListing) throws Exception {
	    this.adminId = adminId;
	    this.programListing = programListing;
	}

	public CmWebResource getWebResource(final Connection conn) throws Exception {
	    return makeWebResource(makePdf(conn));
	}
	
	private CmWebResource makeWebResource(ByteArrayOutputStream baos) throws Exception {
	    String outputBase = CmWebResourceManager.getInstance().getFileBase();
        File filePath = new File(outputBase, System.currentTimeMillis() + reportName + ".pdf");
        LOGGER.info("Writing PDF output: " + filePath);
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

    public ByteArrayOutputStream makePdf(Connection conn) throws Exception {
        ByteArrayOutputStream baos = null;

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
                
        HeaderFooter header = ReportUtils.getBasicReportHeader(info, null);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        Table tbl = new Table(1);
        tbl.setWidth(100.0f);
        tbl.setBorder(Table.BOTTOM);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        int idx = 0;
        for(String label: labels) {
            int width = widths[idx++];
            addHeader(label, width + "%", tbl);
        }

        tbl.endHeaders();

        document.add(Chunk.NEWLINE);

        int rowNum = 1;

        List<ProgramType> ptList = programListing.getProgramTypes();
        CmProgramListingDao dao = new CmProgramListingDao();
        
        for (ProgramType pt : ptList) {
            addRow(pt.getLabel(), pt.getLevel(), tbl, rowNum++);
            document.add(Chunk.NEWLINE);
            boolean hasChapters = (pt.getLabel().indexOf("Proficiency") < 0 && pt.getLabel().indexOf("Graduation") < 0);
            if (pt.isSelected()) {
            	List<ProgramSubject> psList = pt.getProgramSubjects();
            	for (ProgramSubject ps : psList) {
                    addRow(ps.getLabel(), ps.getLevel(), tbl, rowNum++);
                    document.add(Chunk.NEWLINE);
                    if (ps.isSelected()) {
                        List<ProgramChapter> pcList = ps.getChapters();
                    	for (ProgramChapter pc : pcList) {
                    		boolean isSelected = true;
                    		if (hasChapters) {
                                addRow(pc.getLabel(), pc.getLevel(), tbl, rowNum++);
                                document.add(Chunk.NEWLINE);
                                isSelected = pc.isSelected();
                    		}
                    		if (isSelected) {
                    			List<ProgramSection> sectList = pc.getSections();
                    			for (ProgramSection pSect : sectList) {
                                    addRow(pSect.getLabel(), pSect.getLevel(), tbl, rowNum++);
                                    document.add(Chunk.NEWLINE);
                                    
                                    if (pSect.isSelected()) {
                                    	List<ProgramLesson> list =
                                        	dao.getLessonsFor(conn, pSect.getTestDefId(), pSect.getNumber(), pSect.getParent().getLabel(), sectList.size());
                                    	StringBuilder sb = new StringBuilder();
                                    	int i = 0;
                                    	int limit = 120;
                                    	for (ProgramLesson pLesson : list) {
                                    		int length = sb.length();
                                    		if (length + 2 + pLesson.getLabel().length() < limit) {
                                        		sb.append(pLesson.getLabel());
                                        		if (++i < list.size())
                                        			sb.append(", ");
                                    		}
                                    		else {
                                                addRow(sb.toString(), list.get(0).getLevel(), tbl, rowNum);                                    		
                                                document.add(Chunk.NEWLINE);
                                    			sb = new StringBuilder();
                                    		}
                                    	}
                                    	if (sb.length() > 0) {
                                            addRow(sb.toString(), list.get(0).getLevel(), tbl, rowNum);                                    		
                                            document.add(Chunk.NEWLINE);
                                    	}
                                    	rowNum++;
                                    }
                    			}
                    		}
                    		
                    	}
                    }
            	}
            	
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

    private void addRow(String label, int level, Table tbl, int rowNum) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	for (int i=0; i<level; i++) {
    		sb.append("     ");
    	}
    	sb.append(label);
    	addCell(sb.toString(), tbl, rowNum);
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