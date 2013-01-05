package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.util.CmMessageBoxGxt2;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;


public class AutoRegisterStudent extends RegisterStudent {
	
    TextField _groupTag;
    TextField _passwordTag;
    ComboBox<NumberModel> _numToCreate;
    
    static NumberModelProperties __propsNumberModel = GWT.create(NumberModelProperties.class);
	public AutoRegisterStudent(StudentModel sm, CmAdminModel cm) {
	    super(sm, cm);
	    
	    _window.setHeadingText("Self Registration Setup");
	    _fsProfile.clear();
	    

	    _window.setHeight(500);
        
        _groupTag = new TextField();  
        //_groupTag.setFieldLabel("Unique Group Name");
        _groupTag.setAllowBlank(false);
        _groupTag.setId("groupTag");
        _groupTag.setEmptyText("-- enter unique group --");
        _fsProfile.addThing(_groupTag);

        _passwordTag = new TextField();  
        //_passwordTag.setFieldLabel("Passcode Tag");
        _passwordTag.setAllowBlank(false);
        _passwordTag.setId("passtag");
        _passwordTag.setEmptyText("-- enter passcode tag --");
        _fsProfile.addThing(_passwordTag);
        
        MyFieldSet fsConfig = new MyFieldSet("Configuration",FIELDSET_WIDTH);
        
        ListStore<NumberModel>numStore = new ListStore <NumberModel> (__propsNumberModel.id());
        numStore.add(new NumberModel(10, "Create 10 Student Accounts"));
        numStore.add(new NumberModel(15, "Create 15 Student Accounts"));
        numStore.add(new NumberModel(25, "Create 25 Student Accounts"));
        numStore.add(new NumberModel(50, "Create 50 Student Accounts"));
        numStore.add(new NumberModel(100, "Create 100 Student Accounts"));
        _numToCreate = new ComboBox<NumberModel>(numStore,__propsNumberModel.description());  
        // _numToCreate.setFieldLabel("How Many");
        //_numToCreate.setMaxLength(60);
        
        _numToCreate.setForceSelection(false);
        _numToCreate.setEditable(false);
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

        _numToCreate.setValue(numStore.get(0));
        _formPanel.add(fsConfig);
        
        
	    // _formPanel.getButtonBar().removeAll();
	    _formPanel.forceLayout();
	    setVisible(true);
	}
	

	public List<TextButton> getActionButtons() {
		List<TextButton> list = new ArrayList<TextButton>();

		TextButton autoCreate = new TextButton("Auto Create", new SelectHandler() {
		    @Override
		    public void onSelect(SelectEvent event) {
				try {
					doSubmitAction(new AfterValidation() {

						@Override
						public void afterValidation(StudentModel student) {
						    
					        if(!verifyOkToSave(student)) {
					            return;
					        }						    
						    
							student.setName(_groupTag.getValue());
							student.setPasscode(_passwordTag.getValue());
							student.setGroup(_groupTag.getValue());

							NumberModel nm = _numToCreate.getValue();
							new AutoRegistrationWindow(student,null);
						}
					});
				}
				catch(CmException cm) {
					CmMessageBoxGxt2.showAlert("First, make sure all values on form are valid");
				}
			}
		});

		list.add(autoCreate);


		TextButton close = new TextButton("Close");
		close.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				_window.close();
			}
		});

		list.add(close);        

		return list;
	}
}



class NumberModel  {
    private int numToCreate;
    private String description;

    public NumberModel(int cnt, String description) {
        this.numToCreate = cnt;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumToCreate() {
        return numToCreate;
    }

    public void setNumToCreate(int numToCreate) {
        this.numToCreate = numToCreate;
    }
}


interface NumberModelProperties extends PropertyAccess<Integer> {
    @Path("numToCreate")
    ModelKeyProvider<NumberModel> id();

    LabelProvider<NumberModel> description();

    LabelProvider<NumberModel> numToCreate();
    
}