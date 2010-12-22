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
