package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Create Group UI
 * 
 * @TODO: remove dependency on ComboBox
 * 
 * @author bob
 *
 */
public class GroupWindow extends LayoutContainer {
	
	private CmWindow gw;
	private GroupModel gm;
	private ComboBox <GroupModel> grpCombo;
	private TextField<String> name; 
	
	private FieldSet fs;
	private CmAdminModel cmAdminMdl;
	private int formHeight = 90;
	private int formWidth  = 350;
	private CmAsyncRequest requestCallback;
	private GroupModel editGroup;
	
	public GroupWindow(CmAsyncRequest callback, CmAdminModel cm, ComboBox <GroupModel> gc, boolean isNew, GroupModel editGroup) {
	    this.requestCallback = callback;
		cmAdminMdl = cm;
		grpCombo = gc;
		gw = new CmWindow();
		this.editGroup = editGroup;
		gw.add(createForm(isNew));
 		gw.show();
        name.focus();
	}
	
	private FormPanel createForm(boolean isNew) {
		FormPanel fp = new FormPanel();
		//fp.setStyleName("group-form-panel");
		fp.setLabelWidth(75);
		fp.setHeight(formHeight);
		fp.setFooter(true);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setLayout(new FormLayout());
		
		name = new TextField<String>();  
		name.setFieldLabel("Group name");
		name.setMaxLength(30);  // matches length in Register student form and DB
		name.setAllowBlank(false);
		name.setId(GroupModel.NAME_KEY);
		name.setEmptyText("-- enter name --");
		
		
		
		if(!isNew && editGroup != null)
		    name.setValue(editGroup.getName());
		
		fp.add(name);

        /** Seems like a bug with setting focus, so the only way to get it 
         *  to work is to set a timer and hope ... 
         *  
         *  @TODO: get name.focus() to just work without timing tricks.
         */
        new Timer() {
            public void run() {
                name.focus();
            }
        }.schedule(1000);
		
		gw.setHeading((isNew)?"Define a New Group":"Edit Group");
		gw.setWidth(formWidth+10);
		gw.setHeight(formHeight+20);
		gw.setLayout(new FitLayout());
		gw.setResizable(false);
		gw.setDraggable(true);
		gw.setModal(true);

		Button cancelBtn = cancelButton();
        cancelBtn.setStyleName("cancel-button");
        
		Button saveBtn = saveButton(fs, isNew, fp);
		saveBtn.setStyleName("save-button");
		
		fp.setButtonAlign(HorizontalAlignment.RIGHT);  
        fp.addButton(saveBtn);
        fp.addButton(cancelBtn);
        return fp;
	}

	private Button cancelButton() {
		Button cancelBtn = new Button("Cancel", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
                gw.close();
                if(grpCombo != null)
	    		   grpCombo.reset();
	        }  
	    });
		return cancelBtn;
	}

	private Button saveButton(final FieldSet fs, final boolean isNew, final FormPanel fp) {
		Button saveBtn = new Button("Save", new SelectionListener<ButtonEvent>() {  
	        @SuppressWarnings("unchecked")
	    	public void componentSelected(ButtonEvent ce) {
	        	TextField<String> tf = (TextField<String>)fp.getItemByItemId(GroupModel.NAME_KEY);
	        	String name = tf.getValue();
	        	if (name == null || name.trim().length() == 0) {
	        		tf.focus();
	        		return;
	        	}
	        	
	        	if (name.trim().equals(GroupModel.NEW_GROUP)) {
	        	    tf.focus();
	        	    String msg = "Invalid Group name, please re-enter";
	        	    CatchupMathTools.showAlert(msg);
	        	    return;
	        	}
	        	
		        if (isNew) {
		        	gm = new GroupModel();
		        	gm.setName(name);
	        	    addGroupRPC(cmAdminMdl.getId(), gm);
	        	}
	        	else {
	        	    if(requestCallback != null)
	        	        requestCallback.requestComplete(GroupWindow.this.name.getValue());
	        	    gw.close();
	        	}
	        }
	    });
		return saveBtn;
	}
	
	protected void addGroupRPC(int adminUid, final GroupModel gm) {
	    
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		
		s.addGroup(adminUid, gm, new AsyncCallback <GroupModel> () {
			
			public void onSuccess(GroupModel g) {
			    if(grpCombo != null) {
				    grpCombo.getStore().add(g);
				    grpCombo.getStore().sort(GroupModel.NAME_KEY, SortDir.ASC);
				    grpCombo.setValue(g);
			    }
				CatchupMathTools.showAlert("Create Group", "Group " + g.getName() + " created");
				gw.close();
				
				if(requestCallback != null)
				    requestCallback.requestComplete(gm.getName());
				
				
				CmAdminDataReader.getInstance().fireRefreshData();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
        	}
        });
	}

}
