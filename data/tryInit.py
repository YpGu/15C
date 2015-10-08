import random

def tryInit():
	fin = open('3k_retweet_bipartite/retweet_dict_3k')
	lines = fin.readlines()
	pd = {}
	for line in lines:
		newID = int(line.split('\t')[0])
		rawID = int(line.split('\t')[1])
		pd[newID] = rawID
	fin.close()

	cand = {}
	fin = open('dict/merge_id_list')
	lines = fin.readlines()
	for line in lines:
		rawID = int(line.split('\t')[1])
		party = line.split('\t')[3].split('\n')[0]
		cand[rawID] = party
	fin.close()

	pos = {}; neg = {}
	for x in pd:
		pos[pd[x]] = []
		neg[pd[x]] = []
	
	fin = open('3k_retweet_bipartite/retweet_list_3k.train')
	lines = fin.readlines()
	for line in lines:
		x = int(line.split('\t')[0])
		y = int(line.split('\t')[1])
		if cand[y] == 'R':
			pos[x].append(y)
		elif cand[y] == 'D':
			neg[x].append(y)
	fin.close()

	# output
	ps = {}
	fout = open('./tmp_init_5', 'w')
	for newID in pd:
		x = pd[newID]		# raw ID
#		print x, pos[x], neg[x]
		if len(pos[x]) + len(neg[x]) != 0:
			idp = -1 + 2 * float(len(pos[x])) / (len(pos[x]) + len(neg[x]))
		else:
			idp = 0
		ps[x] = idp
		newline = ''
		for i in range(5):
			newline = newline + str(idp + 0.2 * (random.random() - 0.5)) + '\t'
		newline = newline + '\n'
		fout.write(newline)
	fout.close()

	qs = {}
	fout = open('./tmp_init_6', 'w')
	for newID in pd:
		x = pd[newID]		# raw ID
#		print x, pos[x], neg[x]
		idp = 0
		if len(pos[x]) + len(neg[x]) != 0:
			for y in pos[x]:
				idp += ps[y]
			for y in neg[x]:
				idp += ps[y]
			idp /= float(len(pos[x]) + len(neg[x]))
#			idp = -1 + 2 * float(pos[x]) / (pos[x] + neg[x])
		else:
			idp = 0
		qs[x] = idp
		newline = ''
		for i in range(5):
			newline = newline + str(idp + 0.2 * (random.random() - 0.5)) + '\t'
		newline = newline + '\n'
		fout.write(newline)
	fout.close()

if __name__ == '__main__':
	tryInit()
