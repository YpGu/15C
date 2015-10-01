#!/bin/bash

for i in `seq 0 9`; do
	echo $i
	for j in 1 0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1 0; do
		# friend, p 
		python pca.py ../f3/saved_param/p_"$j"_"$i" >> log_f_p/"$j".log
		# friend, q 
		python pca.py ../f3/saved_param/q_"$j"_"$i" >> log_f_q/"$j".log
		# mention, p 
		python pca.py ../m3/saved_param/p_"$j"_"$i" >> log_m_p/"$j".log
		# mention, q
		python pca.py ../m3/saved_param/q_"$j"_"$i" >> log_m_q/"$j".log
		# retweet, p 
		python pca.py ../r3/saved_param/p_"$j"_"$i" >> log_r_p/"$j".log
		# retweet, q
		python pca.py ../r3/saved_param/q_"$j"_"$i" >> log_r_q/"$j".log
	done
done
