package hotmath.gwt.hm_mobile.client.view;


import hotmath.gwt.cm_mobile_shared.client.util.ResettablePage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;

public interface BookView extends ResettablePage  {
	void showBook(BookModel book, BookInfoModel infoModel, int page);
	void showProblemNumbers(CmList<ProblemNumber> problems);
	void setPresenter(Presenter presenter);
	BookModel getLoadedBookModel();
	
	static public interface Presenter {
		void loadBookInfo(String textCode,CallbackOnComplete callback);
		void getProblemNumbers(BookModel book, int page);
		void loadSolution(ProblemNumber problem);
	}
}
