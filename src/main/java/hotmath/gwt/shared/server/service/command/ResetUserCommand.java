package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaUser;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

public class ResetUserCommand implements ActionHandler<ResetUserAction, RpcData>{

	@Override
	public RpcData execute(final Connection conn, ResetUserAction action) throws Exception {
		try {
            HaUser user = HaUser.lookUser(conn, action.getUid(), null);
            user.setActiveTest(0);
            user.setActiveTestRunId(0);
            user.setActiveTestSegment(0);
            user.setActiveTestRunSession(0);

            StudentActiveInfo activeInfo = new StudentActiveInfo();
            activeInfo.setActiveSegmentSlot(0);
            new CmStudentDao().setActiveInfo(conn, action.getUid(), activeInfo);

            user.update(conn);
            
            return new RpcData("status=OK");
            
        } finally {
            SqlUtilities.releaseResources(null, null, null);
        }
		
	}

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return ResetUserAction.class;
	}
	

}
