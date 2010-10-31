cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_mobile2.CatchupMathMobile2 -o
cp -r src/main/webapp/cm_mobile2 target/cm-1.0-SNAPSHOT

ant
