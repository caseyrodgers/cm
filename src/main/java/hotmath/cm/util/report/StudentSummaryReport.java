package hotmath.cm.util.report;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class StudentSummaryReport {
	
	public ByteArrayOutputStream makePdf(Integer adminId) {
		ByteArrayOutputStream baos = null;

		try {
			CmAdminDao dao = new CmAdminDao();

			AccountInfoModel info = dao.getAccountInfo(adminId);
			if (info == null) return null;

			List <StudentModel> list = dao.getSummariesForActiveStudents(adminId);
			
			Document document = new Document();
			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			Phrase heading = new Phrase();
			Phrase school   = buildLabelContent("School: ", info.getSchoolName());
			Phrase admin    = buildLabelContent("Administrator: ", info.getSchoolUserName());
			Phrase expires  = buildLabelContent("Expires: ", info.getExpirationDate());
			Phrase stuCount = buildLabelContent("Student Count: ", String.valueOf(list.size()));

			heading.add(school);
			heading.add(Chunk.NEWLINE);
			heading.add(admin);
			heading.add(Chunk.NEWLINE);
			heading.add(expires);
			heading.add(Chunk.NEWLINE);
			heading.add(stuCount);

			HeaderFooter header = new HeaderFooter(heading, false);
			HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
			footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
			document.setHeader(header);
			document.setFooter(footer);

			document.open();

			Table tbl = new Table(7);
			tbl.setWidth(100.0f);
			tbl.setBorder(Table.BOTTOM);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);			

			addHeader("Student", "15%", tbl);
			addHeader("Group", "20%", tbl);
			addHeader("Program", "15%", tbl);
			addHeader("Status", "15%", tbl);
			addHeader("Last Quiz", "10%", tbl);
			addHeader("Last Login", "15%", tbl);
			addHeader("Usage", "10%", tbl);

			tbl.endHeaders();

			int i = 0;
			for (StudentModel sm : list) {
				addCell(sm.getName(), tbl, ++i);
				addCell(sm.getGroup(), tbl, i);
				addCell(sm.getProgramDescr(), tbl, i);
				addCell(sm.getStatus(), tbl, i);
				addCell(sm.getLastQuiz(), tbl, i);
				addCell(sm.getLastLogin(), tbl, i);
				addCell(String.valueOf(sm.getTotalUsage()), tbl, i);
			}

			document.add(tbl);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			document.close();

		} catch (Exception e) {
			System.out.println(String.format("*** Error generating student summary for aid: %d", adminId));
			e.printStackTrace();
		}
		return baos;
	}

	private Phrase buildLabelContent(String label, String value) {
		 Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, new Color(0, 0, 0))));
		 Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new Color(0, 0, 0))));
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


}
