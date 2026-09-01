// Write-sync queue for student answers/progress/step-completion events.
// Queues events in IndexedDB while offline; flushes to the backend's
// progress-sync endpoint on reconnect (retry loop, or Background Sync API
// where supported). Single-writer per student — no conflict resolution needed.
// TODO: enqueue(event); flush(); wire flush() to `online` event + periodic retry.
