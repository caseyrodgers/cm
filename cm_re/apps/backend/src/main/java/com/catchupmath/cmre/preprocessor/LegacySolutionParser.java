package com.catchupmath.cmre.preprocessor;

import com.catchupmath.cmre.preprocessor.model.Identification;
import com.catchupmath.cmre.preprocessor.model.McChoice;
import com.catchupmath.cmre.preprocessor.model.McQuestion;
import com.catchupmath.cmre.preprocessor.model.Solution;
import com.catchupmath.cmre.preprocessor.model.StepUnit;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses one legacy solution export directory into the clean Solution
 * shape. See SOLUTION_INFO.org for the real data this was built
 * against and verified at scale (846 real solutions, alg1ptests
 * subject, 2026-08-26).
 */
public class LegacySolutionParser {

    // Set and Problem are each optional and independent — NOT every
    // solution has a "set" (verified: alg1ptests_10_1__5_10 has only
    // "Problem: 5" in its title, no "Set:" at all — a different problem
    // template, radio-button questions rather than the <li correct>
    // multiple-choice pattern). Matching them separately means a
    // solution missing one doesn't also lose the other.
    private static final Pattern SET = Pattern.compile("Set:\\s*(\\S+)");
    private static final Pattern PROBLEM_NUMBER = Pattern.compile("Problem:\\s*(\\S+)");

    /**
     * @param subjectRoot the root directory findSolutionDirs() was
     *   originally invoked on for this whole batch — chapter/section
     *   are derived as the first two path segments below this root,
     *   NOT as a fixed number of parent-directory hops above
     *   solutionDir. Verified necessary: some solutions sit directly
     *   under chapter/section with no intermediate "set" directory
     *   (alg1ptests_10_1__5_10), which broke a fixed-depth
     *   parent-counting approach — it landed on the wrong directory
     *   entirely for those.
     */
    public static Solution parse(File solutionDir, String subjectId, File subjectRoot) throws IOException {
        File tutorSteps = new File(solutionDir, "tutor_steps.html");
        if (!tutorSteps.exists()) {
            tutorSteps = new File(solutionDir, "tutor_steps_2.html");
        }
        if (!tutorSteps.exists()) {
            throw new IOException("no tutor_steps(.html|_2.html) found in " + solutionDir);
        }

        Document doc = Jsoup.parse(tutorSteps, "UTF-8");

        Solution solution = new Solution();
        solution.subjectId = subjectId;
        solution.pid = derivePid(solutionDir);
        solution.identification = parseIdentification(doc, solutionDir, subjectId, subjectRoot);

        // id vs name: tutor_steps.html uses id="problem_statement",
        // tutor_steps_2.html uses name="problem_statement" — verified
        // this inconsistency isn't confined to the stepunit wrappers
        // (see the dual id/name select below); it affects every key
        // element the _2 variant carries. Match both throughout.
        Element statementEl = doc.selectFirst("[id=problem_statement], [name=problem_statement]");
        if (statementEl != null) {
            // Lift any embedded multiple-choice question OUT of the
            // statement into structured data, and drop its markup from
            // the statement, BEFORE cleaning statement to HTML — so
            // `statement` ends up holding only the surrounding prose and
            // the tutor never has to parse either legacy MC dialect at
            // runtime. See extractQuestion / SOLUTION_INFO.org.
            solution.question = extractQuestion(statementEl, subjectId, solution.pid);
            solution.statement = HtmlCleaner.cleanToInnerHtml(statementEl, subjectId, solution.pid);
        } else {
            solution.statement = "";
        }

        // Matches by [steprole] attribute rather than id vs name, so this
        // works against both tutor_steps.html (id="stepunit-N") and
        // tutor_steps_2.html (name="stepunit-N") without caring which
        // variant a given export used.
        //
        // IMPORTANT: take the whole stepunit wrapper's content, not just
        // .step_content's — some exports (verified in
        // alg1ptests_10_1_Chapter10PracticeTest_18_10) place trailing
        // content (an <img> + <br/>) as a SIBLING of .step_content,
        // still inside the same stepunit wrapper div but after it
        // closes. Extracting only .step_content silently dropped that
        // trailing image. The step_title-N div is always empty in every
        // export examined, so it's dropped rather than carried along as
        // meaningless markup.
        Elements stepUnitEls = doc.select("[steprole]");
        for (Element stepUnitEl : stepUnitEls) {
            String role = stepUnitEl.attr("steprole"); // "hint" or "step"
            stepUnitEl.select("[id^=step_title], [name^=step_title]").remove();
            String content = HtmlCleaner.cleanToInnerHtml(stepUnitEl, subjectId, solution.pid);
            solution.steps.add(new StepUnit(role, content));
        }

        return solution;
    }

    // Leading enumerator on a Format-A choice label: "A. ", "b) ", etc.
    // Always a single letter in the real corpus; kept tight so it can't
    // eat a real one-letter answer like "x)".
    private static final Pattern CHOICE_LETTER_PREFIX = Pattern.compile("^\\s*[A-Za-z][.)]\\s+");

    /**
     * Pulls a multiple-choice question out of the statement element,
     * mutating {@code statementEl} to remove the question's markup, and
     * returns it as structured data. Returns null (leaving the
     * statement as prose) when there's no usable embedded question.
     *
     * Three real markup shapes in the alg1ptests corpus (verified
     * 2026-08-27):
     *
     *   Format A — {@code <div class="question_stepunit">} with
     *   {@code <ul class="question_responses">}: each {@code <li>} holds
     *   a radio {@code <input value="true|false">}, a {@code <label>}
     *   with the (letter-prefixed) choice body, and a
     *   {@code <div style="display:none">} bare "Correct"/"Incorrect"
     *   marker. ~282 of 846.
     *
     *   Format B — a {@code <ul>} whose {@code <li correct="yes|no">}
     *   items each carry two child {@code <div>}s (choice body, then a
     *   feedback cell that in practice is always {@code &nbsp;}). Wrapped
     *   in {@code <div class="hm_question_def">} in the common case
     *   (~459), but the tutor_steps_2.html variant sometimes drops that
     *   class and leaves a bare {@code <div>} — so detection keys off
     *   {@code li[correct]}, not the wrapper class.
     *
     *   Degenerate — {@code <div class="hm_question_def">} with a prompt
     *   but no choices list at all (export dropped it). Not a usable
     *   question: the wrapper is unwrapped so the prompt survives as
     *   plain statement prose, and null is returned.
     */
    private static McQuestion extractQuestion(Element statementEl, String subjectId, String pid) {
        boolean formatA;
        Element list = statementEl.selectFirst("ul.question_responses");
        if (list != null) {
            formatA = true;
        } else {
            formatA = false;
            for (Element ul : statementEl.select("ul")) {
                if (!ul.children().select("li[correct]").isEmpty()) {
                    list = ul;
                    break;
                }
            }
        }
        if (list == null) {
            // No choices list. If a question wrapper is sitting there
            // empty-handed, unwrap it so its prompt text isn't lost and
            // the dead class doesn't leak into the statement.
            Element orphan = statementEl.selectFirst("div.hm_question_def, div.question_stepunit");
            if (orphan != null) {
                orphan.unwrap();
            }
            return null;
        }

        McQuestion question = new McQuestion();
        int index = 0;
        for (Element li : list.children().select("li")) {
            McChoice choice = formatA ? parseRadioChoice(li, subjectId, pid) : parseLiCorrectChoice(li, subjectId, pid);
            if (choice == null) {
                continue;
            }
            question.choices.add(choice);
            boolean correct = formatA
                    ? "true".equalsIgnoreCase(attrOfFirst(li, "input", "value"))
                    : "yes".equalsIgnoreCase(li.attr("correct"));
            if (correct && question.correctIndex == null) {
                question.correctIndex = index;
            }
            index++;
        }
        if (question.choices.isEmpty()) {
            return null;
        }

        // Prompt = everything around the choices list. Prefer the
        // question wrapper div; fall back to the list's parent; last
        // resort (list is a direct child of the statement) gather the
        // list's preceding siblings.
        Element wrapper = list.closest("div.hm_question_def, div.question_stepunit");
        if (wrapper == null && list.parent() != null && list.parent() != statementEl) {
            wrapper = list.parent();
        }
        if (wrapper != null) {
            list.remove();
            question.prompt = HtmlCleaner.cleanToInnerHtml(wrapper, subjectId, pid);
            wrapper.remove();
        } else {
            Element holder = new Element("div");
            Element sib = list.previousElementSibling();
            while (sib != null) {
                Element prev = sib.previousElementSibling();
                holder.prependChild(sib); // reparents out of statementEl
                sib = prev;
            }
            list.remove();
            question.prompt = HtmlCleaner.cleanToInnerHtml(holder, subjectId, pid);
        }

        return question;
    }

    private static McChoice parseLiCorrectChoice(Element li, String subjectId, String pid) {
        Elements divs = li.children().select("div");
        if (divs.isEmpty()) {
            return null;
        }
        String content = HtmlCleaner.cleanToInnerHtml(divs.get(0), subjectId, pid);
        String feedback = divs.size() > 1 ? blankToNull(divs.get(1).text()) : null;
        return new McChoice(content, feedback);
    }

    private static McChoice parseRadioChoice(Element li, String subjectId, String pid) {
        Element label = li.selectFirst("label");
        Element body = label != null ? label : li;

        // Strip the "A." / "B." enumerator from the leading text node so
        // the tutor's own position-based labels don't double up. Only
        // the first text node, so inline markup (a leading <img>, MathML)
        // is left intact.
        Element firstBlock = body.selectFirst("p");
        Element prefixHost = firstBlock != null ? firstBlock : body;
        if (prefixHost.childNodeSize() > 0 && prefixHost.childNode(0) instanceof TextNode) {
            TextNode tn = (TextNode) prefixHost.childNode(0);
            tn.text(CHOICE_LETTER_PREFIX.matcher(tn.getWholeText()).replaceFirst(""));
        }

        String content = HtmlCleaner.cleanToInnerHtml(body, subjectId, pid);

        // Format A's trailing <div> is a bare "Correct"/"Incorrect"
        // marker, not authored feedback — don't carry it as feedback.
        String last = null;
        Elements divs = li.children().select("div");
        if (!divs.isEmpty()) {
            String t = divs.last().text().trim();
            if (!t.equalsIgnoreCase("correct") && !t.equalsIgnoreCase("incorrect")) {
                last = blankToNull(t);
            }
        }
        return new McChoice(content, last);
    }

    private static String attrOfFirst(Element scope, String selector, String attr) {
        Element el = scope.selectFirst(selector);
        return el != null ? el.attr(attr) : "";
    }

    /** Treats an empty string or a whitespace/&nbsp;-only string as absent. */
    private static String blankToNull(String s) {
        if (s == null) {
            return null;
        }
        String cleaned = s.strip(); // Java 11+ strip() is Unicode-aware: drops &nbsp; (U+00A0) too
        return cleaned.isEmpty() ? null : cleaned;
    }

    /** Matches the pid convention already established by index.html's redirect and the real hand-conversion: lowercase directory name. */
    private static String derivePid(File solutionDir) {
        return solutionDir.getName().toLowerCase();
    }

    private static Identification parseIdentification(Document doc, File solutionDir, String subjectId, File subjectRoot) throws IOException {
        Identification id = new Identification();
        id.book = subjectId;

        Path relative = subjectRoot.toPath().relativize(solutionDir.toPath());
        if (relative.getNameCount() >= 1) {
            id.chapter = relative.getName(0).toString();
        }
        if (relative.getNameCount() >= 2) {
            id.section = relative.getName(1).toString();
        }

        Element titleEl = doc.selectFirst("[id=steps_head_title], [name=steps_head_title]");
        if (titleEl != null) {
            String text = titleEl.text();
            Matcher setMatcher = SET.matcher(text);
            if (setMatcher.find()) {
                id.set = setMatcher.group(1);
            }
            Matcher problemMatcher = PROBLEM_NUMBER.matcher(text);
            if (problemMatcher.find()) {
                id.problemNumber = problemMatcher.group(1);
            }
        }

        File tutorDataFile = new File(solutionDir, "tutor_data.js");
        if (tutorDataFile.exists()) {
            String json = Files.readString(tutorDataFile.toPath(), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("tutorProperties")) {
                JsonObject props = root.getAsJsonObject("tutorProperties");
                if (props.has("_textCode") && !props.get("_textCode").isJsonNull()) {
                    id.book = props.get("_textCode").getAsString();
                }
            }
        }

        return id;
    }
}
