package com.catchupmath.cmre.progress;

// Receives batched ProgressEvents flushed from apps/tutor's syncQueue.ts
// once a student reconnects. Single-writer per student, so this is a
// straightforward append, not a merge/conflict-resolution problem.
// TODO: POST /api/progress/sync (batch of ProgressEvent) -> 200
public class ProgressSyncController {
}
