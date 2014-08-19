#cvs update -d -P src/main/java
mvn gwt:compile -Dgwt.module=hotmath.gwt.assignment_print.AssignmentPrint -Dgwt.compiler.force=true -o
cp -r src/main/webapp/cm_student target/cm-1.0-SNAPSHOT
