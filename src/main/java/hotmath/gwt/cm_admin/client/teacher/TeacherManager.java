package hotmath.gwt.cm_admin.client.teacher;

import java.util.Date;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.GetAdminTeachersAction;
import hotmath.gwt.cm_rpc.client.ui.AddAdminTeacherAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class TeacherManager extends GWindow {
    
    public interface Callback {
        void teacherSet(TeacherIdentity teacher);
    }

    private Callback callback;

    static private TeacherIdentity __currentTeacherIdentity;
    
    public TeacherManager(Callback callbackIn) {
        super(false);
        
        this.callback = callbackIn;
        setHeadingText("Teacher Manager");
        setModal(true);
        setPixelSize(400, 200);
        setResizable(false);
        setMaximizable(false);
        
        buildUi();
        
        _selected = Cookies.getCookie("teacher_name");
        
        addButton(new TextButton("OK", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                __currentTeacherIdentity = getSelectedTeacher();
                
                
                Date now = new Date();
                long nowLong = now.getTime();
                nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);//seven days
                now.setTime(nowLong);

                Cookies.setCookie("teacher_name", __currentTeacherIdentity.getTeacherName(), now);
                
                callback.teacherSet(__currentTeacherIdentity);
                hide();
            }
        }));
        
        addButton(new TextButton("Cancel", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        readDataFromServer();
        
        
        setVisible(true);
    }
    
    private void readDataFromServer() {
        new RetryAction<CmList<TeacherIdentity>>() {
            @Override
            public void attempt() {
                GetAdminTeachersAction action = new GetAdminTeachersAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(CmList<TeacherIdentity> teachers) {
                _combo.getStore().clear();
                _combo.getStore().addAll(teachers);
                
                if(_selected != null) {
                    setComboSelected(_selected);
                }
                
            }
        }.register();
    }
    
    private void setComboSelected(String sel) {
        for(int i=0;i<_combo.getStore().getAll().size();i++) {
            TeacherIdentity ti = _combo.getStore().getAll().get(i);
            if(ti.getTeacherName().equals(sel)) {
                _combo.setValue(ti);
                break;
            }
        }
    }

    protected TeacherIdentity getSelectedTeacher() {
        return _combo.getCurrentValue();
    }

    interface ComboProps extends PropertyAccess<String> {
        @Path("teacherId")
        ModelKeyProvider<TeacherIdentity> key();
        LabelProvider<TeacherIdentity> teacherName();
    }
    
    
    ComboProps props = GWT.create(ComboProps.class);
    ComboBox<TeacherIdentity> _combo;
    private void buildUi() {
        ListStore<TeacherIdentity> store = new ListStore<TeacherIdentity>(props.key());
        _combo = new ComboBox<TeacherIdentity>(store, props.teacherName());
        _combo.setEmptyText("-- Choose a Teacher Name --");
        _combo.setAllowBlank(false);
        _combo.setTriggerAction(TriggerAction.ALL);
        _combo.setEditable(false);
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(new HTML("<p style='margin-top: 20px;'>Choose your teacher name.  If it does not exists, create a new one"));
        flow.add(new MyFieldLabel(_combo,  "Teacher Name",  90, 200));
        
        FramedPanel fp = new FramedPanel();
        addTool(new TextButton("Add New Teacher", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                addNewTeacher();
            }
        }));
        fp.setHeaderVisible(false);
        fp.setWidget(flow);
        setWidget(fp);
    }

    protected void addNewTeacher() {
        final PromptMessageBox mb = new PromptMessageBox("Add Teacher", "Teacher Name");
        mb.addHideHandler(new HideHandler() {
          public void onHide(HideEvent event) {
            if (mb.getHideButton() == mb.getButtonById(PredefinedButton.OK.name())) {
                addNewTeacher(mb.getValue(), new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        mb.hide();
                    }
                });
            } else if (mb.getHideButton() == mb.getButtonById(PredefinedButton.CANCEL.name())) {
                mb.hide();
            }
          }
        });
        mb.setWidth(300);
        mb.show();
    }

    String _selected;
    protected void addNewTeacher(final String teacherName, final CallbackOnComplete callback) {
        if(!validateName(teacherName)) {
            CmMessageBox.showAlert("Invalid teacher name");
            return;
        }
        
        _selected = teacherName;
        CmBusyManager.setBusy(true);
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                AddAdminTeacherAction action = new AddAdminTeacherAction(UserInfoBase.getInstance().getUid(), teacherName);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                readDataFromServer();
                callback.isComplete();
            }
            
            public void onFailure(Throwable error) {
                Log.error("Error adding teacher", error);
                CmBusyManager.setBusy(false);
                RetryActionManager.getInstance().requestComplete(this);
                if(error.getMessage().toLowerCase().contains("duplicate")) {
                    //CmMessageBox.showAlert("Duplicate teacher name.");
                    setComboSelected(teacherName);
                }
                else {
                    CmMessageBox.showAlert(error.getMessage());
                }
            }
        }.register();
        
        
    }

    private boolean validateName(String name) {
        if(name == null || name.length() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    public static TeacherIdentity getTeacher() {
        return __currentTeacherIdentity;
    }

}
