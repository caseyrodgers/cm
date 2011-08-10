package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.Map;

import org.junit.Test;

public class StudentActivityDao_Test extends CmDbTestCase {


    public StudentActivityDao_Test(String name) throws Exception {
        super(name);
    }

    StudentActivityDao _dao;

    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
        
        _dao = StudentActivityDao.getInstance();
    }

    @Test
    public void testGetActivityTimeMap() throws Exception  {
    	Map<StudentActivityDao.ActivityTypeEnum, StudentActivityDao.ActivityTime> atMap =
    		StudentActivityDao.getInstance().getActivityTimeMap();
    	
    	assertNotNull(atMap);
    	
    	assert(atMap.size() > 0);
    }
    
 }
