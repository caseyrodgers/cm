package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CustomProgramInfoModel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramInfoAction;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CustomProgramInfoSubDialog extends CmWindow {

    CustomProgramModel program;

    public CustomProgramInfoSubDialog(CustomProgramModel program) {
        this.program = program;
        setModal(true);
        setSize(400, 400);
        setResizable(false);
        
        addCloseButton();

        setHeading("Custom Program Information For: " + program.getProgramName());

        getProgramInfo();
        buildGui();
    }

    private void buildGui() {
        setLayout(new FitLayout());
        FormPanel form = new FormPanel();
        form.setHeaderVisible(false);
        
        String html = "<div style='margin-bottom: 10px;'>" +
                      "<h1>Program Name: " + program.getProgramName() + 
                      (program.getIsTemplate()?" (is a built-in)":"") +
                      "</h1>" +
                      "</div>";
        form.add(new Html(html));

        FieldSet fsLessons = new FieldSet();
        fsLessons.setHeading("Lessons in Program");
        fsLessons.setHeight("35%");
        fsLessons.setLayout(new FitLayout());
        _lessons =  new MyListViewLessons(program);
        fsLessons.add(_lessons);
        
        form.add(fsLessons);
        
        FieldSet assignments = new FieldSet();
        assignments.setHeading("Students Assigned Program");
        assignments.setHeight("35%");
        assignments.setLayout(new FitLayout());
        _assignments =  new MyListViewAssignments(program);
        assignments.add(_assignments);
        
        form.add(assignments);
        
        add(form);
    }

    private void getProgramInfo() {
        new RetryAction<CustomProgramInfoModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramInfoAction action = new CustomProgramInfoAction(StudentGridPanel.instance._cmAdminMdl
                        .getId(), program);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CustomProgramInfoModel info) {
                CmBusyManager.setBusy(false);
                _assignments.setProgramInfo(info);
                _lessons.setProgramInfo(info);
            }
        }.register();
    }
    
    MyListViewAssignments _assignments;
    MyListViewLessons _lessons;
    
    static class MyListViewAssignments extends ListView<StudentModelExt> {
        CustomProgramModel program;
        public MyListViewAssignments(CustomProgramModel program) {
            this.program = program;
            getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            ListStore<StudentModelExt> store = new ListStore<StudentModelExt>();
            setStore(store);
            setDisplayProperty("name");
        }
        public void setProgramInfo(CustomProgramInfoModel info) {
            getStore().add(info.getAssignedStudents());
        }
    }
    
    static class MyListViewLessons extends ListView<CustomLessonModel> {
        CustomProgramModel program;
        public MyListViewLessons(CustomProgramModel program) {
            this.program = program;
            getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            ListStore<CustomLessonModel> store = new ListStore<CustomLessonModel>();
            setStore(store);
            setDisplayProperty("lesson");
        }
        public void setProgramInfo(CustomProgramInfoModel info) {
            getStore().add(info.getLessons());
        }
    }    
}
