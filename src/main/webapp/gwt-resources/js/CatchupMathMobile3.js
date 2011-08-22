function initStartCmMobile() {}

/**
 * register a listener with Tutor to be notified when last step is reached. This
 * is used to only advance when the solution has actually been viewed.
 * 
 * 
 * gwt_solutionHasBeenViewed is defined GWT source: PrescriptionResourceLessonTutorActivity
 * 
 */
HmEvents.eventTutorLastStep.subscribe(function(x) {
	gwt_solutionHasBeenViewed();
});
