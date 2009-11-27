package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.shared.client.CmShared;

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
	static public  CmResourcePanel create(InmhItemData item) throws Exception {
	    
	    String type = item.getType();
	    
	    Log.debug("ResourceViewerFactory: creating new resource viewer: " + type);
	    
	    CmResourcePanel rp=null;
		if(type.equals("practice")) {
		    
		    if(CmShared.getQueryParameter("tutor_view_sidebyside") != null)
		        rp = new ResourceViewerImplTutorSideBySide();
		    else 
			    rp = new ResourceViewerImplTutor();
		}
		else if(type.equals("video")) { 
		    rp = new ResourceViewerImplVideo();
	    }
		else if(type.startsWith("activity")) {
		    rp = new ResourceViewerImplActivity();
		}
		else if(type.equals("workbook")) {
		    rp =  new ResourceViewerImplWorkbook();
		}
		else if(type.equals("review")) {
		    rp =  new ResourceViewerImplReview();
		}
		else if(type.equals("testset")) {
		    rp =  new ResourceViewerImplQuiz();
		}
		else if(type.equals("results")) {
		    rp =  new ResourceViewerImplResults();
		}
		else if(type.equals("cmextra")) {
		    rp =  new ResourceViewerImplTutor();
		}
		else if(type.equals("flashcard")) {
		    rp =  new ResourceViewerImplFlashCard();
		}
		else
		    rp =  new CmResourcePanelImplDefault();
		
		rp.setResourceItem(item);
		
		return rp;
	}
}
