package hotmath.gwt.cm_mobile3.client.event;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.AutoAdvanceUserAction;
import hotmath.gwt.cm_rpc.client.rpc.AutoUserAdvanced;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class AutoAdvanceUserEventHandlerImpl implements AutoAdvanceUserEventHandler {
    public void autoAdvanceUser(int userId) {

        CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));
        AutoAdvanceUserAction action = new AutoAdvanceUserAction(SharedData.getUserInfo().getUid());
        CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<AutoUserAdvanced>() {
            public void onSuccess(AutoUserAdvanced result) {
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
                new AutoAdvanceInfoWindow(result);
            }
            
            public void onFailure(Throwable caught) {
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
                MessageBox.showError("Error auto advancing: " + caught.getMessage());
                Log.error("Error during auto advance", caught);
            }
        });
    }
}



class AutoAdvanceInfoWindow extends DialogBox {
    public AutoAdvanceInfoWindow(AutoUserAdvanced result) {
        addStyleName("autoAdvanceInfoWindow");
        setText("Auto Advance Program");
        String html = "Your program has been automatically advanced to: <b>" + result.getProgramTitle() + "</b>";
        setWidget(new HTML(html));
        setAutoHideEnabled(true);
        setAnimationEnabled(true);
        setSize("300px", "200px");
        setModal(true);
        center();
        
        setVisible(true);
        
        
        CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadActiveProgramFlowEvent());        
    }
    
}