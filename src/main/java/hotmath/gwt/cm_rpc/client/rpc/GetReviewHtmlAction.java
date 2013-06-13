package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetReviewHtmlAction implements Action<LessonResult>{
    
    String file;
    boolean spanish;

    public GetReviewHtmlAction() {}
    
    public GetReviewHtmlAction(String file) {
        this(file, false);
    }
    
    public GetReviewHtmlAction(String file, boolean spanish) {
        this.file = file;
        this.spanish = spanish;
    }

    /** Return absolute path to file either Spanish or English ,
     *  
     * @return
     */
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isSpanish() {
        return spanish;
    }

    public void setSpanish(boolean isSpanish) {
        this.spanish = isSpanish;
    }

    @Override
    public String toString() {
        return "GetReviewHtmlAction [file=" + file + ", isSpanish=" + spanish
                + "]";
    }
}
