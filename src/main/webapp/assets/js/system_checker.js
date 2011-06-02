var systemIsOk = true;

function setupPageLocal() {
    
    if(window.location.search.indexOf('hide') > -1) {
        $get('main-content').parentNode.parentNode.style.background = 'white';
        $get('header').parentNode.style.display = 'none';
        $get('footer-find').style.display = 'none';
        $get('footer').style.display = 'none';
    }

    $get('javascript_check').innerHTML = '<p><img src="/gwt-resources/images/check_correct.png"/>OK</p>';
    $get('browser_check').innerHTML = checkBrowser();
    $get('window_check').innerHTML = checkWindowSize();
    $get('flash_check').innerHTML = checkFlash();
    checkNetwork($get('network_check'));
}

function checkBrowser() {
    var b = BrowserDetect.browser;
    var v = BrowserDetect.version;
    var browserString = b + ' version: ' + v;

    var str = '';
    var isOk = false;
    if (b == 'Firefox') {
        if (v > 3) {
            isOk = true;
        }
    } else if (b == 'Chrome') {
        if (v > 10) {
            isOk = true;
        }
    } else if (b == 'Explorer') {
        if (v >= 6) {
            isOk = true;
        }
    } else if (b == 'Opera') {
        if (v >= 9) {
            isOk = true;
        }
    } else if (b == 'Safari') {
        if (v >= 5) {
            isOk = true;
        }
    }

    if (isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        systemIsOk = false;
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + browserString + '</p>'

    if (!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">Your browser is not supported by Catchup Math. '
                + 'Even though your browser is not fully supported it may work, but you should expect problems.</p>';
    }

    return str;
}

function checkFlash() {
    var str = '';
    var playerVersion = swfobject.getFlashPlayerVersion();
    var majorVersion = playerVersion.major; // major, minor and release

    versionStr = playerVersion.major + '.' + playerVersion.minor + '.'
            + playerVersion.release;

    var isOk = false;
    if (majorVersion >= 9) {
        isOk = true;
    } else {
        systemIsOk = false;
        versionStr += ' Not OK!';
    }

    if (isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + 'Version: ' + versionStr + '</p>';

    if (!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">' +
               'You need to have at least version 9 of Flash installed. ' +
               'You can download the latest Flash player <a href="http://get.adobe.com/flashplayer/">here</a>.'
               '<p>';
    }

    return str;
}

function checkCookies() {
    var isOk = areCookiesEnabled();
    if (isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        systemIsOk = false;
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + (isOk ? 'Are enabled' : 'Not Enabled') + '</p>';

    if (!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">Cookies must be enabled.</p>';
    }

    return str;
}

function checkWindowSize() {
    var windowSizeObject = getBrowserWindowSize();
    
    var windowStr = 'width: ' + windowSizeObject.width + ', height: ' + windowSizeObject.height;
    
    var isOk=false;
    if(windowSizeObject.height > 600 &&  windowSizeObject.width > 800) {
        isOk = true;
    }

    var str='';
    if (isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + windowStr + '</p>';

    if (!isOk) {
        str += '<p style="color: red;font-size: 1.1em;">Increase the size of your browser window (if possible) to take full advantage of Catchup Math. </p>';
    }
    return str;
}

function setNetworkSpeed(timeSpent) {
    var isOk = false;
    var speed = 'Unknown';
    if (timeSpent == 0) {
        isOk = false
    } else if (timeSpent < 200) {
        isOk = true;
        speed = 'Fast';
    } else if (timeSpent < 700) {
        isOk = true;
        speed = 'Medium';
    } else if (timeSpent < 1000) {
        isOk = true;
        speed = 'Slow';
    } else {
        isOk = false;
        speed = 'Too Slow!'
    }

    if (isOk) {
        str = '<img src="/gwt-resources/images/check_correct.png"/>';
    } else {
        systemIsOk = false;
        str = '<img src="/gwt-resources/images/check_incorrect.png"/>';
    }

    str = '<p>' + str + speed + '</p>';

    if (!isOk) {
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
        }
        ;

        function onError(id, o, args) {
            systemIsOk = false;
            div.innerHTML = setNetworkSpeed(0);

            setFinalMessage();
        }
        ;

        // Subscribe to event "io:complete", and pass an array
        // as an argument to the event handler "complete", since
        // "complete" is global. At this point in the transaction
        // lifecycle, success or failure is not yet known.
        Y.on('io:success', complete, Y);
        Y.on('io:failure', complete, Y);

        // Make an HTTP request to 'get.php'.
        // NOTE: This transaction does not use a configuration object.
        var request = Y.io(uri);
    });
}

function setFinalMessage() {
    if (systemIsOk) {
        $get('final_message').innerHTML = '<p class="ready">Your system is ready to run Catchup Math!</p>';
    } else {
        $get('final_message').innerHTML = '<p class="not_ready">You may wish to change your configuration or use a different computer for best use of Catchup Math.</p>';
    }

}

function areCookiesEnabled() {
    var cookieEnabled = (navigator.cookieEnabled) ? true : false

    // if not IE4+ nor NS6+
    if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled) {
        document.cookie = "testcookie"
        cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true
                : false
    }

    return cookieEnabled;
}

function getBrowserWindowSize() {
    var win = window.parent;
    
    var myWidth = 0, myHeight = 0;
    if (typeof (win.innerWidth) == 'number') {
        // Non-IE
        myWidth = win.innerWidth;
        myHeight = win.innerHeight;
    } else if (document.documentElement
            && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
        // IE 6+ in 'standards compliant mode'
        myWidth = document.documentElement.clientWidth;
        myHeight = document.documentElement.clientHeight;
    } else if (document.body
            && (document.body.clientWidth || document.body.clientHeight)) {
        // IE 4 compatible
        myWidth = document.body.clientWidth;
        myHeight = document.body.clientHeight;
    }

    return {
        width : myWidth,
        height : myHeight
    };
}