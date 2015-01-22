package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceWebLinkView;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class PrescriptionLessonResourceWebLinkViewImpl extends Composite implements PrescriptionLessonResourceWebLinkView {

    private Presenter presenter;
    SimplePanel _main = new SimplePanel();
    public PrescriptionLessonResourceWebLinkViewImpl() {
        initWidget(_main);
    }
    
    @Override
    public String getViewTitle() {
        return "Web Link View: " + presenter.getWebLinkName();
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
        _main.setWidget(new HTML(""));
        return null;
    }
    
    
    @Override
    public void isNowActive() {
    }
    
    
    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }
    
    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
        Frame frame = new Frame(p.getWebLinkUrl());
        _main.getElement().setAttribute("style",  "position: relative");
        frame.getElement().setAttribute("style", "position:static; top:0px; left:0px; bottom:0px; right:0px; width:100%; height:100%; border:none; margin:0; padding:0; overflow:hidden; z-index:999999;");
        _main.setWidget(frame);
    }

    @Override
    public String getHeaderBackground() {
        return null;
    }

}
