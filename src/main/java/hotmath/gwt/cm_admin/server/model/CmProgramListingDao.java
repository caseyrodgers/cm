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
        String sql = "select * from HA_PROG_DEF where id in ('Prof','Chap','Grad Prep') order by load_order asc";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
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
            	if (id.indexOf("Grad Prep") > -1) {
            		pr.getProgramTypes().add(createGradPrepProgramType(conn, id, rs.getString("title")));
            		continue;
            	}
            }

            return pr;
        } finally {
            SqlUtilities.releaseResources(rs, stmt, null);
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
    	
    	HaTestDefDao dao = HaTestDefDao.getInstance();
    	
    	List<HaTestDef> testDefs = dao.getTestDefsForProgramType(conn, type);;
    	
        ProgramType pt = new ProgramType(type,title);

        for (HaTestDef testDef : testDefs) {
            ProgramSubject ps = new ProgramSubject();
            ps.setTestDefId(testDef.getTestDefId());
            ps.setName(testDef.getSubjectId());
            pt.getProgramSubjects().add(ps);

            ProgramChapter chapter = new ProgramChapterAll();
            ps.getChapters().add(chapter);
            
            List<ProgramSection> list = buildSectionListForTestDef(testDef);
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
    	
    	HaTestDefDao dao = HaTestDefDao.getInstance();
    	
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

                List<ProgramSection> list = buildSectionListForTestDef(testDef);
                chapter.setSections(list);
                i++;
            }
            
        }
        return pt;
    }

    /** Return program type for all Grad Prep Programs
     * 
     * @param conn
     * @param type
     * @return
     * @throws Exception
     */
    private ProgramType createGradPrepProgramType(final Connection conn, String type, String title) throws Exception {
    	
    	// collect all Grad Prep programs
    	String sql = "select * from HA_TEST_DEF where prog_id like 'Grad Prep%' and is_active = 1 order by load_order asc";

        ProgramType pt = new ProgramType(type, "Graduation Preparation");

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while(rs.next()) {
            	ProgramSubject ps = new ProgramSubject();
            	ps.setName(rs.getString("test_name"));
            	ps.setTestDefId(rs.getInt("test_def_id"));
                pt.getProgramSubjects().add(ps);

                ProgramChapter chapter = new ProgramChapterAll();
                ps.getChapters().add(chapter);

                HaTestConfig tc = new HaTestConfig(rs.getString("test_config_json"));

                List<ProgramSection> list = buildSectionListForTestConfig(ps.getTestDefId(), tc);
                chapter.setSections(list);
            }
        } finally {
            SqlUtilities.releaseResources(rs, stmt, null);
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
            HaTestDefDao hda = HaTestDefDao.getInstance();
            HaTestDef testDef = hda.getTestDef(testDefId);
            
            CmList<ProgramLesson> lessons = new CmArrayList<ProgramLesson>();
            
            StudentUserProgramModel userProgram = new StudentUserProgramModel();
            userProgram.setTestDef(testDef);
            
            if (chapter == null) {
            	chapter = testDef.getChapter();
            }
            else {
            	// trim leading 'Ch N': '
            	int offset = chapter.indexOf(":");
            	if(offset > -1) { 
            	    /** look for tokenized chapter name.
            	     *  If specified, then it is a chapter 
            	     *  test.  Remove number part of token
            	     */
            	    chapter = chapter.substring(offset + 2);
            	}
            	else {
            		chapter = testDef.getChapter();
            	}
            }
        	if (logger.isDebugEnabled())
                logger.debug(String.format("+++ getLessonsFor(): textCode: %s, chapter: %s", testDef.getTextCode(), chapter));
            
            List<String> pids = hda.getTestIdsForSegment(userProgram, segment, testDef.getTextCode(), chapter, testDef.getTestConfig(), 0);
        	if (logger.isDebugEnabled())
                logger.debug(String.format("+++ getLessonsFor(): pids.size(): %d", pids.size()));
            
            /** now get list of lessons assigned to these pids by looking in HM_PROGRAM_LESSONS_static which is
             *  created by the HA_PRESCRIPTION_LOG Deploylet action.  This is a static table that holds information
             *  about lessons... and must be rebuilt on data changes.
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

            if (logger.isDebugEnabled()) {
            	logger.debug(String.format("+++ getLessonsFor(): Program Lesson sql: %s", sql));
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                lessons.add(new ProgramLesson(rs.getString("lesson")));
            }
            logger.info(String.format("+++ getLessonsFor(): found %d lessons", lessons.size()));
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(rs, stmt, null);
        }
    }

    private List<ProgramSection> buildSectionListForTestDef(HaTestDef testDef) {

    	return buildSectionListForTestConfig(testDef.getTestDefId(), testDef.getTestConfig());

    }
    
    private List<ProgramSection> buildSectionListForTestConfig(int testDefId, HaTestConfig testConfig) {
    	List<ProgramSection> list = new ArrayList<ProgramSection>();
    	
    	int segmentCount = testConfig.getSegmentCount();
    	
    	for (int i=1; i<=segmentCount; i++) {
    		ProgramSection s = new ProgramSection();
    		s.setTestDefId(testDefId);
    		s.setName(String.format("Section %d", i));
    		s.setNumber(i);
    		list.add(s);
    	}
    	return list;
    }
}
