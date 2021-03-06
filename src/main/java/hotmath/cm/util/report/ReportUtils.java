package hotmath.cm.util.report;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.awt.Color;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;


public class ReportUtils {
	
	public static HeaderFooter getFooter() {
        HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("."));
        footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
        return footer;
	}

	public static Paragraph buildTitle(String titleText) {
        Phrase phrase = new Phrase(new Chunk(titleText, FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD,
                new Color(0, 0, 0))));
        Paragraph p = new Paragraph();
        p.setSpacingBefore(10);
        p.setSpacingAfter(10);
        p.add(phrase);
        return p;
	}

    public static Phrase buildPhraseLabel(String label, String value) {
        Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD,
                new Color(0, 0, 0))));
        Phrase content = new Phrase(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
                new Color(0, 0, 0))));
        phrase.add(content);
        return phrase;
    }
    
    public static Phrase buildPhraseLabel(String label, Phrase content) {
        Phrase phrase = new Phrase(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD,
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

    public static HeaderFooter getGroupReportHeader(String title, AccountInfoModel info, int studentCount, String filterDescription)
        throws Exception {
    	return getGroupReportHeader(info, "Student Count: ", studentCount, filterDescription, title);
    }
    
    public static HeaderFooter getGroupReportHeader(AccountInfoModel info, String countLabel, int count, String filterDescription, String title)
        throws Exception {
        Paragraph heading = new Paragraph();

		Image cmLogo = ReportUtils.getCatchupMathLogo();
		Paragraph titleP = ReportUtils.buildTitle((title != null) ? title : " ");

		heading.add(cmLogo);
        heading.add(Chunk.NEWLINE);
		heading.add(titleP);

		/*
		 * School: Casey Test School Group: a_test_group
         * Date range: 2009-02-01 to 2013-07-13 Date: 2013-07-12
		 */
        Phrase school = buildPhraseLabel("School: ", nz(info.getSchoolName()));
        Phrase admin = buildPhraseLabel("Administrator: ", nz(info.getSchoolUserName()));
        Phrase expires = buildPhraseLabel("Expires: ", nz(info.getExpirationDate()));
        Phrase stuCount = buildPhraseLabel((countLabel != null)?countLabel:"Student Count: ", String.valueOf(count));

		heading.add(school);
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
        header.setBorder(Rectangle.NO_BORDER);

        return header;
    }

	public static HeaderFooter getBriefGroupReportHeader(AccountInfoModel info,
			String groupName, String filterDescription, String title)
			throws Exception {

		Paragraph heading = getBriefGroupReportHeading(info, groupName, filterDescription, title);

		HeaderFooter header = new HeaderFooter(heading, false);
		header.setBorder(Rectangle.NO_BORDER);

		return header;
	}

	public static HeaderFooter getAssignmentReportHeader(AccountInfoModel info,
			String groupName, String filterDescription, String title, Color inProgress, Color readyToGrade)
			throws Exception {

		Paragraph heading = getBriefGroupReportHeading(info, groupName, filterDescription, title);
		
		Phrase phrase = new Phrase(new Chunk("  In progress  ", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, inProgress)));
		phrase.add(new Chunk("  Ready to grade", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, readyToGrade)));
		Phrase legend = buildPhraseLabel("Legend: ", phrase);
		heading.add(legend);

		heading.add(Chunk.NEWLINE);

		HeaderFooter header = new HeaderFooter(heading, false);
		header.setBorder(Rectangle.NO_BORDER);

		return header;
	}

	private static Paragraph getBriefGroupReportHeading(AccountInfoModel info,
			String groupName, String filterDescription, String title)
			throws Exception {
		Paragraph heading = new Paragraph();

		Image cmLogo = ReportUtils.getCatchupMathLogo();
		Paragraph titleP = ReportUtils
				.buildTitle((title != null) ? title : " ");

		heading.add(cmLogo);
		heading.add(Chunk.NEWLINE);
		heading.add(titleP);

		if (groupName != null) {
			Phrase group = buildPhraseLabel("Group: ", groupName);
			heading.add(group);
			heading.add(Chunk.NEWLINE);
		}
		Phrase school = buildPhraseLabel("School: ", nz(info.getSchoolName()));
		heading.add(school);
		heading.add(Chunk.NEWLINE);
		if (filterDescription != null && filterDescription.length() > 0) {
			Phrase filterDescr = buildPhraseLabel("Filter: ", filterDescription);
			heading.add(filterDescr);
			heading.add(Chunk.NEWLINE);
		}
		String printDate = String.format("%1$tY-%1$tm-%1$td",
				Calendar.getInstance());
		Phrase date = buildPhraseLabel("Date: ", printDate);
		heading.add(date);
		heading.add(Chunk.NEWLINE);

		return heading;
	}

    public static HeaderFooter getBasicReportHeader(AccountInfoModel info, String label) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	String today = dateFormat.format(new Date());
        Phrase heading = new Phrase();
        Phrase school = buildPhraseLabel("School: ", nz(info.getSchoolName()));
        Phrase date = buildPhraseLabel("Date: ", today);
        
        heading.add(school);
        heading.add(Chunk.NEWLINE);
        heading.add(date);
        heading.add(Chunk.NEWLINE);
        if (label != null) heading.add(label);

        HeaderFooter header = new HeaderFooter(heading, false);

        return header;
    }

    static private String nz(String s) {
        return s == null?"":s;
    }
    
    public static HeaderFooter getStudentReportHeader(String title, StudentModelI sm, AccountInfoModel info, String filterDescription) 
        throws Exception {
        Paragraph heading = new Paragraph();

		Image cmLogo = ReportUtils.getCatchupMathLogo();
		Paragraph titleP = ReportUtils.buildTitle((title != null) ? title : " ");

		heading.add(cmLogo);
        heading.add(Chunk.NEWLINE);
		heading.add(titleP);

        Phrase school        = buildPhraseLabel("School: ", nz(info.getSchoolName()));
        Phrase admin         = buildPhraseLabel("Administrator: ", nz(info.getSchoolUserName()));
        // Phrase expires       = buildPhraseLabel("Expires: ", nz(info.getExpirationDate()));
		Phrase student       = buildPhraseLabel("Student: ", nz(sm.getName()));
        String showWorkState = (sm.getSettings().getShowWorkRequired()) ? "REQUIRED" : "OPTIONAL";
		Phrase showWork      = buildPhraseLabel("Show Work: ", showWorkState);
		String printDate     = String.format("%1$tY-%1$tm-%1$td %1$tI:%1$tM %1$Tp", Calendar.getInstance());
		Phrase date          = buildPhraseLabel("Date: ", printDate);

		heading.add(student);
        heading.add(Chunk.NEWLINE);
		heading.add(showWork);
        heading.add(Chunk.NEWLINE);
		heading.add(school);
        heading.add(Chunk.NEWLINE);
        heading.add(admin);
        if (filterDescription != null && filterDescription.length() > 0) {
            Phrase filterDescr = buildPhraseLabel("Filter: ", filterDescription);
            heading.add(Chunk.NEWLINE);
            heading.add(filterDescr);
        }
        heading.add(Chunk.NEWLINE);
        heading.add(date);

        HeaderFooter header = new HeaderFooter(heading, false);
        header.setBorder(Rectangle.NO_BORDER);

        return header;
    }

    public static String getFilterDescription(final Connection conn, Integer adminId, CmAdminDao dao,
    		Map<FilterType, String> filterMap) throws Exception {
    	if (filterMap != null && filterMap.size() > 0) {
    		StringBuilder sb = new StringBuilder();
    		
    		if (filterMap.containsKey(FilterType.GROUP)) {
           		Integer groupId = Integer.valueOf(filterMap.get(FilterType.GROUP));
    			List<GroupInfoModel> groups = dao.getActiveGroups(adminId);
    			for(GroupInfoModel group : groups) {
    				if (group.getId() == groupId) {
    					sb.append("Group: ").append(group.getGroupName());
    					break;
    				}
    			}
    		}
    		
    		if (filterMap.containsKey(FilterType.QUICKTEXT)) {
    			if (sb.length() > 0) sb.append(", ");
    			sb.append("Quick search: ");
    			sb.append(filterMap.get(FilterType.QUICKTEXT).trim());
    		}
    	    
    		if (filterMap.containsKey(FilterType.DATE_RANGE)) {
    			if (sb.length() > 0) sb.append(", ");
    			sb.append("Date range: ");
    			sb.append(filterMap.get(FilterType.DATE_RANGE).trim());
    		}
    	    
    	    return sb.toString();
    	}
    	return "";
    }

    public static String getReportName(String baseName, String schoolName) {
		StringBuilder sb = new StringBuilder();
		sb.append(baseName).append("-");
		sb.append(schoolName.replaceAll(" ", "").replaceAll("/", "-").replaceAll("#", "-"));
		return sb.toString();
    }

    public static Image getCatchupMathLogo() throws Exception {
		String cmLogoFile = CatchupMathProperties.getInstance().getCatchupRuntime() + "/images/catchupmath.png";
		Image cmLogo = Image.getInstance(cmLogoFile);
		cmLogo.scalePercent(70.0f);
		return cmLogo;
    }

}
