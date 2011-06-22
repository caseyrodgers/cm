package hotmath.gwt.hm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookModel;

public class BookSearchAction implements Action<CmList<BookModel>>{
	
	String searchFor;
	
	public BookSearchAction() {}
	
	public BookSearchAction(String searchFor) {
		this.searchFor = searchFor;
	}

	public String getSearchFor() {
    	return searchFor;
    }

	public void setSearchFor(String searchFor) {
    	this.searchFor = searchFor;
    }

	@Override
    public String toString() {
	    return "BookSearchAction [searchFor=" + searchFor + "]";
    }

}
