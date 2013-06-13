package hotmath.gwt.cm_mobile_shared.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.AutoCreateView;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.CheckUserAccountStatusAction;
import hotmath.gwt.cm_rpc.client.rpc.LogUserInAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CreateAutoRegistrationAccountAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class AutoCreateActivity implements AutoCreateView.Presenter{

    private int uid;

    public AutoCreateActivity(int uid) {
        this.uid = uid;
    }

    @Override
    public void doRegistration(String firstName, String lastName, String birthDate) {
        
        final String password = (lastName + "-" + firstName + "-" + birthDate).toLowerCase();

        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        
        CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction(uid, lastName + ", " + firstName.trim(), password);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData rdata) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));

                /**
                 * check for build-in error message
                 * 
                 * @TODO: move to specific object away from RpcData
                 * 
                 *        if error_message is non-null, then a programmatic
                 *        (local domain) error occurred. As opposed to a lower
                 *        level exception that would be caught by the generic
                 *        error handlers.
                 * 
                 */
                String errorMessage = rdata.getDataAsString("error_message");
                if (errorMessage != null && errorMessage.length() > 0) {
                    if (errorMessage.indexOf("passcode you entered") > -1) {
                        showAlreadyMsg(password);
                    } else if (errorMessage.indexOf("name you entered") > -1) {
                        checkIfPasswordMatches(password);
                    }
                } else {
                    String key = rdata.getDataAsString("key");
                    showPasswordAssignment(password, key,rdata.getDataAsInt("uid"), rdata.getDataAsString("userName"));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Log.error(caught.getMessage(), caught);
                String msg = caught.getMessage();
                PopupMessageBox.showError("There was a problem creating your new account: " + msg);
            }
        });        
        
    }


    private void showAlreadyMsg(final String password) {
        String msg = "<p style='padding: 10px'>You are already registered with this name.</p>";
        FlowPanel messagePanel = new FlowPanel();
        messagePanel.add(new HTML(msg));
        messagePanel.add(new SexyButton("Get Login Info", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
                LogUserInAction action = new LogUserInAction(null, password,"");
                CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
                    public void onSuccess(RpcData result) {
                        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                        String key = result.getDataAsString("key");
                        int uid = result.getDataAsInt("uid");
                        showPasswordAssignment(password, key, uid,result.getDataAsString("userName"));
                    }

                    public void onFailure(Throwable caught) {
                        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                        Log.error("Error getting login info", caught);
                        PopupMessageBox.showError("There was a problem getting existing login information: " + caught.getMessage());
                    }
                });
            }
        }));
        
        PopupMessageBox.showMessage("Already Registered", messagePanel, new CallbackOnComplete() {
            @Override
            public void isComplete() {
            }
        });
        
    }

    private void checkIfPasswordMatches(final String password) {
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        CheckUserAccountStatusAction action = new CheckUserAccountStatusAction(password);
        CatchupMathMobileShared.getCmService().execute(action,  new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                String msg = result.getDataAsString("message");
                if (msg.indexOf("duplicate") > -1) {
                    showAlreadyMsg(password);
                } else {
                    msg = "There is another registration with that name, so please add your middle name to the first-name box (e.g., Jim Bob).";
                    // this means the password including the date portion is
                    // unique
                    PopupMessageBox.showError(msg);
                }
            }
            public void onFailure(Throwable caught) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Log.error("Error checking if password matches", caught);
            }
        });
    }

    private void showPasswordAssignment(String password, final String key, int uid, String loginName) {

        String html = "<div style='margin: 10px;'>" + "<p>Your personal password is: <br/><b>" + password + "</b></p>"
                + "<p style='margin-top: 10px;'>In the future, your Login Name will be <b>" + loginName
                + "</b> along with the above password, so please write them both down!</p>" + "</div>";

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new HTML(html));

        SexyButton close = new SexyButton("Begin Catchup Math", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String userKey = key;
                String url = "http://" + Window.Location.getHost();

                url += "/loginService?key=" + userKey;
                Window.Location.replace(url);
            }
        });
        flowPanel.add(close);
        
        PopupPanel messageBox = PopupMessageBox.showMessage("Your Personal Password",flowPanel,  new CallbackOnComplete() {
            @Override
            public void isComplete() {
            }
        });
        messageBox.setAutoHideEnabled(false);
    }

}
