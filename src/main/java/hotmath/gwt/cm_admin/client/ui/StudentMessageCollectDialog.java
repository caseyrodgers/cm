package hotmath.gwt.cm_admin.client.ui;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.rpc.SendMessageToStudentAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;

public class StudentMessageCollectDialog extends GWindow {
	
	private int uid;

	public StudentMessageCollectDialog(int uid) {
		super(false);
		this.uid = uid;
		
		setPixelSize(400, 150);
		setHeadingText("Enter Student Message");
		setModal(true);
		
		drawUi();
	}
	
	TextArea _textArea = new TextArea();
	public void drawUi() {

		addButton(createSubmit());
		addCloseButton();

		setWidget(_textArea);
	}

	private Widget createSubmit() {
		TextButton tb = new TextButton("Submit",  new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				String text = _textArea.getText();
				if(text == null || text.length() == 0) {
					CmMessageBox.showAlert("Please enter the student's message.");
					return;
				}
				sendMessageToStudent(uid, text);
			}
		});
		
		return tb;
	}
		

	 private void sendMessageToStudent(final int uid, final String message) {
         new RetryAction<RpcData>() {
             @Override
             public void attempt() {
                 CmBusyManager.setBusy(true);
                 SendMessageToStudentAction action = new SendMessageToStudentAction(uid, message);
                 setAction(action);
                 CmRpcCore.getCmService().execute(action, this);
             }

             @Override
             public void oncapture(RpcData result) {
                 CmBusyManager.setBusy(false);
                 Info.display("Ok", "Student message has been queued.");
                 hide();
             }
         }.register();
     }

	public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                CmAdminModel admin = new CmAdminModel();
                admin.setUid(2);
                new StudentMessageCollectDialog(2).setVisible(true);
            }
        });
		
	}

}
