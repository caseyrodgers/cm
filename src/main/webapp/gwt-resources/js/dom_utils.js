function DL_GetElementLeft(eElement)
{
    if (!eElement && this)                    // if argument is invalid
    {                                         // (not specified, is null or is
												// 0)
        eElement = this;                       // and function is a method
    }                                         // identify the element as the
												// method owner

    var DL_bIE = document.all ? true : false; // initialize var to identify IE

    var nLeftPos = eElement.offsetLeft;       // initialize var to store
												// calculations
    var eParElement = eElement.offsetParent;  // identify first offset parent
												// element

    while (eParElement != null)
    {                                         // move up through element
												// hierarchy
        if(DL_bIE)
        {
            if(eParElement.tagName == "TD")     // if parent a table cell,
												// then...
            {
                nLeftPos += eParElement.clientLeft; // append cell border width
													// to calcs
            }
        }

        nLeftPos += eParElement.offsetLeft;    // append left offset of parent
        eParElement = eParElement.offsetParent; // and move up the element
												// hierarchy
    }                                         // until no more offset parents
												// exist
    return nLeftPos;                          // return the number calculated
}

function DL_GetElementTop(eElement)
{
    if (!eElement && this)                    // if argument is invalid
    {                                         // (not specified, is null or is
												// 0)
        eElement = this;                       // and function is a method
    }                                         // identify the element as the
												// method owner

    var DL_bIE = document.all ? true : false; // initialize var to identify IE

    var nTopPos = eElement.offsetTop;       // initialize var to store
											// calculations
    var eParElement = eElement.offsetParent;  // identify first offset parent
												// element

    while (eParElement != null)
    {                                         // move up through element
												// hierarchy
        if(DL_bIE)
        {
            if(eParElement.tagName == "TD")     // if parent a table cell,
												// then...
            {
                nTopPos += eParElement.clientTop; // append cell border width
													// to calcs
            }
        }

        nTopPos += eParElement.offsetTop;    // append top offset of parent
        eParElement = eParElement.offsetParent; // and move up the element
												// hierarchy
    }                                         // until no more offset parents
												// exist
    return nTopPos;                          // return the number calculated
}




function getViewableSize() {
    var myWidth = 0, myHeight = 0;
    if( typeof( window.innerWidth ) == 'number' ) {
        // Non-IE
        myWidth = window.innerWidth;
        myHeight = window.innerHeight;
    } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
        // IE 6+ in 'standards compliant mode'
        myWidth = document.documentElement.clientWidth;
        myHeight = document.documentElement.clientHeight;
    } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
        // IE 4 compatible
        myWidth = document.body.clientWidth;
        myHeight = document.body.clientHeight;
    }
    a = [myWidth, myHeight];
    return  a;
}


function getScrollXY() {
    var scrOfX = 0, scrOfY = 0;
    if( typeof( window.pageYOffset ) == 'number' ) {
        // Netscape compliant
        scrOfY = window.pageYOffset;
        scrOfX = window.pageXOffset;
    } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
        // DOM compliant
        scrOfY = document.body.scrollTop;
        scrOfX = document.body.scrollLeft;
    } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
        // IE6 standards compliant mode
        scrOfY = document.documentElement.scrollTop;
        scrOfX = document.documentElement.scrollLeft;
    }
    return [ scrOfX, scrOfY ];
}



// DHTML Event Handling
function _addEvent(obj, evType, fn, useCapture){
    if (obj.addEventListener){
        obj.addEventListener(evType, fn, useCapture);
        return true;
    } else if (obj.attachEvent){
        var r = obj.attachEvent("on"+evType, fn);
        return r;
    } else {
        alert("Handler could not be attached");
    }
}

function _removeEvent(obj, evType, fn, useCapture){
    if (obj.removeEventListener){
        obj.removeEventListener(evType, fn, useCapture);
        return true;
    } else if (obj.detachEvent){
        var r = obj.detachEvent("on"+evType, fn);
        return r;
    } else {
        alert("Handler could not be removed");
    }
}


function hideDivOnMouseOut(event) {
    var current, related;

    if (window.event) {
        current = this;
        related = window.event.toElement;
    }
    else {
        current = event.currentTarget;
        related = event.relatedTarget;
    }

    // log('hideHelpDiv related: ' + related.nodeName + ' (' +
	// related.innerHTML.length + ') value: ' + related.nodeValue + ' current: '
	// + current.nodeName + ' (' + current.innerHTML.length + ')');

    if (current != related) {
        // log('current != related');
        if( !contains(current, related)) {
            // log('related is not in current');

            // dojo.lfx.wipeOut(current,200).play();
            // fadeOut s
            current.style.display = 'none';
        }
    }
}


// return true if a contains b
function contains(a, b) {
    // Return true if node a contains node b.
    while (b.parentNode) {
        b = b.parentNode;
        if (b == a) {
            return true;
        }
    }
    return false;
}


function grabComputedStyle(_10,_11)
{
    if(document.defaultView&&document.defaultView.getComputedStyle)
    {
        return document.defaultView.getComputedStyle(_10,null).getPropertyValue(_11);
    }
    else
    {
        if(_10.currentStyle)
        {
            return _10.currentStyle[_11];
        }
        else
        {
            return null;
        }
    }
}
function grabComputedHeight(_12)
{
    var _13=grabComputedStyle(_12,"height");
    if(_13!=null)
    {
        if(_13=="auto")
        {
            if(_12.offsetHeight)
            {
                _13=_12.offsetHeight;
            }
        }
        _13=parseInt(_13);
    }
    return _13;
}
function grabComputedWidth(_14)
{
    var _15=grabComputedStyle(_14,"width");
    if(_15!=null)
    {
        if(_15.indexOf("px")!=-1)
        {
            _15=_15.substring(0,_15.indexOf("px"));
        }
        if(_15=="auto")
        {
            if(_14.offsetWidth)
            {
                _15=_14.offsetWidth;
            }
        }
    }
    return _15;
}
