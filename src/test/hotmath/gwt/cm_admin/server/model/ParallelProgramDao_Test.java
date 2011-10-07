package hotmath.gwt.cm_admin.server.model;

import java.util.Date;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgramAssign;
import hotmath.gwt.cm_rpc.client.model.CmProgramInfo;
import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

import org.junit.Test;

public class ParallelProgramDao_Test extends CmDbTestCase {


    public ParallelProgramDao_Test(String name) throws Exception {
        super(name);
    }

    ParallelProgramDao _dao;

    int progInstId;

    protected void setUp() throws Exception {
    	super.setUp();
        _dao = ParallelProgramDao.getInstance();
    }

    @Test
    public void testAddProgram() throws Exception  {

        CmProgram prog = new CmProgram();
        CmProgramInfo progInfo = prog.getCmProgInfo();
        
        prog.setAdminId(6);

        progInfo.setProgramType(CmProgramType.PROF);
        progInfo.setSubjectId("Alg 1");

        prog.setPassPercent(70);
        prog.setCustomProgId(0);
        prog.setCustomQuizId(0);
               		
        _dao.addProgram(prog);

        progInstId = prog.getId();
    	assert(prog.getId() > 0);
    }

    @Test
    public void testAddParallelProgram() throws Exception  {

        CmParallelProgram prog = new CmParallelProgram();
        prog.setAdminId(6);
        prog.setCmProgId(progInstId);
        prog.setCreateDate(new Date());
        prog.setName("Test PP");
        prog.setPassword("Password: " + System.currentTimeMillis());
                       		
        _dao.addParallelProgram(prog);
    	
    	assert(prog.getId() > 0);
    }

    @Test
    public void testIsParallelProgramAssignedToStudent() throws Exception  {

        boolean isAssigned = _dao.isParallelProgramAssignedToStudent(2, "188580");
    	
    	assert(isAssigned);
    }
    
    @Test
    public void testIsParallelProgramStudent() throws Exception {
    	boolean isParallelProgramStudent = _dao.isParallelProgramStudent(2, "188581");
    	
    	assert(isParallelProgramStudent);
    }

    @Test
    public void testParallelProgramPrevAssignedToStudent() throws Exception {
    	boolean prevAssigned = _dao.parallelProgramPrevAssignedToStudent(2, "cm425c");
    	
    	assert(prevAssigned == false);
    }

    @Test
    public void testGetStudentUserId() throws Exception {
    	int userId = _dao.getStudentUserId(2, "188580");
    	
    	assert (userId == 9456);
    }

    @Test
    public void testAddProgramAssignment() throws Exception {
    	hotmath.gwt.cm_rpc.client.model.CmProgram cmProg = _dao.addCurrentProgramForStudent(9461);
    	
    	StudentModelI sm = CmStudentDao.getInstance().getStudentModelBase(conn, 9461);
		CmProgramAssign cmProgAssign = new CmProgramAssign();
		cmProgAssign.setCmProgram(cmProg);
		cmProgAssign.setUserId(9461);
		cmProgAssign.setUserProgId(sm.getProgram().getProgramId());
		_dao.addProgramAssignment(cmProgAssign);
    }

    @Test
    public void testGetCmProgramForId() throws Exception {
    	CmProgram cmProg = _dao.getCmProgramForId(18);
    	
    	assert (cmProg != null);
    	
    	System.out.println("CmProgram: " + cmProg);
    }

    @Test
    public void testGetCmProgramForParallelProgramId() throws Exception {
    	CmProgram cmProg = _dao.getCmProgramForParallelProgramId(2);
    	
    	assert (cmProg != null);
    	
    	System.out.println("CmProgram: " + cmProg);
    }
 }
