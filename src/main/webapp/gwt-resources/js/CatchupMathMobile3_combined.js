window.showCorrectAnswers=function(B){var D=document.getElementById("testset_div");if(D){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}}};function setQuizQuestionActive(A){}function findQuestionGuid(B){while(B){var A=B.getAttribute("guid");if(A){return A}B=B.parentNode}return null}function findQuestionByPid(B){var D=document.getElementById("testset_div").getElementsByTagName("div");try{for(var C=0;C<D.length;C++){var E=D[C].getAttribute("guid");if(E==B){return D[C]}}}catch(A){alert("Error while setting selected question response: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function findQuestionNumberByPid(B){var E=document.getElementById("testset_div").getElementsByTagName("div");var C=0;try{for(var D=0;D<E.length;D++){var F=E[D].getAttribute("guid");if(F){if(F==B){return C}else{C++}}}}catch(A){alert("Error while question index: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function questionGuessChanged(H){try{var B=findQuestionGuid(H);var G=-1;if(H.id=="optionSkipped"){G="-2"}else{var D=H.parentNode.parentNode.parentNode.parentNode;var A=D.getElementsByTagName("input");for(var C=0;C<A.length;C++){if(A.item(C)==H){G=C;break}}}var E=findQuestionNumberByPid(B);questionGuessChanged_Gwt(""+E,""+G,B)}catch(F){alert("Error answering question in external JS: "+F)}}function setSolutionQuestionAnswerIndexByNumber(B,C){var A=0;showCorrectAnswers(function(H){var G=H.getElementsByTagName("input");if(A==B){for(var F=0,E=G.length;F<E;F++){if(F==C){G[F].checked=true;var D=G[F];questionGuessChanged(D)}else{G[F].checked=false}}}A++})}window.setSolutionQuestionAnswerIndex=function(B,H,E){var G=findQuestionByPid(B);if(G){var F=G.getElementsByTagName("input");for(var D=0,C=F.length;D<C;D++){var A=F.item(D);A.disabled=E?true:false;if(D==H){A.checked=true}}}};function doLoadResource(B,A){doLoadResource_Gwt(B,A);return false}window.markAllCorrectAnswers=function(){showCorrectAnswers(markCorrectResponse)};window.getQuizResultsCorrect=function(){var A=0;showCorrectAnswers(function(E){var D=E.getElementsByTagName("input");for(var C=0,B=D.length;C<B;C++){var F=D[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){if(D[C].checked){A++}}}});return A};window.getQuizQuestionCount=function(){var A=0;showCorrectAnswers(function(B){A++});return A};window.showCorrectAnswers=function(B){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}};window.markCorrectResponse=function(D){var E=D.getElementsByTagName("input");for(var C=0,B=E.length;C<B;C++){var F=E[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){E[C].checked=true;var A=E[C];questionGuessChanged(A);break}}};function checkQuiz_Gwt(){alert("Checking quiz ...")}window.setQuizQuestionResult=function(C,A){var E=findQuestionByPid(C);var D=getQuestionMarkImage(C);var B=getQuestionMarkText(C);if(A=="Correct"){D.src="/gwt-resources/images/check_correct.png";B.innerHTML="Correct"}else{if(A=="Incorrect"){D.src="/gwt-resources/images/check_incorrect.png";B.innerHTML="Incorrect"}else{D.src="/gwt-resources/images/check_notanswered.png";B.innerHTML="Not answered"}}D.parentNode.style.display="block"};function getQuestionMarkImage(A){return document.getElementById("response_image_"+A)}function getQuestionMarkText(A){return document.getElementById("response_text_"+A)}function log(){}InmhButtons={};if(typeof deconcept=="undefined"){var deconcept=new Object()}if(typeof deconcept.util=="undefined"){deconcept.util=new Object()}if(typeof deconcept.SWFObjectUtil=="undefined"){deconcept.SWFObjectUtil=new Object()}deconcept.SWFObject=function(K,B,L,D,H,I,F,E,C,J){if(!document.getElementById){return }this.DETECT_KEY=J?J:"detectflash";this.skipDetect=deconcept.util.getRequestParameter(this.DETECT_KEY);this.params=new Object();this.variables=new Object();this.attributes=new Array();if(K){this.setAttribute("swf",K)}if(B){this.setAttribute("id",B)}if(L){this.setAttribute("width",L)}if(D){this.setAttribute("height",D)}if(H){this.setAttribute("version",new deconcept.PlayerVersion(H.toString().split(".")))}this.installedVer=deconcept.SWFObjectUtil.getPlayerVersion();if(!window.opera&&document.all&&this.installedVer.major>7){deconcept.SWFObject.doPrepUnload=true}if(I){this.addParam("bgcolor",I)}var A=F?F:"high";this.addParam("quality",A);this.setAttribute("useExpressInstall",false);this.setAttribute("doExpressInstall",false);var G=(E)?E:window.location;this.setAttribute("xiRedirectUrl",G);this.setAttribute("redirectUrl","");if(C){this.setAttribute("redirectUrl",C)}};deconcept.SWFObject.prototype={useExpressInstall:function(A){this.xiSWFPath=!A?"expressinstall.swf":A;this.setAttribute("useExpressInstall",true)},setAttribute:function(A,B){this.attributes[A]=B},getAttribute:function(A){return this.attributes[A]},addParam:function(B,A){this.params[B]=A},getParams:function(){return this.params},addVariable:function(B,A){this.variables[B]=A},getVariable:function(A){return this.variables[A]},getVariables:function(){return this.variables},getVariablePairs:function(){var C=new Array();var B;var A=this.getVariables();for(B in A){C[C.length]=B+"="+A[B]}return C},getSWFHTML:function(){var B="";if(navigator.plugins&&navigator.mimeTypes&&navigator.mimeTypes.length){if(this.getAttribute("doExpressInstall")){this.addVariable("MMplayerType","PlugIn");this.setAttribute("swf",this.xiSWFPath)}B='<embed type="application/x-shockwave-flash" src="'+this.getAttribute("swf")+'" width="'+this.getAttribute("width")+'" height="'+this.getAttribute("height")+'" style="'+this.getAttribute("style")+'"';B+=' id="'+this.getAttribute("id")+'" name="'+this.getAttribute("id")+'" ';var F=this.getParams();for(var E in F){B+=[E]+'="'+F[E]+'" '}var D=this.getVariablePairs().join("&");if(D.length>0){B+='flashvars="'+D+'"'}B+="/>"}else{if(this.getAttribute("doExpressInstall")){this.addVariable("MMplayerType","ActiveX");this.setAttribute("swf",this.xiSWFPath)}B='<object id="'+this.getAttribute("id")+'" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="'+this.getAttribute("width")+'" height="'+this.getAttribute("height")+'" style="'+this.getAttribute("style")+'">';B+='<param name="movie" value="'+this.getAttribute("swf")+'" />';var C=this.getParams();for(var E in C){B+='<param name="'+E+'" value="'+C[E]+'" />'}var A=this.getVariablePairs().join("&");if(A.length>0){B+='<param name="flashvars" value="'+A+'" />'}B+="</object>"}return B},write:function(B){if(this.getAttribute("useExpressInstall")){var A=new deconcept.PlayerVersion([6,0,65]);if(this.installedVer.versionIsValid(A)&&!this.installedVer.versionIsValid(this.getAttribute("version"))){this.setAttribute("doExpressInstall",true);this.addVariable("MMredirectURL",escape(this.getAttribute("xiRedirectUrl")));document.title=document.title.slice(0,47)+" - Flash Player Installation";this.addVariable("MMdoctitle",document.title)}}if(this.skipDetect||this.getAttribute("doExpressInstall")||this.installedVer.versionIsValid(this.getAttribute("version"))){var C=(typeof B=="string")?document.getElementById(B):B;C.innerHTML=this.getSWFHTML();return true}else{if(this.getAttribute("redirectUrl")!=""){document.location.replace(this.getAttribute("redirectUrl"))}}return false}};deconcept.SWFObjectUtil.getPlayerVersion=function(){var E=new deconcept.PlayerVersion([0,0,0]);if(navigator.plugins&&navigator.mimeTypes.length){var A=navigator.plugins["Shockwave Flash"];if(A&&A.description){E=new deconcept.PlayerVersion(A.description.replace(/([a-zA-Z]|\s)+/,"").replace(/(\s+r|\s+b[0-9]+)/,".").split("."))}}else{if(navigator.userAgent&&navigator.userAgent.indexOf("Windows CE")>=0){var B=1;var C=3;while(B){try{C++;B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash."+C);E=new deconcept.PlayerVersion([C,0,0])}catch(D){B=null}}}else{try{var B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7")}catch(D){try{var B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");E=new deconcept.PlayerVersion([6,0,21]);B.AllowScriptAccess="always"}catch(D){if(E.major==6){return E}}try{B=new ActiveXObject("ShockwaveFlash.ShockwaveFlash")}catch(D){}}if(B!=null){E=new deconcept.PlayerVersion(B.GetVariable("$version").split(" ")[1].split(","))}}}return E};deconcept.PlayerVersion=function(A){this.major=A[0]!=null?parseInt(A[0]):0;this.minor=A[1]!=null?parseInt(A[1]):0;this.rev=A[2]!=null?parseInt(A[2]):0};deconcept.PlayerVersion.prototype.versionIsValid=function(A){if(this.major<A.major){return false}if(this.major>A.major){return true}if(this.minor<A.minor){return false}if(this.minor>A.minor){return true}if(this.rev<A.rev){return false}return true};deconcept.util={getRequestParameter:function(C){var D=document.location.search||document.location.hash;if(C==null){return D}if(D){var B=D.substring(1).split("&");for(var A=0;A<B.length;A++){if(B[A].substring(0,B[A].indexOf("="))==C){return B[A].substring((B[A].indexOf("=")+1))}}}return""}};deconcept.SWFObjectUtil.cleanupSWFs=function(){var B=document.getElementsByTagName("OBJECT");for(var C=B.length-1;C>=0;C--){B[C].style.display="none";for(var A in B[C]){if(typeof B[C][A]=="function"){B[C][A]=function(){}}}}};if(deconcept.SWFObject.doPrepUnload){if(!deconcept.unloadSet){deconcept.SWFObjectUtil.prepUnload=function(){__flash_unloadHandler=function(){};__flash_savedUnloadHandler=function(){};window.attachEvent("onunload",deconcept.SWFObjectUtil.cleanupSWFs)};window.attachEvent("onbeforeunload",deconcept.SWFObjectUtil.prepUnload);deconcept.unloadSet=true}}if(!document.getElementById&&document.all){document.getElementById=function(A){return document.all[A]}}var getQueryParamValue=deconcept.util.getRequestParameter;var FlashObject=deconcept.SWFObject;var SWFObject=deconcept.SWFObject;function f_clientWidth(){return f_filterResults(window.innerWidth?window.innerWidth:0,document.documentElement?document.documentElement.clientWidth:0,document.body?document.body.clientWidth:0)}function f_clientHeight(){return f_filterResults(window.innerHeight?window.innerHeight:0,document.documentElement?document.documentElement.clientHeight:0,document.body?document.body.clientHeight:0)}function f_scrollLeft(){return f_filterResults(window.pageXOffset?window.pageXOffset:0,document.documentElement?document.documentElement.scrollLeft:0,document.body?document.body.scrollLeft:0)}function f_scrollTop(){return f_filterResults(window.pageYOffset?window.pageYOffset:0,document.documentElement?document.documentElement.scrollTop:0,document.body?document.body.scrollTop:0)}function f_filterResults(D,B,A){var C=D?D:0;if(B&&(!C||(C>B))){C=B}return A&&(!C||(C>A))?A:C};var _productionMode=false;var HmEvents={eventTutorInitialized:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorInitialized.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorInitialized.listeners;for(var A=0;A<B.length;A++){B[A]()}}},eventTutorLastStep:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorLastStep.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorLastStep.listeners;for(var A=0;A<B.length;A++){B[A]()}}},eventTutorWidgetComplete:{listeners:[],subscribe:function(B){var A=HmEvents.eventTutorWidgetComplete.listeners;A[A.length]=B},fire:function(){var B=HmEvents.eventTutorWidgetComplete.listeners;for(var A=0;A<B.length;A++){B[A]()}}}};function $get(A){return document.getElementById(A)}HmEvents.eventTutorInitialized.subscribe(function(){try{MathJax.Hub.Queue(["Typeset",MathJax.Hub])}catch(A){alert("MathJAX processing failed: "+A)}});function setStepsInfoHelp(){}function resetStepsInfo(){}function getNextMoveTo(){}var TutorManager={currentRealStep:-1,currentStepUnit:-1,stepUnitsMo:[],stepUnits:[],steps:[],pid:"",stepUnit:null,tutorData:null,initializeTutor:function(A,D,C,B){TutorManager.pid=A;TutorManager.currentRealStep=-1;TutorManager.currentStepUnit=-1;TutorManager.loadTutorData(D);TutorManager.analyzeLoadedData();enabledNext(true);HmEvents.eventTutorInitialized.fire()},showMessage:function(B){var A=$get("tutor_message");A.innerHTML=B;setTimeout(function(){A.innerHTML="&nbsp;"},2000)},showNextStep:function(){if(TutorManager.currentStepUnit+1<TutorManager.stepUnits.length){TutorManager.currentStepUnit++;showStepUnit(TutorManager.currentStepUnit)}else{TutorManager.showMessage("no more steps")}},showPreviousStep:function(){if(TutorManager.currentStepUnit<0){TutorManager.showMessage("No previous step");return }else{while(TutorManager.currentStepUnit>-1){var A=TutorManager.stepUnits[TutorManager.currentStepUnit].ele;if(TutorManager.stepUnits[TutorManager.currentStepUnit].realNum!=TutorManager.currentRealStep){TutorManager.currentRealStep=TutorManager.stepUnits[TutorManager.currentStepUnit].realNum;break}A.style.display="none";TutorManager.currentStepUnit--}if(TutorManager.currentStepUnit==0){TutorManager.currentStepUnit=-1;window.scrollTo(0,0)}if(TutorManager.currentStepUnit>-1){setAsCurrent(TutorManager.stepUnits[TutorManager.currentStepUnit].ele)}setButtonState();scrollToStep(TutorManager.currentStepUnit);return false}},loadTutorData:function(solutionData){try{TutorManager.tutorData=eval("("+solutionData+")")}catch(e){alert(e)}},analyzeLoadedData:function(){TutorManager.stepUnits=[];TutorManager.steps=[];var I=100;for(var J=0;J<I;J++){var E=_getStepUnit(J);if(E==null){break}var B=E.getAttribute("id");var A=TutorManager.stepUnits.length;var C=E.getAttribute("steprole");var F=E.getAttribute("steptype");var G=parseInt(E.getAttribute("realstep"));var H=new StepUnit(B,A,F,C,G,E);TutorManager.stepUnits[TutorManager.stepUnits.length]=H;var D=TutorManager.steps[G];if(D==null){D=new Step(G);TutorManager.steps[G]=D}D.stepUnits[D.stepUnits.length]=H}return TutorManager.stepUnits.length},backToLesson:function(){gwt_backToLesson()},newProblem:function(){gwt_tutorNewProblem()}};function setButtonState(){setState("step",TutorManager.currentStepUnit<(TutorManager.stepUnits.length-1));setState("back",TutorManager.currentStepUnit>-1)}function enabledPrevious(A){enabledButton("steps_prev",A)}function enabledNext(A){enabledButton("steps_next",A)}function enabledButton(B,C){var A="sexybutton ";if(!C){A+=" disabled"}$get(B).className=A}function StepUnit(F,E,B,A,D,C){this.id=F;this.stepUnitNum=E;this.type=B;this.roleType=A;this.realNum=D;this.ele=C}function Step(A){this.realNum=A;this.stepUnits=new Array()}function _getStepUnit(A){return _getElement("stepunit",A)}function _getHintUnit(A){return _getElement("hintunit",A)}function _getFigureUnit(A){return _getElement("figure",A)}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setAsNotCurrent(A){A.style.backgroundColor="#E2E2E2"}function _getElement(A,B){var C=A+"-"+B;return document.getElementById(C)}function showStepUnit(A){if(A<0){return }try{var B=TutorManager.stepUnits[A].ele;if(B==null){return false}B.style.display="block";if(B.getAttribute("steprole")=="step"){setAsCurrent(B)}setStepTitle(A,B);var D=_getFigureUnit(A);if(D!=null){if(A==0){D.style.display="block"}else{var E=findPreviousFigureUnit(A);if(E!=null&&E.src==D.src){D.style.display="none"}else{D.style.display="block"}}}for(i=A-1;i>-1;i--){if(TutorManager.stepUnits[i].roleType=="hint"){TutorManager.stepUnits[i].ele.style.display="none"}else{setAsNotCurrent(TutorManager.stepUnits[i].ele)}}TutorManager.currentStepUnit=A;TutorManager.currentRealStep=TutorManager.stepUnits[A].realNum;setButtonState();scrollToStep(A)}catch(C){alert("Error showing step: "+C)}return true}function setAsCurrent(A){A.style.backgroundColor="#F1F1F1"}function setStepTitle(A,C){stepTitle=document.getElementById("step_title-"+A);if(stepTitle){var B=C.getAttribute("steprole");if(B&&B=="step"){stepTitle.innerHTML="Step "+(parseInt(C.getAttribute("realstep"))+1);stepTitle.className="step_title_step"}else{stepTitle.innerHTML="Hint";stepTitle.className="step_title_hint"}}}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setState(B,A){if(B=="step"){enabledNext(A);if(!A){HmEvents.eventTutorLastStep.fire()}}else{if(B=="back"){enabledPrevious(A)}}}function scrollToStep(C){var B=document.getElementById("scrollTo-button");if(B){var G=DL_GetElementTop(B);var E=getViewableSize();var A=getScrollXY();var F=A[1];var D=E[1];var H=D+F;if(true||G<F||G>H){gwt_scrollToBottomOfScrollPanel(G-D)}}}function hideAllSteps(){for(var A=0;A<TutorManager.stepUnits.length;A++){var B=TutorManager.stepUnits[A].ele;if(B==null){return }if(B.style.display!="none"){B.style.display="none"}}window.scrollTo(0,0)}function initializeExternalJs(){var A="control-floater";new FloatLayer(A,150,15,10);detach(A);alignControlFloater()}function alignControlFloater(){alignFloatLayers();setTimeout(alignControlFloater,2000)}function doQuestionResponseEnd(){}var _activeQuestion;function doQuestionResponse(A,D){var C=TutorManager.tutorData._strings_moArray[A];if(_activeQuestion){var B=document.createElement("div");B.className="questionResponseAnswer";B.innerHTML=C;_activeQuestion.parentNode.appendChild(B)}else{gwt_showMessage(C)}}HmEvents.eventTutorInitialized.subscribe(function(){var H=document.getElementById("tutor_raw_steps_wrapper");if(H==null){return }var B=H.getElementsByTagName("div");var A=B.length;for(var E=0;E<A;E++){var G=B.item(E);if(G.className=="question_guess"){var F=G.getElementsByTagName("img");var D=F.item(0);var C=D.onmouseout=null;D.onmouseoverDeferred=D.onmouseover;D.onmouseover=null;D.onclick=function(I){var J=(I)?I:window.event;var K=J.srcElement?J.srcElement:J.target;_activeQuestion=K;if(!K.onmouseoverDeferred){alert("error: no deferred move event");return }K.onclick=null;K.onmouseoverDeferred()}}}});function DL_GetElementLeft(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetLeft;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientLeft}}A+=D.offsetLeft;D=D.offsetParent}return A}function DL_GetElementTop(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetTop;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientTop}}A+=D.offsetTop;D=D.offsetParent}return A}function getViewableSize(){var B=0,A=0;if(typeof (window.innerWidth)=="number"){B=window.innerWidth;A=window.innerHeight}else{if(document.documentElement&&(document.documentElement.clientWidth||document.documentElement.clientHeight)){B=document.documentElement.clientWidth;A=document.documentElement.clientHeight}else{if(document.body&&(document.body.clientWidth||document.body.clientHeight)){B=document.body.clientWidth;A=document.body.clientHeight}}}a=[B,A];return a}function getScrollXY(){var B=0,A=0;if(typeof (window.pageYOffset)=="number"){A=window.pageYOffset;B=window.pageXOffset}else{if(document.body&&(document.body.scrollLeft||document.body.scrollTop)){A=document.body.scrollTop;B=document.body.scrollLeft}else{if(document.documentElement&&(document.documentElement.scrollLeft||document.documentElement.scrollTop)){A=document.documentElement.scrollTop;B=document.documentElement.scrollLeft}}}return[B,A]}function _addEvent(E,D,B,A){if(E.addEventListener){E.addEventListener(D,B,A);return true}else{if(E.attachEvent){var C=E.attachEvent("on"+D,B);return C}else{alert("Handler could not be attached")}}}function _removeEvent(E,D,B,A){if(E.removeEventListener){E.removeEventListener(D,B,A);return true}else{if(E.detachEvent){var C=E.detachEvent("on"+D,B);return C}else{alert("Handler could not be removed")}}}function hideDivOnMouseOut(A){var C,B;if(window.event){C=this;B=window.event.toElement}else{C=A.currentTarget;B=A.relatedTarget}if(C!=B){if(!contains(C,B)){C.style.display="none"}}}function contains(B,A){while(A.parentNode){A=A.parentNode;if(A==B){return true}}return false}function grabComputedStyle(B,A){if(document.defaultView&&document.defaultView.getComputedStyle){return document.defaultView.getComputedStyle(B,null).getPropertyValue(A)}else{if(B.currentStyle){return B.currentStyle[A]}else{return null}}}function grabComputedHeight(B){var A=grabComputedStyle(B,"height");if(A!=null){if(A=="auto"){if(B.offsetHeight){A=B.offsetHeight}}A=parseInt(A)}return A}function grabComputedWidth(B){var A=grabComputedStyle(B,"width");if(A!=null){if(A.indexOf("px")!=-1){A=A.substring(0,A.indexOf("px"))}if(A=="auto"){if(B.offsetWidth){A=B.offsetWidth}}}return A};if(!document.createElement("canvas").getContext){(function(){var AB=Math;var K=AB.round;var J=AB.sin;var W=AB.cos;var e=AB.abs;var n=AB.sqrt;var D=10;var F=D/2;var V=+navigator.userAgent.match(/MSIE ([\d.]+)?/)[1];function U(){return this.context_||(this.context_=new a(this))}var P=Array.prototype.slice;function G(i,j,m){var Z=P.call(arguments,2);return function(){return i.apply(j,Z.concat(P.call(arguments)))}}function AF(Z){return String(Z).replace(/&/g,"&amp;").replace(/"/g,"&quot;")}function z(j,i,Z){if(!j.namespaces[i]){j.namespaces.add(i,Z,"#default#VML")}}function s(i){z(i,"g_vml_","urn:schemas-microsoft-com:vml");z(i,"g_o_","urn:schemas-microsoft-com:office:office");if(!i.styleSheets.ex_canvas_){var Z=i.createStyleSheet();Z.owningElement.id="ex_canvas_";Z.cssText="canvas{display:inline-block;overflow:hidden;text-align:left;width:300px;height:150px}"}}s(document);var E={init:function(Z){var i=Z||document;i.createElement("canvas");i.attachEvent("onreadystatechange",G(this.init_,this,i))},init_:function(m){var j=m.getElementsByTagName("canvas");for(var Z=0;Z<j.length;Z++){this.initElement(j[Z])}},initElement:function(i){if(!i.getContext){i.getContext=U;s(i.ownerDocument);i.innerHTML="";i.attachEvent("onpropertychange",T);i.attachEvent("onresize",x);var Z=i.attributes;if(Z.width&&Z.width.specified){i.style.width=Z.width.nodeValue+"px"}else{i.width=i.clientWidth}if(Z.height&&Z.height.specified){i.style.height=Z.height.nodeValue+"px"}else{i.height=i.clientHeight}}return i}};function T(i){var Z=i.srcElement;switch(i.propertyName){case"width":Z.getContext().clearRect();Z.style.width=Z.attributes.width.nodeValue+"px";Z.firstChild.style.width=Z.clientWidth+"px";break;case"height":Z.getContext().clearRect();Z.style.height=Z.attributes.height.nodeValue+"px";Z.firstChild.style.height=Z.clientHeight+"px";break}}function x(i){var Z=i.srcElement;if(Z.firstChild){Z.firstChild.style.width=Z.clientWidth+"px";Z.firstChild.style.height=Z.clientHeight+"px"}}E.init();var I=[];for(var AE=0;AE<16;AE++){for(var AD=0;AD<16;AD++){I[AE*16+AD]=AE.toString(16)+AD.toString(16)}}function X(){return[[1,0,0],[0,1,0],[0,0,1]]}function g(m,j){var i=X();for(var Z=0;Z<3;Z++){for(var AH=0;AH<3;AH++){var p=0;for(var AG=0;AG<3;AG++){p+=m[Z][AG]*j[AG][AH]}i[Z][AH]=p}}return i}function R(i,Z){Z.fillStyle=i.fillStyle;Z.lineCap=i.lineCap;Z.lineJoin=i.lineJoin;Z.lineWidth=i.lineWidth;Z.miterLimit=i.miterLimit;Z.shadowBlur=i.shadowBlur;Z.shadowColor=i.shadowColor;Z.shadowOffsetX=i.shadowOffsetX;Z.shadowOffsetY=i.shadowOffsetY;Z.strokeStyle=i.strokeStyle;Z.globalAlpha=i.globalAlpha;Z.font=i.font;Z.textAlign=i.textAlign;Z.textBaseline=i.textBaseline;Z.arcScaleX_=i.arcScaleX_;Z.arcScaleY_=i.arcScaleY_;Z.lineScale_=i.lineScale_}var B={aliceblue:"#F0F8FF",antiquewhite:"#FAEBD7",aquamarine:"#7FFFD4",azure:"#F0FFFF",beige:"#F5F5DC",bisque:"#FFE4C4",black:"#000000",blanchedalmond:"#FFEBCD",blueviolet:"#8A2BE2",brown:"#A52A2A",burlywood:"#DEB887",cadetblue:"#5F9EA0",chartreuse:"#7FFF00",chocolate:"#D2691E",coral:"#FF7F50",cornflowerblue:"#6495ED",cornsilk:"#FFF8DC",crimson:"#DC143C",cyan:"#00FFFF",darkblue:"#00008B",darkcyan:"#008B8B",darkgoldenrod:"#B8860B",darkgray:"#A9A9A9",darkgreen:"#006400",darkgrey:"#A9A9A9",darkkhaki:"#BDB76B",darkmagenta:"#8B008B",darkolivegreen:"#556B2F",darkorange:"#FF8C00",darkorchid:"#9932CC",darkred:"#8B0000",darksalmon:"#E9967A",darkseagreen:"#8FBC8F",darkslateblue:"#483D8B",darkslategray:"#2F4F4F",darkslategrey:"#2F4F4F",darkturquoise:"#00CED1",darkviolet:"#9400D3",deeppink:"#FF1493",deepskyblue:"#00BFFF",dimgray:"#696969",dimgrey:"#696969",dodgerblue:"#1E90FF",firebrick:"#B22222",floralwhite:"#FFFAF0",forestgreen:"#228B22",gainsboro:"#DCDCDC",ghostwhite:"#F8F8FF",gold:"#FFD700",goldenrod:"#DAA520",grey:"#808080",greenyellow:"#ADFF2F",honeydew:"#F0FFF0",hotpink:"#FF69B4",indianred:"#CD5C5C",indigo:"#4B0082",ivory:"#FFFFF0",khaki:"#F0E68C",lavender:"#E6E6FA",lavenderblush:"#FFF0F5",lawngreen:"#7CFC00",lemonchiffon:"#FFFACD",lightblue:"#ADD8E6",lightcoral:"#F08080",lightcyan:"#E0FFFF",lightgoldenrodyellow:"#FAFAD2",lightgreen:"#90EE90",lightgrey:"#D3D3D3",lightpink:"#FFB6C1",lightsalmon:"#FFA07A",lightseagreen:"#20B2AA",lightskyblue:"#87CEFA",lightslategray:"#778899",lightslategrey:"#778899",lightsteelblue:"#B0C4DE",lightyellow:"#FFFFE0",limegreen:"#32CD32",linen:"#FAF0E6",magenta:"#FF00FF",mediumaquamarine:"#66CDAA",mediumblue:"#0000CD",mediumorchid:"#BA55D3",mediumpurple:"#9370DB",mediumseagreen:"#3CB371",mediumslateblue:"#7B68EE",mediumspringgreen:"#00FA9A",mediumturquoise:"#48D1CC",mediumvioletred:"#C71585",midnightblue:"#191970",mintcream:"#F5FFFA",mistyrose:"#FFE4E1",moccasin:"#FFE4B5",navajowhite:"#FFDEAD",oldlace:"#FDF5E6",olivedrab:"#6B8E23",orange:"#FFA500",orangered:"#FF4500",orchid:"#DA70D6",palegoldenrod:"#EEE8AA",palegreen:"#98FB98",paleturquoise:"#AFEEEE",palevioletred:"#DB7093",papayawhip:"#FFEFD5",peachpuff:"#FFDAB9",peru:"#CD853F",pink:"#FFC0CB",plum:"#DDA0DD",powderblue:"#B0E0E6",rosybrown:"#BC8F8F",royalblue:"#4169E1",saddlebrown:"#8B4513",salmon:"#FA8072",sandybrown:"#F4A460",seagreen:"#2E8B57",seashell:"#FFF5EE",sienna:"#A0522D",skyblue:"#87CEEB",slateblue:"#6A5ACD",slategray:"#708090",slategrey:"#708090",snow:"#FFFAFA",springgreen:"#00FF7F",steelblue:"#4682B4",tan:"#D2B48C",thistle:"#D8BFD8",tomato:"#FF6347",turquoise:"#40E0D0",violet:"#EE82EE",wheat:"#F5DEB3",whitesmoke:"#F5F5F5",yellowgreen:"#9ACD32"};function l(i){var m=i.indexOf("(",3);var Z=i.indexOf(")",m+1);var j=i.substring(m+1,Z).split(",");if(j.length!=4||i.charAt(3)!="a"){j[3]=1}return j}function C(Z){return parseFloat(Z)/100}function N(i,j,Z){return Math.min(Z,Math.max(j,i))}function f(AG){var Z,AI,AJ,AH,AK,m;AH=parseFloat(AG[0])/360%360;if(AH<0){AH++}AK=N(C(AG[1]),0,1);m=N(C(AG[2]),0,1);if(AK==0){Z=AI=AJ=m}else{var i=m<0.5?m*(1+AK):m+AK-m*AK;var j=2*m-i;Z=A(j,i,AH+1/3);AI=A(j,i,AH);AJ=A(j,i,AH-1/3)}return"#"+I[Math.floor(Z*255)]+I[Math.floor(AI*255)]+I[Math.floor(AJ*255)]}function A(i,Z,j){if(j<0){j++}if(j>1){j--}if(6*j<1){return i+(Z-i)*6*j}else{if(2*j<1){return Z}else{if(3*j<2){return i+(Z-i)*(2/3-j)*6}else{return i}}}}var Y={};function c(Z){if(Z in Y){return Y[Z]}var AG,p=1;Z=String(Z);if(Z.charAt(0)=="#"){AG=Z}else{if(/^rgb/.test(Z)){var m=l(Z);var AG="#",AH;for(var j=0;j<3;j++){if(m[j].indexOf("%")!=-1){AH=Math.floor(C(m[j])*255)}else{AH=+m[j]}AG+=I[N(AH,0,255)]}p=+m[3]}else{if(/^hsl/.test(Z)){var m=l(Z);AG=f(m);p=m[3]}else{AG=B[Z]||Z}}}return Y[Z]={color:AG,alpha:p}}var L={style:"normal",variant:"normal",weight:"normal",size:10,family:"sans-serif"};var k={};function b(Z){if(k[Z]){return k[Z]}var m=document.createElement("div");var j=m.style;try{j.font=Z}catch(i){}return k[Z]={style:j.fontStyle||L.style,variant:j.fontVariant||L.variant,weight:j.fontWeight||L.weight,size:j.fontSize||L.size,family:j.fontFamily||L.family}}function Q(j,i){var Z={};for(var AH in j){Z[AH]=j[AH]}var AG=parseFloat(i.currentStyle.fontSize),m=parseFloat(j.size);if(typeof j.size=="number"){Z.size=j.size}else{if(j.size.indexOf("px")!=-1){Z.size=m}else{if(j.size.indexOf("em")!=-1){Z.size=AG*m}else{if(j.size.indexOf("%")!=-1){Z.size=(AG/100)*m}else{if(j.size.indexOf("pt")!=-1){Z.size=m/0.75}else{Z.size=AG}}}}}Z.size*=0.981;return Z}function AC(Z){return Z.style+" "+Z.variant+" "+Z.weight+" "+Z.size+"px "+Z.family}var O={butt:"flat",round:"round"};function t(Z){return O[Z]||"square"}function a(Z){this.m_=X();this.mStack_=[];this.aStack_=[];this.currentPath_=[];this.strokeStyle="#000";this.fillStyle="#000";this.lineWidth=1;this.lineJoin="miter";this.lineCap="butt";this.miterLimit=D*1;this.globalAlpha=1;this.font="10px sans-serif";this.textAlign="left";this.textBaseline="alphabetic";this.canvas=Z;var j="width:"+Z.clientWidth+"px;height:"+Z.clientHeight+"px;overflow:hidden;position:absolute";var i=Z.ownerDocument.createElement("div");i.style.cssText=j;Z.appendChild(i);var m=i.cloneNode(false);m.style.backgroundColor="red";m.style.filter="alpha(opacity=0)";Z.appendChild(m);this.element_=i;this.arcScaleX_=1;this.arcScaleY_=1;this.lineScale_=1}var M=a.prototype;M.clearRect=function(){if(this.textMeasureEl_){this.textMeasureEl_.removeNode(true);this.textMeasureEl_=null}this.element_.innerHTML=""};M.beginPath=function(){this.currentPath_=[]};M.moveTo=function(i,Z){var j=w(this,i,Z);this.currentPath_.push({type:"moveTo",x:j.x,y:j.y});this.currentX_=j.x;this.currentY_=j.y};M.lineTo=function(i,Z){var j=w(this,i,Z);this.currentPath_.push({type:"lineTo",x:j.x,y:j.y});this.currentX_=j.x;this.currentY_=j.y};M.bezierCurveTo=function(j,i,AK,AJ,AI,AG){var Z=w(this,AI,AG);var AH=w(this,j,i);var m=w(this,AK,AJ);h(this,AH,m,Z)};function h(Z,m,j,i){Z.currentPath_.push({type:"bezierCurveTo",cp1x:m.x,cp1y:m.y,cp2x:j.x,cp2y:j.y,x:i.x,y:i.y});Z.currentX_=i.x;Z.currentY_=i.y}M.quadraticCurveTo=function(AI,j,i,Z){var AH=w(this,AI,j);var AG=w(this,i,Z);var AJ={x:this.currentX_+2/3*(AH.x-this.currentX_),y:this.currentY_+2/3*(AH.y-this.currentY_)};var m={x:AJ.x+(AG.x-this.currentX_)/3,y:AJ.y+(AG.y-this.currentY_)/3};h(this,AJ,m,AG)};M.arc=function(AL,AJ,AK,AG,i,j){AK*=D;var AP=j?"at":"wa";var AM=AL+W(AG)*AK-F;var AO=AJ+J(AG)*AK-F;var Z=AL+W(i)*AK-F;var AN=AJ+J(i)*AK-F;if(AM==Z&&!j){AM+=0.125}var m=w(this,AL,AJ);var AI=w(this,AM,AO);var AH=w(this,Z,AN);this.currentPath_.push({type:AP,x:m.x,y:m.y,radius:AK,xStart:AI.x,yStart:AI.y,xEnd:AH.x,yEnd:AH.y})};M.rect=function(j,i,Z,m){this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath()};M.strokeRect=function(j,i,Z,m){var p=this.currentPath_;this.beginPath();this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath();this.stroke();this.currentPath_=p};M.fillRect=function(j,i,Z,m){var p=this.currentPath_;this.beginPath();this.moveTo(j,i);this.lineTo(j+Z,i);this.lineTo(j+Z,i+m);this.lineTo(j,i+m);this.closePath();this.fill();this.currentPath_=p};M.createLinearGradient=function(i,m,Z,j){var p=new v("gradient");p.x0_=i;p.y0_=m;p.x1_=Z;p.y1_=j;return p};M.createRadialGradient=function(m,AG,j,i,p,Z){var AH=new v("gradientradial");AH.x0_=m;AH.y0_=AG;AH.r0_=j;AH.x1_=i;AH.y1_=p;AH.r1_=Z;return AH};M.drawImage=function(AR,m){var AK,AI,AM,AY,AP,AN,AT,Aa;var AL=AR.runtimeStyle.width;var AQ=AR.runtimeStyle.height;AR.runtimeStyle.width="auto";AR.runtimeStyle.height="auto";var AJ=AR.width;var AW=AR.height;AR.runtimeStyle.width=AL;AR.runtimeStyle.height=AQ;if(arguments.length==3){AK=arguments[1];AI=arguments[2];AP=AN=0;AT=AM=AJ;Aa=AY=AW}else{if(arguments.length==5){AK=arguments[1];AI=arguments[2];AM=arguments[3];AY=arguments[4];AP=AN=0;AT=AJ;Aa=AW}else{if(arguments.length==9){AP=arguments[1];AN=arguments[2];AT=arguments[3];Aa=arguments[4];AK=arguments[5];AI=arguments[6];AM=arguments[7];AY=arguments[8]}else{throw Error("Invalid number of arguments")}}}if(AR.tagName=="canvas"){var i=document.createElement("div");i.style.position="absolute";i.style.left=AK+"px";i.style.top=AI+"px";i.innerHTML=AR.outerHTML;this.element_.insertAdjacentHTML("BeforeEnd",i.outerHTML);return }var AZ=w(this,AK,AI);var p=AT/2;var j=Aa/2;var AX=[];var Z=10;var AH=10;AX.push(" <g_vml_:group",' coordsize="',D*Z,",",D*AH,'"',' coordorigin="0,0"',' style="width:',Z,"px;height:",AH,"px;position:absolute;");if(this.m_[0][0]!=1||this.m_[0][1]||this.m_[1][1]!=1||this.m_[1][0]){var AG=[];AG.push("M11=",this.m_[0][0],",","M12=",this.m_[1][0],",","M21=",this.m_[0][1],",","M22=",this.m_[1][1],",","Dx=",K(AZ.x/D),",","Dy=",K(AZ.y/D),"");var AV=AZ;var AU=w(this,AK+AM,AI);var AS=w(this,AK,AI+AY);var AO=w(this,AK+AM,AI+AY);AV.x=AB.max(AV.x,AU.x,AS.x,AO.x);AV.y=AB.max(AV.y,AU.y,AS.y,AO.y);AX.push("padding:0 ",K(AV.x/D),"px ",K(AV.y/D),"px 0;filter:progid:DXImageTransform.Microsoft.Matrix(",AG.join(""),", sizingmethod='clip');")}else{AX.push("top:",K(AZ.y/D),"px;left:",K(AZ.x/D),"px;")}AX.push(' ">','<g_vml_:image src="',AR.src,'"',' style="width:',D*AM,"px;"," height:",D*AY,'px"',' cropleft="',AP/AJ,'"',' croptop="',AN/AW,'"',' cropright="',(AJ-AP-AT)/AJ,'"',' cropbottom="',(AW-AN-Aa)/AW,'"'," />","</g_vml_:group>");this.element_.insertAdjacentHTML("BeforeEnd",AX.join(""))};M.stroke=function(AL){var AJ=[];var m=false;var j=10;var AM=10;AJ.push("<g_vml_:shape",' filled="',!!AL,'"',' style="position:absolute;width:',j,"px;height:",AM,'px;"',' coordorigin="0,0"',' coordsize="',D*j,",",D*AM,'"',' stroked="',!AL,'"',' path="');var AN=false;var AG={x:null,y:null};var AK={x:null,y:null};for(var AH=0;AH<this.currentPath_.length;AH++){var Z=this.currentPath_[AH];var AI;switch(Z.type){case"moveTo":AI=Z;AJ.push(" m ",K(Z.x),",",K(Z.y));break;case"lineTo":AJ.push(" l ",K(Z.x),",",K(Z.y));break;case"close":AJ.push(" x ");Z=null;break;case"bezierCurveTo":AJ.push(" c ",K(Z.cp1x),",",K(Z.cp1y),",",K(Z.cp2x),",",K(Z.cp2y),",",K(Z.x),",",K(Z.y));break;case"at":case"wa":AJ.push(" ",Z.type," ",K(Z.x-this.arcScaleX_*Z.radius),",",K(Z.y-this.arcScaleY_*Z.radius)," ",K(Z.x+this.arcScaleX_*Z.radius),",",K(Z.y+this.arcScaleY_*Z.radius)," ",K(Z.xStart),",",K(Z.yStart)," ",K(Z.xEnd),",",K(Z.yEnd));break}if(Z){if(AG.x==null||Z.x<AG.x){AG.x=Z.x}if(AK.x==null||Z.x>AK.x){AK.x=Z.x}if(AG.y==null||Z.y<AG.y){AG.y=Z.y}if(AK.y==null||Z.y>AK.y){AK.y=Z.y}}}AJ.push(' ">');if(!AL){S(this,AJ)}else{d(this,AJ,AG,AK)}AJ.push("</g_vml_:shape>");this.element_.insertAdjacentHTML("beforeEnd",AJ.join(""))};function S(j,AG){var i=c(j.strokeStyle);var m=i.color;var p=i.alpha*j.globalAlpha;var Z=j.lineScale_*j.lineWidth;if(Z<1){p*=Z}AG.push("<g_vml_:stroke",' opacity="',p,'"',' joinstyle="',j.lineJoin,'"',' miterlimit="',j.miterLimit,'"',' endcap="',t(j.lineCap),'"',' weight="',Z,'px"',' color="',m,'" />')}function d(AQ,AI,Aj,AR){var AJ=AQ.fillStyle;var Aa=AQ.arcScaleX_;var AZ=AQ.arcScaleY_;var Z=AR.x-Aj.x;var m=AR.y-Aj.y;if(AJ instanceof v){var AN=0;var Ae={x:0,y:0};var AW=0;var AM=1;if(AJ.type_=="gradient"){var AL=AJ.x0_/Aa;var j=AJ.y0_/AZ;var AK=AJ.x1_/Aa;var Al=AJ.y1_/AZ;var Ai=w(AQ,AL,j);var Ah=w(AQ,AK,Al);var AG=Ah.x-Ai.x;var p=Ah.y-Ai.y;AN=Math.atan2(AG,p)*180/Math.PI;if(AN<0){AN+=360}if(AN<0.000001){AN=0}}else{var Ai=w(AQ,AJ.x0_,AJ.y0_);Ae={x:(Ai.x-Aj.x)/Z,y:(Ai.y-Aj.y)/m};Z/=Aa*D;m/=AZ*D;var Ac=AB.max(Z,m);AW=2*AJ.r0_/Ac;AM=2*AJ.r1_/Ac-AW}var AU=AJ.colors_;AU.sort(function(Am,i){return Am.offset-i.offset});var AP=AU.length;var AT=AU[0].color;var AS=AU[AP-1].color;var AY=AU[0].alpha*AQ.globalAlpha;var AX=AU[AP-1].alpha*AQ.globalAlpha;var Ad=[];for(var Ag=0;Ag<AP;Ag++){var AO=AU[Ag];Ad.push(AO.offset*AM+AW+" "+AO.color)}AI.push('<g_vml_:fill type="',AJ.type_,'"',' method="none" focus="100%"',' color="',AT,'"',' color2="',AS,'"',' colors="',Ad.join(","),'"',' opacity="',AX,'"',' g_o_:opacity2="',AY,'"',' angle="',AN,'"',' focusposition="',Ae.x,",",Ae.y,'" />')}else{if(AJ instanceof u){if(Z&&m){var AH=-Aj.x;var Ab=-Aj.y;AI.push("<g_vml_:fill",' position="',AH/Z*Aa*Aa,",",Ab/m*AZ*AZ,'"',' type="tile"',' src="',AJ.src_,'" />')}}else{var Ak=c(AQ.fillStyle);var AV=Ak.color;var Af=Ak.alpha*AQ.globalAlpha;AI.push('<g_vml_:fill color="',AV,'" opacity="',Af,'" />')}}}M.fill=function(){this.stroke(true)};M.closePath=function(){this.currentPath_.push({type:"close"})};function w(i,p,j){var Z=i.m_;return{x:D*(p*Z[0][0]+j*Z[1][0]+Z[2][0])-F,y:D*(p*Z[0][1]+j*Z[1][1]+Z[2][1])-F}}M.save=function(){var Z={};R(this,Z);this.aStack_.push(Z);this.mStack_.push(this.m_);this.m_=g(X(),this.m_)};M.restore=function(){if(this.aStack_.length){R(this.aStack_.pop(),this);this.m_=this.mStack_.pop()}};function H(Z){return isFinite(Z[0][0])&&isFinite(Z[0][1])&&isFinite(Z[1][0])&&isFinite(Z[1][1])&&isFinite(Z[2][0])&&isFinite(Z[2][1])}function AA(i,Z,j){if(!H(Z)){return }i.m_=Z;if(j){var p=Z[0][0]*Z[1][1]-Z[0][1]*Z[1][0];i.lineScale_=n(e(p))}}M.translate=function(j,i){var Z=[[1,0,0],[0,1,0],[j,i,1]];AA(this,g(Z,this.m_),false)};M.rotate=function(i){var m=W(i);var j=J(i);var Z=[[m,j,0],[-j,m,0],[0,0,1]];AA(this,g(Z,this.m_),false)};M.scale=function(j,i){this.arcScaleX_*=j;this.arcScaleY_*=i;var Z=[[j,0,0],[0,i,0],[0,0,1]];AA(this,g(Z,this.m_),true)};M.transform=function(p,m,AH,AG,i,Z){var j=[[p,m,0],[AH,AG,0],[i,Z,1]];AA(this,g(j,this.m_),true)};M.setTransform=function(AG,p,AI,AH,j,i){var Z=[[AG,p,0],[AI,AH,0],[j,i,1]];AA(this,Z,true)};M.drawText_=function(AM,AK,AJ,AP,AI){var AO=this.m_,AS=1000,i=0,AR=AS,AH={x:0,y:0},AG=[];var Z=Q(b(this.font),this.element_);var j=AC(Z);var AT=this.element_.currentStyle;var p=this.textAlign.toLowerCase();switch(p){case"left":case"center":case"right":break;case"end":p=AT.direction=="ltr"?"right":"left";break;case"start":p=AT.direction=="rtl"?"right":"left";break;default:p="left"}switch(this.textBaseline){case"hanging":case"top":AH.y=Z.size/1.75;break;case"middle":break;default:case null:case"alphabetic":case"ideographic":case"bottom":AH.y=-Z.size/2.25;break}switch(p){case"right":i=AS;AR=0.05;break;case"center":i=AR=AS/2;break}var AQ=w(this,AK+AH.x,AJ+AH.y);AG.push('<g_vml_:line from="',-i,' 0" to="',AR,' 0.05" ',' coordsize="100 100" coordorigin="0 0"',' filled="',!AI,'" stroked="',!!AI,'" style="position:absolute;width:1px;height:1px;">');if(AI){S(this,AG)}else{d(this,AG,{x:-i,y:0},{x:AR,y:Z.size})}var AN=AO[0][0].toFixed(3)+","+AO[1][0].toFixed(3)+","+AO[0][1].toFixed(3)+","+AO[1][1].toFixed(3)+",0,0";var AL=K(AQ.x/D)+","+K(AQ.y/D);AG.push('<g_vml_:skew on="t" matrix="',AN,'" ',' offset="',AL,'" origin="',i,' 0" />','<g_vml_:path textpathok="true" />','<g_vml_:textpath on="true" string="',AF(AM),'" style="v-text-align:',p,";font:",AF(j),'" /></g_vml_:line>');this.element_.insertAdjacentHTML("beforeEnd",AG.join(""))};M.fillText=function(j,Z,m,i){this.drawText_(j,Z,m,i,false)};M.strokeText=function(j,Z,m,i){this.drawText_(j,Z,m,i,true)};M.measureText=function(j){if(!this.textMeasureEl_){var Z='<span style="position:absolute;top:-20000px;left:0;padding:0;margin:0;border:none;white-space:pre;"></span>';this.element_.insertAdjacentHTML("beforeEnd",Z);this.textMeasureEl_=this.element_.lastChild}var i=this.element_.ownerDocument;this.textMeasureEl_.innerHTML="";this.textMeasureEl_.style.font=this.font;this.textMeasureEl_.appendChild(i.createTextNode(j));return{width:this.textMeasureEl_.offsetWidth}};M.clip=function(){};M.arcTo=function(){};M.createPattern=function(i,Z){return new u(i,Z)};function v(Z){this.type_=Z;this.x0_=0;this.y0_=0;this.r0_=0;this.x1_=0;this.y1_=0;this.r1_=0;this.colors_=[]}v.prototype.addColorStop=function(i,Z){Z=c(Z);this.colors_.push({offset:i,color:Z.color,alpha:Z.alpha})};function u(i,Z){r(i);switch(Z){case"repeat":case null:case"":this.repetition_="repeat";break;case"repeat-x":case"repeat-y":case"no-repeat":this.repetition_=Z;break;default:o("SYNTAX_ERR")}this.src_=i.src;this.width_=i.width;this.height_=i.height}function o(Z){throw new q(Z)}function r(Z){if(!Z||Z.nodeType!=1||Z.tagName!="IMG"){o("TYPE_MISMATCH_ERR")}if(Z.readyState!="complete"){o("INVALID_STATE_ERR")}}function q(Z){this.code=this[Z];this.message=Z+": DOM Exception "+this.code}var y=q.prototype=new Error;y.INDEX_SIZE_ERR=1;y.DOMSTRING_SIZE_ERR=2;y.HIERARCHY_REQUEST_ERR=3;y.WRONG_DOCUMENT_ERR=4;y.INVALID_CHARACTER_ERR=5;y.NO_DATA_ALLOWED_ERR=6;y.NO_MODIFICATION_ALLOWED_ERR=7;y.NOT_FOUND_ERR=8;y.NOT_SUPPORTED_ERR=9;y.INUSE_ATTRIBUTE_ERR=10;y.INVALID_STATE_ERR=11;y.SYNTAX_ERR=12;y.INVALID_MODIFICATION_ERR=13;y.NAMESPACE_ERR=14;y.INVALID_ACCESS_ERR=15;y.VALIDATION_ERR=16;y.TYPE_MISMATCH_ERR=17;G_vmlCanvasManager=E;CanvasRenderingContext2D=a;CanvasGradient=v;CanvasPattern=u;DOMException=q})()};var Whiteboard=(function(){var wb={};var canvas,context,pencil_btn,rect_btn,width,height,x,y,clickX,clickY,penDown=false;var origcanvas,origcontext,currentTool="pencil";var graphcanvas,graphcontext,topcanvas,topcontext,gr2D,nL,graphMode,gr2D_xp,gr2D_yp,nL_xp,nL_yp;var offX,offY,x0,y0,w0,h0,drawingLayer,drawcolor,rendering;var graphicData,tool_id;var scope=this;var isTouchEnabled=false;function renderText(xt,xp,yp){var txt=xt?xt:$get_Element("#content").value;var str=txt.split("\n");var x0=xp?xp:clickX;var y0=yp?yp:clickY;var ht=15;for(var i=0;i<str.length;i++){context.fillText(str[i],x0,y0);y0+=ht}updateCanvas();if(!xt){updateText(txt);sendData();$get_Element("#content").value="";$get_Element("#inputBox").style.display="none"}}function onkeyupHandler(){}function onkeydownHandler(_event){var event=_event?_event:window.event;if(currentTool=="text"&&event.keyCode==13){if(!event.shiftKey){if(event.preventDefault){event.preventDefault()}else{event.returnValue=false}renderText()}}}function resetButtonHighlite(){$get_Element("#button_text").style.border="1px solid #000000";$get_Element("#button_pencil").style.border="1px solid #000000";$get_Element("#button_line").style.border="1px solid #000000";$get_Element("#button_rect").style.border="1px solid #000000";$get_Element("#button_oval").style.border="1px solid #000000";$get_Element("#button_eraser").style.border="1px solid #000000"}function buttonHighlite(t){resetButtonHighlite();$get_Element("#button_"+t).style.border="2px solid #ff9900"}function viewport(){var e=window,a="inner";if(!("innerWidth" in window)){a="client";e=document.documentElement||document.body}return{width:e[a+"Width"],height:e[a+"Height"]}}function getDocHeight(){var D=document;return Math.max(Math.max(D.body.scrollHeight,D.documentElement.scrollHeight),Math.max(D.body.offsetHeight,D.documentElement.offsetHeight),Math.max(D.body.clientHeight,D.documentElement.clientHeight))}function touchStartFunction(event){event.preventDefault()}var touchMoveFunction=touchStartFunction;var _imageBaseDir="/gwt-resources/images/whiteboard/";var mainDoc;wb.initWhiteboard=function(mainDocIn){console.log("WHITEBOARD_INITIATED! - document object:"+mainDocIn);mainDoc=mainDocIn;canvas=$get_Element("#canvas");var siz=viewport();var docWidth=siz.width;var docHeight=siz.height;var topOff=$get_Element("#tools").offsetHeight+$get_Element("#tools").offsetTop+15;var leftOff=$get_Element("#tools").offsetLeft+15;origcanvas=$get_Element("#ocanvas");graphcanvas=$get_Element("#gcanvas");topcanvas=$get_Element("#tcanvas");canvas.width=origcanvas.width=graphcanvas.width=topcanvas.width=docWidth-leftOff;canvas.height=origcanvas.height=graphcanvas.height=topcanvas.height=docHeight-topOff;context=canvas.getContext("2d");origcontext=origcanvas.getContext("2d");graphcontext=graphcanvas.getContext("2d");topcontext=topcanvas.getContext("2d");width=canvas.width;height=canvas.height;context.font=origcontext.font=topcontext.font="12px sans-serif";gr2D=new Image();gr2D.src=_imageBaseDir+"gr2D.png";nL=new Image();nL.src=_imageBaseDir+"nL.png";graphMode="";gr2D_xp=nL_xp=(width-300)/2;gr2D_yp=(height-300)/2;nL_yp=(height-100)/2;gr2D_w=300;gr2D_h=300;nL_w=300;nL_h=100;offX=$get_Element("#canvas-container").offsetLeft;offY=$get_Element("#canvas-container").offsetTop;function getCanvasPos(){console.log("getCanvasPos processing!");var box=canvas.getBoundingClientRect();console.log("canvas bound= top: "+box.top+" left:"+box.left);var body=mainDoc.body;var docElem=mainDoc.documentElement;var scrollTop=window.pageYOffset||docElem.scrollTop||body.scrollTop;var scrollLeft=window.pageXOffset||docElem.scrollLeft||body.scrollLeft;var clientTop=docElem.clientTop||body.clientTop||0;var clientLeft=docElem.clientLeft||body.clientLeft||0;console.log("offset_datas: scrollTop="+scrollTop+" scrollLeft="+scrollLeft+" clientTop="+clientTop+" clientLeft="+clientLeft);var top=box.top+scrollTop-clientTop;var left=box.left+scrollLeft-clientLeft;offX=Math.round(left);offY=Math.round(top);console.log("OFFSET: top="+offY+" left="+offX);return{top:offY,left:offX}}console.log("getCanvasPos calling!");getCanvasPos();console.log("getCanvasPos CALL END!");graphicData={};tool_id={};tool_id.eraser=0;tool_id.pencil=1;tool_id.text=2;tool_id.line=3;tool_id.rect=4;tool_id.oval=5;tool_id.gr2D=11;tool_id.nL=12;drawingLayer="1";$get_Element("#button_pencil").style.border="2px solid #ff9900";$get_Element("#button_text").onclick=function(event){currentTool="text";buttonHighlite(currentTool)};$get_Element("#button_pencil").onclick=function(event){currentTool="pencil";buttonHighlite(currentTool)};$get_Element("#button_rect").onclick=function(event){currentTool="rect";buttonHighlite(currentTool)};$get_Element("#button_line").onclick=function(event){currentTool="line";buttonHighlite(currentTool)};$get_Element("#button_oval").onclick=function(event){currentTool="oval";buttonHighlite(currentTool)};$get_Element("#button_gr2D").onclick=function(event){currentTool="gr2D";showHideGraph("gr2D");buttonHighlite("pencil")};$get_Element("#button_nL").onclick=function(event){currentTool="nL";showHideGraph("nL");buttonHighlite("pencil")};$get_Element("#button_clear").onclick=function(event){currentTool="pencil";buttonHighlite(currentTool);resetWhiteBoard(true)};$get_Element("#button_eraser").onclick=function(event){currentTool="eraser";buttonHighlite(currentTool)};$get_Element("#done_btn").onclick=function(event){renderText()};$get_Element("#button_save").onclick=function(event){wb.saveWhiteboard()};var ev_onmousedown=function(_event){isTouchEnabled=_event.type.indexOf("touch")>-1;if(isTouchEnabled){canvas.removeEventListener("mousedown",ev_onmousedown,false);canvas.removeEventListener("mouseup",ev_onmouseup,false);canvas.removeEventListener("mousemove",ev_onmousemove,false)}getCanvasPos();var event=_event?_event:window.event;event=isTouchEnabled?_event.changedTouches[0]:event;var dx,dy,dist;if(event.pageX!=undefined){dx=event.pageX-offX;dy=event.pageY-offY}else{dx=event.clientX-offX;dy=event.clientY-offY}console.log(dy+":"+event.clientY+":"+event.layerY+":"+event.pageY+":"+offY);context.lineWidth=2;context.strokeStyle="rgb(0, 0, 0)";if(dx>=0&&dx<width){penDown=true;rendering=false;clickX=dx;clickY=dy;x=dx;y=dy;if(!graphicData.dataArr){graphicData.dataArr=[]}graphicData.id=tool_id[currentTool];if(currentTool=="pencil"){context.beginPath();context.moveTo(clickX,clickY)}else{if(currentTool=="eraser"){erase(x,y)}}drawcolor=colorToNumber(context.strokeStyle);if(currentTool=="text"){penDown=false;graphicData.dataArr[0]={x:x,y:y,text:"",color:drawcolor,name:"",layer:drawingLayer};showTextBox()}else{graphicData.dataArr[graphicData.dataArr.length]={x:x,y:y,id:"move",color:drawcolor,name:"",layer:drawingLayer}}}else{penDown=false}if(event.preventDefault){event.preventDefault()}};var ev_onmouseup=function(_event){var event=_event?_event:window.event;event=_event.type.indexOf("touch")>-1?_event.targetTouches[0]:event;if(rendering){penDown=false;if(currentTool=="rect"||currentTool=="oval"){graphicData.dataArr[0].w=w0;graphicData.dataArr[0].h=h0;graphicData.dataArr[0].xs=w0/400;graphicData.dataArr[0].ys=h0/400}else{if(currentTool=="line"||currentTool=="pencil"||currentTool=="eraser"){var xp=x-clickX;var yp=y-clickY;xp=currentTool=="eraser"?x:xp;yp=currentTool=="eraser"?y:yp;graphicData.dataArr[graphicData.dataArr.length]={x:xp,y:yp,id:"line"}}}if(currentTool!="eraser"){updateCanvas();context.beginPath()}sendData();rendering=false}};var ev_onmousemove=function(_event){var event=_event?_event:window.event;event=_event.type.indexOf("touch")>-1?_event.changedTouches[0]:event;if(penDown){rendering=true;if(currentTool!="pencil"&&currentTool!="text"){context.clearRect(0,0,canvas.width,canvas.height)}if(event.pageX!=undefined){x=event.pageX-offX;y=event.pageY-offY}else{x=event.clientX-offX;y=event.clientY-offY}if(currentTool=="rect"||currentTool=="oval"){x0=clickX;y0=clickY;w0=x-clickX;h0=y-clickY;if(currentTool=="rect"){drawRect(x0,y0,w0,h0)}if(currentTool=="oval"){drawOval(x0,y0,w0,h0)}}else{if(currentTool=="line"){context.beginPath();context.moveTo(clickX,clickY);drawLine()}else{if(currentTool=="eraser"){erase(x,y);graphicData.dataArr[graphicData.dataArr.length]={x:x,y:y,id:"line"}}else{graphicData.dataArr[graphicData.dataArr.length]={x:x-clickX,y:y-clickY,id:"line"};drawLine()}}}}if(event.preventDefault){event.preventDefault()}};if(document.addEventListener){canvas.addEventListener("mousedown",ev_onmousedown,false);canvas.addEventListener("mouseup",ev_onmouseup,false);canvas.addEventListener("mousemove",ev_onmousemove,false);canvas.addEventListener("touchstart",touchStartFunction,false);canvas.addEventListener("touchmove",touchMoveFunction,false);canvas.addEventListener("touchstart",ev_onmousedown,false);canvas.addEventListener("touchmove",ev_onmousemove,false);canvas.addEventListener("touchend",ev_onmouseup,false)}else{canvas.attachEvent("onmousedown",ev_onmousedown);canvas.attachEvent("onmouseup",ev_onmouseup);canvas.attachEvent("onmousemove",ev_onmousemove);canvas.attachEvent("touchstart",touchStartFunction);canvas.attachEvent("touchmove",touchMoveFunction);canvas.attachEvent("touchstart",ev_onmousedown);canvas.attachEvent("touchmove",ev_onmousemove);canvas.attachEvent("touchend",ev_onmouseup)}canvas.focus()};function $get_Element(n){var str=n.indexOf("#")>-1?n.split("#")[1]:n;return mainDoc.getElementById(str)}function updateText(txt){graphicData.dataArr[0].text=txt}function showTextBox(){$get_Element("#inputBox").style.display="block";$get_Element("#inputBox").style.top=clickY+"px";$get_Element("#inputBox").style.left=clickX+"px";$get_Element("#content").focus()}function resetWhiteBoard(boo){penDown=false;graphMode="";origcanvas.width=graphcanvas.width=topcanvas.width=canvas.width=width;origcontext.clearRect(0,0,canvas.width,canvas.height);graphcontext.clearRect(0,0,canvas.width,canvas.height);topcontext.clearRect(0,0,canvas.width,canvas.height);context.clearRect(0,0,canvas.width,canvas.height);drawingLayer="1";$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000";if(boo){clear(true)}}function showHideGraph(flag,x,y,boo){graphcanvas.width=graphcanvas.width;graphcanvas.height=graphcanvas.height;graphcontext.clearRect(0,0,canvas.width,canvas.height);graphicData.dataArr=[];graphicData.id=tool_id[currentTool];var addGraph=false;if(!boo&&((graphMode=="gr2D"&&flag=="gr2D")||(graphMode=="nL"&&flag=="nL"))){graphMode="";drawingLayer="1";$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000"}else{$get_Element("#button_gr2D").style.border="1px solid #000000";$get_Element("#button_nL").style.border="1px solid #000000";var gr,xp,yp,xs,ys;graphMode=flag;if(flag=="gr2D"){gr=gr2D;xp=x?x-(gr2D_w/2):gr2D_xp;yp=y?y-(gr2D_h/2):gr2D_yp;xs=x?x:gr2D_xp+(gr2D_w/2);ys=y?y:gr2D_yp+(gr2D_h/2);$get_Element("#button_gr2D").style.border="2px solid #ff0000"}else{gr=nL;xp=x?x-(nL_w/2):nL_xp;yp=y?y-(nL_h/2):nL_yp;xs=x?x:nL_xp+(nL_w/2);ys=y?y:nL_yp+(nL_h/2);$get_Element("#button_nL").style.border="2px solid #ff0000"}drawingLayer="3";addGraph=true;graphcontext.drawImage(gr,xp,yp)}graphicData.dataArr.push({x:xs,y:ys,name:"graphImage",addImage:addGraph});sendData()}function mouseOverGraph(){getCanvasPos();var mx=event.layerX?event.layerX:event.pageX-offX;var my=event.layerY?event.layerY:event.pageY-offY;var xp,yp,wi,hi;if(graphMode=="gr2D"){gr=gr2D;xp=gr2D_xp;yp=gr2D_yp;wi=300;hi=300}else{if(graphMode=="nL"){gr=nL;xp=nL_xp;yp=nL_yp;wi=300;hi=100}}if((mx>=xp&&mx<=xp+wi)&&(my>=yp&&my<=yp+hi)){return true}return false}function updateCanvas(){var cntxt=drawingLayer=="1"?origcontext:topcontext;cntxt.drawImage(canvas,0,0);context.clearRect(0,0,canvas.width,canvas.height);context.beginPath()}function erase(x,y){var ew=10;var ep=ew/2;origcontext.clearRect(x-ep,y-ep,ew,ew);topcontext.clearRect(x-ep,y-ep,ew,ew)}function drawLine(){context.lineTo(x,y);context.stroke()}function drawRect(x,y,w,h,color){if(color!=undefined){context.strokeStyle=color}context.strokeRect(x,y,w,h)}function drawOval(x,y,w,h,color){if(color!=undefined){context.strokeStyle=color}var kappa=0.5522848;var ox=(w/2)*kappa;var oy=(h/2)*kappa;var xe=x+w;var ye=y+h;var xm=x+w/2;var ym=y+h/2;context.beginPath();context.moveTo(x,ym);context.bezierCurveTo(x,ym-oy,xm-ox,y,xm,y);context.bezierCurveTo(xm+ox,y,xe,ym-oy,xe,ym);context.bezierCurveTo(xe,ym+oy,xm+ox,ye,xm,ye);context.bezierCurveTo(xm-ox,ye,x,ym+oy,x,ym);context.closePath();context.stroke()}function sendData(){if(graphicData.id||graphicData.id===0){var txtVal=graphicData.dataArr[graphicData.dataArr.length-1].text;if(graphicData.id==2&&(txtVal==""||txtVal==undefined)){resetArrays();textRendering=false;return }if(graphicData.id==1&&graphicData.dataArr.length>500){var jStr=convertObjToString(graphicData);currentObj.tempData=convertStringToObj(jStr);var ptC=graphicData.dataArr.length;var segArr=[];var buf;var header=graphicData.dataArr.shift();var tarr=graphicData.dataArr;var segData;var nxtStart;var nx0;var ny0;var pt={x:header.x,y:header.y};var nname=header.name;var segC=0;var nheader;while(ptC>0){segC++;buf=Math.min(500,ptC);ptC=ptC-buf;segData=tarr.splice(0,buf);var ngdata={};ngdata.lineColor=graphicData.lineColor;ngdata.id=graphicData.id;if(segC>1){var sObj={};sObj.id="move";sObj.x=pt.x;sObj.y=pt.y;segData.unshift(sObj)}nheader=cloneObject(header);nheader.name=nname;segData.unshift(nheader);ngdata.dataArr=segData;segArr.push(ngdata);nxtStart=segData[segData.length-1];pt={x:nxtStart.x,y:nxtStart.y};var n=header.name.split("_");nname=n[0]+"_"+(Number(n[1])+1)}for(var z=0;z<segArr.length;z++){sendDataToSERVER(segArr[z])}render=false;resetArrays();textRendering=false;return }render=false;sendDataToSERVER(graphicData);textRendering=false}resetArrays()}function sendDataToSERVER(jsdata){var jsonStr=convertObjToString(jsdata);wb.whiteboardOut(jsonStr,true)}function cloneObject(obj){var clone={};for(var m in obj){clone[m]=obj[m]}return clone}function resetArrays(){graphicData.dataArr=null;graphicData={}}function getToolFromID(id){for(var m in tool_id){if(id==tool_id[m]){return m}}}function convertObjToString(obj){try{var s=JSON.stringify(obj);return s}catch(ex){console.log(ex.name+":"+ex.message+":"+ex.location+":"+ex.text)}}function convertStringToObj(str){try{var o=eval("("+str+")");return o}catch(ex){console.log(ex.name+":"+ex.message+":"+ex.location+":"+ex.text)}}function renderObj(obj){var graphic_id=obj.id;var graphic_data=obj.dataArr;var line_rgb=obj.lineColor;var dLength=graphic_data.length;var dep,x0,y0,x1,y1;var textF;var idName;drawingLayer=graphic_data[0].layer?graphic_data[0].layer:drawingLayer;context.lineWidth=2;context.strokeStyle="rgb(0, 0, 0)";var deb="";if(graphic_id===0){for(var i=0;i<dLength;i++){x1=graphic_data[i].x;y1=graphic_data[i].y;deb+=x1+":"+y1+"||";erase(x1,y1)}}if(graphic_id===3||graphic_id===1){for(i=0;i<dLength;i++){x1=graphic_data[i].x;y1=graphic_data[i].y;if(graphic_data[i].id=="move"){context.beginPath();context.moveTo(x1,y1);x0=x1;y0=y1}else{context.lineTo(x0+x1,y0+y1)}}context.stroke();updateCanvas()}if(graphic_id===2){for(i=0;i<dLength;i++){if(graphic_data[i].text!=""||graphic_data[i].text!=undefined){x0=graphic_data[i].x;y0=graphic_data[i].y;renderText(xt,x0,y0)}}updateCanvas()}if(graphic_id===4||graphic_id===5){var fName=graphic_id==4?drawRect:drawOval;for(i=0;i<dLength;i++){var xd=graphic_data[i].xs<0?-1:1;var yd=graphic_data[i].ys<0?-1:1;x0=graphic_data[i].x;y0=graphic_data[i].y;w0=graphic_data[i].w*xd;h0=graphic_data[i].h*yd;fName(x0,y0,w0,h0)}updateCanvas()}if(graphic_id===11||graphic_id===12){idName=graphic_id==11?"gr2D":"nL";showHideGraph(idName,graphic_data[0].x,graphic_data[0].y,graphic_data[0].addImage)}}updateWhiteboard=function(cmdArray){var oaL=cmdArray.length;for(var l=0;l<oaL;l++){if(cmdArray[l] instanceof Array){var arg=cmdArray[l][1];arg=arg==undefined?[]:arg;this[cmdArray[l][0]].apply(scope,arg)}else{if(cmdArray[l].indexOf("dataArr")!=-1){draw(cmdArray[l])}else{scope[cmdArray[l]]()}}}};function gwt_updatewhiteboard(cmdArray){var realArray=[];for(var i=0,t=cmdArray.length;i<t;i++){var ele=[];ele[0]=cmdArray[i][0];ele[1]=cmdArray[i][1];realArray[i]=ele}updateWhiteboard(realArray)}wb.updateWhiteboard=function(cmdArray){gwt_updatewhiteboard(cmdArray)};draw=function(json_str){var grobj=convertStringToObj(json_str);renderObj(grobj)};function colorToNumber(c){var n=c.split("#").join("0x");return Number(n)}function clear(boo){if(!boo){resetWhiteBoard(false)}wb.whiteboardOut("clear",false)}wb.saveWhiteboard=function(){alert("default whiteboard save")};wb.whiteboardOut=function(data,boo){alert("default whiteboard out: "+data)};wb.disconnectWhiteboard=function(documentObject){alert("default whiteboard disconnect")};return wb}());function initializeQuiz(){var D=document.getElementById("testset_div");if(D){var C=D.getElementsByTagName("div");for(var B=0,A=C.length;B<A;B++){var F=C[B];if(F.className=="hm_question_def"){var E="<button class='edit-button' onclick='editQuizQuestion(this);'>!</button>";F.innerHTML=E+F.innerHTML;initializeQuizQuestion(F)}}processMathJax()}}var uniquer=1;function initializeQuizQuestion(E){var L="answer_"+uniquer;var D="ABCDEFGHIJKLMNOPQRSTUVWXYZ";var I=E.getElementsByTagName("li");for(var F=0,K=I.length;F<K;F++){answer=I[F];var H=answer.getAttribute("correct");var B=answer.getElementsByTagName("div");var G=B[0];uniquer++;var C="answer_id_"+uniquer;var A=D.charAt(F);var J="<span class='question-input' style='margin-right: 10px'><input value='"+H+"' type='radio' name='"+L+"' id='"+C+"' onclick='questionGuessChanged(this)'/>&nbsp;"+A+"</span>";G.innerHTML=J+G.innerHTML;if(B.length>0){B[1].style.display="none"}}}function hideQuestionResult(B){var E=B.getElementsByTagName("li");for(var D=0,C=E.length;D<C;D++){answer=E[D];var A=answer.getElementsByTagName("div");if(A.length>1){A[1].style.display="none"}}}function editQuizQuestion(C){var B=C.parentNode.parentNode.getAttribute("guid");if(B){var A=window.open("/solution_editor/SolutionEditor.html?pid="+B)}}function hideQuizQuestionResults(C){if(!C){C="testset_div"}var E=document.getElementById(C);if(E){var D=E.getElementsByTagName("div");for(var B=0,A=D.length;B<A;B++){var F=D[B];if(F.className=="hm_question_def"){hideQuestionResult(F)}}processMathJax()}}function prepareCustomQuizForDisplay(F,H){var E=F.getElementsByTagName("input");var G=0;var D=0;for(var C=0,B=E.length;C<B;C++){if(C>3&&(C%4)==0){G++}E[C].disabled=true;var A=C-(G*4);if(H[G]==A){E[C].checked=true}}};// version 1.1
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
    
    if(expectedValue.indexOf("[")>-1){
        var splitVal=expectedValue.split("]")
        var ewhole=splitVal[0].split("[")[1];
        splitVal=splitVal[1].split("/")
        var enumero=splitVal[0]
        var eden=splitVal[1]
        expectedValue=((ewhole*eden)+Number(enumero))+"/"+eden;
    }
    var inputValue=$get('widget_input_field_1').value;
    var isFrac=$get('widget_input_field_2')!=undefined;
    var isMixed=$get('widget_input_field_3')!=undefined;
    var num,den,whole
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
HmFlashWidgetImplOdds.prototype.processWidgetValidation = validateOdds;/**
 * @author sathesh
 */
var Plotter = function(graphObj, plot_type, plot_input) {
	this.pInf = Number.POSITIVE_INFINITY;
	this.nInf = Number.NEGATIVE_INFINITY;
	this.fArr = ["sin", "cos", "tan", "asin", "acos", "atan", "log", "abs", "sqrt", "ln", "cot", "sec", "csc"];
	this.graphObj = graphObj;
	this.plot_type = plot_type;
	this.plot_input = plot_input;
	this.graph_type = this.graphObj.graph_type;
	this.numberLine = this.graph_type == 'x' ? true : false;
	//
	this.canvas = this.graphObj.document.createElement("canvas");
	this.canvas.width = this.graphObj.width;
	this.canvas.height = this.graphObj.height;
	//
	this.canvas.style.position = 'absolute';
	this.canvas.style.top = 0;
	this.canvas.style.left = 0;
	//
	this.graphObj.board.appendChild(this.canvas);
	this.context = this.canvas.getContext('2d');
	//
	//this.context.strokeStyle='red';
	//console.log(this.context.strokeStyle);
	EqParser.init();
	this.getPlots();
}
Plotter.prototype.validateFunc = function() {
	var boo = true;
	var veqn = this.fn;
	var pos, len, brac;
	for(var f = 0; f < this.fArr.length; f++) {
		len = this.fArr[f].length;
		pos = veqn.indexOf(this.fArr[f]);
		if(pos == -1) {
			continue;
		} else {
			brac = veqn.substr(pos + len, 1);
			if(brac != "(") {
				boo = false;
				break;
			}
		}
	}
	return boo;
}
Plotter.prototype.getDval = function(eqO, v, dv) {
	
	var dval = this.fixTo(v + dv, 4);
	return this.evalFor(eqO, dval);
}
Plotter.prototype.sign = function(val) {
	if(val * 1 < 0) {
		return '-';
	} else {
		return '+';
	}
}
Plotter.prototype.setFunctionDatas = function() {
	this.fn = this.funcObj[0][0];
	this.fncol = this.funcObj[2];
	this.fneql = this.funcObj[0][2];
	this.fand = this.funcObj[0][3];
	this.fof = this.funcObj[0][1];
	this.fof = this.fof ? this.fof : 'x';
	this.fneql = this.fneql ? this.fneql : 'eq';
	if(this.fand===undefined){
		if(this.fneql.indexOf("_")>-1&&this.fn.indexOf("_")>-1){
			this.fand=true;
		}
	}
	//alert(this.fncol);
}
Plotter.prototype.coordToCanvasPoint = function(x, y) {
	var xp = this.graphObj.axisYpos + x * (this.graphObj.scaleX / this.graphObj.xinc);
	var yp = this.graphObj.axisXpos - y * (this.graphObj.scaleY / this.graphObj.yinc);
	return [xp, yp];
}
Plotter.prototype.fixTo = function(v, p) {
	var p10 = Math.pow(10, p);
	return Math.round(p10 * v) / p10;
}
Plotter.prototype.evalFor = function(ev, fr) {
	return EqParser.evalEq(ev, fr);
}
Plotter.prototype.getPlots = function() {
	this.getPlotInputs();
	var temp1 = this.plot_datas.split("|");
	for(var i = 0; i < temp1.length; i++) {
		var pointdata = eval(temp1[i]);
		this.plot_type = pointdata.shift();
		this.plot_data = pointdata;
		this.drawPlot();
	}
}
Plotter.prototype.drawPlot = function() {

	if(this.plot_type == 'point') {
		this.plotPoint();
	} else if(this.plot_type == 'function') {
		this.plotFunction();
	} else if(this.plot_type == 'polygon') {
		this.close_path = true;
		this.plotPolygon();
	} else if(this.plot_type == 'path') {
		this.close_path = false;
		this.plotPolygon();
	} else if(this.plot_type == 'circle') {
		this.plotCircle();
	} else if(this.plot_type == 'rectangle') {
		this.plotRect();
	}
}
Plotter.prototype.getPlotInputs = function() {
	this.plot_datas = this.plot_input.data;
	this.fn_color = this.plot_input.fn_color;
	this.fn_color = this.fn_color ? this.fn_color : '#0000ff';
}
Plotter.prototype.plotCircle = function() {
	var cx, cy, r, w, h
	if(this.plot_data[0].length == 1) {
		cx = 0;
		cy = 0;
		r = this.plot_data[0][0];
		w = r * 2;
		h = w;
	} else if(this.plot_data[0].length == 2) {
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
	if(fcolor) {
		var falpha = color[2] ? color[2] : 0.6;
		fcolor = this.hex2rgb(fcolor, falpha);
	}
	this.drawCircle(pt[0], pt[1], r, true, scolor, falpha, fcolor);
}
Plotter.prototype.plotRect = function() {
	var cx, cy, r, w, h
	if(this.plot_data[0].length == 1) {
		cx = 0;
		cy = 0;
		//r=this.plot_data[0][0];
		w = this.plot_data[0][0];
		h = this.plot_data[0][1];
	} else if(this.plot_data[0].length == 2) {
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
	if(fcolor) {
		var falpha = color[2] ? color[2] : 0.6;
		fcolor = this.hex2rgb(fcolor, falpha);
	}

	this.drawRect(pt[0], pt[1], w, h, true, scolor, falpha, fcolor);
}
Plotter.prototype.plotPolygon = function() {
	var pts = this.plot_data[0];
	var label = this.plot_data[1];
	var color = this.plot_data[2];
	var scolor = color[0] ? color[0] : "BLUE";
	var fcolor = color[1];
	var close = this.close_path;
	if(!close) {
		scolor = color;
		fcolor = undefined;
	}
	if(fcolor) {
		var falpha = color[2] ? color[2] : 0.6;
		fcolor = this.hex2rgb(fcolor, falpha);
	}
	var scope = this.context;
	scope.lineWidth = 2.0;
	scope.strokeStyle = scolor
	scope.beginPath();
	for(var i = 0; i < pts.length; i++) {
		var pt = this.coordToCanvasPoint(pts[i][0], pts[i][1]);
		if(i == 0) {
			scope.moveTo(pt[0], pt[1]);
		} else {
			scope.lineTo(pt[0], pt[1]);
		}
		//console.log(pt[0] + ":" + pt[1] + ":" + scolor + ":" + fcolor)
	}
	if(close) {
		pt = this.coordToCanvasPoint(pts[0][0], pts[0][1]);
		scope.lineTo(pt[0], pt[1]);
	}

	if(true) {
		scope.stroke();
	}
	if(close && falpha) {
		scope.fill();
	}
	scope.closePath();
	if(label) {
		for(var i = 0; i < pts.length; i++) {

			this.plotPoint([[pts[i][0], pts[i][1]], label, scolor])
		}
	}

}
Plotter.prototype.plotPoints = function() {
	var temp1 = this.plot_data.split("|");
	for(var i = 0; i < temp1.length; i++) {
		var pointdata = eval(temp1[i]);
		if(pointdata[0].length == 1) {
			this.plotPoint(pointdata[0][0], 0, pointdata[1], pointdata[2]);
		} else {
			this.plotPoint(pointdata[0][0], pointdata[0][1], pointdata[1], pointdata[2]);
		}

	}
}
Plotter.prototype.plotPoint = function(data) {
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
	if(label) {

		this.context.textBaseline = 'bottom';
		this.context.font = "bold 12px sans-serif";
		if(this.graph_type == 'xy') {
			this.context.fillText("(" + x + ", " + y + ")", xp + 3, yp - 6);
		} else {
			this.context.fillText(x, xp - 3, yp - 6);
		}

	}
}
Plotter.prototype.plotFunctions = function() {
	var temp1 = this.plot_data.split("|");
	for(var i = 0; i < temp1.length; i++) {
		var fndata = eval(temp1[i]);
		this.plotFunction(fndata);
	}

}

Plotter.prototype.plotFunction = function() {
	this.funcObj = this.plot_data;
	this.getPointsFromEq();
}
//--------------------------------------------------------------------------------------//
Plotter.prototype.drawPoint = function(x, y, color, r, _open) {
	var ctx = this.context;
	var open = _open ? _open : false;
	var rad = r ? r : 4;
	if(!open) {
		ctx.fillStyle = color;
	} else {
		ctx.strokeStyle = color;
	}

	ctx.beginPath();
	ctx.arc(x, y, rad, 0, 2 * Math.PI, false);

	if(!open) {
		ctx.fill();
	} else {
		ctx.stroke();
	}
	ctx.closePath();
}
Plotter.prototype.drawCircle = function(x, y, rad, stroke, stroke_color, fill, fill_color) {
	var context = this.context;
	if(stroke) {
		context.lineWidth = 2.0;
		context.strokeStyle = stroke_color;
	}
	if(fill) {
		context.fillStyle = fill_color;
	}
	//var w = wd;
	//var h = ht;

	context.beginPath();
	context.arc(x, y, rad, 0, 2 * Math.PI, false);
	if(stroke) {
		context.stroke();
	}
	if(fill) {
		context.fill();
	}
	context.closePath();
}
Plotter.prototype.drawRect = function(x, y, w, h, stroke, stroke_color, fill, fill_color) {
	var context = this.context;
	if(stroke) {
		context.lineWidth = 2.0;
		context.strokeStyle = stroke_color;
	}
	if(fill) {
		context.fillStyle = fill_color;
	}
	//var w = wd;
	//var h = ht;

	context.beginPath();
	if(stroke) {
		context.strokeRect(x, y, w, h);
		//context.stroke();

	}
	if(fill) {
		context.fillRect(x, y, w, h);
		//context.fill();
	}
	context.closePath();
}
//--------------------------------------------------------------------------------------//
Plotter.prototype.getPointsFromEq = function() {
	var pInf = this.pInf;
	var nInf = this.nInf;
	this.setFunctionDatas();
	this.setAxisDatas();
	if(this.numbLine) {
		return this.plotNumberLine();
	}
	var fn = this.fn;
	var fneql = this.fneql;
	var f = this.fof;
	if(f == "y") {
		return this.getPointsFromEqY();
	}
	this.graph_color = this.fncol;
	if(fn == "" || fn == undefined) {
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
	var getDval=this.getDval;
	acc = Math.abs(xHi - xLow) / 300;
	for(var x = xLow; x <= xHi; x += acc) {
		x = fixTo(x, 4);
		y = -this.evalFor(eqnObj, x);
		x0 = x - acc;
		y0 = -this.evalFor(eqnObj, x0);
		x1 = x + acc;
		y1 = -this.evalFor(eqnObj, x1);
		//console.log(x + " <> " + y);
		if(Number(y)) {
			//var xpts=this.
			xPix = x * xscale + xaxis;
			yPix = y * yscale + yaxis;
			x0Pix = x0 * xscale + xaxis;
			y0Pix = y0 * yscale + yaxis;
			x1Pix = x1 * xscale + xaxis;
			y1Pix = y1 * yscale + yaxis;

			if(yPix > -2880 && yPix < 2880) {
				xPix = fixTo(xPix, 2);
				yPix = fixTo(yPix, 2);
				ptsArr.push({
					x : xPix,
					y : yPix,
					l : "line"
				});
			} else if(yPix == nInf) {
				if(y0Pix != nInf && y1Pix != nInf && y0Pix != pInf && y1Pix != pInf && Number(y0Pix)!=Number.NaN && Number(y1Pix)!=Number.NaN) {
					dy0 = this.getDval(eqnObj, x0, acc / 10);
					if(y0Pix < this.yh && y0Pix > this.yl) {
						if(dy0 > y0) {
							ycomp = this.yh;
						} else {
							ycomp = this.yl;
						}
					} else {
						ycomp = y0Pix;
					}
					ptsArr.push({
						x : x0Pix,
						y : ycomp,
						l : "line"
					});
					ptsArr.push({
						x : xPix,
						y : ycomp,
						l : "line"
					});
					ptsArr.push({
						x : xPix,
						y : -ycomp,
						l : "move"
					});
					dy1 = this.getDval(eqnObj, x1, -acc / 10);
					if(y1Pix < this.yh && y1Pix > this.yl) {
						if(dy1 > y1) {
							ycomp = this.yh;
						} else {
							ycomp = this.yl;
						}
					} else {
						ycomp = y1Pix;
					}
					ptsArr.push({
						x : x1Pix,
						y : ycomp,
						l : "move"
					});
				} else {
					ptsArr.push({
						x : xPix,
						y : nInf,
						l : "move"
					});
				}
			} else if(yPix == pInf) {
				if(y0Pix != nInf && y1Pix != nInf && y0Pix != pInf && y1Pix != pInf && Number(y0Pix) != Number.NaN && Number(y1Pix) != Number.NaN) {
					dy0 = this.getDval(eqnObj, x0, acc / 10);
					if(y0Pix < this.yh && y0Pix > this.yl) {
						if(dy0 > y0) {
							ycomp = this.yh;
						} else {
							ycomp = this.yl;
						}
					} else {
						ycomp = y0Pix;
					}
					ptsArr.push({
						x : x0Pix,
						y : ycomp,
						l : "line"
					});
					ptsArr.push({
						x : xPix,
						y : ycomp,
						l : "line"
					});
					ptsArr.push({
						x : xPix,
						y : -ycomp,
						l : "move"
					});
					dy1 = this.getDval(eqnObj, x1, -acc / 10);
					if(y1Pix < this.yh && y1Pix > this.yl) {
						if(dy1 > y1) {
							ycomp = this.yh;
						} else {
							ycomp = this.yl;
						}
					} else {
						ycomp = y1Pix;
					}
					ptsArr.push({
						x : x1Pix,
						y : ycomp,
						l : "line"
					});
				} else {
					ptsArr.push({
						x : xPix,
						y : pInf,
						l : "move"
					});
				}
			} else {
				buffrArr.push(y);
			}
		}
	}
	ptsArr = this.asymFix(ptsArr);
	var n;
	if(ptsArr.length < 2) {
		if(buffrArr.length < 2) {
			if(this.validateFunc()) {
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
Plotter.prototype.getNodeDetails = function(arr) {
	var pInf = this.pInf;
	var nInf = this.nInf;
	var sNode = {};
	var eNode = {};
	var nodes = {};
	
	for(var q = 0; q < arr.length; q++) {
		if(q == 0) {
			if(this.fof == "x") {
				if(arr[q].y == this.pInf) {
					arr[q].y = yh;
					arr[q].l = "line";
				}
				if(arr[q].y == this.nInf) {
					arr[q].y = yl;
					arr[q].l = "line";
				}
			}
			if(this.fof == "y") {
				if(arr[q].x == this.pInf) {
					arr[q].x = xh;
					arr[q].l = "line";
				}
				if(arr[q].x == this.nInf) {
					arr[q].x = xl;
					arr[q].l = "line";
				}
			}
		} else {
			if(this.fof == "x") {
				if(arr[q].y == this.pInf || arr[q].y == this.nInf) {
					arr[q].l = "move";
					arr[q + 1].l = "move";
				}
			}
			if(this.fof == "y") {
				if(arr[q].x == this.pInf || arr[q].x == this.nInf) {
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
Plotter.prototype.setAxisDatas = function() {
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
	if(mygraphObj.labType == "pi") {
		this.xMin = Math.round((xMin) * Math.PI);
		this.xMax = Math.round((xMax) * Math.PI);
		this.xscale = xscale / Math.PI;
	}
	if(mygraphObj.ylabType == "pi") {
		this.yMin = Math.round((yMin) * Math.PI);
		this.yMax = Math.round((yMax) * Math.PI);
		this.yscale = yscale / Math.PI;
	}
	this.xl = 0;
	this.xh = this.midX * 2;
	this.yl = 0;
	this.yh = this.midY * 2;
}
Plotter.prototype.getDir = function(pt) {
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
	if(pt.y >= yh && (pt.x <= xh && pt.x >= xl)) {
		dir = "N";
	}
	if(pt.y <= yl && (pt.x <= xh && pt.x >= xl)) {
		dir = "S";
	}
	if(pt.x >= xh && (pt.y <= yh && pt.y >= yl)) {
		dir = "E";
	}
	if(pt.x <= xl && (pt.y <= yh && pt.y >= yl)) {
		dir = "W";
	}
	if(pt.y >= yh && pt.x >= xh) {
		dir = "NE";
	}
	if(pt.y <= yl && pt.x >= xh) {
		dir = "SE";
	}
	if(pt.y >= yh && pt.x <= xl) {
		dir = "NW";
	}
	if(pt.y <= yl && pt.x <= xl) {
		dir = "SW";
	}
	return dir;
}
Plotter.prototype.plotPath = function(sN, eN, mc) {
	var pInf = this.pInf;
	var nInf = this.nInf;
	var pathString, pathArr, rot;
	var eqS = this.eqSy;
	var xh = this.xh;
	var xl = this.xl;
	var yh = this.yh;
	var yl = this.yl;
	var fof=this.fof;
	if(eqS == "eq" || eN.dir == "C" || sN.dir == "C") {
		return false;
	}
	if(eqS == "ge" || eqS == "gt") {
		if(fof == "y") {
			pathString = "S-SE-E-NE-N-NW-W-SW-S";
			rot = "+";
		} else {
			pathString = "N-NE-E-SE-S-SW-W-NW-N";
			rot = "-";
		}
	}
	if(eqS == "le" || eqS == "lt") {
		if(fof == "y") {
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
	for(var p = 0; p < pathArr.length; p++) {
		dir = pathArr[p];
		
		if(eN.dir == dir) {
			xp = eN.pt.x;
			yp = eN.pt.y;
			mc.moveTo(xp, yp);
			//console.log('move:'+xp+":"+yp);
			flag = true;
			if(eN.dir == sN.dir) {
				if((rot == "-" && dir == "S") || (rot == "+" && dir == "N")) {
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
		if(flag && dir.length == 2) {
			if(pathArr[p] == sN.dir) {
				//trace("DD:"+dir+":"+dir.length+":"+flag+":"+sN.pt.x+":"+sN.pt.y);
				mc.lineTo(sN.pt.x, sN.pt.y);
				//console.log('line0:'+dir+":"+sN.pt.x+":"+sN.pt.y);
				flag = false;
				break;
			} else {
				switch (dir) {
					case "NE" :
						if(rot == "-") {
							xp = xh;
						} else {
							if(sN.dir == "N") {
								yp = sN.pt.y;
							} else {
								yp = yh;
							}
						}
						//trace("DD:"+dir+":"+dir.length+":"+xp+":"+yp);
						mc.lineTo(xp, yp);
					//	console.log('line:'+dir+":"+xp+":"+yp);
						break;
					case "SE" :
						if(rot == "-") {
							yp = yl;
						} else {
							xp = xh;
						}
						mc.lineTo(xp, yp);
						//console.log('line:'+dir+":"+xp+":"+yp);
						break;
					case "SW" :
						if(rot == "-") {
							xp = xl;
						} else {
							yp = yl;
						}
						mc.lineTo(xp, yp);
					//	console.log('line:'+dir+":"+xp+":"+yp);
						break;
					case "NW" :
						if(rot == "-") {
							yp = yh;
						} else {
							xp = xl;
						}
						mc.lineTo(xp, yp);
						//console.log('line:'+dir+":"+xp+":"+yp);
						break;
				}
			}
		} else if(flag) {
			if(pathArr[p] == sN.dir) {
				mc.lineTo(sN.pt.x, sN.pt.y);
				//console.log('line1:'+dir+":"+sN.pt.x+":"+sN.pt.y);
				flag = false;
				break;
			}
		}
	}
	return true;
}
Plotter.prototype.drawFunction = function(nodes, arr) {
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
	var pathBegin=false;
	var sign=this.sign;
	this.overFlowObj={};
	if(eqSy == "lt" || eqSy == "gt" || eqSy == "neq") {
		lalpha = 0;
	}
	if(eqSy != "eq" && eqSy != "neq") {
		fHold.lineWidth = 0.0;
		fHold.strokeStyle = this.hex2rgb(this.graph_color, 0);
		fHold.fillStyle = this.hex2rgb(this.graph_color, 0.15);
		fHold.beginPath();
		this.fill = this.plotPath(nodes.s, nodes.e, fHold);
		pathBegin=true;
		fHold.stroke();
		if(!this.fill) {
			pathBegin=false;
			fHold.fill();
			fHold.closePath();
		}
		
	} else if(eqSy == "neq") {
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
	if(eqSy != "neq") {
		fHold.lineWidth = 2.0;
		fHold.strokeStyle = this.hex2rgb(this.graph_color, lalpha);
		//fHold.fillStyle=this.hex2rgb(this.graph_color,0.15);
		//alert(this.hex2rgb(this.graph_color, lalpha));
		if(!pathBegin){
			fHold.beginPath();
		}
		
		for(var q = 0; q < arr.length; q++) {
			if(q > 0) {
				var p1 = {
					x : arr[q].x,
					y : arr[q].y
				};
				var p2 = pPt;
				
				if(p1.x == pInf || p1.x == nInf || p1.y == pInf || p1.y == nInf || p2.x == pInf || p2.x == nInf || p2.y == pInf || p2.y == nInf) {
					//fHold.lineStyle(1, 0xff0000, lalpha);
					
					fHold.lineWidth = 2.0;
					fHold.strokeStyle = this.hex2rgb('#ff0000', lalpha);
					if(eqSy == "eq") {
						fHold.lineWidth = 2.0;
						fHold.strokeStyle = this.hex2rgb('#ff0000', 0);
					}
				} else {
					var dp = (this.distance(p1, p2));
					if(fof == "x") {
						if(dp >= (this.xh) && sign(p1.y) != sign(p2.y)) {
							fHold.lineWidth = 2.0;
							fHold.strokeStyle = this.hex2rgb('#ff0000', lalpha);
							if(eqSy == "eq") {
								fHold.lineWidth = 2.0;
								fHold.strokeStyle = this.hex2rgb('#ff0000', 0);
								fHold.moveTo(arr[q].x, arr[q].y);
								console.log('INFI:'+p1.x+":"+p1.y+":"+p2.x+":"+p2.y);
							}
						} else {
							fHold.lineWidth = 2.0;
							fHold.strokeStyle = this.hex2rgb(this.graph_color, lalpha);
						}
					}
					if(fof == "y") {
						if(dp >= (this.yh) && this.sign(p1.x) != this.sign(p2.x)) {
							fHold.lineWidth = 2.0;
							fHold.strokeStyle = this.hex2rgb(this.graph_color, lalpha);
							if(eqSy == "eq") {
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
						x : arr[q].x,
						y : arr[q].y
					};
				}
			} else {
				pPt = {
					x : arr[q].x,
					y : arr[q].y
				};
			}
			
			if(q == 0 && (eqSy == "eq" || !this.fill)) {
				//
				console.log("FUNCTION_PATH_START:");
				fHold.moveTo(arr[q].x, arr[q].y);
			} else {

				if(eqSy == "eq") {
					if(arr[q].l == "line") {
						fHold.lineTo(arr[q].x, arr[q].y);
					} else if(arr[q].l == "move") {
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
	if(eqSy != "eq") {
		//alert(fHold.paths);
		fHold.fill();
		
	}
	fHold.closePath();
	
	if(lalpha == 0) {
		fHold.beginPath();
		for(var q = 0; q < arr.length; q++) {
			if(q < arr.length - 1) {
				var p1 = {
					x : arr[q].x,
					y : arr[q].y
				};
				var p2 = {
					x : arr[q + 1].x,
					y : arr[q + 1].y
				};
				if(p1.x == pInf) {
					p1.x = xh;
				} else if(p1.x == nInf) {
					p1.x = xl;
				}
				if(p1.y == pInf) {
					p1.y = yh;
				} else if(p1.y == nInf) {
					p1.y = yl;
				}
				if(p2.x == pInf) {
					p2.x = xh;
				} else if(p2.x == nInf) {
					p2.x = xl;
				}
				if(p2.y == pInf) {
					p2.y = yh;
				} else if(p2.y == nInf) {
					p2.y = yl;
				}
				var dp = (this.distance(p1, p2));
				var sL, gL;
				//fHold.lineStyle(2, graph_color, 100, false);
				fHold.lineWidth = 1;
				fHold.strokeStyle = this.hex2rgb(this.graph_color, 1.0);
				
				if(dp >= 4) {
					sL = 1;
					gL = 3;
					if(fof == "x") {
						if(dp >= (this.xh) && this.sign(p1.y) != this.sign(p2.y)) {
							//fHold.lineStyle(2, 0xff0000, 100);
							fHold.lineWidth = 1;
							fHold.strokeStyle = this.hex2rgb('#ff0000', 1.0);
						}
					}
					if(fof == "y") {
						if(dp >= (this.yh) && this.sign(p1.x) != this.sign(p2.x)) {
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
				fHold.dashTo(p1.x, p1.y, p2.x, p2.y, sL, gL,this);
				fHold.stroke();
			}
		}
	}
	fHold.closePath();
	return fHold;
}
Plotter.prototype.getPointsFromEqY = function() {
	var pInf = this.pInf;
	var nInf = this.nInf;
	var isNaN = function(n) {
		return Number(n) == Number.NaN;
	}
	this.setFunctionDatas();
	this.setAxisDatas();
	this.graph_color = this.fncol;
	var fn=this.fn;
	var fneql=this.fneql;
	var fncol=this.fncol;
	if(fn == "" || fn == undefined) {
		return null;
	}
	var buffrArr = [];
	var eqnObj=this.eqnObj = this.fn;
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
	var xh=this.xh;
	var xl=this.xl;
	var yh=this.yh;
	var yl=this.yl;
	var y, x0, y0, x1, y1;
	var xPix, yPix, x0Pix, y0Pix, x1Pix, y1Pix, ycomp, dy0, dy1;
	this.doPlot = true;
	var fixTo = this.fixTo;
	acc = (yHi - yLow) / 300;
	acc=this.fixTo(acc,4);
	console.log(yLow+":"+yHi+":"+acc)
	for(var y = yLow; y <= yHi; y += acc) {
		y = fixTo(y, 4);
		x = this.evalFor(eqnObj, -y);
		y0 = -(y - acc);
		x0 = this.evalFor(eqnObj, y0);
		y1 = -(y + acc);
		x1 = this.evalFor(eqnObj, y1);
		
		if(Number(x)) {
			xPix = x * xscale + xaxis;
			yPix = y * yscale + yaxis;
			x0Pix = x0 * xscale + xaxis;
			y0Pix = y0 * yscale + yaxis;
			x1Pix = x1 * xscale + xaxis;
			y1Pix = y1 * yscale + yaxis;
			if(xPix > -2880 && xPix < 2880) {
				xPix = fixTo(xPix, 2);
				yPix = fixTo(yPix, 2);
				ptsArr.push({
					x : xPix,
					y : yPix,
					l : "line"
				});
			} else if(xPix == nInf) {
				if(x0Pix != nInf && x1Pix != nInf && x0Pix != pInf && x1Pix != pInf && !isNaN(x0Pix) && !isNaN(x1Pix)) {
					dx0 = this.getDval(eqnObj, y0, acc / 10);
					if(x0Pix < xh && x0Pix > xl) {
						if(dx0 > x0) {
							xcomp = xh;
						} else {
							xcomp = xl;
						}
					} else {
						xcomp = x0Pix;
					}
					ptsArr.push({
						y : y0Pix,
						x : xcomp,
						l : "line"
					});
					ptsArr.push({
						y : yPix,
						x : xcomp,
						l : "line"
					});
					ptsArr.push({
						y : yPix,
						x : -xcomp,
						l : "move"
					});
					dx1 = this.getDval(eqnObj, y1, -acc / 10);
					//trace(y1+":::::"+dy1);
					if(x1Pix < xh && x1Pix > xl) {
						if(dx1 > x1) {
							xcomp = xh;
						} else {
							xcomp = xl;
						}
					} else {
						xcomp = x1Pix;
					}
					ptsArr.push({
						y : y1Pix,
						x : xcomp,
						l : "move"
					});
					//ptsArr.push({x:x1Pix, y:yh, l:"move"});
				} else {
					ptsArr.push({
						y : yPix,
						x : nInf,
						l : "move"
					});
				}
				//ptsArr.push({x:xPix, y:nInf, l:"move"});
			} else if(xPix == pInf) {
				if(x0Pix != nInf && x1Pix != nInf && x0Pix != pInf && x1Pix != pInf && !isNaN(x0Pix) && !isNaN(x1Pix)) {
					dx0 = this.getDval(eqnObj, y0, acc / 10);
					//trace(y0+":::::"+dy0);
					if(x0Pix < midX && x0Pix > -midX) {
						if(dx0 > x0) {
							xcomp = xh;
						} else {
							xcomp = xl;
						}
					} else {
						xcomp = x0Pix;
					}
					//trace("P0:"+y0Pix+":"+xcomp+":"+yPix);
					ptsArr.push({
						y : y0Pix,
						x : xcomp,
						l : "line"
					});
					ptsArr.push({
						y : yPix,
						x : xcomp,
						l : "line"
					});
					ptsArr.push({
						y : yPix,
						x : -xcomp,
						l : "move"
					});
					//ptsArr.push({x:xPix, y:pInf, l:"move"});
					dx1 = this.getDval(eqnObj, y1, -acc / 10);
					if(x1Pix < xh && x1Pix > xl) {
						if(dx1 > x1) {
							xcomp = xh;
						} else {
							xcomp = xl;
						}
					} else {
						xcomp = x1Pix;
					}
					ptsArr.push({
						y : y1Pix,
						x : xcomp,
						l : "line"
					});

				} else {
					ptsArr.push({
						y : yPix,
						x : pInf,
						l : "move"
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
	if(ptsArr.length < 2) {
		if(buffrArr.length < 2) {
			if(this.validateFunc()) {
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
Plotter.prototype.plotNumberLine=function() {
	var pInf = this.pInf;
	var nInf = this.nInf;
	var gmc = this.context;
	this.setAxisDatas();
	if(this.fand) {
		return this.plotNumberLineAnd();
	}
	var eqn = this.fn;
	var eql = this.fneql;
	if(eqn == "" || eqn == undefined) {
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
	if(eqSy == "eq") {

	} else if(eqSy == "le") {
		xs = xpos - dx;
		xe = 0+10;
		ad = 'w';
	} else if(eqSy == "lt") {
		xs = xpos - dx;
		xe = 0+10;
		ad = 'w';
	} else if(eqSy == "ge") {
		xs = xpos + dx;
		xe = this.xh-10;
		ad = 'e';
	} else if(eqSy == "gt") {
		xs = xpos + dx;
		xe = this.xh-10;
		ad = 'e';
	}
	gmc.beginPath();
	gmc.moveTo(xs, ys);
	gmc.lineTo(xe, ye);
	gmc.stroke();
	gmc.closePath();
	//
	if(ad == 'e') {
		//!- arrow at east end
		gmc.beginPath();
		gmc.moveTo((this.xh), this.yaxis);
		gmc.lineTo((this.xh) - 10, this.yaxis + 4);
		gmc.lineTo((this.xh) - 10, this.yaxis - 4);
		gmc.lineTo((this.xh), this.yaxis);
		gmc.fill();
	} else if(ad == 'w') {
		//!- arrow at west end
		gmc.beginPath();
		gmc.moveTo((0), this.yaxis);
		gmc.lineTo((0) + 10, this.yaxis + 4);
		gmc.lineTo((0) + 10, this.yaxis - 4);
		gmc.lineTo((0), this.yaxis);
		gmc.fill();
	}
gmc.closePath();
}

Plotter.prototype.plotNumberLineAnd=function() {
	var pInf = this.pInf;
	var nInf = this.nInf;
	var gmc = this.context;
	this.setAxisDatas();
	this.xscale = this.fixTo(this.xscale, 8);
	var eqDatas = this.fn.split("_");
	var leqn = eqDatas[0];
	var reqn = eqDatas[1];
	if(leqn == "" || leqn == undefined || reqn == "" || reqn == undefined) {
		return null;
	}
	var eqSy=this.eqSy = this.fneql.split("_");
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
	x0=point0[0];
	x1=point1[0];
	if(x0 < x1) {
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
};

Plotter.prototype.checkAsym = function(x1, x2) {
	var asYm;
	for(var i = x1 + .01; i < x2; i = this.fixTo(i + .01, 2)) {
		asYm = this.getDval(this.eqnObj, i, 0);
		console.log("ASYM_CHECK:"+asYm);
		if(Math.abs(asYm) > 10000) {
			return i;
		}
	}
	return null;
}

Plotter.prototype.fixAsym = function(arr, p) {
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
	if(fof == "x") {
		dy0 = this.getDval(this.eqnObj, x0, .01);
		if(y0Pix < yh && y0Pix > yl) {
			if(dy0 > y0) {
				ycomp = yh;
			} else {
				ycomp = yl;
			}
		} else {
			ycomp = y0Pix;
		}
		arr.splice(l - 1, 0, {
			x : x0Pix,
			y : ycomp,
			l : "line"
		});
		arr.splice(l, 0, {
			x : asymX,
			y : ycomp,
			l : "line"
		});
		arr.splice(l + 1, 0, {
			x : asymX,
			y : -ycomp,
			l : "line"
		});
		dy1 = this.getDval(this.eqnObj, x1, -.01);
		if(y1Pix < yh && y1Pix > yl) {
			if(dy1 > y1) {
				ycomp = yh;
			} else {
				ycomp = yl;
			}
		} else {
			ycomp = y1Pix;
		}
		arr.splice(l + 2, 0, {
			x : x1Pix,
			y : ycomp,
			l : "move"
		});
	}
	if(fof == "y") {
		dx0 = this.getDval(this.eqnObj, y0, .01);
		if(x0Pix < xh && x0Pix > xl) {
			if(dx0 > x0) {
				xcomp = xh;
			} else {
				xcomp = xl;
			}
		} else {
			xcomp = x0Pix;
		}
		arr.splice(l - 1, 0, {
			y : y0Pix,
			x : xcomp,
			l : "line"
		});
		arr.splice(l, 0, {
			y : asymY,
			x : xcomp,
			l : "line"
		});
		arr.splice(l + 1, 0, {
			y : asymY,
			x : -xcomp,
			l : "line"
		});
		dx1 = this.getDval(this.eqnObj, y1, -.01);
		if(x1Pix < xh && x1Pix > xl) {
			if(dx1 > x1) {
				xcomp = xh;
			} else {
				xcomp = xl;
			}
		} else {
			xcomp = x1Pix;
		}
		arr.splice(l + 2, 0, {
			y : y1Pix,
			x : xcomp,
			l : "move"
		});
	}
}

Plotter.prototype.asymFix = function(arr) {
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
	for(var a = 0; a < arr.length; a++) {
		var pt = arr[a];
		var axX = (pt.x - xaxis) / xscale;
		var axY = (pt.y - yaxis) / yscale;
		nArr.push(arr[a]);
		if(a != 0) {
			if(fof == "x") {
				console.log("ASYM_SIGN:"+pPt.y+":"+pt.y);
				if(pPt&&(sign(pPt.y) != sign(pt.y))) {
					asymP = this.checkAsym(axPX, axX);
					console.log("ASYM_FIX:"+axX+":"+axPX);
					if(asymP != null) {
						this.fixAsym(nArr, asymP);
					}
				}
			}
			if(fof == "y") {

				if(pPt&&(sign(pPt.x) != sign(pt.x))) {
					asymP = this.checkAsym(axPY, axY);
					if(asymP != null) {
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

Plotter.prototype.hex2rgb = function(col, alp) {
	var r, g, b;
	var colour;
	var tc = this.context.strokeStyle;
	this.context.strokeStyle = col;
	colour = this.context.strokeStyle;
	this.context.strokeStyle = tc;
	if(colour.charAt(0) == '#') {
		colour = colour.substr(1);
	}
	r = colour.charAt(0) + '' + colour.charAt(1);
	g = colour.charAt(2) + '' + colour.charAt(3);
	b = colour.charAt(4) + '' + colour.charAt(5);
	r = parseInt(r, 16);
	g = parseInt(g, 16);
	b = parseInt(b, 16);
	if(alp!==undefined) {
		return "rgba(" + r + "," + g + "," + b + "," + alp + ")";
	} else {
		return "rgb(" + r + "," + g + "," + b + ")";
	}

}
Plotter.prototype.distance = function(p1, p2) {
	var x1 = p1.x;
	var x2 = p2.x;
	var y1 = p1.y;
	var y2 = p2.y;
	var sq = Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2);
	return Math.sqrt(sq);
}
Plotter.prototype.angle = function() {
	var var1 = {};
	var var2 = arguments;
	var var3 = arguments.length;
	var x1, x2, y1, y2;
	var pt = {};
	var dx, dy;
	if(var3 == 2) {
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
CanvasRenderingContext2D.prototype.dashTo = function(sx, sy, ex, ey, lW, gW, scope) {
	if(arguments.length < 6) {
		return;
	}
	var startPt = {
		x : sx,
		y : sy
	};
	var endPt = {
		x : ex,
		y : ey
	};
	var segLength = scope.distance(startPt, endPt);
	segLength = scope.fixTo(segLength, 2);
	var segAngle = scope.angle(startPt, endPt).ra;
	var dashLength = lW + gW;
	var oLen = 0;
	this.moveTo(sx, sy);
	var xp, yp, nseg, overFlow, deltax, deltay, __mx, __my, lx, ly;
	var buffer = false;
	xp = sx;
	yp = sy;
	//console.log(scope.overFlowObj.boo+":"+scope.overFlowObj.type+":"+scope.overFlowObj.len)
	if(scope.overFlowObj.boo) {
		oLen = scope.overFlowObj.len;
		buffer = (oLen - segLength) >= 0;
		if(buffer) {
			segL = segLength;
		} else {
			segL = scope.overFlowObj.len;
		}
		xp = sx + segL * Math.cos(segAngle);
		yp = sy + segL * Math.sin(segAngle);
		xp = scope.fixTo(xp, 2);
		yp = scope.fixTo(yp, 2);
		if(scope.overFlowObj.type == "gap") {
			this.moveTo(xp, yp);
			if(buffer) {
				scope.overFlowObj.boo = true;
				scope.overFlowObj.type = "gap";
				scope.overFlowObj.len = oLen - segLength;
				if(scope.overFlowObj.len < 0) {
					scope.overFlowObj.boo = !true;
					scope.overFlowObj.len = 0;
				}
				return;
			}
		} else {
			this.lineTo(xp, yp);
			if(buffer) {
				scope.overFlowObj.boo = true;
				scope.overFlowObj.type = "line";
				scope.overFlowObj.len = oLen - segLength;
				if(scope.overFlowObj.len < 0) {
					scope.overFlowObj.boo = !true;
					scope.overFlowObj.len = 0;
				}
				return;
			} else {
				segL = segLength - segL
				if(segL > gW) {
					oLen += gW;
					xp = xp + gW * Math.cos(segAngle);
					yp = yp + gW * Math.sin(segAngle);
					xp = scope.fixTo(xp, 2);
					yp = scope.fixTo(yp, 2);
					this.moveTo(xp, yp);
					scope.overFlowObj.type = "gap";
				} else {
					scope.overFlowObj.boo = true;
					scope.overFlowObj.type = "gap";
					scope.overFlowObj.len = gW - segL
					if(scope.overFlowObj.len <= 0) {
						scope.overFlowObj.boo = !true;
						scope.overFlowObj.len = 0;
					}
					return;
				}
			}
		}
	}
	segLength = scope.distance({
		x : xp,
		y : yp
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
	for(var n = 0; n < nseg; n++) {
		this.moveTo(__mx, __my);
		lx = __mx + Math.cos(segAngle) * lW;
		ly = __my + Math.sin(segAngle) * lW;
		lx = scope.fixTo(lx, 2);
		ly = scope.fixTo(ly, 2);
		this.lineTo(lx, ly);
		__mx += deltax;
		__my += deltay;
		remLength = scope.distance({
			x : __mx,
			y : __my
		}, endPt);
		scope.overFlowObj.boo = true;
		scope.overFlowObj.type = "gap";
	}
	if(overFlow >= 0.1) {
		if(!scope.overFlowObj.boo) {
			scope.overFlowObj.type = "gap";
		}
		scope.overFlowObj.boo = true;
		if(scope.overFlowObj.type == "gap") {
			scope.overFlowObj.type = overFlow >= lW ? "gap" : "line";
			scope.overFlowObj.len = scope.overFlowObj.type == "gap" ? gW - (overFlow - lW) : (lW - overFlow);
			if(scope.overFlowObj.type == "line") {
				this.moveTo(__mx, __my);
				lx = __mx + Math.cos(segAngle) * overFlow;
				ly = __my + Math.sin(segAngle) * overFlow;
				lx = scope.fixTo(lx, 2);
				ly = scope.fixTo(ly, 2);
				this.lineTo(__mx + Math.cos(segAngle) * overFlow, __my + Math.sin(segAngle) * overFlow);
			} else {
				this.moveTo(__mx, __my);
				lx = __mx + Math.cos(segAngle) * lW;
				ly = __my + Math.sin(segAngle) * lW;
				lx = scope.fixTo(lx, 2);
				ly = scope.fixTo(ly, 2);
				this.lineTo(__mx + Math.cos(segAngle) * lW, __my + Math.sin(segAngle) * lW);
			}
		} else if(scope.overFlowObj.type == "line") {
			scope.overFlowObj.type = overFlow >= gW ? "line" : "gap";
			scope.overFlowObj.len = scope.overFlowObj.type == "line" ? lW - (overFlow - gW) : (gW - overFlow);
			if(scope.overFlowObj.type == "line") {
				lx = __mx + Math.cos(segAngle) * overFlow;
				ly = __my + Math.sin(segAngle) * overFlow;
				lx1 = __mx + Math.cos(segAngle) * gW;
				ly1 = __my + Math.sin(segAngle) * gW;
				lx = scope.fixTo(lx, 2);
				ly = scope.fixTo(ly, 2);
				lx1 = scope.fixTo(lx1, 2);
				ly1 = scope.fixTo(ly1, 2);
				this.moveTo(lx1, ly1);
				this.lineTo(lx, ly);
			} else {
				this.moveTo(__mx, __my);
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
var EqParser = ( function() {
	var parser = {};
	parser.init = function() {
		console.log("EqParser --> PARSER_INITED!");
		this.operators = ["^", "*", "/", "+", "-"];
		this.a_arr = ["exp", "sinh", "cosh", "tanh", "csch", "sech", "coth", "arccsc", "arcsec", "arccot", "log", "abs", "e", "arcsin", "arccos", "arctan", "sin", "cos", "tan", "sqrt", "pi", "csc", "sec", "cot"];
		this.b_arr = ["S", "d", "e", "?", "?", "?", "?", "?", "µ", "?", "!", "@", "#", "$", "'", "_", "&", "|", "?", ";", "~", "a", "ß", "?"];
		this.ioperators = ["^", "*", "/", "+", "-", ","];
		this.m_fn = ["exp", "sinh", "cosh", "tanh", "csch", "sech", "coth", "arccsc", "arcsec", "arccot", "log", "abs", "arcsin", "arccos", "arctan", "sin", "cos", "tan", "sqrt", "pow", "csc", "sec", "cot"];
	}
	parser.convertTo_JSEq = function(str) {
		this.input_str = str;
		this.input_str = this.formatMulti();
		this.input_str = this.formatPow();
		return this.input_str;
	}
	parser.findandreplace = function(input, search, replace) {
		return input.split(search).join(replace);
	}
	parser.fandr = function(input, search, replace, ipos) {
		var my = input;
		var pos = my.indexOf(search, ipos);
		for(var i = ipos; i < my.length; i++) {
			if(pos == -1 || search == replace) {
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
	parser.formatMulti = function() {
		var my_str = this.input_str;
		my_str = my_str.split(" ").join("");
		var npos;
		var a_arr = this.a_arr;
		var b_arr = this.b_arr;
		for(var j = 0; j < a_arr.length; j++) {
			npos = my_str.indexOf(a_arr[j], 0);
			if(npos != -1) {
				my_str = this.findandreplace(my_str, a_arr[j], b_arr[j]);
			}
		}
		var brac = my_str.indexOf(")(", 0);
		if(brac != -1) {
			my_str = this.findandreplace(my_str, ")(", ")*(");
		}
		for(var i = 0; i < my_str.length; i++) {
			for(var j = 97; j <= 122; j++) {
				if(my_str.charCodeAt(i) == j) {
					for(var k = 97; k <= 122; k++) {
						if(my_str.charCodeAt(i - 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
						}
					}
					for(var k = 97; k <= 122; k++) {
						if(my_str.charCodeAt(i + 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
						}
					}
					for(var k = 48; k <= 57; k++) {
						if(my_str.charCodeAt(i - 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
						}
					}
					for(var k = 48; k <= 57; k++) {
						if(my_str.charCodeAt(i + 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
						}
					}
					if(my_str.charAt(i - 1) == ')') {
						my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
					}
					if(my_str.charAt(i + 1) == '(') {
						my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
					}
				}
			}
		}
		for(var i = 0; i < my_str.length; i++) {
			for(var j = 0; j < b_arr.length; j++) {
				if(my_str.charAt(i) == b_arr[j]) {
					for(var k = 97; k <= 122; k++) {
						if(my_str.charCodeAt(i - 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
						}
					}
					for(var k = 97; k <= 122; k++) {
						if(my_str.charCodeAt(i + 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
						}
					}
					for(var k = 48; k <= 57; k++) {
						if(my_str.charCodeAt(i - 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
						}
					}
					for(var k = 48; k <= 57; k++) {
						if(my_str.charCodeAt(i + 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
						}
					}
					if(my_str.charAt(i) == "~") {
						if(my_str.charAt(i - 1) == ')') {
							my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
						}
						if(my_str.charAt(i + 1) == '(') {
							my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
						}
						for(var k = 0; k < b_arr.length; k++) {
							if(my_str.charAt(i + 1) == b_arr[k]) {
								my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
							}
							if(my_str.charAt(i - 1) == b_arr[k]) {
								my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
							}
						}
					}
					if(my_str.charAt(i) == ";") {
						if(my_str.charAt(i - 1) == ')') {
							my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
						}
					}
				}
				if(my_str.charAt(i) == ")") {
					for(var k = 0; k < b_arr.length; k++) {
						if(my_str.charAt(i + 1) == b_arr[k]) {
							my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
						}
					}
				}
			}
		}
		for(var i = 0; i < my_str.length; i++) {
			for(var j = 48; j <= 57; j++) {
				if(my_str.charCodeAt(i) == j) {
					for(var k = 97; k <= 122; k++) {
						if(my_str.charCodeAt(i - 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
						}
					}
					for(var k = 97; k <= 122; k++) {
						if(my_str.charCodeAt(i + 1) == k) {
							my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
						}
					}
					if(my_str.charAt(i - 1) == ')') {
						my_str = this.fandr(my_str, my_str.charAt(i), '*' + my_str.charAt(i), i);
					}
					if(my_str.charAt(i + 1) == '(') {
						my_str = this.fandr(my_str, my_str.charAt(i), my_str.charAt(i) + '*', i);
					}
				}
			}
		}
		for(var j = 0; j < b_arr.length; j++) {
			npos = my_str.indexOf(b_arr[j], 0);
			if(npos != -1) {
				my_str = this.findandreplace(my_str, b_arr[j], a_arr[j]);
			}
		}
		return my_str;
	}
	parser.formatPow = function() {
		var equ = this.input_str;
		var ioperators = this.ioperators;
		var m_fn = this.m_fn;
		var rcount = 0;
		var lcount = 0;
		var rrcount = 0;
		var rlcount = 0;
		var pow_pos = equ.indexOf("^", 0);
		var neg, lt_str, rt_str, base, lbase, mflag, mlen, check_cp;
		for( i = 0; i < equ.length; i++) {
			if(pow_pos == -1) {
				break;
			}
			lt_str = equ.substr(0, pow_pos);
			rt_str = equ.substr(pow_pos + 1);
			if(rt_str.charAt(0) == "-") {
				rt_str = rt_str.substr(1, rt_str.length);
				neg = true;
			}
			if(lt_str.indexOf(")", lt_str.length - 1) != lt_str.length - 1) {
				for(var ij = lt_str.length - 1; ij >= 0; ij--) {
					for(var j = 0; j < ioperators.length; j++) {
						if(lt_str.charAt(ij) == ioperators[j]) {
							base = lt_str.substr(ij + 1, lt_str.length);
							lbase = lt_str.indexOf(0, ij);
							if(lt_str.indexOf("(", ij) != -1) {
								base = findandreplace(base, "(", "");
							}
							ij = -1;
							break;
						} else {
							if(lt_str.indexOf("(", ij) != -1) {
								base = lt_str.substr(ij + 1, lt_str.length);
								ij = -1;
								break;
							}
							if(ij == 0) {
								base = lt_str;
							}
						}
					}
				}
			} else {
				mflag = false;
				for(var ii = lt_str.length - 1; ii >= 0; ii--) {
					if(lt_str.charAt(ii) == ")") {
						rcount++;
					}
					if(lt_str.charAt(ii) == "(") {
						mlen = ii;
						for(var j = 0; j < m_fn.length; j++) {
							if(lt_str.substr(ii - m_fn[j].length, m_fn[j].length) == m_fn[j]) {
								mflag = true;
								mlen = ii - m_fn[j].length;
								break;
							}
						}
						lcount++;
					}
					if(lcount == rcount) {
						check_cp = lt_str.charAt(ii - 1);
						if(ii != 0 && check_cp != "(") {
							for(var iii = ii; iii >= 0; iii--) {
								for(var j = 0; j < ioperators.length; j++) {
									if(lt_str.charAt(iii) == ioperators[j]) {
										base = lt_str.substr(iii + 1, lt_str.length);
										lbase = lt_str.substr(0, lt_str_pos - 1);
										iii = -1;
										ii = -1;
										break;
									}
								}
							}
							if(mflag == true) {
								ii = mlen;
							}
							for(var iii = ii; iii >= 0; iii--) {
								for(var j = 0; j < m_fn.length; j++) {
									if(lt_str.indexOf(m_fn[j], iii) != -1) {
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
			for(var j = 0; j < m_fn.length; j++) {
				if(rt_str.indexOf(m_fn[j], 0) == 0) {
					pdl = rt_str.substr(0, m_fn[j].length);
					pdr = rt_str.substr(m_fn[j].length, rt_str.length);
					rt_str = pdr;
				}
			}
			if(rt_str.indexOf("(", 0) != 0) {
				for(var i = 0; i < rt_str.length; i++) {
					for(var j = 0; j < ioperators.length; j++) {
						if(rt_str.charAt(i) == ioperators[j]) {
							power = rt_str.substr(0, i);
							rpower = rt_str.substr(i + 1, rt_str.length - 1);
							i = rt_str.length;
							break;
						}
						if(i == rt_str.length - 1) {
							power = rt_str;
						}
					}
				}
				if(power.indexOf(")", 0) != -1) {
					// power = power.substr(0, power.indexOf(")"));
				}
			} else {
				for(var ii = 0; ii < rt_str.length; ii++) {
					if(rt_str.charAt(ii) == "(") {
						rlcount++;
					}
					if(rt_str.charAt(ii) == ")") {
						rrcount++;
					}
					if((rlcount == rrcount) && (rrcount != 0)) {
						check_p = rt_str.charAt(ii + 1);
						if(ii != (rt_str.length - 1) && check_p != ")") {
							for(var iii = ii; iii < rt_str.length; iii++) {
								for(var j = 0; j < ioperators.length; j++) {
									if(rt_str.charAt(iii) == ioperators[j]) {
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
			if(neg == true) {
				power = "-" + power;
			}
			var find = base + "^" + power;
			var replace = "pow(" + base + "," + power + ")";
			equ = this.fandr(equ, find, replace, 0);
			pow_pos = equ.indexOf("^", pow_pos);
		}
		return equ;
	}
	parser.evalEq = function(str, val) {
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
		var y=val;
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
			var estr, pstr, psub, psup, ba;

			if(isFinite(pchek)) {
				return pchek;
			} else {
				estr = inequ;
				estr = estr.split('^');
				pstr = estr[1].split('/');
				psub = parseInt(pstr[1]);
				psup = findandreplace(pstr[0], '(', "");

				if(psub % 2 == 1 && psup % 2 == 1) {
					if(ba < 0) {
						ba = -ba;
						pchek = -Math.pow(ba, p);
					} else {
						pchek = Number.NaN;
					}
				} else if(psub % 2 == 1 && psup % 2 == 0) {
					ba = -ba;
					pchek = Math.pow(ba, p);
				} else {
					pchek = Number.NaN;
				}
				return pchek;
			}
		}

		function log(x, base) {
			if(base == "" || base == undefined) {
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
/**
 * @author sathesh
 */

var Graph = function(doc, canvas_cont, graph_type, xmin, xmax, ymin, ymax, xinc, yinc, show_axis, show_axis_label, show_grid, show_half_grid, width, height) {
	//console.log(doc + ":" + canvas_cont + ":" + graph_type + ":" + xmin + ":" + xmax + ":" + ymin + ":" + ymax + ":" + xinc + ":" + yinc)
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
	this.show_axis = show_axis ? show_axis : true;
	this.show_axis_label = show_axis_label ? show_axis_label : true;
	this.show_grid = show_grid ? show_grid : true;
	this.show_half_grid = show_half_grid ? show_half_grid : false;
	//
	this.scaleX = this.width / ((Math.abs((this.xmax + this.xinc) - (this.xmin - this.xinc))) / this.xinc);
	this.scaleY = this.height / ((Math.abs((this.ymax + this.yinc) - (this.ymin - this.yinc))) / this.yinc);
	this.axisYpos = (this.width) - (((this.xmax + this.xinc) / this.xinc) * (this.scaleX));
	this.axisXpos = (this.height) - (((this.ymax + this.yinc) / this.yinc) * (this.scaleY));
	//
	this.canvas = this.document.createElement("canvas");
	this.canvas.width = this.width;
	this.canvas.height = this.height;
	this.board.appendChild(this.canvas);
	this.context = this.canvas.getContext('2d');
	this.canvas.style.position='absolute';
	this.canvas.style.top=0;
	this.canvas.style.left=0;	
	//
	
	this.drawGraph();
}
/**Drawing Methods*/
Graph.prototype.drawGraph = function() {

	/**Draw grid lines*/
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
	for( i = this.axisYpos; i <= this.width; i += this.scaleX) {
		scope.moveTo(i, grid_s);
		scope.lineTo(i, grid_len);
		if(alab > 0 && (i < this.width)) {
			label = alab * this.xinc;
			scope.fillText(label, i-label_dx, this.axisXpos + 12)
		}
		//console.log(i+":"+label)
		alab++;
	}
	//scope.stroke();
	alab = 0;
	for( i = this.axisYpos; i > 0; i -= this.scaleX) {
		scope.moveTo(i, grid_s);
		scope.lineTo(i, grid_len);
		if(alab > 0 && (i > 0)) {
			label = -alab * this.xinc;
			scope.fillText(label, i-label_dx, this.axisXpos + 12)
		}
		alab++;
	}
	if(this.graph_type=='xy'){
	alab = 0;
	for( i = this.axisXpos; i <= this.height; i += this.scaleY) {
		scope.moveTo(0, i);
		scope.lineTo(this.width, i);
		if(alab > 0 && (i < this.height)) {
			label = -alab * this.yinc;
			scope.fillText(label, this.axisYpos + 3, i)
		}
		alab++;
	}
	alab = 0;
	for( i = this.axisXpos; i > 0; i -= this.scaleY) {
		scope.moveTo(0, i);
		scope.lineTo(this.width, i);
		if(alab > 0 && (i > 0)) {
			label = alab * this.yinc;
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
     console.log(cn[i]);  
     cn[i].getContext('2d').clearRect(0, 0, this.width, this.height);
  }

}
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
 *    
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
				config.ymin, config.ymax,config.xinc, config.yinc);
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
				boo=true
				tarr.splice(g,1)
				break
			}
		}
		if(i==ex.length-1){
			op=""
		}else{
			op="*"
		}
		if(boo){
			str+="<b>"+ex[i]+"</b>"+op
		}else{
			str+=ex[i]+op
		}
	}
	str=str.split("*").join(" * ")
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
	cpf_n=cpf_n+" <b>("+n+" is prime)</b>";
	}
	if(dpf.length==1){
	cpf_d=cpf_d+" <b>("+d+" is prime)</b>";
	}
	if(epf&&epf.length==1){
	cpf_e=cpf_e+" <b>("+e+" is prime)</b>";
	}
	if(epf){
	return [cpf_n, cpf_d,npf, dpf, cpf,cpf_e,epf];
	}else{
	return [cpf_n, cpf_d,npf, dpf, cpf];
	}
}
/** END OF GLOBAL UTILITY METHODS*/
/**
* tempororily adding the flash card manager class for testing purpose
*/
var Flashcard_mngr=(function(){
    var mngr={};
    mngr.topic_id=0;
    mngr.current_pblm_index=0;
    mngr.completed_pblms=0;
    mngr.limit=3;
    mngr.initialized=false;
    mngr.quest_data={};
	mngr.current_quest=null;
	mngr.current_ans=null;
	console.log("FLASH_CARD_MNGR_INITIATED")
    mngr.init=function(id,lim){
	console.log("FLASH_CARD_MNGR_INITED FOR TOPIC ID:"+id)
	mngr.totalQuest=10;
		this.limit=lim?lim:this.limit;
        this.topic_id=id;
        this.initialized=true;
		console.log("FLASH_CARD_MNGR_CALLING GEN PROBLEMS FOR:"+id)
        this.genProblems();
        
    };
    mngr.reset=function(){
        this.initialized=false;
        this.current_pblm_index=0;
        this.completed_pblms=0;
    }
	mngr.getMultiplesDisplay=function(a,b){
	var str='';
		for(var i=1;i<b;i++){
		str+=a*i+',';
		}
		str+='<b>'+(a*b)+'</b>';
		return str;
	}
	mngr.getGCFExplain=function(num,den,em,gcf){
	var v1
	var n,d,e,g
	if(arguments.length==4){
	n=arguments[0];
	d=arguments[1];
	e=arguments[2];
	g=arguments[3];
	}else{
	n=arguments[0];
	d=arguments[1];
	e=null;
	g=arguments[2];
	}
		var pfData = Math.getCommonPrimeFactors(n, d, e,g);
		var neq = pfData[0];
		var deq = pfData[1];
		var eeq=pfData[5];
		var prodStr = pfData[4].length > 1 ? " = " + Math.getProduct(pfData[4]) : "";
		var geq = "<b>GCF = " + pfData[4].join(" * ") + prodStr + "</b>";
		var exp="To find the greatest common factor (GCF) of two or more numbers, first write out their prime factorizations.<br/>";
		var exp_sub="";
		if(g == 1){
			exp_sub="In this case there are no common prime factors. So,";
		}
		exp+="\t"+neq+"<br/>";
		exp+="\t"+deq+"<br/>";
		if(eeq){
		exp+="\t"+eeq+"<br/>";
		}
		exp+="The GCF is the product of all common factors. "+exp_sub+"<br/>";
		exp+="\t"+geq+".";
		return exp;
	}
mngr.getIndexArr=function(f){
	var l=this['arr_'+f].length;
	return Array.randomNumberArray(0,l-1,100,true);
}
mngr.random=function(n){
	return Math.floor(Math.random()*n)
	}
mngr.genMultiFracProb=function(type) {
	var ind, indT;
	var arr;
	switch (type) {
	case 'pp' :
		ind = ++this.pp_ind;
		indT = this.arr_pp_l;
		if (ind == indT) {
			ind = this.pp_ind=0;
			this.arr_pp_i = this.getIndexArr('pp');
		}
		break;
	case 'pw' :
		ind = ++this.pw_ind;
		indT = this.arr_pw_l;
		if (ind == indT) {
			ind = this.pw_ind=0;
			this.arr_pw_i = this.getIndexArr('pw');
		}
		break;
	case 'pi' :
		ind = ++this.pi_ind;
		indT = this.arr_pi_l;
		if (ind == indT) {
			ind = this.pi_ind=0;
			this.arr_pi_i = this.getIndexArr('pi');
		}
		break;
	case 'ii' :
		ind = ++this.ii_ind;
		indT = this.arr_ii_l;
		if (ind == indT) {
			ind = this.ii_ind=0;
			this.arr_ii_i = this.getIndexArr('ii');
		}
		break;
	case 'iw' :
		ind = ++this.iw_ind;
		indT = this.arr_iw_l;
		if (ind == indT) {
			ind = this.iw_ind=0;
			this.arr_iw_i = this.getIndexArr('iw');
		}
		break;
	}
	var arr = this['arr_'+type];
	var arri = this['arr_'+type+"_i"];
	var p = arr[arri[ind]].split("|");
	var l = Math.floor(Math.random()*2);
	var r = (l+1)%2;
	var pl = p[l];
	var pr = p[r];
	var strL = pl.split("/");
	var strR = pr.split("/");
	strL[1]=strL[1]?strL[1]:1;
	strR[1]=strR[1]?strR[1]:1;
	var n = strL[0]*strR[0];
	var d = (strL[1] ? strL[1] : 1)*(strR[1] ? strR[1] : 1);
	var ans1 = Math.simpleFrac(n+"/"+d);
	var nr = ans1.split("/");
	n = nr[0]*1;
	d = nr[1]*1;
	var ans2 = n>d ? "["+Math.floor(n/d)+"]"+((n%d)+"/"+d) : undefined;
	if (d === 1) {
		ans1 = String(n);
		ans2 = undefined;
	}
	var a = [[pl, pr,[strL,strR],nr], {frac:n+"/"+d,val:ans1}, {frac:ans2,val:ans2}];	
	return a
}
    mngr.genProblems=function(){
	console.log("FLASH_CARD_MNGR_CALLING GEN PROBLEMS - INSIDE METHOD")
	var totalQuest=mngr.totalQuest;
	var actType=mngr.actType;
	var level=mngr.level;
	var random=mngr.random;
	var rand=mngr.rand;
		if(this.topic_id==0){
			var gdata = [];
			gdata.randomNumbers(0, 12, 13, true)
			var i = 0;
			var l = gdata.length;
			var c = 0;
			var ldata = [];
			var rdata = [];
			while (gdata.length) {
				ldata[i] = gdata[c];
				rdata[i] = gdata[Math.floor(Math.random()*gdata.length)];
				gdata.shift();
				if (ldata[i] != rdata[i]) {
					ind = gdata.getIndex(rdata[i]);
					gdata.splice(ind, 1);
					gdata.unshift(rdata[i]);
				}                                                                         
				if (i>(20)) {
					break;
				}
				i++;
			}
			var _data = [];
			for (var i = 0; i<ldata.length; i++) {
				//console.log("MULTI_DATA:"+ldata[i]+":"+rdata[i])
				_data.push([[ldata[i], rdata[i]], ldata[i]*rdata[i]]);
			}
			_data.shuffle();
		}
	if(this.topic_id==1||this.topic_id==2||this.topic_id==6){
		var dataStr_like = "1/3 + 1/3,1/3 + 2/3,2/3 + 1/3,1/4 + 1/4,1/4 + 2/4,2/4 + 1/4,1/4 + 3/4,1/5 + 1/5,2/5 + 2/5,3/5 + 1/5,3/5 + 2/5,1/6 + 1/6,1/6 + 2/6,1/6 + 3/6,1/6 + 5/6,2/6 + 1/6,5/6 + 1/6,1/7 + 4/7,2/7 + 4/7,3/7 + 1/7,1/7 + 6/7,1/8 + 1/8,2/8 + 1/8,2/8 + 2/8,3/8 + 1/8,5/8 + 1/8,3/8 + 4/8,3/8 + 5/8,1/9 + 1/9,1/9 + 5/9,2/9 + 1/9,4/9 + 2/9,4/9 + 4/9,5/9 + 3/9,7/9 + 2/9,8/9 + 1/9,1/10 + 3/10,1/10 + 4/10,2/10 + 3/10,3/10 + 3/10,3/10 + 5/10,3/10 + 7/10,1/11 + 1/11,2/11 + 3/11,4/11 + 5/11,7/11 + 2/11,8/11 + 3/11,1/12 + 2/12,2/12 + 3/12,5/12 + 5/12,5/12 + 7/12";
		var dataStr_unlike="1/2 + 1/3,1/2 + 1/4,1/2 + 3/4,1/2 + 1/5,1/2 + 2/5,1/2 + 1/6,1/2 + 1/8,1/2 + 3/8,1/2 + 1/9,1/2 + 4/9,1/2 + 1/10,1/2 + 3/10,1/3 + 1/2,1/3 + 1/4,1/3 + 1/5,1/3 + 2/5,1/3 + 1/6,2/3 + 1/4,2/3 + 1/5,2/3 + 1/6,2/3 + 1/8,1/4 + 1/2,1/4 + 1/3,1/4 + 1/5,1/4 + 2/5,1/4 + 3/5,1/4 + 1/6,1/4 + 1/8,1/4 + 3/8,3/4 + 1/8,3/4 + 3/8,1/4 + 3/10,1/4 + 1/12,1/4 + 5/12,1/4 + 7/12,1/4 + 11/12,3/4 + 1/12,3/4 + 5/12,3/4 + 7/12,1/5 + 1/2,1/5 + 1/3,1/5 + 1/4,1/5 + 3/4,1/5 + 1/6,1/5 + 1/10,1/5 + 3/10,1/5 + 7/10,1/5 + 1/11,1/5 + 3/11,2/5 + 3/10,2/5 + 7/10,3/5 + 1/10,3/5 + 3/10,4/5 + 1/10,1/6 + 1/2,1/6 + 1/3,1/6 + 2/3,1/6 + 1/4,1/6 + 3/4,1/6 + 1/5,1/6 + 3/5,1/6 + 4/5,1/6 + 1/7,1/6 + 2/7,1/6 + 6/7,1/6 + 1/8,1/6 + 3/8,1/6 + 5/8,1/6 + 7/8,1/6 + 1/9,1/6 + 2/9,1/6 + 4/9,1/6 + 5/9,1/6 + 7/9,1/6 + 8/9,1/6 + 1/10,1/6 + 3/10,1/6 + 7/10,1/6 + 1/12,1/6 + 5/12,1/6 + 7/12,5/6 + 1/2,5/6 + 3/4,5/6 + 1/5,5/6 + 2/5,5/6 + 3/5,5/6 + 4/7,5/6 + 6/7,5/6 + 1/8,5/6 + 3/8,5/6 + 5/8,5/6 + 1/10,5/6 + 3/10,5/6 + 7/10,5/6 + 1/12,5/6 + 5/12,5/6 + 7/12,1/7 + 1/2,1/7 + 1/3,1/7 + 1/4,1/7 + 1/5,1/7 + 1/6,1/7 + 1/8,1/7 + 3/8,1/7 + 5/8,1/7 + 1/9,1/7 + 2/9,1/7 + 4/9,1/7 + 5/9,1/7 + 7/9,2/7 + 2/8,3/7 + 3/4,4/7 + 1/8,5/7 + 1/8,1/8 + 1/2,1/8 + 1/3,1/8 + 2/3,1/8 + 1/4,1/8 + 3/4,1/8 + 1/5,1/8 + 3/5,1/8 + 4/5,1/8 + 1/6,1/8 + 5/6,1/8 + 1/7,1/8 + 2/7,1/8 + 3/7,1/8 + 4/7,1/8 + 1/9 ,1/8 + 2/9,1/8 + 1/10,1/8 + 7/10,1/8 + 1/11,1/8 + 2/11,1/8 + 9/11,1/8 + 10/11,1/8 + 1/12,1/8 + 5/12,1/8 + 7/12,1/8 + 11/12,3/8 + 1/2,3/8 + 1/3,3/8 + 2/3,3/8 + 1/4,3/8 + 3/4,3/8 + 1/5,3/8 + 3/5,3/8 + 4/5,3/8 + 1/6,3/8 + 5/6,3/8 + 1/7,3/8 + 2/7,3/8 + 3/7,3/8 + 4/7,3/8 + 1/9 ,3/8 + 2/9,3/8 + 1/10,3/8 + 7/10,3/8 + 1/11,3/8 + 2/11,3/8 + 9/11,3/8 + 10/11,3/8 + 1/12,3/8 + 5/12,3/8 + 7/12,1/9 + 1/2,1/9 + 1/3,1/9 + 2/3,1/9 + 1/4,1/9 + 3/4,1/9 + 1/5,2/9 + 1/3,2/9 + 2/3,2/9 + 4/5,2/9 + 1/6,2/9 + 3/4,2/9 + 5/6,4/9 + 1/3,4/9 + 2/3,5/9 + 1/3,5/9 + 1/6,5/9 + 5/6,1/10 + 1/2,1/10 + 1/3,1/10 + 2/3,1/10 + 1/4,1/10 + 3/4,1/10 + 1/5,1/10 + 2/5,1/10 + 3/5,1/10 + 4/5,1/10 + 1/6,1/10 + 1/8,1/10 + 7/8,1/10 + 1/12,1/10 + 7/12,3/10 + 1/2,3/10 + 1/3,3/10 + 1/4,3/10 + 3/4,3/10 + 1/5,3/10 + 2/5,3/10 + 3/5,3/10 + 4/5,7/10 + 1/2,7/10 + 1/3,7/10 + 1/4,7/10 + 1/5,7/10 + 2/5,7/10 + 1/11,7/10 + 3/11,7/11 + 1/4,1/12 + 1/2,1/12 + 1/3,1/12 + 2/3,1/12 + 1/4,1/12 + 3/4,1/12 + 1/5,1/12 + 2/5,1/12 + 1/6,1/12 + 5/6,1/12 + 1/8,1/12 + 3/8,1/12 + 5/8,1/12 + 7/8,5/12 + 1/2,5/12 + 1/3,5/12 + 2/3,5/12 + 1/4,5/12 + 3/4,5/12 + 1/6,5/12 + 5/6,5/12 + 1/7,5/12 + 1/8,5/12 + 7/8,7/12 + 1/3,7/12 + 1/5,11/12 + 1/4,11/12 + 3/4,11/12 + 1/6,11/12 + 5/6,11/12 + 1/8,11/12 + 3/7";
		var dataStr_redfrac="6/8,22/55,24/96,2/5,3/20,12/28,26/39,6/15,14/35,200/250,8/10,75/300,3/9,19/30,2/7,28/40,5/6,4/17,5/8,9/11,7/12,8/15,15/18,6/10,9/21,44/60,32/80,27/99,45/100,38/100,42/50,10/25,9/14,16/36,16/28,6/12,9/18,6/24,15/20,32/40,17/34,23/31,7/51,39/52,50/90,36/120,108/120,70/77,28/100,1/16,3/25,44/48,32/56,24/54,20/64,48/60,54/72,36/45,49/70,32/44,150/225,80/340";
		var dataStr
		//dataStr=this.topic_id==1?dataStr_like:dataStr_unlike;
		switch (this.topic_id){
			case 1:
			dataStr=dataStr_like;
			break;
			case 2:
			dataStr=dataStr_unlike;
			break;
			case 6:
			dataStr=dataStr_redfrac;
			break;
		}
		var dataArr = dataStr.split(" ").join("").split(",");
		var _data = [];
		var ldata;
		var rdata;
		var dataEl;
		var elL,elR,ans,dans;
		for (var i = 0; i<dataArr.length; i++) {
			dataEl = this.topic_id==6?dataArr[i]:dataArr[i].split("+");
			ldata = this.topic_id==6?dataEl:dataEl[0];
			rdata = dataEl[1];
			elL=ldata.split("/");
			elR=rdata?rdata.split("/"):null;
			if (ldata != "" && ldata != undefined) {
			/*if(console){
			console.log("ADDFRAC_DATA:"+ldata+":"+rdata);
			}*/
			ans=this.topic_id==6?Math.simpleFrac(ldata):Math.addFrac(ldata, rdata);
			ans=this.topic_id==6?{frac:ans,val:ans}:ans;
			dans=(ans.val).split('/');
				_data.push([[ldata, rdata,[elL,elR],dans], ans]);
			}
		}
		_data.shuffle();
	}
       if(this.topic_id==3){
        var dataStr= "The result of an addition problem	sum,The result of a subtraction problem	difference,The result of a multiplication problem	product,The result of a division problem	quotient,The top number in a fraction	numerator,The bottom number in a fraction	denominator,A polygon with 5 sides	pentagon,A polygon with 6 sides	hexagon,A polygon with 8 sides	octagon,An angle measuring between 0 and 90 degrees	acute,An angle measuring between 90 and 180 degrees	obtuse,A triangle with two congruent sides	isosceles,A triangle with three congruent sides	equilateral,The distance from the center of a circle to the boundary of the circle	radius,The distance across a circle through its center	diameter,A quadrilateral with four congruent sides	rhombus,A quadrilateral with two pairs of parallel sides	parallelogram,A solid figure with two circular bases that are congruent and parallel	cylinder,A solid figure with 6 square faces	cube,A solid figure with 4 triangular faces	tetrahedron,A whole number with exactly two divisors	prime,A whole number with more than two divisors	composite";
		var dataArr = dataStr.split(",");
		var _data = [];
		var ldata;
		var rdata;
		var dataEl;
		for (var i = 0; i<dataArr.length; i++) {
		dataEl = dataArr[i].split("\t");
		ldata = dataEl[0];
		rdata = dataEl[1];
		cdata = ldata.split(" ").join("");
		if (cdata != "" && ldata != undefined) {
			_data.push([[ldata], rdata]);
		}
		}
		_data.shuffle();
       }
	   if(this.topic_id==10||this.topic_id==23){
	   mngr.arr_pp = ['1/11|7/12', '7/9|7/12', '4/7|7/8', '1/12|2/9', '1/10|4/11', '1/10|3/8', '1/4|1/5', '6/7|8/11', '1/2|9/11', '3/10|7/8', '3/8|3/10', '1/11|5/8', '2/7|5/6', '4/5|4/9', '3/4|4/11', '1/5|6/7', '1/4|1/8', '1/6|5/11', '3/4|8/9', '7/9|8/11', '6/11|9/10', '1/5|8/9', '1/6|7/11', '2/9|3/4', '3/4|5/11', '1/8|4/5', '7/12|8/11', '5/9|7/9', '3/5|8/9', '1/2|5/6', '1/3|4/11', '2/11|7/9', '1/2|3/11', '1/3|5/8', '3/11|5/8', '1/3|7/12', '2/9|6/11', '3/4|3/8', '2/5|3/5', '2/11|5/12', '4/11|5/8', '1/2|1/12', '6/11|8/9', '1/5|6/11', '4/9|6/11', '1/10|2/9', '1/9|2/11', '2/3|7/8', '2/3|3/10', '1/10|8/11', '1/7|7/8', '5/7|5/9', '2/5|5/12', '2/9|4/5', '1/6|9/11', '8/9|9/10', '1/5|1/6', '1/12|3/5', '2/11|9/10', '1/8|2/9', '1/5|3/10', '2/9|4/11', '3/7|11/12', '4/7|8/9', '3/8|5/9', '3/4|3/5', '2/3|11/12', '6/7|11/12', '3/5|3/11', '1/4|3/5', '2/3|4/11', '2/5|6/7', '1/9|9/10', '1/6|1/7', '1/10|5/8', '1/5|2/3', '1/2|8/11', '1/5|5/12', '1/4|3/4', '1/4|7/10', '5/7|8/11', '1/10|1/11', '6/11|7/12', '1/12|4/5', '5/8|9/10', '1/11|6/7', '5/9|9/10', '1/9|1/12', '5/9|7/10', '1/6|1/10', '1/12|7/12', '1/12|2/5', '1/10|7/8', '3/7|3/11', '2/9|3/11', '1/2|4/7', '1/5|3/4', '2/9|8/9', '3/11|9/10', '7/10|9/10', '1/7|1/8', '1/3|3/5', '7/11|8/9', '1/2|7/11', '5/9|7/12', '3/4|3/10', '1/6|7/10', '6/7|8/9', '1/4|3/10', '1/3|2/5', '5/7|7/10', '3/4|7/12', '1/2|2/7', '2/9|4/9', '5/12|11/12', '1/6|1/8', '1/11|2/5', '3/4|9/11', '5/6|5/12', '3/4|11/12', '1/8|5/11', '2/3|2/11', '3/10|4/5', '1/10|3/7', '1/12|9/11', '3/5|7/10', '1/5|7/9', '1/4|7/11', '7/9|9/10', '5/12|7/9', '1/8|7/12', '1/7|1/11', '2/7|3/5', '3/11|4/11', '1/11|4/11', '1/5|4/9', '1/5|3/7', '3/10|9/10', '1/6|3/4', '1/7|5/8', '1/11|5/7', '2/11|4/5', '2/11|3/11', '8/11|9/10', '1/11|1/12', '6/7|9/10', '2/11|7/12', '3/5|3/7', '5/12|7/12', '1/6|3/5', '1/10|11/12', '4/11|7/11', '2/5|3/4', '1/9|5/7', '3/11|4/7', '1/8|11/12', '1/12|5/6', '1/8|6/7', '1/9|5/12', '4/11|6/11', '2/7|5/7', '2/11|8/9', '5/7|5/12', '1/6|5/7', '1/12|3/8', '1/11|5/11', '1/12|8/11', '5/8|7/10', '5/6|8/11', '3/4|5/8', '2/7|5/8', '1/5|2/9', '1/2|1/11', '3/7|7/8', '3/5|3/10', '1/3|7/11', '1/6|5/8', '2/9|3/8', '3/8|4/11', '1/9|2/3', '3/11|5/7', '1/4|8/9', '4/9|7/9', '3/11|8/9', '3/11|5/12', '3/7|5/12', '1/11|7/11', '2/3|4/5', '1/11|9/10', '8/11|9/11', '2/11|5/11', '2/3|2/7', '1/3|8/9', '4/5|9/10', '4/7|5/11', '1/12|11/12', '2/3|7/10', '4/11|5/6', '1/11|2/7', '1/4|2/7', '5/8|6/7', '1/11|8/9', '1/8|9/11', '1/4|4/9', '4/7|5/8', '2/7|2/9', '3/8|4/9', '1/4|1/6', '2/9|3/10', '8/9|11/12', '1/2|7/8', '2/9|5/12', '3/4|8/11', '4/7|6/11', '1/12|4/9', '1/11|7/10', '5/8|7/9', '5/7|6/11', '1/7|5/11', '1/5|1/12', '1/7|2/5', '4/9|7/8', '1/10|9/10', '1/11|5/9', '8/9|9/11', '3/11|6/7', '1/5|1/10', '3/8|4/5', '2/7|6/7', '1/12|7/8', '2/5|3/10', '4/5|7/11', '3/4|7/8', '2/5|5/8', '1/5|9/10', '9/10|11/12', '4/5|7/9', '4/11|7/10', '3/7|4/11', '4/7|5/7', '1/5|1/11', '1/12|5/12', '5/6|7/10', '1/9|5/6', '1/8|3/11', '1/7|4/9', '3/5|9/11', '2/3|7/9', '1/11|5/6', '1/9|3/8', '1/8|4/9', '1/3|5/7', '1/5|1/7', '1/2|1/7', '7/12|8/9', '5/9|8/9', '5/8|7/12', '3/11|4/9', '1/2|1/5', '2/3|5/8', '3/5|6/11', '4/9|5/8', '2/5|2/11', '5/8|6/11', '1/2|1/10', '3/4|6/11', '1/5|2/5', '3/8|3/11', '3/4|5/9', '3/11|6/11', '5/8|9/11', '3/10|8/11', '2/9|7/10', '1/11|2/11', '1/2|3/8', '2/9|7/9', '2/5|11/12', '4/5|5/7', '2/3|9/11', '1/12|6/11', '3/5|3/8', '2/3|9/10', '1/2|4/9', '1/3|3/10', '3/5|4/7', '1/2|7/9', '3/10|7/10', '3/7|4/7', '1/11|4/7', '2/11|3/8', '2/11|3/5', '1/3|9/10', '3/7|9/11', '1/12|5/9', '4/9|8/9', '5/8|5/9', '3/11|7/8', '3/8|9/11', '1/7|5/9', '1/7|3/10', '1/7|9/11', '1/6|9/10', '3/11|7/9', '5/11|9/10', '3/5|7/9', '3/4|5/6', '2/3|3/5', '2/11|4/11', '1/8|3/8', '1/6|2/3', '1/8|4/7', '1/10|2/11', '4/9|4/11', '1/7|3/8', '2/5|5/11', '4/11|8/11', '1/5|7/8', '3/7|7/12', '2/7|4/5', '1/2|2/11', '3/7|5/9', '1/9|5/9', '1/3|1/4', '1/4|2/3', '2/5|7/12', '7/8|9/10', '3/7|7/11', '1/9|9/11', '1/3|7/10', '1/7|11/12', '1/8|6/11', '2/5|4/5', '5/7|8/9', '1/9|2/9', '1/10|2/7', '1/5|3/5', '3/10|11/12', '5/11|7/11', '3/8|8/9', '5/11|7/10', '5/7|7/12', '1/5|9/11', '1/7|7/10', '5/12|8/9', '5/11|5/12', '1/7|6/11', '7/10|7/11', '2/3|2/9', '2/11|11/12', '5/7|9/11', '4/5|9/11', '4/7|8/11', '1/3|3/7', '1/6|7/12', '1/5|7/11', '1/5|2/11', '1/2|8/9', '6/11|11/12', '5/6|9/10', '1/10|3/5', '2/7|5/9', '1/7|7/11', '4/7|5/12', '1/4|4/5', '5/6|5/11', '3/8|8/11', '1/2|4/5', '4/9|7/11', '3/10|7/12', '2/3|3/4', '1/12|7/10', '1/10|1/12', '1/7|3/11', '5/9|7/8', '3/10|6/7', '4/7|7/12', '1/3|8/11', '1/8|7/8', '3/5|5/11', '1/3|5/11', '6/7|7/10', '1/8|3/10', '1/6|5/12', '1/12|3/11', '1/2|4/11', '3/4|3/11', '1/6|2/11', '4/11|11/12', '2/5|4/7', '2/5|3/7', '3/5|5/7', '5/9|6/11', '3/5|7/8', '3/10|7/11', '2/7|3/8', '4/7|7/10', '1/12|3/7', '5/6|5/8', '1/9|7/12', '1/4|1/9', '5/9|11/12', '7/11|11/12', '5/6|8/9', '1/7|4/11', '5/8|8/9', '1/2|3/4', '1/3|7/9', '7/10|7/12', '1/3|2/3', '2/7|8/9', '5/12|6/7', '3/10|6/11', '3/10|5/12', '1/4|9/10', '3/11|5/9', '7/8|7/9', '7/8|9/11', '5/12|9/11', '3/7|7/10', '3/5|9/10', '1/6|4/11', '1/6|2/7', '4/9|9/11', '3/5|4/11', '1/7|2/7', '2/11|8/11', '1/3|2/7', '1/4|2/9', '2/3|2/5', '1/11|4/5', '1/6|8/11', '1/5|4/11', '2/7|7/8', '3/4|7/11', '2/7|2/11', '1/11|6/11', '1/4|2/5', '7/8|8/9', '1/10|5/6', '3/8|7/9', '5/6|7/8', '2/7|3/11', '5/11|11/12', '2/11|5/9', '1/7|5/12', '1/8|4/11', '4/9|7/10', '2/5|5/6', '4/11|9/10', '1/12|4/7', '1/8|7/11', '2/5|8/11', '4/11|9/11', '7/10|8/9', '1/10|2/5', '3/11|7/12', '6/11|7/10', '2/7|7/10', '1/5|7/12', '1/7|8/11', '1/11|9/11', '5/6|11/12', '1/8|1/10', '1/5|5/11', '3/8|7/11', '1/10|5/9', '7/11|8/11', '2/5|5/9', '1/7|4/7', '1/3|5/9', '4/5|5/9', '1/5|1/8', '2/5|9/10', '2/11|6/7', '4/11|5/12', '1/4|4/7', '5/6|5/9', '2/3|3/11', '1/6|1/11', '7/9|11/12', '1/7|2/3', '1/5|4/7', '1/7|1/10', '1/10|3/10', '7/11|7/12', '1/8|3/5', '3/11|11/12', '1/4|5/6', '1/3|4/5', '2/11|3/7', '3/7|3/8', '2/7|9/11', '2/7|4/9', '2/9|2/11', '4/5|6/7', '1/3|3/4', '1/2|6/7', '1/2|2/5', '1/3|1/12', '1/10|7/11', '2/9|4/7', '1/7|1/9', '1/7|8/9', '1/2|1/8', '7/9|7/10', '3/7|5/8', '7/9|7/11', '7/9|8/9', '1/9|8/11', '1/11|3/4', '2/11|7/8', '6/11|8/11', '2/11|5/6', '5/11|6/7', '5/9|7/11', '2/5|7/10', '1/9|4/9', '1/4|1/11', '1/3|3/11', '1/4|9/11', '6/7|7/11', '1/6|4/7', '1/4|6/7', '1/10|5/11', '3/7|9/10', '1/6|7/8', '1/4|7/8', '3/10|3/11', '1/4|5/7', '1/11|3/5', '4/9|5/11', '1/12|4/11', '2/3|5/12', '1/7|3/5', '1/2|1/9', '1/11|8/11', '2/7|3/7', '7/12|11/12', '1/8|5/12', '2/11|3/4', '1/5|1/9', '1/3|3/8', '1/3|1/10', '1/2|7/10', '1/12|2/11', '5/12|7/10', '7/10|9/11', '1/2|5/9', '3/10|9/11', '7/11|9/10', '2/11|3/10', '5/7|7/11', '3/7|4/5', '4/7|9/10', '2/3|7/12', '6/7|7/9', '1/9|7/11', '1/3|6/11', '1/6|3/10', '1/7|9/10', '1/9|3/10', '5/6|9/11', '1/4|5/11', '1/12|5/11', '3/8|6/7', '4/5|8/9', '1/3|1/11', '4/7|5/6', '2/9|5/11', '4/9|5/6', '1/2|2/3', '1/3|6/7', '1/7|2/11', '2/11|5/8', '1/10|7/12', '4/5|4/7', '3/11|4/5', '2/9|7/8', '5/12|7/11', '1/3|7/8', '1/4|1/12', '1/9|7/10', '5/6|6/11', '3/4|4/9', '3/4|5/12', '3/8|6/11', '4/7|7/9', '2/9|9/10', '1/3|1/8', '3/5|11/12', '5/8|5/11', '3/11|9/11', '1/9|4/7', '3/10|7/9', '1/9|8/9', '5/12|9/10', '1/12|7/9', '4/11|8/9', '2/9|3/7', '4/11|5/7', '1/3|1/7', '1/11|4/9', '1/12|5/7', '2/5|4/9', '3/8|9/10', '1/9|11/12', '1/3|2/11', '3/8|4/7', '1/10|8/9', '2/7|11/12', '4/9|9/10', '5/6|6/7', '2/7|8/11', '3/10|5/7', '1/5|5/9', '3/11|7/11', '1/5|7/10', '7/8|7/12', '1/9|3/11', '6/11|9/11', '1/9|7/8', '1/10|5/12', '4/7|7/11', '1/8|2/3', '2/5|3/11', '1/8|2/5', '1/5|3/8', '2/9|11/12', '1/4|7/9', '4/5|6/11', '1/4|8/11', '4/9|5/9', '6/7|6/11', '3/5|4/5', '3/8|7/12', '2/9|3/5', '4/7|9/11', '8/11|11/12', '2/11|4/7', '1/6|7/9', '1/5|3/11', '1/8|2/11', '5/11|7/9', '2/11|7/10', '3/4|9/10', '4/5|5/6', '2/11|7/11', '4/11|7/8', '1/8|1/12', '1/10|4/5', '6/11|7/11', '3/5|7/12', '1/8|1/9', '5/8|11/12', '4/11|5/11', '2/3|8/11', '2/5|4/11', '1/7|6/7', '3/11|8/11', '1/3|5/12', '3/7|5/7', '5/11|7/12', '1/4|5/9', '1/12|3/4', '1/11|2/9', '5/6|5/7', '1/8|5/9', '1/10|4/7', '3/8|5/6', '1/3|9/11', '3/5|4/9', '4/9|5/7', '1/9|2/7', '1/8|5/7', '1/8|7/9', '1/11|2/3', '1/12|3/10', '2/9|5/8', '1/11|3/7', '3/10|8/9', '3/4|3/7', '2/7|4/7', '3/5|5/12', '1/2|5/7', '2/3|6/7', '1/9|3/7', '1/12|2/7', '4/5|8/11', '3/4|4/7', '1/3|4/9', '1/11|3/10', '1/12|8/9', '1/4|11/12', '1/2|3/10', '1/3|5/6', '1/7|1/12', '3/10|4/11', '2/7|7/11', '1/8|5/8', '2/5|7/8', '3/10|4/9', '1/11|11/12', '7/10|11/12', '5/7|5/8', '1/4|1/10', '2/9|6/7', '1/9|7/9', '1/9|6/11', '3/5|5/6', '1/2|5/11', '4/7|4/9', '3/11|7/10', '1/10|3/11', '2/7|6/11', '2/3|7/11', '3/10|5/11', '3/8|5/12', '1/4|3/7', '6/11|7/9', '5/12|8/11', '7/12|9/11', '1/7|2/9', '1/3|11/12', '1/9|3/4', '3/8|5/11', '1/7|3/7', '2/5|7/11', '5/8|8/11', '5/7|7/9', '2/7|7/12', '1/2|1/4', '2/11|4/9', '3/5|5/8', '2/3|3/8', '1/7|4/5', '1/11|3/8', '1/2|1/6', '5/9|5/12', '2/7|7/9', '1/10|3/4', '1/6|3/11', '3/7|6/7', '1/6|6/11', '5/6|7/9', '2/5|5/7', '3/4|4/5', '1/8|8/9', '4/7|5/9', '1/2|5/8', '3/5|8/11', '3/7|8/9', '1/7|3/4', '1/7|5/7', '1/11|3/11', '3/7|6/11', '1/3|4/7', '7/11|9/11', '2/3|4/7', '3/4|6/7', '9/10|9/11', '2/3|3/7', '1/6|6/7', '1/8|3/4', '4/9|5/12', '3/10|4/7', '2/7|3/10', '1/8|9/10', '7/8|7/11', '6/7|9/11', '2/9|8/11', '2/7|3/4', '5/12|6/11', '1/8|2/7', '5/11|6/11', '5/12|7/8', '2/3|8/9', '1/4|5/8', '1/7|7/9', '1/10|9/11', '1/6|4/9', '6/11|7/8', '5/11|9/11', '1/10|7/10', '3/7|4/9', '1/2|9/10', '5/6|7/11', '1/5|5/7', '4/11|7/12', '4/5|5/11', '5/7|5/11', '1/9|4/5', '1/3|1/9', '1/10|6/11', '4/5|7/8', '4/5|5/12', '2/11|9/11', '3/8|5/8', '5/9|5/11', '1/9|3/5', '3/5|7/11', '2/7|5/12', '3/5|6/7', '1/2|2/9', '1/10|5/7', '1/8|1/11', '5/7|11/12', '2/5|7/9', '5/6|7/12', '4/7|6/7', '4/5|11/12', '1/4|7/12', '3/7|7/9', '3/5|5/9', '1/4|5/12', '1/5|5/6', '1/4|3/8', '5/11|7/8', '1/6|2/5', '2/7|4/11', '3/7|8/11', '1/2|3/5', '1/4|6/11', '1/6|4/5', '1/6|11/12', '2/5|3/8', '4/11|7/9', '1/12|6/7', '2/9|5/9', '1/6|5/9', '1/11|7/8', '1/12|7/11', '1/9|1/10', '1/6|3/8', '5/7|6/7', '5/11|8/11', '1/6|1/9', '1/10|4/9', '1/10|2/3', '3/10|5/8', '1/9|6/7', '5/11|8/9', '2/9|5/7', '2/5|6/11', '2/11|6/11', '3/7|5/11', '5/9|9/11', '1/11|5/12', '5/7|9/10', '1/2|3/7', '9/11|11/12', '1/10|7/9', '4/9|11/12', '2/5|9/11', '1/12|5/8', '5/9|6/7', '2/9|7/11', '1/2|5/12', '4/5|5/8', '1/9|5/11', '3/7|3/10', '2/9|7/12', '1/5|2/7', '1/9|1/11', '4/5|7/12', '3/8|7/10', '1/2|6/11', '3/11|5/6', '6/7|7/12', '7/8|11/12', '1/2|11/12', '4/9|8/11', '4/9|6/7', '2/3|5/9', '2/3|5/7', '7/9|9/11', '1/7|7/12', '1/6|3/7', '1/12|2/3', '1/8|5/6', '7/8|8/11', '4/5|4/11', '1/3|1/5', '6/7|7/8', '3/4|7/9', '4/7|4/11', '1/12|9/10', '3/4|5/7', '3/8|7/8', '1/4|3/11', '1/4|2/11', '5/8|5/12', '2/3|6/11', '1/3|1/6', '1/6|2/9', '1/9|5/8', '1/5|11/12', '7/12|9/10', '1/8|7/10', '1/4|4/11', '2/7|9/10', '2/5|8/9', '2/9|9/11', '1/8|8/11', '3/10|5/9', '1/5|8/11', '5/8|7/11', '2/11|5/7', '4/7|11/12', '4/5|7/10', '1/7|5/6', '2/5|2/9', '1/3|2/9', '3/11|5/11', '1/9|4/11', '1/6|5/6', '4/11|6/7', '2/7|5/11', '1/8|3/7', '2/5|2/7', '3/7|5/6', '1/2|1/3', '2/3|5/11', '4/11|5/9', '1/6|1/12', '2/3|4/9', '1/4|1/7', '7/10|8/11', '3/10|5/6', '1/10|6/7', '2/9|5/6', '3/4|7/10', '1/2|7/12', '7/8|7/10', '3/8|5/7', '8/9|8/11', '1/11|7/9', '5/7|7/8', '2/3|5/6', '1/6|8/9', '1/9|2/5', '1/5|5/8', '1/5|4/5', '5/9|8/11', '4/9|7/12', '3/8|11/12', '5/8|7/8'];
//
mngr.arr_pi=['3/5|11/2','1/2|7/3','2/5|11/2','1/2|6/5','1/2|5/4','3/4|11/2','2/3|7/3','1/6|9/2','3/4|7/2','1/2|9/5','3/5|3/2','2/3|5/3','1/2|9/4','2/3|11/2','1/6|11/2','2/3|11/4','4/5|11/2','1/5|3/2','2/5|5/2','3/5|7/2','1/2|11/3','1/3|8/3','1/4|4/3','2/3|8/3','1/6|3/2','2/3|7/2','1/3|4/3','1/4|3/2','1/3|11/4','2/3|3/2','1/2|5/2','1/3|5/4','1/4|9/2','5/6|3/2','2/3|11/3','1/2|5/3','1/2|7/5','1/4|8/3','1/3|11/2','3/4|3/2','1/3|3/2','2/3|7/4','1/5|7/2','3/4|9/2','1/2|7/4','2/5|3/2','1/2|8/3','1/2|12/5','1/2|11/6','4/5|7/2','2/3|9/2','1/5|11/2','1/2|8/5','4/5|3/2','1/2|3/2','2/5|7/2','3/4|4/3','1/4|11/2','1/3|7/3','1/2|7/6','5/6|9/2','3/4|11/3','1/2|11/5','1/3|11/3','1/2|4/3','2/3|9/4','3/5|5/2','5/6|5/2','1/3|7/4','3/4|7/3','3/4|5/3','1/3|9/2','1/4|5/2','1/4|5/3','4/5|5/2','1/2|11/2','1/2|9/2','1/6|5/2','2/5|9/2','1/3|5/3','1/4|11/3','3/5|9/2','1/3|9/4','1/4|7/2','1/2|7/2','2/3|5/2','2/3|5/4','3/4|8/3','3/4|5/2','1/5|9/2','5/6|11/2','2/3|4/3','1/3|5/2','1/2|11/4','1/6|7/2','4/5|9/2','5/6|7/2','1/3|7/2','1/4|7/3','1/5|5/2']
//
mngr.arr_pw=['5|7/10','5|1/6','1|8/9','3|9/10','6|2/9','5|3/5','5|1/4','3|9/11','7|3/11','9|3/8','1|2/9','7|1/9','11|3/5','8|5/9','1|5/6','2|8/11','9|5/9','4|5/6','1|2/11','2|2/9','1|5/7','7|8/9','11|5/12','3|7/12','8|2/3','1|7/11','5|1/5','10|9/10','2|3/8','2|1/6','6|1/7','9|1/3','8|1/12','8|6/11','3|7/11','4|1/7','4|7/12','7|6/11','4|1/2','5|7/11','3|3/5','3|2/11','12|5/12','4|8/9','11|8/11','8|1/10','1|7/8','12|3/4','8|6/7','2|2/3','4|5/12','6|4/9','7|5/8','8|5/11','6|7/11','6|1/8','4|1/8','9|3/11','8|1/11','6|1/5','9|5/11','6|1/4','10|5/6','2|3/5','8|1/8','7|3/8','5|1/8','6|6/7','9|1/5','7|1/6','5|5/11','2|4/5','2|5/6','11|7/9','12|7/10','2|7/11','6|3/10','4|1/12','8|3/5','6|2/5','9|4/5','6|1/11','12|4/11','9|11/12','4|1/11','12|5/9','2|1/10','7|4/7','4|5/7','12|2/9','5|1/10','2|3/4','9|2/5','3|1/5','4|2/5','5|2/11','5|5/8','8|3/8','11|9/10','12|1/12','4|5/9','8|2/5','9|6/11','6|7/12','1|7/10','11|8/9','10|1/6','11|7/8','5|1/9','7|3/5','12|8/11','4|4/11','1|1/4','10|2/9','1|1/8','1|3/7','11|5/9','10|1/11','3|4/5','1|3/4','7|4/9','12|5/8','6|5/8','3|1/12','9|5/8','4|9/11','7|1/11','11|5/6','7|9/11','10|8/11','9|1/9','5|6/7','1|2/7','9|8/9','3|2/7','6|5/9','10|7/10','11|1/6','2|5/12','8|5/7','12|2/3','7|1/10','12|1/4','12|5/7','11|7/11','2|9/10','3|1/4','8|3/10','5|3/11','2|8/9','9|1/7','10|2/3','8|2/7','9|1/4','12|1/9','3|2/5','3|3/4','7|11/12','12|1/11','8|8/9','12|1/3','2|2/11','1|7/12','3|1/9','6|8/9','9|2/9','9|5/12','8|4/7','5|4/7','3|5/8','12|6/7','1|5/11','11|3/7','8|3/11','11|1/7','8|4/5','3|4/11','11|1/10','2|4/11','5|3/10','10|1/5','5|9/10','4|1/4','9|8/11','1|6/7','4|9/10','2|3/11','7|7/12','2|1/2','6|1/9','8|1/3','1|8/11','8|5/8','10|8/9','6|3/7','6|9/10','12|3/10','8|11/12','12|5/6','11|2/7','9|3/7','3|7/10','1|1/9','8|1/2','12|1/7','6|3/8','9|3/4','4|7/9','7|1/3','1|5/9','11|1/5','5|8/9','12|4/7','5|8/11','6|5/11','6|11/12','1|11/12','3|4/7','1|1/11','6|7/9','4|6/7','7|2/5','11|5/7','6|1/6','7|4/5','7|6/7','10|4/7','5|3/7','3|1/2','8|7/8','11|4/9','1|1/7','10|3/5','10|1/9','4|3/11','9|1/11','10|2/7','11|11/12','7|1/4','5|1/11','2|11/12','12|2/5','2|6/11','1|2/5','7|5/12','7|1/2','7|1/7','11|7/10','5|4/9','11|1/8','3|1/6','10|1/7','11|3/8','3|1/7','4|2/3','10|6/11','3|1/10','10|1/4','11|2/11','12|4/5','6|1/3','5|1/2','7|2/11','3|3/10','4|4/5','9|3/5','12|9/10','1|3/10','3|2/3','1|4/7','11|1/3','3|6/7','12|1/5','2|1/9','3|5/7','8|7/10','7|4/11','12|4/9','4|1/9','8|1/4','3|1/11','5|4/5','1|5/8','10|6/7','10|7/8','9|1/6','9|2/3','10|5/7','1|1/3','11|4/7','11|1/12','2|1/7','11|3/11','4|3/4','9|3/10','8|3/7','7|5/11','5|2/9','4|1/6','4|5/8','9|1/10','3|5/12','12|11/12','11|4/5','1|9/11','5|5/6','9|1/2','10|5/11','7|3/7','11|5/8','7|1/5','5|1/7','2|1/5','3|1/3','9|7/10','2|5/9','12|3/7','5|2/7','12|8/9','3|1/8','2|6/7','10|3/4','10|7/11','9|5/7','3|11/12','9|2/11','5|6/11','6|3/5','7|7/10','11|6/7','12|7/8','9|6/7','6|4/5','2|1/11','4|11/12','9|7/9','10|1/3','10|9/11','2|4/7','6|5/6','7|1/8','10|5/12','9|4/11','1|4/11','6|1/12','12|7/9','7|3/10','10|1/12','8|2/11','7|3/4','11|2/9','6|3/11','2|3/7','5|7/12','2|5/8','3|4/9','2|7/12','10|3/10','9|1/12','1|1/5','8|4/9','3|7/8','8|4/11','8|2/9','8|5/12','8|9/11','8|5/6','1|9/10','11|6/11','12|1/8','7|2/3','2|1/8','4|4/9','7|5/9','4|1/10','8|7/12','9|7/8','8|1/6','1|1/6','12|3/5','3|7/9','2|1/12','11|2/3','6|4/7','6|4/11','8|1/9','10|3/8','12|7/12','11|7/12','6|9/11','12|1/6','1|3/5','10|1/8','1|3/11','9|9/11','7|7/8','4|5/11','1|1/10','2|7/10','2|7/9','3|8/9','1|5/12','1|2/3','10|1/2','8|3/4','5|3/4','12|5/11','9|5/6','12|2/7','8|7/9','4|7/11','4|7/10','10|3/11','3|5/9','11|9/11','8|1/7','11|1/4','12|6/11','11|2/5','5|5/9','2|1/4','7|5/6','6|3/4','8|7/11','6|5/7','4|2/9','10|5/8','2|5/7','2|7/8','2|9/11','9|4/7','4|3/5','10|11/12','10|4/5','3|5/11','7|7/11','5|2/5','5|1/12','3|6/11','9|7/12','11|4/11','1|3/8','6|6/11','5|9/11','10|2/11','4|3/7','3|3/11','4|3/8','11|3/4','1|4/5','12|3/11','4|6/11','4|2/7','12|2/11','9|1/8','9|4/9','6|5/12','3|3/8','2|2/7','7|9/10','1|1/12','5|2/3','2|1/3','5|11/12','10|4/11','9|9/10','2|4/9','7|7/9','1|1/2','5|3/8','7|8/11','3|8/11','12|1/10','2|5/11','9|2/7','10|1/10','10|2/5','11|5/11','5|1/3','7|2/9','1|7/9','6|7/8','3|5/6','5|4/11','3|3/7','4|1/3','4|2/11','3|2/9','11|1/2','7|5/7','4|3/10','10|5/9','6|1/10','4|4/7','4|8/11','5|5/12','2|3/10','9|7/11','5|7/8','6|2/7','4|1/5','5|7/9','8|9/10','6|1/2','2|2/5','12|7/11','7|2/7','1|4/9','10|4/9','12|9/11','6|2/11','8|1/5','11|3/10','11|1/11','7|1/12','10|7/9','12|3/8','6|8/11','6|7/10','4|7/8','6|2/3','12|1/2','1|6/11','10|7/12','5|5/7','8|8/11','11|1/9','10|3/7']
//
mngr.arr_ii=['11/2|11/6','5/2|9/2','8/3|11/3','7/2|4/3','11/3|5/4','9/2|7/6','4/3|5/4','11/3|9/4','3/2|11/2','5/2|11/3','8/3|7/4','5/2|11/5','11/2|8/5','11/2|5/4','11/3|7/4','11/2|6/5','7/2|11/5','3/2|6/5','9/2|7/5','3/2|4/3','5/3|7/4','3/2|11/4','3/2|9/2','3/2|7/2','3/2|9/4','7/2|7/5','5/2|7/3','11/3|11/4','7/2|5/3','5/3|9/4','9/2|11/5','7/2|7/4','7/2|8/5','9/2|7/4','9/2|11/6','5/2|8/3','5/2|11/6','7/2|7/6','9/2|4/3','5/2|5/4','7/3|11/3','5/2|4/3','5/3|11/3','8/3|9/4','4/3|9/4','7/2|11/4','9/2|9/4','11/2|7/4','5/3|11/4','3/2|11/6','3/2|9/5','7/3|7/4','5/2|11/4','7/2|11/6','9/2|9/5','4/3|7/4','7/3|9/4','3/2|11/3','3/2|8/5','3/2|5/3','7/3|11/4','9/2|8/5','11/2|7/6','5/2|7/5','3/2|5/4','9/2|5/3','5/2|7/4','7/3|5/4','5/2|5/3','4/3|7/3','9/2|8/3','5/3|7/3','5/3|8/3','7/2|8/3','11/2|4/3','11/2|5/3','7/2|6/5','3/2|7/6','3/2|7/4','3/2|12/5','5/2|8/5','7/2|5/4','4/3|11/3','5/3|5/4','7/2|9/5','3/2|7/5','5/2|9/5','5/2|12/5','7/2|7/3','8/3|11/4','11/2|9/5','3/2|11/5','9/2|7/3','3/2|7/3','7/2|12/5','8/3|5/4','4/3|8/3','5/2|6/5','11/2|7/5','9/2|5/4','9/2|6/5','4/3|5/3','9/2|12/5','5/2|7/2','5/2|7/6','3/2|5/2','5/2|9/4','7/2|9/4','4/3|11/4','3/2|8/3','7/3|8/3']
//
mngr.arr_iw=['6|8/5','6|11/9','7|11/7','1|11/10','1|7/6','2|8/3','2|11/2','5|11/10','2|11/6','3|11/5','1|5/4','5|3/2','1|12/11','5|8/7','1|7/4','4|12/11','2|7/6','3|8/5','5|5/4','1|9/8','6|7/5','1|8/3','8|5/4','6|5/4','6|11/7','3|11/3','7|9/7','3|4/3','2|3/2','3|8/3','4|11/6','2|12/11','6|11/10','4|5/4','5|9/7','2|9/2','2|12/5','8|11/8','8|9/7','10|6/5','10|11/10','4|11/8','1|11/2','7|8/5','1|9/7','6|11/8','3|12/11','3|3/2','8|3/2','4|12/7','3|11/4','10|12/11','3|5/3','5|11/6','2|7/3','8|4/3','3|7/4','5|7/5','2|11/4','7|5/4','3|12/5','3|8/7','4|7/5','4|9/4','5|11/9','4|11/10','2|5/3','1|5/2','5|12/5','6|7/4','8|8/7','4|8/7','4|7/6','2|9/4','5|7/4','1|7/3','1|11/7','4|5/2','5|9/4','2|11/10','5|7/3','4|5/3','10|7/6','3|9/8','9|11/10','5|6/5','4|7/3','5|11/5','7|5/3','9|9/8','8|11/10','4|11/9','9|12/11','9|5/4','6|12/7','5|7/6','3|5/4','4|9/7','1|11/4','6|4/3','4|11/7','3|12/7','9|11/9','4|7/4','1|7/5','1|3/2','7|11/8','9|4/3','2|7/2','1|11/5','4|3/2','2|11/5','1|11/8','3|5/2','1|6/5','7|12/11','4|9/8','6|9/7','3|11/9','2|11/3','7|8/7','2|9/7','5|11/7','4|11/5','2|5/4','1|5/3','10|9/8','7|6/5','5|9/5','1|12/5','5|12/11','1|11/3','4|6/5','2|12/7','6|9/5','3|11/7','5|8/5','2|8/5','6|6/5','4|12/5','5|5/3','3|11/8','6|7/6','1|11/9','1|9/2','7|11/10','7|7/5','1|8/5','7|7/6','4|8/3','3|11/6','3|6/5','1|11/6','6|8/7','7|12/7','2|11/8','8|11/9','6|9/8','3|11/10','10|8/7','1|9/5','8|7/6','2|11/7','3|7/3','1|7/2','9|8/7','3|7/5','2|5/2','2|6/5','3|7/6','7|9/8','8|12/11','2|11/9','3|9/5','4|9/5','4|11/4','2|8/7','7|4/3','1|4/3','5|12/7','2|7/4','6|12/11','5|4/3','3|9/7','9|7/6','8|6/5','7|3/2','9|9/7','2|9/8','3|9/4','4|8/5','8|9/8','1|8/7','7|11/9','11|12/11','1|12/7','5|11/8','3|7/2','2|9/5','6|3/2','1|9/4','2|7/5','6|5/3','4|4/3','8|7/5','9|6/5','5|9/8','6|11/6','2|4/3']
//
mngr.arr_pp_i=mngr.getIndexArr('pp')
mngr.arr_pw_i=mngr.getIndexArr('pw')
mngr.arr_pi_i=mngr.getIndexArr('pi')
mngr.arr_ii_i=mngr.getIndexArr('ii')
mngr.arr_iw_i=mngr.getIndexArr('iw')
//
mngr.arr_pp_l=mngr.arr_pp_i.length
mngr.arr_pw_l=mngr.arr_pw_i.length
mngr.arr_pi_l=mngr.arr_pi_i.length
mngr.arr_ii_l=mngr.arr_ii_i.length
mngr.arr_iw_l=mngr.arr_iw_i.length
//
mngr.pp_ind = 0;
mngr.pw_ind = 0;
mngr.pi_ind = 0;
mngr.ii_ind = 0;
mngr.iw_ind = 0;


	_data = [];
	var tarr1 = ['pp', 'pp', 'pp', 'pp', 'pp', 'pw', 'pw', 'pi', 'ii', 'iw'];
	var tarr2 = ['pw', 'pw', 'pw', 'pw', 'pw', 'iw', 'iw', 'pw', 'iw', 'iw'];
	var tarr=this.topic_id==10?tarr1:tarr2;
	for (var i = 0; i<10; i++) {
		_data.push(this.genMultiFracProb(tarr[i]));
	}
	_data.shuffle();
}
if(this.topic_id==14){
var m1 = Array.randomNumberArray(1, 20, 3, true);
	var m2 = Array.randomNumberArray(1, 20, 3, true);
	var n2 = Array.randomNumberArray(2, 20, 3, true);
	var m3 = Array.randomNumberArray(1, 20, 1, true)[0];
	var m4 = Array.randomNumberArray(1, 20, 1, true)[0];
	var n4 = Array.randomNumberArray(1, 20, 1, true)[0];
	var m5 = Array.randomNumberArray(1, 100, 1, true)[0];
	var m6 = Array.randomNumberArray(1, 9, 1, true)[0];
	var n6 = Array.randomNumberArray(2, 9, 1, true)[0];
	var k6 = Array.randomNumberArray(1, n6-1, 1, true)[0];
	var _data = [];
	var m, n, k;
	var ldata, rdata;
	var type;
	var random=mngr.random;
	var si=""
	for (var i = 0; i<mngr.totalQuest; i++) {
		if (i<3) {
			type = 1;
			m = m1[i];
			ldata = m;
			rdata = '1/'+m;
		}
		if (i>=3 && i<6) {
			type = 2;
			m = m2[i-3];
			n = n2[i-3];
			if (m%n == 0) {
				m = n2[i-3];
				n = m2[i-3];
			}
			if (m == n) {
				n = n+1;
			}
			ldata = Math.simpleFrac(m+"/"+n);
			rdata = Math.simpleFrac(n+"/"+m);
		}
		if (i == 6) {
			type = 3;
			ldata =m= -m3;
			rdata = "-1/"+m3;
		}
		if (i == 7) {
			type = 4;
			m = m4;
			n = n4;
			if (m%n == 0) {
				m = n4;
				n = m4;
			}
			if (m == n) {
				n = n+1;
			}
			si="-"
			ldata = "-"+(Math.simpleFrac(m+"/"+n));
			rdata = "-"+(Math.simpleFrac(n+"/"+m));
		}
		if (i == 8) {
			type = 5;
			if (m5%10 == 0) {
				m5 = m5+1;
			}
			m = [-1, 1][random(2)]*m5;
			ldata = m/10;
			if (m>0) {
				rdata = "10/"+m;
			} else {
				rdata = "-10/"+Math.abs(m);
			}
			rdata = Math.simpleFrac(rdata);
		}
		if (i == 9) {
			type = 6;
			var s = [-1, 1][random(2)];
			si = s == -1 ? "-" : "";
			m = m6;
			var f = Math.simpleFrac(k6+"/"+n6).split("/");
			n = f[1];
			k = f[0];
			ldata = si+m+"|"+(k+"/"+n);
			var m0 = (m*n)+(k*1);
			rdata = si+n+"/"+m0;
		}
		_data.push([[ldata,[si,m,n,k],type], {frac:rdata,val:rdata}]);
	}
	_data.shuffle();
}
if(this.topic_id==15){

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
	var random=mngr.random;
	for (var i = 0; i<mngr.totalQuest; i++) {
	
		var m, n, k, j, y, z;
		if (i == 0) {
			type = 1;
			k = k1[0];
			m = m1[0];
			n = n1[0];
			y = y1v[y1[0]];
			var dm = m == 1 ? "" : m;
			ldata = k+"("+dm+""+y+"+"+n+")";
			rdata = (k*m) == 1 ? "" : (k*m)+""+y+"+"+(k*n);
		}
		if (i>0 && i<=3) {
			type = 2;
			k = k2[i-1];
			m = m2[i-1];
			n = n2[i-1];
			k = k === 1 ? (random(3)+2) : k;
			switch (random(4)) {
			case 0 :
				break;
			case 1 :
				n = -n;
				break;
			case 2 :
				m = -m;
				break;
			case 3 :
				m = -m;
				n = -n;
				break;
			}
			//op = "*";
			y = y1v[y2[i-1]];
			op1 = k*n<0 ? "-" : "+";
			var km = (k*m) == 1 ? "" : (k*m);
			rdata = (km)+""+y+op1+Math.abs((k*n));
			k0 = k;
			if (n<0) {
				n0 = "("+n+")";
			} else {
				n0 = n;
			}
			m0 = m;
			op1 = "+";
			var dm = m0 == 1 ? "" : m0 == -1 ? "-" : m0;
			ldata = k0+"("+dm+""+y+"+"+n0+")";
		}
		if (i>3 && i<6) {
			type = 3;
			j = j3[i-4];
			k = k3[i-4];
			m = m3[i-4];
			n = n3[i-4];
			k = k === 1 ? random(3)+2 : k;
			switch (random(4)) {
			case 0 :
				op1 = "+";
				op2 = "+";
				break;
			case 1 :
				op1 = "+";
				op2 = "-";
				break;
			case 2 :
				op1 = "-";
				op2 = "+";
				break;
			case 3 :
				op1 = "-";
				op2 = "-";
				break;
			}
			//op = "*";
			y = y1v[y3[i-4]];
			z = z3v[z3[i-4]];
			k0 = k;
			j0 = j === 0 ? (random(5)+1) : j;
			m0 = m;
			n0 = n;
			var dm = m == 1 ? "" : m;
			var dj = j == 1 ? "" : j == -1 ? "-" : j;
			ldata = k0+"("+dj+""+y+op1+dm+""+z+op2+n0+")";
			var oop1, oop2;
			oop1 = op1;
			oop2 = op2;
			if (k*m<0) {
				op1 = op1 == "-" ? "+" : "-";
			}
			if (k*n<0) {
				op2 = op2 == "-" ? "+" : "-";
			}
			var km = (k*m) == 1 ? "" : (k*m) == -1 ? "" : Math.abs(k*m);
			var kj = (k*j) == 1 ? "" : (k*j) == -1 ? "-" : (k*j);
			rdata = (kj)+""+y+op1+(km)+""+z+op2+Math.abs(k*n);
		}
		if (i>=6) {
			switch (random(7)) {
			case 0 :
				type = 4;
				m = [100, 1000][random(2)];
				n = [2, 3][random(2)];
				k = [2, 3, 4, 5][random(4)];
				j = m-n;
				ldata = k+"("+j+")* = "+k+"("+m+"-"+n+")";
				rdata = k*j;
				break;
			case 1 :
				type = 5;
				m = [100, 1000][random(2)];
				n = [2, 3][random(2)];
				k = [2, 3, 4, 5][random(4)];
				j = m+n;
				ldata = k+"("+j+")* = "+k+"("+m+"+"+n+")";
				rdata = k*j;
				break;
			case 2 :
				type = 6;
				k = ['1/2', '1/3', '1/4'][random(3)];
				y = y1v[random(6)];
				if (k == '1/2') {
					m = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
					n = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
					var km = (m/2) == 1 ? "" : m/2;
					rdata = km+""+y+"+"+(n/2);
				}
				if (k == '1/3') {
					m = [3, 6, 9, 12, 15, 18][random(6)];
					n = [3, 6, 9, 12, 15, 18][random(6)];
					var km = (m/3) == 1 ? "" : m/3;
					rdata = km+""+y+"+"+(n/3);
				}
				if (k == '1/4') {
					m = [4, 8, 12, 16, 20][random(5)];
					n = [4, 8, 12, 16, 20][random(5)];
					var km = (m/4) == 1 ? "" : m/4;
					rdata = km+""+y+"+"+(n/4);
				}
				var dm = m == 1 ? "" : m;
				ldata = k+"("+dm+""+y+"+"+n+")";
				break;
			case 3 :
				type = 7;
				k = ['1/2', '1/3', '1/4'][random(3)];
				y = y1v[random(6)];
				if (k == '1/2') {
					m = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
					n = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20][random(10)];
					var km = (m/2) == 1 ? "" : m/2;
					rdata = km+""+y+"+"+(n/2);
				}
				if (k == '1/3') {
					m = [3, 6, 9, 12, 15, 18][random(6)];
					n = [3, 6, 9, 12, 15, 18][random(6)];
					var km = (m/3) == 1 ? "" : m/3;
					rdata = km+""+y+"+"+(n/3);
				}
				if (k == '1/4') {
					m = [4, 8, 12, 16, 20][random(5)];
					n = [4, 8, 12, 16, 20][random(5)];
					var km = (m/4) == 1 ? "" : m/4;
					rdata = km+""+y+"+"+(n/4);
				}
				var dm = m == 1 ? "" : m;
				ldata = "("+dm+""+y+"+"+n+")*"+k+"";
				break;
			case 4 :
				type = 8;
				k = [0.2, 0.5, 1.5][random(3)];
				y = y1v[random(6)];
				m = random(10)+1;
				n = random(10)+1;
				var km = (k*m) == 1 ? "" : (k*m);
				var dm = m == 1 ? "" : m;
				rdata = (km)+""+y+"+"+(k*n);
				ldata = k+"("+dm+""+y+"+"+n+")";
				break;
			case 5 :
				type = 9;
				k = [0.2, 0.5, 1.5][random(3)];
				y = y1v[random(6)];
				m = random(10)+1;
				n = random(10)+1;
				var km = (km) == 1 ? "" : (k*m);
				var dm = m == 1 ? "" : m;
				rdata = (k*m)+""+y+"+"+(k*n);
				ldata = "("+dm+""+y+"+"+n+")*"+k+"";
				break;
			case 6 :
				type = 10;
				k = z3v[random(6)];
				y = y1v[random(6)];
				m = random(10)+1;
				n = random(10)+1;
				var dm = m == 1 ? "" : m;
				rdata = dm+""+y+""+k+"+"+n+""+k;
				ldata = "("+dm+""+y+"+"+n+")*"+k+"";
				break;
			}
		}
		if (oop1) {
			op1 = oop1;
			op2 = oop2;
		}
		_data.push([[ldata, [op1, op2], [k, j, m, n, y, z], type], rdata]);
	}
	_data.shuffle();

}
	   if(this.topic_id==18){
	   var de_pair=["1/2","1/3","1/4","1/5","1/6","1/7","1/8","1/9","1/10","2/3","1/4","3/4","1/5","2/5","3/5","4/5","1/6","5/6","1/7","3/7","5/7","1/8","3/8","5/8","7/8","1/9","2/9","4/9","5/9","7/9","8/9","1/10","3/10","7/10","9/10","1/20","3/20","1/25","1/40","1/50","1/100"]
var gh_pair=["2/1","3/1","4/1","5/1","6/1","7/1","8/1","9/1","10/1","20/1","25/1","40/1","50/1","100/1","3/2","5/2","7/2","9/2","11/2","4/3","5/3","7/3","8/3","5/4","7/4","9/4","11/4","6/5","9/5","12/5","9/8","11/8"]
mngr.totalQuest=mngr.totalQuest?mngr.totalQuest:10;
var l1 = 4*mngr.totalQuest/10;
	var l2 = 3*mngr.totalQuest/10;
	var l3 = 3*mngr.totalQuest/10;
	l1 = Math.round(l1);
	l2 = Math.round(l2);
	l3 = mngr.totalQuest-(l1+l2);
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
	for (var i = 0; i<mngr.totalQuest; i++) {
		if (i<l1) {
			type = 1;
			ldata0 = ld1[i];
			ldata1 = rd1[i];
		}
		if (i>=l1 && i<l1+l2) {
			type = 2;
			ldata0 = ld2[i-l1];
			ldata1 = rd2[i-l1];
		}
		if (i>=l1+l2 && i<mngr.totalQuest) {
			type = 3;
			ldata0 = ld3[i-(l1+l2)];
			ldata1 = rd3[i-(l1+l2)];
		}
		s1 = ldata0.split("/");
		s2 = ldata1.split("/");
		ansn = s1[0]*s2[1]+"/"+s1[1]*s2[0];
		ans = Math.simpleFrac(ansn);
		var ans0 = ans.split("/");
		ans0[1]=ans0[1]==undefined?'1':ans0[1];
		rdata = ans0[1] == '1' ? ans0[0] : ans;
		ldata0 = s1[1] == '1' ? s1[0] : ldata0;
		ldata1 = s2[1] == '1' ? s2[0] : ldata1;
		_data.push([[ldata0, ldata1,[s1,s2],ans0,type], {frac:ansn,val:rdata,uans:ans}]);
		
	}
	_data.shuffle();
	   }
	   if(this.topic_id==8||this.topic_id==9){
		var ifracStr="1 1/2,2 1/2,3 1/2,4 1/2,5 1/2,6 1/2,7 1/2,8 1/2,9 1/2,10 1/2,1 1/3,1 2/3,2 1/3,2 2/3,3 1/3,3 2/3,4 1/3,4 2/3,5 1/3,5 2/3,6 1/3,6 2/3,7 1/3,7 2/3,8 1/3,8 2/3,9 1/3,9 2/3,10 1/3,10 2/3,1 1/4,1 3/4,2 1/4,2 3/4,3 1/4,3 3/4,4 1/4,4 3/4,5 1/4,5 3/4,6 1/4,6 3/4,7 1/4,7 3/4,8 1/4,8 3/4,9 1/4,9 3/4,10 1/4,10 3/4,1 1/5,1 2/5,1 3/5,1 4/5,2 1/5,2 2/5,2 3/5,2 4/5,3 1/5,3 2/5,3 3/5,3 4/5,4 1/5,4 2/5,4 3/5,4 4/5,5 1/5,5 2/5,5 3/5,5 4/5,6 1/5,6 2/5,6 3/5,6 4/5,7 1/5,7 2/5,7 3/5,7 4/5,8 1/5,8 2/5,8 3/5,8 4/5,9 1/5,9 2/5,9 3/5,9 4/5,10 1/5,10 2/5,10 3/5,10 4/5,1 1/6,1 5/6,2 1/6,2 5/6,3 1/6,3 5/6,4 1/6,4 5/6,5 1/6,5 5/6,6 1/6,6 5/6,7 1/6,7 5/6,8 1/6,8 5/6,9 1/6,9 5/6,10 1/6,10 5/6,1 1/7,1 2/7,1 3/7,1 4/7,1 5/7,1 6/7,2 1/7,2 2/7,2 3/7,2 4/7,2 5/7,2 6/7,3 1/7,3 2/7,3 3/7,3 4/7,3 5/7,3 6/7,4 1/7,4 2/7,4 3/7,4 4/7,4 5/7,4 6/7,5 1/7,5 2/7,5 3/7,5 4/7,5 5/7,5 6/7,6 1/7,6 2/7,6 3/7,6 4/7,6 5/7,6 6/7,7 1/7,7 2/7,7 3/7,7 4/7,7 5/7,7 6/7,8 1/7,8 2/7,8 3/7,8 4/7,8 5/7,8 6/7,9 1/7,9 2/7,9 3/7,9 4/7,9 5/7,9 6/7,10 1/7,10 2/7,10 3/7,10 4/7,10 5/7,10 6/7,1 1/8,1 3/8,1 5/8,1 7/8,2 1/8,2 3/8,2 5/8,2 7/8,3 1/8,3 3/8,3 5/8,3 7/8,4 1/8,4 3/8,4 5/8,4 7/8,5 1/8,5 3/8,5 5/8,5 7/8,6 1/8,6 3/8,6 5/8,6 7/8,7 1/8,7 3/8,7 5/8,7 7/8,8 1/8,8 3/8,8 5/8,8 7/8,9 1/8,9 3/8,9 5/8,9 7/8,10 1/8,10 3/8,10 5/8,10 7/8,1 1/9,1 2/9,1 4/9,1 5/9,1 7/9,1 8/9,2 1/9,2 2/9,2 4/9,2 5/9,2 7/9,2 8/9,3 1/9,3 2/9,3 4/9,3 5/9,3 7/9,3 8/9,4 1/9,4 2/9,4 4/9,4 5/9,4 7/9,4 8/9,5 1/9,5 2/9,5 4/9,5 5/9,5 7/9,5 8/9,6 1/9,6 2/9,6 4/9,6 5/9,6 7/9,6 8/9,7 1/9,7 2/9,7 4/9,7 5/9,7 7/9,7 8/9,8 1/9,8 2/9,8 4/9,8 5/9,8 7/9,8 8/9,9 1/9,9 2/9,9 4/9,9 5/9,9 7/9,9 8/9,10 1/9,10 2/9,10 4/9,10 5/9,10 7/9,10 8/9,1 1/10,1 3/10,1 7/10,1 9/10,2 1/10,2 3/10,2 7/10,2 9/10,3 1/10,3 3/10,3 7/10,3 9/10,4 1/10,4 3/10,4 7/10,4 9/10,5 1/10,5 3/10,5 7/10,5 9/10,6 1/10,6 3/10,6 7/10,6 9/10,7 1/10,7 3/10,7 7/10,7 9/10,8 1/10,8 3/10,8 7/10,8 9/10,9 1/10,9 3/10,9 7/10,9 9/10,10 1/10,10 3/10,10 7/10,10 9/10";
		var obj;
		var ifracArr= ifracStr.split(",");
		var l = ifracArr.length;
		var d;
		var _data= [];
		console.log("FLASH_CARD_MNGR_CALLING GEN PROBLEMS - topic "+this.topic_id+" "+l)
		for (var i = 0; i<l; i++) {
			d = ifracArr[i].split(" ");
			obj = {};
			obj.q = Number(d[0]);
			d = d[1].split("/");
			obj.num = Number(d[0]);
			obj.den = Number(d[1]);
			obj.improper = ifracArr[i];
			obj.frac = ((obj.q*obj.den)+obj.num)+"/"+obj.den;
			obj.deci = ((obj.q*obj.den)+obj.num)/obj.den;
			var dans=obj.frac.split("/")
			var sans=Math.simpleFrac(obj.frac).split("/")
			var mixedStr="["+obj.q+"]"+obj.num+"/"+obj.den;
			if(this.topic_id==8){
			_data.push([[[obj.q,obj.num,obj.den],dans,sans,obj.improper],{frac:obj.frac,val:obj.deci,sval:obj.deci}]);
			}else{
			_data.push([[dans,[obj.q,obj.num,obj.den],obj.improper],{frac:obj.frac,val:mixedStr,sval:obj.deci}]);
			}
		}
		_data.shuffle();
	   }
	   
	   if(this.topic_id==26){
		var l = mngr.totalQuest;
		//var l1 = 4*mngr.totalQuest/10;
		//var l2 = 3*mngr.totalQuest/10;
		//var l3 = 3*mngr.totalQuest/10;
		var g = [1, 1, 1, 1, 1, 2, 3, 4, 5, 1, 1, 6, 7, 8, 9, 10, 1, 1];
		var qstr="Find the greatest common factor of "
		var andStr=" and "
		var _data = [];
		var rand=Math.rand;
		console.log("GENERATING_PROBLEMS_FOR_TOPIC: "+this.topic_id)
		for (var i = 0; i<10; i++) {
			var _ldata;
			var _rdata = [];
			var p, q, r;
			var gr = g[Math.floor(Math.random(g.length))];
			if (gr == 1) {
				p = rand(1, 30);
			} else {
				p = rand(1, 10)*gr;
			}
			if (gr == 1) {
				q = rand(1, 30);
			} else {
				q = rand(1, 10)*gr;
			}
			if (p == q) {
				q = p+rand(2, 10);
			}
			if (i>7) {
				if (gr == 1) {
					r = rand(1, 30);
				} else {
					r = rand(1, 10)*gr;
				}
			}
			if (p == r || q == r) {
				r = p+q;
			}
			_ldata = i<8 ? [p, q] : [p, q, r];
			if (_ldata.length == 2) {
				
				_rdata[0] = Math.getGCD(p, q);
				
				_rdata[1] = 2;
				_rdata[2] = qstr+String(p)+andStr+String(q)+".";
			} else {
				
				_rdata[0] = Math.getGCD(Math.getGCD(p, q), r);
				_rdata[1] = 3;
				_rdata[2] = qstr+String(p)+", "+String(q)+","+andStr+String(r)+".";
			}
			_data.push([[_ldata,_rdata[2],_rdata[1]],_rdata[0]]);
		}
		_data.shuffle()
	  }
	  if(this.topic_id==11){

var t1 = Math.round((4/5)*totalQuest);
	t1 = actType == 'div' ? totalQuest : t1;
	var t2 = totalQuest-t1;
	var mdata = Array.randomNumberArray(1, 12, t1, true);
	var ndata = Array.randomNumberArray(1, 12, t1, true);
	var i = 0;
	var l = mdata.length;
	var c = 0;
	var ldata;
	var rdata;
	var l, m, n;
	var action = ["*", "÷"];
	if (actType == 'multi') {
		action = ["*", "*"];
	}
	if (actType == 'div') {
		action = ["÷", "÷"];
	}
	var op;
	var _data = [];
	for (; c<mdata.length; c++) {
		m = mdata[c];
		n = ndata[c];
		switch (random(3)) {
		case 0 :
			m = -m;
			break;
		case 1 :
			n = -n;
			break;
		case 2 :
			m = -m;
			n = -n;
			break;
		}
		op = action[random(2)];
		if (n<0) {
			n0 = "("+n+")";
		} else {
			n0 = n;
		}
		if (m<0) {
			m0 = "("+m+")";
		} else {
			m0 = m;
		}
		ldata = op == "*" ? m+op+n0+"" : (m*n)+op+m0;
		rdata = op == "*" ? m*n : n;
		_data.push([[[m, n], op, ldata], rdata]);
	}
	mdata = Array.randomNumberArray(1, 5, t2, true);
	ndata = Array.randomNumberArray(1, 5, t2, true);
	odata = Array.randomNumberArray(1, 5, t2, true);
	for (var d = 0; d<mdata.length; d++) {
		m = mdata[d];
		n = ndata[d];
		o = odata[d];
		switch (random(9)) {
		case 0 :
			m = -m;
			break;
		case 1 :
			n = -n;
			break;
		case 2 :
			o = -o;
			break;
		case 3 :
			m = -m;
			n = -n;
			break;
		case 4 :
			m = -m;
			o = -o;
			break;
		case 5 :
			n = -n;
			o = -o;
			break;
		default :
			m = -m;
			n = -n;
			o = -o;
		}
		op = "*";
		if (n<0) {
			n0 = "("+n+")";
		} else {
			n0 = n;
		}
		if (o<0) {
			o0 = "("+o+")";
		} else {
			o0 = o;
		}
		ldata = m+op+n0+op+o0;
		rdata = m*n*o;
		_data.push([[[m, n, o], op,ldata], rdata]);
	}
	_data.shuffle()
}
if(this.topic_id==12){
var t1 = Math.round((4/5)*totalQuest);
	var t2 = totalQuest-t1;
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
	for (; c<mdata.length; c++) {
		m = mdata[c];
		n = ndata[c];
		op = action[random(2)];
		var rr = actType == 'add' ? random(3) : random(4);
		switch (rr) {
		case 0 :
			m = -m;
			break;
		case 1 :
			n = -n;
			break;
		case 2 :
			m = -m;
			n = -n;
			break;
		case 3 :
			m = Math.min(m, n);
			n = Math.max(mdata[c], ndata[c]);
			op = "-";
			if (m-n>0) {
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
		if (n<0) {
			n0 = "("+n+")";
		} else {
			n0 = n;
		}
		if (m<0) {
			m0 = "("+m+")";
		} else {
			m0 = m;
		}
		ldata = m+op+n0;
		rdata = op == "+" ? m+n : m-n;
		_data.push([[[m, n], [op], ldata], rdata]);
	}
	mdata = Array.randomNumberArray(-20, 20, t2, true);
	ndata = Array.randomNumberArray(-20, 20, t2, true);
	odata = Array.randomNumberArray(-20, 20, t2, true);
	for (var d = 0; d<mdata.length; d++) {
		m = mdata[d];
		n = ndata[d];
		o = odata[d];
		m = m === 0 ? ([1, -1][random(2)]) : m;
		n = n === 0 ? ([1, -1][random(2)]) : n;
		o = o === 0 ? ([1, -1][random(2)]) : o;
		if (m>0 && n>0 && o>0) {
			n = -n;
		}
		op1 = "+";
		op2 = "+";
		var rr = actType == 'sub' ? random(3)+1 : actType == 'add' ? 0 : random(4);
		switch (rr) {
		case 0 :
			op1 = "+";
			op2 = "+";
			rdata = m+n+o;
			break;
		case 1 :
			op1 = "+";
			op2 = "-";
			rdata = m+n-o;
			break;
		case 2 :
			op1 = "-";
			op2 = "+";
			rdata = m-n+o;
			break;
		case 3 :
			op1 = "-";
			op2 = "-";
			rdata = m-n-o;
			break;
		}
		//op = "*";
		if (n<0) {
			n0 = "("+n+")";
		} else {
			n0 = n;
		}
		if (o<0) {
			o0 = "("+o+")";
		} else {
			o0 = o;
		}
		ldata = m+op1+n0+op2+o0;
		_data.push([[[m, n, o], [op1, op2],ldata ], rdata]);
	}
}
if(this.topic_id==13){
function getDeciDataType(t) {
	var m, n, s1, e1, s2, e2, d1, d2, op,mp;
	switch (t) {
	case 1 :
		s1 = s2=1;
		e1 = e2=99;
		d1 = d2=10;
		op = "+";
		break;
	case 2 :
		s1 = 50;
		s2 = 1;
		e1 = 99;
		e2 = 49;
		d1 = d2=10;
		op = "-";
		break;
	case 3 :
		s1 = 1;
		s2 = 1;
		e1 = 999;
		e2 = 999;
		d1 = d2=100;
		op = "+";
		break;
	case 4 :
		s1 = 500;
		s2 = 1;
		e1 = 999;
		e2 = 499;
		d1 = d2=100;
		op = "-";
		break;
	case 5 :
		s1 = 1;
		s2 = 1;
		e1 = 9;
		e2 = 99;
		d1 = 100;
		d2 = 1000;
		op = "+";
		break;
	case 6 :
		s1 = 1;
		s2 = 1;
		e1 = 9;
		e2 = 9;
		d1 = 10;
		d2 = 100;
		op = "-";
		break;
	case 7 :
		s1 = 1;
		s2 = 1;
		e1 = 99;
		e2 = 999;
		d1 = 100;
		d2 = 1000;
		op = "+";
		break;
	case 8 :
		s1 = 30;
		s2 = 1;
		e1 = 99;
		e2 = 299;
		d1 = 100;
		d2 = 1000;
		op = "-";
		break;
	case 9 :
		s1 = 10;
		s2 = 1;
		e1 = 99;
		e2 = 999;
		d1 = 10;
		d2 = 10000;
		op = "+";
		break;
	case 10 :
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
	m = m[0]/d1;
	n = n[0]/d2;
	var m1 = m;
	if (t == 5 || t == 7 || t == 9) {
		if (random(2)) {
			m = n;
			n = m1;
		}
	}
	var mDeci = String(m).indexOf(".")>-1;
	var nDeci = String(n).indexOf(".")>-1;
	if (!mDeci && !nDeci) {
		m = m+(1/d1);
	}
	var ls = String(m).split(".")[1];
	mp = ls ? ls.length : 1;
	var pow = Math.pow(10, mp);
	m = Math.round(m*pow)/pow;
	ls = String(n).split(".")[1];
	np = ls ? ls.length : 1;
	pow = Math.pow(10, np);
	n = Math.round(n*pow)/pow;
	return {m:m, n:n, op:op};
}
var type, ldata, rdata,mp;
for(var i=0;i<totalQuest;i++){
	type = random(10)+1;	
	var _data = [];
	var t1 = getDeciDataType(type);
	ldata = t1.m+t1.op+t1.n;
	rdata = t1.op == "+" ? t1.m+t1.n : t1.m-t1.n;
	var ls = String(rdata).split(".")[1];
	mp = ls ? ls.length : 1;
	var pow = Math.pow(10, mp);
	rdata = Math.round(rdata*pow)/pow;
	_data.push([[ [t1.m, t1.n],[t1.op],ldata], rdata]);
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
if(this.topic_id==19){
var k, m, n;
	var _data = [];
	var ldata, rdata;
	var l1 = 5;
	var l2 = 2;
	var l3 = 3;
	var ind, pind;
	var type;
	var t1ma = Array.randomNumberArray(2, 9, 2, true);
	var t1mb = Array.randomNumberArray(2, 15, l1-2, true);
	var t1mc = Array.randomNumberArray(2, 5, l1-2, true);
	var t2n = Array.randomNumberArray(5, 75, l2, true);
	t2n.sort(16);
	var t3na = Array.randomNumberArray(4, 8, l3, true);
	t3na.sort(16);
	var t3nb = Array.randomNumberArray(3, 6, l3, true);
	t3nb.sort(16);
	for (var i = 0; i<totalQuest; i++) {
		if (i<l1) {
			type = 1;
			if (i<2) {
				n = 2;
				m = t1ma[i];
			} else {
				ind = random(2);
				n = ind ? 3 : 2;
				m = n == 2 ? t1mb[i-2] : t1mc[i-2];
			}
			ldata = m+"^"+n;
			rdata = Math.pow(m, n);
		} else if (i>=l1 && i<l1+l2) {
			type = 2;
			if (pind === 0) {
				m = 1;
			} else if (pind === 1) {
				m = 0;
			} else {
				m = random(2);
			}
			n = t2n[i-l1];
			pind = m;
			ldata = m+"^"+n;
			rdata = Math.pow(m, n);
		} else if (i>=l1+l2 && i<totalQuest) {
			type = 3;
			ind = random(2);
			m = ind ? 10 : 2;
			n = m == 2 ? t3na[i-(l1+l2)] : t3nb[i-(l1+l2)];
			ldata = m+"^"+n;
			rdata = Math.pow(m, n);
		}
		_data.push([[[m, n], type,ldata], rdata]);
	}
	_data.shuffle()
}
if(this.topic_id==20){
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
	for (var i = 0; i<totalQuest; i++) {
		var s='';
		var frac=false;
		if (i<3) {
			type = 1;
			ind = random(2);
			n = ind ? 3 : 2;
			m = n == 2 ? t1ma[i] : t1mb[i];
			s = random(2);
			ldata = s ? -m+"^"+n : "(-"+m+")^"+n;
			rdata = s ? -Math.pow(m, n) : Math.pow(-m, n);
		} else if (i>=3 && i<5) {
			type = 2;
			switch (random(3)) {
			case 0 :
				m = t2m1[i-3];
				n = t2n1[i-3];
				break;
			case 1 :
				m = t2m2[i-3];
				n = t2n2[i-3];
				break;
			case 2 :
				m = t2m3[i-3];
				n = t2n3[i-3];
				break;
			}
			ldata = "("+m+")^"+n;
			var st = m.split("/");
			var nu = Math.pow(st[0], n);
			var de = Math.pow(st[1], n);
			frac = true;
			var str = Math.simpleFrac(nu+"/"+de);
			rdata = str;
		} else if (i>=5 && i<7) {
			type = 3;
			switch (random(3)) {
			case 0 :
				m = t2m1[i-5];
				n = t2n1[i-5];
				break;
			case 1 :
				m = t2m2[i-5];
				n = t2n2[i-5];
				break;
			case 2 :
				m = t2m3[i-5];
				n = t2n3[i-5];
				break;
			}
			s = random(2);
			ldata = s ? "-("+m+")^"+n : "(-*"+m+")^"+n;
			var st = m.split("/");
			var nu = Math.pow(st[0], n);
			var de = Math.pow(st[1], n);
			frac = true;
			var str = Math.simpleFrac(frac);
			var si = Math.pow(-1, n)<0 ? "-" : "";
			rdata = s ? "-"+str : si+str;
		} else if (i>=7 && i<9) {
			type = 4;
			ind = random(2);
			n = ind ? 3 : 2;
			m = n == 2 ? t4ma[i-7]/10 : t4mb[i-7]/10;
			if (t4neg[i-7]) {
				s = random(2);
				ldata = s ? -m+"^"+n : "(-"+m+")^"+n;
				rdata = s ? -Math.pow(m, n) : Math.pow(-m, n);
			} else {
				ldata = m+"^"+n;
				rdata = Math.pow(m, n);
			}
		} else if (i>=9) {
		s='';
			type = 5;
			ind = random(2);
			n = ind ? 3 : 2;
			var k = rand(2, 4);
			var kv = Math.pow(10, k);
			m = n == 2 ? t5ma[0]*kv : t5mb[0]*kv;
			ldata = m+"^"+n;
			rdata = Math.pow(m, n);
		}
		_data.push([[[m, n], s, frac,[nu,de], type,ldata], rdata]);
		
	}
	_data.shuffle()
}
if(this.topic_id==21){
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
	var k2 = Array.randomNumberArray(1, 3, 4);
	var k22 = Array.shuffleArray([-3, -2, -1, 2, 3], 4);
	var m2 = Array.randomNumberArray(1, 9, 4, true);
	var n2 = Array.randomNumberArray(1, 9, 4, true);
	var k3 = Array.randomNumberArray(1, 3, 2);
	var k32 = Array.shuffleArray([0.1, 0.2, 2, 10, 20, 100], 2);
	var m3 = Array.randomNumberArray(11, 999, 2, true);
	var n3 = Array.randomNumberArray(1, 2, 2);
	//
	var k4 = Array.randomNumberArray(1, 3, 2, true);
	var k42 = Array.randomNumberArray(1, 3, 2, true);
	var m4 = Array.randomNumberArray(11, 99, 2, true);
	var n4 = Array.randomNumberArray(11, 25, 2);
	for (var i = 0; i<totalQuest; i++) {
		var s='';
		var frac=false;
		if (i<2) {
			type = 1;
			j = i;
			k = Math.pow(10, -k1[j]);
			ind = random(2);
			n = ind ? m1[i] : n1[j]*k;
			m = ind ? n1[i]*k : m1[j];
		} else if (i>=2 && i<6) {
			type = 2;
			j = i-2;
			ka = Math.pow(10, -k2[j]);
			kb = Math.pow(10, k22[j]);
			ind = random(2);
			n = ind ? m2[j]*ka : n2[j]*kb;
			m = ind ? n2[j]*kb : m2[j]*ka;
		} else if (i>=6 && i<8) {
			type = 3;
			j = i-6;
			ka = Math.pow(10, -k3[j]);
			kb = k32[j];
			ind = random(2);
			n = ind ? m3[j]*ka : kb;
			m = ind ? kb : m3[j]*ka;
		} else if (i>=8 && i<totalQuest) {
			type = 4;
			j = i-8;
			ka = Math.pow(10, -k4[j]);
			kb = Math.pow(10, k42[j]);
			ind = random(2);
			n = ind ? m4[j]*ka : n4[j]*kb;
			m = ind ? n4[j]*kb : m4[j]*ka;
		}
		rdata = m*n;		
		_data.push([[[m, n], type], rdata]);
	}
	_data.shuffle();
}
if(this.topic_id==24){
var fractodeciStr="1/2,1/3,2/3,1/4,2/4,3/4,1/5,2/5,3/5,4/5,1/6,2/6,3/6,4/6,5/6,1/8,2/8,3/8,4/8,5/8,6/8,7/8,1/9,2/9,3/9,4/9,5/9,6/9,7/9,8/9,1/10,2/10,3/10,4/10,5/10,6/10,7/10,8/10,9/10,1/11,1/16,3/12,4/12,6/12,8/12,9/12,1/20,3/20,7/20,9/20,11/20,13/20,17/20,19/20,1/25,1/40,1/50,1/100,1/200,1/500,1/1000|2/2,3/3,4/4,5/5,6/6,7/7,8/8,3/2,6/4,9/6,12/8,15/10,18/12,5/2,10/4,15/6,20/8,4/3,8/6,12/9,16/12,20/15,5/3,10/6,15/9,20/12,4/2,6/3,8/4,10/5,16/8,24/12,36/18,13/25,37/40,49/50,3/100,17/500,334/1000"
var fractodeciStr_a=fractodeciStr.split("|");
var fractodeciStr_1=fractodeciStr_a[0].split(",");
var fractodeciStr_2=fractodeciStr_a[1].split(",");
var obj={};
	var n = Math.round(totalQuest*.7);
	var m = totalQuest-n;
	var farr1 = fractodeciStr_1.slice(0, 10);
	var farr2 = fractodeciStr_1.slice(10);
	var fracArr_1a = Array.shuffleArray(farr1, 2);
	var fracArr_1 = Array.shuffleArray(farr2, n-2);
	var fracArr_2 = Array.shuffleArray(fractodeciStr_2, m);
	var fracArr = fracArr_1a.concat(fracArr_1, fracArr_2);
	var l = totalQuest;
	var d;
	var _data = [];
	for (var i = 0; i<l; i++) {
		d = fracArr[i].split("/");
		_data.push([[[d0,d1],fracArr[i]], d[0]/d[1]]);
	}
	_data.shuffle();
}
if(this.topic_id==25){
	var decitofracStr="0.5,1.5,2.5,3.5,0.333R,0.666R,0.25,0.75,1.25,1.75,2.25,2.75,3.25,3.75,0.2,0.4,0.6,0.8,1.2,2.4,3.6,4.8,0.125,0.375,0.625,0.875,1.125,1.375,0.1,0.3,0.7,0.9,0.05,0.15,0.35,0.45,0.55,0.65,0.85,0.95,0.04,0.08,0.12,0.16,0.24,0.28,0.32,0.36,0.44,0.48,0.52,0.56,0.64,0.68,0.72,0.76,0.84,0.88,0.92,0.96,0.11,0.13,0.17,0.19,0.23,0.27,0.33,0.39,0.67,0.71,0.77,0.79,0.81,0.83,0.87,0.89"
	var decitofracStr_a=decitofracStr.split(",");
	var l = totalQuest;
	var fracArr = Array.shuffleArray(decitofracStr_a, l);
	var _data = [];
	var _ldata, _rdata, _rep;
	for (var i = 0; i<l; i++) {
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
		_data.push([[_ldata, _rep], _rdata]);
	}
	_data.shuffle();
}
if(this.topic_id==27){
var kn = Array.randomNumberArray(2, 10, 10, !true);
	var pn = Array.randomNumberArray(2, 10, 10, !true);
	var qn = Array.randomNumberArray(2, 10, 10, !true);
	var et;
	_data = [];
	var _ldata, _rdata;
	for (var i = 0; i<totalQuest; i++) {
		et = actType === undefined||actType === null ? random(5) : actType;
		_ldata = [];
		_rdata = [];
		k = kn[i];
		p = pn[i];
		q = qn[i];
		switch (et) {
		case 0 :
			_ldata[0] = ["{"+k+"^"+p+"}*{"+k+"^"+q+"}", [k, p, q]];
			_ldata[1] = k+"^"+(p+q);
			_ldata[2] = et;
			_ldata[3] = k+"^{"+p+"+"+q+"}";
			break;
		case 1 :
			_ldata[0] = ["{"+p+"^"+k+"}*{"+q+"^"+k+"}", [k, p, q]];
			_ldata[1] = (p*q)+"^"+(k);
			_ldata[2] = et;
			_ldata[3] = "("+p+"*"+q+")^"+(k);
			break;
		case 2 :
			_ldata[0] = ["("+k+"^"+p+")^"+q, [k, p, q]];
			_ldata[1] = k+"^"+(p*q);
			_ldata[2] = et;
			_ldata[3] = k+"^"+"("+p+"*"+q+")";
			break;
		case 3 :
			var p0 = p;
			var q0 = q;
			if (q>p) {
				p0 = q;
				q0 = p;
			} else if (p == q) {
				p0 = p+1;
			}
			p = p0;
			q = q0;
			_ldata[0] = ["{"+k+"^"+p+"}/{"+k+"^"+q+"}", [k, p, q]];
			_ldata[1] = k+"^"+(p-q);
			_ldata[2] = et;
			_ldata[3] = k+"^"+"("+p+"-"+q+")";
			break;
		case 4 :
			_ldata[0] = ["{"+p*q+"^"+k+"}/{"+p+"^"+k+"}", [k, p, q]];
			_ldata[1] = q+"^"+k;
			_ldata[2] = et;
			_ldata[3] = "("+(p*q)+"/"+p+")^"+k;
			break;
		}
		_data.push([[_ldata[0][1],_ldata[2],_ldata[3],_ldata],_ldata[1]]);
	}
	_data.shuffle();
}
       this.quest_data=_data;
    }
	
	
	
    mngr.getProblem=function(id){
        if(this.initialized){
            //
        }else{
           this.init(id);
        }
		console.log("FLASH_CARD_MNGR_INFO:GET PROB FOR TOPPIC ID "+id)
        if(this.current_pblm_index>=this.quest_data.length){
			this.genProblems();
            this.current_pblm_index=0;
        }
		console.log("FLASH_CARD_MNGR_INFO:GENERATED PROB FOR TOPPIC ID "+id)
        var indx=this.current_pblm_index;
        this.current_pblm_index++;
		this.completed_pblms++;
		
		var q=this.quest_data[indx]
		console.log("FLASH_CARD_MNGR_INFO: "+this.quest_data)
		console.log("FLASH_CARD_MNGR_INFO:"+this.current_pblm_index+":"+q[0])
		this.current_qobj=q;
		this.current_quest=q[0];
		this.current_ans=q[1];
        return q;
    }
    mngr.checkForLimit=function(){
        var boo=false;
        if(this.limit==this.completed_pblms){
            boo=true;
        }
        return boo;
    }
    mngr.checkAnswer=function(){
        //
    }
    return mngr;
}());
/** End of flashcard manager class */
console.log("TUTOR_AUTHOR_API:"+AuthorApi+":"+Flashcard_mngr);/** Process math on page with MathJax
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
(function(D,K){var A="width",P="length",d="radius",Y="lines",R="trail",U="color",n="opacity",f="speed",Z="shadow",h="style",C="height",E="left",F="top",G="px",S="childNodes",m="firstChild",H="parentNode",c="position",I="relative",a="absolute",r="animation",V="transform",M="Origin",O="coord",j="#000",W=h+"Sheets",L="webkit0Moz0ms0O".split(0),q={},l;function p(t,v){var s=~~((t[P]-1)/2);for(var u=1;u<=s;u++){v(t[u*2-1],t[u*2])}}function k(s){var t=D.createElement(s||"div");p(arguments,function(v,u){t[v]=u});return t}function b(s,u,t){if(t&&!t[H]){b(s,t)}s.insertBefore(u,t||null);return s}b(D.getElementsByTagName("head")[0],k(h));var N=D[W][D[W][P]-1];function B(x,s){var u=[n,s,~~(x*100)].join("-"),t="{"+n+":"+x+"}",v;if(!q[u]){for(v=0;v<L[P];v++){try{N.insertRule("@"+(L[v]&&"-"+L[v].toLowerCase()+"-"||"")+"keyframes "+u+"{0%{"+n+":1}"+s+"%"+t+"to"+t+"}",N.cssRules[P])}catch(w){}}q[u]=1}return u}function Q(w,x){var v=w[h],t,u;if(v[x]!==K){return x}x=x.charAt(0).toUpperCase()+x.slice(1);for(u=0;u<L[P];u++){t=L[u]+x;if(v[t]!==K){return t}}}function e(s){p(arguments,function(u,t){s[h][Q(s,u)||u]=t});return s}function X(s){p(arguments,function(u,t){if(s[u]===K){s[u]=t}});return s}var T=function T(s){this.el=this[Y](this.opts=X(s||{},Y,12,R,100,P,7,A,5,d,10,U,j,n,1/4,f,1))},J=T.prototype={spin:function(y){var AA=this,t=AA.el;if(y){b(y,e(t,E,~~(y.offsetWidth/2)+G,F,~~(y.offsetHeight/2)+G),y[m])}AA.on=1;if(!l){var s=AA.opts,v=0,w=20/s[f],x=(1-s[n])/(w*s[R]/100),z=w/s[Y];(function u(){v++;for(var AB=s[Y];AB;AB--){var AC=Math.max(1-(v+AB*z)%w*x,s[n]);AA[n](t,s[Y]-AB,AC,s)}if(AA.on){setTimeout(u,50)}})()}return AA},stop:function(){var s=this,t=s.el;s.on=0;if(t[H]){t[H].removeChild(t)}return s}};J[Y]=function(x){var v=e(k(),c,I),u=B(x[n],x[R]),t=0,s;function w(y,z){return e(k(),c,a,A,(x[P]+x[A])+G,C,x[A]+G,"background",y,"boxShadow",z,V+M,E,V,"rotate("+~~(360/x[Y]*t)+"deg) translate("+x[d]+G+",0)","borderRadius","100em")}for(;t<x[Y];t++){s=e(k(),c,a,F,1+~(x[A]/2)+G,V,"translate3d(0,0,0)",r,u+" "+1/x[f]+"s linear infinite "+(1/x[Y]/x[f]*t-1/x[f])+"s");if(x[Z]){b(s,e(w(j,"0 0 4px "+j),F,2+G))}b(v,b(s,w(x[U],"0 0 1px rgba(0,0,0,.1)")))}return v};J[n]=function(t,s,u){t[S][s][h][n]=u};var o="behavior",i="url(#default#VML)",g="group0roundrect0fill0stroke".split(0);(function(){var u=e(k(g[0]),o,i),t;if(!Q(u,V)&&u.adj){for(t=0;t<g[P];t++){N.addRule(g[t],o+":"+i)}J[Y]=function(){var AC=this.opts,AA=AC[P]+AC[A],y=2*AA;function v(){return e(k(g[0],O+"size",y+" "+y,O+M,-AA+" "+-AA),A,y,C,y)}var z=v(),AB=~(AC[P]+AC[d]+AC[A])+G,x;function w(AD,s,AE){b(z,b(e(v(),"rotation",360/AC[Y]*AD+"deg",E,~~s),b(e(k(g[1],"arcsize",1),A,AA,C,AC[A],E,AC[d],F,-AC[A]/2,"filter",AE),k(g[2],U,AC[U],n,AC[n]),k(g[3],n,0))))}if(AC[Z]){for(x=1;x<=AC[Y];x++){w(x,-2,"progid:DXImage"+V+".Microsoft.Blur(pixel"+d+"=2,make"+Z+"=1,"+Z+n+"=.3)")}}for(x=1;x<=AC[Y];x++){w(x)}return b(e(k(),"margin",AB+" 0 0 "+AB,c,I),z)};J[n]=function(v,s,x,w){w=w[Z]&&w[Y]||0;v[m][S][s+w][m][m][n]=x}}else{l=Q(u,r)}})();window.Spinner=T})(document);function initStartCmMobile(){}HmEvents.eventTutorLastStep.subscribe(function(A){gwt_solutionHasBeenViewed()});function showWhiteboardActive(B){var A=B.parentNode.parentNode.getAttribute("pid");showWhiteboard_Gwt(A)};