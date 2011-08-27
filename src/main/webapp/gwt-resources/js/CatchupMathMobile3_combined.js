window.showCorrectAnswers=function(B){var D=document.getElementById("testset_div");if(D){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}}};function setQuizQuestionActive(A){}function findQuestionGuid(B){while(B){var A=B.getAttribute("guid");if(A){return A}B=B.parentNode}return null}function findQuestionByPid(B){var D=document.getElementById("testset_div").getElementsByTagName("div");try{for(var C=0;C<D.length;C++){var E=D[C].getAttribute("guid");if(E==B){return D[C]}}}catch(A){alert("Error while setting selected question response: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function findQuestionNumberByPid(B){var E=document.getElementById("testset_div").getElementsByTagName("div");var C=0;try{for(var D=0;D<E.length;D++){var F=E[D].getAttribute("guid");if(F){if(F==B){return C}else{C++}}}}catch(A){alert("Error while question index: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function questionGuessChanged(H){try{var B=findQuestionGuid(H);var G=-1;if(H.id=="optionSkipped"){G="-2"}else{var D=H.parentNode.parentNode.parentNode;var A=D.getElementsByTagName("input");for(var C=0;C<A.length;C++){if(A.item(C)==H){G=C;break}}}var E=findQuestionNumberByPid(B);questionGuessChanged_Gwt(""+E,""+G,B)}catch(F){alert("Error answering question in external JS: "+F)}}function setSolutionQuestionAnswerIndexByNumber(B,C){var A=0;showCorrectAnswers(function(H){var G=H.getElementsByTagName("input");if(A==B){for(var F=0,E=G.length;F<E;F++){if(F==C){G[F].checked=true;var D=G[F];questionGuessChanged(D)}else{G[F].checked=false}}}A++})}window.setSolutionQuestionAnswerIndex=function(B,H,E){var G=findQuestionByPid(B);if(G){var F=G.getElementsByTagName("input");for(var D=0,C=F.length;D<C;D++){var A=F.item(D);A.disabled=E?true:false;if(D==H){A.checked=true}}}};function doLoadResource(B,A){doLoadResource_Gwt(B,A);return false}window.markAllCorrectAnswers=function(){showCorrectAnswers(markCorrectResponse)};window.getQuizResultsCorrect=function(){var A=0;showCorrectAnswers(function(E){var D=E.getElementsByTagName("input");for(var C=0,B=D.length;C<B;C++){var F=D[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){if(D[C].checked){A++}}}});return A};window.getQuizQuestionCount=function(){var A=0;showCorrectAnswers(function(B){A++});return A};window.showCorrectAnswers=function(B){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}};window.markCorrectResponse=function(D){var E=D.getElementsByTagName("input");for(var C=0,B=E.length;C<B;C++){var F=E[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){E[C].checked=true;var A=E[C];questionGuessChanged(A);break}}};function checkQuiz_Gwt(){alert("Checking quiz ...")}window.setQuizQuestionResult=function(C,A){var E=findQuestionByPid(C);var D=getQuestionMarkImage(C);var B=getQuestionMarkText(C);if(A=="Correct"){D.src="/gwt-resources/images/check_correct.png";B.innerHTML="Correct"}else{if(A=="Incorrect"){D.src="/gwt-resources/images/check_incorrect.png";B.innerHTML="Incorrect"}else{D.src="/gwt-resources/images/check_notanswered.png";B.innerHTML="Not answered"}}D.parentNode.style.display="block"};function getQuestionMarkImage(A){return document.getElementById("response_image_"+A)}function getQuestionMarkText(A){return document.getElementById("response_text_"+A)}function log(){}InmhButtons={};if(typeof deconcept=="undefined"){var deconcept=new Object()}if(typeof deconcept.util=="undefined"){deconcept.util=new Object()}if(typeof deconcept.SWFObjectUtil=="undefined"){deconcept.SWFObjectUtil=new Object()}deconcept.SWFObject=function(K,B,L,D,H,I,F,E,C,J){if(!document.getElementById){return }this.DETECT_KEY=J?J:"detectflash";this.skipDetect=deconcept.util.getRequestParameter(this.DETECT_KEY);this.params=new Object();this.variables=new Object();this.attributes=new Array();if(K){this.setAttribute("swf",K)}if(B){this.setAttribute("id",B)}if(L){this.setAttribute("width",L)}if(D){this.setAttribute("height",D)}if(H){this.setAttribute("version",new deconcept.PlayerVersion(H.toString().split(".")))}this.installedVer=deconcept.SWFObjectUtil.getPlayerVersion();if(!window.opera&&document.all&&this.installedVer.major>7){deconcept.SWFObject.doPrepUnload=true}if(I){this.addParam("bgcolor",I)}var A=F?F:"high";this.addParam("quality",A);this.setAttribute("useExpressInstall",false);this.setAttribute("doExpressInstall",false);var G=(E)?E:window.location;this.setAttribute("xiRedirectUrl",G);this.setAttribute("redirectUrl","");if(C){this.setAttribute("redirectUrl",C)}};deconcept.SWFObject.prototype={useExpressInstall:function(A){this.xiSWFPath=!A?"expressinstall.swf":A;this.setAttribute("useExpressInstall",true)},setAttribute:function(A,B){this.attributes[A]=B},getAttribute:function(A){return this.attributes[A]},addParam:function(B,A){this.params[B]=A},getParams:function(){return this.params},addVariable:function(B,A){this.variables[B]=A},getVariable:function(A){return this.variables[A]},getVariables:function(){return this.variables},getVariablePairs:function(){var C=new Array();var B;var A=this.getVariables();for(B in A){C[C.length]=B+"="+A[B]}return C},getSWFHTML:function(){var B="";if(navigator.plugins&&navigator.mimeTypes&&navigator.mimeTypes.length){if(this.getAttribute("doExpressInstall")){this.addVariable("MMplayerType","PlugIn");this.setAttribute("swf",this.xiSWFPath)}B='<embed type="application/x-shockwave-flash" src="'+this.getAttribute("swf")+'" width="'+this.getAttribute("width")+'" height="'+this.getAttribute("height")+'" style="'+this.getAttribute("style")+'"';B+=' id="'+this.getAttribute("id")+'" name="'+this.getAttribute("id")+'" ';var F=this.getParams();for(var E in F){B+=[E]+'="'+F[E]+'" '}var D=this.getVariablePairs().join("&");if(D.length>0){B+='flashvars="'+D+'"'}B+="/>"}else{if(this.getAttribute("doExpressInstall")){this.addVariable("MMplayerType","ActiveX");this.setAttribute("swf",this.xiSWFPath)}B='<object id="'+this.getAttribute("id")+'" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="'+this.getAttribute("width")+'" height="'+this.getAttribute("height")+'" style="'+this.getAttribute("style")+'">';B+='<param name="movie" value="'+this.getAttribute("swf")+'" />';var C=this.getParams();for(var E in C){B+='<param name="'+E+'" value="'+C[E]+'" />'}var A=this.getVariablePairs().join("&");if(A.length>0){B+='<param name="flashvars" value="'+A+'" />'}B+="</object>"}return B},write:function(B){if(this.getAttribute("useExpressInstall")){var A=new deconcept.PlayerVersion([6,0,65]);if(this.installedVer.versionIsValid(A)&&!this.installedVer.versionIsValid(this.getAttribute("version"))){this.setAttribute("doExpressInstall",true);this.addVariable("MMredirectURL",escape(this.getAttribute("xiRedirectUrl")));document.title=document.title.slice(0,47)+" - Flash Player Installation";this.addVariable("MMdoctitle",document.title)}}if(this.skipDetect||this.getAttribute("doExpressInstall")||this.installedVer.versionIsValid(this.getAttribute("version"))){var C=(typeof B=="string")?document.getElementById(B):B;C.innerHTML=this.getSWFHTML();return true}else{if(this.getAttribute("redirectUrl")!=""){document.location.replace(this.getAttribute("redirectUrl"))}}return false}};deconcept.SWFObjectUtil.getPlayerVersion=function(){var E=new deconcept.PlayerVersion([0,0,0]);if(navigator.plugins&&navigator.mimeTypes.length){var A=navigator.plugins["Shockwave Flash"];if(A&&A.description){E=new deconcept.PlayerVersion(A.description.replace(/([a-zA-Z]|\s)+/,"").replace(/(\s+r|\s+b[0-9]+)/,".").split("."))}}else{if(navigator.userAgent&&navigator.userAgent.indexOf("Windows CE")>=0){var B=1;var C=3;while(B){try{C++;B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash."+C);E=new deconcept.PlayerVersion([C,0,0])}catch(D){B=null}}}else{try{var B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7")}catch(D){try{var B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");E=new deconcept.PlayerVersion([6,0,21]);B.AllowScriptAccess="always"}catch(D){if(E.major==6){return E}}try{B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash")}catch(D){}}if(B!=null){E=new deconcept.PlayerVersion(B.GetVariable("$version").split(" ")[1].split(","))}}}return E};deconcept.PlayerVersion=function(A){this.major=A[0]!=null?parseInt(A[0]):0;this.minor=A[1]!=null?parseInt(A[1]):0;this.rev=A[2]!=null?parseInt(A[2]):0};deconcept.PlayerVersion.prototype.versionIsValid=function(A){if(this.major<A.major){return false}if(this.major>A.major){return true}if(this.minor<A.minor){return false}if(this.minor>A.minor){return true}if(this.rev<A.rev){return false}return true};deconcept.util={getRequestParameter:function(C){var D=document.location.search||document.location.hash;if(C==null){return D}if(D){var B=D.substring(1).split("&");for(var A=0;A<B.length;A++){if(B[A].substring(0,B[A].indexOf("="))==C){return B[A].substring((B[A].indexOf("=")+1))}}}return""}};deconcept.SWFObjectUtil.cleanupSWFs=function(){var B=document.getElementsByTagName("OBJECT");for(var C=B.length-1;C>=0;C--){B[C].style.display="none";for(var A in B[C]){if(typeof B[C][A]=="function"){B[C][A]=function(){}}}}};if(deconcept.SWFObject.doPrepUnload){if(!deconcept.unloadSet){deconcept.SWFObjectUtil.prepUnload=function(){__flash_unloadHandler=function(){};__flash_savedUnloadHandler=function(){};window.attachEvent("onunload",deconcept.SWFObjectUtil.cleanupSWFs)};window.attachEvent("onbeforeunload",deconcept.SWFObjectUtil.prepUnload);deconcept.unloadSet=true}}if(!document.getElementById&&document.all){document.getElementById=function(A){return document.all[A]}}var getQueryParamValue=deconcept.util.getRequestParameter;var FlashObject=deconcept.SWFObject;var SWFObject=deconcept.SWFObject;function f_clientWidth(){return f_filterResults(window.innerWidth?window.innerWidth:0,document.documentElement?document.documentElement.clientWidth:0,document.body?document.body.clientWidth:0)}function f_clientHeight(){return f_filterResults(window.innerHeight?window.innerHeight:0,document.documentElement?document.documentElement.clientHeight:0,document.body?document.body.clientHeight:0)}function f_scrollLeft(){return f_filterResults(window.pageXOffset?window.pageXOffset:0,document.documentElement?document.documentElement.scrollLeft:0,document.body?document.body.scrollLeft:0)}function f_scrollTop(){return f_filterResults(window.pageYOffset?window.pageYOffset:0,document.documentElement?document.documentElement.scrollTop:0,document.body?document.body.scrollTop:0)}function f_filterResults(D,B,A){var C=D?D:0;if(B&&(!C||(C>B))){C=B}return A&&(!C||(C>A))?A:C};var _productionMode=false;var HmEvents={eventTutorInitialized:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorInitialized.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorInitialized.listeners;for(var A=0;A<B.length;A++){B[A]()}}},eventTutorLastStep:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorLastStep.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorLastStep.listeners;for(var A=0;A<B.length;A++){B[A]()}}},eventTutorWidgetComplete:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorWidgetComplete.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorWidgetComplete.listeners;for(var A=0;A<B.length;A++){B[A]()}}}};function $get(A){return document.getElementById(A)}HmEvents.eventTutorInitialized.subscribe(function(){try{MathJax.Hub.Queue(["Typeset",MathJax.Hub])}catch(A){alert("MathJAX processing failed: "+A)}});function setStepsInfoHelp(){}function resetStepsInfo(){}function getNextMoveTo(){}var TutorManager={currentRealStep:-1,currentStepUnit:-1,stepUnitsMo:[],stepUnits:[],steps:[],pid:"",stepUnit:null,tutorData:null,initializeTutor:function(A,D,C,B){TutorManager.pid=A;TutorManager.currentRealStep=-1;TutorManager.currentStepUnit=-1;TutorManager.loadTutorData(D);TutorManager.analyzeLoadedData();enabledNext(true);HmEvents.eventTutorInitialized.fire()},showMessage:function(B){var A=$get("tutor_message");A.innerHTML=B;setTimeout(function(){A.innerHTML="&nbsp;"},2000)},showNextStep:function(){if(TutorManager.currentStepUnit+1<TutorManager.stepUnits.length){TutorManager.currentStepUnit++;showStepUnit(TutorManager.currentStepUnit)}else{TutorManager.showMessage("no more steps")}},showPreviousStep:function(){if(TutorManager.currentStepUnit<0){TutorManager.showMessage("No previous step");return }else{while(TutorManager.currentStepUnit>-1){var A=TutorManager.stepUnits[TutorManager.currentStepUnit].ele;if(TutorManager.stepUnits[TutorManager.currentStepUnit].realNum!=TutorManager.currentRealStep){TutorManager.currentRealStep=TutorManager.stepUnits[TutorManager.currentStepUnit].realNum;break}A.style.display="none";TutorManager.currentStepUnit--}if(TutorManager.currentStepUnit==0){TutorManager.currentStepUnit=-1;window.scrollTo(0,0)}if(TutorManager.currentStepUnit>-1){setAsCurrent(TutorManager.stepUnits[TutorManager.currentStepUnit].ele)}setButtonState();scrollToStep(TutorManager.currentStepUnit);return false}},loadTutorData:function(solutionData){try{TutorManager.tutorData=eval("("+solutionData+")")}catch(e){alert(e)}},analyzeLoadedData:function(){TutorManager.stepUnits=[];TutorManager.steps=[];var I=100;for(var J=0;J<I;J++){var E=_getStepUnit(J);if(E==null){break}var B=E.getAttribute("id");var A=TutorManager.stepUnits.length;var C=E.getAttribute("steprole");var F=E.getAttribute("steptype");var G=parseInt(E.getAttribute("realstep"));var H=new StepUnit(B,A,F,C,G,E);TutorManager.stepUnits[TutorManager.stepUnits.length]=H;var D=TutorManager.steps[G];if(D==null){D=new Step(G);TutorManager.steps[G]=D}D.stepUnits[D.stepUnits.length]=H}return TutorManager.stepUnits.length},backToLesson:function(){gwt_backToLesson()},newProblem:function(){gwt_tutorNewProblem()}};function setButtonState(){setState("step",TutorManager.currentStepUnit<(TutorManager.stepUnits.length-1));setState("back",TutorManager.currentStepUnit>-1)}function enabledPrevious(A){enabledButton("steps_prev",A)}function enabledNext(A){enabledButton("steps_next",A)}function enabledButton(B,C){var A="sexybutton ";if(!C){A+=" disabled"}$get(B).className=A}function StepUnit(F,E,B,A,D,C){this.id=F;this.stepUnitNum=E;this.type=B;this.roleType=A;this.realNum=D;this.ele=C}function Step(A){this.realNum=A;this.stepUnits=new Array()}function _getStepUnit(A){return _getElement("stepunit",A)}function _getHintUnit(A){return _getElement("hintunit",A)}function _getFigureUnit(A){return _getElement("figure",A)}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setAsNotCurrent(A){A.style.backgroundColor="#E2E2E2"}function _getElement(A,B){var C=A+"-"+B;return document.getElementById(C)}function showStepUnit(A){if(A<0){return }try{var B=TutorManager.stepUnits[A].ele;if(B==null){return false}B.style.display="block";if(B.getAttribute("steprole")=="step"){setAsCurrent(B)}setStepTitle(A,B);var D=_getFigureUnit(A);if(D!=null){if(A==0){D.style.display="block"}else{var E=findPreviousFigureUnit(A);if(E!=null&&E.src==D.src){D.style.display="none"}else{D.style.display="block"}}}for(i=A-1;i>-1;i--){if(TutorManager.stepUnits[i].roleType=="hint"){TutorManager.stepUnits[i].ele.style.display="none"}else{setAsNotCurrent(TutorManager.stepUnits[i].ele)}}TutorManager.currentStepUnit=A;TutorManager.currentRealStep=TutorManager.stepUnits[A].realNum;setButtonState();scrollToStep(A)}catch(C){alert("Error showing step: "+C)}return true}function setAsCurrent(A){A.style.backgroundColor="#F1F1F1"}function setStepTitle(A,C){stepTitle=document.getElementById("step_title-"+A);if(stepTitle){var B=C.getAttribute("steprole");if(B&&B=="step"){stepTitle.innerHTML="Step "+(parseInt(C.getAttribute("realstep"))+1);stepTitle.className="step_title_step"}else{stepTitle.innerHTML="Hint";stepTitle.className="step_title_hint"}}}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setState(B,A){if(B=="step"){enabledNext(A);if(!A){HmEvents.eventTutorLastStep.fire()}}else{if(B=="back"){enabledPrevious(A)}}}function scrollToStep(C){var B=document.getElementById("scrollTo-button");if(B){var G=DL_GetElementTop(B);var E=getViewableSize();var A=getScrollXY();var F=A[1];var D=E[1];var H=D+F;if(true||G<F||G>H){gwt_scrollToBottomOfScrollPanel(G-D)}}}function hideAllSteps(){for(var A=0;A<TutorManager.stepUnits.length;A++){var B=TutorManager.stepUnits[A].ele;if(B==null){return }if(B.style.display!="none"){B.style.display="none"}}window.scrollTo(0,0)}function initializeExternalJs(){var A="control-floater";new FloatLayer(A,150,15,10);detach(A);alignControlFloater()}function alignControlFloater(){alignFloatLayers();setTimeout(alignControlFloater,2000)}function doQuestionResponseEnd(){}var _activeQuestion;function doQuestionResponse(A,D){var C=TutorManager.tutorData._strings_moArray[A];if(_activeQuestion){var B=document.createElement("div");B.className="questionResponseAnswer";B.innerHTML=C;_activeQuestion.parentNode.appendChild(B)}else{gwt_showMessage(C)}}HmEvents.eventTutorInitialized.subscribe(function(){var H=document.getElementById("tutor_raw_steps_wrapper");if(H==null){return }var B=H.getElementsByTagName("div");var A=B.length;for(var E=0;E<A;E++){var G=B.item(E);if(G.className=="question_guess"){var F=G.getElementsByTagName("img");var D=F.item(0);var C=D.onmouseout=null;D.onmouseoverDeferred=D.onmouseover;D.onmouseover=null;D.onclick=function(I){var J=(I)?I:window.event;var K=J.srcElement?J.srcElement:J.target;_activeQuestion=K;if(!K.onmouseoverDeferred){alert("error: no deferred move event");return }K.onclick=null;K.onmouseoverDeferred()}}}});function DL_GetElementLeft(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetLeft;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientLeft}}A+=D.offsetLeft;D=D.offsetParent}return A}function DL_GetElementTop(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetTop;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientTop}}A+=D.offsetTop;D=D.offsetParent}return A}function getViewableSize(){var B=0,A=0;if(typeof (window.innerWidth)=="number"){B=window.innerWidth;A=window.innerHeight}else{if(document.documentElement&&(document.documentElement.clientWidth||document.documentElement.clientHeight)){B=document.documentElement.clientWidth;A=document.documentElement.clientHeight}else{if(document.body&&(document.body.clientWidth||document.body.clientHeight)){B=document.body.clientWidth;A=document.body.clientHeight}}}a=[B,A];return a}function getScrollXY(){var B=0,A=0;if(typeof (window.pageYOffset)=="number"){A=window.pageYOffset;B=window.pageXOffset}else{if(document.body&&(document.body.scrollLeft||document.body.scrollTop)){A=document.body.scrollTop;B=document.body.scrollLeft}else{if(document.documentElement&&(document.documentElement.scrollLeft||document.documentElement.scrollTop)){A=document.documentElement.scrollTop;B=document.documentElement.scrollLeft}}}return[B,A]}function _addEvent(E,D,B,A){if(E.addEventListener){E.addEventListener(D,B,A);return true}else{if(E.attachEvent){var C=E.attachEvent("on"+D,B);return C}else{alert("Handler could not be attached")}}}function _removeEvent(E,D,B,A){if(E.removeEventListener){E.removeEventListener(D,B,A);return true}else{if(E.detachEvent){var C=E.detachEvent("on"+D,B);return C}else{alert("Handler could not be removed")}}}function hideDivOnMouseOut(A){var C,B;if(window.event){C=this;B=window.event.toElement}else{C=A.currentTarget;B=A.relatedTarget}if(C!=B){if(!contains(C,B)){C.style.display="none"}}}function contains(B,A){while(A.parentNode){A=A.parentNode;if(A==B){return true}}return false}function grabComputedStyle(B,A){if(document.defaultView&&document.defaultView.getComputedStyle){return document.defaultView.getComputedStyle(B,null).getPropertyValue(A)}else{if(B.currentStyle){return B.currentStyle[A]}else{return null}}}function grabComputedHeight(B){var A=grabComputedStyle(B,"height");if(A!=null){if(A=="auto"){if(B.offsetHeight){A=B.offsetHeight}}A=parseInt(A)}return A}function grabComputedWidth(B){var A=grabComputedStyle(B,"width");if(A!=null){if(A.indexOf("px")!=-1){A=A.substring(0,A.indexOf("px"))}if(A=="auto"){if(B.offsetWidth){A=B.offsetWidth}}}return A};if(!document.createElement("canvas").getContext){(function(){var AB=Math;var K=AB.round;var J=AB.sin;var W=AB.cos;var e=AB.abs;var n=AB.sqrt;var D=10;var F=D/2;var V=+navigator.userAgent.match(/MSIE ([\d.]+)?/)[1];function U(){return this.context_||(this.context_=new a(this))}var P=Array.prototype.slice;function G(i,j,m){var Z=P.call(arguments,2);return function(){return i.apply(j,Z.concat(P.call(arguments)))}}function AF(Z){return String(Z).replace(/&/g,"&amp;").replace(/"/g,"&quot;")}function z(j,i,Z){if(!j.namespaces[i]){j.namespaces.add(i,Z,"#default#VML")}}function s(i){z(i,"g_vml_","urn:schemas-microsoft-com:vml");z(i,"g_o_","urn:schemas-microsoft-com:office:office");if(!i.styleSheets.ex_canvas_){var Z=i.createStyleSheet();Z.owningElement.id="ex_canvas_";Z.cssText="canvas{display:inline-block;overflow:hidden;text-align:left;width:300px;height:150px}"}}s(document);var E={init:function(Z){var i=Z||document;i.createElement("canvas");i.attachEvent("onreadystatechange",G(this.init_,this,i))},init_:function(m){var j=m.getElementsByTagName("canvas");for(var Z=0;Z<j.length;Z++){this.initElement(j[Z])}},initElement:function(i){if(!i.getContext){i.getContext=U;s(i.ownerDocument);i.innerHTML="";i.attachEvent("onpropertychange",T);i.attachEvent("onresize",x);var Z=i.attributes;if(Z.width&&Z.width.specified){i.style.width=Z.width.nodeValue+"px"}else{i.width=i.clientWidth}if(Z.height&&Z.height.specified){i.style.height=Z.height.nodeValue+"px"}else{i.height=i.clientHeight}}return i}};function T(i){var Z=i.srcElement;switch(i.propertyName){case"width":Z.getContext().clearRect();Z.style.width=Z.attributes.width.nodeValue+"px";Z.firstChild.style.width=Z.clientWidth+"px";break;case"height":Z.getContext().clearRect();Z.style.height=Z.attributes.height.nodeValue+"px";Z.firstChild.style.height=Z.clientHeight+"px";break}}function x(i){var Z=i.srcElement;if(Z.firstChild){Z.firstChild.style.width=Z.clientWidth+"px";Z.firstChild.style.height=Z.clientHeight+"px"}}E.init();var I=[];for(var AE=0;AE<16;AE++){for(var AD=0;AD<16;AD++){I[AE*16+AD]=AE.toString(16)+AD.toString(16)}}function X(){return[[1,0,0],[0,1,0],[0,0,1]]}function g(m,j){var i=X();for(var Z=0;Z<3;Z++){for(var AH=0;AH<3;AH++){var p=0;for(var AG=0;AG<3;AG++){p+=m[Z][AG]*j[AG][AH]}i[Z][AH]=p}}return i}function R(i,Z){Z.fillStyle=i.fillStyle;Z.lineCap=i.lineCap;Z.lineJoin=i.lineJoin;Z.lineWidth=i.lineWidth;Z.miterLimit=i.miterLimit;Z.shadowBlur=i.shadowBlur;Z.shadowColor=i.shadowColor;Z.shadowOffsetX=i.shadowOffsetX;Z.shadowOffsetY=i.shadowOffsetY;Z.strokeStyle=i.strokeStyle;Z.globalAlpha=i.globalAlpha;Z.font=i.font;Z.textAlign=i.textAlign;Z.textBaseline=i.textBaseline;Z.arcScaleX_=i.arcScaleX_;Z.arcScaleY_=i.arcScaleY_;Z.lineScale_=i.lineScale_}var B={aliceblue:"#F0F8FF",antiquewhite:"#FAEBD7",aquamarine:"#7FFFD4",azure:"#F0FFFF",beige:"#F5F5DC",bisque:"#FFE4C4",black:"#000000",blanchedalmond:"#FFEBCD",blueviolet:"#8A2BE2",brown:"#A52A2A",burlywood:"#DEB887",cadetblue:"#5F9EA0",chartreuse:"#7FFF00",chocolate:"#D2691E",coral:"#FF7F50",cornflowerblue:"#6495ED",cornsilk:"#FFF8DC",crimson:"#DC143C",cyan:"#00FFFF",darkblue:"#00008B",darkcyan:"#008B8B",darkgoldenrod:"#B8860B",darkgray:"#A9A9A9",darkgreen:"#006400",darkgrey:"#A9A9A9",darkkhaki:"#BDB76B",darkmagenta:"#8B008B",darkolivegreen:"#556B2F",darkorange:"#FF8C00",darkorchid:"#9932CC",darkred:"#8B0000",darksalmon:"#E9967A",darkseagreen:"#8FBC8F",darkslateblue:"#483D8B",darkslategray:"#2F4F4F",darkslategrey:"#2F4F4F",darkturquoise:"#00CED1",darkviolet:"#9400D3",deeppink:"#FF1493",deepskyblue:"#00BFFF",dimgray:"#696969",dimgrey:"#696969",dodgerblue:"#1E90FF",firebrick:"#B22222",floralwhite:"#FFFAF0",forestgreen:"#228B22",gainsboro:"#DCDCDC",ghostwhite:"#F8F8FF",gold:"#FFD700",goldenrod:"#DAA520",grey:"#808080",greenyellow:"#ADFF2F",honeydew:"#F0FFF0",hotpink:"#FF69B4",indianred:"#CD5C5C",indigo:"#4B0082",ivory:"#FFFFF0",khaki:"#F0E68C",lavender:"#E6E6FA",lavenderblush:"#FFF0F5",lawngreen:"#7CFC00",lemonchiffon:"#FFFACD",lightblue:"#ADD8E6",lightcoral:"#F08080",lightcyan:"#E0FFFF",lightgoldenrodyellow:"#FAFAD2",lightgreen:"#90EE90",lightgrey:"#D3D3D3",lightpink:"#FFB6C1",lightsalmon:"#FFA07A",lightseagreen:"#20B2AA",lightskyblue:"#87CEFA",lightslategray:"#778899",lightslategrey:"#778899",lightsteelblue:"#B0C4DE",lightyellow:"#FFFFE0",limegreen:"#32CD32",linen:"#FAF0E6",magenta:"#FF00FF",mediumaquamarine:"#66CDAA",mediumblue:"#0000CD",mediumorchid:"#BA55D3",mediumpurple:"#9370DB",mediumseagreen:"#3CB371",mediumslateblue:"#7B68EE",mediumspringgreen:"#00FA9A",mediumturquoise:"#48D1CC",mediumvioletred:"#C71585",midnightblue:"#191970",mintcream:"#F5FFFA",mistyrose:"#FFE4E1",moccasin:"#FFE4B5",navajowhite:"#FFDEAD",oldlace:"#FDF5E6",olivedrab:"#6B8E23",orange:"#FFA500",orangered:"#FF4500",orchid:"#DA70D6",palegoldenrod:"#EEE8AA",palegreen:"#98FB98",paleturquoise:"#AFEEEE",palevioletred:"#DB7093",papayawhip:"#FFEFD5",peachpuff:"#FFDAB9",peru:"#CD853F",pink:"#FFC0CB",plum:"#DDA0DD",powderblue:"#B0E0E6",rosybrown:"#BC8F8F",royalblue:"#4169E1",saddlebrown:"#8B4513",salmon:"#FA8072",sandybrown:"#F4A460",seagreen:"#2E8B57",seashell:"#FFF5EE",sienna:"#A0522D",skyblue:"#87CEEB",slateblue:"#6A5ACD",slategray:"#708090",slategrey:"#708090",snow:"#FFFAFA",springgreen:"#00FF7F",steelblue:"#4682B4",tan:"#D2B48C",thistle:"#D8BFD8",tomato:"#FF6347",turquoise:"#40E0D0",violet:"#EE82EE",wheat:"#F5DEB3",whitesmoke:"#F5F5F5",yellowgreen:"#9ACD32"};function l(i){var m=i.indexOf("(",3);var Z=i.indexOf(")",m+1);var j=i.substring(m+1,Z).split(",");if(j.length!=4||i.charAt(3)!="a"){j[3]=1}return j}function C(Z){return parseFloat(Z)/100}function N(i,j,Z){return Math.min(Z,Math.max(j,i))}function f(AG){var Z,AI,AJ,AH,AK,m;AH=parseFloat(AG[0])/360%360;if(AH<0){AH++}AK=N(C(AG[1]),0,1);m=N(C(AG[2]),0,1);if(AK==0){Z=AI=AJ=m}else{var i=m<0.5?m*(1+AK):m+AK-m*AK;var j=2*m-i;Z=A(j,i,AH+1/3);AI=A(j,i,AH);AJ=A(j,i,AH-1/3)}return"#"+I[Math.floor(Z*255)]+I[Math.floor(AI*255)]+I[Math.floor(AJ*255)]}function A(i,Z,j){if(j<0){j++}if(j>1){j--}if(6*j<1){return i+(Z-i)*6*j}else{if(2*j<1){return Z}else{if(3*j<2){return i+(Z-i)*(2/3-j)*6}else{return i}}}}var Y={};function c(Z){if(Z in Y){return Y[Z]}var AG,p=1;Z=String(Z);if(Z.charAt(0)=="#"){AG=Z}else{if(/^rgb/.test(Z)){var m=l(Z);var AG="#",AH;for(var j=0;j<3;j++){if(m[j].indexOf("%")!=-1){AH=Math.floor(C(m[j])*255)}else{AH=+m[j]}AG+=I[N(AH,0,255)]}p=+m[3]}else{if(/^hsl/.test(Z)){var m=l(Z);AG=f(m);p=m[3]}else{AG=B[Z]||Z}}}return Y[Z]={color:AG,alpha:p}}var L={style:"normal",variant:"normal",weight:"normal",size:10,family:"sans-serif"};var k={};function b(Z){if(k[Z]){return k[Z]}var m=document.createElement("div");var j=m.style;try{j.font=Z}catch(i){}return k[Z]={style:j.fontStyle||L.style,variant:j.fontVariant||L.variant,weight:j.fontWeight||L.weight,size:j.fontSize||L.size,family:j.fontFamily||L.family}}function Q(j,i){var Z={};for(var AH in j){Z[AH]=j[AH]}var AG=parseFloat(i.currentStyle.fontSize),m=parseFloat(j.size);if(typeof j.size=="number"){Z.size=j.size}else{if(j.size.indexOf("px")!=-1){Z.size=m}else{if(j.size.indexOf("em")!=-1){Z.size=AG*m}else{if(j.size.indexOf("%")!=-1){Z.size=(AG/100)*m}else{if(j.size.indexOf("pt")!=-1){Z.size=m/0.75}else{Z.size=AG}}}}}Z.size*=0.981;return Z}function AC(Z){return Z.style+" "+Z.variant+" "+Z.weight+" "+Z.size+"px "+Z.family}var O={butt:"flat",round:"round"};function t(Z){return O[Z]||"square"}function a(Z){this.m_=X();this.mStack_=[];this.aStack_=[];this.currentPath_=[];this.strokeStyle="#000";this.fillStyle="#000";this.lineWidth=1;this.lineJoin="miter";this.lineCap="butt";this.miterLimit=D*1;this.globalAlpha=1;this.font="10px sans-serif";this.textAlign="left";this.textBaseline="alphabetic";this.canvas=Z;var j="width:"+Z.clientWidth+"px;height:"+Z.clientHeight+"px;overflow:hidden;position:absolute";var i=Z.ownerDocument.createElement("div");i.style.cssText=j;Z.appendChild(i);var m=i.cloneNode(false);m.style.backgroundColor="red";m.style.filter="alpha(opacity=0)";Z.appendChild(m);this.element_=i;this.arcScaleX_=1;this.arcScaleY_=1;this.lineScale_=1}var M=a.prototype;M.clearRect=function(){if(this.textMeasureEl_){this.textMeasureEl_.removeNode(true);this.textMeasureEl_=null}this.element_.innerHTML=""};M.beginPath=function(){this.currentPath_=[]};M.moveTo=function(i,Z){var j=w(this,i,Z);this.currentPath_.push({type:"moveTo",x:j.x,y:j.y});this.currentX_=j.x;this.currentY_=j.y};M.lineTo=function(i,Z){var j=w(this,i,Z);this.currentPath_.push({type:"lineTo",x:j.x,y:j.y});this.currentX_=j.x;this.currentY_=j.y};M.bezierCurveTo=function(j,i,AK,AJ,AI,AG){var Z=w(this,AI,AG);var AH=w(this,j,i);var m=w(this,AK,AJ);h(this,AH,m,Z)};function h(Z,m,j,i){Z.currentPath_.push({type:"bezierCurveTo",cp1x:m.x,cp1y:m.y,cp2x:j.x,cp2y:j.y,x:i.x,y:i.y});Z.currentX_=i.x;Z.currentY_=i.y}M.quadraticCurveTo=function(AI,j,i,Z){var AH=w(this,AI,j);var AG=w(this,i,Z);var AJ={x:this.currentX_+2/3*(AH.x-this.currentX_),y:this.currentY_+2/3*(AH.y-this.currentY_)};var m={x:AJ.x+(AG.x-this.currentX_)/3,y:AJ.y+(AG.y-this.currentY_)/3};h(this,AJ,m,AG)};M.arc=function(AL,AJ,AK,AG,i,j){AK*=D;var AP=j?"at":"wa";var AM=AL+W(AG)*AK-F;var AO=AJ+J(AG)*AK-F;var Z=AL+W(i)*AK-F;var AN=AJ+J(i)*AK-F;if(AM==Z&&!j){AM+=0.125}var m=w(this,AL,AJ);var AI=w(this,AM,AO);var AH=w(this,Z,AN);this.currentPath_.push({type:AP,x:m.x,y:m.y,radius:AK,xStart:AI.x,yStart:AI.y,xEnd:AH.x,yEnd:AH.y})};M.rect=function(j,i,Z,m){this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath()};M.strokeRect=function(j,i,Z,m){var p=this.currentPath_;this.beginPath();this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath();this.stroke();this.currentPath_=p};M.fillRect=function(j,i,Z,m){var p=this.currentPath_;this.beginPath();this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath();this.fill();this.currentPath_=p};M.createLinearGradient=function(i,m,Z,j){var p=new v("gradient");p.x0_=i;p.y0_=m;p.x1_=Z;p.y1_=j;return p};M.createRadialGradient=function(m,AG,j,i,p,Z){var AH=new v("gradientradial");AH.x0_=m;AH.y0_=AG;AH.r0_=j;AH.x1_=i;AH.y1_=p;AH.r1_=Z;return AH};M.drawImage=function(AR,m){var AK,AI,AM,AY,AP,AN,AT,Aa;var AL=AR.runtimeStyle.width;var AQ=AR.runtimeStyle.height;AR.runtimeStyle.width="auto";AR.runtimeStyle.height="auto";var AJ=AR.width;var AW=AR.height;AR.runtimeStyle.width=AL;AR.runtimeStyle.height=AQ;if(arguments.length==3){AK=arguments[1];AI=arguments[2];AP=AN=0;AT=AM=AJ;Aa=AY=AW}else{if(arguments.length==5){AK=arguments[1];AI=arguments[2];AM=arguments[3];AY=arguments[4];AP=AN=0;AT=AJ;Aa=AW}else{if(arguments.length==9){AP=arguments[1];AN=arguments[2];AT=arguments[3];Aa=arguments[4];AK=arguments[5];AI=arguments[6];AM=arguments[7];AY=arguments[8]}else{throw Error("Invalid number of arguments")}}}if(AR.tagName=="canvas"){var i=document.createElement("div");i.style.position="absolute";i.style.left=AK+"px";i.style.top=AI+"px";i.innerHTML=AR.outerHTML;this.element_.insertAdjacentHTML("BeforeEnd",i.outerHTML);return }var AZ=w(this,AK,AI);var p=AT/2;var j=Aa/2;var AX=[];var Z=10;var AH=10;AX.push(" <g_vml_:group",' coordsize="',D*Z,",",D*AH,'"',' coordorigin="0,0"',' style="width:',Z,"px;height:",AH,"px;position:absolute;");if(this.m_[0][0]!=1||this.m_[0][1]||this.m_[1][1]!=1||this.m_[1][0]){var AG=[];AG.push("M11=",this.m_[0][0],",","M12=",this.m_[1][0],",","M21=",this.m_[0][1],",","M22=",this.m_[1][1],",","Dx=",K(AZ.x/D),",","Dy=",K(AZ.y/D),"");var AV=AZ;var AU=w(this,AK+AM,AI);var AS=w(this,AK,AI+AY);var AO=w(this,AK+AM,AI+AY);AV.x=AB.max(AV.x,AU.x,AS.x,AO.x);AV.y=AB.max(AV.y,AU.y,AS.y,AO.y);AX.push("padding:0 ",K(AV.x/D),"px ",K(AV.y/D),"px 0;filter:progid:DXImageTransform.Microsoft.Matrix(",AG.join(""),", sizingmethod='clip');")}else{AX.push("top:",K(AZ.y/D),"px;left:",K(AZ.x/D),"px;")}AX.push(' ">','<g_vml_:image src="',AR.src,'"',' style="width:',D*AM,"px;"," height:",D*AY,'px"',' cropleft="',AP/AJ,'"',' croptop="',AN/AW,'"',' cropright="',(AJ-AP-AT)/AJ,'"',' cropbottom="',(AW-AN-Aa)/AW,'"'," />","</g_vml_:group>");this.element_.insertAdjacentHTML("BeforeEnd",AX.join(""))};M.stroke=function(AL){var AJ=[];var m=false;var j=10;var AM=10;AJ.push("<g_vml_:shape",' filled="',!!AL,'"',' style="position:absolute;width:',j,"px;height:",AM,'px;"',' coordorigin="0,0"',' coordsize="',D*j,",",D*AM,'"',' stroked="',!AL,'"',' path="');var AN=false;var AG={x:null,y:null};var AK={x:null,y:null};for(var AH=0;AH<this.currentPath_.length;AH++){var Z=this.currentPath_[AH];var AI;switch(Z.type){case"moveTo":AI=Z;AJ.push(" m ",K(Z.x),",",K(Z.y));break;case"lineTo":AJ.push(" l ",K(Z.x),",",K(Z.y));break;case"close":AJ.push(" x ");Z=null;break;case"bezierCurveTo":AJ.push(" c ",K(Z.cp1x),",",K(Z.cp1y),",",K(Z.cp2x),",",K(Z.cp2y),",",K(Z.x),",",K(Z.y));break;case"at":case"wa":AJ.push(" ",Z.type," ",K(Z.x-this.arcScaleX_*Z.radius),",",K(Z.y-this.arcScaleY_*Z.radius)," ",K(Z.x+this.arcScaleX_*Z.radius),",",K(Z.y+this.arcScaleY_*Z.radius)," ",K(Z.xStart),",",K(Z.yStart)," ",K(Z.xEnd),",",K(Z.yEnd));break}if(Z){if(AG.x==null||Z.x<AG.x){AG.x=Z.x}if(AK.x==null||Z.x>AK.x){AK.x=Z.x}if(AG.y==null||Z.y<AG.y){AG.y=Z.y}if(AK.y==null||Z.y>AK.y){AK.y=Z.y}}}AJ.push(' ">');if(!AL){S(this,AJ)}else{d(this,AJ,AG,AK)}AJ.push("</g_vml_:shape>");this.element_.insertAdjacentHTML("beforeEnd",AJ.join(""))};function S(j,AG){var i=c(j.strokeStyle);var m=i.color;var p=i.alpha*j.globalAlpha;var Z=j.lineScale_*j.lineWidth;if(Z<1){p*=Z}AG.push("<g_vml_:stroke",' opacity="',p,'"',' joinstyle="',j.lineJoin,'"',' miterlimit="',j.miterLimit,'"',' endcap="',t(j.lineCap),'"',' weight="',Z,'px"',' color="',m,'" />')}function d(AQ,AI,Aj,AR){var AJ=AQ.fillStyle;var Aa=AQ.arcScaleX_;var AZ=AQ.arcScaleY_;var Z=AR.x-Aj.x;var m=AR.y-Aj.y;if(AJ instanceof v){var AN=0;var Ae={x:0,y:0};var AW=0;var AM=1;if(AJ.type_=="gradient"){var AL=AJ.x0_/Aa;var j=AJ.y0_/AZ;var AK=AJ.x1_/Aa;var Al=AJ.y1_/AZ;var Ai=w(AQ,AL,j);var Ah=w(AQ,AK,Al);var AG=Ah.x-Ai.x;var p=Ah.y-Ai.y;AN=Math.atan2(AG,p)*180/Math.PI;if(AN<0){AN+=360}if(AN<0.000001){AN=0}}else{var Ai=w(AQ,AJ.x0_,AJ.y0_);Ae={x:(Ai.x-Aj.x)/Z,y:(Ai.y-Aj.y)/m};Z/=Aa*D;m/=AZ*D;var Ac=AB.max(Z,m);AW=2*AJ.r0_/Ac;AM=2*AJ.r1_/Ac-AW}var AU=AJ.colors_;AU.sort(function(Am,i){return Am.offset-i.offset});var AP=AU.length;var AT=AU[0].color;var AS=AU[AP-1].color;var AY=AU[0].alpha*AQ.globalAlpha;var AX=AU[AP-1].alpha*AQ.globalAlpha;var Ad=[];for(var Ag=0;Ag<AP;Ag++){var AO=AU[Ag];Ad.push(AO.offset*AM+AW+" "+AO.color)}AI.push('<g_vml_:fill type="',AJ.type_,'"',' method="none" focus="100%"',' color="',AT,'"',' color2="',AS,'"',' colors="',Ad.join(","),'"',' opacity="',AX,'"',' g_o_:opacity2="',AY,'"',' angle="',AN,'"',' focusposition="',Ae.x,",",Ae.y,'" />')}else{if(AJ instanceof u){if(Z&&m){var AH=-Aj.x;var Ab=-Aj.y;AI.push("<g_vml_:fill",' position="',AH/Z*Aa*Aa,",",Ab/m*AZ*AZ,'"',' type="tile"',' src="',AJ.src_,'" />')}}else{var Ak=c(AQ.fillStyle);var AV=Ak.color;var Af=Ak.alpha*AQ.globalAlpha;AI.push('<g_vml_:fill color="',AV,'" opacity="',Af,'" />')}}}M.fill=function(){this.stroke(true)};M.closePath=function(){this.currentPath_.push({type:"close"})};function w(i,p,j){var Z=i.m_;return{x:D*(p*Z[0][0]+j*Z[1][0]+Z[2][0])-F,y:D*(p*Z[0][1]+j*Z[1][1]+Z[2][1])-F}}M.save=function(){var Z={};R(this,Z);this.aStack_.push(Z);this.mStack_.push(this.m_);this.m_=g(X(),this.m_)};M.restore=function(){if(this.aStack_.length){R(this.aStack_.pop(),this);this.m_=this.mStack_.pop()}};function H(Z){return isFinite(Z[0][0])&&isFinite(Z[0][1])&&isFinite(Z[1][0])&&isFinite(Z[1][1])&&isFinite(Z[2][0])&&isFinite(Z[2][1])}function AA(i,Z,j){if(!H(Z)){return }i.m_=Z;if(j){var p=Z[0][0]*Z[1][1]-Z[0][1]*Z[1][0];i.lineScale_=n(e(p))}}M.translate=function(j,i){var Z=[[1,0,0],[0,1,0],[j,i,1]];AA(this,g(Z,this.m_),false)};M.rotate=function(i){var m=W(i);var j=J(i);var Z=[[m,j,0],[-j,m,0],[0,0,1]];AA(this,g(Z,this.m_),false)};M.scale=function(j,i){this.arcScaleX_*=j;this.arcScaleY_*=i;var Z=[[j,0,0],[0,i,0],[0,0,1]];AA(this,g(Z,this.m_),true)};M.transform=function(p,m,AH,AG,i,Z){var j=[[p,m,0],[AH,AG,0],[i,Z,1]];AA(this,g(j,this.m_),true)};M.setTransform=function(AG,p,AI,AH,j,i){var Z=[[AG,p,0],[AI,AH,0],[j,i,1]];AA(this,Z,true)};M.drawText_=function(AM,AK,AJ,AP,AI){var AO=this.m_,AS=1000,i=0,AR=AS,AH={x:0,y:0},AG=[];var Z=Q(b(this.font),this.element_);var j=AC(Z);var AT=this.element_.currentStyle;var p=this.textAlign.toLowerCase();switch(p){case"left":case"center":case"right":break;case"end":p=AT.direction=="ltr"?"right":"left";break;case"start":p=AT.direction=="rtl"?"right":"left";break;default:p="left"}switch(this.textBaseline){case"hanging":case"top":AH.y=Z.size/1.75;break;case"middle":break;default:case null:case"alphabetic":case"ideographic":case"bottom":AH.y=-Z.size/2.25;break}switch(p){case"right":i=AS;AR=0.05;break;case"center":i=AR=AS/2;break}var AQ=w(this,AK+AH.x,AJ+AH.y);AG.push('<g_vml_:line from="',-i,' 0" to="',AR,' 0.05" ',' coordsize="100 100" coordorigin="0 0"',' filled="',!AI,'" stroked="',!!AI,'" style="position:absolute;width:1px;height:1px;">');if(AI){S(this,AG)}else{d(this,AG,{x:-i,y:0},{x:AR,y:Z.size})}var AN=AO[0][0].toFixed(3)+","+AO[1][0].toFixed(3)+","+AO[0][1].toFixed(3)+","+AO[1][1].toFixed(3)+",0,0";var AL=K(AQ.x/D)+","+K(AQ.y/D);AG.push('<g_vml_:skew on="t" matrix="',AN,'" ',' offset="',AL,'" origin="',i,' 0" />','<g_vml_:path textpathok="true" />','<g_vml_:textpath on="true" string="',AF(AM),'" style="v-text-align:',p,";font:",AF(j),'" /></g_vml_:line>');this.element_.insertAdjacentHTML("beforeEnd",AG.join(""))};M.fillText=function(j,Z,m,i){this.drawText_(j,Z,m,i,false)};M.strokeText=function(j,Z,m,i){this.drawText_(j,Z,m,i,true)};M.measureText=function(j){if(!this.textMeasureEl_){var Z='<span style="position:absolute;top:-20000px;left:0;padding:0;margin:0;border:none;white-space:pre;"></span>';this.element_.insertAdjacentHTML("beforeEnd",Z);this.textMeasureEl_=this.element_.lastChild}var i=this.element_.ownerDocument;this.textMeasureEl_.innerHTML="";this.textMeasureEl_.style.font=this.font;this.textMeasureEl_.appendChild(i.createTextNode(j));return{width:this.textMeasureEl_.offsetWidth}};M.clip=function(){};M.arcTo=function(){};M.createPattern=function(i,Z){return new u(i,Z)};function v(Z){this.type_=Z;this.x0_=0;this.y0_=0;this.r0_=0;this.x1_=0;this.y1_=0;this.r1_=0;this.colors_=[]}v.prototype.addColorStop=function(i,Z){Z=c(Z);this.colors_.push({offset:i,color:Z.color,alpha:Z.alpha})};function u(i,Z){r(i);switch(Z){case"repeat":case null:case"":this.repetition_="repeat";break;case"repeat-x":case"repeat-y":case"no-repeat":this.repetition_=Z;break;default:o("SYNTAX_ERR")}this.src_=i.src;this.width_=i.width;this.height_=i.height}function o(Z){throw new q(Z)}function r(Z){if(!Z||Z.nodeType!=1||Z.tagName!="IMG"){o("TYPE_MISMATCH_ERR")}if(Z.readyState!="complete"){o("INVALID_STATE_ERR")}}function q(Z){this.code=this[Z];this.message=Z+": DOM Exception "+this.code}var y=q.prototype=new Error;y.INDEX_SIZE_ERR=1;y.DOMSTRING_SIZE_ERR=2;y.HIERARCHY_REQUEST_ERR=3;y.WRONG_DOCUMENT_ERR=4;y.INVALID_CHARACTER_ERR=5;y.NO_DATA_ALLOWED_ERR=6;y.NO_MODIFICATION_ALLOWED_ERR=7;y.NOT_FOUND_ERR=8;y.NOT_SUPPORTED_ERR=9;y.INUSE_ATTRIBUTE_ERR=10;y.INVALID_STATE_ERR=11;y.SYNTAX_ERR=12;y.INVALID_MODIFICATION_ERR=13;y.NAMESPACE_ERR=14;y.INVALID_ACCESS_ERR=15;y.VALIDATION_ERR=16;y.TYPE_MISMATCH_ERR=17;G_vmlCanvasManager=E;CanvasRenderingContext2D=a;CanvasGradient=v;CanvasPattern=u;DOMException=q})()};var Whiteboard=(function(){var wb={};var canvas,context,pencil_btn,rect_btn,width,height,x,y,clickX,clickY,penDown=false;var origcanvas,origcontext,currentTool="pencil";var graphcanvas,graphcontext,topcanvas,topcontext,gr2D,nL,graphMode,gr2D_xp,gr2D_yp,nL_xp,nL_yp;var offX,offY,x0,y0,w0,h0,drawingLayer,drawcolor,rendering;var graphicData,tool_id;var scope=this;var isTouchEnabled=false;function renderText(xt,xp,yp){var txt=xt?xt:$get_Element("#content").value;var str=txt.split("\n");var x0=xp?xp:clickX;var y0=yp?yp:clickY;var ht=15;for(var i=0;i<str.length;i++){context.fillText(str[i],x0,y0);y0+=ht}updateCanvas();if(!xt){updateText(txt);sendData();$get_Element("#content").value="";$get_Element("#inputBox").style.display="none"}}function onkeyupHandler(){}function onkeydownHandler(_event){var event=_event?_event:window.event;if(currentTool=="text"&&event.keyCode==13){if(!event.shiftKey){if(event.preventDefault){event.preventDefault()}else{event.returnValue=false}renderText()}}}function resetButtonHighlite(){$get_Element("#button_text").style.border="1px solid #000000";$get_Element("#button_pencil").style.border="1px solid #000000";$get_Element("#button_line").style.border="1px solid #000000";$get_Element("#button_rect").style.border="1px solid #000000";$get_Element("#button_oval").style.border="1px solid #000000";$get_Element("#button_eraser").style.border="1px solid #000000"}function buttonHighlite(t){resetButtonHighlite();$get_Element("#button_"+t).style.border="2px solid #ff9900"}function viewport(){var e=window,a="inner";if(!("innerWidth" in window)){a="client";e=document.documentElement||document.body}return{width:e[a+"Width"],height:e[a+"Height"]}}function getDocHeight(){var D=document;return Math.max(Math.max(D.body.scrollHeight,D.documentElement.scrollHeight),Math.max(D.body.offsetHeight,D.documentElement.offsetHeight),Math.max(D.body.clientHeight,D.documentElement.clientHeight))}function touchStartFunction(event){event.preventDefault()}var touchMoveFunction=touchStartFunction;var _imageBaseDir="/gwt-resources/images/whiteboard/";var mainDoc;wb.initWhiteboard=function(mainDocIn){mainDoc=mainDocIn;canvas=$get_Element("#canvas");var siz=viewport();var docWidth=siz.width;var docHeight=siz.height;var topOff=$get_Element("#tools").offsetHeight+$get_Element("#tools").offsetTop+15;var leftOff=$get_Element("#tools").offsetLeft+15;origcanvas=$get_Element("#ocanvas");graphcanvas=$get_Element("#gcanvas");topcanvas=$get_Element("#tcanvas");canvas.width=origcanvas.width=graphcanvas.width=topcanvas.width=docWidth-leftOff;canvas.height=origcanvas.height=graphcanvas.height=topcanvas.height=docHeight-topOff;context=canvas.getContext("2d");origcontext=origcanvas.getContext("2d");graphcontext=graphcanvas.getContext("2d");topcontext=topcanvas.getContext("2d");width=canvas.width;height=canvas.height;context.font=origcontext.font=topcontext.font="12px sans-serif";gr2D=new Image();gr2D.src=_imageBaseDir+"gr2D.png";nL=new Image();nL.src=_imageBaseDir+"nL.png";graphMode="";gr2D_xp=nL_xp=(width-300)/2;gr2D_yp=(height-300)/2;nL_yp=(height-100)/2;gr2D_w=300;gr2D_h=300;nL_w=300;nL_h=100;offX=$get_Element("#canvas-container").offsetLeft;offY=$get_Element("#canvas-container").offsetTop;graphicData={};tool_id={};tool_id.eraser=0;tool_id.pencil=1;tool_id.text=2;tool_id.line=3;tool_id.rect=4;tool_id.oval=5;tool_id.gr2D=11;tool_id.nL=12;drawingLayer="1";$get_Element("#button_pencil").style.border="2px solid #ff9900";$get_Element("#button_text").onclick=function(event){currentTool="text";buttonHighlite(currentTool)};$get_Element("#button_pencil").onclick=function(event){currentTool="pencil";buttonHighlite(currentTool)};$get_Element("#button_rect").onclick=function(event){currentTool="rect";buttonHighlite(currentTool)};$get_Element("#button_line").onclick=function(event){currentTool="line";buttonHighlite(currentTool)};$get_Element("#button_oval").onclick=function(event){currentTool="oval";buttonHighlite(currentTool)};$get_Element("#button_gr2D").onclick=function(event){currentTool="gr2D";showHideGraph("gr2D");buttonHighlite("pencil")};$get_Element("#button_nL").onclick=function(event){currentTool="nL";showHideGraph("nL");buttonHighlite("pencil")};$get_Element("#button_clear").onclick=function(event){currentTool="pencil";buttonHighlite(currentTool);resetWhiteBoard(true)};$get_Element("#button_eraser").onclick=function(event){currentTool="eraser";buttonHighlite(currentTool)};$get_Element("#done_btn").onclick=function(event){renderText()};$get_Element("#button_save").onclick=function(event){wb.saveWhiteboard()};var ev_onmousedown=function(_event){isTouchEnabled=_event.type.indexOf("touch")>-1;if(isTouchEnabled){canvas.removeEventListener("mousedown",ev_onmousedown,false);canvas.removeEventListener("mouseup",ev_onmouseup,false);canvas.removeEventListener("mousemove",ev_onmousemove,false)}var event=_event?_event:window.event;event=isTouchEnabled?_event.targetTouches[0]:event;var dx,dy,dist;if(event.layerX||event.pageX){dx=event.layerX?event.layerX:event.pageX-offX;dy=event.layerY?event.layerY:event.pageY-offY}else{dx=event.clientX-offX;dy=event.clientY-offY}context.lineWidth=2;context.strokeStyle="rgb(0, 0, 0)";if(dx>=0&&dx<width){penDown=true;rendering=false;clickX=dx;clickY=dy;x=dx;y=dy;if(!graphicData.dataArr){graphicData.dataArr=[]}graphicData.id=tool_id[currentTool];if(currentTool=="pencil"){context.beginPath();context.moveTo(clickX,clickY)}else{if(currentTool=="eraser"){erase(x,y)}}drawcolor=colorToNumber(context.strokeStyle);if(currentTool=="text"){penDown=false;graphicData.dataArr[0]={x:x,y:y,text:"",color:drawcolor,name:"",layer:drawingLayer};showTextBox()}else{graphicData.dataArr[graphicData.dataArr.length]={x:x,y:y,id:"move",color:drawcolor,name:"",layer:drawingLayer}}}else{penDown=false}if(event.preventDefault){event.preventDefault()}};var ev_onmouseup=function(_event){var event=_event?_event:window.event;event=_event.type.indexOf("touch")>-1?_event.targetTouches[0]:event;if(rendering){penDown=false;if(currentTool=="rect"||currentTool=="oval"){graphicData.dataArr[0].w=w0;graphicData.dataArr[0].h=h0;graphicData.dataArr[0].xs=w0/400;graphicData.dataArr[0].ys=h0/400}else{if(currentTool=="line"||currentTool=="pencil"||currentTool=="eraser"){var xp=x-clickX;var yp=y-clickY;xp=currentTool=="eraser"?x:xp;yp=currentTool=="eraser"?y:yp;graphicData.dataArr[graphicData.dataArr.length]={x:xp,y:yp,id:"line"}}}if(currentTool!="eraser"){updateCanvas();context.beginPath()}sendData();rendering=false}};var ev_onmousemove=function(_event){var event=_event?_event:window.event;event=_event.type.indexOf("touch")>-1?_event.targetTouches[0]:event;if(penDown){rendering=true;if(currentTool!="pencil"&&currentTool!="text"){context.clearRect(0,0,canvas.width,canvas.height)}if(event.layerX||event.pageX){x=event.layerX?event.layerX:event.pageX-offX;y=event.layerY?event.layerY:event.pageY-offY}else{x=event.clientX-offX;y=event.clientY-offY}if(currentTool=="rect"||currentTool=="oval"){x0=clickX;y0=clickY;w0=x-clickX;h0=y-clickY;if(currentTool=="rect"){drawRect(x0,y0,w0,h0)}if(currentTool=="oval"){drawOval(x0,y0,w0,h0)}}else{if(currentTool=="line"){context.beginPath();context.moveTo(clickX,clickY);drawLine()}else{if(currentTool=="eraser"){erase(x,y);graphicData.dataArr[graphicData.dataArr.length]={x:x,y:y,id:"line"}}else{graphicData.dataArr[graphicData.dataArr.length]={x:x-clickX,y:y-clickY,id:"line"};drawLine()}}}}if(event.preventDefault){event.preventDefault()}};if(document.addEventListener){canvas.addEventListener("mousedown",ev_onmousedown,false);canvas.addEventListener("mouseup",ev_onmouseup,false);canvas.addEventListener("mousemove",ev_onmousemove,false);canvas.addEventListener("touchstart",touchStartFunction,false);canvas.addEventListener("touchmove",touchMoveFunction,false);canvas.addEventListener("touchstart",ev_onmousedown,false);canvas.addEventListener("touchmove",ev_onmousemove,false);canvas.addEventListener("touchend",ev_onmouseup,false)}else{canvas.attachEvent("onmousedown",ev_onmousedown);canvas.attachEvent("onmouseup",ev_onmouseup);canvas.attachEvent("onmousemove",ev_onmousemove);canvas.attachEvent("touchstart",touchStartFunction);canvas.attachEvent("touchmove",touchMoveFunction);canvas.attachEvent("touchstart",ev_onmousedown);canvas.attachEvent("touchmove",ev_onmousemove);canvas.attachEvent("touchend",ev_onmouseup)}canvas.focus()};function $get_Element(n){var str=n.indexOf("#")>-1?n.split("#")[1]:n;return mainDoc.getElementById(str)}function updateText(txt){graphicData.dataArr[0].text=txt}function showTextBox(){$get_Element("#inputBox").style.display="block";$get_Element("#inputBox").style.top=clickY+"px";$get_Element("#inputBox").style.left=clickX+"px";$get_Element("#content").focus()}function resetWhiteBoard(boo){penDown=false;graphMode="";origcanvas.width=graphcanvas.width=topcanvas.width=canvas.width=width;origcontext.clearRect(0,0,canvas.width,canvas.height);graphcontext.clearRect(0,0,canvas.width,canvas.height);topcontext.clearRect(0,0,canvas.width,canvas.height);context.clearRect(0,0,canvas.width,canvas.height);if(boo){clear(true)}}function showHideGraph(flag,x,y){graphcanvas.width=graphcanvas.width;graphcanvas.height=graphcanvas.height;graphcontext.clearRect(0,0,canvas.width,canvas.height);graphicData.dataArr=[];graphicData.id=tool_id[currentTool];var addGraph=false;if((graphMode=="gr2D"&&flag=="gr2D")||(graphMode=="nL"&&flag=="nL")){graphMode="";drawingLayer="1";$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000"}else{$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000";var gr,xp,yp,xs,ys;graphMode=flag;if(flag=="gr2D"){gr=gr2D;xp=x?x-(gr2D_w/2):gr2D_xp;yp=y?y-(gr2D_h/2):gr2D_yp;xs=x?x:gr2D_xp+(gr2D_w/2);ys=y?y:gr2D_yp+(gr2D_h/2);$get_Element("#button_gr2D").style.border="2px solid #ff0000"}else{gr=nL;xp=x?x-(nL_w/2):nL_xp;yp=y?y-(nL_h/2):nL_yp;xs=x?x:nL_xp+(nL_w/2);ys=y?y:nL_yp+(nL_h/2);$get_Element("#button_nL").style.border="2px solid #ff0000"}drawingLayer="3";addGraph=true;graphcontext.drawImage(gr,xp,yp)}graphicData.dataArr.push({x:xs,y:ys,name:"graphImage",addImage:addGraph});sendData()}function mouseOverGraph(){var mx=event.layerX?event.layerX:event.pageX-offX;var my=event.layerY?event.layerY:event.pageY-offY;var xp,yp,wi,hi;if(graphMode=="gr2D"){gr=gr2D;xp=gr2D_xp;yp=gr2D_yp;wi=300;hi=300}else{if(graphMode=="nL"){gr=nL;xp=nL_xp;yp=nL_yp;wi=300;hi=100}}if((mx>=xp&&mx<=xp+wi)&&(my>=yp&&my<=yp+hi)){return true}return false}function updateCanvas(){var cntxt=drawingLayer=="1"?origcontext:topcontext;cntxt.drawImage(canvas,0,0);context.clearRect(0,0,canvas.width,canvas.height);context.beginPath()}function erase(x,y){var ew=10;var ep=ew/2;origcontext.clearRect(x-ep,y-ep,ew,ew);topcontext.clearRect(x-ep,y-ep,ew,ew)}function drawLine(){context.lineTo(x,y);context.stroke()}function drawRect(x,y,w,h,color){if(color!=undefined){context.strokeStyle=color}context.strokeRect(x,y,w,h)}function drawOval(x,y,w,h,color){if(color!=undefined){context.strokeStyle=color}var kappa=0.5522848;var ox=(w/2)*kappa;var oy=(h/2)*kappa;var xe=x+w;var ye=y+h;var xm=x+w/2;var ym=y+h/2;context.beginPath();context.moveTo(x,ym);context.bezierCurveTo(x,ym-oy,xm-ox,y,xm,y);context.bezierCurveTo(xm+ox,y,xe,ym-oy,xe,ym);context.bezierCurveTo(xe,ym+oy,xm+ox,ye,xm,ye);context.bezierCurveTo(xm-ox,ye,x,ym+oy,x,ym);context.closePath();context.stroke()}function sendData(){if(graphicData.id||graphicData.id===0){var txtVal=graphicData.dataArr[graphicData.dataArr.length-1].text;if(graphicData.id==2&&(txtVal==""||txtVal==undefined)){resetArrays();textRendering=false;return }if(graphicData.id==1&&graphicData.dataArr.length>500){var jStr=convertObjToString(graphicData);currentObj.tempData=convertStringToObj(jStr);var ptC=graphicData.dataArr.length;var segArr=[];var buf;var header=graphicData.dataArr.shift();var tarr=graphicData.dataArr;var segData;var nxtStart;var nx0;var ny0;var pt={x:header.x,y:header.y};var nname=header.name;var segC=0;var nheader;while(ptC>0){segC++;buf=Math.min(500,ptC);ptC=ptC-buf;segData=tarr.splice(0,buf);var ngdata={};ngdata.lineColor=graphicData.lineColor;ngdata.id=graphicData.id;if(segC>1){var sObj={};sObj.id="move";sObj.x=pt.x;sObj.y=pt.y;segData.unshift(sObj)}nheader=cloneObject(header);nheader.name=nname;segData.unshift(nheader);ngdata.dataArr=segData;segArr.push(ngdata);nxtStart=segData[segData.length-1];pt={x:nxtStart.x,y:nxtStart.y};var n=header.name.split("_");nname=n[0]+"_"+(Number(n[1])+1)}for(var z=0;z<segArr.length;z++){sendDataToSERVER(segArr[z])}render=false;resetArrays();textRendering=false;return }render=false;sendDataToSERVER(graphicData);textRendering=false}resetArrays()}function sendDataToSERVER(jsdata){var jsonStr=convertObjToString(jsdata);wb.whiteboardOut(jsonStr,true)}function cloneObject(obj){var clone={};for(var m in obj){clone[m]=obj[m]}return clone}function resetArrays(){graphicData.dataArr=null;graphicData={}}function getToolFromID(id){for(var m in tool_id){if(id==tool_id[m]){return m}}}function convertObjToString(obj){try{var s=JSON.stringify(obj);return s}catch(ex){console.log(ex.name+":"+ex.message+":"+ex.location+":"+ex.text)}}function convertStringToObj(str){try{var o=eval("("+str+")");return o}catch(ex){console.log(ex.name+":"+ex.message+":"+ex.location+":"+ex.text)}}function renderObj(obj){var graphic_id=obj.id;var graphic_data=obj.dataArr;var line_rgb=obj.lineColor;var dLength=graphic_data.length;var dep,x0,y0,x1,y1;var textF;var idName;drawingLayer=graphic_data[0].layer?graphic_data[0].layer:drawingLayer;context.lineWidth=2;context.strokeStyle="rgb(0, 0, 0)";var deb="";if(graphic_id===0){for(var i=0;i<dLength;i++){x1=graphic_data[i].x;y1=graphic_data[i].y;deb+=x1+":"+y1+"||";erase(x1,y1)}}if(graphic_id===3||graphic_id===1){for(i=0;i<dLength;i++){x1=graphic_data[i].x;y1=graphic_data[i].y;if(graphic_data[i].id=="move"){context.beginPath();context.moveTo(x1,y1);x0=x1;y0=y1}else{context.lineTo(x0+x1,y0+y1)}}context.stroke();updateCanvas()}if(graphic_id===2){for(i=0;i<dLength;i++){if(graphic_data[i].text!=""||graphic_data[i].text!=undefined){x0=graphic_data[i].x;y0=graphic_data[i].y;renderText(xt,x0,y0)}}updateCanvas()}if(graphic_id===4||graphic_id===5){var fName=graphic_id==4?drawRect:drawOval;for(i=0;i<dLength;i++){var xd=graphic_data[i].xs<0?-1:1;var yd=graphic_data[i].ys<0?-1:1;x0=graphic_data[i].x;y0=graphic_data[i].y;w0=graphic_data[i].w*xd;h0=graphic_data[i].h*yd;fName(x0,y0,w0,h0)}updateCanvas()}if(graphic_id===11||graphic_id===12){idName=graphic_id==11?"gr2D":"nL";showHideGraph(idName,graphic_data[0].x,graphic_data[0].y)}}updateWhiteboard=function(cmdArray){var oaL=cmdArray.length;for(var l=0;l<oaL;l++){if(cmdArray[l] instanceof Array){var arg=cmdArray[l][1];arg=arg==undefined?[]:arg;this[cmdArray[l][0]].apply(scope,arg)}else{if(cmdArray[l].indexOf("dataArr")!=-1){draw(cmdArray[l])}else{scope[cmdArray[l]]()}}}};function gwt_updatewhiteboard(cmdArray){var realArray=[];for(var i=0,t=cmdArray.length;i<t;i++){var ele=[];ele[0]=cmdArray[i][0];ele[1]=cmdArray[i][1];realArray[i]=ele}updateWhiteboard(realArray)}wb.updateWhiteboard=function(cmdArray){gwt_updatewhiteboard(cmdArray)};draw=function(json_str){var grobj=convertStringToObj(json_str);renderObj(grobj)};function colorToNumber(c){var n=c.split("#").join("0x");return Number(n)}function clear(boo){if(!boo){resetWhiteBoard(false)}wb.whiteboardOut("clear",false)}wb.saveWhiteboard=function(){alert("default whiteboard save")};wb.whiteboardOut=function(data,boo){alert("default whiteboard out: "+data)};wb.disconnectWhiteboard=function(documentObject){alert("default whiteboard disconnect")};return wb}());// version 1.1
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
                return new HmFlashWidgetImplNumberInteger(jsonObj);
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
             var html = "<img src='/images/tutor5/hint_question-16x16.gif' onmouseover='showMouseAnswer(this)' onmouseout='nd()'/>";
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
var restrictionType_digitsOnlyWithSlash = /[1234567890\/]/g;
var restrictionType_digitsOnlyWithColon = /[1234567890:]/g;
var restrictionType_integerOnly = /[0-9\.]/g;
var restrictionType_alphaOnly = /[A-Z]/g;

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

    return restrictCharacters(ele, event, restrictionType_integerOnly);
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
               "<input id='widget_input_field_2' type='text' style='width: 80px;display: block;'/>" +
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

(function(D,K){var A="width",P="length",d="radius",Y="lines",R="trail",U="color",n="opacity",f="speed",Z="shadow",h="style",C="height",E="left",F="top",G="px",S="childNodes",m="firstChild",H="parentNode",c="position",I="relative",a="absolute",r="animation",V="transform",M="Origin",O="coord",j="#000",W=h+"Sheets",L="webkit0Moz0ms0O".split(0),q={},l;function p(t,v){var s=~~((t[P]-1)/2);for(var u=1;u<=s;u++){v(t[u*2-1],t[u*2])}}function k(s){var t=D.createElement(s||"div");p(arguments,function(v,u){t[v]=u});return t}function b(s,u,t){if(t&&!t[H]){b(s,t)}s.insertBefore(u,t||null);return s}b(D.getElementsByTagName("head")[0],k(h));var N=D[W][D[W][P]-1];function B(x,s){var u=[n,s,~~(x*100)].join("-"),t="{"+n+":"+x+"}",v;if(!q[u]){for(v=0;v<L[P];v++){try{N.insertRule("@"+(L[v]&&"-"+L[v].toLowerCase()+"-"||"")+"keyframes "+u+"{0%{"+n+":1}"+s+"%"+t+"to"+t+"}",N.cssRules[P])}catch(w){}}q[u]=1}return u}function Q(w,x){var v=w[h],t,u;if(v[x]!==K){return x}x=x.charAt(0).toUpperCase()+x.slice(1);for(u=0;u<L[P];u++){t=L[u]+x;if(v[t]!==K){return t}}}function e(s){p(arguments,function(u,t){s[h][Q(s,u)||u]=t});return s}function X(s){p(arguments,function(u,t){if(s[u]===K){s[u]=t}});return s}var T=function T(s){this.el=this[Y](this.opts=X(s||{},Y,12,R,100,P,7,A,5,d,10,U,j,n,1/4,f,1))},J=T.prototype={spin:function(y){var AA=this,t=AA.el;if(y){b(y,e(t,E,~~(y.offsetWidth/2)+G,F,~~(y.offsetHeight/2)+G),y[m])}AA.on=1;if(!l){var s=AA.opts,v=0,w=20/s[f],x=(1-s[n])/(w*s[R]/100),z=w/s[Y];(function u(){v++;for(var AB=s[Y];AB;AB--){var AC=Math.max(1-(v+AB*z)%w*x,s[n]);AA[n](t,s[Y]-AB,AC,s)}if(AA.on){setTimeout(u,50)}})()}return AA},stop:function(){var s=this,t=s.el;s.on=0;if(t[H]){t[H].removeChild(t)}return s}};J[Y]=function(x){var v=e(k(),c,I),u=B(x[n],x[R]),t=0,s;function w(y,z){return e(k(),c,a,A,(x[P]+x[A])+G,C,x[A]+G,"background",y,"boxShadow",z,V+M,E,V,"rotate("+~~(360/x[Y]*t)+"deg) translate("+x[d]+G+",0)","borderRadius","100em")}for(;t<x[Y];t++){s=e(k(),c,a,F,1+~(x[A]/2)+G,V,"translate3d(0,0,0)",r,u+" "+1/x[f]+"s linear infinite "+(1/x[Y]/x[f]*t-1/x[f])+"s");if(x[Z]){b(s,e(w(j,"0 0 4px "+j),F,2+G))}b(v,b(s,w(x[U],"0 0 1px rgba(0,0,0,.1)")))}return v};J[n]=function(t,s,u){t[S][s][h][n]=u};var o="behavior",i="url(#default#VML)",g="group0roundrect0fill0stroke".split(0);(function(){var u=e(k(g[0]),o,i),t;if(!Q(u,V)&&u.adj){for(t=0;t<g[P];t++){N.addRule(g[t],o+":"+i)}J[Y]=function(){var AC=this.opts,AA=AC[P]+AC[A],y=2*AA;function v(){return e(k(g[0],O+"size",y+" "+y,O+M,-AA+" "+-AA),A,y,C,y)}var z=v(),AB=~(AC[P]+AC[d]+AC[A])+G,x;function w(AD,s,AE){b(z,b(e(v(),"rotation",360/AC[Y]*AD+"deg",E,~~s),b(e(k(g[1],"arcsize",1),A,AA,C,AC[A],E,AC[d],F,-AC[A]/2,"filter",AE),k(g[2],U,AC[U],n,AC[n]),k(g[3],n,0))))}if(AC[Z]){for(x=1;x<=AC[Y];x++){w(x,-2,"progid:DXImage"+V+".Microsoft.Blur(pixel"+d+"=2,make"+Z+"=1,"+Z+n+"=.3)")}}for(x=1;x<=AC[Y];x++){w(x)}return b(e(k(),"margin",AB+" 0 0 "+AB,c,I),z)};J[n]=function(v,s,x,w){w=w[Z]&&w[Y]||0;v[m][S][s+w][m][m][n]=x}}else{l=Q(u,r)}})();window.Spinner=T})(document);function initStartCmMobile(){}HmEvents.eventTutorLastStep.subscribe(function(A){gwt_solutionHasBeenViewed()});