ln -s ../../../hotmath2/web/js target/cm-1.0-SNAPSHOT
ln -s ../../../hotmath2/web/css target/cm-1.0-SNAPSHOT
ln -s ../../../hotmath2/web/tutor target/cm-1.0-SNAPSHOT
ln -s ../../../hotmath2/web/images target/cm-1.0-SNAPSHOT
ln -s ../../../hotmath2/web/collab target/cm-1.0-SNAPSHOT
ln -s ../../../hotmath2/web/learning_activities target/cm-1.0-SNAPSHOT
ln -s ../../../hotmath2/web/hotmath_help target/cm-1.0-SNAPSHOT
ln -s ../../../hotmath2/web/util/input_widgets target/cm-1.0-SNAPSHOT/util/input_widgets

ln -s /hm_static target/cm-1.0-SNAPSHOT/help

# make gwt-resources point back to source for easy dev
mv target/cm-1.0-SNAPSHOT/gwt-resources target/cm-1.0-SNAPSHOT/gwt-resources_$$
ln -s ../../src/main/webapp/gwt-resources target/cm-1.0-SNAPSHOT/gwt-resources


# make assests point back to source for easy dev
mv target/cm-1.0-SNAPSHOT/assets target/cm-1.0-SNAPSHOT/assets_$$
ln -s ../../src/main/webapp/assets target/cm-1.0-SNAPSHOT/assets
