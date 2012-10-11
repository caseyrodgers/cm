package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentsContentPanel;
import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_admin.client.ui.assignment.GroupNameProperties;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGroupsAction;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
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

    int aid;
    AssignmentsContentPanel _assignmentsPanel;
    ComboBox<GroupDto> _groupCombo;
    public AssignmentManagerDialog2(int aid) {
        this.aid = aid;
        
        Window window = new GWindow(true);
        window.setPixelSize(950,500);
        window.setHeadingHtml("Assignment Manager");
        window.setMaximizable(true);

        final BorderLayoutContainer con = new BorderLayoutContainer();
        con.setBorders(true);

        BorderLayoutData northData = new BorderLayoutData(30);
        northData.setMargins(new Margins(10));

        HorizontalLayoutContainer header = new HorizontalLayoutContainer();
        
        _groupCombo = createGroupNameCombo();
        
        header.add(new FieldLabel(_groupCombo, "Group Name"));
        String msg = "<p style='margin-left: 20px;font-size: .9em;'>NOTE: Assignments are linked to groups.  Each student in a group will be assigned all the assignments defined for that group.</p>";
        header.add(new HTML(msg));
        
        con.setNorthWidget(header, northData);
        
        GradeBookPanel gbPanel = new GradeBookPanel();
        _assignmentsPanel = new AssignmentsContentPanel(gbPanel);
        
        BorderLayoutData westData = new BorderLayoutData();
        westData.setSize(440);
        westData.setCollapsible(true);
        westData.setFloatable(true);
        con.setWestWidget(_assignmentsPanel,  westData);
        
        BorderLayoutData eastData = new BorderLayoutData();
        eastData.setSize(490);
        

        con.setCenterWidget(gbPanel, eastData);
        
        window.setWidget(con);
        
        window.show();
    }
    
    private void loadGroupInfo(GroupDto group) {
        Info.display("Group Loading", "Loading assignments for '" + group + "'");
        _assignmentsPanel.loadAssignentsFor(group);
    }
    
    private ComboBox<GroupDto> createGroupNameCombo() {
        
        GroupNameProperties props = GWT.create(GroupNameProperties.class);
        ListStore<GroupDto> groupStore = new ListStore<GroupDto>(props.groupId());
   
        ComboBox<GroupDto> combo = new ComboBox<GroupDto>(groupStore, props.name());
        loadGroupNames();

        combo.setToolTip("Select a group in which Assignments are associated with. [students=N, assignments=N]");
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
        combo.setEmptyText("--Select Associated Group--");
        combo.setForceSelection(true);
        
        return combo;
    }

    private void loadGroupNames() {
        new RetryAction<CmList<GroupDto>>() {
            @Override
            public void attempt() {
                GetAssignmentGroupsAction action = new GetAssignmentGroupsAction(aid);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<GroupDto> groupInfos) {
                _groupCombo.getStore().addAll(groupInfos);
            }
        }.register();                
    }
}

