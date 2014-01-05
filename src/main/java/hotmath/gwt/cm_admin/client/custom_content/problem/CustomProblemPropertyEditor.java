package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.CreateCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Cookies;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/** Edit Custom Problem meta info
 * 
 * @author casey
 *
 */
public class CustomProblemPropertyEditor extends GWindow {

    CallbackOnComplete callback;
    CustomProblemModel problem;
    String _teacherNameFromCookie;
    
    public CustomProblemPropertyEditor(CustomProblemModel problem, CallbackOnComplete callback) {
        super(false);
        
        assert(problem != null);
        
        this.problem = problem;
        this.callback = callback;
        
        setPixelSize(400, 300);
        setHeadingText("Problem Properties");
        
        
        _teacherNameFromCookie = Cookies.getCookie("teacher_name");
        

        buildGui();
        
        
        TextButton saveBtn = new TextButton("Save", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                saveProblem();
            }
        });
        if(problem.getPid() == null) {
            saveBtn.setText("Create");
        }
        addButton(saveBtn);
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        setVisible(true);
    }
    
    
    protected void saveProblem() {
        String message = verifyFields();
        if(message != null) {
            CmMessageBox.showAlert(message);
            return;
        }
        
        Cookies.setCookie("teacher_name", _teacherName.getCurrentValue());
        
        problem.getTeacher().setTeacherName(_teacherName.getCurrentValue());
        
        problem.setComments(_comments.getValue());
        
        new RetryAction<SolutionInfo>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                

                CreateCustomProblemAction action = new CreateCustomProblemAction(problem);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(SolutionInfo solution) {
                CmBusyManager.setBusy(false);
                InfoPopupBox.display("Problem Created",  "New custom problem created: " + solution.getPid());
                hide();
                callback.isComplete();
            }
        }.register();
    }


    private String verifyFields() {
        if(_teacherName.getValue() == null || _teacherName.getValue().length() == 0) {
            return "Teacher must be specified";
        }
        return null;
    }


    TextField _teacherName = new TextField();
    TextArea _comments = new TextArea();
    TextField _problemNumber = new TextField();
    private void buildGui() {
        FramedPanel frame = new FramedPanel();

        _teacherName.setToolTip("Unique teacher name");
        _comments.setHeight(100);
        _comments.setValue(problem.getComments());
        _problemNumber.setToolTip("The unique problem number automatically assigned");
        _problemNumber.setEnabled(false);

        VerticalLayoutContainer form = new VerticalLayoutContainer();
        form.add(new MyFieldLabel(_teacherName, "Teacher", 100));
        form.add(new MyFieldLabel(_comments, "Comment", 100, 200));
        //form.add(new MyFieldLabel(_problemNumber, "Number", 100, 40));
        
        
        String teacherName = this.problem.getTeacher().getTeacherName();
        if(teacherName == null) {
            teacherName = _teacherNameFromCookie;
        }
        _teacherName.setValue(teacherName);
        
        
        frame.setWidget(form);
        frame.setHeaderVisible(false);
        setWidget(frame);
    }

}
