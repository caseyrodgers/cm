package hotmath.gwt.shared.server.service.command;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.report.ParallelProgramUsageReport;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfParallelProgramUsageReportAction;

import java.sql.Connection;

import org.apache.log4j.Logger;

public class GeneratePdfParallelProgramUsageReportCommand implements ActionHandler<GeneratePdfParallelProgramUsageReportAction,CmWebResource>{

	private static final Logger LOGGER = Logger.getLogger(GeneratePdfParallelProgramUsageReportCommand.class);
    @Override
    public CmWebResource execute(Connection conn, GeneratePdfParallelProgramUsageReportAction action) throws Exception {
    	if (LOGGER.isDebugEnabled())
    		LOGGER.debug(String.format("+++ adminId: %d, ppId: %d", action.getAdminId(), action.getParallelProgId()));
        return new ParallelProgramUsageReport(action.getAdminId(), action.getParallelProgId()).getWebResource(conn);
    }
    
    public String getPrintableReportId(Integer adminUid, Integer parallelProgId) {
        String reportId = String.format("%d%d%d", adminUid, System.currentTimeMillis(), parallelProgId);
        CmCacheManager.getInstance().addToCache(REPORT_ID, reportId, parallelProgId);
        return reportId;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GeneratePdfParallelProgramUsageReportAction.class;
    }
}
