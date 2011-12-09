cd ../hotmath2/build
call ant -f build_resource_combine.xml
cd ../../cm

call mvn minify:minify -o

call ant -f build_minify.xml
