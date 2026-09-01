package com.catchupmath.cmre.progress;

// One queued event from apps/tutor's offline/syncQueue.ts: an answer
// submission, step completion, etc. Analogous to the legacy
// hotmath.cm.server.StudentEventService's queued events, but arriving
// batched from a client that may have been offline for a while.
// TODO: studentId, solutionId, stepId, eventType, timestamp, payload.
public class ProgressEvent {
}
