cvs -q update -d -P
mvn gwt:compile -Dgwt.module=hotmath.gwt.hm_mobile.HmMobile -o
cp -r src/main/webapp/hm_mobile target/cm-1.0-SNAPSHOT

cd target/cm-1.0-SNAPSHOT/hm_mobile
chmod +x make_links.sh
./make_links.sh

cd ../../../

