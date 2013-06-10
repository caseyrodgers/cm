package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentListViewImpl extends Composite implements AssignmentListView {
    
    private Presenter presenter;
    
    GenericList listItems = new GenericList();
    Label countLabel;

    private List<StudentAssignmentInfo> _lastAssignments;
    public AssignmentListViewImpl() {
        DockPanel dockPanel = new DockPanel();
        
        SubToolBar subToolBar = new SubToolBar(true);
        subToolBar.add(new SexyButton("Check For Changes", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.readDataFromServer(AssignmentListViewImpl.this, true);
            }
        }));
        dockPanel.add(subToolBar, DockPanel.NORTH);
       
        dockPanel.add(listItems, DockPanel.CENTER);
        initWidget(dockPanel);
        
        addStyleName("AssignmentListViewImpl");
    }


    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        
        getWidget().getElement().setAttribute("style","display:none");
        
        this.presenter.readDataFromServer(this, false);
    }

    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                CmRpcCore.EVENT_BUS.fireEvent(new HandleNextFlowEvent(SharedData.getMobileUser().getFlowAction()));
                return false;
            }
        };
    }

    @Override
    public void displayAssigmments(List<StudentAssignmentInfo> assignments) {
        _lastAssignments = assignments;
        listItems.getList().clear();
        for(StudentAssignmentInfo bm: assignments) {
            
//            if(bm.getStatus().equals("Closed")) {
//                continue;
//            }
            
            GenericTextTag<String> tt = new MyGenericTextTag(bm);
            tt.addStyleName("group");
            
            tt.addHandler(new TouchClickHandler<String>() {
                @Override
                public void touchClick(TouchClickEvent<String> event) {
                    MyGenericTextTag tag = (MyGenericTextTag)event.getTarget();
                    presenter.showAssignment(tag.getStudentAssignmentInfo());
                }
            });
            listItems.getList().add(tt);
        }
        
        listItems.updateCount();
        
        getWidget().getElement().setAttribute("style","display:block");
    }

    @Override
    public String getViewTitle() {
        return "Your Assignments";
    }


    @Override
    public void isNowActive() {
    }


    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.ASSIGNMENT;
    }
}


class MyGenericTextTag extends GenericTextTag<String> {
    private StudentAssignmentInfo studentAssignmentInfo;

    public MyGenericTextTag(StudentAssignmentInfo stuInfo) {
        super("li");
        this.studentAssignmentInfo = stuInfo;
        
        String statusLine = getStatusString();
        statusLine += "<div style='float: right;margin-right: 25px;'>" + DateUtils4Gwt.getPrettyDateString(stuInfo.getDueDate()) + "</div>";
        String html= 
               "<div>" + statusLine + "</div>" +
               "<div style='clear: both'>"  + stuInfo.getComments() + "</div>";
        
        if(stuInfo.isChanged()) {
            html = "<span style='color: red'>" + html + "</span>";
        }
        setHtml(html);
    }

    
    
    
    private String getStatusString() {
        List<String> statuses = new ArrayList<String>();
        if(studentAssignmentInfo.isGraded()) {
            statuses.add("Graded, Score: " + studentAssignmentInfo.getScore());
        }
        else {
            if(studentAssignmentInfo.getTurnInDate() != null) {
                statuses.add("Turned In");
            }
            else if(studentAssignmentInfo.isOverdue()) {
                statuses.add("Past Due");
            }
            else {
                statuses.add(studentAssignmentInfo.getStatus());
            }
        }
        
        if(studentAssignmentInfo.getNumUnreadAnnotations() > 0) {
            statuses.add("Unread Note: " + studentAssignmentInfo.getNumUnreadAnnotations());
        }

        String info="";
        for(String status: statuses) {
            if(info.length() > 0) {
                info += ", ";
            }
            info += status;
        }
        
        
        return info;
    }

    public StudentAssignmentInfo getStudentAssignmentInfo() {
        return studentAssignmentInfo;
    }
    
    public static Widget getLedgend() {
        String html = "<div style='margin-bottom: 5px;font-size: .8em'>Legend: *=Open; $=Turned in; +=teacher note; G=graded</div>";
        return new HTML(html);
    }
}
