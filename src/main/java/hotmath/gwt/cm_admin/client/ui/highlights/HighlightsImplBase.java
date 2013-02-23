package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

abstract class HighlightsImplBase  {
    String name;
    String toolTip;
    Widget _widget;
    
    HighlightsImplBase(String name, String toolTip) {
        this.name = name;
        this.toolTip = toolTip;
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
    
    
    /** return the column labels used
     *  to print the report.
     *  
     * @return
     */
    protected HighlightReportLayout getReportLayout() {
        String cols[] = getReportCols();
        HighlightReportLayout rl = new HighlightReportLayout(cols, getReportValues());
        rl.setCountLabel("Student Count: ");
        return rl;
    }
    
    abstract String[] getReportCols();
    abstract String[][] getReportValues();
}

