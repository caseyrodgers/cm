# create a standalone domain
# by hooking up shared resources
# as if they are in the current directory.
#
# This will allow for a standalone domain
# to be served through apache.
##
ln -s ../gwt-resources .
ln -s ../assets .
ln -s ../activities .


ln -s ../../../../hotmath2/web/js ./
ln -s ../../../../hotmath2/web/css ./
ln -s ../../../../hotmath2/web/tutor ./
ln -s ../../../../hotmath2/web/images ./
ln -s ../../../../hotmath2/web/help ./
ln -s ../../../../hotmath2/web/collab ./
ln -s ../../../../hotmath2/web/learning_activities ./
ln -s ../../../../hotmath2/web/hotmath_help ./

mkdir util
cd util
ln -s ../../../../../hotmath2/web/util/input_widgets ./
cd ..

ln -s HmMobile.html ./index.html