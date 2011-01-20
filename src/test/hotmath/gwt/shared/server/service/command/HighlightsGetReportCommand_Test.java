package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction.ReportType;

public class HighlightsGetReportCommand_Test extends CmDbTestCase {
    
    public HighlightsGetReportCommand_Test(String name) {
        super(name);
    }
    
    public void testExecute() throws Exception {
        HighlightsGetReportAction action = new HighlightsGetReportAction(ReportType.GREATEST_EFFORT,2,null,null);
        CmList<HighlightReportData> list = new HighlightsGetReportCommand().execute(conn, action);
        assertTrue(list.size() > 0);
    }

}
