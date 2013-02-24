package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentsContentPanel;
import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentsContentPanel.Callback;
import hotmath.gwt.cm_admin.client.ui.assignment.GroupNameProperties;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGroupsAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * Provide dialog to allow Admins ability to define and manage Assignments.
 * 
 * 
 * @author casey
 * 
 */
public class AssignmentManagerDialog2  {

    static AssignmentManagerDialog2 __lastInstance;
    int aid;
    AssignmentsContentPanel _assignmentsPanel;
    ComboBox<GroupDto> _groupCombo;
    BorderLayoutContainer _mainContainer;
    private int _groupIdToLoad;
    public AssignmentManagerDialog2(int groupIdToLoad, int aid) {
        __lastInstance = this;
        _groupIdToLoad = groupIdToLoad;
        this.aid = aid;
        
        GWindow window = new GWindow(false);
        window.setPixelSize(700, 480);
        window.setHeadingHtml("Assignments Manager");
        window.setMaximizable(true);

        _mainContainer = new BorderLayoutContainer();
        _mainContainer.setBorders(true);

        BorderLayoutData northData = new BorderLayoutData(30);
        northData.setMargins(new Margins(10));

        HorizontalLayoutContainer header = new HorizontalLayoutContainer();
        
        _groupCombo = createGroupNameCombo();
        
        header.add(new FieldLabel(_groupCombo, "Group"));
        HorizontalLayoutData hd = new HorizontalLayoutData();
        hd.setMargins(new Margins(0,0,0,20));
        header.add(new TextButton("Assignments Guide", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                AssignmentGuideWindow.showWindow();
            }
        }),hd);
        
        _mainContainer.setNorthWidget(header, northData);
        
        _assignmentsPanel = new AssignmentsContentPanel(new Callback() {
            @Override
            public void showAssignmentStatus(Assignment assignment) {
                new AssignmentStatusDialog(assignment);
            }
        });
        
        /** 
         * intially have the assignments window full
         */
        _mainContainer.setCenterWidget(_assignmentsPanel);
        
        window.addButton(createGradeBookButton());
        window.addCloseButton();
        
        window.setWidget(_mainContainer);
        window.show();
    }
    
    private void loadGroupInfo(GroupDto group) {
        Info.display("Group Loading", "Loading assignments for '" + group + "'");
        _assignmentsPanel.loadAssignentsFor(group);
    }
    
    
    
    interface ComboBoxTemplates extends XTemplates {
        @XTemplate("<div qtip=\"{info}\" qtitle=\"Group Info\">{name}</div>")
        SafeHtml group(String info, String name);
      }
    

    private ComboBox<GroupDto> createGroupNameCombo() {
        
        
        GroupNameProperties props = GWT.create(GroupNameProperties.class);
        ListStore<GroupDto> groupStore = new ListStore<GroupDto>(props.groupId());
   
        ComboBox<GroupDto> combo = new ComboBox<GroupDto>(groupStore, props.name(), new AbstractSafeHtmlRenderer<GroupDto>() {
            @Override
            public SafeHtml render(GroupDto object) {
                final ComboBoxTemplates comboBoxTemplates = GWT.create(ComboBoxTemplates.class);
                return comboBoxTemplates.group(object.getInfo(), object.getName());
            }
        });
    
        
        loadGroupNames();

        //combo.setToolTip("Select a group with [N students, M assignments]");
        combo.setWidth(200);
        combo.setTypeAhead(false);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.addSelectionHandler(new SelectionHandler<GroupDto>() {
            
            @Override
            public void onSelection(SelectionEvent<GroupDto> event) {
                loadGroupInfo(event.getSelectedItem());
            }
        });
        
        combo.setAllowBlank(false);
        combo.setEmptyText("--Select a group -");
        combo.setForceSelection(true);
        
        return combo;
    }

    private void loadGroupNames() {
        
        CatchupMathTools.setBusy(true);
        
        new RetryAction<CmList<GroupDto>>() {
            @Override
            public void attempt() {
                GetAssignmentGroupsAction action = new GetAssignmentGroupsAction(aid);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<GroupDto> groupInfos) {
                
                CatchupMathTools.setBusy(false);
                
                _groupCombo.getStore().clear();
                _groupCombo.getStore().addAll(groupInfos);
                
                boolean groupSelected=false;
                if(_groupIdToLoad > 0) {
                    for(GroupDto gd: _groupCombo.getStore().getAll()) {
                        if(gd.getGroupId() == _groupIdToLoad) {
                            _groupCombo.setValue(gd);
                            loadGroupInfo(gd);
                            groupSelected=true;
                            break;
                        }
                    }
                }
                
                if(!groupSelected) {
                    _groupCombo.expand();
                }
                
                _mainContainer.forceLayout();
            }
        }.register();                
    }
    

    private Widget createGradeBookButton() {
        TextButton gradeBook = new TextButton("Gradebook", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        CmMessageBox.showAlert("The Assignment Gradebook View");
                    }
                });                
            }});
        return gradeBook;
    }

    protected void refreshData() {
        _assignmentsPanel.refreshData();
    }
    
    static {
        CmRpc.EVENT_BUS.addHandler(DataBaseHasBeenUpdatedEvent.TYPE, new DataBaseHasBeenUpdatedHandler() {
            @Override
            public void databaseUpdated() {
                __lastInstance.refreshData();
            }
        });
    }

    
}

