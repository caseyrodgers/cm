package hotmath.gwt.hm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.HmMobileActionBase;
import hotmath.gwt.hm_mobile.client.model.BookModel;

public class GetProblemNumbersAction extends HmMobileActionBase implements Action<CmList<ProblemNumber>> {
	BookModel book;
	int page;
	
	public GetProblemNumbersAction() {}
	
	public GetProblemNumbersAction(BookModel book, int page) {
		this.book = book;
		this.page = page;
	}

	public BookModel getBook() {
    	return book;
    }

	public void setBook(BookModel book) {
    	this.book = book;
    }

	public int getPage() {
    	return page;
    }

	public void setPage(int page) {
    	this.page = page;
    }

	@Override
    public String toString() {
	    return "GetProblemNumbersAction [book=" + book + ", page=" + page + "]";
    }
}
