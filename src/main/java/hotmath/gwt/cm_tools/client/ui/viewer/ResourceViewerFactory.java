package hotmath.gwt.cm_tools.client.ui.viewer;

import com.allen_sauer.gwt.log.client.Log;


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
	    
	    Log.debug("ResourceViewerFactory: creating new resource viewer: " + type);
	    
		if(type.equals("practice")) {
			return new ResourceViewerImplTutor();
		}
		else if(type.equals("video")) { 
	        return new ResourceViewerImplVideo();
	    }
		else if(type.startsWith("activity")) {
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
		else if(type.equals("results")) {
		    return new ResourceViewerImplResults();
		}
		else if(type.equals("cmextra")) {
		    return new ResourceViewerImplTutor();
		}
		else if(type.equals("flashcard")) {
		    return new ResourceViewerImplFlashCard();
		}
		else
			return new ResourceViewerImplDefault();
	}
}
