import { useEffect, useState } from "react";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Image from "@tiptap/extension-image";
import { MathNode } from "./mathNode";

/**
 * TipTap-based rich content editor for one SolutionStep's HTML.
 * Replaces the legacy HtmlEditorApplet/TinyMCE step editor
 * (hotmath.gwt.solution_editor.client.SolutionStepEditor.java).
 *
 * StarterKit alone would silently destroy any embedded MathML on load
 * (confirmed empirically — see mathml-survival.check.ts: a real fraction
 * collapses into unstructured plain text the moment it's parsed into the
 * schema). MathNode guards against that: `<math>` elements round-trip
 * byte-for-byte as an opaque, click-to-edit-raw-source node. A real
 * KaTeX-rendered/LaTeX-in math authoring UI is a separate, later
 * increment (SOLUTION_EDITOR.org milestone 4) — this only has to not
 * lose what's already there.
 *
 * Same silent-destruction problem hit `<img>` (found 2026-09-19, real
 * pid: a chapter-practice-test question whose 4 MC choices are pure
 * diagram images, zero text — see SOLUTION_INFO.org's ~43%-of-corpus
 * image finding). Without an Image node in the schema, ProseMirror's
 * HTML parser drops any `<img>` on load with no error — the editor
 * looked "blank" for image-only content, and saving would have
 * permanently deleted the image reference from the real content.
 * `@tiptap/extension-image` fixes it the standard way: `src` is already
 * app-root-absolute (`/modules/<subjectId>/<pid>/<basename>`, per
 * SOLUTION_INFO.org's image-URL-rewrite fix) so it resolves unchanged
 * regardless of the editor being served at `/editor/`.
 *
 * Controlled from the outside: `content` is the source of truth (the
 * parent, SolutionView, owns the actual step array and Save button);
 * `onChange` fires on every edit with the editor's current HTML. Content
 * is only pushed back INTO the editor when it changes and the editor
 * isn't focused, so a parent re-render never fights the user's typing.
 *
 * "View HTML" (added 2026-09-19, Casey: "we need to be able to get to
 * the raw html/mathml in question steps") — MathNode's click-to-edit
 * prompt only reaches one `<math>` element at a time; there was no way
 * to see or hand-edit a step's *whole* markup (surrounding tags, an
 * `<img>`'s attributes, anything the rich toolbar has no button for).
 * Toggling swaps the WYSIWYG view for a plain `<textarea>` of the exact
 * current HTML (`editor.getHTML()`) — Apply parses it back through the
 * same schema (so it also round-trips MathML/images correctly) and
 * calls onChange; Cancel discards the textarea edits untouched.
 */
export default function StepEditor({
  content,
  onChange,
}: {
  content: string;
  onChange: (html: string) => void;
}) {
  const editor = useEditor({
    extensions: [StarterKit, MathNode, Image],
    content,
    onUpdate: ({ editor }) => onChange(editor.getHTML()),
  });

  // TipTap v2's useEditor doesn't re-render on selection/content
  // changes on its own (that's what v3's useEditorState hook is for;
  // not available on the ^2 pin here) — force one so toolbar
  // active-states (bold/italic/list) track the cursor.
  const [, bump] = useState(0);
  useEffect(() => {
    if (!editor) return;
    const rerender = () => bump((n) => n + 1);
    editor.on("transaction", rerender);
    return () => {
      editor.off("transaction", rerender);
    };
  }, [editor]);

  const [rawMode, setRawMode] = useState(false);
  const [rawHtml, setRawHtml] = useState("");

  // Resync if the step's content is replaced out from under us (e.g. a
  // different solution loaded into a remounted-but-reused instance) —
  // never while the user is actively editing this exact editor (rich OR
  // raw — an open raw-HTML edit shouldn't get silently discarded either).
  useEffect(() => {
    if (!editor || editor.isFocused || rawMode) return;
    if (editor.getHTML() === content) return;
    editor.commands.setContent(content, false);
  }, [content, editor, rawMode]);

  if (!editor) return null;

  function enterRawMode() {
    setRawHtml(editor!.getHTML());
    setRawMode(true);
  }

  function applyRawMode() {
    editor!.commands.setContent(rawHtml, false);
    onChange(rawHtml);
    setRawMode(false);
  }

  function cancelRawMode() {
    setRawMode(false);
  }

  return (
    <div className="step-editor">
      <div className="step-editor-toolbar">
        <ToolbarButton
          label="B"
          title="Bold"
          active={editor.isActive("bold")}
          disabled={rawMode}
          onClick={() => editor.chain().focus().toggleBold().run()}
        />
        <ToolbarButton
          label="I"
          title="Italic"
          active={editor.isActive("italic")}
          disabled={rawMode}
          onClick={() => editor.chain().focus().toggleItalic().run()}
        />
        <ToolbarButton
          label="• List"
          title="Bullet list"
          active={editor.isActive("bulletList")}
          disabled={rawMode}
          onClick={() => editor.chain().focus().toggleBulletList().run()}
        />
        <ToolbarButton
          label="1. List"
          title="Ordered list"
          active={editor.isActive("orderedList")}
          disabled={rawMode}
          onClick={() => editor.chain().focus().toggleOrderedList().run()}
        />
        <ToolbarButton label="Undo" title="Undo" disabled={rawMode} onClick={() => editor.chain().focus().undo().run()} />
        <ToolbarButton label="Redo" title="Redo" disabled={rawMode} onClick={() => editor.chain().focus().redo().run()} />
        <ToolbarButton
          label="</>"
          title="View / edit the raw HTML (and MathML) source"
          active={rawMode}
          onClick={() => (rawMode ? cancelRawMode() : enterRawMode())}
          testId="raw-html-toggle"
        />
      </div>
      {rawMode ? (
        <div className="step-editor-raw">
          <textarea
            className="step-editor-raw-textarea"
            data-testid="raw-html-textarea"
            value={rawHtml}
            onChange={(e) => setRawHtml(e.target.value)}
            spellCheck={false}
          />
          <div className="step-editor-raw-actions">
            <button type="button" data-testid="raw-html-cancel" onClick={cancelRawMode}>
              Cancel
            </button>
            <button type="button" data-testid="raw-html-apply" className="primary" onClick={applyRawMode}>
              Apply
            </button>
          </div>
        </div>
      ) : (
        <EditorContent editor={editor} className="step-editor-content" />
      )}
    </div>
  );
}

function ToolbarButton({
  label,
  title,
  active,
  disabled,
  testId,
  onClick,
}: {
  label: string;
  title: string;
  active?: boolean;
  disabled?: boolean;
  testId?: string;
  onClick: () => void;
}) {
  return (
    <button
      type="button"
      title={title}
      disabled={disabled}
      data-testid={testId}
      className={active ? "active" : undefined}
      // Keep the editor's own selection/focus intact — otherwise
      // clicking a toolbar button first steals focus from the editor,
      // and the command would apply to no selection.
      onMouseDown={(e) => e.preventDefault()}
      onClick={onClick}
    >
      {label}
    </button>
  );
}
