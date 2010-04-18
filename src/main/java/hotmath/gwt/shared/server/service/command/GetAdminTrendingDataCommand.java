package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDefault;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;

import java.sql.Connection;
import java.util.List;

public class GetAdminTrendingDataCommand implements ActionHandler<GetAdminTrendingDataAction, CmAdminTrendingDataI>{

    List<StudentModelExt> studentPool;
    @Override
    public CmAdminTrendingDataI execute(Connection conn, GetAdminTrendingDataAction action) throws Exception {
        studentPool = new GetStudentGridPageCommand().getStudentPool(conn, action.getDataAction());
        if(studentPool.size() == 0)
            throw new CmRpcException("No students found");
        
        boolean useOnlyActiveProgram = action.getDataType() == GetAdminTrendingDataAction.DataType.ONLY_ACTIVE;
        
        CmList<ProgramData> pd = new CmAdminDao().getTrendingData_ForProgram(conn, action.getAdminId(), studentPool,useOnlyActiveProgram);
        CmList<TrendingData> td = new CmAdminDao().getTrendingData(conn, action.getAdminId(), studentPool,useOnlyActiveProgram);
        return new CmAdminTrendingDataImplDefault(td, pd);
    }

    public List<StudentModelExt> getStudentPool() {
        return studentPool;
    }

    public void setStudentPool(List<StudentModelExt> studentPool) {
        this.studentPool = studentPool;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTrendingDataAction.class;
    }
}
