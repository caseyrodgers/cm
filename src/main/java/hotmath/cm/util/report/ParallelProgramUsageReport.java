package hotmath.cm.util.report;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
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

public class ParallelProgramUsageReport {
    
    static private final Logger LOGGER = Logger.getLogger(ParallelProgramUsageReport.class);
	
	private String reportName = "parallel-prog-usage";
	int adminId;
	int parallelProgId;
	
	String labels[] = { "Name", "Activity", "Result", "Date" };
	int widths[] = { 30, 20, 30, 20 };

	public ParallelProgramUsageReport(int adminId, int parallelProgId) throws Exception {
	    this.adminId = adminId;
	    this.parallelProgId = parallelProgId;
	}

	public CmWebResource getWebResource(final Connection conn) throws Exception {
	    return makeWebResource(makePdf(adminId));
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

    public ByteArrayOutputStream makePdf(Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
        
        List<ParallelProgramUsageModel> list = ParallelProgramDao.getInstance().getUsageForParallelProgram(parallelProgId);
        
        int rowCount = list.size();
        String countLabel = "Student Count: ";

        HeaderFooter header = ReportUtils.getGroupReportHeader(info, countLabel, rowCount, null);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        Table tbl = new Table(4);
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
        
        for (ParallelProgramUsageModel mdl : list) {
        	addRow(mdl, tbl, rowNum);
            document.add(Chunk.NEWLINE);
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

    private void addRow(ParallelProgramUsageModel mdl, Table tbl, int rowNum) throws Exception {
    	addCell(mdl.getStudentName(), tbl, rowNum);
    	addCell(mdl.getActivity(), tbl, rowNum);
    	addCell(mdl.getResult(), tbl, rowNum);
    	addCell(mdl.getUseDate(), tbl, rowNum);
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