/** add trim, if not available */
if(typeof String.prototype.trim !== 'function') {
    String.prototype.trim = function() {
      return this.replace(/^\s+|\s+$/g, '');
    }
  }


/** Initialize system
 *  Might be modified in
 *  individual pages */
var CmPage = {
        menuItem:-1,
        sticky:[]
};

function $get(x) {
    return document.getElementById(x);
}


function setupPage() {
	
	if(isIPadOrIPhone()) {
		setupForMobile();
	}
    
    /** setup menu buttons
     *
     */
    YUI().use('node', function(Y) {
        Y.on('domready',function() {
            if(CmPage.menuItem > -1) {
                /** Set the selected menu button */
                var menuButtons = Y.one('#menubar2').all('a');
                _mainMenuItemObject = menuButtons.item(CmPage.menuItem);
                _mainMenuItemObject.removeClass('notselected');
                _mainMenuItemObject.removeClass('sexy_cm_gray');
                _mainMenuItemObject.addClass('sexy_cm_green');
                _mainMenuItemObject.addClass('sexybutton');
            }

            /** Setup the sticky nodes area with two random
             *  quotes from QuoteManager.
             *
             */
            var postIt = Y.one('#postit');
            if(postIt) {
                if(CmPage.sticky.length == 2) {
                    var html= formatQuote(Quotes[CmPage.sticky[0]]);
                    html += formatQuote(Quotes[CmPage.sticky[1]]);
                    postIt.set('innerHTML',html);
                }
            }

           setupPageLocal();
     });
    });


    /** provide anchor to 2-min video
        (CSS in core.css)
    */
    var el = document.createElement("a");
    el.href='/how-it-works';
    el.innerHTML = '<img src="/assets/images/blank-25x25.png"/>';
    document.getElementById("header").appendChild(el);
 }

function setupForMobile() {
	//document.getElementById("training_videos_link").style.display = 'none';
	//document.getElementById("student_video_link").style.display = 'none';
	//document.getElementById("webinar_link").style.display = 'none';
}


var MobileCheck = {
	    Android: function() {
	        return navigator.userAgent.match(/Android/i);
	    },
	    BlackBerry: function() {
	        return navigator.userAgent.match(/BlackBerry/i);
	    },
	    iOS: function() {
	        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
	    },
	    Opera: function() {
	        return navigator.userAgent.match(/Opera Mini/i);
	    },
	    Windows: function() {
	        return navigator.userAgent.match(/IEMobile/i);
	    },
	    any: function() {
	        return (MobileCheck.Android() || MobileCheck.BlackBerry() || MobileCheck.iOS() || MobileCheck.Opera() || MobileCheck.Windows());
	    }
};


function isIPadOrIPhone() {
	return MobileCheck.any();
}

function redirectIfMobile() {
	if(isIPadOrIPhone()) {
		document.location = '/not-available-on-mobile.html';
	}
}

function setupPageLocal() {
    /** empty */
}


YUI().use('node',function(Y) {
    Y.on('domready',function() {
        setupPage();
    });
});



var _overlay=null;

function showMonaMotivationalVideo() {
    var closeFoot = '';


    var html = '<iframe src="/motivational_video/" width="320" height="265px" scrolling="no" frameborder="no"></iframe>' +
              closeFoot;

    var head = '<a href="#" onclick="closeMonaVideo();return false" class="close"><span>close</span> X</a>' + "Catchup Math Motivational Video";



    YUI().use('anim','overlay',
                    function(Y) {
                        _overlay = new Y.Overlay(
                                {
                                    id:"mona-video",
                                    width : "328px",
                                    centered : true,
                                    headerContent : head,
                                    bodyContent : html,
                                    zIndex : 2,
                                    visible:true
                                });

                       _overlay.render();
    });
}

/** Closed from close button */
function closeMonaVideo() {
    _overlay.set("bodyContent", "");  // make sure video stops
    _overlay.hide();
}

/** Closed from close button */
function closeGeneralDialog() {
    _overlay.hide();
}

function closeWebinar() {
    _overlay.set("bodyContent", "");  // make sure video stops
    _overlay.hide();
}

var _assignmentsOverlay=null;

function closeAssignmentsWebinar() {
    _assignmentsOverlay.set("bodyContent", "");  // make sure video stops
    _assignmentsOverlay.hide();
}

function showWebinar() {
    var closeFoot = '';
    var html = '<iframe src="/teacher-training-video/embedded.html" width="800" height="498px" scrolling="no" frameborder="no"></iframe>' +
              closeFoot;

    var head = '<a href="#" onclick="closeWebinar();return false" class="close"><span>close</span> X</a>' + "Teaching With Catchup Math";

    YUI().use('anim','overlay',
                    function(Y) {
                        _overlay = new Y.Overlay(
                                {
                                    id:"webinar-video",
                                    width : "810px",
                                    height: "498px",
                                    centered : true,
                                    headerContent : head,
                                    bodyContent : html,
                                    zIndex : 2,
                                    visible:true
                                });

                       _overlay.render();
    });
}

function showAssignmentsWebinar() {
    var closeFoot = '';
    var html = '<iframe src="/assets/util/assignments-video-frame.html" width="970" height="530px" scrolling="no" frameborder="no"></iframe>' +
              closeFoot;

    var head = '<a href="#" onclick="closeAssignmentsWebinar();return false" class="close"><span>close</span> X</a>' + "Catchup Math Assignments Webinar";

    YUI().use('anim','overlay',
                    function(Y) {
                        _assignmentsOverlay = new Y.Overlay(
                                {
                                    id:"assignments-webinar-video",
                                    width : "982px",
                                    height: "570px",
                                    centered : true,
                                    headerContent : head,
                                    bodyContent : html,
                                    zIndex : 2,
                                    visible:true
                                });

                       _assignmentsOverlay.render();
    });
}

var _videoOverlay=null;
var _videoOverlay2=null;

function closeTeacherVideo() {
    _videoOverlay.set("bodyContent", "");  // make sure video stops
    _videoOverlay.hide();
    _videoOverlay = null;
}

function closeTeacherVideo2() {
	alert('test');
    _videoOverlay2.set("bodyContent", "");  // make sure video stops
    _videoOverlay2.hide();
    _videoOverlay2 = null;
}

var unique=0;
function showTeacherVideo2(ele, videoUrl) {
	
	var title = ele.innerHTML;
	var html = '<iframe src="/training-videos/embedded-wrapper.html?video=' + videoUrl + '"  width="630" height="525px" scrolling="no" frameborder="no"></iframe>';
	
	var head = '<a href="#" onclick="javascript:closeTeacherVideo()" class="close"><span>close</span> X</a>' + title;
    var tail = '<a href="#" onclick="javascript:closeTeacherVideo()" class="close" style="background-color: white"><span>close</span> X</a>';

    unique++;
    YUI().use('anim','overlay',
                    function(Y) {
                        _videoOverlay = new Y.Overlay(
                                {
                                    id:"training-video-" + unique,
                                    width : "642px",
                                    height: "562px",
                                    centered : true,
                                    fixedcenter : true,
                                    headerContent : head,
                                    bodyContent : html + tail,
                                    zIndex : 2,
                                    visible:true
                                });

                       _videoOverlay.render();
    });
}

function showTeacherVideo(name) {
        if (_videoOverlay != null) {
                showDialog('Sorry, only one video may be viewed at a time', 'Training Video Message', 4);
                return;
        }

        var html = '';
        var closeFoot = '';
        var title = 'Catchup Math Training Video';

        if (name =='overview') {
             html = '<iframe src="/training-videos/embedded-overview.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
         closeFoot;
             title = 'Overview of Teacher Resources';
        }
        else if (name == 'sample-session') {
             html = '<iframe src="/training-videos/embedded-sample-session.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
         closeFoot;
             title = 'Sample Student Session';
        }
        else if (name == 'available-content') {
             html = '<iframe src="/training-videos/embedded-available-content.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
         closeFoot;
             title = 'Available Content';
        }
        else if (name == 'registering-students') {
             html = '<iframe src="/training-videos/embedded-registering-students.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
         closeFoot;
             title = 'Registering Students';
        }
        else if (name == 'managing-students') {
             html = '<iframe src="/training-videos/embedded-managing-students.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
         closeFoot;
             title = 'Managing Students';
        }
        else if (name == 'managing-groups') {
             html = '<iframe src="/training-videos/embedded-managing-groups.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
         closeFoot;
             title = 'Managing Groups';
        }
        else if (name == 'custom-programs') {
            html = '<iframe src="/training-videos/embedded-custom-programs.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
        closeFoot;
            title = 'Custom Programs and Quizzes';
       }
        else if (name == 'parallel-programs') {
            html = '<iframe src="/training-videos/embedded-parallel-programs.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
        closeFoot;
            title = 'Parallel Programs';
       }
       else if (name == 'special-needs') {
            html = '<iframe src="/training-videos/embedded-special-needs.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
        closeFoot;
            title = 'At Risk and Special Needs';
       }
       else if (name == 'student-how-to') {
            html = '<iframe src="/training-videos/embedded-student-how-to.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
        closeFoot;
            title = 'Student How To';
       }
       else if (name == 'weblinks-quick') {
            html = '<iframe src="/training-videos/embedded-weblinks.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
        closeFoot;
            title = 'Web Links Quick Overview';
       }
       else if (name == 'student_registration-quick') {
           html = '<iframe src="/training-videos/embedded-student_registration-quick.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
       closeFoot;
           title = 'Student Registration - Quick Start';
      }
      else if (name == 'student_registration') {
           html = '<iframe src="/training-videos/embedded-student_registration-groups.html" width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
       closeFoot;
           title = 'Registering Groups and Classes';
      }
      else if(name === 'overview-reports') {
    	  
      }
        	
    var head = '<a href="#" onclick="javascript:closeTeacherVideo();" class="close"><span>close</span> X</a>' + title;

    var tail = '<a href="#" onclick="javascript:closeTeacherVideo();" class="close" style="background-color: white"><span>close</span> X</a>';

    YUI().use('anim','overlay',
                    function(Y) {
                        _videoOverlay = new Y.Overlay(
                                {
                                    id:"training-video",
                                    width : "642px",
                                    height: "562px",
                                    centered : true,
                                    fixedcenter : true,
                                    headerContent : head,
                                    bodyContent : html + tail,
                                    zIndex : 2,
                                    visible:true
                                });

                       _videoOverlay.render();
    });
}

function getQueryStringParameter (paramName, url) {

    var i, len, idx, queryString, params, tokens;

    url = url || top.location.href;

    idx = url.indexOf("?");
    queryString = idx >= 0 ? url.substr(idx + 1) : url;

    // Remove the hash if any
    idx = queryString.lastIndexOf("#");
    queryString = idx >= 0 ? queryString.substr(0, idx) : queryString;

    params = queryString.split("&");

    for (i = 0, len = params.length; i < len; i++) {
        tokens = params[i].split("=");
        if (tokens.length >= 2) {
            if (tokens[0] === paramName) {
                return unescape(tokens[1]);
            }
        }
    }

    return null;
}

function formatQuote(quote) {

    var url = quote.link_type == 'success'?'/success.html':'/research.html';
    var readMore = (quote.link_type == 'success')?'Read more testimonials':'Read more research';
    return '<p>' +
               quote.text +
               '<div class="credit">' + quote.link_text + '</div><a href="' + url +
               '"> ' + readMore + '</a>' +
           '</p>';
}




var _overlay = null;

function showDialog(msg, title, indexVal) {
	if(!indexVal) {
		indexVal = 999;
	}
    if (_overlay) {
        _overlay.hide();
    }
    var closer = '<div style="text-align: center;padding-bottom: 10px;">'
            + '<a class="sexybutton sexysimple" href="#" onclick="closeGeneralDialog();return false;">Close</a></div>';

    closer = '';

    var html = '<div style="padding: 10px;">' + msg + '</div>' + closer;
    var head = '<a href="#" onclick="closeGeneralDialog();return false" class="close"><span>close</span> X</a>' + title;
    YUI().use('overlay', function(Y) {
        _overlay = new Y.Overlay({
            width : "420px",
            centered : true,
            headerContent : head,
            bodyContent : html,
            zIndex : indexVal,
            visible : true
        });

        _overlay.render();
    });
}


var Quotes =
    [
     {
         text:"Really great for students at all achievement levels. It's easy to use, self-explanatory, and encourages students to become independent learners.",
         link_text: 'High School Math Teacher, Illinois',
         link_type:'success'
     },
     {
         text:'Eighth-graders who take an online Algebra I course score higher on end-of-year algebra assessments than other students who take the standard instructor-led math program offered by their schools and are twice as likely to follow an advanced course sequence in high school as their peers.',
         link_text: 'Institute of Education Sciences',
         link_type:'research'
     },
     {
         text:'A new student initially tested into our lowest math course. Not satisfied, she used Catchup Math for three weeks and then retested four course levels higher — a full year and a quarter. This was exactly our purpose for using it.',
         link_text: 'College Instructor, Ohio',
         link_type:'success'
     },
     {
         text:"Use of multi-media to provide alternate forms of representation improves effectiveness. The addition of graphs, diagrams, conceptual activities, and video lessons reduces students' cognitive load.",
         link_text: 'Mayer',
         link_type:'research'
     },
     {
         text:"I've never done so well in a math class before.",
         link_text: 'Formerly Failing Middle School Student, Kentucky',
         link_type:'success'
     },
     {
         text:'A test-reteach-retest model that targets and remediates individual skill weaknesses dramatically accelerates learning.',
         link_text: 'Bloom',
         link_type:'research'
     },
     {
         text:"We used Catchup Math with 30 remedial pre-algebra students. All of the students' scores improved, and five students passed into mainstream classes.",
         link_text: 'Middle School Intervention Specialist, California',
         link_type:'success'
     },
     {
         text:"Last year we used another company's service, which cost us $3000 for just 60 students and the results were not that great. This year we got Catchup Math for 200 students for $950, and the support and results were outstanding.",
         link_text: 'High School Teacher, Washington',
         link_type:'success'
     },
     {
         text:'Technology-infused interventions were the top model predictor of improved high stakes test scores, dropout rate reduction and improved discipline.',
         link_text: 'Greaves',
         link_type:'research'
     },
     {
         text:'Catchup Math shows students that they CAN achieve success in math; it builds up their confidence and self-esteem.',
         link_text: 'High School Principal, Michigan',
         link_type:'success'
     },
     {
         text:'Guiding the student through sample problems that have been worked out is more effective than simply assigning problems to work on their own, and transfers better to test performance.',
         link_text: 'Carroll',
         link_type:'research'
     },
     {
         text:'We had incredible results this summer. The teachers are clamoring for us to buy it for the fall!',
         link_text: 'District Curriculum Coordinator, Texas',
         link_type:'success'
     },
     {
         text:'It worked so well over the last 3 weeks that my students are all caught up and doing their regular work now.',
         link_text: 'Geometry Teacher, New York',
         link_type:'success'
     },
     {
         text:'Online mathematics computer-aided instruction products can be a significant component of a school or college review and remediation program.',
         link_text: 'Berniker',
         link_type:'research'
     },
     {
         text:'As close to perfect as I have found.',
         link_text: 'High School Math Intervention Teacher, Kentucky',
         link_type:'success'
     },
     {
         text:'Students enjoy learning with Catchup Math! In our survey of remedial students using Catchup Math, 76% said they would recommend it to a friend and 81% said they would like to use it themselves again.',
         link_text: 'High School Instructional Technology Coordinator, California',
         link_type:'success'
     },
     {
         text:'We increased the number of students achieving honor-roll status from 13% to 22%.',
         link_text: 'High School Principal, California',
         link_type:'success'
     },
     {
         text:'More than 60 percent of students admitted to California state colleges require remedial education.',
         link_text: 'Early Assessment Program',
         link_type: 'research'
     }
 ];
