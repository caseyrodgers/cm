function $get(x) {
    return document.getElementById(x);
}

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
            var mainButton = menuButtons.item(_mainMenuItem);
            mainButton.removeClass('sexybutton');
            mainButton.addClass('sexy_cm_green');
            mainButton.addClass('sexybutton');
            mainButton.addClass('sexy_cm_green');
        });
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