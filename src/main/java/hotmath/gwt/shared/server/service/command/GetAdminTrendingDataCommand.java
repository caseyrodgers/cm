package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDefault;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;

import java.sql.Connection;
import java.util.List;

public class GetAdminTrendingDataCommand implements ActionHandler<GetAdminTrendingDataAction, CmAdminTrendingDataI>{

    List<StudentModelI> studentPool;
    @Override
    public CmAdminTrendingDataI execute(Connection conn, GetAdminTrendingDataAction action) throws Exception {
        studentPool = new GetStudentGridPageCommand().getStudentPool(action.getDataAction());
        if(studentPool.size() == 0)
            throw new CmRpcException("No students found");
        
        boolean useOnlyActiveProgram = action.getDataType() == GetAdminTrendingDataAction.DataType.ONLY_ACTIVE;
        
        CmList<ProgramData> pd = CmAdminDao.getInstance().getTrendingData_ForProgram(conn, action.getAdminId(), studentPool,useOnlyActiveProgram);
        CmList<TrendingData> td = CmAdminDao.getInstance().getTrendingData(conn, action.getAdminId(), studentPool,useOnlyActiveProgram);
        return new CmAdminTrendingDataImplDefault(td, pd);
    }

    public List<StudentModelI> getStudentPool() {
        return studentPool;
    }

    public void setStudentPool(List<StudentModelI> studentPool) {
        this.studentPool = studentPool;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTrendingDataAction.class;
    }

}
