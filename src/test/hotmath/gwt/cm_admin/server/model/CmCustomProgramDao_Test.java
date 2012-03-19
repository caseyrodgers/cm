package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.model.CustomQuizDef;

import java.util.ArrayList;
import java.util.List;

public class CmCustomProgramDao_Test extends CmDbTestCase {
    
    public CmCustomProgramDao_Test(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(_user == null)
            setupDemoAccount();
    }
    
    public void testCustGetQuizInfo() throws Exception {
        CustomQuizDef def = CmQuizzesDao.getInstance().getCustomQuizDefinitions(2).get(0);
        assertTrue(CmCustomProgramDao.getInstance().getCustomQuizInfo(conn,2, def.getQuizId()).getAssignedStudents().size() > 0);
    }
    public void testCustGetAll() throws Exception {
        /** requires row for samples HA_CUSTOM_PROGRAM */
        CmList<CustomLessonModel> models = CmCustomProgramDao.getInstance().getAllLessons(conn);
        assertTrue(models.size() > 0);
    }
    
    public void testCustProgs() throws Exception {
        /** requires row for samples HA_CUSTOM_PROGRAM */
        assertTrue(CmCustomProgramDao.getInstance().getCustomPrograms(conn, 2).size() > 0);
    }
    
    public void testCreateNew() throws Exception {
        String programName = "New Custom Program Test: " + System.currentTimeMillis();
        
        List<CustomLessonModel> lessons = new ArrayList<CustomLessonModel>();
        lessons.add(new CustomLessonModel("Square Root", "topics/square-roots.html", "alg1"));
        lessons.add(new CustomLessonModel("Square Root", "topics/square-roots.html", "alg1"));        
        CmCustomProgramDao.getInstance().createNewCustomProgram(conn, _user.getAid(),programName, lessons);
    }
    
    public void testDelete() throws Exception {
        /** todo .. actually test it! */
        CmCustomProgramDao.getInstance().deleteCustomProgram(conn,-1);
    }

    public void testGetAll() throws Exception {
        assertTrue(CmCustomProgramDao.getInstance().getAllLessons(conn).size() > 0);
    }
    
    public void testGetCustomProgramById() throws Exception {
    	assertTrue(CmCustomProgramDao.getInstance().getCustomProgram(7) != null);
    }

}
