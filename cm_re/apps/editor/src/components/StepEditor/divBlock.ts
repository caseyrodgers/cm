import { Node } from "@tiptap/core";
import { openRawEditor } from "./rawEditModal";

/**
 * Preserves `<div>` elements as opaque, non-editable block nodes so
 * TipTap doesn't silently strip them and dump their text content into
 * a bare `<p>`. Same class of silent-destruction problem as MathNode
 * (see its doc), but for block-level elements.
 *
 * The immediate trigger: legacy content contains
 * `<div id="hm_flash_widget"><div id="hm_flash_widget_def" style="display: none">…</div></div>`
 * — the `style="display: none"` hides a widget definition from the
 * student. Without this node, TipTap strips the `<div>` wrappers on
 * parse, the `display: none` is lost, and the raw widget-def text
 * becomes visible. Editing ANY field in the same solution triggers
 * a full save, so even an unrelated edit permanently corrupts the
 * statement.
 *
 * Rendered in the editor as a muted, non-editable block showing the
 * raw HTML. Clicking it opens a prompt to edit the source (same
 * pattern as MathNode's click-to-edit).
 */
export const DivBlock = Node.create({
  name: "divBlock",
  group: "block",
  atom: true,
  selectable: true,
  draggable: false,

  addAttributes() {
    return {
      html: {
        default: "<div></div>",
        parseHTML: (el) => el.outerHTML,
        renderHTML: () => ({}),
      },
    };
  },

  parseHTML() {
    return [{
      tag: "div",
      getAttrs: (el) => {
        if (!(el instanceof HTMLElement)) return false;
        if (el.classList.contains("step_content")) return false;
        if (el.id) return null;
        const style = el.getAttribute("style") ?? "";
        if (/display\s*:\s*none/i.test(style)) return null;
        if (/visibility\s*:\s*hidden/i.test(style)) return null;
        return false;
      },
    }];
  },

  renderHTML({ node }) {
    const template = document.createElement("template");
    template.innerHTML = node.attrs.html as string;
    return template.content.firstElementChild ?? document.createElement("div");
  },

  addNodeView() {
    return ({ node, editor, getPos }) => {
      let latestHtml = node.attrs.html as string;

      const wrapper = document.createElement("div");
      wrapper.className = "div-block-node";
      wrapper.contentEditable = "false";
      wrapper.title = "Preserved <div> block — click to edit raw HTML";

      const label = document.createElement("span");
      label.className = "div-block-label";
      label.textContent = "<div>";
      wrapper.appendChild(label);

      const preview = document.createElement("code");
      preview.className = "div-block-preview";
      preview.textContent = latestHtml.length > 120 ? latestHtml.slice(0, 120) + "…" : latestHtml;
      wrapper.appendChild(preview);

      wrapper.addEventListener("click", () => {
        openRawEditor(latestHtml, { title: "Edit HTML block" }).then((next) => {
          if (next == null || next === latestHtml) return;
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
      });

      return {
        dom: wrapper,
        update: (updatedNode) => {
          if (updatedNode.type.name !== "divBlock") return false;
          latestHtml = updatedNode.attrs.html as string;
          preview.textContent = latestHtml.length > 120 ? latestHtml.slice(0, 120) + "…" : latestHtml;
          return true;
        },
      };
    };
  },
});
