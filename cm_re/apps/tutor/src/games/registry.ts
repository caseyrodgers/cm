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
    skills: ["multiples", "primes", "perfect-squares", "rationals"],
    gradeRange: [5, 9],
    contextParams: { type: "skill", difficulty: "difficulty" },
  },
  {
    slug: "flashcards-fractions",
    title: "Fraction Flashcards",
    description: "Timed practice adding and simplifying fractions",
    url: "/games/flashcards/index.html",
    skills: ["fractions-add", "fractions-multiply", "fraction-models"],
    gradeRange: [4, 8],
  },
  {
    slug: "math-quiz-game",
    title: "Math Quiz",
    description: "Rapid-fire arithmetic — add, subtract, multiply, divide under time pressure",
    url: "https://play.gamepix.com/math-quiz-game/embed",
    skills: ["integers", "order-of-operations", "decimals"],
    gradeRange: [3, 8],
  },
  {
    slug: "sudoku-classic",
    title: "Sudoku",
    description: "Classic 9x9 logic puzzle — builds number sense and deductive reasoning",
    url: "https://play.gamepix.com/sudoku-classic/embed",
    skills: ["integers", "data-analysis"],
    gradeRange: [5, 12],
  },
  {
    slug: "get-10",
    title: "Get 10",
    description: "Merge adjacent numbers to reach 10 — addition and strategy",
    url: "https://play.gamepix.com/get-10/embed",
    skills: ["integers", "order-of-operations"],
    gradeRange: [3, 7],
  },
  {
    slug: "math-duck",
    title: "Math Duck",
    description: "Platformer where you solve arithmetic to progress through levels",
    url: "https://play.gamepix.com/math-duck/embed",
    skills: ["integers", "order-of-operations", "fractions-add"],
    gradeRange: [3, 7],
  },
  {
    slug: "number-sum",
    title: "Number Sums",
    description: "Find number combinations that add to the target — mental math workout",
    url: "https://play.gamepix.com/number-sum/embed",
    skills: ["integers", "order-of-operations"],
    gradeRange: [3, 8],
  },
  {
    slug: "math-crossword",
    title: "Math Crossword",
    description: "Crossword puzzle with arithmetic clues — equations in every direction",
    url: "https://play.gamepix.com/math-crossword-number-puzzle/embed",
    skills: ["integers", "order-of-operations", "linear-equations"],
    gradeRange: [4, 9],
  },
];
