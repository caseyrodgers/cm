window.showCorrectAnswers=function(B){var D=document.getElementById("testset_div");if(D){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}}};function setQuizQuestionActive(A){}function findQuestionGuid(B){while(B){var A=B.getAttribute("guid");if(A){return A}B=B.parentNode}return null}function findQuestionByPid(B){var D=document.getElementById("testset_div").getElementsByTagName("div");try{for(var C=0;C<D.length;C++){var E=D[C].getAttribute("guid");if(E==B){return D[C]}}}catch(A){alert("Error while setting selected question response: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function findQuestionNumberByPid(B){var E=document.getElementById("testset_div").getElementsByTagName("div");var C=0;try{for(var D=0;D<E.length;D++){var F=E[D].getAttribute("guid");if(F){if(F==B){return C}else{C++}}}}catch(A){alert("Error while question index: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function questionGuessChanged(H){try{var B=findQuestionGuid(H);var G=-1;if(H.id=="optionSkipped"){G="-2"}else{var D=H.parentNode.parentNode.parentNode.parentNode;var A=D.getElementsByTagName("input");for(var C=0;C<A.length;C++){if(A.item(C)==H){G=C;break}}}var E=findQuestionNumberByPid(B);questionGuessChanged_Gwt(""+E,""+G,B)}catch(F){alert("Error answering question in external JS: "+F)}}function setSolutionQuestionAnswerIndexByNumber(B,C){var A=0;showCorrectAnswers(function(H){var G=H.getElementsByTagName("input");if(A==B){for(var F=0,E=G.length;F<E;F++){if(F==C){G[F].checked=true;var D=G[F];questionGuessChanged(D)}else{G[F].checked=false}}}A++})}window.setSolutionQuestionAnswerIndex=function(B,H,E){var G=findQuestionByPid(B);if(G){var F=G.getElementsByTagName("input");for(var D=0,C=F.length;D<C;D++){var A=F.item(D);A.disabled=E?true:false;if(D==H){A.checked=true}}}};function doLoadResource(B,A){doLoadResource_Gwt(B,A);return false}window.markAllCorrectAnswers=function(){showCorrectAnswers(markCorrectResponse)};window.getQuizResultsCorrect=function(){var A=0;showCorrectAnswers(function(E){var D=E.getElementsByTagName("input");for(var C=0,B=D.length;C<B;C++){var F=D[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){if(D[C].checked){A++}}}});return A};window.getQuizQuestionCount=function(){var A=0;showCorrectAnswers(function(B){A++});return A};window.showCorrectAnswers=function(B){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}};window.markCorrectResponse=function(D){var E=D.getElementsByTagName("input");for(var C=0,B=E.length;C<B;C++){var F=E[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){E[C].checked=true;var A=E[C];questionGuessChanged(A);break}}};function checkQuiz_Gwt(){alert("Checking quiz ...")}window.setQuizQuestionResult=function(C,A){var E=findQuestionByPid(C);var D=getQuestionMarkImage(C);var B=getQuestionMarkText(C);if(A=="Correct"){D.src="/gwt-resources/images/check_correct.png";B.innerHTML="Correct"}else{if(A=="Incorrect"){D.src="/gwt-resources/images/check_incorrect.png";B.innerHTML="Incorrect"}else{D.src="/gwt-resources/images/check_notanswered.png";B.innerHTML="Not answered"}}D.parentNode.style.display="block"};function getQuestionMarkImage(A){return document.getElementById("response_image_"+A)}function getQuestionMarkText(A){return document.getElementById("response_text_"+A)}function log(){}InmhButtons={};if(typeof deconcept=="undefined"){var deconcept=new Object()}if(typeof deconcept.util=="undefined"){deconcept.util=new Object()}if(typeof deconcept.SWFObjectUtil=="undefined"){deconcept.SWFObjectUtil=new Object()}deconcept.SWFObject=function(K,B,L,D,H,I,F,E,C,J){if(!document.getElementById){return }this.DETECT_KEY=J?J:"detectflash";this.skipDetect=deconcept.util.getRequestParameter(this.DETECT_KEY);this.params=new Object();this.variables=new Object();this.attributes=new Array();if(K){this.setAttribute("swf",K)}if(B){this.setAttribute("id",B)}if(L){this.setAttribute("width",L)}if(D){this.setAttribute("height",D)}if(H){this.setAttribute("version",new deconcept.PlayerVersion(H.toString().split(".")))}this.installedVer=deconcept.SWFObjectUtil.getPlayerVersion();if(!window.opera&&document.all&&this.installedVer.major>7){deconcept.SWFObject.doPrepUnload=true}if(I){this.addParam("bgcolor",I)}var A=F?F:"high";this.addParam("quality",A);this.setAttribute("useExpressInstall",false);this.setAttribute("doExpressInstall",false);var G=(E)?E:window.location;this.setAttribute("xiRedirectUrl",G);this.setAttribute("redirectUrl","");if(C){this.setAttribute("redirectUrl",C)}};deconcept.SWFObject.prototype={useExpressInstall:function(A){this.xiSWFPath=!A?"expressinstall.swf":A;this.setAttribute("useExpressInstall",true)},setAttribute:function(A,B){this.attributes[A]=B},getAttribute:function(A){return this.attributes[A]},addParam:function(B,A){this.params[B]=A},getParams:function(){return this.params},addVariable:function(B,A){this.variables[B]=A},getVariable:function(A){return this.variables[A]},getVariables:function(){return this.variables},getVariablePairs:function(){var C=new Array();var B;var A=this.getVariables();for(B in A){C[C.length]=B+"="+A[B]}return C},getSWFHTML:function(){var B="";if(navigator.plugins&&navigator.mimeTypes&&navigator.mimeTypes.length){if(this.getAttribute("doExpressInstall")){this.addVariable("MMplayerType","PlugIn");this.setAttribute("swf",this.xiSWFPath)}B='<embed type="application/x-shockwave-flash" src="'+this.getAttribute("swf")+'" width="'+this.getAttribute("width")+'" height="'+this.getAttribute("height")+'" style="'+this.getAttribute("style")+'"';B+=' id="'+this.getAttribute("id")+'" name="'+this.getAttribute("id")+'" ';var F=this.getParams();for(var E in F){B+=[E]+'="'+F[E]+'" '}var D=this.getVariablePairs().join("&");if(D.length>0){B+='flashvars="'+D+'"'}B+="/>"}else{if(this.getAttribute("doExpressInstall")){this.addVariable("MMplayerType","ActiveX");this.setAttribute("swf",this.xiSWFPath)}B='<object id="'+this.getAttribute("id")+'" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="'+this.getAttribute("width")+'" height="'+this.getAttribute("height")+'" style="'+this.getAttribute("style")+'">';B+='<param name="movie" value="'+this.getAttribute("swf")+'" />';var C=this.getParams();for(var E in C){B+='<param name="'+E+'" value="'+C[E]+'" />'}var A=this.getVariablePairs().join("&");if(A.length>0){B+='<param name="flashvars" value="'+A+'" />'}B+="</object>"}return B},write:function(B){if(this.getAttribute("useExpressInstall")){var A=new deconcept.PlayerVersion([6,0,65]);if(this.installedVer.versionIsValid(A)&&!this.installedVer.versionIsValid(this.getAttribute("version"))){this.setAttribute("doExpressInstall",true);this.addVariable("MMredirectURL",escape(this.getAttribute("xiRedirectUrl")));document.title=document.title.slice(0,47)+" - Flash Player Installation";this.addVariable("MMdoctitle",document.title)}}if(this.skipDetect||this.getAttribute("doExpressInstall")||this.installedVer.versionIsValid(this.getAttribute("version"))){var C=(typeof B=="string")?document.getElementById(B):B;C.innerHTML=this.getSWFHTML();return true}else{if(this.getAttribute("redirectUrl")!=""){document.location.replace(this.getAttribute("redirectUrl"))}}return false}};deconcept.SWFObjectUtil.getPlayerVersion=function(){var E=new deconcept.PlayerVersion([0,0,0]);if(navigator.plugins&&navigator.mimeTypes.length){var A=navigator.plugins["Shockwave Flash"];if(A&&A.description){E=new deconcept.PlayerVersion(A.description.replace(/([a-zA-Z]|\s)+/,"").replace(/(\s+r|\s+b[0-9]+)/,".").split("."))}}else{if(navigator.userAgent&&navigator.userAgent.indexOf("Windows CE")>=0){var B=1;var C=3;while(B){try{C++;B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash."+C);E=new deconcept.PlayerVersion([C,0,0])}catch(D){B=null}}}else{try{var B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7")}catch(D){try{var B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");E=new deconcept.PlayerVersion([6,0,21]);B.AllowScriptAccess="always"}catch(D){if(E.major==6){return E}}try{B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash")}catch(D){}}if(B!=null){E=new deconcept.PlayerVersion(B.GetVariable("$version").split(" ")[1].split(","))}}}return E};deconcept.PlayerVersion=function(A){this.major=A[0]!=null?parseInt(A[0]):0;this.minor=A[1]!=null?parseInt(A[1]):0;this.rev=A[2]!=null?parseInt(A[2]):0};deconcept.PlayerVersion.prototype.versionIsValid=function(A){if(this.major<A.major){return false}if(this.major>A.major){return true}if(this.minor<A.minor){return false}if(this.minor>A.minor){return true}if(this.rev<A.rev){return false}return true};deconcept.util={getRequestParameter:function(C){var D=document.location.search||document.location.hash;if(C==null){return D}if(D){var B=D.substring(1).split("&");for(var A=0;A<B.length;A++){if(B[A].substring(0,B[A].indexOf("="))==C){return B[A].substring((B[A].indexOf("=")+1))}}}return""}};deconcept.SWFObjectUtil.cleanupSWFs=function(){var B=document.getElementsByTagName("OBJECT");for(var C=B.length-1;C>=0;C--){B[C].style.display="none";for(var A in B[C]){if(typeof B[C][A]=="function"){B[C][A]=function(){}}}}};if(deconcept.SWFObject.doPrepUnload){if(!deconcept.unloadSet){deconcept.SWFObjectUtil.prepUnload=function(){__flash_unloadHandler=function(){};__flash_savedUnloadHandler=function(){};window.attachEvent("onunload",deconcept.SWFObjectUtil.cleanupSWFs)};window.attachEvent("onbeforeunload",deconcept.SWFObjectUtil.prepUnload);deconcept.unloadSet=true}}if(!document.getElementById&&document.all){document.getElementById=function(A){return document.all[A]}}var getQueryParamValue=deconcept.util.getRequestParameter;var FlashObject=deconcept.SWFObject;var SWFObject=deconcept.SWFObject;function f_clientWidth(){return f_filterResults(window.innerWidth?window.innerWidth:0,document.documentElement?document.documentElement.clientWidth:0,document.body?document.body.clientWidth:0)}function f_clientHeight(){return f_filterResults(window.innerHeight?window.innerHeight:0,document.documentElement?document.documentElement.clientHeight:0,document.body?document.body.clientHeight:0)}function f_scrollLeft(){return f_filterResults(window.pageXOffset?window.pageXOffset:0,document.documentElement?document.documentElement.scrollLeft:0,document.body?document.body.scrollLeft:0)}function f_scrollTop(){return f_filterResults(window.pageYOffset?window.pageYOffset:0,document.documentElement?document.documentElement.scrollTop:0,document.body?document.body.scrollTop:0)}function f_filterResults(D,B,A){var C=D?D:0;if(B&&(!C||(C>B))){C=B}return A&&(!C||(C>A))?A:C};var _productionMode=false;var HmEvents={eventTutorInitialized:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorInitialized.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorInitialized.listeners;for(var A=0;A<B.length;A++){B[A]()}}},eventTutorLastStep:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorLastStep.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorLastStep.listeners;for(var A=0;A<B.length;A++){B[A]()}}},eventTutorWidgetComplete:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorWidgetComplete.listeners;A[A.length]=B},fire:function(C){var B=HmEvents.eventTutorWidgetComplete.listeners;for(var A=0;A<B.length;A++){B[A](null,[C])}}},eventTutorSetComplete:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorSetComplete.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorSetComplete.listeners;for(var A=0;A<B.length;A++){B[A]()}}}};function $get(A){return document.getElementById(A)}HmEvents.eventTutorInitialized.subscribe(function(){try{MathJax.Hub.Queue(["Typeset",MathJax.Hub])}catch(A){alert("MathJAX processing failed: "+A)}});function setStepsInfoHelp(){}function resetStepsInfo(){}function getNextMoveTo(){}var TutorManager={currentRealStep:-1,currentStepUnit:-1,stepUnitsMo:[],stepUnits:[],steps:[],pid:"",stepUnit:null,tutorData:null,initializeTutor:function(B,A,G,D,E,C){TutorManager.pid=B;TutorManager.jsonConfig=A;TutorManager.currentRealStep=-1;TutorManager.currentStepUnit=-1;TutorManager.stepText=D;TutorManager.solutionData=G;TutorManager.loadTutorData(G,D);TutorManager.analyzeLoadedData();enabledNext(true);var F=createNewSolutionMessageContext(B,A);TutorDynamic.initializeTutor(F)},showMessage:function(B){var A=$get("tutor_message");A.innerHTML=B;setTimeout(function(){A.innerHTML="&nbsp;"},2000)},showNextStep:function(){if(TutorManager.currentStepUnit+1<TutorManager.stepUnits.length){TutorManager.currentStepUnit++;showStepUnit(TutorManager.currentStepUnit)}else{TutorManager.showMessage("no more steps")}},showPreviousStep:function(){if(TutorManager.currentStepUnit<0){TutorManager.showMessage("No previous step");return }else{while(TutorManager.currentStepUnit>-1){var A=TutorManager.stepUnits[TutorManager.currentStepUnit].ele;if(TutorManager.stepUnits[TutorManager.currentStepUnit].realNum!=TutorManager.currentRealStep){TutorManager.currentRealStep=TutorManager.stepUnits[TutorManager.currentStepUnit].realNum;break}A.style.display="none";TutorManager.currentStepUnit--}if(TutorManager.currentStepUnit==0){TutorManager.currentStepUnit=-1;window.scrollTo(0,0)}if(TutorManager.currentStepUnit>-1){setAsCurrent(TutorManager.stepUnits[TutorManager.currentStepUnit].ele)}setButtonState();scrollToStep(TutorManager.currentStepUnit);return false}},loadTutorData:function(solutionData,stepText){try{TutorManager.tutorData=eval("("+solutionData+")");processTutorData(TutorManager.tutorData,stepText)}catch(e){alert(e)}},resetTutor:function(){var A=document.getElementById("tutor_raw_steps_wrapper");A.innerHTML=TutorManager.stepText;TutorManager.currentRealStep=-1;TutorManager.currentStepUnit=-1;TutorManager.loadTutorData(TutorManager.solutionData,TutorManager.stepText);TutorManager.analyzeLoadedData();setButtonState();TutorDynamic.resetTutor()},analyzeLoadedData:function(){TutorManager.stepUnits=[];TutorManager.steps=[];var I=100;for(var J=0;J<I;J++){var E=_getStepUnit(J);if(E==null){break}var B=E.getAttribute("id");var A=TutorManager.stepUnits.length;var C=E.getAttribute("steprole");var F=E.getAttribute("steptype");var G=parseInt(E.getAttribute("realstep"));var H=new StepUnit(B,A,F,C,G,E);TutorManager.stepUnits[TutorManager.stepUnits.length]=H;var D=TutorManager.steps[G];if(D==null){D=new Step(G);TutorManager.steps[G]=D}D.stepUnits[D.stepUnits.length]=H}return TutorManager.stepUnits.length},backToLesson:function(){gwt_backToLesson()},newProblem:function(){gwt_tutorNewProblem()},showWhiteboard:function(){}};function setButtonState(){setState("step",TutorManager.currentStepUnit<(TutorManager.stepUnits.length-1));setState("back",TutorManager.currentStepUnit>-1)}function enabledPrevious(A){enabledButton("steps_prev",A)}function enabledNext(A){enabledButton("steps_next",A)}function enabledButton(B,C){var A="sexybutton ";if(!C){A+=" disabled"}$get(B).className=A}function StepUnit(F,E,B,A,D,C){this.id=F;this.stepUnitNum=E;this.type=B;this.roleType=A;this.realNum=D;this.ele=C}function Step(A){this.realNum=A;this.stepUnits=new Array()}function _getStepUnit(A){return _getElement("stepunit",A)}function _getHintUnit(A){return _getElement("hintunit",A)}function _getFigureUnit(A){return _getElement("figure",A)}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setAsNotCurrent(A){A.style.backgroundColor="#E2E2E2"}function _getElement(A,B){var C=A+"-"+B;return document.getElementById(C)}function showStepUnit(A){if(A<0){return }try{var C=TutorManager.stepUnits[A].ele;if(C==null){return false}C.style.display="block";if(C.getAttribute("steprole")=="step"){setAsCurrent(C)}setStepTitle(A,C);var E=_getFigureUnit(A);if(E!=null){if(A==0){E.style.display="block"}else{var F=findPreviousFigureUnit(A);if(F!=null&&F.src==E.src){E.style.display="none"}else{E.style.display="block"}}}for(B=A-1;B>-1;B--){if(TutorManager.stepUnits[B].roleType=="hint"){TutorManager.stepUnits[B].ele.style.display="none"}else{setAsNotCurrent(TutorManager.stepUnits[B].ele)}}for(var B=A+1;B<TutorManager.stepUnits.length;B++){if(TutorManager.stepUnits[B].roleType=="hint"){TutorManager.stepUnits[B].ele.style.display="none"}else{setAsNotCurrent(TutorManager.stepUnits[B].ele)}}TutorManager.currentStepUnit=A;TutorManager.currentRealStep=TutorManager.stepUnits[A].realNum;setButtonState();scrollToStep(A)}catch(D){alert("Error showing step: "+D)}return true}function setAsCurrent(A){A.style.backgroundColor="#F1F1F1"}function setStepTitle(A,C){stepTitle=document.getElementById("step_title-"+A);if(stepTitle){var B=C.getAttribute("steprole");if(B&&B=="step"){stepTitle.innerHTML="Step "+(parseInt(C.getAttribute("realstep"))+1);stepTitle.className="step_title_step"}else{stepTitle.innerHTML="Hint";stepTitle.className="step_title_hint"}}}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setState(B,A){if(B=="step"){enabledNext(A);if(!A){HmEvents.eventTutorLastStep.fire()}}else{if(B=="back"){enabledPrevious(A)}}}function scrollToStep(C){var B=document.getElementById("scrollTo-button");if(B){var G=DL_GetElementTop(B);var E=getViewableSize();var A=getScrollXY();var F=A[1];var D=E[1];var H=D+F;if(true||G<F||G>H){gwt_scrollToBottomOfScrollPanel(G-D)}}}function hideAllSteps(){for(var A=0;A<TutorManager.stepUnits.length;A++){var B=TutorManager.stepUnits[A].ele;if(B==null){return }if(B.style.display!="none"){B.style.display="none"}}window.scrollTo(0,0)}function initializeExternalJs(){var A="control-floater";new FloatLayer(A,150,15,10);detach(A);alignControlFloater()}function alignControlFloater(){alignFloatLayers();setTimeout(alignControlFloater,2000)}function doQuestionResponseEnd(){}var _activeQuestion;function doQuestionResponse(A,D){var C=TutorManager.tutorData._strings_moArray[A];if(_activeQuestion){var B=document.createElement("div");B.className="questionResponseAnswer";B.innerHTML=C;_activeQuestion.parentNode.appendChild(B)}else{gwt_showMessage(C)}}HmEvents.eventTutorInitialized.subscribe(function(){var H=document.getElementById("tutor_raw_steps_wrapper");if(H==null){return }var B=H.getElementsByTagName("div");var A=B.length;for(var E=0;E<A;E++){var G=B.item(E);if(G.className=="question_guess"){var F=G.getElementsByTagName("img");var D=F.item(0);var C=D.onmouseout=null;D.onmouseoverDeferred=D.onmouseover;D.onmouseover=null;D.onclick=function(I){var J=(I)?I:window.event;var K=J.srcElement?J.srcElement:J.target;_activeQuestion=K;if(!K.onmouseoverDeferred){alert("error: no deferred move event");return }K.onclick=null;K.onmouseoverDeferred()}}}});function createNewSolutionMessageContext(pid,jsonConfig){var loc=new SolutionMessageLocation("solution",pid);var mc=new MessageContext(loc);if(jsonConfig){try{mc.jsonConfig=eval("("+jsonConfig+")")}catch(e){alert("could not process solution "+pid+" config: "+jsonConfig)}}return mc}function MessageContext(C,A,B){this.messageLocation=C;this.collabPointer=A;this.message=B}MessageContext.prototype.toString=function(){return this.messageLocation+", "+this.collabPointer};function SolutionMessageLocation(A,B,C){this.type=A;this.locationString1=B;this.locationInt1=C;this.complete=false}function gotoGUID(B,A){TutorManager.resetTutor()};function DL_GetElementLeft(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetLeft;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientLeft}}A+=D.offsetLeft;D=D.offsetParent}return A}function DL_GetElementTop(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetTop;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientTop}}A+=D.offsetTop;D=D.offsetParent}return A}function getViewableSize(){var B=0,A=0;if(typeof (window.innerWidth)=="number"){B=window.innerWidth;A=window.innerHeight}else{if(document.documentElement&&(document.documentElement.clientWidth||document.documentElement.clientHeight)){B=document.documentElement.clientWidth;A=document.documentElement.clientHeight}else{if(document.body&&(document.body.clientWidth||document.body.clientHeight)){B=document.body.clientWidth;A=document.body.clientHeight}}}a=[B,A];return a}function getScrollXY(){var B=0,A=0;if(typeof (window.pageYOffset)=="number"){A=window.pageYOffset;B=window.pageXOffset}else{if(document.body&&(document.body.scrollLeft||document.body.scrollTop)){A=document.body.scrollTop;B=document.body.scrollLeft}else{if(document.documentElement&&(document.documentElement.scrollLeft||document.documentElement.scrollTop)){A=document.documentElement.scrollTop;B=document.documentElement.scrollLeft}}}return[B,A]}function _addEvent(E,D,B,A){if(E.addEventListener){E.addEventListener(D,B,A);return true}else{if(E.attachEvent){var C=E.attachEvent("on"+D,B);return C}else{alert("Handler could not be attached")}}}function _removeEvent(E,D,B,A){if(E.removeEventListener){E.removeEventListener(D,B,A);return true}else{if(E.detachEvent){var C=E.detachEvent("on"+D,B);return C}else{alert("Handler could not be removed")}}}function hideDivOnMouseOut(A){var C,B;if(window.event){C=this;B=window.event.toElement}else{C=A.currentTarget;B=A.relatedTarget}if(C!=B){if(!contains(C,B)){C.style.display="none"}}}function contains(B,A){while(A.parentNode){A=A.parentNode;if(A==B){return true}}return false}function grabComputedStyle(B,A){if(document.defaultView&&document.defaultView.getComputedStyle){return document.defaultView.getComputedStyle(B,null).getPropertyValue(A)}else{if(B.currentStyle){return B.currentStyle[A]}else{return null}}}function grabComputedHeight(B){var A=grabComputedStyle(B,"height");if(A!=null){if(A=="auto"){if(B.offsetHeight){A=B.offsetHeight}}A=parseInt(A)}return A}function grabComputedWidth(B){var A=grabComputedStyle(B,"width");if(A!=null){if(A.indexOf("px")!=-1){A=A.substring(0,A.indexOf("px"))}if(A=="auto"){if(B.offsetWidth){A=B.offsetWidth}}}return A};if(!document.createElement("canvas").getContext){(function(){var AB=Math;var K=AB.round;var J=AB.sin;var W=AB.cos;var e=AB.abs;var n=AB.sqrt;var D=10;var F=D/2;var V=+navigator.userAgent.match(/MSIE ([\d.]+)?/)[1];function U(){return this.context_||(this.context_=new a(this))}var P=Array.prototype.slice;function G(i,j,m){var Z=P.call(arguments,2);return function(){return i.apply(j,Z.concat(P.call(arguments)))}}function AF(Z){return String(Z).replace(/&/g,"&amp;").replace(/"/g,"&quot;")}function z(j,i,Z){if(!j.namespaces[i]){j.namespaces.add(i,Z,"#default#VML")}}function s(i){z(i,"g_vml_","urn:schemas-microsoft-com:vml");z(i,"g_o_","urn:schemas-microsoft-com:office:office");if(!i.styleSheets.ex_canvas_){var Z=i.createStyleSheet();Z.owningElement.id="ex_canvas_";Z.cssText="canvas{display:inline-block;overflow:hidden;text-align:left;width:300px;height:150px}"}}s(document);var E={init:function(Z){var i=Z||document;i.createElement("canvas");i.attachEvent("onreadystatechange",G(this.init_,this,i))},init_:function(m){var j=m.getElementsByTagName("canvas");for(var Z=0;Z<j.length;Z++){this.initElement(j[Z])}},initElement:function(i){if(!i.getContext){i.getContext=U;s(i.ownerDocument);i.innerHTML="";i.attachEvent("onpropertychange",T);i.attachEvent("onresize",x);var Z=i.attributes;if(Z.width&&Z.width.specified){i.style.width=Z.width.nodeValue+"px"}else{i.width=i.clientWidth}if(Z.height&&Z.height.specified){i.style.height=Z.height.nodeValue+"px"}else{i.height=i.clientHeight}}return i}};function T(i){var Z=i.srcElement;switch(i.propertyName){case"width":Z.getContext().clearRect();Z.style.width=Z.attributes.width.nodeValue+"px";Z.firstChild.style.width=Z.clientWidth+"px";break;case"height":Z.getContext().clearRect();Z.style.height=Z.attributes.height.nodeValue+"px";Z.firstChild.style.height=Z.clientHeight+"px";break}}function x(i){var Z=i.srcElement;if(Z.firstChild){Z.firstChild.style.width=Z.clientWidth+"px";Z.firstChild.style.height=Z.clientHeight+"px"}}E.init();var I=[];for(var AE=0;AE<16;AE++){for(var AD=0;AD<16;AD++){I[AE*16+AD]=AE.toString(16)+AD.toString(16)}}function X(){return[[1,0,0],[0,1,0],[0,0,1]]}function g(m,j){var i=X();for(var Z=0;Z<3;Z++){for(var AH=0;AH<3;AH++){var p=0;for(var AG=0;AG<3;AG++){p+=m[Z][AG]*j[AG][AH]}i[Z][AH]=p}}return i}function R(i,Z){Z.fillStyle=i.fillStyle;Z.lineCap=i.lineCap;Z.lineJoin=i.lineJoin;Z.lineWidth=i.lineWidth;Z.miterLimit=i.miterLimit;Z.shadowBlur=i.shadowBlur;Z.shadowColor=i.shadowColor;Z.shadowOffsetX=i.shadowOffsetX;Z.shadowOffsetY=i.shadowOffsetY;Z.strokeStyle=i.strokeStyle;Z.globalAlpha=i.globalAlpha;Z.font=i.font;Z.textAlign=i.textAlign;Z.textBaseline=i.textBaseline;Z.arcScaleX_=i.arcScaleX_;Z.arcScaleY_=i.arcScaleY_;Z.lineScale_=i.lineScale_}var B={aliceblue:"#F0F8FF",antiquewhite:"#FAEBD7",aquamarine:"#7FFFD4",azure:"#F0FFFF",beige:"#F5F5DC",bisque:"#FFE4C4",black:"#000000",blanchedalmond:"#FFEBCD",blueviolet:"#8A2BE2",brown:"#A52A2A",burlywood:"#DEB887",cadetblue:"#5F9EA0",chartreuse:"#7FFF00",chocolate:"#D2691E",coral:"#FF7F50",cornflowerblue:"#6495ED",cornsilk:"#FFF8DC",crimson:"#DC143C",cyan:"#00FFFF",darkblue:"#00008B",darkcyan:"#008B8B",darkgoldenrod:"#B8860B",darkgray:"#A9A9A9",darkgreen:"#006400",darkgrey:"#A9A9A9",darkkhaki:"#BDB76B",darkmagenta:"#8B008B",darkolivegreen:"#556B2F",darkorange:"#FF8C00",darkorchid:"#9932CC",darkred:"#8B0000",darksalmon:"#E9967A",darkseagreen:"#8FBC8F",darkslateblue:"#483D8B",darkslategray:"#2F4F4F",darkslategrey:"#2F4F4F",darkturquoise:"#00CED1",darkviolet:"#9400D3",deeppink:"#FF1493",deepskyblue:"#00BFFF",dimgray:"#696969",dimgrey:"#696969",dodgerblue:"#1E90FF",firebrick:"#B22222",floralwhite:"#FFFAF0",forestgreen:"#228B22",gainsboro:"#DCDCDC",ghostwhite:"#F8F8FF",gold:"#FFD700",goldenrod:"#DAA520",grey:"#808080",greenyellow:"#ADFF2F",honeydew:"#F0FFF0",hotpink:"#FF69B4",indianred:"#CD5C5C",indigo:"#4B0082",ivory:"#FFFFF0",khaki:"#F0E68C",lavender:"#E6E6FA",lavenderblush:"#FFF0F5",lawngreen:"#7CFC00",lemonchiffon:"#FFFACD",lightblue:"#ADD8E6",lightcoral:"#F08080",lightcyan:"#E0FFFF",lightgoldenrodyellow:"#FAFAD2",lightgreen:"#90EE90",lightgrey:"#D3D3D3",lightpink:"#FFB6C1",lightsalmon:"#FFA07A",lightseagreen:"#20B2AA",lightskyblue:"#87CEFA",lightslategray:"#778899",lightslategrey:"#778899",lightsteelblue:"#B0C4DE",lightyellow:"#FFFFE0",limegreen:"#32CD32",linen:"#FAF0E6",magenta:"#FF00FF",mediumaquamarine:"#66CDAA",mediumblue:"#0000CD",mediumorchid:"#BA55D3",mediumpurple:"#9370DB",mediumseagreen:"#3CB371",mediumslateblue:"#7B68EE",mediumspringgreen:"#00FA9A",mediumturquoise:"#48D1CC",mediumvioletred:"#C71585",midnightblue:"#191970",mintcream:"#F5FFFA",mistyrose:"#FFE4E1",moccasin:"#FFE4B5",navajowhite:"#FFDEAD",oldlace:"#FDF5E6",olivedrab:"#6B8E23",orange:"#FFA500",orangered:"#FF4500",orchid:"#DA70D6",palegoldenrod:"#EEE8AA",palegreen:"#98FB98",paleturquoise:"#AFEEEE",palevioletred:"#DB7093",papayawhip:"#FFEFD5",peachpuff:"#FFDAB9",peru:"#CD853F",pink:"#FFC0CB",plum:"#DDA0DD",powderblue:"#B0E0E6",rosybrown:"#BC8F8F",royalblue:"#4169E1",saddlebrown:"#8B4513",salmon:"#FA8072",sandybrown:"#F4A460",seagreen:"#2E8B57",seashell:"#FFF5EE",sienna:"#A0522D",skyblue:"#87CEEB",slateblue:"#6A5ACD",slategray:"#708090",slategrey:"#708090",snow:"#FFFAFA",springgreen:"#00FF7F",steelblue:"#4682B4",tan:"#D2B48C",thistle:"#D8BFD8",tomato:"#FF6347",turquoise:"#40E0D0",violet:"#EE82EE",wheat:"#F5DEB3",whitesmoke:"#F5F5F5",yellowgreen:"#9ACD32"};function l(i){var m=i.indexOf("(",3);var Z=i.indexOf(")",m+1);var j=i.substring(m+1,Z).split(",");if(j.length!=4||i.charAt(3)!="a"){j[3]=1}return j}function C(Z){return parseFloat(Z)/100}function N(i,j,Z){return Math.min(Z,Math.max(j,i))}function f(AG){var Z,AI,AJ,AH,AK,m;AH=parseFloat(AG[0])/360%360;if(AH<0){AH++}AK=N(C(AG[1]),0,1);m=N(C(AG[2]),0,1);if(AK==0){Z=AI=AJ=m}else{var i=m<0.5?m*(1+AK):m+AK-m*AK;var j=2*m-i;Z=A(j,i,AH+1/3);AI=A(j,i,AH);AJ=A(j,i,AH-1/3)}return"#"+I[Math.floor(Z*255)]+I[Math.floor(AI*255)]+I[Math.floor(AJ*255)]}function A(i,Z,j){if(j<0){j++}if(j>1){j--}if(6*j<1){return i+(Z-i)*6*j}else{if(2*j<1){return Z}else{if(3*j<2){return i+(Z-i)*(2/3-j)*6}else{return i}}}}var Y={};function c(Z){if(Z in Y){return Y[Z]}var AG,p=1;Z=String(Z);if(Z.charAt(0)=="#"){AG=Z}else{if(/^rgb/.test(Z)){var m=l(Z);var AG="#",AH;for(var j=0;j<3;j++){if(m[j].indexOf("%")!=-1){AH=Math.floor(C(m[j])*255)}else{AH=+m[j]}AG+=I[N(AH,0,255)]}p=+m[3]}else{if(/^hsl/.test(Z)){var m=l(Z);AG=f(m);p=m[3]}else{AG=B[Z]||Z}}}return Y[Z]={color:AG,alpha:p}}var L={style:"normal",variant:"normal",weight:"normal",size:10,family:"sans-serif"};var k={};function b(Z){if(k[Z]){return k[Z]}var m=document.createElement("div");var j=m.style;try{j.font=Z}catch(i){}return k[Z]={style:j.fontStyle||L.style,variant:j.fontVariant||L.variant,weight:j.fontWeight||L.weight,size:j.fontSize||L.size,family:j.fontFamily||L.family}}function Q(j,i){var Z={};for(var AH in j){Z[AH]=j[AH]}var AG=parseFloat(i.currentStyle.fontSize),m=parseFloat(j.size);if(typeof j.size=="number"){Z.size=j.size}else{if(j.size.indexOf("px")!=-1){Z.size=m}else{if(j.size.indexOf("em")!=-1){Z.size=AG*m}else{if(j.size.indexOf("%")!=-1){Z.size=(AG/100)*m}else{if(j.size.indexOf("pt")!=-1){Z.size=m/0.75}else{Z.size=AG}}}}}Z.size*=0.981;return Z}function AC(Z){return Z.style+" "+Z.variant+" "+Z.weight+" "+Z.size+"px "+Z.family}var O={butt:"flat",round:"round"};function t(Z){return O[Z]||"square"}function a(Z){this.m_=X();this.mStack_=[];this.aStack_=[];this.currentPath_=[];this.strokeStyle="#000";this.fillStyle="#000";this.lineWidth=1;this.lineJoin="miter";this.lineCap="butt";this.miterLimit=D*1;this.globalAlpha=1;this.font="10px sans-serif";this.textAlign="left";this.textBaseline="alphabetic";this.canvas=Z;var j="width:"+Z.clientWidth+"px;height:"+Z.clientHeight+"px;overflow:hidden;position:absolute";var i=Z.ownerDocument.createElement("div");i.style.cssText=j;Z.appendChild(i);var m=i.cloneNode(false);m.style.backgroundColor="red";m.style.filter="alpha(opacity=0)";Z.appendChild(m);this.element_=i;this.arcScaleX_=1;this.arcScaleY_=1;this.lineScale_=1}var M=a.prototype;M.clearRect=function(){if(this.textMeasureEl_){this.textMeasureEl_.removeNode(true);this.textMeasureEl_=null}this.element_.innerHTML=""};M.beginPath=function(){this.currentPath_=[]};M.moveTo=function(i,Z){var j=w(this,i,Z);this.currentPath_.push({type:"moveTo",x:j.x,y:j.y});this.currentX_=j.x;this.currentY_=j.y};M.lineTo=function(i,Z){var j=w(this,i,Z);this.currentPath_.push({type:"lineTo",x:j.x,y:j.y});this.currentX_=j.x;this.currentY_=j.y};M.bezierCurveTo=function(j,i,AK,AJ,AI,AG){var Z=w(this,AI,AG);var AH=w(this,j,i);var m=w(this,AK,AJ);h(this,AH,m,Z)};function h(Z,m,j,i){Z.currentPath_.push({type:"bezierCurveTo",cp1x:m.x,cp1y:m.y,cp2x:j.x,cp2y:j.y,x:i.x,y:i.y});Z.currentX_=i.x;Z.currentY_=i.y}M.quadraticCurveTo=function(AI,j,i,Z){var AH=w(this,AI,j);var AG=w(this,i,Z);var AJ={x:this.currentX_+2/3*(AH.x-this.currentX_),y:this.currentY_+2/3*(AH.y-this.currentY_)};var m={x:AJ.x+(AG.x-this.currentX_)/3,y:AJ.y+(AG.y-this.currentY_)/3};h(this,AJ,m,AG)};M.arc=function(AL,AJ,AK,AG,i,j){AK*=D;var AP=j?"at":"wa";var AM=AL+W(AG)*AK-F;var AO=AJ+J(AG)*AK-F;var Z=AL+W(i)*AK-F;var AN=AJ+J(i)*AK-F;if(AM==Z&&!j){AM+=0.125}var m=w(this,AL,AJ);var AI=w(this,AM,AO);var AH=w(this,Z,AN);this.currentPath_.push({type:AP,x:m.x,y:m.y,radius:AK,xStart:AI.x,yStart:AI.y,xEnd:AH.x,yEnd:AH.y})};M.rect=function(j,i,Z,m){this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath()};M.strokeRect=function(j,i,Z,m){var p=this.currentPath_;this.beginPath();this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath();this.stroke();this.currentPath_=p};M.fillRect=function(j,i,Z,m){var p=this.currentPath_;this.beginPath();this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath();this.fill();this.currentPath_=p};M.createLinearGradient=function(i,m,Z,j){var p=new v("gradient");p.x0_=i;p.y0_=m;p.x1_=Z;p.y1_=j;return p};M.createRadialGradient=function(m,AG,j,i,p,Z){var AH=new v("gradientradial");AH.x0_=m;AH.y0_=AG;AH.r0_=j;AH.x1_=i;AH.y1_=p;AH.r1_=Z;return AH};M.drawImage=function(AR,m){var AK,AI,AM,AY,AP,AN,AT,Aa;var AL=AR.runtimeStyle.width;var AQ=AR.runtimeStyle.height;AR.runtimeStyle.width="auto";AR.runtimeStyle.height="auto";var AJ=AR.width;var AW=AR.height;AR.runtimeStyle.width=AL;AR.runtimeStyle.height=AQ;if(arguments.length==3){AK=arguments[1];AI=arguments[2];AP=AN=0;AT=AM=AJ;Aa=AY=AW}else{if(arguments.length==5){AK=arguments[1];AI=arguments[2];AM=arguments[3];AY=arguments[4];AP=AN=0;AT=AJ;Aa=AW}else{if(arguments.length==9){AP=arguments[1];AN=arguments[2];AT=arguments[3];Aa=arguments[4];AK=arguments[5];AI=arguments[6];AM=arguments[7];AY=arguments[8]}else{throw Error("Invalid number of arguments")}}}if(AR.tagName=="canvas"){var i=document.createElement("div");i.style.position="absolute";i.style.left=AK+"px";i.style.top=AI+"px";i.innerHTML=AR.outerHTML;this.element_.insertAdjacentHTML("BeforeEnd",i.outerHTML);return }var AZ=w(this,AK,AI);var p=AT/2;var j=Aa/2;var AX=[];var Z=10;var AH=10;AX.push(" <g_vml_:group",' coordsize="',D*Z,",",D*AH,'"',' coordorigin="0,0"',' style="width:',Z,"px;height:",AH,"px;position:absolute;");if(this.m_[0][0]!=1||this.m_[0][1]||this.m_[1][1]!=1||this.m_[1][0]){var AG=[];AG.push("M11=",this.m_[0][0],",","M12=",this.m_[1][0],",","M21=",this.m_[0][1],",","M22=",this.m_[1][1],",","Dx=",K(AZ.x/D),",","Dy=",K(AZ.y/D),"");var AV=AZ;var AU=w(this,AK+AM,AI);var AS=w(this,AK,AI+AY);var AO=w(this,AK+AM,AI+AY);AV.x=AB.max(AV.x,AU.x,AS.x,AO.x);AV.y=AB.max(AV.y,AU.y,AS.y,AO.y);AX.push("padding:0 ",K(AV.x/D),"px ",K(AV.y/D),"px 0;filter:progid:DXImageTransform.Microsoft.Matrix(",AG.join(""),", sizingmethod='clip');")}else{AX.push("top:",K(AZ.y/D),"px;left:",K(AZ.x/D),"px;")}AX.push(' ">','<g_vml_:image src="',AR.src,'"',' style="width:',D*AM,"px;"," height:",D*AY,'px"',' cropleft="',AP/AJ,'"',' croptop="',AN/AW,'"',' cropright="',(AJ-AP-AT)/AJ,'"',' cropbottom="',(AW-AN-Aa)/AW,'"'," />","</g_vml_:group>");this.element_.insertAdjacentHTML("BeforeEnd",AX.join(""))};M.stroke=function(AL){var AJ=[];var m=false;var j=10;var AM=10;AJ.push("<g_vml_:shape",' filled="',!!AL,'"',' style="position:absolute;width:',j,"px;height:",AM,'px;"',' coordorigin="0,0"',' coordsize="',D*j,",",D*AM,'"',' stroked="',!AL,'"',' path="');var AN=false;var AG={x:null,y:null};var AK={x:null,y:null};for(var AH=0;AH<this.currentPath_.length;AH++){var Z=this.currentPath_[AH];var AI;switch(Z.type){case"moveTo":AI=Z;AJ.push(" m ",K(Z.x),",",K(Z.y));break;case"lineTo":AJ.push(" l ",K(Z.x),",",K(Z.y));break;case"close":AJ.push(" x ");Z=null;break;case"bezierCurveTo":AJ.push(" c ",K(Z.cp1x),",",K(Z.cp1y),",",K(Z.cp2x),",",K(Z.cp2y),",",K(Z.x),",",K(Z.y));break;case"at":case"wa":AJ.push(" ",Z.type," ",K(Z.x-this.arcScaleX_*Z.radius),",",K(Z.y-this.arcScaleY_*Z.radius)," ",K(Z.x+this.arcScaleX_*Z.radius),",",K(Z.y+this.arcScaleY_*Z.radius)," ",K(Z.xStart),",",K(Z.yStart)," ",K(Z.xEnd),",",K(Z.yEnd));break}if(Z){if(AG.x==null||Z.x<AG.x){AG.x=Z.x}if(AK.x==null||Z.x>AK.x){AK.x=Z.x}if(AG.y==null||Z.y<AG.y){AG.y=Z.y}if(AK.y==null||Z.y>AK.y){AK.y=Z.y}}}AJ.push(' ">');if(!AL){S(this,AJ)}else{d(this,AJ,AG,AK)}AJ.push("</g_vml_:shape>");this.element_.insertAdjacentHTML("beforeEnd",AJ.join(""))};function S(j,AG){var i=c(j.strokeStyle);var m=i.color;var p=i.alpha*j.globalAlpha;var Z=j.lineScale_*j.lineWidth;if(Z<1){p*=Z}AG.push("<g_vml_:stroke",' opacity="',p,'"',' joinstyle="',j.lineJoin,'"',' miterlimit="',j.miterLimit,'"',' endcap="',t(j.lineCap),'"',' weight="',Z,'px"',' color="',m,'" />')}function d(AQ,AI,Aj,AR){var AJ=AQ.fillStyle;var Aa=AQ.arcScaleX_;var AZ=AQ.arcScaleY_;var Z=AR.x-Aj.x;var m=AR.y-Aj.y;if(AJ instanceof v){var AN=0;var Ae={x:0,y:0};var AW=0;var AM=1;if(AJ.type_=="gradient"){var AL=AJ.x0_/Aa;var j=AJ.y0_/AZ;var AK=AJ.x1_/Aa;var Al=AJ.y1_/AZ;var Ai=w(AQ,AL,j);var Ah=w(AQ,AK,Al);var AG=Ah.x-Ai.x;var p=Ah.y-Ai.y;AN=Math.atan2(AG,p)*180/Math.PI;if(AN<0){AN+=360}if(AN<0.000001){AN=0}}else{var Ai=w(AQ,AJ.x0_,AJ.y0_);Ae={x:(Ai.x-Aj.x)/Z,y:(Ai.y-Aj.y)/m};Z/=Aa*D;m/=AZ*D;var Ac=AB.max(Z,m);AW=2*AJ.r0_/Ac;AM=2*AJ.r1_/Ac-AW}var AU=AJ.colors_;AU.sort(function(Am,i){return Am.offset-i.offset});var AP=AU.length;var AT=AU[0].color;var AS=AU[AP-1].color;var AY=AU[0].alpha*AQ.globalAlpha;var AX=AU[AP-1].alpha*AQ.globalAlpha;var Ad=[];for(var Ag=0;Ag<AP;Ag++){var AO=AU[Ag];Ad.push(AO.offset*AM+AW+" "+AO.color)}AI.push('<g_vml_:fill type="',AJ.type_,'"',' method="none" focus="100%"',' color="',AT,'"',' color2="',AS,'"',' colors="',Ad.join(","),'"',' opacity="',AX,'"',' g_o_:opacity2="',AY,'"',' angle="',AN,'"',' focusposition="',Ae.x,",",Ae.y,'" />')}else{if(AJ instanceof u){if(Z&&m){var AH=-Aj.x;var Ab=-Aj.y;AI.push("<g_vml_:fill",' position="',AH/Z*Aa*Aa,",",Ab/m*AZ*AZ,'"',' type="tile"',' src="',AJ.src_,'" />')}}else{var Ak=c(AQ.fillStyle);var AV=Ak.color;var Af=Ak.alpha*AQ.globalAlpha;AI.push('<g_vml_:fill color="',AV,'" opacity="',Af,'" />')}}}M.fill=function(){this.stroke(true)};M.closePath=function(){this.currentPath_.push({type:"close"})};function w(i,p,j){var Z=i.m_;return{x:D*(p*Z[0][0]+j*Z[1][0]+Z[2][0])-F,y:D*(p*Z[0][1]+j*Z[1][1]+Z[2][1])-F}}M.save=function(){var Z={};R(this,Z);this.aStack_.push(Z);this.mStack_.push(this.m_);this.m_=g(X(),this.m_)};M.restore=function(){if(this.aStack_.length){R(this.aStack_.pop(),this);this.m_=this.mStack_.pop()}};function H(Z){return isFinite(Z[0][0])&&isFinite(Z[0][1])&&isFinite(Z[1][0])&&isFinite(Z[1][1])&&isFinite(Z[2][0])&&isFinite(Z[2][1])}function AA(i,Z,j){if(!H(Z)){return }i.m_=Z;if(j){var p=Z[0][0]*Z[1][1]-Z[0][1]*Z[1][0];i.lineScale_=n(e(p))}}M.translate=function(j,i){var Z=[[1,0,0],[0,1,0],[j,i,1]];AA(this,g(Z,this.m_),false)};M.rotate=function(i){var m=W(i);var j=J(i);var Z=[[m,j,0],[-j,m,0],[0,0,1]];AA(this,g(Z,this.m_),false)};M.scale=function(j,i){this.arcScaleX_*=j;this.arcScaleY_*=i;var Z=[[j,0,0],[0,i,0],[0,0,1]];AA(this,g(Z,this.m_),true)};M.transform=function(p,m,AH,AG,i,Z){var j=[[p,m,0],[AH,AG,0],[i,Z,1]];AA(this,g(j,this.m_),true)};M.setTransform=function(AG,p,AI,AH,j,i){var Z=[[AG,p,0],[AI,AH,0],[j,i,1]];AA(this,Z,true)};M.drawText_=function(AM,AK,AJ,AP,AI){var AO=this.m_,AS=1000,i=0,AR=AS,AH={x:0,y:0},AG=[];var Z=Q(b(this.font),this.element_);var j=AC(Z);var AT=this.element_.currentStyle;var p=this.textAlign.toLowerCase();switch(p){case"left":case"center":case"right":break;case"end":p=AT.direction=="ltr"?"right":"left";break;case"start":p=AT.direction=="rtl"?"right":"left";break;default:p="left"}switch(this.textBaseline){case"hanging":case"top":AH.y=Z.size/1.75;break;case"middle":break;default:case null:case"alphabetic":case"ideographic":case"bottom":AH.y=-Z.size/2.25;break}switch(p){case"right":i=AS;AR=0.05;break;case"center":i=AR=AS/2;break}var AQ=w(this,AK+AH.x,AJ+AH.y);AG.push('<g_vml_:line from="',-i,' 0" to="',AR,' 0.05" ',' coordsize="100 100" coordorigin="0 0"',' filled="',!AI,'" stroked="',!!AI,'" style="position:absolute;width:1px;height:1px;">');if(AI){S(this,AG)}else{d(this,AG,{x:-i,y:0},{x:AR,y:Z.size})}var AN=AO[0][0].toFixed(3)+","+AO[1][0].toFixed(3)+","+AO[0][1].toFixed(3)+","+AO[1][1].toFixed(3)+",0,0";var AL=K(AQ.x/D)+","+K(AQ.y/D);AG.push('<g_vml_:skew on="t" matrix="',AN,'" ',' offset="',AL,'" origin="',i,' 0" />','<g_vml_:path textpathok="true" />','<g_vml_:textpath on="true" string="',AF(AM),'" style="v-text-align:',p,";font:",AF(j),'" /></g_vml_:line>');this.element_.insertAdjacentHTML("beforeEnd",AG.join(""))};M.fillText=function(j,Z,m,i){this.drawText_(j,Z,m,i,false)};M.strokeText=function(j,Z,m,i){this.drawText_(j,Z,m,i,true)};M.measureText=function(j){if(!this.textMeasureEl_){var Z='<span style="position:absolute;top:-20000px;left:0;padding:0;margin:0;border:none;white-space:pre;"></span>';this.element_.insertAdjacentHTML("beforeEnd",Z);this.textMeasureEl_=this.element_.lastChild}var i=this.element_.ownerDocument;this.textMeasureEl_.innerHTML="";this.textMeasureEl_.style.font=this.font;this.textMeasureEl_.appendChild(i.createTextNode(j));return{width:this.textMeasureEl_.offsetWidth}};M.clip=function(){};M.arcTo=function(){};M.createPattern=function(i,Z){return new u(i,Z)};function v(Z){this.type_=Z;this.x0_=0;this.y0_=0;this.r0_=0;this.x1_=0;this.y1_=0;this.r1_=0;this.colors_=[]}v.prototype.addColorStop=function(i,Z){Z=c(Z);this.colors_.push({offset:i,color:Z.color,alpha:Z.alpha})};function u(i,Z){r(i);switch(Z){case"repeat":case null:case"":this.repetition_="repeat";break;case"repeat-x":case"repeat-y":case"no-repeat":this.repetition_=Z;break;default:o("SYNTAX_ERR")}this.src_=i.src;this.width_=i.width;this.height_=i.height}function o(Z){throw new q(Z)}function r(Z){if(!Z||Z.nodeType!=1||Z.tagName!="IMG"){o("TYPE_MISMATCH_ERR")}if(Z.readyState!="complete"){o("INVALID_STATE_ERR")}}function q(Z){this.code=this[Z];this.message=Z+": DOM Exception "+this.code}var y=q.prototype=new Error;y.INDEX_SIZE_ERR=1;y.DOMSTRING_SIZE_ERR=2;y.HIERARCHY_REQUEST_ERR=3;y.WRONG_DOCUMENT_ERR=4;y.INVALID_CHARACTER_ERR=5;y.NO_DATA_ALLOWED_ERR=6;y.NO_MODIFICATION_ALLOWED_ERR=7;y.NOT_FOUND_ERR=8;y.NOT_SUPPORTED_ERR=9;y.INUSE_ATTRIBUTE_ERR=10;y.INVALID_STATE_ERR=11;y.SYNTAX_ERR=12;y.INVALID_MODIFICATION_ERR=13;y.NAMESPACE_ERR=14;y.INVALID_ACCESS_ERR=15;y.VALIDATION_ERR=16;y.TYPE_MISMATCH_ERR=17;G_vmlCanvasManager=E;CanvasRenderingContext2D=a;CanvasGradient=v;CanvasPattern=u;DOMException=q})()};var Whiteboard=(function(){var wb={};var canvas,context,pencil_btn,rect_btn,width,height,x,y,clickX,clickY,penDown=false;var origcanvas,origcontext,currentTool="pencil";var graphcanvas,graphcontext,topcanvas,topcontext,gr2D,nL,graphMode,gr2D_xp,gr2D_yp,nL_xp,nL_yp;var offX,offY,x0,y0,w0,h0,drawingLayer,drawcolor,rendering;var graphicData,tool_id;var scope=this;var isTouchEnabled=false;function renderText(xt,xp,yp){var txt=xt?xt:$get_Element("#content").value;var str=txt.split("\n");var x0=xp?xp:clickX;var y0=yp?yp:clickY;var ht=15;for(var i=0;i<str.length;i++){context.fillText(str[i],x0,y0);y0+=ht}updateCanvas();if(!xt){updateText(txt);sendData();$get_Element("#content").value="";$get_Element("#inputBox").style.display="none"}}function onkeyupHandler(){}function onkeydownHandler(_event){var event=_event?_event:window.event;if(currentTool=="text"&&event.keyCode==13){if(!event.shiftKey){if(event.preventDefault){event.preventDefault()}else{event.returnValue=false}renderText()}}}function resetButtonHighlite(){$get_Element("#button_text").style.border="1px solid #000000";$get_Element("#button_pencil").style.border="1px solid #000000";$get_Element("#button_line").style.border="1px solid #000000";$get_Element("#button_rect").style.border="1px solid #000000";$get_Element("#button_oval").style.border="1px solid #000000";$get_Element("#button_eraser").style.border="1px solid #000000"}function buttonHighlite(t){resetButtonHighlite();$get_Element("#button_"+t).style.border="2px solid #ff9900"}function viewport(){var e=window,a="inner";if(!("innerWidth" in window)){a="client";e=document.documentElement||document.body}return{width:e[a+"Width"],height:e[a+"Height"]}}function getDocHeight(){var D=document;return Math.max(Math.max(D.body.scrollHeight,D.documentElement.scrollHeight),Math.max(D.body.offsetHeight,D.documentElement.offsetHeight),Math.max(D.body.clientHeight,D.documentElement.clientHeight))}function touchStartFunction(event){event.preventDefault()}var touchMoveFunction=touchStartFunction;var _imageBaseDir="/gwt-resources/images/whiteboard/";var mainDoc;wb.initWhiteboard=function(mainDocIn){console.log("WHITEBOARD_INITIATED! - document object:"+mainDocIn);mainDoc=mainDocIn;canvas=$get_Element("#canvas");var siz=viewport();var docWidth=siz.width;var docHeight=siz.height;var topOff=$get_Element("#tools").offsetHeight+$get_Element("#tools").offsetTop+15;var leftOff=$get_Element("#tools").offsetLeft+15;origcanvas=$get_Element("#ocanvas");graphcanvas=$get_Element("#gcanvas");topcanvas=$get_Element("#tcanvas");canvas.width=origcanvas.width=graphcanvas.width=topcanvas.width=docWidth-leftOff;canvas.height=origcanvas.height=graphcanvas.height=topcanvas.height=docHeight-topOff;context=canvas.getContext("2d");origcontext=origcanvas.getContext("2d");graphcontext=graphcanvas.getContext("2d");topcontext=topcanvas.getContext("2d");width=canvas.width;height=canvas.height;context.font=origcontext.font=topcontext.font="12px sans-serif";gr2D=new Image();gr2D.src=_imageBaseDir+"gr2D.png";nL=new Image();nL.src=_imageBaseDir+"nL.png";graphMode="";gr2D_xp=nL_xp=(width-300)/2;gr2D_yp=(height-300)/2;nL_yp=(height-100)/2;gr2D_w=300;gr2D_h=300;nL_w=300;nL_h=100;offX=$get_Element("#canvas-container").offsetLeft;offY=$get_Element("#canvas-container").offsetTop;function getCanvasPos(){console.log("getCanvasPos processing!");var box=canvas.getBoundingClientRect();console.log("canvas bound= top: "+box.top+" left:"+box.left);var body=mainDoc.body;var docElem=mainDoc.documentElement;var scrollTop=window.pageYOffset||docElem.scrollTop||body.scrollTop;var scrollLeft=window.pageXOffset||docElem.scrollLeft||body.scrollLeft;var clientTop=docElem.clientTop||body.clientTop||0;var clientLeft=docElem.clientLeft||body.clientLeft||0;console.log("offset_datas: scrollTop="+scrollTop+" scrollLeft="+scrollLeft+" clientTop="+clientTop+" clientLeft="+clientLeft);var top=box.top+scrollTop-clientTop;var left=box.left+scrollLeft-clientLeft;offX=Math.round(left);offY=Math.round(top);console.log("OFFSET: top="+offY+" left="+offX);return{top:offY,left:offX}}console.log("getCanvasPos calling!");getCanvasPos();console.log("getCanvasPos CALL END!");graphicData={};tool_id={};tool_id.eraser=0;tool_id.pencil=1;tool_id.text=2;tool_id.line=3;tool_id.rect=4;tool_id.oval=5;tool_id.gr2D=11;tool_id.nL=12;drawingLayer="1";$get_Element("#button_pencil").style.border="2px solid #ff9900";$get_Element("#button_text").onclick=function(event){currentTool="text";buttonHighlite(currentTool)};$get_Element("#button_pencil").onclick=function(event){currentTool="pencil";buttonHighlite(currentTool)};$get_Element("#button_rect").onclick=function(event){currentTool="rect";buttonHighlite(currentTool)};$get_Element("#button_line").onclick=function(event){currentTool="line";buttonHighlite(currentTool)};$get_Element("#button_oval").onclick=function(event){currentTool="oval";buttonHighlite(currentTool)};$get_Element("#button_gr2D").onclick=function(event){currentTool="gr2D";showHideGraph("gr2D");buttonHighlite("pencil")};$get_Element("#button_nL").onclick=function(event){currentTool="nL";showHideGraph("nL");buttonHighlite("pencil")};$get_Element("#button_clear").onclick=function(event){currentTool="pencil";buttonHighlite(currentTool);resetWhiteBoard(true)};$get_Element("#button_eraser").onclick=function(event){currentTool="eraser";buttonHighlite(currentTool)};$get_Element("#done_btn").onclick=function(event){renderText()};$get_Element("#button_save").onclick=function(event){wb.saveWhiteboard()};var ev_onmousedown=function(_event){isTouchEnabled=_event.type.indexOf("touch")>-1;if(isTouchEnabled){canvas.removeEventListener("mousedown",ev_onmousedown,false);canvas.removeEventListener("mouseup",ev_onmouseup,false);canvas.removeEventListener("mousemove",ev_onmousemove,false)}getCanvasPos();var event=_event?_event:window.event;event=isTouchEnabled?_event.changedTouches[0]:event;var dx,dy,dist;if(event.pageX!=undefined){dx=event.pageX-offX;dy=event.pageY-offY}else{dx=event.clientX-offX;dy=event.clientY-offY}console.log(dy+":"+event.clientY+":"+event.layerY+":"+event.pageY+":"+offY);context.lineWidth=2;context.strokeStyle="rgb(0, 0, 0)";if(dx>=0&&dx<width){penDown=true;rendering=false;clickX=dx;clickY=dy;x=dx;y=dy;if(!graphicData.dataArr){graphicData.dataArr=[]}graphicData.id=tool_id[currentTool];if(currentTool=="pencil"){context.beginPath();context.moveTo(clickX,clickY)}else{if(currentTool=="eraser"){erase(x,y)}}drawcolor=colorToNumber(context.strokeStyle);if(currentTool=="text"){penDown=false;graphicData.dataArr[0]={x:x,y:y,text:"",color:drawcolor,name:"",layer:drawingLayer};showTextBox()}else{graphicData.dataArr[graphicData.dataArr.length]={x:x,y:y,id:"move",color:drawcolor,name:"",layer:drawingLayer}}}else{penDown=false}if(event.preventDefault){event.preventDefault()}};var ev_onmouseup=function(_event){var event=_event?_event:window.event;event=_event.type.indexOf("touch")>-1?_event.targetTouches[0]:event;if(rendering){penDown=false;if(currentTool=="rect"||currentTool=="oval"){graphicData.dataArr[0].w=w0;graphicData.dataArr[0].h=h0;graphicData.dataArr[0].xs=w0/400;graphicData.dataArr[0].ys=h0/400}else{if(currentTool=="line"||currentTool=="pencil"||currentTool=="eraser"){var xp=x-clickX;var yp=y-clickY;xp=currentTool=="eraser"?x:xp;yp=currentTool=="eraser"?y:yp;graphicData.dataArr[graphicData.dataArr.length]={x:xp,y:yp,id:"line"}}}if(currentTool!="eraser"){updateCanvas();context.beginPath()}sendData();rendering=false}};var ev_onmousemove=function(_event){var event=_event?_event:window.event;event=_event.type.indexOf("touch")>-1?_event.changedTouches[0]:event;if(penDown){rendering=true;if(currentTool!="pencil"&&currentTool!="text"){context.clearRect(0,0,canvas.width,canvas.height)}if(event.pageX!=undefined){x=event.pageX-offX;y=event.pageY-offY}else{x=event.clientX-offX;y=event.clientY-offY}if(currentTool=="rect"||currentTool=="oval"){x0=clickX;y0=clickY;w0=x-clickX;h0=y-clickY;if(currentTool=="rect"){drawRect(x0,y0,w0,h0)}if(currentTool=="oval"){drawOval(x0,y0,w0,h0)}}else{if(currentTool=="line"){context.beginPath();context.moveTo(clickX,clickY);drawLine()}else{if(currentTool=="eraser"){erase(x,y);graphicData.dataArr[graphicData.dataArr.length]={x:x,y:y,id:"line"}}else{graphicData.dataArr[graphicData.dataArr.length]={x:x-clickX,y:y-clickY,id:"line"};drawLine()}}}}if(event.preventDefault){event.preventDefault()}};if(document.addEventListener){canvas.addEventListener("mousedown",ev_onmousedown,false);canvas.addEventListener("mouseup",ev_onmouseup,false);canvas.addEventListener("mousemove",ev_onmousemove,false);canvas.addEventListener("touchstart",touchStartFunction,false);canvas.addEventListener("touchmove",touchMoveFunction,false);canvas.addEventListener("touchstart",ev_onmousedown,false);canvas.addEventListener("touchmove",ev_onmousemove,false);canvas.addEventListener("touchend",ev_onmouseup,false)}else{canvas.attachEvent("onmousedown",ev_onmousedown);canvas.attachEvent("onmouseup",ev_onmouseup);canvas.attachEvent("onmousemove",ev_onmousemove);canvas.attachEvent("touchstart",touchStartFunction);canvas.attachEvent("touchmove",touchMoveFunction);canvas.attachEvent("touchstart",ev_onmousedown);canvas.attachEvent("touchmove",ev_onmousemove);canvas.attachEvent("touchend",ev_onmouseup)}canvas.focus()};function $get_Element(n){var str=n.indexOf("#")>-1?n.split("#")[1]:n;return mainDoc.getElementById(str)}function updateText(txt){graphicData.dataArr[0].text=txt}function showTextBox(){$get_Element("#inputBox").style.display="block";$get_Element("#inputBox").style.top=clickY+"px";$get_Element("#inputBox").style.left=clickX+"px";$get_Element("#content").focus()}function resetWhiteBoard(boo){penDown=false;graphMode="";origcanvas.width=graphcanvas.width=topcanvas.width=canvas.width=width;origcontext.clearRect(0,0,canvas.width,canvas.height);graphcontext.clearRect(0,0,canvas.width,canvas.height);topcontext.clearRect(0,0,canvas.width,canvas.height);context.clearRect(0,0,canvas.width,canvas.height);drawingLayer="1";$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000";if(boo){clear(true)}}function showHideGraph(flag,x,y,boo){graphcanvas.width=graphcanvas.width;graphcanvas.height=graphcanvas.height;graphcontext.clearRect(0,0,canvas.width,canvas.height);graphicData.dataArr=[];graphicData.id=tool_id[currentTool];var addGraph=false;if(!boo&&((graphMode=="gr2D"&&flag=="gr2D")||(graphMode=="nL"&&flag=="nL"))){graphMode="";drawingLayer="1";$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000"}else{$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000";var gr,xp,yp,xs,ys;graphMode=flag;if(flag=="gr2D"){gr=gr2D;xp=x?x-(gr2D_w/2):gr2D_xp;yp=y?y-(gr2D_h/2):gr2D_yp;xs=x?x:gr2D_xp+(gr2D_w/2);ys=y?y:gr2D_yp+(gr2D_h/2);$get_Element("#button_gr2D").style.border="2px solid #ff0000"}else{gr=nL;xp=x?x-(nL_w/2):nL_xp;yp=y?y-(nL_h/2):nL_yp;xs=x?x:nL_xp+(nL_w/2);ys=y?y:nL_yp+(nL_h/2);$get_Element("#button_nL").style.border="2px solid #ff0000"}drawingLayer="3";addGraph=true;graphcontext.drawImage(gr,xp,yp)}graphicData.dataArr.push({x:xs,y:ys,name:"graphImage",addImage:addGraph});sendData()}function mouseOverGraph(){getCanvasPos();var mx=event.layerX?event.layerX:event.pageX-offX;var my=event.layerY?event.layerY:event.pageY-offY;var xp,yp,wi,hi;if(graphMode=="gr2D"){gr=gr2D;xp=gr2D_xp;yp=gr2D_yp;wi=300;hi=300}else{if(graphMode=="nL"){gr=nL;xp=nL_xp;yp=nL_yp;wi=300;hi=100}}if((mx>=xp&&mx<=xp+wi)&&(my>=yp&&my<=yp+hi)){return true}return false}function updateCanvas(){var cntxt=drawingLayer=="1"?origcontext:topcontext;cntxt.drawImage(canvas,0,0);context.clearRect(0,0,canvas.width,canvas.height);context.beginPath()}function erase(x,y){var ew=10;var ep=ew/2;origcontext.clearRect(x-ep,y-ep,ew,ew);topcontext.clearRect(x-ep,y-ep,ew,ew)}function drawLine(){context.lineTo(x,y);context.stroke()}function drawRect(x,y,w,h,color){if(color!=undefined){context.strokeStyle=color}context.strokeRect(x,y,w,h)}function drawOval(x,y,w,h,color){if(color!=undefined){context.strokeStyle=color}var kappa=0.5522848;var ox=(w/2)*kappa;var oy=(h/2)*kappa;var xe=x+w;var ye=y+h;var xm=x+w/2;var ym=y+h/2;context.beginPath();context.moveTo(x,ym);context.bezierCurveTo(x,ym-oy,xm-ox,y,xm,y);context.bezierCurveTo(xm+ox,y,xe,ym-oy,xe,ym);context.bezierCurveTo(xe,ym+oy,xm+ox,ye,xm,ye);context.bezierCurveTo(xm-ox,ye,x,ym+oy,x,ym);context.closePath();context.stroke()}function sendData(){if(graphicData.id||graphicData.id===0){var txtVal=graphicData.dataArr[graphicData.dataArr.length-1].text;if(graphicData.id==2&&(txtVal==""||txtVal==undefined)){resetArrays();textRendering=false;return }if(graphicData.id==1&&graphicData.dataArr.length>500){var jStr=convertObjToString(graphicData);currentObj.tempData=convertStringToObj(jStr);var ptC=graphicData.dataArr.length;var segArr=[];var buf;var header=graphicData.dataArr.shift();var tarr=graphicData.dataArr;var segData;var nxtStart;var nx0;var ny0;var pt={x:header.x,y:header.y};var nname=header.name;var segC=0;var nheader;while(ptC>0){segC++;buf=Math.min(500,ptC);ptC=ptC-buf;segData=tarr.splice(0,buf);var ngdata={};ngdata.lineColor=graphicData.lineColor;ngdata.id=graphicData.id;if(segC>1){var sObj={};sObj.id="move";sObj.x=pt.x;sObj.y=pt.y;segData.unshift(sObj)}nheader=cloneObject(header);nheader.name=nname;segData.unshift(nheader);ngdata.dataArr=segData;segArr.push(ngdata);nxtStart=segData[segData.length-1];pt={x:nxtStart.x,y:nxtStart.y};var n=header.name.split("_");nname=n[0]+"_"+(Number(n[1])+1)}for(var z=0;z<segArr.length;z++){sendDataToSERVER(segArr[z])}render=false;resetArrays();textRendering=false;return }render=false;sendDataToSERVER(graphicData);textRendering=false}resetArrays()}function sendDataToSERVER(jsdata){var jsonStr=convertObjToString(jsdata);wb.whiteboardOut(jsonStr,true)}function cloneObject(obj){var clone={};for(var m in obj){clone[m]=obj[m]}return clone}function resetArrays(){graphicData.dataArr=null;graphicData={}}function getToolFromID(id){for(var m in tool_id){if(id==tool_id[m]){return m}}}function convertObjToString(obj){try{var s=JSON.stringify(obj);return s}catch(ex){console.log(ex.name+":"+ex.message+":"+ex.location+":"+ex.text)}}function convertStringToObj(str){try{var o=eval("("+str+")");return o}catch(ex){console.log(ex.name+":"+ex.message+":"+ex.location+":"+ex.text)}}function renderObj(obj){var graphic_id=obj.id;var graphic_data=obj.dataArr;var line_rgb=obj.lineColor;var dLength=graphic_data.length;var dep,x0,y0,x1,y1;var textF;var idName;drawingLayer=graphic_data[0].layer?graphic_data[0].layer:drawingLayer;context.lineWidth=2;context.strokeStyle="rgb(0, 0, 0)";var deb="";if(graphic_id===0){for(var i=0;i<dLength;i++){x1=graphic_data[i].x;y1=graphic_data[i].y;deb+=x1+":"+y1+"||";erase(x1,y1)}}if(graphic_id===3||graphic_id===1){for(i=0;i<dLength;i++){x1=graphic_data[i].x;y1=graphic_data[i].y;if(graphic_data[i].id=="move"){context.beginPath();context.moveTo(x1,y1);x0=x1;y0=y1}else{context.lineTo(x0+x1,y0+y1)}}context.stroke();updateCanvas()}if(graphic_id===2){for(i=0;i<dLength;i++){if(graphic_data[i].text!=""||graphic_data[i].text!=undefined){x0=graphic_data[i].x;y0=graphic_data[i].y;renderText(xt,x0,y0)}}updateCanvas()}if(graphic_id===4||graphic_id===5){var fName=graphic_id==4?drawRect:drawOval;for(i=0;i<dLength;i++){var xd=graphic_data[i].xs<0?-1:1;var yd=graphic_data[i].ys<0?-1:1;x0=graphic_data[i].x;y0=graphic_data[i].y;w0=graphic_data[i].w*xd;h0=graphic_data[i].h*yd;fName(x0,y0,w0,h0)}updateCanvas()}if(graphic_id===11||graphic_id===12){idName=graphic_id==11?"gr2D":"nL";showHideGraph(idName,graphic_data[0].x,graphic_data[0].y,graphic_data[0].addImage)}}updateWhiteboard=function(cmdArray){var oaL=cmdArray.length;for(var l=0;l<oaL;l++){if(cmdArray[l] instanceof Array){var arg=cmdArray[l][1];arg=arg==undefined?[]:arg;this[cmdArray[l][0]].apply(scope,arg)}else{if(cmdArray[l].indexOf("dataArr")!=-1){draw(cmdArray[l])}else{scope[cmdArray[l]]()}}}};function gwt_updatewhiteboard(cmdArray){var realArray=[];for(var i=0,t=cmdArray.length;i<t;i++){var ele=[];ele[0]=cmdArray[i][0];ele[1]=cmdArray[i][1];realArray[i]=ele}updateWhiteboard(realArray)}wb.updateWhiteboard=function(cmdArray){gwt_updatewhiteboard(cmdArray)};draw=function(json_str){var grobj=convertStringToObj(json_str);renderObj(grobj)};function colorToNumber(c){var n=c.split("#").join("0x");return Number(n)}function clear(boo){if(!boo){resetWhiteBoard(false)}wb.whiteboardOut("clear",false)}wb.saveWhiteboard=function(){alert("default whiteboard save")};wb.whiteboardOut=function(data,boo){alert("default whiteboard out: "+data)};wb.disconnectWhiteboard=function(documentObject){alert("default whiteboard disconnect")};return wb}());function initializeQuiz(){var D=document.getElementById("testset_div");if(D){var C=D.getElementsByTagName("div");for(var B=0,A=C.length;B<A;B++){var E=C[B];if(E.className=="hm_question_def"){initializeQuizQuestion(E)}}processMathJax()}}var uniquer=1;function initializeQuizQuestion(E){var L="answer_"+uniquer;var D="ABCDEFGHIJKLMNOPQRSTUVWXYZ";var I=E.getElementsByTagName("li");for(var F=0,K=I.length;F<K;F++){answer=I[F];var H=answer.getAttribute("correct");var B=answer.getElementsByTagName("div");var G=B[0];uniquer++;var C="answer_id_"+uniquer;var A=D.charAt(F);var J="<span class='question-input' style='margin-right: 10px'><input value='"+H+"' type='radio' name='"+L+"' id='"+C+"' onclick='questionGuessChanged(this)'/>&nbsp;"+A+"</span>";G.innerHTML=J+G.innerHTML;if(B.length>0){B[1].style.display="none"}}}function hideQuestionResult(B){var E=B.getElementsByTagName("li");for(var D=0,C=E.length;D<C;D++){answer=E[D];var A=answer.getElementsByTagName("div");if(A.length>1){A[1].style.display="none"}}}function editQuizQuestion(B){if(B){var A=window.open("/solution_editor/SolutionEditor.html?pid="+B)}}function hideQuizQuestionResults(C){if(!C){C="testset_div"}var E=document.getElementById(C);if(E){var D=E.getElementsByTagName("div");for(var B=0,A=D.length;B<A;B++){var F=D[B];if(F.className=="hm_question_def"){hideQuestionResult(F)}}processMathJax()}}function prepareCustomQuizForDisplay(F,H){var E=F.getElementsByTagName("input");var G=0;var D=0;for(var C=0,B=E.length;C<B;C++){if(C>3&&(C%4)==0){G++}E[C].disabled=true;var A=C-(G*4);if(H[G]==A){E[C].checked=true}}};// alert('loading tutor_dynamic.js');
var TutorDynamic = (function() {
	var theApi = {}

	theApi.thisContext;
	theApi.pid;
	theApi.complete = false;

	theApi.initializeTutor = function(solutionContext) {

		theApi.thisContext = solutionContext;
		theApi.pid = theApi.thisContext.messageLocation.locationString1;
		if (typeof theApi.thisContext.probNum === "undefined"
				|| !theApi.thisContext.probNum) {
			// first view of this solution
			log('first view of solution: ' + theApi.pid);

			var tc = theApi.thisContext;
			tc.probNum = 0;
			tc.numCorrect = 0;
		}

		if (theApi.thisContext.jsonConfig) {
			theApi.thisContext.jsonConfig.limit = 2;

			theApi.setSolutionTitle(theApi.thisContext.probNum + 1,
					theApi.thisContext.jsonConfig.limit);
		}
		else {
			theApi.setSolutionTitle(0,0);
		}

		HmEvents.eventTutorInitialized.fire();
	}
	
	theApi.resetTutor = function() {
		theApi.initializeTutor(theApi.thisContext);
	},

	theApi.setSolutionTitle = function(probNum, limit) {
		$get('steps_head_title').innerHTML = 'Problem ' + (probNum + 1)
				+ ' of ' + limit;
	}

	theApi.endOfSolutionReached = function() {
		try {
			if (typeof theApi.thisContext.jsonConfig === "undefined"
					|| (!theApi.thisContext.complete && (theApi.thisContext.probNum + 1) >= theApi.thisContext.jsonConfig.limit)) {
				// alert('endOfSolutionSetReached');

				theApi.thisContext.complete = true;

				// depending on current state of
				// solution context, fire event
				// informing listenrs that this
				// solution context is complete.
				HmEvents.eventTutorLastStep.fire();
			}
		} catch (e) {
			alert(e);
		}

	}

	theApi.getSolutionSetTitle = function() {
		return 'Problem ' + theApi.thisContext.probNum + ' of '
				+ theApi.thisContext.jsonConfig.limit;
	}

	/** Reload the existing solution */
	theApi.refreshProblem = function() {
		log("Refreshing current solution set member");

		theApi.thisContext.probNum++;

		if (!theApi.thisContext.jsonConfig
				|| theApi.thisContext.probNum < theApi.thisContext.jsonConfig.limit) {
			gotoGUID(theApi.thisContext);
		} else {
			HmEvents.eventTutorSetComplete.fire();
		}
	}

	HmEvents.eventTutorWidgetComplete.subscribe(function(x, yesNo) {
		if (yesNo[0]) {
			theApi.thisContext.numCorrect++;
			theApi.refreshProblem(); // cause a new instance of problem.
		}
	});

	HmEvents.eventTutorSetComplete.subscribe(function(x) {
		solutionSetComplete(theApi.thisContext.numCorrect,
				theApi.thisContext.jsonConfig.limit);
	});

	return theApi;
})();

/** This will be overriden for specific needs  by
 *   CM or standard tutor.
 */
function solutionSetComplete(numCorrect, limit) {

	var msg = '  <div>' + '    <b>Total Questions: </b>' + '' + limit
			+ '  </div>' + '  <div>' + '    <b>Correct Responses: </b>' + ''
			+ numCorrect + '  </div>';
	msg = '<p class="solution_set_results">' + msg + '</p>';

	YAHOO.solutionCompleteDialog = new YAHOO.widget.Panel(
			"solution_complete_dialog", {
				modal : true,
				underlay : "none",
				fixedcenter : true,
				zIndex : 999,
				width : "300px",
				height : "240px",
				visible : true,
				close : true,
				contraintoviewport : true,
				draggable : false
			});

	html += '<div id="solutionCompleteDialog_close"><a href="#" onclick="closeSolutionCompleteDialog();return false;">Click to close</a></div>';
	YAHOO.solutionCompleteDialog.setHeader("Problem Set Complete!");
	YAHOO.solutionCompleteDialog.setBody(html);

	YAHOO.solutionCompleteDialog.render(document.body);
}

function closeSolutionCompleteDialog() {
	YAHOO.solutionCompleteDialog.hide();
	YAHOO.solutionCompleteDialog.destroy();
}

function processTutorData(tutorData, stepText) {

	if (!tutorData)
		return;

	if(tutorData._code) {
		for ( var i = 0, t = tutorData._code.length; i < t; i++) {
			var c = TutorManager.tutorData._code[i];
			if (c.code && c.code > '') {
				
				if(!c.base64Processed) {
					c.code = Base64.decode(c.code);
					c.base64Processed=true;
				}
				
	
				try {
					//alert('code to eval: ' + c.code);
					/** TODO: use safe method */
					eval(c.code);
				} catch (e) {
					alert("code eval failed: " + e);
				}
			}
		}
	}
	if (tutorData._variables) {
		for ( var i = 0, t = tutorData._variables.length; i < t; i++) {
			var v = tutorData._variables[i];
			if (v.init && v.init > '' && !v.base64Processed) {
				v.init = Base64.decode(v.init);
				v.base64Processed=true;
			}
		}
	}
	/** insert the raw HTML into the dom node */
	var tsw = $get('tutor_raw_steps_wrapper');
	tsw.innerHTML = processTutorDataVariables(tutorData._variables, stepText);
}

/** replace any instance of variables in text */
function processTutorDataVariables(vars, text) {
	if (vars) {
		for ( var i = 0, t = vars.length; i < t; i++) {
			var v = vars[i];
			if (!v.value) {
				v.value = generateVarValue(v);
			}
			text = replaceAll(text, '\\$' + v.name, v.value);

			// apply changes to variables below
			for (j = i; j < t; j++) {
				vars[j].init = replaceAll(vars[j].init, '\\$' + v.name, v.value);
			}
		}
	}
	return text;
}

/**
 * create value for variable v
 *
 * Depending on type of variable, create the appropriate type.
 *
 * @param v
 */
function generateVarValue(v) {
	try {
		if (v.init) {
			if (v.type == 'PROVIDED') {
				return eval(v.init);
			} else if (v.type == 'TEMPLATE') {
				// alert('evaluating template: ' + v.init);
				return eval(v.init);
			} else {
				return v.init;
			}
		} else {
			return Math.ceil(1000 * Math.random())
		}
	} catch (e) {
		alert('error processing variable: ' + v.name + ', ' + v.type + ', '
				+ v.init);
	}
}

function replaceAll(txt, replace, with_this) {
	return txt.replace(new RegExp(replace, 'g'), with_this);
}
if(typeof console === "undefined") {
    console = {log:function(x) {
        // empty
    }};
}

// version 1.1
var _json;
var HmFlashWidgetFactory = {
    createWidget:function(jsonObj) {

        if(jsonObj.type == 'number_integer') {
            if(jsonObj.format == 'angle_deg') {
                return new HmFlashWidgetImplNumberIntegerAngleDeg(jsonObj);
            }
            else {
                return new HmFlashWidgetImplNumberInteger(jsonObj);
            }
        }
        else if(jsonObj.type == 'number_decimal') {
            if(jsonObj.format == 'money') {
                return new HmFlashWidgetImplNumberMoney(jsonObj);
            }
            else {
                return new HmFlashWidgetImplNumberDecimal(jsonObj);
            }
        }
        else if(jsonObj.type == 'inequality') {
            return new HmFlashWidgetImplInequality(jsonObj);
        }
        else if(jsonObj.type == 'number_fraction') {
            return new HmFlashWidgetImplNumberIntegerFraction(jsonObj);
        }
        else if(jsonObj.type == 'number_simple_fraction') {
            return new HmFlashWidgetImplSimpleFraction(jsonObj);
        }
        else if(jsonObj.type == 'number_rational') {
            return new HmFlashWidgetImplRational(jsonObj);
        }
        else if(jsonObj.type == 'mChoice') {
            return new HmFlashWidgetImplMulti(jsonObj);
        }
        else if(jsonObj.type == 'coordinates') {
            return new HmFlashWidgetImplCoord(jsonObj);
        }
        else if(jsonObj.type == 'number_mixed_fraction') {
            return new HmFlashWidgetImplMixedFraction(jsonObj);
        }
        else if(jsonObj.type == 'power_form') {
            return new HmFlashWidgetImplPowerForm(jsonObj);
        }
        else if(jsonObj.type == 'scientific_notation') {
            return new HmFlashWidgetImplSciNotation(jsonObj);
        }
        else if(jsonObj.type == 'letter') {
            return new HmFlashWidgetImplLetter(jsonObj);
        }
        else if(jsonObj.type == 'odds') {
            return new HmFlashWidgetImplOdds(jsonObj);
        }
        else if(jsonObj.type == 'point_slope_form') {
            return new HmFlashWidgetImplPointSlopeForm(jsonObj);
        }
        else if(jsonObj.type == 'inequality_exact') {
            return new HmFlashWidgetImplInequalityExact(jsonObj);
        }
        else {
            alert("tutor widget: do not know how to initialize: " + jsonObj.type);
        }
    }
};


/** Called when Flash Tutor widget calls */
function flash_quizResult(x) {
    // enable the Tutor's next button
    // setState('step',true);
}



/** Create question element */
function _createQuestionStep(el) {
    // first child div is the question
    var divs = el.getElementsByTagName('div');

    for(var d=0,t=divs.length;d<t;d++) {
         var del = divs[d];
         if(d == 0) {
             // the question text
         }
         else if((d%2)!=0){
             // a choice
             var html = "<img class='ques_icon' src='/images/tutor5/hint_question-16x16.gif' onmouseover='showMouseAnswer(this)' onmouseout='nd()'/>";
             del.innerHTML = html + del.innerHTML;
         }
         else {
             del.style.display = 'none';
         }
    }
}

function showMouseAnswer(el) {
    var imgTarget = el;
    var questionDiv = el.parentNode.parentNode;
    var qc = questionDiv.getAttribute('correct');
    var isCorrect = (qc == 'true' || qc == 'yes');
    var divs = questionDiv.getElementsByTagName("div");

    var divResponse = divs[1]; // second div in list item
    var message = divResponse.innerHTML;
    var stringResourceCorrect = isCorrect?'yes':'no';
    var pathToImages='/images';
    if(stringResourceCorrect == 'yes') {
        message = "<img class='question_correct_incorrect' src='" + pathToImages + "/tutor5/question_correct.gif'>" + message;
    }
    else {
        message = "<img class='question_correct_incorrect' src='" + pathToImages + "/tutor5/question_incorrect.gif'>" + message;
    }
    return overlib(message, FGCLASS, 'ol_question_style');
}




function _setupTutorMultiChoice() {
    var multiChoice = document.getElementsByTagName('div');
    for(var d=0,t=multiChoice.length;d<t;d++) {
        if(multiChoice[d].className == 'hm_question_def') {
            var el = multiChoice[d];
            _createQuestionStep(el);
        }
    }
}


/**
 * Register with the HM Solution event system to be notified after the Tutor has
 * been fully initialized
 *
 */
HmEvents.eventTutorInitialized.subscribe(
    function(x) {
        try {
            _showTutorWidget();
            _setupTutorMultiChoice();
       }
        catch(e) {
            alert('error: ' + e);
        }
    }
);

var _enableJsWidgets=true;

function _showTutorWidget() {
    var problemHead = document.getElementById('problem_statement');
    var divs = problemHead.getElementsByTagName('div');
    for(var w=0,t=divs.length;w<t;w++) {
         var widgetDiv = divs[w];
         var cn = widgetDiv.getAttribute('id')
         if(cn == 'hm_flash_widget') {
             /** extract embedded JSON */
             var jsonDef = $get('hm_flash_widget_def');
             if(jsonDef) {
                     if(_enableJsWidgets) {
                         _json = jsonDef.innerHTML;
                         var jsonObj = eval('(' + _json + ')');
                          HmFlashWidgetFactory.createWidget(jsonObj);
                     }
                     else {
                         showFlashObject();
                         widgetDiv.style.display = 'none';
                     }
             }
             else {
                 widgetDiv.innerHTML = _createGuiWrapper().innerHTML;
                 var info = document.createElement("div");
                 info.innerHTML = _getWidgetNotUsedHtml();
                 widgetDiv.appendChild(info);
             }
         }
    }
}

function _getWidgetNotUsedHtml() {

   return "<p>" +
            "Work out your answer on our whiteboard; your teacher will receive a copy." +
            " Then, click the arrow buttons to see our step-by-step answer." +
        "</p>";
}

function showWidgetSubmit(yesNo) {
    var w = $get('widget_submit');
    if(w) {
        w.disabled = yesNo?false:true;
    }
}

/** Set main widget message.
 *
 *  If null msg then clears any messages and shows the Submit button.
 * @param msg
 */
function setWidgetMessage(msg) {
    if(!msg) {
        msg = '&nbsp;';
        var img = $get('hm_flash_widget_indicator');
        if(img) {
            img.style.display = 'none';
        }
        showWidgetSubmit(true);

        if($get('widget_input_simplified')) {
            $get('widget_input_simplified').checked = false;
        }
    }
    var h = $get('hm_flash_widget_head');
    if(h)
        h.innerHTML = msg;
}


/** Copy prototype from one class to another.
 *  Allows for JS inheritance.
 */
function copyPrototype(descendant, parent) {
    var sConstructor = parent.toString();
    var aMatch = sConstructor.match( /\s*function (.*)\(/ );
    if ( aMatch != null ) { descendant.prototype[aMatch[1]] = parent; }
    for (var m in parent.prototype) {
        descendant.prototype[m] = parent.prototype[m];
    }
};


/** Utility Methods
 *
 */
var restrictionType_digitsOnly = /[1234567890-]/g;
var restrictionType_decimals = /[1234567890\-\+\.]/g;
var restrictionType_digitsOnlyWithSlash = /[1234567890\/]/g;
var restrictionType_digitsOnlyWithColon = /[1234567890:]/g;
var restrictionType_integerOnly = /[0-9]/g;
var restrictionType_alphaOnly = /[A-Z]/g;

var restrictionType_rational = /[1234567890\-\.]/g;

function restrictCharacters(myfield, e, restrictionType) {
    if (!e) var e = window.event
    if (e.keyCode) code = e.keyCode;
    else if (e.which) code = e.which;
    var character = String.fromCharCode(code);

    // if they pressed esc... remove focus from field...
    if (code==27) { this.blur(); return false; }

    // ignore if they are press other keys
    if (code!=13 && !e.ctrlKey && code!=9 && code!=8 && code!=36 && code!=37 && code!=38 && (code!=39 || (code==39 && character=="'")) && code!=40) {
        if (character.match(restrictionType)) {
            return true;
        } else {
            return false;
        }
    }
}
function isASlashCharacter(event) {
    if (!event) var event = window.event
    if (event.keyCode) code = event.keyCode;
    else if (event.which) code = event.which;
    if(code == 47) { // slash
        return true;
    }
    return false;
}

function isABackSpaceCharacter(event) {
    if (!event) var event = window.event
    if (event.keyCode) code = event.keyCode;
    else if (event.which) code = event.which;
    if(code == 8) {
        return true;
    }
    return false;
}


/**
 * Class to define a single flash widget
 *
 * @param widgetDiv
 * @return
 */
function HmFlashWidget(jsonObj) {
    try {
        this._jsonObj = jsonObj;
        this._widgetAnswer = jsonObj.value;
        this.initializeWidget();
    }
    catch(e) {
        alert('Widget initialization error: ' + e);
    }
}

/**
 * Class HmFlashWidget base class
 *
 * - Takes a widget and assigns a key listener on field widget_input_field_1-n.
 * - Each key press will call the method widgetKeyPress on the attached listener.
 * - The form submit is assigned to the proper wigetObject handler.
 *
 * @param widgetDiv
 *
 */
HmFlashWidget.prototype.initializeWidget = function() {
    /** put value in widget */
    var widgetObj = this;
    this._widgetGui = _createGuiWrapper();
    this._widgetForm = this.drawGui();
    this._widgetForm.onsubmit = function(){widgetObj.processWidget(this);return false;};
    this._widgetGui.appendChild(this._widgetForm);

    /** add the submit button to widget form */

    var submit = "<input type='submit'  id='widget_submit' class='sexybutton sexysimple sexylarge sexyred' disabled='true' style='display: block' value='Check Answer'/>";

    this._widgetForm.innerHTML = this._widgetForm.innerHTML + submit;

    var fields = this._widgetForm.getElementsByTagName("input");
    for(var f=0,t=fields.length;f<t;f++) {
        var inputField = fields[f];
        if(inputField.getAttribute("type") == 'text') {
            if(inputField) {
                inputField.onkeypress = function(event) {
                    setWidgetMessage(null);
                    return widgetObj.processKey(this,event);
                };
            }
        }
    }
    setWidgetMessage('Optional: enter an answer!');
}

HmFlashWidget.prototype.drawGui = function() {
    return this.drawGuiDefault();
}

/** Define GUI for basic widget
 *
 */
HmFlashWidget.prototype.drawGuiDefault = function() {
    var widget = this.createWidgetForm();

    var html = "<input name='widget_input_field_1' id='widget_input_field_1' type='text'/>";
    widget.innerHTML = html;

    return widget;
}


/** Default processWidget functionality.
 *
 *   This will take the value from single
 *   field and matches it with jsonObj.value.
 *
 *   If it is equal, then show correct
 *   image else show error image.
 *
 */
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}

HmFlashWidget.prototype.processWidgetDefault = function() {
        try {
            if(this.processWidgetValidation()) {
                this.markWidgetCorrect();
            }
            else {
                this.markWidgetIncorrect();
            }
        }
        catch(msg) {
                setWidgetMessage(msg);
        }
        showWidgetSubmit(false);
}

/** Default process validation method (equality)
 *
 *  This method will be overridden by subclasses
 *  to provide widget specific validation.
 *
 *  It must return either true, or false.  Any
 *  widget specific errors should throw an exception.
 */
HmFlashWidget.prototype.processWidgetValidation = function() {
    var d = $get('widget_input_field_1');
    var ans = d.value;
    return ans == this._widgetAnswer;
}


/** Default functionality is to allow any key
 *
 */
HmFlashWidget.prototype.processKey = function() {
    //allow all
}


/** Add the head and control box for indicator/submit button
 *
 */
_createGuiWrapper = function() {

    /** FIX, this must be specified ...
     *
     */
    var guiWrapper = $get('hm_flash_widget');

    guiWrapper.setAttribute("style", "position: relative;");
    var html = "     <div id='hm_flash_widget_indicator' style='position: absolute;right: 5px;top: 25px;display: none;'>&nbsp;</div>" +
               "     <div id='hm_flash_widget_head' onclick='showFlashObject();'>&nbsp;</div>";
    guiWrapper.innerHTML = html;
    return guiWrapper;
}

function showFlashObject() {
    var fo = $get('hm_flash_object');
    if(fo) {
        fo.style.display = 'block';
    }
    else {

    }
}

HmFlashWidget.prototype.markWidgetCorrect = function() {
    setWidgetMessage("Correct!");
    var indicator = $get('hm_flash_widget_indicator');
    indicator.innerHTML = "<img src='/tutor/widget/images/widget_correct.png'/>";
    indicator.style.display = 'block';

    HmEvents.eventTutorWidgetComplete.fire(true);
}

HmFlashWidget.prototype.markWidgetIncorrect = function() {
   setWidgetMessage("TRY AGAIN!");
   var indicator = $get('hm_flash_widget_indicator');
   indicator.innerHTML = "<img src='/tutor/widget/images/widget_incorrect.png'/>";
   indicator.style.display = 'block';

   HmEvents.eventTutorWidgetComplete.fire(false);
}

/** Create base widget form object
 *
 */
HmFlashWidget.prototype.createWidgetForm = function() {
    var widgetForm = document.createElement("form");
    widgetForm.setAttribute('styleName','width: ' + this._jsonObj.width + 'px');
    widgetForm.setAttribute("id", "hm_widget_form");
    widgetForm.setAttribute("autocomplete", "off");
    return widgetForm;
}





/** Start of widget specific classes
 *
 *  Order is important!
 *
 * */
/** Number integer entry
 *  test: cmextras_1_1_1_1_1
 */
function HmFlashWidgetImplNumberInteger(jsonObj) {
    this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.drawGui = function() {
    return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
        return this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplNumberInteger, HmFlashWidget);



/** Class for simple decimal values
 *
 */
function HmFlashWidgetImplNumberDecimal(jsonObj) {
        this.HmFlashWidget(jsonObj); // super
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_decimals);
}
copyPrototype(HmFlashWidgetImplNumberDecimal,HmFlashWidget);







/** Class for Money entry
 *
 * test: cmextras_1_1_1_11_1
 */
function HmFlashWidgetImplNumberMoney(jsonObj) {
    this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.drawGui = function() {
    return this.drawGuiDefault();
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.processWidget= function() {
        this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplNumberMoney, HmFlashWidget);




/** Define Class for Fraction entry
 *
 * test: cmextras_1_2_1_56_1
 *
 */
function HmFlashWidgetImplNumberIntegerFraction(jsonObj) {
    this.HmFlashWidget(jsonObj);
    var p = jsonObj.value.split('/');
    prepareSimpleFraction(jsonObj, this);
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.processWidget = function() {
        this.processWidgetDefault();
}
HmFlashWidget.prototype.drawGui = function() {
    var widget = this.createWidgetForm();

    var html = "<div class='widget-fraction' style='background: white'>" +
                   "<input name='widget_input_field_1' id='widget_input_field_1' type='text'/>" +
                   "<div class='divider'>&nbsp;</div>" +
                   "<input name='widget_input_field_2' id='widget_input_field_2' type='text'/>" +
               "</div>";

    /** should a simplified choice been added?
     *
     */
    if(this._jsonObj.format == 'text_simplified') {
        this.hasSimplified=true;

        html += "<div class='simplified_wrapper'>" +
                "  <label>Simplified?<input id='widget_input_simplified' type='checkbox'/></label>" +
                "</div>";
    }
    widget.innerHTML = html;

    return widget;
}
copyPrototype(HmFlashWidgetImplNumberIntegerFraction, HmFlashWidget);

/** Define class for Number Simple Fraction
 *
 * test: genericprealg_3_5_NumberTheory_29_320
 *
 * inherit all from IntegerFraction
 */
function HmFlashWidgetImplSimpleFraction(jsonObj) {
    this.HmFlashWidget(jsonObj);
    prepareSimpleFraction(jsonObj,this);
}
copyPrototype(HmFlashWidgetImplSimpleFraction, HmFlashWidget);

/** helper method for Fraction setup */
function prepareSimpleFraction(jsonObj, obj) {
    var p = jsonObj.value.split('/');
    obj.numerator = p[0];
    obj.denominator = p[1];
    obj.hasSimplified=false;
    var num = $get('widget_input_field_1');
    var den = $get('widget_input_field_2');
    var simpEl = $get('widget_input_simplified');
    if(simpEl) {
        simpEl.onclick = function() {showWidgetSubmit(true); num.value = '';den.value='';};
    }
}








/** Define class for Integer with Angle Deg
 *
 */

function HmFlashWidgetImplNumberIntegerAngleDeg(jsonObj) {
    this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.drawGui = function() {
   var widget = this.drawGuiDefault();

   var tag = document.createElement("span");
   tag.innerHTML = "&nbsp;&deg;";
   widget.appendChild(tag);

   return widget;
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplNumberIntegerAngleDeg, HmFlashWidget);









/** Define class for Multi entry
 *
 * test:
 *
 */
function HmFlashWidgetImplMulti(jsonObj) {
    this.HmFlashWidget(jsonObj);

    var values = jsonObj.value.split('|');
    this._correct = values[values.length-1];

    var widgetHolder = document.getElementById("widget_wrapper");

    var items='';
    /** all but last, which has answer */
    for(var v=0,t=values.length-1;v<t;v++) {
        items += '<input type="radio" name="widget_value" onclick="setWidgetMessage(null)" value="' + values[v] + '"/>';
        items += '<span>' + values[v] + '</span>';
        items += '<br/>';
    }

    widgetHolder.innerHTML = items;

}
HmFlashWidget.prototype.processKey = function(event) {
    // empty
}
HmFlashWidget.prototype.processWidget = function() {
        this.processWidgetDefault();

}
HmFlashWidget.prototype.drawGui = function() {
    var widget = this.createWidgetForm();

    var html = "<div id='widget_wrapper'>" +
               "</div>";

    widget.innerHTML = html;

    return widget;
}
copyPrototype(HmFlashWidgetImplMulti, HmFlashWidget);







/** Define class for Rational
 *
 * test: cmextras_1_1_1_83_1
 *
 */
function HmFlashWidgetImplRational(jsonObj) {
    this.isFraction=false;
    this.HmFlashWidget(jsonObj);
    var widgetObj = this;
}

HmFlashWidget.prototype.processKey = function(ele, event) {
    if (!event) var event = window.event
    if (event.keyCode) code = event.keyCode;
    else if (event.which) code = event.which;
    if(isASlashCharacter(event)) {
        if(!this.isFraction) {
            this.configureAsFraction(true);
            this.isFraction = true;
        }
        return false;
    }
    else if(isABackSpaceCharacter(event)) {
        if(this.isFraction) {
            if(ele.id == 'widget_input_field_2' && ele.value.length < 2) {
                this.configureAsFraction(false);
                this.isFraction = false;
                ele.value = '';
                return false;
            }
        }
        return true;
    }
    else if(code == 39) {
        return true;
    }

    return restrictCharacters(ele, event, restrictionType_rational);
}

HmFlashWidget.prototype.configureAsFraction = function(yesNo) {
    if(yesNo) {
        $get('widget_input_field_2').style.display = 'inline';
        $get('widget_divider').style.display = 'block';
        $get('widget_input_field_2').focus();
    }
    else {
        $get('widget_input_field_2').style.display = 'none';
        $get('widget_divider').style.display = 'none';
        $get('widget_input_field_1').focus();
    }
}

/** Builds UI and places measure text to right of main input field
 *
 */
HmFlashWidget.prototype.drawGui = function() {
    var measure = '';
    var format = '';
    var prefix = '';
    if(this._jsonObj.format)  {
        format = this._jsonObj.format;
        var vm = unescape(format).split('|');
        if(vm.length > 1) {
            measure = vm[1];
            prefix = vm[0];
        }
        var mp = format.split('^');
        var raised=null;
        if(mp.length > 1) {
            measure = mp[0];
            raised = mp[1];
        }
        else {
            measure = unescape(this._jsonObj.format);
        }

        if(measure) {
            measure = measure.split('_')[1];
        }

        if(!measure)
            measure = '';

        format = '';
        if(raised) {
            measure += "<span style='vertical-align: baseline;font-size: 0.8em;position: relative;top: -0.4em;'>" + raised + "</span>";
        }
    }
    var widget = this.createWidgetForm();
    var ms = prefix==''?150:125;

    var html =
        "<div style='height: 75px;'>" +
            "<div style='float: left;margin-top: 8px;margin-right: 5px;'>" + prefix + "</div>" +

            "<div style='float: left;'>" +

                "<input id='widget_input_field_1' type='text' style='width: 80px;'/>" +
                "<sup style='font-weight: bold;'>" + measure + "</sup>" +
                "<div id='widget_divider' class='divider' style='display: none'>&nbsp;</div>" +
                "<input id='widget_input_field_2' type='text' style='display: none;width: 80px;'/>" +
            "</div>" +
        "</div>";


    if(this._jsonObj.symbols) {
        var symbols = this._jsonObj.symbols;
        if(symbols == 'pi') {
            symbols = '&pi;';
        }
        else {
            /** square root ?*/
            symbols = '&radic;';
        }

        html += "" +
                "<div class='buttons' style='padding: 5px; background: #DDD;'>" +
                "    <button onclick='_addButtonTextToWidget(\"" + symbols + "\");return false;'>" + symbols + "</button>" +
                "</div>";
    }
    widget.innerHTML = html;
    return widget;
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplRational, HmFlashWidget);




/** Define class for Inequality
 *
 * test: cmextras_1_2_1_43_1
 *
 */
function HmFlashWidgetImplInequality(jsonObj) {
    this.isFraction=false;
    this.HmFlashWidget(jsonObj);

    var widgetObj = this;
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return true; // accept all
}

HmFlashWidget.prototype.configureAsFraction = function(yesNo) {
    if(yesNo) {
        $get('widget_input_field_2').style.display = 'inline';
        $get('widget_divider').style.display = 'block';
        $get('widget_input_field_2').focus();
    }
    else {
        $get('widget_input_field_2').style.display = 'none';
        $get('widget_divider').style.display = 'none';
        $get('widget_input_field_1').focus();
    }
}
HmFlashWidget.prototype.drawGui = function() {
    var widget = this.createWidgetForm();
    var html =
           "<input id='widget_input_field_1' type='text'/>" +
           "<div class='buttons' style='margin-top: 10px;'>" +
             "<button type='button' onclick='_addButtonTextToWidget(\"&lt;\");return false;'>&lt;</button>" +
             "<button type='button' onclick='_addButtonTextToWidget(\"&le;\");return false;'>&le;</button>" +
             "<button type='button' onclick='_addButtonTextToWidget(\"&gt;\");return false;'>&gt;</button>" +
             "<button type='button' onclick='_addButtonTextToWidget(\"&ge;\");return false;'>&ge;</button>" +
             "<button type='button' onclick='_addButtonTextToWidget(\"=\");return false;'>=</button>" +
             "<button type='button' onclick='_addButtonTextToWidget(\"&ne;\");return false;'>&ne;</button>" +
           "</div>";
    widget.innerHTML = html;
    return widget;
}

function _addButtonTextToWidget(ch) {
    var v = $get('widget_input_field_1').value;
    $get('widget_input_field_1').value = v + ch;
    setWidgetMessage(null);
    $get('widget_input_field_1').focus();
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplInequality, HmFlashWidget);






/** Define class for Coord
 *
 */
function HmFlashWidgetImplCoord(jsonObj) {
    this.HmFlashWidget(jsonObj);
    var inputField = $get('widget_input_field_2');
    inputField.onkeydown = function(event) {setWidgetMessage(null);return widgetObj.processKey(this,event);};
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
    var widget = this.createWidgetForm();
    var html =
           "<span style='font-size: 40px;font-weight: bold'>&#40;<span>" +
           "<input style='width: 50px;' id='widget_input_field_1' type='text'/>" +
           ",&nbsp;" +
           "<input style='width: 50px;' id='widget_input_field_2' type='text'/>" +
           "<span style='font-size: 40px;font-weight: bold'>&#41;<span>";

    widget.innerHTML = html;
    return widget;
}
HmFlashWidget.prototype.processWidget = function() {
    var var1 = $get('widget_input_field_1').value;
    var var2 = $get('widget_input_field_2').value;

    var vals = this._widgetAnswer.split('|');
    if(var1 == vals[0] && var2 == vals[1]) {
        this.markWidgetCorrect();
    }
    else {
        this.markWidgetIncorrect();
    }
    showWidgetSubmit(false);
}
copyPrototype(HmFlashWidgetImplCoord, HmFlashWidget);








/** Define class for number_mixed_fraction: cmextras_1_4_1_1_4
 *
 */
function HmFlashWidgetImplMixedFraction(jsonObj) {
    this.HmFlashWidget(jsonObj);
    var inputField = $get('widget_input_field_2');
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
    var widget = this.createWidgetForm();
    var html =
           "<div style='height: 50px;'>" +
           "<div style='float: left;margin-top: 8px;margin-right: 5px;'>" +
           "    <input id='widget_input_field_1' type='text' style='width: 30px'/>" +
           "</div>" +
           "<div style='float: left;width: 80px;'>" +
               "<input id='widget_input_field_2' type='text' style='width: 80px;display: block;'/>" +
               "<div class='divider'>&nbsp;</div>" +
               "<input id='widget_input_field_3' type='text' style='width: 80px;display: block;'/>" +
           "</div>" +
           "</div>";

    widget.innerHTML = html;
    return widget;
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplMixedFraction, HmFlashWidget);



/** Define class for Power Form
 *
 * test: genericprealg_1_6_Operations,ExpressionsandVariables_15_125
 *
 */
function HmFlashWidgetImplPowerForm(jsonObj) {
    this.HmFlashWidget(jsonObj);
}

HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
    var power = '';
    var format = '';
    var prefix = '';

    var v = this._jsonObj.value.split('^');
    var base = v[0];
    var power = v[1];
    var widget = this.createWidgetForm();
    var html =
        "<div style='position: relative;height: 40px;'>" +
        "   <input id='widget_input_field_1' type='text' style='position: absolute; left: 0;width: 87px;'/>" +
        "   <input id='widget_input_field_2' type='text' style='position: absolute;  top: -10px;left:90px;font-size: .8em;width: 40px;'/>" +
        "</div>";
    widget.innerHTML = html;

    return widget;
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplPowerForm, HmFlashWidget);









/** type = 'science notation'
 *  test = genericprealg_3_8_NumberTheory_1_335
 */
function HmFlashWidgetImplSciNotation(jsonObj) {
    this.HmFlashWidget(jsonObj);

    var val = unescape(jsonObj.value);
    var p1 = val.split('x');
}

HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
    var widget = this.createWidgetForm();
    widget.className = 'science-notation';
    var html =
        "<div style='position: relative;height: 40px;'>" +
        "   <input id='widget_input_field_1' type='text' style='position: absolute; left: 0;width: 63px;'/>" +
        "   <input id='widget_input_field_2' type='text' style='position: absolute;  top: -10px;left:110px;font-size: .5em;width: 40px;'/>" +
        "   <span style='position: absolute;left: 70px;font-weight: bold;'>x 10</span>" +
        "</div>";
    widget.innerHTML = html;
    return widget;
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplSciNotation, HmFlashWidget);




/** type = letter
 *  test = 'genericprealg_10_1_LinearEquationsandInequalities_5_900'
 */
function HmFlashWidgetImplLetter(jsonObj) {
    this.HmFlashWidget(jsonObj);

    var val = unescape(jsonObj.value);
    var p1 = val.split('x');
}

HmFlashWidget.prototype.processKey = function(ele, event) {
    return true;
}

HmFlashWidget.prototype.drawGui = function() {
    return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplLetter, HmFlashWidget);






/** Type = Odds
 *  test = genericalg1_13_1_DiscreteMathematicsandProbability_13_1200
 */
function HmFlashWidgetImplOdds(jsonObj) {
    this.HmFlashWidget(jsonObj);
}

HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnlyWithColon);
}

HmFlashWidget.prototype.drawGui = function() {
    return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplOdds, HmFlashWidget);





/** Type = point_slope_form
 *  test = genericalg1_2_7_GraphingLinearEquations_1_130
 */
function HmFlashWidgetImplPointSlopeForm(jsonObj) {
    this.HmFlashWidget(jsonObj);
}

HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}

HmFlashWidget.prototype.drawGui = function() {
    var widget = this.createWidgetForm();
    widget.className='point-slope-form';
    var html =
           "<span>y-&nbsp;</span>" +
           "<input id='widget_input_field_1' type='text' style='width: 30px;display: inline'/>" +
           "<span>&nbsp;=&nbsp;</span>" +
           "<input id='widget_input_field_2' type='text' style='width: 30px;display: inline'/>" +
           "<span>&nbsp;<span style='font-size: 50px;'>(</span>x-&nbsp;</span>" +
           "<input id='widget_input_field_3' type='text' style='width: 30px;display: inline;'/>" +
           "<span><span style='font-size: 50px;'>)</span>";


    widget.innerHTML = html;
    return widget;
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplPointSlopeForm, HmFlashWidget);





function HmFlashWidgetImplInequalityExact(jsonObj) {
    this.HmFlashWidget(jsonObj);
}
HmFlashWidget.prototype.processKey = function(ele, event) {
    return restrictCharacters(ele, event, restrictionType_digitsOnly);
}
HmFlashWidget.prototype.drawGui = function() {
    return this.drawGuiDefault();
}
HmFlashWidget.prototype.processWidget = function() {
    this.processWidgetDefault();
}
copyPrototype(HmFlashWidgetImplInequalityExact, HmFlashWidget);

/**
 * tutor flash widget validations
 * 
 * validation functions are assigned to specific widgets defiend in
 * tutor_flash_widget.js.
 * 
 * Each validation method is made an instance method of the assigned class. This
 * makes the instance vars (like _jsonObj) to be available.
 * 
 */

var widget_answer;
var widget_format;
var MESSAGE_CORRECT='Correct!';
var MESSAGE_INCORRECT='Try Again!';
var ERROR_UNITS='Enter correct units!'
var ERROR_SIMPLE_FRACTION = "Not in lowest terms!";
var ERROR_SCI_NOT = "The first number has to be between 1 and 10";
var ERROR_ODDS="The problem asks for the odds not probability"
var PI_SYM = '\u03c0';
var SQUARE_ROOT_SYM = '\u221A';
    
function regExpMatch(reg,str) {
  var re = new RegExp(reg);
  if (str.match(re)) {
    return true;
  } else {
    return false;
  }
}
function regExpReplace(reg,str,repstr) {
  var re = new RegExp(reg, "g");
  return str.replace(re, repstr);
}
var revSymb = {};
revSymb['<'] = '>';
revSymb['>'] = '<';
revSymb['='] = '=';
revSymb['!='] = '!=';
revSymb['<='] = '>=';
revSymb['>='] = '<=';

// unicode:
// less-than-or-equals-to = \u2264
// greater-than-or-equals-to = \u2265
// not-equals-to = \u2260

function splitAtInEq(str) {
    var syArr = ["<=", "<", "&lt;", "\u2264", "&le;", ">=", ">", "&gt;", "\u2265", "&ge;", "=", "&eq;", "\u2260", "&ne;"];
    var ieq = null;
    for (var i = 0; i < syArr.length; i++) {
        if (str.indexOf(syArr[i]) != -1) {
            ieq = syArr[i];
            break;
        }
    }
    var exp = str.split(ieq);
    if (exp.length > 2) {
        var texp0 = exp.pop();
        var texp1 = exp.join("");
        exp = [texp0, texp1];
    }
    var _ieq = ieq;
    ieq = ieq == '&lt;' ? '<' : ieq == "\u2264" ? '<=' : ieq == '&le;' ? '<=' : ieq;
    ieq = ieq == '&gt;' ? '>' : ieq == "\u2265" ? '>=' : ieq == '&ge;' ? '>=' : ieq;
    ieq = ieq == "&eq;" ? '=' : ieq;
    ieq = ieq == "&ne;" || ieq == "\u2260" ? '!=' : ieq;
    return {ueq:_ieq, syeq:ieq, lexp:exp[0], rexp:exp[1]};
}
function applyUniqueVarX(str){
        if(!str) {
            return str;
        }
        
        var inputValue=str
        if(regExpMatch('\\*[a-z()]|[a-z()]\\*',inputValue)){
            inputValue=(regExpReplace('[a-z]',inputValue,'x'))
        }else if(regExpMatch('[0-9]\\([a-z]\\)',inputValue)){
            inputValue=(regExpReplace('\\([',inputValue,'*('))
        }else if(regExpMatch('[a-z]\\([0-9]',inputValue)){
            inputValue=(regExpReplace('\\(',inputValue,'*('))
        }else if(regExpMatch('[0-9][a-z]',inputValue)){
            inputValue=(regExpReplace('[a-z]',inputValue,'*x'))
        }
        return inputValue
}
function applyUniqueVarXForEval(str){
        if(!str)
            return str;
        
        var inputValue=str
        if(regExpMatch('\\*[a-z()]|[a-z()]\\*',inputValue)){
            inputValue=(regExpReplace('[a-z]',inputValue,'x'))
        }else if(regExpMatch('[0-9]\\([a-z]\\)',inputValue)){
            inputValue=(regExpReplace('\\([',inputValue,'*('))
        }else if(regExpMatch('[a-z]\\([0-9]',inputValue)){
            inputValue=(regExpReplace('\\(',inputValue,'*('))
        }else if(regExpMatch('[0-9][a-z]',inputValue)){
            inputValue=(regExpReplace('[a-z]',inputValue,'*x'))
        }
        return inputValue
}
function cm_unescape(str){
return unescape(decodeURI(str));
}
// unicode used:
// divide = \u00f7
// subscript_minus = \u208B (verify correct?)---->\u2013
// plus_minus = \u00B1
// asterisk = \u002A (verify correct?)----->correct
function toMathFormat(_str) {
    var str = _str;
    var opStr='/\u00f7*+-\u2013<=>][^\u00B1()\u002A';
    var hasPiSymb = str.indexOf(PI_SYM)>-1;
    if (hasPiSymb) {
        // str = str.split("\u03a0").join("pi");
        if(regExpMatch('\\*\\(?\u03c0|\u03c0\\)?\\*',str)){
        str=regExpReplace('\u03a0',str,'Math.PI')
        }else{
        if(regExpMatch('[0-9)]\u03c0',str)){
        str=regExpReplace('\u03c0',str,'*Math.PI')
        }
        if(regExpMatch('\u03c0[(0-9]',str)){
        str=regExpReplace('\u03c0',str,'Math.PI*')
        }
        }
    }
    var hasSqrtSymb = str.indexOf(SQUARE_ROOT_SYM);
    var strL = str.length;
    var lstr, rstr, uchar, fstr;
    fstr = "";
	var closeB=")"
    if (hasSqrtSymb > -1) {
        lstr = str.substring(0, hasSqrtSymb);
        rstr = str.substring(hasSqrtSymb + 1);
        if (rstr.length < 1) {
            return lstr;
        }
		if(lstr.length<1){
		fstr= "Math.sqrt("
		}else{
        if(lstr.charAt(lstr.length-1)=='*'){
       if(rstr.charAt(0)=="("){
		fstr = lstr + "Math.sqrt"
		closeB=""
		}else{
		fstr = lstr + "Math.sqrt("
		}
        }else{
       if(rstr.charAt(0)=="("){
		fstr = lstr + "*Math.sqrt"
		closeB=""
		}else{
		fstr = lstr + "*Math.sqrt("
		}
        }
		}
		//alert('fstr '+fstr+":"+closeB)
        var end = false;
        for (var i = 0; i < rstr.length; i++) {
            uchar = rstr.charAt(i);
            if (opStr.indexOf(uchar) != -1&&i!=0) {
                fstr += closeB+ rstr.substring(i);
                end = true;
                break;
            } else {
                fstr += uchar;
            }
        }
        if (!end) {
            fstr += closeB;
        }
		//alert('fstr '+fstr)
        return fstr;
    }
    return str;
}
// utility functions ends


/**
 * Each validation function has access to this._jsonObj which is the JSON
 * configuration object (ie, this._jsonObj.value)
 * 
 */
// VALIDATION FUNCTIONS START
// /////////////////////////////////////////////////////////////////////
// -! type=number_integer
function validateNumberInteger()
{
    var expectedValue = cm_unescape(this._jsonObj.value);
    var format = this._jsonObj.format;
    
    var inputValue=$get('widget_input_field_1').value;
    if(inputValue==expectedValue){
        return true;
    }else{
        if(format=="money"){
            if(Number(inputValue)==Number(expectedValue.substring(1))){
                throw (ERROR_UNITS);
            }else{
                return false;
            }
        }else{
            return false
        }
    }
}
HmFlashWidgetImplNumberInteger.prototype.processWidgetValidation = validateNumberInteger;



// -! type=number_decimal format=money
function validateNumberMoney()
{
    var inputValue=$get('widget_input_field_1').value;
    var expectedValue=cm_unescape(this._jsonObj.value);

    var numVal = 0;
    var hasUnits = false;
    if(inputValue.substring(0,1) == '$') {
        numVal = Number(inputValue.substring(1));
        hasUnits = true;
    }
    else 
        numVal = Number(inputValue);

    /** strip off leading $ */
    var numExpect = Number(expectedValue.substring(1));
    if(numVal==numExpect){
        if(!hasUnits)
            throw(ERROR_UNITS);
        return true;
    }else{
        return false;
    }
}
HmFlashWidgetImplNumberMoney.prototype.processWidgetValidation = validateNumberMoney;


// -! type=number_decimal
function validateNumberDecimal()
{
    var inputValue=$get('widget_input_field_1').value;
    var expectedValue=cm_unescape(this._jsonObj.value);
    if(inputValue==expectedValue){
        return true;
    }else{
        if(widget_format=="money"){
            if(Number(inputValue)==Number(expectedValue.substring(1))){
                throw Exception(ERROR_UNITS);
            }else{
                return false;
            }
        }else{
            return false
        }
    }
}





// -! type=number_simple_fraction
function validateNumberSimpleFraction()
{
    var expectedValue=cm_unescape(this._jsonObj.value);
	var simplified = $get('widget_input_simplified');

    /** isSimplified field avaialbe and checked?
     * 
     */
    var isSimplified = simplified && simplified.checked;
	var ans_isSimplified=expectedValue.split("|")[1]=='simplified'
    if(expectedValue.indexOf("[")>-1){
        var splitVal=expectedValue.split("]")
        var ewhole=splitVal[0].split("[")[1];
        splitVal=splitVal[1].split("/")
        var enumero=splitVal[0]
        var eden=splitVal[1]
        expectedValue=((ewhole*eden)+Number(enumero))+"/"+eden;
    }
	var eAns=eval(expectedValue);
    var inputValue=$get('widget_input_field_1').value;
    var isFrac=$get('widget_input_field_2').value!="";
    var fld3 = $get('widget_input_field_3');
    var isMixed=(fld3 != null && fld3.value!="");
    // alert($get('widget_input_field_2').value)
    isFrac=isMixed?false:isFrac
    var num,den,whole
    if(isFrac){
        num=inputValue
        den=$get('widget_input_field_2').value
        inputValue=num+"/"+den
    }
    if(isMixed){
        num=inputValue;
        den=$get('widget_input_field_2').value;
        whole=fld3.value;
        inputValue=((whole*den)+Number(num))+"/"+den
    }
    
    if(inputValue==expectedValue){
        return true;
    }else{
		if(eAns==inputValue){
		return true
		}
		if(isSimplified&&ans_isSimplified){
		return true;
		}
        if(eval(inputValue)==eval(expectedValue)){            
                throw(ERROR_SIMPLE_FRACTION);            
        }else{
            return false
        }
    }
}
HmFlashWidgetImplSimpleFraction.prototype.processWidgetValidation = validateNumberSimpleFraction;



// -! type=number_fraction
/**
 * TODO: need widget for this
 * 
 * widget_input_simplified is avaiable
 * on text_simple.
 * 
 */
function validateNumberFraction()
{
    var simplified = $get('widget_input_simplified');

    /** isSimplified field avaialbe and checked?
     * 
     */
    var isSimplified = simplified && simplified.checked;
    
    var expectedValue=cm_unescape(this._jsonObj.value);
    
    if(expectedValue.indexOf("[")>-1){
        var splitVal=expectedValue.split("]")
        var ewhole=splitVal[0].split("[")[1];
        splitVal=splitVal[1].split("/")
        var enumero=splitVal[0]
        var eden=splitVal[1]
        expectedValue=((ewhole*eden)+Number(enumero))+"/"+eden;
    }
    var inputValue=$get('widget_input_field_1').value;
    var isFrac=$get('widget_input_field_2').value!="";
    var fld3 = $get('widget_input_field_3');
    var isMixed=false;
    if(fld3)
        isMixed=fld3.value!="";
    
    
    isFrac=isMixed?false:isFrac
    var num,den,whole
    if(isFrac){
        num=inputValue
        den=$get('widget_input_field_2').value
        inputValue=num+"/"+den
    }
    if(isMixed){
        num=inputValue;
        den=$get('widget_input_field_2').value;
        whole=fld3.value;
        inputValue=((whole*den)+Number(num))+"/"+den
    }
    
    if(inputValue==expectedValue){
        return true;
    }else{
        if(eval(inputValue)==eval(expectedValue)){
            return true;            
        }else{
            return false;
        }
    }
}
HmFlashWidgetImplNumberIntegerFraction.prototype.processWidgetValidation = validateNumberFraction;





// -! type=number_rational
function validateNumberRational()
{
    var expectedValue=cm_unescape(this._jsonObj.value);
    if(expectedValue.indexOf("[")>-1){
        var splitVal=expectedValue.split("]")
        var ewhole=splitVal[0].split("[")[1];
        splitVal=splitVal[1].split("/")
        var enumero=splitVal[0]
        var eden=splitVal[1]
        expectedValue=((ewhole*eden)+Number(enumero))+"/"+eden;
    }
    var inputValue=$get('widget_input_field_1').value;
    var isFrac=$get('widget_input_field_2').value!="";
    var fld3 = $get('widget_input_field_3');
    var isMixed=fld3 != null && fld3.value!="";
    // alert($get('widget_input_field_2').value)
    isFrac=isMixed?false:isFrac
    var num,den,whole
    if(isFrac){
        num=inputValue
        den=$get('widget_input_field_2').value
        inputValue=num+"/"+den;
        
    }
    if(isMixed){
        num=inputValue;
        den=$get('widget_input_field_2').value;
        whole=fld3.value;
        inputValue=((whole*den)+Number(num))+"/"+den
    }
    // alert(inputValue+":"+num+":"+den)\
    //
    // Regular expressions do not support unicode chars
    // unicode:
    // pi = \u03a0
    // sroot = \u221A

    var isExpression_input=regExpMatch('[a-z\u221A\u03c0]',inputValue)
    var isExpression_ans=regExpMatch('[a-z\u221A\u03c0]',expectedValue)
    
    try {
        if(isExpression_input){
            var x=2
            inputValue=applyUniqueVarX(inputValue)
            inputValue=toMathFormat(inputValue)
            inputValue=eval(inputValue);
        }else{
            inputValue=eval(inputValue)
        }
        if(isExpression_ans){
            var x=2
            expectedValue=expectedValue.replace("pi",PI_SYM)
            expectedValue=expectedValue.replace("sqrt",SQUARE_ROOT_SYM)
            expectedValue=applyUniqueVarX(expectedValue)
            expectedValue=toMathFormat(expectedValue)
            // alert(expectedValue)
            expectedValue=eval(expectedValue);
        }else{
            expectedValue=eval(expectedValue)
        }
        // alert(expectedValue+":"+inputValue)
        if(inputValue==expectedValue){
            return true;
        }else{        
            return false;        
        }
    }
    catch(ex) {
        // report error?
        // alert(ex);
        return false;
    }
}
HmFlashWidgetImplRational.prototype.processWidgetValidation = validateNumberRational;








// -! type=number_mixed_fraction
function validateNumberMixedFraction()
{
    var expectedValue=cm_unescape(this._jsonObj.value);
    console.log("DEBUG_MIXED_FRACTION_VALIDATION: expected value 1 "+expectedValue)
    if(expectedValue.indexOf("[")>-1){
        var splitVal=expectedValue.split("]")
        var ewhole=splitVal[0].split("[")[1];
        splitVal=splitVal[1].split("/")
        var enumero=splitVal[0]
        var eden=splitVal[1]
        expectedValue=((ewhole*eden)+Number(enumero))+"/"+eden;
    }
	console.log("DEBUG_MIXED_FRACTION_VALIDATION: expected value 2 "+expectedValue)
    var inputValue=$get('widget_input_field_1').value;
    var isFrac=$get('widget_input_field_2')!=undefined;
    var isMixed=$get('widget_input_field_3')!=undefined;
    var num,den,whole
	console.log("DEBUG_MIXED_FRACTION_VALIDATION: isMixed "+isMixed+":"+isFrac)
    if(isFrac){
        num=inputValue
        den=$get('widget_input_field_2').value
        inputValue=num+"/"+den
    }
    if(isMixed){
        /** todo:
         *   If a mixed fraction, then inputValue 
         *   here would always be a string, ie. 1/5 ...
         *   because it is also a isFrac.
         */
        whole=$get('widget_input_field_1').value;
        num=$get('widget_input_field_2').value;
        den=$get('widget_input_field_3').value;
        inputValue=((Number(whole)*Number(den))+Number(num))+"/"+den
		console.log("DEBUG_MIXED_FRACTION_VALIDATION: input value "+whole+":"+num+":"+den+":"+inputValue)
    }
    
    if(inputValue==expectedValue){
        return true;
    }else{        
        return false;        
    }
}
HmFlashWidgetImplMixedFraction.prototype.processWidgetValidation = validateNumberMixedFraction;








// -! type=mChoice
function validateMChoice()
{
    var expectedValue=cm_unescape(this._jsonObj.value).split("|");
    expectedValue=eval(expectedValue[expectedValue.length-1])-1;
    var inputControls=$get('hm_widget_form');
    var inputValue=null;
    for(var i=0,l=inputControls.length;i<l;i++) {
        if(inputControls[i].checked) {
            inputValue = i;
            break;
        }
    }    
    if(inputValue==expectedValue){
        return true;
    }else{        
        return false;    
    }    
}
HmFlashWidgetImplMulti.prototype.processWidgetValidation = validateMChoice;









// -! type=coordinates
function validateCoordinates()
{
	widget_answer=cm_unescape(this._jsonObj.value);
    var expectedValue=widget_answer.indexOf(",")>-1?widget_answer.split(","):widget_answer.split("|");
    var inputValue=[$get('widget_input_field_1').value,$get('widget_input_field_2').value]    
    if(inputValue[0]==expectedValue[0]&&inputValue[1]==expectedValue[1]){
        return true;
    }else{        
        return false;    
    }    
}
// -! type=string&letter
//  ignore case 
function validateString()
{
    var expectedValue=cm_unescape(this._jsonObj.value);
    var inputValue=$get('widget_input_field_1').value  
    if(inputValue.toLowerCase() == expectedValue.toLowerCase()){
        return true;
    }else{        
        return false;    
    }    
}
HmFlashWidgetImplLetter.prototype.processWidgetValidation = validateString;


// -! type=inequality
function validateInequality()
{
    var expectedValue=cm_unescape(this._jsonObj.value);
    var inputValue=$get('widget_input_field_1').value 
    var inIEq=splitAtInEq(inputValue);
    var outIEq=splitAtInEq(expectedValue);
    var olex = applyUniqueVarX(outIEq.lexp);
    var ilex = applyUniqueVarX(inIEq.lexp);
    var orex = applyUniqueVarX(outIEq.rexp);
    var irex = applyUniqueVarX(inIEq.rexp);
    if (outIEq.syeq == inIEq.syeq) {            
            if (olex == ilex) {
                if (orex == irex) {
                    return true;
                }
            } else {
                //
                var x=2;
                var ole = eval(olex);
                var ile = eval(ilex);
                var ore = eval(orex);
                var ire = eval(irex);
                if (ole == ile) {
                    if (ore == ire) {
                        return true;
                    }
                }
            }
    } else if (revSymb[outIEq.syeq] == inIEq.syeq) {
            if (orex == ilex) {
                if (olex == irex) {
                    return true;
                }
            } else {
                var x=2;
                var ole = eval(olex);
                var ile = eval(ilex);
                var ore = eval(orex);
                var ire = eval(irex);
                if (ore == ile) {
                    if (ole == ire) {
                        return true;
                    }
                }
            }
    }
    return false;
}
HmFlashWidgetImplInequality.prototype.processWidgetValidation = validateInequality;






// -! type=inequality_exact
function validateInequalityExact()
{
    var expectedValue=cm_unescape(this._jsonObj.value);
    var inputValue=$get('widget_input_field_1').value  
    inputValue=inptValue.replace(/[*()]/g,"");
    if(inputValue==expectedValue){
        return true;
    }else{        
        return false;    
    }
}
HmFlashWidgetImplInequalityExact.prototype.processWidgetValidation = validateInequalityExact;



// -! type=power
function validatePower()
{
    var expectedValue=cm_unescape(this._jsonObj.value).split("^");
    var inputValue=[$get('widget_input_field_1').value,$get('widget_input_field_2').value];
    var x=2;
    var obex = applyUniqueVarX(expectedValue[0]);
    var ibex = applyUniqueVarX(inputValue[0]);
    var oeex = applyUniqueVarX(expectedValue[1]);
    var ieex = applyUniqueVarX(inputValue[1]);
    var obe = eval(obex);
    var ibe = eval(ibex);
    var oee = eval(oeex);
    var iee = eval(ieex);
    if(obe==ibe&&oee==iee){
        return true;
    }else{        
        return false;    
    }
}
HmFlashWidgetImplPowerForm.prototype.processWidgetValidation = validatePower;




// -! type=scientific_notation
function validateScientificNotation()
{
    var fld1 = $get('widget_input_field_1');
    var fld2 = $get('widget_input_field_2');
    widget_answer = cm_unescape(this._jsonObj.value);
    
    var expectedValue=widget_answer.indexOf("x10^")?widget_answer.split("x10^"):widget_answer.split("|");
    var inputValue=[$get('widget_input_field_1').value,$get('widget_input_field_2').value];
    if (inputValue[0]<1||inputValue[0]>9) {
                throw (ERROR_SCI_NOT);
    }else{
        if(inputValue[0]==expectedValue[0]&&inputValue[1]==expectedValue[1]){
            return true;
        }else{
            return false;
        }
    }
}
HmFlashWidgetImplSciNotation.prototype.processWidgetValidation = validateScientificNotation;






// -! type=point_slope
function validtePointSlope()
{
var expectedValue=cm_unescape(this._jsonObj.value);
var yval=$get('widget_input_field_1').value
var slope=$get('widget_input_field_2').value
var xval=$get('widget_input_field_3').value
var inputValue=yval+"|"+slope+"|"+xval;
if(inputValue==expectedValue){
    return true;
}else{        
    return false;    
}
}
HmFlashWidgetImplPointSlopeForm.prototype.processWidgetValidation = validtePointSlope;


// -! type=odds
function validateOdds()
{
    var expectedValue=cm_unescape(this._jsonObj.value);
    var inputValue=$get('widget_input_field_1').value;
    var ans={};
    var inp={};
    if (expectedValue.indexOf("/") != -1) {
        ans = expectedValue.split("/");
    } else if (expectedValue.indexOf(":") != -1) {
        ans = expectedValue.split(":");
    } else if (expectedValue.indexOf("to") != -1) {
        ans = expectedValue.split("to");
    }
    var prob = eval(ans[0]) + eval(ans[1]);
    if (inputValue.indexOf("/") != -1) {
        inp = inputValue.split("/");
    } else if (inputValue.indexOf(":") != -1) {
        inp = inputValue.split(":");
    } else if (inputValue.indexOf("to") != -1) {
        inp = inputValue.split("to");
    }
    if (inputValue == expectedValue) {
        return true;
    } else {
        if (eval(ans[0]) == eval(inp[0]) && eval(ans[1]) == eval(inp[1])) {
            return true;
        } else if (eval(ans[0]) == eval(inp[0]) && (eval(inp[1]) == prob)) {
            throw Exception(ERROR_ODDS)
        } else {
            return false;
        }
    }
}
HmFlashWidgetImplOdds.prototype.processWidgetValidation = validateOdds;if(typeof console === "undefined") {
    console = {log:function(x) {
        // empty
    }};
}
/**
 * @author sathesh
 */
var Plotter = function (graphObj, plot_type, plot_input) {
        this.pInf = Number.POSITIVE_INFINITY;
        this.nInf = Number.NEGATIVE_INFINITY;
        this.fArr = ["sin", "cos", "tan", "asin", "acos", "atan", "log", "abs", "sqrt", "ln", "cot", "sec", "csc"];
        this.graphObj = graphObj;
        this.graphObj.plotObj = this;
        this.plot_type = plot_type;
        this.plot_input = plot_input;
        this.graph_type = this.graphObj.graph_type;
        this.numberLine = this.graph_type == 'x' ? true : false;
        //
        this.document = this.graphObj.document
        this.canvas = this.graphObj.document.createElement("canvas");
        this.canvas.width = this.graphObj.width;
        this.canvas.height = this.graphObj.height;
        //
        this.canvas.style.position = 'absolute';
        this.canvas.style.top = 0;
        this.canvas.style.left = 0;
        //
        this.graphObj.board.appendChild(this.canvas);
		try{
			if (typeof G_vmlCanvasManager != "undefined") {
				G_vmlCanvasManager.initElement(this.canvas);
				//console.log("DEBUG_IE: G_vmlCanvasManager available");
			}else{
				console.log("DEBUG_IE: G_vmlCanvasManager not available");
			}
		}catch(error){
			console.log("DEBUG_IE:"+error);
		}
        this.context = this.canvas.getContext('2d');
        //
        this.icanvas = this.graphObj.document.createElement("canvas");
        this.icanvas.width = this.graphObj.width;
        this.icanvas.height = this.graphObj.height;
        //
        this.icanvas.style.position = 'absolute';
        this.icanvas.style.top = 0;
        this.icanvas.style.left = 0;
        //
        this.graphObj.board.appendChild(this.icanvas);
		try{
			if (typeof G_vmlCanvasManager != "undefined") {
				G_vmlCanvasManager.initElement(this.icanvas);
				//console.log("DEBUG_IE: G_vmlCanvasManager available");
			}else{
				console.log("DEBUG_IE: G_vmlCanvasManager not available");
			}
		}catch(error){
			console.log("DEBUG_IE:"+error);
		}
        this.icontext = this.icanvas.getContext('2d');
        //
        //this.context.strokeStyle='red';
        //console.log(this.context.strokeStyle);
        this.offX = 0;
        this.offY = 0;
        this.getCanvasOffSet();
        this.cpos = this.coordToCanvasPoint(0, 0);
        //console.log(this.cpos[0]+":"+this.cpos[1]);
        EqParser.init();
        this.getPlots();
    }
Plotter.prototype.createZoomCanvas = function () {
    this.zcanvas = this.graphObj.document.createElement("canvas");
    this.zcanvas.width = 100;
    this.zcanvas.height = 60;
    //
    this.zcanvas.style.position = 'absolute';
    this.zcanvas.style.top = -60 + "px";
    this.zcanvas.style.left = 0 + "px";
    //
    this.graphObj.board.appendChild(this.zcanvas);
	try{
		if (typeof G_vmlCanvasManager != "undefined") {
				G_vmlCanvasManager.initElement(this.canvas);
				//console.log("DEBUG_IE: G_vmlCanvasManager available");
		}else{
				console.log("DEBUG_IE: G_vmlCanvasManager not available");
			}
	}catch(error){
		console.log("DEBUG_IE:"+error);
	}
    this.zcontext = this.zcanvas.getContext('2d');
}
Plotter.prototype.renderZoomCanvas = function (x, y) {
    this.zcontext.clearRect(0, 0, this.zcanvas.width, this.zcanvas.height);
    //this.zcontext.save();
    var gcntx = this.graphObj.context;
    var pcntx = this.context;
    var gw = this.graphObj.width;
    var gh = this.graphObj.height;
    var off = 15;
    var wi = this.zcanvas.width;
    var hi = this.zcanvas.height;
    var hwi = wi / 2;
    var hhi = hi / 2;
    var dx = x - hwi;
    var dy = y - hi - off;
    //console.log("TOP_POS:" + y + ":" + hi + ":" + off + ":" + dx + ":" + dy)
    if (dx < 0) {
        dx = x
    } else if (dx + wi > gw) {
        dx = x - wi
    }
    if (dy < 0) {
        dy = y + (off)
        dx = x
        if (dx < 0) {
            dx = x
        } else if (dx + wi > gw) {
            dx = x - wi
        }
    } else if (dy > gh) {
        //dy=y-wi
    }
    var sx, sy
    sx = x < hwi / 2 ? 0 : x - (hwi / 2);
    sx = x > gw - (hwi / 2) ? gw - hwi : sx;
    sy = y < hhi / 2 ? 0 : y - (hhi / 2);
    sy = y > gh - (hhi / 2) ? gh - hhi : sy;
    //console.log("TOP_POS2:"+y+":"+hi+":"+off)
    this.zcanvas.style.top = dy + "px";
    this.zcanvas.style.left = dx + "px";

    this.zcontext.fillStyle = "white";
    this.zcontext.lineWidth = 2.0;
    this.zcontext.beginPath();
    this.zcontext.fillRect(0, 0, wi, hi);

    this.zcontext.strokeRect(0, 0, wi, hi);
    //this.zcontext.arc(x,dy-35,25,0,2*Math.PI,false);
    this.zcontext.closePath();
    //this.zcontext.fill();
    //this.zcontext.globalCompositeOperation = "copy";
    //this.zcontext.beginPath();
    this.zcontext.drawImage(this.graphObj.canvas, sx, sy, hwi, hhi, 0, 0, wi, hi);
    this.zcontext.drawImage(this.canvas, sx, sy, hwi, hhi, 0, 0, wi, hi);
    this.zcontext.drawImage(this.icanvas, sx, sy, hwi, hhi, 0, 0, wi, hi);
    //this.zcontext.drawImage(this.canvas,x-25,y-25,50,50,dx,dy,100,100);
    //this.zcontext.globalCompositeOperation = "source-over";
    //this.drawPoint(hwi,hhi,'#ff0000',8,false,this.zcontext);
    //this.zcontext.restore();
}
Plotter.prototype.clear = function (ctx) {
    var _ctx = ctx ? ctx : this.context
    _ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
}
Plotter.prototype.getCanvasOffSet = function () {
    var box = this.canvas.getBoundingClientRect();
    var body = this.document.body;
    var docElem = this.document.documentElement;
    var scrollTop = window.pageYOffset || docElem.scrollTop || body.scrollTop;
    var scrollLeft = window.pageXOffset || docElem.scrollLeft || body.scrollLeft;
    var clientTop = docElem.clientTop || body.clientTop || 0;
    var clientLeft = docElem.clientLeft || body.clientLeft || 0;
    var top = box.top + scrollTop - clientTop;
    var left = box.left + scrollLeft - clientLeft;
    this.offX = Math.round(left);
    this.offY = Math.round(top);
    return {
        top: this.offY,
        left: this.offX
    }
}
Plotter.prototype.getCursorPos = function (e) {
    this.isTouchEnabled = e.type.indexOf('touch') > -1;
    var ev = e ? e : window.event;
    ev = this.isTouchEnabled ? ev.changedTouches[0] : ev;
    var cursor = {
        x: 0,
        y: 0
    };
    if (ev.pageX !== undefined) {
        cursor.x = ev.pageX - this.offX;
        cursor.y = ev.pageY - this.offY;

    } else {
        cursor.x = ev.clientX - this.offX;
        cursor.y = ev.clientY - this.offY;

    }
    //console.log("C_OFFSET:"+ev.pageX+":"+ev.clientX+":"+this.offX)
    return cursor;
}
Plotter.prototype.validateFunc = function () {
    var boo = true;
    var veqn = this.fn;
    var pos, len, brac;
    for (var f = 0; f < this.fArr.length; f++) {
        len = this.fArr[f].length;
        pos = veqn.indexOf(this.fArr[f]);
        if (pos == -1) {
            continue;
        } else {
            brac = veqn.substr(pos + len, 1);
            if (brac != "(") {
                boo = false;
                break;
            }
        }
    }
    return boo;
}
Plotter.prototype.getDval = function (eqO, v, dv) {

    var dval = this.fixTo(v + dv, 4);
    return this.evalFor(eqO, dval);
}
Plotter.prototype.sign = function (val) {
    if (val * 1 < 0) {
        return '-';
    } else {
        return '+';
    }
}
Plotter.prototype.setFunctionDatas = function () {
    this.fn = this.funcObj[0][0];
    this.fncol = this.funcObj[2];
    this.fneql = this.funcObj[0][2];
    this.fand = this.funcObj[0][3];
    this.fof = this.funcObj[0][1];
    this.fof = this.fof ? this.fof : 'x';
    this.fneql = this.fneql ? this.fneql : 'eq';
    if (this.fand === undefined) {
        if (this.fneql.indexOf("_") > -1 && this.fn.indexOf("_") > -1) {
            this.fand = true;
        }
    }
    //alert(this.fncol);
}
Plotter.prototype.coordToCanvasPoint = function (x, y) {
    var xp = this.graphObj.axisYpos + x * (this.graphObj.scaleX / this.graphObj.xinc);
    var yp = this.graphObj.axisXpos - y * (this.graphObj.scaleY / this.graphObj.yinc);
    return [xp, yp];
}
Plotter.prototype.canvasPosToCoords = function (xp, yp) {
    //console.log("canvasPosToCoords"+xp+":"+yp)
    var x = (xp - this.graphObj.axisYpos) / (this.graphObj.scaleX / this.graphObj.xinc)
    var y = (-yp + this.graphObj.axisXpos) / (this.graphObj.scaleY / this.graphObj.yinc)
    return [x, y];
}
Plotter.prototype.fixTo = function (v, p) {
    var p10 = Math.pow(10, p);
    return Math.round(p10 * v) / p10;
}
Plotter.prototype.evalFor = function (ev, fr) {
    return EqParser.evalEq(ev, fr);
}
Plotter.prototype.getPlots = function () {
    this.getPlotInputs();
    var temp1 = this.plot_datas.split("|");
    //console.log("getPlots:"+temp1)
    for (var i = 0; i < temp1.length; i++) {
        var pointdata = eval(temp1[i]);
        //console.log("getPlots_pd:"+pointdata)
        this.plot_type = pointdata.shift();
        this.plot_data = pointdata;
        //console.log("getPlots_ty:"+this.plot_type)
        this.drawPlot();
    }
}
Plotter.prototype.drawPlot = function () {

    if (this.plot_type == 'point') {
        this.plotPoint();
    } else if (this.plot_type == 'function') {
        this.plotFunction();
    } else if (this.plot_type == 'polygon') {
        this.close_path = true;
        this.plotPolygon();
    } else if (this.plot_type == 'path') {
        this.close_path = false;
        this.plotPolygon();
    } else if (this.plot_type == 'circle') {
        this.plotCircle();
    } else if (this.plot_type == 'rectangle') {
        this.plotRect();
    } else if (this.plot_type == "interactive") {
        this.iplot_type = this.plot_data[0];
        this.node_count = 0;
        if (this.iplot_type == 'point') {
            this.totalNodes = 1;
        } else if (this.iplot_type == 'line' || this.iplot_type == 'segment') {
            this.totalNodes = 2;
            //console.log("getPlots_pd2:"+this.plot_data)
        } else if (this.iplot_type == 'path' || this.iplot_type == 'polygon') {
            this.totalNodes = this.plot_data[1].length;
        } else if (this.iplot_type == 'inequality') {

            var f1 = function () {
                    this.scope.totalNodes = 1;
                    this.scope.fand = false;
                }
            var f2 = function () {
                    this.scope.totalNodes = 1;
                    this.scope.fand = false;
                }
            var f3 = function () {
                    this.scope.totalNodes = 1;
                    this.scope.fand = false;
                }
            var f4 = function () {
                    this.scope.totalNodes = 1;
                    this.scope.fand = false;
                }
            var f5 = function () {
                    this.scope.totalNodes = 2;
                    this.scope.fand = true;
                }
            var f6 = function () {
                    this.scope.totalNodes = 2;
                    this.scope.fand = true;
                }
            var f7 = function () {
                    this.scope.totalNodes = 2;
                    this.scope.fand = true;
                }
            var f8 = function () {
                    this.scope.totalNodes = 2;
                    this.scope.fand = true;
                }

            var t1 = {
                type: 'button',
                id: 'btn_lt',
                label: 'lt',
                img_norm_src: 'in_lt_norm.png',
                img_sel_src: 'in_lt_sel.png',
                callfunc: f1,
                scope: this
            };
            var t2 = {
                type: 'button',
                id: 'btn_le',
                label: 'le',
                img_norm_src: 'in_le_norm.png',
                img_sel_src: 'in_le_sel.png',
                callfunc: f2,
                scope: this
            };
            var t3 = {
                type: 'button',
                id: 'btn_gt',
                label: 'gt',
                img_norm_src: 'in_gt_norm.png',
                img_sel_src: 'in_gt_sel.png',
                callfunc: f3,
                scope: this
            };
            var t4 = {
                type: 'button',
                id: 'btn_ge',
                label: 'ge',
                img_norm_src: 'in_ge_norm.png',
                img_sel_src: 'in_ge_sel.png',
                callfunc: f4,
                scope: this
            };
            var t5 = {
                type: 'button',
                id: 'btn_gt_lt',
                label: 'gt_lt',
                img_norm_src: 'in_gt_lt_norm.png',
                img_sel_src: 'in_gt_lt_sel.png',
                callfunc: f5,
                scope: this
            };
            var t6 = {
                type: 'button',
                id: 'btn_ge_le',
                label: 'ge_le',
                img_norm_src: 'in_ge_le_norm.png',
                img_sel_src: 'in_ge_le_sel.png',
                callfunc: f6,
                scope: this
            };
            var t7 = {
                type: 'button',
                id: 'btn_gt_le',
                label: 'gt_le',
                img_norm_src: 'in_gt_le_norm.png',
                img_sel_src: 'in_gt_le_sel.png',
                callfunc: f7,
                scope: this
            };
            var t8 = {
                type: 'button',
                id: 'btn_ge_lt',
                label: 'ge_lt',
                img_norm_src: 'in_ge_lt_norm.png',
                img_sel_src: 'in_ge_lt_sel.png',
                callfunc: f8,
                scope: this
            };
            var toolData = [t1, t2, t3, t4, t5, t6, t7, t8];
            this.graphObj.createTools(toolData, {
                x: 0,
                y: this.canvas.height - 42
            });
            this.graphObj.inter_obj = {
                zoom: true
            };
            this.graphObj.addGraphUI();
        }
        if (this.iplot_type != 'inequality') {
            this.startListeners();
        }
        this.createZoomCanvas();
    }
}
Plotter.prototype.startListeners = function () {
    this.initMouseListeners();
    this.graphObj.init_graph_transformListeners();
}
Plotter.prototype.getPlotInputs = function () {
    this.plot_datas = this.plot_input.data;
    this.fn_color = this.plot_input.fn_color;
    this.fn_color = this.fn_color ? this.fn_color : '#0000ff';
}
Plotter.prototype.plotCircle = function () {
    var cx, cy, r, w, h
    if (this.plot_data[0].length == 1) {
        cx = 0;
        cy = 0;
        r = this.plot_data[0][0];
        w = r * 2;
        h = w;
    } else if (this.plot_data[0].length == 2) {
        cx = 0;
        cy = 0;
        w = this.plot_data[0][0];
        h = this.plot_data[0][1];

    } else {
        cx = this.plot_data[0][0];
        cy = this.plot_data[0][1];
        r = this.plot_data[0][2];
        w = r * 2;
        h = w;
    }
    var pt = this.coordToCanvasPoint(cx, cy);
    r = r * this.graphObj.scaleX;
    var label = this.plot_data[1];
    var color = this.plot_data[2];
    var scolor = color[0];
    var fcolor = color[1];
    var falpha = color[2] ? color[2] : 0.6;
    if (fcolor) {
        var falpha = color[2] ? color[2] : 0.6;
        fcolor = this.hex2rgb(fcolor, falpha);
    }
    this.drawCircle(pt[0], pt[1], r, true, scolor, falpha, fcolor);
}
Plotter.prototype.plotRect = function () {
    var cx, cy, r, w, h
    if (this.plot_data[0].length == 1) {
        cx = 0;
        cy = 0;
        //r=this.plot_data[0][0];
        w = this.plot_data[0][0];
        h = this.plot_data[0][1];
    } else if (this.plot_data[0].length == 2) {
        cx = 0;
        cy = 0;
        w = this.plot_data[0][0];
        h = this.plot_data[0][1];

    } else {
        cx = this.plot_data[0][0];
        cy = this.plot_data[0][1];
        //r=this.plot_data[0][2];
        w = this.plot_data[0][2];
        h = this.plot_data[0][3];
    }
    var pt = this.coordToCanvasPoint(cx, cy);
    w = w * this.graphObj.scaleX;
    h = h * this.graphObj.scaleY;
    //console.log(pt[0] + ":" + pt[1] + ":" + w + ":" + h)
    pt[0] = pt[0] - w / 2;
    pt[1] = pt[1] - h / 2;
    var label = this.plot_data[1];
    var color = this.plot_data[2];
    var scolor = color[0];
    var fcolor = color[1];
    if (fcolor) {
        var falpha = color[2] ? color[2] : 0.6;
        fcolor = this.hex2rgb(fcolor, falpha);
    }

    this.drawRect(pt[0], pt[1], w, h, true, scolor, falpha, fcolor);
}
Plotter.prototype.plotPolygon = function () {
    var pts = this.plot_data[0];
    var label = this.plot_data[1];
    var color = this.plot_data[2];
    var scolor = color[0] ? color[0] : "BLUE";
    var fcolor = color[1];
    var close = this.close_path;
    if (!close) {
        scolor = color;
        fcolor = undefined;
    }
    if (fcolor) {
        var falpha = color[2] ? color[2] : 0.6;
        fcolor = this.hex2rgb(fcolor, falpha);
    }
    var scope = this.context;
    scope.lineWidth = 3.0;
    scope.strokeStyle = scolor
    scope.beginPath();
    for (var i = 0; i < pts.length; i++) {
        var pt = this.coordToCanvasPoint(pts[i][0], pts[i][1]);
        if (i == 0) {
            scope.moveTo(pt[0], pt[1]);
        } else {
            scope.lineTo(pt[0], pt[1]);
        }
        //console.log(pt[0] + ":" + pt[1] + ":" + scolor + ":" + fcolor)
    }
    if (close) {
        pt = this.coordToCanvasPoint(pts[0][0], pts[0][1]);
        scope.lineTo(pt[0], pt[1]);
    }

    if (true) {
        scope.stroke();
    }
    if (close && falpha) {
        scope.fill();
    }
    scope.closePath();
    if (label) {
        for (var i = 0; i < pts.length; i++) {

            this.plotPoint([
                [pts[i][0], pts[i][1]], label, scolor])
        }
    }

}
Plotter.prototype.plotPoints = function () {
    var temp1 = this.plot_data.split("|");
    for (var i = 0; i < temp1.length; i++) {
        var pointdata = eval(temp1[i]);
        if (pointdata[0].length == 1) {
            this.plotPoint(pointdata[0][0], 0, pointdata[1], pointdata[2]);
        } else {
            this.plotPoint(pointdata[0][0], pointdata[0][1], pointdata[1], pointdata[2]);
        }

    }
}
Plotter.prototype.plotPoint = function (data) {
    var pdata = data ? data : this.plot_data
    var x = pdata[0][0];
    var y = pdata[0][1];
    y = y ? y : 0;
    var label = pdata[1];
    var color = pdata[2];
    var pt = this.coordToCanvasPoint(x, this.graph_type == 'xy' ? y : 0);
    var xp = pt[0];
    var yp = pt[1];
    this.drawPoint(xp, yp, color ? color : this.fn_color);
    if (label) {

        this.context.textBaseline = 'bottom';
        this.context.font = "bold 12px sans-serif";
        if (this.graph_type == 'xy') {
            this.context.fillText("(" + x + ", " + y + ")", xp + 3, yp - 6);
        } else {
            this.context.fillText(x, xp - 3, yp - 6);
        }

    }
}
Plotter.prototype.plotFunctions = function () {
    var temp1 = this.plot_data.split("|");
    for (var i = 0; ihilength; i++) {
        var fndata = eval(temp1[i]);
        this.plotFunction(fndata);
    }

}

Plotter.prototype.plotFunction = function () {
    this.funcObj = this.plot_data;
    this.getPointsFromEq();
}
//--------------------------------------------------------------------------------------//
Plotter.prototype.drawPoint = function (x, y, color, r, _open, _cntx) {
    var ctx = _cntx ? _cntx : this.context;
    var open = _open ? _open : false;
    var rad = r ? r : 4;
    if (!open) {
        ctx.fillStyle = color;
    } else {
        ctx.strokeStyle = color;
    }

    ctx.beginPath();
    ctx.arc(x, y, rad, 0, 2 * Math.PI, false);

    if (!open) {
        ctx.fill();
    } else {
        ctx.stroke();
    }
    ctx.closePath();
}
Plotter.prototype.drawCircle = function (x, y, rad, stroke, stroke_color, fill, fill_color, ctx) {
    var context = ctx ? ctx : this.context;
    if (stroke) {
        context.lineWidth = 2.0;
        context.strokeStyle = stroke_color;
    }
    if (fill) {
        context.fillStyle = fill_color;
    }
    //var w = wd;
    //var h = ht;
    context.beginPath();
    context.arc(x, y, rad, 0, 2 * Math.PI, false);
    if (stroke) {
        context.stroke();
    }
    if (fill) {
        context.fill();
    }
    context.closePath();
}
Plotter.prototype.drawRect = function (x, y, w, h, stroke, stroke_color, fill, fill_color, ctx) {
    var context = ctx ? ctx : this.context;
    if (stroke) {
        context.lineWidth = 2.0;
        context.strokeStyle = stroke_color;
    }
    if (fill) {
        context.fillStyle = fill_color;
    }
    //var w = wd;
    //var h = ht;
    context.beginPath();

    if (fill) {
        context.fillRect(x, y, w, h);
        //context.fill();
    }
    if (stroke) {
        context.strokeRect(x, y, w, h);
        //context.stroke();
    }
    context.closePath();
}
Plotter.prototype.roundRect = function (ctx, x, y, width, height, radius, fill, stroke) {
    if (typeof stroke == "undefined") {
        stroke = true;
    }
    if (typeof radius === "undefined") {
        radius = 5;
    }
    ctx.beginPath();
    ctx.moveTo(x + radius, y);
    ctx.lineTo(x + width - radius, y);
    ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
    ctx.lineTo(x + width, y + height - radius);
    ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
    ctx.lineTo(x + radius, y + height);
    ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
    ctx.lineTo(x, y + radius);
    ctx.quadraticCurveTo(x, y, x + radius, y);
    ctx.closePath();
    if (stroke) {
        //console.log("STROKE")
        ctx.stroke();
    }
    if (fill) {
        ctx.fill();
    }
}
//--------------------------------------------------------------------------------------//
Plotter.prototype.getPointsFromEq = function () {
    var pInf = this.pInf;
    var nInf = this.nInf;
    this.setFunctionDatas();
    this.setAxisDatas();
    if (this.numbLine) {
        return this.plotNumberLine();
    }
    var fn = this.fn;
    var fneql = this.fneql;
    var f = this.fof;
    if (f == "y") {
        return this.getPointsFromEqY();
    }
    this.graph_color = this.fncol;
    if (fn == "" || fn == undefined) {
        return null;
    }
    var buffrArr = [];
    var eqnObj = this.eqnObj = this.fn;
    var eqSy = this.eqSy = fneql;
    var acc = .1;
    var xscale = this.xscale;
    var yscale = this.yscale;
    var xaxis = this.graphObj.axisYpos;
    var yaxis = this.graphObj.axisXpos;
    var ptsArr = [];
    var xLow = this.xMin;
    var xHi = this.xMax;
    var yLow = this.yMin;
    var yHi = this.yMax;
    var y, x0, y0, x1, y1;
    var xPix, yPix, x0Pix, y0Pix, x1Pix, y1Pix, ycomp, dy0, dy1;
    this.doPlot = true;
    var fixTo = this.fixTo;
    var getDval = this.getDval;
    acc = Math.abs(xHi - xLow) / 300;
    for (var x = xLow; x <= xHi; x += acc) {
        x = fixTo(x, 4);
        y = -this.evalFor(eqnObj, x);
        x0 = x - acc;
        y0 = -this.evalFor(eqnObj, x0);
        x1 = x + acc;
        y1 = -this.evalFor(eqnObj, x1);
        //console.log(x + " <> " + y);
        if (Number(y)) {
            //var xpts=this.
            xPix = x * xscale + xaxis;
            yPix = y * yscale + yaxis;
            x0Pix = x0 * xscale + xaxis;
            y0Pix = y0 * yscale + yaxis;
            x1Pix = x1 * xscale + xaxis;
            y1Pix = y1 * yscale + yaxis;

            if (yPix > -2880 && yPix < 2880) {
                xPix = fixTo(xPix, 2);
                yPix = fixTo(yPix, 2);
                ptsArr.push({
                    x: xPix,
                    y: yPix,
                    l: "line"
                });
            } else if (yPix == nInf) {
                if (y0Pix != nInf && y1Pix != nInf && y0Pix != pInf && y1Pix != pInf && Number(y0Pix) != Number.NaN && Number(y1Pix) != Number.NaN) {
                    dy0 = this.getDval(eqnObj, x0, acc / 10);
                    if (y0Pix < this.yh && y0Pix > this.yl) {
                        if (dy0 > y0) {
                            ycomp = this.yh;
                        } else {
                            ycomp = this.yl;
                        }
                    } else {
                        ycomp = y0Pix;
                    }
                    ptsArr.push({
                        x: x0Pix,
                        y: ycomp,
                        l: "line"
                    });
                    ptsArr.push({
                        x: xPix,
                        y: ycomp,
                        l: "line"
                    });
                    ptsArr.push({
                        x: xPix,
                        y: -ycomp,
                        l: "move"
                    });
                    dy1 = this.getDval(eqnObj, x1, -acc / 10);
                    if (y1Pix < this.yh && y1Pix > this.yl) {
                        if (dy1 > y1) {
                            ycomp = this.yh;
                        } else {
                            ycomp = this.yl;
                        }
                    } else {
                        ycomp = y1Pix;
                    }
                    ptsArr.push({
                        x: x1Pix,
                        y: ycomp,
                        l: "move"
                    });
                } else {
                    ptsArr.push({
                        x: xPix,
                        y: nInf,
                        l: "move"
                    });
                }
            } else if (yPix == pInf) {
                if (y0Pix != nInf && y1Pix != nInf && y0Pix != pInf && y1Pix != pInf && Number(y0Pix) != Number.NaN && Number(y1Pix) != Number.NaN) {
                    dy0 = this.getDval(eqnObj, x0, acc / 10);
                    if (y0Pix < this.yh && y0Pix > this.yl) {
                        if (dy0 > y0) {
                            ycomp = this.yh;
                        } else {
                            ycomp = this.yl;
                        }
                    } else {
                        ycomp = y0Pix;
                    }
                    ptsArr.push({
                        x: x0Pix,
                        y: ycomp,
                        l: "line"
                    });
                    ptsArr.push({
                        x: xPix,
                        y: ycomp,
                        l: "line"
                    });
                    ptsArr.push({
                        x: xPix,
                        y: -ycomp,
                        l: "move"
                    });
                    dy1 = this.getDval(eqnObj, x1, -acc / 10);
                    if (y1Pix < this.yh && y1Pix > this.yl) {
                        if (dy1 > y1) {
                            ycomp = this.yh;
                        } else {
                            ycomp = this.yl;
                        }
                    } else {
                        ycomp = y1Pix;
                    }
                    ptsArr.push({
                        x: x1Pix,
                        y: ycomp,
                        l: "line"
                    });
                } else {
                    ptsArr.push({
                        x: xPix,
                        y: pInf,
                        l: "move"
                    });
                }
            } else {
                buffrArr.push(y);
            }
        }
    }
    ptsArr = this.asymFix(ptsArr);
    var n;
    if (ptsArr.length < 2) {
        if (buffrArr.length < 2) {
            if (this.validateFunc()) {
                alert("Error in function!");
            } else {
                alert("Check your input function!");
            }
        } else {
            alert("Cannot plot the given function!");
        }
        return null;
    } else {
        n = this.getNodeDetails(ptsArr);
        return this.drawFunction(n, ptsArr);
    }
}
Plotter.prototype.getNodeDetails = function (arr) {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var sNode = {};
    var eNode = {};
    var nodes = {};

    for (var q = 0; q < arr.length; q++) {
        if (q == 0) {
            if (this.fof == "x") {
                if (arr[q].y == this.pInf) {
                    arr[q].y = yh;
                    arr[q].l = "line";
                }
                if (arr[q].y == this.nInf) {
                    arr[q].y = yl;
                    arr[q].l = "line";
                }
            }
            if (this.fof == "y") {
                if (arr[q].x == this.pInf) {
                    arr[q].x = xh;
                    arr[q].l = "line";
                }
                if (arr[q].x == this.nInf) {
                    arr[q].x = xl;
                    arr[q].l = "line";
                }
            }
        } else {
            if (this.fof == "x") {
                if (arr[q].y == this.pInf || arr[q].y == this.nInf) {
                    arr[q].l = "move";
                    arr[q + 1].l = "move";
                }
            }
            if (this.fof == "y") {
                if (arr[q].x == this.pInf || arr[q].x == this.nInf) {
                    arr[q].l = "move";
                    arr[q + 1].l = "move";
                }
            }
        }
    }
    sNode.pt = arr[0];
    sNode.dir = this.getDir(sNode.pt);
    eNode.pt = arr[arr.length - 1];
    eNode.dir = this.getDir(eNode.pt);
    //alert("INF:"+sNode.dir+":"+eNode.dir);
    nodes.s = sNode;
    nodes.e = eNode;
    return nodes;
}
Plotter.prototype.setAxisDatas = function () {
    var mygraphObj = this.graphObj;
    this.numbLine = mygraphObj.graph_type == "x" ? true : false;
    this.midX = mygraphObj.width / 2;
    this.midY = mygraphObj.height / 2;
    this.xMin = (mygraphObj.xmin - mygraphObj.xinc);
    this.xMax = (mygraphObj.xmax + mygraphObj.xinc);
    this.yMin = (mygraphObj.ymin - mygraphObj.yinc);
    this.yMax = (mygraphObj.ymax + mygraphObj.yinc);
    this.xscale = mygraphObj.scaleX / mygraphObj.xinc;
    this.yscale = mygraphObj.scaleY / mygraphObj.yinc;
    this.xaxis = mygraphObj.axisYpos;
    this.yaxis = mygraphObj.axisXpos;
    if (mygraphObj.labType == "pi") {
        this.xMin = Math.round((xMin) * Math.PI);
        this.xMax = Math.round((xMax) * Math.PI);
        this.xscale = xscale / Math.PI;
    }
    if (mygraphObj.ylabType == "pi") {
        this.yMin = Math.round((yMin) * Math.PI);
        this.yMax = Math.round((yMax) * Math.PI);
        this.yscale = yscale / Math.PI;
    }
    this.xl = 0;
    this.xh = this.midX * 2;
    this.yl = 0;
    this.yh = this.midY * 2;
}
Plotter.prototype.getDir = function (pt) {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var xscale = this.xscale;
    var yscale = this.yscale;
    var xaxis = this.xaxis;
    var yaxis = this.yaxis;
    var xh = this.xh;
    var xl = this.xl;
    var yh = this.yh;
    var yl = this.yl;
    var dir = "C";
    if (pt.y >= yh && (pt.x <= xh && pt.x >= xl)) {
        dir = "N";
    }
    if (pt.y <= yl && (pt.x <= xh && pt.x >= xl)) {
        dir = "S";
    }
    if (pt.x >= xh && (pt.y <= yh && pt.y >= yl)) {
        dir = "E";
    }
    if (pt.x <= xl && (pt.y <= yh && pt.y >= yl)) {
        dir = "W";
    }
    if (pt.y >= yh && pt.x >= xh) {
        dir = "NE";
    }
    if (pt.y <= yl && pt.x >= xh) {
        dir = "SE";
    }
    if (pt.y >= yh && pt.x <= xl) {
        dir = "NW";
    }
    if (pt.y <= yl && pt.x <= xl) {
        dir = "SW";
    }
    return dir;
}
Plotter.prototype.plotPath = function (sN, eN, mc) {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var pathString, pathArr, rot;
    var eqS = this.eqSy;
    var xh = this.xh;
    var xl = this.xl;
    var yh = this.yh;
    var yl = this.yl;
    var fof = this.fof;
    if (eqS == "eq" || eN.dir == "C" || sN.dir == "C") {
        return false;
    }
    if (eqS == "ge" || eqS == "gt") {
        if (fof == "y") {
            pathString = "S-SE-E-NE-N-NW-W-SW-S";
            rot = "+";
        } else {
            pathString = "N-NE-E-SE-S-SW-W-NW-N";
            rot = "-";
        }
    }
    if (eqS == "le" || eqS == "lt") {
        if (fof == "y") {
            pathString = "N-NE-E-SE-S-SW-W-NW-N";
            rot = "-";
        } else {
            pathString = "S-SE-E-NE-N-NW-W-SW-S";
            rot = "+";
        }
    }
    pathArr = pathString.split("-");
    var xp, yp;
    var flag = false;
    var dir = "C";
    for (var p = 0; p < pathArr.length; p++) {
        dir = pathArr[p];

        if (eN.dir == dir) {
            xp = eN.pt.x;
            yp = eN.pt.y;
            mc.moveTo(xp, yp);
            //console.log('move:'+xp+":"+yp);
            flag = true;
            if (eN.dir == sN.dir) {
                if ((rot == "-" && dir == "S") || (rot == "+" && dir == "N")) {
                    xp = sN.pt.x;
                    yp = sN.pt.y;
                    mc.lineTo(xp, yp);
                    //console.log('line:'+xp+":"+yp);
                    flag = !true;
                    break;
                }
            }
            continue;
        }
        if (flag && dir.length == 2) {
            if (pathArr[p] == sN.dir) {
                //trace("DD:"+dir+":"+dir.length+":"+flag+":"+sN.pt.x+":"+sN.pt.y);
                mc.lineTo(sN.pt.x, sN.pt.y);
                //console.log('line0:'+dir+":"+sN.pt.x+":"+sN.pt.y);
                flag = false;
                break;
            } else {
                switch (dir) {
                case "NE":
                    if (rot == "-") {
                        xp = xh;
                    } else {
                        if (sN.dir == "N") {
                            yp = sN.pt.y;
                        } else {
                            yp = yh;
                        }
                    }
                    //trace("DD:"+dir+":"+dir.length+":"+xp+":"+yp);
                    mc.lineTo(xp, yp);
                    //      console.log('line:'+dir+":"+xp+":"+yp);
                    break;
                case "SE":
                    if (rot == "-") {
                        yp = yl;
                    } else {
                        xp = xh;
                    }
                    mc.lineTo(xp, yp);
                    //console.log('line:'+dir+":"+xp+":"+yp);
                    break;
                case "SW":
                    if (rot == "-") {
                        xp = xl;
                    } else {
                        yp = yl;
                    }
                    mc.lineTo(xp, yp);
                    //      console.log('line:'+dir+":"+xp+":"+yp);
                    break;
                case "NW":
                    if (rot == "-") {
                        yp = yh;
                    } else {
                        xp = xl;
                    }
                    mc.lineTo(xp, yp);
                    //console.log('line:'+dir+":"+xp+":"+yp);
                    break;
                }
            }
        } else if (flag) {
            if (pathArr[p] == sN.dir) {
                mc.lineTo(sN.pt.x, sN.pt.y);
                //console.log('line1:'+dir+":"+sN.pt.x+":"+sN.pt.y);
                flag = false;
                break;
            }
        }
    }
    return true;
}
Plotter.prototype.drawFunction = function (nodes, arr) {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var eqSy = this.eqSy;
    var lalpha = 1.0;
    var fHold = this.context;
    var fill;
    var xh = this.xh;
    var xl = this.xl;
    var yh = this.yh;
    var yl = this.yl;
    var pInf = this.pInf;
    var nInf = this.nInf;
    var fof = this.fof;
    var pathBegin = false;
    var sign = this.sign;
    this.overFlowObj = {};
    if (eqSy == "lt" || eqSy == "gt" || eqSy == "neq") {
        lalpha = 0;
    }
    if (eqSy != "eq" && eqSy != "neq") {
        fHold.lineWidth = 0.0;
        fHold.strokeStyle = this.hex2rgb(this.graph_color, 0);
        fHold.fillStyle = this.hex2rgb(this.graph_color, 0.15);
        fHold.beginPath();
        this.fill = this.plotPath(nodes.s, nodes.e, fHold);
        pathBegin = true;
        fHold.stroke();
        if (!this.fill) {
            pathBegin = false;
            fHold.fill();
            fHold.closePath();
        }

    } else if (eqSy == "neq") {
        //fHold.lineWidth = 0.0;
        //fHold.strokeStyle = this.hex2rgb(this.graph_color, 0);
        fHold.fillStyle = this.hex2rgb(this.graph_color, 0.15);
        fHold.beginPath();
        fHold.moveTo(xl, yl);
        fHold.lineTo(xh, yl);
        fHold.lineTo(xh, yh);
        fHold.lineTo(xl, yh);
        fHold.lineTo(xl, yl);
        fHold.fill();
        fHold.closePath();
    }
    var pPt;
    if (eqSy != "neq") {
        fHold.lineWidth = 2.0;
        fHold.strokeStyle = this.hex2rgb(this.graph_color, lalpha);
        //fHold.fillStyle=this.hex2rgb(this.graph_color,0.15);
        //alert(this.hex2rgb(this.graph_color, lalpha));
        if (!pathBegin) {
            fHold.beginPath();
        }

        for (var q = 0; q < arr.length; q++) {
            if (q > 0) {
                var p1 = {
                    x: arr[q].x,
                    y: arr[q].y
                };
                var p2 = pPt;

                if (p1.x == pInf || p1.x == nInf || p1.y == pInf || p1.y == nInf || p2.x == pInf || p2.x == nInf || p2.y == pInf || p2.y == nInf) {
                    //fHold.lineStyle(1, 0xff0000, lalpha);
                    fHold.lineWidth = 2.0;
                    fHold.strokeStyle = this.hex2rgb('#ff0000', lalpha);
                    if (eqSy == "eq") {
                        fHold.lineWidth = 2.0;
                        fHold.strokeStyle = this.hex2rgb('#ff0000', 0);
                    }
                } else {
                    var dp = (this.distance(p1, p2));
                    if (fof == "x") {
                        if (dp >= (this.xh) && sign(p1.y) != sign(p2.y)) {
                            fHold.lineWidth = 2.0;
                            fHold.strokeStyle = this.hex2rgb('#ff0000', lalpha);
                            if (eqSy == "eq") {
                                fHold.lineWidth = 2.0;
                                fHold.strokeStyle = this.hex2rgb('#ff0000', 0);
                                fHold.moveTo(arr[q].x, arr[q].y);
                                //console.log('INFI:'+p1.x+":"+p1.y+":"+p2.x+":"+p2.y);
                            }
                        } else {
                            fHold.lineWidth = 2.0;
                            fHold.strokeStyle = this.hex2rgb(this.graph_color, lalpha);
                        }
                    }
                    if (fof == "y") {
                        if (dp >= (this.yh) && this.sign(p1.x) != this.sign(p2.x)) {
                            fHold.lineWidth = 2.0;
                            fHold.strokeStyle = this.hex2rgb(this.graph_color, lalpha);
                            if (eqSy == "eq") {
                                fHold.lineWidth = 2.0;
                                fHold.strokeStyle = this.hex2rgb(this.graph_color, 0);
                                fHold.moveTo(arr[q].x, arr[q].y);
                            }
                        } else {
                            fHold.lineWidth = 2.0;
                            fHold.strokeStyle = this.hex2rgb(this.graph_color, lalpha);
                        }
                    }
                    pPt = {
                        x: arr[q].x,
                        y: arr[q].y
                    };
                }
            } else {
                pPt = {
                    x: arr[q].x,
                    y: arr[q].y
                };
            }

            if (q == 0 && (eqSy == "eq" || !this.fill)) {
                //
                //console.log("FUNCTION_PATH_START:");
                fHold.moveTo(arr[q].x, arr[q].y);
            } else {

                if (eqSy == "eq") {
                    if (arr[q].l == "line") {
                        fHold.lineTo(arr[q].x, arr[q].y);
                    } else if (arr[q].l == "move") {
                        fHold.moveTo(arr[q].x, arr[q].y);
                    }
                } else {
                    //console.log("FUNCTION_PATH_LINE:"+fHold.strokeStyle);
                    fHold.lineTo(arr[q].x, arr[q].y);
                }
            }
        }
        fHold.stroke();
        //fHold.closePath();
    }
    if (eqSy != "eq") {
        //alert(fHold.paths);
        fHold.fill();

    }
    fHold.closePath();

    if (lalpha == 0) {
        fHold.beginPath();
        for (var q = 0; q < arr.length; q++) {
            if (q < arr.length - 1) {
                var p1 = {
                    x: arr[q].x,
                    y: arr[q].y
                };
                var p2 = {
                    x: arr[q + 1].x,
                    y: arr[q + 1].y
                };
                if (p1.x == pInf) {
                    p1.x = xh;
                } else if (p1.x == nInf) {
                    p1.x = xl;
                }
                if (p1.y == pInf) {
                    p1.y = yh;
                } else if (p1.y == nInf) {
                    p1.y = yl;
                }
                if (p2.x == pInf) {
                    p2.x = xh;
                } else if (p2.x == nInf) {
                    p2.x = xl;
                }
                if (p2.y == pInf) {
                    p2.y = yh;
                } else if (p2.y == nInf) {
                    p2.y = yl;
                }
                var dp = (this.distance(p1, p2));
                var sL, gL;
                //fHold.lineStyle(2, graph_color, 100, false);
                fHold.lineWidth = 1;
                fHold.strokeStyle = this.hex2rgb(this.graph_color, 1.0);

                if (dp >= 4) {
                    sL = 1;
                    gL = 3;
                    if (fof == "x") {
                        if (dp >= (this.xh) && this.sign(p1.y) != this.sign(p2.y)) {
                            //fHold.lineStyle(2, 0xff0000, 100);
                            fHold.lineWidth = 1;
                            fHold.strokeStyle = this.hex2rgb('#ff0000', 1.0);
                        }
                    }
                    if (fof == "y") {
                        if (dp >= (this.yh) && this.sign(p1.x) != this.sign(p2.x)) {
                            //fHold.lineStyle(2, 0xff0000, 100);
                            fHold.lineWidth = 1;
                            fHold.strokeStyle = this.hex2rgb('#ff0000', 1.0);
                        }
                    }
                } else {
                    sL = 3;
                    gL = 3;
                }
                sL = 3;
                gL = 3;
                //fHold.dashTo(p1.x, p1.y, p2.x, p2.y, sL, gL, this);
				this.dashTo(p1.x, p1.y, p2.x, p2.y, sL, gL, this,fHold);
                fHold.stroke();
            }
        }
    }
    fHold.closePath();
    return fHold;
}
Plotter.prototype.getPointsFromEqY = function () {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var isNaN = function (n) {
            return Number(n) == Number.NaN;
        }
    this.setFunctionDatas();
    this.setAxisDatas();
    this.graph_color = this.fncol;
    var fn = this.fn;
    var fneql = this.fneql;
    var fncol = this.fncol;
    if (fn == "" || fn == undefined) {
        return null;
    }
    var buffrArr = [];
    var eqnObj = this.eqnObj = this.fn;
    this.eqSy = this.fneql;
    var acc = .1;
    var xscale = this.xscale;
    var yscale = this.yscale;
    var xaxis = this.xaxis;
    var yaxis = this.yaxis;
    var ptsArr = [];
    var xLow = this.xMin;
    var xHi = this.xMax;
    var yLow = this.yMin;
    var yHi = this.yMax;
    var xh = this.xh;
    var xl = this.xl;
    var yh = this.yh;
    var yl = this.yl;
    var y, x0, y0, x1, y1;
    var xPix, yPix, x0Pix, y0Pix, x1Pix, y1Pix, ycomp, dy0, dy1;
    this.doPlot = true;
    var fixTo = this.fixTo;
    acc = (yHi - yLow) / 300;
    acc = this.fixTo(acc, 4);
    //console.log(yLow+":"+yHi+":"+acc)
    for (var y = yLow; y <= yHi; y += acc) {
        y = fixTo(y, 4);
        x = this.evalFor(eqnObj, -y);
        y0 = -(y - acc);
        x0 = this.evalFor(eqnObj, y0);
        y1 = -(y + acc);
        x1 = this.evalFor(eqnObj, y1);

        if (Number(x)) {
            xPix = x * xscale + xaxis;
            yPix = y * yscale + yaxis;
            x0Pix = x0 * xscale + xaxis;
            y0Pix = y0 * yscale + yaxis;
            x1Pix = x1 * xscale + xaxis;
            y1Pix = y1 * yscale + yaxis;
            if (xPix > -2880 && xPix < 2880) {
                xPix = fixTo(xPix, 2);
                yPix = fixTo(yPix, 2);
                ptsArr.push({
                    x: xPix,
                    y: yPix,
                    l: "line"
                });
            } else if (xPix == nInf) {
                if (x0Pix != nInf && x1Pix != nInf && x0Pix != pInf && x1Pix != pInf && !isNaN(x0Pix) && !isNaN(x1Pix)) {
                    dx0 = this.getDval(eqnObj, y0, acc / 10);
                    if (x0Pix < xh && x0Pix > xl) {
                        if (dx0 > x0) {
                            xcomp = xh;
                        } else {
                            xcomp = xl;
                        }
                    } else {
                        xcomp = x0Pix;
                    }
                    ptsArr.push({
                        y: y0Pix,
                        x: xcomp,
                        l: "line"
                    });
                    ptsArr.push({
                        y: yPix,
                        x: xcomp,
                        l: "line"
                    });
                    ptsArr.push({
                        y: yPix,
                        x: -xcomp,
                        l: "move"
                    });
                    dx1 = this.getDval(eqnObj, y1, -acc / 10);

                    if (x1Pix < xh && x1Pix > xl) {
                        if (dx1 > x1) {
                            xcomp = xh;
                        } else {
                            xcomp = xl;
                        }
                    } else {
                        xcomp = x1Pix;
                    }
                    ptsArr.push({
                        y: y1Pix,
                        x: xcomp,
                        l: "move"
                    });
                    //ptsArr.push({x:x1Pix, y:yh, l:"move"});
                } else {
                    ptsArr.push({
                        y: yPix,
                        x: nInf,
                        l: "move"
                    });
                }
                //ptsArr.push({x:xPix, y:nInf, l:"move"});
            } else if (xPix == pInf) {
                if (x0Pix != nInf && x1Pix != nInf && x0Pix != pInf && x1Pix != pInf && !isNaN(x0Pix) && !isNaN(x1Pix)) {
                    dx0 = this.getDval(eqnObj, y0, acc / 10);
                    //trace(y0+":::::"+dy0);
                    if (x0Pix < midX && x0Pix > -midX) {
                        if (dx0 > x0) {
                            xcomp = xh;
                        } else {
                            xcomp = xl;
                        }
                    } else {
                        xcomp = x0Pix;
                    }
                    //trace("P0:"+y0Pix+":"+xcomp+":"+yPix);
                    ptsArr.push({
                        y: y0Pix,
                        x: xcomp,
                        l: "line"
                    });
                    ptsArr.push({
                        y: yPix,
                        x: xcomp,
                        l: "line"
                    });
                    ptsArr.push({
                        y: yPix,
                        x: -xcomp,
                        l: "move"
                    });
                    //ptsArr.push({x:xPix, y:pInf, l:"move"});
                    dx1 = this.getDval(eqnObj, y1, -acc / 10);
                    if (x1Pix < xh && x1Pix > xl) {
                        if (dx1 > x1) {
                            xcomp = xh;
                        } else {
                            xcomp = xl;
                        }
                    } else {
                        xcomp = x1Pix;
                    }
                    ptsArr.push({
                        y: y1Pix,
                        x: xcomp,
                        l: "line"
                    });

                } else {
                    ptsArr.push({
                        y: yPix,
                        x: pInf,
                        l: "move"
                    });
                }

            } else {

                buffrArr.push(x);
            }
        }
        //ptsArr.push({x:x, y:y});
    }
    ptsArr = this.asymFix(ptsArr);
    var n;
    if (ptsArr.length < 2) {
        if (buffrArr.length < 2) {
            if (this.validateFunc()) {
                //alert("Not a valid function!");
            } else {
                alert("Check your input function!");
            }
        } else {
            alert("Cannot plot the given function!");
        }
        return null;
    } else {
        n = this.getNodeDetails(ptsArr);
        return this.drawFunction(n, ptsArr);
    }
}
Plotter.prototype.plotNumberLine = function () {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var gmc = this.context;
    this.setAxisDatas();
    if (this.fand) {
        return this.plotNumberLineAnd();
    }
    var eqn = this.fn;
    var eql = this.fneql;
    if (eqn == "" || eqn == undefined) {
        return null;
    }
    var dx = 4;
    var eqnObj = eqn;
    var eqSy = eql;
    var x0 = this.evalFor(eqnObj, 0);
    var y0 = 0;
    var point;
    point = this.coordToCanvasPoint(x0, y0);
    var pty = eqSy == 'lt' || eqSy == 'gt';
    var fncol = this.fncol ? this.fncol : "RED";
    gmc.lineWidth = 2.0;
    this.drawPoint(point[0], point[1], fncol, dx, pty);
    var xpos = point[0];
    gmc.closePath();

    gmc.lineWidth = 3.0;
    gmc.strokeStyle = fncol;
    gmc.fillStyle = fncol;
    //gmc.beginPath();
    var xs, ys, xe, ye;
    ys = this.yaxis;
    ye = this.yaxis;
    var ad;
    if (eqSy == "eq") {

    } else if (eqSy == "le") {
        xs = xpos - dx;
        xe = 0 + 10;
        ad = 'w';
    } else if (eqSy == "lt") {
        xs = xpos - dx;
        xe = 0 + 10;
        ad = 'w';
    } else if (eqSy == "ge") {
        xs = xpos + dx;
        xe = this.xh - 10;
        ad = 'e';
    } else if (eqSy == "gt") {
        xs = xpos + dx;
        xe = this.xh - 10;
        ad = 'e';
    }
    gmc.beginPath();
    gmc.moveTo(xs, ys);
    gmc.lineTo(xe, ye);
    gmc.stroke();
    gmc.closePath();
    //
    if (ad == 'e') {
        //!- arrow at east end
        gmc.beginPath();
        gmc.moveTo((this.xh), this.yaxis);
        gmc.lineTo((this.xh) - 10, this.yaxis + 4);
        gmc.lineTo((this.xh) - 10, this.yaxis - 4);
        gmc.lineTo((this.xh), this.yaxis);
        gmc.fill();
    } else if (ad == 'w') {
        //!- arrow at west end
        gmc.beginPath();
        gmc.moveTo((0), this.yaxis);
        gmc.lineTo((0) + 10, this.yaxis + 4);
        gmc.lineTo((0) + 10, this.yaxis - 4);
        gmc.lineTo((0), this.yaxis);
        gmc.fill();
    }
    gmc.closePath();
    return gmc;
}

Plotter.prototype.plotNumberLineAnd = function () {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var gmc = this.context;
    this.setAxisDatas();
    this.xscale = this.fixTo(this.xscale, 8);
    var eqDatas = this.fn.split("_");
    var leqn = eqDatas[0];
    var reqn = eqDatas[1];
    if (leqn == "" || leqn == undefined || reqn == "" || reqn == undefined) {
        return null;
    }
    var eqSy = this.eqSy = this.fneql.split("_");
    var leqSy = eqSy[0];
    var reqSy = eqSy[1];
    var x0 = this.evalFor(leqn, 0);
    var y0 = 0;
    var x1 = this.evalFor(reqn, 0);
    var y1 = 0;
    var dx = 4;
    var point0 = this.coordToCanvasPoint(x0, y0);
    var point1 = this.coordToCanvasPoint(x1, y1);
    var pty0 = leqSy == 'lt' || leqSy == 'gt';
    var pty1 = reqSy == 'lt' || reqSy == 'gt';
    var fncol = this.fncol ? this.fncol : "RED";
    gmc.lineWidth = 2.0;
    this.drawPoint(point0[0], point0[1], fncol, dx, pty0);
    this.drawPoint(point1[0], point1[1], fncol, dx, pty1);
    var xpos = point0[0];
    gmc.closePath();
    gmc.lineWidth = 3.0;
    gmc.strokeStyle = fncol;
    gmc.fillStyle = fncol;
    var xs, ys, xe, ye;
    ys = this.yaxis;
    ye = this.yaxis;
    var ad;
    //
    x0 = point0[0];
    x1 = point1[0];
    if (x0 < x1) {
        xs = x0 + dx;
        xe = x1 - dx;
    } else {
        xs = x1 + dx;
        xe = x0 - dx;
    }
    gmc.beginPath();
    gmc.moveTo(xs, ys);
    gmc.lineTo(xe, ye);
    gmc.stroke();
    gmc.closePath();
    return gmc;
};

Plotter.prototype.checkAsym = function (x1, x2) {
    var asYm;
    for (var i = x1 + .01; i < x2; i = this.fixTo(i + .01, 2)) {
        asYm = this.getDval(this.eqnObj, i, 0);
        //console.log("ASYM_CHECK:"+asYm);
        if (Math.abs(asYm) > 10000) {
            return i;
        }
    }
    return null;
}

Plotter.prototype.fixAsym = function (arr, p) {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var xscale = this.xscale;
    var yscale = this.yscale;
    var xaxis = this.xaxis;
    var yaxis = this.yaxis;
    var xh = this.xh;
    var xl = this.xl;
    var yh = this.yh;
    var yl = this.yl;
    var l = arr.length;
    var x0Pix = arr[l - 2].x;
    var y0Pix = arr[l - 2].y;
    var x1Pix = arr[l - 1].x;
    var y1Pix = arr[l - 1].y;
    var x0 = (x0Pix - xaxis) / xscale;
    var y0 = (y0Pix - yaxis) / yscale;
    var x1 = (x1Pix - xaxis) / xscale;
    var y1 = (y1Pix - yaxis) / yscale;
    var asymX = p * xscale + xaxis;
    var asymY = p * yscale + yaxis;
    var dy0, dy1, ycomp, dx0, dx1, xcomp;
    if (fof == "x") {
        dy0 = this.getDval(this.eqnObj, x0, .01);
        if (y0Pix < yh && y0Pix > yl) {
            if (dy0 > y0) {
                ycomp = yh;
            } else {
                ycomp = yl;
            }
        } else {
            ycomp = y0Pix;
        }
        arr.splice(l - 1, 0, {
            x: x0Pix,
            y: ycomp,
            l: "line"
        });
        arr.splice(l, 0, {
            x: asymX,
            y: ycomp,
            l: "line"
        });
        arr.splice(l + 1, 0, {
            x: asymX,
            y: -ycomp,
            l: "line"
        });
        dy1 = this.getDval(this.eqnObj, x1, -.01);
        if (y1Pix < yh && y1Pix > yl) {
            if (dy1 > y1) {
                ycomp = yh;
            } else {
                ycomp = yl;
            }
        } else {
            ycomp = y1Pix;
        }
        arr.splice(l + 2, 0, {
            x: x1Pix,
            y: ycomp,
            l: "move"
        });
    }
    if (fof == "y") {
        dx0 = this.getDval(this.eqnObj, y0, .01);
        if (x0Pix < xh && x0Pix > xl) {
            if (dx0 > x0) {
                xcomp = xh;
            } else {
                xcomp = xl;
            }
        } else {
            xcomp = x0Pix;
        }
        arr.splice(l - 1, 0, {
            y: y0Pix,
            x: xcomp,
            l: "line"
        });
        arr.splice(l, 0, {
            y: asymY,
            x: xcomp,
            l: "line"
        });
        arr.splice(l + 1, 0, {
            y: asymY,
            x: -xcomp,
            l: "line"
        });
        dx1 = this.getDval(this.eqnObj, y1, -.01);
        if (x1Pix < xh && x1Pix > xl) {
            if (dx1 > x1) {
                xcomp = xh;
            } else {
                xcomp = xl;
            }
        } else {
            xcomp = x1Pix;
        }
        arr.splice(l + 2, 0, {
            y: y1Pix,
            x: xcomp,
            l: "move"
        });
    }
}

Plotter.prototype.asymFix = function (arr) {
    var pInf = this.pInf;
    var nInf = this.nInf;
    var xscale = this.xscale;
    var yscale = this.yscale;
    var xaxis = this.xaxis;
    var yaxis = this.yaxis;
    var xh = this.xh;
    var xl = this.xl;
    var yh = this.yh;
    var yl = this.yl;
    var sign = this.sign;
    var aL = arr.length;
    var nArr = [];
    var asymP;
    var fof = this.fof;
    //var c = funcOf == "x" ? "y" : "x";
    for (var a = 0; a < arr.length; a++) {
        var pt = arr[a];
        var axX = (pt.x - xaxis) / xscale;
        var axY = (pt.y - yaxis) / yscale;
        nArr.push(arr[a]);
        if (a != 0) {
            if (fof == "x") {
                //console.log("ASYM_SIGN:"+pPt.y+":"+pt.y);
                if (pPt && (sign(pPt.y) != sign(pt.y))) {
                    asymP = this.checkAsym(axPX, axX);
                    //console.log("ASYM_FIX:"+axX+":"+axPX);
                    if (asymP != null) {
                        this.fixAsym(nArr, asymP);
                    }
                }
            }
            if (fof == "y") {

                if (pPt && (sign(pPt.x) != sign(pt.x))) {
                    asymP = this.checkAsym(axPY, axY);
                    if (asymP != null) {
                        this.fixAsym(nArr, asymP);
                    }
                }
            }
        }
        var pPt = arr[a];
        var axPX = axX;
        var axPY = axY;
    }
    return nArr;
}

Plotter.prototype.hex2rgb = function (col, alp) {
    var r, g, b;
    var colour;
    var tc = this.context.strokeStyle;
    this.context.strokeStyle = col;
    colour = this.context.strokeStyle;
    this.context.strokeStyle = tc;
    if (colour.charAt(0) == '#') {
        colour = colour.substr(1);
    }
    r = colour.charAt(0) + '' + colour.charAt(1);
    g = colour.charAt(2) + '' + colour.charAt(3);
    b = colour.charAt(4) + '' + colour.charAt(5);
    r = parseInt(r, 16);
    g = parseInt(g, 16);
    b = parseInt(b, 16);
    if (alp !== undefined) {
        return "rgba(" + r + "," + g + "," + b + "," + alp + ")";
    } else {
        return "rgb(" + r + "," + g + "," + b + ")";
    }

}
Plotter.prototype.distance = function (p1, p2) {
    var x1 = p1.x;
    var x2 = p2.x;
    var y1 = p1.y;
    var y2 = p2.y;
    var sq = Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2);
    return Math.sqrt(sq);
}
Plotter.prototype.angle = function () {
    var var1 = {};
    var var2 = arguments;
    var var3 = arguments.length;
    var x1, x2, y1, y2;
    var pt = {};
    var dx, dy;
    if (var3 == 2) {
        x1 = var2[0].x;
        x2 = var2[1].x;
        y1 = var2[0].y;
        y2 = var2[1].y;
        dx = x2 - x1;
        dy = y2 - y1;
        var r = Math.sqrt((dx) * (dx) + (dy) * (dy));
        var t = Math.atan2((dy), (dx));
        pt.r = r;
        pt.ra = t;
        pt.a = t * 180 / Math.PI;
    }

    return pt;
}

function delegate(obj, func) {
    return function () {
        return func.apply(obj, arguments);
    };
}
Plotter.prototype.initMouseListeners = function () {
    var canvas = this.icanvas;
    var touchStartFunction = function (event) {
            event.preventDefault();
        }
    var touchMoveFunction = touchStartFunction;
    this.mouse_down = delegate(this, this.ev_onmousedown);
    this.mouse_move = delegate(this, this.ev_onmousemove);
    this.mouse_up = delegate(this, this.ev_onmouseup);
    console.log("ON_initMouseListeners: " + this)
    if (this.document.addEventListener) {
        console.log("ON_initMouseListeners: " + this.mouse_down)
        canvas.addEventListener("mousedown", this.mouse_down, false);
        // canvas.addEventListener("mouseup", this.ev_onmouseup, false);
        // canvas.addEventListener("mousemove", this.ev_onmousemove, false);
        // touchscreen specific - to prevent web page being scrolled while drawing
        canvas.addEventListener('touchstart', touchStartFunction, false);
        canvas.addEventListener('touchmove', touchMoveFunction, false);

        // attach the touchstart, touchmove, touchend event listeners.
        canvas.addEventListener('touchstart', this.mouse_down, false);
        // canvas.addEventListener('touchmove', this.ev_onmousemove, false);
        // canvas.addEventListener('touchend', this.ev_onmouseup, false);
    } else {
        canvas.attachEvent("onmousedown", this.mouse_down);
        // canvas.attachEvent("onmouseup", this.ev_onmouseup);
        // canvas.attachEvent("onmousemove", this.ev_onmousemove);

        // touchscreen specific - to prevent web page being scrolled while drawing
        canvas.attachEvent('touchstart', touchStartFunction);
        canvas.attachEvent('touchmove', touchMoveFunction);

        // attach the touchstart, touchmove, touchend event listeners.
        canvas.attachEvent('touchstart', this.mouse_down);
        // canvas.attachEvent('touchmove', this.ev_onmousemove);
        // canvas.attachEvent('touchend', this.ev_onmouseup);
    }
}
Plotter.prototype.killListeners = function () {
    this.killMouseListeners();
    this.killTouchListeners();
    console.log("LISTENERS_KILLED")
}
Plotter.prototype.killMouseListeners = function () {
    var canvas = this.icanvas;
    if (this.document.addEventListener) {
        canvas.removeEventListener("mousedown", this.mouse_down, false);
        canvas.removeEventListener("mouseup", this.mouse_up, false);
        canvas.removeEventListener("mousemove", this.mouse_move, false);
        //
    } else {
        canvas.detachEvent("onmousedown", this.mouse_down);
        canvas.detachEvent("onmouseup", this.mouse_up);
        canvas.detachEvent("onmousemove", this.mouse_move);
        //
    }
}
Plotter.prototype.killTouchListeners = function () {
    var canvas = this.icanvas;
    if (this.document.addEventListener) {
        //
        canvas.removeEventListener('touchstart', this.mouse_down, false);
        canvas.removeEventListener('touchmove', this.mouse_move, false);
        canvas.removeEventListener('touchend', this.mouse_up, false);
    } else {
        //
        canvas.detachEvent('touchstart', this.mouse_down);
        canvas.detachEvent('touchmove', this.mouse_move);
        canvas.detachEvent('touchend', this.mouse_up);
    }
}

Plotter.prototype.ev_onmousedown = function (_event) {
    var canvas = this.icanvas;
    this.mousePos = 'down';
    this.isTouchEnabled = _event.type.indexOf('touch') > -1;
    console.log("ON_MOUSE_DOWN: " + _event.toString())
    if (this.isTouchEnabled) {
        this.killMouseListeners();
        if (this.document.addEventListener) {
            canvas.addEventListener('touchmove', this.mouse_move, false);
            canvas.addEventListener('touchend', this.mouse_up, false);
        } else {
            canvas.attachEvent('touchmove', this.mouse_move);
            canvas.attachEvent('touchend', this.mouse_up);
        }
    } else {
        if (this.document.addEventListener) {
            canvas.addEventListener("mouseup", this.mouse_up, false);
            canvas.addEventListener("mousemove", this.mouse_move, false);
        } else {
            canvas.attachEvent("onmouseup", this.mouse_up);
            canvas.attachEvent("onmousemove", this.mouse_move);
        }
    }
    this.node_count++;
    //console.log("ON_MOUSE_DOWN: "+this.node_count+":"+this.totalNodes)
    if (this.node_count > this.totalNodes) {
        this.finishRendering = false;
        this.node_count = 1
        this.clear();
    }
    if (this.node_count == this.totalNodes) {
        this.finishRendering = true;
    }
    this.showCursorPos = true;
    this.render(_event);
}
Plotter.prototype.ev_onmouseup = function (_event) {
    console.log("ON_MOUSE_UP: " + this.finishRendering)
    this.mousePos = 'up';
    this.showCursorPos = true;
    var pos = this.render(_event, true);
    var coords = this.canvasPosToCoords(pos[0], pos[1]);
    coords[0] = Math.fixTo(coords[0], 1);
    coords[1] = Math.fixTo(coords[1], 1);
    if (this.finishRendering) {
        var apos = this.plot_data[1][0] + "," + this.plot_data[1][1];
        var ipos = coords[0] + "," + coords[1];
        var boo = apos == ipos;
        if (this.graph_type == 'x') {
            boo = String(this.plot_data[1][0]) == String(coords[0]);
        }
        try {
            if (HmEvents) {
                if (boo) {
                    //HmEvents.eventTutorWidgetComplete.fire(true);
                } else {
                    //HmEvents.eventTutorWidgetComplete.fire(false);
                }
            } else {
                if (boo) {
                    alert("Correct!");
                } else {
                    alert("Incorrect!");
                }
            }
        } catch (error) {
            console.log(error);
            if (boo) {
                console.log("Correct!");
            } else {
                console.log("Incorrect!");
            }
        }

        if (boo) {
            alert("Correct!");
        } else {
            alert("Incorrect!");
        }

        //return
    }
    this.lastNode = pos;
    this.killListeners();
    this.initMouseListeners();
    if (this.zcontext) {
        this.zcontext.clearRect(0, 0, this.zcanvas.width, this.zcanvas.height);
        this.zcanvas.style.top = -60 + "px";
        this.zcanvas.style.left = 0 + "px";
    }
}
Plotter.prototype.ev_onmousemove = function (_event) {
    this.mousePos = 'move';
    this.showCursorPos = true;
    var pos = this.render(_event);
    this.renderZoomCanvas(pos[0], pos[1]);
}
Plotter.prototype.render = function (e, end) {
    var isEnd = end ? end : false;
    var cp = this.getCursorPos(e);
    var pos = [cp.x, cp.y];
    pos[1] = this.numberLine ? this.cpos[1] : pos[1];

    var cw = this.canvas.width;
    var ch = this.canvas.height;
    var render_cntx = isEnd ? this.context : this.icontext;
    this.clear(this.icontext);
    this.point_open = false;
    if (this.plot_data[0] == 'inequality') {
        var eqSy = this.fneql.split("_");
        if (this.fneql == 'lt' || this.fneql == 'gt') {
            this.point_open = true;
        }
        if (eqSy[this.node_count - 1] == 'lt' || eqSy[this.node_count - 1] == 'gt') {
            this.point_open = true;
        }
    }
    var isOpen = this.point_open;
    if (isEnd) {
        this.icontext.clearRect(0, 0, cw, ch);
    }
    if (this.plot_data[0] == 'point' || this.plot_data[0] == 'segment' || this.plot_data[0] == 'line' || this.plot_data[0] == 'polygon' || this.plot_data[0] == 'path' || (this.plot_data[0] == 'inequality' && this.fand)) {
        if (this.plot_data[0] == 'point') {
            this.clear();
        }
        this.drawPoint(pos[0], pos[1], '#ff0000', 4, isOpen, render_cntx)
    }
    if (this.plot_data[0] == 'segment' || this.plot_data[0] == 'line' || this.plot_data[0] == 'polygon' || this.plot_data[0] == 'path' || (this.plot_data[0] == 'inequality' && this.fand)) {
        if (this.lastNode && this.node_count > 1) {
            var x1 = this.lastNode[0];
            var y1 = this.lastNode[1];
            var x2 = pos[0];
            var y2 = pos[1];
            render_cntx.strokeStyle = "#ff0000";
            render_cntx.lineWidth = 2.0;
            render_cntx.beginPath();
            render_cntx.moveTo(x1, y1);
            render_cntx.lineTo(x2, y2);
            render_cntx.stroke();
        }
    }
    if (this.plot_data[0] == 'line') {
        if (this.lastNode && this.node_count > 1) {
            var dm = (y2 - y1) / (x2 - x1);
            var da2 = Math.atan(dm);
            var da1 = Math.PI - da2;
            da2 = Math.PI + da1
            var r = Math.sqrt((cw * cw) + (ch * ch))
            var nx1 = x1 + r * Math.cos(da1);
            var ny1 = y1 - r * Math.sin(da1);
            var nx2 = x2 + r * Math.cos(da2);
            var ny2 = y2 - r * Math.sin(da2);
            render_cntx.lineStyle = "#ff0000";
            render_cntx.lineWidth = 2.0;
            render_cntx.beginPath();
            render_cntx.moveTo(nx1, ny1);
            render_cntx.lineTo(x1, y1);
            render_cntx.moveTo(x2, y2);
            render_cntx.lineTo(nx2, ny2);
            render_cntx.stroke();
        }
    }
    var coords = this.canvasPosToCoords(pos[0], pos[1]);
    coords[0] = Math.fixTo(coords[0], 1);
    coords[1] = Math.fixTo(coords[1], 1);
    if (this.iplot_type == 'inequality') {
        if (this.totalNodes == 1) {
            this.fn = String(coords[0]);
            this.clear();
            this.plotNumberLine();
        }
    }
    if (this.showCursorPos) {

        var ipos = this.graph_type == 'x' ? String(coords[0]) : "(" + coords[0] + ", " + coords[1] + ")";

        //this.drawRect(xp, yp, 50, 25, true, '#000000', true, this.hex2rgb('#999999',0.5));
        render_cntx.textBaseline = 'bottom';
        render_cntx.font = "bold 12px sans-serif";
        render_cntx.fillStyle = "BLACK";
        var tw = render_cntx.measureText(ipos).width;
        var xp = pos[0] - 25;
        var yp = pos[1] - 2;

        //console.log(cw+":"+tw+":"+xp)
        if (xp + tw > cw) {
            var dx = xp + tw - cw
            xp = xp - dx
        }
        if (xp < 0) {
            var dx = xp
            xp = xp - dx
        }
        if (yp < 0) {
            var dy = yp
            yp = yp - dy
        }
        if (this.graph_type == 'x') {
            xp = pos[0] - (tw / 2);
        }
        if (this.iplot_type == 'inequality' && this.mousePos == 'up') {
            return pos
        }
        render_cntx.fillText(ipos, xp, yp);
        //console.log(this.plot_type+" ---> "+ipos);
    }

    return pos;
}


/**
 * End of Plotter Class
 */
Plotter.prototype.dashTo = function (sx, sy, ex, ey, lW, gW, scope,ctx) {
    if (arguments.length < 6) {
        return;
    }
    var startPt = {
        x: sx,
        y: sy
    };
    var endPt = {
        x: ex,
        y: ey
    };
    var segLength = scope.distance(startPt, endPt);
    segLength = scope.fixTo(segLength, 2);
    var segAngle = scope.angle(startPt, endPt).ra;
    var dashLength = lW + gW;
    var oLen = 0;
    ctx.moveTo(sx, sy);
    var xp, yp, nseg, overFlow, deltax, deltay, __mx, __my, lx, ly;
    var buffer = false;
    xp = sx;
    yp = sy;
    //console.log(scope.overFlowObj.boo+":"+scope.overFlowObj.type+":"+scope.overFlowObj.len)
    if (scope.overFlowObj.boo) {
        oLen = scope.overFlowObj.len;
        buffer = (oLen - segLength) >= 0;
        if (buffer) {
            segL = segLength;
        } else {
            segL = scope.overFlowObj.len;
        }
        xp = sx + segL * Math.cos(segAngle);
        yp = sy + segL * Math.sin(segAngle);
        xp = scope.fixTo(xp, 2);
        yp = scope.fixTo(yp, 2);
        if (scope.overFlowObj.type == "gap") {
            ctx.moveTo(xp, yp);
            if (buffer) {
                scope.overFlowObj.boo = true;
                scope.overFlowObj.type = "gap";
                scope.overFlowObj.len = oLen - segLength;
                if (scope.overFlowObj.len < 0) {
                    scope.overFlowObj.boo = !true;
                    scope.overFlowObj.len = 0;
                }
                return;
            }
        } else {
            ctx.lineTo(xp, yp);
            if (buffer) {
                scope.overFlowObj.boo = true;
                scope.overFlowObj.type = "line";
                scope.overFlowObj.len = oLen - segLength;
                if (scope.overFlowObj.len < 0) {
                    scope.overFlowObj.boo = !true;
                    scope.overFlowObj.len = 0;
                }
                return;
            } else {
                segL = segLength - segL
                if (segL > gW) {
                    oLen += gW;
                    xp = xp + gW * Math.cos(segAngle);
                    yp = yp + gW * Math.sin(segAngle);
                    xp = scope.fixTo(xp, 2);
                    yp = scope.fixTo(yp, 2);
                    ctx.moveTo(xp, yp);
                    scope.overFlowObj.type = "gap";
                } else {
                    scope.overFlowObj.boo = true;
                    scope.overFlowObj.type = "gap";
                    scope.overFlowObj.len = gW - segL
                    if (scope.overFlowObj.len <= 0) {
                        scope.overFlowObj.boo = !true;
                        scope.overFlowObj.len = 0;
                    }
                    return;
                }
            }
        }
    }
    segLength = scope.distance({
        x: xp,
        y: yp
    }, endPt);
    segLength = scope.fixTo(segLength, 2);
    nseg = (segLength) / dashLength;
    overFlow = Math.abs(nseg - parseInt(nseg)) * dashLength;
    nseg = Math.floor(nseg);
    deltax = Math.cos(segAngle) * dashLength;
    deltay = Math.sin(segAngle) * dashLength;
    deltax = scope.fixTo(deltax, 2);
    deltay = scope.fixTo(deltay, 2);
    __mx = xp;
    __my = yp;
    for (var n = 0; n < nseg; n++) {
        ctx.moveTo(__mx, __my);
        lx = __mx + Math.cos(segAngle) * lW;
        ly = __my + Math.sin(segAngle) * lW;
        lx = scope.fixTo(lx, 2);
        ly = scope.fixTo(ly, 2);
        ctx.lineTo(lx, ly);
        __mx += deltax;
        __my += deltay;
        remLength = scope.distance({
            x: __mx,
            y: __my
        }, endPt);
        scope.overFlowObj.boo = true;
        scope.overFlowObj.type = "gap";
    }
    if (overFlow >= 0.1) {
        if (!scope.overFlowObj.boo) {
            scope.overFlowObj.type = "gap";
        }
        scope.overFlowObj.boo = true;
        if (scope.overFlowObj.type == "gap") {
            scope.overFlowObj.type = overFlow >= lW ? "gap" : "line";
            scope.overFlowObj.len = scope.overFlowObj.type == "gap" ? gW - (overFlow - lW) : (lW - overFlow);
            if (scope.overFlowObj.type == "line") {
                ctx.moveTo(__mx, __my);
                lx = __mx + Math.cos(segAngle) * overFlow;
                ly = __my + Math.sin(segAngle) * overFlow;
                lx = scope.fixTo(lx, 2);
                ly = scope.fixTo(ly, 2);
                ctx.lineTo(__mx + Math.cos(segAngle) * overFlow, __my + Math.sin(segAngle) * overFlow);
            } else {
                ctx.moveTo(__mx, __my);
                lx = __mx + Math.cos(segAngle) * lW;
                ly = __my + Math.sin(segAngle) * lW;
                lx = scope.fixTo(lx, 2);
                ly = scope.fixTo(ly, 2);
                ctx.lineTo(__mx + Math.cos(segAngle) * lW, __my + Math.sin(segAngle) * lW);
            }
        } else if (scope.overFlowObj.type == "line") {
            scope.overFlowObj.type = overFlow >= gW ? "line" : "gap";
            scope.overFlowObj.len = scope.overFlowObj.type == "line" ? lW - (overFlow - gW) : (gW - overFlow);
            if (scope.overFlowObj.type == "line") {
                lx = __mx + Math.cos(segAngle) * overFlow;
                ly = __my + Math.sin(segAngle) * overFlow;
                lx1 = __mx + Math.cos(segAngle) * gW;
                ly1 = __my + Math.sin(segAngle) * gW;
                lx = scope.fixTo(lx, 2);
                ly = scope.fixTo(ly, 2);
                lx1 = scope.fixTo(lx1, 2);
                ly1 = scope.fixTo(ly1, 2);
                ctx.moveTo(lx1, ly1);
                ctx.lineTo(lx, ly);
            } else {
                ctx.moveTo(__mx, __my);
                lx1 = __mx + Math.cos(segAngle) * overFlow;
                ly1 = __my + Math.sin(segAngle) * overFlow;
                lx1 = scope.fixTo(lx1, 2);
                ly1 = scope.fixTo(ly1, 2);
            }
        }
    } else {
        scope.overFlowObj.boo = !true;
    }
};


/**
 * @author sathesh
 * formats input math equation string, so that it can be evaluated by JS
 */
var EqParser = (function () {
    var parser = {};
    parser.init = function () {
        console.log("EqParser --> PARSER_INITED!");
        this.operators = ["^", "*", "/", "+", "-"];
        this.a_arr = ["exp", "sinh", "cosh", "tanh", "csch", "sech", "coth", "arccsc", "arcsec", "arccot", "log", "abs", "e", "arcsin", "arccos", "arctan", "sin", "cos", "tan", "sqrt", "pi", "csc", "sec", "cot"];
        this.b_arr = ["S", "d", "e", "?", "?", "?", "?", "?", "�", "?", "!", "@", "#", "$", "'", "_", "&", "|", "?", ";", "~", "a", "�", "?"];
        this.ioperators = ["^", "*", "/", "+", "-", ","];
        this.m_fn = ["exp", "sinh", "cosh", "tanh", "csch", "sech", "coth", "arccsc", "arcsec", "arccot", "log", "abs", "arcsin", "arccos", "arctan", "sin", "cos", "tan", "sqrt", "pow", "csc", "sec", "cot"];
    }
    parser.convertTo_JSEq = function (str) {
        this.input_str = str;
        this.input_str = this.formatMulti();
        this.input_str = this.formatPow();
        return this.input_str;
    }
    parser.findandreplace = function (input, search, replace) {
        return input.split(search).join(replace);
    }
    parser.fandr = function (input, search, replace, ipos) {
        var my = input;
        var pos = my.indexOf(search, ipos);
        for (var i = ipos; i < my.length; i++) {
            if (pos == -1 || search == replace) {
                break;
            }
            var startstr = my.substr(0, pos);
            var restr = replace;
            var endstr = my.substr(pos + search.length);
            my = startstr + restr + endstr;
            break;
        }

        return my;
    }
    parser.formatMulti = function () {
        var my_str = this.input_str;
        my_str = my_str.split(" ").join("");
        var npos;
        var a_arr = this.a_arr;
        var b_arr = this.b_arr;
        for (var j = 0; j < a_arr.length; j++) {
            npos = my_str.indexOf(a_arr[j], 0);
            if (npos != -1) {
                my_str = this.findandreplace(my_str, a_arr[j], b_arr[j]);
            }
        }
        var brac = my_str.indexOf(")(", 0);
        if (brac != -1) {
            my_str = this.findandreplace(my_str, ")(", ")*(");
        }
        for (var i = 0; i < my_str.length; i++) {
            for (var j = 97; j <= 122; j++) {
                if (my_str.charCodeAt(i) == j) {
                    for (var k = 97; k <= 122; k++) {
                        if (my_str.charCodeAt(i - 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                        }
                    }
                    for (var k = 97; k <= 122; k++) {
                        if (my_str.charCodeAt(i + 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                        }
                    }
                    for (var k = 48; k <= 57; k++) {
                        if (my_str.charCodeAt(i - 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                        }
                    }
                    for (var k = 48; k <= 57; k++) {
                        if (my_str.charCodeAt(i + 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                        }
                    }
                    if (my_str.charAt(i - 1) == ')') {
                        my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                    }
                    if (my_str.charAt(i + 1) == '(') {
                        my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                    }
                }
            }
        }
        for (var i = 0; i < my_str.length; i++) {
            for (var j = 0; j < b_arr.length; j++) {
                if (my_str.charAt(i) == b_arr[j]) {
                    for (var k = 97; k <= 122; k++) {
                        if (my_str.charCodeAt(i - 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                        }
                    }
                    for (var k = 97; k <= 122; k++) {
                        if (my_str.charCodeAt(i + 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                        }
                    }
                    for (var k = 48; k <= 57; k++) {
                        if (my_str.charCodeAt(i - 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                        }
                    }
                    for (var k = 48; k <= 57; k++) {
                        if (my_str.charCodeAt(i + 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                        }
                    }
                    if (my_str.charAt(i) == "~") {
                        if (my_str.charAt(i - 1) == ')') {
                            my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                        }
                        if (my_str.charAt(i + 1) == '(') {
                            my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                        }
                        for (var k = 0; k < b_arr.length; k++) {
                            if (my_str.charAt(i + 1) == b_arr[k]) {
                                my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                            }
                            if (my_str.charAt(i - 1) == b_arr[k]) {
                                my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                            }
                        }
                    }
                    if (my_str.charAt(i) == ";") {
                        if (my_str.charAt(i - 1) == ')') {
                            my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                        }
                    }
                }
                if (my_str.charAt(i) == ")") {
                    for (var k = 0; k < b_arr.length; k++) {
                        if (my_str.charAt(i + 1) == b_arr[k]) {
                            my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                        }
                    }
                }
            }
        }
        for (var i = 0; i < my_str.length; i++) {
            for (var j = 48; j <= 57; j++) {
                if (my_str.charCodeAt(i) == j) {
                    for (var k = 97; k <= 122; k++) {
                        if (my_str.charCodeAt(i - 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                        }
                    }
                    for (var k = 97; k <= 122; k++) {
                        if (my_str.charCodeAt(i + 1) == k) {
                            my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                        }
                    }
                    if (my_str.charAt(i - 1) == ')') {
                        my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
                    }
                    if (my_str.charAt(i + 1) == '(') {
                        my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
                    }
                }
            }
        }
        for (var j = 0; j < b_arr.length; j++) {
            npos = my_str.indexOf(b_arr[j], 0);
            if (npos != -1) {
                my_str = this.findandreplace(my_str, b_arr[j], a_arr[j]);
            }
        }
        return my_str;
    }
    parser.formatPow = function () {
        var equ = this.input_str;
        var ioperators = this.ioperators;
        var m_fn = this.m_fn;
        var rcount = 0;
        var lcount = 0;
        var rrcount = 0;
        var rlcount = 0;
        var pow_pos = equ.indexOf("^", 0);
        var neg, lt_str, rt_str, base, lbase, mflag, mlen, check_cp;
        for (i = 0; i < equ.length; i++) {
            if (pow_pos == -1) {
                break;
            }
            lt_str = equ.substr(0, pow_pos);
            rt_str = equ.substr(pow_pos + 1);
            if (rt_str.charAt(0) == "-") {
                rt_str = rt_str.substr(1, rt_str.length);
                neg = true;
            }
            if (lt_str.indexOf(")", lt_str.length - 1) != lt_str.length - 1) {
                for (var ij = lt_str.length - 1; ij >= 0; ij--) {
                    for (var j = 0; j < ioperators.length; j++) {
                        if (lt_str.charAt(ij) == ioperators[j]) {
                            base = lt_str.substr(ij + 1, lt_str.length);
                            lbase = lt_str.indexOf(0, ij);
                            if (lt_str.indexOf("(", ij) != -1) {
                                base = findandreplace(base, "(", "");
                            }
                            ij = -1;
                            break;
                        } else {
                            if (lt_str.indexOf("(", ij) != -1) {
                                base = lt_str.substr(ij + 1, lt_str.length);
                                ij = -1;
                                break;
                            }
                            if (ij == 0) {
                                base = lt_str;
                            }
                        }
                    }
                }
            } else {
                mflag = false;
                for (var ii = lt_str.length - 1; ii >= 0; ii--) {
                    if (lt_str.charAt(ii) == ")") {
                        rcount++;
                    }
                    if (lt_str.charAt(ii) == "(") {
                        mlen = ii;
                        for (var j = 0; j < m_fn.length; j++) {
                            if (lt_str.substr(ii - m_fn[j].length, m_fn[j].length) == m_fn[j]) {
                                mflag = true;
                                mlen = ii - m_fn[j].length;
                                break;
                            }
                        }
                        lcount++;
                    }
                    if (lcount == rcount) {
                        check_cp = lt_str.charAt(ii - 1);
                        if (ii != 0 && check_cp != "(") {
                            for (var iii = ii; iii >= 0; iii--) {
                                for (var j = 0; j < ioperators.length; j++) {
                                    if (lt_str.charAt(iii) == ioperators[j]) {
                                        base = lt_str.substr(iii + 1, lt_str.length);
                                        lbase = lt_str.substr(0, lt_str_pos - 1);
                                        iii = -1;
                                        ii = -1;
                                        break;
                                    }
                                }
                            }
                            if (mflag == true) {
                                ii = mlen;
                            }
                            for (var iii = ii; iii >= 0; iii--) {
                                for (var j = 0; j < m_fn.length; j++) {
                                    if (lt_str.indexOf(m_fn[j], iii) != -1) {
                                        base = lt_str.substr(iii, lt_str.length);
                                        iii = -1;
                                        ii = -1;
                                        break;
                                    }
                                }
                            }
                        } else {
                            base = lt_str.substr(ii, lt_str.length);
                            break;
                        }
                    }
                }
            }
            var pdl = "";
            var pdr, power, rpower;
            for (var j = 0; j < m_fn.length; j++) {
                if (rt_str.indexOf(m_fn[j], 0) == 0) {
                    pdl = rt_str.substr(0, m_fn[j].length);
                    pdr = rt_str.substr(m_fn[j].length, rt_str.length);
                    rt_str = pdr;
                }
            }
            if (rt_str.indexOf("(", 0) != 0) {
                for (var i = 0; i < rt_str.length; i++) {
                    for (var j = 0; j < ioperators.length; j++) {
                        if (rt_str.charAt(i) == ioperators[j]) {
                            power = rt_str.substr(0, i);
                            rpower = rt_str.substr(i + 1, rt_str.length - 1);
                            i = rt_str.length;
                            break;
                        }
                        if (i == rt_str.length - 1) {
                            power = rt_str;
                        }
                    }
                }
                if (power.indexOf(")", 0) != -1) {
                    // power = power.substr(0, power.indexOf(")"));
                }
            } else {
                for (var ii = 0; ii < rt_str.length; ii++) {
                    if (rt_str.charAt(ii) == "(") {
                        rlcount++;
                    }
                    if (rt_str.charAt(ii) == ")") {
                        rrcount++;
                    }
                    if ((rlcount == rrcount) && (rrcount != 0)) {
                        check_p = rt_str.charAt(ii + 1);
                        if (ii != (rt_str.length - 1) && check_p != ")") {
                            for (var iii = ii; iii < rt_str.length; iii++) {
                                for (var j = 0; j < ioperators.length; j++) {
                                    if (rt_str.charAt(iii) == ioperators[j]) {
                                        power = (rt_str.substr(0, iii));
                                        // rpower = rt_str.substr(iii+1, rt_str.length-1);
                                        iii = rt_str.length;
                                        ii = rt_str.length;
                                        break;
                                    }
                                }
                            }
                        } else {
                            power = rt_str.substr(0, ii + 1);
                            break;
                        }
                    }
                }
            }
            power = pdl + power;
            if (neg == true) {
                power = "-" + power;
            }
            var find = base + "^" + power;
            var replace = "pow(" + base + "," + power + ")";
            equ = this.fandr(equ, find, replace, 0);
            pow_pos = equ.indexOf("^", pow_pos);
        }
        return equ;
    }

    parser.evalEq = function (str, val) {
        var floor = Math.floor;
        var ceil = Math.ceil;
        var round = Math.round;
        var Pi = Math.PI;
        var PI = Math.PI;
        var pI = Math.PI;
        var pi = Math.PI;
        var E = Math.E;
        var e = Math.E;
        var x = val;
        var y = val;
        var inequ = str;
        //


        function findandreplace(inputVal, searchVal, replaceVal) {
            var mystr = inputVal.split(searchVal).join(replaceVal);
            return mystr;
        }

        //------------------------------------------------------------------------//
        //trig


        function sin(x) {
            return Math.sin(x);
        }

        function cos(x) {
            return Math.cos(x);
        }

        function tan(x) {
            return Math.tan(x);
        }

        function sec(x) {
            return 1 / Math.cos(x);
        }

        function csc(x) {
            return 1 / Math.sin(x);
        }

        function cot(x) {
            return 1 / Math.tan(x);
        }

        //inverse


        function arcsin(x) {
            return Math.asin(x);
        }

        function arccos(x) {
            return Math.acos(x);
        }

        function arctan(x) {
            return Math.atan(x);
        }

        function arccsc(x) {
            return Math.asin(1 / x);
        }

        function arcsec(x) {
            return Math.acos(1 / x);
        }

        function arccot(x) {
            return (pi / 2) - Math.atan(x);
        }

        //hyperbolic


        function sinh(x) {
            return (exp(x) - exp(-x)) / 2;
        }

        function cosh(x) {
            return (exp(x) + exp(-x)) / 2;
        }

        function tanh(x) {
            return (sinh(x) / cosh(x));
        }

        function csch(x) {
            return 1 / sinh(x);
        }

        function sech(x) {
            return 1 / cosh(x);
        }

        function coth(x) {
            return 1 / tanh(x);
        }

        function pow(ba, p) {
            var pchek = Math.pow(ba, p);
            var estr, pstr, psub, psup;

            if (isFinite(pchek)) {
                return pchek;
            } else {
                estr = inequ;
                estr = estr.split('^');
                pstr = estr[1].split('/');
                psub = parseInt(pstr[1]);
                psup = findandreplace(pstr[0], '(', "");

                if (psub % 2 == 1 && psup % 2 == 1) {
                    if (ba < 0) {
                        ba = -ba;
                        pchek = -Math.pow(ba, p);
                    } else {
                        pchek = Number.NaN;
                    }
                } else if (psub % 2 == 1 && psup % 2 == 0) {
                    ba = -ba;
                    pchek = Math.pow(ba, p);
                } else {
                    pchek = Number.NaN;
                }
                return pchek;
            }
        }

        function log(x, base) {
            if (base == "" || base == undefined) {
                base = 10;
            }
            return (Math.log(x) / Math.log(base));
        }

        function sqrt(x) {
            return Math.sqrt(x);
        }

        function abs(x) {
            return Math.abs(x);
        }

        function exp(x) {
            return Math.pow(e, x);
        }


        this.convertTo_JSEq(str);
        //alert(this.input_str)
        return eval(this.input_str);
    }
    return parser;
}());


Math.fixTo = function (n, pl, round) {
    if (Number(n) == Number.NaN) {
        return n;
    }
    if (round == undefined) {
        round = true;
    }
    var fixed;
    fixed = Math.pow(10, pl) * n;
    if (round) {
        fixed = Math.round(fixed);
    } else {
        fixed = Math.floor(fixed);
    }
    fixed = fixed / Math.pow(10, pl);
    return fixed;
};/**
 * @author sathesh
 */
if(typeof console === "undefined") {
    console = {log:function(x) {
        // empty
    }};
}
var Graph = function(doc, canvas_cont, graph_type, xmin, xmax, ymin, ymax, xinc, yinc,interact, show_axis, show_axis_label, show_grid, show_half_grid, width, height) {
	//console.log(doc + ":" + canvas_cont + ":" + graph_type + ":" + xmin + ":" + xmax + ":" + ymin + ":" + ymax + ":" + xinc + ":" + yinc)
	this._imageBaseDir = '/js/images/dynamic_widgets/';
	this.document = doc;
	this.board = canvas_cont;
	this.graph_type = graph_type ? graph_type : 'xy';
	this.width = width ? width : 300;
	this.height = height ? height : (this.graph_type == 'xy' ? 300 : 150);
	this.xmin = xmin ? xmin * 1 : -5;
	this.xmax = xmax ? xmax * 1 : 5;
	this.ymin = ymin ? ymin * 1 : -5;
	this.ymax = ymax ? ymax * 1 : 5;
	this.xinc = xinc ? (xinc == 'pi' ? 1 : xinc * 1) : 1;
	this.yinc = yinc ? (yinc == 'pi' ? 1 : yinc * 1) : 1;
	this.inter_obj=interact;
	this.show_axis = show_axis ? show_axis : true;
	this.show_axis_label = show_axis_label ? show_axis_label : true;
	this.show_grid = show_grid ? show_grid : true;
	this.show_half_grid = show_half_grid ? show_half_grid : false;
	this.scaleUnits = [1, 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000];
	//
	this.scaleX = this.width / ((Math.abs((this.xmax + this.xinc) - (this.xmin - this.xinc))) / this.xinc);
	this.scaleY = this.height / ((Math.abs((this.ymax + this.yinc) - (this.ymin - this.yinc))) / this.yinc);
	this.paxisYpos=this.axisYpos = (this.width) - (((this.xmax + this.xinc) / this.xinc) * (this.scaleX));
	this.paxisXpos=this.axisXpos = (this.height) - (((this.ymax + this.yinc) / this.yinc) * (this.scaleY));
	this.xPscale=this.scaleX;
	this.yPscale=this.scaleY;
	
	//
	this.canvas = this.document.createElement("canvas");
	this.canvas.width = this.width;
	this.canvas.height = this.height;
	this.board.appendChild(this.canvas);	
	this.context = this.canvas.getContext('2d');
	try{
	if (typeof G_vmlCanvasManager != "undefined") {
		G_vmlCanvasManager.initElement(this.canvas);
		//console.log("DEBUG_IE: G_vmlCanvasManager available");
	}else{
	console.log("DEBUG_IE: G_vmlCanvasManager not available");
	}
	}catch(error){
	console.log("DEBUG_IE:"+error);
	}
	this.canvas.style.position='absolute';
	this.canvas.style.top=0+'px';
	this.canvas.style.left=0+'px';
	this.plotObj=null;	
	//
	if(this.inter_obj){
	this.addGraphUI()
	}
	this.getRatio();
	this.drawGraph();
}
Graph.prototype.resetGraph=function(){
console.log("RESET_CALLED "+this)
this.scaleX=this.xPscale;
this.scaleY=this.yPscale;
this.axisYpos=this.paxisYpos;
this.axisXpos=this.paxisXpos;
this.getRatio();
this.drawGraph();
}
Graph.prototype.addGraphUI=function(){
	this.gtool_div = this.document.createElement("div");
	this.gtool_div.width = this.width;
	this.board.appendChild(this.gtool_div );
	this.gtool_div.style.position='absolute';
	this.gtool_div.style.top=1+'px';
	this.gtool_div.style.left=1+'px';
	var this_scope=this
	//
	///*
	var reset_btn=this.createGTool("RESET");
	var reset=function(){
	this_scope.clearGraph();
	this_scope.resetGraph();
	console.log("RESET: "+this)
	this_scope.zmpt.style.top = this_scope.axisXpos+'px';
	this_scope.zmpt.style.left = this_scope.axisYpos-(this_scope.zmpt.width/2)+'px';
	this_scope.zmpt_pos={px:this_scope.axisYpos,py:this_scope.axisXpos,sx:this_scope.axisYpos,sy:this_scope.axisXpos,cx:this_scope.axisYpos,cy:this_scope.axisXpos,nx:this_scope.axisYpos,ny:this_scope.axisXpos,dx:0,dy:0};
	
	}
	reset_btn.onmousedown=function(){reset()};
	this.gtool_div.appendChild(reset_btn);
	//
	var aL=this.createGTool("\u25c4\u25AC");
	var aR=this.createGTool("\u25AC\u25BA");
	
	aL.onmousedown=function(){this_scope.scrollLeft()};
	aR.onmousedown=function(){this_scope.scrollRight()};
	this.gtool_div.appendChild(aL);
	this.gtool_div.appendChild(aR);
	//
	if(this.inter_obj&&this.inter_obj.zoom){
	var zIn=this.createGTool("ZmIn");
	var zOut=this.createGTool("ZmOut");
	zIn.onmousedown=function(){this_scope.zoomIn()};
	zOut.onmousedown=function(){this_scope.zoomOut()};
	this.gtool_div.appendChild(zIn);
	this.gtool_div.appendChild(zOut);
	this.addZoomPointer();
	}
	
	this.init_graph_transformListeners();
	//*/
}
Graph.prototype.addZoomPointer=function(){
	//var zmpt=this.document.getElementById('zmptr');
	
	if(this.zmpt){
		//this.clearCanvas(this.zmpt_cntx);
//this.renderZoomPinter();		
	}else{
	this.zmtool_div = this.document.createElement("div");
	this.zmtool_div.width = this.width;
	this.zmtool_div.height = this.height;
	this.board.appendChild(this.zmtool_div );
	this.zmtool_div.style.position='absolute';
	this.zmtool_div.style.top=0+'px';
	this.zmtool_div.style.left=0+'px';
		this.zmpt= this.document.createElement('canvas');
		
		this.zmpt.setAttribute('id',"cbtn_zmptr");
		this.zmpt.width=20;
	this.zmpt.height=35;
	this.zmtool_div.appendChild(this.zmpt);
	var zmpt=this.zmpt;
	console.log("ZMPOINT:"+zmpt+":"+zmpt.width+":"+zmpt.height)
	zmpt.style.position = 'absolute';
	zmpt.style.top = this.axisXpos+'px';
	zmpt.style.left = this.axisYpos-(this.zmpt.width/2)+'px';
	
		
		this.zmpt_cntx=zmpt.getContext('2d');
		this.renderZoomPointer();
		this.zmpt_pos={px:this.axisYpos,py:this.axisXpos,sx:this.axisYpos,sy:this.axisXpos,cx:this.axisYpos,cy:this.axisXpos,nx:this.axisYpos,ny:this.axisXpos,dx:0,dy:0};
		var this_scope=this;
		var touchStartFunction=function(event) {
    event.preventDefault();
	}
	var touchMoveFunction = touchStartFunction;
	if (this.document.addEventListener) {
	 console.log("ON_initMouseListeners: "+this.mouse_down)
       

        // touchscreen specific - to prevent web page being scrolled while drawing
        this.zmpt.addEventListener('touchstart', touchStartFunction, false);
         this.zmpt.addEventListener('touchmove', touchMoveFunction, false);

        // attach the touchstart, touchmove, touchend event listeners.
        //canvas.addEventListener('touchstart', this.mouse_down, false);
       
    } else {
       // canvas.attachEvent("onmousedown", this.mouse_down);
       // canvas.attachEvent("onmouseup", this.ev_onmouseup);
       // canvas.attachEvent("onmousemove", this.ev_onmousemove);


        // touchscreen specific - to prevent web page being scrolled while drawing
         this.zmpt.attachEvent('touchstart', touchStartFunction);
         this.zmpt.attachEvent('touchmove', touchMoveFunction);

        // attach the touchstart, touchmove, touchend event listeners.
        //canvas.attachEvent('touchstart', this.mouse_down);
       // canvas.attachEvent('touchmove', this.ev_onmousemove);
       // canvas.attachEvent('touchend', this.ev_onmouseup);
    }
		zmpt.ontouchstart=zmpt.onmousedown=function(e){
		var cpos=this_scope.plotObj.getCursorPos(e)
		this_scope.zmpt_pos.sx=cpos.x;
		this_scope.zmpt_pos.sy=cpos.y;
		console.log("ZM_PT_ON_MOUSE_DOWN:"+cpos.x+":"+cpos.y)
		zmpt.ontouchmove=this.onmousemove=this_scope.zmtool_div.onmousemove=function(e){
		var cpos=this_scope.plotObj.getCursorPos(e)
		this_scope.zmpt_pos.cx=cpos.x;
		this_scope.zmpt_pos.cy=cpos.y;
		
		this_scope.zmpt_pos.dx=this_scope.zmpt_pos.cx-this_scope.zmpt_pos.sx;
		this_scope.zmpt_pos.dy=this_scope.zmpt_pos.cy-this_scope.zmpt_pos.sy;
		this_scope.zmpt_pos.nx=this_scope.zmpt_pos.sx+this_scope.zmpt_pos.dx;
		this_scope.zmpt_pos.ny=this_scope.zmpt_pos.sy+this_scope.zmpt_pos.dy;
		setZPointerPos()
		}
		zmpt.ontouchend=this.onmouseup=this_scope.zmtool_div.onmouseup=function(e){
		var cpos=this_scope.plotObj.getCursorPos(e)
		this_scope.zmpt_pos.cx=cpos.x;
		this_scope.zmpt_pos.cy=cpos.y;		
		this_scope.zmpt_pos.dx=this_scope.zmpt_pos.cx-this_scope.zmpt_pos.sx;
		this_scope.zmpt_pos.dy=this_scope.zmpt_pos.cy-this_scope.zmpt_pos.sy;
		this_scope.zmpt_pos.nx=this_scope.zmpt_pos.sx+this_scope.zmpt_pos.dx;
		this_scope.zmpt_pos.ny=this_scope.zmpt_pos.sy+this_scope.zmpt_pos.dy;
		setZPointerPos()
		zmpt.ontouchmove=this_scope.onmousemove=this.onmousemove=null
		zmpt.ontouchend=this_scope.onmouseup=this.onmouseup=null
		}
		}
		
		
		function setZPointerPos(){
			var pos=this_scope.zmpt_pos;
			this_scope.zmpt.style.top=this_scope.graph_type=='x'?(pos.py)+'px':pos.ny+'px';
			this_scope.zmpt.style.left=(pos.nx-this_scope.zmpt.width/2)+'px';
			//console.log("ZM_PT_ON_POS:"+pos.nx+":"+pos.ny+":"+this_scope.zmpt.style.top+":"+this_scope.zmpt.style.left)
		}
	}
}
Graph.prototype.renderZoomPointer=function(){
var xs=10;
var ys=0;
var x1=xs-3;
var y1=ys+7.5;
var x2=xs+3;
var y2=y1;
var x3=xs;
var y3=y1;
var x4=xs;
var y4=y1+7.5;
var x5=xs;
var y5=y4+10;
this.zmpt_cntx.strokeStyle="RED";
this.zmpt_cntx.fillStyle="RED";
this.zmpt_cntx.lineWidth=1.0;
this.zmpt_cntx.beginPath();
this.zmpt_cntx.moveTo(xs,ys);
this.zmpt_cntx.lineTo(x1,y1);
this.zmpt_cntx.lineTo(x2,y2);
this.zmpt_cntx.lineTo(xs,ys);
this.zmpt_cntx.fill();
 this.zmpt_cntx.closePath();
this.zmpt_cntx.strokeStyle="RED";
this.zmpt_cntx.lineWidth=1.0;
this.zmpt_cntx.beginPath();
this.zmpt_cntx.moveTo(x3,y3);
this.zmpt_cntx.lineTo(x4,y4);
this.zmpt_cntx.stroke();
this.zmpt_cntx.closePath(); 
this.plotObj.drawPoint(x5,y5,'#ff0000',10,false,this.zmpt_cntx);
}
Graph.prototype.createGTool=function(label){	
	var tool_label=label;
	var btn_height=35;
	var buttonnode= this.document.createElement('canvas');
	buttonnode.setAttribute('id',"cbtn_"+tool_label);
	buttonnode.width=this.btn_width;
	buttonnode.height=btn_height;
	var btn_cntx=buttonnode.getContext('2d');
	btn_cntx.strokeStyle="#ffffff";
	btn_cntx.lineWidth=2.0;
	btn_cntx.fillStyle="#999999";
	//this.plotObj.roundRect(btn_cntx,0,0,this.btn_width,this.btn_height,0,true,true);
	btn_cntx.fillRect(0,0,this.btn_width,btn_height);
	btn_cntx.strokeRect(0,0,this.btn_width,btn_height);
	btn_cntx.closePath();
	btn_cntx.textBaseline = 'top';
	btn_cntx.font = "bold 12px sans-serif";
	btn_cntx.fillStyle="#ffffff";  
	var to=btn_cntx.measureText(tool_label);
	
	var tw=to.width;
	var th=12;
	var img_x=(this.btn_width-tw)/2;
	var img_y=(btn_height-th)/2;
	btn_cntx.fillText(tool_label,img_x,img_y);
	console.log("LABEL:"+tool_label)
	return buttonnode;
}
Graph.prototype.createTools=function(tool_data,pos){
	this.tool_div = this.document.createElement("div");
	this.tool_div .width = this.width;
	//this.tool_div .height = 30;
	this.board.appendChild(this.tool_div );
	//this.tool_context = this.tool_div.getContext('2d');
	this.tool_div.style.position='absolute';
	this.tool_div.style.top=pos.y+'px';
	this.tool_div.style.left=pos.x+'px';
	
	this.btn_width=45;
	this.btn_height=20;
	this.btn_offsetX=this.width;
	this.btn_offsetY=0
	this.tool_row=1;
	this.tool_arr=[]
	var l=tool_data.length;
	for(var i=0;i<l;i++){
		this.createTool(tool_data[i]);
		//break
	}
	//this.init_graph_transformListeners();
}
Graph.prototype.createTool=function(tool_data){

	var tool_type=tool_data.type
	var tool_name=tool_data.id
	var tool_label=tool_data.label;
	var labelType=tool_data.img_norm_src?'image':'text'
	console.log("CREATE_TOOLS "+tool_type+":"+tool_name+":"+tool_label)
	var buttonnode= this.document.createElement('canvas');
	buttonnode.setAttribute('id',"cbtn_"+tool_name);
	buttonnode.width=this.btn_width;
	buttonnode.height=this.btn_height;
	buttonnode.style.position = 'absolute';
	
	this.btn_offsetX-=(this.btn_width+2);
	if(this.btn_offsetX<0){
	this.btn_offsetX=this.width-(this.btn_width+2);
	this.btn_offsetY+=this.btn_height+2;
	this.tool_row++
	}
	console.log("btn_offset "+this.btn_offsetX+":"+this.btn_offsetY)
	buttonnode.style.top = this.btn_offsetY+"px";
	buttonnode.style.left = this.btn_offsetX+"px";
	var btn_cntx=buttonnode.getContext('2d');
	//btn_cntx.shadowOffsetX = 2;
	//btn_cntx.shadowOffsetY = 2;
	//btn_cntx.shadowBlur = 2;
	//btn_cntx.shadowColor = 'rgba(105, 105, 105, 0.5)';
	btn_cntx.strokeStyle="#cccccc";
	btn_cntx.lineWidth=2.0;
	btn_cntx.fillStyle="#999999";
	//this.plotObj.roundRect(btn_cntx,0,0,this.btn_width,this.btn_height,0,true,true);
	btn_cntx.fillRect(0,0,this.btn_width,this.btn_height);
	btn_cntx.strokeRect(0,0,this.btn_width,this.btn_height);
	btn_cntx.closePath();
	var this_scope=this;
	var obj
	if(labelType=='image'){
	var img_norm = new Image();
	img_norm.onload=function(){
	var w=this.width;
	var h=this.height;
	this_scope.btn_img_x=(this_scope.btn_width-w)/2;
	this_scope.btn_img_y=(this_scope.btn_height-h)/2;
	btn_cntx.drawImage(img_norm,this_scope.btn_img_x,this_scope.btn_img_y)
	}
    img_norm.src = this._imageBaseDir + tool_data.img_norm_src;
	var img_sel = new Image();
    img_sel.src = this._imageBaseDir + tool_data.img_sel_src;
	
	this.tool_div.appendChild(buttonnode);
	obj={ctx:btn_cntx,labelType:labelType,img_norm:img_norm,img_sel:img_sel,w:buttonnode.width,h:buttonnode.height,id:tool_name,x:this_scope.btn_img_x,y:this_scope.btn_img_y};
	}else{
		btn_cntx.textBaseline = 'bottom';
		btn_cntx.font = "bold 12px sans-serif";
		btn_cntx.fillStyle="WHITE";  
		var to=btn_cntx.measureText(tool_label);
		var tw=to.width;
		var th=to.height;
		this_scope.btn_img_x=(this_scope.btn_width-tw)/2;
	this_scope.btn_img_y=(this_scope.btn_height-th)/2;
		btn_cntx.fillText(tool_label,this_scope.btn_img_x,this_scope.btn_img_y);
obj={ctx:btn_cntx,labelType:labelType,label:tool_label,w:buttonnode.width,h:buttonnode.height,id:tool_name,x:this_scope.btn_img_x,y:this_scope.btn_img_y};		
	}
	buttonnode.scope=this
	buttonnode.onmousedown = function(){
	this.scope.resetTools();
	this.scope.plotObj.killListeners();
	this.scope.plotObj.clear();
	
	if(obj.id==this.current_tool){
	this.scope.init_graph_transformListeners()
	return;
	}
	//
	this.current_tool=obj.id
	this.scope.selTool(obj);
	this.scope.plotObj.fneql=tool_label;	
	this.scope.plotObj.initMouseListeners();
	this.scope.stop_graph_transformListeners();	
	tool_data.callfunc();
	
	}
	this.tool_arr.push(obj);
}
Graph.prototype.resetTools=function(){
	var l=this.tool_arr.length;
	var btn_cntx
	var obj
	for(var i=0;i<l;i++){
	obj=this.tool_arr[i];
	btn_cntx=obj.ctx;
	btn_cntx.clearRect(0,0,obj.w,obj.h)
	//btn_cntx.shadowOffsetX = 2;
	//btn_cntx.shadowOffsetY = 2;
	//btn_cntx.shadowBlur = 5;
	//btn_cntx.shadowColor = "#666666";
	btn_cntx.strokeStyle="#cccccc";
	btn_cntx.lineWidth=2.0;
	btn_cntx.fillStyle="#999999";
	//this.plotObj.roundRect(btn_cntx,0,0,this.btn_width,this.btn_height,0,true,true);
	btn_cntx.fillRect(0,0,this.btn_width,this.btn_height);
	btn_cntx.strokeRect(0,0,this.btn_width,this.btn_height);
	if(obj.labelType=='image'){
	btn_cntx.drawImage(obj.img_norm,this.btn_img_x,this.btn_img_y);
	}else{
	btn_cntx.fillStyle="WHITE"; 
	btn_cntx.fillText(obj.label,obj.x,obj.y);
	}
	}
}
Graph.prototype.selTool=function(obj){
	
	var btn_cntx=obj.ctx;
	btn_cntx.clearRect(0,0,obj.w,obj.h)
	//btn_cntx.shadowOffsetX = 2;
	//btn_cntx.shadowOffsetY = 2;
	//btn_cntx.shadowBlur = 5;
	//btn_cntx.shadowColor = "#666666";
	btn_cntx.strokeStyle="#cccccc";
	btn_cntx.lineWidth=2.0;
	btn_cntx.fillStyle="#999999";
	//this.plotObj.roundRect(btn_cntx,0,0,this.btn_width,this.btn_height,0,true,true);
	btn_cntx.fillRect(0,0,this.btn_width,this.btn_height);
	btn_cntx.strokeRect(0,0,this.btn_width,this.btn_height);
	
	if(obj.labelType=='image'){
	btn_cntx.drawImage(obj.img_sel,this.btn_img_x,this.btn_img_y)
	}else{
	btn_cntx.fillStyle="GREEN"; 
	btn_cntx.fillText(obj.label,obj.x,obj.y);
	}
}
/**Drawing Methods*/
Graph.prototype.drawGraph = function() {

	/**Draw grid lines*/
	var errorD;
	var isNaN=function(n){
	return Number(n)==Number.NaN;
	}
		if (this.graph_type == "xy") {
			errorD = isNaN(this.axisYpos) || isNaN(this.axisXpos) || isNaN(this.incX1) || isNaN(this.incY1);
		} else {
			errorD = isNaN(this.axisYpos) || isNaN(this.incX1);
		}
		if (errorD) {
			alert("ERROR cannot plot the given axis range!");
			return;
		}
	var scope = this.context;
	var alab = 0;
	var i;
	var label;
	var label_dx=this.graph_type=='xy'?0:3;
	var grid_s=this.graph_type=='xy'?0:this.axisXpos-5;
	var grid_len=this.graph_type=='xy'?this.height:this.axisXpos+5;
	scope.lineWidth = 1.0;
	scope.strokeStyle = this.graph_type=='xy'?"rgb(150, 150, 150)":"BLACK";
	scope.beginPath();
	console.log(this.axisYpos + ":" + this.width + ":" + this.scaleX)
	for( i = this.axisYpos; i <= this.width; i += this.incX) {
		scope.moveTo(i, grid_s);
		scope.lineTo(i, grid_len);
		if(alab > 0 && (i < this.width)) {
			label = alab /this.divX;
			scope.fillText(label, i-label_dx, this.axisXpos + 12)
		}
		//console.log(i+":"+label)
		alab++;
	}
	//scope.stroke();
	alab = 0;
	for( i = this.axisYpos; i > 0; i -= this.incX) {
		scope.moveTo(i, grid_s);
		scope.lineTo(i, grid_len);
		if(alab > 0 && (i > 0)) {
			label = -alab /this.divX;
			scope.fillText(label, i-label_dx, this.axisXpos + 12)
		}
		alab++;
	}
	if(this.graph_type=='xy'){
	alab = 0;
	for( i = this.axisXpos; i <= this.height; i += this.incY) {
		scope.moveTo(0, i);
		scope.lineTo(this.width, i);
		if(alab > 0 && (i < this.height)) {
			label = -alab / this.divY;
			scope.fillText(label, this.axisYpos + 3, i)
		}
		alab++;
	}
	alab = 0;
	for( i = this.axisXpos; i > 0; i -= this.incY) {
		scope.moveTo(0, i);
		scope.lineTo(this.width, i);
		if(alab > 0 && (i > 0)) {
			label = alab /this.divY;
			scope.fillText(label, this.axisYpos + 3, i)
		}
		alab++;
	}
	}else{
		scope.fillText('0', this.axisYpos-label_dx, this.axisXpos+12)
	}
	scope.stroke();
	/**Draw Axes Lines*/
	scope.lineWidth = this.graph_type=='xy'?2.0:1.0;
	scope.strokeStyle = "BLACK";
	scope.beginPath();
	scope.moveTo(0, this.axisXpos);
	scope.lineTo(this.width, this.axisXpos);
	scope.moveTo(this.axisYpos, grid_s);
	scope.lineTo(this.axisYpos, grid_len);
	scope.stroke();
	/**Draw Arrows for Axes*/
	//!- arrow at west end
	scope.beginPath();
	scope.moveTo((this.width), this.axisXpos);
	scope.lineTo((this.width) - 10, this.axisXpos + 4);
	scope.lineTo((this.width) - 10, this.axisXpos - 4);
	scope.lineTo((this.width), this.axisXpos);
	scope.fill();
	//!- arrow at east end
	scope.beginPath();
	scope.moveTo((0), this.axisXpos);
	scope.lineTo((0) + 10, this.axisXpos + 4);
	scope.lineTo((0) + 10, this.axisXpos - 4);
	scope.lineTo((0), this.axisXpos);
	scope.fill();
	if(this.graph_type=='xy'){
	//!- arrow at north end
	scope.beginPath();
	scope.moveTo(this.axisYpos, (0));
	scope.lineTo(this.axisYpos + 4, (0) + 10);
	scope.lineTo(this.axisYpos - 4, (0) + 10);
	scope.lineTo(this.axisYpos, (0));
	scope.fill();
	//!- arrow at south end
	scope.beginPath();
	scope.moveTo(this.axisYpos, (this.height));
	scope.lineTo(this.axisYpos + 4, (this.width) - 10);
	scope.lineTo(this.axisYpos - 4, (this.width) - 10);
	scope.lineTo(this.axisYpos, (this.height));
	scope.fill();
	}

	/**Draw Graph Border*/
	scope.strokeRect(0, 0, this.width, this.height);

}
Graph.prototype.clearGraph=function(){
	//this.canvas.clearRect(0, 0, this.width, this.height);
	var cn = this.board.getElementsByTagName("canvas");
  for (var i = 0; i < cn.length; i++) 
  {
    //console.log(cn[i]);  
	if(cn[i].id.indexOf('cbtn')>-1){
	continue;
	}
     cn[i].getContext('2d').clearRect(0, 0, this.width, this.height);
  }

}
Graph.prototype.clearCanvas=function(cntx){	
var ctx=cntx?cntx:this.context;
   ctx.clearRect(0, 0, this.width, this.height);
}
/** Utility methods*/
Graph.prototype.getCanvasOffSet=function () {
	var box = this.canvas.getBoundingClientRect();    
	var body = this.document.body;
	var docElem = this.document.documentElement;
    var scrollTop = window.pageYOffset || docElem.scrollTop || body.scrollTop;
    var scrollLeft = window.pageXOffset || docElem.scrollLeft || body.scrollLeft;
    var clientTop = docElem.clientTop || body.clientTop || 0;
    var clientLeft = docElem.clientLeft || body.clientLeft || 0;
    var top  = box.top +  scrollTop - clientTop;
    var left = box.left + scrollLeft - clientLeft;
    this.offX=Math.round(left);
	this.offY=Math.round(top);
    return { top: this.offY, left: this.offX }
}
Graph.prototype.getCursorPos=function (e) {
this.isTouchEnabled = e.type.indexOf('touch') > -1;
	var ev = e ?e: window.event;
	ev=this.isTouchEnabled?ev.changedTouches[0]:ev;
    var cursor = {x:0, y:0};
    if (ev.pageX!==undefined) {
        cursor.x = ev.pageX-this.offX;
        cursor.y = ev.pageY-this.offY;
		
    } 
    else {
        cursor.x = ev.clientX - this.offX;
        cursor.y = ev.clientY - this.offY;
		
    }
    return cursor;
}
Graph.prototype.coordToCanvasPoint = function(x, y) {
	var xp = this.axisYpos + x * (this.scaleX / this.xinc);
	var yp = this.axisXpos - y * (this.scaleY / this.yinc);
	return [xp, yp];
}
Graph.prototype.canvasPosToCoords = function(xp, yp) {
	console.log("canvasPosToCoords"+xp+":"+yp)
	var x=(xp-this.axisYpos)/(this.scaleX / this.xinc)
	var y=(-yp+this.axisXpos)/(this.scaleY / this.yinc)
	return [x, y];
}
Graph.prototype.scaleGraph=function(sx,sy){
	this.scaleX = sx;
	this.scaleY = sy;
	this.clearGraph();	
	this.getRatio();
	this.drawGraph();		
}
Graph.prototype.shiftAxis=function(ax,ay){
	this.axisYpos+=ax;
	this.axisXpos+=ay?ay:0;
	this.clearGraph();
	this.drawGraph();
}
Graph.prototype.ScaleFromCenter=function(sx,sy){
		var pos={x:this.canvas.width/2,y:this.canvas.height/2};
		var pXscale=this.scaleX;
		var pXaxis=this.axisYpos;
		var pYscale=this.scaleY;
		var pYaxis=this.axisXpos;
		var npos=this.canvasPosToCoords(pos.x,pos.y)
		var inx=this.xinc;
		var iny=this.yinc;
		var px=(Math.round(npos[0]/inx)*inx);
		var py=(Math.round(npos[1]/iny)*iny);
		this.scaleGraph(sx,sy);
		var dx=(sx-pXscale)*px;	
		var dy=(sy-pYscale)*py;		
		this.axisYpos = pXaxis-dx;
		this.axisXpos = pYaxis-dy;
		this.clearGraph();
		this.drawGraph();
}
Graph.prototype.ScaleFromPt=function(sx,sy){
		var pos={x:this.zmpt_pos.nx,y:this.zmpt_pos.ny};
		sy=this.graph_type=='x'?this.scaleY:sy;
		sx=sx?sx:this.scaleX;
		sy=sy?sy:this.scaleY;
		var pXscale=this.scaleX;
		var pXaxis=this.axisYpos;
		var pYscale=this.scaleY;
		var pYaxis=this.axisXpos;
		var npos=this.canvasPosToCoords(pos.x,pos.y)
		var inx=this.xinc;
		var iny=this.yinc;
		var px=(Math.round(npos[0]/inx)*inx);
		var py=(Math.round(npos[1]/iny)*iny);
		this.scaleGraph(sx,sy)
		var dx=(sx-pXscale)*px;	
		var dy=(sy-pYscale)*py;		
		this.axisYpos = pXaxis-dx;
		this.axisXpos = pYaxis-dy;
		this.clearGraph();
		this.drawGraph();
}
Graph.prototype.increment=function(val) {
		var inc;
		var scaleUnits=this.scaleUnits;
		console.log("SCALE_UNITS:"+this)
		if (val>=1000) {
			inc = Math.round(val/1000)*1000;
			return inc;
		}
		if (val>=1) {
			for (var v = 0; v<scaleUnits.length-1; v++) {
				//trace("::: " + val + ":" + scaleUnits[v] + ":" + scaleUnits[v + 1]);
				if (val>=scaleUnits[v] && val<scaleUnits[v+1]) {
					inc = Math.round(val/scaleUnits[v])*scaleUnits[v];
					break;
				}
			}
		}
		//trace("::: inc " + inc);        
		return inc;
}
Graph.prototype.getRatio=function() {
var xscale=this.scaleX;
var yscale=this.scaleY;
var xPscale=this.xPscale;
var yPscale=this.yPscale;

		this.ratioX = xscale/xPscale;
		this.ratioY = yscale/yPscale;
		var ratioX=this.ratioX;
		var ratioY=this.ratioY;
		ratioX = Math.fixTo(ratioX, 8);
		ratioY = Math.fixTo(ratioY, 8);
		var incX,divX;
		var incY,divY;
		var increment=this.increment;
		if (ratioX>=1 && ratioX<2) {
			incX = xscale;
			divX = 1;
		} else if (ratioX>=2 && ratioX<5) {
			incX = xscale/2;
			divX = 2;
		} else if (ratioX>=5) {
			divX = ratioX;
			divX = divX>1 ? this.increment(divX) : divX;
			incX = xscale/divX;
		} else if (ratioX>=.5 && ratioX<1) {
			incX = xscale*2;
			divX = .5;
		} else if (ratioX>=.2 && ratioX<.5) {
			incX = xscale*5;
			divX = .2;
		} else if (ratioX>=.1 && ratioX<.2) {
			incX = xscale*10;
			divX = .1;
		} else {
			var __rxT = 1/this.increment(1/ratioX);
			__rxT = Math.fixTo(__rxT, String(1/__rxT).split(".")[0].length);
			divX = __rxT;
			incX = xscale/divX;
		}
		if (ratioY>=1 && ratioY<2) {
			incY = yscale;
			divY = 1;
		} else if (ratioY>=2 && ratioY<5) {
			incY = yscale/2;
			divY = 2;
		} else if (ratioY>=5) {
			divY = ratioY;
			divY = divY>1 ? this.increment(divY) : divY;
			incY = yscale/divY;
		} else if (ratioY>=.5 && ratioY<1) {
			incY = yscale*2;
			divY = .5;
		} else if (ratioY>=.2 && ratioY<.5) {
			incY = yscale*5;
			divY = .2;
		} else if (ratioY>=.1 && ratioY<.2) {
			incY = yscale*10;
			divY = .1;
		} else if (ratioY<.1) {
			var __ryT = 1/this.increment(1/ratioY);
			__ryT = Math.fixTo(__ryT, String(1/__ryT).split(".")[0].length);
			//ratioY = increment(ratioY)
			divY = __ryT;
			incY = yscale/divY;
		}
		incX = Math.fixTo(incX, 8);
		incY = Math.fixTo(incY, 8);
		this.incX1 = incX/2;
		this.incY1 = incY/2;
		var iX = (1/divX);
		var iY = (1/divY);
		this.incX=incX;
		this.incY=incY;
		this.divX=divX;
		this.divY=divY;
	}
Graph.prototype.zoomIn=function(){
	var ds=(this.scaleX/10);
	var sx=this.scaleX+ds
	var sy=this.graph_type=='x'?this.scaleY:this.scaleY+ds;
	if(sx<=55000){
		this.ScaleFromPt(sx,sy)
		this.plotObj.setAxisDatas()
	}
}
Graph.prototype.zoomOut=function(){
	var ds=(this.scaleX/10);
	var sx=this.scaleX-ds
	var sy=this.graph_type=='x'?this.scaleY:this.scaleY-ds;
	if(sx>=0.009){
		this.ScaleFromPt(sx,sy);
		this.plotObj.setAxisDatas();
	}
}
Graph.prototype.scrollLeft=function(a){
	var a0=a==undefined?this.scaleX:a
	var dx = this.xinc*a0;
	dy = 0;
	this.shiftAxis(dx, dy);	
	this.plotObj.setAxisDatas();
}
Graph.prototype.scrollRight=function(a){
	var a0=a==undefined?this.scaleX:a
	var dx = -this.xinc*a0;
	dy = 0;
	this.shiftAxis(dx, dy);	
	this.plotObj.setAxisDatas();
}
Graph.prototype.scrollTop=function(a){
	var a0=a==undefined?this.scaleY:a
	var dy = this.yinc*a0;
	dx = 0;
	this.shiftAxis(dx, dy);	
	this.plotObj.setAxisDatas();
}
Graph.prototype.scrollBottom=function(a){
	var a0=a==undefined?this.scaleY:a
	var dy = -this.yinc*a0;
	dx = 0;
	this.shiftAxis(dx, dy);	
	this.plotObj.setAxisDatas();
}
Graph.prototype.init_graph_transformListeners=function(){
this.trans_graph=true;
if(this.zmtool_div){
this.zmtool_div.style.display='block';
}
	this.initKeyListeners1();
}
Graph.prototype.stop_graph_transformListeners=function(){
this.trans_graph=false;
this.zmtool_div.style.display='none'
	this.killKeyListeners1();
}
Graph.prototype.killKeyListeners1=function(){
	console.log("killKeyListeners1")
	var that=this;
	this.document.onkeydown=null;
}
Graph.prototype.initKeyListeners1=function(){
	console.log("initKeyListeners1")
	var that=this;
	this.document.onkeydown=function(e){
		var event=e?e:window.event;
		event.preventDefault();
		var code=event.keyCode;
		console.log("KEY_DOWN: "+code)
		switch(code){
			case 107:
			that.zoomIn();
			break;
			case 109:
			that.zoomOut();
			break;
			case 37:
			that.scrollLeft();
			break;
			case 39:
			that.scrollRight();
			break;
			case 38:
			that.scrollTop();
			break;
			case 40:
			that.scrollBottom();
			break;
		}
	}
}
var Anim_mngr=function(_canvas,tweenObj){
	this.canvas=_canvas;
	window.requestAnimFrame = (function(){
      return  window.requestAnimationFrame       || 
              window.webkitRequestAnimationFrame || 
              window.mozRequestAnimationFrame    || 
              window.oRequestAnimationFrame      || 
              window.msRequestAnimationFrame     || 
              function(callback, element){
                window.setTimeout(callback, 1000 / 60);
              };
    })();
    window.cancelRequestAnimFrame = ( function() {
		return window.cancelAnimationFrame          ||
        window.webkitCancelRequestAnimationFrame    ||
        window.mozCancelRequestAnimationFrame       ||
        window.oCancelRequestAnimationFrame     ||
        window.msCancelRequestAnimationFrame        ||
        clearTimeout
	} )();
}
Anim_mngr.prototype.startAnim=function(func,scope,params){
    this.render(func,scope,params);
    this.animObj=window.requestAnimFrame(this.animloop,this.canvas)
}
Anim_mngr.prototype.stopAnim=function(){
	if(this.animObj){
		window.cancelAnimFrame(this.animObj);
	}
}
Anim_mngr.prototype.render=function(func,scope,params){
	scope[func](params);
}
/**
* Global utility methods
*/
Array.prototype.shuffle = function(n) {
        var len = this.length;
        var i = n ? n : len;
        while (i--) {
                var p = parseInt(String(Math.random()*len));
                var t = this[i];
                this[i] = this[p];
                this[p] = t;
        }
};
Array.prototype.pushUnique = function(ele, prop) {
        var push = true;
        var arrVal;
        var eleVal;
        var array = this;
        for (var i = 0; i<array.length; i++) {
                if (prop) {
                        arrVal = array[i][prop];
                        eleVal = ele[prop];
                } else {
                        arrVal = array[i];
                        eleVal = ele;
                }
                if (eleVal == arrVal) {
                        push = !true;
                        break;
                }
        }
        push ? array.push(ele) : "";
        return push;
};
Array.prototype.randomNumbers = function(from, to, count, unique) {
        var num;
        var boo;
        var unq = unique == undefined ? true : unique;
        while (this.length<count) {
                num = Math.round(Math.random()*(from-to))+to;
                if (unq) {
                        boo = this.pushUnique(num);
                } else {
                        this.push(num);
                }
        }
        return this;
};
Array.randomNumberArray = function(from, to, count, unique) {
        var num;
        var boo;
        var unq = unique == undefined ? true : unique;
        var a=[];
        while (a.length<count) {
                num = Math.round(Math.random()*(from-to))+to;
                if (unq) {
                        boo = a.pushUnique(num);
                } else {
                        a.push(num);
                }
        }
        return a;
};
Array.shuffleArray = function(arr, count) {
        var len = arr.length;
        var shuf = [];
        var a=[];
        shuf.randomNumbers(0, len-1, len, true);
        if (count == undefined) {
                count = len;
        }
        for (var i = 0; i<count; i++) {
                a.push(arr[shuf[i]]);
        }
        return a;
};
Array.prototype.getIndex=function(indexObj, prop) {
                for (var i = 0; i<this.length; i++) {
                        var arrE = prop == undefined ? this[i] : this[i][prop];
                        if (arrE == indexObj) {
                                return i;
                        }
                }
                return null;
};
Array.joinWith=function(arr,op){
return arr.join(op);
}
Array.arrayOf=function(n,l){
var a=[]
for(var i=0;i<l;i++){
a.push(n)
}
return a;
}

Math.multiple=function(x,y)
{

     var myString = "";
         var myString1="";
         var mult=x;

     for (i=1; i<y; i++) {

          myString += x + " &times; " ;
          mult=mult*x
           myString1 +=   myString + x + " = " + mult + "<br/>";

         }
     return (myString1);

}
Math.leMultiple=function(x,y,z)
{

     var myString = "";
         var myString1="";
         var mult=x;
		 var a;

     for (i=1; i<y; i++) {

          myString += x + " &times; " ;
          mult=mult*(x);
		  a=mult.toFixed(z);
		  
            myString1 +=   myString + x + " = " + a + "<br/>";

         }
     return (myString1);

}
Math.reMultiple=function(n,d,y)
{

    	 var myString = "";
         var myString1="";
		 var myString2="";
         var mult1=n;
		 var mult2=d;
		
				var str_s="<math><mrow>";
                var str_e="</math></mrow>";
                var str="";
				
				
     for (i=1; i<y; i++) {

          myString += str_s+"<mfrac><mtext>"+n+"</mtext><mtext>"+d+"</mtext></mfrac>" +str_e+ " &times; " ;
          mult1=mult1*(n);
		 mult2=mult2*(d);
		  
            myString1 +=   myString + str_s+"<mfrac><mtext>"+n+"</mtext><mtext>"+d+"</mtext></mfrac>" + str_e+" = " + str_s+"<mfrac><mtext>"+mult1+"</mtext><mtext>"+mult2+"</mtext></mfrac>" +str_e+ "<br/>";
         }
     return (myString1);

}
Math.randomNumber = function(from, to) {
        var num;
        var boo;
        var unq = true;
        var arr=[];
        var count=to-from;
        while (arr.length<count) {
                num = Math.round(Math.random()*(from-to))+to;
                if (unq) {
                        boo = arr.pushUnique(num);
                } else {
                        arr.push(num);
                }
        }
        arr.shuffle();
        return arr[0];
};
Math.fixTo = function(n, pl, round) {
        if (Number(n) == Number.NaN) {
                return n;
        }
        if (round == undefined) {
                round = true;
        }
        var fixed;
        fixed = Math.pow(10, pl)*n;
        if (round) {
                fixed = Math.round(fixed);
        } else {
                fixed = Math.floor(fixed);
        }
        fixed = fixed/Math.pow(10, pl);
        return fixed;
};
Math.addFrac = function(f1, f2) {
        var num1 = f1.split("/");
        var num2 = f2.split("/");
        var n, d, lcm, n0, n1, d0, d1;
        n0 = num1[0];
        n1 = num2[0];
        d0 = num1[1] ? num1[1] : 1;
        d1 = num2[1] ? num2[1] : 1;
        lcm = Math.getLCM(d0*1, d1*1);
        d = lcm;
        n0 = (lcm/d0)*n0;
        n1 = (lcm/d1)*n1;
        n = n0+n1;
        var frac = n+"/"+d;
        var sfrac = Math.simpleFrac(frac);
        return {frac:frac, val:sfrac};
};
Math.simpleFrac = function(frac) {
        var itsfrac = String(frac).indexOf('/') != -1 ? true : false;
        var itsDeci = String(frac).indexOf('.') != -1 ? true : false;
        var n, d;
        var _frac = frac;
        if (itsfrac) {
        } else if (itsDeci) {
                _frac = Math.convertToFrac(frac).val;
        } else {
                return frac;
        }
        var splitVal = String(_frac).split("/");
        n = splitVal[0];
        d = splitVal[1];
        var s1 = n.indexOf("-")>-1 ? "-" : "";
        var s2 = d.indexOf("-")>-1 ? "-" : "";
        n = n.split("-").join("");
        d = d.split("-").join("");
        gcd = Math.getGCD(n, d);
        n = n/gcd;
        d = d/gcd;
        var s = (s1 == "-" && s2 == "-") ? "" : (s1 == "-" || s2 == "-" ? "-" : "");
        if(d==1){
                return s+""+n;
        }
        return s+""+n+"/"+d;
};
Math.convertToFrac = function(n) {
        var n0, d0;
        var fracObj = {};
        var itsfrac = String(n).indexOf('/') != -1 ? true : false;
        var itsDeci = String(n).indexOf('.') != -1 ? true : false;
        if (itsfrac) {
                var splitVal = String(n).split("/");
                fracObj.n = splitVal[0];
                fracObj.d = splitVal[1];
                fracObj.val = String(n);
                return fracObj;
        }
        if (!itsDeci) {
                fracObj.n = n;
                fracObj.d = 1;
                fracObj.val = String(n);
        } else {
                var deciNum = String(n).split(".")[1].length;
                var process = true;
                var a = 0;
                var b;
                while (process) {
                        a++;
                        b = (n*a);
                        if (deciNum>=4) {
                                b = Math.fixTo(b, 3);
                        } else {
                                b = Math.fixTo(b, 8);
                        }
                        if (b == parseInt(b) || a>10000) {
                                fracObj.n = b;
                                fracObj.d = a;
                                fracObj.val = b+"/"+a;
                                process = false;
                        }
                }
        }
        return fracObj;
};
Math.getGCD = function() {
        var argL = arguments.length;
        var gcd;
        if (argL<2) {
                var mystr = String(arguments[0]);
                if (Number(mystr) != Number.NaN) {
                        return arguments[0];
                } else {
                        if (mystr.indexOf("/") == -1) {
                                return arguments[0];
                        } else {
                                var myvars = mystr.split("/");
                                var n = myvars[0];
                                var d = myvars[1];
                                return Math.getGCD(n, d);
                        }
                }
        } else if (argL>2) {
                return null;
        } else {
                if (arguments[1] == 0) {
                        return arguments[0];
                } else {
                        return Math.getGCD(arguments[1], Math.fixTo(arguments[0]%arguments[1], 8));
                }
        }
};
Math.getLCM = function() {
        var argL = arguments.length;
        var lcm;
        var gcd;
        if (argL<2) {
                var mystr = String(arguments[0]);
                if (Number(mystr) != Number.NaN) {
                        return arguments[0];
                } else {
                        if (mystr.indexOf("/") == -1) {
                                return arguments[0];
                        } else {
                                var myvars = mystr.split("/");
                                var n = myvars[0];
                                var d = myvars[1];
                                return getLCM(n, d);
                        }
                }
        } else if (argL>2) {
                return null;
        } else {
                if (arguments[1] == 0) {
                        return arguments[0];
                } else {
                        gcd = Math.getGCD(arguments[0], arguments[1]);
                        lcm = (arguments[0]*arguments[1])/gcd;
                        return lcm;
                }
        }
};
Math.rand=function (n, m) {
        var n0, m0;
        m0 = m;
        n0 = n;
        if (!m) {
                m0 = n;
                n0 = 0;
        }
        return Math.floor(Math.random()*(m0-n0+1))+n0;
}
Math.getProduct=function(arr){
        var prod=1;
        for(var i=0;i<arr.length;i++){
                prod=prod*arr[i];
        }
        return prod;
}
Math.primeStr = "~2~3~5~7~11~13~17~19~23~29~31~37~41~43~47~53~59~61~67~71~73~79~83~89~97~101~103~107~109~113~127~131~137~139~149~151~157~163~167~173~179~181~191~193~197~199~211~223~227~229~233~239~241~251~257~263~269~271~277~281~283~293~307~311~313~317~331~337~347~349~353~359~367~373~379~383~389~397~401~409~419~421~431~433~439~443~449~457~461~463~467~479~487~491~499~503~509~521~523~541~547~557~563~569~571~577~587~593~599~601~607~613~617~619~631~641~643~647~653~659~661~673~677~683~691~701~709~719~727~733~739~743~751~757~761~769~773~787~797~809~811~821~823~827~829~839~853~857~859~863~877~881~883~887~907~911~919~929~937~941~947~953~967~971~977~983~991~997~";
Math.primeArr = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997];
Math.isPrime=function(n) {
        var cStr = "~" + n + "~";
        return Math.primeStr.indexOf(cStr) != -1;
}
Math.getFirstPrimeFact=function (n) {
        var pLen=Math.primeArr.length;
        for (var i = 0; i < pLen; i++) {
                if (n % Math.primeArr[i] == 0) {
                        return Math.primeArr[i];
                }
        }
}
Math.getPrimeFactors=function(n) {
        var pF = [];
        var f;
        if(n<2){
                return [n];
        }
        console.log("FLASH_CARD_MNGR_CALLING GET PRIME FACTORS "+n+":"+Math.isPrime(n));
        while (!Math.isPrime(n)) {
                f = Math.getFirstPrimeFact(n);
                pF.push(f);
                n = n / f;
        }
        pF.push(n);
        return pF;
}
Math.getCommonPrimeFactors=function() {
        function getString(d,ex, arr) {
        var tarr = [].concat(arr);
        var str = d+" = ";
        var boo;
        for (var i = 0; i < ex.length; i++) {
                boo=false
                for (var g = 0; g < tarr.length; g++) {
                        if (ex[i] == tarr[g]) {
                                boo=true;
                                tarr.splice(g,1);
                                break
                        }
                }
                if(i==ex.length-1){
                        op="";
                }else{
                        op="*";
                }
                if(boo){
					if(ex[i]=='1'){
						str+="<b>"+ex[i]+"</b>"+op;
					}else{
                        str+="<b><font color='red'>"+ex[i]+"</font></b>"+op;
					}
                }else{
                    str+=ex[i]+op;
                }
        }
        str=str.split("*").join(" \u00d7 ");
        return str
}
var n,d,e,gcf
n=arguments[0];
d=arguments[1];
if(arguments.length==3){
gcf=arguments[2]
}else{
e=arguments[2];
gcf=arguments[3]
}
        var npf = Math.getPrimeFactors(n);
        var dpf = Math.getPrimeFactors(d);
        var epf=e?Math.getPrimeFactors(e):null;
        var cpf = Math.getPrimeFactors(gcf);
        var cpf_n=getString(n,npf,cpf);
        var cpf_d=getString(d,dpf,cpf);
        var cpf_e=epf==null?null:getString(e,epf,cpf);
        if(npf.length==1){
		if(String(n)=='1'){
		 cpf_n=cpf_n+""
		}else{
        cpf_n=cpf_n+" <b>("+n+" is prime)</b>";
		}
        }
        if(dpf.length==1){
		if(String(d)=='1'){
        cpf_d=cpf_d+"";
		}else{
		 cpf_d=cpf_d+" <b>("+d+" is prime)</b>";
		}
        }
        if(epf&&epf.length==1){
		if(String(e)=='1'){
        cpf_e=cpf_e+"";
		}else{
        cpf_e=cpf_e+" <b>("+e+" is prime)</b>";
		}
        }
		console.log(cpf_n+":"+cpf_d+":"+cpf_e);
        if(epf){
        return [cpf_n, cpf_d,cpf_e,npf, dpf, cpf,epf];
        }else{
        return [cpf_n, cpf_d,npf, dpf, cpf];
        }
}
/** END OF GLOBAL UTILITY METHODS*/
/**
 * Install any widgets
 *
 * Each widget is registered with the AuthorApi.
 *
 * Each widget must have a name property and an initialize method. The name
 * property returns the 'type' of the widget, referenced via the type property
 * (ie, <widget type='NAME'/?\>). Also each widget must have an initialize
 * method that accepts two params: the domElement where the widget is referenced
 * and a configuration object evaled from the contents of the widget div.
 * It is up to the widget to use the JSON as it sees fit.
 *
 */

/** configuration:
 *
 *    gtype
 *    xmin
 *    xmax
 *    ymin
 *    ymax
 *    xinc
 *    yinc
 *   interactive
 *
 */
var GraphWidget = {
        name : "graph",
        initialize : function(widgetDom, config) {

                if(config.gtype == 'x') {
                    widgetDom.className = "widget_graph_x";
                }
                else {
                        widgetDom.className = "widget_graph_xy";
                }

                // prepare the dom widget to contain the graph
                var board = widgetDom;
                var graph = new Graph(document, board, config.gtype, config.xmin, config.xmax,
                                config.ymin, config.ymax,config.xinc, config.yinc, config.interactive);
                var plot = new Plotter(graph, 'point', {data : config.plot_data});
        }
};

/**
 * defines global methods that can be called in real time by the solution
 * infrastructure authors.
 */
var AuthorApi = (function() {
        var theApi = {}

        theApi.sayHello = function(from) {
                alert('say hello: ' + from);
        }

        theApi.getRandomInt = function(amount) {
                return Math.floor(Math.random() * Number(amount))
        }
        theApi.formatFractionDisplay=function(n,d,ntype){
                var node=ntype?ntype:'full';
                var str_s=node=='part'?'':"<math><mrow>";
                var str_e=node=='part'?'':"</math></mrow>";
                var str="";
                if(d==1||d==undefined||d==""){
                        str="<mtext>"+n+"</mtext>";
                }else{
                        str="<mfrac><mtext>"+n+"</mtext><mtext>"+d+"</mtext></mfrac>"
                }
                return str_s+str+str_e;
        }
        theApi.getMathML=function(val,type,ntype){
                var node=ntype?ntype:'full';
                var str_s=node=='part'?'':"<math><mrow>";
                var str_e=node=='part'?'':"</math></mrow>";
                var str="";
                if(type=='text'){
                        str="<mtext>"+val+"</mtext>";
                }else if(type=='op'){
                        str="<mo>"+val+"</mo>";
                }else if(type=='frac'){
                        var arr=val.split("/");
                        arr[1]=arr[1]==undefined?'1':arr[1];
                        str="<mfrac><mtext>"+arr[0]+"</mtext><mtext>"+arr[1]+"</mtext></mfrac>"
                }
                return str_s+str+str_e;
        }
        /**
        *Flash cards specific methods
        *
        */
        theApi.getFlashCardProblem = function(id) {
                //console.log('GET_FLASH_CARD_PROBLEM FOR '+id);
                Flashcard_mngr.getProblem(id);
        }

        /**
         * register widget that can be called with <widget> only widgets matching a
         * type will be created.
         */
        theApi.widgets = [ GraphWidget ];

        function initializeWidgets() {
                // look for widget definitions
                var tutorHead = $get('tutoroutput');
                if (tutorHead) {
                        var widgets = tutorHead.getElementsByTagName("widget");
                        for ( var i = 0, t = widgets.length; i < t; i++) {
                                var widgetDom = widgets[i];
                                var type = widgetDom.getAttribute('type');
                                var args = widgetDom.innerHTML;
                                widgetDom.innerHTML = "";
                                for ( var w = 0, wt = theApi.widgets.length; w < wt; w++) {
                                        if (theApi.widgets[w].name === type) {
                                                var config='';
                                                try {
                                                        if(args && args.length > 1) {
                                                                config = eval('(' + args + ')');
                                                        }
                                                }
                                                catch(e) {
                                                        alert('Error creating widget configuration: ' + e);
                                                }
                                                theApi.widgets[w].initialize(widgetDom, config);
                                        }
                                }
                        }
                }
        }

        HmEvents.eventTutorInitialized.subscribe(function(x) {
                initializeWidgets();
        });

        return theApi;
}());

/** END OF GLOBAL UTILITY METHODS*/
/**
 * tempororily adding the flash card manager class for testing purpose
 */
var Flashcard_mngr = (function () {
    var mngr = {};
    mngr.topic_id = 0;
    mngr.current_pblm_index = 0;
    mngr.completed_pblms = 0;
    mngr.limit = 3;
    mngr.initialized = false;
    mngr.quest_data = {};
    mngr.current_quest = null;
    mngr.current_ans = null;
    console.log("FLASH_CARD_MNGR_INITIATED");

    mngr.init = function (id, lim) {
        console.log("FLASH_CARD_MNGR_INITED FOR TOPIC ID:" + id)
        mngr.totalQuest = 10;
        this.limit = lim ? lim : this.limit;
        this.topic_id = id;
        this.initialized = true;
		mngr.current_pblm_index = 0;
    mngr.completed_pblms = 0;
    mngr.quest_data = {};
    mngr.current_quest = null;
    mngr.current_ans = null;
	 mngr.topic_data=null;
        console.log("FLASH_CARD_MNGR_CALLING GEN PROBLEMS FOR:" + id)
        //this.genProblems();
        if ((this.topic_id > 10 && this.topic_id < 18) || (this.topic_id > 18 && this.topic_id < 22) || this.topic_id == 26 || this.topic_id == 27) {
            this.genProblems();
        } else {
            // this.fetchTopicData();
            this.genProblems();
/**
                !-enable 'fetchTopicData' and disable 'genProblems' once the manual data are moved to server db
                */
        }
    };
/**
        BEGINING OF DATA DEFINITIONS
        !- manually defined data are organised here, we can move this database when needed
        */
    //
    var topic_data_1 = "1/3 + 1/3,1/3 + 2/3,2/3 + 1/3,1/4 + 1/4,1/4 + 2/4,2/4 + 1/4,1/4 + 3/4,1/5 + 1/5,2/5 + 2/5,3/5 + 1/5,3/5 + 2/5,1/6 + 1/6,1/6 + 2/6,1/6 + 3/6,1/6 + 5/6,2/6 + 1/6,5/6 + 1/6,1/7 + 4/7,2/7 + 4/7,3/7 + 1/7,1/7 + 6/7,1/8 + 1/8,2/8 + 1/8,2/8 + 2/8,3/8 + 1/8,5/8 + 1/8,3/8 + 4/8,3/8 + 5/8,1/9 + 1/9,1/9 + 5/9,2/9 + 1/9,4/9 + 2/9,4/9 + 4/9,5/9 + 3/9,7/9 + 2/9,8/9 + 1/9,1/10 + 3/10,1/10 + 4/10,2/10 + 3/10,3/10 + 3/10,3/10 + 5/10,3/10 + 7/10,1/11 + 1/11,2/11 + 3/11,4/11 + 5/11,7/11 + 2/11,8/11 + 3/11,1/12 + 2/12,2/12 + 3/12,5/12 + 5/12,5/12 + 7/12";
    //
    var topic_data_2 = "1/2 + 1/3,1/2 + 1/4,1/2 + 3/4,1/2 + 1/5,1/2 + 2/5,1/2 + 1/6,1/2 + 1/8,1/2 + 3/8,1/2 + 1/9,1/2 + 4/9,1/2 + 1/10,1/2 + 3/10,1/3 + 1/2,1/3 + 1/4,1/3 + 1/5,1/3 + 2/5,1/3 + 1/6,2/3 + 1/4,2/3 + 1/5,2/3 + 1/6,2/3 + 1/8,1/4 + 1/2,1/4 + 1/3,1/4 + 1/5,1/4 + 2/5,1/4 + 3/5,1/4 + 1/6,1/4 + 1/8,1/4 + 3/8,3/4 + 1/8,3/4 + 3/8,1/4 + 3/10,1/4 + 1/12,1/4 + 5/12,1/4 + 7/12,1/4 + 11/12,3/4 + 1/12,3/4 + 5/12,3/4 + 7/12,1/5 + 1/2,1/5 + 1/3,1/5 + 1/4,1/5 + 3/4,1/5 + 1/6,1/5 + 1/10,1/5 + 3/10,1/5 + 7/10,1/5 + 1/11,1/5 + 3/11,2/5 + 3/10,2/5 + 7/10,3/5 + 1/10,3/5 + 3/10,4/5 + 1/10,1/6 + 1/2,1/6 + 1/3,1/6 + 2/3,1/6 + 1/4,1/6 + 3/4,1/6 + 1/5,1/6 + 3/5,1/6 + 4/5,1/6 + 1/7,1/6 + 2/7,1/6 + 6/7,1/6 + 1/8,1/6 + 3/8,1/6 + 5/8,1/6 + 7/8,1/6 + 1/9,1/6 + 2/9,1/6 + 4/9,1/6 + 5/9,1/6 + 7/9,1/6 + 8/9,1/6 + 1/10,1/6 + 3/10,1/6 + 7/10,1/6 + 1/12,1/6 + 5/12,1/6 + 7/12,5/6 + 1/2,5/6 + 3/4,5/6 + 1/5,5/6 + 2/5,5/6 + 3/5,5/6 + 4/7,5/6 + 6/7,5/6 + 1/8,5/6 + 3/8,5/6 + 5/8,5/6 + 1/10,5/6 + 3/10,5/6 + 7/10,5/6 + 1/12,5/6 + 5/12,5/6 + 7/12,1/7 + 1/2,1/7 + 1/3,1/7 + 1/4,1/7 + 1/5,1/7 + 1/6,1/7 + 1/8,1/7 + 3/8,1/7 + 5/8,1/7 + 1/9,1/7 + 2/9,1/7 + 4/9,1/7 + 5/9,1/7 + 7/9,2/7 + 2/8,3/7 + 3/4,4/7 + 1/8,5/7 + 1/8,1/8 + 1/2,1/8 + 1/3,1/8 + 2/3,1/8 + 1/4,1/8 + 3/4,1/8 + 1/5,1/8 + 3/5,1/8 + 4/5,1/8 + 1/6,1/8 + 5/6,1/8 + 1/7,1/8 + 2/7,1/8 + 3/7,1/8 + 4/7,1/8 + 1/9 ,1/8 + 2/9,1/8 + 1/10,1/8 + 7/10,1/8 + 1/11,1/8 + 2/11,1/8 + 9/11,1/8 + 10/11,1/8 + 1/12,1/8 + 5/12,1/8 + 7/12,1/8 + 11/12,3/8 + 1/2,3/8 + 1/3,3/8 + 2/3,3/8 + 1/4,3/8 + 3/4,3/8 + 1/5,3/8 + 3/5,3/8 + 4/5,3/8 + 1/6,3/8 + 5/6,3/8 + 1/7,3/8 + 2/7,3/8 + 3/7,3/8 + 4/7,3/8 + 1/9 ,3/8 + 2/9,3/8 + 1/10,3/8 + 7/10,3/8 + 1/11,3/8 + 2/11,3/8 + 9/11,3/8 + 10/11,3/8 + 1/12,3/8 + 5/12,3/8 + 7/12,1/9 + 1/2,1/9 + 1/3,1/9 + 2/3,1/9 + 1/4,1/9 + 3/4,1/9 + 1/5,2/9 + 1/3,2/9 + 2/3,2/9 + 4/5,2/9 + 1/6,2/9 + 3/4,2/9 + 5/6,4/9 + 1/3,4/9 + 2/3,5/9 + 1/3,5/9 + 1/6,5/9 + 5/6,1/10 + 1/2,1/10 + 1/3,1/10 + 2/3,1/10 + 1/4,1/10 + 3/4,1/10 + 1/5,1/10 + 2/5,1/10 + 3/5,1/10 + 4/5,1/10 + 1/6,1/10 + 1/8,1/10 + 7/8,1/10 + 1/12,1/10 + 7/12,3/10 + 1/2,3/10 + 1/3,3/10 + 1/4,3/10 + 3/4,3/10 + 1/5,3/10 + 2/5,3/10 + 3/5,3/10 + 4/5,7/10 + 1/2,7/10 + 1/3,7/10 + 1/4,7/10 + 1/5,7/10 + 2/5,7/10 + 1/11,7/10 + 3/11,7/11 + 1/4,1/12 + 1/2,1/12 + 1/3,1/12 + 2/3,1/12 + 1/4,1/12 + 3/4,1/12 + 1/5,1/12 + 2/5,1/12 + 1/6,1/12 + 5/6,1/12 + 1/8,1/12 + 3/8,1/12 + 5/8,1/12 + 7/8,5/12 + 1/2,5/12 + 1/3,5/12 + 2/3,5/12 + 1/4,5/12 + 3/4,5/12 + 1/6,5/12 + 5/6,5/12 + 1/7,5/12 + 1/8,5/12 + 7/8,7/12 + 1/3,7/12 + 1/5,11/12 + 1/4,11/12 + 3/4,11/12 + 1/6,11/12 + 5/6,11/12 + 1/8,11/12 + 3/7";
    //
    var topic_data_3 = "The result of an addition problem    sum,The result of a subtraction problem difference,The result of a multiplication problem       product,The result of a division problem        quotient,The top number in a fraction   numerator,The bottom number in a fraction       denominator,A polygon with 5 sides      pentagon,A polygon with 6 sides hexagon,A polygon with 8 sides  octagon,An angle measuring between 0 and 90 degrees     acute,An angle measuring between 90 and 180 degrees     obtuse,A triangle with two congruent sides      isosceles,A triangle with three congruent sides equilateral,The distance from the center of a circle to the boundary of the circle      radius,The distance across a circle through its center  diameter,A quadrilateral with four congruent sides      rhombus,A quadrilateral with two pairs of parallel sides        parallelogram,A solid figure with two circular bases that are congruent and parallel    cylinder,A solid figure with 6 square faces     cube,A solid figure with 4 triangular faces     tetrahedron,A whole number with exactly two divisors    prime,A whole number with more than two divisors        composite";
    //
    var topic_data_4 = "<data><item><question eqn='2+7*8-10|&#215;'/><answer val='48'/><step eqn='2+7*8-10=|&#215;' txt='There are no grouping symbols or exponents.'/><step eqn='2+hi(7*8)-10=2+hi(56)-10|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(2+56)-10=hi(58)-10' txt='Add and subtract from left to right.'/><step eqn='hi(58-10)=hi(48)' txt='Add and subtract from left to right.'/></item><item><question eqn='10-2*4+11|&#215;'/><answer val='13'/><step eqn='10-2*4+11=|&#215;' txt='There are no grouping symbols or exponents.'/><step eqn='10-hi(2*4)+11=10-hi(8)+11|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(10-8)+11=hi(2)+11' txt='Add and subtract from left to right.'/><step eqn='hi(2+11)=hi(13)' txt='Add and subtract from left to right.'/></item><item><question eqn='7+9*2-20|&#215;'/><answer val='5'/><step eqn='7+9*2-20=|&#215;' txt='There are no grouping symbols or exponents.'/><step eqn='7+hi(9*2)-20=7+hi(18)-20|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(7+18)-20=hi(25)-20' txt='Add and subtract from left to right.'/><step eqn='hi(25-20)=hi(5)' txt='Add and subtract from left to right.'/></item><item><question eqn='{30&#247;6}+{4^2}-20'/><answer val='1'/><step eqn='{30&#247;6}+{4^2}-20=' txt='There are no grouping symbols.'/><step eqn='{30&#247;6}+{hi(4^2)}-20={30&#247;6}+{hi(16)}-20' txt='Evaluate the exponent.'/><step eqn='hi({30&#247;6})+16-20=hi(5)+16-20' txt='Multiply and divide from left to right.'/><step eqn='hi(5+16)-20=hi(21)-20' txt='Add and subtract from left to right.'/><step eqn='hi(21-20)=hi(1)' txt='Add and subtract from left to right.'/></item><item><question eqn='{55&#247;5}-{3^2}+4'/><answer val='6'/><step eqn='{55&#247;5}-{3^2}+4=' txt='There are no grouping symbols.'/><step eqn='{55&#247;5}-{hi(3^2)}+4={55&#247;5}-{hi(9)}+4' txt='Evaluate the exponent.'/><step eqn='hi({55&#247;5})-9+4=hi(11)-9+4' txt='Multiply and divide from left to right.'/><step eqn='hi(11-9)+4=hi(2)+4' txt='Add and subtract from left to right.'/><step eqn='hi(2+4)=hi(6)' txt='Add and subtract from left to right.'/></item><item><question eqn='{8*5}-{6^2}+6|&#215;'/><answer val='10'/><step eqn='{8*5}-{6^2}+6=|&#215;' txt='There are no grouping symbols.'/><step eqn='{8*5}-{hi(6^2)}+6={8*5}-{hi(36)}+6|&#215;' txt='Evaluate the exponent.'/><step eqn='{hi(8*5)}-36+6=hi(40)-36+6|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(40-36)+6=hi(4)+6' txt='Add and subtract from left to right.'/><step eqn='hi(4+6)=hi(10)' txt='Add and subtract from left to right.'/></item><item><question eqn='5+25&#247;5*3-10|&#215;'/><answer val='10'/><step eqn='5+25&#247;5*3-10=|&#215;' txt='There are no grouping symbols or exponents.'/><step eqn='5+hi(25&#247;5)*3-10=5+hi(5)*3-10|&#215;' txt='Multiply and divide from left to right.'/><step eqn='5+hi(5*3)-10=5+hi(15)-10|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(5+15)-10=hi(20)-10' txt='Add and subtract from left to right.'/><step eqn='hi(20-10)=hi(10)' txt='Add and subtract from left to right.'/></item><item><question eqn='7+21&#247;3*2-11|&#215;'/><answer val='10'/><step eqn='7+21&#247;3*2-11=|&#215;' txt='There are no grouping symbols or exponents.'/><step eqn='7+hi(21&#247;3)*2-11=7+hi(7)*2-11|&#215;' txt='Multiply and divide from left to right.'/><step eqn='7+hi(7*2)-11=7+hi(14)-11|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(7+14)-11=hi(21)-11' txt='Add and subtract from left to right.'/><step eqn='hi(21-11)=hi(10)' txt='Add and subtract from left to right.'/></item><item><question eqn='10-12&#247;4+7'/><answer val='14'/><step eqn='10-12&#247;4+7=' txt='There are no grouping symbols or exponents.'/><step eqn='10-hi(12&#247;4)+7=10-hi(3)+7' txt='Multiply and divide from left to right.'/><step eqn='hi(10-3)+7=hi(7)+7' txt='Add and subtract from left to right.'/><step eqn='hi(7+7)=hi(14)' txt='Add and subtract from left to right.'/></item><item><question eqn='-1+[-5+(-2)*3]|&#215;'/><answer val='-12'/><step eqn='-1+[-5+hi((-2)*3)]=-1+[-5+hi((-6))]|&#215;' txt='Multiply in the brackets first.'/><step eqn='-1+[hi(-5+(-6))]=-1+hi((-11))' txt='Add in the brackets.'/><step eqn='hi(-1+(-11))=hi(-12)' txt='Finally, add.'/></item><item><question eqn='5+[-3+4*4]|&#215;'/><answer val='18'/><step eqn='5+[-3+hi(4*4)]=5+[-3+hi(16)]|&#215;' txt='Multiply in the brackets first.'/><step eqn='5+[hi(-3+16)]=5+hi(13)' txt='Add in the brackets.'/><step eqn='hi(5+13)=hi(18)' txt='Finally, add.'/></item><item><question eqn='10+[5+(-3)*5]|&#215;'/><answer val='0'/><step eqn='10+[5+hi((-3)*5)]=10+[5+hi((-15))]|&#215;' txt='Multiply in the brackets first.'/><step eqn='10+[hi(5+(-15))]=10+hi((-10))' txt='Add in the brackets.'/><step eqn='hi(10+(-10))=hi(0)' txt='Finally, add.'/></item><item><question eqn='{(14&#247;2)^2}-20*2|&#215;'/><answer val='9'/><step eqn='{hi((14&#247;2))^2}-20*2={hi(7)^2}-20*2|&#215;' txt='Simplify parentheses first.'/><step eqn='{hi(7^2)}-20*2=hi(49)-20*2|&#215;' txt='Evaluate the exponent.'/><step eqn='49-hi(20*2)=49-hi(40)|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(49-40)=hi(9)' txt='Add and subtract from left to right.'/></item><item><question eqn='{(35&#247;5)^2}-9*5|&#215;'/><answer val='4'/><step eqn='{hi((35&#247;5))^2}-9*5={hi(7)^2}-9*5|&#215;' txt='Simplify parentheses first.'/><step eqn='{hi(7^2)}-9*5=hi(49)-9*5|&#215;' txt='Evaluate the exponent.'/><step eqn='49-hi(9*5)=49-hi(45)|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(49-45)=hi(4)' txt='Add and subtract from left to right.'/></item><item><question eqn='{(54&#247;9)^2}-8*4|&#215;'/><answer val='4'/><step eqn='{hi((54&#247;9))^2}-8*4={hi(6)^2}-8*4|&#215;' txt='Simplify parentheses first.'/><step eqn='{hi(6^2)}-8*4=hi(36)-8*4|&#215;' txt='Evaluate the exponent.'/><step eqn='36-hi(8*4)=36-hi(32)|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(36-32)=hi(4)' txt='Add and subtract from left to right.'/></item></data>";
    var topic_data_5 = "<data><item><question eqn='({{2*8}/{1+7}}+2)^2|&#215;'/><answer val='16'/><step eqn='({hi({2*8})/{1+7}}+2)^2=({hi({16})/{1+7}}+2)^2|&#215;' txt='Simplify in the parentheses first.|The fraction bar is a grouping symbol.|Start with the numerator.'/><step eqn='({{16}/hi({1+7})}+2)^2=({{16}/hi({8})}+2)^2' txt='Next simplify the denominator.'/><step eqn='({hi({16}/{8})}+2)^2=(hi(2+2))^2' txt='Reduce the fraction(divide).'/><step eqn='hi((2+2))^2=hi(4)^2' txt='Add to finish simplifying the parentheses.'/><step eqn='hi(4^2)=hi(16)' txt='Finally, evaluate the exponent.'/></item><item><question eqn='{60*{1/2}*3}-{10*7}|&#215;'/><answer val='20'/><step eqn='{60*{1/2}*3}-{10*7}=|&#215;' txt='There are no grouping symbols or exponents.'/><step eqn='{hi(60*{1/2})*3}-{10*7}=hi(30)*3-10*7|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(30*3)-10*7=hi(90)-10*7|&#215;' txt='Multiply and divide from left to right.'/><step eqn='90-hi(10*7)=90-hi(70)|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(90-70)=hi(20)' txt='Add and subtract from left to right.'/></item><item><question eqn='{{1/2}*{2/3}}&#247;({1/4}+{3/4})|&#215;'/><answer val='1/3'/><step eqn='{{1/2}*{2/3}}&#247;hi(({1/4}+{3/4}))={1/2}*{2/3}&#247;hi(1)|&#215;' txt='Add in the parentheses first.'/><step eqn='hi({{1/2}*{2/3}})&#247;1=hi({1/3})&#247;1|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi({1/3}&#247;1)=hi(1/3)' txt='Multiply and divide from left to right.'/></item><item><question eqn='3+[(12+3)*2]&#247;10|&#215;'/><answer val='6'/><step eqn='3+[hi((12+3))*2]&#247;10=3+[hi(15)*2]&#247;10|&#215;' txt='Start in the innermost parentheses first.'/><step eqn='3+[hi(15*2)]&#247;10=3+hi(30)&#247;10|&#215;' txt='Then simplify the outer parentheses.'/><step eqn='3+30&#247;10=' txt='There are no exponents.'/><step eqn='3+hi(30&#247;10)=3+hi(3)' txt='Multiply and divide from left to right.'/><step eqn='hi(3+3)=hi(6)' txt='Add and subtract from left to right.'/></item><item><question eqn='{{5+7}/{6*2}}-9|&#215;'/><answer val='-8'/><step eqn='{{hi(5+7)}/{6*2}}-9={{hi(12)}/{6*2}}-9|&#215;' txt='The fraction bar acts a grouping symbol.|Start by simplifying the numerator.'/><step eqn='{{12}/{hi(6*2)}}-9={12/hi(12)}-9|&#215;' txt='Now simplify the denominator.'/><step eqn='{hi(12/12)}-9=hi(1)-9' txt='Next, do the division.'/><step eqn='hi(1-9)=hi(-8)' txt='Finally, do the subtraction.'/></item><item><question eqn='{(8-{3^2}/2)^2}+{1/2}'/><answer val='3/4'/><step eqn='{(8-{hi(3^2)}/2)^2}+{1/2}={(8-{hi(9)}/2)^2}+{1/2}' txt='Start in the parentheses.|Simplify the numerator first|Start with the exponent.'/><step eqn='{({hi(8-9)}/2)^2}+{1/2}={({hi(-1)}/2)^2}+{1/2}' txt='Finish simplifying the numerator.'/><step eqn='{hi((-1/2))^2}+{1/2}={hi(1/4)}+{1/2}' txt='Now square the fraction.|Remember that the product of two negatives|is positive.'/><step eqn='hi({1/4}+{1/2})=hi(3/4)' txt='Finally, add the fractions.'/></item><item><question eqn='{(-3)^2}-{4^2}+5*6|&#215;'/><answer val='23'/><step eqn='{hi((-3)^2)}-{4^2}+5*6={hi(9)}-{{4^2}+5*6}|&#215;' txt='Evaluate the first exponent.|Remember that the product of two negatives|is positive.'/><step eqn='9-{hi(4^2)}+5*6=9-{hi(16)}+5*6|&#215;' txt='Evaluate the second exponent.|Here the minus sign is not squared.'/><step eqn='9-16+hi(5*6)=9-16+hi(30)|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(9-16)+30=hi(-7)+30' txt='Add and subtract from left to right.'/><step eqn='hi(-7+30)=hi(23)' txt='Add and subtract from left to right.'/></item><item><question eqn='{2^2*(5-3)}+1|&#215;'/><answer val='17'/><step eqn='{2^2*hi((5-3))}+1={2^2*hi(2)}+1|&#215;' txt='Simplify the parentheses first.'/><step eqn='{2^hi(2*2)}+1={2^hi(4)}+1|&#215;' txt='Next, multiply in the exponent.'/><step eqn='{hi(2^4)}+1=hi(16)+1' txt='Evaluate the exponent.'/><step eqn='hi(16+1)=hi(17)' txt='Add.'/></item><item><question eqn='{5/{3*8-9}}*2|&#215;'/><answer val='2/3'/><step eqn='{5/{hi(3*8)-9}}*2={5/{hi(24)-9}}*2|&#215;' txt='The fraction bar acts as a grouping symbol.|Simplify in the denominator first.|Multiply and divide from left to right.'/><step eqn='{5/{hi(24-9)}}*2={5/hi(15)}*2|&#215;' txt='Subtract in the denominator.'/><step eqn='{hi(5/15)}*2=hi({1/3})*2|&#215;' txt='Simplify the fraction.'/><step eqn='hi({1/3}*2)=hi(2/3)|&#215;' txt='Multiply.'/></item><item><question eqn='{-3^3}/{(-9)^2}'/><answer val='-1/3'/><step eqn='{-hi(3^3)}/{(-9)^2}={-hi(27)}/{(-9)^2}' txt='The fraction bar acts as a grouping symbol.|Start by simplifying the numerator.|Evaluate the exponent before applying the negative sign.'/><step eqn='{-27}/{hi((-9)^2)}=-27/hi(81)' txt='In the denominator you are squaring a negative number.|So the result is positive.'/><step eqn='hi(-27/81)=hi(-1/3)' txt='Simplify the fraction.'/></item><item><question eqn='2.31*2+1.5^2|&#215;'/><answer val='6.87'/><step eqn='2.31*2+1.5^2=|&#215;' txt='There are no grouping symbols.'/><step eqn='2.31*2+hi(1.5^2)=2.31*2+hi(2.25)|&#215;' txt='Evaluate the exponent.'/><step eqn='hi(2.31*2)+2.25=hi(4.62)+2.25|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(4.62+2.25)=hi(6.87)' txt='Add and subtract from left to right.'/></item><item><question eqn='4*3*2*1-12&#247;6|&#183;'/><answer val='22'/><step eqn='4*3*2*1-12&#247;6=|&#183;' txt='There are no grouping symbols or exponents.'/><step eqn='hi(4*3*2*1)-12&#247;6=hi(24)-12&#247;6|&#183;' txt='Multiply and divide from left to right.'/><step eqn='24-hi(12&#247;6)=24-hi(2)' txt='Multiply and divide from left to right.'/><step eqn='hi(24-2)=hi(22)' txt='Add and subtract from left to right.'/></item><item><question eqn='-{1/2}*{[5+(3*7)]+14}|&#215;'/><answer val='1'/><step eqn='-{1/2}*{[5+hi((3*7))]+14}=-{1/2}*{[5+hi(21)]+14}|&#215;' txt='Start in the innermost parentheses.'/><step eqn='-{1/2}*{hi([5+21])+14}=-{1/2}*{hi((26))+14}|&#215;' txt='Simplify the outer parentheses.'/><step eqn='hi(-{1/2}*{(26)})+14=hi(-13)+14|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(-13+14)=hi(1)' txt='Add and subtract from left to right.'/></item><item><question eqn='{(5*2)^3}-9*100|&#215;'/><answer val='100'/><step eqn='{hi((5*2))^3}-9*100={hi(10)^3}-9*100|&#215;' txt='Simplify parentheses first.'/><step eqn='{hi(10^3)}-9*100=hi(1000)-9*100|&#215;' txt='Evaluate the exponent.'/><step eqn='1000-hi(9*100)=1000-hi(900)|&#215;' txt='Multiply and divide from left to right.'/><step eqn='hi(1000-900)=hi(100)' txt='Add and subtract from left to right.'/></item><item><question eqn='4+[{(-2)^4}/{-4^2}]'/><answer val='3'/><step eqn='4+[{hi((-2)^4)}/{-4^2}]=4+[{hi(16)}/{-4^2}]' txt='Start in brackets first.|The fraction bar is also a grouping symbol.|Start by simplifying the numerator.|Raising a negative number to a even numerator|gives a positive result.'/><step eqn='4+[{16}/{-hi(4^2)}]=4+[{16}/{-hi(16)}]' txt='In the denominator, evaluate the exponent |before applying negative sign.'/><step eqn='4+[hi(16/-16)]=4+(hi(-1))' txt='Simplify the fraction(divide).'/><step eqn='hi(4+(-1))=hi(3)' txt='Add and subtract from left to right.'/></item></data>";
    //
    var topic_data_6 = "6/8,22/55,24/96,2/5,3/20,12/28,26/39,6/15,14/35,200/250,8/10,75/300,3/9,19/30,2/7,28/40,5/6,4/17,5/8,9/11,7/12,8/15,15/18,6/10,9/21,44/60,32/80,27/99,45/100,38/100,42/50,10/25,9/14,16/36,16/28,6/12,9/18,6/24,15/20,32/40,17/34,23/31,7/51,39/52,50/90,36/120,108/120,70/77,28/100,1/16,3/25,44/48,32/56,24/54,20/64,48/60,54/72,36/45,49/70,32/44,150/225,80/340";
    //
    var topic_data_7 = "<data><item lexp='1/200' rexp='3/50' val='%3C' expType='***'/><item lexp='1/200' rexp='1%' val='%3C' expType=''/><item lexp='1/121' rexp='1/61' val='%3C' expType='*'/><item lexp='1/101' rexp='1/100' val='%3C' expType=''/><item lexp='1/100' rexp='0.001' val='%3E' expType=''/><item lexp='1/100' rexp='0.01' val='%3D' expType=''/><item lexp='1/100' rexp='1%' val='%3D' expType=''/><item lexp='1/100' rexp='1/20' val='%3C' expType='*'/><item lexp='1/100' rexp='0.1' val='%3C' expType=''/><item lexp='2/55' rexp='1/55' val='%3E' expType='**'/><item lexp='2/55' rexp='3/55' val='%3C' expType='**'/><item lexp='1/50' rexp='2%' val='%3D' expType=''/><item lexp='2%' rexp='0.2' val='%3C' expType=''/><item lexp='2%' rexp='0.05' val='%3C' expType=''/><item lexp='1/50' rexp='3%' val='%3C' expType=''/><item lexp='1/25' rexp='25%' val='%3C' expType=''/><item lexp='1/25' rexp='4%' val='%3D' expType=''/><item lexp='1/20' rexp='0.02' val='%3E' expType=''/><item lexp='1/20' rexp='3%' val='%3E' expType=''/><item lexp='1/20' rexp='20%' val='%3C' expType=''/><item lexp='1/20' rexp='5%' val='%3D' expType=''/><item lexp='5%' rexp='0.05' val='%3D' expType=''/><item lexp='5%' rexp='0.5' val='%3C' expType=''/><item lexp='1/20' rexp='0.05' val='%3D' expType=''/><item lexp='1/20' rexp='0.2' val='%3C' expType=''/><item lexp='2/22' rexp='3/22' val='%3C' expType='**'/><item lexp='2/22' rexp='1/11' val='%3D' expType='****'/><item lexp='7%' rexp='0.1' val='%3C' expType=''/><item lexp='1/10' rexp='0.01' val='%3E' expType=''/><item lexp='1/10' rexp='1/5' val='%3C' expType='*'/><item lexp='1/10' rexp='5/11' val='%3C' expType=''/><item lexp='1/10' rexp='0.1' val='%3D' expType=''/><item lexp='10%' rexp='0.1' val='%3D' expType=''/><item lexp='10%' rexp='0.10' val='%3D' expType=''/><item lexp='1/10' rexp='8%' val='%3E' expType=''/><item lexp='1/10' rexp='11%' val='%3C' expType=''/><item lexp='1/10' rexp='3/30' val='%3D' expType=''/><item lexp='1/10' rexp='0.5' val='%3C' expType=''/><item lexp='12%' rexp='0.1' val='%3E' expType=''/><item lexp='12%' rexp='0.12' val='%3D' expType=''/><item lexp='12%' rexp='0.2' val='%3C' expType=''/><item lexp='1/8' rexp='0.1' val='%3E' expType=''/><item lexp='1/8' rexp='1/9' val='%3E' expType='*'/><item lexp='1/8' rexp='0.125' val='%3D' expType=''/><item lexp='1/8' rexp='1/6' val='%3C' expType='*'/><item lexp='1/8' rexp='15%' val='%3C' expType=''/><item lexp='1/8' rexp='20%' val='%3C' expType=''/><item lexp='1/8' rexp='10%' val='%3E' expType=''/><item lexp='1/8' rexp='0.25' val='%3C' expType=''/><item lexp='13%' rexp='0.26' val='%3C' expType=''/><item lexp='1/5' rexp='0.1' val='%3E' expType=''/><item lexp='1/5' rexp='2/10' val='%3D' expType='****'/><item lexp='1/5' rexp='0.2' val='%3D' expType=''/><item lexp='1/5' rexp='0.20' val='%3D' expType=''/><item lexp='20%' rexp='0.2' val='%3D' expType=''/><item lexp='20%' rexp='0.15' val='%3E' expType=''/><item lexp='20%' rexp='0.7' val='%3C' expType=''/><item lexp='0.222' rexp='200%' val='%3C' expType=''/><item lexp='1/5' rexp='3/20' val='%3E' expType=''/><item lexp='1/5' rexp='0.3' val='%3C' expType=''/><item lexp='2/9' rexp='2/7' val='%3C' expType='*'/><item lexp='2/9' rexp='2/11' val='%3E' expType='*'/><item lexp='2/7' rexp='5/7' val='%3C' expType='**'/><item lexp='1/4' rexp='0.2' val='%3E' expType=''/><item lexp='1/4' rexp='0.25' val='%3D' expType=''/><item lexp='1/4' rexp='20%' val='%3E' expType=''/><item lexp='1/4' rexp='29%' val='%3C' expType=''/><item lexp='1/4' rexp='0.4' val='%3C' expType=''/><item lexp='25%' rexp='0.21' val='%3E' expType=''/><item lexp='25%' rexp='0.025' val='%3E' expType=''/><item lexp='25%' rexp='0.26' val='%3C' expType=''/><item lexp='2/7' rexp='1/7' val='%3E' expType='**'/><item lexp='2/7' rexp='5/7' val='%3C' expType='**'/><item lexp='2/7' rexp='6/21' val='%3D' expType='****'/><item lexp='3/10' rexp='0.03' val='%3E' expType=''/><item lexp='3/10' rexp='1/4' val='%3E' expType=''/><item lexp='3/10' rexp='0.3' val='%3D' expType=''/><item lexp='3/10' rexp='30%' val='%3D' expType=''/><item lexp='3/10' rexp='27%' val='%3E' expType=''/><item lexp='3/10' rexp='3%' val='%3E' expType=''/><item lexp='3/10' rexp='41%' val='%3C' expType=''/><item lexp='3/10' rexp='30/100' val='%3D' expType='****'/><item lexp='3/10' rexp='6/20' val='%3D' expType='****'/><item lexp='3/10' rexp='0.7' val='%3C' expType=''/><item lexp='1/3' rexp='0.3' val='%3E' expType=''/><item lexp='1/3' rexp='3%' val='%3E' expType=''/><item lexp='1/3' rexp='30%' val='%3E' expType=''/><item lexp='1/3' rexp='33%' val='%3E' expType=''/><item lexp='1/3' rexp='3/9' val='%3D' expType='****'/><item lexp='1/3' rexp='0.4' val='%3C' expType=''/><item lexp='1/3' rexp='35%' val='%3C' expType=''/><item lexp='1/3' rexp='70%' val='%3C' expType=''/><item lexp='35%' rexp='0.35' val='%3D' expType=''/><item lexp='35%' rexp='0.3' val='%3E' expType=''/><item lexp='37/100' rexp='0.4' val='%3C' expType=''/><item lexp='2/5' rexp='0.5' val='%3C' expType=''/><item lexp='2/5' rexp='0.4' val='%3D' expType=''/><item lexp='2/5' rexp='6/15' val='%3D' expType='****'/><item lexp='2/5' rexp='0.39' val='%3E' expType=''/><item lexp='2/5' rexp='1/4' val='%3E' expType=''/><item lexp='2/5' rexp='4/10' val='%3D' expType='****'/><item lexp='2/5' rexp='40%' val='%3D' expType=''/><item lexp='2/5' rexp='25%' val='%3E' expType=''/><item lexp='2/5' rexp='0.25' val='%3E' expType=''/><item lexp='9/20' rexp='0.18' val='%3E' expType=''/><item lexp='9/20' rexp='0.45' val='%3D' expType=''/><item lexp='9/20' rexp='1/2' val='%3C' expType=''/><item lexp='9/20' rexp='1' val='%3C' expType='x'/><item lexp='1/2' rexp='0.2' val='%3E' expType=''/><item lexp='1/2' rexp='0.5' val='%3D' expType=''/><item lexp='1/2' rexp='50%' val='%3D' expType=''/><item lexp='1/2' rexp='20%' val='%3E' expType=''/><item lexp='1/2' rexp='82%' val='%3C' expType=''/><item lexp='1/2' rexp='100%' val='%3C' expType=''/><item lexp='1/2' rexp='150%' val='%3C' expType=''/><item lexp='1/2' rexp='1' val='%3C' expType='x'/><item lexp='1/2' rexp='200%' val='%3C' expType=''/><item lexp='1/2' rexp='0.75' val='%3C' expType=''/><item lexp='50%' rexp='0.05' val='%3E' expType=''/><item lexp='50%' rexp='0.5' val='%3D' expType=''/><item lexp='50%' rexp='0.500' val='%3D' expType=''/><item lexp='50%' rexp='0.55' val='%3C' expType=''/><item lexp='55%' rexp='0.6' val='%3C' expType=''/><item lexp='55%' rexp='0.99' val='%3C' expType=''/><item lexp='57/100' rexp='2/3' val='%3C' expType=''/><item lexp='3/5' rexp='0.6' val='%3D' expType=''/><item lexp='3/5' rexp='60%' val='%3D' expType=''/><item lexp='3/5' rexp='6/10' val='%3D' expType='****'/><item lexp='3/5' rexp='35%' val='%3E' expType=''/><item lexp='3/5' rexp='53%' val='%3E' expType=''/><item lexp='3/5' rexp='80%' val='%3C' expType=''/><item lexp='3/5' rexp='4/8' val='%3E' expType=''/><item lexp='3/5' rexp='15/25' val='%3D' expType='****'/><item lexp='3/5' rexp='4/5' val='%3C' expType='**'/><item lexp='60%' rexp='0.66' val='%3C' expType=''/><item lexp='60%' rexp='0.6' val='%3D' expType=''/><item lexp='5/8' rexp='0.5' val='%3E' expType=''/><item lexp='5/8' rexp='0.6' val='%3E' expType=''/><item lexp='5/8' rexp='50%' val='%3E' expType=''/><item lexp='5/8' rexp='1/3' val='%3E' expType=''/><item lexp='5/8' rexp='0.625' val='%3D' expType=''/><item lexp='5/8' rexp='3/4' val='%3C' expType=''/><item lexp='5/8' rexp='0.85' val='%3C' expType=''/><item lexp='5/7' rexp='5/9' val='%3E' expType='*'/><item lexp='5/7' rexp='5/6' val='%3C' expType='*'/><item lexp='5/7' rexp='1' val='%3C' expType='x'/><item lexp='5/7' rexp='4/9' val='%3E' expType='***'/><item lexp='13/20' rexp='0.65' val='%3D' expType=''/><item lexp='2/3' rexp='0.7' val='%3C' expType=''/><item lexp='2/3' rexp='70%' val='%3C' expType=''/><item lexp='2/3' rexp='100%' val='%3C' expType=''/><item lexp='2/3' rexp='60%' val='%3E' expType=''/><item lexp='2/3' rexp='66%' val='%3E' expType=''/><item lexp='2/3' rexp='0.65' val='%3E' expType=''/><item lexp='2/3' rexp='0.66R' val='%3D' expType=''/><item lexp='2/3' rexp='44/66' val='%3D' expType='****'/><item lexp='2/3' rexp='1/2' val='%3E' expType=''/><item lexp='7/10' rexp='0.07' val='%3E' expType=''/><item lexp='7/10' rexp='0.7' val='%3D' expType=''/><item lexp='7/10' rexp='14/20' val='%3D' expType='****'/><item lexp='7/10' rexp='3/4' val='%3C' expType=''/><item lexp='7/10' rexp='1.07' val='%3C' expType=''/><item lexp='7/10' rexp='70%' val='%3D' expType=''/><item lexp='72%' rexp='0.72' val='%3D' expType=''/><item lexp='73%' rexp='0.8' val='%3C' expType=''/><item lexp='73%' rexp='0.444' val='%3E' expType=''/><item lexp='3/4' rexp='4/5' val='%3C' expType=''/><item lexp='3/4' rexp='0.995' val='%3C' expType=''/><item lexp='3/4' rexp='100%' val='%3C' expType=''/><item lexp='3/4' rexp='0.75' val='%3D' expType=''/><item lexp='3/4' rexp='75/100' val='%3D' expType='****'/><item lexp='3/4' rexp='0.34' val='%3E' expType=''/><item lexp='3/4' rexp='0.55' val='%3E' expType=''/><item lexp='3/4' rexp='75%' val='%3D' expType=''/><item lexp='3/4' rexp='90%' val='%3C' expType=''/><item lexp='3/4' rexp='60%' val='%3E' expType=''/><item lexp='77/100' rexp='0.7' val='%3E' expType=''/><item lexp='4/5' rexp='0.95' val='%3C' expType=''/><item lexp='4/5' rexp='0.8' val='%3D' expType=''/><item lexp='4/5' rexp='80%' val='%3D' expType=''/><item lexp='4/5' rexp='90%' val='%3C' expType=''/><item lexp='4/5' rexp='40%' val='%3E' expType=''/><item lexp='4/5' rexp='80/100' val='%3D' expType='****'/><item lexp='4/5' rexp='16/25' val='%3E' expType=''/><item lexp='4/5' rexp='2/3' val='%3E' expType=''/><item lexp='80%' rexp='0.8' val='%3D' expType=''/><item lexp='80%' rexp='0.08' val='%3E' expType=''/><item lexp='80%' rexp='0.999' val='%3C' expType=''/><item lexp='7/8' rexp='7/9' val='%3E' expType='*'/><item lexp='7/8' rexp='0.875' val='%3D' expType=''/><item lexp='7/8' rexp='0.995' val='%3C' expType=''/><item lexp='7/8' rexp='1' val='%3C' expType='x'/><item lexp='88%' rexp='1.1' val='%3C' expType=''/><item lexp='9/10' rexp='0.09' val='%3E' expType=''/><item lexp='9/10' rexp='0.9' val='%3D' expType=''/><item lexp='9/10' rexp='90%' val='%3D' expType=''/><item lexp='9/10' rexp='50%' val='%3E' expType=''/><item lexp='9/10' rexp='99%' val='%3C' expType=''/><item lexp='9/10' rexp='0.5' val='%3E' expType=''/><item lexp='9/10' rexp='11/10' val='%3C' expType='**'/><item lexp='90%' rexp='0.9' val='%3D' expType=''/><item lexp='90%' rexp='0.95' val='%3C' expType=''/><item lexp='90%' rexp='0.75' val='%3E' expType=''/><item lexp='90%' rexp='0.875' val='%3E' expType=''/><item lexp='49/50' rexp='1' val='%3C' expType='x'/><item lexp='49/50' rexp='0.49' val='%3E' expType=''/><item lexp='49/50' rexp='0.98' val='%3D' expType=''/><item lexp='99/100' rexp='0.777' val='%3E' expType=''/><item lexp='99/100' rexp='0.999' val='%3C' expType=''/><item lexp='1' rexp='100%' val='%3D' expType=''/><item lexp='9/9' rexp='1.00' val='%3D' expType='****'/><item lexp='7/7' rexp='1.0' val='%3D' expType='****'/><item lexp='1.1' rexp='100%' val='%3E' expType=''/><item lexp='8/7' rexp='1' val='%3E' expType='xx'/><item lexp='7/6' rexp='1' val='%3E' expType='xx'/><item lexp='6/5' rexp='1.2' val='%3D' expType=''/><item lexp='6/5' rexp='120%' val='%3D' expType=''/><item lexp='6/5' rexp='1.5' val='%3C' expType=''/><item lexp='5/4' rexp='1.25' val='%3D' expType=''/><item lexp='5/4' rexp='1' val='%3E' expType='xx'/><item lexp='5/4' rexp='1.5' val='%3C' expType=''/><item lexp='5/4' rexp='150%' val='%3C' expType=''/><item lexp='5/4' rexp='2' val='%3C' expType=''/><item lexp='4/3' rexp='1' val='%3E' expType='xx'/><item lexp='1.5' rexp='200%' val='%3C' expType=''/><item lexp='1.5' rexp='150%' val='%3D' expType=''/><item lexp='3/2' rexp='1.5' val='%3D' expType=''/><item lexp='3/2' rexp='2/2' val='%3E' expType=''/><item lexp='3/2' rexp='1.25' val='%3E' expType=''/><item lexp='3/2' rexp='175%' val='%3C' expType=''/><item lexp='3/2' rexp='2' val='%3C' expType=''/><item lexp='7/4' rexp='2' val='%3C' expType=''/><item lexp='7/4' rexp='1.75' val='%3D' expType=''/><item lexp='7/4' rexp='150%' val='%3E' expType=''/><item lexp='7/4' rexp='190%' val='%3C' expType=''/><item lexp='2.2' rexp='200%' val='%3E' expType=''/><item lexp='1/3' rexp='0.33R' val='%3D' expType=''/></data>";
    var topic_data_8 = "1 1/2,2 1/2,3 1/2,4 1/2,5 1/2,6 1/2,7 1/2,8 1/2,9 1/2,10 1/2,1 1/3,1 2/3,2 1/3,2 2/3,3 1/3,3 2/3,4 1/3,4 2/3,5 1/3,5 2/3,6 1/3,6 2/3,7 1/3,7 2/3,8 1/3,8 2/3,9 1/3,9 2/3,10 1/3,10 2/3,1 1/4,1 3/4,2 1/4,2 3/4,3 1/4,3 3/4,4 1/4,4 3/4,5 1/4,5 3/4,6 1/4,6 3/4,7 1/4,7 3/4,8 1/4,8 3/4,9 1/4,9 3/4,10 1/4,10 3/4,1 1/5,1 2/5,1 3/5,1 4/5,2 1/5,2 2/5,2 3/5,2 4/5,3 1/5,3 2/5,3 3/5,3 4/5,4 1/5,4 2/5,4 3/5,4 4/5,5 1/5,5 2/5,5 3/5,5 4/5,6 1/5,6 2/5,6 3/5,6 4/5,7 1/5,7 2/5,7 3/5,7 4/5,8 1/5,8 2/5,8 3/5,8 4/5,9 1/5,9 2/5,9 3/5,9 4/5,10 1/5,10 2/5,10 3/5,10 4/5,1 1/6,1 5/6,2 1/6,2 5/6,3 1/6,3 5/6,4 1/6,4 5/6,5 1/6,5 5/6,6 1/6,6 5/6,7 1/6,7 5/6,8 1/6,8 5/6,9 1/6,9 5/6,10 1/6,10 5/6,1 1/7,1 2/7,1 3/7,1 4/7,1 5/7,1 6/7,2 1/7,2 2/7,2 3/7,2 4/7,2 5/7,2 6/7,3 1/7,3 2/7,3 3/7,3 4/7,3 5/7,3 6/7,4 1/7,4 2/7,4 3/7,4 4/7,4 5/7,4 6/7,5 1/7,5 2/7,5 3/7,5 4/7,5 5/7,5 6/7,6 1/7,6 2/7,6 3/7,6 4/7,6 5/7,6 6/7,7 1/7,7 2/7,7 3/7,7 4/7,7 5/7,7 6/7,8 1/7,8 2/7,8 3/7,8 4/7,8 5/7,8 6/7,9 1/7,9 2/7,9 3/7,9 4/7,9 5/7,9 6/7,10 1/7,10 2/7,10 3/7,10 4/7,10 5/7,10 6/7,1 1/8,1 3/8,1 5/8,1 7/8,2 1/8,2 3/8,2 5/8,2 7/8,3 1/8,3 3/8,3 5/8,3 7/8,4 1/8,4 3/8,4 5/8,4 7/8,5 1/8,5 3/8,5 5/8,5 7/8,6 1/8,6 3/8,6 5/8,6 7/8,7 1/8,7 3/8,7 5/8,7 7/8,8 1/8,8 3/8,8 5/8,8 7/8,9 1/8,9 3/8,9 5/8,9 7/8,10 1/8,10 3/8,10 5/8,10 7/8,1 1/9,1 2/9,1 4/9,1 5/9,1 7/9,1 8/9,2 1/9,2 2/9,2 4/9,2 5/9,2 7/9,2 8/9,3 1/9,3 2/9,3 4/9,3 5/9,3 7/9,3 8/9,4 1/9,4 2/9,4 4/9,4 5/9,4 7/9,4 8/9,5 1/9,5 2/9,5 4/9,5 5/9,5 7/9,5 8/9,6 1/9,6 2/9,6 4/9,6 5/9,6 7/9,6 8/9,7 1/9,7 2/9,7 4/9,7 5/9,7 7/9,7 8/9,8 1/9,8 2/9,8 4/9,8 5/9,8 7/9,8 8/9,9 1/9,9 2/9,9 4/9,9 5/9,9 7/9,9 8/9,10 1/9,10 2/9,10 4/9,10 5/9,10 7/9,10 8/9,1 1/10,1 3/10,1 7/10,1 9/10,2 1/10,2 3/10,2 7/10,2 9/10,3 1/10,3 3/10,3 7/10,3 9/10,4 1/10,4 3/10,4 7/10,4 9/10,5 1/10,5 3/10,5 7/10,5 9/10,6 1/10,6 3/10,6 7/10,6 9/10,7 1/10,7 3/10,7 7/10,7 9/10,8 1/10,8 3/10,8 7/10,8 9/10,9 1/10,9 3/10,9 7/10,9 9/10,10 1/10,10 3/10,10 7/10,10 9/10";
    var topic_data_10 = "1/11|7/12, 7/9|7/12, 4/7|7/8, 1/12|2/9, 1/10|4/11, 1/10|3/8, 1/4|1/5, 6/7|8/11, 1/2|9/11, 3/10|7/8, 3/8|3/10, 1/11|5/8, 2/7|5/6, 4/5|4/9, 3/4|4/11, 1/5|6/7, 1/4|1/8, 1/6|5/11, 3/4|8/9, 7/9|8/11, 6/11|9/10, 1/5|8/9, 1/6|7/11, 2/9|3/4, 3/4|5/11, 1/8|4/5, 7/12|8/11, 5/9|7/9, 3/5|8/9, 1/2|5/6, 1/3|4/11, 2/11|7/9, 1/2|3/11, 1/3|5/8, 3/11|5/8, 1/3|7/12, 2/9|6/11, 3/4|3/8, 2/5|3/5, 2/11|5/12, 4/11|5/8, 1/2|1/12, 6/11|8/9, 1/5|6/11, 4/9|6/11, 1/10|2/9, 1/9|2/11, 2/3|7/8, 2/3|3/10, 1/10|8/11, 1/7|7/8, 5/7|5/9, 2/5|5/12, 2/9|4/5, 1/6|9/11, 8/9|9/10, 1/5|1/6, 1/12|3/5, 2/11|9/10, 1/8|2/9, 1/5|3/10, 2/9|4/11, 3/7|11/12, 4/7|8/9, 3/8|5/9, 3/4|3/5, 2/3|11/12, 6/7|11/12, 3/5|3/11, 1/4|3/5, 2/3|4/11, 2/5|6/7, 1/9|9/10, 1/6|1/7, 1/10|5/8, 1/5|2/3, 1/2|8/11, 1/5|5/12, 1/4|3/4, 1/4|7/10, 5/7|8/11, 1/10|1/11, 6/11|7/12, 1/12|4/5, 5/8|9/10, 1/11|6/7, 5/9|9/10, 1/9|1/12, 5/9|7/10, 1/6|1/10, 1/12|7/12, 1/12|2/5, 1/10|7/8, 3/7|3/11, 2/9|3/11, 1/2|4/7, 1/5|3/4, 2/9|8/9, 3/11|9/10, 7/10|9/10, 1/7|1/8, 1/3|3/5, 7/11|8/9, 1/2|7/11, 5/9|7/12, 3/4|3/10, 1/6|7/10, 6/7|8/9, 1/4|3/10, 1/3|2/5, 5/7|7/10, 3/4|7/12, 1/2|2/7, 2/9|4/9, 5/12|11/12, 1/6|1/8, 1/11|2/5, 3/4|9/11, 5/6|5/12, 3/4|11/12, 1/8|5/11, 2/3|2/11, 3/10|4/5, 1/10|3/7, 1/12|9/11, 3/5|7/10, 1/5|7/9, 1/4|7/11, 7/9|9/10, 5/12|7/9, 1/8|7/12, 1/7|1/11, 2/7|3/5, 3/11|4/11, 1/11|4/11, 1/5|4/9, 1/5|3/7, 3/10|9/10, 1/6|3/4, 1/7|5/8, 1/11|5/7, 2/11|4/5, 2/11|3/11, 8/11|9/10, 1/11|1/12, 6/7|9/10, 2/11|7/12, 3/5|3/7, 5/12|7/12, 1/6|3/5, 1/10|11/12, 4/11|7/11, 2/5|3/4, 1/9|5/7, 3/11|4/7, 1/8|11/12, 1/12|5/6, 1/8|6/7, 1/9|5/12, 4/11|6/11, 2/7|5/7, 2/11|8/9, 5/7|5/12, 1/6|5/7, 1/12|3/8, 1/11|5/11, 1/12|8/11, 5/8|7/10, 5/6|8/11, 3/4|5/8, 2/7|5/8, 1/5|2/9, 1/2|1/11, 3/7|7/8, 3/5|3/10, 1/3|7/11, 1/6|5/8, 2/9|3/8, 3/8|4/11, 1/9|2/3, 3/11|5/7, 1/4|8/9, 4/9|7/9, 3/11|8/9, 3/11|5/12, 3/7|5/12, 1/11|7/11, 2/3|4/5, 1/11|9/10, 8/11|9/11, 2/11|5/11, 2/3|2/7, 1/3|8/9, 4/5|9/10, 4/7|5/11, 1/12|11/12, 2/3|7/10, 4/11|5/6, 1/11|2/7, 1/4|2/7, 5/8|6/7, 1/11|8/9, 1/8|9/11, 1/4|4/9, 4/7|5/8, 2/7|2/9, 3/8|4/9, 1/4|1/6, 2/9|3/10, 8/9|11/12, 1/2|7/8, 2/9|5/12, 3/4|8/11, 4/7|6/11, 1/12|4/9, 1/11|7/10, 5/8|7/9, 5/7|6/11, 1/7|5/11, 1/5|1/12, 1/7|2/5, 4/9|7/8, 1/10|9/10, 1/11|5/9, 8/9|9/11, 3/11|6/7, 1/5|1/10, 3/8|4/5, 2/7|6/7, 1/12|7/8, 2/5|3/10, 4/5|7/11, 3/4|7/8, 2/5|5/8, 1/5|9/10, 9/10|11/12, 4/5|7/9, 4/11|7/10, 3/7|4/11, 4/7|5/7, 1/5|1/11, 1/12|5/12, 5/6|7/10, 1/9|5/6, 1/8|3/11, 1/7|4/9, 3/5|9/11, 2/3|7/9, 1/11|5/6, 1/9|3/8, 1/8|4/9, 1/3|5/7, 1/5|1/7, 1/2|1/7, 7/12|8/9, 5/9|8/9, 5/8|7/12, 3/11|4/9, 1/2|1/5, 2/3|5/8, 3/5|6/11, 4/9|5/8, 2/5|2/11, 5/8|6/11, 1/2|1/10, 3/4|6/11, 1/5|2/5, 3/8|3/11, 3/4|5/9, 3/11|6/11, 5/8|9/11, 3/10|8/11, 2/9|7/10, 1/11|2/11, 1/2|3/8, 2/9|7/9, 2/5|11/12, 4/5|5/7, 2/3|9/11, 1/12|6/11, 3/5|3/8, 2/3|9/10, 1/2|4/9, 1/3|3/10, 3/5|4/7, 1/2|7/9, 3/10|7/10, 3/7|4/7, 1/11|4/7, 2/11|3/8, 2/11|3/5, 1/3|9/10, 3/7|9/11, 1/12|5/9, 4/9|8/9, 5/8|5/9, 3/11|7/8, 3/8|9/11, 1/7|5/9, 1/7|3/10, 1/7|9/11, 1/6|9/10, 3/11|7/9, 5/11|9/10, 3/5|7/9, 3/4|5/6, 2/3|3/5, 2/11|4/11, 1/8|3/8, 1/6|2/3, 1/8|4/7, 1/10|2/11, 4/9|4/11, 1/7|3/8, 2/5|5/11, 4/11|8/11, 1/5|7/8, 3/7|7/12, 2/7|4/5, 1/2|2/11, 3/7|5/9, 1/9|5/9, 1/3|1/4, 1/4|2/3, 2/5|7/12, 7/8|9/10, 3/7|7/11, 1/9|9/11, 1/3|7/10, 1/7|11/12, 1/8|6/11, 2/5|4/5, 5/7|8/9, 1/9|2/9, 1/10|2/7, 1/5|3/5, 3/10|11/12, 5/11|7/11, 3/8|8/9, 5/11|7/10, 5/7|7/12, 1/5|9/11, 1/7|7/10, 5/12|8/9, 5/11|5/12, 1/7|6/11, 7/10|7/11, 2/3|2/9, 2/11|11/12, 5/7|9/11, 4/5|9/11, 4/7|8/11, 1/3|3/7, 1/6|7/12, 1/5|7/11, 1/5|2/11, 1/2|8/9, 6/11|11/12, 5/6|9/10, 1/10|3/5, 2/7|5/9, 1/7|7/11, 4/7|5/12, 1/4|4/5, 5/6|5/11, 3/8|8/11, 1/2|4/5, 4/9|7/11, 3/10|7/12, 2/3|3/4, 1/12|7/10, 1/10|1/12, 1/7|3/11, 5/9|7/8, 3/10|6/7, 4/7|7/12, 1/3|8/11, 1/8|7/8, 3/5|5/11, 1/3|5/11, 6/7|7/10, 1/8|3/10, 1/6|5/12, 1/12|3/11, 1/2|4/11, 3/4|3/11, 1/6|2/11, 4/11|11/12, 2/5|4/7, 2/5|3/7, 3/5|5/7, 5/9|6/11, 3/5|7/8, 3/10|7/11, 2/7|3/8, 4/7|7/10, 1/12|3/7, 5/6|5/8, 1/9|7/12, 1/4|1/9, 5/9|11/12, 7/11|11/12, 5/6|8/9, 1/7|4/11, 5/8|8/9, 1/2|3/4, 1/3|7/9, 7/10|7/12, 1/3|2/3, 2/7|8/9, 5/12|6/7, 3/10|6/11, 3/10|5/12, 1/4|9/10, 3/11|5/9, 7/8|7/9, 7/8|9/11, 5/12|9/11, 3/7|7/10, 3/5|9/10, 1/6|4/11, 1/6|2/7, 4/9|9/11, 3/5|4/11, 1/7|2/7, 2/11|8/11, 1/3|2/7, 1/4|2/9, 2/3|2/5, 1/11|4/5, 1/6|8/11, 1/5|4/11, 2/7|7/8, 3/4|7/11, 2/7|2/11, 1/11|6/11, 1/4|2/5, 7/8|8/9, 1/10|5/6, 3/8|7/9, 5/6|7/8, 2/7|3/11, 5/11|11/12, 2/11|5/9, 1/7|5/12, 1/8|4/11, 4/9|7/10, 2/5|5/6, 4/11|9/10, 1/12|4/7, 1/8|7/11, 2/5|8/11, 4/11|9/11, 7/10|8/9, 1/10|2/5, 3/11|7/12, 6/11|7/10, 2/7|7/10, 1/5|7/12, 1/7|8/11, 1/11|9/11, 5/6|11/12, 1/8|1/10, 1/5|5/11, 3/8|7/11, 1/10|5/9, 7/11|8/11, 2/5|5/9, 1/7|4/7, 1/3|5/9, 4/5|5/9, 1/5|1/8, 2/5|9/10, 2/11|6/7, 4/11|5/12, 1/4|4/7, 5/6|5/9, 2/3|3/11, 1/6|1/11, 7/9|11/12, 1/7|2/3, 1/5|4/7, 1/7|1/10, 1/10|3/10, 7/11|7/12, 1/8|3/5, 3/11|11/12, 1/4|5/6, 1/3|4/5, 2/11|3/7, 3/7|3/8, 2/7|9/11, 2/7|4/9, 2/9|2/11, 4/5|6/7, 1/3|3/4, 1/2|6/7, 1/2|2/5, 1/3|1/12, 1/10|7/11, 2/9|4/7, 1/7|1/9, 1/7|8/9, 1/2|1/8, 7/9|7/10, 3/7|5/8, 7/9|7/11, 7/9|8/9, 1/9|8/11, 1/11|3/4, 2/11|7/8, 6/11|8/11, 2/11|5/6, 5/11|6/7, 5/9|7/11, 2/5|7/10, 1/9|4/9, 1/4|1/11, 1/3|3/11, 1/4|9/11, 6/7|7/11, 1/6|4/7, 1/4|6/7, 1/10|5/11, 3/7|9/10, 1/6|7/8, 1/4|7/8, 3/10|3/11, 1/4|5/7, 1/11|3/5, 4/9|5/11, 1/12|4/11, 2/3|5/12, 1/7|3/5, 1/2|1/9, 1/11|8/11, 2/7|3/7, 7/12|11/12, 1/8|5/12, 2/11|3/4, 1/5|1/9, 1/3|3/8, 1/3|1/10, 1/2|7/10, 1/12|2/11, 5/12|7/10, 7/10|9/11, 1/2|5/9, 3/10|9/11, 7/11|9/10, 2/11|3/10, 5/7|7/11, 3/7|4/5, 4/7|9/10, 2/3|7/12, 6/7|7/9, 1/9|7/11, 1/3|6/11, 1/6|3/10, 1/7|9/10, 1/9|3/10, 5/6|9/11, 1/4|5/11, 1/12|5/11, 3/8|6/7, 4/5|8/9, 1/3|1/11, 4/7|5/6, 2/9|5/11, 4/9|5/6, 1/2|2/3, 1/3|6/7, 1/7|2/11, 2/11|5/8, 1/10|7/12, 4/5|4/7, 3/11|4/5, 2/9|7/8, 5/12|7/11, 1/3|7/8, 1/4|1/12, 1/9|7/10, 5/6|6/11, 3/4|4/9, 3/4|5/12, 3/8|6/11, 4/7|7/9, 2/9|9/10, 1/3|1/8, 3/5|11/12, 5/8|5/11, 3/11|9/11, 1/9|4/7, 3/10|7/9, 1/9|8/9, 5/12|9/10, 1/12|7/9, 4/11|8/9, 2/9|3/7, 4/11|5/7, 1/3|1/7, 1/11|4/9, 1/12|5/7, 2/5|4/9, 3/8|9/10, 1/9|11/12, 1/3|2/11, 3/8|4/7, 1/10|8/9, 2/7|11/12, 4/9|9/10, 5/6|6/7, 2/7|8/11, 3/10|5/7, 1/5|5/9, 3/11|7/11, 1/5|7/10, 7/8|7/12, 1/9|3/11, 6/11|9/11, 1/9|7/8, 1/10|5/12, 4/7|7/11, 1/8|2/3, 2/5|3/11, 1/8|2/5, 1/5|3/8, 2/9|11/12, 1/4|7/9, 4/5|6/11, 1/4|8/11, 4/9|5/9, 6/7|6/11, 3/5|4/5, 3/8|7/12, 2/9|3/5, 4/7|9/11, 8/11|11/12, 2/11|4/7, 1/6|7/9, 1/5|3/11, 1/8|2/11, 5/11|7/9, 2/11|7/10, 3/4|9/10, 4/5|5/6, 2/11|7/11, 4/11|7/8, 1/8|1/12, 1/10|4/5, 6/11|7/11, 3/5|7/12, 1/8|1/9, 5/8|11/12, 4/11|5/11, 2/3|8/11, 2/5|4/11, 1/7|6/7, 3/11|8/11, 1/3|5/12, 3/7|5/7, 5/11|7/12, 1/4|5/9, 1/12|3/4, 1/11|2/9, 5/6|5/7, 1/8|5/9, 1/10|4/7, 3/8|5/6, 1/3|9/11, 3/5|4/9, 4/9|5/7, 1/9|2/7, 1/8|5/7, 1/8|7/9, 1/11|2/3, 1/12|3/10, 2/9|5/8, 1/11|3/7, 3/10|8/9, 3/4|3/7, 2/7|4/7, 3/5|5/12, 1/2|5/7, 2/3|6/7, 1/9|3/7, 1/12|2/7, 4/5|8/11, 3/4|4/7, 1/3|4/9, 1/11|3/10, 1/12|8/9, 1/4|11/12, 1/2|3/10, 1/3|5/6, 1/7|1/12, 3/10|4/11, 2/7|7/11, 1/8|5/8, 2/5|7/8, 3/10|4/9, 1/11|11/12, 7/10|11/12, 5/7|5/8, 1/4|1/10, 2/9|6/7, 1/9|7/9, 1/9|6/11, 3/5|5/6, 1/2|5/11, 4/7|4/9, 3/11|7/10, 1/10|3/11, 2/7|6/11, 2/3|7/11, 3/10|5/11, 3/8|5/12, 1/4|3/7, 6/11|7/9, 5/12|8/11, 7/12|9/11, 1/7|2/9, 1/3|11/12, 1/9|3/4, 3/8|5/11, 1/7|3/7, 2/5|7/11, 5/8|8/11, 5/7|7/9, 2/7|7/12, 1/2|1/4, 2/11|4/9, 3/5|5/8, 2/3|3/8, 1/7|4/5, 1/11|3/8, 1/2|1/6, 5/9|5/12, 2/7|7/9, 1/10|3/4, 1/6|3/11, 3/7|6/7, 1/6|6/11, 5/6|7/9, 2/5|5/7, 3/4|4/5, 1/8|8/9, 4/7|5/9, 1/2|5/8, 3/5|8/11, 3/7|8/9, 1/7|3/4, 1/7|5/7, 1/11|3/11, 3/7|6/11, 1/3|4/7, 7/11|9/11, 2/3|4/7, 3/4|6/7, 9/10|9/11, 2/3|3/7, 1/6|6/7, 1/8|3/4, 4/9|5/12, 3/10|4/7, 2/7|3/10, 1/8|9/10, 7/8|7/11, 6/7|9/11, 2/9|8/11, 2/7|3/4, 5/12|6/11, 1/8|2/7, 5/11|6/11, 5/12|7/8, 2/3|8/9, 1/4|5/8, 1/7|7/9, 1/10|9/11, 1/6|4/9, 6/11|7/8, 5/11|9/11, 1/10|7/10, 3/7|4/9, 1/2|9/10, 5/6|7/11, 1/5|5/7, 4/11|7/12, 4/5|5/11, 5/7|5/11, 1/9|4/5, 1/3|1/9, 1/10|6/11, 4/5|7/8, 4/5|5/12, 2/11|9/11, 3/8|5/8, 5/9|5/11, 1/9|3/5, 3/5|7/11, 2/7|5/12, 3/5|6/7, 1/2|2/9, 1/10|5/7, 1/8|1/11, 5/7|11/12, 2/5|7/9, 5/6|7/12, 4/7|6/7, 4/5|11/12, 1/4|7/12, 3/7|7/9, 3/5|5/9, 1/4|5/12, 1/5|5/6, 1/4|3/8, 5/11|7/8, 1/6|2/5, 2/7|4/11, 3/7|8/11, 1/2|3/5, 1/4|6/11, 1/6|4/5, 1/6|11/12, 2/5|3/8, 4/11|7/9, 1/12|6/7, 2/9|5/9, 1/6|5/9, 1/11|7/8, 1/12|7/11, 1/9|1/10, 1/6|3/8, 5/7|6/7, 5/11|8/11, 1/6|1/9, 1/10|4/9, 1/10|2/3, 3/10|5/8, 1/9|6/7, 5/11|8/9, 2/9|5/7, 2/5|6/11, 2/11|6/11, 3/7|5/11, 5/9|9/11, 1/11|5/12, 5/7|9/10, 1/2|3/7, 9/11|11/12, 1/10|7/9, 4/9|11/12, 2/5|9/11, 1/12|5/8, 5/9|6/7, 2/9|7/11, 1/2|5/12, 4/5|5/8, 1/9|5/11, 3/7|3/10, 2/9|7/12, 1/5|2/7, 1/9|1/11, 4/5|7/12, 3/8|7/10, 1/2|6/11, 3/11|5/6, 6/7|7/12, 7/8|11/12, 1/2|11/12, 4/9|8/11, 4/9|6/7, 2/3|5/9, 2/3|5/7, 7/9|9/11, 1/7|7/12, 1/6|3/7, 1/12|2/3, 1/8|5/6, 7/8|8/11, 4/5|4/11, 1/3|1/5, 6/7|7/8, 3/4|7/9, 4/7|4/11, 1/12|9/10, 3/4|5/7, 3/8|7/8, 1/4|3/11, 1/4|2/11, 5/8|5/12, 2/3|6/11, 1/3|1/6, 1/6|2/9, 1/9|5/8, 1/5|11/12, 7/12|9/10, 1/8|7/10, 1/4|4/11, 2/7|9/10, 2/5|8/9, 2/9|9/11, 1/8|8/11, 3/10|5/9, 1/5|8/11, 5/8|7/11, 2/11|5/7, 4/7|11/12, 4/5|7/10, 1/7|5/6, 2/5|2/9, 1/3|2/9, 3/11|5/11, 1/9|4/11, 1/6|5/6, 4/11|6/7, 2/7|5/11, 1/8|3/7, 2/5|2/7, 3/7|5/6, 1/2|1/3, 2/3|5/11, 4/11|5/9, 1/6|1/12, 2/3|4/9, 1/4|1/7, 7/10|8/11, 3/10|5/6, 1/10|6/7, 2/9|5/6, 3/4|7/10, 1/2|7/12, 7/8|7/10, 3/8|5/7, 8/9|8/11, 1/11|7/9, 5/7|7/8, 2/3|5/6, 1/6|8/9, 1/9|2/5, 1/5|5/8, 1/5|4/5, 5/9|8/11, 4/9|7/12, 3/8|11/12, 5/8|7/8:3/5|11/2,1/2|7/3,2/5|11/2,1/2|6/5,1/2|5/4,3/4|11/2,2/3|7/3,1/6|9/2,3/4|7/2,1/2|9/5,3/5|3/2,2/3|5/3,1/2|9/4,2/3|11/2,1/6|11/2,2/3|11/4,4/5|11/2,1/5|3/2,2/5|5/2,3/5|7/2,1/2|11/3,1/3|8/3,1/4|4/3,2/3|8/3,1/6|3/2,2/3|7/2,1/3|4/3,1/4|3/2,1/3|11/4,2/3|3/2,1/2|5/2,1/3|5/4,1/4|9/2,5/6|3/2,2/3|11/3,1/2|5/3,1/2|7/5,1/4|8/3,1/3|11/2,3/4|3/2,1/3|3/2,2/3|7/4,1/5|7/2,3/4|9/2,1/2|7/4,2/5|3/2,1/2|8/3,1/2|12/5,1/2|11/6,4/5|7/2,2/3|9/2,1/5|11/2,1/2|8/5,4/5|3/2,1/2|3/2,2/5|7/2,3/4|4/3,1/4|11/2,1/3|7/3,1/2|7/6,5/6|9/2,3/4|11/3,1/2|11/5,1/3|11/3,1/2|4/3,2/3|9/4,3/5|5/2,5/6|5/2,1/3|7/4,3/4|7/3,3/4|5/3,1/3|9/2,1/4|5/2,1/4|5/3,4/5|5/2,1/2|11/2,1/2|9/2,1/6|5/2,2/5|9/2,1/3|5/3,1/4|11/3,3/5|9/2,1/3|9/4,1/4|7/2,1/2|7/2,2/3|5/2,2/3|5/4,3/4|8/3,3/4|5/2,1/5|9/2,5/6|11/2,2/3|4/3,1/3|5/2,1/2|11/4,1/6|7/2,4/5|9/2,5/6|7/2,1/3|7/2,1/4|7/3,1/5|5/2:5|7/10,5|1/6,1|8/9,3|9/10,6|2/9,5|3/5,5|1/4,3|9/11,7|3/11,9|3/8,1|2/9,7|1/9,11|3/5,8|5/9,1|5/6,2|8/11,9|5/9,4|5/6,1|2/11,2|2/9,1|5/7,7|8/9,11|5/12,3|7/12,8|2/3,1|7/11,5|1/5,10|9/10,2|3/8,2|1/6,6|1/7,9|1/3,8|1/12,8|6/11,3|7/11,4|1/7,4|7/12,7|6/11,4|1/2,5|7/11,3|3/5,3|2/11,12|5/12,4|8/9,11|8/11,8|1/10,1|7/8,12|3/4,8|6/7,2|2/3,4|5/12,6|4/9,7|5/8,8|5/11,6|7/11,6|1/8,4|1/8,9|3/11,8|1/11,6|1/5,9|5/11,6|1/4,10|5/6,2|3/5,8|1/8,7|3/8,5|1/8,6|6/7,9|1/5,7|1/6,5|5/11,2|4/5,2|5/6,11|7/9,12|7/10,2|7/11,6|3/10,4|1/12,8|3/5,6|2/5,9|4/5,6|1/11,12|4/11,9|11/12,4|1/11,12|5/9,2|1/10,7|4/7,4|5/7,12|2/9,5|1/10,2|3/4,9|2/5,3|1/5,4|2/5,5|2/11,5|5/8,8|3/8,11|9/10,12|1/12,4|5/9,8|2/5,9|6/11,6|7/12,1|7/10,11|8/9,10|1/6,11|7/8,5|1/9,7|3/5,12|8/11,4|4/11,1|1/4,10|2/9,1|1/8,1|3/7,11|5/9,10|1/11,3|4/5,1|3/4,7|4/9,12|5/8,6|5/8,3|1/12,9|5/8,4|9/11,7|1/11,11|5/6,7|9/11,10|8/11,9|1/9,5|6/7,1|2/7,9|8/9,3|2/7,6|5/9,10|7/10,11|1/6,2|5/12,8|5/7,12|2/3,7|1/10,12|1/4,12|5/7,11|7/11,2|9/10,3|1/4,8|3/10,5|3/11,2|8/9,9|1/7,10|2/3,8|2/7,9|1/4,12|1/9,3|2/5,3|3/4,7|11/12,12|1/11,8|8/9,12|1/3,2|2/11,1|7/12,3|1/9,6|8/9,9|2/9,9|5/12,8|4/7,5|4/7,3|5/8,12|6/7,1|5/11,11|3/7,8|3/11,11|1/7,8|4/5,3|4/11,11|1/10,2|4/11,5|3/10,10|1/5,5|9/10,4|1/4,9|8/11,1|6/7,4|9/10,2|3/11,7|7/12,2|1/2,6|1/9,8|1/3,1|8/11,8|5/8,10|8/9,6|3/7,6|9/10,12|3/10,8|11/12,12|5/6,11|2/7,9|3/7,3|7/10,1|1/9,8|1/2,12|1/7,6|3/8,9|3/4,4|7/9,7|1/3,1|5/9,11|1/5,5|8/9,12|4/7,5|8/11,6|5/11,6|11/12,1|11/12,3|4/7,1|1/11,6|7/9,4|6/7,7|2/5,11|5/7,6|1/6,7|4/5,7|6/7,10|4/7,5|3/7,3|1/2,8|7/8,11|4/9,1|1/7,10|3/5,10|1/9,4|3/11,9|1/11,10|2/7,11|11/12,7|1/4,5|1/11,2|11/12,12|2/5,2|6/11,1|2/5,7|5/12,7|1/2,7|1/7,11|7/10,5|4/9,11|1/8,3|1/6,10|1/7,11|3/8,3|1/7,4|2/3,10|6/11,3|1/10,10|1/4,11|2/11,12|4/5,6|1/3,5|1/2,7|2/11,3|3/10,4|4/5,9|3/5,12|9/10,1|3/10,3|2/3,1|4/7,11|1/3,3|6/7,12|1/5,2|1/9,3|5/7,8|7/10,7|4/11,12|4/9,4|1/9,8|1/4,3|1/11,5|4/5,1|5/8,10|6/7,10|7/8,9|1/6,9|2/3,10|5/7,1|1/3,11|4/7,11|1/12,2|1/7,11|3/11,4|3/4,9|3/10,8|3/7,7|5/11,5|2/9,4|1/6,4|5/8,9|1/10,3|5/12,12|11/12,11|4/5,1|9/11,5|5/6,9|1/2,10|5/11,7|3/7,11|5/8,7|1/5,5|1/7,2|1/5,3|1/3,9|7/10,2|5/9,12|3/7,5|2/7,12|8/9,3|1/8,2|6/7,10|3/4,10|7/11,9|5/7,3|11/12,9|2/11,5|6/11,6|3/5,7|7/10,11|6/7,12|7/8,9|6/7,6|4/5,2|1/11,4|11/12,9|7/9,10|1/3,10|9/11,2|4/7,6|5/6,7|1/8,10|5/12,9|4/11,1|4/11,6|1/12,12|7/9,7|3/10,10|1/12,8|2/11,7|3/4,11|2/9,6|3/11,2|3/7,5|7/12,2|5/8,3|4/9,2|7/12,10|3/10,9|1/12,1|1/5,8|4/9,3|7/8,8|4/11,8|2/9,8|5/12,8|9/11,8|5/6,1|9/10,11|6/11,12|1/8,7|2/3,2|1/8,4|4/9,7|5/9,4|1/10,8|7/12,9|7/8,8|1/6,1|1/6,12|3/5,3|7/9,2|1/12,11|2/3,6|4/7,6|4/11,8|1/9,10|3/8,12|7/12,11|7/12,6|9/11,12|1/6,1|3/5,10|1/8,1|3/11,9|9/11,7|7/8,4|5/11,1|1/10,2|7/10,2|7/9,3|8/9,1|5/12,1|2/3,10|1/2,8|3/4,5|3/4,12|5/11,9|5/6,12|2/7,8|7/9,4|7/11,4|7/10,10|3/11,3|5/9,11|9/11,8|1/7,11|1/4,12|6/11,11|2/5,5|5/9,2|1/4,7|5/6,6|3/4,8|7/11,6|5/7,4|2/9,10|5/8,2|5/7,2|7/8,2|9/11,9|4/7,4|3/5,10|11/12,10|4/5,3|5/11,7|7/11,5|2/5,5|1/12,3|6/11,9|7/12,11|4/11,1|3/8,6|6/11,5|9/11,10|2/11,4|3/7,3|3/11,4|3/8,11|3/4,1|4/5,12|3/11,4|6/11,4|2/7,12|2/11,9|1/8,9|4/9,6|5/12,3|3/8,2|2/7,7|9/10,1|1/12,5|2/3,2|1/3,5|11/12,10|4/11,9|9/10,2|4/9,7|7/9,1|1/2,5|3/8,7|8/11,3|8/11,12|1/10,2|5/11,9|2/7,10|1/10,10|2/5,11|5/11,5|1/3,7|2/9,1|7/9,6|7/8,3|5/6,5|4/11,3|3/7,4|1/3,4|2/11,3|2/9,11|1/2,7|5/7,4|3/10,10|5/9,6|1/10,4|4/7,4|8/11,5|5/12,2|3/10,9|7/11,5|7/8,6|2/7,4|1/5,5|7/9,8|9/10,6|1/2,2|2/5,12|7/11,7|2/7,1|4/9,10|4/9,12|9/11,6|2/11,8|1/5,11|3/10,11|1/11,7|1/12,10|7/9,12|3/8,6|8/11,6|7/10,4|7/8,6|2/3,12|1/2,1|6/11,10|7/12,5|5/7,8|8/11,11|1/9,10|3/7:11/2|11/6,5/2|9/2,8/3|11/3,7/2|4/3,11/3|5/4,9/2|7/6,4/3|5/4,11/3|9/4,3/2|11/2,5/2|11/3,8/3|7/4,5/2|11/5,11/2|8/5,11/2|5/4,11/3|7/4,11/2|6/5,7/2|11/5,3/2|6/5,9/2|7/5,3/2|4/3,5/3|7/4,3/2|11/4,3/2|9/2,3/2|7/2,3/2|9/4,7/2|7/5,5/2|7/3,11/3|11/4,7/2|5/3,5/3|9/4,9/2|11/5,7/2|7/4,7/2|8/5,9/2|7/4,9/2|11/6,5/2|8/3,5/2|11/6,7/2|7/6,9/2|4/3,5/2|5/4,7/3|11/3,5/2|4/3,5/3|11/3,8/3|9/4,4/3|9/4,7/2|11/4,9/2|9/4,11/2|7/4,5/3|11/4,3/2|11/6,3/2|9/5,7/3|7/4,5/2|11/4,7/2|11/6,9/2|9/5,4/3|7/4,7/3|9/4,3/2|11/3,3/2|8/5,3/2|5/3,7/3|11/4,9/2|8/5,11/2|7/6,5/2|7/5,3/2|5/4,9/2|5/3,5/2|7/4,7/3|5/4,5/2|5/3,4/3|7/3,9/2|8/3,5/3|7/3,5/3|8/3,7/2|8/3,11/2|4/3,11/2|5/3,7/2|6/5,3/2|7/6,3/2|7/4,3/2|12/5,5/2|8/5,7/2|5/4,4/3|11/3,5/3|5/4,7/2|9/5,3/2|7/5,5/2|9/5,5/2|12/5,7/2|7/3,8/3|11/4,11/2|9/5,3/2|11/5,9/2|7/3,3/2|7/3,7/2|12/5,8/3|5/4,4/3|8/3,5/2|6/5,11/2|7/5,9/2|5/4,9/2|6/5,4/3|5/3,9/2|12/5,5/2|7/2,5/2|7/6,3/2|5/2,5/2|9/4,7/2|9/4,4/3|11/4,3/2|8/3,7/3|8/3:6|8/5,6|11/9,7|11/7,1|11/10,1|7/6,2|8/3,2|11/2,5|11/10,2|11/6,3|11/5,1|5/4,5|3/2,1|12/11,5|8/7,1|7/4,4|12/11,2|7/6,3|8/5,5|5/4,1|9/8,6|7/5,1|8/3,8|5/4,6|5/4,6|11/7,3|11/3,7|9/7,3|4/3,2|3/2,3|8/3,4|11/6,2|12/11,6|11/10,4|5/4,5|9/7,2|9/2,2|12/5,8|11/8,8|9/7,10|6/5,10|11/10,4|11/8,1|11/2,7|8/5,1|9/7,6|11/8,3|12/11,3|3/2,8|3/2,4|12/7,3|11/4,10|12/11,3|5/3,5|11/6,2|7/3,8|4/3,3|7/4,5|7/5,2|11/4,7|5/4,3|12/5,3|8/7,4|7/5,4|9/4,5|11/9,4|11/10,2|5/3,1|5/2,5|12/5,6|7/4,8|8/7,4|8/7,4|7/6,2|9/4,5|7/4,1|7/3,1|11/7,4|5/2,5|9/4,2|11/10,5|7/3,4|5/3,10|7/6,3|9/8,9|11/10,5|6/5,4|7/3,5|11/5,7|5/3,9|9/8,8|11/10,4|11/9,9|12/11,9|5/4,6|12/7,5|7/6,3|5/4,4|9/7,1|11/4,6|4/3,4|11/7,3|12/7,9|11/9,4|7/4,1|7/5,1|3/2,7|11/8,9|4/3,2|7/2,1|11/5,4|3/2,2|11/5,1|11/8,3|5/2,1|6/5,7|12/11,4|9/8,6|9/7,3|11/9,2|11/3,7|8/7,2|9/7,5|11/7,4|11/5,2|5/4,1|5/3,10|9/8,7|6/5,5|9/5,1|12/5,5|12/11,1|11/3,4|6/5,2|12/7,6|9/5,3|11/7,5|8/5,2|8/5,6|6/5,4|12/5,5|5/3,3|11/8,6|7/6,1|11/9,1|9/2,7|11/10,7|7/5,1|8/5,7|7/6,4|8/3,3|11/6,3|6/5,1|11/6,6|8/7,7|12/7,2|11/8,8|11/9,6|9/8,3|11/10,10|8/7,1|9/5,8|7/6,2|11/7,3|7/3,1|7/2,9|8/7,3|7/5,2|5/2,2|6/5,3|7/6,7|9/8,8|12/11,2|11/9,3|9/5,4|9/5,4|11/4,2|8/7,7|4/3,1|4/3,5|12/7,2|7/4,6|12/11,5|4/3,3|9/7,9|7/6,8|6/5,7|3/2,9|9/7,2|9/8,3|9/4,4|8/5,8|9/8,1|8/7,7|11/9,11|12/11,1|12/7,5|11/8,3|7/2,2|9/5,6|3/2,1|9/4,2|7/5,6|5/3,4|4/3,8|7/5,9|6/5,5|9/8,6|11/6,2|4/3";
    var topic_data_18 = "1/2,1/3,1/4,1/5,1/6,1/7,1/8,1/9,1/10,2/3,1/4,3/4,1/5,2/5,3/5,4/5,1/6,5/6,1/7,3/7,5/7,1/8,3/8,5/8,7/8,1/9,2/9,4/9,5/9,7/9,8/9,1/10,3/10,7/10,9/10,1/20,3/20,1/25,1/40,1/50,1/100:2/1,3/1,4/1,5/1,6/1,7/1,8/1,9/1,10/1,20/1,25/1,40/1,50/1,100/1,3/2,5/2,7/2,9/2,11/2,4/3,5/3,7/3,8/3,5/4,7/4,9/4,11/4,6/5,9/5,12/5,9/8,11/8";
    var topic_data_22 = '<data><item><question><![CDATA[The number of minutes it takes me to read 7 pages if I read 1 page in <i>M</i> minutes.]]></question><answer vars="M"><![CDATA[7M]]></answer><explanation><![CDATA[It takes|<eqn>M|minutes to read 1 page,|<eqn>2*M|minutes to read 2 pages,|<eqn>3*M|minutes to read 3 pages, etc. So it takes|<eqn>7*M|minutes to read 7 pages.]]></explanation></item><item><question><![CDATA[The age in years of someone who is twice as old as Cayenne, if Cayenne is <i>C</i> years old.]]></question><answer vars="C"><![CDATA[2C]]></answer><explanation><![CDATA[If Cayenne is 5 years old, then twice that is|<eqn>2*5=10|, if Cayenne is 7 years old, then twice that is would be|<eqn>2 *7=14|years old. If Cayenne is|<eqn>C|years old, then twice that would be|<eqn>2*C|years old.]]></explanation></item><item><question><![CDATA[The total price, in cents, of 5 apples at <i>A</i> cents each plus 9 bananas at <i>B</i> cents each.]]></question><answer vars="A,B"><![CDATA[5A+9B]]></answer><explanation><![CDATA[If one apple costs 15 cents, then 5 apples cost|<eqn>5*15=75|cents.  If an apple costs|<eqn>A|cents, then 5 apples cost|<eqn>5*A|cents. Likewise, the bananas cost|<eqn>9*B|cents. Adding the two amounts together gives|<eqn>5A+9B|, also written as|<eqn>5*A+9*B|.]]></explanation></item><item><question><![CDATA[Number of gallons of gas it takes to go 50 miles if you get <i>G</i> miles per gallon.]]></question><answer vars="G"><![CDATA[50/G]]></answer><explanation><![CDATA[At 10 miles per gallon, it will take|<eqn>{50/10}=5|gallons. At 25 miles per gallon, it will take|<eqn>{50/25}=2|gallons. So, at|<eqn>G|miles per gallon it will take me |<eqn>50/G|gallons.]]></explanation></item><item><question><![CDATA[The sum of two consecutive numbers with <i>N</i> as the first one.]]></question><answer vars="N"><![CDATA[2N+1]]></answer><explanation><![CDATA[As an example, 5 and 6 are two consecutive numbers. Their sum would be|<eqn>5+6 = 11|. If the first number is|<eqn>N|, then the next one would be|<eqn>N+1|. The sum would be|<eqn>N+(N+1)|which simplifies to|<eqn>2N+1|.]]></explanation></item><item><question><![CDATA[The number of minutes required to watch half of an <i>M</i>-minute movie.]]></question><answer vars="M"><![CDATA[M/2]]></answer><explanation><![CDATA[To find half of any quantity, divide by 2. For example, half of a 110 minute movie would take|<eqn>{110/2}=55|minutes. So, half of an|<eqn>M|-minute movie would take|<eqn>{M/2}|minutes.]]></explanation></item><item><question><![CDATA[The perimeter, in units, of an equilateral triangle with each side <i>L</i> units long.]]></question><answer vars="L"><![CDATA[3L]]></answer><explanation><![CDATA[An equilateral triangle has three equal sides. The perimeter of a triangle is the sum of the lengths of the three sides. For our triangle, the perimeter is|<eqn>L+L+L|which simplifies to|<eqn>3*L|or|<eqn>3L|.]]></explanation></item><item><question><![CDATA[The number of months it takes to grow one foot of hair, if the hair grows <i>N</i> inches per month.]]></question><answer vars="N"><![CDATA[12/N]]></answer><explanation><![CDATA[There are 12 inches in 1 foot.  Therefore, we divide 12 by the number of inches per month to get the answer. For example, if hair grows 2 inches per month, then in|<eqn>{12/2}=6|months to grow one foot. So, at|<eqn>N|inches per month it would take|<eqn>{12/N}|months.]]></explanation></item><item><question><![CDATA[The total score of a football game with <i>T</i> 6-pointer touchdowns, <i>F</i> 3-pointer field goals, and <i>C</i> 1-point conversions.]]></question><answer vars="T,F,C"><![CDATA[6T+3F+C]]></answer><explanation><![CDATA[As an example, three touchdowns give|<eqn>3*6=18|points, 5 field goals add|<eqn>5*3=15|more points, and 3 conversions add|<eqn>3*1=3|points. So the answer using|<eqn>T|,|<eqn>F|and|<eqn>C|would be|<eqn>6T+3F+C|.]]></explanation></item><item><question><![CDATA[The average score of three tests with scores of <i>a</i>, <i>b</i>, and <i>c</i>.]]></question><answer vars="a,b,c"><![CDATA[(a+b+c)/3]]></answer><explanation><![CDATA[Average is computed by adding all the values and dividing by the number of values. For example, if the scores were 70, 80, and 90, then the average score would be|<eqn>{(70+80+90)/3}={240/3}=80|. So, the answer is|<eqn>{(a+b+c)/3}|.]]></explanation></item></data>';
    var topic_data_24 = "1/2,1/3,2/3,1/4,2/4,3/4,1/5,2/5,3/5,4/5,1/6,2/6,3/6,4/6,5/6,1/8,2/8,3/8,4/8,5/8,6/8,7/8,1/9,2/9,3/9,4/9,5/9,6/9,7/9,8/9,1/10,2/10,3/10,4/10,5/10,6/10,7/10,8/10,9/10,1/11,1/16,3/12,4/12,6/12,8/12,9/12,1/20,3/20,7/20,9/20,11/20,13/20,17/20,19/20,1/25,1/40,1/50,1/100,1/200,1/500,1/1000|2/2,3/3,4/4,5/5,6/6,7/7,8/8,3/2,6/4,9/6,12/8,15/10,18/12,5/2,10/4,15/6,20/8,4/3,8/6,12/9,16/12,20/15,5/3,10/6,15/9,20/12,4/2,6/3,8/4,10/5,16/8,24/12,36/18,13/25,37/40,49/50,3/100,17/500,334/1000";
    var topic_data_25 = "0.5,1.5,2.5,3.5,0.333R,0.666R,0.25,0.75,1.25,1.75,2.25,2.75,3.25,3.75,0.2,0.4,0.6,0.8,1.2,2.4,3.6,4.8,0.125,0.375,0.625,0.875,1.125,1.375,0.1,0.3,0.7,0.9,0.05,0.15,0.35,0.45,0.55,0.65,0.85,0.95,0.04,0.08,0.12,0.16,0.24,0.28,0.32,0.36,0.44,0.48,0.52,0.56,0.64,0.68,0.72,0.76,0.84,0.88,0.92,0.96,0.11,0.13,0.17,0.19,0.23,0.27,0.33,0.39,0.67,0.71,0.77,0.79,0.81,0.83,0.87,0.89";
    var topic_data_28 = "<data><item><question eqn='2.4 d 0.2' type='1' txt='The value closest to'/><answer val='10'/><choices val='1|10|20|50'/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(2.4/0.2)*(10/10)| = |<eqn>24/2|.]]></step><step><![CDATA[Since 2 divides evenly into 24, our answer simplifies to]]></step><step><![CDATA[|<eqn>24/2| = 12.]]></step><step><![CDATA[Of all of the choices, the result is closest to 10.]]></step></item><item><question eqn='0.36 d 0.04' type='1' txt='The value closest to'/><answer val='10'/><choices val='1|10|15|20'/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.36/0.04)*(100/100)| = |<eqn> 36/4|.]]></step><step><![CDATA[Since 4 divides evenly into 36, our answer simplifies to]]></step><step><![CDATA[|<eqn>36/4| = 9.]]></step><step><![CDATA[Of all of the choices, the result is closest to 10.]]></step></item><item><question eqn='2.9 d 0.62' type='1' txt='The value closest to'/><answer val='5'/><choices val='5|10|15|20'/><step><![CDATA[You may notice that 2.9 is close to 3.0 and 0.62 is close to 0.60, so |<eqn>2.9/0.62| is close to |<eqn>3.0/0.60|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(3.0/0.60)*(10/10)| = |<eqn>30/6|.]]></step><step><![CDATA[Since 6 divides evenly into 30, our answer simplifies to]]></step><step><![CDATA[|<eqn>30/6| = 5.]]></step><step><![CDATA[The answer to the original problem will be closest to 5.]]></step></item><item><question eqn='9.1 d 0.32' type='1' txt='The value closest to'/><answer val='30'/><choices val='5|10|20|30'/><step><![CDATA[You may notice that 9.1 is close to 9.0 and 0.32 is close to 0.30, so |<eqn>9.1/0.32| is close to |<eqn>9.0/0.3|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(9.0/0.3)*(10/10)| = |<eqn>90/3|.]]></step><step><![CDATA[Since 3 divides evenly into 90, our answer simplifies to]]></step><step><![CDATA[|<eqn>90/3| = 30.]]></step><step><![CDATA[The answer to the original problem will be closest to 30.]]></step></item><item><question eqn='40 d 0.02' type='2' txt='Find the value of'/><answer val='2000'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(40/0.02)*(100/100)| = |<eqn>4000/2|.]]></step><step><![CDATA[Since 2 divides evenly into 4000, our answer simplifies to]]></step><step><![CDATA[|<eqn>4000/2| = 2000.]]></step></item><item><question eqn='56 d 0.07' type='2' txt='Find the value of'/><answer val='800'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(56/0.07)*(100/100)| = |<eqn>5600/7|.]]></step><step><![CDATA[Since 7 divides evenly into 5600, our answer simplifies to]]></step><step><![CDATA[|<eqn>5600/7| = 800.]]></step></item><item><question eqn='0.48 d 1.2' type='2' txt='Find the value of'/><answer val='0.4'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.48/1.2)*(100/100)| = |<eqn>48/120|.]]></step><step><![CDATA[Since 12 divides evenly into both the top and the bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>48/120| = |<eqn>4/10| = 0.4.]]></step></item><item><question eqn='0.75 d 2.5' type='2' txt='Find the value of'/><answer val='0.3'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.75/2.5)*(100/100)| = |<eqn>75/250|.]]></step><step><![CDATA[Since 25 divides evenly into both the top and the bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>75/250| = |<eqn>3/10| = 0.3.]]></step></item><item><question eqn='0.77 d 3.5' type='2' txt='Find the value of'/><answer val='0.22'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.77/3.5)*(100/100)| = |<eqn>77/350|.]]></step><step><![CDATA[Since 7 divides evenly into both the top and the bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>77/350| = |<eqn>11/50|.]]></step><step><![CDATA[To express this fraction in decimal form, we need the bottom to be a power of 10. Notice that we can multiply the top and bottom by 2:]]></step><step><![CDATA[|<eqn>(11/50)*(2/2)| = |<eqn>22/100| = 0.22.]]></step></item><item><question eqn='30 d 2.4' type='2' txt='Find the value of'/><answer val='12.5'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(30/2.4)*(10/10)| = |<eqn>300/24|.]]></step><step><![CDATA[Since 12 divides evenly into both the top and the bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>300/24| = |<eqn>25/2|.]]></step><step><![CDATA[To express this fraction in decimal form, we need the bottom to be a power of 10. Notice that we can multiply the top and bottom by 5:]]></step><step><![CDATA[|<eqn>(25/2)*(5/5)| = |<eqn>125/10| = 12.5.]]></step></item><item><question eqn='48 d 0.08' type='2' txt='Find the value of'/><answer val='600'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(48/0.08)*(100/100)| = |<eqn>4800/8|.]]></step><step><![CDATA[Since 8 divides evenly into 4800, our answer simplifies to]]></step><step><![CDATA[|<eqn>4800/8| = 600.]]></step></item><item><question eqn='32 d 0.016' type='2' txt='Find the value of'/><answer val='2000'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>1000/1000|:]]></step><step><![CDATA[|<eqn>(32/0.016)*(1000/1000)| = |<eqn>32000/16|.]]></step><step><![CDATA[Since 16 divides evenly into 32000, our answer simplifies to]]></step><step><![CDATA[|<eqn>32000/16| = 2000.]]></step></item><item><question eqn='7.2 d 0.51' type='1' txt='The value closest to'/><answer val='15'/><choices val='1|10|15|20'/><step><![CDATA[You may notice that 7.2 is close to 7.0 and 0.51 is close to 0.50, so |<eqn>7.2/0.51| is close to |<eqn>7.0/0.5|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(7.0/0.5)*(10/10)| = |<eqn>70/5|.]]></step><step><![CDATA[Since 5 divides evenly into 70, our answer simplifies to]]></step><step><![CDATA[|<eqn>70/5| = 14.]]></step><step><![CDATA[The answer to the original problem will be closest to 15.]]></step></item><item><question eqn='0.81 d 0.38' type='1' txt='The value closest to'/><answer val='2'/><choices val='1|2|10|20'/><step><![CDATA[You may notice that 0.81 is close to 0.8 and 0.38 is close to 0.40, so |<eqn>0.81/0.38| is close to |<eqn>0.8/0.4|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(0.8/0.4)*(10/10)| = |<eqn>8/4|.]]></step><step><![CDATA[Since 4 divides evenly into 8, our answer simplifies to]]></step><step><![CDATA[|<eqn>8/4| = 2.]]></step><step><![CDATA[The answer to the original problem will be closest to 2.]]></step></item><item><question eqn='0.639 d 0.079' type='1' txt='The value closest to'/><answer val='8.0'/><choices val='0.4|0.8|4|8'/><step><![CDATA[You may notice that 0.639 is close to 0.640 and 0.079 is close to 0.080, so |<eqn>0.639/0.079| is close to |<eqn>0.64/0.08|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.64/0.08)*(100/100)| = |<eqn>64/8|.]]></step><step><![CDATA[Since 8 divides evenly into 64, our answer simplifies to]]></step><step><![CDATA[|<eqn>64/8| = 8.]]></step><step><![CDATA[The answer to the original problem will be closest to 8.]]></step></item><item><question eqn='0.747 d 2.52' type='1' txt='The value closest to'/><answer val='0.3'/><choices val='0.1|0.3|0.5|0.7'/><step><![CDATA[You may notice that 0.747 is close to 0.750 and 2.52 is close to 2.50, so |<eqn>0.747/2.52| is close to |<eqn>0.75/2.5|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.75/2.5)*(100/100)| = |<eqn>75/250|.]]></step><step><![CDATA[Both top and bottom are divisible by 25, so we can simplify the answer:]]></step><step><![CDATA[|<eqn>75/250| = |<eqn>3/10| = 0.3.]]></step><step><![CDATA[The answer to the original problem will be closest to 0.3.]]></step></item><item><question eqn='0.039 d 0.21' type='1' txt='The value closest to'/><answer val='0.2'/><choices val='200|20|2|0.2'/><step><![CDATA[You may notice that 0.039 is close to 0.04 and 0.21 is close to 0.2, so |<eqn>0.039/0.21| is close to |<eqn>0.04/0.2|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.04/0.2)*(100/100)| = |<eqn>4/20|.]]></step><step><![CDATA[Both top and bottom are divisible by 4, so we can simplify the answer:]]></step><step><![CDATA[|<eqn>4/20| = |<eqn>1/5| = 0.2.]]></step><step><![CDATA[The answer to the original problem will be closest to 0.2.]]></step></item><item><question eqn='0.0048 d 5.01' type='1' txt='The value closest to'/><answer val='0.001'/><choices val='0.1|0.01|0.001|0.0001'/><step><![CDATA[You may notice that 0.0048 is close to 0.0050 and 5.01 is close to 5.00, so |<eqn>0.0048/5.01| is close to |<eqn>0.0050/5.00|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>1000/1000|:]]></step><step><![CDATA[|<eqn>(0.005/5)*(1000/1000)| = |<eqn>5/5000|.]]></step><step><![CDATA[Both top and bottom are divisible by 5, so we can simplify the answer:]]></step><step><![CDATA[|<eqn>5/5000| = |<eqn>1/1000| = 0.001.]]></step><step><![CDATA[The answer to the original problem will be closest to 0.001.]]></step></item><item><question eqn='1.98 d 0.297' type='1' txt='The value closest to'/><answer val='7'/><choices val='0.7|7|14|70'/><step><![CDATA[You may notice that 1.98 is close to 2.00 and 0.297 is close to 0.30, so |<eqn>1.98/0.297| is close to |<eqn>2/0.3|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(2/0.3)*(10/10)| = |<eqn>20/3|.]]></step><step><![CDATA[Since 3 divided into 21 is 7, we see that 3 divided into 20 should be nearly 7.]]></step><step><![CDATA[The answer to the original problem will be closest to 7.0.]]></step></item><item><question eqn='2.04 d 59.2' type='1' txt='The value closest to'/><answer val='0.03'/><choices val='0.03|0.003|0.0003|0.00003'/><step><![CDATA[You may notice that 2.04 is close to 2.00 and 59.2 is close to 60.0, so |<eqn>2.04/59.2| is close to |<eqn>2/60|.]]></step><step><![CDATA[These are integers, both of which are divisible by 2, so we have]]></step><step><![CDATA[|<eqn>2/60| = |<eqn>1/30|.]]></step><step><![CDATA[We know that |<eqn>1/3| = |<eqn>0.333..., where the 3's continue forever, so |<eqn>1/30| = 0.0333....]]></step><step><![CDATA[The answer to the original problem will be closest to 0.03.]]></step></item><item><question eqn='11.4 d 19' type='2' txt='Find the value of'/><answer val='0.6'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(11.4/19)*(10/10)| = |<eqn>114/190|.]]></step><step><![CDATA[Since 19 divides evenly into both the top and the bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>114/190| = |<eqn>6/10| = 0.6.]]></step></item><item><question eqn='16.24 d 0.14' type='2' txt='Find the value of'/><answer val='116'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(16.24/0.14)*(100/100)| = |<eqn>1624/14|.]]></step><step><![CDATA[Since 14 divides evenly into 1624, our answer simplifies to]]></step><step><![CDATA[|<eqn>1624/14| = 116.]]></step></item><item><question eqn='96.6 d 0.42' type='2' txt='Find the value of'/><answer val='230'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(96.6/0.42)*(100/100)| = |<eqn>9660/42|.]]></step><step><![CDATA[Since 42 divides evenly into 9660, our answer simplifies to]]></step><step><![CDATA[|<eqn>9660/42| = 230.]]></step></item><item><question eqn='13.5 d 0.03' type='2' txt='Find the value of'/><answer val='450'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(13.5/0.03)*(100/100)| = |<eqn>1350/3|.]]></step><step><![CDATA[Since 3 divides evenly into 1350, our answer simplifies to]]></step><step><![CDATA[|<eqn>1350/3| = 450.]]></step></item><item><question eqn='4.5 d 0.75' type='2' txt='Find the value of'/><answer val='6'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(4.5/0.75)*(100/100)| = |<eqn>450/75|.]]></step><step><![CDATA[Since 75 divides evenly into 450, our answer simplifies to]]></step><step><![CDATA[|<eqn>450/75| = 6.]]></step></item><item><question eqn='2.07 d 0.9' type='2' txt='Find the value of'/><answer val='2.3'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(2.07/0.9)*(100/100)| = |<eqn>207/90|.]]></step><step><![CDATA[Since 9 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>207/90| = |<eqn>23/10| = 2.3.]]></step></item><item><question eqn='1.08 d 2.7' type='2' txt='Find the value of'/><answer val='0.4'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(1.08/2.7)*(100/100)| = |<eqn>108/270|.]]></step><step><![CDATA[Since 27 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>108/270| = |<eqn>4/10| = 0.4.]]></step></item><item><question eqn='29.12 d 1.3' type='2' txt='Find the value of'/><answer val='22.4'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(29.12/1.3)*(100/100)| = |<eqn>2912/130|.]]></step><step><![CDATA[Since 13 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>2912/130| = |<eqn>224/10| = 22.4.]]></step></item><item><question eqn='33.8 d 0.26' type='2' txt='Find the value of'/><answer val='130'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(33.8/0.26)*(100/100)| = |<eqn>3380/26|.]]></step><step><![CDATA[Since 26 divides evenly into 3380, our answer simplifies to]]></step><step><![CDATA[|<eqn>3380/26| = 130.]]></step></item><item><question eqn='186.9 d 8.9' type='2' txt='Find the value of'/><answer val='21'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(186.9/8.9)*(10/10)| = |<eqn>1869/89|.]]></step><step><![CDATA[Since 89 divides evenly into 1869, our answer simplifies to]]></step><step><![CDATA[|<eqn>1869/89| = 21.]]></step></item><item><question eqn='592 d 0.8' type='2' txt='Find the value of'/><answer val='740'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(592/0.8)*(10/10)| = |<eqn>5920/8|.]]></step><step><![CDATA[Since 8 divides evenly into 5920, our answer simplifies to]]></step><step><![CDATA[|<eqn>5920/8| = 740.]]></step></item><item><question eqn='0.1 d 1.25' type='2' txt='Find the value of'/><answer val='0.08'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.1/1.25)*(100/100)| = |<eqn>10/125|.]]></step><step><![CDATA[Since 5 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>10/125| = |<eqn>2/25|.]]></step><step><![CDATA[To express this fraction in decimal form, we need the bottom to be a power of 10. Notice that we can multiply the top and bottom by 4:]]></step><step><![CDATA[|<eqn>(2/25)*(4/4)| = |<eqn>8/100| = 0.08.]]></step></item><item><question eqn='5.04 d 0.07' type='2' txt='Find the value of'/><answer val='72'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(5.04/0.07)*(100/100)| = |<eqn>504/7|.]]></step><step><![CDATA[Since 7 divides evenly into 504, our answer simplifies to]]></step><step><![CDATA[|<eqn>504/7| = 72.]]></step></item><item><question eqn='8.36 d 1.9' type='2' txt='Find the value of'/><answer val='4.4'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(8.36/1.9)*(100/100)| = |<eqn>836/190|.]]></step><step><![CDATA[Since 19 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>836/190| = |<eqn>44/10| = 4.4.]]></step></item><item><question eqn='3.57 d 1.7' type='2' txt='Find the value of'/><answer val='2.1'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(3.57/1.7)*(100/100)| = |<eqn>357/170|.]]></step><step><![CDATA[Since 17 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>357/170| = |<eqn>21/10| = 2.1.]]></step></item><item><question eqn='9.89 d 2.3' type='2' txt='Find the value of'/><answer val='4.3'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(9.89/2.3)*(100/100)| = |<eqn>989/230|.]]></step><step><![CDATA[Since 23 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>989/230| = |<eqn>43/10| = 4.3.]]></step></item><item><question eqn='2.88 d 16' type='2' txt='Find the value of'/><answer val='0.18'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(2.88/16)*(100/100)| = |<eqn>288/1600|.]]></step><step><![CDATA[Since 16 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>288/1600| = |<eqn>18/100| = 0.18.]]></step></item><item><question eqn='1.98 d 0.11' type='2' txt='Find the value of'/><answer val='18'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(1.98/0.11)*(100/100)| = |<eqn>198/11|.]]></step><step><![CDATA[Since 11 divides evenly into 198, our answer simplifies to]]></step><step><![CDATA[|<eqn>198/11| = 18.]]></step></item><item><question eqn='1.95 d 15' type='2' txt='Find the value of'/><answer val='0.13'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(1.95/15)*(100/100)| = |<eqn>195/1500|.]]></step><step><![CDATA[Since 15 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>195/1500| = |<eqn>13/100| = 0.13.]]></step></item><item><question eqn='41.31 d 17' type='2' txt='Find the value of'/><answer val='2.43'/><choices val=''/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(41.31/17)*(100/100)| = |<eqn>4131/1700|.]]></step><step><![CDATA[Since 17 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>4131/1700| = |<eqn>243/100| = 2.43.]]></step></item><item><question eqn='7.7 d 0.51' type='1' txt='The value closest to'/><answer val='15'/><choices val='1|10|15|20'/><step><![CDATA[You may notice that 7.7 is close to 8 and 0.51 is close to 0.5, so |<eqn>7.7/0.51| is close to |<eqn>8/0.5|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(8/0.5)*(10/10)| = |<eqn>80/5|.]]></step><step><![CDATA[Since 5 divides evenly into 80, our answer simplifies to]]></step><step><![CDATA[|<eqn>80/5| = 16.]]></step><step><![CDATA[Of all of the choices, the result is closest to 15.]]></step></item><item><question eqn='6.8 d 0.51' type='1' txt='The value closest to'/><answer val='15'/><choices val='1|10|15|20'/><step><![CDATA[You may notice that 6.8 is close to 7 and 0.51 is close to 0.5, so |<eqn>6.8/0.51| is close to |<eqn>7/0.5|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(7/0.5)*(10/10)| = |<eqn>70/5|.]]></step><step><![CDATA[Since 5 divides evenly into 70, our answer simplifies to]]></step><step><![CDATA[|<eqn>70/5| = 14.]]></step><step><![CDATA[Of all of the choices, the result is closest to 15.]]></step></item><item><question eqn='3.8 d 0.83' type='1' txt='The value closest to'/><answer val='5'/><choices val='1|3|5|7'/><step><![CDATA[You may notice that 3.8 is close to 4 and 0.83 is close to 0.8, so |<eqn>3.8/0.83| is close to |<eqn>4/0.8|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(4/0.8)*(10/10)| = |<eqn>40/8|.]]></step><step><![CDATA[Since 8 divides evenly into 40, our answer simplifies to]]></step><step><![CDATA[|<eqn>40/8| = 5.]]></step><step><![CDATA[The answer to the original problem will be closest to 5.]]></step></item><item><question eqn='0.0012 d 0.021' type='1' txt='The value closest to'/><answer val='0.05'/><choices val='0.05|0.005|0.0005|0.00005'/><step><![CDATA[You may notice that 0.0012 is close to 0.001 and 0.021 is close to 0.02, so |<eqn>0.0012/0.021| is close to |<eqn>0.001/0.02|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>1000/1000|:]]></step><step><![CDATA[|<eqn>(0.001/0.02)*(1000/1000)| = |<eqn>1/20|.]]></step><step><![CDATA[We know that |<eqn>1/2| = |<eqn>0.5, so |<eqn>1/20| = 0.05.]]></step><step><![CDATA[The answer to the original problem will be closest to 0.05.]]></step></item><item><question eqn='0.41 d 0.082' type='1' txt='The value closest to'/><answer val='5'/><choices val='0.5|5|10|50'/><step><![CDATA[You may notice that 0.41 is close to 0.4 and 0.082 is close to 0.08, so |<eqn>0.41/0.082| is close to |<eqn>0.4/0.08|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.4/0.08)*(100/100)| = |<eqn>40/8|.]]></step><step><![CDATA[Since 8 divides evenly into 40, our answer simplifies to]]></step><step><![CDATA[|<eqn>40/8| = 5.]]></step><step><![CDATA[The answer to the original problem will be closest to 5.]]></step></item><item><question eqn='0.064 d 0.31' type='1' txt='The value closest to'/><answer val='0.2'/><choices val='200|20|2|0.2'/><step><![CDATA[You may notice that 0.064 is close to 0.06 and 0.31 is close to 0.3, so |<eqn>0.064/0.31| is close to |<eqn>0.06/0.3|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.06/0.3)*(100/100)| = |<eqn>6/30|.]]></step><step><![CDATA[Since 3 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>6/30| = |<eqn>2/10| = 0.2.]]></step><step><![CDATA[The answer to the original problem will be closest to 0.2.]]></step></item><item><question eqn='4.522 d 0.88' type='1' txt='The value closest to'/><answer val='5'/><choices val='0.05|0.5|5.0|50'/><step><![CDATA[You may notice that 4.522 is close to 4.5 and 0.88 is close to 0.9, so |<eqn>4.522/0.88| is close to |<eqn>4.5/0.9|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(4.5/0.9)*(10/10)| = |<eqn>45/9|.]]></step><step><![CDATA[Since 9 divides evenly into 45, our answer simplifies to]]></step><step><![CDATA[|<eqn>45/9| = 5.]]></step><step><![CDATA[The answer to the original problem will be closest to 5.]]></step></item><item><question eqn='1.528 d 0.47' type='1' txt='The value closest to'/><answer val='3'/><choices val='0.03|0.3|3.0|30'/><step><![CDATA[You may notice that 1.528 is close to 1.5 and 0.47 is close to 0.5, so |<eqn>1.528/0.47| is close to |<eqn>1.5/0.5|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(1.5/0.5)*(10/10)| = |<eqn>15/5|.]]></step><step><![CDATA[Since 5 divides evenly into 15, our answer simplifies to]]></step><step><![CDATA[|<eqn>15/5| = 3.]]></step><step><![CDATA[The answer to the original problem will be closest to 3.]]></step></item><item><question eqn='1.813 d 0.59' type='1' txt='The value closest to'/><answer val='3'/><choices val='0.03|0.3|3.0|30'/><step><![CDATA[You may notice that 1.813 is close to 1.8 and 0.59 is close to 0.6, so |<eqn>1.813/0.59| is close to |<eqn>1.8/0.6|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(1.8/0.6)*(10/10)| = |<eqn>18/6|.]]></step><step><![CDATA[Since 6 divides evenly into 18, our answer simplifies to]]></step><step><![CDATA[|<eqn>18/6| = 3.]]></step><step><![CDATA[The answer to the original problem will be closest to 3.]]></step></item><item><question eqn='0.116 d 0.04' type='1' txt='The value closest to'/><answer val='3'/><choices val='3|4|12|30'/><step><![CDATA[You may notice that 0.116 is close to 0.12, so |<eqn>0.116/0.04| is close to |<eqn>0.12/0.04|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.12/0.04)*(100/100)| = |<eqn>12/4|.]]></step><step><![CDATA[Since 4 divides evenly into 12, our answer simplifies to]]></step><step><![CDATA[|<eqn>12/4| = 3.]]></step><step><![CDATA[The answer to the original problem will be closest to 3.]]></step></item><item><question eqn='220 d 2.5' type='1' txt='The value closest to'/><answer val='90'/><choices val='25|80|90|110'/><step><![CDATA[We can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(220/2.5)*(10/10)| = |<eqn>2200/25|.]]></step><step><![CDATA[Since 25 divides evenly into 2200, our answer simplifies to]]></step><step><![CDATA[|<eqn>2200/25| = 90.]]></step><step><![CDATA[The answer to the original problem will be closest to 90.]]></step></item><item><question eqn='4.22 d 7.1' type='1' txt='The value closest to'/><answer val='0.6'/><choices val='0.4|0.5|0.6|0.7'/><step><![CDATA[You may notice that 4.22 is close to 4.2 and 7.1 is close to 7, so |<eqn>4.22/7.1| is close to |<eqn>4.2/7|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(4.2/7)*(10/10)| = |<eqn>42/70|.]]></step><step><![CDATA[Since 7 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>42/70| = |<eqn>6/10| = 0.6.]]></step><step><![CDATA[The answer to the original problem will be closest to 0.6.]]></step></item><item><question eqn='0.153 d 0.3' type='1' txt='The value closest to'/><answer val='0.5'/><choices val='0.1|0.2|0.4|0.5'/><step><![CDATA[You may notice that 0.153 is close to 0.15, so |<eqn>0.153/0.3| is close to |<eqn>0.15/0.3|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>100/100|:]]></step><step><![CDATA[|<eqn>(0.15/0.3)*(100/100)| = |<eqn>15/30|.]]></step><step><![CDATA[Since 3 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>15/30| = |<eqn>5/10| = 0.5.]]></step><step><![CDATA[The answer to the original problem will be closest to 0.5.]]></step></item><item><question eqn='8.7 d 5.8' type='1' txt='The value closest to'/><answer val='1.5'/><choices val='0.1|1.3|1.5|2.0'/><step><![CDATA[You may notice that 8.7 is close to 9 and 5.8 is close to 6, so |<eqn>8.7/5.8| is close to |<eqn>9/6|.]]></step><step><![CDATA[Since 3 divides evenly into both top and bottom, our answer simplifies to]]></step><step><![CDATA[|<eqn>9/6| = |<eqn>3/2| = 1.5.]]></step><step><![CDATA[The answer to the original problem will be closest to 1.5.]]></step></item><item><question eqn='9.14 d 1.29' type='1' txt='The value closest to'/><answer val='7.0'/><choices val='0.7|0.9|7.0|9.0'/><step><![CDATA[You may notice that 9.14 is close to 9.1 and 1.29 is close to 1.3, so |<eqn>9.14/1.29| is close to |<eqn>9.1/1.3|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(9.1/1.3)*(10/10)| = |<eqn>91/13|.]]></step><step><![CDATA[Since 7 divides evenly into 91, our answer simplifies to]]></step><step><![CDATA[|<eqn>91/13| = 7.]]></step><step><![CDATA[The answer to the original problem will be closest to 7.]]></step></item><item><question eqn='9.61 d 1.58' type='1' txt='The value closest to'/><answer val='5'/><choices val='1|5|9|10'/><step><![CDATA[You may notice that 9.61 is close to 9.6 and 1.58 is close to 1.6, so |<eqn>9.6/1.6| is close to |<eqn>9.6/1.6|.]]></step><step><![CDATA[Now we can convert both the top and the bottom into integers by multiplying by |<eqn>10/10|:]]></step><step><![CDATA[|<eqn>(9.6/1.6)*(10/10)| = |<eqn>96/16|.]]></step><step><![CDATA[Since 16 divides evenly into 96, our answer simplifies to]]></step><step><![CDATA[|<eqn>96/16| = 6.]]></step><step><![CDATA[Of all of the choices, the result is closest to 5.]]></step></item></data>";

/** END OF DATA DEFINITIONS
        /**
        API for fetching/parsing data for current topic
        */
    mngr.fetchTopicData = function () {
        mngr.readyStatus = 0;
/**
        TODO: fetch data from data base for data_id
        the data_id can be get from mngr.data_id
        */
    }
    mngr.onTopicDataReady = function () {
/**
        TODO: to be called when data is fully loaded and ready to be parsed
        */
        mngr.readyStatus = 1;
    }
    mngr.parseTopicData = function (data_string) {
/*if(this.topic_id==4||this.topic_id==5||this.topic_id==7||this.topic_id==22||this.topic_id==28){
                        mngr.topic_data=parseXML(data_string)
                }*/
        switch (this.topic_id) {
        case 4:
        case 5:
        case 7:
        case 22:
        case 28:
            mngr.data_id = mngr.topic_id;
            mngr.topic_data = mngr.parseXML(data_string, mngr.topic_id);
            mngr.genProblems();
            break;
        case 1:
        case 2:
        case 3:
        case 6:
        case 8:
        case 9:
        case 10:
        case 18:
        case 23:
        case 24:
        case 25:
            mngr.data_id = mngr.topic_id == 9 ? 8 : mngr.topic_id == 23 ? 10 : mngr.topic_id;
            mngr.topic_data = data_string;
            mngr.genProblems();
            break;
        }
    }

    mngr.loadXMLString = function (xmlString) {
        // ObjectExists checks if the passed parameter is not null.
        // isString (as the name suggests) checks if the type is a valid string.
        if (ObjectExists(xmlString) && isString(xmlString)) {
            var xDoc;
            // The GetBrowserType function returns a 2-letter code representing
            // ...the type of browser.
            var bType = GetBrowserType();

            switch (bType) {
            case "ie":
                // This actually calls into a function that returns a DOMDocument
                // on the basis of the MSXML version installed.
                // Simplified here for illustration.
                xDoc = new ActiveXObject("MSXML2.DOMDocument")
                xDoc.async = false;
                xDoc.loadXML(xmlString);
                break;
            default:
                var dp = new DOMParser();
                xDoc = dp.parseFromString(xmlString, "text/xml");
                break;
            }
            return xDoc;
        } else return null;
    }
    mngr.parseXML = function (dat, topicID) {
        var xmlD = mngr.loadXMLString(dat)
        var root = xmlD.documentElement;
        var children = root.childNodes;
        var cL = children.length;
        var _data = [];
        var _data1 = [];
        var _data2 = [];
        var _steps = [];
        var _substeps = [];
        var child, q, a, steps, sub, cNL, seq, stxt;
        for (var i = 0; i < cL; i++) {
            child = children[i];
            sub = child.childNodes;
            cNL = sub.length;
            if (topicID == 4 || topicID == 5) {
                q = sub[0].attributes.eqn;
                a = sub[1].attributes.val;
                _substeps = [];
                for (var j = 2; j < cNL; j++) {
                    seq = sub[j].attributes.eqn;
                    stxt = sub[j].attributes.txt;
                    _substeps.push([seq, stxt]);
                }
                _data.push([
                    [q, _substeps], a]);
            } else if (topicID == 7) {
                ql = child.attributes.lexp;
                qr = child.attributes.rexp;
                rep = false;
                repL = false;
                repR = false;
                if (ql.indexOf("R") != -1) {
                    ql = ql.split("R")[0];
                    repL = rep = true;
                }
                if (qr.indexOf("R") != -1) {
                    qr = qr.split("R")[0];
                    repR = rep = true;
                }
                a = child.attributes.val;
                expl = child.attributes.expType;
                _data.push([
                    [
                        [ql, qr], expl, rep, [repL, repR]
                    ], a]);
            } else if (topicID == 22) {
                q = sub[0].firstChild.nodeValue;
                a = sub[1].firstChild.nodeValue;
                var exp = (sub[2].firstChild.nodeValue);
                exp = exp.split("&#215;").join("�");
                var vars = sub[1].attributes.vars;
                _data.push([
                    [q, exp, vars], a]);
            } else if (topicID == 28) {
                q = sub[0].attributes.eqn;
                q = q.split(" ").join("").split("d");
                t = sub[0].attributes.type;
                txt = sub[0].attributes.txt;
                a = sub[1].attributes.val;
                c = sub[2].attributes.val;
                _substeps = [];
                for (var j = 3; j < cNL; j++) {
                    stxt = sub[j].firstChild.nodeValue;
                    _substeps.push([stxt]);
                }
                if (String(t) == "1") {
                    _data1.push([
                        [q, t, c, txt, _substeps], a]);
                } else {
                    _data2.push([
                        [q, t, c, txt, _substeps], a]);
                }
            }
        }
        if (_data1.length) {
            _data = [_data1, _data2];
        }
        return _data;
    } /** END OF API methods for fetching/parsing data for current topic */
    mngr.reset = function () {
        this.initialized = false;
        this.current_pblm_index = 0;
        this.completed_pblms = 0;
    }

    mngr.getMultiplesDisplay = function (a, b) {
        var str = '';
        for (var i = 1; i < b; i++) {
            str += a * i + ',';
        }
        str += '<b>' + (a * b) + '</b>';
        return str;
    }
    mngr.getGCFExplain = function (num, den, em, gcf) {
        var v1
        var n, d, e, g
        if (arguments.length == 4) {
            n = arguments[0];
            d = arguments[1];
            e = arguments[2];
            g = arguments[3];
        } else {
            n = arguments[0];
            d = arguments[1];
            e = null;
            g = arguments[2];
        }
        var pfData = Math.getCommonPrimeFactors(n, d, e, g);
        var neq = pfData[0];
        var deq = pfData[1];
        var eeq = pfData[5];
        var prodStr = pfData[4].length > 1 ? " = " + Math.getProduct(pfData[4]) : "";
        var geq = "<b>GCF = " + pfData[4].join(" * ") + prodStr + "</b>";
        var exp = "To find the greatest common factor (GCF) of two or more numbers, first write out their prime factorizations.<br/>";
        var exp_sub = "";
        if (g == 1) {
            exp_sub = "In this case there are no common prime factors. So,";
        }
        exp += "\t" + neq + "<br/>";
        exp += "\t" + deq + "<br/>";
        if (eeq) {
            exp += "\t" + eeq + "<br/>";
        }
        exp += "The GCF is the product of all common factors. " + exp_sub + "<br/>";
        exp += "\t" + geq + ".";
        return exp;
    }
    mngr.getIndexArr = function (f) {
        var l = this['arr_' + f].length;
        return Array.randomNumberArray(0, l - 1, 100, true);
    }
    mngr.random = function (n) {
        return Math.floor(Math.random() * n)
    }
    mngr.genMultiFracProb = function (type) {
        var ind, indT;
        var arr;
        switch (type) {
        case 'pp':
            ind = ++this.pp_ind;
            indT = this.arr_pp_l;
            if (ind == indT) {
                ind = this.pp_ind = 0;
                this.arr_pp_i = this.getIndexArr('pp');
            }
            break;
        case 'pw':
            ind = ++this.pw_ind;
            indT = this.arr_pw_l;
            if (ind == indT) {
                ind = this.pw_ind = 0;
                this.arr_pw_i = this.getIndexArr('pw');
            }
            break;
        case 'pi':
            ind = ++this.pi_ind;
            indT = this.arr_pi_l;
            if (ind == indT) {
                ind = this.pi_ind = 0;
                this.arr_pi_i = this.getIndexArr('pi');
            }
            break;
        case 'ii':
            ind = ++this.ii_ind;
            indT = this.arr_ii_l;
            if (ind == indT) {
                ind = this.ii_ind = 0;
                this.arr_ii_i = this.getIndexArr('ii');
            }
            break;
        case 'iw':
            ind = ++this.iw_ind;
            indT = this.arr_iw_l;
            if (ind == indT) {
                ind = this.iw_ind = 0;
                this.arr_iw_i = this.getIndexArr('iw');
            }
            break;
        }
        var arr = this['arr_' + type];
        var arri = this['arr_' + type + "_i"];
        var p = arr[arri[ind]].split("|");
        var l = Math.floor(Math.random() * 2);
        var r = (l + 1) % 2;
        var pl = p[l];
        var pr = p[r];
        var strL;
        var strR;
        strL = pl.split("/");
        strR = pr.split("/");
        strL[1] = strL[1] ? strL[1] : 1;
        strR[1] = strR[1] ? strR[1] : 1;
        var n = strL[0] * strR[0];
        var d = (strL[1] ? strL[1] : 1) * (strR[1] ? strR[1] : 1);
        var ans1 = Math.simpleFrac(n + "/" + d);
        var nr = ans1.split("/");
        n = nr[0] * 1;
        d = nr[1] * 1;
        var ans2 = n > d ? "[" + Math.floor(n / d) + "]" + ((n % d) + "/" + d) : undefined;
        if (d === 1) {
            ans1 = String(n);
            ans2 = undefined;
        }
        var a = [
            [pl, pr, [strL, strR], nr],
            {
                frac: n + "/" + d,
                val: ans1
            }, {
                frac: ans2,
                val: ans2
            }];
        return a
    }
    mngr.genProblems = function () {
        console.log("FLASH_CARD_MNGR_CALLING GEN PROBLEMS - INSIDE METHOD")
        var totalQuest = mngr.totalQuest;
        var actType = mngr.actType;
        var level = mngr.level;
        var random = mngr.random;
        var rand = Math.rand;
        mngr.topic_data = mngr.topic_data ? mngr.topic_data : 'temp';
        if (mngr.topic_data == 'temp') {
/*if(this.topic_id==4||this.topic_id==5||this.topic_id==7||this.topic_id==22||this.topic_id==28){
                        mngr.topic_data=mngr.parseXML(eval("topic_data_"+this.topic_id));
                }else{
                        mngr.topic_data=eval("topic_data_"+this.topic_id);
                }*/
            switch (this.topic_id) {
            case 4:
            case 5:
            case 7:
            case 22:
            case 28:
                mngr.data_id = mngr.topic_id;
                mngr.topic_data = mngr.parseXML(eval("topic_data_" + mngr.data_id));
                break;
            case 1:
            case 2:
            case 3:
            case 6:
            case 8:
            case 9:
            case 10:
            case 18:
            case 23:
            case 24:
            case 25:
                mngr.data_id = mngr.topic_id == 9 ? 8 : mngr.topic_id == 23 ? 10 : mngr.topic_id;
                mngr.topic_data = eval("topic_data_" + mngr.data_id);
                break;
            }
        }
        if (this.topic_id == 0) {
            var gdata = [];
            gdata.randomNumbers(0, 12, 13, true);
            var i = 0;
            var l = gdata.length;
            var c = 0;
            var ldata = [];
            var rdata = [];
            while (gdata.length) {
                ldata[i] = gdata[c];
                rdata[i] = gdata[Math.floor(Math.random() * gdata.length)];
                gdata.shift();
                if (ldata[i] != rdata[i]) {
                    ind = gdata.getIndex(rdata[i]);
                    gdata.splice(ind, 1);
                    gdata.unshift(rdata[i]);
                }
                if (i > (20)) {
                    break;
                }
                i++;
            }
            var _data = [];
            for (var i = 0; i < ldata.length; i++) {
                //console.log("MULTI_DATA:"+ldata[i]+":"+rdata[i])
                _data.push([
                    [ldata[i], rdata[i]], ldata[i] * rdata[i]
                ]);
            }
            _data.shuffle();
        }
        if (this.topic_id == 1 || this.topic_id == 2 || this.topic_id == 6) {
            var dataStr = mngr.topic_data;
            var dataArr = dataStr.split(" ").join("").split(",");
            var _data = [];
            var ldata;
            var rdata;
            var dataEl;
            var elL, elR, ans, dans;
            for (var i = 0; i < dataArr.length; i++) {
                dataEl = this.topic_id == 6 ? dataArr[i] : dataArr[i].split("+");
                ldata = this.topic_id == 6 ? dataEl : dataEl[0];
                rdata = dataEl[1];
                elL = ldata.split("/");
                elR = rdata ? rdata.split("/") : null;
                if (ldata != "" && ldata != undefined) {
/*if(console){
                        console.log("ADDFRAC_DATA:"+ldata+":"+rdata);
                        }*/
                    ans = this.topic_id == 6 ? Math.simpleFrac(ldata) : Math.addFrac(ldata, rdata);
                    ans = this.topic_id == 6 ? {
                        frac: ans,
                        val: ans
                    } : ans;
                    dans = (ans.val).split('/');
                    _data.push([
                        [ldata, rdata, [elL, elR], dans], ans]);
                }
            }
            _data.shuffle();
        }
        if (this.topic_id == 3) {
            var dataStr = mngr.topic_data;
            var dataArr = dataStr.split(",");
            var _data = [];
            var ldata;
            var rdata;
            var dataEl;
            for (var i = 0; i < dataArr.length; i++) {
                dataEl = dataArr[i].split("\t");
                ldata = dataEl[0];
                rdata = dataEl[1];
                cdata = ldata.split(" ").join("");
                if (cdata != "" && ldata != undefined) {
                    _data.push([
                        [ldata], rdata]);
                }
            }
            _data.shuffle();
        }
        if (this.topic_id == 10 || this.topic_id == 23) {
            var dataStr = mngr.topic_data;
            var dataArr = dataStr.split(":");
            mngr.arr_pp = dataArr[0].split(",");
            //
            mngr.arr_pi = dataArr[1].split(",");
            //
            mngr.arr_pw = dataArr[2].split(",");
            //
            mngr.arr_ii = dataArr[3].split(",");
            //
            mngr.arr_iw = dataArr[4].split(",");
            //
            mngr.arr_pp_i = mngr.getIndexArr('pp')
            mngr.arr_pw_i = mngr.getIndexArr('pw')
            mngr.arr_pi_i = mngr.getIndexArr('pi')
            mngr.arr_ii_i = mngr.getIndexArr('ii')
            mngr.arr_iw_i = mngr.getIndexArr('iw')
            //
            mngr.arr_pp_l = mngr.arr_pp_i.length
            mngr.arr_pw_l = mngr.arr_pw_i.length
            mngr.arr_pi_l = mngr.arr_pi_i.length
            mngr.arr_ii_l = mngr.arr_ii_i.length
            mngr.arr_iw_l = mngr.arr_iw_i.length
            //
            mngr.pp_ind = 0;
            mngr.pw_ind = 0;
            mngr.pi_ind = 0;
            mngr.ii_ind = 0;
            mngr.iw_ind = 0;
            _data = [];
            var tarr1 = ['pp', 'pp', 'pp', 'pp', 'pp', 'pw', 'pw', 'pi', 'ii', 'iw'];
            var tarr2 = ['pw', 'pw', 'pw', 'pw', 'pw', 'iw', 'iw', 'pw', 'iw', 'iw'];
            var tarr = this.topic_id == 10 ? tarr1 : tarr2;
            for (var i = 0; i < 10; i++) {
                _data.push(this.genMultiFracProb(tarr[i]));
            }
            _data.shuffle();
        }
        if (this.topic_id == 14) {
            var m1 = Array.randomNumberArray(1, 20, 3, true);
            var m2 = Array.randomNumberArray(1, 20, 3, true);
            var n2 = Array.randomNumberArray(2, 20, 3, true);
            var m3 = Array.randomNumberArray(1, 20, 1, true)[0];
            var m4 = Array.randomNumberArray(1, 20, 1, true)[0];
            var n4 = Array.randomNumberArray(1, 20, 1, true)[0];
            var m5 = Array.randomNumberArray(1, 100, 1, true)[0];
            var m6 = Array.randomNumberArray(1, 9, 1, true)[0];
            var n6 = Array.randomNumberArray(2, 9, 1, true)[0];
            var k6 = Array.randomNumberArray(1, n6 - 1, 1, true)[0];
            var _data = [];
            var m, n, k;
            var ldata, rdata;
            var type;
            //var random=mngr.random;
            var si = ""
            for (var i = 0; i < mngr.totalQuest; i++) {
                si = ""
                if (i < 3) {
                    type = 1;
                    m = m1[i];
                    ldata = m;
                    rdata = '1/' + m;
                }
                if (i >= 3 && i < 6) {
                    type = 2;
                    m = m2[i - 3];
                    n = n2[i - 3];
                    if (m % n == 0) {
                        m = n2[i - 3];
                        n = m2[i - 3];
                    }
                    if (m == n) {
                        n = n + 1;
                    }
                    ldata = Math.simpleFrac(m + "/" + n);
                    rdata = Math.simpleFrac(n + "/" + m);
                }
                if (i == 6) {
                    type = 3;
                    ldata = m = -m3;
                    rdata = "-1/" + m3;
                }
                if (i == 7) {
                    type = 4;
                    m = m4;
                    n = n4;
                    if (m % n == 0) {
                        m = n4;
                        n = m4;
                    }
                    if (m == n) {
                        n = n + 1;
                    }
                    si = "-"
                    ldata = "-" + (Math.simpleFrac(m + "/" + n));
                    rdata = "-" + (Math.simpleFrac(n + "/" + m));
                }
                if (i == 8) {
                    type = 5;
                    if (m5 % 10 == 0) {
                        m5 = m5 + 1;
                    }
                    m = [-1, 1][random(2)] * m5;
                    ldata = m / 10;
                    if (m > 0) {
                        rdata = "10/" + m;
                    } else {
                        rdata = "-10/" + Math.abs(m);
                    }

                    rdata = Math.simpleFrac(rdata);
                }
                if (i == 9) {
                    type = 6;
                    var s = [-1, 1][random(2)];
                    si = s == -1 ? "-" : "";
                    m = m6;
                    var f = Math.simpleFrac(k6 + "/" + n6).split("/");
                    n = f[0];
                    k = f[1];
                    ldata = si + m + "|" + (n + "/" + k);
                    var m0 = (m * k) + (n * 1);
                    rdata = si + k + "/" + m0;
                }
                _data.push([
                    [
                        [si, m, n, k], type, ldata],
                    {
                        frac: rdata,
                        val: rdata
                    }]);
            }
            //_data.shuffle();
        }
        if (this.topic_id == 15) {

            var k1 = Array.randomNumberArray(2, 6, 1, true);
            var m1 = Array.randomNumberArray(1, 10, 1, true);
            var n1 = Array.randomNumberArray(1, 10, 1, true);
            var y1 = Array.randomNumberArray(0, 5, 1, true);
            var y1v = ['a', 'b', 'c', 'k', 'g', 'h'];
            //
            var k2 = Array.randomNumberArray(-10, 10, 3, true);
            var m2 = Array.randomNumberArray(1, 10, 3, true);
            var n2 = Array.randomNumberArray(1, 10, 3, true);
            var y2 = Array.randomNumberArray(0, 5, 3, true);
            var y2v = y1v;
            //
            var j3 = Array.randomNumberArray(-10, 10, 2, true);
            var k3 = Array.randomNumberArray(-10, 10, 2, true);
            var m3 = Array.randomNumberArray(1, 10, 2, true);
            var n3 = Array.randomNumberArray(1, 10, 2, true);
            var y3 = Array.randomNumberArray(0, 5, 2, true);
            var z3 = Array.randomNumberArray(0, 5, 2, true);
            var y3v = y1v;
            var z3v = ['d', 'e', 'j', 'p', 'q', 's'];
            //
            var _data = [];
            //var m, n, k, j, y, z;
            var ldata, rdata;
            var op1, op2, k0, j0, m0, n0;
            var type;
            //var random=mngr.random;
            for (var i = 0; i < mngr.totalQuest; i++) {

                var m, n, k, j, y, z;
                if (i == 0) {
                    type = 1;
                    k = k1[0];
                    m = m1[0];
                    n = n1[0];
                    y = y1v[y1[0]];
                    var dm = m == 1 ? "" : m;
                    ldata = k + "(" + dm + "" + y + "+" + n + ")";
                    rdata = (k * m) == 1 ? "" : (k * m) + "" + y + "+" + (k * n);
                }
                if (i > 0 && i <= 3) {
                    type = 2;
                    k = k2[i - 1];
                    m = m2[i - 1];
                    n = n2[i - 1];
                    k = k === 1 ? (random(3) + 2) : k;
                    switch (random(4)) {
                    case 0:
                        break;
                    case 1:
                        n = -n;
                        break;
                    case 2:
                        m = -m;
                        break;
                    case 3:
                        m = -m;
                        n = -n;
                        break;
                    }
                    //op = "*";
                    y = y1v[y2[i - 1]];
                    op1 = k * n < 0 ? "-" : "+";
                    var km = (k * m) == 1 ? "" : (k * m);
                    rdata = (km) + "" + y + op1 + Math.abs((k * n));
                    k0 = k;
                    if (n < 0) {
                        n0 = "(" + n + ")";
                    } else {
                        n0 = n;
                    }
                    m0 = m;
                    op1 = "+";
                    var dm = m0 == 1 ? "" : m0 == -1 ? "-" : m0;
                    ldata = k0 + "(" + dm + "" + y + "+" + n0 + ")";
                }
                if (i > 3 && i < 6) {
                    type = 3;
                    j = j3[i - 4];
                    k = k3[i - 4];
                    m = m3[i - 4];
                    n = n3[i - 4];
                    k = k === 1 ? random(3) + 2 : k;
                    switch (random(4)) {
                    case 0:
                        op1 = "+";
                        op2 = "+";
                        break;
                    case 1:
                        op1 = "+";
                        op2 = "-";
                        break;
                    case 2:
                        op1 = "-";
                        op2 = "+";
                        break;
                    case 3:
                        op1 = "-";
                        op2 = "-";
                        break;
                    }
                    //op = "*";
                    y = y1v[y3[i - 4]];
                    z = z3v[z3[i - 4]];
                    k0 = k;
                    j0 = j === 0 ? (random(5) + 1) : j;
                    m0 = m;
                    n0 = n;
                    var dm = m == 1 ? "" : m;
                    var dj = j == 1 ? "" : j == -1 ? "-" : j;
                    ldata = k0 + "(" + dj + "" + y + op1 + dm + "" + z + op2 + n0 + ")";
                    var oop1, oop2;
                    oop1 = op1;
                    oop2 = op2;
                    if (k * m < 0) {
                        op1 = op1 == "-" ? "+" : "-";
                    }
                    if (k * n < 0) {
                        op2 = op2 == "-" ? "+" : "-";
                    }
                    var km = (k * m) == 1 ? "" : (k * m) == -1 ? "" : Math.abs(k * m);
                    var kj = (k * j) == 1 ? "" : (k * j) == -1 ? "-" : (k * j);
                    rdata = (kj) + "" + y + op1 + (km) + "" + z + op2 + Math.abs(k * n);
                }
                if (i >= 6) {
                    switch (random(7)) {
                    case 0:
                        type = 4;
                        m = [100, 1000][random(2)];
                        n = [2, 3][random(2)];
                        k = [2, 3, 4, 5][random(4)];
                        j = m - n;
                        ldata = k + "(" + j + ")* = " + k + "(" + m + "-" + n + ")";
                        rdata = k * j;
                        break;
                    case 1:
                        type = 5;
                        m = [100, 1000][random(2)];
                        n = [2, 3][random(2)];
                        k = [2, 3, 4, 5][random(4)];
                        j = m + n;
                        ldata = k + "(" + j + ")* = " + k + "(" + m + "+" + n + ")";
                        rdata = k * j;
                        break;
                    case 2:
                        type = 6;
                        k = ['1/2', '1/3', '1/4'][random(3)];
                        y = y1v[random(6)];
                        if (k == '1/2') {
                            m = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
                            n = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
                            var km = (m / 2) == 1 ? "" : m / 2;
                            rdata = km + "" + y + "+" + (n / 2);
                        }
                        if (k == '1/3') {
                            m = [3, 6, 9, 12, 15, 18][random(6)];
                            n = [3, 6, 9, 12, 15, 18][random(6)];
                            var km = (m / 3) == 1 ? "" : m / 3;
                            rdata = km + "" + y + "+" + (n / 3);
                        }
                        if (k == '1/4') {
                            m = [4, 8, 12, 16, 20][random(5)];
                            n = [4, 8, 12, 16, 20][random(5)];
                            var km = (m / 4) == 1 ? "" : m / 4;
                            rdata = km + "" + y + "+" + (n / 4);
                        }
                        var dm = m == 1 ? "" : m;
                        ldata = k + "(" + dm + "" + y + "+" + n + ")";
                        break;
                    case 3:
                        type = 7;
                        k = ['1/2', '1/3', '1/4'][random(3)];
                        y = y1v[random(6)];
                        if (k == '1/2') {
                            m = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
                            n = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
                            var km = (m / 2) == 1 ? "" : m / 2;
                            rdata = km + "" + y + "+" + (n / 2);
                        }
                        if (k == '1/3') {
                            m = [3, 6, 9, 12, 15, 18][random(6)];
                            n = [3, 6, 9, 12, 15, 18][random(6)];
                            var km = (m / 3) == 1 ? "" : m / 3;
                            rdata = km + "" + y + "+" + (n / 3);
                        }
                        if (k == '1/4') {
                            m = [4, 8, 12, 16, 20][random(5)];
                            n = [4, 8, 12, 16, 20][random(5)];
                            var km = (m / 4) == 1 ? "" : m / 4;
                            rdata = km + "" + y + "+" + (n / 4);
                        }
                        var dm = m == 1 ? "" : m;
                        ldata = "(" + dm + "" + y + "+" + n + ")*" + k + "";
                        break;
                    case 4:
                        type = 8;
                        k = [0.2, 0.5, 1.5][random(3)];
                        y = y1v[random(6)];
                        m = random(10) + 1;
                        n = random(10) + 1;
                        var km = (k * m) == 1 ? "" : (k * m);
                        var dm = m == 1 ? "" : m;
                        rdata = (km) + "" + y + "+" + (k * n);
                        ldata = k + "(" + dm + "" + y + "+" + n + ")";
                        break;
                    case 5:
                        type = 9;
                        k = [0.2, 0.5, 1.5][random(3)];
                        y = y1v[random(6)];
                        m = random(10) + 1;
                        n = random(10) + 1;
                        var km = (km) == 1 ? "" : (k * m);
                        var dm = m == 1 ? "" : m;
                        rdata = (k * m) + "" + y + "+" + (k * n);
                        ldata = "(" + dm + "" + y + "+" + n + ")*" + k + "";
                        break;
                    case 6:
                        type = 10;
                        k = z3v[random(6)];
                        y = y1v[random(6)];
                        m = random(10) + 1;
                        n = random(10) + 1;
                        var dm = m == 1 ? "" : m;
                        rdata = dm + "" + y + "" + k + "+" + n + "" + k;
                        ldata = "(" + dm + "" + y + "+" + n + ")*" + k + "";
                        break;
                    }
                }
                if (oop1) {
                    op1 = oop1;
                    op2 = oop2;
                }
                _data.push([
                    [ldata, [op1, op2],
                        [k, j, m, n, y, z], type], rdata]);
            }
            _data.shuffle();
        }
        if (this.topic_id == 16) {
            var k, m, n;
            var k0, m0, n0;
            var x, p, y;
            //var typ = ['c', 'b', 'b1', 'a', 'a1'];
            var typ = [1, 2, 3, 4, 5];
            var _data = [];
            //var rand=Math.rand;
            var ldata, rdata;
            var l = mngr.totalQuest;
            for (var i = 0; i < l; i++) {
                k = i == 2 ? rand(1, 99) : i == 9 ? rand(2, 5) : rand(1, 9);
                if (i > 3) {
                    if (i == 4 || i == 5) {
                        m = rand(1, 3);
                    }
                    if (i == 6) {
                        m = rand(1, 9);
                    }
                    if (i == 7 || i == 8) {
                        m = rand(2, 9);
                    }
                    if (i == 9) {
                        m = rand(1, 99);
                    }
                }
                switch (i + 1) {
                case 1:
                    x = k;
                    p = 10;
                    y = 10 * k;
                    break;
                case 2:
                    x = 10 * k;
                    p = 10;
                    y = 100 * k;
                    break;
                case 3:
                    x = 5 * k;
                    p = 50;
                    y = 10 * k;
                    break;
                case 4:
                    x = k;
                    p = 5;
                    y = 20 * k;
                    break;
                case 5:
                    x = 5 * k * m;
                    p = 25 * m;
                    y = 20 * k;
                    break;
                case 6:
                    x = 25 * k * m;
                    p = 25 * m;
                    y = 100 * k;
                    break;
                case 7:
                    x = (10 * k) + m;
                    p = 10;
                    y = (100 * k + 10 * m);
                    break;
                case 8:
                    x = k * m;
                    p = 10 * m;
                    y = 10 * k;
                    break;
                case 9:
                    x = 10 * k * m;
                    p = 10 * m;
                    y = 100 * k;
                    break;
                case 10:
                    x = m * (Math.pow(10, k - 2));
                    p = m;
                    y = Math.pow(10, k);
                    break;
                }
                var tyi = rand(0, 4);
                var statT = typ[tyi];
                var findV;
                switch (statT) {
                case 1:
                    rdata = x;
                    break;
                case 4:
                case 5:
                    rdata = y;
                    break;
                case 2:
                case 3:
                    rdata = p;
                    break;
                }
                _data.push([
                    [
                        [x, p, y], statT], rdata]);
            }
            _data.shuffle();
        }
        if (this.topic_id == 17) {
            var k, m, n;
            var k0, m0, n0;
            var x, p, y;
            var typ = [1, 2, 3, 4, 5];
            //var rand=Math.rand;
            var _data = [];
            var ldata, rdata;
            for (var i = 0; i < 12; i++) {
                switch (i + 1) {
                case 1:
                    k = rand(1, 9);
                    m = rand(1, 9);
                    x = k * m;
                    p = m;
                    y = 100 * k;
                    break;
                case 2:
                    k = rand(1, 9);
                    m = rand(1, 9);
                    x = (100 * k) - (k * m);
                    p = 100 - m;
                    y = 100 * k;
                    break;
                case 3:
                    k = rand(1, 4);
                    m = rand(1, 9);
                    x = (k / 5) * ((10 * m) + 5);
                    p = (10 * m) + 5;
                    y = 20 * k;
                    break;
                case 4:
                    k = rand(1, 9);
                    m = rand(1, 9);
                    x = (k) * ((10 * m) + 5);
                    p = (10 * m) + 5;
                    y = 100 * k;
                    break;
                case 5:
                    k = rand(1, 9);
                    m = rand(1, 4);
                    n = rand(1, 3);
                    x = (n / 4) * ((100 * k) + (20 * m));
                    p = 25 * n;
                    y = (100 * k) + (20 * m);
                    break;
                case 6:
                    k = rand(2, 5);
                    m = rand(1, 99);
                    n = rand(1, 3);
                    x = m * n * (Math.pow(10, (k - 2)));
                    p = m;
                    y = n * (Math.pow(10, k));
                    break;
                case 7:
                    k = rand(1, 9);
                    m = rand(1, 4);
                    x = k * (10 + (2.5 * m));
                    p = 100 + (25 * m);
                    y = 10 * k;
                    break;
                case 8:
                    k = rand(1, 9);
                    m = rand(1, 4);
                    x = k * (100 + (25 * m));
                    p = 100 + (25 * m);
                    y = 100 * k;
                    break;
                case 9:
                    k = rand(1, 9);
                    m = rand(1, 5);
                    n = rand(0, 3);
                    x = k * m * (Math.pow(10, n));
                    p = 100 * m;
                    y = k * Math.pow(10, n);
                    break;
                case 10:
                    k = rand(1, 9);
                    m = rand(1, 9);
                    x = (k * m) / 10;
                    p = 10 * m;
                    y = k;
                    break;
                case 11:
                    k = rand(1, 9);
                    m = rand(1, 9);
                    n = rand(2, 5);
                    x = k * m * (Math.pow(10, n - 3));
                    p = m / 10;
                    y = k * Math.pow(10, n);
                    break;
                case 12:
                    k = rand(1, 9);
                    m = rand(1, 9);
                    n = rand(0, 2);
                    x = k * (Math.pow(10, n - 2)) * (m + 0.5);
                    p = m + 0.5;
                    y = k * Math.pow(10, n);
                    break;
                }
                var tyi = rand(0, 4);
                var statT = typ[tyi];
                var findV;
                switch (statT) {
                case 1:
                    rdata = x;
                    break;
                case 4:
                case 5:
                    rdata = y;
                    break;
                case 2:
                case 3:
                    rdata = p;
                    break;
                }
                _data.push([
                    [
                        [x, p, y], statT], rdata]);
            }
            _data = Array.shuffleArray(_data, mngr.totalQuest);
        }
        if (this.topic_id == 18) {
            var dataStr = mngr.topic_data;
            var dataArr = dataStr.split(":");
            var de_pair = dataArr[0].split(",");
            var gh_pair = dataArr[1].split(",");
            mngr.totalQuest = mngr.totalQuest ? mngr.totalQuest : 10;
            var l1 = 4 * mngr.totalQuest / 10;
            var l2 = 3 * mngr.totalQuest / 10;
            var l3 = 3 * mngr.totalQuest / 10;
            l1 = Math.round(l1);
            l2 = Math.round(l2);
            l3 = mngr.totalQuest - (l1 + l2);
            var _data = [];
            var ld1 = Array.shuffleArray(de_pair, l1);
            var rd1 = Array.shuffleArray(de_pair, l1);
            //
            var ld2 = Array.shuffleArray(gh_pair, l2);
            var rd2 = Array.shuffleArray(de_pair, l2);
            //
            var ld3 = Array.shuffleArray(de_pair, l3);
            var rd3 = Array.shuffleArray(gh_pair, l3);
            var ldata0, ldata1, rdata, type, s1, s2, ans;
            for (var i = 0; i < mngr.totalQuest; i++) {
                if (i < l1) {
                    type = 1;
                    ldata0 = ld1[i];
                    ldata1 = rd1[i];
                }
                if (i >= l1 && i < l1 + l2) {
                    type = 2;
                    ldata0 = ld2[i - l1];
                    ldata1 = rd2[i - l1];
                }
                if (i >= l1 + l2 && i < mngr.totalQuest) {
                    type = 3;
                    ldata0 = ld3[i - (l1 + l2)];
                    ldata1 = rd3[i - (l1 + l2)];
                }
                s1 = ldata0.split("/");
                s2 = ldata1.split("/");
                ansn = s1[0] * s2[1] + "/" + s1[1] * s2[0];
                ans = Math.simpleFrac(ansn);
                var ans0 = ans.split("/");
                ans0[1] = ans0[1] == undefined ? '1' : ans0[1];
                rdata = ans0[1] == '1' ? ans0[0] : ans;
                ldata0 = s1[1] == '1' ? s1[0] : ldata0;
                ldata1 = s2[1] == '1' ? s2[0] : ldata1;
                _data.push([
                    [ldata0, ldata1, [s1, s2], ans0, type],
                    {
                        frac: ansn,
                        val: rdata,
                        uans: ans
                    }]);

            }
            _data.shuffle();
        }
        if (this.topic_id == 8 || this.topic_id == 9) {
            //var dataStr=mngr.topic_data;
            var ifracStr = mngr.topic_data;
            var obj;
            var ifracArr = ifracStr.split(",");
            var l = ifracArr.length;
            var d;
            var _data = [];
            console.log("FLASH_CARD_MNGR_CALLING GEN PROBLEMS - topic " + this.topic_id + " " + l)
            for (var i = 0; i < l; i++) {
                d = ifracArr[i].split(" ");
                obj = {};
                obj.q = Number(d[0]);
                d = d[1].split("/");
                obj.num = Number(d[0]);
                obj.den = Number(d[1]);
                obj.improper = ifracArr[i];
                obj.frac = ((obj.q * obj.den) + obj.num) + "/" + obj.den;
                obj.deci = ((obj.q * obj.den) + obj.num) / obj.den;
                var dans = obj.frac.split("/")
                var sans = Math.simpleFrac(obj.frac).split("/")
                var mixedStr = "[" + obj.q + "]" + obj.num + "/" + obj.den;
                if (this.topic_id == 8) {
                    _data.push([
                        [
                            [obj.q, obj.num, obj.den], dans, sans, obj.improper],
                        {
                            frac: obj.frac,
                            val: obj.deci,
                            sval: obj.deci
                        }]);
                } else {
                    _data.push([
                        [dans, [obj.q, obj.num, obj.den], obj.improper],
                        {
                            frac: obj.frac,
                            val: mixedStr,
                            sval: obj.deci
                        }]);
                }
            }
            _data.shuffle();
        }

        if (this.topic_id == 26) {
            var l = mngr.totalQuest;
            //var l1 = 4*mngr.totalQuest/10;
            //var l2 = 3*mngr.totalQuest/10;
            //var l3 = 3*mngr.totalQuest/10;
			var deb=[]
            var g = [1, 1, 1, 1, 1, 2, 3, 4, 5, 1, 1, 6, 7, 8, 9, 10, 1, 1];
            var qstr = "Find the greatest common factor of "
            var andStr = " and "
            var _data = [];
            //var rand=Math.rand;
            console.log("GENERATING_PROBLEMS_FOR_TOPIC: " + this.topic_id)
            for (var i = 0; i < 10; i++) {
                var _ldata;
                var _rdata = [];
                var p, q, r;
                var gr = g[Math.floor(random(g.length))];
                if (gr == 1) {
                    p = rand(1, 30);
                } else {
                    p = rand(1, 10) * gr;
                }
                if (gr == 1) {
                    q = rand(1, 30);
                } else {
                    q = rand(1, 10) * gr;
                }
                if (p == q) {
                    q = p + rand(2, 10);
                }
                if (i > 7) {
                    if (gr == 1) {
                        r = rand(1, 30);
                    } else {
                        r = rand(1, 10) * gr;
                    }
                }
                if (p == r || q == r) {
                    r = p + q;
                }
                _ldata = i < 8 ? [p, q] : [p, q, r];
                if (_ldata.length == 2) {
                    _rdata[0] = Math.getGCD(p, q);
                    _rdata[1] = 2;
                    _rdata[2] = qstr + String(p) + andStr + String(q) + ".";
                } else {

                    _rdata[0] = Math.getGCD(Math.getGCD(p, q), r);
                    _rdata[1] = 3;
                    _rdata[2] = qstr + String(p) + ", " + String(q) + "," + andStr + String(r) + ".";
                }
				deb.push(p+":"+q+" --> "+ _rdata[0]);
                _data.push([
                    [_ldata, _rdata[2], _rdata[1]], _rdata[0]
                ]);
            }
			console.log(deb.join("\n"));
            //_data.shuffle()
        }
        if (this.topic_id == 11) {
            var t1 = Math.round((4 / 5) * totalQuest);
            t1 = actType == 'div' ? totalQuest : t1;
            var t2 = totalQuest - t1;
            var mdata = Array.randomNumberArray(1, 12, t1, true);
            var ndata = Array.randomNumberArray(1, 12, t1, true);
            var i = 0;
            var l = mdata.length;
            var c = 0;
            var ldata;
            var rdata;
            var l, m, n;
            var action = ["*", "�"];
            if (actType == 'multi') {
                action = ["*", "*"];
            }
            if (actType == 'div') {
                action = ["�", "�"];
            }
            var op;
            var _data = [];
            for (; c < mdata.length; c++) {
                m = mdata[c];
                n = ndata[c];
                switch (random(3)) {
                case 0:
                    m = -m;
                    break;
                case 1:
                    n = -n;
                    break;
                case 2:
                    m = -m;
                    n = -n;
                    break;
                }
                op = action[random(2)];
                if (n < 0) {
                    n0 = "(" + n + ")";
                } else {
                    n0 = n;
                }
                if (m < 0) {
                    m0 = "(" + m + ")";
                } else {
                    m0 = m;
                }
                ldata = op == "*" ? m + op + n0 + "" : (m * n) + op + m0;
                rdata = op == "*" ? m * n : n;
                _data.push([
                    [
                        [m, n], op, ldata], rdata]);
            }
            mdata = Array.randomNumberArray(1, 5, t2, true);
            ndata = Array.randomNumberArray(1, 5, t2, true);
            odata = Array.randomNumberArray(1, 5, t2, true);
            for (var d = 0; d < mdata.length; d++) {
                m = mdata[d];
                n = ndata[d];
                o = odata[d];
                switch (random(9)) {
                case 0:
                    m = -m;
                    break;
                case 1:
                    n = -n;
                    break;
                case 2:
                    o = -o;
                    break;
                case 3:
                    m = -m;
                    n = -n;
                    break;
                case 4:
                    m = -m;
                    o = -o;
                    break;
                case 5:
                    n = -n;
                    o = -o;
                    break;
                default:
                    m = -m;
                    n = -n;
                    o = -o;
                }
                op = "*";
                if (n < 0) {
                    n0 = "(" + n + ")";
                } else {
                    n0 = n;
                }
                if (o < 0) {
                    o0 = "(" + o + ")";
                } else {
                    o0 = o;
                }
                ldata = m + op + n0 + op + o0;
                rdata = m * n * o;
                _data.push([
                    [
                        [m, n, o], op, ldata], rdata]);
            }
            _data.shuffle()
        }
        if (this.topic_id == 12) {
            var t1 = Math.round((4 / 5) * totalQuest);
            var t2 = totalQuest - t1;
            var mdata = Array.randomNumberArray(1, 20, t1, true);
            var ndata = Array.randomNumberArray(1, 20, t1, true);
            var i = 0;
            var l = mdata.length;
            var c = 0;
            var ldata;
            var rdata;
            var l, m, n;
            var action = ["+", "-"];
            if (actType == 'add') {
                action = ["+", "+"];
            }
            if (actType == 'sub') {
                action = ["-", "-"];
            }
            var op, op1, op2;
            var pcount = 0;
            var _data = [];
            for (; c < mdata.length; c++) {
                m = mdata[c];
                n = ndata[c];
                op = action[random(2)];
                var rr = actType == 'add' ? random(3) : random(4);
                switch (rr) {
                case 0:
                    m = -m;
                    break;
                case 1:
                    n = -n;
                    break;
                case 2:
                    m = -m;
                    n = -n;
                    break;
                case 3:
                    m = Math.min(m, n);
                    n = Math.max(mdata[c], ndata[c]);
                    op = "-";
                    if (m - n > 0) {
                        if (pcount == 2) {
                            m = m ^ n;
                            n = n ^ m;
                            m = m ^ n;
                        } else {
                            pcount++;
                        }
                    }
                    break;
                }
                if (n < 0) {
                    n0 = "(" + n + ")";
                } else {
                    n0 = n;
                }
                if (m < 0) {
                    m0 = "(" + m + ")";
                } else {
                    m0 = m;
                }
                ldata = m + op + n0;
                rdata = op == "+" ? m + n : m - n;
                _data.push([
                    [
                        [m, n],
                        [op], ldata], rdata]);
            }
            mdata = Array.randomNumberArray(-20, 20, t2, true);
            ndata = Array.randomNumberArray(-20, 20, t2, true);
            odata = Array.randomNumberArray(-20, 20, t2, true);
            for (var d = 0; d < mdata.length; d++) {
                m = mdata[d];
                n = ndata[d];
                o = odata[d];
                m = m === 0 ? ([1, -1][random(2)]) : m;
                n = n === 0 ? ([1, -1][random(2)]) : n;
                o = o === 0 ? ([1, -1][random(2)]) : o;
                if (m > 0 && n > 0 && o > 0) {
                    n = -n;
                }
                op1 = "+";
                op2 = "+";
                var rr = actType == 'sub' ? random(3) + 1 : actType == 'add' ? 0 : random(4);
                switch (rr) {
                case 0:
                    op1 = "+";
                    op2 = "+";
                    rdata = m + n + o;
                    break;
                case 1:
                    op1 = "+";
                    op2 = "-";
                    rdata = m + n - o;
                    break;
                case 2:
                    op1 = "-";
                    op2 = "+";
                    rdata = m - n + o;
                    break;
                case 3:
                    op1 = "-";
                    op2 = "-";
                    rdata = m - n - o;
                    break;
                }
                //op = "*";
                if (n < 0) {
                    n0 = "(" + n + ")";
                } else {
                    n0 = n;
                }
                if (o < 0) {
                    o0 = "(" + o + ")";
                } else {
                    o0 = o;
                }
                ldata = m + op1 + n0 + op2 + o0;
                _data.push([
                    [
                        [m, n, o],
                        [op1, op2], ldata], rdata]);
            }
        }
        if (this.topic_id == 13) {
            function getDeciDataType(t) {
                var m, n, s1, e1, s2, e2, d1, d2, op, mp;
                switch (t) {
                case 1:
                    s1 = s2 = 1;
                    e1 = e2 = 99;
                    d1 = d2 = 10;
                    op = "+";
                    break;
                case 2:
                    s1 = 50;
                    s2 = 1;
                    e1 = 99;
                    e2 = 49;
                    d1 = d2 = 10;
                    op = "-";
                    break;
                case 3:
                    s1 = 1;
                    s2 = 1;
                    e1 = 999;
                    e2 = 999;
                    d1 = d2 = 100;
                    op = "+";
                    break;
                case 4:
                    s1 = 500;
                    s2 = 1;
                    e1 = 999;
                    e2 = 499;
                    d1 = d2 = 100;
                    op = "-";
                    break;
                case 5:
                    s1 = 1;
                    s2 = 1;
                    e1 = 9;
                    e2 = 99;
                    d1 = 100;
                    d2 = 1000;
                    op = "+";
                    break;
                case 6:
                    s1 = 1;
                    s2 = 1;
                    e1 = 9;
                    e2 = 9;
                    d1 = 10;
                    d2 = 100;
                    op = "-";
                    break;
                case 7:
                    s1 = 1;
                    s2 = 1;
                    e1 = 99;
                    e2 = 999;
                    d1 = 100;
                    d2 = 1000;
                    op = "+";
                    break;
                case 8:
                    s1 = 30;
                    s2 = 1;
                    e1 = 99;
                    e2 = 299;
                    d1 = 100;
                    d2 = 1000;
                    op = "-";
                    break;
                case 9:
                    s1 = 10;
                    s2 = 1;
                    e1 = 99;
                    e2 = 999;
                    d1 = 10;
                    d2 = 10000;
                    op = "+";
                    break;
                case 10:
                    s1 = 1;
                    s2 = 1;
                    e1 = 99;
                    e2 = 99;
                    d1 = 100;
                    d2 = 10000;
                    op = "-";
                    break;
                }
                m = Array.randomNumberArray(s1, e1, 1, true);
                n = Array.randomNumberArray(s2, e2, 1, true);
                m = m[0] / d1;
                n = n[0] / d2;
                var m1 = m;
                if (t == 5 || t == 7 || t == 9) {
                    if (random(2)) {
                        m = n;
                        n = m1;
                    }
                }
                var mDeci = String(m).indexOf(".") > -1;
                var nDeci = String(n).indexOf(".") > -1;
                if (!mDeci && !nDeci) {
                    m = m + (1 / d1);
                }
                var ls = String(m).split(".")[1];
                mp = ls ? ls.length : 1;
                var pow = Math.pow(10, mp);
                m = Math.round(m * pow) / pow;
                ls = String(n).split(".")[1];
                np = ls ? ls.length : 1;
                pow = Math.pow(10, np);
                n = Math.round(n * pow) / pow;
                return {
                    m: m,
                    n: n,
                    op: op
                };
            }
            var type, ldata, rdata, mp;
            for (var i = 0; i < totalQuest; i++) {
                type = random(10) + 1;
                var _data = [];
                var t1 = getDeciDataType(type);
                ldata = t1.m + t1.op + t1.n;
                rdata = t1.op == "+" ? t1.m + t1.n : t1.m - t1.n;
                var ls = String(rdata).split(".")[1];
                mp = ls ? ls.length : 1;
                var pow = Math.pow(10, mp);
                rdata = Math.round(rdata * pow) / pow;
                _data.push([
                    [
                        [t1.m, t1.n],
                        [t1.op], ldata], rdata]);
/* var t2 = getDeciDataType(type+1);
        ldata = t2.m+t2.op+t2.n;
        rdata = t2.op == "+" ? t2.m+t2.n : t2.m-t2.n;
        var ls = String(rdata).split(".")[1];
        mp = ls ? ls.length : 1;
        var pow = Math.pow(10, mp);
        rdata = Math.round(rdata*pow)/pow;
        _data.push([[[t2.m, t2.n], [t2.op], ldata], rdata]); */
            }
            _data.shuffle()
        }
        if (this.topic_id == 19) {
            function sortNumber(a, b) {
                return a - b;
            }
            var k, m, n;
            var _data = [];
            var ldata, rdata;
            var l1 = 5;
            var l2 = 2;
            var l3 = 3;
            var ind, pind;
            var type;
            var t1ma = Array.randomNumberArray(2, 9, 2, true);
            var t1mb = Array.randomNumberArray(2, 15, l1 - 2, true);
            var t1mc = Array.randomNumberArray(2, 5, l1 - 2, true);
            var t2n = Array.randomNumberArray(5, 75, l2, true);
            t2n.sort(sortNumber);
            var t3na = Array.randomNumberArray(4, 8, l3, true);
            t3na.sort(sortNumber);
            var t3nb = Array.randomNumberArray(3, 6, l3, true);
            t3nb.sort(sortNumber);
            for (var i = 0; i < totalQuest; i++) {
                if (i < l1) {
                    type = 1;
                    if (i < 2) {
                        n = 2;
                        m = t1ma[i];
                    } else {
                        ind = random(2);
                        n = ind ? 3 : 2;
                        m = n == 2 ? t1mb[i - 2] : t1mc[i - 2];
                    }
                    ldata = m + "^" + n;
                    rdata = Math.pow(m, n);
                } else if (i >= l1 && i < l1 + l2) {
                    type = 2;
                    if (pind === 0) {
                        m = 1;
                    } else if (pind === 1) {
                        m = 0;
                    } else {
                        m = random(2);
                    }
                    n = t2n[i - l1];
                    pind = m;
                    ldata = m + "^" + n;
                    rdata = Math.pow(m, n);
                } else if (i >= l1 + l2 && i < totalQuest) {
                    type = 3;
                    ind = random(2);
                    m = ind ? 10 : 2;
                    n = m == 2 ? t3na[i - (l1 + l2)] : t3nb[i - (l1 + l2)];
                    ldata = m + "^" + n;
                    rdata = Math.pow(m, n);
                }
                _data.push([
                    [
                        [m, n], type, ldata], rdata]);
            }
            //_data.shuffle()
        }
        if (this.topic_id == 20) {
            var k, m, n;
            var _data = [];
            var ldata, rdata;
            var l1 = 5;
            var l2 = 2;
            var l3 = 3;
            var ind, pind;
            var type;
            var t1ma = Array.randomNumberArray(1, 20, 3, true);
            var t1mb = Array.randomNumberArray(1, 6, 3, true);
            var t2m1 = Array.shuffleArray(['1/2', '1/3'], 2);
            var t2m2 = Array.shuffleArray(['1/4', '1/5', '1/6', '1/10', '2/3', '2/5', '3/4', '3/5', '3/10', '4/5'], 2);
            var t2m3 = Array.shuffleArray(['1/7', '2/7', '3/7', '4/7', '5/7', '6/7', '1/8', '3/8', '5/8', '7/8', '2/9', '4/9', '5/9', '7/9', '8/9', '7/10', '9/10'], 2);
            var t2n1 = Array.randomNumberArray(2, 4, 2, false);
            var t2n2 = Array.randomNumberArray(2, 3, 2, !false);
            var t2n3 = [2, 2];
            var t4ma = Array.randomNumberArray(3, 20, 2, true);
            var t4mb = Array.randomNumberArray(1, 6, 2, true);
            var t4neg = Array.shuffleArray([0, 1], 2);
            var t5ma = Array.randomNumberArray(1, 9, 1, true);
            var t5mb = Array.randomNumberArray(1, 4, 1, true);
			var st
            for (var i = 0; i < totalQuest; i++) {
                var s = '';
                var frac = false;
				st=['','']
                if (i < 3) {
                    type = 1;
                    ind = random(2);
                    n = ind ? 3 : 2;
                    m = n == 2 ? t1ma[i] : t1mb[i];
                    s = random(2);
                    ldata = s ? -m + "^" + n : "(-" + m + ")^" + n;
                    rdata = s ? -Math.pow(m, n) : Math.pow(-m, n);
					st[0]=m
                } else if (i >= 3 && i < 5) {
                    type = 2;
                    switch (random(3)) {
                    case 0:
                        m = t2m1[i - 3];
                        n = t2n1[i - 3];
                        break;
                    case 1:
                        m = t2m2[i - 3];
                        n = t2n2[i - 3];
                        break;
                    case 2:
                        m = t2m3[i - 3];
                        n = t2n3[i - 3];
                        break;
                    }
                    ldata = "(" + m + ")^" + n;
                    var st = m.split("/");
                    var nu = Math.pow(st[0], n);
                    var de = Math.pow(st[1], n);
                    frac = true;
                    var str = Math.simpleFrac(nu + "/" + de);
                    rdata = str;
                } else if (i >= 5 && i < 7) {
                    type = 3;
                    switch (random(3)) {
                    case 0:
                        m = t2m1[i - 5];
                        n = t2n1[i - 5];
                        break;
                    case 1:
                        m = t2m2[i - 5];
                        n = t2n2[i - 5];
                        break;
                    case 2:
                        m = t2m3[i - 5];
                        n = t2n3[i - 5];
                        break;
                    }
                    s = random(2);
                    ldata = s ? "-(" + m + ")^" + n : "(-*" + m + ")^" + n;
                    var st = m.split("/");
                    var nu = Math.pow(st[0], n);
                    var de = Math.pow(st[1], n);
                    frac = true;
                    var str = Math.simpleFrac(frac);
                    var si = Math.pow(-1, n) < 0 ? "-" : "";
                    rdata = s ? "-" + str : si + str;
                } else if (i >= 7 && i < 9) {
                    type = 4;
                    ind = random(2);
                    n = ind ? 3 : 2;
                    m = n == 2 ? t4ma[i - 7] / 10 : t4mb[i - 7] / 10;
                    if (t4neg[i - 7]) {
                        s = random(2);
                        ldata = s ? -m + "^" + n : "(-" + m + ")^" + n;
                        rdata = s ? -Math.pow(m, n) : Math.pow(-m, n);
                    } else {
                        ldata = m + "^" + n;
                        rdata = Math.pow(m, n);
                    }
					st[0]=m
                } else if (i >= 9) {
                    s = '';
                    type = 5;
                    ind = random(2);
                    n = ind ? 3 : 2;
                    var k = rand(2, 4);
                    var kv = Math.pow(10, k);
                    m = n == 2 ? t5ma[0] * kv : t5mb[0] * kv;
                    ldata = m + "^" + n;
                    rdata = Math.pow(m, n);
					st[0]=m
                }
                _data.push([
                    [
                        [m, n], s, frac, [st[0], st[1]], type, ldata], [rdata,[nu,de]]]);

            }
            _data.shuffle()
        }
        if (this.topic_id == 21) {
            var k, m, n;
            var _data = [];
            var ldata, rdata;
            var l1 = 5;
            var l2 = 2;
            var l3 = 3;
            var ind, pind;
            var type;
            var k1 = Array.randomNumberArray(1, 3, 2, true);
            var m1 = Array.randomNumberArray(1, 12, 2, true);
            var n1 = Array.randomNumberArray(1, 9, 2, true);
            var k2 = Array.randomNumberArray(1, 3, 4, false);
            var k22 = Array.shuffleArray([-3, -2, -1, 2, 3], 4);
            var m2 = Array.randomNumberArray(1, 9, 4, true);
            var n2 = Array.randomNumberArray(1, 9, 4, true);
            var k3 = Array.randomNumberArray(1, 3, 2, false);
            var k32 = Array.shuffleArray([0.1, 0.2, 2, 10, 20, 100], 2);
            var m3 = Array.randomNumberArray(11, 999, 2, true);
            var n3 = Array.randomNumberArray(1, 2, 2, false);
            //
            var k4 = Array.randomNumberArray(1, 3, 2, true);
            var k42 = Array.randomNumberArray(1, 3, 2, true);
            var m4 = Array.randomNumberArray(11, 99, 2, true);
            var n4 = Array.randomNumberArray(11, 25, 2, false);
            for (var i = 0; i < totalQuest; i++) {
                var s = '';
                var frac = false;
                if (i < 2) {
                    type = 1;
                    j = i;
                    k = Math.pow(10, -k1[j]);
                    ind = random(2);
                    n = ind ? m1[i] : n1[j] * k;
                    m = ind ? n1[i] * k : m1[j];
                } else if (i >= 2 && i < 6) {
                    type = 2;
                    j = i - 2;
                    ka = Math.pow(10, -k2[j]);
                    kb = Math.pow(10, k22[j]);
                    ind = random(2);
                    n = ind ? m2[j] * ka : n2[j] * kb;
                    m = ind ? n2[j] * kb : m2[j] * ka;
                } else if (i >= 6 && i < 8) {
                    type = 3;
                    j = i - 6;
                    ka = Math.pow(10, -k3[j]);
                    kb = k32[j];
                    ind = random(2);
                    n = ind ? m3[j] * ka : kb;
                    m = ind ? kb : m3[j] * ka;
                } else if (i >= 8 && i < totalQuest) {
                    type = 4;
                    j = i - 8;
                    ka = Math.pow(10, -k4[j]);
                    kb = Math.pow(10, k42[j]);
                    ind = random(2);
                    n = ind ? m4[j] * ka : n4[j] * kb;
                    m = ind ? n4[j] * kb : m4[j] * ka;
                }
                m = Math.fixTo(m, 8);
                n = Math.fixTo(n, 8);
                rdata = m * n;
                _data.push([
                    [
                        [m, n], type], rdata]);
            }
            _data.shuffle();
        }
        if (this.topic_id == 24) {
            var fractodeciStr = mngr.topic_data;
            var fractodeciStr_a = fractodeciStr.split("|");
            var fractodeciStr_1 = fractodeciStr_a[0].split(",");
            var fractodeciStr_2 = fractodeciStr_a[1].split(",");
            var obj = {};
            var n = Math.round(totalQuest * .7);
            var m = totalQuest - n;
            var farr1 = fractodeciStr_1.slice(0, 10);
            var farr2 = fractodeciStr_1.slice(10);
            var fracArr_1a = Array.shuffleArray(farr1, 2);
            var fracArr_1 = Array.shuffleArray(farr2, n - 2);
            var fracArr_2 = Array.shuffleArray(fractodeciStr_2, m);
            var fracArr = fracArr_1a.concat(fracArr_1, fracArr_2);
            var l = totalQuest;
            var d;
            var _data = [];
            for (var i = 0; i < l; i++) {
                d = fracArr[i].split("/");
                _data.push([
                    [
                        [d0, d1], fracArr[i]
                    ], d[0] / d[1]
                ]);
            }
            _data.shuffle();
        }
        if (this.topic_id == 25) {
            var decitofracStr = mngr.topic_data;
            var decitofracStr_a = decitofracStr.split(",");
            var l = totalQuest;
            var fracArr = Array.shuffleArray(decitofracStr_a, l);
            var _data = [];
            var _ldata, _rdata, _rep;
            for (var i = 0; i < l; i++) {
                _ldata = fracArr[i];
                _rep = false;
                if (fracArr[i] == '0.333R') {
                    _rdata = '1/3';
                    _ldata = fracArr[i].split("R")[0];
                    _rep = true;
                } else if (fracArr[i] == '0.666R') {
                    _rdata = '2/3';
                    _ldata = fracArr[i].split("R")[0];
                    _rep = true;
                } else {
                    _rdata = Math.convertToFrac(fracArr[i]).val;
                }
                _data.push([
                    [_ldata, _rep], _rdata]);
            }
            _data.shuffle();
        }
        if (this.topic_id == 27) {
            var kn = Array.randomNumberArray(2, 10, 10, !true);
            var pn = Array.randomNumberArray(2, 10, 10, !true);
            var qn = Array.randomNumberArray(2, 10, 10, !true);
            var et;
            var _data = [];
            var _ldata, _rdata;
            for (var i = 0; i < totalQuest; i++) {
                et = actType === undefined || actType === null ? random(5) : actType;
                _ldata = [];
                _rdata = [];
                k = kn[i];
                p = pn[i];
                q = qn[i];
                switch (et) {
                case 0:
                    _ldata[0] = ["{" + k + "^" + p + "}*{" + k + "^" + q + "}", [k, p, q]];
                    _ldata[1] = k + "^" + (p + q);
                    _ldata[2] = et;
                    _ldata[3] = k + "^{" + p + "+" + q + "}";
                    break;
                case 1:
                    _ldata[0] = ["{" + p + "^" + k + "}*{" + q + "^" + k + "}", [k, p, q]];
                    _ldata[1] = (p * q) + "^" + (k);
                    _ldata[2] = et;
                    _ldata[3] = "(" + p + "*" + q + ")^" + (k);
                    break;
                case 2:
                    _ldata[0] = ["(" + k + "^" + p + ")^" + q, [k, p, q]];
                    _ldata[1] = k + "^" + (p * q);
                    _ldata[2] = et;
                    _ldata[3] = k + "^" + "(" + p + "*" + q + ")";
                    break;
                case 3:
                    var p0 = p;
                    var q0 = q;
                    if (q > p) {
                        p0 = q;
                        q0 = p;
                    } else if (p == q) {
                        p0 = p + 1;
                    }
                    p = p0;
                    q = q0;
                    _ldata[0] = ["{" + k + "^" + p + "}/{" + k + "^" + q + "}", [k, p, q]];
                    _ldata[1] = k + "^" + (p - q);
                    _ldata[2] = et;
                    _ldata[3] = k + "^" + "(" + p + "-" + q + ")";
                    break;
                case 4:
                    _ldata[0] = ["{" + p * q + "^" + k + "}/{" + p + "^" + k + "}", [k, p, q]];
                    _ldata[1] = q + "^" + k;
                    _ldata[2] = et;
                    _ldata[3] = "(" + (p * q) + "/" + p + ")^" + k;
                    break;
                }
                _data.push([
                    [_ldata[0][1], _ldata[2], _ldata[3], _ldata], _ldata[1]
                ]);
            }
            _data.shuffle();
        }
        if (this.topic_id == 4 || this.topic_id == 5 || this.topic_id == 7 || this.topic_id == 22 || this.topic_id == 28) {
            var _data = mngr.topic_data
            _data.shuffle();
        }
        this.quest_data = _data;
    }



    mngr.getProblem = function (id) {
        if (this.initialized) {
        	if(this.topic_id!=id){
        	this.init(id);
			}
        } else {
            this.init(id);
        }
        console.log("FLASH_CARD_MNGR_INFO:GET PROB FOR TOPPIC ID " + id)
        if (this.current_pblm_index >= this.quest_data.length) {
            this.genProblems();
            this.current_pblm_index = 0;
        }
        console.log("FLASH_CARD_MNGR_INFO:GENERATED PROB FOR TOPPIC ID " + id)
        var indx = this.current_pblm_index;
        this.current_pblm_index++;
        this.completed_pblms++;

        var q = this.quest_data[indx]
        console.log("FLASH_CARD_MNGR_INFO: " + this.quest_data)
        console.log("FLASH_CARD_MNGR_INFO:" + this.current_pblm_index + ":" + q[0])
        this.current_qobj = q;
        this.current_quest = q[0];
        this.current_ans = q[1];
        return q;
    }
    mngr.checkForLimit = function () {
        var boo = false;
        if (this.limit == this.completed_pblms) {
            boo = true;
        }
        return boo;
    }
    mngr.checkAnswer = function () {
        //
    }
    return mngr;
}()); /** End of flashcard manager class */
console.log("TUTOR_AUTHOR_API:" + AuthorApi + ":" + Flashcard_mngr);/** Process math on page with MathJax
 *
 */
function processMathJax() {
    try {
       MathJax.Hub.Queue(["Typeset",MathJax.Hub]);
    }
    catch(e) {
       alert("MathJAX processing failed: " + e);
    }
}

  HmEvents.eventTutorInitialized.subscribe( function(x) {
    processMathJax();
});
var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(C){var A="";var J,H,F,I,G,E,D;var B=0;C=Base64._utf8_encode(C);while(B<C.length){J=C.charCodeAt(B++);H=C.charCodeAt(B++);F=C.charCodeAt(B++);I=J>>2;G=((J&3)<<4)|(H>>4);E=((H&15)<<2)|(F>>6);D=F&63;if(isNaN(H)){E=D=64}else{if(isNaN(F)){D=64}}A=A+this._keyStr.charAt(I)+this._keyStr.charAt(G)+this._keyStr.charAt(E)+this._keyStr.charAt(D)}return A},decode:function(C){var A="";var J,H,F;var I,G,E,D;var B=0;C=C.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(B<C.length){I=this._keyStr.indexOf(C.charAt(B++));G=this._keyStr.indexOf(C.charAt(B++));E=this._keyStr.indexOf(C.charAt(B++));D=this._keyStr.indexOf(C.charAt(B++));J=(I<<2)|(G>>4);H=((G&15)<<4)|(E>>2);F=((E&3)<<6)|D;A=A+String.fromCharCode(J);if(E!=64){A=A+String.fromCharCode(H)}if(D!=64){A=A+String.fromCharCode(F)}}A=Base64._utf8_decode(A);return A},_utf8_encode:function(B){B=B.replace(/\r\n/g,"\n");var A="";for(var D=0;D<B.length;D++){var C=B.charCodeAt(D);if(C<128){A+=String.fromCharCode(C)}else{if((C>127)&&(C<2048)){A+=String.fromCharCode((C>>6)|192);A+=String.fromCharCode((C&63)|128)}else{A+=String.fromCharCode((C>>12)|224);A+=String.fromCharCode(((C>>6)&63)|128);A+=String.fromCharCode((C&63)|128)}}}return A},_utf8_decode:function(A){var B="";var C=0;var D=c1=c2=0;while(C<A.length){D=A.charCodeAt(C);if(D<128){B+=String.fromCharCode(D);C++}else{if((D>191)&&(D<224)){c2=A.charCodeAt(C+1);B+=String.fromCharCode(((D&31)<<6)|(c2&63));C+=2}else{c2=A.charCodeAt(C+1);c3=A.charCodeAt(C+2);B+=String.fromCharCode(((D&15)<<12)|((c2&63)<<6)|(c3&63));C+=3}}}return B}};(function(D,K){var A="width",P="length",d="radius",Y="lines",R="trail",U="color",n="opacity",f="speed",Z="shadow",h="style",C="height",E="left",F="top",G="px",S="childNodes",m="firstChild",H="parentNode",c="position",I="relative",a="absolute",r="animation",V="transform",M="Origin",O="coord",j="#000",W=h+"Sheets",L="webkit0Moz0ms0O".split(0),q={},l;function p(t,v){var s=~~((t[P]-1)/2);for(var u=1;u<=s;u++){v(t[u*2-1],t[u*2])}}function k(s){var t=D.createElement(s||"div");p(arguments,function(v,u){t[v]=u});return t}function b(s,u,t){if(t&&!t[H]){b(s,t)}s.insertBefore(u,t||null);return s}b(D.getElementsByTagName("head")[0],k(h));var N=D[W][D[W][P]-1];function B(x,s){var u=[n,s,~~(x*100)].join("-"),t="{"+n+":"+x+"}",v;if(!q[u]){for(v=0;v<L[P];v++){try{N.insertRule("@"+(L[v]&&"-"+L[v].toLowerCase()+"-"||"")+"keyframes "+u+"{0%{"+n+":1}"+s+"%"+t+"to"+t+"}",N.cssRules[P])}catch(w){}}q[u]=1}return u}function Q(w,x){var v=w[h],t,u;if(v[x]!==K){return x}x=x.charAt(0).toUpperCase()+x.slice(1);for(u=0;u<L[P];u++){t=L[u]+x;if(v[t]!==K){return t}}}function e(s){p(arguments,function(u,t){s[h][Q(s,u)||u]=t});return s}function X(s){p(arguments,function(u,t){if(s[u]===K){s[u]=t}});return s}var T=function T(s){this.el=this[Y](this.opts=X(s||{},Y,12,R,100,P,7,A,5,d,10,U,j,n,1/4,f,1))},J=T.prototype={spin:function(y){var AA=this,t=AA.el;if(y){b(y,e(t,E,~~(y.offsetWidth/2)+G,F,~~(y.offsetHeight/2)+G),y[m])}AA.on=1;if(!l){var s=AA.opts,v=0,w=20/s[f],x=(1-s[n])/(w*s[R]/100),z=w/s[Y];(function u(){v++;for(var AB=s[Y];AB;AB--){var AC=Math.max(1-(v+AB*z)%w*x,s[n]);AA[n](t,s[Y]-AB,AC,s)}if(AA.on){setTimeout(u,50)}})()}return AA},stop:function(){var s=this,t=s.el;s.on=0;if(t[H]){t[H].removeChild(t)}return s}};J[Y]=function(x){var v=e(k(),c,I),u=B(x[n],x[R]),t=0,s;function w(y,z){return e(k(),c,a,A,(x[P]+x[A])+G,C,x[A]+G,"background",y,"boxShadow",z,V+M,E,V,"rotate("+~~(360/x[Y]*t)+"deg) translate("+x[d]+G+",0)","borderRadius","100em")}for(;t<x[Y];t++){s=e(k(),c,a,F,1+~(x[A]/2)+G,V,"translate3d(0,0,0)",r,u+" "+1/x[f]+"s linear infinite "+(1/x[Y]/x[f]*t-1/x[f])+"s");if(x[Z]){b(s,e(w(j,"0 0 4px "+j),F,2+G))}b(v,b(s,w(x[U],"0 0 1px rgba(0,0,0,.1)")))}return v};J[n]=function(t,s,u){t[S][s][h][n]=u};var o="behavior",i="url(#default#VML)",g="group0roundrect0fill0stroke".split(0);(function(){var u=e(k(g[0]),o,i),t;if(!Q(u,V)&&u.adj){for(t=0;t<g[P];t++){N.addRule(g[t],o+":"+i)}J[Y]=function(){var AC=this.opts,AA=AC[P]+AC[A],y=2*AA;function v(){return e(k(g[0],O+"size",y+" "+y,O+M,-AA+" "+-AA),A,y,C,y)}var z=v(),AB=~(AC[P]+AC[d]+AC[A])+G,x;function w(AD,s,AE){b(z,b(e(v(),"rotation",360/AC[Y]*AD+"deg",E,~~s),b(e(k(g[1],"arcsize",1),A,AA,C,AC[A],E,AC[d],F,-AC[A]/2,"filter",AE),k(g[2],U,AC[U],n,AC[n]),k(g[3],n,0))))}if(AC[Z]){for(x=1;x<=AC[Y];x++){w(x,-2,"progid:DXImage"+V+".Microsoft.Blur(pixel"+d+"=2,make"+Z+"=1,"+Z+n+"=.3)")}}for(x=1;x<=AC[Y];x++){w(x)}return b(e(k(),"margin",AB+" 0 0 "+AB,c,I),z)};J[n]=function(v,s,x,w){w=w[Z]&&w[Y]||0;v[m][S][s+w][m][m][n]=x}}else{l=Q(u,r)}})();window.Spinner=T})(document);function initStartCmMobile(){}HmEvents.eventTutorLastStep.subscribe(function(A){gwt_solutionHasBeenViewed()});function showWhiteboardActive(B){var A=B.parentNode.parentNode.getAttribute("pid");showWhiteboard_Gwt(A)};