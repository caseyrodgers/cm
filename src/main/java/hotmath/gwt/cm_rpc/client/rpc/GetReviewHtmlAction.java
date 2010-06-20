package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_tools.client.model.StringHolder;


public class GetReviewHtmlAction implements Action<LessonResult>{
    
    String file;
    boolean isSpanish;

    public GetReviewHtmlAction() {}
    
    public GetReviewHtmlAction(String file) {
        this.file = file;
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
        return isSpanish;
    }

    public void setSpanish(boolean isSpanish) {
        this.isSpanish = isSpanish;
    }

    @Override
    public String toString() {
        return "GetReviewHtmlAction [file=" + file + ", isSpanish=" + isSpanish
                + "]";
    }
}
