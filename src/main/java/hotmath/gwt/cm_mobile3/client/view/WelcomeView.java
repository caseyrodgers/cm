package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;


public interface WelcomeView extends IPage{
    
    public void setPresenter(Presenter presenter);
    public void prepareView(String firstThing, String prepareView);
    
    public interface Presenter {
        void beginCatchupMath();
    }
}
