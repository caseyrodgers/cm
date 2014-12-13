package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
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
    private String pidInLesson;

    public ResetLessonDialog(int userId, String pidInLesson) {
        super(false);
        this.userId = userId;
        this.pidInLesson = pidInLesson;
        
        setPixelSize(300,  200);
        
        String message="";
        if(pidInLesson == null) {
            message = "Are you sure you want to reset the current lesson?";
        }
        else {
            message = "Are you sure you want to reset problem '" + pidInLesson + "' in current lesson?";
        }
        setWidget(new HTML("<div style='padding: 10px'>" + message + "</div>"));
        
        addButton(new TextButton("Reset", new SelectHandler() {
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
                ResetUserPrescripionLessonAction action = new ResetUserPrescripionLessonAction(userId, pidInLesson);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            @Override
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                
                if(value.getDataAsString("status").equals("OK")) {
                    CmCore.reloadUser();
                }
            }
        }.register();
    }

}
