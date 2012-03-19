#!/bin/sh

showUsage() {
    echo "usage: comp_cm.sh {noupdate} {admin, student, qa}"
    echo "  noupdate - don't update CVS"
    echo "  admin   - GWT compile of CM Admin"
    echo "  student - GWT compile of CM Student"
    echo "  qa      - GWT compile of CM QA"
    exit 1;
}

if [ $# -eq 0 ]; then
    showUsage;
fi

if [ "$1" != "noupdate" ]; then
    cvs -q update -d -P
fi

shift;

while [ $# -ne 0 ]; do
    c=$1;
    case "$c" in
    "admin")
       echo building admin...;
       mvn -o compile;
       mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_admin.CatchupMathAdmin -o
       cp -r src/main/webapp/cm_admin target/cm-1.0-SNAPSHOT
       ;;
    "student")
       echo building student...;
       mvn -o compile;
       mvn gwt:compile -Dgwt.module=hotmath.gwt.cm.CatchupMath -o
       cp -r src/main/webapp/cm_student target/cm-1.0-SNAPSHOT
       ;;

    "qa")
       echo building qa;
       mvn -o compile;
       mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_qa.CmQa -o
       cp -r src/main/webapp/cm_qa target/cm-1.0-SNAPSHOT
       ;;
    esac
    shift
done

#mvn gwt:compile -Dgwt.module=hotmath.gwt.cm_admin.CatchupMathAdmin -o
#cp -r src/main/webapp/cm_admin target/cm-1.0-SNAPSHOT
