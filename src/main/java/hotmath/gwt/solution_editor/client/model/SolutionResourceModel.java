package hotmath.gwt.solution_editor.client.model;

import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

public class SolutionResourceModel  {
    String file;
    String url;
    public SolutionResourceModel(SolutionResource resource) {
        this.file = resource.getFile();
        this.url = resource.getUrlPath();
    }
    
    public String getFile() {
        return file;
    }
    public String getUrl() {
        return url;
    }
}

