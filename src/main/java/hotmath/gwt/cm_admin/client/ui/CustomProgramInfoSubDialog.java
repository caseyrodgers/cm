package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.list.ListCustomLesson;
import hotmath.gwt.cm_admin.client.ui.list.ListStudents;
import hotmath.gwt.cm_admin.client.ui.list.ListStudents.CallbackOnDoubleClick;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CustomProgramInfoModel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramInfoAction;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldSet;


public class CustomProgramInfoSubDialog extends GWindow {

    CustomProgramModel program;
    public CustomProgramInfoSubDialog(CustomProgramModel program) {
        super(false);
        this.program = program;
        setModal(true);
        setPixelSize(400, 500);
        setResizable(false);
        
        addCloseButton();

        setHeadingText("Custom Program Information For: " + program.getProgramName());

        buildGui();
        getProgramInfo();
    }

    private void buildGui() {
        
        BorderLayoutContainer blc = new BorderLayoutContainer();

        
        String html = "<div style='margin-bottom: 10px;'>" +
                      "<h1>Program Name: " + program.getProgramName() + 
                      (program.getIsTemplate()?" (is a built-in)":"") +
                      "</h1>" +
                      "</div>";
        blc.setNorthWidget(new HTML(html), new BorderLayoutData(.10));

        FieldSet fsLessons = new FieldSet();
        fsLessons.setHeadingText ("Lessons in Program");

        BorderLayoutContainer blcLessons = new BorderLayoutContainer();
        _lessons =  new MyListViewLessons(program);
        blcLessons.setCenterWidget(_lessons);
        blcLessons.setSouthWidget(CustomProgramDesignerDialog.getLegend(), new BorderLayoutData(40));
        
        fsLessons.setWidget(blcLessons);
        fsLessons.setBorders(false);
        
        CustomProgramDesignerDialog.getLegend();
        blc.setCenterWidget(fsLessons, new BorderLayoutData(.45));
        
        FieldSet assignments = new FieldSet();
        assignments.setHeadingText("Students Assigned Program");
        assignments.setBorders(false);
        
        _studentsAssignments =  new MyListStudents(program, new CallbackOnDoubleClick() {
            @Override
            public void doubleClicked(StudentModelExt student) {
            }
        });
        // setProgram(program)
        //
        
        assignments.add(_studentsAssignments);
        
        blc.setSouthWidget(assignments, new BorderLayoutData(.45));
        
        
        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        frame.setWidget(blc);
        
        setWidget(blc);
    }

    private void getProgramInfo() {
        new RetryAction<CustomProgramInfoModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramInfoAction action = new CustomProgramInfoAction(UserInfoBase.getInstance().getUid(), program);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CustomProgramInfoModel info) {
                CmBusyManager.setBusy(false);
                _studentsAssignments.setProgramInfo(info);
                _lessons.setProgramInfo(info);
            }
        }.register();
    }
    
    MyListStudents _studentsAssignments;
    ListCustomLesson _lessons;
    
    static class MyListStudents extends ListStudents {
        CustomProgramModel program;
        public MyListStudents(CustomProgramModel program, CallbackOnDoubleClick callback) {
            super(callback);
            this.program = program;
        }
        public void setProgramInfo(CustomProgramInfoModel info) {
            getStore().addAll(info.getAssignedStudents());
        }
    }  

    
    static class MyListViewLessons extends ListCustomLesson {
        CustomProgramModel program;
        public MyListViewLessons(CustomProgramModel program) {
            super(null);
            this.program = program;
            // setDisplayProperty("customProgramItem");
        }
        public void setProgramInfo(CustomProgramInfoModel info) {
            getStore().addAll(info.getLessons());
        }
    }    
}
