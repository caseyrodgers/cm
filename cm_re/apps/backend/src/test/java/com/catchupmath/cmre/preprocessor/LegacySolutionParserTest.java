package com.catchupmath.cmre.preprocessor;

import com.catchupmath.cmre.preprocessor.model.McQuestion;
import com.catchupmath.cmre.preprocessor.model.Solution;
import com.catchupmath.cmre.preprocessor.model.StepUnit;
import com.catchupmath.cmre.preprocessor.model.WidgetSlot;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests capturing three real bugs found running the
 * preprocessor against the actual legacy corpus at scale (846 real
 * solutions, alg1ptests subject, 2026-08-26) — see SOLUTION_INFO.org
 * and NEW_DIRECTION.org's "Legacy content pre-processor" decision.
 * Fixtures under src/test/resources are minimal reproductions of the
 * real files that exposed each bug, not the real files themselves
 * (self-contained — doesn't depend on the external temp/ export tree).
 */
class LegacySolutionParserTest {

    private File resource(String path) throws URISyntaxException {
        return new File(getClass().getClassLoader().getResource(path).toURI());
    }

    /**
     * Bug 1: some exports place trailing content (an <img> + <br/>) as
     * a SIBLING of .step_content, still inside the same stepunit
     * wrapper div but after it closes. Extracting only .step_content's
     * innerHTML silently dropped that image. Also exercises the
     * malformed-attribute recovery (missing closing quote on class=)
     * found in the same real file — confirms the src survives intact.
     */
    @Test
    void capturesContentTrailingAfterStepContentDiv() throws Exception {
        File dir = resource("trailing_sibling_image");
        Solution solution = LegacySolutionParser.parse(dir, "algebra1", dir);

        assertEquals(1, solution.steps.size());
        StepUnit step = solution.steps.get(0);
        assertTrue(step.content.contains("f-10-18-5.gif"), "trailing image src should survive despite the malformed class attribute");
        assertTrue(step.content.contains("plot the points"), "original step text should still be present");
    }

    /**
     * Bug 2: tutor_steps_2.html exports use name="..." instead of
     * id="..." for problem_statement/steps_head_title (not just the
     * stepunit wrappers, which were already handled) — matching only
     * #id selectors silently produced an empty statement and null
     * identification for every _2-variant solution.
     */
    @Test
    void handlesNameAttributeVariant() throws Exception {
        File dir = resource("name_attr_variant");
        Solution solution = LegacySolutionParser.parse(dir, "algebra1", dir);

        assertFalse(solution.statement.isEmpty(), "statement must not be empty for the name= variant");
        assertEquals("chapter2practicetest", solution.identification.set);
        assertEquals("7", solution.identification.problemNumber);
        assertEquals(1, solution.steps.size());
    }

    /**
     * Bug 3: not every solution has a "Set:" label — some templates
     * (radio-button questions, no multiple-choice <li correct>) only
     * have "Problem: N". A regex requiring both together silently
     * dropped problemNumber whenever set was absent. Also exercises
     * chapter/section derivation relative to a fixed subject root
     * rather than a fixed parent-directory depth — this fixture has
     * no intermediate "set" directory between section and the
     * solution dir, which broke a parent-counting approach.
     */
    @Test
    void extractsProblemNumberWithoutSet() throws Exception {
        File subjectRoot = resource("subj");
        File dir = new File(subjectRoot, "1/1/no_set_template");
        Solution solution = LegacySolutionParser.parse(dir, "algebra1", subjectRoot);

        assertNull(solution.identification.set, "this template genuinely has no set");
        assertEquals("5", solution.identification.problemNumber);
        assertEquals("1", solution.identification.chapter);
        assertEquals("1", solution.identification.section);
    }

    /**
     * Multiple-choice support, dialect 1: the older
     * {@code <div class="hm_question_def">} with
     * {@code <li correct="yes|no">} items (~459 of the 846 alg1ptests
     * solutions). The question must be lifted OUT of the statement
     * into structured data, its markup removed from `statement`, and
     * the real worked steps left untouched.
     */
    @Test
    void extractsHmQuestionDefMultipleChoice() throws Exception {
        File dir = resource("mc_li_correct");
        Solution solution = LegacySolutionParser.parse(dir, "algebra1", dir);

        McQuestion q = solution.question;
        assertNotNull(q, "hm_question_def should be lifted into solution.question");
        assertEquals(4, q.choices.size());
        assertEquals(3, q.correctIndex, "the <li correct=\"yes\"> is the 4th choice (index 3)");
        assertTrue(q.prompt.contains("solution set"), "prompt keeps the question text");
        assertNull(q.choices.get(0).feedback, "the &nbsp; feedback cell counts as absent");

        assertFalse(solution.statement.toLowerCase().contains("hm_question_def"),
                "question markup must be gone from the statement");
        assertFalse(solution.statement.toLowerCase().contains("<ul"),
                "the choices list must be gone from the statement");
        assertEquals(1, solution.steps.size(), "real worked steps are untouched");
        assertTrue(solution.steps.get(0).content.contains("answer choice is D"));
    }

    /**
     * Multiple-choice support, dialect 2: the newer
     * {@code <div class="question_stepunit">} with
     * {@code <ul class="question_responses">} radio inputs (~282 of
     * 846). Correctness comes from {@code <input value="true|false">},
     * the "A."/"B." enumerator on each label is stripped (the tutor
     * numbers choices itself), and per-solution image src inside a
     * choice still gets the same rewrite the rest of the content does.
     */
    @Test
    void extractsQuestionResponsesRadioMultipleChoice() throws Exception {
        File dir = resource("mc_radio");
        Solution solution = LegacySolutionParser.parse(dir, "alg1ptests", dir);

        McQuestion q = solution.question;
        assertNotNull(q, "question_stepunit should be lifted into solution.question");
        assertEquals(3, q.choices.size());
        assertEquals(1, q.correctIndex, "the <input value=\"true\"> is the 2nd choice (index 1)");
        assertTrue(q.prompt.contains("absolute value inequality"), "prompt keeps the question text");

        assertFalse(q.choices.get(0).content.contains("A. All real numbers"),
                "leading 'A.' enumerator is stripped from the choice body");
        assertTrue(q.choices.get(0).content.contains("All real numbers less than 0"),
                "the rest of the choice body survives the prefix strip");
        assertNull(q.choices.get(0).feedback,
                "a bare \"Incorrect\" marker is not carried as authored feedback");
        assertTrue(q.choices.get(1).content.contains("/modules/alg1ptests/mc_radio/image016.gif"),
                "per-solution image src is rewritten inside the choice body too");

        assertFalse(solution.statement.toLowerCase().contains("question_responses"));
        assertFalse(solution.statement.toLowerCase().contains("question_stepunit"));
    }

    /**
     * Real bug found investigating a 404 (TUTOR_WIDGET.org, 2026-09-04):
     * a legacy statement can carry a dead
     * {@code <img src=".../tutor_widget_dummy.png">} — a placeholder
     * for an interactive widget slot, with no cm_re asset behind it.
     * The parser must strip it from the statement (it 404s otherwise)
     * and record the slot as structured data instead of silently
     * dropping or silently 404-ing it.
     */
    @Test
    void extractsWidgetSlotFromDummyImageMarker() throws Exception {
        File dir = resource("widget_dummy_image");
        Solution solution = LegacySolutionParser.parse(dir, "alg1ptests", dir);

        WidgetSlot slot = solution.widgetSlot;
        assertNotNull(slot, "the tutor_widget_dummy.png marker should be lifted into solution.widgetSlot");
        assertEquals("whiteboard", slot.type, "the legacy banner told the student to use the whiteboard");

        assertFalse(solution.statement.toLowerCase().contains("tutor_widget_dummy"),
                "the dead image reference must not survive into the statement (it 404s in cm_re)");
        assertFalse(solution.statement.toLowerCase().contains("widget-dummy"));

        // The MC question sharing this statement is unaffected.
        assertNotNull(solution.question, "extracting the widget slot must not disturb the MC question extraction");
        assertEquals(4, solution.question.choices.size());
        assertEquals(3, solution.question.correctIndex);
    }
}
