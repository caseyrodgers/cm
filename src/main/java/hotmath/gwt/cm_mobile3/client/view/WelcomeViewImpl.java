package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class WelcomeViewImpl extends AbstractPagePanel implements WelcomeView {

    Presenter presenter;
    
    
    private static WelcomeViewImplUiBinder uiBinder = GWT.create(WelcomeViewImplUiBinder.class);

    interface WelcomeViewImplUiBinder extends UiBinder<Widget, WelcomeViewImpl> {
    }

    public WelcomeViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public String getBackButtonText() {
        return null;
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
    public String getViewTitle() {
        return "Welcome To Catchup Math, " + SharedData.getUserInfo().getUserName();
    }

    @Override
    public void prepareView(String firstThingDescription, String status) {
        firstThing.setInnerHTML(firstThingDescription);
        programStatus.setInnerHTML(status);
        
        AssignmentUserInfo ad = SharedData.getMobileUser().getAssignmentInfo();
        if(!ad.isAdminUsingAssignments() || 
                (ad.getActiveAssignments() + ad.getClosedAssignments() == 0)) {
            beginAssignments.setVisible(false);
        }
        
    }
    
    @UiHandler("begin")
    protected void handleBegin(ClickEvent e) {
        presenter.beginCatchupMath();
    }
    
    @UiHandler("beginAssignments")
    protected void handleBegineAssignments(ClickEvent e) {
        presenter.beingCatchupMathAssignments();
    }
    
    
    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }
    
    
    @UiField
    DivElement firstThing,programStatus;
    
    @UiField
    TouchButton begin, beginAssignments;
}
