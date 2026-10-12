import type { SkillId } from "./skills";

export interface GameEntry {
  slug: string;
  title: string;
  description: string;
  url: string;
  skills: SkillId[];
  gradeRange?: [number, number];
  thumbnail?: string;
  contextParams?: Record<string, ContextField>;
}

export type ContextField =
  | "skill"
  | "difficulty"
  | "grade"
  | "chapterLabel"
  | "subjectId";

export const GAMES: GameEntry[] = [
  {
    slug: "numbercop",
    title: "Number Cop",
    description: "Drive through multiples, primes, and perfect squares",
    url: "/games/numbercop/index.html",
    skills: ["least-common-multiples", "prime-composite", "perfect-squares", "rationals"],
    gradeRange: [5, 9],
    contextParams: { type: "skill", difficulty: "difficulty" },
  },
  {
    slug: "flashcards-fractions",
    title: "Fraction Flashcards",
    description: "Timed practice adding and simplifying fractions",
    url: "/games/flashcards/index.html",
    skills: ["adding-fractions", "adding-and-subtracting-fractions", "multiplying-a-fraction-by-a-fraction", "reducing-fractions"],
    gradeRange: [4, 8],
  },
];
