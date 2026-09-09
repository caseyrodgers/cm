import type { ComponentType, ReactNode } from "react";
import DefaultShell from "./DefaultShell";

/**
 * Registry of app shells (header/footer/nav looks). Add a file here and
 * a line below to try a new one; select it with `?shell=<id>` (see
 * lib/shell.ts). The content components never change.
 */
export type ShellComponent = ComponentType<{ children: ReactNode }>;

export const SHELLS: Record<string, ShellComponent> = {
  default: DefaultShell,
};

export const SHELL_IDS = Object.keys(SHELLS);
