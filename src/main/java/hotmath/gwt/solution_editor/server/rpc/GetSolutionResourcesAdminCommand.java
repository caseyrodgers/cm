package hotmath.gwt.solution_editor.server.rpc;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction.ResourceType;
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
        CmList<SolutionResource> resources = new CmArrayList<SolutionResource>();        
        File solutionPath=null;
        if(action.getType() == ResourceType.WIDGET) {
            getAllRegisteredWidgets(resources);
        }
        else {
            switch(action.getType()) {
                case LOCAL:
                    solutionDef = new SolutionDef(action.getPid());
                    solutionPath = new File(solutionDef.getSolutionPathOnDisk());
                    break;
                    
                case GLOBAL:
                    solutionPath = new File(CatchupMathProperties.getInstance().getSolutionBase(), "/help/solutions/resources"); 
                    break;
            }
            getSolutionResources(solutionPath, resources);
        }
        
        return resources;
    }

    private void getAllRegisteredWidgets(CmList<SolutionResource> resources) throws Exception {
        SolutionResource widget = new SolutionResource();
        widget.setFile("The Widget Name");
        widget.setDisplay("<h1>The Widget Markup</h1><p>This is the example of this widget</p>");
        widget.setContents("THIS IS THE WIDGET MARKUP");
        
        resources.add(widget);
        
        widget = new SolutionResource();
        widget.setFile("The Widget Name 2");
        widget.setDisplay("<h1>The Widget Markup 2</h1><p>This is the example of this widget 2</p>");
        widget.setContents("THIS IS THE WIDGET 2 MARKUP");
        
        resources.add(widget);
    }
    
    int depth=0;
    private void getSolutionResources(File dir, CmList<SolutionResource> resources) throws Exception {
        if(!dir.exists())
            return;
        for(File kid: dir.listFiles()) {
            if(kid.isDirectory()) {
                ++depth;
                getSolutionResources(kid,resources);
            }
            else {
                if(kid.getName().endsWith(".mathml")
                        || kid.getName().endsWith(".html")
                        || kid.getName().endsWith(".xml")
                        || kid.getName().endsWith(".json") 
                        || kid.getName().endsWith(".css")
                        || kid.getName().endsWith(".js")) {
                    continue;
                }
                String urlPath = null;
                if(solutionDef != null) {
                    urlPath = solutionDef.getSolutionPathHttp();
                }
                else {
                    urlPath = "/help/solutions/resources";  /** global resources */
                }

                
                if(depth > 0) {
                    urlPath += "/resources/" + kid.getName();
                }
                else {
                    urlPath += "/" + kid.getName();
                }
                resources.add(new SolutionResource(kid.getName(), urlPath));
            }
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionResourcesAdminAction.class;
    }
}
