#cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm.CatchupMath -o
cp -r src/main/webapp/cm_student target/cm-1.0-SNAPSHOT
