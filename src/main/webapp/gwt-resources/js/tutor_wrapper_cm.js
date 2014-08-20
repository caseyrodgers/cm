	/**
 * Tutor wrapper class.
 
    A single instance for each new tutor wrapper
 *
 *
 *
 * Problem CM abstraction over HM tutor API
 *
 */

var TutorWrapper = (function () {

    // private static
    var nextId = 1;

    // constructor
    var cls = function (tutorDomNode, pid, jsonConfig, solutionData, stepText,solutionTitle,showWork, shouldExpandSolution, solutionVariableContext, submitButtonText, indicateWidgetStatus, installCustomSteps) {
    
        // private variables 
        this.id = nextId++;

        // store solution info into instance vars
        this.tutorDomNode = tutorDomNode;        
        this.pid = pid;
        this.jsonConfig = jsonConfig;
        this.solutionData = solutionData;
        this.stepText = stepText;
        this.solutionTitle = solutionTitle;
        this.showWork = showWork;
        this.shouldExpandSolution = shouldExpandSolution;
        this.solutionVariableContext = solutionVariableContext;
        this.submitButtonText = submitButtonText;
        this.indicateWidgetStatus = indicateWidgetStatus;
        this.installCustomSteps = installCustomSteps;
        
        // state vars used to drive this TutorWrapper
        this.currentRealStep=-1;
        this.currentStepUnit=-1;
        this.stepUnitsMo=[];
        this.stepUnits=[];
        this.steps=[];
        this.stepUnit=null;
        this.tutorData=null;
        this.solutionTitle=null;
        this.context=null;
        
                
        if(installCustomSteps) { 
            installCustomSteps();
        }
        
        
        this.context = createNewSolutionMessageContext(pid, jsonConfig);
        

        // TutorWrapper API        
        this.getPid = function() {
            return this.pid;
        };


        this.initializeTutor = function() {
            TutorDynamic.initializeTutor(this);
        };        
        
 
        this.clearEndOfProblem = function() {
            var e = $('[name=refresh_problem_button]', this.tutorDomNode);
            if (e.length > 0) {
                e.get(0).innerHTML = '';
            }
        };
 
        /**
          * read loaded tutor html and create meta data used to drive the solution.
          *
          * The stepUnits are individual parts of a step.
          *
          * Stepunit contains these attribute: id, steprole, steptype, realstep
          *
          * steps are the complete steps consisting of two stepUnits.
          *
          * Return count of stepUnits loaded
          *
        */
        this.analyzeLoadedData = function() {
            this.stepUnits = [];
            this.steps = [];

            /**
            * for each step unit div on page
            *
            */
            var maxStepUnits = 100;
            for ( var s = 0; s < maxStepUnits; s++) {
                var stepUnit = _getStepUnit(s);
                if (stepUnit == null) {
                    break; // done
                }

                var id = stepUnit.getAttribute("id");
                var stepUnitNum = this.stepUnits.length;
                var role = stepUnit.getAttribute("steprole");
                var type = stepUnit.getAttribute("steptype");
                var realNum = parseInt(stepUnit.getAttribute("realstep"));

                var su = new StepUnit(id, stepUnitNum, type, role, realNum,stepUnit);
                this.stepUnits[this.stepUnits.length] = su;

                // is this realStep already created?
                var myStep = this.steps[realNum];
                if (myStep == null) {
                    myStep = new Step(realNum);
                    this.steps[realNum] = myStep;
                }
                myStep.stepUnits[myStep.stepUnits.length] = su;
            }
            
            
            
            return this.stepUnits.length;
        };      
        
        
        /** load data into internal data structures used to 
            drive tutor.
        */
        this.loadTutorData = function() {
            try {
                this.tutorData = eval("(" + this.solutionData + ")");
                
                // in tutor_dynamic.js
                processTutorData(this.tutorData, this.stepText, this.solutionVariableContext, this.tutorDomNode);
                
                
                this.analyzeLoadedData();
            } catch (e) {
                alert('Error processing TutorWidget: ' + e);
            }
        };
        
        this.scrollToStep = function(num) {
            var stb = $get('scrollTo-button');
            if (stb) {
                var top = _getElementTop(stb);
                var visibleSize = getViewableSize();
                var scrollXy = getScrollXY();
                var visTop = scrollXy[1];
                var visHeight = visibleSize[1];
                var visBot = visHeight + visTop;

                if (true || top < visTop || top > visBot) {
                
                    // alert('Need to scroll, visibleSize: ' + visibleSize + ' scrollXy:
                    // ' + scrollXy + ' visTop: '
                    // + visTop + ' visHeight: ' + visHeight + ' visBot: ' + visBot + '
                    // buttonBar: '
                    // + stb);
                    
                    gwt_scrollToBottomOfScrollPanel(top - visHeight);
                }
            }
        };        
        
        
        this.enabledNext = function(yesNo) {
            this.enabledButton('steps_next', yesNo);
        };
        
        
        // Set the state of the toolbar buttons
        this.setState = function(n, onoff) {
            if (n == 'step') {
                this.enabledNext(onoff);

                if (!onoff) {
                    TutorDynamic.endOfSolutionReached();
                }
            } else if (n == 'back') {
                this.enabledPrevious(onoff);
            }
        };
        
        this.enabledPrevious = function(yesNo) {
            this.enabledButton('steps_prev', yesNo);
        }
        
        
        this.setButtonState = function() {
            this.setState('step', this.currentStepUnit < (this.stepUnits.length - 1));
            this.setState('back', this.currentStepUnit > -1);
        }
        
        this.enabledButton = function(btn, yesNo) {
            // gwt_enableTutorButton(btn, yesNo);
            //    var clazz = 'sexybutton sexy_cm_silver ';
            //    if (!yesNo) {
            //        clazz += ' disabled';
            //    }
            //    var b = $get(btn);
            //    if(b) {
            //       b.className = clazz;
            //    }
           //    else {
           //        alert('required button with id [' + btn + '] not found');
           //    }
        }
        
        this.setStepTitle = function(num, stepElement) {
            // put title up.
            // put up step number if not a hint
            // otherwise, show 'Hint'
            var stepTitle = $get('step_title-' + num);
            if (stepTitle) {
                var sr = stepElement.getAttribute("steprole");
                if (sr && sr == 'step') {
                    stepTitle.innerHTML = 'Step '
                        + (parseInt(stepElement.getAttribute('realstep')) + 1);
                    stepTitle.className = 'step_title_step';
                } else {
                    // assume hint
                    stepTitle.innerHTML = 'Hint';
                    stepTitle.className = 'step_title_hint';
                }
            }
        };
        
        this.resetTutor = function() {
            var tutor = this.tutorDomNode;
            
            if(tutor) {
                tutor.innerHTML = this.stepText;
            }

            this.currentRealStep = -1;
            this.currentStepUnit = -1;
            this.loadTutorData();
            
            if(this.isVisible) {
                this.setButtonState();
            }
        },

        
        
        // Show the next available step unit
        this.showStepUnit = function(num) {
            if (num < 0)
                return;
            try {
                var stepElement = this.stepUnits.length == 0?null:this.stepUnits[num].ele;
                if (stepElement == null) {
                    return false;
                }

                // use animation library to show step
                // appear added as method during intiialize
                // of scripty2
                stepElement.style.display = 'block';
                //stepElement.appear();

                if (stepElement.getAttribute("steprole") == 'step')
                    setAsCurrent(stepElement);

                this.setStepTitle(num, stepElement);

                // determine if figure should be displayed. Only display
                // if is the first one or different than the previous.
                var figureUnit = _getFigureUnit(num);
                if (figureUnit != null) {
                if (num == 0) {
                    figureUnit.style.display = "block";
                } else {
                    // only display image if it is not
                    // the same as the previously displayed image.
                    // find the first previous image.
                    var prevFigureUnit = findPreviousFigureUnit(num);
                    if (prevFigureUnit != null && prevFigureUnit.src == figureUnit.src) {
                        // skip it
                        figureUnit.style.display = "none";
                    } else {
                        // image is different, so show it
                        figureUnit.style.display = "block";
                    }
                }
            }

            // make sure all previous hints are invisible
            // stepunits of role == 'hint'
            for (i = num - 1; i > -1; i--) {
                if (this.stepUnits[i].roleType == 'hint') {
                    this.stepUnits[i].ele.style.display = 'none';
                } else {
                    // set as not-current
                    setAsNotCurrent(this.stepUnits[i].ele);
                }
            }

            // make sure all future step unitsare hidden
            for ( var i = num + 1; i < this.stepUnits.length; i++) {
                if (this.stepUnits[i].roleType == 'hint') {
                    this.stepUnits[i].ele.style.display = 'none';
                } else {
                    // set as not-current
                    setAsNotCurrent(this.stepUnits[i].ele);
               }
            }

            this.currentStepUnit = num;
            this.currentRealStep = this.stepUnits[num].realNum;

            this.setButtonState();

            this.scrollToStep(num);

            HmEvents.eventTutorChangeStep.fire(num, this);

        } catch (e) {
            alert('Error showing step: ' + e);
        }
        return true;
    }

    
    
    function createNewSolutionMessageContext(pid, jsonConfig) {
        var loc = new SolutionMessageLocation('solution', pid);
        var mc = new MessageContext(loc);
        if (jsonConfig) {
            try {
                mc.jsonConfig = eval('(' + jsonConfig + ')');
            } catch (e) {
                alert('could not process solution ' + pid + ' config: ' + jsonConfig)
            }
        }
        return mc;
    }
    
        
        
    function installCustomSteps() {
        try {
            /** install new step unit showing answer to multi choice
            * 
            */
            var root = document.getElementById("raw_tutor_steps");
        
            var root = document.getElementById("raw_tutor_steps");
            var ps = document.getElementById("problem_statement");
            var options = ps.getElementsByTagName("input");
            if(options.length == 0) {
                return;
            }
        
            var answer=-1;
            for(var i=0;i<options.length;i++) {
                if(options[i].getAttribute("value") == 'true') {
                    answer = i;
                    break;
                }
            }
            if(answer == -1) {
                return;
            }
        
            /**
            * remove all existing step units
            * 
            */
            var maxStepUnits = 100;
            for ( var s = 0; s < maxStepUnits; s++) {
                var stepUnit = _getStepUnit(s);
                if (stepUnit == null) {
                    break; // done
                }
            
                // remove it
                stepUnit.parentNode.removeChild(stepUnit);
            }
            
        
            var letters = ['A','B','C','D', 'E', 'F'];
            var answer = letters[answer];
            var html = '<div style="display:none;" realstep="0" steprole="hint" class="hint" steptype="hint" id="stepunit-0"><div class="step_content"><p>Click the Lesson button for review.</p></div></div>';
            var el = document.createElement('div');
            el.innerHTML = html;
            root.appendChild(el);
        
            var html2 = '<div style="display:none;" realstep="0" steprole="step" class="step" steptype="step" id="stepunit-1"> <div id="step_title-1"></div> <div class="step_content"><p>The correct choice is ' + answer + '.</p> </div></div>'
            var el = document.createElement('div');
            el.innerHTML = html2;
            root.appendChild(el);
        }
        catch(e) {
            console.log('error installing custom steps: ' + e);
        }
    }
     }   
    return cls;
    
})();













