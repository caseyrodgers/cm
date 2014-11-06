package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class FeedbackWindow extends PromptMessageBox {

    public FeedbackWindow(final String feedbackStateInfo) {

        super("Feedback", "Enter Catchup Math feedback.");
        
        addDialogHideHandler(new DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton() == PredefinedButton.OK) {


                    final String value = getValue();
                    if (value == null || value.length() == 0)
                        return;

                    new RetryAction<RpcData>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            CmServiceAsync s = CmShared.getCmService();
                            String fullFeedbackStateInfo = getFeedbackState() + ", " + feedbackStateInfo;
                            SaveFeedbackAction action = new SaveFeedbackAction(value, "", fullFeedbackStateInfo);
                            setAction(action);
                            s.execute(action, this);
                        }

                        public void oncapture(RpcData result) {
                            Info.display("Info", "Feedback saved");
                            CmBusyManager.setBusy(false);
                        }
                    }.register();
                }
            }
        });
        
        center();
        setVisible(true);
    }
    

    protected String getFeedbackState() {
        String msg = "user_agent=" + CmShared.getBrowserInfo() + ", uid: " + UserInfoBase.getInstance().getUid();
        return msg;
    }
}
