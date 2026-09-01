package com.catchupmath.cmre.preprocessor.model;

import java.util.List;

/** Mirrors shared-types' StepUnit. Legacy TutorStepUnit.Role is only ever HINT or STEP. */
public class StepUnit {
    public String role; // "hint" | "step"
    public String content;
    public String figure;
    public List<String> figures;

    public StepUnit(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
