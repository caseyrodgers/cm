package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.MarkPrescriptionLessonAsViewedAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaTestRunDao;

import java.sql.Connection;


/** Mark the given Prescription Lesson as being viewed
 * 
 * @author casey
 *
 */
public class MarkPrescriptionLessonAsViewedCommand implements ActionHandler<MarkPrescriptionLessonAsViewedAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, MarkPrescriptionLessonAsViewedAction action) throws Exception {
        
        new HaTestRunDao().markLessonAsViewed(conn, action.getRunId(),action.getLesson());
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return MarkPrescriptionLessonAsViewedAction.class;
    }

}
