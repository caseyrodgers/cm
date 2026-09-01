# cm_re — Catchup Math, rewrite

First-pass component layout for the CM rewrite. See
[`../NEW_DIRECTION.org`](../NEW_DIRECTION.org) for the full design
discussion and decisions log — this directory is the structural
mockup of what that document describes. Nothing in here is a working
implementation yet; files are scaffolding/stubs marking where each
piece lives and what it's responsible for.

## Layout

```
cm_re/
├── apps/
│   ├── tutor/     student-facing PWA — offline-capable, downloads
│   │              per-subject content modules, renders solution
│   │              steps one at a time
│   ├── editor/    solution_editor replacement — online-only
│   │              authoring tool (TipTap-based rich content editor)
│   └── backend/   Java / Spring Boot — reuses the existing DAO/SQL
│                  layer's shape, adds solution/module/progress-sync
│                  endpoints backed by Postgres (JSONB solution docs)
├── packages/
│   └── shared-types/   canonical solution-document TS type, shared
│                       by tutor + editor so both agree on shape
└── infra/              local dev infra (Postgres, etc.)
```

## Why two frontend apps, not one

`tutor` and `editor` are deliberately separate deployables:

- `tutor` is the offline-capable PWA (service worker, module
  downloads, write-sync queue) — see NEW_DIRECTION.org's "Offline
  support" section.
- `editor` stays online-only by design (see "Frontend platform"
  decision) — no offline complexity, no service worker, just a normal
  SPA authoring tool.

They share `packages/shared-types` so the solution-document shape
can't drift between what the editor writes and what the tutor reads.
