// setup hooks back to the GWT engine
// these are needed due to how Flash is 
// handled when loaded dynamically. The 
// main problem is the ExternalInterface
// from the flash component only works when
// during page load.  It breaks
// when loaded dynamically.

// called by flash component, we funnel
// it back to the parent JSNI method defined
// in ShowWorkPanel.class
function flashWhiteboardOut(jsonOut) {
    parent.flashWhiteboardOut(jsonOut);
}

// called when whiteboard is ready for
// commands.  Funnel it back to parent
// JSNI defined method in ShowWorkPanel.class
function flashWhiteboardIsReady() {
   // alert('flashWhiteboardIsReady in test');
   var fo = document.getElementById("whiteboard");
   parent.flashWhiteboardIsReady();   // call back to GWT JSNI
}

// called by GWT in ShowWorkPanel when reading
// data from server and requesting whiteboard
// to update itself.  
// The id is the unique flash id, the command+data
// is a single request for the whiteboard to do
// something. Such as 'draw', 'clear', or 'load'
// commandData, if used, is JSON.
function updateWhiteboard(id, command, commandData) {
   
   //  only works if single object on page
   // @TODO: get browser depend object
   var fo = swfobject.getObjectById("whiteboard-object");
   
   //alert(fo + ', ' + fo.name + ', ' + fo.updateWhiteboard);
   if(!fo) {
       alert('Could not find whiteboard flash object');
   }
   if(fo.updateWhiteboard) {
       // send an array of commands.  Each element in array
       // is a command and an array of data.  For example, one
       // 'draw', but a bunch of draw requests.
       fo.updateWhiteboard([['draw',[commandData]]]);
   }
   else {
      alert('could not find updateWhiteboard: ' + id);
   }
}

// register callback in the parent GWT to allow
// GWT to call back.
function setWhiteboardBackground(html) {
    //var wbg = document.getElementById('whiteboard-bg');
    //wbg.innerHTML = html;        
}

// setup hooks for GWT to call from the app window.
parent.setWhiteboardBackground = setWhiteboardBackground;
parent.updateWhiteboard = updateWhiteboard;
