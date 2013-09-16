package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CmUserDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.GetUserActivityLogAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.ActivityLogRecord;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;


public class GetUserActivityLogCommand implements ActionHandler<GetUserActivityLogAction, CmList<ActivityLogRecord>>{

    @Override
    public CmList<ActivityLogRecord> execute(Connection conn, GetUserActivityLogAction action) throws Exception {
        CmList<ActivityLogRecord> list = new CmArrayList<ActivityLogRecord>();
        list.addAll(HaUserDao.getInstance().getUserActivityLog(action.getUid()));
        return list;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserActivityLogAction.class;
    }
}
