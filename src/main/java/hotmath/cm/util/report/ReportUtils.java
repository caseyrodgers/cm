package hotmath.cm.util.report;

import java.awt.Color;
import java.sql.Connection;
import java.util.Map;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;


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
    
    public static HeaderFooter getGroupReportHeader(AccountInfoModel info, int studentCount, String filterDescription) {
        Phrase heading = new Phrase();
        Phrase school = buildPhraseLabel("School: ", nz(info.getSchoolName()));
        Phrase admin = buildPhraseLabel("Administrator: ", nz(info.getSchoolUserName()));
        Phrase expires = buildPhraseLabel("Expires: ", nz(info.getExpirationDate()));
        Phrase stuCount = buildPhraseLabel("Student Count: ", String.valueOf(studentCount));
        
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
        if (filterDescription != null && filterDescription.length() > 0) {
            Phrase filterDescr = buildPhraseLabel("Filter: ", filterDescription);
            heading.add(Chunk.NEWLINE);
            heading.add(filterDescr);
        }

        HeaderFooter header = new HeaderFooter(heading, false);

        return header;
    }
    
    static private String nz(String s) {
        return s == null?"":s;
    }
    
    public static PdfPTable getStudentReportHeader(StudentModelI sm, AccountInfoModel info) {
    	return null;
    }

    public static String getFilterDescription(final Connection conn, Integer adminId, CmAdminDao dao,
    		Map<FilterType, String> filterMap) throws Exception {
    	if (filterMap != null && filterMap.size() > 0) {
    		StringBuilder sb = new StringBuilder();
    		
    		if (filterMap.containsKey(FilterType.GROUP)) {
           		Integer groupId = Integer.valueOf(filterMap.get(FilterType.GROUP));
    			CmList<GroupInfoModel> groups = dao.getActiveGroups(conn, adminId);
    			for(GroupInfoModel group : groups) {
    				if (group.getId().equals(groupId)) {
    					sb.append("Group: ").append(group.getName());
    					break;
    				}
    			}
    		}
    		
    		if (filterMap.containsKey(FilterType.QUICKTEXT)) {
    			if (sb.length() > 0) sb.append(", ");
    			sb.append("Quick search: ");
    			sb.append(filterMap.get(FilterType.QUICKTEXT).trim());
    		}
    	    
    	    return sb.toString();
    	}
    	return "";
    }
}
