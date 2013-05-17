package hotmath.cm.util.report;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.InformationOnlyException;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

public class StudentCCSSReport {

	private String reportName;
    private String title;

	private static final Logger LOGGER = Logger.getLogger(StudentCCSSReport.class);

	public StudentCCSSReport(String title) {
		this.title = title;
	}

	public ByteArrayOutputStream makePdf(int adminId, int userId, Date fromDate, Date toDate) throws Exception {
		ByteArrayOutputStream baos = null;
		try {

	        AccountInfoModel info = CmAdminDao.getInstance().getAccountInfo(adminId);
            if (info == null) return null;

            CmStudentDao studentDao = CmStudentDao.getInstance();
            List<Integer> uidList = new ArrayList<Integer>();
            uidList.add(userId);
            List<StudentModelI> smList = studentDao.getStudentSummaries(adminId, uidList, true);
            if (smList == null || smList.size() == 0) return null;
            StudentModelI stuMdl = smList.get(0);

            CCSSReportDao crDao = CCSSReportDao.getInstance();

            List<String> quizCCSS = crDao.getStudentQuizStandardNames(userId, fromDate, toDate);
            List<String> reviewCCSS = crDao.getStudentReviewStandardNames(userId, fromDate, toDate);
            List<String> assignmentCCSS = crDao.getStudentAssignmentStandardNames(userId, fromDate, toDate);

            if ((quizCCSS == null || quizCCSS.size() == 0) &&
            	(reviewCCSS == null || reviewCCSS.size() == 0) &&
            	(assignmentCCSS == null || assignmentCCSS.size() == 0)) {
            	String msg = String.format("Student has no CCSS coverage in Date Range: %1$ty-%1$tm-%1$td to %2$ty-%2$tm-%2$td",
            		fromDate, toDate);
            	//throw new InformationOnlyException(msg);
            }

			Document document = new Document();
			document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin()+50, document.bottomMargin());

			baos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			document.open();

            PdfPTable pdfTbl = addHeaderInfo(info, stuMdl, fromDate, toDate);
            document.add(pdfTbl);
			//writer.setPageEvent(new HeaderTable(writer, pdfTbl));

			StringBuilder sb = new StringBuilder();
			sb.append("CM-Student-CCSS-Report");

			HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
			footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
			document.setFooter(footer);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

    		Table tbl = new Table(1);
			tbl.setWidth(100.0f);
			tbl.setBorder(Table.BOTTOM);
			tbl.setBorder(Table.TOP);

			document.add(tbl);

			addSection("Quizzed and passed", quizCCSS, document);
			addSection("Reviewed", reviewCCSS, document);
			addSection("Assigned work completed", assignmentCCSS, document);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			document.close();

		} catch (Exception e) {
			LOGGER.error(String.format("*** Error generating CCSS report for adminId: %d, userId: %d",
				adminId, userId), e);
			if (e instanceof InformationOnlyException) throw e;
		}
		return baos;
	}

    private void addSection(String labelText, List<String> standardNames, Document document) throws DocumentException {
        PdfPTable tbl = new PdfPTable(1);
        tbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Phrase label = buildSectionLabel(labelText);
        tbl.addCell(label);

        String content;
		if (standardNames != null && standardNames.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String stdName : standardNames) {
            	//String link = convertStandardNameToLink(stdName);
                sb.append(stdName).append(", ");
            }
            content = sb.toString().substring(0, sb.length() - 2);
		}
		else {
			content = "None";
		}
        Paragraph p = new Paragraph();
        p.setFont(FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new Color(0, 0, 0)));
        p.add(content);
        tbl.addCell(p);
        tbl.setWidthPercentage(100.0f);
        tbl.setSpacingBefore(20.0f);
        document.add(tbl);
        document.add(Chunk.NEWLINE);
    }

    private static final String CCSS_LINK_FMT = "<a href='http://www.corestandards.org/Math/Content/%s' target='_blank'>%s</a>";

	private String convertStandardNameToLink(String stdName) {
		String uri = stdName.replace("-", "/").replace(".", "/");
		return String.format(CCSS_LINK_FMT, uri, stdName);
	}

	private Phrase buildSectionLabel(String label) {
        Chunk chunk = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0, 0, 0)));
        chunk.setUnderline(0.5f, -3f);
        Phrase phrase = new Phrase(chunk);
        return phrase;
    }

	private Phrase buildLabelContent(String label, String value) {
		if (value == null || value.trim().length() == 0) value = "NONE";
		Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(0, 0, 0))));
		Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(0, 0, 0))));
		phrase.add(content);
		return phrase;
	}

	public String getReportName() {
		return reportName;
	}

	private PdfPTable addHeaderInfo(AccountInfoModel info, StudentModelI sm, Date fromDate, Date toDate) throws DocumentException {

		Phrase school = ReportUtils.buildParagraphLabel("School: ", info.getSchoolName());
		Phrase group = ReportUtils.buildParagraphLabel("Group: ", sm.getGroup());
		Phrase student = ReportUtils.buildParagraphLabel("Student: ", sm.getName());
		String printDate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());
		Phrase date = ReportUtils.buildParagraphLabel("Today's Date: ", printDate);

		Phrase dateRange = null;
		if (fromDate != null && toDate != null) {
			dateRange = buildLabelContent("Date range: ", String.format("%1$tY-%1$tm-%1$td to %2$tY-%2$tm-%2$td", fromDate, toDate));
		}
		else {
			dateRange = new Phrase(" ");
		}

		int numberOfColumns = 2;
		PdfPTable pdfTbl = new PdfPTable(numberOfColumns);
		pdfTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		pdfTbl.setWidthPercentage(100.0f);
		//pdfTbl.setTotalWidth(100.0f);

		pdfTbl.addCell(student);
		pdfTbl.addCell(date);

		pdfTbl.addCell(group);
		pdfTbl.addCell(" ");

		pdfTbl.addCell(school);
		pdfTbl.addCell(" ");

		pdfTbl.addCell(" ");
		pdfTbl.addCell(" ");

		pdfTbl.addCell(dateRange);
		pdfTbl.addCell(" ");

    	return pdfTbl;
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