import { useEffect, useState } from "react";
import type { SubjectSummary } from "@cm_re/shared-types";
import { listSubjects } from "../../api/client";
import { isModuleInstalled, checkForUpdate } from "../../offline/moduleManager";
import { List, ListItemButton } from "../ui/list";
import { Spinner } from "../ui/spinner";

type SubjectStatus = "not-installed" | "up-to-date" | "update-available";

/**
 * Entry-point widget: lists the available subjects (fetched from the
 * static /modules/index.json for this first pass) and hands the chosen
 * subjectId back to the parent via onSelect, which loads the tutor for
 * that subject (see App.tsx).
 *
 * Also doubles as the "check on open" update check: this is the first
 * thing rendered whenever the app opens (or the student backs out to
 * pick another subject), so it's the natural place to compare every
 * *already-installed* module's version against the server's and flag
 * anything stale — see NEW_DIRECTION.org's module-update discussion.
 */
export default function SubjectSelector({
  onSelect,
  onlyPracticeTestable = false,
}: {
  onSelect: (subjectId: string) => void;
  /** Practice Tests entry point only: drop subjects with no scorable MC content (scorableCount === 0/absent) — e.g. Mini Calculus, Graphing Calculator Practice have nothing to score a test on. Problems browsing shows every subject regardless. */
  onlyPracticeTestable?: boolean;
}) {
  const [subjects, setSubjects] = useState<SubjectSummary[] | null>(null);
  const [statuses, setStatuses] = useState<Record<string, SubjectStatus>>({});
  const [error, setError] = useState(false);

  useEffect(() => {
    listSubjects()
      .then(async (all) => {
        const list = onlyPracticeTestable ? all.filter((s) => (s.scorableCount ?? 0) > 0) : all;
        setSubjects(list);
        const entries = await Promise.all(
          list.map(async (s): Promise<[string, SubjectStatus]> => {
            const installed = await isModuleInstalled(s.subjectId);
            if (!installed) return [s.subjectId, "not-installed"];
            const hasUpdate = await checkForUpdate(s.subjectId).catch(() => false);
            return [s.subjectId, hasUpdate ? "update-available" : "up-to-date"];
          })
        );
        setStatuses(Object.fromEntries(entries));
      })
      .catch(() => setError(true));
  }, [onlyPracticeTestable]);

  if (error) {
    return <p className="text-sm text-red-600">Couldn't load the subject list. Check your connection and try again.</p>;
  }

  if (!subjects) {
    return <Spinner />;
  }

  return (
    <>
      <List>
        {subjects.map((s) => (
          <ListItemButton key={s.subjectId} onClick={() => onSelect(s.subjectId)}>
            <span>{s.title}</span>
            {statuses[s.subjectId] === "update-available" && (
              <span className="rounded-full bg-amber-100 px-2 py-0.5 text-xs font-medium text-amber-800">
                Update available
              </span>
            )}
            {statuses[s.subjectId] === "up-to-date" && (
              <span className="rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-800">
                Installed
              </span>
            )}
            {(statuses[s.subjectId] === "not-installed" || statuses[s.subjectId] === undefined) && (
              <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-500">
                Not installed
              </span>
            )}
          </ListItemButton>
        ))}
      </List>
    </>
  );
}
