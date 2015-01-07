package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.GetAdminTeachersAction;
import hotmath.gwt.cm_rpc.client.ui.AddAdminTeacherAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.teacher.TeacherManager;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class SelectTeacherCombo extends Composite {

	private Callback callback;

	public SelectTeacherCombo(Callback callbackOnComplete) {
		this.callback = callbackOnComplete;

		buildCombo();
		readDataFromServer(null);
	}

	interface ComboProps extends PropertyAccess<String> {
		@Path("teacherId")
		ModelKeyProvider<TeacherIdentity> key();

		LabelProvider<TeacherIdentity> teacherName();
	}

	public interface Callback {
		void teacherSelected(TeacherIdentity teacher);
		void teacherAdded(String teacherName);
	}

	ComboProps props = GWT.create(ComboProps.class);
	ComboBox<TeacherIdentity> _combo;

	private void buildCombo() {
		ListStore<TeacherIdentity> store = new ListStore<TeacherIdentity>(
				props.key());
		_combo = new ComboBox<TeacherIdentity>(store, props.teacherName());
		_combo.setEmptyText("-- Choose a Teacher Name --");
		_combo.setAllowBlank(false);
		_combo.setTriggerAction(TriggerAction.ALL);
		_combo.setEditable(false);
		_combo.setForceSelection(true);

		_combo.addSelectionHandler(new SelectionHandler<TeacherIdentity>() {
			@Override
			public void onSelection(SelectionEvent<TeacherIdentity> event) {
				TeacherIdentity teacherSelected = event.getSelectedItem();
				if (teacherSelected == null || teacherSelected.getAdminId() == 0) {
					addNewTeacher();
					return;
				}

				TeacherManager.setTeacher(teacherSelected);
				saveSelectedTeacher();
				callback.teacherSelected(teacherSelected);
			}
		});

		initWidget(new MyFieldLabel(_combo, "Teacher Name", 90, 150));
	}

	
	public ComboBox<TeacherIdentity> getCombo() {
		return _combo;
	}
	
	private void addNewTeacher() {
		new AddTeacherDialog(new AddTeacherDialog.Callback() {
			public void teacherAdded(String teacherName) {
				callback.teacherAdded(teacherName);
			}
		});
		
//		final PromptMessageBox mb = new PromptMessageBox("Add Teacher","Teacher Name");
//		mb.addHideHandler(new HideHandler() {
//			public void onHide(HideEvent event) {
//				if (mb.getHideButton() == mb.getButtonById(PredefinedButton.OK.name())) {
//					addNewTeacher(mb.getValue(), new CallbackOnComplete() {
//						@Override
//						public void isComplete() {
//							mb.hide();
//						}
//					});
//				} else if (mb.getHideButton() == mb.getButtonById(PredefinedButton.CANCEL.name())) {
//					mb.hide();
//				}
//			}
//		});
//		mb.setWidth(300);
//		mb.show();
	}

	protected void saveSelectedTeacher() {
		TeacherIdentity teacher = _combo.getCurrentValue();
		String val = teacher.getAdminId() + "|" + teacher.getTeacherId() + "|"
				+ teacher.getTeacherName();
		Storage.getLocalStorage().setItem("current_teacher", val);
	}

	public void readDataFromServer(final CallbackOnComplete callbackOnComplete) {
		new RetryAction<CmList<TeacherIdentity>>() {
			@Override
			public void attempt() {
				GetAdminTeachersAction action = new GetAdminTeachersAction(
						UserInfoBase.getInstance().getUid());
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			@Override
			public void oncapture(CmList<TeacherIdentity> teachers) {
				_combo.getStore().clear();
				_combo.getStore().addAll(teachers);

				_combo.getStore().add(new TeacherIdentity(0, "-- Add New Teacher --", 0));
				
				setSelectedTeacher();
				
				if(callbackOnComplete != null) {
					callbackOnComplete.isComplete();
				}
			}
		}.register();
	}
	
	 protected void setSelectedTeacher() {
			TeacherIdentity activeTeacher = TeacherManager.getTeacher();
			if(activeTeacher != null) {
				_combo.setValue(activeTeacher);
			}
	}


	protected boolean addNewTeacher(final String teacherName, final CallbackOnComplete callback) {
	        if(!validateName(teacherName)) {
	            CmMessageBox.showAlert("Invalid teacher name");
	            return false;
	        }
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
	                readDataFromServer(new CallbackOnComplete() {
						@Override
						public void isComplete() {
							
							for(TeacherIdentity ti: _combo.getStore().getAll()) {
								if(ti.getTeacherName().equals(teacherName)) {
									_combo.setValue(ti);
									TeacherManager.setTeacher(ti);
									SelectTeacherCombo.this.callback.teacherSelected(ti);
									break;
								}
							}
							
						}
					});
	                callback.isComplete();
	            }
	            
	            public void onFailure(Throwable error) {
	                Log.error("Error adding teacher", error);
	                CmBusyManager.setBusy(false);
	                RetryActionManager.getInstance().requestComplete(this);
	                if(error.getMessage().toLowerCase().contains("duplicate")) {
	                    //CmMessageBox.showAlert("Duplicate teacher name.");
	                    //setComboSelected(teacherName);
	                }
	                else {
	                    CmMessageBox.showAlert(error.getMessage());
	                }
	            }
	        }.register();
	        
	        
	        return true;
	        
	    }	

	 

	    private boolean validateName(String name) {
	        if(name == null || name.length() == 0) {
	            return false;
	        }
	        else {
	            return true;
	        }
	    }

}
