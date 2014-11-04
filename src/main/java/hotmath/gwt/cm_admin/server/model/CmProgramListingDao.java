package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapterAll;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSectionAll;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
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
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class CmProgramListingDao extends SimpleJdbcDaoSupport {

    private static final Logger logger = Logger.getLogger(CmProgramListingDao.class);

    public CmProgramListingDao() {
    }

    /** 
     *  
     * @return
     * @throws Exception
     */
    public ProgramListing getProgramListing() throws Exception {
        return getProgramListing(false);
    }

    /** 
     *  
     * @param includeBuiltInCustomProgs
     * @return
     * @throws Exception
     */
    public ProgramListing getProgramListing(boolean includeBuiltInCustomProgs) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_PROGRAMS");

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            ProgramListing pr = new ProgramListing();
            int index = 0;
            while(rs.next()) {
            	String id = rs.getString("id");
            	if ("Prof".equals(id)) {
            		ProgramType pt = createProficiencyProgramType(conn, id, rs.getString("title"), index);
            		index += pt.getItemCount();
                    pr.getProgramTypes().add(pt);
                    continue;
            	}
            	if ("Chap".equals(id)) {
            		ProgramType pt = createSubjectAndChapterProgramType(conn, id, rs.getString("title"), index);
            		pr.getProgramTypes().add(pt);
            		index += pt.getItemCount();
            		continue;
            	}
                if (includeBuiltInCustomProgs == true && "Custom".equals(id)) {
                	ProgramType pt = createBuiltInCustomProgramType(id, rs.getString("title"), index);
                	pr.getProgramTypes().add(pt);
                	index += pt.getItemCount();
                	continue;
                }
            	if (id.indexOf("Grad Prep") > -1) {
            		ProgramType pt = createGradPrepProgramType(conn, id, rs.getString("title"), index);
            		pr.getProgramTypes().add(pt);
                	index += pt.getItemCount();
            		continue;
            	}
            }

            return pr;
        } finally {
            SqlUtilities.releaseResources(rs, stmt, conn);
        }
    }

    
    private ProgramType createBuiltInCustomProgramType(String type, String title, int id) throws Exception {
        ProgramType progType = new ProgramType(type, title);

        CmCustomProgramDao cpDao = CmCustomProgramDao.getInstance();
        CmList<CustomProgramModel> cpList = cpDao.getBuiltInCustomPrograms();

        int beginId = id;
        for (CustomProgramModel mdl : cpList) {
        	ProgramSubject ps = new ProgramSubject();

        	ps.setName(String.valueOf(mdl.getProgramId()));
        	ps.setLabel(mdl.getProgramName());
        	ps.setId(id++);
        	progType.getProgramSubjects().add(ps);

        	ProgramChapter pc = new ProgramChapterAll();
        	ps.setId(id++);
        	ps.getChapters().add(pc);

        	ProgramSection pSect = new ProgramSectionAll();
        	pSect.setId(id++);
        	pc.getSections().add(pSect);
        }

        progType.setItemCount(id - beginId);
    	return progType;
	}

	/** Return program type for all proficiency tests
     * 
     * @param conn
     * @param type
     * @return
     * @throws Exception
     */
    private ProgramType createProficiencyProgramType(final Connection conn, String type, String title, int id) throws Exception {
    	
    	HaTestDefDao dao = HaTestDefDao.getInstance();
    	
    	List<HaTestDef> testDefs = dao.getTestDefsForProgramType(conn, type);;
    	
        ProgramType pt = new ProgramType(type,title);
        int beginId = id;

        for (HaTestDef testDef : testDefs) {
            ProgramSubject ps = new ProgramSubject();
            ps.setTestDefId(testDef.getTestDefId());
            ps.setName(testDef.getSubjectId());
            ps.setLabel(testDef.getSubjectName());
            ps.setId(id++);
            pt.getProgramSubjects().add(ps);

            ProgramChapter chapter = new ProgramChapterAll();
            chapter.setId(id++);;
            ps.getChapters().add(chapter);
            
            List<ProgramSection> list = buildSectionListForTestDef(testDef, id);
            id += (list != null) ? list.size() : 0;
            chapter.setSections(list);
        }
        pt.setItemCount(id - beginId);
        return pt;
    }
    
    
    /** Return program type for all Subject and Chapter Lessons
     * 
     * @param conn
     * @param type
     * @return
     * @throws Exception
     */
    private ProgramType createSubjectAndChapterProgramType(final Connection conn, String type, String label, int id) throws Exception {
    	
    	HaTestDefDao dao = HaTestDefDao.getInstance();
    	
    	List<HaTestDef> testDefs = dao.getTestDefs(conn, type);
    	
        ProgramType pt = new ProgramType(type,label);

        int beginId = id;
        for (HaTestDef testDef : testDefs) {
            ProgramSubject ps = new ProgramSubject();
            ps.setTestDefId(testDef.getTestDefId());
            ps.setName(testDef.getSubjectId());
            ps.setLabel(testDef.getSubjectName());
            ps.setId(id++);;
            pt.getProgramSubjects().add(ps);

            List<String> chapNames = dao.getProgramChapters(conn, testDef);
            int i = 1;
            for (String name : chapNames) {
            	ProgramChapter chapter = new ProgramChapter();
            	chapter.setName(name);
            	chapter.setNumber(i);
            	chapter.setId(id++);
                ps.getChapters().add(chapter);

                List<ProgramSection> list = buildSectionListForTestDef(testDef, id);
                id += (list != null) ? list.size() : 0;
                chapter.setSections(list);
                i++;
            }
            
        }
        pt.setItemCount(id - beginId);
        return pt;
    }

    /** Return program type for all Grad Prep Programs
     * 
     * @param conn
     * @param type
     * @return
     * @throws Exception
     */
    private ProgramType createGradPrepProgramType(final Connection conn, String type, String title, int id) throws Exception {
    	
    	// collect all Grad Prep programs
    	String sql = "select * from HA_TEST_DEF where prog_id like 'Grad Prep%' and is_active = 1 order by load_order asc";

        ProgramType pt = new ProgramType(type, "Graduation Preparation");
        int beginId = id;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while(rs.next()) {
            	ProgramSubject ps = new ProgramSubject();
            	ps.setName(rs.getString("test_name"));
            	ps.setTestDefId(rs.getInt("test_def_id"));
            	ps.setId(id++);
                pt.getProgramSubjects().add(ps);

                ProgramChapter chapter = new ProgramChapterAll();
                chapter.setId(id++);
                ps.getChapters().add(chapter);

                HaTestConfig tc = new HaTestConfig(rs.getString("test_config_json"));

                List<ProgramSection> list = buildSectionListForTestConfig(ps.getTestDefId(), tc, id);
                id += (list != null) ? list.size() : 0;
                chapter.setSections(list);
            }
        } finally {
            SqlUtilities.releaseResources(rs, stmt, null);
        }
        pt.setItemCount(id - beginId);
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
    public CmList<ProgramLesson> getLessonsFor(int testDefId, int segment, String chapter, int sectionCount) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            
        	if (logger.isDebugEnabled())
                logger.debug(String.format("getLessonsFor(): testDefId: %d, segment: %d, chapter: %s",
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
                lessons.add(new ProgramLesson(rs.getString("lesson"), rs.getString("file")));
            }
            logger.debug(String.format("+++ getLessonsFor(): found %d lessons", lessons.size()));
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(rs, stmt, conn);
        }
    }

    private List<ProgramSection> buildSectionListForTestDef(HaTestDef testDef, int beginId) {
    	return buildSectionListForTestConfig(testDef.getTestDefId(), testDef.getTestConfig(), beginId);
    }
    
    private List<ProgramSection> buildSectionListForTestConfig(int testDefId, HaTestConfig testConfig, int id) {
    	List<ProgramSection> list = new ArrayList<ProgramSection>();
    	
    	int segmentCount = testConfig.getSegmentCount();
    	
    	for (int i=1; i<=segmentCount; i++) {
    		ProgramSection s = new ProgramSection();
    		s.setTestDefId(testDefId);
    		s.setName(String.format("Section %d", i));
    		s.setNumber(i);
    		s.setId(id++);
    		list.add(s);
    	}
    	return list;
    }

	public CmList<ProgramLesson> getLessonsForBuiltInCustomProg(int programId, String name) throws Exception {
        CmCustomProgramDao cpDao = CmCustomProgramDao.getInstance();
        CmList<CustomLessonModel> list = cpDao.getCustomProgramLessons(programId, name);
        CmList<ProgramLesson> lessons = new CmArrayList<ProgramLesson>();
        for (CustomLessonModel lesson : list) {
        	ProgramLesson pLesson = new ProgramLesson();
        	pLesson.setName(lesson.getLesson());
        	pLesson.setFile(lesson.getFile());
        	lessons.add(pLesson);
        }
		return lessons;
	}

}
