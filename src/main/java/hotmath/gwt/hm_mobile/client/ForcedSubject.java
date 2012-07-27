package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;


/**
 * Provide minimal Hotmath for mobile access.
 * 
 * @author casey
 * 
 */
public class ForcedSubject {
    
    static public ForcedSubject __instance;
    
    String subjects[];
    String subjectName;
    
    public ForcedSubject() {
        __instance = this;
        
        subjectName = CatchupMathMobileShared.getQueryParameter("subject");
        if(subjectName != null && subjectName.length() > 0) {
            if(subjectName.equalsIgnoreCase("advanced")) {
                this.subjectName = "Advanced";
                subjects = new String[]{"Precalculus", "Trigonometry", "Algebra 2", "Calculus"};
            }
            else if(subjectName.equals("middle")) {
                this.subjectName = "Middle School";
                subjects = new String[]{"Middle Math Series", "Pre-Algebra"};
            }
            else {
                this.subjects = new String[]{subjectName};
            }
        }
    }
    
    public boolean isForcedSubject() {
        return subjectName != null;
    }
    
    public String getTitle() {
        return "Hotmath " + this.subjectName;
    }
    
    public String[] getSubjects() {
        return this.subjects;
    }
    public boolean isOnlyOne() {
        return subjects != null && subjects.length == 1;
    }
    
}
