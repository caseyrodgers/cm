package hotmath.gwt.cm_mobile_assignments.client.view;


public interface HomeView extends BaseView {
    void loadUser();
    void setPresenter(Presenter listener);
    public interface Presenter {}       
}
