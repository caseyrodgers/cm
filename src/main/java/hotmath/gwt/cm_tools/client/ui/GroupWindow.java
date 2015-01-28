package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.shared.client.rpc.action.AddGroupAction;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Create Group UI
 * 
 * @TODO: remove dependency on ComboBox
 * 
 * @author bob
 *
 */
public class GroupWindow extends FlowLayoutContainer {
	
	private GWindow gw;
	private GroupInfoModel gm;
	private ComboBox <GroupInfoModel> grpCombo;
	private TextField name; 
	
	private FieldSet fs;
	private CmAdminModel cmAdminMdl;
	private int formHeight = 90;
	private int formWidth  = 350;
	private CmAsyncRequest requestCallback;
	private GroupInfoModel editGroup;
	
	public GroupWindow(CmAsyncRequest callback, CmAdminModel cm, ComboBox <GroupInfoModel> gc, boolean isNew, GroupInfoModel editGroup) {
	    this.requestCallback = callback;
		cmAdminMdl = cm;
		grpCombo = gc;
		gw = new GWindow(false);
		
		gw.setHeadingText((isNew)?"Define a New Group":"Edit Group");
        gw.setWidth(formWidth+10);
        gw.setHeight(formHeight+20);
        gw.setResizable(false);
        gw.setDraggable(true);
        gw.setModal(true);

		
		this.editGroup = editGroup;
		gw.setWidget(createForm(isNew));
 		gw.setVisible(true);
 		
        name.focus();
	}
	
	private Widget createForm(boolean isNew) {
	    
	    
	    FramedPanel framedPanel = new FramedPanel();
		
		//fp.setStyleName("group-form-panel");
		//fp.setLabelWidth(75);
		framedPanel.setHeight(formHeight);
		//framedPanel.setFooter(true);
		//framedPanel.setFrame(false);
		framedPanel.setHeaderVisible(false);
		framedPanel.setBodyBorder(false);
		//framedPanel.setIconStyle("icon-form");

		VerticalLayoutContainer verticalPanel = new VerticalLayoutContainer();
		
		name = new TextField();  
		//name.setFieldLabel("Group name");
		//name.setMaxLength(30);  // matches length in Register student form and DB

		name.setAllowBlank(false);
		name.setEmptyText("-- enter name --");
		if(!isNew && editGroup != null)
		    name.setValue(editGroup.getGroupName());
		
		verticalPanel.add(new MyFieldLabel(name,  "Group name", 100, 220));

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
		
        
        framedPanel.setWidget(verticalPanel);

        gw.addButton(saveButton(fs, isNew, name));
        gw.addButton(cancelButton());

        return framedPanel;
	}

	private TextButton cancelButton() {
		TextButton cancelBtn = new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                gw.close();
                if(grpCombo != null)
	    		   grpCombo.reset();
	        }  
	    });
        cancelBtn.addStyleName("cancel-button");
		return cancelBtn;
	}

	private TextButton saveButton(final FieldSet fs, final boolean isNew, final TextField textField) {
		TextButton saveBtn = new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
	        	String name = textField.getValue();
	        	if (name == null || name.trim().length() == 0) {
	        	    textField.focus();
	        		return;
	        	}
	        	
	        	if (name.trim().equals(GroupInfoModel.NEW_GROUP)) {
	        	    textField.focus();
	        	    String msg = "Invalid Group name, please re-enter";
	        	    CatchupMathTools.showAlert(msg);
	        	    return;
	        	}
	        	
		        if (isNew) {
		        	gm = new GroupInfoModel();
		        	gm.setGroupName(name);
	        	    addGroupRPC(cmAdminMdl.getUid(), gm);
	        	}
	        	else {
	        	    if(requestCallback != null)
	        	        requestCallback.requestComplete(GroupWindow.this.name.getValue());
	        	    gw.close();
	        	}
	        }
	    });
		saveBtn.addStyleName("save-button");
		return saveBtn;
	}
	
	protected void addGroupRPC(final int adminUid, final GroupInfoModel gm) {

        new RetryAction<GroupInfoModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                AddGroupAction action = new AddGroupAction(adminUid, gm);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(GroupInfoModel g) {
            	if(grpCombo != null) {
            		grpCombo.setValue(g, true);
            	}
            	/**
            	 * no message
            	 *
            	 * CatchupMathTools.showAlert("Create Group", "Group " + g.getName() + " created");
            	 */
            	gw.close();

            	if(requestCallback != null)
            		requestCallback.requestComplete(g.getGroupName());

            	//CmAdminDataReader.getInstance().fireRefreshData();
            	CmBusyManager.setBusy(false);
            }
            
            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                
                if(caught.getMessage().indexOf("you entered") > 0) {
                    CatchupMathTools.showAlert("Problem adding group", caught.getMessage());
                    RetryActionManager.getInstance().requestComplete(this);
                    return;
                }
                super.onFailure(caught);
            }

        }.register();

	}

}
