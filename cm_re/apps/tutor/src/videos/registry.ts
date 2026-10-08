import type { SkillId } from "../games/skills";
import videosData from "./registry.json";

export interface VideoEntry {
  slug: string;
  title: string;
  url: string;
  skills: SkillId[];
}

export const VIDEOS: VideoEntry[] = videosData as VideoEntry[];
