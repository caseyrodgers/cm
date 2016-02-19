
function showFontConfigurationDialog() {

    var currMjFont = MathJax.Hub.config['HTML-CSS'].webFont;
    
    var form = "<div>Choose MathJax Font</div>" +
               "<div>Current font: " + currMjFont + "</div>" +
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
    form += "<div style='margin-top: 15px'><button>Close</button></div>";

    var html = "<div id='font-config' style='padding: 10px; position: absolute;top: 10px;left:10px;width: 250px;height: 100px;border: 1px solid blue;background: orange;'>" + form + "</div>";
    
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


/**  MathJax runs after MathJax has been loaded
 *  
 *  called from index.html (or startup) in text/mathjax js block
 */
function setupMathJax() {
    var url = window.location.href;
    var ind = url.indexOf('&mj_font');
    var mjFont=null;
    if(ind > -1) {
        mjFont = url.substring(ind+9);
        console.log('requesting MathJax font: ' + mjFont);
    }
    else {
        // default 
        // possible fonts:
        // Asana-Math,Gyre-Pagella,Gyre-Termes,Latin-Modern,Neo-Euler,STIX-Web,TeX
        mjFont = 'STIX-Web';
    }

/** 
MathJax.Hub.Register.StartupHook("End Jax",function () {
  var BROWSER = MathJax.Hub.Browser;
  var jax = "HTML-CSS";
  if (BROWSER.isMSIE && BROWSER.hasMathPlayer) jax = "NativeMML";
  if (BROWSER.isFirefox) jax = "SVG";
  if (BROWSER.isSafari && BROWSER.versionAtLeast("5.0")) jax = "NativeMML";

  return MathJax.Hub.setRenderer(jax);
});
*/

    MathJax.Hub.Config({
        "showMathMenu": true,
        "HTML-CSS": { 
            webFont: mjFont, 
            preferredFont: "STIX-Web",
            mtextFontInherit: true,
            matchFontHeight: false
            }
    });
  
}


/** overriden from hotmath2/web/js/hm_mathjax.js */
function processMathJax() {
    var el = $get('tutor_raw_steps_wrapper');
    if(!el) {
        console.log("processing mathjax on page");
        MathJax.Hub.Queue(["Typeset",MathJax.Hub]);
    }
    else {
        console.log("processing mathjax on tutor");
        MathJax.Hub.Queue(["Typeset",MathJax.Hub], el);
        MathJax.Hub.Queue(function () {
            console.log('MathJax Complete');
        });
    }
    
}

