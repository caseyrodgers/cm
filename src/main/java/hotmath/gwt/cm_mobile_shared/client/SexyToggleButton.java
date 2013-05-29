package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchToggleButton;

import com.google.gwt.event.dom.client.ClickHandler;

public class SexyToggleButton extends TouchToggleButton {
    
    public SexyToggleButton(String name) {
        this(name, null);
    }
    
    public SexyToggleButton(String name, ClickHandler clickHandler) {
        this(name, clickHandler,null);
    }
    
    public SexyToggleButton(String name, ClickHandler clickHandler,String buttonType) {
         setText(name);
//        String type="";
//        if(buttonType != null) {
//            type = " class='" + buttonType + "'";
//        }
//        getElement().setInnerHTML("<span " + type + "><span>" + name + "</span>");
          addStyleName("sexyButton");
        
        if(clickHandler != null) {
            addClickHandler(clickHandler);
        }
    }

}
