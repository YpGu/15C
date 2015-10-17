def cs_mention():
	print 'mention'
	fin = open('../3k_mention_bipartite/mention_list_3k.train')
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

	fin = open('../3k_mention_bipartite/mention_list_3k.test')
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

	tt = [i for i in test if i not in train]
#	tt = [i for i in test if i in train and train[i] < 10]
	fout = open('./candidate1', 'w')
	for i in tt:
		print i, test[i]
		newline = str(i) + '\n'
		fout.write(newline)
	fout.close()
	print tt

def cs_retweet():
	print 'retweet'
	fin = open('../3k_retweet_bipartite/retweet_list_3k.train')
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

	fin = open('../3k_retweet_bipartite/retweet_list_3k.test')
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
	tt = [i for i in test if i not in train]
	for i in tt:
		print i, test[i]
		newline = str(i) + '\n'
		fout.write(newline)
	fout.close()
#	print tt


if __name__ == '__main__':
	cs_mention()
	cs_retweet()
