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
		showAlert('Sorry, this content is not available on mobile devices.  You will need to access it from a desktop computer.');
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
		showAlert("Sorry, the [" + key + "] video was not found!");
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
		showAlert('Sorry, video type [' + type + '] not recognized!');
	}	
}

function showStudentVideo() {
    showTeacherVideo('', 'student-how-to');
}

function showTeacherVideo(obj, key) {

	if (_videoOverlay != null) {
		showAlert("Sorry, only one video can be viewed at a time.");
		return;
	}

	var theVideo = getVideo(key);

	if (theVideo == null) {
		showAlert("Sorry, the [" + key + "] video was not found!");
		return;
	}
	if (theVideo.type != 'mp4' && isIPadOrIPhone()) {
		showAlert('Sorry, this content is not available on mobile devices.  You will need to access it from a desktop computer.');
		return;
	}

	var title = obj.innerHTML;
	if (title == null) {
		title = obj;
	}

	var videoURI = '';
	var type = '';
	var firstFrame = '';

	videoURI = theVideo.videoURI;
	type = theVideo.type;
	if (title == null || title == '')
		title = theVideo.title;
	firstFrame = theVideo.firstFrame;

	var html = '';
	var closeFoot = '';

    if (type == 'flv') { 
		html = '<iframe src="/training-videos/embedded-wrapper-flv.html?video=' + encodeURI(videoURI) +
		'&frame=' + encodeURI(firstFrame) + '" ' +
		' width="630" height="525px" scrolling="no" frameborder="no"></iframe>' +
		closeFoot;
	}
	else if (type == 'mp4') {
		html = '<iframe src="/training-videos/embedded-wrapper-mp4-html5.html?video=' + encodeURI(videoURI) + '" ' +
		' width="640" height="460px" scrolling="yes" frameborder="no"></iframe>' +
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
                                    width : "653px",
                                    height: "515px",
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

function showAlert(message) {
	if (typeof alertify == 'undefined')
    	alert(message);
	else {
		alert(message);
		//alertify.alert(message);
    }
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
	     key:        'assignments-manual-scoring',
	     title:      'Manual Scoring of Whiteboard Problems',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/mp4/assignments-manual-scoring.mp4',
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
	     key:        'assignments-reporting-options',
	     title:      'Reporting Options',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/mp4/assignments-reporting-options.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-specific-students',
	     title:      'Assignments for Specific Students',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/mp4/assignments-specific-students.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-student-interface',
	     title:      'Student Interface',
	     firstFrame: '',
	     videoURI:   'assets/teacher_videos/mp4/assignments-student-interface.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-update-webinar',
	     title:      'Updates to Assignments (replaced)',
	     firstFrame: '',
	     videoURI:   'assets/webinar/mp4/assignments-update-webinar.mp4',
         type:       'mp4'
     },
     {
	     key:        'assignments-webinar',
	     title:      'Assign Homework or Classswork with Automatic Grading (replaced)',
	     firstFrame: '',
	     videoURI:   'assets/webinar/mp4/assignments-webinar-480.mp4',
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
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/custom-programs-quizzes.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'diverse-student-groups-webinar',
		 title:      'Using Catchup Math with Diverse Student Groups',
		 firstFrame: '',
		 videoURI:   'assets/webinar/mp4/diverse-student-groups-webinar.mp4', 
	     type:       'mp4'
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
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/managing-groups-480.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'managing-students',
		 title:      'Managing Students',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/managing-students-480.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'mobile-webinar',
		 title:      'Student Interface on the iPad',
		 firstFrame: '',
		 videoURI:   'assets/webinar/mp4/mobile-webinar.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'overview',
		 title:      'Overview of Teacher Resources',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/teacher-resources-overview-480.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'parallel-programs',
		 title:      'Parallel Programs',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/parallel-programs-480.mp4', 
	     type:       'mp4'
 	 },
	 {
		 key:        'registering-students',
		 title:      'Registering Students',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/registering-students-480.mp4', 
	     type:       'mp4'
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
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/sample-student-session-480.mp4', 
	     type:       'mp4'
 	 },
 	 {
		 key:        'special-needs',
		 title:      'At Risk and Special Needs',
		 firstFrame: '',
		 videoURI:   'assets/teacher_videos/mp4/special-needs.mp4', 
	     type:       'mp4'
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
