package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathProperties;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetReviewHtmlAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;
import hotmath.util.HmContentExtractor;

import java.sql.Connection;

import sb.util.SbFile;

public class GetReviewHtmlCommand implements ActionHandler<GetReviewHtmlAction, RpcData>, ActionHandlerManualConnectionManagement{

    @Override
    public RpcData execute(Connection conn, GetReviewHtmlAction action) throws Exception {
        
        RpcData rpcData = new RpcData();
        String filePath = HotMathProperties.getInstance().getHotMathWebBase();
        String p = action.getFile();
        
        if(!p.startsWith("/"))
            p = "/" + p;
        
        filePath = filePath + p;

        String html = new SbFile(filePath).getFileContents().toString("\n");

        HmContentExtractor ext = new HmContentExtractor();
        String htmlCooked = ext.extractContent(html, action.getBaseDirectory());

        rpcData.putData("html", htmlCooked);
        return rpcData;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetReviewHtmlAction.class;
    }
}
