$(function () {
    var canvas, context,pencil_btn,rect_btn, width, height, x, y, clickX, clickY, penDown = false;
	var origcanvas, origcontext,currentTool='pencil';
	var graphcanvas,graphcontext,topcanvas,topcontext,gr2D,nL,graphMode,gr2D_xp,gr2D_yp,nL_xp,nL_yp;
	var offX,offY,x0,y0,w0,h0,drawingLayer;
	var graphicData,tool_id
    canvas = $("#canvas")[0];
	origcanvas = $("#ocanvas")[0];
	graphcanvas = $("#gcanvas")[0];
	topcanvas = $("#tcanvas")[0];
    context = canvas.getContext("2d");
	origcontext = origcanvas.getContext("2d");
	graphcontext = graphcanvas.getContext("2d");
	topcontext = topcanvas.getContext("2d");
    width = canvas.width;
    height = canvas.height;
	 context.font=origcontext.font =topcontext.font="12px sans-serif";
	gr2D=new Image();
	gr2D.src='gr2D.png';
	nL=new Image();
	nL.src='nL.png';
	graphMode='';
	gr2D_xp=nL_xp=(width-300)/2;
	gr2D_yp=(height-300)/2;
	nL_yp=(height-100)/2;
	offX=$("#canvas-container").offset().left
	offY=$("#canvas-container").offset().top
	graphicData={}
	tool_id={};
	tool_id['eraser']=0;
	tool_id['pencil']=1;
	tool_id['text']=2;
	tool_id['line']=3;
	tool_id['rect']=4;
	tool_id['oval']=5;
	//tool_id['ellipse']=5;
	tool_id['gr2D']=11;
	tool_id['nL']=12;
	drawingLayer='1'
	var scope=this
	
    //drawRect(0,0,width,height,'#ff0000');
	$("#button_text").click(function (event) {
		currentTool='text';
		
	});
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
	$("#button_gr2D").click(function (event) {
		currentTool='gr2D';		
		showHideGraph('gr2D')
		
	});
	$("#button_nL").click(function (event) {
		currentTool='nL';
		showHideGraph('nL')
	});
	$("#button_clear").click(function (event) {
		//resetWhiteBoard();
		currentTool='pencil'
		//penDown=false;
		//graphMode='';
		//origcanvas.width=graphcanvas.width=topcanvas.width=canvas.width=width;
		resetWhiteBoard();
	});
	$("#button_eraser").click(function (event) {
		//resetWhiteBoard();
		currentTool='eraser'
	});
	//
	$("#done_btn").click(function(event){
	var txt=$("#content").val()
	graphicData.dataArr[0].text=txt;
	context.fillText(txt,clickX,clickY)
	updateCanvas();
	sendData();
	$("#content").val('');
	$("#inputBox").hide();
	})
	//
    $("#canvas").mousedown(function (_event) {
	var event=_event?_event:window.event;
        var dx, dy, dist;
		dx = event.layerX?event.layerX:event.pageX-offX;
		dy = event.layerY?event.layerY:event.pageY-offY;
context.lineWidth = 2.0
    context.strokeStyle = "rgb(0, 0, 0)";		
        if(dx>=0&&dx<width) {
            penDown = true;
            clickX = dx;
            clickY = dy;
            x = dx;
            y = dy;
            
			if(!graphicData.dataArr){
			graphicData.dataArr = [];
			
			}
			graphicData.id = tool_id[currentTool];
			if(currentTool=='pencil'){
            context.beginPath();
            context.moveTo(x,y);			
			}else if(currentTool=='eraser'){
            
            erase(x,y);			
			}
			if(currentTool=='text'){
			 penDown = false;
			graphicData.dataArr[0] = {x:x, y:y,text:"", color:context.strokeStyle, name:"",layer:drawingLayer};
			
			showTextBox();
			}else{
			graphicData.dataArr[graphicData.dataArr.length] = {x:x, y:y, id:"move", color:context.strokeStyle, name:"",layer:drawingLayer};
			}
        }else {
            penDown = false;
        }
    });
 
    $("#canvas").mouseup(function (_event) {
	var event=_event?_event:window.event;
        penDown = false;
		if(currentTool=='rect'||currentTool=='oval'){
		graphicData.dataArr[0].w=w0
		graphicData.dataArr[0].h=h0
		}else if(currentTool=='line'||currentTool=='pencil'||currentTool=='eraser'){
		graphicData.dataArr[graphicData.dataArr.length] = {x:x, y:y, id:"line"};
		}
		if(currentTool!='eraser'){
		updateCanvas ();		
		context.beginPath();
		}
		sendData();
    });
 
    $("#canvas").mousemove(function (_event) {
	var event=_event?_event:window.event;
        if(penDown) {
			if(currentTool!='pencil'&&currentTool!='text'){
			
				context.clearRect(0, 0, canvas.width, canvas.height);
			}
             
			x = event.layerX?event.layerX:event.pageX-offX;
			y = event.layerY?event.layerY:event.pageY-offY;
  
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
					drawLine();
				}else if(currentTool=='eraser'){            
					erase(x,y);		
					graphicData.dataArr[graphicData.dataArr.length] = {x:x, y:y, id:"line"};
				}else{
				graphicData.dataArr[graphicData.dataArr.length] = {x:x, y:y, id:"line"};
				drawLine();
				}
				
			}
        }
    });
	function showTextBox(){
	$("#inputBox").css({"top":clickY, "left":clickX, "position":"absolute"});
	$("#inputBox").show();
	}
	function resetWhiteBoard(){
		penDown=false;
		graphMode='';
		origcanvas.width=graphcanvas.width=topcanvas.width=canvas.width=width;
		clear();
	}
	function showHideGraph(flag,x,y){
		graphcanvas.width=graphcanvas.width;
		graphcanvas.height=graphcanvas.height;
		graphicData.dataArr = [];
		graphicData.id = tool_id[currentTool];
		var addGraph=false
		if((graphMode=='gr2D'&&flag=='gr2D')||(graphMode=='nL'&&flag=='nL')){
			graphMode="";
			drawingLayer='1'
		}else{
			var gr,xp,yp
			graphMode=flag;
			if(flag=='gr2D'){
				gr=gr2D
				xp=x?x:gr2D_xp
				yp=y?y:gr2D_yp
			}else{
				gr=nL;
				xp=x?x:nL_xp
				yp=y?y:nL_yp
			}
			drawingLayer='3'
			addGraph=true;
			graphcontext.drawImage(gr,xp,yp);
		}
		graphicData.dataArr.push({x:xp, y:yp, name:"graphImage", addImage:addGraph});
		sendData();
	}
	function mouseOverGraph(){
		var mx = event.layerX?event.layerX:event.pageX-offX;
		var my = event.layerY?event.layerY:event.pageY-offY;
		var xp,yp,wi,hi
		if(graphMode=='gr2D'){
			gr=gr2D
			xp=gr2D_xp
			yp=gr2D_yp
			wi=300
			hi=300
		}else if(graphMode=='nL'){
			gr=nL;
			xp=nL_xp
			yp=nL_yp
			wi=300
			hi=100
		}
		if((mx>=xp&&mx<=xp+wi)&&(my>=yp&&my<=yp+hi)){
			return true;
		}
		return false;
	}
	function updateCanvas () {
	var cntxt=drawingLayer=='1'?origcontext:topcontext
		cntxt.drawImage(canvas, 0, 0);
		context.clearRect(0, 0, canvas.width, canvas.height);
		context.beginPath();
	}
	function erase(x,y){
	origcontext.clearRect(x-4,y-4,8,8)
	topcontext.clearRect(x-4,y-4,8,8)
	//alert(origcontext.clearRect)
	}
    function drawLine() {
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
	function sendData() {
	if (graphicData.id || graphicData.id === 0) {
		var txtVal = graphicData.dataArr[graphicData.dataArr.length - 1].text;
		if (graphicData.id == 2 && (txtVal == "" || txtVal == undefined)) {
			resetArrays();
			textRendering = false;
			return;
		}
		if(graphicData.id == 1&&graphicData.dataArr.length>500){
			var jStr = convertObjToString(graphicData);		
			currentObj.tempData = convertStringToObj(jStr);
			//ExternalInterface.call("console.log","A")
			var ptC=graphicData.dataArr.length
			var segArr=[]
			var buf
			var header=graphicData.dataArr.shift()
			var tarr=graphicData.dataArr
			var segData
			var nxtStart
			var nx0
			var ny0
			var pt={x:header.x,y:header.y}
			var nname=header.name
			//ExternalInterface.call("console.log","B")
			var segC=0
			var nheader
			while(ptC>0){
				segC++
				buf=Math.min(500,ptC)
				ptC=ptC-buf
				segData=tarr.splice(0,buf)
				var ngdata={}
				ngdata.lineColor=graphicData.lineColor
				ngdata.id=graphicData.id
				
				if(segC>1){
					var sObj={}
					sObj.id='move'
					sObj.x=pt.x
					sObj.y=pt.y
					segData.unshift(sObj);				
				}
				nheader=cloneObject(header)
				nheader.name=nname
				segData.unshift(nheader);								
				ngdata.dataArr=segData;				
				segArr.push(ngdata);				
				nxtStart=segData[segData.length-1];				
				pt={x:nxtStart.x,y:nxtStart.y}
				var n=header.name.split("_")
				nname=n[0]+"_"+(Number(n[1])+1);
			}
			for(var z=0;z<segArr.length;z++){
				sendDataToSERVER(segArr[z]);
			}
			render = false;
			resetArrays();
			textRendering = false;
			return;
		}
		render = false;
		var jsonStr = convertObjToString(graphicData);		
		sendDataToSERVER(jsonStr);
		textRendering = false;
		}
		resetArrays();
	}
	function sendDataToSERVER(jsdata){
		var jsonStr = convertObjToString(jsdata);
		//console.log("Sending json string: " + jsonStr);
		flashWhiteboardOut(jsonStr);
	}
	
	function cloneObject(obj){
		var clone={}
		for(var m in obj){
			clone[m]=obj[m]
		}
		return clone
	}
	function resetArrays() {
		graphicData.dataArr=null;
		graphicData = {};
	}
	function getToolFromID(id){
		for(var m in tool_id){
			if(id==tool_id[m]){
				return m
			}
		}
	}
	//function that converts flash object to JSON string
	function convertObjToString(obj) {
		try {
			var s = JSON.stringify(obj);
			return s;
		} catch (ex) {		
			console.log(ex.name + ":" + ex.message + ":" + ex.location + ":" + ex.text);
		}
	}
	//function that converts  JSON string to flash object
	function convertStringToObj(str) {
		try {	
			var o= eval( "(" + str + ")" );//eval(str);
			return o;
		} catch (ex) {		
			console.log(ex.name + ":" + ex.message + ":" + ex.location + ":" + ex.text);
		}
	}
	//### RENDER OBJECT TO WHITEBOARD
	function renderObj(obj) {
		var graphic_id = obj.id;
		var graphic_data= obj.dataArr;
		var line_rgb = obj.lineColor;
		var dLength = graphic_data.length;
		var dep, x0, y0, x1, y1;
		var textF;
		var idName;
		drawingLayer=graphic_data[0].layer?graphic_data[0].layer:drawingLayer;
		context.lineWidth = 2.0
    context.strokeStyle = "rgb(0, 0, 0)";
		if (graphic_id === 0) {		
			for (var i = 0; i < dLength; i++) {			
				x0 = graphic_data[i].x;
				y0 = graphic_data[i].y;
				erase(x0,y0)
			}
		}
		if (graphic_id === 3 || graphic_id === 1) {
			for (i = 0; i < dLength; i++) {
				if (graphic_data[i].id == "move") {
					context.beginPath();
					context.moveTo(graphic_data[i].x, graphic_data[i].y);
				} else {
					context.lineTo(graphic_data[i].x, graphic_data[i].y);
				}
			}
			context.stroke()
			updateCanvas ()
		}
		if (graphic_id === 2) {
			for ( i = 0; i < dLength; i++) {
				
				if (graphic_data[i].text != "" || graphic_data[i].text != undefined) {
					x0 = graphic_data[i].x;
					y0 = graphic_data[i].y;
					context.fillText(graphic_data[i].text, x0, y0);
				}
			}
			updateCanvas ()
		}
		if (graphic_id === 4 || graphic_id === 5) {
			var fName = graphic_id == 4 ? drawRect : drawOval;
			for ( i = 0; i < dLength; i++) {			
				x0=graphic_data[i].x
				y0=graphic_data[i].y
				w0=graphic_data[i].w
				h0=graphic_data[i].h
				fName(x0,y0,w0,h0);
			}
			updateCanvas ()
		}
		if (graphic_id === 11 || graphic_id ===12) {
			idName = graphic_id == 11 ? "gr2D" : "nL";
			showHideGraph(idName,graphic_data[0].x,graphic_data[0].y)		
		}
	}
	 $.fn.updateWhiteboard=function(cmdArray) {	
		var oaL = cmdArray.length;	
		for (var l = 0; l < oaL; l++) {		
			if (cmdArray[l] instanceof Array) {
				var arg = cmdArray[l][1];
				arg = arg == undefined ? [] : arg;		
				this[cmdArray[l][0]].apply(scope, arg);
			} else if (cmdArray[l].indexOf("dataArr") != -1) {
				
				draw(cmdArray[l]);
			} else {			
				scope[cmdArray[l]]();
			}		
		}
		//updateScroller();
	}
	//function receives jsonData and renders it to the screen
	$.fn.draw = function (json_str) {	
		var grobj = convertStringToObj(json_str);
		renderObj(grobj);
	}
	function clear() {
	}
	function flashWhiteboardOut(data){
	//console.log(data);
	}
//--!
});