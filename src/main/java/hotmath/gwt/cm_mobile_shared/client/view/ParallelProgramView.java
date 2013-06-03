package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

public interface ParallelProgramView extends IPage {
    
    interface Presenter {
        void doLogin(ParallelProgramView view, String value);
    }
    void setPresenter(Presenter presenter, CallbackOnComplete callbackOnComplete);
    void showParallelProgNotAvail(String password);
}
