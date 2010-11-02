cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_mobile2.CatchupMathMobile2 -o
cp -r src/main/webapp/cm_mobile2 target/cm-1.0-SNAPSHOT


# install cm_mobile as standalone domain
cd target/cm-1.0-SNAPSHOT/cm_mobile2
./make_links.sh
cd ../../../


ant
