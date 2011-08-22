cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_mobile3.CatchupMathMobile3 -o
cp -r src/main/webapp/cm_mobile3 target/cm-1.0-SNAPSHOT


# install cm_mobile as standalone domain
cd target/cm-1.0-SNAPSHOT/cm_mobile3
./make_links.sh
cd ../../../


ant
