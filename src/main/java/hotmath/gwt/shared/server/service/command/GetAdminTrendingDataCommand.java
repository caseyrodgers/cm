package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDefault;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;
import java.util.List;

public class GetAdminTrendingDataCommand implements ActionHandler<GetAdminTrendingDataAction, CmAdminTrendingDataI>{

    @Override
    public CmAdminTrendingDataI execute(Connection conn, GetAdminTrendingDataAction action) throws Exception {
        
        /** get filtered dataset from cache */
        List<StudentModelExt> studentPool = (List<StudentModelExt>) CmCacheManager.getInstance().retrieveFromCache(CacheName.STUDENT_PAGED_DATA, 
                GetStudentGridPageCommand.getPoolCacheKey(action.getAdminId()));
        
        return new CmAdminTrendingDataImplDefault(new CmAdminDao().getTrendingData(conn, action.getAdminId(), studentPool));
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTrendingDataAction.class;
    }
}
