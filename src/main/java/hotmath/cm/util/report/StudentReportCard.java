package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.server.model.CmReportCardDao;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
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
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * StudentReportCard generates a PDF that represents a student's status
 * 
 */

public class StudentReportCard {

	private static final Logger logger = Logger.getLogger(StudentReportCard.class);
	
    static Map<String, String> labelMap;
    
    static List<String> orderList;

    static {
        labelMap = new HashMap<String, String>();

        labelMap.put("login", "Logins: ");
        labelMap.put("activity", "Learning Activities: ");
        labelMap.put("cmextra", "Extra Practice Problems: ");
        labelMap.put("review", "Prescribed Lessons Reviewed: ");
        labelMap.put("practice", "Required Practice Problems: ");
        labelMap.put("flashcard", "Flashcard Sessions: ");
        labelMap.put("video", "Videos: ");
        labelMap.put("game", "Games: ");
        
        orderList = new ArrayList<String>();
        
        orderList.add("login");
        orderList.add("review");
        orderList.add("practice");
        orderList.add("cmextra");
        orderList.add("flashcard");
        orderList.add("game");
        orderList.add("activity");
        orderList.add("video");
        
    }

    private String reportName;
    
    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(String reportId, Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;

        Integer stuUid = -1;
        List<Integer> studentUids = (List<Integer>) CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);
        stuUid = studentUids.get(0);

        CmAdminDao adminDao = new CmAdminDao();

        AccountInfoModel info = adminDao.getAccountInfo(adminId);
        
        if (info == null) {
            logger.warn("*** Account info is NULL for adminId: " + adminId);
            return null;
        }

        CmReportCardDao rcDao = new CmReportCardDao();
        StudentReportCardModelI rc = rcDao.getStudentReportCard(stuUid, null, null);
        
        CmStudentDao studentDao = new CmStudentDao();
        StudentModel sm = studentDao.getStudentModel(stuUid);

        Document document = new Document();
        baos = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, baos);

        Phrase school = buildLabelContent("School: ", info.getSchoolName());
        Phrase group = buildLabelContent("Group: ", sm.getGroup());
        String printDate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());
        Phrase date = buildLabelContent("Today's Date: ", printDate);

        Date actDate = rc.getFirstActivityDate();
        String activityDate = (actDate != null) ? String.format("%1$tY-%1$tm-%1$td", actDate) : " ";
        Phrase firstActivityDate = buildLabelContent("First activity: ", activityDate);

        actDate = rc.getLastActivityDate();
        activityDate = (actDate != null) ? String.format("%1$tY-%1$tm-%1$td", actDate) : " ";
        Phrase lastActivityDate = buildLabelContent("Last activity: ", activityDate);

        Phrase student = buildLabelContent("Student: ", String.valueOf(sm.getName()));

        StringBuilder sb = new StringBuilder();
        sb.append("CM-ReportCard");
        if (sm.getName() != null) {
        	sb.append("-").append(sm.getName().replaceAll(" ", ""));
        }
        reportName = sb.toString();
        
        int numberOfColumns = 2;
        PdfPTable pdfTbl = new PdfPTable(numberOfColumns);
        pdfTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        pdfTbl.addCell(student);
        pdfTbl.addCell(date);

        pdfTbl.addCell(group);
        pdfTbl.addCell(" ");

        pdfTbl.addCell(school);
        pdfTbl.addCell(" ");

        pdfTbl.addCell(" ");
        pdfTbl.addCell(" ");

        pdfTbl.addCell(firstActivityDate);
        pdfTbl.addCell(lastActivityDate);

        pdfTbl.setTotalWidth(600.0f);

        writer.setPageEvent(new HeaderTable(writer, pdfTbl));

        HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
        footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
        document.setFooter(footer);

        document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin() + 90, document
                .bottomMargin());
        document.open();
        document.add(Chunk.NEWLINE);

        addProgramInfo(rc, document);

        addQuizInfo(rc, document);

        if (rc.getResourceUsage().size() > 0) {
            addResourceUsage(rc, document);
        }

        if (rc.getPrescribedLessonList().size() > 0) {
            addPrescribedLessons(rc, document);
        }

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        document.close();

        return baos;
    }
    
    public String getReportName() {
    	return reportName;
    }

    private void addPrescribedLessons(StudentReportCardModelI rc, Document document) throws DocumentException {
        PdfPTable lessonTbl = new PdfPTable(1);
        lessonTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Phrase lesson = buildSectionLabel("Prescribed Lessons (based on wrong quiz answers)");
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
            System.out.println("key: " + key + ", label: " + label);
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
        Paragraph totalQuizzes = buildSectionContent("Attempted: ", rc.getQuizCount().toString(), true);
        Paragraph passedQuizzes = buildSectionContent("Passed: ", rc.getQuizPassCount().toString(), true);
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

    private void addProgramInfo(StudentReportCardModelI rc, Document document) throws DocumentException {
        PdfPTable progTbl = new PdfPTable(1);
        progTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        String initialProgDesc = rc.getInitialProgramName();

        /*
         * don't include initial status for now if (initialProgDesc != null) {
         * initialProgDesc += (rc.getInitialProgramStatus() != null) ? " " +
         * rc.getInitialProgramStatus() : ""; }
         */

        StringBuilder sb = new StringBuilder();
        sb.append("Initial program");
        Date assignedDate = rc.getInitialProgramDate();
        if (assignedDate != null) {
            sb.append(" assigned ").append(String.format("%1$tY-%1$tm-%1$td", assignedDate));
        }
        sb.append(": ");
        Phrase initialProg = buildLabelContent(sb.toString(), initialProgDesc);

        String lastProgDesc = rc.getLastProgramName();
        if (lastProgDesc != null) {
            lastProgDesc += (rc.getLastProgramStatus() != null) ? ", " + rc.getLastProgramStatus() : "";
        }
        Phrase lastProg = buildLabelContent("Current program & section: ", lastProgDesc);
        assignedDate = rc.getLastProgramDate();

        // don't include assigned date for current (last) program
        // String lastDate = (assignedDate != null) ?
        // String.format("%1$tY-%1$tm-%1$td", assignedDate) : " ";

        progTbl.addCell(initialProg);
        progTbl.addCell(lastProg);
        progTbl.setWidthPercentage(100.0f);
        document.add(progTbl);
        document.add(Chunk.NEWLINE);
    }

    private Paragraph buildLabelContent(String label, String value) {
        return buildLabelContent(label, value, true);
    }

    private Paragraph buildLabelContent(String label, String value, Boolean useDefault) {
        if (value == null || value.trim().length() == 0) {
            value = (useDefault) ? "n/a" : " ";
        }
        Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD,
                new Color(0, 0, 0))));
        Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
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

    public class HeaderTable implements PdfPageEvent {

        PdfPTable header;

        HeaderTable(PdfWriter writer, PdfPTable header) {
            // event = writer.getPageEvent();
            this.header = header;
            writer.setPageEvent(this);
        }

        public void onChapter(PdfWriter arg0, Document arg1, float arg2, Paragraph arg3) {
        }

        public void onChapterEnd(PdfWriter arg0, Document arg1, float arg2) {
        }

        public void onCloseDocument(PdfWriter arg0, Document arg1) {
        }

        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            header.writeSelectedRows(0, -1, document.left(), document.top() + 100, cb);
        }

        public void onGenericTag(PdfWriter arg0, Document arg1, Rectangle arg2, String arg3) {
        }

        public void onOpenDocument(PdfWriter arg0, Document arg1) {
        }

        public void onParagraph(PdfWriter arg0, Document arg1, float arg2) {
        }

        public void onParagraphEnd(PdfWriter arg0, Document arg1, float arg2) {
        }

        public void onSection(PdfWriter arg0, Document arg1, float arg2, int arg3, Paragraph arg4) {
        }

        public void onSectionEnd(PdfWriter arg0, Document arg1, float arg2) {
        }

        public void onStartPage(PdfWriter arg0, Document arg1) {
        }

    }
}