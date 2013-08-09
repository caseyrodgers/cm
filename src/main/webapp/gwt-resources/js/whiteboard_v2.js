/**
 * Define Whiteboard API:
 *
 * initWhitegboard(document); // initialze whiteboard and prepare for new use
 * updateWhiteboard(cmdArray); // write to the whiteboard, sending command and
 * JSON whiteboardOut(data); // changes to whiteboard and published here.
 * saveWhiteboard(); // called when user/system requests persisting whitebroard
 * data.
 *
 * NOTE: detachWhiteboard not needed.
 */
if (typeof console == "undefined") {
    console = {
        log: function (x) {
            // empty
        }
    };
}
var console_log = function (txt) {
    console.log(txt)
}
var Whiteboard = function (cont, isStatic) {
    var wb = this;
    var contDiv = cont;
    var canvas, context, pencil_btn, rect_btn, width, height, x, y, clickX, clickY, penDown = false;
    var origcanvas, origcontext, currentTool = 'pencil';
    var graphcanvas, graphcontext, topcanvas, topcontext, gr2D, nL, graphMode, gr2D_xp, gr2D_yp, nL_xp, nL_yp;
    var offX, offY, x0, y0, w0, h0, drawingLayer, drawcolor, rendering;
    var graphicData, tool_id;
    var scope = wb;
    var isTouchEnabled = false;
    var screen_width;
    var screen_height;
    var mq_holder = new Image();
    var lastTxt = null;
    var scrollInt = null;
    var swipe_mx, swipe_my, swipe_nx, swipe_ny, swipe_ox, swipe_oy, swipe_dx, swipe_dy, swipe_sx, swipe_sy;
    swipe_ox = swipe_oy = 0;
    var swipe_action = 'off';
    var IS_IPAD = navigator.userAgent.match(/iPad/i) != null;
    var IS_IE8 = navigator.userAgent.match(/MSIE 8.0/i) != null;
    var IS_IE9 = navigator.userAgent.match(/MSIE 9.0/i) != null;
    var IS_IE = IS_IE8 || IS_IE9;
    var IS_ANDROID = navigator.userAgent.match(/Android/i) != null;
    var IS_KINDLE = navigator.userAgent.match(/Kindle/i) != null || navigator.userAgent.match(/Silk/i) != null;
    var IS_IPHONE = navigator.userAgent.match(/iPhone/i) != null;
    var IS_OPERA = navigator.userAgent.match(/Opera/i) != null;
    var IS_TOUCH_ONLY = IS_IPAD || IS_ANDROID || IS_KINDLE || IS_IPHONE
    var IS_IOS = IS_IPAD || IS_IPHONE
    //boolean whether calculator is enabled for this whiteboard
    var enable_calc = true;
    var isReadOnly = isStatic ? isStatic : false;
    var lastGesture = null;
    //
    var selectionMode = false;
    var selectionDragMode = false;
    var graphicDataStore = [];
    var selectedObj = null;
    var selectedObjIndex = -1;
    var lineBound = {};
    var useMQ = false;
    //
    var toolArr = [{
        name: 'button_text',
        title: 'Text',
        classes: 'big_tool_button button_text',
        text: ""
    }, {
        name: 'button_pencil',
        title: 'Draw or Write',
        classes: 'big_tool_button button_pencil',
        text: ""
    }, {
        name: 'button_line',
        title: 'Lines',
        classes: 'big_tool_button button_line',
        text: ""
    }, {
        name: 'button_rect',
        title: 'Rectangles',
        classes: 'big_tool_button button_rect',
        text: ""
    }, {
        name: 'button_oval',
        title: 'Circles/Ellipses',
        classes: 'big_tool_button button_oval',
        text: ""
    }, {
        name: 'button_eraser',
        title: 'Erase',
        classes: 'big_tool_button button_eraser',
        text: ""
    }, {
        name: 'button_gr2D',
        title: '2D Graph',
        classes: 'big_tool_button button_gr2D',
        text: ""
    }, {
        name: 'button_nL',
        title: 'Number line',
        classes: 'big_tool_button button_nL',
        text: ""
    }, {
        name: 'button_move',
        title: 'Move',
        classes: 'big_tool_button button_move',
        text: ""
    }, {
        name: 'button_clear',
        title: 'Clear Whiteboard',
        classes: 'big_tool_button button_clear',
        text: "Clear"
    }, {
        name: 'button_undo',
        title: 'Undo',
        classes: 'big_tool_button button_undo',
        text: "Undo"
    }]
    //
	/**
	 * methods to create whiteboard gui
	 */

        function createToolBtn(obj) {
            var btn = $('<button/>', {
                title: obj.title,
                name: obj.name,
                text: obj.text
            }).addClass(obj.classes);
            return btn;
        }

        function buildGUI() {
            var parentDiv = $("#" + contDiv)
            var wbc = $('<div></div>').attr('name', 'wb-container').addClass("wb-container").appendTo(parentDiv);
            var toolCont = buildTools(toolArr).appendTo(wbc);
            var canvasCont = buildCanvasLayers().appendTo(wbc);
            //buildInputTextBox().appendTo(canvasCont);
            var vScroll = buildScrollBar('v').appendTo(wbc);
            var hScroll = buildScrollBar('h').appendTo(wbc)
        }

        function buildTools(arr) {
            var divObj = $("<div/>", {
                name: 'tools'
            }).addClass('tools');
            var tool
            for (var k = 0; k < arr.length; k++) {
                tool = createToolBtn(arr[k])
                tool.appendTo(divObj);
            }
            return divObj
        }

        function buildCanvasLayers() {
            var divObj = $("<div/>", {
                name: 'drawsection'
            }).addClass('drawsection');
            var canvasCont = $("<div/>", {
                name: 'canvas-container',
                width: 800,
                height: 600
            }).addClass('canvas-container');
            $("<canvas/>", {
                name: 'ocanvas',
                text: "(Your browser doesn't support canvas)"
            }).addClass('ocanvas').appendTo(canvasCont);
            $("<canvas/>", {
                name: 'gcanvas'
            }).addClass('gcanvas').appendTo(canvasCont);
            $("<canvas/>", {
                name: 'tcanvas'
            }).addClass('tcanvas').appendTo(canvasCont);
            $("<canvas/>", {
                name: 'canvas'
            }).addClass('canvas').appendTo(canvasCont);
            var itxt = $("<div/>", {
                name: 'inputBox'
            }).addClass('inputBox');
            var mqbox = $('<div><span class="mathquill-editable" id="editable-math"></span></div>');
            var dbtn = $("<button/>", {
                name: 'done_btn',
                text: 'DONE',
                type: 'button'
            }).addClass('done_btn');
            mqbox.appendTo(itxt);
            dbtn.appendTo(itxt);
            itxt.appendTo(canvasCont);
            canvasCont.appendTo(divObj);
            return divObj
        }

        function buildInputTextBox() {
            var divObj
            return divObj
        }

        function buildScrollBar(dir) {
            var divObj
            if (dir == 'v') {
                divObj = $("<div name='vscroller' class='vscroller'><div name='vscroll_track' class='scroll_track vscroll_track'></div><div name='vscroll_thumb' class='scroll_thumb vscroll_thumb'></div></div>");
            } else {
                divObj = $("<div name='hscroller' class='hscroller'><div name='hscroll_track' class='scroll_track hscroll_track'></div><div name='hscroll_thumb' class='scroll_thumb hscroll_thumb'></div></div>");
            }
            return divObj
        }
		//

        function drawBoundRect(obj) {
            var dx = x - clickX
            var dy = y - clickY
            context.save();
            context.lineWidth = 4;
            context.strokeStyle = "rgba(0, 0, 255, 0.5)";
            context.strokeRect(obj.brect.xmin - 2 + dx, obj.brect.ymin - 2 + dy, obj.brect.w + 6, obj.brect.h + 6)
            context.restore()
        }

        function removeBoundRect() {
            context.clearRect(0, 0, canvas.width, canvas.height);
        }

        function drawTempObj(selectedObj, dx, dy) {
            //console.log("DRAW_TEMP_OBJ")
            //console.log(selectedObj)
            context.clearRect(0, 0, canvas.width, canvas.height);
            if (selectedObj.id === 2 && useMQ) {
                context.drawImage(selectedObj.imageData, selectedObj.brect.xmin + dx, selectedObj.brect.ymin + dy)
            } else {
                try {
                    context.putImageData(selectedObj.imageData, selectedObj.brect.xmin + dx, selectedObj.brect.ymin + dy)
                } catch (ex) {
                    alert(ex)
                }
            }
        }

        function transformObj(obj, dx, dy) {
            var da = obj.dataArr
            var sobj = da[0]
            sobj.x = sobj.x + dx
            sobj.y = sobj.y + dy
            obj.dataArr[0] = sobj;
            da = obj.brect;
            for (var m in da) {
                if (m == 'x' || m == 'xmin' || m == 'xmax') {
                    obj.brect[m] = obj.brect[m] + dx
                }
                if (m == 'y' || m == 'ymin' || m == 'ymax') {
                    obj.brect[m] = obj.brect[m] + dy
                }
            }
            return obj

        }

        function findSelectedObjIndex(xp, yp) {
            var l = graphicDataStore.length
            for (var i = l - 1; i >= 0; i--) {
                var rect = graphicDataStore[i].brect
                selectedObj = null
                if (!rect) {
                    return -1
                }
                if (contains(rect, xp, yp)) {
                    selectedObj = graphicDataStore[i]
                    return i
                }
            }
            return -1
        }

        function contains(rect, xp, yp) {
            if (xp >= rect.xmin - 1 && xp <= rect.xmax + 1 && yp >= rect.ymin - 1 && yp <= rect.ymax + 1) {
                return true
            }
            return false
        }

        function intersectRect(r1, r2) {
            return !(r2.xmin > r1.xmax ||
                r2.xmax < r1.xmin ||
                r2.ymin > r1.ymax ||
                r2.ymax < r1.ymin);
        }

        function checkForObjectErase(r) {
            var l = graphicDataStore.length
            for (var i = 0; i < l; i++) {
                var obj = graphicDataStore[i]

                if (!obj.isErased) {
                    var r1 = obj.brect
                    obj.isErased = intersectRect(r1, r)
                }
            }
            //return -1
        }
        //
    mq_holder.onload = function () {

        context.drawImage(this, holder_x, holder_y);
        // alert(this.width+":"+this.height+":"+holder_x+":"+holder_y);
        updateCanvas();
    }
    //

    function getInternetExplorerVersion()
    // Returns the version of Internet Explorer or a -1
    // (indicating the use of another browser).
    {
        var rv = -1; // Return value assumes failure.
        if (navigator.appName == 'Microsoft Internet Explorer') {
            var ua = navigator.userAgent;
            var re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
            if (re.exec(ua) != null) rv = parseFloat(RegExp.$1);
        }
        return rv;
    }
    //
    /**
     *internal methods to: disable,enable calculator and show or hide them
     */

    function showCalc() {
        console_log("SHOW_CALC")
        if ($('#calc_hold').html() || !enable_calc) {} else {
            var calc_x = screen_width - 325
            var calc_y = $get_Element("#tools").offsetTop + $get_Element("#tools").offsetHeight;
            var ch = '<div id="calc_hold" style="width:325px;position:absolute;left:' + calc_x + 'px;top:' + calc_y + 'px"></div>'
            var calc_hold = $('#main-content').append($(ch))
            console_log("SHOW_HIDE_CALC-s2")
            $('#calc_hold').calculator({
                layout: $.calculator.scientificLayout
            });
            $get_Element("#button_calc").style.border = '2px solid #ff0000';
        }
    }

    function hideCalc() {
        if ($('#calc_hold').html()) {
            console_log("HIDE_CALC")
            $('#calc_hold').remove();
            $get_Element("#button_calc").style.border = '1px solid #000000';
        }
    }

    function _enableCalc() {
        $get_jqElement('#button_calc').css('background-image', 'url(' + _imageBaseDir + 'calculator.png)');
    }

    function _disableCalc() {
        hideCalc()
        $get_jqElement('#button_calc').css('background-image', 'url(' + _imageBaseDir + 'calculator_no.png)');
        console_log($get_jqElement('#button_calc').css('backgroundImage'))
    }

    function showHideCalc() {
        console_log("SHOW_HIDE_CALC")
        if ($('#calc_hold').html()) {
            console_log("SHOW_HIDE_CALC-s0")
            hideCalc()
        } else {
            console_log("SHOW_HIDE_CALC-s1")
            showCalc()
        }
    }

    function mouseOverCalc(event) {
        if (!$('#calc_hold').html()) {
            return false;
        }
        getCanvasPos();
        var mx = event.layerX ? event.layerX : event.pageX - offX;
        var my = event.layerY ? event.layerY : event.pageY - offY;
        var box = $get_jqElement('#canvas-container').position();
        var xp, yp, wi, hi
            xp = screen_width - 325 - box.left
            yp = 0 - box.top
            wi = 325
            hi = $get_jqElement('#calc_hold').height()
            console_log(xp + ":" + yp + ":" + wi + ":" + hi + ":" + mx + ":" + my + ":" + event.layerX + ":" + event.pageX)
            if ((mx >= xp && mx <= xp + wi) && (my >= yp && my <= yp + hi)) {
                return true;
            }
        return false;
    }
    //end of calc internal methods
    //
    var determineFontHeight = function (fontStyle) {
        var body = document.getElementsByTagName("body")[0];
        var dummy = document.createElement("div");
        var dummyText = document.createTextNode("M");
        dummy.appendChild(dummyText);
        dummy.setAttribute("style", fontStyle);
        body.appendChild(dummy);
        var result = dummy.offsetHeight;
        body.removeChild(dummy);
        return result;
    };

    function renderText_html(xt, xp, yp, col, boo) {

        var txt = xt ? xt : $get_Element("#content").value;
        // alert(txt);

        var str = txt.split("\n")
        var x0 = xp ? xp : clickX
        var y0 = yp ? yp : clickY
        var ht = determineFontHeight(str[0]);
        var sy = y0
        context.font = "20pt Arial";
        context.textBaseline = 'top';
        var colr = col ? col : wb.globalStrokeColor;
        context.fillStyle = colr;
        for (var i = 0; i < str.length; i++) {
            context.fillText(str[i], x0, y0)
            y0 += ht + ht / 3
        }
        if (!boo) {
            updateCanvas();
        }
        if (!xt) {
            var rect = {}
            rect.x = rect.xmin = x0
            rect.y = rect.ymin = sy
            rect.w = context.measureText(txt).width
            rect.h = (ht + ht / 3) * str.length
            rect.xmax = rect.x + rect.w;
            rect.ymax = rect.y + rect.h;
            console.log('renderText')
            // console.log(this)
            var gd = graphicData
            gd.brect = rect

            //context.drawImage(this, holder_x, holder_y);
            // alert(this.width+":"+this.height+":"+holder_x+":"+holder_y);
            gd.imageData = context.getImageData(rect.xmin - 1, rect.ymin - 1, rect.w + 2, rect.h + 2)
            //graphicDataStore[graphicDataStore.length - 1] = gd
            updateText(txt, x0, sy, colorToNumber(colr));
            sendData();
            $get_Element("#content").value = "";
            $get_Element("#inputBox").style.display = 'none';
        }
        // alert($get_Element("#inputBox").style.display)
    }

    function renderText_mq(xt, xp, yp, col) {
        // var txt = xt ? xt : $get_Element("#editable-math").value;
        var txt = xt ? xt : $get_jqElement('#editable-math').mathquill('latex');

        // var str = txt.split("\n")
        var x0 = xp ? xp : clickX
        var y0 = yp ? yp : clickY
        var colr = col ? col : wb.globalStrokeColor
        var ht = 15;
        var holder_x = x0
        var holder_y = y0
        // mq_holder.src="http://latex.codecogs.com/png.latex?"+txt;

        if (false) {
            context.drawImage(mq_holder, holder_x, holder_y);
            // alert(this.width+":"+this.height+":"+holder_x+":"+holder_y);
            updateCanvas();
        } else {
            var _mq_holder = new Image();
            _mq_holder.onload = function () {
                var rect = {}
                rect.x = rect.xmin = x0
                rect.y = rect.ymin = y0
                rect.w = this.width
                rect.h = this.height
                rect.xmax = rect.x + rect.w;
                rect.ymax = rect.y + rect.h;
                console.log('renderText')
                console.log(this)
                var gd = graphicDataStore[graphicDataStore.length - 1];
                gd.brect = rect

                context.drawImage(this, holder_x, holder_y);
                // alert(this.width+":"+this.height+":"+holder_x+":"+holder_y);
                // gd.imageData = this;
                // graphicDataStore[graphicDataStore.length - 1] = gd
                //console.log(gd)
                updateCanvas();
                _mq_holder = null;
                delete _mq_holder;
            }
            // _mq_holder.src = "http://chart.apis.google.com/chart?cht=tx&chf=bg,s,ffffff00&chl=" + encodeURIComponent("\\fontsize{18} " + txt);
            var txtCol = String(colr).substring(1)
            _mq_holder.src = "http://chart.apis.google.com/chart?cht=tx&chf=bg,s,ffffff00&chco=" + txtCol + "&chl=" + encodeURIComponent("\\fontsize{18} " + txt);
            lastTxt = txt
        }


        if (!xt) {
            updateText(txt, x0, y0, colorToNumber(colr));
            console.log('AfterUpdateText')
            console.log(graphicData)
            sendData();
            // $get_Element("#editable-math").value = "";

            $get_jqElement('#editable-math').mathquill('latex', "");
            $get_Element("#inputBox").style.display = 'none';
        }

        // alert($get_Element("#inputBox").style.display)
    }

    function renderText(xt, xp, yp, col) {
        if (useMQ) {
            renderText_mq(xt, xp, yp, col)
        } else {
            renderText_html(xt, xp, yp, col)
        }
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
		$get_Element("#button_move").style.border = '1px solid #000000';
        //
    }

    function buttonHighlite(t) {
        resetButtonHighlite();
        $get_Element("#button_" + t).style.border = '2px solid #ff9900';
        if (currentTool != 'text' && $get_Element("#inputBox").style.display == 'block') {
            hideTextBox();
        }
		if(t!=='move'){
		selectionMode = false;
        removeBoundRect()
		}
    }


    var _docWidth = 0;
    var _docHeight = 0;
    var _viewPort = null;
    wb.setWhiteboardViewPort = function (width, height) {
        console_log("EXTERNAL CALL::setWhiteboardViewPort:: " + width + ":" + height)
        _viewPort = {
            width: width,
            height: height
        };
    }
    wb.resizeWhiteboard = function () {
        console_log("EXTERNAL CALL::resizeWhiteboard::")
        adjustToolbar()
    }

    function viewport_testpage() {
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

    function viewport() {
        var e = window,
            a = 'inner';

        if (!('innerWidth' in window)) {
            a = 'client';
            e = document.documentElement || document.body;
        }


        if (_viewPort == null) {
            alert('Whiteboard setup: _docWidth/_docHeight must be set by calling setWhiteboardViewPort(width, height)');
            _viewPort = viewport_testpage()
        }

        return _viewPort;

        // return {
        // width: e[a + 'Width'],
        // height: e[a + 'Height']
        // }
    }

    function getDocHeight() {
        var D = document;
        return Math.max(
            Math.max(D.body.scrollHeight, D.documentElement.scrollHeight), Math.max(D.body.offsetHeight, D.documentElement.offsetHeight), Math.max(D.body.clientHeight, D.documentElement.clientHeight));
    }



    /**
     * Define as functions to allow removing
     *
     * @param event
     */

    function isMultitouch_gesture(event) {
        var ev = event ? event : window.event;
        //console.log(event)
        //console.log(ev.type)
        isTouchEnabled = ev.type.indexOf('touch') > -1;
        var pinch_threshold = 0.01;
        if (isTouchEnabled) {
            var scal = Math.abs(1 - ev.scale);
            var isPinchZoom = scal > pinch_threshold;
            //console.log(ev.type+":"+ev.touches.length+":"+ev.scale+":"+scal+":"+isPinchZoom+"||"+ev.deltaTime+":"+ev.distance+":"+ev.timeStamp);

            if (isPinchZoom) {
                return true;
            }
        }
        return false;
    }

    function touchStartFunction(event) {


        var ev = event ? event : window.event;

        var boo = isMultitouch_gesture(event);
        //console.log('BOO:: '+boo)
        if (boo) {
            return true
        }

        if (ev.touches.length == 2) {} else {

            if (lastGesture && lastGesture.type == 'touchstart' && ev.touches.length === 1 && (ev.timeStamp - lastGesture.timeStamp) < 300) {
                lastGesture = null;
                //console.log("DOUBLE TAP")
                event.preventDefault();
            } else {
                if (ev.touches.length === 1) {} else {
                    event.preventDefault();
                }
            }
        }
        lastGesture = ev;
    }
    var touchMoveFunction = touchStartFunction;

    var _imageBaseDir = '/gwt-resources/images/whiteboard/';

    /** main HTML document object */
    var mainDoc;

    /** Determine if the internal/native canvas will be
     *  be used or excanvas.  IE9 supports canvas native.
     */
    var ieVer = getInternetExplorerVersion();
    var isIE = ieVer != -1 && ieVer < 9;
    console.log('iIE: ' + isIE + ', version: ' + ieVer);


    wb.initWhiteboard = function (mainDocIn) {
        console_log("WHITEBOARD_INITIATED! - document object:" + mainDocIn);
        if (context) {
            var _width = 0;
            origcanvas.width = graphcanvas.width = topcanvas.width = canvas.width = _width;
            origcanvas.height = graphcanvas.height = topcanvas.height = canvas.width = _width;
            if (isIE) {
                $(canvas).css({
                    'width': '0px',
                    'height': '0px'
                })
                $(canvas).empty();
                $(origcanvas).css({
                    'width': '0px',
                    'height': '0px'
                })
                $(origcanvas).empty();
                $(graphcanvas).css({
                    'width': '0px',
                    'height': '0px'
                })
                $(graphcanvas).empty();
                $(topcanvas).css({
                    'width': '0px',
                    'height': '0px'
                })
                $(topcanvas).empty();
            } else {
                canvas = null;
                origcanvas = null;
                graphcanvas = null;
                topcanvas = null;
                context = null;
                origcontext = null;
                graphcontext = null;
                topcontext = null;
            }
        }
        //setupMathQuill(); // defined in mathquill.js
        mainDoc = mainDocIn;
        buildGUI();
        //position calc button in toolbar
        if ($get_Element('#button_calc')) {
            $get_jqElement('#button_calc').css({
                'position': 'absolute',
                'right': '5px'
            });
        }
        if ($get_Element('#vscroller')) {
            if (IS_IOS) {
                $get_jqElement('#vscroller').css({
                    'display': 'none'

                });
                $get_jqElement('#hscroller').css({
                    'display': 'none'

                });
            }
        }
        //
        setTimeout(function () {

            canvas = $get_Element("#canvas");
            var siz = viewport()
            var docWidth = siz.width;
            var docHeight = siz.height;
            console_log("CANVAS_IN_IE: " + canvas + ":" + canvas.getContext);
            if (docWidth > 600) {
                // alert($get_jqElement('#tools button').css('width'));
                $get_jqElement('#tools').css('height', '35px');
                $("div#" + contDiv + " [name='tools'] button").removeClass('small_tool_button').addClass("big_tool_button");
				$get_jqElement('#button_clear').css('width', '45px');
                    $get_jqElement('#button_clear').css('height', '30px');
                $get_jqElement('#button_clear').text("Clear");
                $get_jqElement('#button_save').text("Save");
                $get_jqElement('#button_undo').text("Undo");
                // $get_jqElement('#button_clear').text("CL");
            } else {
                $get_jqElement('#tools').css('height', '28px');
                $("div#" + contDiv + " [name='tools'] button").removeClass('big_tool_button').addClass("small_tool_button");
				$get_jqElement('#button_clear').css('width', '25px');
                    $get_jqElement('#button_clear').css('height', '25px');
                $get_jqElement('#button_clear').text("CL");
                $get_jqElement('#button_save').text("S");
                $get_jqElement('#button_undo').text("U");

            }

            var off_left = $get_Element("#tools").offsetLeft;
            var off_top = $get_Element("#tools").offsetTop;
            var off_ht = $get_Element("#tools").offsetHeight;
            var topOff = off_ht + off_top + 15;
            var leftOff = off_left + 15;
            // var topOff = $get_Element("#tools").offsetHeight +
            // $get_Element("#tools").offsetTop + 15
            // var leftOff = $get_Element("#tools").offsetLeft + 15;
            var vscrollObj = {}
            var hscrollObj = {}
            wb.globalStrokeColor = "#000000";
            wb.mode = 'student';
            origcanvas = $get_Element("#ocanvas");
            graphcanvas = $get_Element("#gcanvas");
            topcanvas = $get_Element("#tcanvas");
            var vWidth = IS_IOS ? docWidth - leftOff : 2000;
            var vHeight = IS_IOS ? docHeight - topOff : 2620;
            canvas.width = vWidth;
            canvas.height = vHeight;
            origcanvas.width = vWidth;
            origcanvas.height = vHeight;
            graphcanvas.width = vWidth;
            graphcanvas.height = vHeight;
            topcanvas.width = vWidth;
            topcanvas.height = vHeight;
            var ccnt = $get_Element("#canvas-container");
            $get_jqElement("#canvas-container").css('width', vWidth + 'px');
            $get_jqElement("#canvas-container").css('height', vHeight + 'px');

            console_log('off_ht_1: ' + $get_Element("#tools").offsetHeight + ":" + $get_Element("#tools").offsetLeft + ":" + $get_Element("#tools").offsetTop)
            if (IS_IPHONE || docWidth <= 600) {
                dox = IS_IPHONE ? 5 : 19
                doy = IS_IPHONE ? 5 : 19
            } else {
                dox = 19
                doy = 19
            }
            if (IS_IOS) {
                dox = 15;
                doy = 0;
            }
            try {
                if (typeof G_vmlCanvasManager != "undefined") {
                    var parent_cont = $get_Element("#canvas-container")
                    console_log("DEBUG_IE parent_cont: " + parent_cont);
                    console_log("DEBUG_IE: parent_cont.removeChild" + parent_cont.removeChild);
                    parent_cont.removeChild(canvas)
                    parent_cont.removeChild(origcanvas)
                    parent_cont.removeChild(graphcanvas)
                    parent_cont.removeChild(topcanvas)
                    canvas = null;
                    origcanvas = null;
                    graphcanvas = null;
                    topcanvas = null;
                    context = null;
                    origcontext = null;
                    graphcontext = null;
                    topcontext = null;
                    //
                    canvas = document.createElement('canvas')
                    canvas.width = vWidth;
                    canvas.height = vHeight;
                    $(canvas).attr('class', 'canvas')
                    //
                    origcanvas = document.createElement('canvas')
                    origcanvas.width = vWidth;
                    origcanvas.height = vHeight;
                    $(origcanvas).attr('class', 'ocanvas')
                    //
                    graphcanvas = document.createElement('canvas')
                    graphcanvas.width = vWidth;
                    graphcanvas.height = vHeight;
                    $(graphcanvas).attr('class', 'gcanvas')
                    //
                    topcanvas = document.createElement('canvas')
                    topcanvas.width = vWidth;
                    topcanvas.height = vHeight;
                    $(topcanvas).attr('class', 'tcanvas')
                    //
                    $(parent_cont).prepend(origcanvas, graphcanvas, topcanvas, canvas);
                    G_vmlCanvasManager.initElement(canvas);
                    G_vmlCanvasManager.initElement(origcanvas);
                    G_vmlCanvasManager.initElement(graphcanvas);
                    G_vmlCanvasManager.initElement(topcanvas);
                }
            } catch (error) {
                console_log("DEBUG_IE:" + error);
            }
            screen_width = docWidth - leftOff - dox;
            screen_height = docHeight - topOff - doy;

            console_log('off_ht_2: ' + $get_Element("#tools").offsetHeight + ":" + $get_Element("#tools").style.height + ":" + $get_jqElement("#tools").height())
            $get_Element('#drawsection').style.width = (screen_width) + 'px';
            $get_Element('#drawsection').style.height = (screen_height) + 'px';
            if (!IS_IOS) {
                $get_Element('#vscroll_track').style.height = (screen_height) + 'px';
                $get_Element('#vscroller').style.left = (screen_width + 3 + off_left) + 'px';
                $get_Element('#vscroller').style.top = (off_ht + off_top) + 'px';

                $get_Element('#hscroll_track').style.width = (screen_width) + 'px';
                $get_Element('#hscroller').style.left = (off_left) + 'px';
                $get_Element('#hscroller').style.top = (off_ht + off_top + screen_height + 3) + 'px';
                var posData = "";
                posData += "Screen-Width:" + docWidth + "\n";
                posData += "Screen-Height:" + docHeight + "\n";
                posData += "wb-Width:" + screen_width + "\n";
                posData += "wb-Height:" + screen_height + "\n";
                posData += "wb-off-top:" + $get_Element("#tools").offsetTop + "\n";
                posData += "wb-off-height:" + $get_Element("#tools").offsetHeight + ":" + off_ht + "\n";
                posData += "vscroller-off-top:" + $get_Element('#vscroller').style.top + "\n";
                posData += "vscroller-off-left:" + $get_Element('#vscroller').style.left + "\n";
                posData += "hscroller-off-top:" + $get_Element('#hscroller').style.top + "\n";
                posData += "hscroller-off-left:" + $get_Element('#hscroller').style.left + "\n";
                console_log(posData);
            }
            var cmd_keys = {};
            var nav_keys = {};
            cmd_keys["frac"] = "/";
            cmd_keys["power"] = "^";
            cmd_keys["sqrt"] = "\\sqrt";
            cmd_keys["prod"] = "*";
            cmd_keys["div"] = "÷";
            cmd_keys["neq"] = "\\ne";
            nav_keys["Up"] = "Up";
            nav_keys["Down"] = "Down";
            nav_keys["Right"] = "Right";
            nav_keys["Left"] = "Left";
            nav_keys["Backspace"] = "Backspace";
            if (!IS_TOUCH_ONLY) {

                // $(".keypad").hide();
                // alert($(".keypad").is(":visible"))
            } else {

            }
            // alert($("button").on)

            function mathquill_focus() {
                $(".mathquill-editable").focus();
            }

            function navigate(key) {
                // var event = _event ? _event : window.event;
                // var target=event.target?event.target:event.srcElement
                // var key=$(target).attr("value");
                var h = $(".mathquill-editable").data("[[mathquill internal data]]").block
                h.keydown({
                    which: key,
                    shiftKey: 0
                })

            }

            $(".keypad").on("click", "button", function (event) {
                var key = $(this).attr("value")
                var h = $(".mathquill-editable");
                if (cmd_keys[key]) {
                    h.mathquill('cmd', cmd_keys[key]);
                } else if (nav_keys[key]) {
                    navigate(nav_keys[key])
                } else {
                    h.mathquill('write', $(this).text())
                }

                setTimeout(function () {
                    mathquill_focus()
                }, 100)
            });
            if (!IS_IOS) {
                if (document.addEventListener) {
                    var thumb_h = $get_Element('#hscroll_thumb');
                    thumb_h.addEventListener("mousedown", initThumbDrag, false)
                    thumb_h.addEventListener('touchstart', touchStartFunction, false);
                    thumb_h.addEventListener('touchmove', touchMoveFunction, false);
                    // attach the touchstart, touchmove, touchend event listeners.
                    thumb_h.addEventListener('touchstart', initThumbDrag, false);
                    //
                    var thumb_v = $get_Element('#vscroll_thumb');
                    thumb_v.addEventListener("mousedown", initThumbDrag, false)
                    thumb_v.addEventListener('touchstart', touchStartFunction, false);
                    thumb_v.addEventListener('touchmove', touchMoveFunction, false);
                    // attach the touchstart, touchmove, touchend event listeners.
                    thumb_v.addEventListener('touchstart', initThumbDrag, false);
                } else {
                    $get_Element('#hscroll_thumb').onmousedown = initThumbDrag;
                    $get_Element('#vscroll_thumb').onmousedown = initThumbDrag;
                }
            }

            function resize_wb() {
                console_log("INTERNAL CALL::WINDOW_RESIZE::")
                adjustToolbar()
            }
            //window.onresize=resize_wb;
            $(window).resize(resize_wb);

            adjustToolbar = function () {
                var siz = viewport()
                var docWidth = siz.width;
                var docHeight = siz.height;

                // if whiteboard is not active
                if ($get_Element('#tools') == null) {
                    return;
                }

                if (docWidth > 600) {
                    // alert($('#tools button').css('width'));
                    $get_jqElement('#tools').css('height', '35px');
                    $("div#" + contDiv + " [name='tools'] button").removeClass('small_tool_button').addClass("big_tool_button");
					$get_jqElement('#button_clear').css('width', '45px');
                    $get_jqElement('#button_clear').css('height', '30px');
                    $get_jqElement('#button_clear').text("Clear");
                    $get_jqElement('#button_save').text("Save");
                    $get_jqElement('#button_undo').text("Undo");
                } else {

                    $get_jqElement('#tools').css('height', '25px');
                    $("div#" + contDiv + " [name='tools'] button").removeClass('big_tool_button').addClass("small_tool_button");
					$get_jqElement('#button_clear').css('width', '25px');
                    $get_jqElement('#button_clear').css('height', '25px');
                    $get_jqElement('#button_clear').text("CL");
                    $get_jqElement('#button_save').text("S");
                    $get_jqElement('#button_undo').text("U");

                }
                // setTimeout(function(){
                var off_left = $get_Element("#tools").offsetLeft;
                var off_top = $get_Element("#tools").offsetTop;
                var off_ht = $get_Element("#tools").offsetHeight;
                var topOff = off_ht + off_top + 15
                var leftOff = off_left + 15;
                if (IS_IPHONE || docWidth <= 600) {
                    dox = IS_IPHONE ? 5 : 19
                    doy = IS_IPHONE ? 5 : 19
                } else {
                    dox = 19;
                    doy = 19;
                }
                // dox=doy=0;
                if (IS_IOS) {
                    dox = 15
                    doy = 0;
                }
                screen_width = docWidth - leftOff - dox;
                screen_height = docHeight - topOff - doy;
                console_log('off_ht_2: ' + $get_Element("#tools").offsetHeight + ":" + $get_Element("#tools").style.height + ":" + $get_jqElement("#tools").height())
                $get_Element('#drawsection').style.width = (screen_width) + 'px';
                $get_Element('#drawsection').style.height = (screen_height) + 'px';
                if (!IS_IOS) {
                    $get_Element('#vscroll_track').style.height = (screen_height) + 'px';
                    $get_Element('#vscroller').style.left = (screen_width + 3 + off_left) + 'px';
                    $get_Element('#vscroller').style.top = (off_ht + off_top) + 'px';

                    $get_Element('#hscroll_track').style.width = (screen_width) + 'px';
                    $get_Element('#hscroller').style.left = (off_left) + 'px';
                    $get_Element('#hscroller').style.top = (off_ht + off_top + screen_height + 3) + 'px';
                    var posData = "";
                    posData += "Screen-Width:" + docWidth + "\n";
                    posData += "Screen-Height:" + docHeight + "\n";
                    posData += "wb-Width:" + screen_width + "\n";
                    posData += "wb-Height:" + screen_height + "\n";
                    posData += "wb-off-top:" + $get_Element("#tools").offsetTop + "\n";
                    posData += "wb-off-height:" + $get_Element("#tools").offsetHeight + ":" + off_ht + "\n";
                    posData += "vscroller-off-top:" + $get_Element('#vscroller').style.top + "\n";
                    posData += "vscroller-off-left:" + $get_Element('#vscroller').style.left + "\n";
                    posData += "hscroller-off-top:" + $get_Element('#hscroller').style.top + "\n";
                    posData += "hscroller-off-left:" + $get_Element('#hscroller').style.left + "\n";
                    console_log(posData);
                    positionScroller();
                }
                // },100);
            }

            function initThumbDrag(_event) {
                // console.log("INIT_THUMB");
                var event = _event ? _event : window.event;
                isTouchEnabled = event.type.indexOf('touch') > -1
                if (isTouchEnabled) {
                    $get_Element('#hscroll_thumb').removeEventListener("mousedown", initThumbDrag, false);

                }

                event = isTouchEnabled ? _event.changedTouches[0] : event;
                if (event.preventDefault) {
                    event.preventDefault();
                } else {
                    event.returnValue = false;
                }
                getCanvasPos();
                var dx, dy;

                if (event.pageX != undefined) {
                    dx = event.pageX - offX;
                    dy = event.pageY - offY;
                } else {
                    dx = event.clientX - offX
                    dy = event.clientY - offY
                }
                var scroll = 'v';
                var pos = 'top';
                var scrollObj = vscrollObj;
                var mouse_pos = dy
                var dim = 'height';
                var sdim = screen_height;
                var target = event.target ? event.target : event.srcElement
                if (target == $get_Element('#hscroll_thumb')) {
                    scroll = 'h';
                    pos = 'left';
                    scrollObj = hscrollObj;
                    mouse_pos = dx;
                    dim = 'width';
                    sdim = screen_width;
                }
                var spos = $get_Element('#' + scroll + 'scroll_thumb').style[pos]
                spos = spos ? spos : 0
                var scpos = $get_Element('#canvas-container').style[pos]
                scpos = scpos ? scpos : 0
                scrollObj.sy = parseInt(spos)
                scrollObj.screeny = parseInt(scpos)
                scrollObj.my = mouse_pos;
                scrollObj.dragged = true;
                scrollObj.scrub = (canvas[dim] - sdim) / (sdim - 30)
                console_log("INIT_SCROLL_SCRUB:" + scrollObj.sy + ":" + scrollObj.my + ":" + scrollObj.scrub + ":" + event.target);
                if (document.addEventListener) {
                    if (isTouchEnabled) {
                        document.addEventListener("touchend", stopThumbDrag, false);
                        document.addEventListener("touchmove", startThumbDrag, false);
                        $get_Element('#' + scroll + 'scroll_thumb').addEventListener("touchend", stopThumbDrag, false);
                    } else {
                        document.addEventListener("mouseup", stopThumbDrag, false);
                        document.addEventListener("mousemove", startThumbDrag, false);
                        $get_Element('#' + scroll + 'scroll_thumb').addEventListener("mouseup", stopThumbDrag, false);
                    }
                } else {
                    document.onmousemove = startThumbDrag;
                    $get_Element('#' + scroll + 'scroll_thumb').onmouseup = stopThumbDrag;
                    document.onmouseup = stopThumbDrag;
                }
            }

            function startThumbDrag(_event) {

                if (!vscrollObj.dragged && !hscrollObj.dragged) {
                    return
                }
                var event = _event ? _event : window.event;
                event = isTouchEnabled ? event.changedTouches[0] : event;
                if (event.preventDefault) {
                    event.preventDefault();
                } else {
                    event.returnValue = false;
                }
                // getCanvasPos();
                var dx, dy;
                if (event.pageX != undefined) {
                    dx = event.pageX - offX;
                    dy = event.pageY - offY;
                } else {
                    dx = event.clientX - offX
                    dy = event.clientY - offY
                }
                var scroll = 'v';
                var pos = 'top';
                var scrollObj = vscrollObj;
                var mouse_pos = dy
                var dim = 'height';
                var sdim = screen_height;
                var neg = -1
                if (hscrollObj.dragged) {
                    scroll = 'h';
                    pos = 'left';
                    scrollObj = hscrollObj;
                    mouse_pos = dx;
                    dim = 'width';
                    sdim = screen_width;
                    neg = -1
                }
                var change = mouse_pos - scrollObj.my
                var newpos = scrollObj.sy + change
                newpos = newpos < 0 ? 0 : newpos
                newpos = newpos > sdim - 30 ? sdim - 30 : newpos;
                if (newpos >= 0 && newpos <= sdim - 30) {
                    var currPos = scrollObj.screeny - (change * scrollObj.scrub);
                    currPos = currPos > 0 ? 0 : currPos
                    currPos = currPos < neg * (canvas[dim] - sdim) ? -(canvas[dim] - sdim) : currPos;
                    // console.log("ON_SCROLL_SCRUB:"+scrollObj.sy+":"+change+":"+currPos);
                    $get_Element('#' + scroll + 'scroll_thumb').style[pos] = newpos + "px";
                    $get_Element('#canvas-container').style[pos] = currPos + "px";
                }

            }

            function stopThumbDrag(_event) {
                // console.log("STOP_SCROLL_SCRUB:");
                if (!vscrollObj.dragged && !hscrollObj.dragged) {
                    return
                }
                var event = _event ? _event : window.event;
                event = isTouchEnabled ? event.changedTouches[0] : event;
                if (event.preventDefault) {
                    event.preventDefault();
                } else {
                    event.returnValue = false;
                }
                // getCanvasPos();
                var dx, dy;
                if (event.pageX != undefined) {
                    dx = event.pageX - offX;
                    dy = event.pageY - offY;
                } else {
                    dx = event.clientX - offX;
                    dy = event.clientY - offY;
                }
                var scroll = 'v';
                var pos = 'top';
                var scrollObj = vscrollObj;
                var mouse_pos = dy
                var dim = 'height';
                var sdim = screen_height;
                var neg = -1
                if (hscrollObj.dragged) {
                    scroll = 'h';
                    pos = 'left';
                    scrollObj = hscrollObj;
                    mouse_pos = dx;
                    dim = 'width';
                    sdim = screen_width;
                    neg = -1
                }
                var change = mouse_pos - scrollObj.my
                var newpos = scrollObj.sy + change
                newpos = newpos < 0 ? 0 : newpos
                newpos = newpos > sdim - 30 ? sdim - 30 : newpos;
                if (newpos >= 0 && newpos <= sdim - 30) {
                    var currPos = scrollObj.screeny - (change * scrollObj.scrub);
                    currPos = currPos > 0 ? 0 : currPos
                    currPos = currPos < neg * (canvas[dim] - sdim) ? -(canvas[dim] - sdim) : currPos;
                    $get_Element('#' + scroll + 'scroll_thumb').style[pos] = newpos + "px";
                    $get_Element('#canvas-container').style[pos] = currPos + "px";
                }
                if (document.addEventListener) {
                    if (isTouchEnabled) {
                        document.removeEventListener("mousemove", startThumbDrag, false);
                        document.removeEventListener("touchmove", startThumbDrag, false);

                    } else {

                        document.removeEventListener("mousemove", startThumbDrag, false);

                    }
                } else {
                    document.onmousemove = null;
                }
                scrollObj.dragged = false;
                console_log("END_SCROLL_SCRUB:" + newpos + ":" + currPos);
            }
            // canvas.width = origcanvas.width = graphcanvas.width = topcanvas.width
            // = docWidth - leftOff;
            // canvas.height = origcanvas.height = graphcanvas.height =
            // topcanvas.height = docHeight - topOff;
            // console.log("A " + canvas.width + ":" + canvas.height + ":" +
            // docWidth + ":" + docHeight + ":" + leftOff + ":" + topOff);
            context = canvas.getContext("2d");
            origcontext = origcanvas.getContext("2d");
            graphcontext = graphcanvas.getContext("2d");
            topcontext = topcanvas.getContext("2d");
            // canvas.width=origcanvas.width=graphcanvas.width=topcanvas.width=5000;
            // canvas.height=origcanvas.height=graphcanvas.height=topcanvas.height=5000;
            width = screen_width; // canvas.width;
            height = screen_height; // canvas.height;
            context.font = origcontext.font = topcontext.font = "12px sans-serif";
            /*
             * context.save(); context.fillStyle='white'
             * context.fillRect(0,0,width,height) context.restore();
             */
            gr2D = new Image();
            gr2D.src = _imageBaseDir + 'gr2D.png';
            nL = new Image();
            nL.src = _imageBaseDir + 'nL.png';
            graphMode = '';
            gr2D_xp = nL_xp = (screen_width - 300) / 2;
            gr2D_yp = (screen_height - 300) / 2;
            nL_yp = (screen_height - 100) / 2;
            gr2D_w = 300;
            gr2D_h = 300;
            nL_w = 300;
            nL_h = 100;
            offX = $get_Element("#canvas-container").offsetLeft;
            offY = $get_Element("#canvas-container").offsetTop;
            // alert(offX+":"+offY);
            /*
             * var getCanvasPos = function(){ var obj =
             * $get_Element("#canvas-container"); var top = 0; var left = 0; while
             * (obj.tagName != "BODY") { top += obj.offsetTop; left +=
             * obj.offsetLeft;
             * console.log(obj.tagName+":"+obj.offsetParent+":"+top+":"+left);
             * if(obj.offsetParent === null) break; obj = obj.offsetParent; }
             * offX=left; offY=top; return { top: top, left: left }; };
             */

            function getCanvasPos() {
                console_log("getCanvasPos processing!");
                var box = canvas.getBoundingClientRect();
                console_log("canvas bound= top: " + box.top + " left:" + box.left);
                var body = mainDoc.body;
                var docElem = mainDoc.documentElement;
                var scrollTop = window.pageYOffset || docElem.scrollTop || body.scrollTop;
                var scrollLeft = window.pageXOffset || docElem.scrollLeft || body.scrollLeft;
                var clientTop = docElem.clientTop || body.clientTop || 0;
                var clientLeft = docElem.clientLeft || body.clientLeft || 0;
                console_log("offset_datas: scrollTop=" + scrollTop + " scrollLeft=" + scrollLeft + " clientTop=" + clientTop + " clientLeft=" + clientLeft);
                var top = box.top + scrollTop - clientTop;
                var left = box.left + scrollLeft - clientLeft;
                offX = Math.round(left);
                offY = Math.round(top);
                console_log("OFFSET: top=" + offY + " left=" + offX);
                return {
                    top: offY,
                    left: offX
                }
            }
            console_log("getCanvasPos calling!");
            getCanvasPos();
            console_log("getCanvasPos CALL END!");
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
            drawingLayer = '1';
            if (currentTool != 'pencil') {
                if (currentTool == 'text' || $get_Element("#inputBox").style.display == 'block') {
                    hideTextBox();
                }
                resetButtonHighlite();
                currentTool = 'pencil';
            }
            $get_Element("#button_pencil").style.border = '2px solid #ff9900';

            // Events
            // drawRect(0,0,width,height,'#ff0000');
            if ($get_Element("#button_calc")) {
                $get_Element("#button_calc").onclick = function (event) {
                    // $get_Element("#drawsection").style.cursor='crosshair';
                    if (enable_calc) {
                        showHideCalc();
                    }
                };
            }
            //
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
                hideTextBox();
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
            if ($get_Element("#button_move")) {
                $get_Element("#button_move").onclick = function (event) {
                    // $get_Element("#drawsection").style.cursor='url("imgs/eraser.png"),auto';
                    // resetWhiteBoard();
                    wb.setSelectionMode()
                    //currentTool = 'move'
                    if (selectionMode) {
                        buttonHighlite('move');
                    } else {
                        wb.removeSelectionMode();
                    }
                };
            }
            //
            $get_Element("#done_btn").onclick = function (event) {
                renderText();
                // check()
            }
            if ($get_Element("#button_undo")) {
                $get_Element("#button_undo").onclick = function (event) {
                    wb.whiteboardOut('undo', true);
                };
            }

            if ($get_Element("#button_save")) {
                $get_Element("#button_save").onclick = function (event) {
                    wb.saveWhiteboard();
                };
            }



            //

            function killMouseListeners() {

                if (document.addEventListener) {
                    canvas.removeEventListener("mousedown", ev_onmousedown, false);
                    canvas.removeEventListener("mouseup", ev_onmouseup, false);
                    canvas.removeEventListener("mousemove", ev_onmousemove, false);

                } else {
                    canvas.detachEvent("onmousedown", ev_onmousedown);
                    canvas.detachEvent("onmouseup", ev_onmouseup);
                    canvas.detachEvent("onmousemove", ev_onmousemove);
                }
            }

            function killTouchListeners() {
                if (document.addEventListener) {


                    // touchscreen specific - to prevent web page being scrolled
                    // while drawing
                    canvas.removeEventListener('touchstart', touchStartFunction, false);
                    canvas.removeEventListener('touchmove', touchMoveFunction, false);

                    // attach the touchstart, touchmove, touchend event listeners.
                    canvas.removeEventListener('touchstart', ev_onmousedown, false);
                    canvas.removeEventListener('touchmove', ev_onmousemove, false);
                    canvas.removeEventListener('touchend', ev_onmouseup, false);

                } else {
                    // touchscreen specific - to prevent web page being scrolled
                    // while drawing
                    canvas.detachEvent('touchstart', touchStartFunction);
                    canvas.detachEvent('touchmove', touchMoveFunction);

                    // attach the touchstart, touchmove, touchend event listeners.
                    canvas.detachEvent('touchstart', ev_onmousedown);
                    canvas.detachEvent('touchmove', ev_onmousemove);
                    canvas.detachEvent('touchend', ev_onmouseup);
                }
            }

            function __killListeners() {
                killMouseListeners();
                killTouchListeners();
            }
            //

            function positionScroller() {
                var scrubH = (canvas.width - screen_width) / (screen_width - 30);
                var scrubV = (canvas.height - screen_height) / (screen_height - 30);
                var currPosH = parseInt($get_Element('#canvas-container').style.left);
                currPosH = currPosH ? currPosH : 0
                currPosH = currPosH > 0 ? 0 : currPosH
                currPosH = currPosH < -(canvas.width - screen_width) ? -(canvas.width - screen_width) : currPosH;
                $get_Element('#hscroll_thumb').style.left = (-currPosH / scrubH) + "px";
                $get_Element('#canvas-container').style.left = currPosH + "px";
                var currPosV = parseInt($get_Element('#canvas-container').style.top);
                currPosV = currPosV ? currPosV : 0
                currPosV = currPosV > 0 ? 0 : currPosV
                currPosV = currPosV < -(canvas.height - screen_height) ? -(canvas.height - screen_height) : currPosV;
                $get_Element('#vscroll_thumb').style.top = (-currPosV / scrubV) + "px";
                $get_Element('#canvas-container').style.top = currPosV + "px";
                console_log("SCROLLER_THUMB_POS:" + scrubH + ":" + scrubV + ":" + (-currPosH) + ":" + (-currPosV))
            }

            function scrollTheCanvas(event) {
                checkForScroll(event);
                // setTimeout(function(){checkForScroll(event)},100)
            }

            function checkForScroll(_event) {
                // console.log("CHECK_FOR_SCROLL")
                var event = _event ? _event : window.event;
                isTouchEnabled = event.type.indexOf('touch') > -1;
                var dx, dy, dist;

                if (event.pageX != undefined) {
                    dx = event.pageX - offX;
                    dy = event.pageY - offY;
                } else {
                    dx = event.clientX - offX
                    dy = event.clientY - offY
                }
                if (penDown) {
                    return
                }

                var cposX = ($get_Element("#wb-container").style.left);
                var cposY = ($get_Element("#wb-container").style.top);

                cposX = cposX ? parseInt(cposX) : 0;
                cposY = cposY ? parseInt(cposY) : 0;
                // console.log((dx-cposX)+":"+screen_width+"||"+(dy-cposY)+":"+screen_height)
                if (dx - cposX >= screen_width) {
                    doRightScroll();
                    setTimeout(function () {
                        checkForScroll(_event)
                    }, 100)
                } else if (dy - cposY >= screen_height) {
                    doUpScroll();
                    setTimeout(function () {
                        checkForScroll(_event)
                    }, 100)
                } else {
                    // clearInterval(scrollInt);
                }
            }

            function doRightScroll() {
                var delta = -2
                var currPos = $get_Element('#canvas-container').style.left;
                currPos = currPos ? currPos : 0;
                currPos = parseInt(currPos) + (delta * 10);
                currPos = currPos > 0 ? 0 : currPos
                currPos = currPos < -(canvas.width - screen_width) ? -(canvas.width - screen_width) : currPos;
                var scrub = (canvas.width - screen_width) / (screen_width - 30)
                $get_Element('#canvas-container').style.left = currPos + "px";
                $get_Element('#hscroll_thumb').style.left = (-currPos / scrub) + "px";
            }

            function doUpScroll() {
                var delta = -2
                var currPos = $get_Element('#canvas-container').style.top;
                currPos = currPos ? currPos : 0;
                currPos = parseInt(currPos) + (delta * 10);
                currPos = currPos > 0 ? 0 : currPos
                currPos = currPos < -(canvas.height - screen_height) ? -(canvas.height - screen_height) : currPos;
                var scrub = (canvas.height - screen_height) / (screen_height - 30)
                $get_Element('#canvas-container').style.top = currPos + "px";
                $get_Element('#vscroll_thumb').style.top = (-currPos / scrub) + "px";
            }
            //
            var ev_onmousedown = function (_event) {
                // alert("MDOWN")
                if (isReadOnly) {
                    return
                }
                var event = _event ? _event : window.event;
                var tevent = event
                isTouchEnabled = event.type.indexOf('touch') > -1
                if (isTouchEnabled) {
                    canvas.removeEventListener("mousedown", ev_onmousedown, false);
                    canvas.removeEventListener("mouseup", ev_onmouseup, false);
                    canvas.removeEventListener("mousemove", ev_onmousemove, false);
                }
                getCanvasPos();
                /*
                 * else{ canvas.removeEventListener('touchstart',ev_onmousedown,
                 * false); canvas.removeEventListener('touchmove',ev_onmousemove,
                 * false); canvas.removeEventListener('touchend',ev_onmouseup,
                 * false); }
                 */


                event = isTouchEnabled ? _event.changedTouches[0] : event;

                var dx, dy, dist;

                if (event.pageX != undefined) {
                    dx = event.pageX - offX;
                    dy = event.pageY - offY;
                } else {
                    dx = event.clientX - offX
                    dy = event.clientY - offY
                }
                // alert(dx+":"+canvas.width)
                // console.log(dy + ":" + event.clientY + ":" + event.layerY + ":" +
                // event.pageY + ":" + offY);
                context.lineWidth = 2.0;
                context.strokeStyle = wb.globalStrokeColor;

                var currPos = $get_Element('#canvas-container').style.left;
                currPos = currPos ? parseInt(currPos) : 0;
                var click_pos = dx + currPos
                console_log("MOUSE_DOWN " + dx + ":" + width + ":::" + click_pos + ":" + screen_width)

                if (mouseOverCalc(event)) {
                    penDown = false;
                    return
                } else if (click_pos >= 0 && click_pos < screen_width) {
                    hideCalc()
                    if (isTouchEnabled) {
                        if (tevent.touches.length > 1) {
                            penDown = false;
                            rendering = false;
                            if (!IS_IOS) {
                                initSwipe(_event);
                            }
                            return
                        }
                    }
                    penDown = true;
                    rendering = false;
                    clickX = dx;
                    clickY = dy;
                    x = dx;
                    y = dy;
                    lineBound = {}
                    if (selectionMode) {
                        var i = findSelectedObjIndex(x, y);
                        selectedObjIndex = i
                        console.log(i)
                        console.log(selectedObj)
                        if (i > -1) {
                            if (selectedObj.isErased) {
                                penDown = !true
                                wb.removeSelectionMode();
                                alert("Sorry! Erased objects cannot be moved!!")
                                return
                            }
                            //remove selected object from data array and store it temp variable
                            //rerender object on drawing canvas...
                            selectionDragMode = true;
                            graphicDataStore.splice(i, 1);
                            resetWhiteBoard(false);
                            //updateWhiteboard(graphicDataStore);
                            for (var j = 0; j < graphicDataStore.length; j++) {
                                renderObj(graphicDataStore[j], true);
                            }
                            drawTempObj(selectedObj, 0, 0);
                            drawBoundRect(selectedObj)
                            penDown = true
                        } else {
                            penDown = !true
                            //alert(wb.removeSelectionMode);
                            wb.removeSelectionMode();
                        }


                        return
                    }
                    if (!graphicData.dataArr) {
                        graphicData.dataArr = [];

                    }
                    graphicData.id = tool_id[currentTool];
                    console_log("CURRENT_TOOL:" + currentTool);
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
                        if (isIE && (currentTool == 'gr2D' || currentTool == 'nl')) {
                            context.beginPath();
                            context.moveTo(clickX, clickY);
                        }
                        lineBound.ymax = y;
                        lineBound.ymin = y;
                        lineBound.xmax = x;
                        lineBound.xmin = x;
                    }
                } else {
                    penDown = false;
                }
                if (event.preventDefault) {
                    event.preventDefault()
                } else {
                    event.returnValue = false
                };
                // _event.stopPropagation();
            };

            var ev_onmouseup = function (_event) {
                if (isReadOnly) {
                    return
                }
                if (selectionMode) {
                    if (selectedObj) {
                        var dx = x - clickX;
                        var dy = y - clickY;
                        selectionDragMode = false;
                        removeBoundRect();
                        drawTempObj(selectedObj, dx, dy);
                        updateCanvas();
                        drawBoundRect(selectedObj);
                        transformObj(selectedObj, dx, dy);
                        //graphicDataStore.push(cloneObject(selectedObj))
                        updateDataToSERVER(selectedObjIndex, selectedObj)
                    }
                    penDown = false;
                    rendering = false;
                    return
                }
                var event = _event ? _event : window.event;
                event = event.type.indexOf('touch') > -1 ? _event.targetTouches[0] : event;
                /*
                 * if(penDown){ x = event.layerX?event.layerX:event.pageX-offX; y =
                 * event.layerY?event.layerY:event.pageY-offY; }
                 */
                penDown = false;
                // alert(rendering);
                if (swipe_action == 'on') {
                    swipe_action = 'off'
                    return
                }
                if (rendering) {

                    if (currentTool == 'rect' || currentTool == 'oval') {
                        graphicData.dataArr[0].w = w0
                        graphicData.dataArr[0].h = h0
                        graphicData.dataArr[0].xs = w0 / 400
                        graphicData.dataArr[0].ys = h0 / 400
                        rect = getBoundRect(graphicData.dataArr[0].x, graphicData.dataArr[0].y, w0, h0)
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
                        if (currentTool != 'eraser') {
                            /*rect.x = graphicData.dataArr[0].x
                            rect.y = graphicData.dataArr[0].y
                            rect.w = xp
                            rect.h = yp
                            rect.xmin = rect.w >= 0 ? rect.x : rect.x + rect.w;
                            rect.xmax = rect.w >= 0 ? rect.x + rect.w : rect.x;
                            rect.ymin = rect.h >= 0 ? rect.y : rect.y + rect.h;
                            rect.ymax = rect.h >= 0 ? rect.y + rect.h : rect.y;
                            rect.w = Math.abs(xp)
                            rect.h = Math.abs(yp)*/
                            rect = getBoundRect(graphicData.dataArr[0].x, graphicData.dataArr[0].y, xp, yp)
                            if (currentTool == 'pencil') {
                                rect.x = lineBound.xmin ? lineBound.xmin : 0
                                rect.y = lineBound.ymin ? lineBound.ymin : 0
                                rect.w = lineBound.xmax - lineBound.xmin
                                rect.h = lineBound.ymax - lineBound.ymin
                                rect.xmin = lineBound.xmin ? lineBound.xmin : 0;
                                rect.xmax = lineBound.xmax ? lineBound.xmax : 0;
                                rect.ymin = lineBound.ymin ? lineBound.ymin : 0;
                                rect.ymax = lineBound.ymax ? lineBound.ymax : 0;
                            }
                        } else {
                            rect = {
                                xmin: 1,
                                ymin: 1,
                                w: 0,
                                h: 0
                            }
                        }
                    }
                    graphicData.imageData = context.getImageData(rect.xmin - 1, rect.ymin - 1, rect.w + 2, rect.h + 2)
                    graphicData.brect = rect
                    if (currentTool != 'eraser') {
                        updateCanvas();
                        context.beginPath();
                    } else if (currentTool == 'eraser' && isIE) {
                        updateCanvas();
                        context.beginPath();
                    }
                    sendData();
                    rendering = false;
                } else if (currentTool == 'eraser' && isIE) {
                    // alert("A");
                    updateCanvas();
                    context.beginPath();
                    // alert(rendering);
                } else {

                    if (currentTool != 'text') {
                        resetArrays()
                    } else {
                        // alert("B");
                        setTimeout(__focus);
                        // $('.mathquill-editable').focus();
                    }
                }
            };

            var ev_onmousemove = function (_event) {
                if (isReadOnly) {
                    return
                }
                var event = _event ? _event : window.event;
                event = event.type.indexOf('touch') > -1 ? _event.changedTouches[0] : event;
                if (penDown) {
                    rendering = true;
                    // console.log("MMOVE")
                    if (currentTool != 'pencil' && currentTool != 'text') {
                        // console.log("MMOVE: "+isIE)
                        if (isIE && currentTool != 'eraser') {

                            context.clearRect(0, 0, canvas.width, canvas.height);
                        } else if (!isIE) {
                            context.clearRect(0, 0, canvas.width, canvas.height);
                        }
                    }

                    // x = event.layerX?event.layerX:event.pageX-offX;
                    // y = event.layerY?event.layerY:event.pageY-offY;
                    // getCanvasPos()
                    if (event.pageX != undefined) {
                        x = event.pageX - offX;
                        y = event.pageY - offY;
                    } else {
                        x = event.clientX - offX
                        y = event.clientY - offY
                    }
                    if (selectionMode) {
                        if (selectedObj && selectionDragMode) {
                            var dx = x - clickX;
                            var dy = y - clickY;
                            drawTempObj(selectedObj, dx, dy)
                            drawBoundRect(selectedObj)
                        }

                    } else if (currentTool == 'rect' || currentTool == 'oval') {

                        x0 = clickX;
                        y0 = clickY;
                        w0 = x - clickX;
                        h0 = y - clickY;
                        if (currentTool == 'rect') {
                            drawRect(x0, y0, w0, h0, wb.globalStrokeColor)
                        }
                        if (currentTool == 'oval') {
                            drawOval(x0, y0, w0, h0, wb.globalStrokeColor)
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
                            if (!selectionMode) {
                                if (x > clickX) {
                                    if (!lineBound.xmax) {
                                        lineBound.xmax = x
                                    }
                                    if (x > lineBound.xmax) {
                                        lineBound.xmax = x
                                    }
                                } else if (x < clickX) {
                                    if (!lineBound.xmin) {
                                        lineBound.xmin = x
                                    }
                                    if (x < lineBound.xmin) {
                                        lineBound.xmin = x
                                    }
                                } else {
                                    if (!lineBound.xmax) {
                                        lineBound.xmax = x
                                    }
                                    if (x > lineBound.xmax) {
                                        lineBound.xmax = x
                                    }
                                    if (!lineBound.xmin) {
                                        lineBound.xmin = x
                                    }
                                    if (x < lineBound.xmin) {
                                        lineBound.xmin = x
                                    }
                                }
                                if (y > clickY) {
                                    if (!lineBound.ymax) {
                                        lineBound.ymax = y
                                    }
                                    if (y > lineBound.ymax) {
                                        lineBound.ymax = y
                                    }
                                } else if (y < clickY) {
                                    if (!lineBound.ymin) {
                                        lineBound.ymin = y
                                    }
                                    if (y < lineBound.ymin) {
                                        lineBound.ymin = y
                                    }
                                } else {
                                    if (!lineBound.ymax) {
                                        lineBound.ymax = y
                                    }
                                    if (y > lineBound.ymax) {
                                        lineBound.ymax = y
                                    }
                                    if (!lineBound.ymin) {
                                        lineBound.ymin = y
                                    }
                                    if (y < lineBound.ymin) {
                                        lineBound.ymin = y
                                    }
                                }
                                lineBound.xmin = lineBound.xmin ? lineBound.xmin : x;
                                lineBound.xmax = lineBound.xmax ? lineBound.xmax : x;
                                lineBound.ymin = lineBound.ymin ? lineBound.ymin : y;
                                lineBound.ymax = lineBound.ymax ? lineBound.ymax : y;
                                graphicData.dataArr[graphicData.dataArr.length] = {
                                    x: x - clickX,
                                    y: y - clickY,
                                    id: "line"
                                };
                                // console.log("DRAW_LINE_PENCIL: "+x+":"+y);
                                drawLine();
                            }
                        }

                    }
                }
                var bool = isMultitouch_gesture(_event);
                if (bool) {
                    return true
                }
                if (event.preventDefault) {
                    event.preventDefault()
                } else {
                    event.returnValue = false
                };
                // _event.stopPropagation();
            };

            function initSwipe(_event) {
                var event = _event ? _event : window.event;
                var c = $get_Element('#canvas-container')
                var tl = 0;
                if (event.touches) {
                    console_log(event.touches.length)
                    tl = event.touches.length
                } else {
                    console_log("NOT TOUCH ENABLED!")
                }
                console_log("TOUCHES: " + tl);
                if (tl >= 2) {
                    event.preventDefault();
                    var tarr = event.touches
                    var tp = {}
                    var tx = 0;
                    var ty = 0;
                    var tlen = tarr.length
                    for (var t = 0; t < tlen; t++) {
                        tx += tarr[t].pageX
                        ty += tarr[t].pageY
                    }
                    var touch = {
                        pageX: tx / tlen,
                        pageY: ty / tlen
                    };
                    swipe_sx = touch.pageX
                    swipe_sy = touch.pageY
                    console_log("TOUCHE_POS: " + swipe_sx + ":" + swipe_sy);
                    swipe_nx = 0
                    swipe_ny = 0
                    swipe_action = 'on'
                    c.addEventListener("touchmove", startSwipe)
                    c.addEventListener("touchend", stopSwipe)
                }
            }

            function startSwipe(_event) {
                var event = _event ? _event : window.event;
                event.preventDefault();
                var tarr = event.touches
                var tp = {}
                var tx = 0;
                var ty = 0;
                var tlen = tarr.length
                if (tlen < 2) {
                    return
                }
                for (var t = 0; t < tlen; t++) {
                    tx += tarr[t].pageX
                    ty += tarr[t].pageY
                }
                var touch = {
                    pageX: tx / tlen,
                    pageY: ty / tlen
                };
                swipe_mx = touch.pageX
                swipe_my = touch.pageY
                swipe_dx = swipe_mx - swipe_sx
                swipe_dy = swipe_my - swipe_sy
                swipe_nx = swipe_ox + swipe_dx
                swipe_ny = swipe_oy + swipe_dy
                if (tlen < 2) {
                    return
                }
                // $get('canvas-container').style.left=dx+"px"
                var currPosY = swipe_ny ? swipe_ny : 0;
                currPosY = parseInt(currPosY)
                currPosY = currPosY > 0 ? 0 : currPosY
                currPosY = currPosY < -(canvas.height - screen_height) ? -(canvas.height - screen_height) : currPosY;
                $get_Element('#canvas-container').style.top = currPosY + "px"
                $get_Element('#vscroll_thumb').style.top = getvscrolldata().t + "px";
                // console.log("Touch x:" + swipe_ox + ":" + swipe_nx + ", y:" +
                // swipe_oy + ":" + swipe_ny + ":::" + event.changedTouches.length);
                //
                var currPosX = swipe_nx ? swipe_nx : 0;
                currPosX = parseInt(currPosX)
                currPosX = currPosX > 0 ? 0 : currPosX
                currPosX = currPosX < -(canvas.width - screen_width) ? -(canvas.width - screen_width) : currPosX;
                $get_Element('#canvas-container').style.left = currPosX + "px"
                $get_Element('#hscroll_thumb').style.left = gethscrolldata().t + "px";
                console_log("Touch x:" + swipe_ox + ":" + swipe_nx + ", y:" + swipe_oy + ":" + swipe_ny + ":::" + event.changedTouches.length);
            }

            function stopSwipe(_event) {
                swipe_ox = swipe_ox + swipe_dx
                swipe_oy = swipe_oy + swipe_dy
                var event = _event ? _event : window.event;
                // var touch=event.changedTouches[0]
                event.preventDefault();
                // swipe_action='stop';
                var c = $get_Element('#canvas-container')
                c.removeEventListener("touchmove", startSwipe)
                c.removeEventListener("touchend", stopSwipe)
                $get_Element('#vscroll_thumb').style.top = getvscrolldata().t + "px";
                $get_Element('#hscroll_thumb').style.left = gethscrolldata().t + "px";
                // console.log("Touch END x:" + touch.pageX + ", y:" + touch.pageY);
            }

            function getvscrolldata() {
                var currPos = $get_Element('#canvas-container').style.top;
                currPos = currPos ? currPos : 0;
                currPos = parseInt(currPos)
                currPos = currPos > 0 ? 0 : currPos
                currPos = currPos < -(canvas.height - screen_height) ? -(canvas.height - screen_height) : currPos;
                var scrub = (canvas.height - screen_height) / (screen_height - 30);
                return {
                    p: currPos,
                    s: scrub,
                    t: (-currPos / scrub)
                }
            }

            function gethscrolldata() {
                var currPos = $get_Element('#canvas-container').style.left;
                currPos = currPos ? currPos : 0;
                currPos = parseInt(currPos)
                currPos = currPos > 0 ? 0 : currPos
                currPos = currPos < -(canvas.width - screen_width) ? -(canvas.width - screen_width) : currPos;
                var scrub = (canvas.width - screen_width) / (screen_width - 30);
                return {
                    p: currPos,
                    s: scrub,
                    t: (-currPos / scrub)
                };
            }

            function moveObject(event) {
                var delta = 0;

                if (!event) event = window.event;

                // normalize the delta
                if (event.wheelDelta) {

                    // IE and Opera
                    delta = event.wheelDelta / 60;

                } else if (event.detail) {

                    // W3C
                    delta = -event.detail / 2;
                }

                var currPos = $get_Element('#canvas-container').style.top;
                currPos = currPos ? currPos : 0;
                // console.log("DELTA:"+delta+":"+currPos);
                // calculating the next position of the object
                currPos = parseInt(currPos) + (delta * 10);
                currPos = currPos > 0 ? 0 : currPos
                currPos = currPos < -(canvas.height - screen_height) ? -(canvas.height - screen_height) : currPos;
                var scrub = (canvas.height - screen_height) / (screen_height - 30)
                $get_Element('#canvas-container').style.top = currPos + "px";
                $get_Element('#vscroll_thumb').style.top = (-currPos / scrub) + "px";
                // console.log("AFTER:"+currPos+":"+(currPos/scrub));
                // moving the position of the object
                // document.getElementById('scroll').style.top = currPos+"px";
                // document.getElementById('scroll').innerHTML = event.wheelDelta +
                // ":" + event.detail;
            }
            if (!IS_IOS) {
                $get_Element('#drawsection').onmousewheel = moveObject;
            }
            __killListeners()
            if (document.addEventListener) {
                canvas.addEventListener("mousedown", ev_onmousedown, false);
                canvas.addEventListener("mouseup", ev_onmouseup, false);
                canvas.addEventListener("mousemove", ev_onmousemove, false);

                // touchscreen specific - to prevent web page being scrolled while
                // drawing
                canvas.addEventListener('touchstart', touchStartFunction, false);
                canvas.addEventListener('touchmove', touchMoveFunction, false);

                // attach the touchstart, touchmove, touchend event listeners.
                canvas.addEventListener('touchstart', ev_onmousedown, false);
                canvas.addEventListener('touchmove', ev_onmousemove, false);
                canvas.addEventListener('touchend', ev_onmouseup, false);
                //
                // document.addEventListener('DOMMouseScroll', moveObject, false);
                // $get_Element('#container').addEventListener('mousemove',
                // scrollTheCanvas, false);
            } else {
                canvas.attachEvent("onmousedown", ev_onmousedown);
                canvas.attachEvent("onmouseup", ev_onmouseup);
                canvas.attachEvent("onmousemove", ev_onmousemove);


                // touchscreen specific - to prevent web page being scrolled while
                // drawing
                canvas.attachEvent('touchstart', touchStartFunction);
                canvas.attachEvent('touchmove', touchMoveFunction);

                // attach the touchstart, touchmove, touchend event listeners.
                canvas.attachEvent('touchstart', ev_onmousedown);
                canvas.attachEvent('touchmove', ev_onmousemove);
                canvas.attachEvent('touchend', ev_onmouseup);
                //

            }
            if (isReadOnly) {
                wb.isReadOnly(true);
            }
            canvas.focus();
            wb.whiteboardIsReady();
        }, 100);
    }

    function $get_Element(n) {
        var str = n.indexOf("#") > -1 ? n.split("#")[1] : n
        var jqobj = $("div#" + contDiv + " [name=" + str + "]");
        return jqobj[0];
    }

    function $get_jqElement(n) {
        return $($get_Element(n))
    }

    function updateText(txt, x, y, c) {
        // alert("UT:"+txt)
        graphicData.dataArr[0].text = escape(txt);
        graphicData.dataArr[0].x = x;
        graphicData.dataArr[0].y = y;
        graphicData.dataArr[0].color = c;
    }

    function showTextBox() {
        if (useMQ) {
            showTextBox_mq()
        } else {
            showTextBox_html()
        }
    }

    function hideTextBox() {
        if (useMQ) {
            hideTextBox_mq()
        } else {
            hideTextBox_html()
        }
    }

    function showTextBox_html() {
        //alert("A")
        if (!$get_Element("#content")) {
            $($("#" + contDiv + " [name='inputBox'] div")[0]).html("<textarea class='content' name='content' cols=15 rows=1 style='font:20pt Arial;color:" + wb.globalStrokeColor + "' ></textarea>")
        }
        $get_Element("#inputBox").style.display = 'block';
        $get_Element("#inputBox").style.top = clickY + "px";
        $get_Element("#inputBox").style.left = clickX + "px";
        //$get_Element("#inputBox").style.color = wb.globalStrokeColor;
        $get_Element("#content").focus();
    }

    function hideTextBox_html() {
        if ($get_Element("#content")) {
            $get_Element("#content").value = "";
        }
        $get_Element("#inputBox").style.display = 'none';
    }

    function showTextBox_mq() {
        // $get_Element("#inputBox").css({"top":clickY, "left":clickX,
        // "position":"absolute"});
        $get_Element("#inputBox").style.display = 'block';
        // $get_Element("#inputBox").style.position="absolute";
        $get_Element("#inputBox").style.top = clickY + "px";
        $get_Element("#inputBox").style.left = clickX + "px";
        // $('#editable-math').mathquill('latex', "");
        // $("#editable-math").focus();
        if (wb.mode == 'student') {
            $get_jqElement("#editable-math").css('color', '#000000')
        } else {
            $get_jqElement("#editable-math").css('color', '#ff0000')
        }
        setTimeout(__focus);
        // alert($("textarea").value)
        // alert($get_Element("#inputBox").style.top+":"+$get_Element("#inputBox").style.left)
    }

    function __focus() {
        // $("#editable-math").focus();

        // $("#editable-math").focus();

        // alert(isIE);
        if (isIE || isTouchEnabled || ieVer > 8) {
            $("#" + contDiv + " [name='inputBox'] textarea").focus();
        } else {
            $('.mathquill-editable').focus();
        }

        // alert()
    }

    function hideTextBox_mq() {
        // $get_Element("#editable-math").value = "";
        var disp = $get_Element("#inputBox").style.display
        if (disp == 'block') {
            //$("#editable-math").mathquill('redraw');
            $get_jqElement('#editable-math').mathquill('latex', "");
            $get_Element("#inputBox").style.display = 'none';
        }
    }

    function resetWhiteBoard(boo) {

        penDown = false;
        graphMode = '';
        // origcanvas.width = graphcanvas.width = topcanvas.width = canvas.width
        // = width;
        origcontext.clearRect(0, 0, canvas.width, canvas.height);
        graphcontext.clearRect(0, 0, canvas.width, canvas.height);
        topcontext.clearRect(0, 0, canvas.width, canvas.height);
        context.clearRect(0, 0, canvas.width, canvas.height);
        var _width = canvas.width;
        origcanvas.width = graphcanvas.width = topcanvas.width = canvas.width = _width
        /*origcontext.clearRect(0, 0, canvas.width, canvas.height);
        graphcontext.clearRect(0, 0, canvas.width, canvas.height);
        topcontext.clearRect(0, 0, canvas.width, canvas.height);
        context.clearRect(0, 0, canvas.width, canvas.height);*/
        drawingLayer = '1'
        $get_Element("#button_gr2D").style.border = '1px solid #000000';
        $get_Element("#button_nL").style.border = '1px solid #000000';
        $('.mathquill-embedded-latex').remove();
		
        if (boo) {
		graphicDataStore=[];
            wb.clearWhiteboard(true);
        }
    }

    function showHideGraph(flag, x, y, boo) {
        graphcanvas.width = graphcanvas.width;
        graphcanvas.height = graphcanvas.height;
        graphcontext.clearRect(0, 0, canvas.width, canvas.height);
        graphicData.dataArr = [];
        graphicData.id = tool_id[currentTool];
        var addGraph = false
        if (!boo && ((graphMode == 'gr2D' && flag == 'gr2D') || (graphMode == 'nL' && flag == 'nL'))) {
            graphMode = "";
            drawingLayer = '1'
            $get_Element("#button_gr2D").style.border = '1px solid #000000';
            $get_Element("#button_nL").style.border = '1px solid #000000';
        } else {
            $get_Element("#button_gr2D").style.border = '1px solid #000000';
            $get_Element("#button_nL").style.border = '1px solid #000000';
            var gr, xp, yp, xs, ys
                graphMode = flag;
            var cposX = parseInt($get_Element("#canvas-container").style.left);
            var cposY = parseInt($get_Element("#canvas-container").style.top);
            cposX = cposX ? cposX : 0;
            cposY = cposY ? cposY : 0;
            if (flag == 'gr2D') {
                gr = gr2D
                xp = x ? x - (gr2D_w / 2) : gr2D_xp - cposX
                yp = y ? y - (gr2D_h / 2) : gr2D_yp - cposY
                xs = x ? x : gr2D_xp - cposX + (gr2D_w / 2)
                ys = y ? y : gr2D_yp - cposY + (gr2D_h / 2)
                $get_Element("#button_gr2D").style.border = '2px solid #ff0000';
            } else {
                gr = nL;
                xp = x ? x - (nL_w / 2) : nL_xp - cposX
                yp = y ? y - (nL_h / 2) : nL_yp - cposY
                xs = x ? x : nL_xp - cposX + (nL_w / 2)
                ys = y ? y : nL_yp - cposY + (nL_h / 2)
                $get_Element("#button_nL").style.border = '2px solid #ff0000';
            }
            drawingLayer = isIE ? '1' : '3';
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
        if (currentTool == 'gr2D' || currentTool == 'nL') {
            currentTool = 'pencil';
        }
    }

    function mouseOverGraph() {
        getCanvasPos();
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
        var cntxt = drawingLayer == '1' ? origcontext : topcontext;
        var cnvs = drawingLayer == '1' ? origcanvas : topcanvas;
        if (currentTool == 'eraser') {
            // cntxt=origcontext;
        }
        // console.log(cntxt);
        if (isIE) {
            //drawImage workaround for IE to fix high memory usage
            var cn = $($(cnvs).children()[0]);
            var cv = $($(canvas).children()[0]);
            var el = '<div style="position:absolute;">' + $($(canvas).html()).html() + '</div><div style="position: absolute; filter: alpha(opacity=0); BACKGROUND-COLOR: red; overflow: hidden;"></div>'
            cn.append(el);
        } else {
            cntxt.drawImage(canvas, 0, 0);
        }
        //cntxt.drawImage(canvas, 0, 0);
        context.clearRect(0, 0, canvas.width, canvas.height);
        /*
         * context.save() context.fillStyle='rgba(255,255,255,255)'
         * context.fillRect(0, 0, canvas.width, canvas.height);
         * context.restore();
         */
        context.beginPath();
    }

    function erase(x, y) {
        var ew = 30
        var ep = ew;
        if (isIE) {
            var x0 = x;
            var y0 = y;
            var graphics = context;
            var eR = ep / 2;
            context.save();
            context.beginPath();
            context.fillStyle = 'white';
            context.lineWidth = 0;
            // context.fillRect(x - ep / 2, y - ep / 2, ew, ew);
            graphics.moveTo(x0 - eR, y0 - eR);
            graphics.lineTo(x0 + eR, y0 - eR);
            graphics.lineTo(x0 + eR, y0 + eR);
            graphics.lineTo(x0 - eR, y0 + eR);
            graphics.lineTo(x0 - eR, y0 - eR);

            context.closePath();
            context.fill();
            context.restore();
            // updateCanvas();
            //
            /*
             * topcontext.save(); topcontext.fillStyle='white';
             * topcontext.fillRect(x - ep / 2, y - ep / 2, ew, ew);
             * topcontext.restore();
             */
            return;
        }
        origcontext.clearRect(x - ep / 2, y - ep / 2, ew, ew);
        topcontext.clearRect(x - ep / 2, y - ep / 2, ew, ew);
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
                // currentObj.tempData = convertStringToObj(jStr);
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

                // alert('test 6');
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

                console_log("Sending json string_segemented line -segments  : " + segArr.length);
                render = false;
                resetArrays();
                textRendering = false;
                return;
            }

            render = false;
            // var jsonStr = convertObjToString(graphicData);
            console_log("Sending Data string for: " + graphicData.id);
            var isAGraph = graphicData.id == 11 || graphicData.id == 12
            if (!isAGraph && graphicData.dataArr[0].name == 'graphImage') {} else {
                sendDataToSERVER(graphicData);
            }
            textRendering = false;
        }
        resetArrays();
    }

    function sendDataToSERVER(jsdata) {
        var nobj = cloneObject(jsdata)
        nobj.imageData = undefined;
        var jsonStr = convertObjToString(nobj);

        console_log("Sending json string: " + jsonStr);

        graphicDataStore.push(cloneObject(jsdata));
        wb.whiteboardOut(jsonStr, true);

        try {
            if (supports_localStorage()) {
                localStorage['jstr'] = jsonStr
            } else {
                console_log("DATA NOT SAVED - LOCAL STORAGE NOT AVAILABLE!")
            }
        } catch (e) {
            console_log("DATA NOT SAVED - LOCAL STORAGE NOT AVAILABLE!")
        }
    }

    function supports_localStorage() {
        try {
            return 'localStorage' in window && window['localStorage'] !== null;
        } catch (e) {
            return false;
        }
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
        // alert("RESET_ARRAYS_CALLED");
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
            var s = stringify(obj);
            return s;
        } catch (ex) {
            console_log(ex.name + ":" + ex.message + ":" + ex.location + ":" + ex.text);
        }
    }
    // function that converts JSON string to flash object

    function convertStringToObj(str) {
        try {
            var o = eval("(" + str + ")"); // eval(str);
            return o;
        } catch (ex) {
            console_log(ex.name + ":" + ex.message + ":" + ex.location + ":" + ex.text);
        }
    }
    //
    /**
json stringify method for browsers which doesnt  have support for JSON
source: https://gist.github.com/754454
*/

    function stringify(obj) {
        if ("JSON" in window) {
            return JSON.stringify(obj);
        }

        var t = typeof (obj);
        if (t != "object" || obj === null) {
            // simple data type
            if (t == "string") obj = '"' + obj + '"';

            return String(obj);
        } else {
            // recurse array or object
            var n, v, json = [],
                arr = (obj && obj.constructor == Array);

            for (n in obj) {
                v = obj[n];
                t = typeof (v);
                if (obj.hasOwnProperty(n)) {
                    if (t == "string") {
                        v = '"' + v + '"';
                    } else if (t == "object" && v !== null) {
                        v = stringify(v);
                    }

                    json.push((arr ? "" : '"' + n + '":') + String(v));
                }
            }

            return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
        }
    }

    function getBoundRect(x0, y0, w0, h0) {
        var rect = {};
        rect.x = x0;
        rect.y = y0
        rect.w = w0
        rect.h = h0
        rect.xmin = rect.w >= 0 ? rect.x : rect.x + rect.w;
        rect.xmax = rect.w >= 0 ? rect.x + rect.w : rect.x;
        rect.ymin = rect.h >= 0 ? rect.y : rect.y + rect.h;
        rect.ymax = rect.h >= 0 ? rect.y + rect.h : rect.y;
        rect.w = Math.abs(w0)
        rect.h = Math.abs(h0)
        return rect
    }
    // ### RENDER OBJECT TO WHITEBOARD

    function renderObj(obj, boo) {
        try {
            renderObjAux(obj, boo);
        } catch (e) {
            console_log('error rendering: ' + e);
        }
    }

    function renderObjAux(obj, boo) {

        var graphic_id = obj.id;
        var graphic_data = obj.dataArr;
        var line_rgb = obj.lineColor;
        var dLength = graphic_data.length;
        var dep, x0, y0, x1, y1;
        var textF;
        var idName;
        drawingLayer = graphic_data[0].layer ? graphic_data[0].layer : drawingLayer;
        drawingLayer = isIE ? '1' : drawingLayer;
        var rect, lineBound;
        if (!boo) {
            rect = {
                xmin: 1,
                ymin: 1,
                w: 0,
                h: 0
            };
        }
        if (context.lineWidth != 2) {
            context.lineWidth = 2.0;
        }
        if (graphic_data[0].color !== undefined) {
            var cstr = String(graphic_data[0].color).indexOf("#") > -1 ? graphic_data[0].color.substr(1) : graphic_data[0].color.toString(16)
            var col = "#" + (cstr == '0' ? '000000' : cstr);
            context.strokeStyle = col;
        }
        var deb = ""
        console_log("RENDER_DATA_FOR: " + graphic_id)
        if (graphic_id === 0) {
            for (var i = 0; i < dLength; i++) {

                x1 = graphic_data[i].x;
                y1 = graphic_data[i].y;
                deb += x1 + ":" + y1 + "||"
                erase(x1, y1);
                if (isIE) {
                    updateCanvas();
                }

            }
        }
        // alert(deb)
        if (graphic_id === 3 || graphic_id === 1) {
            if (graphic_data[0].name == 'graphImage') {
                return
            }
            lineBound = {}
            for (i = 0; i < dLength; i++) {
                x1 = graphic_data[i].x;
                y1 = graphic_data[i].y;
                if (graphic_data[i].id == "move") {
                    context.beginPath();
                    context.moveTo(x1, y1);
                    x0 = x1;
                    y0 = y1;
                    if (graphic_id == 1) {
                        lineBound.ymax = y1;
                        lineBound.ymin = y1;
                        lineBound.xmax = x1;
                        lineBound.xmin = x1;
                    }
                } else {
                    context.lineTo(x0 + x1, y0 + y1);
                    if (!boo && graphic_id == 1) {
                        var xn = x0 + x1;
                        var yn = y0 + y1;
                        if (xn > x0) {
                            if (!lineBound.xmax) {
                                lineBound.xmax = xn
                            }
                            if (xn > lineBound.xmax) {
                                lineBound.xmax = xn
                            }
                        } else if (xn < x0) {
                            if (!lineBound.xmin) {
                                lineBound.xmin = xn
                            }
                            if (xn < lineBound.xmin) {
                                lineBound.xmin = xn
                            }
                        } else {
                            if (!lineBound.xmax) {
                                lineBound.xmax = xn
                            }
                            if (xn > lineBound.xmax) {
                                lineBound.xmax = xn
                            }
                            if (!lineBound.xmin) {
                                lineBound.xmin = xn
                            }
                            if (xn < lineBound.xmin) {
                                lineBound.xmin = xn
                            }
                        }
                        if (yn > y0) {
                            if (!lineBound.ymax) {
                                lineBound.ymax = yn
                            }
                            if (yn > lineBound.ymax) {
                                lineBound.ymax = yn
                            }
                        } else if (yn < y0) {
                            if (!lineBound.ymin) {
                                lineBound.ymin = yn
                            }
                            if (yn < lineBound.ymin) {
                                lineBound.ymin = yn
                            }
                        } else {
                            if (!lineBound.ymax) {
                                lineBound.ymax = yn
                            }
                            if (y1 > lineBound.ymax) {
                                lineBound.ymax = yn
                            }
                            if (!lineBound.ymin) {
                                lineBound.ymin = yn
                            }
                            if (yn < lineBound.ymin) {
                                lineBound.ymin = yn
                            }
                        }
                        lineBound.xmin = lineBound.xmin ? lineBound.xmin : xn;
                        lineBound.xmax = lineBound.xmax ? lineBound.xmax : xn;
                        lineBound.ymin = lineBound.ymin ? lineBound.ymin : yn;
                        lineBound.ymax = lineBound.ymax ? lineBound.ymax : yn;
                    }
                }
            }
            if (!boo) {
                if (graphic_id == 1) {
                    rect.x = lineBound.xmin ? lineBound.xmin : 0
                    rect.y = lineBound.ymin ? lineBound.ymin : 0
                    rect.w = lineBound.xmax - lineBound.xmin
                    rect.h = lineBound.ymax - lineBound.ymin
                    rect.xmin = lineBound.xmin ? lineBound.xmin : 0;
                    rect.xmax = lineBound.xmax ? lineBound.xmax : 0;
                    rect.ymin = lineBound.ymin ? lineBound.ymin : 0;
                    rect.ymax = lineBound.ymax ? lineBound.ymax : 0;
                } else {
                    rect = getBoundRect(x0, y0, x1, y1)
                }
            }
            context.stroke()
            if (!boo) {
                obj.imageData = context.getImageData(rect.xmin - 1, rect.ymin - 1, rect.w + 2, rect.h + 2)
            }
            updateCanvas()
        }
        if (graphic_id === 2) {
            for (i = 0; i < dLength; i++) {

                if (graphic_data[i].text != "" || graphic_data[i].text != undefined) {
                    x0 = graphic_data[i].x;
                    y0 = graphic_data[i].y;
                    // context.fillText(graphic_data[i].text, x0, y0);
                    xt = graphic_data[i].text;
                    xt = unescape(decodeURI(xt));
                    xt = String(xt).split("\\:").join(" ");
                    if (xt.indexOf('\\frac') > -1) {
                        xt = xt.split('\\frac').join("");
                        xt = xt.split('}{').join("/");
                        xt = xt.split('{').join("");
                        xt = xt.split('}').join("")
                    }
                    renderText(xt, x0, y0, col);
                    if (!boo) {
                        var str = xt.split("\n");
                        var ht = determineFontHeight(str[0]);
                        rect.x = rect.xmin = x0
                        rect.y = rect.ymin = y0
                        rect.w = context.measureText(xt).width
                        rect.h = (ht + ht / 3) * str.length
                        rect.xmax = rect.x + rect.w;
                        rect.ymax = rect.y + rect.h;
                    }
                }
            }
            if (!boo) {

                obj.imageData = context.getImageData(rect.xmin - 1, rect.ymin - 1, rect.w + 2, rect.h + 2)
                //context.strokeRect(rect.xmin - 1, rect.ymin - 1, rect.w + 2, rect.h + 2)
            }
            updateCanvas()
        }
        if (graphic_id === 4 || graphic_id === 5) {
            var fName = graphic_id == 4 ? drawRect : drawOval;
            for (i = 0; i < dLength; i++) {
                var xd = graphic_data[i].xs < 0 ? -1 : 1
                var yd = graphic_data[i].ys < 0 ? -1 : 1
                x0 = xd < 0 ? graphic_data[i].x + graphic_data[i].w : graphic_data[i].x
                y0 = yd < 0 ? graphic_data[i].y + graphic_data[i].h : graphic_data[i].y
                w0 = graphic_data[i].w * xd
                h0 = graphic_data[i].h * yd
                fName(x0, y0, w0, h0, col);
            }
            rect = getBoundRect(x0, y0, w0, h0)
            if (!boo) {
                obj.imageData = context.getImageData(rect.xmin - 1, rect.ymin - 1, rect.w + 2, rect.h + 2)

            }
            updateCanvas()
        }
        if (graphic_id === 11 || graphic_id === 12) {
            idName = graphic_id == 11 ? "gr2D" : "nL";
            showHideGraph(idName, graphic_data[0].x, graphic_data[0].y, graphic_data[0].addImage);
        }
        if (!boo) {

            obj.brect = rect
            graphicDataStore.push(obj);
        }
    }
    wb.updateWhiteboard_local = function (cmdArray) {
        var oaL = cmdArray.length;

        for (var l = 0; l < oaL; l++) {
            if (cmdArray[l] instanceof Array) {

                var arg = cmdArray[l][1];
                arg = arg == undefined ? [] : arg;

                // alert('cmdArray[l][0]: ' + cmdArray[l][0]);
                // alert('data: ' + this[cmdArray[l][0]])
                var command = cmdArray[l][0];
                // make unique to whiteboard, otherwise
                // other code can override.
                if (command == 'clear') {
                    resetWhiteBoard(false);
                } else {
                    this[command].apply(scope, arg);
                }
            } else if (cmdArray[l].indexOf("dataArr") != -1) {

                wb.draw(cmdArray[l]);
            } else {
                scope[cmdArray[l]]();
            }
        }
        // updateScroller();
    }

    /**
     * Map GWT array type to JS Array.
     *
     * TODO: not sure why this is needed, otherwise instanceof Array seems to
     * fail.
     *
     * cmdArray is already an array in JSNI.
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
        wb.updateWhiteboard_local(realArray);
    }

    wb.updateWhiteboard = function (cmdArray) {
        gwt_updatewhiteboard(cmdArray);
    }
    wb.renderFromStorage = function () {
        if (supports_localStorage()) {
            var str = localStorage['jstr'];
            updateWhiteboard([
                ["draw", [str]]
            ])
        } else {
            console_log("DATA NOT SAVED - LOCAL STORAGE NOT AVAILABLE!")
        }
    }
    //
    /**
     * * SETS THE WHITEBOARD MODE AS TEACHER MODE ++ ON TEACHER MODE THE DRAWING
     * COLOR WILL BE SET AS RED
     */
    wb.setAsTeacherMode = function (boo) {
        var b = boo === undefined ? true : boo
        if (b) {
            wb.globalStrokeColor = '#ff0000';
            wb.mode = 'admin'
        } else {
            wb.globalStrokeColor = '#000000';
            wb.mode = 'student';
        }
    }
    wb.getWhiteboardMode = function () {
        return wb.mode;
    }
    //
    // function receives jsonData and renders it to the screen
    wb.draw = function (json_str) {
        var grobj = convertStringToObj(json_str);
        renderObj(grobj);
    }

    function colorToNumber(c) {
        var n = c.split('#').join('0x');
        return Number(n);
    }

    wb.clearWhiteboard = function (boo) {
        if (!boo) {
            resetWhiteBoard(false)
        }

        wb.whiteboardOut("clear", false);
    }


    /** will be overriden in GWT/parent */
    wb.saveWhiteboard = function () {
        console_log('default whiteboard save');
    }

    /**
     * API method used to externalize handling of JSON data
     *
     * @param data
     * @param boo
     */

    wb.whiteboardOut = function (data, boo) {
        //alert('WHITEBOARD: whiteboardOut is going nowhere.  Hook up to external process to save data');
    }

    wb.disconnectWhiteboard = function (documentObject) {
        alert('default whiteboard disconnect');
        /** empty */
    }

    wb.whiteboardIsReady = function () {
        alert('This is the default whiteboardIsReady, it should be overridden in GWT');
    }
    /**
     * Exposed methods to: disable,enable calculator and show or hide them
     */
    wb.disableCalculator = function () {
        enable_calc = false;
        _disableCalc()
    }
    wb.enableCalculator = function () {
        enable_calc = true;
        _enableCalc()
    }
    wb.hideCalculator = function () {
        hideCalc();
    }
    wb.showCalculator = function () {
        showCalc();
    }
    //
    wb.setSelectionMode = function () {
        selectionMode = !selectionMode
        if (!selectionMode) {
            removeBoundRect()
        }
    }
    wb.removeSelectionMode = function () {
        selectionMode = !true
        removeBoundRect()
        $get_Element("#button_move").style.border = '1px solid #000000';
        currentTool = 'pencil';
        buttonHighlite(currentTool)
    }
    wb.whiteboardDelete = function (n) {

    }
    //

    function updateDataToSERVER(index, jsdata) {
        wb.whiteboardDelete(index);
        graphicData = jsdata;
        sendData();
    }
    //
    wb.isReadOnly = function (boo) {
        isReadOnly = boo
        if (boo) {
            $("div#" + contDiv + " [name='tools'] button").hide()
        } else {
            $("div#" + contDiv + " [name='tools'] button").show()
        }
    }
    return wb;
};




/** Math quill has to be setup after the HTML has been rendered
 */

function setupMathQuill() {
    //on document ready, mathquill-ify all `<tag class="mathquill-*">latex</tag>`
    //elements according to their CSS class.
    $(function () {
        $('.mathquill-editable:not(.mathquill-rendered-math)').mathquill('editable');
        $('.mathquill-textbox:not(.mathquill-rendered-math)').mathquill('textbox');
        $('.mathquill-embedded-latex').mathquill();
    });
}