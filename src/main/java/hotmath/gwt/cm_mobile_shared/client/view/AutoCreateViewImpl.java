package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CreateAutoRegistrationAccountAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.util.List;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AutoCreateViewImpl extends Composite implements AutoCreateView {

    TextBox firstName, lastName, birthDate;
    private Presenter presenter;

    public AutoCreateViewImpl() {

        FlowPanel main = new FlowPanel();
        FlowPanel form = new FlowPanel();
        form.addStyleName("auto-create-view");

        VerticalPanel holder = new VerticalPanel();

        SubToolBar subBar = new SubToolBar(false);
        subBar.add(new SexyButton("Return To Home Page", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Location.replace("/index.html");
            }
        }));
        subBar.add(new SexyButton("Forgot Password", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showForgotMessage();
            }
        }));

        main.add(subBar);

        holder.add(new Label("First Name"));
        firstName = new TextBox();
        firstName.setName("firstName");
        holder.add(firstName);

        holder.add(new Label("Last Name"));
        lastName = new TextBox();
        lastName.setName("lastName");
        holder.add(lastName);

        holder.add(new Label("Birth date (mmdd)"));
        birthDate = new TextBox();
        birthDate.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                char kc = event.getCharCode();
                if (kc == '\r') {
                    doRegistration();
                }
            }
        });

        lastName.setName("birthDate");
        holder.add(birthDate);

        HorizontalPanel hor = new HorizontalPanel();
        hor.add(new SexyButton("Register", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doRegistration();
            }
        }));
        form.add(holder);
        form.add(new HTML("<div>&nbsp;</div>"));
        form.add(hor);

        main.add(form);
        initWidget(main);
    }

    private String isDateValid(String value) {
        if (value == null || value.length() == 0)
            return "The birth date field must be specified";
        else {
            if (value.length() != 4)
                return "The birth date field must be four digits, such as 0912";
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return "This birth date field must be only four numbers, such as 0703";
            }

            Integer mon = Integer.parseInt(value.substring(0, 2));
            if (mon < 0 || mon > 12)
                return "The month " + mon + " is not a valid month";

            Integer day = Integer.parseInt(value.substring(2, 4));
            if (day < 0 || day > 31)
                return "The day " + day + " is not a valid month day";
        }
        return null;
    }

    private boolean isFormValid() {
        if (firstName.getValue().length() == 0) {
            PopupMessageBox.showError("First Name must be specified");
            firstName.setFocus(true);
            return false;
        }
        if (lastName.getValue().length() == 0) {
            PopupMessageBox.showError("Last Name must be specified");
            lastName.setFocus(true);
            return false;
        }
        
        if((firstName.getValue() + lastName.getValue() + birthDate.getValue()).contains(" ")) {
            PopupMessageBox.showError("Spaces are not allowed");
            return false;
        }
        
        String msg = isDateValid(birthDate.getValue());
        if(msg != null) {
            PopupMessageBox.showError(msg);
            return false;
        }
        return true;
    }

    protected void doRegistration() {

        if (!isFormValid()) {
            return;
        }

        presenter.doRegistration(firstName.getValue(), lastName.getValue(), birthDate.getValue());
    }

    @Override
    public void setPresenter(Presenter presenter, CallbackOnComplete callbackOnComplete) {
        this.presenter = presenter;
        callbackOnComplete.isComplete();
    }

    @Override
    public String getViewTitle() {
        return "Self Registration";
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
    public BackAction getBackAction() {
        return null;
    }

    @Override
    public void isNowActive() {
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }

    private void showForgotMessage() {
        String html = "<p>If you have already registered, use your personal password, not the self-registration password "
                + "when you log in. Perhaps your password was something like Smith-Susie-0705.";

        PopupMessageBox.showMessage(html);
    }

}
