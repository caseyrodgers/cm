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

    public ProgramListing getProgramListing(final Connection conn, int adminId) throws Exception {
        try {
            ProgramListing pr = new ProgramListing();
            pr.getProgramTypes().add(createProficiencyProgramType(conn, "Proficiency"));
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
    private ProgramType createProficiencyProgramType(final Connection conn, String type) throws Exception {
    	
    	HaTestDefDao dao = new HaTestDefDao();
    	List<Integer> testDefIds = new ArrayList<Integer>();
    	testDefIds.add(CmProgram.PREALG_PROF.getDefId());
    	testDefIds.add(CmProgram.ALG1_PROF.getDefId());
    	testDefIds.add(CmProgram.GEOM_PROF.getDefId());
    	testDefIds.add(CmProgram.ALG2_PROF.getDefId());
    	
    	List<HaTestDef> testDefs = dao.getTestDefs(conn, testDefIds);
    	
        ProgramType pt = new ProgramType(type);

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
/*   	
        CmProgram[] programs = { CmProgram.PREALG_PROF, CmProgram.ALG1_PROF, CmProgram.GEOM_PROF, CmProgram.ALG2_PROF};
        for(CmProgram program: programs) {
            ProgramSubject ps = new ProgramSubject();
            ps.setName(program.getSubject());
            pt.getProgramSubjects().add(ps);

            ProgramChapter chapters = new ProgramChapterAll();
            ps.getChapters().add(chapters);
        }
*/
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
    public CmList<ProgramLesson> getLessonsFor(final Connection conn, int testDefId, int segment, HaTestConfig testConfig) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            CmList<ProgramLesson> lessons = new CmArrayList<ProgramLesson>();
            
            logger.info(String.format("+++ getLessonsFor(): testDefId: %d, segment: %d", testDefId, segment));
            
            /** first get list of PIDS that make this quiz segment
             * 
             */
            HaTestDefDao hda = new HaTestDefDao();
            HaTestDef testDef = hda.getTestDef(conn,testDefId);

            StudentUserProgramModel userProgram = new StudentUserProgramModel();
            userProgram.setTestDef(testDef);
            List<String> pids = hda.getTestIdsForSegment(conn,userProgram,2,testDef.getTextCode(),testDef.getChapter(),testConfig,0);
            
            /** now get list of lessons assigned to these pids by looking in HM_PROGRAM_LESSONS which is created
             *  by the HA_PRESCRIPTION_LOG Deploylet action.  This is a static table that holds information about
             *  lessons .. and must be rebuilt on data changes.
             *  
             */
            /** construct pid in list */
            String pidList="";
            for(String id : pids) {
                if(pidList.length()>0) pidList += ",";
                pidList += "'" + id + "'";
            }
            Map<String,String> map = new HashMap<String,String>(); 
            map.put("PIDLIST", pidList);
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_PROGRAM_LESSONS", map);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                lessons.add(new ProgramLesson(rs.getString("lesson")));
            }
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(rs,stmt,null);
        }
    }
    
    private List<ProgramSection> buildSectionListForProficiencyTestDef(HaTestDef testDef) {
    	List<ProgramSection> list = new ArrayList<ProgramSection>();
    	
    	int segmentCount = testDef.getTestConfig().getSegmentCount();
    	
    	for (int i=1; i<=segmentCount; i++) {
    		ProgramSection s = new ProgramSection();
    		s.setName("Section " + i);
    		list.add(s);
    	}
    	return list;
    }
    
}
