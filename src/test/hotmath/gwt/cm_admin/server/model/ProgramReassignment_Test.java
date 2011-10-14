package hotmath.gwt.cm_admin.server.model;

import java.util.Date;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgramAssign;
import hotmath.gwt.cm_rpc.client.model.CmProgramInfo;
import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.server.service.command.ParallelProgramLoginCommand;

import org.junit.Test;

public class ProgramReassignment_Test extends CmDbTestCase {


    public ProgramReassignment_Test(String name) throws Exception {
        super(name);
    }

    CmStudentDao stuDao;
    ParallelProgramDao ppDao;
    ParallelProgramLoginCommand loginCmd;

    StudentActiveInfo activeInfoMainProg;
    StudentActiveInfo activeInfoParallelProg1;
    StudentActiveInfo activeInfoParallelProg2;
    
    CmProgram cmProg1;
    CmProgram cmProg2;

    int progInstId;
    CmProgram cmProg;
    
    static final int TEST_ID = 25828;

    protected void setUp() throws Exception {
    	super.setUp();
        ppDao = ParallelProgramDao.getInstance();
        stuDao = CmStudentDao.getInstance();
        loginCmd = new ParallelProgramLoginCommand();
    }

    @Test
    public void testReassignMainProgram() throws Exception  {

        boolean isStudentInParallelProg = ppDao.isStudentInParallelProgram(TEST_ID);
        
        if (isStudentInParallelProg == true) {
        	ppDao.reassignMainProgram(TEST_ID);
        	isStudentInParallelProg = ppDao.isStudentInParallelProgram(TEST_ID);
        	assert(isStudentInParallelProg == false);
        }
        // for comparison later
        //activeInfoMainProg = stuDao.loadActiveInfo(TEST_ID); 
        
        // "login" to 1st PP
        loginCmd.assignParallelProgram(conn, 34, TEST_ID, null);
        //activeInfoParallelProg1 = stuDao.loadActiveInfo(TEST_ID);

        // reassign Main Program
        ppDao.reassignMainProgram(TEST_ID);
        //StudentActiveInfo testAI = stuDao.loadActiveInfo(TEST_ID);
        // TODO: compare Active Info
        
        // "login" to 2nd PP
        loginCmd.assignParallelProgram(conn, 35, TEST_ID, null);
        //activeInfoParallelProg2 = stuDao.loadActiveInfo(TEST_ID);
        
        // "login" to 1st PP
        loginCmd.assignParallelProgram(conn, 34, TEST_ID, null);
        //testAI = stuDao.loadActiveInfo(TEST_ID);
        // TODO: compare Active Info
        
        // reassign Main Program
        ppDao.reassignMainProgram(TEST_ID);
        //testAI = stuDao.loadActiveInfo(TEST_ID);
        // TODO: compare Active Info
        
        // "login" to 2nd PP
        loginCmd.assignParallelProgram(conn, 35, TEST_ID, null);
        //testAI = stuDao.loadActiveInfo(TEST_ID);
        // TODO: compare Active Info
        
        // reassign Main Program
        ppDao.reassignMainProgram(TEST_ID);
        //testAI = stuDao.loadActiveInfo(TEST_ID);
        // TODO: compare Active Info
        
    }

}
