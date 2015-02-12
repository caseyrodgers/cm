package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;

import com.google.gwt.user.client.ui.FlowPanel;

public class SubToolBar extends FlowPanel {
    private TouchButton _yourProgram;

    public SubToolBar() {
        super();
        addStyleName("SubToolBar");
        
/**
        _searchButton = new SexyButton("Search For Lesson", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CatchupMathMobile3._searchControl.showSearchPanel();
            }
        });
*/        
    }
    
    SexyButton _searchButton;
    public void setupViewForSearch() {
        if(true) {
            return;
        }
        
//        if(CatchupMathMobile3._searchControl.isAllowed()) {
//            if(getWidgetIndex(_searchButton) == -1) {
//                add(_searchButton);
//            }
//        }
//        else {
//            remove(_searchButton);
//        }
    }

    public void showReturnTo(boolean b) {
        if(_yourProgram != null) {
            _yourProgram.setVisible(b);
        }
    }
}