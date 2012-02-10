package hotmath.cm.util.report;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class StudentSummaryReport {
    
    static private Logger __logger = Logger.getLogger(StudentSummaryReport.class);
	
	private String reportName;
	private Map<FilterType,String> filterMap;

    @SuppressWarnings("unchecked")
    public ByteArrayOutputStream makePdf(String reportId, Integer adminId) throws Exception {
        ByteArrayOutputStream baos = null;
        List<Integer> studentUids = (List<Integer>) CmCacheManager.getInstance().retrieveFromCache(REPORT_ID, reportId);

        List<StudentModelI> sList = null;
        List<StudentModelI> extList = null;
        Connection conn=null;
        String filterDescription;
        AccountInfoModel info = null;
        try {
            conn = HMConnectionPool.getConnection();

            CmAdminDao adminDao = CmAdminDao.getInstance();
            CmStudentDao studentDao = CmStudentDao.getInstance();
            info = adminDao.getAccountInfo(adminId);
            if (info == null)
                return null;

            setReportName(info);
            
            sList = studentDao.getBaseSummariesForActiveStudents(conn, adminId);
            extList = studentDao.getStudentExtendedSummaries(conn, studentUids);
            filterDescription = ReportUtils.getFilterDescription(conn, adminId, adminDao, filterMap);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }

        Map<Integer, StudentModelI> map = new HashMap<Integer, StudentModelI>(sList.size());
        for (StudentModelI sm : sList) {
            map.put(sm.getUid(), sm);
        }

        Map<Integer, StudentModelI> extMap = new HashMap<Integer, StudentModelI>(extList.size());
        for (StudentModelI sm : extList) {
            extMap.put(sm.getUid(), sm);
        }
        
        List<StudentModelI> list = new ArrayList<StudentModelI>(studentUids.size());
        for (Integer uid : studentUids) {
            setBaseData(map.get(uid), extMap.get(uid));
            list.add(extMap.get(uid));
        }

        Document document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        HeaderFooter header = ReportUtils.getGroupReportHeader(info, list.size(), filterDescription);
        HeaderFooter footer = ReportUtils.getFooter();

        document.setHeader(header);
        document.setFooter(footer);

        document.open();

        Table tbl = new Table(7);
        tbl.setWidth(100.0f);
        tbl.setBorder(Table.BOTTOM);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        addHeader("Student", "15%", tbl);
        addHeader("Group", "15%", tbl);
        addHeader("Program", "15%", tbl);
        addHeader("Status", "15%", tbl);
        addHeader("Quizzes", "15%", tbl);
        addHeader("Last Quiz", "10%", tbl);
        addHeader("Last Login", "15%", tbl);
        //addHeader("Tutoring", "6%", tbl);

        tbl.endHeaders();

        int i = 0;
        for (StudentModelI sm : list) {
            addCell(sm.getName(), tbl, ++i);
            addCell(sm.getGroup(), tbl, i);
            addCell(sm.getProgram().getProgramDescription(), tbl, i);
            addCell(sm.getStatus(), tbl, i);
            addCell(getQuizzesResult(sm), tbl, i);
            addCell(sm.getLastQuiz(), tbl, i);
            addCell(sm.getLastLogin(), tbl, i);
            //addCell(String.valueOf(sm.getTutoringUse()), tbl, i);
        }

        document.add(tbl);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        document.close();
        return baos;
    }

    public void setFilterMap(Map<FilterType,String> filterMap) {
    	this.filterMap = filterMap;
    }

    private void setReportName(AccountInfoModel info) {
        StringBuilder sb = new StringBuilder();
        sb.append("CM-SummaryReport");
        if (info.getSchoolName() != null)
        	sb.append("-").append(info.getSchoolName().replaceAll("/|# ", ""));
        reportName = sb.toString();
    }

    public String getReportName() {
    	return reportName;
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
        if (content == null)
            content = "";
        Chunk c = new Chunk(content, FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new Color(0, 0, 0)));
        c.setTextRise(3.0f);
        Cell cell = new Cell(c);
        cell.setHeader(false);
        cell.setColspan(1);
        cell.setBorder(0);
        if (rowNum % 2 < 1)
            cell.setGrayFill(0.9f);
        tbl.addCell(cell);
    }

    static public void setBaseData(StudentModelI smBase, StudentModelI smExt) {
        smExt.setName(smBase.getName());
        smExt.setPasscode(smBase.getPasscode());
        smExt.getSettings().setShowWorkRequired(smBase.getSettings().getShowWorkRequired());
        smExt.getSettings().setTutoringAvailable(smBase.getSettings().getTutoringAvailable());
        smExt.setGroupId(smBase.getGroupId());
        smExt.setGroup(smBase.getGroup());
        smExt.setSectionNum(smBase.getSectionNum());
        smExt.setChapter(smBase.getChapter());
        smExt.setSectionNum(smBase.getSectionNum());
        smExt.setPassPercent(smBase.getPassPercent());
        smExt.setProgram(smBase.getProgram());
        smExt.setStatus(smBase.getStatus());
    }

    /**
     *  Set the base data in the StudentModel by extracting data from matching
     *  StudentModel in smExtList.
     *  
     *  NOTE: these two lists might not be in the same order...
     *  
     * @param smBaseList
     * @param smExtList
     */
    static public void setBaseData(List<StudentModelI> smBaseList, List<StudentModelI> smExtList) {
    	for (StudentModelI smBase : smBaseList) {
    		StudentModelI smExt = findStudentModel(smBase.getUid(), smExtList);
    		if(smExt == null) {
    		    __logger.warn("uid not found in extension list: " + smBase);
    		}
    		setBaseData(smBase, smExt);
    	}
    }
    
    /** Return StudentModel with UID specified
     * 
     * @param uid
     * @param models
     * @return
     */
    static private StudentModelI findStudentModel(Integer uid, List<StudentModelI> models) {
        for(StudentModelI sm: models) {
            if(sm.getUid().equals(uid))
                return sm;
        }
        return null;
    }
    
    
    private String getQuizzesResult(StudentModelI sm) {
        if (sm.getPassingCount() != null && sm.getNotPassingCount() != null &&
        	(sm.getPassingCount() > 0 || sm.getNotPassingCount() > 0)) {
        	StringBuilder sb = new StringBuilder();
        	sb.append(sm.getPassingCount()).append(" passed out of ");
        	sb.append(sm.getPassingCount() + sm.getNotPassingCount());
            return sb.toString();
        }
        else {
        	return "";
        }
    }
}
