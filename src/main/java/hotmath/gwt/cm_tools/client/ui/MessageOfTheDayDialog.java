package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetMessageOfTheDayAction;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.CmShared;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MessageOfTheDayDialog extends GWindow {

    interface MyUiBinder extends UiBinder<Widget, MessageOfTheDayDialog> { }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField HTMLPanel mainPanel;
    
    CallbackGeneric callback;
    public MessageOfTheDayDialog(CallbackGeneric callbackIn) {
        super(false);
        
        this.callback = callbackIn;

        setHeadingText("Catchup Math Teacher Alert");
        setPixelSize(450,270);
        setModal(true);

        setWidget(uiBinder.createAndBindUi(this));

        Button okBtn = new Button("OK");
        okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
                callback.callbackReady();
            }
        });
        addButton(okBtn);
        
        CmShared.getCmService().execute(new GetMessageOfTheDayAction(), new AsyncCallback<StringHolder>() {
            
            public void onSuccess(StringHolder result) {
                if(result.getResponse() == null || result.getResponse().length() == 0) {
                    removeFromParent();
                }
                else {
                    mainPanel.getElement().setInnerHTML(result.getResponse());
                    setVisible(true);
                }
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error getting message of the day", caught);
            }
        });

    }
}
