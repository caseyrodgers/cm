package hotmath.gwt.cm_admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentsContentPanel;
import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentsContentPanel.Callback;
import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentsContentPanel.GradebookButtonCallback;
import hotmath.gwt.cm_admin.client.ui.assignment.GroupNameProperties;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGroupsAction;
import hotmath.gwt.cm_rpc.client.rpc.PrintGradebookAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyMenuItem;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.VideoPlayerWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
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
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * Provide dialog to allow Admins ability to define and manage Assignments.
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
    private TextButton _gradeBookButton;

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
        _gradeBookButton = createGradeBookButton();
        header.add(_gradeBookButton, hd);

        header.add(new TextButton("Assignments Guide", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                AssignmentGuideWindow.showWindow();
            }
        }), hd);
        
        header.add(createWebinarButton(), hd);

        _mainContainer.setNorthWidget(header, northData);
        
        _assignmentsPanel = new AssignmentsContentPanel(new Callback() {
                @Override
                public void showAssignmentStatus(Assignment assignment) {
                    new AssignmentStatusDialog(assignment);
                }
            },
            new GradebookButtonCallback() {
            	@Override
        	    public void enable(boolean enable) {
        		    if (enable == true) _gradeBookButton.enable();
        		    else _gradeBookButton.disable();
        	    }
            });
        
        /** 
         * initially have the assignments window full
         */
        _mainContainer.setCenterWidget(_assignmentsPanel);
        
        window.addCloseButton();
        
        window.setWidget(_mainContainer);
        window.show();
    }

    GroupDto _lastGroup;

    private void loadGroupInfo(GroupDto group) {
        _lastGroup = group;
        Log.debug("Group Loading", "Loading assignments for '" + group + "'");
        _assignmentsPanel.loadAssignmentsFor(group);
    }
    
    interface ComboBoxTemplates extends XTemplates {
        @XTemplate("<div qtip=\"{info}\">{name}</div>")
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
        combo.setEmptyText("-- Select a group --");
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
                CmRpcCore.getCmService().execute(action, this);
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
                _mainContainer.forceLayout();
            }
        }.register();                
    }

    private TextButton createGradeBookButton() {
        TextButton gradeBookBtn = new TextButton("Gradebook", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(_lastGroup == null) {
                    CmMessageBox.showAlert("Select a group first.");
                    return;
                }
                PrintGradebookAction action = new  PrintGradebookAction(UserInfoBase.getInstance().getUid(),_lastGroup.getGroupId());
                DateRangePanel dateRange = DateRangePanel.getInstance();
                if (dateRange != null) {
                    action.setFromDate(dateRange.getFromDate());
                    action.setToDate(dateRange.getToDate());
                }
                new PdfWindow(action.getAdminId(), "Grade Book Report", action);
            }});
        gradeBookBtn.disable();
        gradeBookBtn.setToolTip("View summary of scores within Date Range");
        return gradeBookBtn;
    }

    private TextButton createWebinarButton() {
    	TextButton btn = new TextButton("Assignments Videos");
        btn.setToolTip("View Assignments videos");

        Menu menu = new Menu();

        for (final TrainingVideo video : videoList) {
        	
            menu.add(new MyMenuItem(video.title, "View the " + video.title + " video", new SelectionHandler<MenuItem>() {
                @Override
                public void onSelection(SelectionEvent<MenuItem> event) {
                	new VideoPlayerWindow(video.title, video.uri);
                }
            }));

        }

        btn.setMenu(menu);
        return btn;
    }

    private native void showAssignmentsWebinarPage() /*-{
        var aw = window.open('/webinar_assignments');
        aw.focus();
    }-*/;
    
    private native void showAssignmentsUpdateWebinarPage() /*-{
        var aw = window.open('/webinar_assignments/updates.html');
        aw.focus();
    }-*/;

    protected void refreshData() {
        _assignmentsPanel.refreshData();
    }
    
    static {
        CmRpcCore.EVENT_BUS.addHandler(DataBaseHasBeenUpdatedEvent.TYPE, new DataBaseHasBeenUpdatedHandler() {
            @Override
            public void databaseUpdated(TypeOfUpdate type) {
                if(type == TypeOfUpdate.ASSIGNMENTS) {
                    __lastInstance.refreshData();
                }
            }
        });


    }

    public static void startTest() {
        new AssignmentManagerDialog2(151718, 2);
    }

    static List<TrainingVideo> videoList = new ArrayList<TrainingVideo>();
    
    static {
    	videoList.add(0, new TrainingVideo("assignments-overview", "Overview",
                                           "assets/teacher_videos/v2/Assignments-Overview-090714-FINAL.mp4"));

    	videoList.add(1, new TrainingVideo("assignments-creating", "How to Create an Assignment",
                                           "assets/teacher_videos/v2/Assignments-Creating-090814.mp4"));

    	videoList.add(2, new TrainingVideo("assignments-custom", "Teacher-created Problems",
                                           "assets/teacher_videos/v2/Assignments-CPs-9-8-14.mp4"));

    	videoList.add(3, new TrainingVideo("assignments-course-tests", "Course Tests",
                                           "assets/teacher_videos/v2/Assignments-CourseTests-FInal.mp4"));

    	videoList.add(4, new TrainingVideo("assignments-specific-students", "Assignments for Specific Students",
                                           "assets/teacher_videos/mp4/assignments-specific-students.mp4"));

    	videoList.add(5, new TrainingVideo("assignments-reporting-options", "Reporting Options",
                                           "assets/teacher_videos/mp4/assignments-reporting-options.mp4"));

    	videoList.add(6, new TrainingVideo("assignments-manual-scoring", "Manual Scoring of Whiteboard Problems",
                                           "assets/teacher_videos/mp4/assignments-manual-scoring.mp4"));

    	videoList.add(7, new TrainingVideo("assignments-student-interface", "Student Interface",
                                           "assets/teacher_videos/mp4/assignments-student-interface.mp4"));
    }

    static class TrainingVideo {
    	String key;
    	String title;
    	String uri;
    	
    	TrainingVideo(String key, String title, String uri) {
    		this.key = key;
    		this.title = title;
    		this.uri = uri;
    	}
    }
    
}

