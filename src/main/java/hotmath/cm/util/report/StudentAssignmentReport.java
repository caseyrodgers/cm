package hotmath.cm.util.report;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
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
import com.lowagie.text.Image;
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
    private static final String NO_ASSIGNMENTS = "No Assignments";    
    private static final String NO_ASSIGNMENTS_IN_DATE_RANGE = "No Assignments in Date Range";
    
    private SimpleDateFormat sdFmt = new SimpleDateFormat("yyyy-MM-dd");
	
	private static Logger logger = Logger.getLogger(StudentAssignmentReport.class);

	public StudentAssignmentReport(String title) {
		this.title = title;
	}

	public ByteArrayOutputStream makePdf(final Connection conn, int adminId, List<Integer> studentUids) {
		return makePdf(conn, adminId, studentUids, null, null);
	}

	public ByteArrayOutputStream makePdf(final Connection conn, int adminId, List<Integer> studentUids,
			Date fromDate, Date toDate) {
		ByteArrayOutputStream baos = null;

		Integer stuUid = -1;

		try {
			AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
			if (info == null) return null;

            String filterDescription = ReportUtils.getFilterDescription(conn, adminId, CmAdminDao.getInstance(), filterMap);

			reportName = ReportUtils.getReportName("CM-AssignmentReport", (info.getSchoolName()!=null)?info.getSchoolName():"");

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
			Phrase admin    = buildLabelContent("Account login name: ", info.getAdminUserName());
			String printDate = String.format("%1$tY-%1$tm-%1$td %1$tI:%1$tM %1$Tp", Calendar.getInstance());
			
			Phrase dateRange = null;
			if (logger.isDebugEnabled())
   			   logger.debug(String.format("fromDate: %s, toDate: %s",
					((fromDate!=null)?String.format("%1$tY-%1$tm-%1$td", fromDate) : "NULL"),
					((toDate!=null)?String.format("%1$tY-%1$tm-%1$td", toDate) : "NULL")));
			if (fromDate != null && toDate != null) {
    	        String dateRangeStr = String.format("%1$tY-%1$tm-%1$td and %2$tY-%2$tm-%2$td", fromDate, toDate);
			    dateRange = buildLabelContent("Assignments due between: ", dateRangeStr);
			}

			Phrase student;
			Phrase date     = buildLabelContent("Date: ", printDate);

			List<StudentModelI> smList = studentDao.getStudentSummaries(adminId, studentUids, true);

			document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin()-10, document.bottomMargin());
			document.open();
			Image cmLogo = ReportUtils.getCatchupMathLogo();

			for (StudentModelI sm : smList) {
				stuUid = sm.getUid();

				student  = buildTitleContent("Assignments Report for ", sm.getName());

				PdfPTable pdfTbl = new PdfPTable(1);
				pdfTbl.setWidthPercentage(100.0f);
				pdfTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
				//writer.setPageEvent(new HeaderTable(writer, pdfTbl));

				pdfTbl.addCell(student);
				pdfTbl.addCell(new Phrase(" "));

				Phrase group = buildLabelContent("Group: ", sm.getGroup());
				pdfTbl.addCell(group);
				pdfTbl.addCell(school);
				pdfTbl.addCell(admin);
				if (dateRange != null) pdfTbl.addCell(dateRange);
				pdfTbl.addCell(new Phrase(" "));
				pdfTbl.addCell(date);
				pdfTbl.addCell(new Phrase(" "));
				pdfTbl.setSpacingBefore(10);

				document.add(cmLogo);
				document.add(pdfTbl);

				document.add(Chunk.NEWLINE);			
				document.add(Chunk.NEWLINE);

				Table tbl = new Table(3);
				tbl.setWidth(100.0f);
				tbl.setBorder(Table.BOTTOM);
				tbl.setBorder(Table.TOP);

				addHeader("Due Date", "30%", tbl);
				addHeader("Grade", "25%", tbl);
				addHeader("Submitted", "45%", tbl);

				tbl.endHeaders();

				int i = 0;
				int gradedAssignmentCount = 0;
				int gradedAssignmentScore = 0;
				List<StudentAssignment> saList = asgDao.getAssignmentWorkForStudent(stuUid, fromDate, toDate);

				for (StudentAssignment sa : saList) {
					addCell(sdFmt.format(sa.getAssignment().getDueDate()), tbl, ++i);
					addCell(sa.getHomeworkGrade(), tbl, i, sa.isGraded());
					addCell(getStatus(sa), tbl, i);
					if (sa.isGraded()) {
						gradedAssignmentCount++;
						String gradeStr = sa.getHomeworkGrade();
						int grade = 0;
						if (gradeStr.indexOf("%") > 0) {
   						    grade = Integer.parseInt(gradeStr.replaceAll("%", "").trim());
						}
						if (logger.isDebugEnabled())
						    logger.debug(String.format("gradeStr: %s, grade: %d", gradeStr, grade));
						gradedAssignmentScore += grade;
					}
				}

				document.add(tbl);

				if (gradedAssignmentCount > 0) {
					tbl = new Table(1);
					tbl.setWidth(100.0f);
					tbl.setBorder(0);

					addCell(" ", tbl, 1);
					addCell(" ", tbl, 1);
					
					int gradedAssignmentAvg = Math.round((float)gradedAssignmentScore / (float)gradedAssignmentCount);
					String text = String.format("Average for Graded Assignments: %d%s",  gradedAssignmentAvg, "%");
					Chunk c = new Chunk(text, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD|Font.UNDERLINE,
							new Color(0, 0, 200)));
					c.setTextRise(3.0f);
			    	Cell cell = new Cell(c);
					cell.setHeader(false);
					cell.setColspan(1);
					cell.setBorder(0);
					cell.setRowspan(5);
					tbl.addCell(cell);

					addCell(" ", tbl, 1);
					addCell(" ", tbl, 1);

					document.add(tbl);
				}
				
				else if (saList.size() == 0) {
					tbl = new Table(3);
					tbl.setWidth(100.0f);
					tbl.setBorder(0);

					addCell(" ", tbl, 1);
					addCell(" ", tbl, 1);
					addCell(" ", tbl, 1);

					addCell(" ", tbl, 1);
					String msg = (dateRange != null) ? NO_ASSIGNMENTS_IN_DATE_RANGE : NO_ASSIGNMENTS;
					Chunk c = new Chunk(msg, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0, 0, 0)));
					c.setTextRise(3.0f);
			    	Cell cell = new Cell(c);
					cell.setHeader(false);
					cell.setColspan(1);
					cell.setBorder(0);
					cell.setRowspan(5);
					tbl.addCell(cell);
					addCell(" ", tbl, 1);

					addCell(" ", tbl, 1);
					addCell(" ", tbl, 1);
					addCell(" ", tbl, 1);

					document.add(tbl);
				}
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
			return String.format("%d of %d submitted", sa.getProblemCompletedCount(), sa.getProblemCount());
		}
		else {
			return String.format("%d of %d submitted, %d pending", sa.getProblemCompletedCount(),
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

	private Phrase buildTitleContent(String label, String value) {
		if (value == null || value.trim().length() == 0) value = "";
		Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0, 0, 0))));
		Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0, 0, 0))));
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
		addCell(content, tbl, rowNum, false);
	}

	private void addCell(String content, Table tbl, int rowNum, boolean isGraded) throws Exception {
		if (content == null) content = "";
		Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8,
				(isGraded==true)?Font.BOLD|Font.UNDERLINE:Font.NORMAL,
				(isGraded==true)?new Color(0, 0, 200):new Color(0, 0, 0)));
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