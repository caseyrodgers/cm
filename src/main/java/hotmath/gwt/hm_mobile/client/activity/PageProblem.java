package hotmath.gwt.hm_mobile.client.activity;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_rpc.client.model.ProblemNumber;

public class PageProblem {
	int page;
	List<ProblemNumber> problems = new ArrayList<ProblemNumber>();
	
	
	public PageProblem(int page) {
		this.page = page;
	}
	
	public int getPage() {
		return page;
	}
	
	public List<ProblemNumber> getProblems() {
		return problems;
	}
}
