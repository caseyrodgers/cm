package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;

import java.sql.Connection;
import java.util.List;

public class GetLessonItemsForTestRunCommand implements ActionHandler<GetLessonItemsForTestRunAction, CmList<LessonItemModel>>{

    @Override
    public CmList<LessonItemModel> execute(Connection conn, GetLessonItemsForTestRunAction action) throws Exception {
        
        List<LessonItemModel> list = CmStudentDao.getInstance().getLessonItemsForTestRun(conn, action.getRunId());
        CmList<LessonItemModel> toRet = new CmArrayList<LessonItemModel>();
        if(list != null) {
            //  now move each CmList (smaller RPC)
            toRet.addAll(list);
        }
        return toRet;
    }
    
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetLessonItemsForTestRunAction.class;
    }

}
