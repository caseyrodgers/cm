var _whiteboardActive=false;var _questionObjectPointer;var LoginInfo={isValid:function(){return true}};function showAskATutorTooltip_Cm(){var A="<div style='height:100px;padding: 5px;'><p style='margin-bottom: 5px;'>When enabled, this button connects you to an online tutor. Communication is by using an electronic whiteboard and text-chat.</p></div>";overlib(A,FGCLASS,"ol_default_style")}var _shouldExpandSteps;function doLoad_Gwt(pid,title,jsonConfig,hasShowWork,shouldExpandSteps,solutionHtml,solutionJs,isEpp,contextVarJson){_shouldExpandSteps=shouldExpandSteps;var mc=createNewSolutionMessageContext(pid,jsonConfig);mc.solutionData=solutionJs;mc.jsonConfig=jsonConfig?eval("("+jsonConfig+")"):null;var loc=mc.messageLocation;if(loc.type!="solution"){alert("MessageContext must be a solution")}_currentGUID=loc.locationString1;var tsw=$get("tutor_raw_steps_wrapper");if(!tsw){return }try{if(mc.solutionData==undefined){alert("solutionData not available");return }var obj=eval("("+mc.solutionData+")");handleLoadSolutionData(solutionHtml,mc,obj,this.argument,isEpp,contextVarJson)}catch(e){alert("CM loadSolutionData catch: "+e)}if(hasShowWork){var swf=document.getElementById("show-work-force");if(swf){swf.style.display="none"}}}var _bookMeta={textCode:"",isControlled:false};function setBreadCrumbs(A){}function scrollToStep(B){var A=document.getElementById("tutoroutput").parentNode.parentNode;A.scrollTop=A.scrollHeight}TutorManager.askATutor=function(){};function isSolutionIsAvailable(){return true}function showNeedToSignup(){}function setQuizQuestionActive(A){setQuizQuestionDisplayAsActive(A)}function setQuizQuestionDisplayAsActive(A){if(true){return }var G=document.getElementById("testset_div");if(G==null){return }var D=G.getElementsByTagName("div");var E=null;for(var C=0,B=D.length,H=0;C<B;C++){var F=D[C];if(F.className=="question_div"){if(_whiteboardActive==true&&(F.getAttribute("guid")==A||(A==null&&H==0))){F.style.background="#EAEAEA url(/gwt-resources/whiteboard_pointer.png) no-repeat top right";E=F.getAttribute("guid")}else{F.style.background=""}H++}}setQuizActiveQuestion_Gwt(E)}function setWhiteboardIsVisible(A){_whiteboardActive=A}function flash_quizResult(result){try{var resO=eval("("+result+")");flashInputField_Gwt(resO.result,resO.input,resO.answer,resO.id)}catch(e){alert("There was a problem processing Flash Input Field: "+e)}}function get_type(A){if(A===null){return"[object Null]"}return Object.prototype.toString.call(A)}function debug(A){if(window.gwt_debugLog){window.gwt_debugLog(A)}};