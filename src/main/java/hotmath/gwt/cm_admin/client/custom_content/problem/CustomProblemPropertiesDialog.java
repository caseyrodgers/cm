package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class CustomProblemPropertiesDialog extends GWindow {

    private static CustomProblemPropertiesDialog __instance;

    public static Component getInstance(CallbackOnComplete callbackOnComplete, CustomProblemModel problem) {
        __instance = null;
        if (__instance == null) {
            __instance = new CustomProblemPropertiesDialog();
        }
        __instance.setSolution(callbackOnComplete, problem);
        
        __instance.setVisible(true);
        return __instance;
    }

    private CustomProblemModel _customProblem;
    private CallbackOnComplete callbackOnComplete;

    private void setSolution(CallbackOnComplete callbackOnComplete, CustomProblemModel problem) {
        this.callbackOnComplete = callbackOnComplete;
        _customProblem = problem;
        _lessonsPanel.setSolution(problem.getPid());
        _comments.setValue(problem.getComments());
    }

    CustomProblemLinkedLessonsPanel _lessonsPanel;
    TextArea _comments = new TextArea();
    public CustomProblemPropertiesDialog() {
        super(false);
        setHeadingText("Setup Custom Problem Properties");

        setPixelSize(450, 400);

        drawGui();
        
        addButton(new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }

        }));
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(isDirty()) {
                    CmMessageBox.confirm("Close?",  "There are pending changes.  Are you sure you want to cancel?", new ConfirmCallback() {
                        @Override
                        public void confirmed(boolean yesNo) {
                            if(yesNo) {
                                hide();
                            }
                        }
                    });
                }
                else {
                    hide();
                }
            }
        }));
        
    }


    protected boolean isDirty() {
        if(_lessonsPanel._isDirty) {
            return true;
        }
        if(_comments.getValue() != null && (!_comments.getValue().equals(_customProblem.getComments()))) {
            return true;
        }
        return false;
    }


    private void doSave() {
        saveLessonsToServer(_customProblem.getPid(), _comments.getValue(), _lessonsPanel._grid.getStore().getAll());
    }
    

    private void saveLessonsToServer(final String pid, final String comments, final List<LessonModel> lessons) {
        if(!_comments.validate()) {
            CmMessageBox.showAlert("Comment must be specified.");
            return;
        }
        
        CmBusyManager.setBusy(true);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemLinkedLessonAction action = new SaveCustomProblemLinkedLessonAction(UserInfoBase.getInstance().getUid(), _customProblem.getTeacher().getTeacherId(), pid, comments, new CmArrayList<LessonModel>(lessons));
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                Log.info("Custom Problem linked to lessons: " + value);
                CmBusyManager.setBusy(false);
                
                callbackOnComplete.isComplete();
                
                hide();
            }
            
            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);
                if(error.getMessage().indexOf("Duplicate") > -1) {
                    error = null; // handled here
                    CmMessageBox.showAlert("Duplicate problem name");
                }
                super.onFailure(error);                
            }
        }.register();

    }

    
    private void drawGui() {
    	TabPanel tabPanel = new TabPanel();
        BorderLayoutContainer bl = new BorderLayoutContainer();
        FramedPanel fp1 = new FramedPanel();
        BorderLayoutContainer bl2 = new BorderLayoutContainer();
        bl2.setNorthWidget(new HTML("<b>Comment</b>"), new BorderLayoutData(15));
        _comments.setAllowBlank(false);
        bl2.setCenterWidget(_comments);
        
        fp1.setWidget(bl2);
        fp1.setHeaderVisible(false);
        fp1.setBorders(false);
        
        bl.setNorthWidget(fp1, new BorderLayoutData(120));

        _lessonsPanel = new CustomProblemLinkedLessonsPanel();
        bl.setCenterWidget(_lessonsPanel);
        
        tabPanel.add(bl, "Setup Properties");
        
        final AssignmentsUsedPanel _assPanel = new AssignmentsUsedPanel();
        tabPanel.add(_assPanel, "Assigned");
        tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if(event.getSelectedItem() == _assPanel) {
					_assPanel.readDataFromServer(_customProblem.getPid());
				}
			}
		});
        
        setWidget(tabPanel);
    }
}
