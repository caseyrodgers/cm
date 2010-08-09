echo 'importing: '  $1
mysqldump -uhotmath -pgeometry hotmath_live $1 < exported_data/$1.dump
