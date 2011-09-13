
/** Define Whiteboard API:
 *
 *   initWhitegboard(document);   // initialze whiteboard and prepare for new use
 *   updateWhiteboard(cmdArray);  // write to the whiteboard, sending command and JSON
 *   whiteboardOut(data);         // changes to whiteboard and published here.
 *   saveWhiteboard();            // called when user/system requests persisting whitebroard data.
 *
 *   NOTE: detachWhiteboard not needed.
 */
var Whiteboard = (function () {
    var wb = {};
    var canvas, context, pencil_btn, rect_btn, width, height, x, y, clickX, clickY, penDown = false;
    var origcanvas, origcontext, currentTool = 'pencil';
    var graphcanvas, graphcontext, topcanvas, topcontext, gr2D, nL, graphMode, gr2D_xp, gr2D_yp, nL_xp, nL_yp;
    var offX, offY, x0, y0, w0, h0, drawingLayer, drawcolor, rendering;
    var graphicData, tool_id;
    var scope = this;
    var isTouchEnabled = false;

    function renderText(xt, xp, yp) {
        var txt = xt ? xt : $get_Element("#content").value;
        // alert(txt);
        var str = txt.split("\n")
        var x0 = xp ? xp : clickX
        var y0 = yp ? yp : clickY
        var ht = 15
        for (var i = 0; i < str.length; i++) {

            context.fillText(str[i], x0, y0)
            y0 += ht
        }
        updateCanvas();
        if (!xt) {
            updateText(txt);
            sendData();
            $get_Element("#content").value = "";
            $get_Element("#inputBox").style.display = 'none';
        }
        // alert($get_Element("#inputBox").style.display)
    }

    function onkeyupHandler() {
        //
    }

    function onkeydownHandler(_event) {
        var event = _event ? _event : window.event;

        if (currentTool == 'text' && event.keyCode == 13) {
            if (!event.shiftKey) {
                if (event.preventDefault) {
                    event.preventDefault();
                } else {
                    event.returnValue = false;
                }
                renderText();
            }
        }
    }

    function resetButtonHighlite() {
        $get_Element("#button_text").style.border = '1px solid #000000';
        $get_Element("#button_pencil").style.border = '1px solid #000000';
        $get_Element("#button_line").style.border = '1px solid #000000';
        $get_Element("#button_rect").style.border = '1px solid #000000';
        $get_Element("#button_oval").style.border = '1px solid #000000';
        $get_Element("#button_eraser").style.border = '1px solid #000000';
        //
    }

    function buttonHighlite(t) {
        resetButtonHighlite();
        $get_Element("#button_" + t).style.border = '2px solid #ff9900';
    }

    function viewport() {
        var e = window,
            a = 'inner';

        if (!('innerWidth' in window)) {
            a = 'client';
            e = document.documentElement || document.body;
        }

        return {
            width: e[a + 'Width'],
            height: e[a + 'Height']
        }
    }

    function getDocHeight() {
        var D = document;
        return Math.max(
        Math.max(D.body.scrollHeight, D.documentElement.scrollHeight), Math.max(D.body.offsetHeight, D.documentElement.offsetHeight), Math.max(D.body.clientHeight, D.documentElement.clientHeight));
    }



    /** Define as functions to allow removing
     *
     * @param event
     */

    function touchStartFunction(event) {
        event.preventDefault();
    }
    var touchMoveFunction = touchStartFunction;

    var _imageBaseDir = '/gwt-resources/images/whiteboard/';

    /** main HTML document object */
    var mainDoc;
    wb.initWhiteboard = function (mainDocIn) {
        mainDoc = mainDocIn;
        canvas = $get_Element("#canvas");
        var siz = viewport()
        var docWidth = siz.width;
        var docHeight = siz.height;
        var topOff = $get_Element("#tools").offsetHeight + $get_Element("#tools").offsetTop + 15
        var leftOff = $get_Element("#tools").offsetLeft + 15
        origcanvas = $get_Element("#ocanvas");
        graphcanvas = $get_Element("#gcanvas");
        topcanvas = $get_Element("#tcanvas");
        canvas.width = origcanvas.width = graphcanvas.width = topcanvas.width = docWidth - leftOff;
        canvas.height = origcanvas.height = graphcanvas.height = topcanvas.height = docHeight - topOff;
        context = canvas.getContext("2d");
        origcontext = origcanvas.getContext("2d");
        graphcontext = graphcanvas.getContext("2d");
        topcontext = topcanvas.getContext("2d");
        width = canvas.width;
        height = canvas.height;
        // alert(width+":"+height)
        context.font = origcontext.font = topcontext.font = "12px sans-serif";
        gr2D = new Image();
        gr2D.src = _imageBaseDir + 'gr2D.png';
        nL = new Image();
        nL.src = _imageBaseDir + 'nL.png';
        graphMode = '';
        gr2D_xp = nL_xp = (width - 300) / 2;
        gr2D_yp = (height - 300) / 2;
        nL_yp = (height - 100) / 2;
        gr2D_w = 300;
        gr2D_h = 300;
        nL_w = 300;
        nL_h = 100;
        offX = $get_Element("#canvas-container").offsetLeft
        offY = $get_Element("#canvas-container").offsetTop;
        // alert(offX+":"+offY);
		var getCanvasPos = function(){
			var obj = $get_Element("#canvas-container");
			var top = 0;
			var left = 0;
			while (obj.tagName != "BODY") {
				top += obj.offsetTop;
				left += obj.offsetLeft;
				obj = obj.offsetParent;
			}
			offX=left;
			offY=top;
			return {
				top: top,
				left: left
			};
		};
		getCanvasPos();
        graphicData = {}
        tool_id = {};
        tool_id['eraser'] = 0;
        tool_id['pencil'] = 1;
        tool_id['text'] = 2;
        tool_id['line'] = 3;
        tool_id['rect'] = 4;
        tool_id['oval'] = 5;
        // tool_id['ellipse']=5;
        tool_id['gr2D'] = 11;
        tool_id['nL'] = 12;
        drawingLayer = '1'
        $get_Element("#button_pencil").style.border = '2px solid #ff9900';
        // Events
        // drawRect(0,0,width,height,'#ff0000');
        $get_Element("#button_text").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='crosshair';
            currentTool = 'text';
            buttonHighlite(currentTool)
        };
        $get_Element("#button_pencil").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='url("imgs/pencil.png"),auto';
            currentTool = 'pencil';
            buttonHighlite(currentTool)
        };
        $get_Element("#button_rect").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='crosshair';
            currentTool = 'rect';
            buttonHighlite(currentTool)
        };
        $get_Element("#button_line").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='crosshair';
            currentTool = 'line';
            buttonHighlite(currentTool)
        };
        $get_Element("#button_oval").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='crosshair';
            currentTool = 'oval';
            buttonHighlite(currentTool)
        };
        $get_Element("#button_gr2D").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='url("imgs/pencil.png"),auto';
            currentTool = 'gr2D';
            showHideGraph('gr2D')
            buttonHighlite('pencil')
        };
        $get_Element("#button_nL").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='url("imgs/pencil.png"),auto';
            currentTool = 'nL';
            showHideGraph('nL')
            buttonHighlite('pencil')
        };
        $get_Element("#button_clear").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='url("imgs/pencil.png"),auto';
            // resetWhiteBoard();
            currentTool = 'pencil'
            buttonHighlite(currentTool)
            // penDown=false;
            // graphMode='';
            // origcanvas.width=graphcanvas.width=topcanvas.width=canvas.width=width;
            resetWhiteBoard(true);
        };
        $get_Element("#button_eraser").onclick = function (event) {
            // $get_Element("#drawsection").style.cursor='url("imgs/eraser.png"),auto';
            // resetWhiteBoard();
            currentTool = 'eraser'
            buttonHighlite(currentTool)
        };
        //
        $get_Element("#done_btn").onclick = function (event) {
            renderText();
        }
        $get_Element("#button_save").onclick = function (event) {
            wb.saveWhiteboard();
        };
        
        //
        var ev_onmousedown = function (_event) {
                isTouchEnabled = _event.type.indexOf('touch') > -1
                if (isTouchEnabled) {
                    canvas.removeEventListener("mousedown", ev_onmousedown, false);
                    canvas.removeEventListener("mouseup", ev_onmouseup, false);
                    canvas.removeEventListener("mousemove", ev_onmousemove, false);
                }
                /*
                 * else{ canvas.removeEventListener('touchstart',ev_onmousedown, false);
                 * canvas.removeEventListener('touchmove',ev_onmousemove, false);
                 * canvas.removeEventListener('touchend',ev_onmouseup, false); }
                 */
                var event = _event ? _event : window.event;

                event = isTouchEnabled ? _event.targetTouches[0] : event;
                var dx, dy, dist;
                if (event.layerX || event.pageX) {
                    dx = event.layerX ? event.layerX : event.pageX - offX;
                    dy = event.layerY ? event.layerY : event.pageY - offY;
                } else {
                    dx = event.clientX - offX
                    dy = event.clientY - offY
                }
                // alert(dx+":"+event.clientX)
				console.log(dy+":"+event.clientY+":"+event.layerY+":"+event.pageY+":"+offY);
                context.lineWidth = 2.0
                context.strokeStyle = "rgb(0, 0, 0)";

                if (dx >= 0 && dx < width) {
                    penDown = true;
                    rendering = false;
                    clickX = dx;
                    clickY = dy;
                    x = dx;
                    y = dy;

                    if (!graphicData.dataArr) {
                        graphicData.dataArr = [];

                    }
                    graphicData.id = tool_id[currentTool];
                    if (currentTool == 'pencil') {
                        context.beginPath();
                        context.moveTo(clickX, clickY);
                    } else if (currentTool == 'eraser') {

                        erase(x, y);
                    }
                    drawcolor = colorToNumber(context.strokeStyle)
                    if (currentTool == 'text') {
                        penDown = false;

                        graphicData.dataArr[0] = {
                            x: x,
                            y: y,
                            text: "",
                            color: drawcolor,
                            name: "",
                            layer: drawingLayer
                        };
                        // alert("0:: "+graphicData.dataArr[0])
                        showTextBox();
                    } else {
                        graphicData.dataArr[graphicData.dataArr.length] = {
                            x: x,
                            y: y,
                            id: "move",
                            color: drawcolor,
                            name: "",
                            layer: drawingLayer
                        };
                    }
                } else {
                    penDown = false;
                }
                if (event.preventDefault) event.preventDefault();
                // _event.stopPropagation();
            };

        var ev_onmouseup = function (_event) {
                var event = _event ? _event : window.event;
                event = _event.type.indexOf('touch') > -1 ? _event.targetTouches[0] : event;
                /*
                 * if(penDown){ x = event.layerX?event.layerX:event.pageX-offX; y =
                 * event.layerY?event.layerY:event.pageY-offY; }
                 */
                if (rendering) {
                    penDown = false;
                    if (currentTool == 'rect' || currentTool == 'oval') {
                        graphicData.dataArr[0].w = w0
                        graphicData.dataArr[0].h = h0
                        graphicData.dataArr[0].xs = w0 / 400
                        graphicData.dataArr[0].ys = h0 / 400
                    } else if (currentTool == 'line' || currentTool == 'pencil' || currentTool == 'eraser') {
                        // alert(_event.type+": "+clickX+":"+clickY+":"+x+":"+y);
                        // {x:x-clickX, y:y-clickY, id:"line"}
                        var xp = x - clickX
                        var yp = y - clickY
                        xp = currentTool == 'eraser' ? x : xp
                        yp = currentTool == 'eraser' ? y : yp
                        graphicData.dataArr[graphicData.dataArr.length] = {
                            x: xp,
                            y: yp,
                            id: "line"
                        };
                    }
                    if (currentTool != 'eraser') {
                        updateCanvas();
                        context.beginPath();
                    }
                    sendData();
                    rendering = false;
                }
            };

        var ev_onmousemove = function (_event) {
                var event = _event ? _event : window.event;
                event = _event.type.indexOf('touch') > -1 ? _event.targetTouches[0] : event;
                if (penDown) {
                    rendering = true;
                    if (currentTool != 'pencil' && currentTool != 'text') {

                        context.clearRect(0, 0, canvas.width, canvas.height);
                    }

                    // x = event.layerX?event.layerX:event.pageX-offX;
                    // y = event.layerY?event.layerY:event.pageY-offY;
                    if (event.layerX || event.pageX) {
                        x = event.layerX ? event.layerX : event.pageX - offX;
                        y = event.layerY ? event.layerY : event.pageY - offY;
                    } else {
                        x = event.clientX - offX
                        y = event.clientY - offY
                    }

                    if (currentTool == 'rect' || currentTool == 'oval') {

                        x0 = clickX;
                        y0 = clickY;
                        w0 = x - clickX;
                        h0 = y - clickY;
                        if (currentTool == 'rect') {
                            drawRect(x0, y0, w0, h0)
                        }
                        if (currentTool == 'oval') {
                            drawOval(x0, y0, w0, h0)
                        }
                    } else {
                        if (currentTool == 'line') {
                            context.beginPath();
                            context.moveTo(clickX, clickY);
                            drawLine();
                        } else if (currentTool == 'eraser') {
                            erase(x, y);
                            graphicData.dataArr[graphicData.dataArr.length] = {
                                x: x,
                                y: y,
                                id: "line"
                            };
                        } else {
                            graphicData.dataArr[graphicData.dataArr.length] = {
                                x: x - clickX,
                                y: y - clickY,
                                id: "line"
                            };
                            drawLine();
                        }

                    }
                }
                if (event.preventDefault) event.preventDefault();
                // _event.stopPropagation();
            };

        if (document.addEventListener) {
            canvas.addEventListener("mousedown", ev_onmousedown, false);
            canvas.addEventListener("mouseup", ev_onmouseup, false);
            canvas.addEventListener("mousemove", ev_onmousemove, false);

            // touchscreen specific - to prevent web page being scrolled while drawing
            canvas.addEventListener('touchstart', touchStartFunction, false);
            canvas.addEventListener('touchmove', touchMoveFunction, false);

            // attach the touchstart, touchmove, touchend event listeners.
            canvas.addEventListener('touchstart', ev_onmousedown, false);
            canvas.addEventListener('touchmove', ev_onmousemove, false);
            canvas.addEventListener('touchend', ev_onmouseup, false);

        } else {
            canvas.attachEvent("onmousedown", ev_onmousedown);
            canvas.attachEvent("onmouseup", ev_onmouseup);
            canvas.attachEvent("onmousemove", ev_onmousemove);


            // touchscreen specific - to prevent web page being scrolled while drawing
            canvas.attachEvent('touchstart', touchStartFunction);
            canvas.attachEvent('touchmove', touchMoveFunction);

            // attach the touchstart, touchmove, touchend event listeners.
            canvas.attachEvent('touchstart', ev_onmousedown);
            canvas.attachEvent('touchmove', ev_onmousemove);
            canvas.attachEvent('touchend', ev_onmouseup);
        }
        canvas.focus()
    }

    function $get_Element(n) {
        var str = n.indexOf("#") > -1 ? n.split("#")[1] : n
        return mainDoc.getElementById(str);
    }

    function updateText(txt) {
        // alert(graphicData.dataArr)
        graphicData.dataArr[0].text = txt;
    }

    function showTextBox() {
        // $get_Element("#inputBox").css({"top":clickY, "left":clickX,
        // "position":"absolute"});
        $get_Element("#inputBox").style.display = 'block';
        // $get_Element("#inputBox").style.position="absolute";
        $get_Element("#inputBox").style.top = clickY + "px";
        $get_Element("#inputBox").style.left = clickX + "px";
        $get_Element("#content").focus();
        // alert($get_Element("#content"))
        // alert($get_Element("#inputBox").style.top+":"+$get_Element("#inputBox").style.left)
    }

    function resetWhiteBoard(boo) {
        penDown = false;
        graphMode = '';
        origcanvas.width = graphcanvas.width = topcanvas.width = canvas.width = width;
        origcontext.clearRect(0, 0, canvas.width, canvas.height);
        graphcontext.clearRect(0, 0, canvas.width, canvas.height);
        topcontext.clearRect(0, 0, canvas.width, canvas.height);
        context.clearRect(0, 0, canvas.width, canvas.height);
        if (boo) {
            clear(true);
        }
    }

    function showHideGraph(flag, x, y,boo) {
        graphcanvas.width = graphcanvas.width;
        graphcanvas.height = graphcanvas.height;
        graphcontext.clearRect(0, 0, canvas.width, canvas.height);
        graphicData.dataArr = [];
        graphicData.id = tool_id[currentTool];
        var addGraph = false
        if (!boo&&((graphMode == 'gr2D' && flag == 'gr2D') || (graphMode == 'nL' && flag == 'nL'))) {
            graphMode = "";
            drawingLayer = '1'
            $get_Element("#button_gr2D").style.border = '1px solid #000000';
            $get_Element("#button_nL").style.border = '1px solid #000000';
        } else {
            $get_Element("#button_gr2D").style.border = '1px solid #000000';
            $get_Element("#button_nL").style.border = '1px solid #000000';
            var gr, xp, yp, xs, ys
            graphMode = flag;
            if (flag == 'gr2D') {
                gr = gr2D
                xp = x ? x - (gr2D_w / 2) : gr2D_xp
                yp = y ? y - (gr2D_h / 2) : gr2D_yp
                xs = x ? x : gr2D_xp + (gr2D_w / 2)
                ys = y ? y : gr2D_yp + (gr2D_h / 2)
                $get_Element("#button_gr2D").style.border = '2px solid #ff0000';
            } else {
                gr = nL;
                xp = x ? x - (nL_w / 2) : nL_xp
                yp = y ? y - (nL_h / 2) : nL_yp
                xs = x ? x : nL_xp + (nL_w / 2)
                ys = y ? y : nL_yp + (nL_h / 2)
                $get_Element("#button_nL").style.border = '2px solid #ff0000';
            }
            drawingLayer = '3'
            addGraph = true;
            // graphcontext.drawImage(gr,xp,yp);
            graphcontext.drawImage(gr, xp, yp);
        }

        graphicData.dataArr.push({
            x: xs,
            y: ys,
            name: "graphImage",
            addImage: addGraph
        });
        sendData();
    }

    function mouseOverGraph() {
        var mx = event.layerX ? event.layerX : event.pageX - offX;
        var my = event.layerY ? event.layerY : event.pageY - offY;
        var xp, yp, wi, hi
        if (graphMode == 'gr2D') {
            gr = gr2D
            xp = gr2D_xp
            yp = gr2D_yp
            wi = 300
            hi = 300
        } else if (graphMode == 'nL') {
            gr = nL;
            xp = nL_xp
            yp = nL_yp
            wi = 300
            hi = 100
        }
        if ((mx >= xp && mx <= xp + wi) && (my >= yp && my <= yp + hi)) {
            return true;
        }
        return false;
    }

    function updateCanvas() {
        var cntxt = drawingLayer == '1' ? origcontext : topcontext
        cntxt.drawImage(canvas, 0, 0);
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.beginPath();
    }

    function erase(x, y) {
        var ew = 10
        var ep = ew / 2
        origcontext.clearRect(x - ep, y - ep, ew, ew)
        topcontext.clearRect(x - ep, y - ep, ew, ew)
        // alert(origcontext.clearRect)
    }

    function drawLine() {
        context.lineTo(x, y)
        context.stroke();
    }

    function drawRect(x, y, w, h, color) {
        if (color != undefined) {
            context.strokeStyle = color;
        }
        context.strokeRect(x, y, w, h);
    }

    function drawOval(x, y, w, h, color) {
        if (color != undefined) {
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
            if (graphicData.id == 1 && graphicData.dataArr.length > 500) {
                var jStr = convertObjToString(graphicData);
                currentObj.tempData = convertStringToObj(jStr);
                // ExternalInterface.call("console.log","A")
                var ptC = graphicData.dataArr.length
                var segArr = []
                var buf
                var header = graphicData.dataArr.shift()
                var tarr = graphicData.dataArr
                var segData
                var nxtStart
                var nx0
                var ny0
                var pt = {
                    x: header.x,
                    y: header.y
                }
                var nname = header.name
                // ExternalInterface.call("console.log","B")
                var segC = 0;
                var nheader;
                while (ptC > 0) {
                    segC++;
                    buf = Math.min(500, ptC);
                    ptC = ptC - buf;
                    segData = tarr.splice(0, buf);
                    var ngdata = {}
                    ngdata.lineColor = graphicData.lineColor;
                    ngdata.id = graphicData.id;

                    if (segC > 1) {
                        var sObj = {};
                        sObj.id = 'move';
                        sObj.x = pt.x;
                        sObj.y = pt.y;
                        segData.unshift(sObj);
                    }
                    nheader = cloneObject(header);
                    nheader.name = nname;
                    segData.unshift(nheader);
                    ngdata.dataArr = segData;
                    segArr.push(ngdata);
                    nxtStart = segData[segData.length - 1];
                    pt = {
                        x: nxtStart.x,
                        y: nxtStart.y
                    }
                    var n = header.name.split("_");
                    nname = n[0] + "_" + (Number(n[1]) + 1);
                }
                for (var z = 0; z < segArr.length; z++) {
                    sendDataToSERVER(segArr[z]);
                }
                render = false;
                resetArrays();
                textRendering = false;
                return;
            }
            render = false;
            // var jsonStr = convertObjToString(graphicData);
            sendDataToSERVER(graphicData);
            textRendering = false;
        }
        resetArrays();
    }

    function sendDataToSERVER(jsdata) {
        var jsonStr = convertObjToString(jsdata);
        // console.log("Sending json string: " + jsonStr);
        wb.whiteboardOut(jsonStr, true);
    }

    function cloneObject(obj) {
        var clone = {}
        for (var m in obj) {
            clone[m] = obj[m]
        }
        return clone
    }

    function resetArrays() {
        graphicData.dataArr = null;
        graphicData = {};
    }

    function getToolFromID(id) {
        for (var m in tool_id) {
            if (id == tool_id[m]) {
                return m
            }
        }
    }
    // function that converts flash object to JSON string

    function convertObjToString(obj) {
        try {
            var s = JSON.stringify(obj);
            return s;
        } catch (ex) {
            console.log(ex.name + ":" + ex.message + ":" + ex.location + ":" + ex.text);
        }
    }
    // function that converts JSON string to flash object

    function convertStringToObj(str) {
        try {
            var o = eval("(" + str + ")"); // eval(str);
            return o;
        } catch (ex) {
            console.log(ex.name + ":" + ex.message + ":" + ex.location + ":" + ex.text);
        }
    }
    // ### RENDER OBJECT TO WHITEBOARD

    function renderObj(obj) {
        var graphic_id = obj.id;
        var graphic_data = obj.dataArr;
        var line_rgb = obj.lineColor;
        var dLength = graphic_data.length;
        var dep, x0, y0, x1, y1;
        var textF;
        var idName;
        drawingLayer = graphic_data[0].layer ? graphic_data[0].layer : drawingLayer;
        context.lineWidth = 2.0
        context.strokeStyle = "rgb(0, 0, 0)";
        var deb = ""
        if (graphic_id === 0) {
            for (var i = 0; i < dLength; i++) {

                x1 = graphic_data[i].x;
                y1 = graphic_data[i].y;
                deb += x1 + ":" + y1 + "||"
                erase(x1, y1)


            }
        }
        // alert(deb)
        if (graphic_id === 3 || graphic_id === 1) {
            for (i = 0; i < dLength; i++) {
                x1 = graphic_data[i].x;
                y1 = graphic_data[i].y;
                if (graphic_data[i].id == "move") {
                    context.beginPath();
                    context.moveTo(x1, y1);
                    x0 = x1
                    y0 = y1
                } else {
                    context.lineTo(x0 + x1, y0 + y1);
                }
            }
            context.stroke()
            updateCanvas()
        }
        if (graphic_id === 2) {
            for (i = 0; i < dLength; i++) {

                if (graphic_data[i].text != "" || graphic_data[i].text != undefined) {
                    x0 = graphic_data[i].x;
                    y0 = graphic_data[i].y;
                    // context.fillText(graphic_data[i].text, x0, y0);
                    renderText(xt, x0, y0)
                }
            }
            updateCanvas()
        }
        if (graphic_id === 4 || graphic_id === 5) {
            var fName = graphic_id == 4 ? drawRect : drawOval;
            for (i = 0; i < dLength; i++) {
                var xd = graphic_data[i].xs < 0 ? -1 : 1
                var yd = graphic_data[i].ys < 0 ? -1 : 1
                x0 = graphic_data[i].x
                y0 = graphic_data[i].y
                w0 = graphic_data[i].w * xd
                h0 = graphic_data[i].h * yd
                fName(x0, y0, w0, h0);
            }
            updateCanvas()
        }
        if (graphic_id === 11 || graphic_id === 12) {
            idName = graphic_id == 11 ? "gr2D" : "nL";
            showHideGraph(idName, graphic_data[0].x, graphic_data[0].y,graphic_data[0].addImage);
        }
    }
    updateWhiteboard = function (cmdArray) {
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
        // updateScroller();
    }

    /** Map GWT array type to JS Array.
     *
     *  TODO: not sure why this is needed, otherwise
     *        instanceof Array seems to fail.
     *
     *        cmdArray is already an array in JSNI.
     *
     * @param cmdArray
     */
    function gwt_updatewhiteboard(cmdArray) {
        var realArray = [];
        for (var i = 0, t = cmdArray.length; i < t; i++) {
            var ele = [];
            ele[0] = cmdArray[i][0];
            ele[1] = cmdArray[i][1];
            realArray[i] = ele;
        }
        updateWhiteboard(realArray);
    }

    wb.updateWhiteboard = function (cmdArray) {
        gwt_updatewhiteboard(cmdArray);
    }

    // function receives jsonData and renders it to the screen
    draw = function (json_str) {
        var grobj = convertStringToObj(json_str);
        renderObj(grobj);
    }

    function colorToNumber(c) {
        var n = c.split('#').join('0x');
        return Number(n);
    }

    function clear(boo) {
        if (!boo) {
            resetWhiteBoard(false)
        }
        
        wb.whiteboardOut("clear", false);
    }
    
    
    /** will be overriden in GWT/parent */
    wb.saveWhiteboard = function() {
    	alert('default whiteboard save');
    }

    /** API method used to externalize handling of JSON data
     *
     * @param data
     * @param boo
     */

    wb.whiteboardOut = function(data, boo) {
        alert('default whiteboard out: ' + data);
        // console.log(data);
        /*
         * if(boo){ var flashObject=getFlashMovie('sw_wb');
         * data=data.split("\\").join("") data=data.substring(1,data.length-1);
         * flashObject.updateWhiteboard([["draw",[data]]]);
         * //renderFlashWhiteBoard(data); }else{ renderJSWhiteBoard(data) }
         */
    }

    wb.disconnectWhiteboard = function(documentObject) {
    	alert('default whiteboard disconnect');
        /** empty */
    }

    return wb;
}());
