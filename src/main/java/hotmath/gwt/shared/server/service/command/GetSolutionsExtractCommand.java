package hotmath.gwt.shared.server.service.command;

import hotmath.SolutionManager;
import hotmath.gwt.cm_rpc.client.model.SolutionExtractResults;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionsExtractAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;
import hotmath.model.HintStep;
import hotmath.solution.Solution;

import java.sql.Connection;

import net.htmlparser.jericho.Source;

public class GetSolutionsExtractCommand implements ActionHandler<GetSolutionsExtractAction, SolutionExtractResults> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionsExtractAction.class;
    }

    @Override
    public SolutionExtractResults execute(Connection conn, GetSolutionsExtractAction action) throws Exception {
        
        
        StringBuffer buffer = new StringBuffer();
        for(SolutionSearchModel a: action.getRes()) {
            Solution solution = SolutionManager.getSolution(a.getPid());
            
            String html = new Source(solution.getStatement()).getRenderer().toString();
            for(HintStep sh: solution.getHintSteps()) {

                
                
                String stepHtml = sh.getHint().getText() + " " + sh.getStep().getText();
                
                
                Source source=new Source(stepHtml);
                String renderedText=source.getRenderer().toString();
                
                html += " " + renderedText;

            }

            html += "\n\n\n-------------------------\n" +
                    "pid: " + a.getPid() + "\n" +
                    "-------------------------\n\n\n";                    
                    
            buffer.append(html);
        }
        
        
        return new SolutionExtractResults(buffer.toString());
        
    }

  

}
