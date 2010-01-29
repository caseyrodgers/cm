package hotmath.cm.util.report;

import java.awt.Color;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;

import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;


public class ReportUtils {
	
	public static HeaderFooter getFooter() {
        HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
        footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
        return footer;
	}

    public static Phrase buildPhraseLabel(String label, String value) {
        Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD,
                new Color(0, 0, 0))));
        Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
                new Color(0, 0, 0))));
        phrase.add(content);
        return phrase;
    }
    
    public static Paragraph buildParagraphLabel(String label, String value) {
        return buildParagraphLabel(label, value, true);
    }

    public static Paragraph buildParagraphLabel(String label, String value, Boolean useDefault) {
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
    
    public static Phrase buildSectionLabel(String label) {
        Chunk chunk = new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0, 0, 0)));
        chunk.setUnderline(0.5f, -3f);
        Phrase phrase = new Phrase(chunk);
        return phrase;
    }

    public static Paragraph buildSectionContent(String label, String value, Boolean useDefault) {
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
    
    public static HeaderFooter getGroupReportHeader(AccountInfoModel info, int studentCount) {
        Phrase heading = new Phrase();
        Phrase school = ReportUtils.buildPhraseLabel("School: ", info.getSchoolName());
        Phrase admin = ReportUtils.buildPhraseLabel("Administrator: ", info.getSchoolUserName());
        Phrase expires = ReportUtils.buildPhraseLabel("Expires: ", info.getExpirationDate());
        Phrase stuCount = ReportUtils.buildPhraseLabel("Student Count: ", String.valueOf(studentCount));
        
        heading.add(school);
        // Chunk c = new Chunk(new Jpeg(new
        // URL("http://localhost:8081/gwt-resources/images/logo_1.jpg")), 3.5f,
        // 1.0f);
        // heading.add(c);
        heading.add(Chunk.NEWLINE);
        heading.add(admin);
        heading.add(Chunk.NEWLINE);
        heading.add(expires);
        heading.add(Chunk.NEWLINE);
        heading.add(stuCount);
        
        HeaderFooter header = new HeaderFooter(heading, false);

        return header;
    }
    
    public static PdfPTable getStudentReportHeader(StudentModelI sm, AccountInfoModel info) {
    	return null;
    }
}
