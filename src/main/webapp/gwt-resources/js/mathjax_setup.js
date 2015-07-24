
console.log('setting up font configuration dialog');
$(document).dblclick(function(e) {
    showFontConfigurationDialog();
});

function showFontConfigurationDialog() {

	var currMjFont = MathJax.Hub.config['HTML-CSS'].webFont;
	
	var form = "<h3>Choose MathJax Font</h3>" +
	           "<h3>Current font: " + currMjFont + "</h3>" +
	           "<select id='font-choose'>" +
	           "<option></option>" +
	           "<option>Asana-Math</option>" +
	           "<option>Gyre-Pagella</option>" +
	           "<option>Gyre-Termes</option>" +
	           "<option>Latin-Modern</option>" +
	           "<option>Neo-Euler</option>" +
	           "<option>STIX-Web</option>" +
	           "<option>TeX</option>" +
	           "</select>";
    form += "<div style='margin-top: 25px'><button>Close</button></div>";

	var html = "<div id='font-config' style='padding: 10px; position: absolute;top: 10px;left:10px;width: 300px;height: 200px;border: 1px solid blue;background: orange;'>" + form + "</div>";
	
	var el = document.createElement("div");
	el.innerHTML = html;
	
	document.body.appendChild(el);
	
	$('#font-config select').value = currMjFont;
	
	$('#font-config button').click(function(e) {
	    el.remove();      
	});
	
	$('#font-config select').change(function(x) {
		// alert('Changing MathJax font to: ' + x.target.value);
		
		
		var url = window.location.href;
		var ind = url.indexOf('&mj_font');
		if(ind > -1) {
			url = url.substring(0, ind);
		}
		url += '&mj_font=' + x.target.value;
		window.location.href = url;
	})
}


/** run after MathJax has been loaded
 *  
 *  called from index in text/mathjax js block
 */
function setupMathJax() {
	
    var url = window.location.href;
	var ind = url.indexOf('&mj_font');
    var mjFont=null;
	if(ind > -1) {
		mjFont = url.substring(ind+1);
	}
    if(mjFont) {  
        MathJax.Hub.Config({
            "HTML-CSS": { webFont: mjFont }
        });
    }	
}

