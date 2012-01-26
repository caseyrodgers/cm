package hotmath.assessment;

import sb.util.SbException;

public class SbExceptionNoLessonRppsFound extends SbException {
    
    public SbExceptionNoLessonRppsFound(int gradeLevel, InmhItemData itemData) {
        super(itemData + ": " + "could not find RPPs matching absolute grade level '" + gradeLevel + "'");
    }
}
