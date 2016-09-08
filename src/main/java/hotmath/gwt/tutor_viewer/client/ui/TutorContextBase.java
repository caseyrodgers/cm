package hotmath.gwt.tutor_viewer.client.ui;

public class TutorContextBase {
	
    
    protected native String _nativeGenerateContext(String pid, String js, String jsonConfig) /*-{
        $wnd.gwt_solutionHasBeenInitialized = function() {};
        
        var that = this;
        try {
            return $wnd.TutorManager.generateContext(pid, js, jsonConfig);
        }
        catch(x) {
            alert('error generating context: ' + x);
        }
        
    }-*/;	

}
