
// global array of initialized whiteboard objects
window._cmWhiteboards = [];

function setupStaticWhiteboards() {
	try {
		//alert('setting up static whiteboards');
		// find any static whiteboards
		var whiteboardDivs = $('.cm_whiteboard');
		//alert('static whiteboards:  ' + whiteboardDivs.length);
	
		for(var i=0, t=whiteboardDivs.length;i<t;i++) {
			var wb = whiteboardDivs.get(i);
			var whiteboardId = wb.getAttribute('wb_id');
			//alert('loading whiteboard: ' + wbId);
			
			wb.setAttribute("id", whiteboardId);
			window._cmWhiteboards[whiteboardId] = new Whiteboard(whiteboardId, true);
			
			window._cmWhiteboards[whiteboardId].whiteboardIsReady = function() {
				//alert('whiteboard is ready');
		        gwt_loadStaticWhiteboardCommands(whiteboardId);
		        window._cmWhiteboards[whiteboardId].resizeWhiteboardTo('content',100,100);
			}
			window._cmWhiteboards[whiteboardId].setWhiteboardViewPort(500,300);
			window._cmWhiteboards[whiteboardId].initWhiteboard(document);
		}
	}
	catch(e) {
		alert('error loading static whiteboards: ' + e);
	}
}
