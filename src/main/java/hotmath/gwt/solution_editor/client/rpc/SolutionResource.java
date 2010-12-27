package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class SolutionResource implements Response {
    String file;
    String urlPath;
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
}
