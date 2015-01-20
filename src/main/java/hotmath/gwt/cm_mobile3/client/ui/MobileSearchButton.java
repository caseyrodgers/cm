package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;

public class MobileSearchButton extends TouchAnchor {
    
    boolean disallowSearch;
    
    public MobileSearchButton() {
        addStyleName("mobile-search-button");
        getElement().setInnerHTML("<img src='/gwt-resources/images/search_mobile-enabled.png'/>");
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                
                if(disallowSearch) {
                    PopupMessageBox.showError("Search is not allowed at this time.");;
                }
                else {
                    History.newItem("search|" + System.currentTimeMillis());
                }
            }
        });
    }

    public void setAllowSearch(boolean yesNo) {
        this.disallowSearch = !yesNo;
    }
}
