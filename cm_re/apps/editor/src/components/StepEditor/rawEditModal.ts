/**
 * Modal popup for editing raw HTML/MathML source.
 *
 * Plain DOM (no React) so TipTap node views can call it directly.
 * Returns a Promise that resolves to the edited string, or null on cancel.
 *
 * Features:
 * - Multi-line monospace textarea with auto-indent
 * - Live rendered preview pane
 * - Tag-insertion toolbar (wraps selection or inserts at cursor)
 * - Ctrl+Enter to apply, Escape to cancel, Tab inserts spaces
 */

interface RawEditOptions {
  title?: string;
  mode?: "html" | "mathml";
}

const TAG_GROUPS: { label: string; tags: { btn: string; open: string; close: string }[] }[] = [
  {
    label: "HTML",
    tags: [
      { btn: "<b>",   open: "<b>",   close: "</b>" },
      { btn: "<i>",   open: "<i>",   close: "</i>" },
      { btn: "<sup>", open: "<sup>", close: "</sup>" },
      { btn: "<sub>", open: "<sub>", close: "</sub>" },
      { btn: "<p>",   open: "<p>",   close: "</p>" },
      { btn: "<span>",open: '<span>',close: "</span>" },
      { btn: "<div>", open: "<div>", close: "</div>" },
      { btn: "<br/>", open: "<br/>", close: "" },
      { btn: "<img>", open: '<img src="" alt="" />', close: "" },
    ],
  },
  {
    label: "MathML",
    tags: [
      { btn: "<math>",  open: "<math>",  close: "</math>" },
      { btn: "<mfrac>", open: "<mfrac>", close: "</mfrac>" },
      { btn: "<msup>",  open: "<msup>",  close: "</msup>" },
      { btn: "<msub>",  open: "<msub>",  close: "</msub>" },
      { btn: "<mn>",    open: "<mn>",    close: "</mn>" },
      { btn: "<mi>",    open: "<mi>",    close: "</mi>" },
      { btn: "<mo>",    open: "<mo>",    close: "</mo>" },
      { btn: "<mrow>",  open: "<mrow>",  close: "</mrow>" },
      { btn: "<msqrt>", open: "<msqrt>", close: "</msqrt>" },
    ],
  },
];

export function openRawEditor(
  html: string,
  opts: RawEditOptions = {},
): Promise<string | null> {
  return new Promise((resolve) => {
    const title = opts.title ?? "Edit raw HTML";

    // --- overlay ---
    const overlay = el("div", "raw-modal-overlay");
    const modal = el("div", "raw-modal");
    overlay.appendChild(modal);

    // --- header ---
    const header = el("div", "raw-modal-header");
    const titleEl = el("div", "raw-modal-title");
    titleEl.textContent = title;
    header.appendChild(titleEl);
    modal.appendChild(header);

    // --- toolbar ---
    const toolbar = el("div", "raw-modal-toolbar");
    for (const group of TAG_GROUPS) {
      const groupEl = el("div", "raw-modal-tag-group");
      const label = el("span", "raw-modal-tag-label");
      label.textContent = group.label;
      groupEl.appendChild(label);
      for (const tag of group.tags) {
        const btn = el("button", "raw-modal-tag-btn") as HTMLButtonElement;
        btn.type = "button";
        btn.textContent = tag.btn;
        btn.title = tag.close ? `Wrap selection in ${tag.btn}` : `Insert ${tag.btn}`;
        btn.addEventListener("click", () => insertTag(textarea, tag.open, tag.close));
        groupEl.appendChild(btn);
      }
      toolbar.appendChild(groupEl);
    }
    modal.appendChild(toolbar);

    // --- body (textarea + preview) ---
    const body = el("div", "raw-modal-body");

    const editorPane = el("div", "raw-modal-editor-pane");
    const textarea = document.createElement("textarea");
    textarea.className = "raw-modal-textarea";
    textarea.spellcheck = false;
    textarea.value = html;
    editorPane.appendChild(textarea);
    body.appendChild(editorPane);

    const previewPane = el("div", "raw-modal-preview-pane");
    const previewLabel = el("div", "raw-modal-preview-label");
    previewLabel.textContent = "Preview";
    previewPane.appendChild(previewLabel);
    const previewContent = el("div", "raw-modal-preview-content");
    previewPane.appendChild(previewContent);
    body.appendChild(previewPane);

    modal.appendChild(body);

    // --- footer ---
    const footer = el("div", "raw-modal-footer");
    const hint = el("span", "raw-modal-hint");
    hint.textContent = "Ctrl+Enter to apply · Escape to cancel · Tab indents";
    footer.appendChild(hint);
    const btnGroup = el("div", "raw-modal-btn-group");
    const cancelBtn = document.createElement("button");
    cancelBtn.type = "button";
    cancelBtn.className = "raw-modal-btn raw-modal-btn-cancel";
    cancelBtn.textContent = "Cancel";
    const applyBtn = document.createElement("button");
    applyBtn.type = "button";
    applyBtn.className = "raw-modal-btn raw-modal-btn-apply";
    applyBtn.textContent = "Apply";
    btnGroup.appendChild(cancelBtn);
    btnGroup.appendChild(applyBtn);
    footer.appendChild(btnGroup);
    modal.appendChild(footer);

    // --- behavior ---
    function updatePreview() {
      previewContent.innerHTML = textarea.value;
    }
    updatePreview();
    textarea.addEventListener("input", updatePreview);

    textarea.addEventListener("keydown", (e) => {
      if (e.key === "Tab") {
        e.preventDefault();
        insertText(textarea, "  ");
      }
      if (e.key === "Enter" && (e.ctrlKey || e.metaKey)) {
        e.preventDefault();
        apply();
      }
    });

    function apply() {
      cleanup();
      resolve(textarea.value);
    }
    function cancel() {
      cleanup();
      resolve(null);
    }
    function cleanup() {
      overlay.removeEventListener("keydown", onKeydown);
      overlay.remove();
    }

    function onKeydown(e: KeyboardEvent) {
      if (e.key === "Escape") {
        e.preventDefault();
        cancel();
      }
    }

    applyBtn.addEventListener("click", apply);
    cancelBtn.addEventListener("click", cancel);
    overlay.addEventListener("keydown", onKeydown);
    overlay.addEventListener("mousedown", (e) => {
      if (e.target === overlay) cancel();
    });

    // --- mount ---
    document.body.appendChild(overlay);
    textarea.focus();
    textarea.setSelectionRange(0, 0);
  });
}

function el(tag: string, cls: string): HTMLElement {
  const e = document.createElement(tag);
  e.className = cls;
  return e;
}

function insertTag(ta: HTMLTextAreaElement, open: string, close: string) {
  const start = ta.selectionStart;
  const end = ta.selectionEnd;
  const selected = ta.value.slice(start, end);
  const replacement = close ? open + selected + close : open;
  ta.setRangeText(replacement, start, end, "end");
  if (close && !selected) {
    ta.selectionStart = ta.selectionEnd = start + open.length;
  }
  ta.focus();
  ta.dispatchEvent(new Event("input"));
}

function insertText(ta: HTMLTextAreaElement, text: string) {
  ta.setRangeText(text, ta.selectionStart, ta.selectionEnd, "end");
  ta.dispatchEvent(new Event("input"));
}
