package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SolutionInfo implements Response,IsSerializable {

	String html;
	String js;
	List<SolutionContext> contextVariablesJson;
	boolean hasShowWork;
	
	   
    public SolutionInfo() {}
    public SolutionInfo(String html, String js, boolean hasShowWork) {
        this.html = html;
        this.js = js;
        this.hasShowWork = hasShowWork;
    }
    
	public List<SolutionContext> getContextVariablesJson() {
        return contextVariablesJson;
    }
	
    public void setContextVariablesJson(List<SolutionContext> contextVariablesJson) {
        this.contextVariablesJson = contextVariablesJson;
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
    @Override
    public String toString() {
        return "SolutionInfo [html=" + html.length() + ", js=" + js.length() + ", contextVariablesJson=" + contextVariablesJson.size()
                + ", hasShowWork=" + hasShowWork + "]";
    }
}
