cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.solution_editor.SolutionEditor -o
cp -r src/main/webapp/solution_editor target/cm-1.0-SNAPSHOT
