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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

public class StudentReportCard {
	
	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream makePdf(String reportId, Integer adminId) {
		ByteArrayOutputStream baos = null;

		Integer stuUid = -1;
		try {
			List<Integer> studentUids =
				(List<Integer>)CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);
			stuUid = studentUids.get(0);
			
			CmAdminDao adminDao = new CmAdminDao();

			AccountInfoModel info = adminDao.getAccountInfo(adminId);
			if (info == null) return null;

			CmReportCardDao rcDao = new CmReportCardDao();
			StudentReportCardModelI rc = rcDao.getStudentReportCard(stuUid, null, null);

			CmStudentDao studentDao = new CmStudentDao();
			StudentModel sm = studentDao.getStudentModel(stuUid);
						
			Document document = new Document();
			baos = new ByteArrayOutputStream();

			PdfWriter writer = PdfWriter.getInstance(document, baos);

			Phrase school   = buildLabelContent("School: ", info.getSchoolName());
			Phrase group    = buildLabelContent("Group: ", sm.getGroup());
			String printDate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());
			Phrase date     = buildLabelContent("Today's Date: ", printDate);
			
			Date actDate = rc.getFirstActivityDate();
			String activityDate = (actDate != null) ? String.format("%1$tY-%1$tm-%1$td", actDate) : " ";
			Phrase firstActivityDate = buildLabelContent("First activity: ", activityDate);
			
			actDate = rc.getLastActivityDate();
			activityDate = (actDate != null) ? String.format("%1$tY-%1$tm-%1$td", actDate) : " ";
			Phrase lastActivityDate = buildLabelContent("Last activity: ", activityDate);
		    
			Phrase student  = buildLabelContent("Student: ", String.valueOf(sm.getName()));

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

			document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin()+90, document.bottomMargin());
			document.open();
			document.add(Chunk.NEWLINE);

			PdfPTable progTbl = new PdfPTable(numberOfColumns);
			progTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

			String initialProgDesc = rc.getInitialProgramName();
			if (initialProgDesc != null) {
				initialProgDesc += (rc.getInitialProgramStatus() != null) ? " " + rc.getInitialProgramStatus() : "";
			}
			Phrase initialProg = buildLabelContent("Initial program: ", initialProgDesc);			
			Date assignedDate = rc.getInitialProgramDate();
			String initialDate = (assignedDate != null) ? String.format("%1$tY-%1$tm-%1$td", assignedDate) : " ";
			Phrase initialAssignedDate = buildLabelContent("Assigned: ", initialDate);
			
			String lastProgDesc = rc.getLastProgramName();
			if (lastProgDesc != null) {
				lastProgDesc += (rc.getLastProgramStatus() != null) ? " " + rc.getLastProgramStatus() : "";
			}
			Phrase lastProg = buildLabelContent("Last program: ", lastProgDesc);
			assignedDate = rc.getLastProgramDate();
			String lastDate = (assignedDate != null) ? String.format("%1$tY-%1$tm-%1$td", assignedDate) : " ";
			Phrase lastAssignedDate = buildLabelContent("Assigned: ", lastDate);
						
            progTbl.addCell(initialProg);
            progTbl.addCell(initialAssignedDate);
            progTbl.addCell(lastProg);
            progTbl.addCell(lastAssignedDate);
            progTbl.setWidthPercentage(100.0f);
            document.add(progTbl);
			document.add(Chunk.NEWLINE);

			PdfPTable quizTbl = new PdfPTable(numberOfColumns);
			quizTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			Phrase totalQuizzes = buildLabelContent("Quizzes: Attempted: ", rc.getQuizCount().toString());
			Phrase passedQuizzes = buildLabelContent("Passed: ", rc.getQuizPassCount().toString());
			String average = (rc.getQuizAvgPassPercent() != null) ? rc.getQuizAvgPassPercent() + "%" : "n/a";
			Phrase avgScore = buildLabelContent("Avg score of passed quizzes: ", average);
			quizTbl.addCell(totalQuizzes);
			quizTbl.addCell(passedQuizzes);
			quizTbl.addCell(avgScore);
			quizTbl.addCell(" ");
            quizTbl.setWidthPercentage(100.0f);
            quizTbl.setSpacingBefore(20.0f);
			document.add(quizTbl);
			document.add(Chunk.NEWLINE);

			if (rc.getResourceUsage().size() > 0) {
    			PdfPTable usageTbl = new PdfPTable(1);
	    		usageTbl.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

	    		Phrase usage = buildLabelContent("Totals: ", "", false);
    			usageTbl.addCell(usage);
    			usageTbl.addCell(" ");
    			
	    		Map<String, Integer> map = rc.getResourceUsage();
	    		for (String key : map.keySet()) {
	    			usage = buildLabelContent(key + ": ", String.valueOf(map.get(key)));
	    			usageTbl.addCell(usage);
	    		}
                usageTbl.setWidthPercentage(100.0f);
                usageTbl.setSpacingBefore(20.0f);
		    	document.add(usageTbl);
			    document.add(Chunk.NEWLINE);
			}
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			document.close();

		} catch (Exception e) {
			System.out.println(String.format("*** Error generating student report card for aid: %d, uid: %d",
				adminId, stuUid));
			e.printStackTrace();
		}
		return baos;
	}

	private Phrase buildLabelContent(String label, String value) {
		return buildLabelContent(label, value, true);
	}
	
	private Phrase buildLabelContent(String label, String value, Boolean useDefault) {
		if (value == null || value.trim().length() == 0) {
			value = (useDefault) ? "n/a" : " ";
		}
		Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new Color(0, 0, 0))));
		Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new Color(0, 0, 0))));
		phrase.add(content);
		return phrase;
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
			header.writeSelectedRows(0, -1, document.left(), document.top() + 100, cb);
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