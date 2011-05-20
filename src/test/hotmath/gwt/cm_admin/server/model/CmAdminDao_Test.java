package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.StudentUserProgramModel;

import java.util.List;

public class CmAdminDao_Test extends CmDbTestCase {

    /**
     * @TODO: create method to generate test accounts for testing
     * 
     */
    static int TEST_ID;

    public CmAdminDao_Test(String name) throws Exception {
        super(name);
    }

    CmStudentDao _dao;

    protected void setUp() throws Exception {
        super.setUp();
        _dao = CmStudentDao.getInstance();
        
        if(_user == null)
            TEST_ID = setupDemoAccount();

    }
    
    public void testGetGroup() throws Exception {
        GroupInfoModel ac = CmAdminDao.getInstance().getGroup(conn, _user.getAid(), "Teacher-1");
        assertTrue(ac != null);
    }
    
    public void testGetInfo() throws Exception {
        AccountInfoModel ac = CmAdminDao.getInstance().getAccountInfo(_user.getAid());
        assertTrue(ac != null);
    }
    
    public void testGetAccountType() throws Exception {
        AccountType ac = CmAdminDao.getInstance().getAccountType(conn,_user.getAid());
        assertTrue(ac == AccountType.SCHOOL_TEACHER);
    }
    
    
    public void testSetEmail() throws Exception {
        CmAdminDao.getInstance().setAdminPassword(conn, _user.getAid(),"test@test.com");
    }


    public void testCreateSelfRegistrationGroup() throws Exception {
        List<GroupInfoModel> groups = CmAdminDao.getInstance().getActiveGroups( _user.getAid());
        
        /** remove all groups
         * 
         */
        for(GroupInfoModel g: groups) {
            CmAdminDao.getInstance().deleteGroup(conn, _user.getAid(),g.getId());
        }
        
        String testGroup = "TEST_SELF_REG";
        
        conn.createStatement().executeUpdate("delete from HA_USER where admin_id = " + _user.getAid() + " and user_name = '" + testGroup + "'");

        
        CmAdminDao.getInstance().createSelfRegistrationGroup(conn, _user.getAid(), testGroup, CmProgram.ALG1_PROF, false, true);
        
        groups = CmAdminDao.getInstance().getActiveGroups(_user.getAid());
        
        boolean found=false;
        for(GroupInfoModel g: groups) {
            if(g.getName().equals(testGroup)) {
                found=true;
                List<StudentModelI> students = CmStudentDao.getInstance().getStudentModelByUserName(conn, _user.getAid(), testGroup);
                assertTrue(students.size() > 0);
                
                assertTrue(students.get(0).getProgram().getProgramType().equals(CmProgram.ALG1_PROF.getProgramType()));
            }
        }
        
        assertTrue(found);
    }


    public void testUpdateGroup() throws Exception {
        GroupInfoModel gm = setupDemoGroup();
        String newGroupName = gm.getName() + "_updated";
        CmAdminDao.getInstance().updateGroup(conn, _user.getAid(), gm.getId(),newGroupName);
        assertFalse(CmAdminDao.getInstance().checkForDuplicateGroup(conn, _user.getAid(), gm.getName()));
    }

    
    public void testDeleteGroup() throws Exception {
        GroupInfoModel gm = setupDemoGroup();
        CmAdminDao.getInstance().deleteGroup(conn,_user.getUid(),gm.getId());
        assertFalse(CmAdminDao.getInstance().checkForDuplicateGroup(conn, _user.getAid(), gm.getName()));
    }

    
    public void testGetSubjectDefinitions() throws Exception {
        List<SubjectModel> sm = CmAdminDao.getInstance().getSubjectDefinitions(CmProgram.ALG1_PROF.getProgramType());
        assertNotNull(sm);
        assertTrue(sm.size() > 0);
    }
    
    public void testGetChaptersForProgramSubject() throws Exception {
        CmAdminDao dao = CmAdminDao.getInstance();
        CmProgram p = CmProgram.GEOM_CHAP;
        List<ChapterModel> chaps = dao.getChaptersForProgramSubject(conn,p.getProgramType(), p.getSubject());
        assertNotNull(chaps);
        assertTrue(chaps.size() > 0);
        
        // make sure order is OK
        assertTrue(Integer.parseInt(chaps.get(0).getNumber()) < Integer.parseInt(chaps.get(chaps.size()-1).getNumber()));
    }

    
    public void testStudentActiveInfo() throws Exception {
        StudentActiveInfo activeInfo = _dao.loadActiveInfo(TEST_ID);
        assertNotNull(activeInfo);
    }

    public void testRestStudentActiveInfo() throws Exception {
        StudentActiveInfo activeInfo = new StudentActiveInfo();
        activeInfo.setActiveTestId(100);
        _dao.setActiveInfo(conn, TEST_ID, activeInfo);
    }

    public void testSetStudentProgramChapter() throws Exception {

        /**
         * change it once to Pre-Alg Integers
         * 
         */
        StudentModelI sm = _dao.getStudentModelBase(conn, TEST_ID, false);
        _dao.assignProgramToStudent(conn, TEST_ID,CmProgram.PREALG_CHAP, "Integers");
        
        StudentUserProgramModel pi = CmUserProgramDao.getInstance().loadProgramInfoCurrent(TEST_ID);
        assertNotNull(pi);
        assertTrue(pi.getConfig().getChapters().get(0).equals("Integers"));
    }
    
    
    public void testSetStudentProgram() throws Exception {

        /**
         * change it once to Alg 2
         * 
         */
        StudentModelI sm = _dao.getStudentModelBase(conn, TEST_ID);
        sm.getProgram().setProgramType("Prof");
        sm.getProgram().setSubjectId("Alg 2");
        sm.setProgramChanged(true);
        _dao.updateStudent(conn, sm, true, false, true, false,false);

        StudentUserProgramModel progInfo =CmUserProgramDao.getInstance().loadProgramInfoCurrent(TEST_ID);
        assertTrue(progInfo.getTestName().equals("Algebra 2 Proficiency"));

        
        /**
         * change it again to Alg 1
         * 
         */
        sm = _dao.getStudentModelBase(conn, TEST_ID);
        sm.getProgram().setProgramType("Prof");
        sm.getProgram().setSubjectId("Alg 1");
        sm.setProgramChanged(true);
        _dao.updateStudent(conn, sm, true, false, true, false,false);

        progInfo = CmUserProgramDao.getInstance().loadProgramInfoCurrent(TEST_ID);
        assertTrue(progInfo.getTestName().equals("Algebra 1 Proficiency"));
        
        
        /** Change it again to Pre-Alg
         * 
         */
        sm = _dao.getStudentModelBase(conn, TEST_ID);
        sm.getProgram().setProgramType("Prof");
        sm.getProgram().setSubjectId("Pre-Alg");
        sm.setProgramChanged(true);
        _dao.updateStudent(conn, sm, true, false, true, false,false);

        progInfo = CmUserProgramDao.getInstance().loadProgramInfoCurrent(TEST_ID);
        assertTrue(progInfo.getTestName().equalsIgnoreCase("Pre-algebra Proficiency"));
    }

    public void testLoadStudentUserProgramModel() throws Exception {
        StudentUserProgramModel up = CmUserProgramDao.getInstance().loadProgramInfoCurrent(TEST_ID);
        assertNotNull(up);

        assertTrue(up.getTestDefId() > 0);
        assertNotNull(up.getTestName());
    }
}
