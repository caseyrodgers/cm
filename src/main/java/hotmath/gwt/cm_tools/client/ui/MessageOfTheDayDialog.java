package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetMessageOfTheDayAction;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.CmShared;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class MessageOfTheDayDialog extends GWindow {

    interface MyUiBinder extends UiBinder<Widget, MessageOfTheDayDialog> { }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField HTMLPanel mainPanel;
    
    CallbackGeneric callback;
    public MessageOfTheDayDialog(CallbackGeneric callbackIn) {
        super(false);
        
        this.callback = callbackIn;

        setHeadingText("Catchup Math Teacher Alert");
        setPixelSize(450,370);
        setModal(true);

        setWidget(uiBinder.createAndBindUi(this));

        TextButton okBtn = new TextButton("OK");
        okBtn.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
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
