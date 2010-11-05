package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;

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
    
    public void testCustGetAll() throws Exception {
        /** requires row for samples HA_CUSTOM_PROGRAM */
        CmList<CustomLessonModel> models = new CmCustomProgramDao().getAllLessons(conn);
        assertTrue(models.size() > 0);
    }
    
    public void testCustProgs() throws Exception {
        /** requires row for samples HA_CUSTOM_PROGRAM */
        assertTrue(new CmCustomProgramDao().getCustomPrograms(conn, 2).size() > 0);
    }
    
    public void testCreateNew() throws Exception {
        List<CustomLessonModel> lessons = new ArrayList<CustomLessonModel>();
        lessons.add(new CustomLessonModel("test lesson", "test file", "test subject"));
        new CmCustomProgramDao().createNewCustomProgram(conn, _user.getAid(),"New Custom Program Test", lessons);    
    }
      
    
    public void testCreate() throws Exception {
      List<CustomLessonModel> lessons = new ArrayList<CustomLessonModel>();
      lessons.add(new CustomLessonModel("test lesson", "test file", "test subject"));
      new CmCustomProgramDao().saveChanges(conn, 1,"Test Name", lessons);    
    }
    
    public void testDelete() throws Exception {
        /** todo .. actually test it! */
        new CmCustomProgramDao().deleteCustomProgram(conn,-1);
    }

    public void testGetAll() throws Exception {
        assertTrue(new CmCustomProgramDao().getAllLessons(conn).size() > 0);
    }

}
