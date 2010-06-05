package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapterAll;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class CmProgramListingDao {

    private static final Logger logger = Logger.getLogger(CmProgramListingDao.class);

    public CmProgramListingDao() {
    }

    /** why adminId?  The listing will always be same ... plus we can cache it when complete
     *  and share.
     *  
     * @param conn
     * @param adminId
     * @return
     * @throws Exception
     */
    public ProgramListing getProgramListing(final Connection conn, int adminId) throws Exception {
        PreparedStatement stmt=null;
        try {
            String sql = "select * from HA_PROG_DEF where id in ('Prof','Chap') order by id desc";
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ProgramListing pr = new ProgramListing();
            
            while(rs.next()) {
            	String id = rs.getString("id");
            	if ("Prof".equals(id)) {
                    pr.getProgramTypes().add(createProficiencyProgramType(conn, id, rs.getString("title")));
                    continue;
            	}
            	if ("Chap".equals(id)) {
            		pr.getProgramTypes().add(createSubjectAndChapterProgramType(conn, id, rs.getString("title")));
            		continue;
            	}
            }

            return pr;
        } finally {
            SqlUtilities.releaseResources(null, null, null);
        }
    }

    
    /** Return program type for all proficiency tests
     * 
     * @param conn
     * @param type
     * @return
     * @throws Exception
     */
    private ProgramType createProficiencyProgramType(final Connection conn, String type, String title) throws Exception {
    	
    	HaTestDefDao dao = new HaTestDefDao();
    	
    	List<HaTestDef> testDefs = dao.getTestDefsForProgramType(conn, type);;
    	
        ProgramType pt = new ProgramType(type,title);

        for (HaTestDef testDef : testDefs) {
            ProgramSubject ps = new ProgramSubject();
            ps.setTestDefId(testDef.getTestDefId());
            ps.setName(testDef.getSubjectId());
            pt.getProgramSubjects().add(ps);

            ProgramChapter chapter = new ProgramChapterAll();
            ps.getChapters().add(chapter);
            
            List<ProgramSection> list = buildSectionListForProficiencyTestDef(testDef);
            chapter.setSections(list);
        }
        return pt;
    }
    
    
    /** Return program type for all Subject and Chapter Lessons
     * 
     * @param conn
     * @param type
     * @return
     * @throws Exception
     */
    private ProgramType createSubjectAndChapterProgramType(final Connection conn, String type, String label) throws Exception {
    	
    	HaTestDefDao dao = new HaTestDefDao();
    	
    	List<HaTestDef> testDefs = dao.getTestDefs(conn, type);
    	
        ProgramType pt = new ProgramType(type,label);

        for (HaTestDef testDef : testDefs) {
            ProgramSubject ps = new ProgramSubject();
            ps.setTestDefId(testDef.getTestDefId());
            ps.setName(testDef.getSubjectId());
            pt.getProgramSubjects().add(ps);

            List<String> chapNames = dao.getProgramChapters(conn, testDef);
            int i = 1;
            for (String name : chapNames) {
            	ProgramChapter chapter = new ProgramChapter();
            	chapter.setName(name);
            	chapter.setNumber(i);
                ps.getChapters().add(chapter);

                List<ProgramSection> list = buildSectionListForSubjectChapterTestDef(conn, testDef, name, i);
                chapter.setSections(list);
                i++;
            }
            
        }
        return pt;
    }

    /** Return distinct lessons for a given testDefId modified with testConfig
     * 
     * @param conn
     * @param testDefId
     * @param testConfig
     * @return
     * @throws Exception
     */
    public CmList<ProgramLesson> getLessonsFor(final Connection conn, int testDefId, int segment, String chapter, int sectionCount) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
        	if (logger.isDebugEnabled())
                logger.debug(String.format("+++ getLessonsFor(): testDefId: %d, segment: %d, chapter: %s",
                	testDefId, segment, (chapter != null)?chapter:"n/a"));
            
            /** first get list of PIDS that make this quiz segment
             * 
             */
            HaTestDefDao hda = new HaTestDefDao();
            HaTestDef testDef = hda.getTestDef(conn, testDefId);
            
            CmList<ProgramLesson> lessons = new CmArrayList<ProgramLesson>();
            
            StudentUserProgramModel userProgram = new StudentUserProgramModel();
            userProgram.setTestDef(testDef);
            
            if (chapter == null) {
            	chapter = testDef.getChapter();
            }
            else {
            	// trim leading 'Ch N': '
            	int offset = chapter.indexOf(":");
            	chapter = chapter.substring(offset + 2);
            }
            List<String> pids = hda.getTestIdsForSegment(conn, userProgram, segment, testDef.getTextCode(), chapter, testDef.getTestConfig(), 0);
            
            /** now get list of lessons assigned to these pids by looking in HM_PROGRAM_LESSONS which is created
             *  by the HA_PRESCRIPTION_LOG Deploylet action.  This is a static table that holds information about
             *  lessons .. and must be rebuilt on data changes.
             *  
             */
            /** construct pid in list */
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for(String id : pids) {
                if (first != true)
                	sb.append(",");
                else
                	first = false;
                sb.append("'").append(id).append("'");
            }
            Map<String,String> map = new HashMap<String,String>(); 
            map.put("PIDLIST", sb.toString());
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_PROGRAM_LESSONS", map);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                lessons.add(new ProgramLesson(rs.getString("lesson")));
            }
            logger.info(String.format("+++ getLessonsFor(): found %d lessons", lessons.size()));
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(rs,stmt,null);
        }
    }
    
    private List<ProgramSection> buildSectionListForSubjectChapterTestDef(final Connection conn, HaTestDef testDef, String chapName, int chapNumber) {
    	return buildSectionListForProficiencyTestDef(testDef);
    }

    private List<ProgramSection> buildSectionListForProficiencyTestDef(HaTestDef testDef) {
    	List<ProgramSection> list = new ArrayList<ProgramSection>();
    	
    	int segmentCount = testDef.getTestConfig().getSegmentCount();
    	
    	for (int i=1; i<=segmentCount; i++) {
    		ProgramSection s = new ProgramSection();
    		s.setTestDefId(testDef.getTestDefId());
    		s.setName(String.format("Section %d", i));
    		s.setNumber(i);
    		list.add(s);
    	}
    	return list;
    }
    
}
