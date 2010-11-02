echo 'importing: '  $1
mysql -uhotmath -pgeometry -h hotmath-live.cpcll61ssmu3.us-west-1.rds.amazonaws.com hotmath_live < $1 
