package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;

import com.google.gwt.event.dom.client.ClickHandler;

public class SexyButton extends TouchButton {
    
    public SexyButton() {
        addStyleName("sexyButton");
    }
    
    
    public SexyButton(String name) {
        this(name, null);
    }
    
    public SexyButton(String name, ClickHandler clickHandler) {
        this(name, clickHandler,null);
    }
    
    public SexyButton(String name, ClickHandler clickHandler,String buttonType) {
        this();
        setButtonText(name, buttonType);
        
        if(clickHandler != null) {
            addClickHandler(clickHandler);
        }
    }

    public void setButtonText(String name, String buttonType) {
        String type="";
        if(buttonType != null) {
            type = " class='" + buttonType + "'";
        }
        getElement().setInnerHTML("<span " + type + "><span>" + name + "</span>");
    }

}
