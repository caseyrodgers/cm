package hotmath.gwt.shared.server.service.command;

import hotmath.SolutionManager;
import hotmath.gwt.cm_rpc.client.model.SpellCheckResults;
import hotmath.gwt.cm_rpc.client.model.SpellCheckSolutionsResults;
import hotmath.gwt.cm_rpc.client.rpc.SpellCheckAction;
import hotmath.gwt.cm_rpc.client.rpc.SpellCheckSolutionsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;
import hotmath.model.HintStep;
import hotmath.solution.Solution;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

import org.apache.log4j.Logger;

public class SpellCheckSolutionsCommand implements ActionHandler<SpellCheckSolutionsAction, SpellCheckSolutionsResults> {

    static Logger __logger = Logger.getLogger(SpellCheckSolutionsCommand.class);
    
    public SpellCheckSolutionsCommand() throws Exception {
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SpellCheckSolutionsAction.class;
    }

    @Override
    public SpellCheckSolutionsResults execute(Connection conn, SpellCheckSolutionsAction action) throws Exception {
        SpellCheckSolutionsResults results = new SpellCheckSolutionsResults();
       try {
        
            SpellCheckAction scAction = new SpellCheckAction();
            SpellCheckCommand scCommand = new SpellCheckCommand();
            for(SolutionSearchModel a: action.getRes()) {
                __logger.info("processing: " + a.getPid());
                try {
                    Solution solution = SolutionManager.getSolution(a.getPid());
                    
                    String html = solution.getStatement();
                    for(HintStep sh: solution.getHintSteps()) {
                        html += " " + sh.getHint().getHTML() + " " + sh.getStep().getHTML();
                    }
                    
                    scAction.setText(html);
                    results.getResults().add(scCommand.execute(null, scAction));
                    
                    Thread.sleep(1000);  // time between calls...otherwise error on server.
                }
                catch(Exception e) {
                    e.printStackTrace();
                    results.getResults().add(new SpellCheckResults(e.getMessage()));
                }
            }
       }
       finally {
          SqlUtilities.releaseResources(null, null, null); 
          
          __logger.info("Complete!");
       }
        
        return results;  
    }

}
