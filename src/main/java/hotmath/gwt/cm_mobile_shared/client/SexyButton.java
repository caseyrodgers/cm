package hotmath.gwt.cm_mobile_shared.client;

import com.google.gwt.user.client.ui.Button;

public class SexyButton extends Button {
    
    public SexyButton(String name) {
        this(name, null);
    }
    public SexyButton(String name, String buttonType) {
        String type="";
        if(buttonType != null) {
            type = " class='" + buttonType + "'";
        }
        getElement().setInnerHTML("<span " + type + ">" + name + "</sexy>");
        addStyleName("sexybutton");
        addStyleName("sexysimple");
    }

}
