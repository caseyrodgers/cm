package com.catchupmath.cmre.preprocessor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirrors shared-types' Solution (packages/shared-types/src/solution.ts).
 * This is the pre-processor's OUTPUT shape — the "clean format"
 * solution_editor is allowed to assume (see NEW_DIRECTION.org's
 * "Legacy content pre-processor" decision, 2026-08-26).
 */
public class Solution {
    public String pid;
    public String subjectId;
    public String version = "2.0";
    public String date;
    public String createdBy;
    public Boolean active;
    public Identification identification;
    public String statement;
    public String statementFigure;
    /** Set when the legacy statement embedded a multiple-choice question; null otherwise. */
    public McQuestion question;
    /** Set when the legacy statement asked for a widget in place of interactive content; null otherwise (most solutions). */
    public WidgetSlot widgetSlot;
    public List<StepUnit> steps = new ArrayList<>();
}
