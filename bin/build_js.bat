cd ../hotmath2/build
call ant -f build_resource_combine.xml
cp ../web/js/*.js ../../cm/src/main/webapp/js
cp ../web/css/*.css ../../cm/src/main/webapp/css
cd ../../cm

call mvn minify:minify -o
cp target/cm-1.0-SNAPSHOT/gwt-resources/js/CatchupMath_combined*.js src/main/webapp/gwt-resources/js
cp target/cm-1.0-SNAPSHOT/gwt-resources/css/CatchupMath_combined.*.css src/main/webapp/gwt-resources/css

call ant -f build_minify.xml
