$(function () {
    var canvas, context,pencil_btn,rect_btn, width, height, x, y, clickX, clickY, penDown = false;
	var origcanvas, origcontext,currentTool='pencil';
    canvas = $("#canvas")[0];
	origcanvas = $("#ocanvas")[0];
	canvas.x=origcanvas.x
    context = canvas.getContext("2d");
	origcontext = origcanvas.getContext("2d");
    width = canvas.width;
    height = canvas.height;
    //drawRect(0,0,width,height,'#ff0000');
	
	$("#button_pencil").click(function (event) {
		currentTool='pencil';
		
	});
	$("#button_rectangle").click(function (event) {
		currentTool='rect';
	});
	$("#button_line").click(function (event) {
		currentTool='line';
	});
	$("#button_oval").click(function (event) {
		currentTool='oval';
	});
    $("#canvas").mousedown(function (event) {
        var dx, dy, dist;
		dx = event.layerX;
		dy = event.layerY;		
		//alert(event.pageX +":"+ this.offsetLeft+" <> "+event.pageY +":"+ this.offsetTop)
        if(dx>=0&&dx<width) {
            penDown = true;
            clickX = dx;
            clickY = dy;
            x = dx;
            y = dy;
            context.lineWidth = 2.0
            context.strokeStyle = "rgb(0, 0, 0)";
			if(currentTool=='pencil'){
            context.beginPath();
            context.moveTo(x,y);
			}			
        }
        else {
            penDown = false;
        }
    });
 
    $("#canvas").mouseup(function (event) {
        penDown = false;
		updateCanvas ();
    });
 
    $("#canvas").mousemove(function (event) {
        if(penDown) {
			if(currentTool!='pencil'||currentTool!='text'){
				context.clearRect(0, 0, canvas.width, canvas.height);
			}
             
			x = event.layerX;
			y = event.layerY;
  
			if(currentTool=='rect'||currentTool=='oval'){
			
				x0=clickX;
				y0=clickY;
				w0=x-clickX;
				h0=y-clickY;
			if(currentTool=='rect'){
				drawRect(x0,y0,w0,h0)
			}
			if(currentTool=='oval'){
				drawOval(x0,y0,w0,h0)
			}
			}else{
				if(currentTool=='line'){
					context.beginPath();
					context.moveTo(clickX,clickY);
				}
				draw();
			}
        }
    });
	function updateCanvas () {
		origcontext.drawImage(canvas, 0, 0);
		context.clearRect(0, 0, canvas.width, canvas.height);
	}
    function draw() {
        context.lineTo(x,y)
        context.stroke();
    }
    function drawRect(x,y,w,h,color) {
        if(color!=undefined){
			context.strokeStyle = color;
		}
		context.strokeRect(x, y, w, h);
	}
	function drawOval(x,y,w,h,color){
		if(color!=undefined){
			context.strokeStyle = color;
		}
		var kappa = 0.5522848;
		var ox = (w / 2) * kappa;
		var oy = (h / 2) * kappa;
		var xe = x + w;
		var ye = y + h;
		var xm = x + w / 2;
		var ym = y + h / 2;	
		context.beginPath();
		context.moveTo(x, ym);
		context.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
		context.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
		context.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
		context.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
		context.closePath();
		context.stroke();
	}
});