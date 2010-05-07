package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTestConfig;

import java.util.List;

public class CmProgramListingDao_Test extends CmDbTestCase {
    
    public CmProgramListingDao_Test(String name) {
        super(name);
    }
    
    public void testGetLessons() throws Exception {
        HaTestConfig testConfig = new HaTestConfig();
        List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG1_PROF.getDefId(),1, testConfig);
        assertTrue(lessons.size() > 0);
    }
}
