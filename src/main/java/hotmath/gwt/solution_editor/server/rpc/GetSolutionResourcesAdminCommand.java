package hotmath.gwt.solution_editor.server.rpc;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import java.io.File;
import java.sql.Connection;

/** Return list of all resources used by this solution 
 * 
 * @author casey
 *
 */
public class GetSolutionResourcesAdminCommand implements ActionHandler<GetSolutionResourcesAdminAction, CmList<SolutionResource>>{

    SolutionDef solutionDef;
    @Override
    public CmList<SolutionResource> execute(Connection conn, GetSolutionResourcesAdminAction action) throws Exception {
        
        File solutionPath=null;
        switch(action.getType()) {
            case LOCAL:
                solutionDef = new SolutionDef(action.getPid());
                solutionPath = new File(solutionDef.getResourcesPath());
                break;
                
            case GLOBAL:
                solutionPath = new File(CatchupMathProperties.getInstance().getSolutionBase(), "/help/solutions/resources"); 
                break;
        }
        
        CmList<SolutionResource> resources = new CmArrayList<SolutionResource>();
        getSolutionResources(solutionPath, resources);
        return resources;
    }

    private void getSolutionResources(File dir, CmList<SolutionResource> resources) throws Exception {
        if(!dir.exists())
            return;
        
        for(File kid: dir.listFiles()) {
            if(kid.isDirectory()) {
                getSolutionResources(kid,resources);
            }
            else {
                if(kid.getName().endsWith(".mathml")) {
                    continue;
                }
                String urlPath = null;
                if(solutionDef != null) {
                    urlPath = solutionDef.getSolutionPathHttp();
                }
                else {
                    urlPath = "/help/solutions";  /** global resources */
                }
                urlPath += "/resources/" + kid.getName();
                resources.add(new SolutionResource(kid.getName(), urlPath));
            }
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionResourcesAdminAction.class;
    }
}
