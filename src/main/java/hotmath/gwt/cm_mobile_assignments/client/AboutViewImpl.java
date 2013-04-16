package hotmath.gwt.cm_mobile_assignments.client;

import hotmath.gwt.cm_mobile_assignments.client.util.AssData;
import hotmath.gwt.cm_mobile_assignments.client.view.AboutView;
import hotmath.gwt.cm_mobile_assignments.client.view.BaseComposite;

import com.google.gwt.user.client.ui.HTML;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;

public class AboutViewImpl extends BaseComposite implements AboutView {
    
    private CellList<Item> cellListWithHeader;
    ScrollPanel scrollPanel;

    public AboutViewImpl() {

        showAssignments();
        //initWidget(scrollPanel);
        
        initWidget(new HTML("User: " + AssData.getUserData()));
    }

    private void showAssignments() {
        cellListWithHeader = new CellList<Item>(new BasicCell<Item>() {
            @Override
            public String getDisplayString(Item model) {
                return model.getDisplayString();
            }

            @Override
            public boolean canBeSelected(Item model) {
                return true;
            }
        });
        cellListWithHeader.setRound(true);
        
        scrollPanel = new ScrollPanel();
        scrollPanel.setWidget(cellListWithHeader);
        scrollPanel.setScrollingEnabledX(false);
    }

}



class Item {
    private String displayString;

    public Item(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }
}