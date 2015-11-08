find . -name '*.java' | xargs wc -l | cut -c 1-7 | bc | sed 's/^/.+/' | bc | tail -1
