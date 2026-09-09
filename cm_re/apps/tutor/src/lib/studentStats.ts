import { db } from "../offline/db";
import { listSubjects } from "../api/client";
import { getCorrectTotal, resetCorrectTotal } from "./correctCount";
import { scoreTest, testTitle } from "../offline/practiceTestStore";

/**
 * The current student is whoever's using this browser — everything is
 * in localStorage + IndexedDB, no account. This aggregates it into one
 * view for the `#/me` screen, and offers a reset.
 */

const GRADE_KEY = "cm_re.learn.grade";

export interface SubjectStat {
  subjectId: string;
  title: string;
  installed: boolean;
  approxSizeBytes: number;
  /** the active/last test for this subject, if any */
  test?: {
    title: string;
    finished: boolean;
    correct: number;
    total: number;
    answered: number;
  };
}

export interface StudentStats {
  correctTotal: number;
  grade: string | null;
  whiteboardCount: number;
  downloads: { count: number; approxSizeBytes: number };
  subjects: SubjectStat[];
}

export async function getStudentStats(): Promise<StudentStats> {
  const [installedModules, practiceTests, whiteboardCount, subjectList] = await Promise.all([
    db.modules.toArray(),
    db.practiceTests.toArray(),
    db.whiteboards.count(),
    listSubjects().catch(() => [] as { subjectId: string; title: string }[]),
  ]);

  const installedById = new Map(installedModules.map((m) => [m.subjectId, m]));
  const testById = new Map(practiceTests.map((t) => [t.subjectId, t]));
  const titleById = new Map(subjectList.map((s) => [s.subjectId, s.title]));

  // Every subject we know about, from either source.
  const ids = new Set<string>([...titleById.keys(), ...installedById.keys(), ...testById.keys()]);

  const subjects: SubjectStat[] = [...ids].sort().map((subjectId) => {
    const mod = installedById.get(subjectId);
    const t = testById.get(subjectId);
    const stat: SubjectStat = {
      subjectId,
      title: titleById.get(subjectId) ?? subjectId,
      installed: !!mod,
      approxSizeBytes: mod?.approxSizeBytes ?? 0,
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
  });

  let grade: string | null = null;
  try {
    grade = localStorage.getItem(GRADE_KEY);
  } catch {
    /* ignore */
  }

  return {
    correctTotal: getCorrectTotal(),
    grade,
    whiteboardCount,
    downloads: {
      count: installedModules.length,
      approxSizeBytes: installedModules.reduce((n, m) => n + (m.approxSizeBytes ?? 0), 0),
    },
    subjects,
  };
}

/**
 * Wipes this student's progress: the correct-answer tally, every
 * practice test, and every whiteboard. Deliberately keeps downloaded
 * modules (that's storage, not progress) and the grade preference.
 */
export async function resetStats(): Promise<void> {
  resetCorrectTotal();
  await db.transaction("rw", db.practiceTests, db.whiteboards, async () => {
    await db.practiceTests.clear();
    await db.whiteboards.clear();
  });
}
