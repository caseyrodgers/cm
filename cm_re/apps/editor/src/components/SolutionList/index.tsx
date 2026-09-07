import { useEffect, useMemo, useState } from "react";
import {
  listSubjects,
  listSolutions,
  publishModule,
  type SolutionSummary,
} from "../../api/client";

/**
 * Browse/search a subject's solutions and publish the module. Replaces
 * the legacy SolutionSearcherDialog / ListSolutionSearch. Read source
 * is /api/editor/solutions?subject=; "Publish module" recomputes the
 * manifest and copies it into the served web root (the running tutor
 * then sees the version bump).
 */
export default function SolutionList({ onOpen }: { onOpen: (pid: string) => void }) {
  const [subjects, setSubjects] = useState<string[]>([]);
  const [subject, setSubject] = useState("");
  const [rows, setRows] = useState<SolutionSummary[]>([]);
  const [q, setQ] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [publishMsg, setPublishMsg] = useState<string | null>(null);

  useEffect(() => {
    listSubjects()
      .then((s) => {
        setSubjects(s);
        setSubject((cur) => cur || s[0] || "");
      })
      .catch((e) => setError(String(e)));
  }, []);

  useEffect(() => {
    if (!subject) return;
    setBusy(true);
    setError(null);
    setPublishMsg(null);
    listSolutions(subject)
      .then(setRows)
      .catch((e) => setError(String(e)))
      .finally(() => setBusy(false));
  }, [subject]);

  const filtered = useMemo(() => {
    const needle = q.trim().toLowerCase();
    if (!needle) return rows;
    return rows.filter(
      (r) =>
        r.pid.toLowerCase().includes(needle) ||
        r.statementPreview.toLowerCase().includes(needle)
    );
  }, [rows, q]);

  async function doPublish() {
    setPublishMsg("Publishing…");
    try {
      const m = await publishModule(subject);
      const mb = (m.approxSizeBytes / 1024 / 1024).toFixed(1);
      setPublishMsg(
        `Published ${m.subjectId} · version ${m.version} · ${m.solutionIds.length} solutions · ${mb} MB`
      );
    } catch (e) {
      setPublishMsg(`Publish failed: ${e}`);
    }
  }

  return (
    <div>
      <div className="toolbar">
        <label>
          Subject{" "}
          <select value={subject} onChange={(e) => setSubject(e.target.value)}>
            {subjects.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </label>
        <input
          className="search"
          placeholder="filter by pid or statement text…"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <span className="count">{filtered.length} / {rows.length}</span>
        <button onClick={doPublish} disabled={!subject || busy}>Publish module</button>
      </div>

      {publishMsg && <p className="notice">{publishMsg}</p>}
      {error && <p className="error">{error}</p>}
      {busy && <p className="muted">Loading…</p>}

      <ul className="sol-list">
        {filtered.map((r) => (
          <li key={r.pid}>
            <button className="sol-row" onClick={() => onOpen(r.pid)}>
              <span className="sol-pid">{r.pid}</span>
              <span className="sol-tags">
                {r.hasQuestion && <span className="tag tag-mc">MC</span>}
                {r.hasWidget && <span className="tag tag-widget">widget</span>}
                <span className="tag">{r.stepCount} steps</span>
              </span>
              <span className="sol-preview">
                {r.statementPreview || <em className="muted">(no statement text)</em>}
              </span>
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
