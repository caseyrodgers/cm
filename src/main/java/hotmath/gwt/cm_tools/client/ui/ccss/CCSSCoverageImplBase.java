package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

abstract class CCSSCoverageImplBase  {
    String name;
    String toolTip;
    Widget _widget;
    int _uid;
    int count;
    CallbackOnComplete _callback;
    
    CCSSCoverageImplBase(String name, String toolTip, int uid, CallbackOnComplete callback) {
        this.name = name;
        this.toolTip = toolTip;
        this._uid = uid;
        this._callback = callback;
    }
    
    public String getToolTip() {
        return toolTip;
    }
    
    public String getText() {
        return this.name + " (" + count + ")";
    }

    public void setCount(int count) {
    	this.count = count;
    }

    public void isComplete() {
    	if (_callback != null) _callback.isComplete();
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

