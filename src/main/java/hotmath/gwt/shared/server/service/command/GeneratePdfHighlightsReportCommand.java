package hotmath.gwt.shared.server.service.command;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.report.HighlightsReport;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfHighlightsReportAction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GeneratePdfHighlightsReportCommand implements ActionHandler<GeneratePdfHighlightsReportAction,CmWebResource>{

    @Override
    public CmWebResource execute(Connection conn, GeneratePdfHighlightsReportAction action) throws Exception {
        
        List<StudentModelI> studentPool = new GetStudentGridPageCommand().getStudentPool(action.getPageAction());
        if(studentPool.size() == 0)
            return null;
       
        List<Integer> studentIds = new ArrayList<Integer>();
        for(StudentModelI sme: studentPool) {
            studentIds.add(sme.getUid());
        }
        return new HighlightsReport(action.getAdminId(), action.getReportName(), action.getReportLayout()).getWebResource(conn);
    }

    public String getPrintableStudentReportId(List<Integer> studentUids) {
        String reportId = String.format("%d%d%d", studentUids.get(0), System.currentTimeMillis(), studentUids.size());
        CmCacheManager.getInstance().addToCache(REPORT_ID, reportId, studentUids);
        return reportId;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GeneratePdfHighlightsReportAction.class;
    }
}
