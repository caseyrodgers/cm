package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.sql.Connection;

public class HighlightsGetReportCommand implements ActionHandler< HighlightsGetReportAction, CmList<HighlightReportData>>{

    @Override
    public CmList<HighlightReportData> execute(Connection conn, HighlightsGetReportAction action) throws Exception {
        
        CmList<HighlightReportData> list = new CmArrayList<HighlightReportData>();
        
        switch(action.getType()) {
            case GREATEST_EFFORT:
                        
                break;
        }
        
        
        for(int i=0;i<25;i++) {
            list.add(new HighlightReportData(i + " user", "SERVER DATA: " + System.currentTimeMillis()));
        }
        
        return list;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  HighlightsGetReportAction.class;
    }
}
