import { useEffect, useState } from "react";
import type { ModuleManifest, Solution } from "@cm_re/shared-types";
import { getModuleManifest } from "../../api/client";
import {
  downloadModule,
  isModuleInstalled,
  getSolutionsForModule,
  checkForUpdate,
  removeModule,
} from "../../offline/moduleManager";
import { Card, CardHeader, CardTitle, CardSubtitle, CardContent } from "../ui/card";
import { Button } from "../ui/button";
import { Spinner } from "../ui/spinner";
import { List, ListItemButton } from "../ui/list";
import { solutionTitle } from "../../lib/solutionTitle";
import { compareProblems } from "../../lib/problemOrder";
import { navigate, hashFor } from "../../routing";

type Status = "idle" | "downloading" | "removing" | "error";

/**
 * "Download this subject for offline" UX. Shows approxSizeBytes from the
 * manifest before committing, then drives moduleManager's download flow.
 * Once installed, also reads the solutions back out of IndexedDB and
 * lists them — proving the "so the PWA can read it" half of the loop,
 * not just the write. Also checks for updates (see SubjectSelector's
 * same check) so re-visiting an installed subject offers a refresh —
 * downloadModule() overwrites by primary key either way, so "install"
 * and "update" are the same operation under the hood.
 */
export default function ModuleDownloadPrompt({
  subjectId,
  onOpenSolution,
}: {
  subjectId: string;
  onOpenSolution: (solution: Solution) => void;
}) {
  const [manifest, setManifest] = useState<ModuleManifest | null>(null);
  const [installed, setInstalled] = useState(false);
  const [hasUpdate, setHasUpdate] = useState(false);
  const [solutions, setSolutions] = useState<Solution[] | null>(null); // null = not fetched yet
  const [showAll, setShowAll] = useState(false);
  const [status, setStatus] = useState<Status>("idle");

  useEffect(() => {
    getModuleManifest(subjectId)
      .then(setManifest)
      .catch(() => setStatus("error"));
    isModuleInstalled(subjectId).then(setInstalled);
  }, [subjectId]);

  useEffect(() => {
    if (installed) {
      checkForUpdate(subjectId)
        .then(setHasUpdate)
        .catch(() => setHasUpdate(false));
    }
  }, [installed, subjectId]);

  // The full solution list (all 846, for the real subject) is only
  // fetched once the student actually asks to see it — the practice
  // test is the default path and doesn't need it.
  useEffect(() => {
    if (installed && showAll && solutions === null) {
      getSolutionsForModule(subjectId).then((list) =>
        setSolutions([...list].sort((a, b) => compareProblems(a.pid, b.pid, subjectId)))
      );
    }
  }, [installed, showAll, solutions, subjectId]);

  async function handleDownload() {
    setStatus("downloading");
    try {
      await downloadModule(subjectId);
      setInstalled(true);
      setHasUpdate(false);
      setSolutions(null); // re-fetch (the update may have changed content) if/when the list is shown again
      setStatus("idle");
    } catch {
      setStatus("error");
    }
  }

  async function handleRemove() {
    if (!confirm(`Remove "${subjectId}"? You'll need a connection to download it again.`)) {
      return;
    }
    setStatus("removing");
    try {
      await removeModule(subjectId);
      setInstalled(false);
      setHasUpdate(false);
      setSolutions(null);
      setShowAll(false);
      setStatus("idle");
    } catch {
      setStatus("error");
    }
  }

  if (!manifest) {
    return <Spinner />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{subjectId}</CardTitle>
        <CardSubtitle>
          {manifest.solutionIds.length} solutions &middot; {(manifest.approxSizeBytes / 1024).toFixed(0)} KB
        </CardSubtitle>
      </CardHeader>

      <CardContent>
        {installed ? (
          <>
            <p className="mb-3 text-sm font-medium text-green-700">&#10003; Installed for offline use.</p>

            {/* Two ways into a subject's problems: a short random test
                (the default path), or the complete list on demand. */}
            <div className="mb-3 space-y-2">
              <Button className="w-full" onClick={() => navigate(hashFor.test(subjectId))}>
                Take a 10-question practice test &rarr;
              </Button>
              <Button variant="outline" className="w-full" onClick={() => setShowAll((v) => !v)}>
                {showAll ? "Hide full problem list" : `Show all ${manifest.solutionIds.length} problems`}
              </Button>
            </div>

            {showAll &&
              (solutions === null ? (
                <Spinner />
              ) : (
                <List>
                  {solutions.map((s) => (
                    <ListItemButton key={s.pid} onClick={() => onOpenSolution(s)}>
                      <span>{solutionTitle(s.pid, s.subjectId)}</span>
                      <span aria-hidden className="text-slate-400">&rsaquo;</span>
                    </ListItemButton>
                  ))}
                </List>
              ))}

            {hasUpdate && (
              <Button className="mt-3 w-full" onClick={handleDownload} disabled={status === "downloading"}>
                {status === "downloading" ? <Spinner /> : "Update available — tap to update"}
              </Button>
            )}
            <Button
              variant="outline"
              className="mt-2 w-full"
              onClick={handleRemove}
              disabled={status === "downloading" || status === "removing"}
            >
              {status === "removing" ? <Spinner /> : "Remove download"}
            </Button>
          </>
        ) : (
          <Button className="w-full" onClick={handleDownload} disabled={status === "downloading"}>
            {status === "downloading" ? <Spinner /> : "Download for offline"}
          </Button>
        )}

        {status === "error" && <p className="mt-2 text-sm text-red-600">Something went wrong. Try again.</p>}
      </CardContent>
    </Card>
  );
}
