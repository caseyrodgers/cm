package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
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
    public String getTitle() {
        return "Welcome To Catchup Math Mobile!";
    }

    @Override
    public void prepareView(String firstThingDescription) {
        firstThing.setInnerHTML(firstThingDescription);
    }
    
    @UiHandler("begin")
    protected void handleBegin(ClickEvent e) {
        presenter.beginCatchupMath();
    }
    
    
    @UiField
    DivElement firstThing;
    
    @UiField
    Button begin;
}
