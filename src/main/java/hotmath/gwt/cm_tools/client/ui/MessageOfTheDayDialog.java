package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetMessageOfTheDayAction;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.CmShared;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MessageOfTheDayDialog extends Window {

    interface MyUiBinder extends UiBinder<Widget, MessageOfTheDayDialog> { }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField HTMLPanel mainPanel;
    
    CallbackGeneric callback;
    public MessageOfTheDayDialog(CallbackGeneric callbackIn) {
        this.callback = callbackIn;

        setHeading("Catchup Math Teacher Alert");
        setSize(450,270);
        setModal(true);

        add(uiBinder.createAndBindUi(this));

        Button okBtn = new Button("OK");
        okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
                callback.callbackReady();
            }
        });
        addButton(okBtn);
        setVisible(true);
        
        
        CmShared.getCmService().execute(new GetMessageOfTheDayAction(), new AsyncCallback<StringHolder>() {
            
            public void onSuccess(StringHolder result) {
                mainPanel.getElement().setInnerHTML(result.getResponse());
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error getting message of the day", caught);
            }
        });

    }
}
