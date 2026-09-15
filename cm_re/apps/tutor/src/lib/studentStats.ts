import { db } from "../offline/db";
import { listSubjects, getModuleManifest } from "../api/client";
import {
  getCorrectTotal,
  getAnsweredTotal,
  getViewedTotal,
  resetCorrectTotal,
  resetAnsweredTotal,
  resetViewedTotal,
} from "./correctCount";
import { scoreTest, testTitle } from "../offline/practiceTestStore";
import { getChapterMastery, resetChapterMastery, type ChapterMastery } from "./chapterMastery";

/**
 * The current student is whoever's using this browser — everything is
 * in localStorage + IndexedDB, no account. This aggregates it into one
 * view for the `#/me` screen, and offers a reset.
 */

const GRADE_KEY = "cm_re.learn.grade";

export interface SubjectStat {
  subjectId: string;
  title: string;
  /** the active/last test for this subject, if any */
  test?: {
    title: string;
    finished: boolean;
    correct: number;
    total: number;
    answered: number;
  };
  /** Lifetime per-chapter mastery, worst-first. Empty when no test for this subject has ever been finished. */
  chapters: ChapterMastery[];
}

export interface StudentStats {
  correctTotal: number;
  /** every question answered, right or wrong — the denominator for correctTotal */
  answeredTotal: number;
  /** every problem viewed (opened in the full statement+steps view) */
  viewedTotal: number;
  grade: string | null;
  whiteboardCount: number;
  subjects: SubjectStat[];
}

export async function getStudentStats(): Promise<StudentStats> {
  const [practiceTests, whiteboardCount, subjectList] = await Promise.all([
    db.practiceTests.toArray(),
    db.whiteboards.count(),
    listSubjects().catch(() => [] as { subjectId: string; title: string }[]),
  ]);

  const testById = new Map(practiceTests.map((t) => [t.subjectId, t]));
  const titleById = new Map(subjectList.map((s) => [s.subjectId, s.title]));

  // Candidate subjects: the whole catalog (for title lookup) plus any
  // subject with a test but no catalog entry (removed upstream since).
  const ids = new Set<string>([...titleById.keys(), ...testById.keys()]);

  const sortedIds = [...ids].sort();
  const chaptersById = new Map(
    await Promise.all(sortedIds.map(async (id): Promise<[string, ChapterMastery[]]> => [id, await getChapterMastery(id)]))
  );

  // Download status/size moved to Hub (see getSubjectDownloadStats) —
  // #/me is personal performance only now, so a subject with neither a
  // test nor any chapter history has nothing to show here and is
  // dropped rather than listed bare.
  const subjects: SubjectStat[] = sortedIds
    .map((subjectId) => {
      const t = testById.get(subjectId);
      const stat: SubjectStat = {
        subjectId,
        title: titleById.get(subjectId) ?? subjectId,
        chapters: chaptersById.get(subjectId) ?? [],
      };
      if (t) {
        const s = scoreTest(t);
        stat.test = {
          title: testTitle(t.scope),
          finished: t.completedAt != null,
          correct: s.correct,
          total: s.total,
          answered: s.answered,
        };
      }
      return stat;
    })
    .filter((s) => s.test || s.chapters.length > 0);

  let grade: string | null = null;
  try {
    grade = localStorage.getItem(GRADE_KEY);
  } catch {
    /* ignore */
  }

  return {
    correctTotal: getCorrectTotal(),
    answeredTotal: getAnsweredTotal(),
    viewedTotal: getViewedTotal(),
    grade,
    whiteboardCount,
    subjects,
  };
}

/** "512 KB" / "4.2 MB" — used for any installed-content size display (Hub). */
export function formatSize(bytes: number): string {
  return bytes >= 1024 * 1024 ? `${(bytes / 1024 / 1024).toFixed(1)} MB` : `${Math.round(bytes / 1024)} KB`;
}

export interface SubjectDownloadStat {
  subjectId: string;
  title: string;
  installed: boolean;
  /** null when the size couldn't be determined — offline and not yet installed, so no manifest to read. */
  approxSizeBytes: number | null;
}

/**
 * Every subject in the catalog, installed or not, with its download
 * size — Hub's "all the subjects and their download stats" list. An
 * installed subject's size is read straight from IndexedDB (already
 * known, no network); a not-yet-installed one costs one small
 * manifest.json fetch per subject (cheap, static files) so the list
 * shows a real size before committing to a download, same number
 * ModuleDownloadPrompt shows once you're inside that subject.
 */
export async function getSubjectDownloadStats(): Promise<SubjectDownloadStat[]> {
  const [installedModules, subjectList] = await Promise.all([
    db.modules.toArray(),
    listSubjects().catch(() => [] as { subjectId: string; title: string }[]),
  ]);

  const installedById = new Map(installedModules.map((m) => [m.subjectId, m]));
  const titleById = new Map(subjectList.map((s) => [s.subjectId, s.title]));

  // Catalog order first (matches the pickers), then any installed
  // subject the catalog doesn't mention (e.g. since removed upstream).
  const orderedIds = subjectList.map((s) => s.subjectId);
  for (const id of installedById.keys()) {
    if (!orderedIds.includes(id)) orderedIds.push(id);
  }

  return Promise.all(
    orderedIds.map(async (subjectId): Promise<SubjectDownloadStat> => {
      const mod = installedById.get(subjectId);
      const title = titleById.get(subjectId) ?? subjectId;
      if (mod) {
        return { subjectId, title, installed: true, approxSizeBytes: mod.approxSizeBytes ?? null };
      }
      try {
        const manifest = await getModuleManifest(subjectId);
        return { subjectId, title, installed: false, approxSizeBytes: manifest.approxSizeBytes };
      } catch {
        return { subjectId, title, installed: false, approxSizeBytes: null };
      }
    })
  );
}

/**
 * Wipes this student's progress: the correct-answer tally, every
 * practice test, and every whiteboard. Deliberately keeps downloaded
 * modules (that's storage, not progress) and the grade preference.
 */
export async function resetStats(): Promise<void> {
  resetCorrectTotal();
  resetAnsweredTotal();
  resetViewedTotal();
  await db.transaction("rw", db.practiceTests, db.whiteboards, db.chapterStats, async () => {
    await db.practiceTests.clear();
    await db.whiteboards.clear();
    await resetChapterMastery();
  });
}
