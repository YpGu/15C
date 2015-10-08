'''
Using unigram distribution (in-degrees) to the power of 3/4 to generate negative samples.
'''

import random

def InitUnigramTable(dir):
	# create dictionary for authorities: y -> in-degree(y)
	fin = open(dir + 'mention_list_3k')
	dictionary = {}
	lines = fin.readlines()
	for line in lines:
		x = line.split('\t')[0]
		y = line.split('\t')[-1]
		y = int(y)
		if y not in dictionary:
			dictionary[y] = 1
		else:
			dictionary[y] += 1
	fin.close()
#	print 'candidate size = ' + str(len(dictionary))

	# create idMap: id -> y
	idMap = {}
	index = 0
	for y in dictionary:
		idMap[index] = y
		index += 1

	vocab_size = len(dictionary)
	train_words_pow = 0.0
	power = 0.75
	table_size = 1e5
	table = {}
	for i in range(int(table_size)):
		table[i] = 0
	
	for w in dictionary:
		train_words_pow += pow(dictionary[w], power)

	p = 0
	d1 = pow(dictionary[idMap[0]], power) / train_words_pow
	for i in range(int(table_size)):
		table[i] = idMap[p]
		if (i / float(table_size) > d1):
			p += 1
			d1 += pow(dictionary[idMap[p]], power) / train_words_pow
		if (p >= vocab_size):
			p = vocab_size - 1
#	print d1

	# output
	fout = open(dir + 'neg.samples.list', 'w')
	for i in range(int(table_size)):
		newline = str(i) + '\t' + str(table[i]) + '\n'
		fout.write(newline)
	fout.close()


def sample(dir, negative):
	fin = open(dir + 'neg.samples.list')
	lines = fin.readlines()
	table_size = len(lines)
	table = {}
	for line in lines:
		x, y = line.split('\t')
		table[int(x)] = int(y)
	fin.close()

	'''
	useTrain = False
	if (useTrain):
		fin = open(dir + 'mention_list_3k.train')
		fout = open(dir + 'n_mention_list_3k.train.' + str(negative), 'w')
	else:
		fin = open(dir + 'mention_list_3k.test')
		fout = open(dir + 'n_mention_list_3k.test.' + str(negative), 'w')
	'''
	fin = open(dir + 'mention_list_3k')
	fout = open(dir + 'n_mention_list_3k.' + str(negative), 'w')
	lines = fin.readlines()
	pos_links = {}
	for line in lines:
		x = int(line.split('\t')[0])
		y = int(line.split('\t')[1])
		if x not in pos_links:
			pos_links[x] = [y]
		else:
			pos_links[x].append(y)

	random.seed(0)
	for line in lines:
		x = int(line.split('\t')[0])
		y = int(line.split('\t')[1])
		zs = []
		while True:
			next_random = int(random.random() * 25214903917 + 11)
			z = table[(next_random >> 8) % table_size]
#			if z == y or z == x:		# it's OK as long as the sample is not identical to x itself
#				continue
			if z == x or z in pos_links[x]:	# the sample must NOT appear in the existing links 
				continue
			zs.append(z)
			if len(zs) == negative:
				break
		for z in zs:
			newline = str(x) + '\t' + str(z) + '\n'
			fout.write(newline)
	fin.close()
	fout.close()


if __name__ == '__main__':
	dir = './3k_mention_bipartite/'
	negative = 1

	InitUnigramTable(dir)
	sample(dir, negative)

