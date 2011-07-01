package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.hm_mobile.client.model.ProblemNumber;

public interface TutorView {
	
	void loadSolution(ProblemNumber problem, SolutionResponse solution);
	void setPresenter(Presenter p);
	ProblemNumber getLoadedProblem();
	
	static public interface Presenter {
		void getTutor(String pid, CallbackOnComplete callback);
	}

}
