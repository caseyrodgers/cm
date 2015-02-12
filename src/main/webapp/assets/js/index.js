function setupPageLocal() {
    YUI().use("event-mouseenter", function(Y) {
        
        Y.on("mouseenter", function (e) {
            this.all('p').addClass("info-box-selected");
         }, ".info-box-wrapper");
     
        Y.on("mouseleave", function (e) {
            this.all('p').removeClass("info-box-selected");
        }, ".info-box-wrapper");
     
    });    
}

function moveToLocation(el) {
  var dest = el.getAttribute('dest');
  document.location.href = dest;
}

var foundHTML   = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=foundations'>Foundations</a></div>";
var essHTML     = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=ess'>Essentials</a></div>";
var prealgHTML  = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=pre-alg'>Pre-Algebra</a></div>";
var alg1HTML    = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=alg 1'>Algebra 1</a></div>";
var alg2HTML    = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=alg 2'>Algebra 2</a></div>";
var geomHTML    = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=geom'>Geometry</a></div>";
var basicHTML   = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=basicmath'>College Basic Math</a></div>";
var elemalgHTML = "<div><a href='loginService?user=catchup_demo&pwd=demo&prog=elemalg'>College Elementary Algebra</a></div>";
var searchHTML   = "<div style='margin-top: 10px;color: #666'><a href='http://search.catchupmath.com'>Search Any Lesson</a></div>";

function programMenu() { 
	
	var h = 
		'<select onchange="showSampleSession(this)"> ' +
		'<option selected="true">-- Select a Program --</option>' +
		'<option value="foundations">Foundations</option> ' +
		'<option value="ess">Pre-Algebra</option> ' +
		'<option value="pre-alg">Algebra 1</option> ' +
		'<option value="alg 2">Algebra 2</option> ' +
		'<option value="geom">Geometry</option> ' +
		'<option value="basicmath">College Basic Math</option> ' +
		'<option value="elemalg">College Elementary Algebra</option> ' +
		'</select>';
		
	var html = '<h3 style="margin: 0">Programs</h3><div style="padding-left: 15px">' + foundHTML + essHTML + prealgHTML + alg1HTML + alg2HTML + geomHTML + basicHTML + elemalgHTML  + '</div>';
	html += searchHTML;
	
	// html += h;
	// var html = "<button onclick='showSearchPage()' style='margin-bottom: 10px;' type='button'>Search Any Lesson</button>";
	
	
	showSampleSessionDialog(html, 'Select Program or <span style="font-style: italic">Lesson</span>');
}

function showSampleSession(o) {
    var program = o.value;
    if(program) {
    	document.location.href = '/loginService?user=catchup_demo&pwd=demo&prog=' + program;
    }
}

function showSearchPage() {
	document.location.href = 'http://search.catchupmath.com';
}
function showSampleSessionDialog(msg, title, indexVal) {
	if(!indexVal) {
		indexVal = 999;
	}
    if (_overlay) {
        _overlay.hide();
    }
    var closer = '<div style="text-align: center; padding-bottom: 10px;">'
            + '<a href="#" onclick="closeGeneralDialog();return false;">Close</a></div>';

    closer = '';

    var html = '<div style="font-size: 1.1em;padding: 10px;">' + msg + '</div>' + closer;
    var head = '<a href="#" onclick="closeGeneralDialog();return false" class="close"><span>close</span> X</a>' + title;
    YUI().use('overlay', function(Y) {
        _overlay = new Y.Overlay({
            width : "260px",
            //centered : true,
            headerContent : head,
            bodyContent : html,
            zIndex : indexVal,
            visible : true
        });
        _overlay.set("x", 853);
        _overlay.set("y", 422)

        _overlay.render();
    });
}

