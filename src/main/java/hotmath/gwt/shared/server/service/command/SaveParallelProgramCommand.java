package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.CmUserException;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgramInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.shared.client.rpc.action.SaveParallelProgramAction;

import org.apache.log4j.Logger;

import java.sql.Connection;

/** Save Parallel Program setup for this Admin / Group
 * 
 *  Information will be saved in HA_USER_TEMPLATE
 *  The student will login using the usual username (school passcode) and the password,
 *  which is the Parallel Program Group Name.
 *  
 * @author bob
 *
 */
public class SaveParallelProgramCommand implements ActionHandler<SaveParallelProgramAction, RpcData> {

	private static final Logger LOGGER = Logger.getLogger(SaveParallelProgramCommand.class);

    @Override
    public RpcData execute(Connection conn, SaveParallelProgramAction action) throws Exception {
    
        StudentModelI student = action.getStudent();
        
        // Parallel Program password cannot be the same as a student password for the current Admin
        // or the Password for an existing Parallel Program
        student.setPasscode(student.getName().trim());
        Boolean isPasscodeTaken = CmStudentDao.getInstance().checkForDuplicatePasscode(conn, student);
        if (isPasscodeTaken == true) {
            throw new CmUserException("Parallel Program name cannot be the same as the password of a student, the name of a Self Registration Group, or the name of another Parallel Program.");
        }

        // set test config JSON
        CmStudentDao.getInstance().setTestConfig(conn, student);
        
        // Parallel Program with the supplied password does not exist, create it
        // first add CmProgram (CM_PROGRAM)
        CmProgram prog = new CmProgram();
        CmProgramInfo progInfo = prog.getCmProgInfo();
        
        prog.setAdminId(student.getAdminUid());

        StudentProgramModel progMdl = student.getProgram();
        progMdl.getSubjectId();

        progInfo.setProgramType(progMdl.getProgramType());
        progInfo.setSubjectId(progMdl.getSubjectId());
        prog.setTestConfigJson(student.getJson());

        int passPercent = CmStudentDao.getInstance().getPercentFromString(student.getPassPercent());
        prog.setPassPercent(passPercent);
        prog.setCustomProgId(progMdl.getCustom().getCustomProgramId());
        prog.setCustomQuizId(progMdl.getCustom().getCustomQuizId());
               		
        ParallelProgramDao ppDao = ParallelProgramDao.getInstance();
        ppDao.addProgram(prog);
        
        CmParallelProgram pp = new CmParallelProgram();
        pp.setCmProgId(prog.getId());
        pp.setAdminId(action.getAdminId());
        pp.setPassword(student.getPasscode());
        pp.setName(student.getName());
        ppDao.addParallelProgram(pp);

        student.setPasscode(student.getPasscode() + "_" + System.currentTimeMillis());  // make unique
        student.getProgram().setProgramId(prog.getId());
        if (student.getSectionNum() == null) student.setSectionNum(0);
        CmStudentDao.getInstance().addStudentTemplate(student, "parallel-program");
        
        RpcData rdata = new RpcData("status=OK");
        return rdata;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveParallelProgramAction.class;
    }
}
