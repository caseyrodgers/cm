import { Node } from "@tiptap/core";

/**
 * Preserves an embedded MathML `<math>...</math>` element as an opaque,
 * atomic inline node instead of letting TipTap's plain-text schema
 * silently destroy it. Confirmed empirically (mathml-survival.check.ts)
 * that loading real step content into a bare StarterKit editor collapses
 * "y = -1/8x + 25/8" into unstructured text "y = − 1 8 x + 25 8" — every
 * fraction bar gone, silently, on the very first load. Math is pervasive
 * across this corpus (SOLUTION_INFO.org), so a step editor that doesn't
 * guard against this would corrupt content the first time someone opens
 * a step that has never even been touched.
 *
 * The raw MathML markup round-trips byte-for-byte through
 * parseHTML/renderHTML — the node's only job is to survive intact.
 * Clicking it opens a plain raw-source prompt so an author can still fix
 * a formula by hand. A real WYSIWYG math authoring UI (KaTeX-rendered,
 * LaTeX-in) is a separate, later increment — see SOLUTION_EDITOR.org
 * milestone 4 ("MathEditor node"). This one's scope is narrower and more
 * urgent: never lose what's already there.
 */
export const MathNode = Node.create({
  name: "math",
  group: "inline",
  inline: true,
  atom: true,
  selectable: true,
  draggable: false,

  addAttributes() {
    return {
      html: {
        default: "<math></math>",
        // Only used going INTO the schema (parseHTML) — the DOM
        // attribute-level renderHTML is suppressed (see below) since
        // the node-level renderHTML builds the whole element directly
        // from this string rather than merging it in as an HTML attr.
        parseHTML: (el) => el.outerHTML,
        renderHTML: () => ({}),
      },
    };
  },

  parseHTML() {
    return [{ tag: "math" }];
  },

  // Returning a real DOM Node here (rather than a ["tag", attrs, ...]
  // output spec) is what makes this a byte-for-byte round trip: the
  // stored raw markup is parsed back into actual elements and handed
  // straight to ProseMirror's serializer, no TipTap-rebuilt markup.
  renderHTML({ node }) {
    const template = document.createElement("template");
    template.innerHTML = node.attrs.html as string;
    return template.content.firstElementChild ?? document.createElement("math");
  },

  addNodeView() {
    return ({ node, editor, getPos }) => {
      const wrapper = document.createElement("span");
      wrapper.className = "math-node";
      wrapper.contentEditable = "false";
      wrapper.title = "Click to edit the raw MathML";

      const render = document.createElement("span");
      render.className = "math-node-render";
      render.innerHTML = node.attrs.html as string;
      wrapper.appendChild(render);

      wrapper.addEventListener("click", () => {
        const current = wrapper.dataset.html ?? (node.attrs.html as string);
        // eslint-disable-next-line no-alert -- deliberately minimal for
        // now; a proper inline popover is part of the later MathEditor
        // increment, not this one's job (see class doc above).
        const next = window.prompt("Edit raw MathML source:", current);
        if (next == null || next === current) return;
        const pos = typeof getPos === "function" ? getPos() : null;
        if (pos == null) return;
        editor
          .chain()
          .command(({ tr }) => {
            tr.setNodeMarkup(pos, undefined, { html: next });
            return true;
          })
          .run();
      });

      return {
        dom: wrapper,
        update: (updatedNode) => {
          if (updatedNode.type.name !== "math") return false;
          render.innerHTML = updatedNode.attrs.html as string;
          return true;
        },
      };
    };
  },
});
