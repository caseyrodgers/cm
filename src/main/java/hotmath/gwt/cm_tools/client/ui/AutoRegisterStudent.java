package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;

public class AutoRegisterStudent extends RegisterStudent {
	
    TextField<String> _groupTag;
    TextField<String> _passwordTag;
    ComboBox<NumberModel> _numToCreate;
	public AutoRegisterStudent(StudentModel sm, CmAdminModel cm) {
	    super(sm, cm);
	    
	    _window.setHeading("Auto Registration");
	    _fsProfile.removeAll();
	    

	    _window.setHeight(500);
        
        _groupTag = new TextField<String>();  
        _groupTag.setFieldLabel("Unique Group Name");
        _groupTag.setAllowBlank(false);
        _groupTag.setId("groupTag");
        _groupTag.setEmptyText("-- enter unique group --");
        _fsProfile.add(_groupTag);

        _passwordTag = new TextField<String>();  
        _passwordTag.setFieldLabel("Passcode Tag");
        _passwordTag.setAllowBlank(false);
        _passwordTag.setId("passtag");
        _passwordTag.setEmptyText("-- enter passcode tag --");
        _fsProfile.add(_passwordTag);
	    
        
        FieldSet fsConfig = new FieldSet();
        fsConfig.setHeading("Configuration");
        
        ListStore<NumberModel>numStore = new ListStore <NumberModel> ();
        numStore.add(new NumberModel(10, "Create 10 Student Accounts"));
        numStore.add(new NumberModel(15, "Create 15 Student Accounts"));
        numStore.add(new NumberModel(25, "Create 25 Student Accounts"));
        numStore.add(new NumberModel(50, "Create 50 Student Accounts"));
        numStore.add(new NumberModel(100, "Create 100 Student Accounts"));
        _numToCreate = new ComboBox<NumberModel>();  
        _numToCreate.setFieldLabel("How Many");
        _numToCreate.setForceSelection(false);
        _numToCreate.setDisplayField("description");
        _numToCreate.setEditable(false);
        _numToCreate.setMaxLength(60);
        _numToCreate.setAllowBlank(false);
        _numToCreate.setTriggerAction(TriggerAction.ALL);
        _numToCreate.setStore(numStore);
        _numToCreate.setTitle("How Many");
        _numToCreate.setId("howMany-combo");
        _numToCreate.setTypeAhead(true);
        _numToCreate.setSelectOnFocus(true);
        _numToCreate.setWidth(280);        
        // combo.setDisplayField("howMany");  
        _numToCreate.setStore(numStore);  
        fsConfig.add(_numToCreate);

        _numToCreate.setValue(numStore.getAt(0));
        _formPanel.add(fsConfig);
        
        
	    // _formPanel.getButtonBar().removeAll();
	    
	    _formPanel.layout();
	    setVisible(true);
	}
	

	protected List<Button> getActionButtons() {
	    List<Button> list = new ArrayList<Button>();
        
        Button autoCreate = new Button("Auto Create");
        autoCreate  .addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                try {
                    doSubmitAction(_fsProgram, _formPanel, new AfterValidation() {
                        
                        @Override
                        public void afterValidation(StudentModel student) {
                            student.setName(_groupTag.getValue());
                            student.setPasscode(_passwordTag.getValue());
                            student.setGroup(_groupTag.getValue());
                            
                            NumberModel nm = _numToCreate.getValue();
                            new AutoRegistrationWindow(cmAdminMdl.getId(), student, nm.getNumToCreate());
                        }
                    });
                }
                catch(CmException cm) {
                    CatchupMathTools.showAlert("First, make sure all values on form are valid");
                }
            }
        });
        
        list.add(autoCreate);
        
        
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                _window.close();
            }
        });
        
        list.add(close);        
        
        return list;
	}
}



class NumberModel extends BaseModelData {
    public NumberModel(int cnt, String description) {
        set("numToCreate", cnt);
        set("description", description);
    }
    
    public Integer getNumToCreate() {
        return get("numToCreate");
    }
}
