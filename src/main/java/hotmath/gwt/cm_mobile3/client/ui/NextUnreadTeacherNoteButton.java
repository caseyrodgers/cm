package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemAnnotation;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;

public class NextUnreadTeacherNoteButton extends SexyButton implements ClickHandler {
    
    private List<ProblemAnnotation> notes;

    public NextUnreadTeacherNoteButton(List<ProblemAnnotation> list) {
        super("Next Teacher Note");
        addClickHandler(this);
        
        this.notes = list;
    }

    @Override
    public void onClick(ClickEvent event) {
        // goto to top and then remove
        if(notes.size() > 0) {
            ProblemAnnotation note = notes.get(0);
            notes.remove(0);
            History.newItem("assignment_problem:" + note.getAssignKey() + ":" + note.getPid() + ":sw:" + System.currentTimeMillis());
        }
    }
}
