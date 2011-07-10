package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

public interface TutorView {
	
	void loadSolution(SolutionResponse solution);
	void setPresenter(Presenter p);
	ProblemNumber getLoadedProblem();
	
	static public interface Presenter {
		void getTutor(String pid, CallbackOnComplete callback);
	}

}
