package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelBase;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.shared.client.model.UserProgramIsNotActiveException;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.StudentUserProgramModel;

import java.util.ArrayList;
import java.util.List;

public class CmStudentDao_Test extends CmDbTestCase {


    public CmStudentDao_Test(String name) throws Exception {
        super(name);
    }

    CmStudentDao _dao;

    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
        
        _dao = CmStudentDao.getInstance();
    }
    
    
    public void testAddStudent() throws Exception  {
        StudentModelI sm = new StudentModelBase();
        sm.setName("name_" + System.currentTimeMillis());
        sm.setAdminUid(_user.getAid());
        sm.getProgram().setProgramType(CmProgram.PREALG_PROF.getProgramType());
        sm.getProgram().setSubjectId(CmProgram.PREALG_PROF.getSubject());
        sm.setSectionNum(0);
        sm.setGroupId(1);
        sm.setPasscode("pass_" + System.currentTimeMillis());
        StudentModelI sm2 = _dao.addStudent(conn,sm);
        assertNotNull(sm2);
    }
    
    public void testAssignCustomProgram() throws Exception {
        StudentProgramModel spm = new StudentProgramModel();
        spm.getCustom().setCustomProgramId(7);
        spm.setProgramType(CmProgram.CUSTOM_PROGRAM.getProgramType());
        spm.setSubjectId("");
        _dao.assignProgramToStudent(conn, _user.getUid(), spm,null,null);
        
        StudentModelI sm = _dao.getStudentModelBase(conn, _user.getUid());
        assertTrue(sm.getProgram().getCustom().getCustomProgramId() == spm.getCustom().getCustomProgramId());
    }
    
    
    public void testVerifyActiveProgram() throws Exception {
       try {
           _dao.verifyActiveProgram(_test.getTestId());
       }
       catch(UserProgramIsNotActiveException up) {
           assertTrue(true);
       }
       try {
           _dao.verifyActiveProgram(-11);
           assertTrue(true);
       }
       catch(UserProgramIsNotActiveException up) {
          /** expected */
       }       
    }
    
    
    public void testupdateStudentMainProperties() throws Exception {
        _dao.updateStudentMainProperties(conn, _user.getUid(), true,true,90);
        
        StudentModelI sm = _dao.getStudentModelBase(conn, _user.getUid());
        assertTrue(sm.getSettings().getTutoringAvailable() == false);
    }
    
    
    public void testgetTotalInmHViewCount() throws Exception {
        Integer count = _dao.getTotalInmHViewCount(conn,_test.getTestId());
        assertNotNull(count);
    }
    
    public void testGetStudentBasic() throws Exception {
        StudentModelI sm = _dao.getStudentModel(conn,_user.getUid());
        assertNotNull(sm);
        assertTrue(sm.getUid() > 0);
    }
    
    public void testGetStudentModelBase() throws Exception {
        StudentModelI sm = _dao.getStudentModelBase(conn, _user.getUid(), false);
        assertNotNull(sm);
        assertTrue(sm.getUid() > 0);    	
    }
    
    public void testGetStudentActive() throws Exception {
        StudentActiveInfo active = _dao.loadActiveInfo(_user.getUid());
        assertNotNull(active);
    }
    
    public void testGetStudentExtendedSummaries() throws Exception {
    	List<Integer> studentUids = new ArrayList<Integer>();
    	
    	studentUids.add(_user.getUid());
    	studentUids.add(_user.getUid());
    	studentUids.add(_user.getUid());

    	List<StudentModelI> l = _dao.getStudentExtendedSummaries(conn, studentUids);
    	assertTrue(l.size() > 0);
    }
    
    public void testGetStudentProgram() throws Exception {
        StudentUserProgramModel pi = CmUserProgramDao.getInstance().loadProgramInfoCurrent(_user.getUid());
        assertNotNull(pi);
        assertTrue(pi.getTestDefId() > 0);
    }
 }
