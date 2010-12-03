function $get(x) {
    return document.getElementById(x);
}


var _mainMenuItemObject=null;

function setupPage() {
    /** setup menu buttons
     * 
     */
    YUI().use('node', function(Y) {

        setupPageLocal();
            

        if(_mainMenuItem == -1)
            return;
        
        Y.on('domready',function() {
            var menuButtons = Y.one('#menubar').all('a');
            _mainMenuItemObject = menuButtons.item(_mainMenuItem);
            _mainMenuItemObject.removeClass('notselected');
            _mainMenuItemObject.addClass('sexy_cm_green');
            _mainMenuItemObject.addClass('sexybutton');
        });
     });
    
    YUI().use('event-mouseenter', function(Y) {
        return;
        
        Y.on("mouseenter", function (e) {
            if(_mainMenuItemObject == this) {
                alert('this is the selected object');
                return;
            }
            
             this.addClass('sexybutton');
             this.addClass('sexy_cm_green');
             this.addClass('disabled');
         }, ".notselected");
        
        Y.on("mouseleave", function (e) {
             this.removeClass('disabled');
             this.removeClass('sexybutton');
             this.removeClass('sexy_cm_green');

         }, ".notselected");
        
    });
 }

function setupPageLocal() {
    /** empty */
}

YUI().use('node',function(Y) {
    Y.on('domready',function() {
        setupPage();
    });
});



var _overlay=null;

function showMonaMotivationalVideo() {
    var closer = '<div style="text-align: center;margin-top: 25px;">' +
                 '<a class="sexybutton sexysimple" href="#" onclick="closeMonaVideo();return false;">Close</a></div>';
    var html = '<iframe src="/motivational_video/" width="100%" height="250px" scrolling="no" frameborder="no"></iframe>'
            + closer;
    
    YUI().use('anim','overlay',
                    function(Y) {
                        _overlay = new Y.Overlay(
                                {   
                                    id:"mona-video",
                                    width : "350px",
                                    height : "350px",
                                    centered : true,
                                    headerContent : "Catchup Math Motivational Video",
                                    bodyContent : html,
                                    zIndex : 2,
                                    visible:true
                                });
                        
                       _overlay.render();
    });
}

/** Closed from close button */
function closeMonaVideo() {
    _overlay.hide();
}