
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


window._setupTutorDragAndDrop = function() {
	var $div = $('#tutor_output');
	$('.drag')
	.drag("start",function( ev, dd ){
		alert('start drag ...');
		dd.attr = $( ev.target ).prop("className");
		console.log(dd.attr);
		dd.limit = $div.offset();
		dd.limit.bottom = dd.limit.top + $div.outerHeight() - $( this ).outerHeight();
		dd.limit.right = dd.limit.left + $div.outerWidth() - $( this ).outerWidth();
		dd.width = $(this).width();
		dd.height = $(this).height();
	})
	.drag(function( ev, dd ){
		var properties = {};
		if(dd.attr == 'drag'){
	
			$(this).css({
				top: Math.min( dd.limit.bottom, Math.max( dd.limit.top, dd.offsetY ) ),
				left: Math.min( dd.limit.right, Math.max( dd.limit.left, dd.offsetX ) ),
			}); 
			
		}else if(dd.attr == 'handle'){
			$(this).css({
				width: Math.max( 40, Math.min(dd.width + dd.deltaX, $div.width() - dd.limit.left )),
				height: Math.max( 40, Math.min(dd.height + dd.deltaY, $div.height() - dd.limit.top ))
			}); 
		}
	});
}	