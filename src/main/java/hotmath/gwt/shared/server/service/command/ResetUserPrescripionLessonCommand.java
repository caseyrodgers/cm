package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.ResetUserPrescripionLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/**
 * Reset the solution context of the named user's current program lesson
 * or for a single problem in a lesson.
 * 
 * The end result is the content will be recreated on next visit to problem.  The 
 * existing context will be incorrect, so any variables specified shown on whiteboard
 * would be different.
 * 
 * @author casey
 * 
 */
public class ResetUserPrescripionLessonCommand implements ActionHandler<ResetUserPrescripionLessonAction, RpcData> {

    static private Logger __logger = Logger.getLogger(ResetUserPrescripionLessonCommand.class);

    @Override
    public RpcData execute(Connection conn, ResetUserPrescripionLessonAction action) throws Exception {
        CmProgramFlow cmProgram = new CmProgramFlow(conn, action.getUserId());
        int runId = cmProgram.getActiveInfo().getActiveRunId();
        if (runId == 0) {
            throw new CmException("This user is not currently in a prescription");
        }
        int lessonNumber = cmProgram.getActiveInfo().getActiveRunSession();
        if (action.getPidInLesson() == null) {
            resetEntireLesson(conn, runId, action.getUserId(), cmProgram, lessonNumber);
        }
        else {
            resetProblemInLesson(conn, runId, action.getUserId(), cmProgram, action.getPidInLesson(),lessonNumber);
        }
        return new RpcData("status=OK");
    }

    private void resetProblemInLesson(Connection conn, int runId, int userId, CmProgramFlow program, String pidInLesson, int lessonNumber) throws Exception {
        PreparedStatement ps = null;
        try {
            __logger.info("Resetting user problem '" + pidInLesson + "' in lesson: " + userId + ", " + program.getActiveInfo());
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_USER_LESSON_PROBLEM");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ps.setString(2, pidInLesson);

            int cnt = ps.executeUpdate();
            __logger.debug("Reset user lesson: " + cnt + "\n" + ps.toString());
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    private void resetEntireLesson(final Connection conn, int runId, int userId, CmProgramFlow program, int lessonNumber) throws Exception {

        PreparedStatement ps = null;
        try {
            __logger.info("Resetting user lesson: " + userId + ", " + program.getActiveInfo());
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_USER_LESSON");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ps.setInt(2, runId);
            ps.setInt(3, lessonNumber);

            int cnt = ps.executeUpdate();
            __logger.debug("Reset user lesson: " + cnt + "\n" + ps.toString());
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ResetUserPrescripionLessonAction.class;
    }
}