function processMathJax(){try{MathJax.Hub.Queue(["Typeset",MathJax.Hub])}catch(A){alert("Error processing MathJax: "+A)}}function updateBreadCrumbs(){}function handleLoadSolutionData(C,B,A){var D=C.messageLocation;if(D.type!="solution"){alert("invalid MessageContext: "+D.type);return }_currentGUID=D.locationString1;_strings_moArray=B._strings_moArray;_stepUnits_moArray=B._stepUnits_moArray;_bookMeta.isControlled=B.tutorProperties._isControlled=="true";_bookMeta.textCode=B.tutorProperties._textCode;_bookMeta.category=B.tutorProperties._category;_bookMeta.bookTitle=B.tutorProperties._bookTitle;updateBreadCrumbs();_stepUnits=new Array();resetTutor();if(InmhButtons.readInmhListFromServer){InmhButtons.readInmhListFromServer()}var E=isSolutionIsAvailable();if(E){showNeedToSignup(E)}showTutor()}function scrollToStep(B){var A=document.getElementById("tutor_embedded").parentNode.parentNode.parentNode;A.scrollTop=A.scrollHeight}_enableJsWidgets=true;function _showTutorWidget(){var A=$get("hm_flash_widget");if(!A){return }A.onclick=function(){tutorWidgetClicked_gwt()}}_createGuiWrapper=function(){var B=_widgetDiv;B.setAttribute("style","position: relative;");var A="     <div id='hm_flash_widget_indicator' style='position: absolute;right: 5px;top: 25px;display: none;'>&nbsp;</div>     <div id='hm_flash_widget_head' onclick='showFlashObject();'>&nbsp;</div>";B.innerHTML=A;return B};function initializeSolutionEditor(){_showTutorWidget()};