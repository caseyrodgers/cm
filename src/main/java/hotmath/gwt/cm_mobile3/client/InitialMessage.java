package hotmath.gwt.cm_mobile3.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class InitialMessage extends DialogBox {

    private static InitialMessageUiBinder uiBinder = GWT.create(InitialMessageUiBinder.class);

    interface InitialMessageUiBinder extends UiBinder<Widget, InitialMessage> {
    }

    public InitialMessage() {
        super(true);
        setSize("350px", "320px");
        setText("Welcome to Catchup Math Mobile!");
        setGlassEnabled(true);
        setWidget(uiBinder.createAndBindUi(this));

        setAnimationEnabled(true);
        setAutoHideEnabled(true);

        setVisible(true);

        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                setHasSeen();
            }
        });
    }

    public void showCentered() {
        center();
    }

    @UiHandler("closeButton")
    protected void onCloseButton(ClickEvent ce) {
        this.hide();
    }

    /**
     * return true if this user has seen the message
     * 
     * @return
     */
    static public boolean hasBeenSeen() {
        String im = Cookies.getCookie("initial_message");
        return (im != null);
    }

    public void setHasSeen() {
        Cookies.setCookie("initial_message", "true");
    }
}
