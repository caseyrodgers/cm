package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_tools.client.model.StringHolder;


public class GetReviewHtmlAction implements Action<LessonResult>{
    
    String file;
    boolean isSpanish;

    public GetReviewHtmlAction() {}
    
    public GetReviewHtmlAction(String file) {
        this.file = file;
    }

    /** Return the base directory to use for lookup
     *  
     *  If spanish, return specific spanish directory
     *  
     * @return
     */
    public String getBaseDirectory() {
        return "/hotmath_help/" +
               (isSpanish?"spanish/":"") +
               "/topics";
    }
    

    /** Return absolute path to file either spanish or english ,
     *  
     * @return
     */
    public String getFile() {
        return "/hotmath_help/" +
        (isSpanish?"spanish/":"") +
        "/" + file;
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
