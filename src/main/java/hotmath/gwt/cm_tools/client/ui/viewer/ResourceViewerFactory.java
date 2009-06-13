package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Widget;


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
	    
	    if(false) {
	        return new ResourceViewerImplTest();
	    }
	    
	    
	    Log.debug("ResourceViewerFactory: creating new resource viewer: " + type);
	    
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
		else if(type.equals("results")) {
		    return new ResourceViewerImplResults();
		}
		else if(type.equals("cmextra")) {
		    return new ResourceViewerImplTutor();
		}
		else
			return new ResourceViewerImplDefault();
	}
}


class ResourceViewerImplTest extends ResourceViewerContainer {
    
    
    public ResourceViewerImplTest() {
        setStyleName("resource-viewer-impl-test");
    }

    @Override
    public Widget getResourcePanel(InmhItemData resource) {
        String s = "This is a testjklasdflk;as df;l;sg asfdl;kasfdl;sdajfdjl;jasd ;asl sk;ljld;dlakfasldaksdja;lfajaskasdfasf" +
        "This is a testjklasdflk;as df;l;sg asfdl;kasfdl;sdajfdjl;jasd ;asl sk;ljld;dlakfasldaksdja;lfajaskasdfasf" +
        "This is a testjklasdflk;as df;l;sg asfdl;kasfdl;sdajfdjl;jasd ;asl sk;ljld;dlakfasldaksdja;lfajaskasdfasf" +
        "This is a testjklasdflk;as df;l;sg asfdl;kasfdl;sdajfdjl;jasd ;asl sk;ljld;dlakfasldaksdja;lfajaskasdfasf" +
        "This is a testjklasdflk;as df;l;sg asfdl;kasfdl;sdajfdjl;jasd ;asl sk;ljld;dlakfasldaksdja;lfajaskasdfasf";
        Html html = new Html("<b>" + s + s + s + s + s + s + "</b>");
        html.setStyleName("resource-viewer-impl-test-comp");
        html.setHeight(600);
        add(html);
        addResource(html, "A Test");
        return this;
    }
    
}