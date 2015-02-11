package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class MobileSearchButton extends TouchAnchor {
    
    private MobileSearchButtonControl searchControl;
    
    public MobileSearchButton(MobileSearchButtonControl searchControlIn) {
        this.searchControl = searchControlIn;
        addStyleName("mobile-search-button");
        getElement().setInnerHTML("<img src='/gwt-resources/images/search_mobile-enabled.png'/>");
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(!searchControl.isAllowed()) {
                    PopupMessageBox.showMessage("Search is not allowed at this time.");;
                }
                else {
                    searchControl.showSearchPanel();
                }
            }
        });
    }
}
