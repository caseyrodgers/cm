package hotmath.gwt.solution_editor.server.rpc;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.RemoveSolutionResourceAdminAction;

import java.io.File;
import java.sql.Connection;

public class RemoveSolutionResourceAdminCommand implements ActionHandler<RemoveSolutionResourceAdminAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, RemoveSolutionResourceAdminAction action) throws Exception {

        File resourceFile = null;
        switch(action.getType()) {
        case LOCAL:
            SolutionDef solDef = new SolutionDef(action.getPid());
            resourceFile = new File(solDef.getResourcesPath(), action.getFile());
            break;
            
        case GLOBAL:
            resourceFile = new File(CatchupMathProperties.getInstance().getSolutionBase() + "/help/solutions/resources",action.getFile());
            break;
        }
        
        if(resourceFile.exists()) {
            resourceFile.delete();
            
            String fn = resourceFile.getName();
            String base = fn.substring(0, fn.indexOf("."));
            
            File mathMl = new File(resourceFile.getParentFile(), base + ".mathml");
            if(mathMl.exists()) {
                mathMl.delete();
            }
        }
        return new RpcData("status=OK");
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return RemoveSolutionResourceAdminAction.class;
    }
}
