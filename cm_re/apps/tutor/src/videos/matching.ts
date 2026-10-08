import type { SkillId } from "../games/skills";
import type { VideoEntry } from "./registry";
import { VIDEOS } from "./registry";

export interface VideoContext {
  skills: SkillId[];
}

export function videosForContext(ctx: VideoContext): VideoEntry[] {
  return VIDEOS.filter((v) =>
    v.skills.some((s) => ctx.skills.includes(s)),
  );
}
