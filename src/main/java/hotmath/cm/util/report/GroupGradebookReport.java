package hotmath.cm.util.report;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
	private static final Color BLUE = new Color(0, 0, 150);
	private static final Color RED   = new Color(150, 0, 0);
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	public GroupGradebookReport(String title) {
		this.title = title;
	}
	
	public ByteArrayOutputStream makePdf(int adminId,int groupId) {
		ByteArrayOutputStream baos = null;
		try {
			
	        AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
            if (info == null) return null;

            CmAdminDao caDao = CmAdminDao.getInstance();
            List<GroupInfoModel> groupList = caDao.getActiveGroups(adminId);
            String groupName = null;
            for (GroupInfoModel group : groupList) {
            	if (group.getId() == groupId) {
            		groupName = group.getGroupName();
            		break;
            	}
            }

            AssignmentDao asgDao = AssignmentDao.getInstance();
            
            List<Assignment> assignmentList = asgDao.getAssignments(adminId, groupId);

            // sort Assignments by ascending due date
			Collections.sort(assignmentList, new Comparator<Assignment>() {
				@Override
				public int compare(Assignment asg1, Assignment asg2) {
					Date date1 = asg1.getDueDate();
					Date date2 = asg2.getDueDate();
					if (date1 == null && date2 != null) return -1;
					if (date1 != null && date2 == null) return 1;
					if (date1 == date2) {
						return asg1.getAssignKey() - asg2.getAssignKey();
					}
					return date1.compareTo(date2);
				}
			});

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

			Document document = new Document();
			baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);

			Phrase school   = buildLabelContent("School: ", info.getSchoolName());
			Phrase group    = (groupName != null) ? buildLabelContent("Group: ", groupName) : null;
			String printDate = String.format("%1$tY-%1$tm-%1$td %1$tI:%1$tM %1$Tp", Calendar.getInstance());
			Phrase date     = buildLabelContent("Date: ", printDate);
		    
			//Phrase expires  = buildLabelContent("Expires: ", info.getExpirationDate());

			StringBuilder sb = new StringBuilder();
			sb.append("CM-GroupGradebookReport");

			PdfPTable pdfTbl = new PdfPTable(3);
			pdfTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

			pdfTbl.addCell(school);
			if (group != null) pdfTbl.addCell(group);
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

			int assignmentCount = assignmentList.size();
			int rowsPerStudent  = assignmentCount/10 + ((assignmentCount%10 != 0) ? 1 : 0);
			int numberOfAssignmentColumns = (assignmentCount <= 10) ? assignmentCount + 1 : 10;
			int numberOfColumns = numberOfAssignmentColumns + 1;
			Table tbl = new Table(numberOfColumns);
			tbl.setWidth(100.0f);
			tbl.setBorder(Table.BOTTOM);
			tbl.setBorder(Table.TOP);
			
			addHeader("Student", "20%", tbl);
			
			// use due date for assignment column headers
			int rowNum = 0;
			int prevRowNum = 0;
			int asgNum = 0;
			String[] asgnCols = new String[numberOfColumns-1];
			for (Assignment a : assignmentList) {
				rowNum = asgNum / 10;
				int colNum = asgNum % 10;
				if (rowNum == 0) {
					asgnCols[colNum] = asgnDateFmt.format(a.getDueDate());
				}
				else {
					asgnCols[colNum] = asgnCols[colNum] + NEW_LINE + asgnDateFmt.format(a.getDueDate());
				}
				this.LOGGER.debug(String.format("rowNum: %d, colNum: %d, asgnCols[%d]: %s",
						rowNum, colNum, colNum, asgnCols[colNum]));
				asgNum++;
			}
			for (int i=0; i<numberOfColumns-1; i++) {
				addHeader(asgnCols[i],"8%",tbl);
			}

			tbl.endHeaders();

			List<StudentNameUid> stuList = new ArrayList<StudentNameUid>();
			for (Integer stuId : stuNameMap.keySet()) {
				StudentNameUid snu = new StudentNameUid(stuId, stuNameMap.get(stuId));
				stuList.add(snu);
			}

            // sort Assignments by ascending due date
			Collections.sort(stuList, new Comparator<StudentNameUid>() {
				@Override
				public int compare(StudentNameUid stu1, StudentNameUid stu2) {
					String name1 = stu1.name;
					String name2 = stu2.name;
					if (name1.equals(name2)) {
						return stu1.uid - stu2.uid;
					}
					return name1.compareTo(name2);
				}
			});

			
			int i = 0;
			for (StudentNameUid stu : stuList) {
				int stuId = stu.uid;
				LOGGER.debug("stuId: " + stuId);
				boolean isGray = (i%2 < 1);
				i++;

				addCell(stuNameMap.get(stuId), tbl, isGray);
				LOGGER.debug("stuName: " + stuNameMap.get(stuId));
				
				Map<Integer, StudentAssignment> asgnMap = stuAsgnMap.get(stuId);
				asgNum = 0;
				int colNum = 0;
				for (Assignment a : assignmentList) {
					rowNum = asgNum / numberOfAssignmentColumns;
					colNum = asgNum % numberOfAssignmentColumns;
					asgNum++;
					this.LOGGER.debug(String.format("rowNum: %d, colNum: %d, asgnCols[%d]: %s",
							rowNum, colNum, colNum, asgnCols[colNum]));
					if (rowNum > 0 && colNum == 0) {
						// add empty cell for name column
						addCell("          ", tbl, isGray);
					}
					StudentAssignment sa = asgnMap.get(a.getAssignKey());
					if (sa == null || sa.getHomeworkStatus().equalsIgnoreCase("not started")) {
						addCell("N/A", tbl, isGray);
					}
					else if (sa.getHomeworkStatus().equalsIgnoreCase("in progress")) {
						addCell(sa.getHomeworkGrade(), tbl, isGray, RED);
					}
					else if (sa.getHomeworkStatus().equalsIgnoreCase("ready to grade")) {
						addCell(sa.getHomeworkGrade(), tbl, isGray, BLUE);						
					}
					else {
						addCell(sa.getHomeworkGrade(), tbl, isGray, BLACK);						
					}
				}
				// add empty assignment grades
				for (int idx=++colNum; idx<numberOfAssignmentColumns; idx++) {
					addCell("    ", tbl, isGray);						
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
	
	private void addCell(String content, Table tbl, boolean isGray) throws Exception {
		if (content == null) content = "";
		addCell(content, tbl, isGray, BLACK);
	}
	
	private void addCell(String content, Table tbl, boolean isGray, Color color) throws Exception {
		if (content == null) content = "";
		Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, color));
		c.setTextRise(3.0f);
    	Cell cell = new Cell(c);
		cell.setHeader(false);
		cell.setColspan(1);
		cell.setBorder(0);
		if (isGray) cell.setGrayFill(0.9f);
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

	class StudentNameUid {
	    int uid;
	    String name;
	    
	    StudentNameUid(int uid, String name) {
	    	this.uid = uid;
	    	this.name = name;
	    }
	}
}