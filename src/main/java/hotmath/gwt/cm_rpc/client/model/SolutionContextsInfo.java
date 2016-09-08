package hotmath.gwt.cm_rpc.client.model;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class SolutionContextsInfo implements Response{
	
	private List<SolutionContext> contexts = new ArrayList<SolutionContext>();
	
	public SolutionContextsInfo() {}

	public SolutionContextsInfo(List<SolutionContext> contexts) {
		this.contexts.addAll(contexts);
	}
	
	public List<SolutionContext> getContexts() {
		return contexts;
	}

}
