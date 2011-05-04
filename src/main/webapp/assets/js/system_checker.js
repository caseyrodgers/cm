var systemIsOk=true;

function setupPageLocal() {
    var os = BrowserDetect.os;
    
    $get('javascript_check').innerHTML = '<p><img src="/gwt-resources/images/check_correct.png"/>OK</p>';
    $get('browser_check').innerHTML = checkBrowser();
    $get('flash_check').innerHTML = checkFlash();
    $get('cookie_check').innerHTML = checkCookies();
    checkNetwork($get('network_check'));
}

function checkBrowser() {
    var b = BrowserDetect.browser;
    var v = BrowserDetect.version;
    var browserString = b + ' version: ' + v;
    
    var str = '';
    var isOk=false;
    if(b == 'Firefox') {
        if(v > 3) {
            isOk=true;
        }
    }
    else if(b == 'Chrome' ) {
        if(v > 10) {
            isOk=true;
        }
    }
    else if(b == 'Explorer') {
        if(v >= 6) {
            isOk=true;
        }
    }
    else if(b == 'Opera') {
        if(v >= 9) {
            isOk=true;
        }
    }
    else if(b == 'Safari') {
        if(v >= 5) {
            isOk=true;
        }
    }
    
    if(isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        systemIsOk=false;
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }
    
    str = '<p>' + str + browserString + '</p>'
    
    if(!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">Your browser is not supported by Catchup Math. ' +
               'Even though your browser is not fully supported it may work, but you should expect problems.</p>';
    }
    
    return str;
}

function checkFlash() {
    var str='';
    var playerVersion = swfobject.getFlashPlayerVersion();
    var majorVersion = playerVersion.major; // major, minor and release
    
    versionStr = playerVersion.major + '.' + playerVersion.minor + '.' + playerVersion.release;
    
    var isOk=false;
    if(majorVersion >= 9) {
        isOk=true;
    }
    else {
        systemIsOk=false;
        versionStr += ' Not OK!';
    }
    
    if(isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + 'Version: ' +  versionStr + '</p>';
    
    if(!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">You need to have at least version 9 of Flash installed.</p>';
    }
    
    return str;
}





function checkCookies() {
    var isOk=areCookiesEnabled();
    if(isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        systemIsOk=false;
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + (isOk?'Are enabled':'Not Enabled') + '</p>';
    
    if(!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">Cookies must be enabled.</p>';
    }
    
    return str;    
    
}

function setNetworkSpeed(timeSpent) {
    var isOk=false;
    var speed='Unknown';
    if(timeSpent == 0) {
        isOk = false
    }
    else if(timeSpent < 200) {
        isOk = true;
        speed = 'Fast';
    }
    else if(timeSpent < 700) {
        isOk = true;
        speed = 'Medium';
    }
    else if(timeSpent < 1000) {
        isOk = true;
        speed ='Slow';
    }
    else {
        isOk = false;
        speed = 'Too Slow!'
    }
    
    if(isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        systemIsOk=false;
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + speed + '</p>';
    
    if(!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">Catchup Math performance will be sluggish due to your limited network speed.</p>';
    }
    
    return str;    
}

function checkNetwork(div) {
    var startTime = new Date().getMilliseconds();
    YUI().use("io-base", function(Y) {
        var uri = "/assets/util/network_test.txt?rand=" + startTime;
     
        // Define a function to handle the response data.
        function complete(id, o, args) {
            var timeElapsed = new Date().getMilliseconds() - startTime;
            var id = id; // Transaction ID.
            var data = o.responseText; // Response data.
            div.innerHTML = setNetworkSpeed(timeElapsed);
            setFinalMessage();
        };
        
        function onError(id, o, args) {
            systemIsOk=false;
            div.innerHTML = setNetworkSpeed(0);
            
            setFinalMessage();
        };

     
        // Subscribe to event "io:complete", and pass an array
        // as an argument to the event handler "complete", since
        // "complete" is global.   At this point in the transaction
        // lifecycle, success or failure is not yet known.
        Y.on('io:success', complete, Y);
        Y.on('io:failure', complete, Y);
     
        // Make an HTTP request to 'get.php'.
        // NOTE: This transaction does not use a configuration object.
        var request = Y.io(uri);
    });
}

function setFinalMessage() {
    if(systemIsOk) {
        $get('final_message').innerHTML = '<p class="ready">Your system is ready to run Catchup Math!</p>';
    }
    else {
        $get('final_message').innerHTML = '<p class="not_ready">Your system is NOT ready to run Catchup Math.  While Catchup Math might run on your computer you should expect problems.</p>';
    }
        
}

var BrowserDetect = {
    init : function() {
        this.browser = this.searchString(this.dataBrowser)
                || "An unknown browser";
        this.version = this.searchVersion(navigator.userAgent)
                || this.searchVersion(navigator.appVersion)
                || "an unknown version";
        this.OS = this.searchString(this.dataOS) || "an unknown OS";
    },
    searchString : function(data) {
        for ( var i = 0; i < data.length; i++) {
            var dataString = data[i].string;
            var dataProp = data[i].prop;
            this.versionSearchString = data[i].versionSearch
                    || data[i].identity;
            if (dataString) {
                if (dataString.indexOf(data[i].subString) != -1)
                    return data[i].identity;
            } else if (dataProp)
                return data[i].identity;
        }
    },
    searchVersion : function(dataString) {
        var index = dataString.indexOf(this.versionSearchString);
        if (index == -1)
            return;
        return parseFloat(dataString.substring(index
                + this.versionSearchString.length + 1));
    },
    dataBrowser : [ {
        string : navigator.userAgent,
        subString : "Chrome",
        identity : "Chrome"
    }, {
        string : navigator.userAgent,
        subString : "OmniWeb",
        versionSearch : "OmniWeb/",
        identity : "OmniWeb"
    }, {
        string : navigator.vendor,
        subString : "Apple",
        identity : "Safari",
        versionSearch : "Version"
    }, {
        prop : window.opera,
        identity : "Opera"
    }, {
        string : navigator.vendor,
        subString : "iCab",
        identity : "iCab"
    }, {
        string : navigator.vendor,
        subString : "KDE",
        identity : "Konqueror"
    }, {
        string : navigator.userAgent,
        subString : "Firefox",
        identity : "Firefox"
    }, {
        string : navigator.vendor,
        subString : "Camino",
        identity : "Camino"
    }, { // for newer Netscapes (6+)
        string : navigator.userAgent,
        subString : "Netscape",
        identity : "Netscape"
    }, {
        string : navigator.userAgent,
        subString : "MSIE",
        identity : "Explorer",
        versionSearch : "MSIE"
    }, {
        string : navigator.userAgent,
        subString : "Gecko",
        identity : "Mozilla",
        versionSearch : "rv"
    }, { // for older Netscapes (4-)
        string : navigator.userAgent,
        subString : "Mozilla",
        identity : "Netscape",
        versionSearch : "Mozilla"
    } ],
    dataOS : [ {
        string : navigator.platform,
        subString : "Win",
        identity : "Windows"
    }, {
        string : navigator.platform,
        subString : "Mac",
        identity : "Mac"
    }, {
        string : navigator.userAgent,
        subString : "iPhone",
        identity : "iPhone/iPod"
    }, {
        string : navigator.platform,
        subString : "Linux",
        identity : "Linux"
    } ]

};
BrowserDetect.init();



function areCookiesEnabled() {
    var cookieEnabled=(navigator.cookieEnabled)? true : false

    //if not IE4+ nor NS6+
    if (typeof navigator.cookieEnabled=="undefined" && !cookieEnabled) { 
        document.cookie="testcookie"
        cookieEnabled=(document.cookie.indexOf("testcookie")!=-1)? true : false
    }
    
    return cookieEnabled;
}


