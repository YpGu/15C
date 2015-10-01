#!/bin/bash

#for i in 1 0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1 0; do
i=$1
	for j in `seq 0 9`; do
#		python pca.py ../0930r/saved_param/p_"$i"_"$j"
		python pca.py ../0930f/saved_param/q_"$i"_"$j"
	done

