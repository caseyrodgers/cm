package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgramInfo;
import hotmath.gwt.cm_rpc_core.client.CmUserException;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.shared.client.rpc.action.SaveParallelProgramAction;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Save Parallel Program setup for this Admin
 * 
 * The student will login using the usual username (school passcode) and the
 * password, which is the Parallel Program Name.
 * 
 * @author bob
 * 
 */
public class SaveParallelProgramCommand implements ActionHandler<SaveParallelProgramAction, RpcData> {

    private static final Logger LOGGER = Logger.getLogger(SaveParallelProgramCommand.class);

    @Override
    public RpcData execute(Connection conn, SaveParallelProgramAction action) throws Exception {

        try {
           
            StudentModelI student = action.getStudent();

            Integer parallelProgId = action.getParallelProgId();

            // Parallel Program password cannot be the same as a student
            // password for the current Admin
            // or the Password for an existing Parallel Program (unless
            // modifying an existing PP)
            student.setPasscode(student.getName().trim());
            int studentUid = (student.getUid() == null) ? -1 : student.getUid();
            boolean isPasscodeTaken =
            		CmStudentDao.getInstance().checkForDuplicatePasscode(conn, student.getAdminUid(), studentUid, student.getPasscode());
            if (isPasscodeTaken == true) {
                if (action.getParallelProgId() == null
                        || (passwordsMatch(student.getPasscode(), parallelProgId) == false)) {
                    throw new CmUserException(
                            "Parallel Program name cannot be the same as the password of a student, the name of a Self Registration Group, or the name of another Parallel Program.");
                }
            }

            // set test config JSON
            CmStudentDao.getInstance().setTestConfig(conn, student);

            // init CmProgram (CM_PROGRAM)
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

            if (action.getParallelProgId() == null) {
                ppDao.addProgram(prog);
                CmParallelProgram pp = new CmParallelProgram();
                
                pp.setCmProgId(prog.getId());
                pp.setAdminId(action.getAdminId());
                pp.setPassword(student.getPasscode());
                pp.setName(student.getName());
                
                ppDao.addParallelProgram(pp);

            } else {
                ppDao.updateProgram(prog, parallelProgId);

                CmParallelProgram pp = new CmParallelProgram();
                pp.setCmProgId(prog.getId());
                pp.setAdminId(action.getAdminId());
                pp.setPassword(student.getPasscode());
                pp.setName(student.getName());
                ppDao.updateParallelProgram(pp, parallelProgId);
            }
            
            RpcData rdata = new RpcData("status=OK");
            return rdata;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean passwordsMatch(String password, int parallelProgId) throws Exception {
        ParallelProgramDao dao = ParallelProgramDao.getInstance();
        CmParallelProgram pp = dao.getParallelProgramForId(parallelProgId);
        return password.equalsIgnoreCase(pp.getName());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveParallelProgramAction.class;
    }
}
