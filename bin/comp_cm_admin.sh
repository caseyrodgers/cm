#cvs update -d -P src/main/java/hotmath/gwt
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_admin.CatchupMathAdmin -o -Dgwt.compiler.force=true
cp -r src/main/webapp/cm_admin target/cm-1.0-SNAPSHOT

