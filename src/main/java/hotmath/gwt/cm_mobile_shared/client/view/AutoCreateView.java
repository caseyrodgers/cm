package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

public interface AutoCreateView extends IPage {

    interface Presenter {

        void doRegistration(String firstName, String lastName, String birthDate);
    }
    void setPresenter(Presenter presenter, CallbackOnComplete callbackOnComplete);
}
