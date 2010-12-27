package hotmath.gwt.solution_editor.server.rpc;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.GetMathMlResourceAction;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;

import java.io.File;
import java.sql.Connection;

import sb.util.SbFile;

/**

 */
public class GetMathMlResourceCommand implements ActionHandler<GetMathMlResourceAction, MathMlResource> {

    @Override
    public MathMlResource execute(Connection conn, GetMathMlResourceAction action) throws Exception {


        String resourceFile = null;
        switch(action.getType()) {
        case LOCAL:
            SolutionDef def = new SolutionDef(action.getPid());
            /** Construct a mathml name
             * from resource name
             */
            resourceFile = def.getResourcesPath();
            break;
            
            
        case GLOBAL:
            resourceFile = CatchupMathProperties.getInstance().getSolutionBase() + "/help/solutions/resources";
            break;
        }
        
        resourceFile += "/" + action.getResourceName();
        
        String mathmlName = resourceFile.substring(0, resourceFile.indexOf(".")) + ".mathml";
        File mathmlFile = new File(mathmlName);
        
        if(!mathmlFile.exists()) {
            throw new CmRpcException("MathML not found for resource: " + action.getResourceName());
        }
        
        String mathMl = new SbFile(mathmlFile).getFileContents().toString("\n");
        
        return new MathMlResource(action.getResourceName(), mathMl);
    }


    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetMathMlResourceAction.class;
    }
}
