package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDefault;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;
import java.util.List;

public class GetAdminTrendingDataCommand implements ActionHandler<GetAdminTrendingDataAction, CmAdminTrendingDataI>{

    @Override
    public CmAdminTrendingDataI execute(Connection conn, GetAdminTrendingDataAction action) throws Exception {
        List<StudentModelExt> studentPool = new GetStudentGridPageCommand().getStudentPool(conn, action.getDataAction());
        if(studentPool.size() == 0)
            throw new CmRpcException("No students found");
        
        CmList<ProgramData> pd = new CmAdminDao().getTrendingData_ForProgram(conn, action.getAdminId(), studentPool);
        CmList<TrendingData> td = new CmAdminDao().getTrendingData(conn, action.getAdminId(), studentPool);
        return new CmAdminTrendingDataImplDefault(td, pd);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTrendingDataAction.class;
    }
}
