package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SolutionInfo implements Response,IsSerializable {

    String pid;
	String html;
	String js;
	SolutionContext context;
	boolean hasShowWork;
	
	SolutionWidgetResult widgetResult;
	
	
	   
    public SolutionInfo() {}
    public SolutionInfo(String pid, String html, String js, boolean hasShowWork) {
        this.pid = pid;
        this.html = html;
        this.js = js;
        this.hasShowWork = hasShowWork;
    }
    
	public SolutionContext getContext() {
        return context;
    }
	
    public void setContext(SolutionContext context) {
        this.context = context;
    }
    
	public String getHtml() {
		return html;
	}
	
	public void setHtml(String html) {
		this.html = html;
	}
	
	public String getJs() {
		return js;
	}
	
	public void setJs(String js) {
		this.js = js;
	}
	
	public boolean isHasShowWork() {
		return hasShowWork;
	}
	
	public void setHasShowWork(boolean hasShowWork) {
		this.hasShowWork = hasShowWork;
	}
	
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    
    public SolutionWidgetResult getWidgetResult() {
        return widgetResult;
    }
    
    public void setWidgetResult(SolutionWidgetResult widgetResult) {
        this.widgetResult = widgetResult;
    }
    @Override
    public String toString() {
        return "SolutionInfo [pid=" + pid + ", html=" + html + ", js=" + js + ", context=" + context + ", hasShowWork=" + hasShowWork + ", widgetResult="
                + widgetResult + "]";
    }
}
