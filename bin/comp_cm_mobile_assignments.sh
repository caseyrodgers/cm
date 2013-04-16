#cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_mobile_assignments.CmMobileAssignments -o -Dgwt.compiler.force=true -o
cp -r src/main/webapp/cm_mobile_assignments target/cm-1.0-SNAPSHOT


# install cm_mobile as standalone domain
cd target/cm-1.0-SNAPSHOT/cm_mobile_assignments
./make_links.sh
cd ../../../


ant
