package hotmath.gwt.cm_mobile_shared.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class SexyButton extends Button {
    
    public SexyButton(String name) {
        this(name, null);
    }
    
    public SexyButton(String name, ClickHandler clickHandler) {
        this(name, clickHandler,null);
    }
    
    public SexyButton(String name, ClickHandler clickHandler,String buttonType) {
        String type="";
        if(buttonType != null) {
            type = " class='" + buttonType + "'";
        }
        getElement().setInnerHTML("<span " + type + "><span>" + name + "</span>");
        addStyleName("sexyButton");
        
        if(clickHandler != null) {
            addClickHandler(clickHandler);
        }
    }

}
