package hotmath.gwt.hm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.HmMobileActionBase;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;

public class GetBookInfoAction extends HmMobileActionBase implements Action<BookInfoModel>{
	
	BookModel bookModel;
	public GetBookInfoAction() {}
	
	public GetBookInfoAction(BookModel bookModel) {
		this.bookModel = bookModel;
	}

	public BookModel getBookModel() {
    	return bookModel;
    }

	public void setBookModel(BookModel bookModel) {
    	this.bookModel = bookModel;
    }

	@Override
    public String toString() {
	    return "GetBookInfoAction [bookModel=" + bookModel + "]";
    }

}
