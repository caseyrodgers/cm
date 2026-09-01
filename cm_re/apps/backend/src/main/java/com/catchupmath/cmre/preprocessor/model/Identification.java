package com.catchupmath.cmre.preprocessor.model;

/** Mirrors shared-types' Identification (packages/shared-types/src/solution.ts). */
public class Identification {
    public String book;
    public String chapter;
    public String section;
    public String set;
    public String problemNumber;
    /** Nullable — not every problem has a page reference. */
    public String page;
}
