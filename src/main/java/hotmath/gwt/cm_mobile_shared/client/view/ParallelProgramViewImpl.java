package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class ParallelProgramViewImpl extends AbstractIPageComposite  implements ParallelProgramView {

    private Presenter presenter;

    TextBox passwordField;
    
    public ParallelProgramViewImpl() {

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

        holder.add(new Label("Password"));
        passwordField = new TextBox();
        holder.add(passwordField);
        passwordField.setName("password");
        passwordField.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                char kc = event.getCharCode();
                if (kc == '\r') {
                    doLogin();
                }
            }
        });

        HorizontalPanel hor = new HorizontalPanel();
        hor.add(new SexyButton("Login", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doLogin();
            }
        }));
        form.add(holder);
        form.add(new HTML("<div>&nbsp;</div>"));
        form.add(hor);

        main.add(form);
        initWidget(main);
    }
    
    protected void doLogin() {
        String msg = formIsValid();
        if(msg != null) {
            PopupMessageBox.showError(msg);
            return;
        }
        
        presenter.doLogin(this, passwordField.getValue());
    }
    
    
    @Override
    public void showParallelProgNotAvail(String password) {
        String html = "<p>The selected Parallel Program, " + UserInfo.getInstance().getLoginName()
                + ", is not available to the password you entered, " + password
                + ". Perhaps your password is something like Smith-John-0304.";

        PopupMessageBox.showError(html);
    }

    
    /** return error message or null if valid
     * 
     * @return
     */
    private String formIsValid() {
        String v = passwordField.getValue();
       if(v == null || v.length() == 0) {
           return "Password must be specified";
       }
       
       for(int i=0,t=v.length();i<t;i++) {
           char c = v.charAt(i);
           if(c == ' ' || c == '\t') {
               return "No spaces allowed";
           }
       }
       
       return null;
    }

    protected void showForgotMessage() {
        String html = "<p>Perhaps your password is something like Smith-John-0304.</p>";
        PopupMessageBox.showMessage(html);
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }

    @Override
    public String getViewTitle() {
        return "Parallel Program Login";
    }


    @Override
    public void setPresenter(Presenter presenter, CallbackOnComplete callbackOnComplete) {
        this.presenter = presenter;
        callbackOnComplete.isComplete();
    }
}