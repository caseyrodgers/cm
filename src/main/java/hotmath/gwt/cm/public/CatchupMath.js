// External JS for GWT
//
//called from dynamically loaded question HTML
function questionGuessChanged(o) {
    try {

        var pid = findQuestionGuid(o);

        var questionIndex=-1;
        if(o.id == 'optionSkipped') {
            questionIndex = '-2'; // skipped
        }
        else {
            // is it correct or wrong?
            var nd = o.parentNode.getElementsByTagName("div").item(0).innerHTML;
            
            // how to know which index ..
            // .. go to parent, and look for child that matches this
            var parentUl = o.parentNode.parentNode;
            var ndItems = parentUl.getElementsByTagName("input");
            for(var i=0;i<ndItems.length;i++) {
                if(ndItems.item(i) == o) {
                    questionIndex = i;
                    break;
                }
            }
        }
        // call GWT JSNI function defined in QuestionPanel
        questionGuessChanged_Gwt(nd,"" + questionIndex,pid);
    }
    catch(e) {
         alert('Error answering question in external JS: ' + e);
    }
}


/** Search parents looking for closed guid attribute

    return null if not found
*/

function findQuestionGuid(o) {
    while(o) {
        var guid = o.getAttribute('guid');
        if(guid)
           return guid;
        o = o.parentNode;
    }
    return null;
}

var _questionObjectPointer;
// call from JSNI when new question has been loaded in order to set
// the selected question answer 
window.setSolutionQuestionAnswerIndex = function(pid, which) {
   if(which) {
       if(which > -1) {
           ulNode = findQuestionByPid(pid);
           if(ulNode) {
               var inputElements = ulNode.getElementsByTagName("input");
               try {
                   var cb = inputElements.item( Number(which) );
                   cb.style.background = 'red';
                   cb.checked = true;
               }
               catch(e) {
                   alert(e);
               }
           }
       }
   }
}


function findQuestionByPid(pid) {
   var all=document.getElementById('testset_div').getElementsByTagName('div');
   try {
       for(var i=0;i<all.length;i++) {
           var o = all[i].getAttribute('guid');
           if(o == pid) {
               return all[i];
           }
       }
   }
   catch(x) {
       alert('Error while setting selected question response: ' + x);
   }
   alert('findQuestionByPid: pid not found: ' + pid);

   return null;
}

////////////////////
/// For the tutor viewer
/////////////////////
// called by GWT, which setups the context for the pid
function doLoad_Gwt(pid) {
    var mc = createNewSolutionMessageContext(pid);
    gotoGUID(mc);
}

var _bookMeta = {
    textCode:  '',
    isControlled:false 
};
// override for tutor
function setBreadCrumbs(crumbs) {
    // empty
}
////////////////////////
/// End for Tutor /////
////////////////////////


function showWorkPanel_Gwt(yesNo) {
	alert('showing work: ' + yesNo);
}

function _flashWhiteboardOut(jsonOut) {
   alert('json out from flash: ' + jsonOut);
   var fo = document.getElementsByTagName("object")[0];
   alert(fo + ', ' + fo.name + ', ' + fo.updateWhiteboard);
}

function _flashWhiteboardIsReady() {
   alert('flash whiteboard is ready!: ' + document.title );
   
   var fo = document.getElementsByTagName("object")[0];
   alert(fo + ', ' + fo.name + ', ' + fo.updateWhiteboard);
}


function _updateWhiteboard(id, command, commandData) {
   var fo = document.getElementById(id);
   if(!fo) {
       alert('Could not find whiteboard flash object');
   }
   if(fo.updateWhiteboard) {
       fo.updateWhiteboard([['draw',[commandData]]]);
   }
   else {
      alert('could not found updateWhiteboard: ' + id);
   }
}

