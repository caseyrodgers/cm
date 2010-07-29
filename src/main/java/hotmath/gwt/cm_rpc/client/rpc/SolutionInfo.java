package hotmath.gwt.cm_rpc.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SolutionInfo implements Response,IsSerializable {

	String html;
	String js;
	boolean hasShowWork;
	
	public SolutionInfo() {}
	public SolutionInfo(String html, String js, boolean hasShowWork) {
		this.html = html;
		this.js = js;
		this.hasShowWork = hasShowWork;
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
}
