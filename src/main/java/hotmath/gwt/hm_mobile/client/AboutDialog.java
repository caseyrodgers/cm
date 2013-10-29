package hotmath.gwt.hm_mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class AboutDialog extends DialogBox {

    private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

    interface AboutDialogUiBinder extends UiBinder<Widget, AboutDialog> {
    }

    @UiField
    Element loggedInAs, expires, solutionsViewed;

    @UiField
    Element wrapperLoggedIn, wrapperNotLoggedIn, accountInfo;

    public AboutDialog() {
        super(true);
//        setSize("400px", "250px");
//        setText("Math Homework Help");
//        setGlassEnabled(true);
//
//        setWidget(uiBinder.createAndBindUi(this));
//
//        HmMobileLoginInfo li = HmMobile.__instance.getLoginInfo();
//        String userName = li != null ? li.getUser() : null;
//        if (userName != null) {
//            
//            if(!li.isDemoAccount()) {
//                String dateExpired = null;
//                try {
//                    dateExpired = HmMobilePersistedPropertiesManager._expiredDateFormat.format(li.getDateExpired());
//                } catch (Exception e) {
//                    Log.error("Could not format date expired", e);
//                }
//                expires.setInnerHTML(dateExpired);
//                solutionsViewed.setInnerHTML(li.getSolutionCount() + (li.getSolutionCount()==1?" solution":" solutions"));
//            }
//            else {
//                accountInfo.setAttribute("style",  "display: none");
//            }
//            
//            loggedInAs.setInnerHTML(userName);
//            showLoggedInPanel(true);
//        } else {
//            loggedInAs.setInnerHTML("You are not logged in");
//            showLoggedInPanel(false);
//        }
//
//        setAnimationEnabled(true);
//        setAutoHideEnabled(true);

        setVisible(true);
    }
    

    private void showLoggedInPanel(boolean b) {
        if(b) {
            wrapperLoggedIn.setAttribute("style", "display: block");
            wrapperNotLoggedIn.setAttribute("style", "display: none");
        }
        else {
            wrapperLoggedIn.setAttribute("style", "display: none");
            wrapperNotLoggedIn.setAttribute("style", "display: block");
        }
    }

    public void showCentered() {
        center();
    }

    @UiHandler("closeButton")
    protected void onCloseButton(ClickEvent ce) {
        this.hide();
    }
}
