#!/bin/bash

i=$1
for j in `seq 0 9`; do
	python evaluate.py $i $j
done
