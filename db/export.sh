echo 'dumping: '  $1
mysqldump -uhotmath -pgeometry hotmath_live_2 $1 > exported_data/$1.dump
