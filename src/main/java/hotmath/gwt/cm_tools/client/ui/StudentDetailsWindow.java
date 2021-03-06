package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;

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
        
        setPixelSize(645, 410);
        setModal(true);
        setResizable(true);
        setMaximizable(true);
        setHeadingText("Student Details For: " + studentModel.getName());

        studentDetailsPanel = new StudentDetailsPanel(studentModel);
        
        setWidget(studentDetailsPanel);
        
        
        
        addButton(studentDetailsPanel.getDateRange());
        addButton(closeButton());
        
        
        setVisible(true);
        
        
        forceLayout();
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
        
        CmBusyManager.setBusy(true);
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                GetStudentModelAction action = new GetStudentModelAction(userId);
                setAction(action);
                CmRpcCore.getCmService().execute(action,this);
            }            
            
            @Override
            public void oncapture(StudentModelI result) {
                CmBusyManager.setBusy(false);
                new StudentDetailsWindow(result);
            }
        }.register();
    }

}
