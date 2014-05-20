package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.FramedPanel;
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
    TextArea _comments;
    
    public CustomProblemPropertiesDialog() {
        super(false);
        setHeadingText("Setup Custom Problem properties");

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

        CmBusyManager.setBusy(true);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemLinkedLessonAction action = new SaveCustomProblemLinkedLessonAction(UserInfoBase.getInstance().getUid(), pid, comments, new CmArrayList<LessonModel>(lessons));
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                Log.info("Custom Problem linked to lessons: " + value);
                CmBusyManager.setBusy(false);
                
                callbackOnComplete.isComplete();
                
                hide();
            }
        }.register();

    }

    
    private void drawGui() {
        BorderLayoutContainer bl = new BorderLayoutContainer();
        _comments = new TextArea();
        FramedPanel fp1 = new FramedPanel();
        BorderLayoutContainer bl2 = new BorderLayoutContainer();
        bl2.setNorthWidget(new HTML("<b>Problem Comment</b>"), new BorderLayoutData(30));
        bl2.setCenterWidget(_comments);
        
        fp1.setWidget(bl2);
        fp1.setHeaderVisible(false);
        fp1.setBorders(false);
        
        bl.setNorthWidget(fp1, new BorderLayoutData(120));

        _lessonsPanel = new CustomProblemLinkedLessonsPanel();
        bl.setCenterWidget(_lessonsPanel);
        
        setWidget(bl);
    }
}
