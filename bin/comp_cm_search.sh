#cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_search.CatchupMathSearch -o -Dgwt.compiler.force=true
cp -r src/main/webapp/cm_search target/cm-1.0-SNAPSHOT
