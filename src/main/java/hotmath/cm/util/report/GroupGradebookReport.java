package hotmath.cm.util.report;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.InformationOnlyException;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
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

public class GroupGradebookReport {
	
	private String reportName;
    private String title;
    
    private SimpleDateFormat asgnDateFmt = new SimpleDateFormat("MM-dd");
	
	private static final Logger LOGGER = Logger.getLogger(GroupGradebookReport.class);

	private static final Color BLACK = new Color(0, 0, 0);
	private static final Color BLUE = new Color(0, 0, 150);
	private static final Color RED   = new Color(150, 0, 0);
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final int MAX_ASSIGNMENT_COLS = 10;
	private static final int NAME_COL_SPAN = 2;
	
	public GroupGradebookReport(String title) {
		this.title = title;
	}
	
	public ByteArrayOutputStream makePdf(int adminId, int groupId, Date fromDate, Date toDate) throws Exception {
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
            
            List<Assignment> assignmentList = (fromDate != null && toDate != null) ?
            		asgDao.getAssignments(adminId, groupId, fromDate, toDate) :
            		asgDao.getAssignments(adminId, groupId);

            if (assignmentList == null || assignmentList.size() == 0) {
            	String msg = String.format("Group, %s, has no assignments in Date Range: %2$ty-%2$tm-%2$td to %3$ty-%3$tm-%3$td",
            		groupName, fromDate, toDate);
            	throw new InformationOnlyException(msg);
            }

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

            Map<Integer, String> stuNameMap = new HashMap<Integer, String>();
            Map<Integer, Map<Integer, StudentAssignment>> stuAsgnMap = new HashMap<Integer, Map<Integer, StudentAssignment>>();
            Set<Integer> asgnSet = new HashSet<Integer>();

            // pivot on student
            pivotOnStudent(assignmentList, saMap, stuNameMap, stuAsgnMap, asgnSet);

			Document document = new Document();

			baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			String dateRange = null;
			if (fromDate != null && toDate != null) {
				dateRange = String.format("Date range: %1$tY-%1$tm-%1$td to %2$tY-%2$tm-%2$td", fromDate, toDate);
			}
		    
			StringBuilder sb = new StringBuilder();
			sb.append("CM-GroupGradebookReport");

			title = String.format("Group Gradebook Report for %s", groupName);
			HeaderFooter header = ReportUtils.getBriefGroupReportHeader(info, null, dateRange, title);
			HeaderFooter footer = ReportUtils.getFooter();
			document.setHeader(header);
			document.setFooter(footer);

			document.open();

			int assignmentCount = assignmentList.size();
			int numberOfAssignmentColumns = (assignmentCount <= MAX_ASSIGNMENT_COLS) ? assignmentCount : MAX_ASSIGNMENT_COLS;
			int numberOfColumns = NAME_COL_SPAN + MAX_ASSIGNMENT_COLS + 1;

    		Table tbl = new Table(numberOfColumns);
			tbl.setWidth(100.0f);
			tbl.setBorder(Table.BOTTOM);
			tbl.setBorder(Table.TOP);
			
			String[] asgnCols = addHeaders(assignmentList, numberOfColumns, numberOfAssignmentColumns, tbl);

			List<StudentNameUid> stuList = sortStudentsByName(stuNameMap);

			Map<Integer, String> avgMap = calcStudentsAvgScores(stuNameMap, stuAsgnMap, assignmentList);
			
			fillTable(assignmentList, stuNameMap, stuAsgnMap, numberOfAssignmentColumns, tbl, asgnCols, stuList, avgMap);

			document.add(tbl);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			document.close();

		} catch (Exception e) {
			LOGGER.error(String.format("*** Error generating gradebook report for adminId: %d, groupId: %d",
				adminId, groupId), e);
			if (e instanceof InformationOnlyException) throw e;
		}
		return baos;
	}

	private void fillTable(List<Assignment> assignmentList,
			Map<Integer, String> stuNameMap,
			Map<Integer, Map<Integer, StudentAssignment>> stuAsgnMap,
			int numberOfAssignmentColumns, Table tbl, String[] asgnCols,
			List<StudentNameUid> stuList, Map<Integer, String> avgMap)
			throws Exception {
		int i = 0;
		for (StudentNameUid stu : stuList) {
			int stuId = stu.uid;
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(String.format("stuId: %d, name: %s, avg: %s", stuId, stuNameMap.get(stuId), avgMap.get(stuId)));
			boolean isGray = (i%2 < 1);
			i++;

			addCell(stuNameMap.get(stuId).toUpperCase(), NAME_COL_SPAN, tbl, isGray);
			addCell(avgMap.get(stuId), 1, tbl, isGray);
			
			Map<Integer, StudentAssignment> asgnMap = stuAsgnMap.get(stuId);
			int asgNum = 0;
			int colNum = 0;
			for (Assignment a : assignmentList) {
				int rowNum = asgNum / numberOfAssignmentColumns;
				colNum = asgNum % numberOfAssignmentColumns;
				asgNum++;
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(String.format("rowNum: %d, colNum: %d, asgnCols[%d]: %s",
						rowNum, colNum, colNum, asgnCols[colNum]));
				if (rowNum > 0 && colNum == 0) {
					// add empty cells for name and average columns
					addCell("          ", NAME_COL_SPAN, tbl, isGray);
					addCell("   ", 1, tbl, isGray);
				}
				StudentAssignment sa = asgnMap.get(a.getAssignKey());
				String homeworkGrade = sa.getHomeworkGrade();
				if (sa == null || sa.getHomeworkStatus().equalsIgnoreCase("not started")) {
					addCell("N/A", 1, tbl, isGray);
				}
				else if (sa.getHomeworkStatus().equalsIgnoreCase("in progress")) {
					homeworkGrade = ("-".equals(homeworkGrade.trim())) ? "0%" : homeworkGrade;
					addCell(homeworkGrade, 1, tbl, isGray, RED);
				}
				else if (sa.getHomeworkStatus().equalsIgnoreCase("ready to grade")) {
					addCell(homeworkGrade, 1, tbl, isGray, BLUE);						
				}
				else {
					homeworkGrade = ("-".equals(homeworkGrade.trim())) ? "N/A" : homeworkGrade;
					addCell(homeworkGrade, 1, tbl, isGray, BLACK);						
				}
			}
			// add empty assignment grades
			for (int idx=++colNum; idx<MAX_ASSIGNMENT_COLS; idx++) {
				addCell("   ", 1, tbl, isGray);						
			}

		}
	}

	private void pivotOnStudent(List<Assignment> assignmentList,
			Map<Integer, List<StudentAssignment>> saMap,
			Map<Integer, String> stuNameMap,
			Map<Integer, Map<Integer, StudentAssignment>> stuAsgnMap,
			Set<Integer> asgnSet) {
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
	}

	private String[] addHeaders(List<Assignment> assignmentList,
			int numberOfColumns, int numberOfAssignmentColumns, Table tbl) throws Exception {
		addHeader("Student", 10.0f, 1, tbl);
		addHeader("  ", 10.0f, 1, tbl);
		addHeader("AVG", 8.0f, 1, tbl);
		
		// use due date for assignment column headers
		int rowNum = 0;
		int asgNum = 0;
		String[] asgnCols = new String[numberOfColumns-1];
		for (Assignment a : assignmentList) {
			rowNum = asgNum / MAX_ASSIGNMENT_COLS;
			int colNum = asgNum % MAX_ASSIGNMENT_COLS;
			if (rowNum == 0) {
				asgnCols[colNum] = asgnDateFmt.format(a.getDueDate());
			}
			else {
				asgnCols[colNum] = asgnCols[colNum] + NEW_LINE + asgnDateFmt.format(a.getDueDate());
			}
			asgNum++;
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("numberOfColumns: " + numberOfColumns);
		for (int i=0; i<numberOfAssignmentColumns; i++) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(String.format("asgnCols[%d]: %s", i, asgnCols[i]));
			addHeader(asgnCols[i],8.0f,1,tbl);
		}
		for (int i=0; i<(MAX_ASSIGNMENT_COLS-numberOfAssignmentColumns); i++) {
			addHeader(" ",8.0f,1,tbl);
		}

		tbl.endHeaders();
		return asgnCols;
	}

	private Phrase buildLabelContent(String label, String value) {
		if (value == null || value.trim().length() == 0) value = "NONE";
		Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(0, 0, 0))));
		Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(0, 0, 0))));
		phrase.add(content);
		return phrase;
	}

	private void addHeader(String label, float width, int colSpan, Table tbl) throws Exception {
		Chunk c = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, BLACK));
		c.setTextRise(4.0f);
		Cell cell = new Cell(c);
		cell.setWidth(width);
		cell.setColspan(colSpan);
		
		cell.setHeader(true);
		cell.setColspan(1);
		cell.setBorder(Cell.BOTTOM);
		tbl.addCell(cell);
	}
	
	private void addCell(String content, int colSpan, Table tbl, boolean isGray) throws Exception {
		if (content == null) content = "";
		addCell(content, colSpan, tbl, isGray, BLACK);
	}
	
	private void addCell(String content, int colSpan, Table tbl, boolean isGray, Color color) throws Exception {
		if (content == null) content = "";
		Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, color));
		c.setTextRise(3.0f);
    	Cell cell = new Cell(c);
		cell.setHeader(false);
		cell.setColspan(colSpan);
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

    private List<StudentNameUid> sortStudentsByName(Map<Integer, String> stuNameMap) {
		List<StudentNameUid> stuList = new ArrayList<StudentNameUid>();
		for (Integer stuId : stuNameMap.keySet()) {
			StudentNameUid snu = new StudentNameUid(stuId, stuNameMap.get(stuId));
			stuList.add(snu);
		}

        // sort Students by ascending name
		Collections.sort(stuList, new Comparator<StudentNameUid>() {
			@Override
			public int compare(StudentNameUid stu1, StudentNameUid stu2) {
				String name1 = stu1.name;
				String name2 = stu2.name;
				if (name1.equalsIgnoreCase(name2)) {
					return stu1.uid - stu2.uid;
				}
				return name1.compareToIgnoreCase(name2);
			}
		});
        return stuList;
    }

	// calculate students' Average scores
    private Map<Integer, String> calcStudentsAvgScores(Map<Integer, String> stuNameMap, Map<Integer, Map<Integer,StudentAssignment>> stuAsgnMap, List<Assignment> assignmentList) {
    	Map<Integer, String> avgMap = new HashMap<Integer,String>();
    	for (Integer stuId : stuNameMap.keySet()) {
    		int asgCount = 0;
    		int totalPercent = 0;
    		Map<Integer, StudentAssignment> asgnMap = stuAsgnMap.get(stuId);
    		for (Assignment a : assignmentList) {
    			StudentAssignment sa = asgnMap.get(a.getAssignKey());
    			if (sa == null) {
    				LOGGER.warn("No Assignment for key: " + a.getAssignKey());
    				continue;
    			}
    			String homeworkGrade = sa.getHomeworkGrade();
    			int offset = homeworkGrade.indexOf("%");
    			if (offset > 0) {
    				totalPercent += Integer.parseInt(homeworkGrade.substring(0, offset).trim());
    			}
    			else {
    				if ("-".equals(homeworkGrade.trim()) == false &&
    					"N/A".equals(homeworkGrade.trim()) == false) {
    					totalPercent += Integer.parseInt(homeworkGrade.trim());
    				}
    			}
    			asgCount++;
    		}
    		String average;
    		if (asgCount > 0)
    			average = String.format("  %d%s", Math.round((float)totalPercent / (float)asgCount), "%");
    		else
    			average = "  0%";
    		avgMap.put(stuId, average);
    	}
    	return avgMap;
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