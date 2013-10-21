package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.ResetUserPrescripionLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ResetLessonDialog extends GWindow {

    private int userId;

    public ResetLessonDialog(int userId) {
        super(false);
        this.userId = userId;
        
        setPixelSize(300,  200);
        
        setWidget(new HTML("<div style='padding: 10px'>Are you sure want to reset the current lesson?</div>"));
        
        addButton(new TextButton("Reset Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doReset();
            }
        }));
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        setVisible(true);
    }

    protected void doReset() {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                ResetUserPrescripionLessonAction action = new ResetUserPrescripionLessonAction(userId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            @Override
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                
                if(value.getDataAsString("status").equals("OK")) {
                    CmShared.reloadUser();
                }
            }
        }.register();
    }

}
