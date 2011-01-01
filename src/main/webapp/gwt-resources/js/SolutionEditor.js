function processMathJax() {
    try {
        MathJax.Hub.Queue([ "Typeset", MathJax.Hub ]);
    } catch (e) {
        alert('Error processing MathJax: ' + e);
    }
}

function updateBreadCrumbs() {
}

function handleLoadSolutionData(messageContext, tutorData, callAfter) {
    var loc = messageContext.messageLocation;
    if(loc.type != 'solution') {
        alert('invalid MessageContext: ' + loc.type);
        return;
    }
   // must set the current GUID
   _currentGUID = loc.locationString1;
   
   // transfer data from tutorData to existing variables
   // used to control the tutor
   _strings_moArray = tutorData._strings_moArray;
   _stepUnits_moArray = tutorData._stepUnits_moArray;
   _bookMeta.isControlled = tutorData.tutorProperties._isControlled == "true";
   _bookMeta.textCode = tutorData.tutorProperties._textCode;
   _bookMeta.category = tutorData.tutorProperties._category;
   _bookMeta.bookTitle = tutorData.tutorProperties._bookTitle;
   
   updateBreadCrumbs();
   
   // HTML is loaded, and JS is loaded 
   // Initialize tutor
   _stepUnits = new Array(); // force loading new dom elements
   resetTutor();
   
   if(InmhButtons.readInmhListFromServer)
       InmhButtons.readInmhListFromServer();
   
   // register a mouse listener to listen for
   // step selections.  When a step is selected
   // set style property to 'highlight' the step
   // this information will be sent with the context.
   // The problem statment
   // all steps
   // _stepElements = new Array();
   //var step=0;
   //var ele = $get('stepunit-' + step);
   //while(ele) {
   //    _stepElements[step] = ele;
   //    YAHOO.util.Event.addListener(ele, 'mousedown', highLightStepUnit);
   //    ++step;
   //    ele = $get('stepunit-' + step);
   //}
   // This creates a dependency on the tutor_collab.js (???)
   // positionContextForMessage(messageContext);
   
   var reasonForForNotAvailable = isSolutionIsAvailable();
   if(reasonForForNotAvailable) {
       showNeedToSignup(reasonForForNotAvailable);
   }
   showTutor();
}


function scrollToStep(num) {
    var objDiv = document.getElementById("tutor_embedded").parentNode.parentNode.parentNode;
    objDiv.scrollTop = objDiv.scrollHeight;
}  


_enableJsWidgets=true

var _tutorWidgetDefinition;
function _showTutorWidget() {
    var problemHead = document.getElementById('problem_statement');
    if(problemHead != null) {
        _showTutorWidgetReal();
        return;
    }
    
     var widgetDiv = $get('hm_flash_widget');
     if(!widgetDiv)
         return;
     /** extract embedded JSON */
     var jsonDiv = $get('hm_flash_widget_def');
     if(jsonDiv) {
         _tutorWidgetDefinition=jsonDiv;
     }
     else {
         
         /** create anew jsonDef widget
          * 
          */
         jsonDiv = document.createElement("div");
         jsonDiv.setAttribute("id", "hm_flash_widget_def");
         jsonDiv.setAttribute("style", "display:none;");
         widgetDiv.innerHTML = "";
         widgetDiv.appendChild(jsonDiv);
         widgetDiv.setAttribute("class", "");
         
         jsonDiv.innerHTML = "{}"; // default widget?
     }
     
     _tutorWidgetDefinition = jsonDiv;
     /** bring up widget editor in Gwt */
     widgetDiv.onclick=function(){tutorWidgetClicked_gwt(jsonDiv.innerHTML)};
}


function _showTutorWidgetReal() {
    var problemHead = document.getElementById('problem_statement');
    var divs = problemHead.getElementsByTagName('div');
    
    var wd=null;
    var jsonDiv=null;
    var _json="";
    
    for(var w=0,t=divs.length;w<t;w++) {
         var widgetDiv = divs[w];
         var at = widgetDiv.getAttribute('id');
         if(at == 'hm_flash_widget') {
             wd=widgetDiv;
         }
         else if(at == 'hm_flash_widget_def') {
             jsonDiv = widgetDiv;
         }
    }
     /** extract embedded JSON */
     if(jsonDiv) {
         if(_enableJsWidgets) {
             _json = jsonDiv.innerHTML;
             var jsonObj = eval('(' + _json + ')');
          HmFlashWidgetFactory.createWidget(jsonObj);
     }
     else {
         showFlashObject();
         widgetDiv.style.display = 'none';
         }
     }
     else {
         wd.innerHTML = _createGuiWrapper().innerHTML;
         var info = document.createElement("div");
         info.innerHTML = _getWidgetNotUsedHtml();
         wd.appendChild(info);
     }
}
