package hotmath.gwt.hm_mobile.client.view;


public interface BookSearchView {
	public void setPresenter(Presenter pres);
	
	public static interface Presenter {
		void doBookSearch(String search);
	}
}
