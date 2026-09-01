package com.catchupmath.cmre.preprocessor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirrors shared-types' McQuestion. Structured single-select
 * multiple-choice question, extracted from the legacy statement HTML
 * by LegacySolutionParser (handles both the `hm_question_def`
 * `<li correct>` dialect and the newer `question_responses` radio
 * dialect — see SOLUTION_INFO.org).
 */
public class McQuestion {
    /** Prompt HTML (cleaned); the choices list is lifted out into `choices`. */
    public String prompt;
    public List<McChoice> choices = new ArrayList<>();
    /** 0-based index of the correct choice; null only when the source marked none correct. */
    public Integer correctIndex;
}
