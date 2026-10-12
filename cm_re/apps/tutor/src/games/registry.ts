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
  {
    slug: "equation-balance",
    title: "Equation Balance",
    description: "Solve for x to balance the scale — one-step through multi-step",
    url: "/games/equation-balance/index.html",
    skills: [
      "solving-one-step-equations",
      "solving-one-step-linear-equations",
      "solving-two-step-linear-equations",
      "solving-multi-step-linear-equations",
      "solving-linear-equations",
      "solving-equations",
      "combining-like-terms",
    ],
    gradeRange: [6, 10],
  },
  {
    slug: "angle-estimator",
    title: "Angle Estimator",
    description: "Estimate angles by sight — from common angles to reflex",
    url: "/games/angle-estimator/index.html",
    skills: [
      "angles",
      "degree-measure-of-an-angle",
      "complementary-angles",
      "supplementary-angles",
      "vertical-angles",
      "central-angles",
      "congruent-angles",
      "angle-sum-theorem",
    ],
    gradeRange: [5, 9],
  },
];
