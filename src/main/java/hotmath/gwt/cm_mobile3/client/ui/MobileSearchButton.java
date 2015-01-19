package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;

public class MobileSearchButton extends TouchAnchor {
    
    public MobileSearchButton() {
        addStyleName("mobile-search-button");
        getElement().setInnerHTML("<img src='/gwt-resources/images/search_mobile-enabled.png'/>");
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                History.newItem("search|" + System.currentTimeMillis());
            }
        });
    }
}
