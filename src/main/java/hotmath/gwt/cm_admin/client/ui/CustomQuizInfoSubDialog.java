package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomQuizInfoAction;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CustomQuizInfoSubDialog extends CmWindow {

    CustomLessonModel quiz;

    public CustomQuizInfoSubDialog(CustomLessonModel quiz) {
        this.quiz = quiz;
        setModal(true);
        setSize(400, 300);
        setResizable(false);
        
        addCloseButton();

        setHeading("Custom Quiz Information For: " + quiz.getCustomProgramItem());

        getQuizInfo();
        buildGui();
    }

    Label _numberAssigned = new Label();
    private void buildGui() {
        setLayout(new BorderLayout());
        
        FieldSet quizInfo = new FieldSet();
        quizInfo.add(_numberAssigned);
        
        FieldSet assignments = new FieldSet();
        assignments.setHeading("Students Assigned Quiz");
        assignments.setHeight("50%");
        assignments.setLayout(new FitLayout());
        _assignments =  new MyListViewAssignments();
        assignments.add(_assignments);
        
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        form.setBodyBorder(false);
        form.add(quizInfo);
        form.add(assignments);
        add(form, new BorderLayoutData(LayoutRegion.CENTER));
    }

    private void getQuizInfo() {
        new RetryAction<CustomQuizInfoModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomQuizInfoAction action = new CustomQuizInfoAction(StudentGridPanel.instance._cmAdminMdl
                        .getId(), quiz);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CustomQuizInfoModel info) {
                CmBusyManager.setBusy(false);
                _numberAssigned.setText("Number of questions: " + info.getQuestionCount());
                _assignments.setQuizInfo(info);
            }
        }.register();
    }
    
    MyListViewAssignments _assignments;

    static class MyListViewAssignments extends ListView<StudentModelExt> {
        CustomProgramModel program;
        public MyListViewAssignments() {
            getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            ListStore<StudentModelExt> store = new ListStore<StudentModelExt>();
            setStore(store);
            setDisplayProperty("name");
        }
        public void setQuizInfo(CustomQuizInfoModel info) {
            getStore().add(info.getAssignedStudents());
        }
    }
}
