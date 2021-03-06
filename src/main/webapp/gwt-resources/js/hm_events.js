/** replacement for YUI events */
var HmEvents = {
    eventTutorInitialized : {
        listeners : [],
        subscribe : function(callBack) {
            var hel = HmEvents.eventTutorInitialized.listeners;
            hel[hel.length] = callBack;
        },
        fire : function(args) {
            var hel = HmEvents.eventTutorInitialized.listeners;
            for ( var i = 0; i < hel.length; i++) {
                hel[i](args,[]);
            }
        }
    },
    
    eventWidgetInitialized : {
        listeners : [],
        subscribe : function(callBack) {
            var hel = HmEvents.eventWidgetInitialized.listeners;
            hel[hel.length] = callBack;
        },
        fire : function(args) {
            var hel = HmEvents.eventWidgetInitialized.listeners;
            for ( var i = 0; i < hel.length; i++) {
                hel[i](args,[]);
            }
        }
    },

    eventTutorLastStep : {
        listeners : [],
        subscribe : function(callBack) {
            var hel = HmEvents.eventTutorLastStep.listeners;
            hel[hel.length] = callBack;
        },
        fire : function() {
            var hel = HmEvents.eventTutorLastStep.listeners;
            for ( var i = 0; i < hel.length; i++) {
                hel[i]([]);
            }
        }
    },

    eventTutorWidgetComplete : {
        listeners : [],
        subscribe : function(callBack) {
            var hel = HmEvents.eventTutorWidgetComplete.listeners;
            hel[hel.length] = callBack;
        },
        fire : function(data, args) {
            var hel = HmEvents.eventTutorWidgetComplete.listeners;
            for ( var i = 0; i < hel.length; i++) {
                //alert("firing eventTutorWidgetComplete: " + hel[i]);
                hel[i](args, [ data ]);
            }
        }
    },

    eventTutorSetComplete : {
        listeners : [],
        subscribe : function(callBack) {
            var hel = HmEvents.eventTutorSetComplete.listeners;
            hel[hel.length] = callBack;
        },
        fire : function(args) {
            var hel = HmEvents.eventTutorSetComplete.listeners;
            for ( var i = 0; i < hel.length; i++) {
                hel[i](null,[args]);
            }
        }
    },

    eventTutorChangeStep : {
        listeners : [],
        subscribe : function(callBack) {
            var hel = HmEvents.eventTutorChangeStep.listeners;
            hel[hel.length] = callBack;
        },
        fire : function(args, tutorWrapper) {
            var hel = HmEvents.eventTutorChangeStep.listeners;
            for ( var i = 0; i < hel.length; i++) {
                hel[i](tutorWrapper,[args]);
            }
        }
    }
}
