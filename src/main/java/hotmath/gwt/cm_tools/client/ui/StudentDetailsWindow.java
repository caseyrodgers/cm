package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/*
 * Displays wrapper around StudentDetails Panel
 *
 * Derived from StudentDetailsPanel (retired)
 * 
 * @author Bob
 * @author Casey
 * 
 */

public class StudentDetailsWindow extends GWindow {
    
    StudentDetailsPanel studentDetailsPanel;

    /**
     * Create StudentDetailsWindow for student. Shows all student activity for
     * given user order by last use (most recent first)
     * 
     * StudentModel must be fully filled out to populate the infoPanel
     * 
     * @param studentModel
     */
    public StudentDetailsWindow(final StudentModelI studentModel) {
        super(false);
        
        addStyleName("student-details-window");
        setPixelSize(645, 410);
        setModal(true);
        setResizable(true);
        setMaximizable(true);
        setHeadingText("Student Details For: " + studentModel.getName());
        addStyleName("student-details-window-container");

        studentDetailsPanel = new StudentDetailsPanel(studentModel);
        
        setWidget(studentDetailsPanel);
        
        getButtonBar().addStyleName("student-details-window-button-bar");
        getButtonBar().add(studentDetailsPanel.getDateRange());
        addButton(closeButton());

        setVisible(true);
    }

    private TextButton closeButton() {
        TextButton btn = new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
            }
        });
        // btn.setIconStyle("icon-delete");
        return btn;
    }


    static public void showStudentDetails(final int userId) {
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetStudentModelAction action = new GetStudentModelAction(userId);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }            
            
            @Override
            public void oncapture(StudentModelI result) {
                CmBusyManager.setBusy(false);
                new StudentDetailsWindow(result);
            }
        }.register();
    }

}
