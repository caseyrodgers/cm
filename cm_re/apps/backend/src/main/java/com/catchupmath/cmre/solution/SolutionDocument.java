package com.catchupmath.cmre.solution;

// JSONB-backed entity mapping to the `solutions` table (see
// resources/db/migration). Mirrors packages/shared-types/src/solution.ts —
// keep the two in sync by hand until a schema codegen step exists.
// TODO: @Entity; id, subjectId, programId, title, version, and a JSONB
// `steps` column mapped via hibernate-types or a custom converter.
public class SolutionDocument {
}
