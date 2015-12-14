#!/bin/bash

i=$1

# for mention: k = 0, 1, 2
if [ $i -eq 1 ]; then
	for k in 0 1 2; do 
		echo $k
		for j in `seq 0 9`; do
			python evaluate.py $i $j $k
		done
	done
fi

# for retweet: k = 0, 1, 3
if [ $i -eq 2 ]; then
	for k in 0 1 3; do
		echo $k
		for j in `seq 0 9`; do
			python evaluate.py $i $j $k
		done
	done
fi

