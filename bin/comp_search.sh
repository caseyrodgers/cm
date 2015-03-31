#cvs update -d -P src/main/java/hotmath/gwt
mvn gwt:compile -Dgwt.module=hotmath.gwt.search.Search -o -Dgwt.compiler.force=true
cp -r src/main/webapp/search target/cm-1.0-SNAPSHOT

