package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.server.model.CmReportCardDao;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * StudentReportCard generates a PDF that represents a student's status
 * 
 * @author bob
 * 
 */

public class StudentReportCard {

	private static final Logger LOGGER = Logger.getLogger(StudentReportCard.class);
	
    static Map<String, String> labelMap;
    
    static List<String> orderList;

    static {
        labelMap = new HashMap<String, String>();

        labelMap.put("login",      "Login Days: ");
        labelMap.put("activetime", "Active Time: ");
        labelMap.put("activity",   "Learning Activities: ");
        labelMap.put("review",     "Lessons Completed: ");
        labelMap.put("practice",   "Required Practice Problems: ");
        labelMap.put("flashcard",  "Flashcard Sessions: ");
        labelMap.put("video",      "Videos: ");
        labelMap.put("game",       "Games: ");
        
        orderList = new ArrayList<String>();
        
        orderList.add("login");
        orderList.add("activetime");
        orderList.add("review");
        orderList.add("practice");
        orderList.add("flashcard");
        orderList.add("game");
        orderList.add("activity");
        orderList.add("video");
        
    }

    private String reportName;

    private Date _fromDate;
    private Date _toDate;

    public StudentReportCard() {
    }

    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(final Connection conn, String reportId, Integer adminId,
    		Date fromDate, Date toDate) throws Exception {
        ByteArrayOutputStream baos = null;

        _fromDate = fromDate;
        _toDate = toDate;

        List<Integer> studentUids = (List<Integer>) CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);

        CmAdminDao adminDao = CmAdminDao.getInstance();
        AccountInfoModel info = adminDao.getAccountInfo(adminId);
        if (info == null) {
            LOGGER.warn("*** Account info is NULL for adminId: " + adminId);
            return null;
        }

        CmReportCardDao rcDao = CmReportCardDao.getInstance();
        CmStudentDao studentDao = CmStudentDao.getInstance();

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        long startTime = System.currentTimeMillis();
        List<StudentModelI> smList = studentDao.getStudentSummaries(adminId, studentUids, true);
        if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug(String.format("Student Summaries time: %d msec", System.currentTimeMillis() - startTime));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CM-ReportCard");
        String token = (smList.size() < 2) ? smList.get(0).getName() : info.getSchoolName();
        sb.append("-").append(token.replaceAll(" ", "").replaceAll("/|#", ""));
        this.reportName = sb.toString();

        Phrase school = ReportUtils.buildParagraphLabel("School: ", info.getSchoolName());

        /*
         * don't include page number footer
         */
        //HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
        //footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
        //document.setFooter(footer);

		document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin()-10, document.bottomMargin());
        document.open();

        Image cmLogo = ReportUtils.getCatchupMathLogo();
        document.add(cmLogo);
        document.add(Chunk.NEWLINE);
        Paragraph p = ReportUtils.buildTitle("Report Card");
        document.add(p);

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);

    	int idx = 0;
    	
        for (StudentModelI sm : smList) {

        	startTime = System.currentTimeMillis();
            StudentReportCardModelI rc = rcDao.getStudentReportCard(sm.getUid(), fromDate, toDate);
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug(String.format("Report Card Dao uid: %d, time: %d msec", sm.getUid(), System.currentTimeMillis() - startTime));
            }

        	addStudentInfo(school, sm, rc, document);
        	
        	addProgramInfo(rc, sm, document);

        	addQuizInfo(rc, document);

        	addAssignmentInfo(sm.getUid(), document, fromDate, toDate);

        	if (rc.getResourceUsage() != null && rc.getResourceUsage().size() > 0) {
        		addResourceUsage(rc, document);
        	}

        	if (rc.getPrescribedLessonList() != null && rc.getPrescribedLessonList().size() > 0) {
        		addLessons(rc, sm.getProgram().getCustom().isCustom(), document);
        	}

        	document.add(Chunk.NEWLINE);
        	document.add(Chunk.NEWLINE);
        	
        	if (idx++ < smList.size()) {
        		document.newPage();
        	}
        }

        document.close();

        return baos;
    }

	private void addAssignmentInfo(int userId, Document document, Date fromDate, Date toDate) throws Exception {
        AssignmentDao asgDao = AssignmentDao.getInstance();

        List<StudentAssignmentInfo> list = asgDao.getCompletedAssignmentsForUserDateRange(userId, fromDate, toDate);

        if (list.size() < 1) return;

        /*
          * calculate number of assignments and average score
         */
        int totalScore = 0;
        for (StudentAssignmentInfo sai : list) {
        	String score = sai.getScore();
        	int numericScore = 0;
        	if ("-".equals(score.trim()) == false) {
        		int offset = score.indexOf("%");
        		numericScore = (offset > 0) ?
        			Integer.parseInt(score.substring(0, offset)) : Integer.parseInt(score);
        	}
        	totalScore += numericScore;
        }
        int avgScore = Math.round(((float)totalScore / (float) list.size()));

        PdfPTable assignmentTbl = new PdfPTable(1);
        assignmentTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Phrase assignments = buildSectionLabel("Graded Assignments");
		Paragraph numberOfAssignments = buildSectionContent("Number of Assignments: ", String.valueOf(list.size()), true);
		Paragraph averageGrade = buildSectionContent("Average grade: ", String.valueOf(avgScore), true);
        assignmentTbl.addCell(assignments);
		assignmentTbl.addCell(numberOfAssignments);
		assignmentTbl.addCell(averageGrade);

        assignmentTbl.setWidthPercentage(100.0f);
        assignmentTbl.setSpacingBefore(20.0f);

		document.add(assignmentTbl);
        document.add(Chunk.NEWLINE);
	}

	private void addStudentInfo(Phrase school, StudentModelI sm,
			StudentReportCardModelI rc, Document document) throws DocumentException {

		Phrase group = ReportUtils.buildParagraphLabel("Group: ", sm.getGroup());
		String printDate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());
		Phrase date = ReportUtils.buildParagraphLabel("Today's Date: ", printDate);

		Date actDate = rc.getFirstActivityDate();
		String activityDate = (actDate != null) ? String.format("%1$tY-%1$tm-%1$td", actDate) : " ";
		Phrase firstActivityDate = ReportUtils.buildParagraphLabel("First activity: ", activityDate);

		actDate = rc.getLastActivityDate();
		activityDate = (actDate != null) ? String.format("%1$tY-%1$tm-%1$td", actDate) : " ";
		Phrase lastActivityDate = ReportUtils.buildParagraphLabel("Last activity: ", activityDate);

		Phrase student = ReportUtils.buildParagraphLabel("Student: ", sm.getName());

		Phrase dateRange = null;
		if (_fromDate != null && _toDate != null) {
			String dateStr = String.format("%1$tY-%1$tm-%1$td - %2$tY-%2$tm-%2$td", _fromDate, _toDate);
			dateRange = ReportUtils.buildParagraphLabel("Date range: ", dateStr);
		}

		int numberOfColumns = 2;
		PdfPTable pdfTbl = new PdfPTable(numberOfColumns);
		pdfTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		pdfTbl.setWidthPercentage(100.0f);

		pdfTbl.addCell(student);
		pdfTbl.addCell(date);

		pdfTbl.addCell(group);
		pdfTbl.addCell(" ");

		pdfTbl.addCell(school);
		if (dateRange != null) {
			pdfTbl.addCell(dateRange);
		}
		else {
			pdfTbl.addCell(" ");
		}

		pdfTbl.addCell(" ");
		pdfTbl.addCell(" ");

		pdfTbl.addCell(firstActivityDate);
		pdfTbl.addCell(lastActivityDate);

		pdfTbl.setSpacingBefore(10);
    	document.add(pdfTbl);
	}
    
    public String getReportName() {
    	return reportName;
    }

    private void addLessons(StudentReportCardModelI rc, boolean isCustomProgram, Document document) throws DocumentException {
        PdfPTable lessonTbl = new PdfPTable(1);
        lessonTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        //String text = (isCustomProgram) ? "Assigned Lessons Completed" : "Prescribed Lessons Completed";
        Phrase lesson = buildSectionLabel("Lessons Completed");
        lessonTbl.addCell(lesson);

        StringBuilder sb = new StringBuilder();
        List<String> list = rc.getPrescribedLessonList();
        for (String lsn : list) {
            sb.append(lsn).append(", ");
        }
        String allLessons = sb.toString().substring(0, sb.length() - 2);
        Paragraph p = new Paragraph();
        p.setFont(FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new Color(0, 0, 0)));
        p.add(allLessons);
        lessonTbl.addCell(p);
        lessonTbl.setWidthPercentage(100.0f);
        lessonTbl.setSpacingBefore(20.0f);
        document.add(lessonTbl);
        document.add(Chunk.NEWLINE);
    }

    private void addResourceUsage(StudentReportCardModelI rc, Document document) throws DocumentException {
        PdfPTable usageTbl = new PdfPTable(1);
        usageTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Phrase usage = buildSectionLabel("Totals");
        usageTbl.addCell(usage);

        Map<String, Integer> map = rc.getResourceUsage();

        for (String key : orderList) {
            String label = labelMap.get(key);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("key: " + key + ", label: " + label);
            Integer count = (map.get(key) == null) ? 0 : map.get(key);
            usage = buildSectionContent(label, String.valueOf(count), true);
            usageTbl.addCell(usage);
        }
        usageTbl.setWidthPercentage(100.0f);
        usageTbl.setSpacingBefore(20.0f);
        document.add(usageTbl);
        document.add(Chunk.NEWLINE);
    }

    private void addQuizInfo(StudentReportCardModelI rc, Document document) throws DocumentException {
        PdfPTable quizTbl = new PdfPTable(1);
        quizTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        Phrase quizLabel = buildSectionLabel("Quizzes");
        Paragraph totalQuizzes = buildSectionContent("Attempted: ", (rc.getQuizCount() != null) ? rc.getQuizCount().toString() : "0", true);
        Paragraph passedQuizzes = buildSectionContent("Passed: ", (rc.getQuizPassCount() != null) ? rc.getQuizPassCount().toString() : "0", true);
        String average = (rc.getQuizAvgPassPercent() != null) ? rc.getQuizAvgPassPercent() + "%" : "n/a";
        Paragraph avgScore = buildSectionContent("Avg score of passed quizzes: ", average, true);

        quizTbl.addCell(quizLabel);
        quizTbl.addCell(totalQuizzes);
        quizTbl.addCell(passedQuizzes);
        quizTbl.addCell(avgScore);
        quizTbl.setWidthPercentage(100.0f);
        quizTbl.setSpacingBefore(20.0f);
        document.add(quizTbl);
        document.add(Chunk.NEWLINE);
    }

    private void addProgramInfo(StudentReportCardModelI rc, StudentModelI sm, Document document) throws DocumentException {
        PdfPTable progTbl = new PdfPTable(1);
        progTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        String initialProgDesc = rc.getInitialProgramName();

        /*
         * don't include initial status for now
         * if (initialProgDesc != null) {
         *     initialProgDesc += (rc.getInitialProgramStatus() != null) ?
         *         " " + rc.getInitialProgramStatus() : "";
         * }
         */

        StringBuilder sb = new StringBuilder();
        sb.append("Initial program");
        Date assignedDate = rc.getInitialProgramDate();
        if (assignedDate != null) {
            sb.append(" assigned ").append(String.format("%1$tY-%1$tm-%1$td", assignedDate));
        }
        sb.append(": ");
        Phrase initialProg = ReportUtils.buildParagraphLabel(sb.toString(), initialProgDesc);

        Boolean isCurrentProgCustom = sm.getProgram().getCustom().isCustom();
        
        String lastProgDesc = rc.getLastProgramName();
        
        if (isCurrentProgCustom == false) {
            if (lastProgDesc != null) {
                lastProgDesc += (rc.getLastProgramStatus() != null) ? ", " + rc.getLastProgramStatus() : "";
            }
        }
        StringBuilder sBuff = new StringBuilder((rc.getReportStartDate() == null) ? "Current " : "Final ");

        if (isCurrentProgCustom)
        	sBuff.append("program: ");
        else 
        	sBuff.append("program & section: ");

        Phrase lastProg = ReportUtils.buildParagraphLabel(sBuff.toString(), lastProgDesc);

        // don't include assigned date for current (last) program
        // assignedDate = rc.getLastProgramDate();
        // String lastDate = (assignedDate != null) ?
        // String.format("%1$tY-%1$tm-%1$td", assignedDate) : " ";

        progTbl.addCell(initialProg);
        progTbl.addCell(lastProg);
        progTbl.setWidthPercentage(100.0f);
        progTbl.setSpacingBefore(20.0f);

        document.add(progTbl);
        document.add(Chunk.NEWLINE);
    }

	private void addAssignmentInfo(int uid, AssignmentDao asgDao, Date fromDate, Date toDate, Document document) throws Exception {
    	List<StudentAssignment> asgList = asgDao.getAssignmentWorkForStudent(uid, fromDate, toDate);

    	int totalCount = 0;
    	int gradedCount = 0;
    	int correctCount = 0;
    	int incorrectCount = 0;
    	int pendingCount = 0;
    	int notViewedCount = 0;
    	int viewedCount = 0;
    	int asgCount = 0;

    	int completedCount = 0;
        int notStartedCount = 0;
        int readyToGradeCount = 0;
        int inProgressCount = 0;
    	List<Integer> percentList = new ArrayList<Integer>();

    	for (StudentAssignment asg : asgList) {
    		
    		if (asgCount > 0) {
    			
    			if (gradedCount == totalCount) {
    				completedCount++;
                    int percent = Math.round((((float)correctCount / (float)totalCount)) * 100.0f);
                    percentList.add(percent);
    			}
    			//TODO: should this include viewedCount?
    			else if ((correctCount + incorrectCount + pendingCount) == totalCount) {
    				readyToGradeCount++;
    			}
    			else if (notViewedCount == totalCount) {
    				notStartedCount++;
    			}
    			else if (pendingCount > 0 || viewedCount > 0) {
    				inProgressCount++;
    			}
    			
    			// reset counts
    		    correctCount = 0;
    		    gradedCount = 0;
    		    incorrectCount = 0;
    		    notViewedCount = 0;
    		    pendingCount = 0;
    		    totalCount = 0;
    		    viewedCount = 0;
    		}
    		asgCount++;
    		
    		List<StudentProblemDto> pList = asg.getStudentStatuses().getAssigmentStatuses();

    		for (StudentProblemDto probDto : pList) {
        		totalCount++;
        		String probStatus = probDto.getStatus().trim();
        		if ("not viewed".equalsIgnoreCase(probStatus)) {
        			notViewedCount++;
        			continue;
        		}
        		//TODO: is "answered" an actual status?
        		if ("answered".equalsIgnoreCase(probStatus) ||
                    "correct".equalsIgnoreCase(probStatus)  ||
                    "incorrect".equalsIgnoreCase(probStatus)) {
        			gradedCount += (probDto.isGraded()) ? 1 : 0;
        			correctCount += ("correct".equalsIgnoreCase(probStatus)) ? 1 : 0;
        			incorrectCount += ("incorrect".equalsIgnoreCase(probStatus)) ? 1 : 0;
        			continue;
        		}
        		if ("viewed".equalsIgnoreCase(probStatus)) {
        			viewedCount++;
        		}
        		else if ("pending".equalsIgnoreCase(probStatus)) {
        			pendingCount++;
        		}
    		}
    	}

    	if (asgCount > 0) {

    		if (gradedCount == totalCount) {
    			completedCount++;
    			int percent = Math.round((((float)correctCount / (float)totalCount)) * 100.0f);
    			percentList.add(percent);
    		}
    		//TODO: should this include viewedCount?
    		else if ((correctCount + incorrectCount + pendingCount) == totalCount) {
    			readyToGradeCount++;
    		}
    		else if (notViewedCount == totalCount) {
    			notStartedCount++;
    		}
    		else if (pendingCount > 0 || viewedCount > 0) {
    			inProgressCount++;
    		}

    		PdfPTable assignTbl = new PdfPTable(1);
    		assignTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
    		Phrase label = buildSectionLabel("Assignments");
    		Paragraph total = buildSectionContent("Assigned: ", String.valueOf(asgCount), true);
    		Paragraph completed = buildSectionContent("Completed: ", String.valueOf(completedCount), true);
    		Paragraph pending = buildSectionContent("Ready to Grade: ", String.valueOf(readyToGradeCount), true);
    		Paragraph inProgress = buildSectionContent("In Progress: ", String.valueOf(inProgressCount), true);
    		Paragraph notStarted = buildSectionContent("Not started: ", String.valueOf(notStartedCount), true);

    		String grade = "n/a";
    		if (percentList.size() > 0 ) {
    			int totalPercent = 0;
    			for (int percent : percentList) {
    				totalPercent += percent;
    			}
    			int percent = Math.round( (float)totalPercent / (float)percentList.size() );
    			grade = String.format("%d%s", percent, "%");
    		}
    		Paragraph avgScore = buildSectionContent("Average Grade: ", grade, true);

    		assignTbl.addCell(label);
    		assignTbl.addCell(total);
    		assignTbl.addCell(completed);
    		assignTbl.addCell(avgScore);
    		assignTbl.addCell(pending);
    		assignTbl.addCell(inProgress);
    		assignTbl.addCell(notStarted);

    		assignTbl.setWidthPercentage(100.0f);
    		assignTbl.setSpacingBefore(20.0f);
    		document.add(assignTbl);
    		document.add(Chunk.NEWLINE);
    	}
		
	}

	private Phrase buildSectionLabel(String label) {
        Chunk chunk = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0, 0, 0)));
        chunk.setUnderline(0.5f, -3f);
        Phrase phrase = new Phrase(chunk);
        return phrase;
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

}