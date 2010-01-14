package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.util.UserInfo.AccountType;
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
        _dao = new CmStudentDao();
        
        if(_user == null)
            TEST_ID = setupDemoAccount();

    }


    public void testCreateSelfRegistrationGroup() throws Exception {
        List<GroupInfoModel> groups = new CmAdminDao().getActiveGroups(conn, _user.getAid());
        
        /** remove all groups
         * 
         */
        for(GroupInfoModel g: groups) {
            new CmAdminDao().deleteGroup(conn, _user.getAid(),g.getId());
        }
        
        String testGroup = "TEST_SELF_REG";
        
        conn.createStatement().executeUpdate("delete from HA_USER where admin_id = " + _user.getAid() + " and user_name = '" + testGroup + "'");

        
        new CmAdminDao().createSelfRegistrationGroup(conn, _user.getAid(), testGroup, CmProgram.ALG1_PROF, false, true);
        
        groups = new CmAdminDao().getActiveGroups(conn, _user.getAid());
        
        boolean found=false;
        for(GroupInfoModel g: groups) {
            if(g.getName().equals(testGroup)) {
                found=true;
                List<StudentModelI> students = new CmStudentDao().getStudentModelByUserName(conn, _user.getAid(), testGroup);
                assertTrue(students.size() > 0);
                
                assertTrue(students.get(0).getProgId().equals(CmProgram.ALG1_PROF.getProgramId()));
            }
        }
        
        assertTrue(found);
    }


    public void testGetAccountType() throws Exception {
        AccountType ac = new CmAdminDao().getAccountType(conn,_user.getAid());
        assertTrue(ac == AccountType.SCHOOL_TEACHER);
    }
    
    
    public void testUpdateGroup() throws Exception {
        GroupInfoModel gm = setupDemoGroup();
        String newGroupName = gm.getName() + "_updated";
        new CmAdminDao().updateGroup(conn,gm.getId(),newGroupName);
        
        // how to test?
    }

    
    public void testDeleteGroup() throws Exception {
        GroupInfoModel gm = setupDemoGroup();
        new CmAdminDao().deleteGroup(conn,_user.getUid(),gm.getId());
        assertFalse(new CmAdminDao().checkForDuplicateGroup(conn, _user.getAid(), gm));
    }

    
    public void testGetSubjectDefinitions() throws Exception {
        List<SubjectModel> sm = new CmAdminDao().getSubjectDefinitions(CmProgram.ALG1_PROF.getProgramId());
        assertNotNull(sm);
        assertTrue(sm.size() > 0);
    }
    
    public void testGetChaptersForProgramSubject() throws Exception {
        CmAdminDao dao = new CmAdminDao();
        CmProgram p = CmProgram.GEOM_CHAP;
        List<ChapterModel> chaps = dao.getChaptersForProgramSubject(conn,p.getProgramId(), p.getSubject());
        assertNotNull(chaps);
        assertTrue(chaps.size() > 0);
        
        // make sure order is OK
        assertTrue(Integer.parseInt(chaps.get(0).getNumber()) < Integer.parseInt(chaps.get(chaps.size()-1).getNumber()));
    }

    
    public void testStudentActiveInfo() throws Exception {
        StudentActiveInfo activeInfo = _dao.loadActiveInfo(conn, TEST_ID);
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
        StudentModelI sm = _dao.getStudentModel(conn, TEST_ID, false);
        _dao.assignProgramToStudent(conn, TEST_ID,CmProgram.PREALG_CHAP, "Integers");
        
        StudentUserProgramModel pi = new CmUserProgramDao().loadProgramInfoCurrent(conn, TEST_ID);
        assertNotNull(pi);
        assertTrue(pi.getConfig().getChapters().get(0).equals("Integers"));
    }
    
    
    public void testSetStudentProgram() throws Exception {

        /**
         * change it once to Alg 2
         * 
         */
        StudentModelI sm = _dao.getStudentModel(TEST_ID);
        sm.setProgId("Prof");
        sm.setSubjId("Alg 2");
        sm.setProgramChanged(true);
        _dao.updateStudent(conn, sm, true, false, true, false);

        StudentUserProgramModel progInfo =new CmUserProgramDao().loadProgramInfoCurrent(conn, TEST_ID);
        assertTrue(progInfo.getTestName().equals("Algebra 2 Proficiency"));

        
        /**
         * change it again to Alg 1
         * 
         */
        sm = _dao.getStudentModel(TEST_ID);
        sm.setProgId("Prof");
        sm.setSubjId("Alg 1");
        sm.setProgramChanged(true);
        _dao.updateStudent(conn, sm, true, false, true, false);

        progInfo = new CmUserProgramDao().loadProgramInfoCurrent(conn, TEST_ID);
        assertTrue(progInfo.getTestName().equals("Algebra 1 Proficiency"));
        
        
        /** Change it again to Pre-Alg
         * 
         */
        sm = _dao.getStudentModel(TEST_ID);
        sm.setProgId("Prof");
        sm.setSubjId("Pre-Alg");
        sm.setProgramChanged(true);
        _dao.updateStudent(conn, sm, true, false, true, false);

        progInfo = new CmUserProgramDao().loadProgramInfoCurrent(conn, TEST_ID);
        assertTrue(progInfo.getTestName().equalsIgnoreCase("Pre-algebra Proficiency"));
    }

    public void testLoadStudentUserProgramModel() throws Exception {
        StudentUserProgramModel up = new CmUserProgramDao().loadProgramInfoCurrent(conn, TEST_ID);
        assertNotNull(up);

        assertTrue(up.getTestDefId() > 0);
        assertNotNull(up.getTestName());
    }


}
