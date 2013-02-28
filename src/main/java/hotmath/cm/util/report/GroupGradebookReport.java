package hotmath.cm.util.report;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

public class GroupGradebookReport {
	
	private String reportName;
    private String title;
    
    private SimpleDateFormat sdFmt = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat asgnDateFmt = new SimpleDateFormat("MM-dd");
	
	private static final Logger LOGGER = Logger.getLogger(GroupGradebookReport.class);

	private static final Color BLACK = new Color(0, 0, 0);
	private static final Color GREEN = new Color(0, 150, 0);
	private static final Color RED   = new Color(150, 0, 0);
	
	public GroupGradebookReport(String title) {
		this.title = title;
	}
	
	public ByteArrayOutputStream makePdf(final Connection conn, int adminId,int groupId) {
		ByteArrayOutputStream baos = null;
		try {
			
	        AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
            if (info == null) return null;

            AssignmentDao asgDao = AssignmentDao.getInstance();
            
            List<Assignment> assignmentList = asgDao.getAssignments(adminId, groupId);

            //TODO: apply assignment count limit of?

            Map<Integer, List<StudentAssignment>> saMap = new HashMap<Integer, List<StudentAssignment>>();
            
            for (Assignment a : assignmentList) {
                List<StudentAssignment> saList = asgDao.getAssignmentGradeBook(a.getAssignKey());
                saMap.put(a.getAssignKey(), saList);
            }

            // pivot on student
            Map<Integer, String> stuNameMap = new HashMap<Integer, String>();
            Map<Integer, Map<Integer, StudentAssignment>> stuAsgnMap = new HashMap<Integer, Map<Integer, StudentAssignment>>();
            Set<Integer> asgnSet = new HashSet<Integer>();

            for (Assignment a : assignmentList) {
            	List<StudentAssignment> saList = saMap.get(a.getAssignKey());
            	for (StudentAssignment sa : saList) {
            		Integer stuId = sa.getUid();
            	    stuNameMap.put(stuId, sa.getStudentName());
            	    Map<Integer, StudentAssignment> asgnMap = stuAsgnMap.get(stuId);
            	    if (asgnMap == null) {
            	    	asgnMap = new HashMap<Integer, StudentAssignment>();
            	    	stuAsgnMap.put(stuId, asgnMap);
            	    }
            	    asgnMap.put(sa.getAssignment().getAssignKey(), sa);
            	    asgnSet.add(sa.getAssignment().getAssignKey());
            	}
            }

            for (Integer stuId : stuNameMap.keySet()) {
            	Map<Integer, StudentAssignment> asgnMap = stuAsgnMap.get(stuId);

            	for (Integer asgnKey : asgnMap.keySet()) {
            		StudentAssignment sa = asgnMap.get(asgnKey);
            	}
            	
            }
						
			Document document = new Document();
			baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);

			Phrase school   = buildLabelContent("School: ", info.getSchoolName());
			Phrase admin    = buildLabelContent("Administrator: ", info.getSchoolUserName());
			String printDate = String.format("%1$tY-%1$tm-%1$td %1$tI:%1$tM %1$Tp", Calendar.getInstance());
			Phrase date     = buildLabelContent("Date: ", printDate);
		    
			//Phrase expires  = buildLabelContent("Expires: ", info.getExpirationDate());

			StringBuilder sb = new StringBuilder();
			sb.append("CM-GroupGradebookReport");

			PdfPTable pdfTbl = new PdfPTable(3);
			pdfTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

			pdfTbl.addCell(school);
			pdfTbl.addCell(admin);
			pdfTbl.addCell(date);
			
			pdfTbl.addCell(new Phrase(" "));

			pdfTbl.setTotalWidth(600.0f);

			writer.setPageEvent(new HeaderTable(writer, pdfTbl));

			HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
			footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
			document.setFooter(footer);

			document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin()+50, document.bottomMargin());
			document.open();
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);			

			Table tbl = new Table(5);
			tbl.setWidth(100.0f);
			tbl.setBorder(Table.BOTTOM);
			tbl.setBorder(Table.TOP);
			
			addHeader("Student", "20%", tbl);
			
			// use due date for assignment column headers
			// TODO: limit to ?
			for (Assignment a : assignmentList) {
				Date due = a.getDueDate();
				addHeader(asgnDateFmt.format(a.getDueDate()),"10%",tbl);
			}

			tbl.endHeaders();

			int i = 0;
			for (Integer stuId : stuNameMap.keySet()) {
				addCell(stuNameMap.get(stuId), tbl, ++i);
				Map<Integer, StudentAssignment> asgnMap = stuAsgnMap.get(stuId);
				for (Assignment a : assignmentList) {
					StudentAssignment sa = asgnMap.get(a.getAssignKey());
					if (sa == null || sa.getHomeworkStatus().equalsIgnoreCase("not started")) {
						addCell("N/A", tbl, i);
					}
					else if (sa.getHomeworkStatus().equalsIgnoreCase("in progress")) {
						addCell("<"+sa.getHomeworkGrade()+">", tbl, i, RED);
					}
					else if (sa.getHomeworkStatus().equalsIgnoreCase("ready to grade")) {
						addCell("{"+sa.getHomeworkGrade()+"}", tbl, i, GREEN);						
					}
					else {
						addCell(sa.getHomeworkGrade(), tbl, i, BLACK);						
					}
				}

			}

			document.add(tbl);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			document.close();

		} catch (Exception e) {
			LOGGER.error(String.format("*** Error generating gradebook report for adminId: %d, groupId: %d",
				adminId, groupId), e);
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
		Chunk c = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, BLACK));
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
		addCell(content, tbl, rowNum, BLACK);
	}
	
	private void addCell(String content, Table tbl, int rowNum, Color color) throws Exception {
		if (content == null) content = "";
		Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, color));
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
		// TODO Auto-generated method stub
		
	}
}