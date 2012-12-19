package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/*
 * Displays wrapper around StudentDetails Panel
 *
 * Derived from StudentDetailsPanel (retired)
 * 
 * @author Bob
 * @author Casey
 * 
 */

public class StudentDetailsWindow extends CmWindow {
    
    StudentDetailsPanel studentDetailsPanel;

    /**
     * Create StudentDetailsWindow for student. Shows all student activity for
     * given user order by last use (most recent first)
     * 
     * StudentModel must be fully filled out to populate the infoPanel
     * 
     * @param studentModel
     */
    public StudentDetailsWindow(final StudentModelExt studentModel) {
        addStyleName("student-details-window");
        setSize(645, 410);
        setModal(true);
        setResizable(false);
        setHeading("Student Details For: " + studentModel.getName());
        addStyleName("student-details-window-container");

        Button btnClose = closeButton();
        setButtonAlign(HorizontalAlignment.RIGHT);
        addButton(btnClose);

        studentDetailsPanel = new StudentDetailsPanel(studentModel);
        
        setLayout(new FitLayout());
        add(studentDetailsPanel);
        
        getButtonBar().setStyleAttribute("position", "relative");
        getButtonBar().add(studentDetailsPanel.getDateRange());

        setVisible(true);
        
        
        setZIndex(10);
    }

    private Button closeButton() {
        Button btn = new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        btn.setIconStyle("icon-delete");
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
                StudentModelExt sm = new StudentModelExt(result);
                new StudentDetailsWindow(sm);
            }
        }.register();
    }

}
