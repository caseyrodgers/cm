package hotmath.cm.assignment;

public class AssignmentNotFoundException extends Exception {
    public AssignmentNotFoundException(int assKey, Exception e) {
        super("Assignment not found: " + assKey, e);
    }
}
