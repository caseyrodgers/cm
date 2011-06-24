package hotmath.gwt.hm_mobile.client.view;



import com.google.gwt.user.client.ui.IsWidget;

public interface HelpView extends IsWidget  {
	void showHelp();
	
	void setPresenter(Presenter presenter);
	
	public interface Presenter {
	}	
}
