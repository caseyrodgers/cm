package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentListViewImpl extends Composite implements AssignmentListView {
    
    private Presenter presenter;
    
    GenericContainerTag listItems = new GenericContainerTag("ul");
    
    public AssignmentListViewImpl() {
        listItems.addStyleName("touch");
        listItems.addStyleName("large");
        DockPanel dockPanel = new DockPanel();
        
        SubToolBar subToolBar = new SubToolBar();
        subToolBar.add(new SexyButton("Refresh", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.readDataFromServer(AssignmentListViewImpl.this, true);
            }
        }));
        dockPanel.add(subToolBar, DockPanel.NORTH);
        
        FlowPanel sp = new FlowPanel();
        
        sp.getElement().setAttribute("style",  "margin: 10px");
        sp.add(listItems);
        
        dockPanel.add(sp, DockPanel.CENTER);
        initWidget(dockPanel);
        
        addStyleName("AssignmentListViewImpl");
    }

    private Widget createLedgend() {
        return MyGenericTextTag.getLedgend();
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
                History.newItem("welcome");
                return false;
            }
        };
    }

    @Override
    public void displayAssigmments(List<StudentAssignmentInfo> assignments) {
        listItems.clear();
        for(StudentAssignmentInfo bm: assignments) {
            
            GenericTextTag<String> tt = new MyGenericTextTag(bm);
            tt.addStyleName("group");
            
            tt.addHandler(new TouchClickHandler<String>() {
                @Override
                public void touchClick(TouchClickEvent<String> event) {
                    MyGenericTextTag tag = (MyGenericTextTag)event.getTarget();
                    presenter.showAssignment(tag.getStudentAssignmentInfo());
                }
            });
            listItems.add(tt);
        }
        
        getWidget().getElement().setAttribute("style","display:block");
    }

    @Override
    public String getViewTitle() {
        return "Your Assignments";
    }
}


class MyGenericTextTag extends GenericTextTag<String> {
    private StudentAssignmentInfo studentAssignmentInfo;

    public MyGenericTextTag(StudentAssignmentInfo stuInfo) {
        super("li");
        this.studentAssignmentInfo = stuInfo;
        String html=
                "<div style='color: gray;float: left;width: 225px;'>" + getStatusString() + "</div>" +
               "<div style='clear: both'>"  + stuInfo.getComments() + "</div>";
        setHtml(html);
    }

    private String getStatusString() {
        List<String> statuses = new ArrayList<String>();
        if(studentAssignmentInfo.isGraded()) {
            statuses.add("<span style='font-weight: bold;color: green'>score: " + studentAssignmentInfo.getScore() + "</span>");
        }
        else {
            if(studentAssignmentInfo.isOverdue()) {
                statuses.add("Overdue");
            }
            else {
                statuses.add(studentAssignmentInfo.getStatus().toLowerCase());
            }
            
            if(studentAssignmentInfo.getTurnInDate() == null) {
                statuses.add("due: " + studentAssignmentInfo.getDueDate());
            }
        }
        
        if(studentAssignmentInfo.getNumUnreadAnnotations() > 0) {
            statuses.add("unread note: " + studentAssignmentInfo.getNumUnreadAnnotations());
        }

        String info="";
        for(String status: statuses) {
            if(info.length() > 0) {
                info += ", ";
            }
            info += status;
        }
        
        if(!studentAssignmentInfo.isGraded() && studentAssignmentInfo.isOverdue()) {
            info = "<span style='color: red'>" + info + "</span>";
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
