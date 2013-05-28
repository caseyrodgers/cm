package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.ui.Widget;

abstract class CCSSCoverageImplBase  {
    String name;
    String toolTip;
    Widget _widget;
    int userId;
    
    CCSSCoverageImplBase(String name, String toolTip, int userId) {
        this.name = name;
        this.toolTip = toolTip;
        this.userId = userId;
    }
    
    public String getToolTip() {
        return toolTip;
    }
    
    public String getText() {
        return this.name;
    }
    
    public Widget getWidget() {
        if(_widget == null)
            _widget = prepareWidget();
        return _widget;
    }
    
    /** one time call to draw the gui
     * 
     * @return
     */
    abstract Widget prepareWidget();
        
    abstract String[] getReportCols();
    abstract String[][] getReportValues();
}

