package hotmath.gwt.cm_mobile_shared.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class UnOrderedList extends ComplexPanel { 
    public UnOrderedList() { 
            setElement(DOM.createElement("UL")); 
    } 
    public void add(Widget w) { 
            super.add(w, getElement()); 
    } 
    public void insert(Widget w, int beforeIndex) { 
            super.insert(w, getElement(), beforeIndex, true); 
    } 
} 

