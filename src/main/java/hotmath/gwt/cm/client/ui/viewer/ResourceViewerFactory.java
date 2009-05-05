package hotmath.gwt.cm.client.ui.viewer;


/** Create the appropriate resource viewer
 * 
 * @author Casey
 *
 */
public class ResourceViewerFactory {

    
    /** Create the appropriate resource viewer type
     * 
     * @param type
     * @return
     * @throws Exception
     */
	static public  ResourceViewer create(String type) throws Exception {
		if(type.equals("practice")) {
			return new ResourceViewerImplTutor();
		}
		else if(type.equals("video")) { 
	        return new ResourceViewerImplVideo();
	    }
		else if(type.equals("activity")) {
			return new ResourceViewerImplActivity();
		}
		else if(type.equals("workbook")) {
			return new ResourceViewerImplWorkbook();
		}
		else if(type.equals("review")) {
			return new ResourceViewerImplReview();
		}
		else if(type.equals("testset")) {
			return new ResourceViewerImplQuiz();
		}
		else
			return new ResourceViewerImplDefault();
	}
}
