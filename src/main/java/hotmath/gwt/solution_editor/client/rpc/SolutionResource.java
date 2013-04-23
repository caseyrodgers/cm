package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class SolutionResource implements Response {
    String file;
    String urlPath;
    String contents;
    String display;
    
    public SolutionResource() {}
    public SolutionResource(String file, String urlPath) {
        this.file = file;
        this.urlPath = urlPath;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public String getUrlPath() {
        return urlPath;
    }
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
    public String getDisplay() {
        return display;
    }
    public void setDisplay(String display) {
        this.display = display;
    }
    
}
