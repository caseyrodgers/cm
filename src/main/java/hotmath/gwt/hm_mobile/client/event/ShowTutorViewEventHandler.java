package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_rpc.client.model.ProblemNumber;

import com.google.gwt.event.shared.EventHandler;

public interface ShowTutorViewEventHandler extends EventHandler {
	void showTutor(ProblemNumber book);
}
