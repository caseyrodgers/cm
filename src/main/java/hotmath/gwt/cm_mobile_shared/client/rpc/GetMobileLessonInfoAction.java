package hotmath.gwt.cm_mobile_shared.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;

public class GetMobileLessonInfoAction implements Action<MobileLessonInfo>{
    String file;
    
    public GetMobileLessonInfoAction() {}
    
    public GetMobileLessonInfoAction(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
