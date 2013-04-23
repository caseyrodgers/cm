package hotmath.cm.util.report;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

public class StudentAssignmentReport {
	
	private Map<FilterType, String> filterMap;
	private String reportName;
    private String title;
    
    private SimpleDateFormat sdFmt = new SimpleDateFormat("yyyy-MM-dd");
	
	private static Logger logger = Logger.getLogger(StudentAssignmentReport.class);

	public StudentAssignmentReport(String title) {
		this.title = title;
	}

	public ByteArrayOutputStream makePdf(final Connection conn, String reportId, int adminId,
			List<Integer> studentUids) {
		return makePdf(conn, reportId, adminId, studentUids, null, null);
	}

	public ByteArrayOutputStream makePdf(final Connection conn, String reportId, int adminId,
			List<Integer> studentUids, Date fromDate, Date toDate) {
		ByteArrayOutputStream baos = null;

		Integer stuUid = -1;

		try {
			AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
			if (info == null) return null;

            String filterDescription = ReportUtils.getFilterDescription(conn, adminId, CmAdminDao.getInstance(), filterMap);

			StringBuilder sb = new StringBuilder();
			sb.append("CM-AssignmentReport-");
			if (info.getSchoolName() != null)
				sb.append("-").append(info.getSchoolName().replaceAll(" ", ""));
			reportName = sb.toString();

			AssignmentDao asgDao = AssignmentDao.getInstance();
			CmStudentDao studentDao = CmStudentDao.getInstance();

			Document document = new Document();

			//HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
			//footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
			//document.setFooter(footer);

			baos = new ByteArrayOutputStream();

			PdfWriter writer = PdfWriter.getInstance(document, baos);

			int idx = 0;

			Phrase school   = buildLabelContent("School: ", info.getSchoolName());
			Phrase admin    = buildLabelContent("Administrator: ", info.getSchoolUserName());
			String printDate = String.format("%1$tY-%1$tm-%1$td %1$tI:%1$tM %1$Tp", Calendar.getInstance());
			Phrase date     = buildLabelContent("Date: ", printDate);
			Phrase student;
			Phrase showWork;

			List<StudentModelI> smList = studentDao.getStudentSummaries(adminId, studentUids, true);

			document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin()+50, document.bottomMargin());
			document.open();
			document.add(Chunk.NEWLINE);

			for (StudentModelI sm : smList) {
				stuUid = sm.getUid();

				student  = buildLabelContent("Student: ", String.valueOf(sm.getName()));

				String showWorkState = (sm.getSettings().getShowWorkRequired()) ? "REQUIRED" : "OPTIONAL";
				showWork = buildLabelContent("Show Work: ", showWorkState);

				PdfPTable pdfTbl = new PdfPTable(3);
				pdfTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
				//writer.setPageEvent(new HeaderTable(writer, pdfTbl));

				pdfTbl.addCell(school);
				pdfTbl.addCell(admin);
				pdfTbl.addCell(date);
				pdfTbl.addCell(student);
				pdfTbl.addCell(showWork);
				pdfTbl.addCell(new Phrase(" "));

				pdfTbl.setTotalWidth(600.0f);

				document.add(Chunk.NEWLINE);			

				Table tbl = new Table(5);
				tbl.setWidth(100.0f);
				tbl.setBorder(Table.BOTTOM);
				tbl.setBorder(Table.TOP);

				addHeader("Assignment", "30%", tbl);
				addHeader("Due Date", "15%", tbl);
				addHeader("Status", "23%", tbl);
				addHeader("Grade", "7%", tbl);
				addHeader("Problem Status", "25%", tbl);

				tbl.endHeaders();

				int i = 0;
				List<StudentAssignment> saList = asgDao.getAssignmentWorkForStudent(stuUid);

				for (StudentAssignment sa : saList) {
					addCell(sa.getAssignment().getComments(), tbl, ++i);
					addCell(sdFmt.format(sa.getAssignment().getDueDate()), tbl, i);
					addCell(sa.getHomeworkStatus(), tbl, i);
					addCell(sa.getHomeworkGrade(), tbl, i);
					addCell(getStatus(sa), tbl, i);
				}

				document.add(tbl);

				document.add(Chunk.NEWLINE);
				document.add(Chunk.NEWLINE);

				if (idx++ < smList.size()) {
					document.newPage();
				}

			}

			document.close();

		} catch (Exception e) {
			logger.error(String.format("*** Error generating assignment report for aid: %d, uid: %d",
					adminId, stuUid), e);
		}
		return baos;
	}

	private String getStatus(StudentAssignment sa) {
		if (sa.getProblemPendingCount() < 1) {
			return String.format("%d of %d completed", sa.getProblemCompletedCount(), sa.getProblemCount());
		}
		else {
			return String.format("%d of %d completed, %d pending", sa.getProblemCompletedCount(),
					sa.getProblemCount(), sa.getProblemPendingCount());
		}
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

	public void setFilterMap(Map<FilterType, String> filterMap) {
    	this.filterMap = filterMap;
	}
}