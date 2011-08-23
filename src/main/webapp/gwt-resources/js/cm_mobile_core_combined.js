var _leftAnchor=null;var _leftAnchorElement;var _maxTop=null;var _floatDiv=null;function detach(B,C,A){_floatDiv=B;_leftAnchorElement=C;lay=document.getElementById(B);_maxTop=DL_GetElementTop($get(B));l=getXCoord(lay);t=getYCoord(lay);if(!A){lay.style.position="absolute"}if(_funcToCallOnRePosition){_funcToCallOnRePosition(lay,l,t)}else{lay.style.top=t+"px";lay.style.left=l+"px"}getFloatLayer(B).initialize()}var FloatLayers=new Array();var FloatLayersByName=new Array();var _funcToCallOnRePosition;function addFloatLayer(D,B,A,C){new FloatLayer(D,B,A,C)}function getFloatLayer(A){return FloatLayersByName[A]}var _isAligning;function alignFloatLayers(){if(_isAligning){return }_isAligning=true;var E=$get(_floatDiv);var D=0;var C=0;var F=0;if(_leftAnchorElement){var A=_leftAnchorElement;D=DL_GetElementLeft(A);C=grabComputedWidth(A);F=parseInt(D)+parseInt(C)}else{C=grabComputedWidth($get(_floatDiv));D=getViewableSize();F=D[0]-C}fdw=grabComputedWidth(E);fdl=parseInt(F)-parseInt(fdw);_leftAnchor=fdl;for(var B=0;B<FloatLayers.length;B++){FloatLayers[B].align()}_isAligning=false}function getXCoord(A){x=0;while(A){x+=A.offsetLeft;A=A.offsetParent}return x}function getYCoord(A){y=0;while(A){y+=A.offsetTop;A=A.offsetParent}return y}FloatLayer.prototype.setFloatToTop=setTopFloater;FloatLayer.prototype.setFloatToBottom=setBottomFloater;FloatLayer.prototype.setFloatToLeft=setLeftFloater;FloatLayer.prototype.setFloatToRight=setRightFloater;FloatLayer.prototype.initialize=defineFloater;FloatLayer.prototype.adjust=adjustFloater;FloatLayer.prototype.align=alignFloater;function FloatLayer(E,B,A,C,D){this.index=FloatLayers.length;_funcToCallOnRePosition=D;FloatLayers.push(this);FloatLayersByName[E]=this;this.name=E;this.floatX=0;this.floatY=0;this.tm=null;this.steps=C;this.alignHorizontal=(B>=0)?leftFloater:rightFloater;this.alignVertical=(A>=0)?topFloater:bottomFloater;this.ifloatX=Math.abs(B);this.ifloatY=Math.abs(A)}function defineFloater(){if(!this.layer){this.layer=document.getElementById(this.name)}this.width=this.layer.offsetWidth;this.height=this.layer.offsetHeight;this.prevX=this.layer.offsetLeft;this.prevY=this.layer.offsetTop}var cnt=0;function adjustFloater(){this.tm=null;var B=Math.abs(this.floatX-this.prevX);var A=Math.abs(this.floatY-this.prevY);if(B<this.steps/2){cx=(B>=1)?1:0}else{cx=Math.round(B/this.steps)}if(A<this.steps/2){cy=(A>=1)?1:0}else{cy=Math.round(A/this.steps)}if(this.floatX>this.prevX){this.prevX+=cx}else{if(this.floatX<this.prevX){this.prevX-=cx}}if(this.floatY>this.prevY){this.prevY+=cy}else{if(this.floatY<this.prevY){this.prevY-=cy}}if((_maxTop>0)&&this.prevY<_maxTop){this.prevY=_maxTop}if(_funcToCallOnRePosition){cnt++;_funcToCallOnRePosition(this.layer,_leftAnchor,this.prevY)}else{this.layer.style.left=_leftAnchor+"px";this.layer.style.top=this.prevY+"px"}if(cx!=0||cy!=0){log("cx: "+cx+"   cy: "+cy);if(this.tm==null){this.tm=setTimeout("FloatLayers["+this.index+"].adjust()",50)}}else{alignFloatLayers()}}function setLeftFloater(){this.alignHorizontal=leftFloater}function setRightFloater(){this.alignHorizontal=rightFloater}function setTopFloater(){this.alignVertical=topFloater}function setBottomFloater(){this.alignVertical=bottomFloater}function leftFloater(){this.floatX=document.body.scrollLeft+this.ifloatX}function topFloater(){var A=getScrollXY()[1];this.floatY=A+this.ifloatY}function rightFloater(){this.floatX=document.body.scrollLeft+document.body.clientWidth-this.ifloatX-this.width}function bottomFloater(){this.floatY=document.body.scrollTop+document.body.clientHeight-this.ifloatY-this.height}function alignFloater(){this.initialize();this.alignHorizontal();this.alignVertical();if(this.prevX!=this.floatX||this.prevY!=this.floatY){if(this.tm==null){this.tm=setTimeout("FloatLayers["+this.index+"].adjust()",50)}}};function DL_GetElementLeft(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetLeft;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientLeft}}A+=D.offsetLeft;D=D.offsetParent}return A}function DL_GetElementTop(B){if(!B&&this){B=this}var C=document.all?true:false;var A=B.offsetTop;var D=B.offsetParent;while(D!=null){if(C){if(D.tagName=="TD"){A+=D.clientTop}}A+=D.offsetTop;D=D.offsetParent}return A}function getViewableSize(){var B=0,A=0;if(typeof (window.innerWidth)=="number"){B=window.innerWidth;A=window.innerHeight}else{if(document.documentElement&&(document.documentElement.clientWidth||document.documentElement.clientHeight)){B=document.documentElement.clientWidth;A=document.documentElement.clientHeight}else{if(document.body&&(document.body.clientWidth||document.body.clientHeight)){B=document.body.clientWidth;A=document.body.clientHeight}}}a=[B,A];return a}function getScrollXY(){var B=0,A=0;if(typeof (window.pageYOffset)=="number"){A=window.pageYOffset;B=window.pageXOffset}else{if(document.body&&(document.body.scrollLeft||document.body.scrollTop)){A=document.body.scrollTop;B=document.body.scrollLeft}else{if(document.documentElement&&(document.documentElement.scrollLeft||document.documentElement.scrollTop)){A=document.documentElement.scrollTop;B=document.documentElement.scrollLeft}}}return[B,A]}function _addEvent(E,D,B,A){if(E.addEventListener){E.addEventListener(D,B,A);return true}else{if(E.attachEvent){var C=E.attachEvent("on"+D,B);return C}else{alert("Handler could not be attached")}}}function _removeEvent(E,D,B,A){if(E.removeEventListener){E.removeEventListener(D,B,A);return true}else{if(E.detachEvent){var C=E.detachEvent("on"+D,B);return C}else{alert("Handler could not be removed")}}}function hideDivOnMouseOut(A){var C,B;if(window.event){C=this;B=window.event.toElement}else{C=A.currentTarget;B=A.relatedTarget}if(C!=B){if(!contains(C,B)){C.style.display="none"}}}function contains(B,A){while(A.parentNode){A=A.parentNode;if(A==B){return true}}return false}function grabComputedStyle(B,A){if(document.defaultView&&document.defaultView.getComputedStyle){return document.defaultView.getComputedStyle(B,null).getPropertyValue(A)}else{if(B.currentStyle){return B.currentStyle[A]}else{return null}}}function grabComputedHeight(B){var A=grabComputedStyle(B,"height");if(A!=null){if(A=="auto"){if(B.offsetHeight){A=B.offsetHeight}}A=parseInt(A)}return A}function grabComputedWidth(B){var A=grabComputedStyle(B,"width");if(A!=null){if(A.indexOf("px")!=-1){A=A.substring(0,A.indexOf("px"))}if(A=="auto"){if(B.offsetWidth){A=B.offsetWidth}}}return A};window.showCorrectAnswers=function(B){var D=document.getElementById("testset_div");if(D){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}}};function setQuizQuestionActive(A){}function findQuestionGuid(B){while(B){var A=B.getAttribute("guid");if(A){return A}B=B.parentNode}return null}function findQuestionByPid(B){var D=document.getElementById("testset_div").getElementsByTagName("div");try{for(var C=0;C<D.length;C++){var E=D[C].getAttribute("guid");if(E==B){return D[C]}}}catch(A){alert("Error while setting selected question response: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function findQuestionNumberByPid(B){var E=document.getElementById("testset_div").getElementsByTagName("div");var C=0;try{for(var D=0;D<E.length;D++){var F=E[D].getAttribute("guid");if(F){if(F==B){return C}else{C++}}}}catch(A){alert("Error while question index: "+A)}alert("findQuestionByPid: pid not found: "+B);return null}function questionGuessChanged(H){try{var B=findQuestionGuid(H);var G=-1;if(H.id=="optionSkipped"){G="-2"}else{var D=H.parentNode.parentNode.parentNode;var A=D.getElementsByTagName("input");for(var C=0;C<A.length;C++){if(A.item(C)==H){G=C;break}}}var E=findQuestionNumberByPid(B);questionGuessChanged_Gwt(""+E,""+G,B)}catch(F){alert("Error answering question in external JS: "+F)}}function setSolutionQuestionAnswerIndexByNumber(B,C){var A=0;showCorrectAnswers(function(H){var G=H.getElementsByTagName("input");if(A==B){for(var F=0,E=G.length;F<E;F++){if(F==C){G[F].checked=true;var D=G[F];questionGuessChanged(D)}else{G[F].checked=false}}}A++})}window.setSolutionQuestionAnswerIndex=function(B,G,E){ulNode=findQuestionByPid(B);if(ulNode){var F=ulNode.getElementsByTagName("input");for(var D=0,C=F.length;D<C;D++){var A=F.item(D);A.disabled=E?true:false;if(D==G){A.checked=true}}}};function doLoadResource(B,A){doLoadResource_Gwt(B,A);return false}window.markAllCorrectAnswers=function(){showCorrectAnswers(markCorrectResponse)};window.getQuizResultsCorrect=function(){var A=0;showCorrectAnswers(function(E){var D=E.getElementsByTagName("input");for(var C=0,B=D.length;C<B;C++){var F=D[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){if(D[C].checked){A++}}}});return A};window.getQuizQuestionCount=function(){var A=0;showCorrectAnswers(function(B){A++});return A};window.showCorrectAnswers=function(B){var A=document.getElementById("testset_div").getElementsByTagName("div");for(var C=0;C<A.length;C++){if(A[C].className=="question_wrapper"){B(A[C])}}};window.markCorrectResponse=function(D){var E=D.getElementsByTagName("input");for(var C=0,B=E.length;C<B;C++){var F=E[C].parentNode.getElementsByTagName("div");if(F[0].innerHTML=="Correct"){E[C].checked=true;var A=E[C];questionGuessChanged(A);break}}};function checkQuiz_Gwt(){alert("Checking quiz ...")}window.setQuizQuestionResult=function(C,A){var E=findQuestionByPid(C);var D=getQuestionMarkImage(C);var B=getQuestionMarkText(C);if(A=="Correct"){D.src="/gwt-resources/images/check_correct.png";B.innerHTML="Correct"}else{if(A=="Incorrect"){D.src="/gwt-resources/images/check_incorrect.png";B.innerHTML="Incorrect"}else{D.src="/gwt-resources/images/check_notanswered.png";B.innerHTML="Not answered"}}D.parentNode.style.display="block"};function getQuestionMarkImage(A){return document.getElementById("response_image_"+A)}function getQuestionMarkText(A){return document.getElementById("response_text_"+A)}function log(){}InmhButtons={};var _productionMode=false;var HmEvents={listeners:[],eventTutorInitialized:{subscribe:function(A){HmEvents.listeners[HmEvents.listeners.length]=A},fire:function(){for(var A=0;A<HmEvents.listeners.length;A++){HmEvents.listeners[A]()}}}};function $get(A){return document.getElementById(A)}function setStepsInfoHelp(){}function resetStepsInfo(){}function getNextMoveTo(){}var TutorManager={currentRealStep:-1,currentStepUnit:-1,stepUnitsMo:[],stepUnits:[],steps:[],pid:"",stepUnit:null,tutorData:null,initializeTutor:function(A,D,C,B){TutorManager.pid=A;TutorManager.currentRealStep=-1;TutorManager.currentStepUnit=-1;TutorManager.loadTutorData(D);TutorManager.analyzeLoadedData();HmEvents.eventTutorInitialized.fire()},showMessage:function(B){var A=$get("tutor_message");A.innerHTML=B;setTimeout(function(){A.innerHTML="&nbsp;"},5000)},showNextStep:function(){if(TutorManager.currentStepUnit+1<TutorManager.stepUnits.length){TutorManager.currentStepUnit++;showStepUnit(TutorManager.currentStepUnit)}else{TutorManager.showMessage("no more steps")}},showPreviousStep:function(){if(TutorManager.currentStepUnit<0){TutorManager.showMessage("No previous step");return }else{while(TutorManager.currentStepUnit>0){var A=TutorManager.stepUnits[TutorManager.currentStepUnit].ele;if(TutorManager.stepUnits[TutorManager.currentStepUnit].realNum!=TutorManager.currentRealStep){TutorManager.currentRealStep=TutorManager.stepUnits[TutorManager.currentStepUnit].realNum;break}A.style.display="none";TutorManager.currentStepUnit--}if(TutorManager.currentStepUnit==0){TutorManager.currentStepUnit=-1;window.scrollTo(0,0)}if(TutorManager.currentStepUnit>-1){setAsCurrent(TutorManager.stepUnits[TutorManager.currentStepUnit].ele)}return false}},loadTutorData:function(solutionData){try{TutorManager.tutorData=eval("("+solutionData+")")}catch(e){alert(e)}},analyzeLoadedData:function(){TutorManager.stepUnits=[];TutorManager.steps=[];var I=100;for(var J=0;J<I;J++){var E=_getStepUnit(J);if(E==null){break}var B=E.getAttribute("id");var A=TutorManager.stepUnits.length;var C=E.getAttribute("steprole");var F=E.getAttribute("steptype");var G=parseInt(E.getAttribute("realstep"));var H=new StepUnit(B,A,F,C,G,E);TutorManager.stepUnits[TutorManager.stepUnits.length]=H;var D=TutorManager.steps[G];if(D==null){D=new Step(G);TutorManager.steps[G]=D}D.stepUnits[D.stepUnits.length]=H}return TutorManager.stepUnits.length},backToLesson:function(){gwt_backToLesson()}};function StepUnit(F,E,B,A,D,C){this.id=F;this.stepUnitNum=E;this.type=B;this.roleType=A;this.realNum=D;this.ele=C}function Step(A){this.realNum=A;this.stepUnits=new Array()}function _getStepUnit(A){return _getElement("stepunit",A)}function _getHintUnit(A){return _getElement("hintunit",A)}function _getFigureUnit(A){return _getElement("figure",A)}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setAsNotCurrent(A){A.style.backgroundColor="#E2E2E2"}function _getElement(A,B){var C=A+"-"+B;return document.getElementById(C)}function showStepUnit(A){if(A<0){return }try{var B=TutorManager.stepUnits[A].ele;if(B==null){return false}B.style.display="block";if(B.getAttribute("steprole")=="step"){setAsCurrent(B)}setStepTitle(A,B);var D=_getFigureUnit(A);if(D!=null){if(A==0){D.style.display="block"}else{var E=findPreviousFigureUnit(A);if(E!=null&&E.src==D.src){D.style.display="none"}else{D.style.display="block"}}}for(i=A-1;i>-1;i--){if(TutorManager.stepUnits[i].roleType=="hint"){TutorManager.stepUnits[i].ele.style.display="none"}else{setAsNotCurrent(TutorManager.stepUnits[i].ele)}}TutorManager.currentStepUnit=A;TutorManager.currentRealStep=TutorManager.stepUnits[A].realNum;if(TutorManager.stepUnits[A+1]==null){setState("step",false)}else{setState("step",true)}setState("back",true);scrollToStep(A)}catch(C){alert("Error showing step: "+C)}return true}function setAsCurrent(A){A.style.backgroundColor="#F1F1F1"}function setStepTitle(A,C){stepTitle=document.getElementById("step_title-"+A);if(stepTitle){var B=C.getAttribute("steprole");if(B&&B=="step"){stepTitle.innerHTML="Step "+(parseInt(C.getAttribute("realstep"))+1);stepTitle.className="step_title_step"}else{stepTitle.innerHTML="Hint";stepTitle.className="step_title_hint"}}}function findPreviousFigureUnit(A){for(p=A-1;p>-1;p--){fu=_getFigureUnit(p);if(fu!=null){return fu}}return null}function setState(B,A){}function scrollToStep(D){var F=getViewableSize();var A=getScrollXY();var G=A[1];var E=F[1];var H=E+G;var B=DL_GetElementTop(document.getElementById("scrollTo-button"));if(B<G||B>H){var C=Number(B)-E;setTimeout("window.scrollTo(0,10000);",0)}}function hideAllSteps(){for(var A=0;A<TutorManager.stepUnits.length;A++){var B=TutorManager.stepUnits[A].ele;if(B==null){return }if(B.style.display!="none"){B.style.display="none"}}window.scrollTo(0,0)}function initializeExternalJs(){var A="control-floater";new FloatLayer(A,150,15,10);detach(A);alignControlFloater()}function alignControlFloater(){alignFloatLayers();setTimeout(alignControlFloater,2000)}function doQuestionResponseEnd(){}var _activeQuestion;function doQuestionResponse(A,D){var C=TutorManager.tutorData._strings_moArray[A];if(_activeQuestion){var B=document.createElement("div");B.className="questionResponseAnswer";B.innerHTML=C;_activeQuestion.parentNode.appendChild(B)}else{gwt_showMessage(C)}}HmEvents.eventTutorInitialized.subscribe(function(){var H=document.getElementById("tutor_raw_steps_wrapper");if(H==null){return }var B=H.getElementsByTagName("div");var A=B.length;for(var E=0;E<A;E++){var G=B.item(E);if(G.className=="question_guess"){var F=G.getElementsByTagName("img");var D=F.item(0);var C=D.onmouseout=null;D.onmouseoverDeferred=D.onmouseover;D.onmouseover=null;D.onclick=function(I){var J=(I)?I:window.event;var K=J.srcElement?J.srcElement:J.target;_activeQuestion=K;if(!K.onmouseoverDeferred){alert("error: no deferred move event");return }K.onclick=null;K.onmouseoverDeferred()}}}});// version 1.1
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

var _enableJsWidgets=false;

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
}

HmFlashWidget.prototype.markWidgetIncorrect = function() {
   setWidgetMessage("TRY AGAIN!");
   var indicator = $get('hm_flash_widget_indicator');
   indicator.innerHTML = "<img src='/tutor/widget/images/widget_incorrect.png'/>";
   indicator.style.display = 'block';
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
HmFlashWidgetImplOdds.prototype.processWidgetValidation = validateOdds;



_enableJsWidgets=true;