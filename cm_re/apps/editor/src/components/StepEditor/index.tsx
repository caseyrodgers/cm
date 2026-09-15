import { useEffect, useState } from "react";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
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
 * Controlled from the outside: `content` is the source of truth (the
 * parent, SolutionView, owns the actual step array and Save button);
 * `onChange` fires on every edit with the editor's current HTML. Content
 * is only pushed back INTO the editor when it changes and the editor
 * isn't focused, so a parent re-render never fights the user's typing.
 */
export default function StepEditor({
  content,
  onChange,
}: {
  content: string;
  onChange: (html: string) => void;
}) {
  const editor = useEditor({
    extensions: [StarterKit, MathNode],
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

  // Resync if the step's content is replaced out from under us (e.g. a
  // different solution loaded into a remounted-but-reused instance) —
  // never while the user is actively editing this exact editor.
  useEffect(() => {
    if (!editor || editor.isFocused) return;
    if (editor.getHTML() === content) return;
    editor.commands.setContent(content, false);
  }, [content, editor]);

  if (!editor) return null;

  return (
    <div className="step-editor">
      <div className="step-editor-toolbar">
        <ToolbarButton
          label="B"
          title="Bold"
          active={editor.isActive("bold")}
          onClick={() => editor.chain().focus().toggleBold().run()}
        />
        <ToolbarButton
          label="I"
          title="Italic"
          active={editor.isActive("italic")}
          onClick={() => editor.chain().focus().toggleItalic().run()}
        />
        <ToolbarButton
          label="• List"
          title="Bullet list"
          active={editor.isActive("bulletList")}
          onClick={() => editor.chain().focus().toggleBulletList().run()}
        />
        <ToolbarButton
          label="1. List"
          title="Ordered list"
          active={editor.isActive("orderedList")}
          onClick={() => editor.chain().focus().toggleOrderedList().run()}
        />
        <ToolbarButton label="Undo" title="Undo" onClick={() => editor.chain().focus().undo().run()} />
        <ToolbarButton label="Redo" title="Redo" onClick={() => editor.chain().focus().redo().run()} />
      </div>
      <EditorContent editor={editor} className="step-editor-content" />
    </div>
  );
}

function ToolbarButton({
  label,
  title,
  active,
  onClick,
}: {
  label: string;
  title: string;
  active?: boolean;
  onClick: () => void;
}) {
  return (
    <button
      type="button"
      title={title}
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
