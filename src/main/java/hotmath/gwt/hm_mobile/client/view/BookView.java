package hotmath.gwt.hm_mobile.client.view;


import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.ProblemNumber;

public interface BookView  {
	void showBook(BookModel book, BookInfoModel infoModel, int page);
	void showProblemNumbers(CmList<ProblemNumber> problems);
	void setPresenter(Presenter presenter);
	BookModel getLoadedBookModel();
	
	static public interface Presenter {
		void loadBookInfo(BookModel book,CallbackOnComplete callback);
		void getProblemNumbers(BookModel book, int page);
		void loadSolution(ProblemNumber problem);
	}
}
