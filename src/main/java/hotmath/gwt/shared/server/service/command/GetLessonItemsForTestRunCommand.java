package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;
import java.util.List;

public class GetLessonItemsForTestRunCommand implements ActionHandler<GetLessonItemsForTestRunAction, CmList<LessonItemModel>>{

    @Override
    public CmList<LessonItemModel> execute(Connection conn, GetLessonItemsForTestRunAction action) throws Exception {
        
        CmAdminDao dao = new CmAdminDao();
        List<LessonItemModel> list = new CmStudentDao().getLessonItemsForTestRun(conn, action.getRunId());
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
