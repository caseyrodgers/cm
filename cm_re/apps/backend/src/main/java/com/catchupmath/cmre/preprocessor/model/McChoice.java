package com.catchupmath.cmre.preprocessor.model;

/** Mirrors shared-types' McChoice. One option of a multiple-choice question. */
public class McChoice {
    /** Choice body HTML (cleaned; may embed MathML/img). Legacy "A."/"B." prefixes stripped. */
    public String content;
    /** Per-choice feedback HTML, or null when the legacy source carried none / only a bare Correct|Incorrect marker. */
    public String feedback;

    public McChoice(String content, String feedback) {
        this.content = content;
        this.feedback = feedback;
    }
}
