package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

public class StudentDetailReport {
	
	private String reportName;
	
	private static Logger logger = Logger.getLogger(StudentDetailReport.class);
	
	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream makePdf(final Connection conn, String reportId, Integer adminId,
			Date fromDate, Date toDate) {
		ByteArrayOutputStream baos = null;

		Integer stuUid = -1;
		try {
			List<Integer> studentUids =
				(List<Integer>)CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);
			stuUid = studentUids.get(0);
			
	        AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
            if (info == null) return null;

        	CmStudentDao studentDao = CmStudentDao.getInstance();
        	StudentModelI sm = studentDao.getStudentModelBase(conn, stuUid, false);
        	
        	StudentActivityDao activityDao = StudentActivityDao.getInstance();
        	List<StudentActivityModel> sList = activityDao.getStudentActivity(stuUid, fromDate, toDate);
						
			Document document = new Document();
			baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);

			StringBuilder sb = new StringBuilder();
			sb.append("CM-DetailReport");
			if (sm.getName() != null)
				sb.append("-").append(sm.getName().replaceAll(" ", ""));
			reportName = sb.toString();

	        HeaderFooter header = ReportUtils.getStudentReportHeader("Student Details", sm, info, null);
	        HeaderFooter footer = ReportUtils.getFooter();

	        document.setHeader(header);
	        document.setFooter(footer);

	        document.open();

			document.add(Chunk.NEWLINE);

			Table tbl = new Table(5);
			tbl.setWidth(100.0f);
			tbl.setBorder(Table.BOTTOM);
			tbl.setBorder(Table.TOP);

			addHeader("Date", "10%", tbl);
			addHeader("Program", "20%", tbl);
			addHeader("Prog-Type", "20%", tbl);
			addHeader("Activity-Section", "20%", tbl);
			addHeader("Result", "30%", tbl);

			tbl.endHeaders();

			int i = 0;
			for (StudentActivityModel sam : sList) {
				addCell(sam.getUseDate(), tbl, ++i);
				addCell(sam.getProgramDescr(), tbl, i);
				addCell(sam.getProgramType(), tbl, i);
				addCell(sam.getActivity(), tbl, i);
				addCell(sam.getResult(), tbl, i);
			}

			document.add(tbl);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			document.close();

		} catch (Exception e) {
			logger.error(String.format("*** Error generating student detail for aid: %d, uid: %d",
				adminId, stuUid), e);
		}
		return baos;
	}

	private Phrase buildLabelContent(String label, String value) {
		if (value == null || value.trim().length() == 0) value = "NONE";
		Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(0, 0, 0))));
		Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(0, 0, 0))));
		phrase.add(content);
		return phrase;
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
		if (content == null) content = "";
		Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0, 0, 0)));
		c.setTextRise(3.0f);
    	Cell cell = new Cell(c);
		cell.setHeader(false);
		cell.setColspan(1);
		cell.setBorder(0);
		if (rowNum%2 < 1) cell.setGrayFill(0.9f);
		tbl.addCell(cell);
	}
	
	public String getReportName() {
		return reportName;
	}

    public class HeaderTable implements PdfPageEvent {
    	
    	PdfPTable header;
    	
    	HeaderTable(PdfWriter writer, PdfPTable header) {
    		//event = writer.getPageEvent();
    		this.header = header;
    		writer.setPageEvent(this);
    	}

		public void onChapter(PdfWriter arg0, Document arg1, float arg2,
				Paragraph arg3) {
		}

		public void onChapterEnd(PdfWriter arg0, Document arg1, float arg2) {
		}

		public void onCloseDocument(PdfWriter arg0, Document arg1) {
 		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			header.writeSelectedRows(0, -1, document.left(), document.top() + 60, cb);
    	}

		public void onGenericTag(PdfWriter arg0, Document arg1, Rectangle arg2,
				String arg3) {
		}

		public void onOpenDocument(PdfWriter arg0, Document arg1) {
		}

		public void onParagraph(PdfWriter arg0, Document arg1, float arg2) {
		}

		public void onParagraphEnd(PdfWriter arg0, Document arg1, float arg2) {
		}

		public void onSection(PdfWriter arg0, Document arg1, float arg2,
				int arg3, Paragraph arg4) {
		}

		public void onSectionEnd(PdfWriter arg0, Document arg1, float arg2) {
		}

		public void onStartPage(PdfWriter arg0, Document arg1) {
		}
    	
    }
}