'''
    cold start problem  (section 5.2.3) 
'''

import sys

def cs_mention(threshold):
	print ' --- mention --- '
	fin = open('../40k_mention_bipartite/mention_list_40k.train')
	train = {}
	lines = fin.readlines()
	for line in lines:
		x = int(line.split('\t')[0])
		y = int(line.split('\t')[1])
		if x not in train:
			train[x] = 1
		else:
			train[x] += 1
	fin.close()

	fin = open('../40k_mention_bipartite/mention_list_40k.test')
	test = {}
	lines = fin.readlines()
	for line in lines:
		x = int(line.split('\t')[0])
		y = int(line.split('\t')[1])
		if x not in test:
			test[x] = 1
		else:
			test[x] += 1
	fin.close()

#	tt = [i for i in test if i not in train]
	tt = [i for i in test if i not in train or train[i] <= threshold]
	fout = open('./candidate1', 'w')
	for i in tt:
	#	print i, test[i]
		newline = str(i) + '\n'
		fout.write(newline)
	fout.close()
	print len(tt)
#	print tt

def cs_retweet(threshold):
	print ' --- retweet --- '
	fin = open('../40k_retweet_bipartite/retweet_list_40k.train')
	train = {}
	lines = fin.readlines()
	for line in lines:
		x = int(line.split('\t')[0])
		y = int(line.split('\t')[1])
		if x not in train:
			train[x] = 1
		else:
			train[x] += 1
	fin.close()

	fin = open('../40k_retweet_bipartite/retweet_list_40k.test')
	test = {}
	lines = fin.readlines()
	for line in lines:
		x = int(line.split('\t')[0])
		y = int(line.split('\t')[1])
		if x not in test:
			test[x] = 1
		else:
			test[x] += 1
	fin.close()

	fout = open('./candidate2', 'w')
#	tt = [i for i in test if train[i] < 5]
	tt = [i for i in test if i not in train or train[i] <= threshold]
	for i in tt:
	#	print i, test[i]
		newline = str(i) + '\n'
		fout.write(newline)
	fout.close()
	print len(tt)
#	print tt


if __name__ == '__main__':
	if len(sys.argv) != 2:
		print 'Usage: python cs.py <threshold>'
		sys.exit()

	t = int(sys.argv[1])
	cs_mention(t)
	cs_retweet(t)
