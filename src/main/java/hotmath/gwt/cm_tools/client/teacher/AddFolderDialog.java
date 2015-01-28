package hotmath.gwt.cm_tools.client.teacher;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.rpc.AddCustomProblemTreePathAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
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

public class AddFolderDialog extends GWindow {

	public interface Callback {
		void folderAdded(String folderName);
	}

	private Callback callback;

	public AddFolderDialog(Callback callback) {
		super(false);
		this.callback = callback;

		setPixelSize(300, 130);

		setHeadingText("Add New Custom Folder");
		buildUi();

		addButton(new TextButton("Create", new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				addFolder();
			}
		}));
		addCloseButton().setText("Cancel");

		setVisible(true);
	}

	protected void addFolder() {
		if (!validateName()) {
			return;
		}

		final String folderName = _folderName.getCurrentValue().trim();

		CmBusyManager.setBusy(true);

		new RetryAction<RpcData>() {
			@Override
			public void attempt() {
				AddCustomProblemTreePathAction action = new AddCustomProblemTreePathAction(TeacherManager.getTeacher(), folderName);
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(RpcData value) {
				CmBusyManager.setBusy(false);
				callback.folderAdded(folderName);

				hide();
			}

			public void onFailure(Throwable error) {
				Log.error("Error adding folder", error);
				CmBusyManager.setBusy(false);
				RetryActionManager.getInstance().requestComplete(this);
				if (error.getMessage().toLowerCase().contains("duplicate")) {
					CmMessageBox.showAlert("Duplicate folder name.");
					// setComboSelected(teacherName);
				} else {
					CmMessageBox.showAlert(error.getMessage());
				}
			}
		}.register();
	}

	protected boolean validateName() {
		String name = _folderName.getCurrentValue();
		if (name == null || name.length() == 0) {
			CmMessageBox.showAlert("Folder name must be specified");
			return false;
		}

		return true;
	}

	TextField _folderName = new TextField();

	private void buildUi() {

		FlowLayoutContainer flow = new FlowLayoutContainer();

		flow.add(new MyFieldLabel(_folderName, "Folder Name", 100));
		// flow.add(new
		// HTML("<p style='font-size: .9em'>Enter a new teacher name containing only letters and numbers.</p>"));

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
				new AddFolderDialog(new Callback() {
					@Override
 					public void folderAdded(String folderName) {
						Window.alert("New Teacher: " + folderName);
					}
				});
			}
		}));
		tester.setVisible(true);

	}
}
