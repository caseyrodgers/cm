for a in `cat tables.txt` 
do 
   ./import.sh exported_data/$a.dump
done
