// global array of initialized whiteboard objects
window._cmWhiteboards = [];

function setupStaticWhiteboards() {
	try {
		// find any static whiteboard json defs 
		
		
		var tutorWrapper = TutorManager.getActiveTutorWrapper();
		
		var whiteboardDivs = $('.wb_json', tutorWrapper.tutorDomNode);
		//alert('inline whiteboards:  ' + whiteboardDivs.length);
	
		for(var i=0, t=whiteboardDivs.length;i<t;i++) {
			var wb = whiteboardDivs.get(i);
			
			var wbJson = wb.innerText;
			wb.innerHTML = '';
			
			var uniq = new Date().getUTCMilliseconds();
			var whiteboardId = 'wb_json-' + uniq + "_" + i;
			
			wb.setAttribute("id", whiteboardId);
			window._cmWhiteboards[whiteboardId] = new Whiteboard(whiteboardId, true);

			/** Create execution context for closure value */ 
			window._cmWhiteboards[whiteboardId].whiteboardIsReady = (function(wbId, json) {
				return function() {
					// alert('process: ' + wbId);
					window._cmWhiteboards[wbId].loadFromJson(json);
			        window._cmWhiteboards[wbId].resizeWhiteboardTo('content',100,100);
				}
			})(whiteboardId, wbJson);
			window._cmWhiteboards[whiteboardId].setWhiteboardViewPort(500,300);
			window._cmWhiteboards[whiteboardId].initWhiteboard(document);
		}
		
	}
	catch(e) {
		alert('error loading static whiteboards: ' + e);
	}
}


