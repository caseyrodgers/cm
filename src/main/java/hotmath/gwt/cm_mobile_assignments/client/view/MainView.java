package hotmath.gwt.cm_mobile_assignments.client.view;

public interface MainView extends BaseView {
    
    interface Presenter {}
    void setPresenter(Presenter p);
    void setView(BaseView view, String title, boolean needsBackButton);
    void setHeaderTitle(String title);
    void setNeedsBackButton(boolean yesNo);
}