package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAssessmentReportAction;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GeneratePdfAssessmentReportCommand implements ActionHandler<GeneratePdfAssessmentReportAction,CmWebResource>{

    @Override
    public CmWebResource execute(Connection conn, GeneratePdfAssessmentReportAction action) throws Exception {
        GetAdminTrendingDataCommand tdCmd = new GetAdminTrendingDataCommand();
        CmAdminTrendingDataI tData = tdCmd.execute(conn, new GetAdminTrendingDataAction(GetAdminTrendingDataAction.DataType.FULL_HISTORY,action.getAdminId(), action.getPageAction()));

        List<Integer> studentIds = new ArrayList<Integer>();
        for(StudentModelExt sme: tdCmd.getStudentPool()) {
            studentIds.add(sme.getUid());
        }

        GeneratePdfAction pdfAction = new GeneratePdfAction(PdfType.GROUP_ASSESSMENT, action.getAdminId(), studentIds);
        pdfAction.setFilterMap(action.getFilterMap());
        return new GeneratePdfCommand().execute(conn, pdfAction);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GeneratePdfAssessmentReportAction.class;
    }
}
