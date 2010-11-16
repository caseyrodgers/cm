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
            mainButton.removeClass('sexyorange');
            mainButton.addClass('sexybutton');
            mainButton.addClass('sexyorange');
        });
     });
 }

function setupPageLocal() {
    /** empty */
}

setupPage();
