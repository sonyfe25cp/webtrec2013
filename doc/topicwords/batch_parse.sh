#! /bin/bash
i=1
echo $i
while [ $i -lt 26 ]; do
  if [ $i -lt 10 ];then
    b=0$i
  else
    b=$i
  fi
  echo 2$b.txt
#  head -n21 2$b.txt > 20$b.txt
  count=$(head -n1 2$b.txt)
  echo $count

  awk '{print $1, $2, $2/$count}' 2$b.txt

  ((i++))
  
done

