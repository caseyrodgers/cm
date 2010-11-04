package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/** Provides input to users answer.  It controls
 * a set of three input boxes used in various ways
 * to allow a variety of input based on type of
 * question.
 * 
 * @author casey
 *
 */
public class AnswerInputPanel extends FlowPanel {
    
    interface MyUiBinder extends UiBinder<Widget, AnswerInputPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField AnswerTextBox numeratorField;
    @UiField AnswerTextBox denominatorField;
    @UiField HTMLPanel answerPanel;
    @UiField FlowPanel dividerPanel;
    
    interface Style extends CssResource {
        String remove();
        String show();
        String fractionSetup();
        String integerSetup();
      }
    @UiField Style style;
      
    public AnswerInputPanel() {
        add(uiBinder.createAndBindUi(this));
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventTypes.EVENT_SHOW_INPUT_FRACTION) {
                    setupFraction();
                }
                else if(event.getEventType() == EventTypes.EVENT_SHOW_INPUT_STRING_EQUATION) {
                    setupDecimal();
                }
                else if(event.getEventType() == EventTypes.EVENT_SHOW_INPUT_RESET) {
                    resetPanel();
                }
            }
        });
        
        denominatorField.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                /** check for backspace */
                if(getText().length() == 0 && event.getCharCode() == 8) {
                    ((TextBox) event.getSource()).cancelKey();
                    
                    setupDecimal();
                }
            }
          });
    }
    
    public void resetPanel() {
        setupDecimal();        
        numeratorField.setValue("?");
        
        new Timer() {
            
            @Override
            public void run() {
                numeratorField.selectAll();
                numeratorField.setFocus(true);
            }
        }.schedule(500);
    }
    
    public void setCallbackOnAnswer(AnswerCallback call) {
        
    }
    
    public String getText() {
        String value="";
        String num = numeratorField.getText();
        String den = denominatorField.getText();
        if(den.length() > 0) {
            value = num + "/" + den;
        }
        else {
            value = num;
        }
        return value;
    }
    
    public void setText(String x) {
        numeratorField.setText(x);
    }
    
    public void setFocus(boolean yesNo) {
        numeratorField.setFocus(yesNo);
    }
    
    public void selectAll() {
        numeratorField.selectAll();
    }
    
    public void setupFraction() {
        numeratorField.getElement().removeClassName(style.integerSetup());
        denominatorField.setValue("");
        denominatorField.getElement().addClassName(style.show());
        dividerPanel.getElement().addClassName(style.show());
        denominatorField.setFocus(true);
    }
    
    public void setupDecimal() {
        denominatorField.getElement().removeClassName(style.show());
        dividerPanel.getElement().removeClassName(style.show());
        
        numeratorField.getElement().addClassName(style.integerSetup());
        numeratorField.setFocus(true);
    }
    
    static public interface AnswerCallback {
        void doComplete(AnswerInputPanel o);
    }
}
