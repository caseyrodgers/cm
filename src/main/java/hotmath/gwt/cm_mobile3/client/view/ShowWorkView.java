package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public interface ShowWorkView extends IPage {
    void setTitle(String title);
    void setPresenter(Presenter presenter);
    static public interface Presenter {
        void prepareShowWorkView(ShowWorkView view);
        String getProblemStatementHtml();
        void goBack();
    }
}
