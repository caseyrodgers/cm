package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.list.ListStudents;
import hotmath.gwt.cm_admin.client.ui.list.ListStudents.CallbackOnDoubleClick;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomQuizInfoAction;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldSet;

public class CustomQuizInfoSubDialog extends GWindow {

    CustomQuizDef quiz;

    public CustomQuizInfoSubDialog(CustomQuizDef quiz) {
    	super(false);
    	
        this.quiz = quiz;
        setModal(true);
        setPixelSize(400, 300);
        setResizable(false);
        
        addCloseButton();

        setHeadingText("Custom Quiz Information For: " + quiz.getQuizName());

        getQuizInfo();
        buildGui();
    }

    Label _numberAssigned = new Label();
    BorderLayoutContainer _main = new BorderLayoutContainer();
    private void buildGui() {
        
        
        FieldSet fsAssignments = new FieldSet();
        fsAssignments.setHeadingText("Students Assigned Quiz");
        fsAssignments.setBorders(false);
        
        _assignments =  new MyListStudents(new CallbackOnDoubleClick() {
            @Override
            public void doubleClicked(StudentModelExt student) {
            }
        });
        fsAssignments.setWidget(_assignments);
        
        BorderLayoutContainer blc = new BorderLayoutContainer();
        blc.setNorthWidget(_numberAssigned, new BorderLayoutData(25));
        
        blc.setCenterWidget(fsAssignments);
        
        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        frame.setWidget(blc);
        
        setWidget(frame);
    }

    private void getQuizInfo() {
        new RetryAction<CustomQuizInfoModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomQuizInfoAction action = new CustomQuizInfoAction(UserInfoBase.getInstance().getUid(), quiz);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CustomQuizInfoModel info) {
                CmBusyManager.setBusy(false);
                _numberAssigned.setText("Number of questions: " + info.getQuestionCount());
                _assignments.setQuizInfo(info);
            }
        }.register();
    }
    
    MyListStudents _assignments;
    static class MyListStudents extends ListStudents {
        public MyListStudents(CallbackOnDoubleClick callback) {
            super(callback);
        }
        public void setQuizInfo(CustomQuizInfoModel info) {
            getStore().addAll(info.getAssignedStudents());
        }
    }
}
