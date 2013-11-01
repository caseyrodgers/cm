package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public interface PrescriptionLessonResourceWebLinkView extends IPage {
    void setPresenter(Presenter p);
    static public interface Presenter {
        String getWebLinkName();
        String getWebLinkUrl();
    }
}
