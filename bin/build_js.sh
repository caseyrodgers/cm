## build hotmath JS combined files
cd ../hotmath2/build
ant -f build_resource_combine.xml
cd ../../cm

## minify 
mvn minify:minify -o

## build cm_mobile2 resources
ant  -f build_minify.xml
