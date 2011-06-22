package hotmath.gwt.hm_mobile.client.rpc;


import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;

public class GetBooksAction implements Action<CmList<BookModel>>{
	
	CategoryModel subject;
	public GetBooksAction() {}
	
	public GetBooksAction(CategoryModel subject) {
		this.subject = subject;
	}

	public CategoryModel getSubject() {
    	return subject;
    }

	public void setSubject(CategoryModel subject) {
    	this.subject = subject;
    }

	@Override
    public String toString() {
	    return "GetBooksAction [subject=" + subject + "]";
    }
}
