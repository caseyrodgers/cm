package hotmath.gwt.cm_rpc.client.rpc;


public class GetReviewHtmlAction implements Action<RpcData>{
    
    String file;
    String baseDirectory;

    public GetReviewHtmlAction() {}
    
    public GetReviewHtmlAction(String file, String baseDirectory) {
        this.file = file;
        this.baseDirectory = baseDirectory;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
