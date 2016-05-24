package hotmath.gwt.solution_editor.server.rpc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.model.MathMlTransformationInfo;
import hotmath.gwt.solution_editor.client.rpc.GetMathMlTransformationAction;
import hotmath.mathml.MathMlTransform;
import hotmath.mathml.MathMlTransform.CallbackInfo;
import hotmath.mathml.MathTemplate;

public class GetMathMlTransformationCommand implements ActionHandler<GetMathMlTransformationAction, MathMlTransformationInfo> {
    
    @Override
    public MathMlTransformationInfo execute(Connection conn, GetMathMlTransformationAction action) throws Exception {
    	
    	final List<String> logMessages = new ArrayList<String>();
    	String results = new MathMlTransform().processMathMlTransformations(action.getHtmlWithMathMl(), new CallbackInfo() {
			
			@Override
			public void ruleFired(MathTemplate fired) {
				logMessages.add("Fired: " + fired.getRuleName());
			}
			
			@Override
			public void ruleActivated(MathTemplate fired) {
				logMessages.add("Activated: " + fired.getRuleName());
			}
		});

    	String messages = "";
    	for(String m: logMessages) {
    		messages += m + "\n";
    	}
    	return new MathMlTransformationInfo(results, messages);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetMathMlTransformationAction.class;
    }
}
