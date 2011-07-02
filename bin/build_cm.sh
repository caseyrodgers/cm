## Build the entire CM package
#
# Only do this if the target directory does not exist
#
if [ -e target ]
  then
     echo target directory already exists
     exit
fi



# Build the hotmath JS files used by CM GWT
#
echo Building Hotmath JS shared resources
cd ../hotmath2
cvs -q update web/js web/css
cd build
ant -f build_resource_combine.xml
cd ../../cm


# Update the CM CVS repostiory
echo Updating CM CVS
cvs -q update -d -P




# build the cm_mobile JS combined files
#
echo Building the cm_mobile JS combined resources
ant


# build the CM package
#
echo Building full CM package
mvn package minify:minify




# make symbolic links for base CM to shared resources
#
echo Setting up CM package symbolic links to shared resources
./make_target_links.sh


# make symbolic links for cm_mobile2 as standalone domain
#
echo Setup the cm_mobile2 GWT app as standalone domain
cd target/cm-1.0-SNAPSHOT/cm_mobile2
chmod +x ./make_links.sh
./make_links.sh
cd ../../..


# make temp dir used in temp file creation
#
echo Setting the CM temp output directory
mkdir target/cm-1.0-SNAPSHOT/cm_temp


# make sure hotmath has the latest CM API
#
echo Copying latest CM jarj into HM
#
cp target/cm-1.0-SNAPSHOT.jar ../hotmath2/web/WEB-INF/lib

# make sure Tomcat has a clean idea of webapp
# WARNING: tomcat2 or tomcat .. depends on live/test 
#
echo Copying tomcat ROOT.xml into CM tomcat instance.
cp src/main/webapp/ROOT.xml ~/dist/cm_tomcat/conf/Catalina/localhost

# be rough
#
echo Restarting all java processes
#killall -9 java
