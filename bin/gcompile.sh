#cvs -q update -d -P
mvn compile gwt:compile -o jar:jar
cp -r src/main/webapp/cm_*  target/cm-1.0-SNAPSHOT






