package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.ui.AddAdminTeacherAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class AddTeacherDialog extends GWindow {

	public interface Callback  {
		void teacherAdded(String teacherName);
	}

	private Callback callback;

	public AddTeacherDialog(Callback callback) {
		super(false);
		this.callback = callback;
		
		setPixelSize(300, 130);
		
		setHeadingText("Add New Custom Problem Teacher");
		buildUi();
		
		addButton(new TextButton("Create", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				addTeacher();
			}
		}));
		addCloseButton().setText("Cancel");
		
		setVisible(true);
	}
	
	protected void addTeacher() {
		if(!validateName()) {
			return;
		}
		
		final String teacherName = _teacherName.getCurrentValue().trim();
		
		CmBusyManager.setBusy(true);
	        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                AddAdminTeacherAction action = new AddAdminTeacherAction(UserInfoBase.getInstance().getUid(), teacherName);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                callback.teacherAdded(teacherName);
                
                hide();
            }
            
            public void onFailure(Throwable error) {
                Log.error("Error adding teacher", error);
                CmBusyManager.setBusy(false);
                RetryActionManager.getInstance().requestComplete(this);
                if(error.getMessage().toLowerCase().contains("duplicate")) {
                    CmMessageBox.showAlert("Duplicate teacher name.");
                    //setComboSelected(teacherName);
                }
                else {
                    CmMessageBox.showAlert(error.getMessage());
                }
            }
        }.register();
	}

	protected boolean validateName() {
		String name = _teacherName.getCurrentValue();
		if(name == null || name.length() == 0) {
			CmMessageBox.showAlert("Teacher name must be specified");
			return false;
		}
		
//		int sz = name.length();
//	      for (int i = 0; i < sz; i++) {
//	          if ((Character.isLetterOrDigit(name.charAt(i)) == false) && (name.charAt(i) != '_')) {
//	        	  CmMessageBox.showAlert("Teacher name can only be letters, numbers or the underscore character.");
//	              return false;
//	          }
//	      }
		
		return true;
	}

	TextField _teacherName = new TextField();
	private void buildUi() {
		
		FlowLayoutContainer flow = new FlowLayoutContainer();
		

		flow.add(new MyFieldLabel(_teacherName, "Teacher Name", 100));
		// flow.add(new HTML("<p style='font-size: .9em'>Enter a new teacher name containing only letters and numbers.</p>"));
		
		FramedPanel fp = new FramedPanel();
		fp.setHeaderVisible(false);
		fp.setWidget(flow);
		
		setWidget(fp);
	}

	
	static public void doTest() {
		
		GWindow tester = new GWindow(false);
		tester.setWidget(new TextButton("Go", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				new AddTeacherDialog(new Callback() {
					@Override
					public void teacherAdded(String teacher) {
						Window.alert("New Teacher: " + teacher);				
					}
				});
			}
		}));
		tester.setVisible(true);
		
	}
}
