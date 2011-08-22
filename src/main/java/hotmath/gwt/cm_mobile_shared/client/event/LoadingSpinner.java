package hotmath.gwt.cm_mobile_shared.client.event;


public class LoadingSpinner {
    String nodeId;
    public LoadingSpinner(String nodeId) {
        this.nodeId = nodeId;
        initializeSpinner(nodeId);
    }
    
    private native void initializeSpinner(String nodeId) /*-{
       _loadingSpinner = null;  // global
       _loadingDiv = $doc.getElementById(nodeId); 
       
       var opts = {
           lines: 10, // The number of lines to draw
           length: 10, // The length of each line
           width: 2, // The line thickness
           radius: 15, // The radius of the inner circle
           color: '#000', // #rbg or #rrggbb
           speed: .5, // Rounds per second
           trail: 100, // Afterglow percentage
           shadow: true // Whether to render a shadow
       };
       
       if(_loadingDiv) {
           _loadingSpinner = new $wnd.Spinner(opts).spin(_loadingDiv);   
       }
       else {
           alert("no target for loading spinner found: " + nodeId);
       }
    }-*/;
    
    public native void startSpinner() /*-{
        if(_loadingSpinner) {
            _loadingSpinner.spin(_loadingDiv);
        }
        else {
           alert('loading spinner not initialized');
        }
    }-*/;

    public native void stopSpinner() /*-{
        if(_loadingSpinner) {
            _loadingSpinner.stop();
        }
        else {
           alert('loading spinner not initialized');
        }
    }-*/;
}