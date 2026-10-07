import type { SkillId } from "./skills";
import type { GameEntry, ContextField } from "./registry";
import { GAMES } from "./registry";

export interface GameContext {
  skills: SkillId[];
  grade?: number;
  difficulty?: number;
  subjectId?: string;
  chapterLabel?: string;
}

export function gamesForContext(ctx: GameContext): GameEntry[] {
  return GAMES.filter((g) => {
    const hasSkillOverlap = g.skills.some((s) => ctx.skills.includes(s));
    if (!hasSkillOverlap) return false;
    if (ctx.grade != null && g.gradeRange) {
      if (ctx.grade < g.gradeRange[0] || ctx.grade > g.gradeRange[1])
        return false;
    }
    return true;
  });
}

export function gameUrl(game: GameEntry, ctx: GameContext): string {
  if (!game.contextParams) return game.url;
  const params = new URLSearchParams();
  const lookup: Record<ContextField, string | undefined> = {
    skill: ctx.skills[0],
    difficulty: ctx.difficulty?.toString(),
    grade: ctx.grade?.toString(),
    chapterLabel: ctx.chapterLabel,
    subjectId: ctx.subjectId,
  };
  for (const [param, field] of Object.entries(game.contextParams)) {
    const value = lookup[field as ContextField];
    if (value != null) params.set(param, value);
  }
  const qs = params.toString();
  return qs ? `${game.url}?${qs}` : game.url;
}
