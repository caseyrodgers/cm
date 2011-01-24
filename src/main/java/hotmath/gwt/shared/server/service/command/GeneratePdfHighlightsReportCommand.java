package hotmath.gwt.shared.server.service.command;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.report.HighlightsReport;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfHighlightsReportAction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GeneratePdfHighlightsReportCommand implements ActionHandler<GeneratePdfHighlightsReportAction,CmWebResource>{

    @Override
    public CmWebResource execute(Connection conn, GeneratePdfHighlightsReportAction action) throws Exception {
        
        List<StudentModelExt> studentPool = new GetStudentGridPageCommand().getStudentPool(conn, action.getPageAction());
        if(studentPool.size() == 0)
            return null;
       
        List<Integer> studentIds = new ArrayList<Integer>();
        for(StudentModelExt sme: studentPool) {
            studentIds.add(sme.getUid());
        }
        return new HighlightsReport(action.getAdminId(), action.getReportName(), action.getModels()).getWebResource(conn);
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
