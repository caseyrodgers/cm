package hotmath.gwt.hm_mobile.client.activity;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_rpc.client.model.Pid;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.activity.Database.BookProblemWrapper;
import hotmath.gwt.hm_mobile.client.model.BookModel;

public class BookIndex {
	int page;
	List<BookProblemWrapper> problemNumbers = new ArrayList<BookProblemWrapper>();
	private BookModel book;
	
	public BookIndex(BookModel book) {
		this.book = book;
	}
	
	public List<BookProblemWrapper> getProblemNumbers() {
		return problemNumbers;
	}
	
	public BookModel getBook() {
		return book;
	}
	
	public int getPage() {
		return page;
	}

	/** return list of problems on page, or empty list
	 * 
	 * @param page
	 * @return
	 */
	public CmList<ProblemNumber> lookupProblemsOnPage(int page) {
		CmList<ProblemNumber> cmList = new CmArrayList<ProblemNumber>();
		for(int i=0;i<problemNumbers.size();i++) {
			BookProblemWrapper pnw = problemNumbers.get(i);
			int pwnPage = pnw.getPage();
			if(pwnPage == page || pwnPage == (page+1) || pwnPage == (page-1)) {
				Pid pid = new Pid(pnw.getPid());
				ProblemNumber pn = new ProblemNumber();
				pn.setPage(pnw.getPage());
				pn.setPid(pnw.getPid());
				pn.setProblem(pid.getProblem());
				pn.setProblemSet(pid.getProblemSet());
				
				cmList.add( pn);
			}
		}
		return cmList;
	}
}