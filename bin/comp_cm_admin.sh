cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_admin.CatchupMathAdmin -o
cp -r src/main/webapp/cm_admin target/cm-1.0-SNAPSHOT

