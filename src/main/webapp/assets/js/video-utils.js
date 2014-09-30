var _assignmentsOverlay=null;
var _videoOverlay=null;
var _webinarOverlay=null;
var _unique=0;

function closeAssignmentsWebinar() {
    _assignmentsOverlay.set("bodyContent", "");  // make sure video stops
    _assignmentsOverlay.hide();
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

function closeTeacherVideo() {
    _videoOverlay.set("bodyContent", "");  // make sure video stops
    _videoOverlay.hide();
    _videoOverlay = null;
}

function showVideo(obj, key) {
	if (isIPadOrIPhone()) {
		alert('Sorry, this content is not available on mobile devices.  You will need to access it from a desktop computer.');
		return;
	}

	var title = obj.innerHTML;
	if (title == null) title = obj;
	
	var videoURI = '';
	var type = '';
	var firstFrame = '';

	var theVideo = getVideo(key);

	if (theVideo != null) {
	    videoURI = theVideo.videoURI;
	    type = theVideo.type;
	    if (title == null || title == '')
	    	title = theVideo.title;
	    firstFrame = theVideo.firstFrame;
	}
	else {
		alert("Sorry, the [" + key + "] video was not found!");
		return;
	}

	if (type == 'mp4')
    	window.open('/training-videos/show-video2.html?video='+videoURI+'&title='+title, '_blank',
			'height=560, width=640, menubar=no, titlebar=yes, status=no, top=200, left=100');
	else if (type == 'flv') {
    	window.open('/training-videos/show-video.html?video='+videoURI+'&title='+title+'&frame='+firstFrame, '_blank',
		'height=560, width=640, menubar=no, titlebar=yes, status=no, top=200, left=100');
    }
	else {
		alert('Sorry, video type [' + type + '] not recognized!');
	}	
}

function showStudentVideo() {
    showTeacherVideo('', 'student-how-to');
}

function showTeacherVideo(obj, key) {

	if (key != 'student-how-to' && key != 'assignments-webinar' && key != 'available-content' && isIPadOrIPhone()) {
		alert('Sorry, this content is not available on mobile devices.  You will need to access it from a desktop computer.');
		return;
	}

	var title = obj.innerHTML;
	if (title == null) {
		title = obj;
	}

	var videoURI = '';
	var type = '';
	var firstFrame = '';

	var theVideo = getVideo(key);

	if (theVideo != null) {
		videoURI = theVideo.videoURI;
		type = theVideo.type;
		if (title == null || title == '')
			title = theVideo.title;
		firstFrame = theVideo.firstFrame;
	}
	else {
		alert("Sorry, the [" + key + "] video was not found!");
		return;
	}

	var html = '';
	var closeFoot = '';
	//alert("videoURI: " + videoURI + ", title: " + title + ", type: " + type + ", frame: " + firstFrame);

    if (key == 'student-how-to' || key == 'assignments-webinar' || key == 'available-content') {
        html = '<iframe src="/training-videos/embedded-wrapper-mp4-html5.html?video=' + encodeURI(videoURI) + '" ' +
        ' width="630" height="500px" scrolling="no" frameborder="no"></iframe>' +
        closeFoot;
    }
    else if (type == 'flv') { 
		html = '<iframe src="/training-videos/embedded-wrapper-flv.html?video=' + encodeURI(videoURI) +
		'&frame=' + encodeURI(firstFrame) + '" ' +
		' width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
		closeFoot;
	}
	else if (type == 'mp4') {
		html = '<iframe src="/training-videos/embedded-wrapper-mp4.html?video=' + encodeURI(videoURI) + '" ' +
		' width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
		closeFoot;
	}
        	
    var head = '<a href="#" onclick="closeTeacherVideo();return false;" class="close"><span>close</span> X</a>' + title;

    var tail = '<a href="#" onclick="closeTeacherVideo();return false;" class="close" style="background-color: white"><span>close</span> X</a>';

    _unique++;

    YUI().use('anim','overlay',
                    function(Y) {
                        _videoOverlay = new Y.Overlay(
                                {
                                    id:"training-video-" + _unique,
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

var _monaOverlay;

function showMonaMotivationalVideo() {
    var closeFoot = '';

    var html = '<iframe src="/motivational_video/" width="320" height="265px" scrolling="no" frameborder="no"></iframe>' +
              closeFoot;

    var head = '<a href="#" onclick="closeMonaVideo();return false" class="close"><span>close</span> X</a>' + "Catchup Math Motivational Video";

    YUI().use('anim','overlay',
                    function(Y) {
                        _monaOverlay = new Y.Overlay(
                                {
                                    id:"mona-video",
                                    width : "328px",
                                    centered : true,
                                    headerContent : head,
                                    bodyContent : html,
                                    zIndex : 2,
                                    visible:true
                                });

                       _monaOverlay.render();
    });
}

/** Closed from close button */
function closeMonaVideo() {
    _monaOverlay.set("bodyContent", "");  // make sure video stops
    _monaOverlay.hide();
}

function getVideo(key) {
	for (var i=0; i<Videos.length; i++) {
		if (key == Videos[i].key) {
			return Videos[i];
	    }
	}
	return null;
}


function closeWebinar() {
    _webinarOverlay.set("bodyContent", "");  // make sure video stops
    _webinarOverlay.hide();
}

function showWebinar() {
    var closeFoot = '';
    var html = '<iframe src="/teacher-training-video/embedded.html" width="800" height="498px" scrolling="no" frameborder="no"></iframe>' +
              closeFoot;

    var head = '<a href="#" onclick="closeWebinar();return false" class="close"><span>close</span> X</a>' + "Teaching With Catchup Math";

    YUI().use('anim','overlay',
                    function(Y) {
                        _webinarOverlay = new Y.Overlay(
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

                       _webinarOverlay.render();
    });
}


var Videos =
  [
     {
	     key:        'assessment-reporting',
	     title:      'Assessment Reporting Tool',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/v2/AssessmentReportingTool-Final2.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-course-tests',
	     title:      'Course Tests',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/v2/Assignments-CourseTests-FInal.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-creating',
	     title:      'How to Create an Assignment',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/v2/Assignments-Creating-090814.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-custom',
	     title:      'Teacher-created Problems',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/v2/Assignments-CPs-9-8-14.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-overview',
	     title:      'Assignments Overview',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/v2/Assignments-Overview-090714-FINAL.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-webinar',
	     title:      'Assignments Overview',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/mp4/assignments-webinar-480.mp4',
         type:       'mp4'
     },
	 {
		 key:        'available-content',
		 title:      'Available Content',
		 firstFrame: ' ',
		 videoURI:   'assets/teacher_videos/mp4/available-content-480.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'ccss-reports',
		 title:      'Common Core Reports',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/v2/CCSSReports-FINAL2.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'custom-programs',
		 title:      'Custom Programs and Quizzes',
		 firstFrame: 'assets/teacher_videos/Custom Programs and quizzes TM/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Custom Programs and quizzes TM/Custom Programs and quizzes TM_controller.swf', 
	     type:       'flv'
 	 },
	 {
		 key:        'export-reports',
		 title:      'Export Reports',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/v2/ExportReport-Final2.mp4', 
	     type:       'mp4'
	 },
     {
	     key:        'highlights',
	     title:      'Highlights',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/v2/HighlightsReports-Final2.mp4',
         type:       'mp4'
     },
	 {
		 key:        'managing-groups',
		 title:      'Managing Groups',
		 firstFrame: 'assets/teacher_videos/Managing Groups/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Managing Groups/Managing Groups_controller.swf', 
	     type:       'flv'
 	 },
	 {
		 key:        'managing-students',
		 title:      'Managing Students',
		 firstFrame: 'assets/teacher_videos/Managing Students/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Managing Students/Managing Students_controller.swf', 
	     type:       'flv'
 	 },
	 {
		 key:        'overview',
		 title:      'Overview of Teacher Resources',
		 firstFrame: 'assets/teacher_videos/Overview of Teacher Resources/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Overview of Teacher Resources/Overview of Teacher Resources_controller.swf', 
	     type:       'flv'
 	 },
	 {
		 key:        'parallel-programs',
		 title:      'Parallel Programs',
		 firstFrame: 'assets/teacher_videos/Using Parallel Programs/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Using Parallel Programs/Using Parallel Programs_controller.swf', 
	     type:       'flv'
 	 },
	 {
		 key:        'registering-students',
		 title:      'Registering Students',
		 firstFrame: 'assets/teacher_videos/Registering Students/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Registering Students/Registering Students_controller.swf', 
	     type:       'flv'
 	 },
	 {
		 key:        'reports-overview',
		 title:      'Overview of Reports',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/v2/ReportsOverview-Final2.mp4',
	     type:       'mp4'
 	 },
 	 {
		 key:        'sample-session',
		 title:      'Sample Student Session',
		 firstFrame: 'assets/teacher_videos/Sample Student Session/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Sample Student Session/Sample Student Session_controller.swf', 
	     type:       'flv'
 	 },
 	 {
		 key:        'special-needs',
		 title:      'At Risk and Special Needs',
		 firstFrame: 'assets/teacher_videos/Special Needs ideas/FirstFrame.png',
		 videoURI:   'assets/teacher_videos/Special Needs ideas/Special Needs ideas_controller.swf', 
	     type:       'flv'
 	 },
	 {
		 key:        'student-detail-history',
		 title:      'Student Detail History',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/v2/StudentDetailHistoryFinal2.mp4',
	     type:       'mp4'
	 },
	 {
		 key:        'student-how-to',
		 title:      'Student How To',
		 firstFrame: 'assets/teacher_videos/student-how-to/student-how-to.png',
		 videoURI:   'assets/teacher_videos/student-how-to/student-how-to-360.mp4',
	     type:       'mp4'
	 },
	 {
		 key:        'student-registration-groups',
		 title:      'Registering Groups and Classes',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/student_registration-groups.mp4',
	     type:       'mp4'
 	 },
	 {
		 key:        'student-registration-quick',
		 title:      'Registering Students - Quick Start',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/StudentRegistrationQuick/student_registration-quick.mp4',
	     type:       'mp4'
 	 },
	 {
		 key:        'technical-tips',
		 title:      'Technical Tips',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/v2/TechTips-Final.mp4',
	     type:       'mp4'
 	 },
	 {
		 key:        'web-links-quick',
		 title:      'Web Links Quick Overview',
		 firstFrame: '',
		 videoURI:   'assets/webinar_weblinks/WebLinksQuick.mp4',
	     type:       'mp4'
	 }
  ];
