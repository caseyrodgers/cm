package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.teacher.TeacherManager;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc.client.rpc.CreateCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/** Edit Custom Problem meta info
 * 
 * @author casey
 *
 */
public class CustomProblemPropertyEditor extends GWindow {

    Callback callback;
    CustomProblemModel problem;
    String _teacherNameFromCookie;
    private List<String> paths;
    
    public interface Callback {
        void solutionCreated(SolutionInfo solution);
    }
    public CustomProblemPropertyEditor(CustomProblemModel problem,List<String> optionalPaths, Callback callback) {
        super(false);
        
        assert(problem != null);
        
        this.paths = optionalPaths;
        
        this.problem = problem;
        this.callback = callback;
        setResizable(false);
        setPixelSize(400, 240);
        setHeadingText("Create New Solution");
        
        
        _teacherNameFromCookie = TeacherManager.getTeacher().getTeacherName();
        

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

        
        problem.getTeacher().setTeacherName(_teacherName.getCurrentValue());
        
        problem.setComments(_comments.getValue());
        problem.setTreePath(_treePath.getCurrentValue()!=null?_treePath.getCurrentValue().getFolder():null);
        
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
                callback.solutionCreated(solution);
            }
        }.register();
    }


    private String verifyFields() {
        if(_teacherName.getValue() == null || _teacherName.getValue().length() == 0) {
            return "Teacher must be specified.";
        }
        if(!_treePath.validate()) {
        	return "Folder name is invalid.";
        }
        if(!_comments.validate()) {
            return "Comment must be specified.";
        }
        return null;
    }

    class OptionalFolder {
        String folder;
        
        public OptionalFolder(String folder){
            this.folder = folder;
        }
        
        public String getFolder() {
            return folder;
        }
    }
    interface Props extends PropertyAccess<String> {
        @Path("folder")
        ModelKeyProvider<OptionalFolder> key();
        
        @Path("folder")
        LabelProvider<OptionalFolder> label();
    }
    
    Props props = GWT.create(Props.class);

    TextField _teacherName = new TextField();
    TextArea _comments = new TextArea();
    TextField _problemNumber = new TextField();
    ComboBox<OptionalFolder> _treePath;
    private void buildGui() {
        FramedPanel frame = new FramedPanel();
        _teacherName.setToolTip("Unique teacher name.  To select a differnt teacher, cancel and use the Select Teacher button.");
        _teacherName.setEnabled(false);
        String teacherName = this.problem.getTeacher().getTeacherName();
        if(teacherName == null) {
            teacherName = _teacherNameFromCookie;
        }
        _teacherName.setValue(teacherName);        
        _comments.setHeight(100);
        _comments.setAllowBlank(false);
        _comments.setValue(problem.getComments());
        _problemNumber.setToolTip("The unique problem number automatically assigned");
        _problemNumber.setEnabled(false);
        
        ListStore<OptionalFolder> store = new ListStore<OptionalFolder>(props.key());
        _treePath = new ComboBox<OptionalFolder>(store,props.label());
        _treePath.setTriggerAction(TriggerAction.ALL);
        //_treePath.setForceSelection(true);
        _treePath.setEditable(false);
        _treePath.setEmptyText("Optional Folder");
        for(String of: this.paths) {
            // only the current teacher paths are added
            String path[] = of.split("/");
            if(path.length > 1) {
                if(path[0].equals(teacherName)) {
                    store.add(new OptionalFolder(path[1]));
                }
            }
        }
        _treePath.setValue(getOptionFolder(problem.getTreePath() != null?problem.getTreePath():""));
        VerticalLayoutContainer form = new VerticalLayoutContainer();
        form.add(new MyFieldLabel(_teacherName, "Teacher", 100));
        form.add(new MyFieldLabel(_comments, "Comment", 100, 270));
        form.add(new MyFieldLabel(_treePath, "Optional Folder", 100, 150));
        //form.add(new MyFieldLabel(_problemNumber, "Number", 100, 40));
        
        
        frame.setWidget(form);
        frame.setHeaderVisible(false);
        setWidget(frame);
    }


    private OptionalFolder getOptionFolder(String folder) {
        for(OptionalFolder op: _treePath.getStore().getAll()) {
            if(op.getFolder().equals(folder)) {
                return op;
            }
        }
        
        return null;
    }

}
