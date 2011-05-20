package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ResetStudentActivityAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaUserExtendedDao;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

/** Reset the user to the named test program/segment
 * 
 * @author casey
 *
 */
public class ResetStudentActivityCommand implements ActionHandler<ResetStudentActivityAction, RpcData>{


    @Override
    public RpcData execute(Connection conn, ResetStudentActivityAction action) throws Exception {
        
        assert(action.getTestId() > 0);
        
        HaTest resetToTest = HaTestDao.getInstance().loadTest(action.getTestId());
        PreparedStatement ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_STUDENT_ACTIVITY_TEST_RUN");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, action.getUserId());
            ps.setInt(2, action.getRunId());
            ps.executeUpdate();
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_STUDENT_ACTIVITY_TEST");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, action.getUserId());
            ps.setInt(2, action.getTestId());
            ps.executeUpdate();
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
        
        CmStudentDao.getInstance().assignProgramToStudent(conn, action.getUserId(), resetToTest.getProgramInfo());

        
        
        HaUserExtendedDao.getInstance().resyncUserExtendedLessonStatusForUid(conn,action.getUserId());
        
        /** update active flow to selected test segment
         * 
         */
        CmProgramFlow flow = new CmProgramFlow(conn,action.getUserId());
        StudentActiveInfo active = flow.getActiveInfo();
        active.setActiveSegment(resetToTest.getSegment());
        active.setActiveRunId(0);
        active.setActiveRunSession(0);
        active.setActiveTestId(0);
        flow.saveActiveInfo(conn);
        
        RpcData rpcData = new RpcData("status=OK");
        return rpcData;

    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ResetStudentActivityAction.class;
    }
}
