function setupPageLocal() {
    
    /** setup menu buttons
     * 
     */
    YUI().use('node', function(Y) {
        if(_mainMenuItem == -1)
            return;
        
        Y.on('domready',function() {
            var menuButtons = Y.one('#menubar').all('a');
            var mainButton = menuButtons.item(_mainMenuItem);
            mainButton.removeClass('sexybutton');
            mainButton.removeClass('sexyorange');
            mainButton.addClass('sexybutton');
            mainButton.addClass('sexyorange');
        });
     });
 }

setupPageLocal();