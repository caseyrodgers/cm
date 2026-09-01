package com.catchupmath.cmre.preprocessor;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans one fragment of legacy solution HTML into the well-formed
 * output solution_editor is allowed to assume — see
 * NEW_DIRECTION.org's "Legacy content pre-processor" decision.
 *
 * Structural malformation (e.g. the class="foo id="bar" missing-quote
 * pattern found in ~4% of one subject's solutions, see
 * SOLUTION_INFO.org Q4) is fixed for free by jsoup's tolerant parser +
 * re-serialization — the same recovery a browser's own HTML parser
 * does, verified empirically in apps/tutor's malformed-html.check.ts.
 * What jsoup does NOT fix on its own, and what this class handles
 * explicitly:
 *   - dead "not_used" markup (e.g. the hm_flash_widget div) — dropped
 *   - specialchars icon URLs, rewritten to the app-root path where
 *     cm_re actually serves them (see SOLUTION_INFO.org Q3)
 *   - per-solution image URLs, rewritten from legacy absolute
 *     /help/solutions/... paths to "/modules/{subjectId}/{pid}/{filename}"
 *
 * The image rewrite target is an app-root-ABSOLUTE path, not a bare
 * relative "<pid>/<filename>" (an earlier version used that). Solution
 * HTML is rendered via dangerouslySetInnerHTML with no base-URL
 * scoping (no <base> tag, no iframe) — a relative src resolves against
 * whatever route the SPA is currently on, not against wherever the
 * image actually lives, so it would silently 404 the moment this is
 * rendered from anywhere other than the app root. An absolute path
 * matching the real module storage location (see cm_re/apps/tutor's
 * public/modules/{subjectId}/{pid}/ layout) is required for images to
 * actually resolve once served for real.
 */
public class HtmlCleaner {

    private static final Pattern SPECIALCHARS_SRC = Pattern.compile("^/images/specialchars/(.+)$");
    private static final Pattern PER_SOLUTION_IMAGE_SRC = Pattern.compile("^/help/solutions/.*/([^/]+)$", Pattern.CASE_INSENSITIVE);

    /** Cleans one element in place (removing dead markup, rewriting image URLs) and returns its inner HTML. */
    public static String cleanToInnerHtml(Element root, String subjectId, String pid) {
        removeDeadMarkup(root);
        rewriteImageUrls(root, subjectId, pid);
        return root.html();
    }

    private static void removeDeadMarkup(Element root) {
        // e.g. <div id="hm_flash_widget" class="not_used">&nbsp;</div> — dead
        // legacy markup, never actually rendered (class name says so explicitly).
        Elements deadNodes = root.select(".not_used");
        for (Element dead : deadNodes) {
            dead.remove();
        }
    }

    private static void rewriteImageUrls(Element root, String subjectId, String pid) {
        for (Element img : root.select("img[src]")) {
            String src = img.attr("src");

            Matcher special = SPECIALCHARS_SRC.matcher(src);
            if (special.matches()) {
                img.attr("src", "/specialchars/" + special.group(1));
                continue;
            }

            Matcher perSolution = PER_SOLUTION_IMAGE_SRC.matcher(src);
            if (perSolution.matches()) {
                img.attr("src", "/modules/" + subjectId + "/" + pid + "/" + perSolution.group(1));
            }
        }
    }
}
