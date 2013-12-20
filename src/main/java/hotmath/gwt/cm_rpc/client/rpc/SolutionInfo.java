package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.WhiteboardModel;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SolutionInfo implements Response,IsSerializable {

    String pid;
	String html;
	String js;
	SolutionContext context;
	boolean hasShowWork;
	
	SolutionWidgetResult widgetResult;
	List<WhiteboardModel> whiteboards = new ArrayList<WhiteboardModel>();
	
	   
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
    
    /** Return whiteboard array.  If no whiteboards 
     * found then return empty list
     * 
     * @return
     */
    public List<WhiteboardModel> getWhiteboards() {
        return whiteboards;
    }

    public void setWhiteboards(List<WhiteboardModel> whiteboards) {
        this.whiteboards = whiteboards;
    }
    @Override
    public String toString() {
        return "SolutionInfo [pid=" + pid + ", html=" + html + ", js=" + js + ", context=" + context + ", hasShowWork=" + hasShowWork + ", widgetResult="
                + widgetResult + "]";
    }
}
