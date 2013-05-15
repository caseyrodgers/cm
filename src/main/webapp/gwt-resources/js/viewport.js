// Page Size and View Port Dimension Tools
// http://stevenbenner.com/2010/04/calculate-page-size-and-view-port-position-in-javascript/
if (!sb_windowTools) { var sb_windowTools = new Object(); };
 
sb_windowTools = {
    scrollBarPadding: 17, // padding to assume for scroll bars
 
    // EXAMPLE METHODS
 
    // center an element in the viewport
    centerElementOnScreen: function(element) {
        var pageDimensions = this.updateDimensions();
        element.style.top = ((this.pageDimensions.verticalOffset() + this.pageDimensions.windowHeight() / 2) - (this.scrollBarPadding + element.offsetHeight / 2)) + 'px';
        element.style.left = ((this.pageDimensions.windowWidth() / 2) - (this.scrollBarPadding + element.offsetWidth / 2)) + 'px';
        element.style.position = 'absolute';
    },

    rightElementOnScreen: function(element) {
        var pageDimensions = this.updateDimensions();
        element.style.top = this.pageDimensions.verticalOffset() + 'px';
        element.style.right = "0px";
        element.style.position = 'absolute';
    },
    
    leftElementOnScreen: function(element) {
        var pageDimensions = this.updateDimensions();
        element.style.top = this.pageDimensions.verticalOffset() + 'px';
        element.style.left = "0px";
        element.style.position = 'absolute';
    },
 
    // INFORMATION GETTERS
 
    // load the page size, view port position and vertical scroll offset
    updateDimensions: function() {
        this.updatePageSize();
        this.updateWindowSize();
        this.updateScrollOffset();
    },
 
    // load page size information
    updatePageSize: function() {
        // document dimensions
        var viewportWidth, viewportHeight;
        if (window.innerHeight && window.scrollMaxY) {
            viewportWidth = document.body.scrollWidth;
            viewportHeight = window.innerHeight + window.scrollMaxY;
        } else if (document.body.scrollHeight > document.body.offsetHeight) {
            // all but explorer mac
            viewportWidth = document.body.scrollWidth;
            viewportHeight = document.body.scrollHeight;
        } else {
            // explorer mac...would also work in explorer 6 strict, mozilla and safari
            viewportWidth = document.body.offsetWidth;
            viewportHeight = document.body.offsetHeight;
        };
        this.pageSize = {
            viewportWidth: viewportWidth,
            viewportHeight: viewportHeight
        };
    },
 
    // load window size information
    updateWindowSize: function() {
        // view port dimensions
        var windowWidth, windowHeight;
        if (self.innerHeight) {
            // all except explorer
            windowWidth = self.innerWidth;
            windowHeight = self.innerHeight;
        } else if (document.documentElement && document.documentElement.clientHeight) {
            // explorer 6 strict mode
            windowWidth = document.documentElement.clientWidth;
            windowHeight = document.documentElement.clientHeight;
        } else if (document.body) {
            // other explorers
            windowWidth = document.body.clientWidth;
            windowHeight = document.body.clientHeight;
        };
        this.windowSize = {
            windowWidth: windowWidth,
            windowHeight: windowHeight
        };
    },
 
    // load scroll offset information
    updateScrollOffset: function() {
        // viewport vertical scroll offset
        var horizontalOffset, verticalOffset;
        if (self.pageYOffset) {
            horizontalOffset = self.pageXOffset;
            verticalOffset = self.pageYOffset;
        } else if (document.documentElement && document.documentElement.scrollTop) {
            // Explorer 6 Strict
            horizontalOffset = document.documentElement.scrollLeft;
            verticalOffset = document.documentElement.scrollTop;
        } else if (document.body) {
            // all other Explorers
            horizontalOffset = document.body.scrollLeft;
            verticalOffset = document.body.scrollTop;
        };
        this.scrollOffset = {
            horizontalOffset: horizontalOffset,
            verticalOffset: verticalOffset
        };
    },
 
    // INFORMATION CONTAINERS
 
    // raw data containers
    pageSize: {},
    windowSize: {},
    scrollOffset: {},
 
    // combined dimensions object with bounding logic
    pageDimensions: {
        pageWidth: function() {
            return sb_windowTools.pageSize.viewportWidth > sb_windowTools.windowSize.windowWidth ?
                sb_windowTools.pageSize.viewportWidth :
                sb_windowTools.windowSize.windowWidth;
        },
        pageHeight: function() {
            return sb_windowTools.pageSize.viewportHeight > sb_windowTools.windowSize.windowHeight ?
                sb_windowTools.pageSize.viewportHeight :
                sb_windowTools.windowSize.windowHeight;
        },
        windowWidth: function() {
            return sb_windowTools.windowSize.windowWidth;
        },
        windowHeight: function() {
            return sb_windowTools.windowSize.windowHeight;
        },
        horizontalOffset: function() {
            return sb_windowTools.scrollOffset.horizontalOffset;
        },
        verticalOffset: function() {
            return sb_windowTools.scrollOffset.verticalOffset;
        }
    }
};





 function docElem( property )
   {
      var t
      return ((t = document.documentElement) || (t = document.body.parentNode)) && Util.isNumber( t[property] ) ? t : document.body
   }
   
   // View width and height excluding any visible scrollbars
   // http://www.highdots.com/forums/javascript/faq-topic-how-do-i-296669.html
   //    1) document.client[Width|Height] always reliable when available, including Safari2
   //    2) document.documentElement.client[Width|Height] reliable in standards mode DOCTYPE, except for Safari2, Opera<9.5
   //    3) document.body.client[Width|Height] is gives correct result when #2 does not, except for Safari2
   //    4) When document.documentElement.client[Width|Height] is unreliable, it will be size of <html> element either greater or less than desired view size
   //       https://bugzilla.mozilla.org/show_bug.cgi?id=156388#c7
   //    5) When document.body.client[Width|Height] is unreliable, it will be size of <body> element less than desired view size
   function viewSize()
   {
      // This algorithm avoids creating test page to determine if document.documentElement.client[Width|Height] is greater then view size,
      // will succeed where such test page wouldn't detect dynamic unreliability,
      // and will only fail in the case the right or bottom edge is within the width of a scrollbar from edge of the viewport that has visible scrollbar(s).
      var doc = DocElem( 'clientWidth' ),
         body = document.body,
         w, h
      return Util.isNumber( document.clientWidth ) ? { w : document.clientWidth, h : document.clientHeight } :
         doc === body
         || (w = Math.max( doc.clientWidth, body.clientWidth )) > self.innerWidth
         || (h = Math.max( doc.clientHeight, body.clientHeight )) > self.innerHeight ? { w : body.clientWidth, h : body.clientHeight } :
         { w : w, h : h }
   }
