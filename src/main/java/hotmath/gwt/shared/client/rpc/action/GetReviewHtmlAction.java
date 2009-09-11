package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

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
